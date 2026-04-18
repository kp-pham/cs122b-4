import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class IngestData {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("File name: ");
        String filename = scanner.nextLine();

        parseCSV(filename);
    }

    public static void parseCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
