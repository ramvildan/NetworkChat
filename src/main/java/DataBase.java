import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataBase {
    private final List<Message> messages = new ArrayList<>();

    public void addMessage(String message, String username) {
//        System.out.println("Add message");
        messages.add(new Message(message, username));
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}
