package server;

import java.io.IOException;
import java.sql.SQLException;

public class Server {

    private static final int port = 9090;

    public static void main(String[] args) throws IOException, SQLException {
        ServerHandler serverHandler = new ServerHandler();

        serverHandler.start(port);
    }
}
