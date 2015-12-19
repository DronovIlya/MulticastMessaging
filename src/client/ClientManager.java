package client;

import commands.JoinChatCmd;
import commands.LoginCmd;
import commands.MessageSendCmd;
import commands.base.BaseResponse;
import commands.entity.Chat;
import commands.entity.User;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientManager {

    public User self;
    public List<Chat> subscribedChats;
    public List<Chat> availableChats;

    private final Client client;

    private Map<Long, MyPanel> panels;

    public ClientManager(Client client) {
        this.client = client;
        panels = new HashMap<>();
    }

    public void handleResponse(BaseResponse response) {
        if (response instanceof LoginCmd.Response) {
            onLogin((LoginCmd.Response) response);
        } else if (response instanceof MessageSendCmd.Response) {
            onMessage(((MessageSendCmd.Response) response));
        } else if (response instanceof JoinChatCmd.Response) {
            onJoinChat((JoinChatCmd.Response) response);
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

    private void onMessage(MessageSendCmd.Response response) {
        panels.get(response.chatId).addNewLine(response);
        System.out.println("onMessage: " + response.message);
    }

    private void onJoinChat(JoinChatCmd.Response response) {
        System.out.println("onJoinChat: start listening chat = " + response.chat);
        subscribedChats.add(response.chat);
        client.joinChat(response.chat.address);
        createFrame(response.chat.id);
    }

    private void createFrame(long id) {
        JFrame f = new JFrame("" + id);
        f.setMinimumSize(new Dimension(500,500));
        MyPanel panel = new MyPanel(client, id);
        f.add(panel);
        f.pack();
        f.setVisible(true);
        panels.put(id, panel);
    }
}
