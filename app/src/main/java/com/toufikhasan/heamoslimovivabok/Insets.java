package com.toufikhasan.heamoslimovivabok;

import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Helpers for drawing edge-to-edge without content hiding behind the system bars. */
public final class Insets {

    private Insets() {
    }

    /** Keeps the view's original bottom padding and adds the navigation bar height to it. */
    public static void padBottom(View view) {
        final int initialBottom = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), initialBottom + bars.bottom);
            return insets;
        });
    }
}
