import java.net.Socket;

import static java.util.Objects.requireNonNull;

public class NewClientSession {

    private String login;
    private Socket connection;

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        requireNonNull(login, "login shouldn't be null");
        if (this.login != null) {
            throw new IllegalStateException("login change isn't allowed");
        }
        this.login = login;
    }

    public Socket getConnection() {
        return connection;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }
}

