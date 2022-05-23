import storage.ServerStorage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpClientListener implements Runnable {

    private final int tcpPort;
    private final int connectionsNumber;
    private final ServerStorage serverStorage;

    public TcpClientListener(int tcpPort, int connectionsNumber, ServerStorage serverStorage) {
        this.tcpPort = tcpPort;
        this.connectionsNumber = connectionsNumber;
        this.serverStorage = serverStorage;
    }


    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(tcpPort)){

            ExecutorService executor = Executors.newFixedThreadPool(connectionsNumber);

            while (true){
                Socket socket = serverSocket.accept();
                Runnable gatewayTask = new GatewayTask(socket, serverStorage);
                executor.execute(gatewayTask);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
