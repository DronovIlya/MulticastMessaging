package commands;

import commands.base.BaseRequest;
import commands.base.BaseResponse;
import commands.entity.Chat;
import commands.entity.User;
import commands.lists.ChatList;
import proto.Opcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginCmd {

    public static class Request extends BaseRequest {

        public String login;
        public String password;

        public Request(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        protected void makeRequestParams() {
            addStringParam("login", login);
            addStringParam("password", password);
        }

        @Override
        public String toString() {
            return "Request{" +
                    "login='" + login + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }

        @Override
        public short getOpcode() {
            return Opcode.LOGIN.value();
        }

        public static Request newInstance(Map<String, Object> data) {
            if (data != null) {
                return new Request(
                        (String)data.get("login"),
                        (String)data.get("password")
                );
            }
            return null;
        }
    }

    public static class Response extends BaseResponse {

        public final User user;
        public final List<Chat> subscribedChat;
        public final List<Chat> availableChats;

        public Response(User user, List<Chat> subscribedChat, List<Chat> availableChats) {
            this.user = user;
            this.subscribedChat = subscribedChat;
            this.availableChats = availableChats;
        }

        public static Response newInstance(Map<String, Object> data) {
            System.out.println(data);
            if (data != null) {
                return new Response(
                        User.newInstance((Map<String, Object>) data.get("user")),
                        ChatList.newInstance((List) data.get("subscribedChat")),
                        ChatList.newInstance((List) data.get("availableChats"))
                );
            }
            return null;
        }

        @Override
        protected void makeRequestParams() {
            addMap("user", user.makeParams());

            List<Object> result = new ArrayList<>();
            for (Chat chat : subscribedChat) {
                result.add(chat.makeParams());
            }
            addList("subscribedChat", result);

            result = new ArrayList<>();
            for (Chat chat : availableChats) {
                result.add(chat.makeParams());
            }
            addList("availableChats", result);
        }

        @Override
        public String toString() {
            return "Response{" +
                    "user=" + user +
                    ", subscribedChat=" + subscribedChat +
                    ", availableChats=" + availableChats +
                    '}';
        }

        @Override
        public short getOpcode() {
            return Opcode.LOGIN.value();
        }
    }
}
