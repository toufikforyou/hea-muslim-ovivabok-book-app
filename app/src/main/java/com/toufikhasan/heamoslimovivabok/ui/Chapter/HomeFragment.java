package com.toufikhasan.heamoslimovivabok.ui.Chapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toufikhasan.heamoslimovivabok.ChapterViewActivity;
import com.toufikhasan.heamoslimovivabok.R;
import com.toufikhasan.heamoslimovivabok.databinding.FragmentHomeBinding;
import com.toufikhasan.heamoslimovivabok.ui.adapter.CpLAdapter;
import com.toufikhasan.heamoslimovivabok.ui.adapter.onCpClickInterface;

public class HomeFragment extends Fragment implements onCpClickInterface {
    private RecyclerView chapterList;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        chapterList = binding.chaptersList;

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] myCpLists = getResources().getStringArray(R.array.chapter_lists_array);

        chapterList.setLayoutManager(new LinearLayoutManager(requireContext()));
        CpLAdapter cpLAdapter = new CpLAdapter(myCpLists, this);
        chapterList.setAdapter(cpLAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onChapterClick(int id, String chapter) {
        Intent intent = new Intent(requireActivity(), ChapterViewActivity.class);
        intent.putExtra("cp_title",chapter);
        intent.putExtra("cp_id",id);
        startActivity(intent);
    }
}