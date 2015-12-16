package client;

import commands.LoginCmd;
import commands.MessageSendCmd;
import commands.base.BaseResponse;
import commands.entity.Chat;
import commands.entity.Message;
import commands.entity.User;

import java.util.List;

public class ClientManager {

    public User self;
    public List<Chat> subscribedChats;
    public List<Chat> availableChats;

    private final Client client;

    public ClientManager(Client client) {
        this.client = client;
    }

    public void handleResponse(BaseResponse response) {
        if (response instanceof LoginCmd.Response) {
            onLogin((LoginCmd.Response) response);
        } else if (response instanceof MessageSendCmd.Response) {
            onMessage(((MessageSendCmd.Response) response).message);
        }
    }

    private void onLogin(LoginCmd.Response response) {
        System.out.println("onLogin: response = " + response);
        this.self = response.user;
        this.subscribedChats = response.subscribedChat;
        this.availableChats = response.availableChats;

        client.onLoggedIn();
        client.startUdpListener(subscribedChats);
    }

    private void onMessage(Message message) {
        System.out.println("onMessage: " + message);
    }
}
