package com.example.adi.inventorymanagment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.PriorityQueue;
import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NarudzbeAdapter extends RecyclerView.Adapter<NarudzbeAdapter.NarudzbeViewHolder> {
    private List<String> listaNarudzbi;
    private Context mCtx;

    public NarudzbeAdapter(List<String> listaNarudzbi, Context mCtx) {
        this.listaNarudzbi = listaNarudzbi;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public NarudzbeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NarudzbeViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_narudzba, parent ,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NarudzbeViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return listaNarudzbi.size();
    }

    class NarudzbeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mImeNarudzbe;

        public NarudzbeViewHolder(@NonNull View v) {
            super(v);

            mImeNarudzbe = v.findViewById(R.id.imeNarudzbe);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mCtx, "mirko", Toast.LENGTH_SHORT).show();
        }
    }

}
