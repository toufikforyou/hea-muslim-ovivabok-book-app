package com.toufikhasan.heamoslimovivabok.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.toufikhasan.heamoslimovivabok.R;
import com.toufikhasan.heamoslimovivabok.data.ReadingPrefs;
import com.toufikhasan.heamoslimovivabok.databinding.ItemChapterBinding;
import com.toufikhasan.heamoslimovivabok.util.BanglaDigits;
import com.toufikhasan.heamoslimovivabok.util.CoverLoader;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {

    public interface OnChapterClickListener {
        void onChapterClick(int chapterId);
    }

    private final String[] titles;
    private final ReadingPrefs prefs;
    private final OnChapterClickListener listener;

    public ChapterAdapter(String[] titles, ReadingPrefs prefs, OnChapterClickListener listener) {
        this.titles = titles;
        this.prefs = prefs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChapterBinding binding = ItemChapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int chapterId = position + 1;
        Context context = holder.itemView.getContext();
        ItemChapterBinding b = holder.binding;

        String number = context.getString(R.string.chapter_label, BanglaDigits.of(chapterId));
        b.chapterNumber.setText(number);
        b.chapterTitle.setText(titles[position]);

        boolean read = prefs.isRead(chapterId);
        b.chapterStatus.setImageDrawable(ContextCompat.getDrawable(context,
                read ? R.drawable.ic_check_circle : R.drawable.ic_chevron_right));
        b.chapterStatus.setColorFilter(MaterialColors.getColor(b.chapterStatus,
                read ? androidx.appcompat.R.attr.colorPrimary : com.google.android.material.R.attr.colorOnSurfaceVariant));

        // Read the whole card as one sentence, e.g. "অধ্যায় ৩, অভিভাবকদের ব্যয়, পড়া হয়েছে".
        String description = number + ", " + titles[position];
        if (read) {
            description += ", " + context.getString(R.string.chapter_read);
        }
        holder.itemView.setContentDescription(description);

        CoverLoader.load(b.chapterCover, chapterId,
                context.getResources().getDimensionPixelSize(R.dimen.chapter_thumb_size));
        holder.itemView.setOnClickListener(v -> listener.onChapterClick(chapterId));
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemChapterBinding binding;

        ViewHolder(ItemChapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
