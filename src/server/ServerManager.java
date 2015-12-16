package server;

import commands.LoginCmd;
import commands.MessageSendCmd;
import commands.base.BaseRequest;
import commands.entity.Chat;
import commands.entity.Message;
import commands.entity.User;
import utils.Texts;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerManager {

    public static final String PUBLIC_ROOM_API = "228.5.6.7";

    private final Map<String, User> loginToUser = new HashMap<>();
    private final Map<String, String> loginToPasswords = new HashMap<>();

    private final Map<Integer, User> sessionToUser = new HashMap<>();

    private final Server server;

    private AtomicInteger lastUserId = new AtomicInteger(1);

    public ServerManager(Server server) {
        this.server = server;
    }

    public void acceptRequest(int sessionId, BaseRequest request) {
        System.out.println("accept request <-- " + sessionId);
        if (!sessionToUser.containsKey(sessionId)) {
            // TODO: don't break session, send error back
            if (!(request instanceof LoginCmd.Request)) {
                System.out.println("first request must be LOGIN request");
                server.rejectSession(sessionId, true);
                return;
            }
        }
        handleRequest(sessionId, request);
    }

    private void handleRequest(int sessionId, BaseRequest request) {
        if (request instanceof LoginCmd.Request) {
            onLogin(sessionId, (LoginCmd.Request) request);
        } else if (request instanceof MessageSendCmd.Request) {
            onMessage(sessionId, (MessageSendCmd.Request) request);
        } else {
            System.out.println("opcode unknown request = " + request.getOpcode());
        }
    }

    private void onLogin(int sessionId, LoginCmd.Request request) {
        System.out.println("onLogin: " + request);
        if (Texts.isEmpty(request.login) || Texts.isEmpty(request.password)) {
            server.rejectSession(sessionId, true);
            return;
        }
        User user;
        if (loginToUser.containsKey(request.login)) {
            if (!loginToPasswords.get(request.login).equals(request.password)) {
                System.out.println("onLogin:Error: wrong password for = " + request.login);
                server.rejectSession(sessionId, true);
                return;
            }
            user = loginToUser.get(request.login);
        } else {
            System.out.println("onLogin: unknown user: register with login = " + request.login);
            loginToPasswords.put(request.login, request.password);
            user = new User(lastUserId.getAndAdd(1), request.login);
            loginToUser.put(request.login, user);
        }
        sessionToUser.put(sessionId, user);
        server.controller.onLogin(user);
        // Send success feedback to client
        sendLoginAccepted(sessionId, user);
    }

    private void sendLoginAccepted(int sessionId, User user) {
        List<Chat> subscribedChats = server.controller.getUserSubscribedChats(user);
        List<Chat> availableChats = server.controller.availableChats;
        availableChats.removeAll(subscribedChats);
        server.api.login(sessionId, user, subscribedChats, availableChats);
    }

    private void onMessage(int sessionId, MessageSendCmd.Request request) {
        System.out.println("onMessage: = " + request);
        User sender = sessionToUser.get(sessionId);
        Message message = server.controller.onMessage(request.chatId, sender, request.text);
        // Broadcast all other client
        server.api.broadcastMessage(server.controller.getChatAddress(request.chatId), request.chatId, message);
    }

    public void onClose(int sessionId) {
        sessionToUser.remove(sessionId);
    }

}
