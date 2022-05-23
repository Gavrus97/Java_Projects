package de.telran.server_spring.thread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TcpServer {

    private final AtomicInteger loadCounter;
    private final int selfTcpPort;
    private final int connectionsNumber;

    public TcpServer(AtomicInteger loadCounter,
                     @Value("${server.tcp.inbound.port}") int selfTcpPort,
                     @Value("${tcp.connections.number}") int connectionsNumber) {
        this.loadCounter = loadCounter;
        this.selfTcpPort = selfTcpPort;
        this.connectionsNumber = connectionsNumber;
    }

    @Async("threadExecutor")
    public void run() {
        System.out.println("tcpServer");
        try (ServerSocket serverSocket = new ServerSocket(selfTcpPort)) {

            while (true) {
                Socket socket = serverSocket.accept();
                loadCounter.incrementAndGet();

                ServerTask serverTask = new ServerTask(socket, loadCounter);
                serverTask.run();
                //executor.execute(serverTask);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
