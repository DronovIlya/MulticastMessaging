package client;

import commands.JoinChatCmd;
import commands.LeaveChatCmd;
import commands.LoginCmd;
import commands.MessageSendCmd;
import commands.entity.Message;
import commands.entity.User;

public class ClientApi {

    private final Client client;

    public ClientApi(Client client) {
        this.client = client;
    }

    public void login(String login, String password) {
        client.sendRequest(new LoginCmd.Request(login, password));
    }

    public void messageSend(long chatId, String text) {
        client.sendRequest(new MessageSendCmd.Request(chatId, text));
    }

    public void joinChat(long chatId) {
        client.sendRequest(new JoinChatCmd.Request(chatId));
    }

    public void leaveChat(long chatId) {
        client.sendRequest(new LeaveChatCmd.Request(chatId));
    }
}
