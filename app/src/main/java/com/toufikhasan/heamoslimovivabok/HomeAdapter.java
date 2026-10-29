package com.toufikhasan.heamoslimovivabok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

/** Home screen: book hero card, table of contents and the "about the writer" pages. */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HERO = 0;
    private static final int TYPE_SECTION = 1;
    private static final int TYPE_CHAPTER = 2;
    private static final int TYPE_ABOUT = 3;

    private static final class Row {
        final int type;
        final String text;
        final ChapterModel chapter;
        final int number;

        Row(int type, String text, ChapterModel chapter, int number) {
            this.type = type;
            this.text = text;
            this.chapter = chapter;
            this.number = number;
        }
    }

    private final List<Row> rows = new ArrayList<>();
    private final ReadingPrefs prefs;
    private final ChapterClick chapterClick;

    public HomeAdapter(Context context, ChapterClick click) {
        this.prefs = new ReadingPrefs(context);
        this.chapterClick = click;

        rows.add(new Row(TYPE_HERO, null, null, 0));
        rows.add(new Row(TYPE_SECTION, context.getString(R.string.section_contents,
                Bangla.number(Book.CHAPTERS.size())), null, 0));
        for (int i = 0; i < Book.CHAPTERS.size(); i++) {
            rows.add(new Row(TYPE_CHAPTER, null, Book.CHAPTERS.get(i), i + 1));
        }
        rows.add(new Row(TYPE_SECTION, context.getString(R.string.section_writer), null, 0));
        rows.add(new Row(TYPE_ABOUT, context.getString(R.string.about_writer_subtitle), Book.ABOUT_WRITER, R.drawable.ic_person));
    }

    /** Re-binds rows that show reading progress (call from onResume). */
    public void refreshProgress() {
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).type;
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HERO:
                return new HeroHolder(inflater.inflate(R.layout.item_home_hero, parent, false));
            case TYPE_SECTION:
                return new SectionHolder(inflater.inflate(R.layout.item_home_section, parent, false));
            case TYPE_ABOUT:
                return new AboutHolder(inflater.inflate(R.layout.item_home_about, parent, false));
            default:
                return new ChapterHolder(inflater.inflate(R.layout.item_home_chapter, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Row row = rows.get(position);
        if (holder instanceof HeroHolder) {
            bindHero((HeroHolder) holder);
        } else if (holder instanceof SectionHolder) {
            ((SectionHolder) holder).title.setText(row.text);
        } else if (holder instanceof AboutHolder) {
            AboutHolder about = (AboutHolder) holder;
            about.title.setText(row.chapter.getTitle());
            about.subtitle.setText(row.text);
            about.icon.setImageResource(row.number);
            about.itemView.setOnClickListener(v -> open(row.chapter));
        } else if (holder instanceof ChapterHolder) {
            bindChapter((ChapterHolder) holder, row);
        }
    }

    private void bindHero(HeroHolder holder) {
        Context context = holder.itemView.getContext();
        ChapterModel last = Book.find(prefs.getLastFile());
        if (last == null) {
            holder.continueGroup.setVisibility(View.GONE);
            holder.button.setText(R.string.start_reading);
            holder.button.setOnClickListener(v -> open(Book.CHAPTERS.get(0)));
        } else {
            int progress = prefs.getProgress(last.getFileName());
            holder.continueGroup.setVisibility(View.VISIBLE);
            holder.continueTitle.setText(last.getTitle());
            holder.continueProgress.setProgressCompat(progress, false);
            holder.continuePercent.setText(context.getString(R.string.percent_read, Bangla.number(progress)));
            holder.button.setText(R.string.continue_reading);
            holder.button.setOnClickListener(v -> open(last));
        }
    }

    private void bindChapter(ChapterHolder holder, Row row) {
        Context context = holder.itemView.getContext();
        int progress = prefs.getProgress(row.chapter.getFileName());

        holder.number.setText(Bangla.number(row.number));
        holder.title.setText(row.chapter.getTitle());

        if (progress >= 98) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(R.string.chapter_done);
            holder.progress.setVisibility(View.GONE);
            holder.done.setVisibility(View.VISIBLE);
        } else if (progress > 0) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(context.getString(R.string.percent_read, Bangla.number(progress)));
            holder.progress.setVisibility(View.VISIBLE);
            holder.progress.setProgressCompat(progress, false);
            holder.done.setVisibility(View.GONE);
        } else {
            holder.status.setVisibility(View.GONE);
            holder.progress.setVisibility(View.GONE);
            holder.done.setVisibility(View.GONE);
        }
        holder.itemView.setContentDescription(context.getString(R.string.chapter_a11y,
                Bangla.number(row.number), row.chapter.getTitle()));
        holder.itemView.setOnClickListener(v -> open(row.chapter));
    }

    private void open(ChapterModel chapter) {
        if (chapterClick != null) {
            chapterClick.onClick(chapter.getFileName(), chapter.getTitle());
        }
    }

    static class HeroHolder extends RecyclerView.ViewHolder {
        final View continueGroup;
        final TextView continueTitle;
        final TextView continuePercent;
        final LinearProgressIndicator continueProgress;
        final MaterialButton button;

        HeroHolder(@NonNull View itemView) {
            super(itemView);
            continueGroup = itemView.findViewById(R.id.continueGroup);
            continueTitle = itemView.findViewById(R.id.continueTitle);
            continuePercent = itemView.findViewById(R.id.continuePercent);
            continueProgress = itemView.findViewById(R.id.continueProgress);
            button = itemView.findViewById(R.id.readButton);
        }
    }

    static class SectionHolder extends RecyclerView.ViewHolder {
        final TextView title;

        SectionHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.sectionTitle);
        }
    }

    static class ChapterHolder extends RecyclerView.ViewHolder {
        final TextView number;
        final TextView title;
        final TextView status;
        final LinearProgressIndicator progress;
        final ImageView done;

        ChapterHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.chapterNumber);
            title = itemView.findViewById(R.id.chapterTitle);
            status = itemView.findViewById(R.id.chapterStatus);
            progress = itemView.findViewById(R.id.chapterProgress);
            done = itemView.findViewById(R.id.chapterDone);
        }
    }

    static class AboutHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title;
        final TextView subtitle;

        AboutHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.aboutIcon);
            title = itemView.findViewById(R.id.aboutTitle);
            subtitle = itemView.findViewById(R.id.aboutSubtitle);
        }
    }
}
