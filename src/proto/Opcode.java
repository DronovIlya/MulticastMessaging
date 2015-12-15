package proto;

import java.util.HashMap;

public enum Opcode {

    NOP(0x00),

    LOGIN(0x01),

    MESSAGE_SEND(0x10);

    private static final HashMap<Short, String> NAMES = new HashMap<>();

    static {
        for (Opcode opcode : values()) {
            NAMES.put(opcode.value, opcode.name());
        }
    }

    private final short value;

    Opcode(int value) {
        this.value = (short) value;
    }

    public short value() {
        return value;
    }

    public static String name(short value) {
        String name = NAMES.get(value);
        return name != null ? name : "0x" + Integer.toHexString(value);
    }
}

