package com.example.adi.inventorymanagment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ListaFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProizvodiAdapter proizvodiAdapter;
    private List<Proizvodi> proizvodiList;
    private MaterialSearchView searchView;
    private SwipeRefreshLayout refreshLayout;
    private Button mDaBtn;
    private Button mNeBtn;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lista, container, false);
        setHasOptionsMenu(true);

        Toolbar toolbar = v.findViewById(R.id.toolbarProizvodi);
        toolbar.setTitle("Lista Proizvoda");
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        AppBarLayout appBarLayout = v.findViewById(R.id.appBarLayout);
        disableImageScroll(appBarLayout);

        searchView = v.findViewById(R.id.searchView);
        refreshLayout = v.findViewById(R.id.swipeRefresh);
        recyclerView = v.findViewById(R.id.recycleViewProizvodi);

        prikazListe();
        swipeRefresh();

        return v;
    }

    private void disableImageScroll(AppBarLayout appBarLayout) {
        CoordinatorLayout.LayoutParams paramss = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        paramss.setBehavior(new AppBarLayout.Behavior());

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
    }

    private void prikazListe() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        proizvodiList = new ArrayList<>();
        proizvodiAdapter = new ProizvodiAdapter(getContext(), proizvodiList);

        recyclerView.setAdapter(proizvodiAdapter);

        db = FirebaseFirestore.getInstance();

        db.collection("Proizvodi")
                .orderBy("imeProizvoda", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot documentSnapshot : list) {
                                Proizvodi proizvodi = documentSnapshot.toObject(Proizvodi.class);
                                if (proizvodi != null) {
                                    proizvodi.setDocumentId(documentSnapshot.getId());
                                    proizvodiList.add(proizvodi);
                                }
                            }
                            proizvodiAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void swipeRefresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                proizvodiList = new ArrayList<>();
                proizvodiAdapter = new ProizvodiAdapter(getContext(), proizvodiList);

                recyclerView.setAdapter(proizvodiAdapter);

                db = FirebaseFirestore.getInstance();

                db.collection("Proizvodi")
                        .orderBy("imeProizvoda", Query.Direction.ASCENDING)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot documentSnapshot : list) {
                                        Proizvodi proizvodi = documentSnapshot.toObject(Proizvodi.class);
                                        if (proizvodi != null) {
                                            proizvodi.setDocumentId(documentSnapshot.getId());
                                            proizvodiList.add(proizvodi);
                                        }
                                    }
                                    proizvodiAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void bottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(v);
        bottomSheetDialog.show();

        if (getActivity() != null) {
            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-SemiBold.ttf");

            mDaBtn = v.findViewById(R.id.daBtn);
            mNeBtn = v.findViewById(R.id.neBtn);
            mDaBtn.setTypeface(face);
            mNeBtn.setTypeface(face);
        }

        String potvrda = "Jeste li sigurni?";
        TextView mTV = v.findViewById(R.id.TV);
        mTV.setText(potvrda);

        mDaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daBtnAnimacija();

                if (getActivity() != null) {
                    FirebaseAuth.getInstance().signOut();
                    getActivity().finish();
                    Intent intent = new Intent(getContext(), LogInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        mNeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neBtnAnimacija();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        bottomSheetDialog.dismiss();
                    }
                }, 250);
            }
        });
    }

    private void daBtnAnimacija() {
        mDaBtn.setScaleX((float) 0.9);
        mDaBtn.setScaleY((float) 0.9);
        mDaBtn.animate().scaleX(1).scaleY(1).start();
    }

    private void neBtnAnimacija() {
        mNeBtn.setScaleX((float) 0.9);
        mNeBtn.setScaleY((float) 0.9);
        mNeBtn.animate().scaleX(1).scaleY(1).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.search_id);

        searchView.setMenuItem(item);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        bottomSheetDialog();
        return true;
    }

    private void filter(String text) {
        ArrayList<Proizvodi> filtriranaLista = new ArrayList<>();

        for (Proizvodi proizvod : proizvodiList) {
            if (proizvod.getImeProizvoda().toLowerCase().contains(text.toLowerCase())) {
                filtriranaLista.add(proizvod);
            }
        }
        proizvodiAdapter.filtriranaLista(filtriranaLista);
    }

}
