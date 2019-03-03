package md2html;

import java.io.*;
import java.util.*;

public class MarkdownParser {
    private final static Map<Character, String> SPECIAL_SYMBOLS = Map.of(
            '<', "&lt;",
            '>', "&gt;",
            '&', "&amp;"
    );
    private final static Map<String, String> MD_TO_HTML_TAGS = Map.of(
            "*", "em",
            "_", "em",
            "__", "strong",
            "**", "strong",
            "--", "s",
            "`", "code",
            "++", "u"
    );
    private HashMap<Character, Integer> tagOccurrences = new HashMap<>();
    private BufferedReader reader;
    private List<String> paragraph;

    public MarkdownParser(BufferedReader reader) {
        this.reader = reader;
    }

    private void setTagOccurrences() {
        tagOccurrences.put('*', 0);
        tagOccurrences.put('_', 0);
        tagOccurrences.put('-', 0);
        tagOccurrences.put('+', 0);
        tagOccurrences.put('`', 0);
    }

    public String parse() throws IOException {
        paragraph = new ArrayList<>();
        String line;
        setTagOccurrences();
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty()) {
                paragraph.add(line);
            } else if (!paragraph.isEmpty()) {
                result.append(parseParagraph());
                paragraph.clear();
            }
        }
        return result.append(parseParagraph()).toString();
    }

    private int getHeaderLevel(String line) {
        int headerLevel = 0;
        while (line.charAt(headerLevel) == '#') {
            headerLevel++;
        }
        return headerLevel * Boolean.compare(Character.isWhitespace(line.charAt(headerLevel)), false);
    }

    private StringBuilder wrapParagraph(int level, StringBuilder body) {
        StringBuilder result = new StringBuilder();
        result.append(level > 0 ? "<h" + level + ">" : "<p>");
        result.append(body);
        result.append(level > 0 ? "</h" + level + ">" : "</p>");
        return result;
    }

    private void increaseTagOccurrences(char tag) {
        tagOccurrences.put(tag, tagOccurrences.get(tag) + 1);
    }

    private void decreaseTagOccurrences(char tag) {
        tagOccurrences.put(tag, tagOccurrences.get(tag) - 1);
    }

    private boolean testNextChar(String line, int charIndex) {
        return charIndex + 1 < line.length() && line.charAt(charIndex + 1) == line.charAt(charIndex);
    }

    private boolean testTagOccurrences(char tag) {
        return tagOccurrences.get(tag) % 2 == 0;
    }

    private boolean tagWasInParagraph(char tag) {
        return tagOccurrences.get(tag) > 0;
    }

    private void appendTagSymbols(String tag, String clarification, StringBuilder result) {
        switch (clarification) {
            case "opening":
                result.append("<").append(MD_TO_HTML_TAGS.get(tag)).append(">");
                break;
            case "closing":
                result.append("</").append(MD_TO_HTML_TAGS.get(tag)).append(">");
                break;
        }
    }

    private void createTag(StringBuilder result, String tag) {
        char ch = tag.charAt(0);
        appendTagSymbols(tag, testTagOccurrences(ch) ? "opening" : "closing", result);
        decreaseTagOccurrences(ch);
    }

    private void countTagOccurrences() {
        for (String line : paragraph) {
            for (int lineIndex = 0; lineIndex < line.length(); lineIndex++) {
                char ch = line.charAt(lineIndex);
                if (tagOccurrences.containsKey(ch)) {
                    if (testNextChar(line, lineIndex) && MD_TO_HTML_TAGS.containsKey(ch + "" + ch)) {
                        lineIndex++;
                        increaseTagOccurrences(ch);
                    } else if (MD_TO_HTML_TAGS.containsKey(String.valueOf(ch))) {
                        increaseTagOccurrences(ch);
                    }
                } else if (ch == '\\') {
                    lineIndex++;
                }
            }
        }
        for (Map.Entry<Character, Integer> entry : tagOccurrences.entrySet()) {
            Character tag = entry.getKey();
            tagOccurrences.put(tag, tagOccurrences.get(tag) - tagOccurrences.get(tag) % 2);
        }
    }

    private StringBuilder parseParagraph() {
        StringBuilder result = new StringBuilder();

        int headerLevel = getHeaderLevel(paragraph.get(0));
        if (headerLevel > 0)
            paragraph.set(0, new StringBuilder(paragraph.get(0)).delete(0, headerLevel + 1).toString());

        countTagOccurrences();

        for (String line : paragraph) {
            for (int lineIndex = 0; lineIndex < line.length(); lineIndex++) {
                char ch = line.charAt(lineIndex);
                if (tagOccurrences.containsKey(ch) && tagWasInParagraph(ch)) {
                    if (testNextChar(line, lineIndex) && MD_TO_HTML_TAGS.containsKey(ch + "" + ch)) {
                        createTag(result, ch + "" + ch);
                        lineIndex++;
                    } else {
                        createTag(result, String.valueOf(ch));
                    }
                } else if (ch == '\\') {
                    lineIndex++;
                    if (lineIndex < line.length()) {
                        result.append(line.charAt(lineIndex));
                    }
                } else if (SPECIAL_SYMBOLS.containsKey(ch)) {
                    result.append(SPECIAL_SYMBOLS.get(ch));
                } else {
                    result.append(line.charAt(lineIndex));
                }
            }
            result.append('\n');
        }
        return wrapParagraph(headerLevel, result);
    }
}