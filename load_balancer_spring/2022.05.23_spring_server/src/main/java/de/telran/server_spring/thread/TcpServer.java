package de.telran.server_spring.thread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TcpServer {

    private final AtomicInteger loadCounter;
    private final int selfTcpPort;
    private final int connectionsNumber;
    private final ServerTask serverTask;

    public TcpServer(AtomicInteger loadCounter,
                     @Value("${server.tcp.inbound.port}") int selfTcpPort,
                     @Value("${tcp.connections.number}") int connectionsNumber,
                     ServerTask serverTask) {
        this.loadCounter = loadCounter;
        this.selfTcpPort = selfTcpPort;
        this.connectionsNumber = connectionsNumber;
        this.serverTask = serverTask;
    }

    @Async("threadExecutor")
    public void run() {
        System.out.println("tcpServer");
        try (ServerSocket serverSocket = new ServerSocket(selfTcpPort)) {

            while (true) {
                Socket socket = serverSocket.accept();
                loadCounter.incrementAndGet();

                serverTask.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
