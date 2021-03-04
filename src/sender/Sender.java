package sender;

import shared.Constant;
import shared.MyFileReaderString;
import shared.Packet;
import shared.UDPUtility;

import java.io.*;
import java.net.*;

public class Sender {

    // window size
    int N = 10;

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
        BufferedReader reader = null;

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
            reader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            System.err.println("Error: file not found!");
            System.exit(-1);
        }
        System.out.println("filename is " + filename);

        // create input file reader
        MyFileReaderString myFileReaderString = new MyFileReaderString(reader);

        // set the output log file
        PrintStream seqLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/seqnum.log")));
        PrintStream ackLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/ack.log")));
        PrintStream nLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/N.log")));

        // create sockets
//        DatagramSocket sendDataSocket = new DatagramSocket(sPort);
//        DatagramSocket receiveACKSocket = new DatagramSocket(rPort);

        UDPUtility sendUtility = new UDPUtility(sPort, emulatorAddress);
        String data = myFileReaderString.getNextSegment();
        // TODO: seqNum
        int seqNum = 0;
        sendUtility.sendPacket(new Packet(Constant.DATA, seqNum, data.length(), data));

        UDPUtility receiveUtility = new UDPUtility(rPort, emulatorAddress);
        Packet ack = receiveUtility.receivePacket();
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
        }
    }
}
