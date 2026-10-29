package com.toufikhasan.heamoslimovivabok;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ChapterClick {

    private static final int[] NIGHT_MODES = {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES
    };

    private HomeAdapter homeAdapter;
    private ReadingPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        prefs = new ReadingPrefs(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        homeAdapter = new HomeAdapter(this, this);
        recyclerView.setAdapter(homeAdapter);
        Insets.padBottom(recyclerView);

        if (savedInstanceState == null) {
            greetOncePerDay();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        homeAdapter.refreshProgress();
    }

    @Override
    public void onClick(String file, String title) {
        startActivity(ChapterViewActivity.intent(this, file, title));
    }

    private void greetOncePerDay() {
        Calendar now = Calendar.getInstance();
        long day = now.get(Calendar.YEAR) * 1000L + now.get(Calendar.DAY_OF_YEAR);
        if (!prefs.shouldGreetToday(day)) {
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setView(R.layout.dialog_salam)
                .setPositiveButton(R.string.salam_reply, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_theme) {
            showThemeDialog();
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showThemeDialog() {
        int current = 0;
        for (int i = 0; i < NIGHT_MODES.length; i++) {
            if (NIGHT_MODES[i] == prefs.getNightMode()) {
                current = i;
            }
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.theme_title)
                .setSingleChoiceItems(R.array.theme_options, current, (dialog, which) -> {
                    prefs.setNightMode(NIGHT_MODES[which]);
                    dialog.dismiss();
                    AppCompatDelegate.setDefaultNightMode(NIGHT_MODES[which]);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.app_name)
                .setMessage(getString(R.string.about_app_message, BuildConfig.VERSION_NAME))
                .setPositiveButton(R.string.ok, null)
                .setNeutralButton(R.string.share_app, (dialog, which) -> shareApp())
                .show();
    }

    private void shareApp() {
        Intent share = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text,
                        "https://play.google.com/store/apps/details?id=" + getPackageName()));
        startActivity(Intent.createChooser(share, getString(R.string.share_app)));
    }
}
