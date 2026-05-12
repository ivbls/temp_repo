package client;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static client.Client.getPayloadLength;
import static client.Client.readPayload;

public class ClientTest {

    @Test
    public void readPayloadBasicTest() {
        try (FileInputStream is = new FileInputStream("./src/test/resources/sample-input-ascii.bin")) {
            byte[] payloadBuffer = new byte[2];
            byte[] payload;
            int readResult;

            readResult = is.read(payloadBuffer);
            Assert.assertNotEquals(-1, readResult);
            payload = readPayload(is, payloadBuffer);
            Assert.assertEquals(24, payload.length);

            readResult = is.read(payloadBuffer);
            Assert.assertNotEquals(-1, readResult);
            payload = readPayload(is, payloadBuffer);
            Assert.assertEquals(22, payload.length);

            readResult = is.read(payloadBuffer);
            Assert.assertNotEquals(-1, readResult);
            payload = readPayload(is, payloadBuffer);
            Assert.assertEquals(3, payload.length);

            readResult = is.read(payloadBuffer);
            Assert.assertEquals(-1, readResult);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    //parsepayload sa null vrijednostima i sa payloadlength od 0, 1 ili 3

    //get payload length je li dobar

    @Test
    public void readPayloadZeroLengthPayloadTest() {
        try (FileInputStream is = new FileInputStream(new File("./src/test/resources/sample-input-ascii-zero-length.bin"))) {
            byte[] payloadBuffer = new byte[2];
            int readResult;

            readResult = is.read(payloadBuffer);
            Assert.assertNotEquals(-1, readResult);

            Assert.assertThrows(IllegalArgumentException.class, () -> readPayload(is, payloadBuffer));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void readPayloadInvalidArgumentsTest() {
        try (FileInputStream is = new FileInputStream(new File("./src/test/resources/sample-input-ascii.bin"))) {
            byte[] payloadBuffer = new byte[2];
            int readResult;

            readResult = is.read(payloadBuffer);
            Assert.assertNotEquals(-1, readResult);

            Assert.assertThrows(IllegalArgumentException.class, () -> readPayload(null, payloadBuffer));
            Assert.assertThrows(IllegalArgumentException.class, () -> readPayload(is, null));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void overlyLargePayloadTest() {
        try (FileInputStream is = new FileInputStream("./src/test/resources/sample-input-ascii-long.bin")) {
            byte[] payloadBuffer = new byte[2];
            byte[] payload;
            int readResult;

            readResult = is.read(payloadBuffer);
            Assert.assertNotEquals(-1, readResult);
            payload = readPayload(is, payloadBuffer);
            Assert.assertEquals(24, payload.length);

            readResult = is.read(payloadBuffer);
            Assert.assertNotEquals(-1, readResult);
            payload = readPayload(is, payloadBuffer);
            Assert.assertEquals(22, payload.length);

            readResult = is.read(payloadBuffer);
            Assert.assertNotEquals(-1, readResult);
            Assert.assertThrows(RuntimeException.class, () -> readPayload(is, payloadBuffer));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void overlyShortPayloadTest() {
        try (FileInputStream is = new FileInputStream("./src/test/resources/sample-input-ascii-short.bin")) {
            byte[] payloadBuffer = new byte[2];
            byte[] payload;
            int readResult;

            readResult = is.read(payloadBuffer);
            Assert.assertNotEquals(-1, readResult);
            payload = readPayload(is, payloadBuffer);
            Assert.assertEquals(24, payload.length);

            readResult = is.read(payloadBuffer);
            Assert.assertNotEquals(-1, readResult);
            payload = readPayload(is, payloadBuffer);
            Assert.assertEquals(22, payload.length);

            readResult = is.read(payloadBuffer);
            Assert.assertNotEquals(-1, readResult);
            Assert.assertThrows(RuntimeException.class, () -> readPayload(is, payloadBuffer));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void payloadLengthBasicTest() {
        byte[] payloadBuffer = new byte[2];
        payloadBuffer[0] = 0x01;
        payloadBuffer[1] = 0x18;
        Assert.assertEquals(280, getPayloadLength(payloadBuffer));
    }

    @Test
    public void payloadLengthWrongLengthTest() {
        Assert.assertThrows(IllegalArgumentException.class, () -> getPayloadLength(new byte[1]));

        Assert.assertThrows(IllegalArgumentException.class, () -> getPayloadLength(new byte[3]));
    }

}
