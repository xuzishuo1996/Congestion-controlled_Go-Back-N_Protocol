package sender;

import shared.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Sender {

    static boolean debugMode = true;

    // window size
    static Integer N = 10;
    static AtomicInteger timestamp = new AtomicInteger(0);
    static final Lock lock = new ReentrantLock();

    static InetAddress emulatorAddress = null;
    static int sPort = 0;
    static int rPort = 0;
    static int timeout;    // in millisecond
    static String filename = null;
    static BufferedReader reader = null;

    /*
     * command line input includes the following:
     * 1. <host address of the network emulator> (hostname)
     * 2. <UDP port number used by the emulator to receive data from the sender>
     * 3. <UDP port number used by the sender to receive ACKs from the emulator>
     * 4. <timeout interval in units of millisecond>
     * 5. <name of the file to be transferred>
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // parse command line args
        if (args.length != 5) {
            System.err.println("Invalid number of arguments. Should specify 5 args.");
            System.exit(-1);
        }

        try {
            emulatorAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.err.println("Error: Invalid emulator Address!");
            System.exit(-1);
        }
        if (debugMode) {
            System.out.println("emulatorAddress is " + emulatorAddress);
        }
        try {
            sPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error: port to receive sender's data should be an integer!");
            System.exit(-1);
        }
        if (debugMode) {
            System.out.println("sPort is " + sPort);
        }
        try {
            rPort = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Error: port to receive receiver's ACK should be an integer!");
            System.exit(-1);
        }
        if (debugMode) {
            System.out.println("rPort is " + rPort);
        }
        try {
            timeout = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.err.println("Error: timeout interval should be an integer!");
            System.exit(-1);
        }
        if (debugMode) {
            System.out.println("timeout is " + timeout);
        }
        filename = args[4];
        try {
            reader = new BufferedReader(new FileReader(filename));
            //reader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            System.err.println("Error: file not found!");
            System.exit(-1);
        }
        if (debugMode) {
            System.out.println("filename is " + filename);
        }

        // create input file reader
        MyFileReaderString myFileReaderString = new MyFileReaderString(reader);

        // set the output log files
        PrintStream seqLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/seqnum.log")));
        PrintStream ackLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/ack.log")));
        PrintStream nLog = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/N.log")));

        UDPUtility udpUtility = new UDPUtility(sPort, rPort, emulatorAddress);

        try {
            sendHelper(myFileReaderString, udpUtility, timeout, seqLog, ackLog, nLog);
        } finally {
            seqLog.close();
            ackLog.close();
            nLog.close();
            reader.close();
        }
    }

    public static void sendHelper(MyFileReaderString reader, UDPUtility udpUtility, int timeout,
                                  PrintStream seqLog, PrintStream ackLog, PrintStream nLog)
            throws IOException, InterruptedException {

        // packets in the send-window
        ArrayDeque<Packet> packets = new ArrayDeque<>();
        // ConcurrentLinkedDeque<Packet> packets = new ConcurrentLinkedDeque<>();

        // keeps only a single TimerTask as TCP only use a single timer for the oldest packet
        Timer timer = new Timer();

        // represents that all packets have been sent once (but may or may not acked)
        boolean EOTStage = false;

        // at the beginning
        // get initial packets
        if (debugMode) {
            System.out.println("N = " + N + " before sending the first packet");
        }
        for (int i = 0; i < N; ++i) {
            try {
                Packet packet = reader.getNextPacket();
                packets.add(packet);
                if (debugMode) {
                    System.out.println("i = " + i);
                }
            } catch (EOFException e) {  // EOT
                EOTStage = true;
                break;
            }
        }

        lock.lock();
        try{
            // send initial packets
            boolean isFirst = true;
            for (Packet packet: packets) {
                udpUtility.sendPacket(packet);
                // inc timestamp upon send
                timestamp.incrementAndGet();
                // log the send action
                seqLog.println("t=" + timestamp + " " + packet.getSeqNum());
                seqLog.flush();
                if (debugMode) {
                    System.out.println("t=" + timestamp + " " + packet.getSeqNum() + " in sending initial packets");
                }
                // start the timer for the oldest packet
                if (isFirst) {
                    timer.schedule(new TimeoutTask(packets, udpUtility, nLog, seqLog, timer, timeout), timeout);
                    isFirst = false;
                }
            }
        } finally {
            lock.unlock();
        }

        if (debugMode) {
            System.out.println("Sender: initial packets sent!");
            System.out.println("N = " + N + " after initial packets sent");
        }

        while (true) {
            // the position of this lock have to be after receive. Could socket receive be interrupted? Yes
            /* receive actions */
            Packet ackPacket = udpUtility.receivePacket();

            lock.lock();
            int oldestSeqNum;
            try {
                // inc timestamp upon receive
                timestamp.incrementAndGet();
                // log the ack action
                ackLog.println("t=" + timestamp + " " + ackPacket.getSeqNum());
                ackLog.flush();

                assert packets.peekFirst() != null;
                oldestSeqNum = packets.peekFirst().getSeqNum();
            } finally {
                lock.unlock();
            }

            if (debugMode) {
                System.out.println("t=" + timestamp + " [After sender receiving " + ackPacket.getSeqNum() + " ] Now N = " + N);
            }

            lock.lock();
            try {
                // check if ack seqNum fall within the sending window
                if (ackPacket.getSeqNum() >= oldestSeqNum && ackPacket.getSeqNum() < oldestSeqNum + N) {
                    // accumulative ack
                    // remove acked packets from the window
                    for (int i = 0; i < ackPacket.getSeqNum() - oldestSeqNum + 1; ++i) {
                        packets.pollFirst();
                    }

                    // if there are no in-flight unacked packets in the window now
                    if (ackPacket.getSeqNum() == oldestSeqNum + N - 1) {
                        // If there are no outstanding packets, the timer is stopped.
                        timer.cancel();
                        if (debugMode) {
                            System.out.println("no in-flight packets now!");
                        }
                    } else {
                        // if there are in-flight unacked packets in the window now:
                        // (ackPacket.getSeqNum() < oldestSeqNum + N - 1)
                        // restart timer
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimeoutTask(packets, udpUtility, nLog, seqLog, timer, timeout), timeout);
                    }

                    if (debugMode) {
                        System.out.print("Current packets in the buffer: ");
                    }
                    for (Packet packet: packets) {
                        System.out.print(packet.getSeqNum() + " ");
                    }
                    System.out.println();

                    // inc the sending window size
                    if (N < 10) {
                        ++N;
                        if (debugMode) {
                            System.out.println("t=" + timestamp + " inc N, now N = " + N);
                        }
                        // log: do not inc timestamp here
                        nLog.println("t=" + timestamp + " " + N);
                        nLog.flush();
                    }

                    // if all packets have been acked
                    if (EOTStage && packets.isEmpty()) {
                        // send EOT to the receiver
                        udpUtility.sendPacket(new Packet(Constant.EOT, 0, 0, null));
                        if (debugMode) {
                            System.out.println("Sender EOT sent!");
                        }
                        break;
                    }

                    /* send actions: check if the window if full */
                    // sent but unacked packets remain in the window, only send newly added packets within the window
                    // resend only appears upon timeout
                    boolean emptyWindowFlag = packets.isEmpty();
                    if (packets.size() < N) {   // need to send new packets
                        if (!EOTStage) {
                            int sendNum = N - packets.size();
                            List<Packet> newlyAddedPackets = new ArrayList<>(sendNum);
                            for (int i = 0; i < sendNum; ++i) {
                                try {
                                    Packet packet = reader.getNextPacket();
                                    newlyAddedPackets.add(packet);
                                    packets.add(packet);
                                } catch (EOFException e) {
                                    EOTStage = true;
                                    sendNum = i;
                                }
                            }
                            for (int i = 0; i < sendNum; ++i) {
                                udpUtility.sendPacket(newlyAddedPackets.get(i));
                                // inc timestamp upon send
                                timestamp.incrementAndGet();
                                // log the send action
                                seqLog.println("t=" + timestamp + " " + newlyAddedPackets.get(i).getSeqNum());
                                seqLog.flush();
                                if (debugMode) {
                                    System.out.println("t=" + timestamp +
                                            " [newly added packet] " + newlyAddedPackets.get(i).getSeqNum());
                                }

                                // if i = 0 and timer not started, start timer
                                if (i == 0 && emptyWindowFlag) {    // packets.size == 0 before adding the new packet
                                    timer.cancel();
                                    timer = new Timer();
                                    timer.schedule(new TimeoutTask(packets, udpUtility, nLog, seqLog, timer, timeout), timeout);
                                    if (debugMode) {
                                        System.out.println("restart timer on newly added packet!");
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
        }

        lock.lock();
        try {
            // wait for receiver's EOT
            Packet ackPacket = udpUtility.receivePacket();
            if (ackPacket.getType() == Constant.EOT) {
                timer.cancel();
                if (debugMode) {
                    System.out.println("Sender receives EOT from the receiver!");
                }
                System.exit(0);
            }
        } finally {
            lock.unlock();
        }
    }

    static class TimeoutTask extends TimerTask {
        // private final ConcurrentLinkedDeque<Packet> packets;
        private final ArrayDeque<Packet> packets;
        private final UDPUtility udpUtility;
        private final PrintStream nLog;
        private final PrintStream seqLog;
        private Timer timer;
        private final int timeout;

        TimeoutTask(ArrayDeque<Packet> packets, UDPUtility udpUtility, PrintStream nLog, PrintStream seqLog, Timer timer, int timeout) {
            this.packets = packets;
            this.udpUtility = udpUtility;
            this.nLog = nLog;
            this.seqLog = seqLog;
            this.timer = timer;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                timer.cancel();
                N = 1;
                // inc timestamp upon timeout
                timestamp.incrementAndGet();
                nLog.println("t=" + timestamp + " " + N);
                nLog.flush();
                // retransmission
                Packet packetToResend = packets.peekFirst();
                try {
                    assert packetToResend != null;
                    udpUtility.sendPacket(packetToResend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // log resend: do not inc timestamp here
                seqLog.println("t=" + timestamp + " " + packetToResend.getSeqNum());
                seqLog.flush();
                if (debugMode) {
                    System.out.println("t=" + timestamp + " buffer size: " + packets.size());
                    System.out.println("t=" + timestamp + " timeout N = 1 re-transmit: seqnum = " + packetToResend.getSeqNum());
                }
                // start the timer
                timer = new Timer();
                timer.schedule(new TimeoutTask(packets, udpUtility, nLog, seqLog, timer, timeout), timeout);
            } finally {
                lock.unlock();
            }
        }
    }
}
