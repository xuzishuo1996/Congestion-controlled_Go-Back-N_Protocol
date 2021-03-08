package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class UDPUtility {
    // possible available port range. for getAnAvailablePort()
    private static final int MIN_PORT_NUM = 1024;
    private static final int MAX_PORT_NUM = 65535;

    private final int sPort;
    private final int rPort;
    // private final int timeout;
    private final DatagramSocket sendDatagramSocket;
    private final DatagramSocket receiveDatagramSocket;
    private final InetAddress IPAddress;    // host name of the other side

    public UDPUtility(int sPort, int rPort, InetAddress ipAddress) throws SocketException {
        this.sPort = sPort;
        this.rPort = rPort;
        // this.timeout = timeout;
        this.sendDatagramSocket = getAnAvailableUDPSocket();
        this.receiveDatagramSocket = new DatagramSocket(rPort);
        // set timeout of the receive DatagramSocket
        // receiveDatagramSocket.setSoTimeout(timeout);
        IPAddress = ipAddress;
    }

    public void sendPacket(Packet packet) throws IOException {
        byte[] sendBytes = packet.toUDPBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, IPAddress, sPort);
        sendDatagramSocket.send(sendPacket);
    }

    public Packet receivePacket() throws IOException {
        byte[] receivedBytes = new byte[Constant.MAX_DATA_PACKET_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receivedBytes, receivedBytes.length);
        receiveDatagramSocket.receive(receivePacket);
        return Packet.parsePacket(receivedBytes);
    }

    /*
     * Randomly choose an available port from [1024, 65535]
     */
    private static DatagramSocket getAnAvailableUDPSocket() throws SocketException {
        int num = 0;
        Random rand = new Random();

        while (num < 200) {
            // nextInt: left inclusive, right exclusive. [0, 65535 - 1024 + 1) + 1024 = [1024, 65536)
            int port = rand.nextInt(MAX_PORT_NUM - MIN_PORT_NUM + 1) + MIN_PORT_NUM;
            try {
                return new DatagramSocket(port);
            } catch (SocketException e) {
                ++num;
            }
        }

        throw new SocketException("No available ports!");
    }

    public static String getTimeStamp() {
        // https://stackoverflow.com/questions/23068676/how-to-get-current-timestamp-in-string-format-in-java-yyyy-mm-dd-hh-mm-ss
        return new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss").format(new Date());
    }
}
