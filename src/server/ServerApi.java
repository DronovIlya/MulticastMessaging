package server;

import commands.LoginCmd;
import commands.MessageSendCmd;
import commands.entity.Message;
import commands.entity.User;

public class ServerApi {

    private final Server server;

    public ServerApi(Server server) {
        this.server = server;
    }

    public void login(int sessionId, User user, String publicRoomAddress) {
        server.sendResponse(sessionId, new LoginCmd.Response(user, publicRoomAddress));
    }

    public void broadcastMessage(Message message) {
        server.sendBroadcast(new MessageSendCmd.Request(message));
    }
}
