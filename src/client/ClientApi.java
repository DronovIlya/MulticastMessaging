package client;

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

    public void messageSend(long chatId, User sender, String text) {
        client.sendRequest(new MessageSendCmd.Request(new Message(chatId, sender, text)));
    }
}