package de.telran.gateway_spring.storage;

import de.telran.gateway_spring.model.Server;
import org.springframework.stereotype.Component;

@Component
public class ServerStorage {
    private Server server;

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}