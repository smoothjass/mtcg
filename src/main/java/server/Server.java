package server;

import app.App;
import app.models.BattleRequest;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private App app;
    private int port;

    public Server(App app, int port) {
        setApp(app);
        setPort(port);
    }

    public void start() throws IOException {
        setServerSocket(new ServerSocket(getPort()));
        run();
    }

    private void run() {
        int counter = 0;
        while (true) {
            try {
                //irgendwie werden pro request zwei threads erzeugt, einer mit dem echten request, einer mit null. why? TODO
                setClientSocket(getServerSocket().accept());
                // ab hier thread erzeugen, der request handlet. server wartet quasi sofort weiter
                RequestHandler task = new RequestHandler(this.clientSocket, this.app);
                Thread thread = new Thread(task);
                thread.setName(String.valueOf(counter));
                // System.out.println(thread.getName() + " working");
                ++counter;
                // ruft RequestHandler.run() auf
                thread.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}