package commands.base;

import commands.JoinChatCmd;
import commands.LeaveChatCmd;
import commands.LoginCmd;
import commands.MessageSendCmd;
import proto.Opcode;
import utils.MsgPackUtils;

import java.util.Map;

public abstract class BaseResponse extends BaseCommand {

    public static BaseResponse decode(short opcode, byte[] buffer) {
        Map<String, Object> result = deserialize(buffer);
        if (opcode == Opcode.LOGIN.value()) {
            return LoginCmd.Response.newInstance(result);
        } else if (opcode == Opcode.MESSAGE_SEND.value()) {
            return MessageSendCmd.Response.newInstance(result);
        } else if (opcode == Opcode.JOIN_CHAT.value()) {
            return JoinChatCmd.Response.newInstance(result);
        } else if (opcode == Opcode.LEAVE_CHAT.value()) {
            return LeaveChatCmd.Response.newInstance(result);
        } else {
            throw new RuntimeException("Cannot decode opcode = " + opcode);
        }
    }
}
