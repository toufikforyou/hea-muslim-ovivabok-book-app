package com.toufikhasan.heamoslimovivabok.ui.writer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.snackbar.Snackbar;
import com.toufikhasan.heamoslimovivabok.R;
import com.toufikhasan.heamoslimovivabok.databinding.FragmentWriterBinding;
import com.toufikhasan.heamoslimovivabok.databinding.ItemInfoRowBinding;

public class WriterFragment extends Fragment {

    private static final int[][] DETAILS = {
            {R.string.writer_label_father, R.string.writer_father},
            {R.string.writer_label_mother, R.string.writer_mother},
            {R.string.writer_label_siblings, R.string.writer_siblings},
            {R.string.writer_label_education, R.string.writer_education},
            {R.string.writer_label_permanent_address, R.string.writer_permanent_address},
            {R.string.writer_label_present_address, R.string.writer_present_address},
    };

    private FragmentWriterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWriterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        for (int i = 0; i < DETAILS.length; i++) {
            if (i > 0) {
                binding.detailsContainer.addView(new MaterialDivider(requireContext()));
            }
            addRow(DETAILS[i][0], DETAILS[i][1]);
        }
        binding.contactButton.setOnClickListener(v -> openFacebook());
    }

    private void addRow(@StringRes int label, @StringRes int value) {
        ItemInfoRowBinding row = ItemInfoRowBinding.inflate(getLayoutInflater(), binding.detailsContainer, false);
        row.infoLabel.setText(label);
        row.infoValue.setText(value);
        binding.detailsContainer.addView(row.getRoot());
    }

    private void openFacebook() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.writer_facebook_url)));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(binding.getRoot(), R.string.about_no_app, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
