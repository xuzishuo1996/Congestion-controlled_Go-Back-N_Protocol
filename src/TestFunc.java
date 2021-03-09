import java.util.Timer;
import java.util.TimerTask;

public class TestFunc {
    public static void main(String[] args) throws InterruptedException {
//        // https://stackoverflow.com/questions/23068676/how-to-get-current-timestamp-in-string-format-in-java-yyyy-mm-dd-hh-mm-ss
//        String timestamp = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss").format(new Date());
//        System.out.println(timestamp);
        Timer timer = new Timer();
        int timeout = 3000;
        timer.schedule(new TimeoutTask(timer, timeout), timeout);
        // Thread.sleep(timeout - 1);
        // timer.cancel(); // will not cancel if sleep 3000 msec because the task is executing; will cancel if sleep 2999
        // timer.purge();      // will also cancel the executing task
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
