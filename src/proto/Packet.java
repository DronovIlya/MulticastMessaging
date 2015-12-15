package proto;

import commands.base.BaseCommand;
import commands.base.BaseRequest;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Packet {

    public static final int HEADER_SIZE = 6;

    private short opcode;
    private int payloadLength;
    private byte[] payload;

    public Packet(byte[] header) {
        ByteBuffer buffer = ByteBuffer.wrap(header);
        opcode = buffer.getShort();
        payloadLength = buffer.getInt();
        if (payloadLength > 0) {
            this.payload = new byte[payloadLength];
        }
    }

    public <T extends BaseCommand> Packet(T request) {
        this.opcode = request.getOpcode();
        this.payload = request.getPayload();
        this.payloadLength = payload.length;
    }

    public short getOpcode() {
        return opcode;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE + payloadLength);
        buffer.putShort(opcode);
        buffer.putInt(payloadLength);
        if (payloadLength > 0) {
            buffer.put(payload);
        }
        return buffer.array();
    }

    @Override
    public String toString() {
        return "Packet{" +
                "opcode=" + opcode +
                ", payloadLength=" + payloadLength +
                ", payload=" + Arrays.toString(payload) +
                '}';
    }
}
