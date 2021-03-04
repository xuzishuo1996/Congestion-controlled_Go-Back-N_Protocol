package shared;

import java.nio.ByteBuffer;

public class Packet {
    private int type;       // 0: ACK, 1: Data, 2: EOT
    private int seqNum;     // Modulo 32
    private int length;     // Length of the String variable ‘data’; for ACK, it should be set to 0
    private String data;    // String with Max Length: 500 chars

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
        if (length != 0) {
            byte[] tmp = new byte[length];
            buffer.get(tmp, 0, length);
            data = new String(tmp);
        }
        return new Packet(type, seqNum, length, data);
    }

    public byte[] toUDPBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(Constant.MAX_DATA_PACKET_SIZE);
        buffer.putInt(type);
        buffer.putInt(seqNum);
        buffer.putInt(length);
        if (length > 0) {
            buffer.put(data.getBytes(), 0, length);
        }
        return buffer.array();
    }
}
