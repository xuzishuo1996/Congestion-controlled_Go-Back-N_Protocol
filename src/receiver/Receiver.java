package receiver;

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
    public static void main(String[] args) {
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


    }
}
