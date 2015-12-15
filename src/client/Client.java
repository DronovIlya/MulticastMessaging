package client;

import commands.base.BaseRequest;
import commands.base.BaseResponse;
import server.Server;
import proto.transport.TcpCallback;
import proto.transport.TcpHandler;
import proto.transport.UdpCallback;
import proto.transport.UdpHandler;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends Thread implements TcpCallback, UdpCallback {

    private ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private TcpHandler tcpHandler;
    private UdpHandler udpHandler;

    public final ClientApi api;
    public final ClientManager manager;

    public Client() {
        api = new ClientApi(this);
        manager = new ClientManager(this);

        ExecutorService connectionExecutor = Executors.newSingleThreadExecutor();
        connectionExecutor.execute(new ConnectionHandler());
    }

    @Override
    public void run() {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            String[] commandArgs = command.split("\\s+");
            // TODO: Probably it moves to GUI
            switch (commandArgs[0]) {
                case "messageSend":
                    if (commandArgs.length != 2) {
                        System.out.println("Unkown command");
                        continue;
                    }
                    String text = commandArgs[1];
                    api.messageSend(0, manager.self, text);
                    break;

            }
        }
    }

    private void breakClient() {
        System.out.println("break client");
        tcpHandler.close();
        udpHandler.close();
        connectionState = ConnectionState.DISCONNECTED;
    }

    public void onLoggedIn() {
        connectionState = ConnectionState.LOGGED_IN;
    }

    public void sendRequest(BaseRequest request) {
        tcpHandler.pushRequest(request);
    }

    public void startUdpListener(String publicRoomAddress) {
        try {
            udpHandler = new UdpHandler(publicRoomAddress, Server.BROADCAST_PORT, this);
            System.out.println("start udp listener");
        } catch (IOException e) {
            e.printStackTrace();
            // Something goes wrong, break client
            breakClient();
        }
    }

    @Override
    public <T extends BaseResponse> void onTcpReceived(T result) {
        manager.handleResponse(result);
    }

    @Override
    public void onTcpClosed() {
        connectionState = ConnectionState.DISCONNECTED;
    }

    @Override
    public <T extends BaseResponse> void onUdpReceived(T result) {
        manager.handleResponse(result);
    }

    @Override
    public void onUdpClose() {
        // TODO: handle it
    }

    private void checkConnectionState() {
        if (connectionState == ConnectionState.DISCONNECTED) {
            try {
                connectionState = ConnectionState.CONNECTING;
                tcpHandler = new TcpHandler(Constants.SERVER_IP, Constants.SERVER_TCP_PORT, this);
                connectionState = ConnectionState.CONNECTED;
            } catch (IOException e) {
                connectionState = ConnectionState.DISCONNECTED;
            }
        }
    }


    private class ConnectionHandler implements Runnable {

        private static final int CONNECTION_RECONNECT_DELAY = 5000;

        @Override
        public void run() {
            while (true) {
                while (connectionState != ConnectionState.DISCONNECTED) {
                    try {
                        Thread.sleep(CONNECTION_RECONNECT_DELAY);
                    } catch (InterruptedException ignored) {
                    }
                }

                if (connectionState == ConnectionState.DISCONNECTED) {
                    checkConnectionState();
                }
            }
        }
    }
}
