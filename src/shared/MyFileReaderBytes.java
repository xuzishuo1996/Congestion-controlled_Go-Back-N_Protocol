package shared;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MyFileReaderBytes {
    private static final int MAX_SEGMENT_LENGTH = Constant.MAX_SEGMENT_LENGTH;
    private int seqNum = -1;

    // BufferedInputStream read raw bytes, whereas BufferedReader read characters
    private final BufferedInputStream bufferedInputStream;

    public MyFileReaderBytes(BufferedInputStream bufferedInputStream) {
        this.bufferedInputStream = bufferedInputStream;
    }

    public byte[] getNextSegment() throws IOException {
        byte[] buffer = new byte[Constant.MAX_DATA_PACKET_SIZE];

        int byteCnt = bufferedInputStream.read(buffer);
        if (byteCnt != -1) {
            // need charCnt in case the number of chars < MAX_SEGMENT_LENGTH
            return buffer;
        } else {
            bufferedInputStream.close();
            // signal eof
            throw new EOFException("End of input file!");
        }
    }

    public Packet getNextPacket() throws IOException {
        seqNum = (seqNum + 1 + Constant.MODULO) % Constant.MODULO;

        byte[] buffer = new byte[Constant.MAX_DATA_PACKET_SIZE];

        int byteCnt = bufferedInputStream.read(buffer);
        if (byteCnt != -1) {
            // need charCnt in case the number of chars < MAX_SEGMENT_LENGTH
            return new Packet(Constant.DATA, seqNum, byteCnt, new String(buffer, StandardCharsets.UTF_8));
        } else {
            bufferedInputStream.close();
            // signal eof
            throw new EOFException("End of input file!");
        }
    }
}
