package com.toufikhasan.heamoslimovivabok;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.toufikhasan.heamoslimovivabok.data.Chapters;
import com.toufikhasan.heamoslimovivabok.data.ReadingPrefs;
import com.toufikhasan.heamoslimovivabok.databinding.ActivityChapterViewBinding;
import com.toufikhasan.heamoslimovivabok.databinding.SheetReaderSettingsBinding;
import com.toufikhasan.heamoslimovivabok.util.BanglaDigits;
import com.toufikhasan.heamoslimovivabok.util.ReadingProgress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ChapterViewActivity extends AppCompatActivity {

    private static final String EXTRA_CHAPTER_ID = "cp_id";
    private static final String STATE_CHAPTER_ID = "chapter_id";
    private static final String[] PAGE_CLASSES = {"theme-light", "theme-sepia", "theme-dark"};
    private static final int[] PAGE_BACKGROUNDS = {
            Color.parseColor("#FBF8F1"), Color.parseColor("#F4ECD8"), Color.parseColor("#131411")};

    private ActivityChapterViewBinding binding;
    private ReadingPrefs prefs;
    private int chapterCount;
    private int chapterId;

    public static Intent intent(Context context, int chapterId) {
        return new Intent(context, ChapterViewActivity.class).putExtra(EXTRA_CHAPTER_ID, chapterId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChapterViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = new ReadingPrefs(this);
        chapterCount = Chapters.count(this);
        int requested = savedInstanceState != null
                ? savedInstanceState.getInt(STATE_CHAPTER_ID)
                : getIntent().getIntExtra(EXTRA_CHAPTER_ID, 1);
        if (!Chapters.isValid(requested, chapterCount)) {
            finish();
            return;
        }

        binding.readerToolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.readerToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_reader_settings) {
                showReaderSettings();
                return true;
            }
            return false;
        });
        binding.readerPrevious.setOnClickListener(v -> openChapter(chapterId - 1));
        binding.readerNext.setOnClickListener(v -> {
            if (chapterId < chapterCount) {
                openChapter(chapterId + 1);
            } else {
                prefs.markRead(chapterId);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.root, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(bars.left, 0, bars.right, 0);
            binding.appBar.setPadding(0, bars.top, 0, 0);
            int vertical = getResources().getDimensionPixelSize(R.dimen.reader_bar_padding);
            binding.bottomBar.setPadding(binding.bottomBar.getPaddingLeft(), vertical,
                    binding.bottomBar.getPaddingRight(), vertical + bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        setUpWebView();
        showChapter(requested);
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void setUpWebView() {
        WebView web = binding.webViewChapter;
        // JavaScript only runs our own bundled pages; it is used to switch page colors and restore position.
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(false);
        web.getSettings().setTextZoom(prefs.getTextZoom());
        // Keep the book's text from being long-press selected and copied.
        web.setLongClickable(false);
        web.setHapticFeedbackEnabled(false);
        web.setOnLongClickListener(v -> true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                float fraction = prefs.getScrollFraction(chapterId);
                String js = String.format(Locale.US,
                        "setTimeout(function(){var h=document.documentElement.scrollHeight-window.innerHeight;"
                                + "window.scrollTo(0,%f*Math.max(h,0));},60);", fraction);
                view.evaluateJavascript(js, null);
                view.postDelayed(ChapterViewActivity.this::updateProgress, 200);
            }
        });
        web.setOnScrollChangeListener((v, scrollX, scrollY, oldX, oldY) -> updateProgress());
    }

    private void openChapter(int id) {
        if (!Chapters.isValid(id, chapterCount)) {
            return;
        }
        saveScrollPosition();
        showChapter(id);
    }

    private void showChapter(int id) {
        chapterId = id;
        prefs.setLastChapter(id);
        String title = Chapters.title(this, id);

        binding.readerToolbar.setTitle(title);
        binding.readerToolbar.setSubtitle(getString(R.string.reader_position,
                BanglaDigits.of(id), BanglaDigits.of(chapterCount)));
        binding.readerPrevious.setEnabled(id > 1);
        binding.readerNext.setText(id < chapterCount ? R.string.reader_next : R.string.reader_finish);
        binding.readingProgress.setProgressCompat(0, false);

        int pageTheme = prefs.getPageTheme(isNightUi());
        binding.webViewChapter.setBackgroundColor(PAGE_BACKGROUNDS[pageTheme]);
        binding.webViewChapter.loadDataWithBaseURL("file:///android_asset/",
                buildPage(id, title, pageTheme), "text/html", "UTF-8", null);
    }

    private String buildPage(int id, String title, int pageTheme) {
        String body;
        try {
            body = readAsset(Chapters.htmlAsset(id));
        } catch (IOException e) {
            body = "<p class='error'>" + TextUtils.htmlEncode(getString(R.string.reader_load_error)) + "</p>";
        }
        return "<!DOCTYPE html><html lang='bn'><head><meta charset='utf-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<link rel='stylesheet' href='css/style.css'></head>"
                + "<body class='" + PAGE_CLASSES[pageTheme] + "'><article>"
                + "<img class='cover' src='" + Chapters.coverAsset(id) + "' alt=''>"
                + "<p class='eyebrow'>" + TextUtils.htmlEncode(getString(R.string.chapter_label, BanglaDigits.of(id))) + "</p>"
                + "<h1>" + TextUtils.htmlEncode(title) + "</h1>"
                + body
                + "<div class='end'>❁</div>"
                + "</article></body></html>";
    }

    private String readAsset(String path) throws IOException {
        try (InputStream in = getAssets().open(path); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toString(StandardCharsets.UTF_8.name());
        }
    }

    private int contentHeightPx() {
        return Math.round(binding.webViewChapter.getContentHeight() * getResources().getDisplayMetrics().density);
    }

    private void updateProgress() {
        if (binding == null) {
            return;
        }
        WebView web = binding.webViewChapter;
        int percent = ReadingProgress.percent(web.getScrollY(), contentHeightPx(), web.getHeight());
        binding.readingProgress.setProgressCompat(percent, false);
        if (ReadingProgress.isFinished(percent) && contentHeightPx() > 0) {
            prefs.markRead(chapterId);
        }
    }

    private void saveScrollPosition() {
        if (binding == null || chapterId == 0) {
            return;
        }
        WebView web = binding.webViewChapter;
        float fraction = ReadingProgress.fraction(web.getScrollY(), contentHeightPx(), web.getHeight());
        // Finishing a chapter means the next visit should start from the top again.
        prefs.setScrollFraction(chapterId, fraction >= 0.98f ? 0f : fraction);
    }

    private boolean isNightUi() {
        return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    private void showReaderSettings() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        SheetReaderSettingsBinding sheet = SheetReaderSettingsBinding.inflate(getLayoutInflater());
        dialog.setContentView(sheet.getRoot());

        Runnable showZoom = () -> {
            int zoom = prefs.getTextZoom();
            sheet.textSizeValue.setText(BanglaDigits.of(getString(R.string.reader_text_size_value, String.valueOf(zoom))));
            sheet.textSmaller.setEnabled(zoom > ReadingPrefs.MIN_TEXT_ZOOM);
            sheet.textLarger.setEnabled(zoom < ReadingPrefs.MAX_TEXT_ZOOM);
        };
        sheet.textSmaller.setOnClickListener(v -> {
            applyTextZoom(prefs.getTextZoom() - ReadingPrefs.TEXT_ZOOM_STEP);
            showZoom.run();
        });
        sheet.textLarger.setOnClickListener(v -> {
            applyTextZoom(prefs.getTextZoom() + ReadingPrefs.TEXT_ZOOM_STEP);
            showZoom.run();
        });
        showZoom.run();

        int[] buttons = {R.id.page_light, R.id.page_sepia, R.id.page_dark};
        sheet.pageThemeToggle.check(buttons[prefs.getPageTheme(isNightUi())]);
        sheet.pageThemeToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            for (int theme = 0; theme < buttons.length; theme++) {
                if (buttons[theme] == checkedId) {
                    applyPageTheme(theme);
                }
            }
        });
        dialog.show();
    }

    private void applyTextZoom(int zoom) {
        binding.webViewChapter.getSettings().setTextZoom(prefs.setTextZoom(zoom));
    }

    private void applyPageTheme(int theme) {
        prefs.setPageTheme(theme);
        binding.webViewChapter.setBackgroundColor(PAGE_BACKGROUNDS[theme]);
        binding.webViewChapter.evaluateJavascript("document.body.className='" + PAGE_CLASSES[theme] + "';", null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveScrollPosition();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CHAPTER_ID, chapterId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
