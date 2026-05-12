package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Client {

    public static void main(String[] args) {
        String filepath = "./src/main/resources/sample-input-ascii.bin";
        File file = new File(filepath);

        try (FileInputStream is = new FileInputStream(file)) {
            Socket socket = new Socket("localhost", 9090);
            byte[] payloadBuffer = new byte[2];
            while(is.read(payloadBuffer) != -1) {
                socket.getOutputStream().write(concatByteArray(payloadBuffer, readPayload(is, payloadBuffer)));
                socket.getOutputStream().flush();
                int confirmation = socket.getInputStream().read();
                if (confirmation == -1) {
                    throw new SocketException("Error when receiving response from server");
                }
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static byte[] readPayload(final FileInputStream is, final byte[] payloadBuffer) {
        if (payloadBuffer == null || is == null) {
            throw new IllegalArgumentException("null values in method arguments");
        }
        if (payloadBuffer.length != 2) {
            throw new IllegalArgumentException("Payload buffer length is incorrect");
        }

        int payloadLength = getPayloadLength(payloadBuffer);

        if(payloadLength == 0) {
            throw new IllegalArgumentException("payload length cannot be 0");
        }
        if(payloadLength > 4096) {
            throw new IllegalArgumentException("payload length exceeds maximum value");
        }

        byte[] payload = new byte[payloadLength];
        try {
            int result = is.read(payload);

            if(result == -1 || result != payloadLength) {
                throw new RuntimeException("error occurred while reading file");
            }

            return payload;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getPayloadLength(final byte[] payloadBuffer) {
        if (payloadBuffer == null || payloadBuffer.length != 2) {
            throw new IllegalArgumentException("payload buffer is invalid");
        }

        return (payloadBuffer[0] << 8) + payloadBuffer[1];
    }

    private static byte[] concatByteArray(byte[] first, byte[] second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("null values in input arrays");
        }

        byte[] retval = new byte[first.length + second.length];

        for(int i = 0; i < first.length; ++i) {
            retval[i] = first[i];
        }

        for(int i = 0; i < second.length; ++i) {
            retval[i + first.length] = second[i];
        }

        return retval;
    }
}
