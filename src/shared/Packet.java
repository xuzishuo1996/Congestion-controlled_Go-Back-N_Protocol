package shared;

import sender.MyFileReaderString;

import java.io.*;
import java.nio.ByteBuffer;

public class Packet {
    private final int type;       // 0: ACK, 1: Data, 2: EOT
    private final int seqNum;     // Modulo 32
    private final int length;     // Length of the String variable ‘data’; for ACK, it should be set to 0
    private final String data;    // String with Max Length: 500 chars

    public int getType() {
        return type;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public int getLength() {
        return length;
    }

    public String getData() {
        return data;
    }

    public Packet(int type, int seqNum, int length, String data) {
        this.type = type;
        this.seqNum = seqNum;
        this.length = length;
        this.data = data;
    }

    public static Packet parsePacket(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        // System.out.println("[parsePacket]: limit = " + buffer.limit() + " remaining = "  + buffer.remaining() + " capacity = " + buffer.capacity());
        int type = buffer.getInt();
        int seqNum = buffer.getInt();
        int length = buffer.getInt();
        String data = null;
        if (length > 0) {
//            byte[] tmp = new byte[length * Constant.SIZE_OF_CHAR];
//            buffer.get(tmp, 0, length * Constant.SIZE_OF_CHAR);
            byte[] tmp = new byte[bytes.length - Constant.PACKET_HEADER_SIZE];
            buffer.get(tmp, 0, tmp.length);
            data = new String(tmp);
        }
        return new Packet(type, seqNum, length, data);
    }

    public byte[] toUDPBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(Constant.MAX_PACKET_SIZE);
        buffer.putInt(type);
        //System.out.println("type: " + type);
        buffer.putInt(seqNum);
        //System.out.println("seqNum: " + seqNum);
        // length is ok? or have to be data.length() and judge whether data == null?
        buffer.putInt(length);
        //System.out.println("data.length(): " + data.length());
        //System.out.println("length: " + length);

        byte[] dataBytes = null;
        if (length > 0) {
             dataBytes = data.getBytes(Constant.ENCODING);
             // This method transfers bytes into this buffer from the given source array.
             // System.out.println("data.getBytes().length: " + data.getBytes().length);
             buffer.put(dataBytes, 0, dataBytes.length);
//            buffer.put(data.getBytes(Constant.ENCODING), 0, length * Constant.SIZE_OF_CHAR);
        }
        // System.out.println("buffer.array().length: " + buffer.array().length);
        byte[] src = buffer.array();
        int dataLength = (dataBytes == null) ? 0 : dataBytes.length;
        int packetLength = Constant.PACKET_HEADER_SIZE + dataLength;
        byte[] des = new byte[packetLength];
        System.arraycopy(src, 0, des, 0, packetLength);
        return des;
    }

    public static void main(String[] args) throws IOException {

//        ByteBuffer buffer = ByteBuffer.allocate(512);
//        buffer.putInt(1);
//        buffer.putInt(2);
//        String data = "1234567890abcdefghj";
//        buffer.putInt(data.length());
//        byte[] bytes = data.getBytes(Constant.ENCODING);
//        buffer.put(bytes, 0, bytes.length);
//
//        System.out.println("buffer.limit(): " + buffer.limit());
//        System.out.println("buffer.remaining(): " + buffer.remaining());

        String data = "1234567890abcdefghj";
        Packet initialPacket = new Packet(1, 2, data.length(), data);
        byte[] UDPBytes = initialPacket.toUDPBytes();
        System.out.println("converted bytes length: " + UDPBytes.length);

        Packet packet = Packet.parsePacket(UDPBytes);
        System.out.println(packet.getType());
        System.out.println(packet.getSeqNum());
        System.out.println(packet.getLength());
        System.out.println(packet.getData());



//        BufferedReader reader = new BufferedReader(
//                new FileReader("/home/xuzishuo1996/Waterloo/cs656-docs/a2/data/input/input2.txt"));
//        // 1 20 394 (384?)
//        // 26384 characters, 26394 words
//        MyFileReaderString myFileReader = new MyFileReaderString(reader);
//
//        try {
//            while (true) {
//                Packet packet = myFileReader.getNextPacket();
//                byte[] bytes = packet.toUDPBytes();
//                Packet back = Packet.parsePacket(bytes);
//                System.out.println(back.getType());
//                System.out.println(back.getSeqNum());
//                System.out.println(back.getLength());
//                System.out.println(back.getData());
//            }
//        } catch (EOFException ignored) {
//
//        }

//        String data1 = "12311'sfa \n adcfc";
//        System.out.println(data1.length());
//        Packet ack = new Packet(Constant.ACK, 123, 0, null);
//        Packet data_packet1 = new Packet(Constant.DATA, 125, data1.length(), data1);
//        Packet data_packet2 = myFileReader.getNextPacket();
//        byte[] bytes1 = ack.toUDPBytes();
//        byte[] bytes2 = data_packet1.toUDPBytes();
//        byte[] bytes3 = data_packet2.toUDPBytes();
//        Packet ack_back = Packet.parsePacket(bytes1);
//        Packet data1_back = Packet.parsePacket(bytes2);
//        Packet data2_back = Packet.parsePacket(bytes3);
//        Packet back = data2_back;
//        System.out.println(back.getType());
//        System.out.println(back.getSeqNum());
//        System.out.println(back.getLength());
//        System.out.println(back.getData());
    }
}
