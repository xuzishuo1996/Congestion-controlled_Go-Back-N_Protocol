package shared;

public class Constant {
    int MAX_SEGMENT_LENGTH = 500;   // measured in char
    int MODULO = 32;

    enum TYPE {
        ACK, DATA, EOT
    }

    public static void main(String[] args) {
        System.out.println(TYPE.ACK.equals(0));
    }
}
