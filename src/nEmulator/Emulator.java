package nEmulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Emulator {
    /*
     * command line input includes the following:
        • <emulator's receiving UDP port number in the forward (sender) direction> ,
        • <receiver’s network address> ,
        • <receiver’s receiving UDP port number> ,
        • <emulator's receiving UDP port number in the backward (receiver) direction> ,
        • <sender’s network address> ,
        • <sender’s receiving UDP port number> ,
        • <maximum delay of the link in units of millisecond> ,
        • <packet discard probability>
        • <verbose-mode> (Boolean: Set to 1, the network emulator will output its internal processing, one
            per line, e.g. receiving Packet seqnum /ACK seqnum , discarding Packet seqnum /ACK seqnum,
            forwarding Packet seqnum /ACK seqnum ).
     */
    public static void main(String[] args)
    {
        // parse command line args
        if (args.length != 9) {
            System.err.println("Invalid number of arguments. Should specify 9 args.");
            System.exit(-1);
        }

        int fwdEmuRcvPort = 0;
        InetAddress rcvAddress = null;
        int fwdRcverRcvPort = 0;
        int backEmuRcvPort = 0;
        InetAddress senderAddress = null;
        int backSenderRcvPort = 0;
        int maxDelay = 0;   // measured in milliseconds
        double discardProbability = 0.0;
        boolean verbose = true;

        try {
            fwdEmuRcvPort = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Error: emulator's receive port in forward direction should be an integer!");
            System.exit(-1);
        }
        try {
            rcvAddress = InetAddress.getByName(args[1]);
        } catch (UnknownHostException e) {
            System.err.println("Error: Invalid receiver address!");
            System.exit(-1);
        }
        try {
            fwdRcverRcvPort = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Error: receiver's receive port in forward direction should be an integer!");
            System.exit(-1);
        }
        try {
            backEmuRcvPort = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.err.println("Error: emulator's receive port in backward direction should be an integer!");
            System.exit(-1);
        }
        try {
            senderAddress = InetAddress.getByName(args[4]);
        } catch (UnknownHostException e) {
            System.err.println("Error: Invalid sender Address!");
            System.exit(-1);
        }
        try {
            backSenderRcvPort = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            System.err.println("Error: receiver's receive port in backward direction should be an integer!");
            System.exit(-1);
        }
        try {
            maxDelay = Integer.parseInt(args[6]);
        } catch (NumberFormatException e) {
            System.err.println("Error: maximum delay of the link should be an integer!");
            System.exit(-1);
        }
        try {
            discardProbability = Double.parseDouble(args[7]);
            if (discardProbability < 0 || discardProbability > 1) {
                System.err.println("Error: discard probability should be a float between 0 and 1!");
                System.exit(-1);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: discard probability should be a float between 0 and 1!");
            System.exit(-1);
        }
        switch (args[8]) {
            case "0": verbose = false; break;
            case "1": verbose = true; break;
            default: {
                System.err.println("Error: verbose mode should be 0 or 1!");
                System.exit(-1);
            }
        }

        System.out.println("here!");
    }
}
