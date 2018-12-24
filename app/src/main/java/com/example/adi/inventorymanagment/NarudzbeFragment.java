package com.example.adi.inventorymanagment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NarudzbeFragment extends Fragment {

    private String[] slova = {
            "2","A","R","H","U","Z","5","7"
    };

    private NarudzbeAdapter narudzbeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_narudzbe, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recycleViewNarudzbe);
        int brojStupaca = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), brojStupaca));
        narudzbeAdapter = new NarudzbeAdapter(Arrays.asList(slova), requireContext());
        recyclerView.setAdapter(narudzbeAdapter);

        return v;
    }

}
