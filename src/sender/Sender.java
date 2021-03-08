package sender;

import shared.*;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Sender {

    // window size
    static int N = 10;

    /*
     * command line input includes the following:
     * 1. <host address of the network emulator> (hostname)
     * 2. <UDP port number used by the emulator to receive data from the sender>
     * 3. <UDP port number used by the sender to receive ACKs from the emulator>
     * 4. <timeout interval in units of millisecond>
     * 5. <name of the file to be transferred> in the given order.
     */
    public static void main(String[] args) throws IOException {
        // parse command line args
        if (args.length != 5) {
            System.err.println("Invalid number of arguments. Should specify 5 args.");
            System.exit(-1);
        }

        InetAddress emulatorAddress = null;
        int sPort = 0;
        int rPort = 0;
        int timeout = 0;    // in millisecond
        String filename = null;
        BufferedInputStream reader = null;
        //BufferedReader reader = null;

        try {
            emulatorAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.err.println("Error: Invalid emulator Address!");
            System.exit(-1);
        }
        System.out.println("emulatorAddress is " + emulatorAddress);
        try {
            sPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error: port to receive sender's data should be an integer!");
            System.exit(-1);
        }
        System.out.println("sPort is " + sPort);
        try {
            rPort = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Error: port to receive receiver's ACK should be an integer!");
            System.exit(-1);
        }
        System.out.println("rPort is " + rPort);
        try {
            timeout = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.err.println("Error: timeout interval should be an integer!");
            System.exit(-1);
        }
        System.out.println("timeout is " + timeout);
        filename = args[4];
        try {
            reader = new BufferedInputStream(new FileInputStream(filename));
            //reader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            System.err.println("Error: file not found!");
            System.exit(-1);
        }
        System.out.println("filename is " + filename);

        // create input file reader
        MyFileReaderBytes myFileReaderBytes = new MyFileReaderBytes(reader);
        //MyFileReaderString myFileReaderString = new MyFileReaderString(reader);

        // set the output log files: in src/ folder
        PrintStream seqLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/seqnum.log")));
        PrintStream ackLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/ack.log")));
        PrintStream nLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/N.log")));

        // create sockets
//        DatagramSocket sendDataSocket = new DatagramSocket(sPort);
//        DatagramSocket receiveACKSocket = new DatagramSocket(rPort);

        int timestamp = 0;

        UDPUtility udpUtility = new UDPUtility(sPort, rPort, emulatorAddress, timeout);

        // TODO: seqNum
        int seqNum = 0;
        Packet currPacket = myFileReaderBytes.getNextPacket();
        udpUtility.sendPacket(currPacket);
        ++timestamp;
        seqLog.println(timestamp + " " + seqNum);

        Packet ack = udpUtility.receivePacket();
        ++timestamp;
        ackLog.println(timestamp + " " + ack.getSeqNum());
//        byte[] placeholderACKBytes = new byte[Constant.ACK_SIZE];
//        DatagramPacket receivedACKPacket = new DatagramPacket(placeholderACKBytes, placeholderACKBytes.length);
//        receiveACKSocket.receive(receivedACKPacket);
//        byte[] receivedACKBytes = receivedACKPacket.getData();
//        Packet ack = Packet.parsePacket(receivedACKBytes);

        try {

        } finally {
            seqLog.close();
            ackLog.close();
            nLog.close();
            reader.close();
        }
    }

    public static void sendHelper(MyFileReaderBytes reader, UDPUtility udpUtility) throws IOException {
        // packets in the send-window
        LinkedList<Packet> packets = new LinkedList<>();

        // at the beginning
        for (int i = 0; i < N; ++i) {
            try {
                Packet packet = reader.getNextPacket();
                packets.add(packet);
            } catch (EOFException e) {  // EOT
                // TODO: EOT handling
                break;
            }
        }
        for (Packet packet: packets) {
            udpUtility.sendPacket(packet);
        }

        try {
            Packet ackPacket = udpUtility.receivePacket();
        } catch (SocketTimeoutException e) {
            // log timeout
        }
    }
}
