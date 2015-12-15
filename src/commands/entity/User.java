package commands.entity;

import java.util.HashMap;
import java.util.Map;

public class User {

    public final long id;
    public final String name;

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + name + '\'' +
                '}';
    }

    public Map<String, Object> makeParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("name", name);
        return params;
    }

    public static User newInstance(Map<String, Object> data) {
        if (data != null) {
            return new User(
                    (Long)data.get("id"),
                    (String)data.get("name")
            );
        }
        return null;
    }
}
