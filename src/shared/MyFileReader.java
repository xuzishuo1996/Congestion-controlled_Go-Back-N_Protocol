package shared;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MyFileReader {
    private static final int MAX_SEGMENT_LENGTH = 10;
    // BufferedInputStream read raw bytes, whereas BufferedReader read characters
    private final BufferedReader bufferedReader;

    public MyFileReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public String getNextSegment() throws IOException {
        char[] buffer = new char[MAX_SEGMENT_LENGTH];

        int charCnt = bufferedReader.read(buffer);
        if (charCnt != -1) {
            // need charCnt in case the number of chars < MAX_SEGMENT_LENGTH
            return String.valueOf(buffer,0, charCnt);
        } else {
            bufferedReader.close();
            // signal eof
            throw new EOFException("End of input file!");
        }
    }

    // pass test
    public static void main(String[] args) throws IOException {
        // System.getProperty("java.class.path")    // class path
        // System.getProperty("user.dir")           // project path
        // /home/xuzishuo1996/Waterloo/cs656-repos/a2/input1.txt
        BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/input1.txt"));
        MyFileReader myFileReader = new MyFileReader(reader);
        try{
            while (true) {
                System.out.println(myFileReader.getNextSegment());
            }
        } catch (EOFException e) {
            System.out.println("has read the file.");
        }
    }
}
