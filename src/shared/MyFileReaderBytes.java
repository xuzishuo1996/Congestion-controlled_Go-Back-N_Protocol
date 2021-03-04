package shared;

import java.io.*;

public class MyFileReaderBytes {
    private static final int MAX_SEGMENT_LENGTH = Constant.MAX_SEGMENT_LENGTH;

    // BufferedInputStream read raw bytes, whereas BufferedReader read characters
    private final BufferedInputStream bufferedInputStream;

    public MyFileReaderBytes(BufferedInputStream bufferedInputStream) {
        this.bufferedInputStream = bufferedInputStream;
    }

    public byte[] getNextSegment() throws IOException {
        byte[] buffer = new byte[Constant.MAX_DATA_PACKET_SIZE];

        int charCnt = bufferedInputStream.read(buffer);
        if (charCnt != -1) {
            // need charCnt in case the number of chars < MAX_SEGMENT_LENGTH
            return buffer;
        } else {
            bufferedInputStream.close();
            // signal eof
            throw new EOFException("End of input file!");
        }
    }
}
