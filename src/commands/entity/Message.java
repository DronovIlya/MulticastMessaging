package commands.entity;

import java.util.HashMap;
import java.util.Map;

public class Message {

    public final long id;
    public final User sender;
    public final String text;

    public Message(long id, User sender, String text) {
        this.id = id;
        this.sender = sender;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender=" + sender +
                ", text='" + text + '\'' +
                '}';
    }

    public Map<String, Object> makeParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("sender", sender.makeParams());
        params.put("text", text);
        return params;
    }

    public static Message newInstance(Map<String, Object> data) {
        if (data != null) {
            return new Message(
                    (Long)data.get("id"),
                    User.newInstance((Map<String, Object>) data.get("sender")),
                    (String)data.get("text")
            );
        }
        return null;
    }
}
