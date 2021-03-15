import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class TestFunc {
    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {

        int a = 0;
        int MODULO = 32;
        int res = (a - 1 + MODULO) % MODULO;
        System.out.println(res);

//        // https://stackoverflow.com/questions/23068676/how-to-get-current-timestamp-in-string-format-in-java-yyyy-mm-dd-hh-mm-ss
//        String timestamp = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss").format(new Date());
//        System.out.println(timestamp);

//        Timer timer = new Timer();
//        int timeout = 3000;
//        timer.schedule(new TimeoutTask(timer, timeout), timeout);
        // Thread.sleep(timeout - 1);
        // timer.cancel(); // will not cancel if sleep 3000 msec because the task is executing; will cancel if sleep 2999
        // timer.purge();      // will also cancel the executing task

        // String s1 = "adasfasfasfasfas\n\n";
//        String s1 = "xASFAF*&/\n\n";
//        System.out.println(s1.length());
//        byte[] bytes1 = s1.getBytes(StandardCharsets.UTF_8);
//        System.out.println(bytes1.length);
//        System.out.println(s1);
//        for (byte b: bytes1) {
//            System.out.print(b);
//        }
//        System.out.println();
    }

    static class TimeoutTask extends TimerTask {
        private static int cnt= 0;
        private final Timer timer;
        private final int timeout;

        TimeoutTask(Timer timer, int timeout) {
            this.timer = timer;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            int a = 0;
            for (int i = 0; i < Integer.MAX_VALUE; ++i) {
                ++a;
            }
            System.out.println("TimeoutTask completed");
            ++cnt;
            if (cnt < 5) {
                timer.schedule(new TimeoutTask(timer, timeout), timeout);
            }
        }
    }
}
