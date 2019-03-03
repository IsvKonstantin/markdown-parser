package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Md2Html {
    public static void main(String[] args) throws IOException {
        String result = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0], StandardCharsets.UTF_8))) {
            MarkdownParser parser = new MarkdownParser(reader);
            result = parser.parse();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("File name was not assigned correctly");
        } catch (FileNotFoundException e) {
            System.out.println("File \"" + args[0] +"\" was not found");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(args[1], StandardCharsets.UTF_8))) {
            writer.write(result);
        } catch (IOException e) {
            System.out.println("Error occurred while writing to file \"" + args[1]);
        }
    }
}
