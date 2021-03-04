package sender;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
    public static void main(String[] args) throws FileNotFoundException {
        // parse command line args
        if (args.length != 5) {
            System.err.println("Invalid number of arguments. Should specify 5 args.");
            System.exit(-1);
        }

        InetAddress serverAddress = null;
        int sPort = 0;
        int rPort = 0;
        int timeout = 0;    // in millisecond
        String filename = null;
        FileInputStream file;

        try {
            serverAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.err.println("Error: Invalid emulator Address!");
            System.exit(-1);
        }
        System.out.println("serverAddress is " + serverAddress);
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
            file = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            System.err.println("Error: file not found!");
            System.exit(-1);
        }
        System.out.println("filename is " + filename);

        // set the output log file
        PrintStream seqLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream("../../seqnum.log")));
        PrintStream ackLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream("../../ack.log")));
        PrintStream nLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream("../../N.log")));

        try {

        } finally {
            seqLog.close();
            ackLog.close();
            nLog.close();
        }
    }
}
