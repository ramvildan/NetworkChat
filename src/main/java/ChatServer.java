import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {

    private static final List<NewClientSession> allSessionsList = Collections.synchronizedList(new ArrayList<>());
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

                enterLogin(session, reader, writer);

                allSessionsList.add(session);

                sendMassages(session, reader, writer);


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
                    allSessionsList.remove(session);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void sendMassages(NewClientSession session, BufferedReader reader, PrintWriter writer) {
        try {
            while (true) {
                String message = reader.readLine();
                if (message == null) break;
                if (message.isBlank()) continue;

                for (NewClientSession thisSession : allSessionsList) {
                    if (thisSession.equals(session)) {
                        writer.println(session.getLogin() + ": " + message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void enterLogin(NewClientSession session, BufferedReader reader, PrintWriter writer) throws IOException {
        do {
            writer.println("Hello and welcome to MyChat! Please enter your login:");
            String clientResponse = reader.readLine();
            if (clientResponse == null) {
                throw new IOException();
            }
            if (!clientResponse.isBlank() && clientResponse.length() <= 10) {
                session.setLogin(clientResponse);
                break;
            }

            writer.println("This login is illegal, please try again...");
        } while (true);
    }
}
