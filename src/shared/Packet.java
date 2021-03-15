package shared;

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
        int type = buffer.getInt();
        int seqNum = buffer.getInt();
        int length = buffer.getInt();
        String data = null;
        if (length > 0) {
            byte[] tmp = new byte[length * Constant.SIZE_OF_CHAR];
            buffer.get(tmp, 0, length * Constant.SIZE_OF_CHAR);
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
        buffer.putInt(data.length());
        //System.out.println("data.length(): " + data.length());
        //System.out.println("length: " + length);
        if (length > 0) {
            // This method transfers bytes into this buffer from the given source array.
            //System.out.println("data.getBytes().length: " + data.getBytes().length);
            buffer.put(data.getBytes(), 0, data.length() * Constant.SIZE_OF_CHAR);
        }
        //System.out.println("buffer.array().length: " + buffer.array().length);
        return buffer.array();
    }
}
