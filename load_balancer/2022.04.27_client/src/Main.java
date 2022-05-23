import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {

    private static final String DEFAULT_PROPS_PATH = "config/application.props";
    private static final String HOST = "tcp.host";
    private static final String PORT = "tcp.port";

    public static void main(String[] args) throws IOException {
        String propsFile = args.length > 0 ? args[0] : DEFAULT_PROPS_PATH;

        Properties properties = new Properties();
        properties.load(new FileReader(propsFile));

        String host = properties.getProperty(HOST);
        int port = Integer.parseInt(properties.getProperty(PORT));

        Runnable client = new Client(host, port);
        new Thread(client).start();
    }
}
