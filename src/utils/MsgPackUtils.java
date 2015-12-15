package utils;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.ImmutableFloatValue;
import org.msgpack.value.MapValue;
import org.msgpack.value.Value;
import org.msgpack.value.impl.ImmutableBooleanValueImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Util for external library MessagePack
 */
public class MsgPackUtils {

    public static Object deserialize(byte[] data) {
        try {
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(data);
            return parseValue(unpacker.unpackValue());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object parseValue(Value value) throws IOException {
        switch (value.getValueType()) {
            case NIL:
                return null;
            case STRING:
                return value.asStringValue().asString();
            case INTEGER:
                return value.asIntegerValue().asLong();
            case BOOLEAN:
                return ((ImmutableBooleanValueImpl) value).getBoolean();
            case FLOAT:
                return ((ImmutableFloatValue) value).toFloat();
            case ARRAY:
                return parseArray(value.asArrayValue());
            case MAP:
                return parseMap(value.asMapValue());
            case BINARY:
                return value.asBinaryValue().asByteBuffer();
            default:
                throw new RuntimeException("Type " + value.getValueType().name() + " isn't yet implemented");
        }
    }

    private static Object parseArray(ArrayValue arrayValue) throws IOException {
        int size = arrayValue.size();
        ArrayList<Object> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(parseValue(arrayValue.get(i)));
        }
        return result;
    }

    public static Map<Object, Object> parseMap(MapValue mapValue) throws IOException {
        int size = mapValue.size();
        Map<Object, Object> map = new HashMap<>(size);
        for (Map.Entry<Value, Value> entry : mapValue.entrySet()) {
            map.put(parseValue(entry.getKey()), parseValue(entry.getValue()));
        }
        return map;
    }

    public static void serialize(Map<String, Object> params, ByteArrayOutputStream out) throws IOException {
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        try {
            packer.packMapHeader(params.size());

            for (String name : params.keySet()) {
                Object value = params.get(name);
                packer.packString(name);
                packObject(packer, value);
            }
        } finally {
            packer.close();
        }
    }

    private static void packObject(MessagePacker packer, Object value) throws IOException {
        if (value instanceof String) {
            packer.packString((String) value);
        } else if (value instanceof Integer) {
            packer.packInt((Integer) value);
        } else if (value instanceof Long) {
            packer.packLong((Long) value);
        } else if (value instanceof Boolean) {
            packer.packBoolean((Boolean) value);
        } else if (value instanceof List) {
            packList(packer, (List<Object>) value);
        } else if (value instanceof Map) {
            packMap(packer, (Map<Object, Object>) value);
        } else if (value instanceof long[]) {
            packLongArray(packer, (long[]) value);
        } else {
            throw new RuntimeException("type " + value.getClass().getSimpleName() + " isn't yet implemented");
        }
    }

    private static void packLongArray(MessagePacker packer, long[] value) throws IOException {
        packer.packArrayHeader(value.length);
        for (long l : value) {
            packer.packLong(l);
        }
    }

    private static void packList(MessagePacker packer, List<Object> list) throws IOException {
        packer.packArrayHeader(list.size());
        for (Object item : list) {
            packObject(packer, item);
        }
    }

    private static void packMap(MessagePacker packer, Map<Object, Object> map) throws IOException {
        packer.packMapHeader(map.size());
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            packObject(packer, entry.getKey());
            packObject(packer, entry.getValue());
        }
    }
}
