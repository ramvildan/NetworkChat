public class Message {
    private final String text;
    private final String username;

    Message(String text, String username) {
        this.text = text;
        this.username = username;
    }

    @Override
    public String toString() {
        return this.username + ": " + this.text;
    }

    public String getUsername() {
        return this.username;
    }

    public String getText() {
        return this.text;
    }
}