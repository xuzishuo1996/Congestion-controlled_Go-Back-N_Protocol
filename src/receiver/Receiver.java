package receiver;

import shared.Constant;
import shared.Packet;
import shared.UDPUtility;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Receiver {
    /*
     * command line input includes the following:
     * 1. <hostname for the network emulator>
     * 2. <UDP port number used by the link emulator to receive ACKs from the receiver>
     * 3. <UDP port number used by the receiver to receive data from the emulator>
     * 4. <name of the file into which the received data is written>
     */
    public static void main(String[] args) throws IOException {
        // parse command line args
        if (args.length != 4) {
            System.err.println("Invalid number of arguments. Should specify 4 args.");
            System.exit(-1);
        }

        InetAddress emulatorAddress = null;
        int sPort = 0;
        int rPort = 0;
        String filename = null;
        BufferedWriter writer = null;

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
        filename = args[3];
        try {
            writer = new BufferedWriter(new FileWriter(filename));
        } catch (IOException e) {
            System.err.println("Error: cannot open or create the file to write!");
            System.exit(-1);
        }
        System.out.println("filename is " + filename);

        // set the output log file: in src/ folder
        PrintStream arrivalLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/arrival.log")));

        // receive window: rcvBase is the next seq to be acked
        // last acked: (rcvBase - 1 + MODULO) % MODULO.
        int rcvBase = 0;

        UDPUtility udpUtility = new UDPUtility(sPort, rPort, emulatorAddress);
        while (true) {
            // get data packet from the emulator
            Packet dataPacket = udpUtility.receivePacket();
            // see the seq number and check whether write to file and send ack, or resend prev acks
            int type = dataPacket.getType();
            int rcvSeqNum = dataPacket.getSeqNum();
            System.out.println("rcvSeqNum: " + rcvSeqNum);
            if (type == Constant.DATA) {
                // log
                System.out.println("Receiver receive data!");
                arrivalLog.println(rcvSeqNum);

                // the sequence number is NOT the one that it is expecting:
                // discard the received packet and resend an ACK packet for the most recently received in-order packet.
                if (rcvSeqNum != rcvBase) {
                    udpUtility.sendPacket(new Packet(Constant.ACK,
                            (rcvBase - 1 + Constant.MODULO) % Constant.MODULO, 0, null));
                }
                // the sequence number is the one that it is expecting:
                // write the newly received packet to the file and update the window
                else {
                    // write to file
                    // System.out.println("[Get Data]: " + dataPacket.getData());
                    writer.write(dataPacket.getData());
                    // ack
                    udpUtility.sendPacket(new Packet(Constant.ACK, rcvBase, 0, null));
                    // update window
                    rcvBase = (rcvBase + 1 + Constant.MODULO) % Constant.MODULO;
                }
            } else {    // type == Constant.EOT
                // has acked all segments
                System.out.println();

                if (rcvSeqNum == (rcvBase - 1 + Constant.MODULO) % Constant.MODULO) {
                    udpUtility.sendPacket(new Packet(Constant.EOT,
                            (rcvBase - 1 + Constant.MODULO) % Constant.MODULO, 0, null));
                    System.out.println("[Receiver] has received all packets!");

                    arrivalLog.close();
                    writer.close();
                    System.exit(1);
                }
                // has segments not been acked
                else {
                    System.out.println("[Receiver] has received EOT but some packets unreceived!");

                    udpUtility.sendPacket(new Packet(Constant.ACK,
                            (rcvBase - 1 + Constant.MODULO) % Constant.MODULO, 0, null));
                }
            }
        }
    }
}
