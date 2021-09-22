package sender;

import shared.Constant;
import shared.Packet;

import java.io.*;

public class MyFileReaderString {
    private static final int MAX_SEGMENT_LENGTH = Constant.MAX_STRING_LENGTH;
    private int seqNum = -1;

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

    public Packet getNextPacket() throws IOException {
        seqNum = (seqNum + 1 + Constant.MODULO) % Constant.MODULO;

        char[] buffer = new char[MAX_SEGMENT_LENGTH];

        int charCnt = bufferedReader.read(buffer);
        if (charCnt != -1) {
            // need charCnt in case the number of chars < MAX_SEGMENT_LENGTH
            String data = String.valueOf(buffer,0, charCnt);
            return new Packet(Constant.DATA, seqNum, data.length(), data);
        } else {
            bufferedReader.close();
            // signal eof
            throw new EOFException("End of input file!");
        }
    }

    
    /**
     * for test only
     */
    public static void main(String[] args) throws IOException {
        // System.getProperty("java.class.path")    // class path
        // System.getProperty("user.dir")           // src path
        // /home/xuzishuo1996/Waterloo/cs656-repos/a2/input1.txt

//        // pass
//        BufferedReader reader = new BufferedReader(
//                new FileReader("/home/xuzishuo1996/Waterloo/cs656-docs/a2/data/input/input1.txt"));
//        MyFileReaderString myFileReader = new MyFileReaderString(reader);
//
//        PrintStream log = new PrintStream(
//                new BufferedOutputStream(
//                        new FileOutputStream("/home/xuzishuo1996/Waterloo/cs656-docs/a2/data/output/test1.txt")));
//        try{
//            while (true) {
//                log.print(myFileReader.getNextSegment());
//                log.flush();
//            }
//        } catch (EOFException e) {
//            System.out.println("has read the file.");
//        }

        // pass
        BufferedReader reader = new BufferedReader(
                new FileReader("/home/xuzishuo1996/Waterloo/cs656-docs/a2/data/input/input1.txt"));
        MyFileReaderString myFileReader = new MyFileReaderString(reader);
        BufferedWriter writer = new BufferedWriter(
                new FileWriter("/home/xuzishuo1996/Waterloo/cs656-docs/a2/data/output/test1.txt"));
        try{
            while (true) {
                writer.write(myFileReader.getNextSegment());
                writer.flush(); // have to flush
            }
        } catch (EOFException e) {
            System.out.println("has read the file.");
        }
    }
}
