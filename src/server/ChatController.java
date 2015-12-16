package server;

import commands.entity.Chat;
import commands.entity.Message;
import commands.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController {

    private final Server server;

    public final Chat publicChat;
    public final Map<Long, Chat> availableChats;

    public final Map<Long, List<User>> chatsToUsers = new HashMap<>();
    public final Map<Long, List<Chat>> usersToChats = new HashMap<>();

    public final Map<Long, List<Message>> chatToMessages = new HashMap<>();

    public ChatController(Server server) {
        this.server = server;
        publicChat = new Chat(0, "228.5.6.7", "Public chat");
        availableChats = generateStubChats();
    }

    private Map<Long, Chat> generateStubChats() {
        Map<Long, Chat> result = new HashMap<>();
        result.put(0l, publicChat);
        result.put(1l, new Chat(1, "228.6.5.7", "Itmo party"));
        result.put(2l, new Chat(2, "228.1.5.7", "Developer chat"));
        return result;
    }

    /**
     * After successful login, put user to public chat
     */
    public void onLogin(User user) {
       addUserToChat(publicChat, user);
    }

    public void addUserToChat(long chatId, User user) {
        addUserToChat(getChat(chatId), user);
    }

    public void addUserToChat(Chat chat, User user) {
        List<User> users = chatsToUsers.get(chat.id);
        if (users == null) {
            users = new ArrayList<>();
            chatsToUsers.put(chat.id, users);
        }
        users.add(user);

        List<Chat> chats = usersToChats.get(user.id);
        if (chats == null) {
            chats = new ArrayList<>();
            usersToChats.put(user.id, chats);
        }
        chats.add(chat);
    }

    public List<Chat> getUserSubscribedChats(User user) {
        return usersToChats.get(user.id);
    }

    public Message onMessage(long chatId, User user, String text) {
        Message incomingMessage;
        List<Message> messages = chatToMessages.get(chatId);
        long id = 0;
        if (messages == null) {
            messages = new ArrayList<>();
        } else {
            id = messages.get(messages.size() - 1).id;
        }

        incomingMessage = new Message(id + 1, user, text);
        messages.add(incomingMessage);
        return incomingMessage;
    }

    public String getChatAddress(long chatId) {
        return getChat(chatId).address;
    }

    public Chat getChat(long chatId) {
        return availableChats.get(chatId);
    }

    public List<Chat> getAvailableChats() {
        return new ArrayList<>(availableChats.values());
    }
}
