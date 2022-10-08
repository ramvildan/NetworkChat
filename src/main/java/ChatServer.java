import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {

    private static final List<NewClientSession> sessionsList = Collections.synchronizedList(new ArrayList<>());
    private static final DataBase db = new DataBase();

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(4451)) {
            while (true) {
                Socket client = server.accept();

                enterAnotherClient(client);
            }
        }
    }

    public static void enterAnotherClient(Socket connection) {
        new Thread(() -> {
            NewClientSession session = new NewClientSession();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter writer = new PrintWriter(connection.getOutputStream(), true, StandardCharsets.UTF_8)) {

                String login = registrationLogin(reader, writer);

                session.setLogin(login);
                session.setConnection(connection);
                sendHistory(session);
                sessionsList.add(session);
                workOnMessage(session, reader);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                sessionsList.remove(session);
                closeSocket(connection);
            }
        }).start();
    }

    private static void sendHistory(NewClientSession newClientSession) throws IOException {
        System.out.println("send history for user:" + newClientSession.getLogin());

        for (Message message : db.getMessages()) {
            PrintWriter writer = new PrintWriter(newClientSession.getConnection().getOutputStream(), true, StandardCharsets.UTF_8);
            if (!message.getUsername().equals(newClientSession.getLogin())) {
                writer.println(message.toString());
            } else {
                writer.println(message.getText());
            }
        }
    }

    private static void workOnMessage(NewClientSession session, BufferedReader reader) {
        try {
            while (true) {
                String message = reader.readLine();
                if (message == null) break;
                if (message.isBlank()) continue;

                String username = session.getLogin();
                db.addMessage(message, username);

                for (NewClientSession clientSession : sessionsList) {
                    if (!clientSession.equals(session)) {
                        PrintWriter writer = new PrintWriter(clientSession.getConnection().getOutputStream(), true, StandardCharsets.UTF_8);
                        writer.println(username + ": " + message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String registrationLogin(BufferedReader reader, PrintWriter writer) throws IOException {
        do {
            writer.println("Hello and welcome to MyChat! Please enter your login:");
            String clientResponse = reader.readLine();

            if (clientResponse.isEmpty() || clientResponse.isBlank() || clientResponse.length() >= 10) {
                writer.println("This login is illegal, please try again...");
                continue;
            }

            return clientResponse;

        } while (true);
    }

    private static void closeSocket(Socket client) {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
