package server;

import commands.LoginCmd;
import commands.MessageSendCmd;
import commands.base.BaseResponse;
import commands.entity.Chat;
import commands.entity.Message;
import commands.entity.User;

import java.util.List;

public class ServerApi {

    private final Server server;

    public ServerApi(Server server) {
        this.server = server;
    }

    public void login(int sessionId, User user, List<Chat> subscribedUserChats, List<Chat> availableChats) {
        server.sendResponse(sessionId, new LoginCmd.Response(user, subscribedUserChats, availableChats));
    }

    public void broadcastMessage(String chatAddress, long chatId, Message message) {
        server.sendBroadcast(chatAddress, new MessageSendCmd.Response(chatId, message));
    }
}
