package customers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private static final String LOG_PATH = "../../docs/log.txt";

    public static synchronized void log(long ts, long tj) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_PATH, true))) {
            writer.write("TS" + ts + ",TJ=" + tj);
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
