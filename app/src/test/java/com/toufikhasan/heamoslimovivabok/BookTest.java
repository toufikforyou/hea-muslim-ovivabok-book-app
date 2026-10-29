package com.toufikhasan.heamoslimovivabok;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class BookTest {

    private static final File ASSETS = new File("src/main/assets");

    @Test
    public void banglaNumber_convertsEveryDigit() {
        assertEquals("০", Bangla.number(0));
        assertEquals("১০", Bangla.number(10));
        assertEquals("৯৯", Bangla.number(99));
    }

    @Test
    public void indexOf_findsChaptersAndSkipsWriterPage() {
        assertEquals(0, Book.indexOf("1"));
        assertEquals(9, Book.indexOf("10"));
        assertEquals(-1, Book.indexOf(Book.ABOUT_WRITER.getFileName()));
        assertNull(Book.find(null));
    }

    @Test
    public void parser_keepsTextParagraphsAndAlignment() {
        List<ChapterParser.Block> blocks = ChapterParser.parse(
                "<p style=\"text-align:center;\">শিরোনাম</p>\n<p><br></p>\n<p>&nbsp; </p>\n<p>প্রথম &bull; অনুচ্ছেদ</p>");
        assertEquals(2, blocks.size());
        assertTrue(blocks.get(0).centered);
        assertEquals("শিরোনাম", blocks.get(0).html);
        assertFalse(blocks.get(1).centered);
        assertEquals("প্রথম &bull; অনুচ্ছেদ", blocks.get(1).html);
    }

    @Test
    public void everyChapter_hasTextAndHeaderImage() throws IOException {
        for (ChapterModel chapter : Book.CHAPTERS) {
            File html = new File(ASSETS, "chapter/" + chapter.getFileName() + ".html");
            assertTrue("Missing chapter " + chapter.getTitle(), html.isFile());
            String content = new String(Files.readAllBytes(html.toPath()), StandardCharsets.UTF_8);
            assertFalse("Empty chapter " + chapter.getTitle(), ChapterParser.parse(content).isEmpty());
            assertTrue("Missing image for " + chapter.getTitle(),
                    new File(ASSETS, "images/" + chapter.getFileName() + ".jpg").isFile());
        }
    }
}
