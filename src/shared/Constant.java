package shared;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Constant {
    public static int MAX_STRING_LENGTH = 500;   // measured in chars
    public static int MODULO = 32;

    public static int SIZE_OF_INT = 4;  // measured in bytes
    public static int SIZE_OF_CHAR = 1; // 1 for English characters
    public static int PACKET_HEADER_SIZE = SIZE_OF_INT * 3;    // measured in bytes; 3 ints in header.
    public static int ACK_SIZE = PACKET_HEADER_SIZE;
    public static int EOT_SIZE = PACKET_HEADER_SIZE;
    public static int MAX_PAYLOAD_SIZE = MAX_STRING_LENGTH * SIZE_OF_CHAR; // measured in bytes;
    public static int MAX_PACKET_SIZE = PACKET_HEADER_SIZE + MAX_PAYLOAD_SIZE; // measured in bytes;

    public static Charset ENCODING = StandardCharsets.UTF_8;

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
