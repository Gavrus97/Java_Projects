package de.telran.gateway_spring;

import de.telran.gateway_spring.model.Server;
import de.telran.gateway_spring.storage.ServerStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Component
public class BalancerListener {

    private final int fromBalancerUdpPort;
    private final ServerStorage serverStorage;

    public BalancerListener(@Value("${udp.balancer.inbound.port}")int fromBalancerUdpPort,
                            ServerStorage serverStorage) {
        this.fromBalancerUdpPort = fromBalancerUdpPort;
        this.serverStorage = serverStorage;
    }


    @Async("threadExecutor")
    public void run() {
        try (DatagramSocket datagramSocket = new DatagramSocket(fromBalancerUdpPort)) {
            byte[] data = new byte[1024];
            DatagramPacket packetIn = new DatagramPacket(data, data.length);

            while (true) {
                datagramSocket.receive(packetIn);
                String optimalServerData = new String(data, 0, packetIn.getLength());

                System.out.println(optimalServerData);

                String[] parts = optimalServerData.split(":");
                String serverHost = parts[0];
                int serverPort = Integer.parseInt(parts[1]);

                serverStorage.setServer(new Server(serverHost, serverPort));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}