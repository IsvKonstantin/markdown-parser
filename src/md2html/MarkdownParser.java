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
    private HashMap<Character, Integer> tags = new HashMap<>();
    private BufferedReader reader;
    private List<String> paragraph;

    public MarkdownParser(BufferedReader reader) {
        this.reader = reader;
    }

    private void setTagMap() {
        tags.put('*', 0);
        tags.put('_', 0);
        tags.put('-', 0);
        tags.put('+', 0);
        tags.put('`', 0);
        tags.put('\\', 0);
    }

    public String parse() throws IOException {
        paragraph = new ArrayList<>();
        setTagMap();
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty()) {
                paragraph.add(line);
            } else if (!paragraph.isEmpty()) {
                result.append(parseParagraph()).append('\n');
                setTagMap();
                paragraph.clear();
            }
        }
        result.append(parseParagraph());
        return result.toString();
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
        tags.put(tag, tags.get(tag) + 1);
    }

    private void decreaseTagOccurrences(char tag) {
        tags.put(tag, tags.get(tag) - 1);
    }

    //Returns true if there is a sequence of 2 identical characters in line at given index
    private boolean testNextChar(String line, int charIndex) {
        return charIndex + 1 < line.length() && line.charAt(charIndex + 1) == line.charAt(charIndex);
    }

    //Returns true if tag count is even, false if odd
    private boolean testTagOccurrences(char tag) {
        return tags.get(tag) % 2 == 0;
    }

    private boolean tagWasInParagraph(char tag) {
        return tags.get(tag) > 0;
    }

    private void appendTag(String tag, String clarification, StringBuilder result) {
        switch (clarification) {
            case "opening":
                result.append("<").append(MD_TO_HTML_TAGS.get(tag)).append(">");
                break;
            case "closing":
                result.append("</").append(MD_TO_HTML_TAGS.get(tag)).append(">");
                break;
        }
    }

    //Count number of tag's occurrences in given text paragraph
    private void countTags() {
        for (String line : paragraph) {
            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                if (tags.containsKey(ch)) {
                    if (ch == '*' || ch == '_') {
                        if (testNextChar(line, i)) {
                            i++;
                            increaseTagOccurrences(ch);
                        } else {
                            increaseTagOccurrences(ch);
                        }
                    } else if (ch == '-' || ch == '+') {
                        increaseTagOccurrences(ch);
                    } else if (ch == '`') {
                        increaseTagOccurrences(ch);
                    } else if (ch == '\\') {
                        i++;
                    }
                }
            }
        }
        for (Map.Entry<Character, Integer> entry : tags.entrySet()) {
            Character tag = entry.getKey();
            tags.put(tag, tags.get(tag) - tags.get(tag) % 2);
        }
    }

    private StringBuilder parseParagraph() {
        StringBuilder result = new StringBuilder("");
        int headerLevel = getHeaderLevel(paragraph.get(0));
        if (headerLevel > 0)
            paragraph.set(0, new StringBuilder(paragraph.get(0)).delete(0, headerLevel + 1).toString());

        countTags();

        for (String line : paragraph) {
            for (int lineIndex = 0; lineIndex < line.length(); ++lineIndex) {
                char ch = line.charAt(lineIndex);
                if (ch == '*' || ch == '_') {
                    if (testNextChar(line, lineIndex) && tagWasInParagraph(ch)) {
                        createDoubleTag(result, ch, 2);
                        lineIndex++;
                    } else {
                        if (tagWasInParagraph(ch)) {
                            createSingleTag(result, lineIndex, ch);
                        } else result.append(line.charAt(lineIndex));
                    }
                } else if (ch == '-' || ch == '+') {
                    if (testNextChar(line, lineIndex) && tagWasInParagraph(ch)) {
                        createDoubleTag(result, ch, 2);
                        lineIndex++;
                    } else result.append(line.charAt(lineIndex));
                } else if (ch == '`') {
                    if (tagWasInParagraph(ch)) {
                        createSingleTag(result, lineIndex, ch);
                    }
                } else if (ch == '\\') {
                    //
                } else if (SPECIAL_SYMBOLS.containsKey(ch)) {
                    result.append(SPECIAL_SYMBOLS.get(ch));
                } else {
                    result.append(line.charAt(lineIndex));
                }
            }
            result.append('\n');
        }
        result.deleteCharAt(result.length() - 1);
        result = wrapParagraph(headerLevel, result);
        return result;
    }

    private void createSingleTag(StringBuilder result, int i, char ch) {
        if (testTagOccurrences(ch))
            appendTag(Character.toString(ch), "opening", result);
        else
            appendTag(Character.toString(ch), "closing", result);
        decreaseTagOccurrences(ch);
    }

    private void createDoubleTag(StringBuilder result, char ch, int tagLength) {
        String tag = ch + "" + ch;
        appendTag(tag, testTagOccurrences(ch) ? "opening" : "closing", result);
        decreaseTagOccurrences(ch);
    }
}