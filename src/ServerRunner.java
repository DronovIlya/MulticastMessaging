import server.Server;

import java.io.IOException;

public class ServerRunner {

    public static void main(String[] args) throws IOException {
        System.out.println("start server");
        Server server = new Server();
        server.start();
        System.out.println("server started");
    }
}
