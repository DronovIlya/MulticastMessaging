package commands;

import commands.base.BaseRequest;
import commands.base.BaseResponse;
import commands.entity.User;
import proto.Opcode;

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
        public final String publicRoomAddress;

        public Response(User user, String publicRoomAddress) {
            this.user = user;
            this.publicRoomAddress = publicRoomAddress;
        }

        public static Response newInstance(Map<String, Object> data) {
            if (data != null) {
                return new Response(
                        User.newInstance((Map<String, Object>) data.get("user")),
                        (String)data.get("publicRoomAddress")
                );
            }
            return null;
        }

        @Override
        protected void makeRequestParams() {
            addMap("user", user.makeParams());
            addStringParam("publicRoomAddress", publicRoomAddress);
        }


        @Override
        public String toString() {
            return "Response{" +
                    "user=" + user +
                    ", publicRoomAddress='" + publicRoomAddress + '\'' +
                    '}';
        }

        @Override
        public short getOpcode() {
            return Opcode.LOGIN.value();
        }
    }
}
