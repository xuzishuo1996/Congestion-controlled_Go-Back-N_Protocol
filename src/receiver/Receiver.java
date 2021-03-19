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
        try {
            sPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error: port to receive sender's data should be an integer!");
            System.exit(-1);
        }
        try {
            rPort = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Error: port to receive receiver's ACK should be an integer!");
            System.exit(-1);
        }
        filename = args[3];
        try {
            writer = new BufferedWriter(new FileWriter(filename));
        } catch (IOException e) {
            System.err.println("Error: cannot open or create the file to write!");
            System.exit(-1);
        }

        // set the output log file
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

            if (type == Constant.DATA) {
                // log
                arrivalLog.println(rcvSeqNum);
                arrivalLog.flush();

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
                    writer.write(dataPacket.getData());
                    writer.flush();
                    // ack
                    udpUtility.sendPacket(new Packet(Constant.ACK, rcvBase, 0, null));
                    // update window
                    rcvBase = (rcvBase + 1 + Constant.MODULO) % Constant.MODULO;
                }
            } else {    // type == Constant.EOT
                // sender logic guarantees that all segments have been transferred

                udpUtility.sendPacket(new Packet(Constant.EOT,
                        rcvSeqNum, 0, null));

                arrivalLog.close();
                writer.close();
                System.exit(0);
            }
        }
    }
}
