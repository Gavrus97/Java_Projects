package de.telran.server_spring.thread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoadSender {

    private final AtomicInteger loadCounter;
    private final int selfTcpPort;
    private final InetAddress balancerHost;
    private final int balancerUdpPort;
    private final int updatePeriod;

    public LoadSender(AtomicInteger loadCounter,
                      @Value("${server.tcp.inbound.port}") int selfTcpPort,
                      @Value("${balancer.host}") String balancerHost,
                      @Value("${balancer.udp.port}") int balancerUdpPort,
                      @Value("${load.update.period}") int updatePeriod) throws UnknownHostException {
        this.loadCounter = loadCounter;
        this.selfTcpPort = selfTcpPort;
        this.balancerHost = InetAddress.getByName(balancerHost);
        this.balancerUdpPort = balancerUdpPort;
        this.updatePeriod = updatePeriod;
    }

    @Async("threadExecutor")
    public void run() {
        System.out.println("LoadSender");

        try (DatagramSocket datagramSocket = new DatagramSocket()) {

            while (true) {
                String data = selfTcpPort + ":" + loadCounter.get();
                byte[] bytesOut = data.getBytes();

                DatagramPacket packet = new DatagramPacket(
                        bytesOut,
                        bytesOut.length,
                        balancerHost,
                        balancerUdpPort
                );
                System.out.println(data);

                datagramSocket.send(packet);

                Thread.sleep(updatePeriod);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
