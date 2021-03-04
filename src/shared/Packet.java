package shared;

public class Packet {
    int type;       // 0: ACK, 1: Data, 2: EOT
    int seqNum;     // Modulo 32
    int length;     // Length of the String variable ‘data’; for ACK, it should be set to 0
    String data;    // String with Max Length: 500 chars

    public Packet(int type, int seqNum, int length, String data) {
        this.type = type;
        this.seqNum = seqNum;
        this.length = length;
        this.data = data;
    }
}
