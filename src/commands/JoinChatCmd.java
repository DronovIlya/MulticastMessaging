package commands;

import commands.base.BaseCommand;
import commands.base.BaseRequest;
import commands.base.BaseResponse;
import commands.entity.Chat;
import proto.Opcode;

import java.util.Map;

public class JoinChatCmd {

    public static class Request extends BaseRequest {

        public final long chatId;

        public Request(long chatId) {
            this.chatId = chatId;
        }

        @Override
        protected void makeRequestParams() {
            addLongParam("chatId", chatId);
        }

        @Override
        public String toString() {
            return "Request{" +
                    "chatId=" + chatId +
                    '}';
        }

        @Override
        public short getOpcode() {
            return Opcode.JOIN_CHAT.value();
        }

        public static Request newInstance(Map<String, Object> data) {
            if (data != null) {
                return new Request(
                        (long) data.get("chatId")
                );
            }
            return null;
        }
    }

    public static class Response extends BaseResponse {

        public final Chat chat;

        public Response(Chat chat) {
            this.chat = chat;
        }

        @Override
        protected void makeRequestParams() {
            addMap("chat", chat.makeParams());
        }

        @Override
        public String toString() {
            return "Response{" +
                    "chat=" + chat +
                    '}';
        }

        @Override
        public short getOpcode() {
            return Opcode.JOIN_CHAT.value();
        }

        public static Response newInstance(Map<String, Object> data) {
            if (data != null) {
                return new Response(
                        Chat.newInstance((Map<String, Object>) data.get("chat"))
                );
            }
            return null;
        }
    }
}
