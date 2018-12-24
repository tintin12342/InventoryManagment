package com.example.adi.inventorymanagment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ChartFragment extends Fragment {

    private PieChart pieChart;
    private Button narudzbeBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chart, container, false);

        pieChart = v.findViewById(R.id.pieChart);

        narudzbeBtn = v.findViewById(R.id.narudzbeBtn);
        narudzbeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NarudzbeFragment nFragment = new NarudzbeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragmentContainer, nFragment).commit();
            }
        });

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 5, 5, 5);
        //Text u centru kruga
        pieChart.setCenterText("Narudžba Zagreb");
        pieChart.setCenterTextSize(30);
        pieChart.setCenterTextColor(R.color.colorPrimaryDark);
        //Scroll efekt
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        //Postavke rupe u chartu
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(55f);
        //Stavke charta
        ArrayList<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(45, "Prodano"));
        value.add(new PieEntry(50, "Naručeno"));
        value.add(new PieEntry(20, "Višak"));
        value.add(new PieEntry(100, "Izrađeno"));
        //Animacija
        pieChart.animateY(1200, Easing.EaseOutSine);
        //Boje charta
        PieDataSet pieSet = new PieDataSet(value, "");
        pieSet.setSliceSpace(2f);
        pieSet.setSelectionShift(5f);
        pieSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //Text stavki
        PieData pieData = new PieData(pieSet);
        pieData.setValueTextSize(14f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);

        return v;
    }
}
