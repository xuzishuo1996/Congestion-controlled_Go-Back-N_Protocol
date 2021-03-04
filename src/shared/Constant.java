package shared;

public class Constant {
    public static int MAX_SEGMENT_LENGTH = 500;   // measured in char
    public static int MODULO = 32;

    public static int SIZE_OF_INT = 4;  // measured in bytes
    public static int SIZE_OF_CHAR = 2; // measured in bytes
    public static int PACKET_HEADER_SIZE = SIZE_OF_INT * 3;    // 3 ints; also = ACK_SIZE
    public static int ACK_SIZE = PACKET_HEADER_SIZE;
    public static int MAX_PAYLOAD_SIZE = SIZE_OF_CHAR * MAX_SEGMENT_LENGTH; // measured in bytes;
    public static int MAX_DATA_PACKET_SIZE = PACKET_HEADER_SIZE + MAX_PAYLOAD_SIZE; // measured in bytes;

//    enum TYPE {
//        ACK, DATA, EOT
//    }
    public static int ACK = 0;
    public static int DATA = 1;
    public static int EOT = 2;

    public static void main(String[] args) {
        // System.out.println(TYPE.ACK.equals(0));
    }
}
