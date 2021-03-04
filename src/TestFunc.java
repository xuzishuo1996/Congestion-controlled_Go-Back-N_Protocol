import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestFunc {
    public static void main(String[] args) {
        // https://stackoverflow.com/questions/23068676/how-to-get-current-timestamp-in-string-format-in-java-yyyy-mm-dd-hh-mm-ss
        String timestamp = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss").format(new Date());
        System.out.println(timestamp);
    }
}
