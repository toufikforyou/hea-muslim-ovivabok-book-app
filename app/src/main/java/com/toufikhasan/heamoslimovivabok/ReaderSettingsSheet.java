package com.toufikhasan.heamoslimovivabok;

import android.content.Context;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;

/** Bottom sheet where the reader picks text size, reading theme and screen-on behaviour. */
public final class ReaderSettingsSheet {

    public interface Listener {
        void onReaderSettingsChanged();
    }

    private static final int[] THEME_BUTTONS = {R.id.themeAuto, R.id.themePaper, R.id.themeSepia, R.id.themeNight};

    private ReaderSettingsSheet() {
    }

    public static void show(Context context, Listener listener) {
        ReadingPrefs prefs = new ReadingPrefs(context);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = View.inflate(context, R.layout.sheet_reader_settings, null);
        dialog.setContentView(view);

        Slider slider = view.findViewById(R.id.textSizeSlider);
        slider.setValue(Math.max(slider.getValueFrom(), Math.min(slider.getValueTo(), Math.round(prefs.getTextSize()))));
        slider.setLabelFormatter(value -> Bangla.number(Math.round(value)));
        slider.addOnChangeListener((s, value, fromUser) -> {
            if (fromUser) {
                prefs.setTextSize(value);
                listener.onReaderSettingsChanged();
            }
        });
        view.findViewById(R.id.textSmaller).setOnClickListener(v -> step(slider, -1, prefs, listener));
        view.findViewById(R.id.textLarger).setOnClickListener(v -> step(slider, 1, prefs, listener));

        MaterialButtonToggleGroup themes = view.findViewById(R.id.themeGroup);
        themes.check(THEME_BUTTONS[prefs.getReaderTheme()]);
        themes.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            for (int i = 0; i < THEME_BUTTONS.length; i++) {
                if (THEME_BUTTONS[i] == checkedId) {
                    prefs.setReaderTheme(i);
                }
            }
            listener.onReaderSettingsChanged();
        });

        MaterialSwitch keepOn = view.findViewById(R.id.keepScreenOn);
        keepOn.setChecked(prefs.getKeepScreenOn());
        keepOn.setOnCheckedChangeListener((button, checked) -> {
            prefs.setKeepScreenOn(checked);
            listener.onReaderSettingsChanged();
        });

        dialog.show();
    }

    private static void step(Slider slider, int delta, ReadingPrefs prefs, Listener listener) {
        float value = Math.max(slider.getValueFrom(), Math.min(slider.getValueTo(), slider.getValue() + delta));
        slider.setValue(value);
        prefs.setTextSize(value);
        listener.onReaderSettingsChanged();
    }
}
