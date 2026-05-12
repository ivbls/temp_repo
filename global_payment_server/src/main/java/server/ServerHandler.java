package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServerHandler {

    private final static String db_name = "global_payment_db";
    private final static String db_socket = "5432";
    private final static String db_uname = "postgres";
    private final static String db_pass = "admin";
    private static final String sql = "INSERT INTO entries(transaction_type, transaction_id, amount, currency) VALUES (?, ?, ?, ?)";

    private ServerSocket serverSocket;
    private static final String EOF = "END";
    private static final String EOF_HR = "KRAJ";
    private static final String ENCODING_STR = "IBM852";
    private static final String LINE_SPLIT = "\\|";
    private static String url = "jdbc:postgresql://localhost:%s/%s?currentSchema=public";
    private static Connection con;

    private static final int PAYLOAD_LENGTH_BUFFER_LENGTH = 2;

    private static final Charset charset = Charset.forName(ENCODING_STR);

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            new EchoClientHandler(serverSocket.accept()).start();
        }
    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public String readPayload() throws IOException {
            byte[] payloadLengthBuffer = new byte[PAYLOAD_LENGTH_BUFFER_LENGTH];
            int readTestVal = clientSocket.getInputStream().read(payloadLengthBuffer);

            if(readTestVal == -1) {
                throw new RuntimeException("Input stream could not be read further");
            }

            int payloadLength = (payloadLengthBuffer[0] << 8) + payloadLengthBuffer[1];
            if (payloadLength > 4096) {
                throw new IOException("File contains overly large payload length");
            }
            byte[] payload = new byte[payloadLength];

            readTestVal = clientSocket.getInputStream().read(payload);
            clientSocket.getOutputStream().write(0xf);

            if(readTestVal == -1) {
                throw new RuntimeException("Input stream could not be read further");
            }

            ByteBuffer inputBuffer = ByteBuffer.wrap(payload);
            CharBuffer data = charset.decode(inputBuffer);
            return data.toString();
        }

        public void run() {
            try {
                con = DriverManager.getConnection(String.format(url, db_socket, db_name), db_uname, db_pass);

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("./src/main/resources/output.txt", true), StandardCharsets.UTF_8);

                String line = readPayload();
                while (!line.equals(EOF) && !line.equals(EOF_HR)) {
                    String[] linesplit = line.split(LINE_SPLIT);
                    if(linesplit.length != 4) {
                        throw new IllegalArgumentException("file formatting incorrect");
                    }

                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, linesplit[0]);
                    ps.setString(2, linesplit[1]);
                    ps.setFloat(3, Float.parseFloat(linesplit[2]));
                    ps.setString(4, linesplit[3]);
                    ps.executeUpdate();

                    writer.write(line + "\n");

                    line = readPayload();
                }

                writer.close();

                in.close();
                out.close();
                clientSocket.close();
                con.close();
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
