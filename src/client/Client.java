package client;

import commands.base.BaseRequest;
import commands.base.BaseResponse;
import client.transport.TcpCallback;
import client.transport.TcpHandler;
import client.transport.UdpCallback;
import client.transport.UdpHandler;
import commands.entity.Chat;

import java.io.IOException;
import java.util.List;
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
            parseCommand(commandArgs);
        }
    }

    private void parseCommand(String[] args) {
        String command = args[0];
        switch (command) {
            case "login":
                if (args.length != 3) {
                    System.out.println("Wrong login command : login \"login\" \"password\"");
                    return;
                }
                api.login(args[1], args[2]);
                break;
            case "messageSend":
                if (args.length != 3) {
                    System.out.println("Wrong messageSend command : messageSend \"chatId\" \"text\"");
                    return;
                }
                long chatId = Long.parseLong(args[1]);
                String text = args[2];
                api.messageSend(chatId, text);
                break;
            case "joinChat":
                if (args.length != 2) {
                    System.out.println("Wring joinChat command : joinChat \"chatId\"");
                    return;
                }
                chatId = Long.parseLong(args[1]);
                api.joinChat(chatId);
                break;
        }
    }

    private void killClient() {
        System.out.println("Client: break client");
        tcpHandler.close();
        udpHandler.close();
        connectionState = ConnectionState.DISCONNECTED;
    }

    public void onLoggedIn() {
        connectionState = ConnectionState.LOGGED_IN;
        System.out.println("connection state = LOGGED_IN");
    }

    public void sendRequest(BaseRequest request) {
        tcpHandler.pushRequest(request);
    }

    public void startUdpListener(List<Chat> subscribedChats) {
        try {
            udpHandler = new UdpHandler(subscribedChats, Constants.BROADCAST_PORT, this);
            System.out.println("start udp listener");
        } catch (IOException e) {
            e.printStackTrace();
            // Something goes wrong, break client
            killClient();
        }
    }

    public void joinChat(String address) {
        try {
            udpHandler.joinChat(address);
        } catch (IOException e) {
            System.out.println("Client: joinChat: error in joining chat = " + address);
            e.printStackTrace();
            // Something goes wrong, break client
            killClient();
        }
    }

    @Override
    public <T extends BaseResponse> void onTcpReceived(T result) {
        manager.handleResponse(result);
    }

    @Override
    public void onTcpClosed() {
        System.out.println("Client: onTcpClosed");
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
                System.out.println("connection state = CONNECTING");
                connectionState = ConnectionState.CONNECTING;
                tcpHandler = new TcpHandler(Constants.SERVER_IP, Constants.SERVER_TCP_PORT, this);
                connectionState = ConnectionState.CONNECTED;
                System.out.println("connection state = CONNECTED");
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
