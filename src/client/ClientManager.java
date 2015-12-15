package client;

import commands.LoginCmd;
import commands.MessageSendCmd;
import commands.base.BaseResponse;
import commands.entity.Message;
import commands.entity.User;

public class ClientManager {

    public User self;
    public String publicAddressRoom;

    private final Client client;

    public ClientManager(Client client) {
        this.client = client;
    }

    public void handleResponse(BaseResponse response) {
        if (response instanceof LoginCmd.Response) {
            onLogin(((LoginCmd.Response) response).user, ((LoginCmd.Response) response).publicRoomAddress);
            client.onLoggedIn();
        } else if (response instanceof MessageSendCmd.Response) {
            onMessage(((MessageSendCmd.Response) response).message);
        }
    }

    private void onLogin(User user, String publicAddressRoom) {
        this.self = user;
        this.publicAddressRoom = publicAddressRoom;
        client.startUdpListener(publicAddressRoom);
    }

    private void onMessage(Message message) {
        System.out.println("Incoming message : " + message);
    }
}
