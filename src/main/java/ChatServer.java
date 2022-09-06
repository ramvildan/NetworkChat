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

            greetingsToAll(client);

            userName = reader.readLine();

            toAllClients(reader, userName);

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

    private static void greetingsToAll(Socket client) throws IOException {
        Writer writeGreeting = new OutputStreamWriter(client.getOutputStream());
        writeGreeting.write("Hello and welcome to MyChat! Please enter your name: " +"\n");
        writeGreeting.flush();
    }

    private static void toAllClients(BufferedReader reader, String userName) {
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                if (line.isBlank()) continue;
                System.out.println(userName + ": " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
