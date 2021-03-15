package sender;

import shared.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Sender {

    // window size
    static Integer N = 10;
    // or int? AtomicLong?
    static AtomicInteger timestamp = new AtomicInteger(0);
    static final Lock lock = new ReentrantLock();

    static InetAddress emulatorAddress = null;
    static int sPort = 0;
    static int rPort = 0;
    static int timeout = 0;    // in millisecond
    static String filename = null;
    static BufferedInputStream reader = null;

    /*
     * command line input includes the following:
     * 1. <host address of the network emulator> (hostname)
     * 2. <UDP port number used by the emulator to receive data from the sender>
     * 3. <UDP port number used by the sender to receive ACKs from the emulator>
     * 4. <timeout interval in units of millisecond>
     * 5. <name of the file to be transferred> in the given order.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // parse command line args
        if (args.length != 5) {
            System.err.println("Invalid number of arguments. Should specify 5 args.");
            System.exit(-1);
        }

//        InetAddress emulatorAddress = null;
//        int sPort = 0;
//        int rPort = 0;
//        int timeout = 0;    // in millisecond
//        String filename = null;
//        BufferedInputStream reader = null;
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

        UDPUtility udpUtility = new UDPUtility(sPort, rPort, emulatorAddress);

//        // TODO: seqNum
//        int seqNum = 0;
//        Packet currPacket = myFileReaderBytes.getNextPacket();
//        udpUtility.sendPacket(currPacket);
//        // ++timestamp;
//        timestamp.incrementAndGet();
//        seqLog.println("t=" + timestamp + " " + seqNum);
//
//        Packet ack = udpUtility.receivePacket();
//        // ++timestamp;
//        timestamp.incrementAndGet();
//        ackLog.println("t=" + timestamp + " " + ack.getSeqNum());

        try {
            sendHelper(myFileReaderBytes, udpUtility, timeout, seqLog, ackLog, nLog);
        } finally {
            seqLog.close();
            ackLog.close();
            nLog.close();
            reader.close();
        }
    }

    /* TODO: need lock in timer task and sender task for N
        timeout task:
            1. inc timestamp and log
            2. resend and log
            3. dec window
         EOT
     */
    public static void sendHelper(MyFileReaderBytes reader, UDPUtility udpUtility, int timeout,
                                  PrintStream seqLog, PrintStream ackLog, PrintStream nLog)
            throws IOException, InterruptedException {
        // packets in the send-window
        // LinkedList<Packet> packets = new LinkedList<>();

        ConcurrentLinkedDeque<Packet> packets = new ConcurrentLinkedDeque<>();
        // keeps only a single TimerTask as TCP only use a single timer for the oldest packet
        Timer timer = new Timer();
        boolean timerStarted = false;
        // represents that all packets have been sent once (but may or may not acked)
        boolean EOTStage = false;

        lock.lock();
        // at the beginning
        // get initial packets
        for (int i = 0; i < N; ++i) {
            try {
                Packet packet = reader.getNextPacket();
                packets.add(packet);
                System.out.println("i = " + i);
                // System.out.println(packet.getData());
            } catch (EOFException e) {  // EOT
                // TODO: EOT handling
                EOTStage = true;
                break;
            }
        }
        // send initial packets
        boolean isFirst = true;
        for (Packet packet: packets) {
            udpUtility.sendPacket(packet);
            // inc timestamp upon send
            timestamp.incrementAndGet();
            // log the send action
            seqLog.println("t=" + timestamp + " " + packet.getSeqNum());
            // start the timer for the oldest packet
            if (isFirst) {
                timer.schedule(new TimeoutTask(packets, udpUtility, nLog, seqLog, timer, timeout), timeout);
                timerStarted = true;
                isFirst = false;
            }
        }
        lock.unlock();

        System.out.println("Sender: initial packets sent!");

        while (true) {
            // TODO: the position of this lock. Could socket receive be interrupted?
            /* receive actions */
            Packet ackPacket = udpUtility.receivePacket();
            lock.lock();
            // inc timestamp upon receive
            timestamp.incrementAndGet();
            // log the ack action
            ackLog.println("t=" + timestamp + " " + ackPacket.getSeqNum());

            assert packets.peekFirst() != null;
            int oldestSeqNum = packets.peekFirst().getSeqNum();
            lock.unlock();

            // Thread.sleep(1);

            // check if ack seqNum fall within the sending window
            lock.lock();
            if (ackPacket.getSeqNum() >= oldestSeqNum && ackPacket.getSeqNum() < oldestSeqNum + N) {
                // if there are unacked packets
                if (ackPacket.getSeqNum() < oldestSeqNum + N - 1) {
                    // restart timer
                    // if (timerStarted) { timer.cancel(); }
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new TimeoutTask(packets, udpUtility, nLog, seqLog, timer, timeout), timeout);
                    timerStarted = true;
                }
                // remove acked packets from the window
                for (int i = 0; i < ackPacket.getSeqNum() - oldestSeqNum + 1; ++i) {
                    packets.pollFirst();
                }
                // inc the sending window size
                if (N < 10) {
                    ++N;
                    // log: do not inc timestamp here
                    nLog.println("t=" + timestamp + " " + N);
                }
                lock.unlock();

                // Thread.sleep(1);

                // if all packets have been acked
                if (EOTStage && packets.isEmpty()) {
                    // send EOT to the receiver
                    udpUtility.sendPacket(new Packet(Constant.EOT, 0, 0, null));
                    break;
                }

                lock.lock();
                /* send actions: check if the window if full */
                // sent but unacked packets remain in the window, only send newly added packets within the window
                // resend only appears upon timeout
                if (packets.size() < N) {
                    if (!EOTStage) {
                        int sendNum = N - packets.size();
                        List<Packet> newlyAddedPackets = new ArrayList<>(sendNum);
                        // int[] seqNums = new int[sendNum];
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

                            // if i = 0 and timer not started, start timer
                            if (i == 0) {
                                // if (timerStarted) { timer.cancel(); }
                                timer.cancel();
                                timer = new Timer();
                                timer.schedule(new TimeoutTask(packets, udpUtility, nLog, seqLog, timer, timeout), timeout);
                                timerStarted = true;
                            }
                        }
                    }
                } else {
                    // start timer
                    // if (timerStarted) { timer.cancel(); }
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new TimeoutTask(packets, udpUtility, nLog, seqLog, timer, timeout), timeout);
                    timerStarted = true;
                }
            }
            lock.unlock();
        }

        // wait for receiver's EOT
        Packet ackPacket = udpUtility.receivePacket();
        if (ackPacket.getType() == Constant.EOT) {
            // for [debug]
            System.out.println("Sender receives EOT from the receiver!");
            // System.exit(0);
        }
    }

    static class TimeoutTask extends TimerTask {
        private final ConcurrentLinkedDeque<Packet> packets;
        private final UDPUtility udpUtility;
        private final PrintStream nLog;
        private final PrintStream seqLog;
        private final Timer timer;
        private final int timeout;

        TimeoutTask(ConcurrentLinkedDeque<Packet> packets, UDPUtility udpUtility, PrintStream nLog, PrintStream seqLog, Timer timer, int timeout) {
            this.packets = packets;
            this.udpUtility = udpUtility;
            this.nLog = nLog;
            this.seqLog = seqLog;
            this.timer = timer;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            // TODO: these assignments need to be atomic
            lock.lock();
            // do not need to timer.cancel() since itself is the single task instance
            N = 1;
            // inc timestamp upon timeout
            timestamp.incrementAndGet();
            nLog.println("t=" + timestamp + " " + N);
            // retransmission
            Packet packetToResend = packets.peekFirst();
            try {
                assert packetToResend != null;
                udpUtility.sendPacket(packetToResend);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // log resend: do not inc timestamp here
            seqLog.println("t=" + packetToResend.getSeqNum() + " " + N);
            // start the timer
            timer.schedule(new TimeoutTask(packets, udpUtility, nLog, seqLog, timer, timeout), timeout);
            lock.unlock();
        }
    }






    static class SenderSendTask implements Runnable {
        private final ConcurrentLinkedDeque<Packet> packets;

        SenderSendTask(ConcurrentLinkedDeque<Packet> packets) {
            this.packets = packets;
        }

        @Override
        public void run() {

            while (true) {
                if (packets.size() < N) {

                }
            }
        }
    }

    static class SenderReceiveTask implements Runnable {
        private final ConcurrentLinkedDeque<Packet> packets;

        SenderReceiveTask(ConcurrentLinkedDeque<Packet> packets) {
            this.packets = packets;
        }

        @Override
        public void run() {

        }
    }
}
