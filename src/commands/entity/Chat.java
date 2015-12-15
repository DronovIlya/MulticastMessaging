package commands.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Chat {

    public final int id;
    public final String title;
    public final List<User> participants;

    public Chat(int id, String title, List<User> participants) {
        this.id = id;
        this.title = title;
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", participants=" + participants +
                '}';
    }

    public static Chat newInstance(Map<String, Object> data) {
        if (data != null) {
            int id = (int) data.get("id");
            String title = (String) data.get("title");

            List<User> participants = new ArrayList<>();
            List list = (List) data.get("participants");
            for (Object obj : list) {
                participants.add(User.newInstance((Map<String, Object>) obj));
            }
            return new Chat(id, title, participants);
        }
        return null;
    }
}
