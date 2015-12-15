package commands;

import commands.base.BaseRequest;
import commands.base.BaseResponse;
import commands.entity.Message;
import proto.Opcode;

import java.util.Map;

public class MessageSendCmd {

    public static class Request extends BaseRequest {

        public final Message message;

        public Request(Message message) {
            this.message = message;
        }

        @Override
        protected void makeRequestParams() {
            addMap("message", message.makeParams());
        }

        @Override
        public String toString() {
            return "Request{" +
                    "message=" + message +
                    '}';
        }

        @Override
        public short getOpcode() {
            return Opcode.MESSAGE_SEND.value();
        }

        public static Request newInstance(Map<String, Object> data) {
            if (data != null) {
                return new Request(
                        Message.newInstance((Map<String, Object>) data.get("message"))
                );
            }
            return null;
        }
    }

    public static class Response extends BaseResponse {

        public final Message message;

        public Response(Message message) {
            this.message = message;
        }

        @Override
        protected void makeRequestParams() {
            addMap("message", message.makeParams());
        }

        @Override
        public String toString() {
            return "Request{" +
                    "message=" + message +
                    '}';
        }

        @Override
        public short getOpcode() {
            return Opcode.MESSAGE_SEND.value();
        }

        public static Response newInstance(Map<String, Object> data) {
            if (data != null) {
                return new Response(
                        Message.newInstance((Map<String, Object>) data.get("message"))
                );
            }
            return null;
        }
    }
}
