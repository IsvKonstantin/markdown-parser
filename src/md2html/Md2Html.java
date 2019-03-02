package md2html;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Md2Html {
    public static void main(String[] args) throws IOException {
        String result;
        try(BufferedReader reader = new BufferedReader(new FileReader(args[0], StandardCharsets.UTF_8))) {
            MarkdownParser parser = new MarkdownParser(reader);
            result = parser.parse();
            System.out.println(result);
        }
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(args[1], StandardCharsets.UTF_8))) {
            writer.write(result);
        }
    }
}
