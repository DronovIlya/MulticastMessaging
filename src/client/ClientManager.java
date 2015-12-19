package client;

import client.ui.ChatView;
import client.ui.GroupsView;
import commands.JoinChatCmd;
import commands.LeaveChatCmd;
import commands.LoginCmd;
import commands.MessageSendCmd;
import commands.base.BaseResponse;
import commands.entity.Chat;
import commands.entity.User;

import java.util.List;

public class ClientManager {

    private final Client client;
    public User self;
    public List<Chat> subscribedChats;
    public List<Chat> availableChats;

    public ClientManager(Client client) {
        this.client = client;
    }

    public void handleResponse(BaseResponse response) {
        if (response instanceof LoginCmd.Response) {
            onLogin((LoginCmd.Response) response);
        } else if (response instanceof MessageSendCmd.Response) {
            onMessage(((MessageSendCmd.Response) response));
        } else if (response instanceof JoinChatCmd.Response) {
            onJoinChat((JoinChatCmd.Response) response);
        } else if (response instanceof LeaveChatCmd.Response) {
            onLeaveChat((LeaveChatCmd.Response) response);
        }
    }

    private void onLogin(LoginCmd.Response response) {
        System.out.println("onLogin: response = " + response);
        this.self = response.user;
        this.subscribedChats = response.subscribedChat;
        this.availableChats = response.availableChats;
        client.onLoggedIn();
        client.startUdpListener(subscribedChats);
        GroupsView.runUI(client, availableChats);
    }


    private void onMessage(MessageSendCmd.Response response) {
        if (response.message.sender.id == self.id) {
            System.out.println("onMessage: ignoring self message = " + response.message);
        } else {
            System.out.println("onMessage: " + response.message);
            ChatView curUI = ChatView.clientUIMap.get(response.chatId);
            curUI.appendMessage(response.message.sender.name, response.message.text);
        }
    }

    private void onJoinChat(JoinChatCmd.Response response) {
        System.out.println("onJoinChat: start listening chat = " + response.chat);
        subscribedChats.add(response.chat);
        client.joinChat(response.chat.address);
        ChatView.runUI(client, response.chat);
    }

    private void onLeaveChat(LeaveChatCmd.Response response) {
        System.out.println("onLeaveChat: stop listening chat = " + response.chat);
        subscribedChats.remove(response.chat);
        client.leaveChat(response.chat.address);
    }
}
