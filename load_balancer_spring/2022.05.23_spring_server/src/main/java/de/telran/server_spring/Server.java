package de.telran.server_spring;

import de.telran.server_spring.thread.LoadSender;
import de.telran.server_spring.thread.TcpServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Server implements CommandLineRunner {

    private final TcpServer tcpServer;
    private final LoadSender loadSender;

    public Server(TcpServer tcpServer, LoadSender loadSender) {
        this.tcpServer = tcpServer;
        this.loadSender = loadSender;
    }

    @Override
    public void run(String... args) throws Exception {
            loadSender.run();
            tcpServer.run();
    }
}
