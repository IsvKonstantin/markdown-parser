package md2html;

import base.MainFilesChecker;
import base.Randomized;

import java.util.*;

public class Md2HtmlTest {
    protected static Map<String, String> TAGS = new HashMap<>();
    static {
        TAGS.put("*", "em");
        TAGS.put("**", "strong");
        TAGS.put("_", "em");
        TAGS.put("__", "strong");
        TAGS.put("--", "s");
        TAGS.put("`", "code");
    }

    protected final Md2HtmlChecker checker = new Md2HtmlChecker("md2html.Md2Html");
    protected final Randomized random = new Randomized();

    protected void test() {
        test(
                "# ��������� ������� ������\n\n",
                "<h1>��������� ������� ������</h1>"
        );
        test(
                "## �������\n\n",
                "<h2>�������</h2>"
        );
        test(
                "### �������� ## ������\n\n",
                "<h3>�������� ## ������</h3>"
        );
        test(
                "#### ����������\n# ��� ��� ����������\n\n",
                "<h4>����������\n# ��� ��� ����������</h4>"
        );
        test(
                "���� ����� ������,\n�������� ��� ������.",
                "<p>���� ����� ������,\n�������� ��� ������.</p>"
        );
        test(
                "    # ����� ����������, ��� ��� ���������.\n�� ���, ��� ����� ������������ � `#`.\n\n",
                "<p>    # ����� ����������, ��� ��� ���������.\n�� ���, ��� ����� ������������ � <code>#</code>.</p>"
        );
        test(
                "#� ��� �� ���������.\n\n",
                "<p>#� ��� �� ���������.</p>"
        );
        test(
                "###### ��������� ����� ���� ��������������\n(� � ��������� ���������� ���������� �������)\n\n",
                "<h6>��������� ����� ���� ��������������\n(� � ��������� ���������� ���������� �������)</h6>"
        );
        test(
                "�� ��� ����� *��������* ����� _�������_ ���������.\n**������� ���������**, ������������ ������� ����,\n�� __������ �� � ���__?\n������� --������������-- ��� �� ���� �� �������.\n��� �������������� ��������� `code`.\n\n",
                "<p>�� ��� ����� <em>��������</em> ����� <em>�������</em> ���������.\n<strong>������� ���������</strong>, ������������ ������� ����,\n�� <strong>������ �� � ���</strong>?\n������� <s>������������</s> ��� �� ���� �� �������.\n��� �������������� ��������� <code>code</code>.</p>"
        );
        test(
                "�������� ��������, ��� ������������ �����������\nHTML-�������, ����� ��� `<`, `>` � `&`.\n\n",
                "<p>�������� ��������, ��� ������������ �����������\nHTML-�������, ����� ��� <code>&lt;</code>, <code>&gt;</code> � <code>&amp;</code>.</p>"
        );
        test(
                "������������� ������ �������� �� ���� ������: <>&.\n\n",
                "<p>������������� ������ �������� �� ���� ������: &lt;&gt;&amp;.</p>"
        );
        test(
                "������ �� ��, ��� � Markdown, ��������� * � _\n�� �������� ���������?\n��� ��� �� ����� ���� ��������������\n��� ������ ��������� �����: \\*.",
                "<p>������ �� ��, ��� � Markdown, ��������� * � _\n�� �������� ���������?\n��� ��� �� ����� ���� ��������������\n��� ������ ��������� �����: *.</p>"
        );
        test(
                "\n\n\n������ ������ ������ ������ ��������������.\n\n\n\n",
                "<p>������ ������ ������ ������ ��������������.</p>"
        );
        test(
                "������ �� �� *�������� __���������__* ���,\n��� __--�����--__ �� �?",
                "<p>������ �� �� <em>�������� <strong>���������</strong></em> ���,\n��� <strong><s>�����</s></strong> �� �?</p>"
        );

        test("# ��������� ������� ������\n\n" +
                "## �������\n\n" +
                "### �������� ## ������\n\n" +
                "#### ����������\n" +
                "# ��� ��� ����������\n\n" +
                "���� ����� ������,\n" +
                "�������� ��� ������.\n\n" +
                "    # ����� ����������, ��� ��� ���������.\n" +
                "�� ���, ��� ����� ������������ � `#`.\n\n" +
                "#� ��� �� ���������.\n\n" +
                "###### ��������� ����� ���� ��������������\n" +
                "(� � ��������� ���������� ���������� �������)\n\n" +
                "�� ��� ����� *��������* ����� _�������_ ���������.\n" +
                "**������� ���������**, ������������ ������� ����,\n" +
                "�� __������ �� � ���__?\n" +
                "������� --������������-- ��� �� ���� �� �������.\n" +
                "��� �������������� ��������� `code`.\n\n" +
                "�������� ��������, ��� ������������ �����������\n" +
                "HTML-�������, ����� ��� `<`, `>` � `&`.\n\n" +
                "������ �� ��, ��� � Markdown, ��������� * � _\n" +
                "�� �������� ���������?\n" +
                "��� ��� �� ����� ���� ��������������\n" +
                "��� ������ ��������� �����: \\*.\n\n\n\n" +
                "������ ������ ������ ������ ��������������.\n\n" +
                "������ �� �� *�������� __���������__* ���,\n" +
                "��� __--�����--__ �� �?", "<h1>��������� ������� ������</h1>\n" +
                "<h2>�������</h2>\n" +
                "<h3>�������� ## ������</h3>\n" +
                "<h4>����������\n" +
                "# ��� ��� ����������</h4>\n" +
                "<p>���� ����� ������,\n" +
                "�������� ��� ������.</p>\n" +
                "<p>    # ����� ����������, ��� ��� ���������.\n" +
                "�� ���, ��� ����� ������������ � <code>#</code>.</p>\n" +
                "<p>#� ��� �� ���������.</p>\n" +
                "<h6>��������� ����� ���� ��������������\n" +
                "(� � ��������� ���������� ���������� �������)</h6>\n" +
                "<p>�� ��� ����� <em>��������</em> ����� <em>�������</em> ���������.\n" +
                "<strong>������� ���������</strong>, ������������ ������� ����,\n" +
                "�� <strong>������ �� � ���</strong>?\n" +
                "������� <s>������������</s> ��� �� ���� �� �������.\n" +
                "��� �������������� ��������� <code>code</code>.</p>\n" +
                "<p>�������� ��������, ��� ������������ �����������\n" +
                "HTML-�������, ����� ��� <code>&lt;</code>, <code>&gt;</code> � <code>&amp;</code>.</p>\n" +
                "<p>������ �� ��, ��� � Markdown, ��������� * � _\n" +
                "�� �������� ���������?\n" +
                "��� ��� �� ����� ���� ��������������\n" +
                "��� ������ ��������� �����: *.</p>\n" +
                "<p>������ ������ ������ ������ ��������������.</p>\n" +
                "<p>������ �� �� <em>�������� <strong>���������</strong></em> ���,\n" +
                "��� <strong><s>�����</s></strong> �� �?</p>\n");

        test("# ��� �������� ������ � �����", "<h1>��� �������� ������ � �����</h1>");
        test("# ���� ������� ������ � �����\n", "<h1>���� ������� ������ � �����</h1>");
        test("# ��� �������� ������ � �����\n\n", "<h1>��� �������� ������ � �����</h1>");
        test(
                "��������� ����� *���������� �� ����� ������,\n � �������������* �� ������",
                "<p>��������� ����� <em>���������� �� ����� ������,\n � �������������</em> �� ������</p>"
        );
        test("# *���������* � `���` � ����������", "<h1><em>���������</em> � <code>���</code> � ����������</h1>");

        for (final String markup : TAGS.keySet()) {
            randomTest(3, 10, markup);
        }

        randomTest(100, 1000, "_", "**", "`", "--");
        randomTest(100, 1000, "*", "__", "`", "--");
    }

    protected void test(final String input, final String output) {
        checker.test(input, output);
    }

    protected void randomTest(final int paragraphs, final int length, final String... markup) {
        final StringBuilder input = new StringBuilder();
        final StringBuilder output = new StringBuilder();
        emptyLines(input);
        final List<String> markupList = new ArrayList<>(Arrays.asList(markup));
        for (int i = 0; i < paragraphs; i++) {
            final StringBuilder inputSB = new StringBuilder();
            paragraph(length, inputSB, output, markupList);
            input.append(inputSB);
            emptyLines(input);
        }
        test(input.toString(), output.toString());
    }

    private void paragraph(final int length, final StringBuilder input, final StringBuilder output, final List<String> markup) {
        final int h = checker.randomInt(0, 6);
        final String tag = h == 0 ? "p" : "h" + h;
        if (h > 0) {
            input.append(new String(new char[h]).replace('\0', '#')).append(" ");
        }

        open(output, tag);
        while (input.length() < length) {
            generate(markup, input, output);
            final String middle = checker.randomString(Randomized.ENGLISH);
            input.append(middle).append("\n");
            output.append(middle).append("\n");
        }
        output.setLength(output.length() - 1);
        close(output, tag);

        output.append("\n");
        input.append("\n");
    }

    private void randomSpace(final StringBuilder input, final StringBuilder output) {
        if (checker.random.nextBoolean()) {
            final String space = checker.random.nextBoolean() ? " " : "\n";
            input.append(space);
            output.append(space);
        }
    }

    protected void generate(final List<String> markup, final StringBuilder input, final StringBuilder output) {
        word(input, output);
        if (markup.isEmpty()) {
            return;
        }
        final String type = checker.randomItem(markup);

        markup.remove(type);
        if (TAGS.containsKey(type)) {
            ordinary(markup, input, output, type);
        } else {
            special(markup, input, output, type);
        }
        markup.add(type);
    }

    private void ordinary(final List<String> markup, final StringBuilder input, final StringBuilder output, final String type) {
        final String tag = TAGS.get(type);

        randomSpace(input, output);
        input.append(type);
        open(output, tag);

        word(input, output);
        generate(markup, input, output);
        word(input, output);

        input.append(type);
        close(output, tag);
        randomSpace(input, output);
    }

    protected void special(final List<String> markup, final StringBuilder input, final StringBuilder output, final String type) {
    }

    private void word(final StringBuilder input, final StringBuilder output) {
        final String word = checker.randomString(Randomized.ENGLISH);
        input.append(word);
        output.append(word);
    }

    private static void open(final StringBuilder output, final String tag) {
        output.append("<").append(tag).append(">");
    }

    private static void close(final StringBuilder output, final String tag) {
        output.append("</").append(tag).append(">");
    }

    private void emptyLines(final StringBuilder sb) {
        while (checker.random.nextBoolean()) {
            sb.append('\n');
        }
    }

    static class Md2HtmlChecker extends MainFilesChecker {
        public Md2HtmlChecker(final String className) {
            super(className);
        }

        private void test(final String input, final String output) {
            final List<String> run = runFiles(List.of(input.split("\n")));
            checkEquals(List.of(output.split("\n")), run);
        }
    }

    protected void run() {
        test();
        checker.printStatus();
    }

    public static void main(final String... args) {
        new Md2HtmlTest().run();
    }
}