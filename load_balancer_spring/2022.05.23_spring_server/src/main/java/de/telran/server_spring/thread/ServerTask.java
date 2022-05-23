package de.telran.server_spring.thread;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ServerTask {

    private final Socket socket;
    private final AtomicInteger loadCounter;

    public ServerTask(Socket socket, AtomicInteger loadCounter) {
        this.socket = socket;
        this.loadCounter = loadCounter;
    }


    @Async("threadPoolExecutor")
    public void run() {
        try (
            PrintStream dataOut = new PrintStream(socket.getOutputStream());
            BufferedReader dataIn = new BufferedReader(new InputStreamReader(socket.getInputStream()))){;

            String line;
            while ((line = dataIn.readLine()) != null) {
                String response = line + " handled by server";
                dataOut.println(response);
            }
            System.out.println("Socket closed");
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            loadCounter.decrementAndGet();
        }
    }
}
