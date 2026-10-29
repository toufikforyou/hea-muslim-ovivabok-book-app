package com.toufikhasan.heamoslimovivabok;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChapterViewActivity extends AppCompatActivity implements ParagraphAdapter.FooterListener, ReaderSettingsSheet.Listener {

    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_FILE = "file";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private ReadingPrefs prefs;
    private String file;
    private boolean isChapter;

    private View root;
    private AppBarLayout appBar;
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LinearProgressIndicator readingProgress;
    private CircularProgressIndicator loading;
    private ParagraphAdapter adapter;

    public static Intent intent(Context context, String file, String title) {
        return new Intent(context, ChapterViewActivity.class)
                .putExtra(EXTRA_FILE, file)
                .putExtra(EXTRA_TITLE, title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_chapter);

        prefs = new ReadingPrefs(this);
        file = getIntent().getStringExtra(EXTRA_FILE);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (file == null) {
            finish();
            return;
        }

        int index = Book.indexOf(file);
        isChapter = index >= 0;
        ChapterModel previous = index > 0 ? Book.CHAPTERS.get(index - 1) : null;
        ChapterModel next = isChapter && index < Book.CHAPTERS.size() - 1 ? Book.CHAPTERS.get(index + 1) : null;

        root = findViewById(R.id.readerRoot);
        appBar = findViewById(R.id.appBar);
        toolbar = findViewById(R.id.toolbar);
        readingProgress = findViewById(R.id.readingProgress);
        loading = findViewById(R.id.loading);
        recyclerView = findViewById(R.id.recyclerView);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
            if (isChapter) {
                getSupportActionBar().setSubtitle(getString(R.string.chapter_of,
                        Bangla.number(index + 1), Bangla.number(Book.CHAPTERS.size())));
            }
        }

        adapter = new ParagraphAdapter(this, previous, next, prefs.getTextSize(),
                ReaderColors.resolve(this, prefs.getReaderTheme()));
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                readingProgress.setProgressCompat(currentProgress(), false);
            }
        });
        Insets.padBottom(recyclerView);

        applyReaderStyle();
        loadChapter();
    }

    private void loadChapter() {
        loading.show();
        executor.execute(() -> {
            Bitmap header = null;
            List<ParagraphAdapter.Paragraph> paragraphs = new ArrayList<>();
            if (Book.ABOUT_WRITER.getFileName().equals(file)) {
                for (String line : getString(R.string.app_writer_info).split("\\n")) {
                    if (!line.trim().isEmpty()) {
                        paragraphs.add(new ParagraphAdapter.Paragraph(line.trim(), false));
                    }
                }
            } else {
                try {
                    header = readHeaderImage();
                    for (ChapterParser.Block block : ChapterParser.parse(readAsset("chapter/" + file + ".html"))) {
                        CharSequence text = trimTrailing(HtmlCompat.fromHtml(block.html, HtmlCompat.FROM_HTML_MODE_COMPACT));
                        paragraphs.add(new ParagraphAdapter.Paragraph(text, block.centered));
                    }
                } catch (IOException e) {
                    paragraphs.clear();
                }
                if (paragraphs.isEmpty()) {
                    paragraphs.add(new ParagraphAdapter.Paragraph(getString(R.string.chapter_load_error), true));
                }
            }
            final Bitmap headerImage = header;
            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                loading.hide();
                adapter.setContent(headerImage, paragraphs);
                layoutManager.scrollToPositionWithOffset(
                        Math.min(prefs.getPosition(file), Math.max(0, adapter.getContentCount() - 1)),
                        prefs.getOffset(file));
                recyclerView.post(() -> readingProgress.setProgressCompat(currentProgress(), false));
            });
        });
    }

    private String readAsset(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    @Nullable
    private Bitmap readHeaderImage() {
        try (InputStream stream = getAssets().open("images/" + file + ".jpg")) {
            return BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            return null;
        }
    }

    private static CharSequence trimTrailing(CharSequence text) {
        int end = text.length();
        while (end > 0 && Character.isWhitespace(text.charAt(end - 1))) {
            end--;
        }
        return text.subSequence(0, end);
    }

    /** Percentage of the chapter that has been scrolled into view. */
    private int currentProgress() {
        int total = adapter.getContentCount();
        if (total == 0) {
            return 0;
        }
        if (!recyclerView.canScrollVertically(1)) {
            return 100;
        }
        int last = layoutManager.findLastVisibleItemPosition();
        return Math.max(0, Math.min(100, Math.round((last + 1) * 100f / total)));
    }

    private void saveReadingPosition() {
        if (adapter == null || adapter.getContentCount() == 0) {
            return;
        }
        int position = layoutManager.findFirstVisibleItemPosition();
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        View first = layoutManager.findViewByPosition(position);
        int offset = first == null ? 0 : first.getTop() - recyclerView.getPaddingTop();
        int progress = Math.max(currentProgress(), prefs.getProgress(file) >= 98 ? 100 : 0);
        prefs.saveReading(file, position, offset, progress, isChapter);
    }

    private void applyReaderStyle() {
        ReaderColors colors = ReaderColors.resolve(this, prefs.getReaderTheme());
        root.setBackgroundColor(colors.background);
        appBar.setBackgroundColor(colors.background);
        toolbar.setBackgroundColor(colors.background);
        toolbar.setTitleTextColor(colors.text);
        toolbar.setSubtitleTextColor(colors.secondaryText);
        ColorStateList tint = ColorStateList.valueOf(colors.text);
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTintList(tint);
        }
        if (toolbar.getOverflowIcon() != null) {
            toolbar.getOverflowIcon().setTintList(tint);
        }
        Menu menu = toolbar.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getIcon() != null) {
                menu.getItem(i).getIcon().setTintList(tint);
            }
        }
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(!colors.isDark);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightNavigationBars(!colors.isDark);

        if (prefs.getKeepScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        adapter.setStyle(prefs.getTextSize(), colors);
    }

    @Override
    public void onReaderSettingsChanged() {
        applyReaderStyle();
    }

    @Override
    public void onOpenChapter(ChapterModel chapter) {
        saveReadingPosition();
        startActivity(intent(this, chapter.getFileName(), chapter.getTitle()));
        finish();
    }

    @Override
    public void onBackToContents() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveReadingPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        applyReaderStyle();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_text_settings) {
            ReaderSettingsSheet.show(this, this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
