package com.toufikhasan.heamoslimovivabok;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a chapter paragraph by paragraph: an optional header image first,
 * then the text, then a footer to move to the next chapter.
 */
public class ParagraphAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface FooterListener {
        void onOpenChapter(ChapterModel chapter);

        void onBackToContents();
    }

    /** A ready-to-show paragraph. */
    public static final class Paragraph {
        final CharSequence text;
        final boolean centered;

        public Paragraph(CharSequence text, boolean centered) {
            this.text = text;
            this.centered = centered;
        }
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_PARAGRAPH = 1;
    private static final int TYPE_FOOTER = 2;

    private final List<Paragraph> paragraphs = new ArrayList<>();
    private final FooterListener footerListener;
    @Nullable
    private final ChapterModel previous;
    @Nullable
    private final ChapterModel next;

    @Nullable
    private Bitmap header;
    private float textSize;
    private ReaderColors colors;

    public ParagraphAdapter(FooterListener listener, @Nullable ChapterModel previous, @Nullable ChapterModel next,
                            float textSize, ReaderColors colors) {
        this.footerListener = listener;
        this.previous = previous;
        this.next = next;
        this.textSize = textSize;
        this.colors = colors;
    }

    public void setContent(@Nullable Bitmap headerImage, List<Paragraph> items) {
        header = headerImage;
        paragraphs.clear();
        paragraphs.addAll(items);
        notifyDataSetChanged();
    }

    /** Number of rows before the footer (header image + paragraphs). */
    public int getContentCount() {
        return paragraphs.isEmpty() ? 0 : paragraphs.size() + headerCount();
    }

    public void setStyle(float textSize, ReaderColors colors) {
        this.textSize = textSize;
        this.colors = colors;
        notifyItemRangeChanged(0, getItemCount());
    }

    private int headerCount() {
        return header == null ? 0 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < headerCount()) {
            return TYPE_HEADER;
        }
        return position - headerCount() < paragraphs.size() ? TYPE_PARAGRAPH : TYPE_FOOTER;
    }

    @Override
    public int getItemCount() {
        return paragraphs.isEmpty() ? 0 : getContentCount() + 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderHolder(inflater.inflate(R.layout.item_chapter_header, parent, false));
        } else if (viewType == TYPE_FOOTER) {
            return new FooterHolder(inflater.inflate(R.layout.item_reader_footer, parent, false));
        }
        return new ParagraphHolder(inflater.inflate(R.layout.item_paragraph, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ImageView image = ((HeaderHolder) holder).image;
            image.setImageBitmap(header);
            image.setAlpha(colors.isDark ? 0.85f : 1f);
        } else if (holder instanceof ParagraphHolder) {
            Paragraph paragraph = paragraphs.get(position - headerCount());
            TextView textView = ((ParagraphHolder) holder).text;
            textView.setText(paragraph.text);
            textView.setGravity(paragraph.centered ? Gravity.CENTER_HORIZONTAL : Gravity.START);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            textView.setTextColor(colors.text);
        } else {
            bindFooter((FooterHolder) holder);
        }
    }

    private void bindFooter(FooterHolder holder) {
        holder.message.setTextColor(colors.secondaryText);
        if (next != null) {
            holder.next.setVisibility(View.VISIBLE);
            holder.next.setText(holder.itemView.getContext().getString(R.string.next_chapter, next.getTitle()));
            holder.next.setOnClickListener(v -> footerListener.onOpenChapter(next));
        } else {
            holder.next.setVisibility(View.GONE);
        }
        if (previous != null) {
            holder.previous.setVisibility(View.VISIBLE);
            holder.previous.setOnClickListener(v -> footerListener.onOpenChapter(previous));
        } else {
            holder.previous.setVisibility(View.GONE);
        }
        holder.contents.setOnClickListener(v -> footerListener.onBackToContents());
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        final ImageView image;

        HeaderHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.headerImage);
        }
    }

    static class ParagraphHolder extends RecyclerView.ViewHolder {
        final TextView text;

        ParagraphHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView;
            // Long press copies a paragraph, so a favourite line can be shared easily.
            text.setOnLongClickListener(v -> {
                Context context = v.getContext();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText(context.getString(R.string.app_name), text.getText().toString()));
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    Toast.makeText(context, R.string.paragraph_copied, Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }
    }

    static class FooterHolder extends RecyclerView.ViewHolder {
        final TextView message;
        final MaterialButton next;
        final MaterialButton previous;
        final MaterialButton contents;

        FooterHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.footerMessage);
            next = itemView.findViewById(R.id.nextButton);
            previous = itemView.findViewById(R.id.previousButton);
            contents = itemView.findViewById(R.id.contentsButton);
        }
    }
}
