package com.toufikhasan.heamoslimovivabok;

public class ChapterModel {
    private final String title;
    private final String fileName;

    public ChapterModel(String title, String fileName) {
        this.title = title;
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    /** Chapter number as used by assets/chapter/&lt;n&gt;.html and assets/images/&lt;n&gt;.jpg */
    public String getFileName() {
        return fileName;
    }
}
