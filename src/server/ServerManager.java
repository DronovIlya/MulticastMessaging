package server;

import commands.LoginCmd;
import commands.MessageSendCmd;
import commands.base.BaseRequest;
import commands.entity.User;
import utils.Texts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerManager {

    public static final String PUBLIC_ROOM_API = "228.5.6.7";

    private final Map<String, User> users = new HashMap<>();
    private final Map<String, String> loginToPasswords = new HashMap<>();

    private final List<Integer> loggedSessions = new ArrayList<>();

    private final Server server;

    private AtomicInteger lastUserId = new AtomicInteger(1);

    public ServerManager(Server server) {
        this.server = server;
    }

    public void acceptRequest(int sessionId, BaseRequest request) {
        System.out.println("accept request <-- " + sessionId);
        if (!loggedSessions.contains(sessionId)) {
            // TODO: don't break session, send error back
            if (!(request instanceof LoginCmd.Request)) {
                System.out.println("first request must be LOGIN request");
                server.rejectSession(sessionId, true);
                return;
            }
        }
        handleRequest(sessionId, request);
    }

    private void handleRequest(int sessionId, BaseRequest req) {
        if (req instanceof LoginCmd.Request) {
            System.out.println("login request");
            LoginCmd.Request request = (LoginCmd.Request) req;

            if (Texts.isEmpty(request.login) || Texts.isEmpty(request.password)) {
                server.rejectSession(sessionId, true);
                return;
            }

            User user;
            if (users.containsKey(request.login)) {
                if (!loginToPasswords.get(request.login).equals(request.password)) {
                    System.out.println("wrong password");
                    server.rejectSession(sessionId, true);
                    return;
                }
                user = users.get(request.login);
            } else {
                loginToPasswords.put(request.login, request.password);
                user = new User(lastUserId.getAndAdd(1), request.login);
            }
            loggedSessions.add(sessionId);
            server.api.login(sessionId, user, PUBLIC_ROOM_API);
        } else if (req instanceof MessageSendCmd.Request) {
            System.out.println("message send request");
            MessageSendCmd.Request request = (MessageSendCmd.Request) req;
            System.out.println("receive message = " + request.message + ", from = " + request.message.sender);
            server.api.broadcastMessage(request.message);
        } else {
            System.out.println("opcode unknown request = " + req.getOpcode());
        }
    }

}
