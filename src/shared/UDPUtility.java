package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPUtility {

    private final int port;
    private final DatagramSocket datagramSocket;
    private final InetAddress IPAddress;

    public UDPUtility(int port, InetAddress ipAddress) throws SocketException {
        this.port = port;
        this.datagramSocket= new DatagramSocket(port);
        IPAddress = ipAddress;
    }

    public void sendPacket(Packet packet) throws IOException {
        byte[] sendBytes = packet.toUDPBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, IPAddress, port);
        datagramSocket.send(sendPacket);
    }

    public Packet receivePacket() throws IOException {
        byte[] receivedBytes = new byte[Constant.MAX_DATA_PACKET_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receivedBytes, receivedBytes.length);
        datagramSocket.receive(receivePacket);
        return Packet.parsePacket(receivedBytes);
    }
}
