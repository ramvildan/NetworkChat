import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {

    private static final List<Socket> allClientsList = Collections.synchronizedList(new ArrayList<>());
    private final MassageList massages = new MassageList();

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(4451)) {
            while (true) {
                Socket client = server.accept();

                anotherClient(client);
            }
        }
    }

    public static void anotherClient(Socket client) {
        new Thread(() -> {
            NewClientSession session = new NewClientSession();
            BufferedReader reader = null;
            PrintWriter writer = null;

            try {
                reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                writer = new PrintWriter(client.getOutputStream(), true, StandardCharsets.UTF_8);

                String login = registrationLogin(reader, writer);

                if (login != null) {
//                sendHistory();

                    allClientsList.add(client);

                    sendMassages(session, client, reader);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.close();
                    if (reader != null) {
                        reader.close();
                    }
                    if (writer != null) {
                        writer.close();
                    }
                    allClientsList.remove(client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void sendMassages(NewClientSession session, Socket client, BufferedReader reader) {
        try {
            while (true) {
                String message = reader.readLine();
                if (message == null) break;
                if (message.isBlank()) continue;

                for (Socket thisClient : allClientsList) {
                    if (!thisClient.equals(client)) {
                        PrintWriter writer = new PrintWriter(thisClient.getOutputStream(), true, StandardCharsets.UTF_8);
                        writer.println(session.getLogin() + ": " + message);
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
            if (clientResponse == null) {
                break;
            }
            if (!clientResponse.isBlank() && clientResponse.length() <= 10) {
                return clientResponse;
            }

            writer.println("This login is illegal, please try again...");
        } while (true);

        return null;
    }
}
