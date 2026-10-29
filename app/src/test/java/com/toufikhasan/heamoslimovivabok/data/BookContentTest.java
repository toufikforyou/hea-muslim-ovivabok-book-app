package com.toufikhasan.heamoslimovivabok.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Guards the bundled book: every chapter in the table of contents must have its text and cover.
 */
public class BookContentTest {

    private static final File MAIN = new File("src/main");
    private static int chapterCount;

    @BeforeClass
    public static void readTableOfContents() throws Exception {
        Document strings = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new File(MAIN, "res/values/strings.xml"));
        NodeList arrays = strings.getElementsByTagName("string-array");
        for (int i = 0; i < arrays.getLength(); i++) {
            Element array = (Element) arrays.item(i);
            if ("chapter_lists_array".equals(array.getAttribute("name"))) {
                chapterCount = array.getElementsByTagName("item").getLength();
            }
        }
    }

    @Test
    public void bookHasTenChapters() {
        assertEquals(10, chapterCount);
    }

    @Test
    public void everyChapterHasTextAndCover() {
        for (int id = 1; id <= chapterCount; id++) {
            File html = new File(MAIN, "assets/" + Chapters.htmlAsset(id));
            File cover = new File(MAIN, "assets/" + Chapters.coverAsset(id));
            assertTrue("Missing " + html, html.isFile() && html.length() > 0);
            assertTrue("Missing " + cover, cover.isFile() && cover.length() > 0);
        }
    }

    @Test
    public void chapterIdsAreOneBased() {
        assertFalse(Chapters.isValid(0, 10));
        assertTrue(Chapters.isValid(1, 10));
        assertTrue(Chapters.isValid(10, 10));
        assertFalse(Chapters.isValid(11, 10));
    }

    @Test
    public void readerStylesheetAndFontAreBundled() {
        assertTrue(new File(MAIN, "assets/css/style.css").isFile());
        assertTrue(new File(MAIN, "assets/font/bangla_font.ttf").isFile());
    }
}
