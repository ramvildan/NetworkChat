import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NewClient {

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 4451)) {
            Writer writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            new Thread(() -> {
                socketReader(socket);
                System.exit(0);
            }).start();

            consoleReader(writer);
        }
    }

    private static void consoleReader(Writer writer) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = reader.readLine();
            writer.write(line + "\n");
            writer.flush();
        }
    }

    private static void socketReader(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                if (line.isBlank()) continue;
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

