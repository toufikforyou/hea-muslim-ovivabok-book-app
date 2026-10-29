package com.toufikhasan.heamoslimovivabok.ui.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.toufikhasan.heamoslimovivabok.BuildConfig;
import com.toufikhasan.heamoslimovivabok.R;
import com.toufikhasan.heamoslimovivabok.data.ReadingPrefs;
import com.toufikhasan.heamoslimovivabok.databinding.FragmentAboutBinding;
import com.toufikhasan.heamoslimovivabok.util.BanglaDigits;

public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.aboutVersion.setText(getString(R.string.about_version, BanglaDigits.of(BuildConfig.VERSION_NAME)));

        ReadingPrefs prefs = new ReadingPrefs(requireContext());
        binding.themeToggle.check(buttonFor(prefs.getNightMode()));
        // Added after the initial check() so restoring the saved choice does not re-trigger it.
        binding.themeToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            int mode = modeFor(checkedId);
            if (mode != prefs.getNightMode()) {
                prefs.setNightMode(mode);
                AppCompatDelegate.setDefaultNightMode(mode);
            }
        });

        binding.shareButton.setOnClickListener(v -> shareApp());
        binding.rateButton.setOnClickListener(v -> rateApp());
    }

    private static int buttonFor(int nightMode) {
        if (nightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            return R.id.theme_light;
        } else if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            return R.id.theme_dark;
        }
        return R.id.theme_system;
    }

    private static int modeFor(int buttonId) {
        if (buttonId == R.id.theme_light) {
            return AppCompatDelegate.MODE_NIGHT_NO;
        } else if (buttonId == R.id.theme_dark) {
            return AppCompatDelegate.MODE_NIGHT_YES;
        }
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }

    private String storeUrl() {
        return "https://play.google.com/store/apps/details?id=" + requireContext().getPackageName();
    }

    private void shareApp() {
        Intent send = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.about_share_text, storeUrl()));
        startActivity(Intent.createChooser(send, getString(R.string.about_share)));
    }

    private void rateApp() {
        Uri market = Uri.parse("market://details?id=" + requireContext().getPackageName());
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, market));
        } catch (ActivityNotFoundException noStore) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl())));
            } catch (ActivityNotFoundException noBrowser) {
                Snackbar.make(binding.getRoot(), R.string.about_no_app, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
