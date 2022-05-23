import storage.ServerStorage;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {

        private static final String DEFAULT_PROPERTIES_FILE = "config/application.props";
        private static final String UDP_FROM_BALANCER_PORT = "udp.balancer.port";
        private static final String TCP_CONNECTIONS_NUMBER_KEY = "tcp.connections.number";
        private static final String SELF_TCP_PORT = "gateway.tcp.port";



    public static void main(String[] args) throws IOException {
        String propsFile = args.length > 0 ? args[0] : DEFAULT_PROPERTIES_FILE;
        Properties properties = new Properties();
        properties.load(new FileReader(propsFile));

        int udpFromBalancerPort = Integer.parseInt(properties.getProperty(UDP_FROM_BALANCER_PORT));
        int connections = Integer.parseInt(properties.getProperty(TCP_CONNECTIONS_NUMBER_KEY));
        int gatewayPort = Integer.parseInt(properties.getProperty(SELF_TCP_PORT));

        //OptimalServerStorage optimalServer = new OptimalServerStorage();
        ServerStorage serverStorage = new ServerStorage();

        UdpBalancerListener balancerListener = new UdpBalancerListener(serverStorage, udpFromBalancerPort);
        new Thread(balancerListener).start();

        TcpClientListener clientListener = new TcpClientListener(gatewayPort, connections, serverStorage);
        new Thread(clientListener).start();
    }
}
