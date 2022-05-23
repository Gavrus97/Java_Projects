import model.Server;
import storage.ServerStorage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpBalancerListener implements Runnable {

    private static final int PACKET_SIZE = 1024;
    ServerStorage serverStorage;
    int udpFromBalancerPort;

    public UdpBalancerListener(ServerStorage serverStorage, int udpFromBalancerPort) {
        this.serverStorage = serverStorage;
        this.udpFromBalancerPort = udpFromBalancerPort;
    }

    @Override
    public void run() {

        try (DatagramSocket balancerUdpSocket = new DatagramSocket(udpFromBalancerPort)) {
            byte[] dataIn = new byte[PACKET_SIZE];
            DatagramPacket packetIn = new DatagramPacket(dataIn, PACKET_SIZE);

            while (true) {
                balancerUdpSocket.receive(packetIn);
                handleDataFromBalancer(packetIn);
            }

        } catch (SocketException e) {
            e.fillInStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDataFromBalancer(DatagramPacket packetIn) {
        byte[] bytes = packetIn.getData();
        String data = new String(bytes, 0, packetIn.getLength());
        String[] dataParts = data.split(":");

        String host = dataParts[0];
        int port = Integer.parseInt(dataParts[1]);

        Server server = new Server(host, port);
        serverStorage.setServer(server);
    }
}
