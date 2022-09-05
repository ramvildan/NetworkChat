import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {

    private static List<String> userName = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(4451)) {
            while (true) {
                Socket client = server.accept();

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

            String greetings = "Hello and welcome to MyChat! Please enter your name: ";
            writer.write(greetings+"\n");
            writer.flush();

            String name = reader.readLine();
            userName.add(name);

            toAllClients(writer, reader, name);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
            }
        }
    }

    private static void toAllClients(Writer writer, BufferedReader reader, String name) {
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                if (line.isBlank()) continue;
                System.out.println(userName.get(userName.lastIndexOf(name)) + ": " + line);
                writer.write(userName.get(userName.lastIndexOf(name)) + ": " + line);
                writer.write("\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
