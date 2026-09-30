import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class CountCharacters {

    static String userFilePath;

    /*
     * Prompts the user for the file they want compressed. Keeps asking
     * until the path points to an existing file.
     */
    public static void promptForFile(Scanner scanner) {
        while (true) {
            System.out.println("Enter the file you would like to be compressed: ");
            String userIn = scanner.nextLine().trim();
            if (Files.isRegularFile(Paths.get(userIn))) {
                userFilePath = userIn;
                return;
            }
            System.out.println("Could not find that file, try again.");
        }
    }

    public static String getUserIn() {
        return userFilePath;
    }

    /*
     * Reads the whole file and records how many times each character appears.
     * Reading the raw text (instead of line by line) means newlines and
     * whitespace are counted too, so the decompressed file matches exactly.
     */
    public static HashMap<Character, Integer> countCharacters() throws IOException {
        String text = Files.readString(Paths.get(userFilePath), StandardCharsets.UTF_8);
        HashMap<Character, Integer> map = new HashMap<>();

        for (int i = 0; i < text.length(); i++) {
            map.merge(text.charAt(i), 1, Integer::sum);
        }
        return map;
    }
}