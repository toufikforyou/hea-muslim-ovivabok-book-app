package com.toufikhasan.heamoslimovivabok.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.toufikhasan.heamoslimovivabok.ChapterViewActivity;
import com.toufikhasan.heamoslimovivabok.R;
import com.toufikhasan.heamoslimovivabok.data.Chapters;
import com.toufikhasan.heamoslimovivabok.data.ReadingPrefs;
import com.toufikhasan.heamoslimovivabok.databinding.FragmentHomeBinding;
import com.toufikhasan.heamoslimovivabok.util.BanglaDigits;

public class HomeFragment extends Fragment implements ChapterAdapter.OnChapterClickListener {

    private FragmentHomeBinding binding;
    private ReadingPrefs prefs;
    private ChapterAdapter adapter;
    private String[] titles;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new ReadingPrefs(requireContext());
        titles = Chapters.titles(requireContext());

        adapter = new ChapterAdapter(titles, prefs, this);
        binding.chaptersList.setAdapter(adapter);
        binding.progressBar.setMax(titles.length);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        // Coming back from the reader: refresh progress and the "read" ticks.
        bindProgress();
        adapter.notifyDataSetChanged();
    }

    private void bindProgress() {
        int total = titles.length;
        int read = prefs.readCount();
        int last = prefs.getLastChapter();

        binding.progressBar.setProgressCompat(read, true);
        binding.progressSummary.setText(read >= total
                ? getString(R.string.home_progress_done)
                : getString(R.string.home_progress_summary, BanglaDigits.of(total), BanglaDigits.of(read)));

        boolean started = Chapters.isValid(last, total);
        binding.lastRead.setVisibility(started ? View.VISIBLE : View.GONE);
        if (started) {
            binding.lastRead.setText(getString(R.string.home_last_read, titles[last - 1]));
        }
        binding.continueButton.setText(started ? R.string.home_continue_reading : R.string.home_start_reading);
        binding.continueButton.setOnClickListener(v -> onChapterClick(started ? last : 1));
    }

    @Override
    public void onChapterClick(int chapterId) {
        startActivity(ChapterViewActivity.intent(requireContext(), chapterId));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
