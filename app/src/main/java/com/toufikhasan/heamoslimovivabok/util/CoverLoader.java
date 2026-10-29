package com.toufikhasan.heamoslimovivabok.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import com.toufikhasan.heamoslimovivabok.R;
import com.toufikhasan.heamoslimovivabok.data.Chapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Loads small, downsampled chapter cover thumbnails off the main thread.
 */
public final class CoverLoader {

    private CoverLoader() {
    }

    /** Created on first use, so the pure helpers below stay usable in plain JVM tests. */
    private static final class Holder {
        static final LruCache<Integer, Bitmap> CACHE = new LruCache<Integer, Bitmap>(8 * 1024 * 1024) {
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getByteCount();
            }
        };
        static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
        static final Handler MAIN = new Handler(Looper.getMainLooper());
    }

    public static void load(ImageView view, int chapterId, int targetPx) {
        view.setTag(R.id.cover_chapter_tag, chapterId);
        Bitmap cached = Holder.CACHE.get(chapterId);
        if (cached != null) {
            view.setImageBitmap(cached);
            return;
        }
        view.setImageDrawable(null);
        Context appContext = view.getContext().getApplicationContext();
        Holder.EXECUTOR.execute(() -> {
            Bitmap bitmap = decode(appContext, chapterId, targetPx);
            if (bitmap == null) {
                return;
            }
            Holder.CACHE.put(chapterId, bitmap);
            Holder.MAIN.post(() -> {
                // The view may have been recycled for another chapter in the meantime.
                Object tag = view.getTag(R.id.cover_chapter_tag);
                if (tag instanceof Integer && (Integer) tag == chapterId) {
                    view.setImageBitmap(bitmap);
                }
            });
        });
    }

    private static Bitmap decode(Context context, int chapterId, int targetPx) {
        String path = Chapters.coverAsset(chapterId);
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream in = context.getAssets().open(path)) {
            BitmapFactory.decodeStream(in, null, bounds);
        } catch (IOException e) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize(bounds.outWidth, bounds.outHeight, targetPx);
        try (InputStream in = context.getAssets().open(path)) {
            return BitmapFactory.decodeStream(in, null, options);
        } catch (IOException e) {
            return null;
        }
    }

    static int sampleSize(int width, int height, int targetPx) {
        int sample = 1;
        int shortest = Math.min(width, height);
        while (targetPx > 0 && shortest / (sample * 2) >= targetPx) {
            sample *= 2;
        }
        return sample;
    }
}
