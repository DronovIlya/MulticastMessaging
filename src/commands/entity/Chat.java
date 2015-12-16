package commands.entity;

import java.util.HashMap;
import java.util.Map;

public class Chat {

    public final long id;
    public final String address;
    public final String title;

    public Chat(long id, String address, String title) {
        this.id = id;
        this.title = title;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public Map<String, Object> makeParams() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("address", address);
        result.put("title", title);
        return result;
    }

    public static Chat newInstance(Map<String, Object> data) {
        if (data != null) {
            long id = (long) data.get("id");
            String title = (String) data.get("title");
            String address = (String)data.get("address");
            return new Chat(id, address, title);
        }
        return null;
    }
}
