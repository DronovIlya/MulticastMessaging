package commands.lists;

import commands.entity.Chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ChatList extends ArrayList<Chat> {

    public ChatList(int initialCapacity) {
        super(initialCapacity);
    }

    public ChatList() {
        super();
    }

    public ChatList(Collection<? extends Chat> c) {
        super(c);
    }

    public static ChatList newInstance(List data) {
        ChatList result = new ChatList();
        if (data != null) {
            for (Object chat : data) {
                result.add(Chat.newInstance((Map<String, Object>) chat));
            }
        }
        return result;
    }
}
