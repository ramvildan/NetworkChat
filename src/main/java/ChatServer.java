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
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String greetings = "Hello and welcome to MyChat! Please enter your name: ";
            writer.write(greetings+"\n");
            writer.flush();
            String name = reader.readLine();
            userName.add(name);

            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                writer.write(userName.get(userName.indexOf(name)) + ": " + line + "\n");
                writer.flush();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
            }
        }
    }
}
