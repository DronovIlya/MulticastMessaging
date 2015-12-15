package commands.base;

import commands.LoginCmd;
import commands.MessageSendCmd;
import proto.Opcode;
import utils.MsgPackUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRequest extends BaseCommand {

    public static BaseRequest decode(short opcode, byte[] buffer) {
        Map<String, Object> result = deserialize(buffer);
        if (opcode == Opcode.LOGIN.value()) {
            return LoginCmd.Request.newInstance(result);
        } else if (opcode == Opcode.MESSAGE_SEND.value()) {
            return MessageSendCmd.Request.newInstance(result);
        }

        // TODO: add IllegalStateException
        return null;
    }

}
