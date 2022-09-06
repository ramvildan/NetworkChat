import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {

    private static String userName;
    private static final List<Socket> allClients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(4451)) {
            while (true) {
                Socket client = server.accept();
                allClients.add(client);

                new Thread(() -> {
                    anotherClient(client);
                }).start();
            }
        }
    }

    private static void anotherClient(Socket client) {
        try {
            Writer writer = new OutputStreamWriter(client.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            greetingsToAll(writer);

            userName = reader.readLine();

            sendMessage(reader, writer, userName);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void greetingsToAll(Writer writer) throws IOException {
        writer.write("Hello and welcome to MyChat! Please enter your name: " + "\n");
        writer.flush();
    }

    private static void sendMessage(BufferedReader reader, Writer writer, String userName) {
        try {
            for (Socket client : allClients) {
                while (true) {
                    String message = reader.readLine();
                    if (message == null) break;
                    if (message.isBlank()) continue;
                    writer.write(userName + ": " + message);
                    writer.write("\n");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
