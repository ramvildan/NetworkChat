import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientApp {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 4451)) {
            socketReader(socket);
            consoleReader(socket);
        }
    }

    private static void consoleReader(Socket socket) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = reader.readLine();
            writer.println(line);
        }
    }

    private static void socketReader(Socket socket) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    if (line.isBlank()) continue;
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }).start();
    }
}
