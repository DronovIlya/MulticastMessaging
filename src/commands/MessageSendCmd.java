package commands;

import commands.base.BaseRequest;
import commands.base.BaseResponse;
import commands.entity.Message;
import proto.Opcode;

import java.util.Map;

public class MessageSendCmd {

    public static class Request extends BaseRequest {

        public final long chatId;
        public final String text;

        public Request(long chatId, String text) {
            this.chatId = chatId;
            this.text = text;
        }

        @Override
        protected void makeRequestParams() {
            addLongParam("chatId", chatId);
            addStringParam("text", text);
        }

        @Override
        public String toString() {
            return "Request{" +
                    "chatId=" + chatId +
                    ", text='" + text + '\'' +
                    '}';
        }

        @Override
        public short getOpcode() {
            return Opcode.MESSAGE_SEND.value();
        }

        public static Request newInstance(Map<String, Object> data) {
            if (data != null) {
                return new Request(
                        (long) data.get("chatId"),
                        (String) data.get("text")
                );
            }
            return null;
        }
    }

    public static class Response extends BaseResponse {

        public final long chatId;
        public final Message message;

        public Response(long chatId, Message message) {
            this.chatId = chatId;
            this.message = message;
        }

        @Override
        protected void makeRequestParams() {
            addLongParam("chatId", chatId);
            addMap("message", message.makeParams());
        }

        @Override
        public String toString() {
            return "Response{" +
                    "chatId=" + chatId +
                    ", message=" + message +
                    '}';
        }

        @Override
        public short getOpcode() {
            return Opcode.MESSAGE_SEND.value();
        }

        public static Response newInstance(Map<String, Object> data) {
            if (data != null) {
                return new Response(
                        (long) data.get("chatId"),
                        Message.newInstance((Map<String, Object>) data.get("message"))
                );
            }
            return null;
        }
    }
}
