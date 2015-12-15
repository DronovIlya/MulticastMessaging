package server;

import client.Constants;
import commands.base.BaseRequest;
import commands.base.BaseResponse;
import proto.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends Thread implements ServerCallback {

    private final ServerManager manager;
    private final ServerSocket serverSocket;
    public final ServerApi api;

    private AtomicInteger sessionId = new AtomicInteger(0);
    private Map<Integer, Session> availableSessions = new HashMap<>();

    private UdpBroadcaster broadcaster;

    public Server() throws IOException {
        manager = new ServerManager(this);
        api = new ServerApi(this);

        serverSocket = new ServerSocket(Constants.SERVER_TCP_PORT);
        serverSocket.setSoTimeout(0);
        serverSocket.setReuseAddress(true);

        broadcaster = new UdpBroadcaster(ServerManager.PUBLIC_ROOM_API, Constants.BROADCAST_PORT);
        broadcaster.start();
    }

    @Override
    public void onRequest(int sessionId, BaseRequest request) {
        manager.acceptRequest(sessionId, request);
    }

    @Override
    public void onSessionClosed(int sessionId) {
        rejectSession(sessionId, false);
    }

    private Session getSession(int sessionId) {
        return availableSessions.containsKey(sessionId) ? availableSessions.get(sessionId) : null;
    }

    public void rejectSession(int sessionId, boolean close) {
        System.out.println("reject session with id = " + sessionId);
        Session session = getSession(sessionId);
        if (close) {
            if (session != null) {
                session.close();
            }
        }
        availableSessions.remove(sessionId);
    }

    public void sendResponse(int sessionId, BaseResponse response) {
        System.out.println("server, response --> " + sessionId + ", response = " + response);
        getSession(sessionId).sendResponse(response);
    }

    public void sendBroadcast(BaseRequest request) {
        System.out.println("send broadcast, request = " + request);
        broadcaster.pushPacket(new Packet(request));
    }

    @Override
    public void run() {
        System.out.println("run");
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("accept new socket");
                int sid = sessionId.incrementAndGet();
                Session session = new Session(sid, socket, this);
                session.start();

                availableSessions.put(sid, session);
            } catch (IOException e) {
                System.out.println("server breaking down..., try to restart it");
            }
        }
    }

}

