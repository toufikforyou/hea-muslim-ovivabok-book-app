package com.toufikhasan.heamoslimovivabok;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Splits the simple chapter HTML (a list of &lt;p&gt; tags) into paragraphs. */
public final class ChapterParser {

    /** One paragraph: still HTML (inline entities/line breaks), plus whether it was centered. */
    public static final class Block {
        public final String html;
        public final boolean centered;

        Block(String html, boolean centered) {
            this.html = html;
            this.centered = centered;
        }
    }

    private static final Pattern PARAGRAPH = Pattern.compile("<p([^>]*)>(.*?)</p>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern TAGS = Pattern.compile("<[^>]+>");
    private static final Pattern CENTER = Pattern.compile("text-align\\s*:\\s*center", Pattern.CASE_INSENSITIVE);

    private ChapterParser() {
    }

    public static List<Block> parse(String html) {
        List<Block> blocks = new ArrayList<>();
        Matcher matcher = PARAGRAPH.matcher(html);
        while (matcher.find()) {
            String body = matcher.group(2).trim();
            String visible = TAGS.matcher(body).replaceAll("").replace("&nbsp;", " ").trim();
            if (visible.isEmpty()) {
                continue;
            }
            blocks.add(new Block(body, CENTER.matcher(matcher.group(1)).find()));
        }
        return blocks;
    }
}
