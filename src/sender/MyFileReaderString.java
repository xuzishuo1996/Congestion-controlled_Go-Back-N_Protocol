package sender;

import shared.Constant;

import java.io.*;

public class MyFileReaderString {
    private static final int MAX_SEGMENT_LENGTH = Constant.MAX_SEGMENT_LENGTH;
    // BufferedInputStream read raw bytes, whereas BufferedReader read characters
    private final BufferedReader bufferedReader;

    public MyFileReaderString(BufferedReader bufferedReader) {
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
        // System.getProperty("user.dir")           // src path
        // /home/xuzishuo1996/Waterloo/cs656-repos/a2/input1.txt
        BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/input1.txt"));
        MyFileReaderString myFileReader = new MyFileReaderString(reader);
        try{
            while (true) {
                System.out.println(myFileReader.getNextSegment());
            }
        } catch (EOFException e) {
            System.out.println("has read the file.");
        }
    }
}
