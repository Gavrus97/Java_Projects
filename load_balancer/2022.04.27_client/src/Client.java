import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client implements Runnable{

    private final String host;
    private final int port;

    public Client(String server_host, int server_port) {
        host = server_host;
        port = server_port;
    }


    @Override
    public void run() {
        try {
            Socket socket = new Socket(host, port);


            try (PrintStream dataOut = new PrintStream(socket.getOutputStream());
                 BufferedReader dataIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in))) {

                String line;
                while ((line = fromConsole.readLine()) != null && !line.equals("exit")) {
                    dataOut.println(line);
                    String response = dataIn.readLine();
                    System.out.println(response);
                }
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
