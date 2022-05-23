import model.Server;
import storage.ServerStorage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class GatewayTask implements Runnable {

    private final Socket socket;
    private final ServerStorage serverStorage;

    public GatewayTask(Socket socket, ServerStorage serverStorage) {
        this.socket = socket;
        this.serverStorage = serverStorage;
    }

    @Override
    public void run() {
        Server server = serverStorage.getServer();
        String host = server.getHost();
        int port = server.getPort();

        try (Socket toServerSocket = new Socket(host, port);
             PrintStream dataToClientOut = new PrintStream(socket.getOutputStream());
             BufferedReader dataFromClientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintStream dataToServerOut = new PrintStream(toServerSocket.getOutputStream());
             BufferedReader dataFromServerIn = new BufferedReader(new InputStreamReader(toServerSocket.getInputStream()))
        ) {

            String line;
            while((line = dataFromClientIn.readLine()) != null){
                dataToServerOut.println(line);
                String response = dataFromServerIn.readLine();
                dataToClientOut.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
