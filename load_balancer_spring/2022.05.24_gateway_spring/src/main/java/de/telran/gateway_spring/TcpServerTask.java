package de.telran.gateway_spring;

import de.telran.gateway_spring.model.Server;
import de.telran.gateway_spring.storage.ServerStorage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.io.*;
import java.net.Socket;

@Component
public class TcpServerTask {
    private final Socket socket;
    private final ServerStorage serverStorage;

    public TcpServerTask(Socket socket, ServerStorage serverStorage) {
        this.socket = socket;
        this.serverStorage = serverStorage;
    }

    @Async("threadPoolExecutor")
    public void run() {
        Server server = serverStorage.getServer();
        String serverHost = server.getHost();
        int serverPort = server.getPort();

        try (Socket serverSocket = new Socket(serverHost, serverPort);
             BufferedReader fromClientData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintStream toClientData = new PrintStream(socket.getOutputStream());
             BufferedReader fromServerData = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
             PrintStream toServerData = new PrintStream(serverSocket.getOutputStream())
        ) {
            String line;
            while ((line = fromClientData.readLine()) != null) {
                toServerData.println(line);
                String responseFromServer = fromServerData.readLine();
                toClientData.println(responseFromServer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}