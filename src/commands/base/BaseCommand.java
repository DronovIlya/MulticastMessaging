package commands.base;

import utils.MsgPackUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseCommand {

    private Map<String, Object> mParams = new HashMap<>();

    public byte[] getPayload() {
        makeRequestParams();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MsgPackUtils.serialize(mParams, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void makeRequestParams();

    public abstract short getOpcode();

    protected void addStringParam(String name, String value) {
        mParams.put(name, value);
    }

    protected void addIntParam(String name, int value) {
        mParams.put(name, value);
    }

    protected void addLongParam(String name, long value) {
        mParams.put(name, value);
    }

    protected void addMap(String name, Map map) {
        mParams.put(name, map);
    }

    protected void addList(String name, List list) {
        mParams.put(name, list);
    }

    protected static Map<String, Object> deserialize(byte[] bytes) {
        return (Map<String, Object>) MsgPackUtils.deserialize(bytes);
    }

}
