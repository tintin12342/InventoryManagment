package com.example.adi.inventorymanagment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import es.dmoral.toasty.Toasty;

public class DodajFragment extends Fragment implements View.OnClickListener {

    private EditText mIdEditText;
    private EditText mImeProizvodaEditText;
    private EditText mOpisEditText;
    private EditText mKolicinaEditText;
    private EditText mCijenaEditText;
    private EditText mDatumEditText;
    private Button mAzurirajBtn;
    private Button mDaBtn;
    private Button mNeBtn;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dodaj, container, false);
        setHasOptionsMenu(true);

        if (getActivity() != null){
            Toolbar toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle("Unos Podataka");
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        mIdEditText = v.findViewById(R.id.regLozinkaEditText);
        mImeProizvodaEditText = v.findViewById(R.id.imeProizvodaEditText);
        mOpisEditText = v.findViewById(R.id.opisEditText);
        mKolicinaEditText = v.findViewById(R.id.kolicinaEditText);
        mCijenaEditText = v.findViewById(R.id.cijenaEditText);
        mDatumEditText = v.findViewById(R.id.datumEditText);

        if (getActivity() != null){
            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-SemiBold.ttf");

            mAzurirajBtn = v.findViewById(R.id.azurirajBtn);
            mAzurirajBtn.setTypeface(face);
        }

        mAzurirajBtn.setOnClickListener(this);

        return v;
    }

    private boolean validateInput(String id,
                                  String imeProizvoda,
                                  String opisProizvoda,
                                  String kolicina,
                                  String cijena,
                                  String datum){

        if (id.isEmpty()){
            mIdEditText.setError("Id je obavezno unjeti.");
            mIdEditText.requestFocus();
            return true;
        }

        if (TextUtils.isEmpty(imeProizvoda)){
            mImeProizvodaEditText.setText("-");
            return true;
        }else{
            mImeProizvodaEditText.setText(imeProizvoda);
        }

        if (TextUtils.isEmpty(opisProizvoda)) {
            mOpisEditText.setText("-");
            return true;
        }else {
            mOpisEditText.setText(opisProizvoda);
        }

        if (TextUtils.isEmpty(kolicina)) {
            mKolicinaEditText.setText("0");
            return true;
        }else {
            mKolicinaEditText.setText(kolicina);
        }

        if (TextUtils.isEmpty(cijena)) {
            mCijenaEditText.setText("0");
            return true;
        }else {
            mCijenaEditText.setText(cijena);
        }

        if (TextUtils.isEmpty(datum)){
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY).format(new Date());
            mDatumEditText.setText(date);
            return true;
        }else {
            mDatumEditText.setText(datum);
        }

        return false;
    }

    private void spremiProizvod(){
        final String id = mIdEditText.getText().toString().trim();
        final String imeProizvoda = mImeProizvodaEditText.getText().toString().trim();
        final String opisProizvoda = mOpisEditText.getText().toString().trim();
        final String kolicina = mKolicinaEditText.getText().toString().trim();
        final String cijena = mCijenaEditText.getText().toString().trim();
        final String datum = mDatumEditText.getText().toString().trim();

        db = FirebaseFirestore.getInstance();

        if (!validateInput(id, imeProizvoda, opisProizvoda, kolicina, cijena, datum)){
            final CollectionReference dbProizvodi = db.collection("Proizvodi");
            dbProizvodi.whereEqualTo("id", id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                Toasty.warning(
                                        requireContext(),
                                        "Proizvod vec postoji",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                CollectionReference dbProizvodi = db.collection("Proizvodi");

                                Proizvodi proizvodi = new Proizvodi(
                                        imeProizvoda,
                                        opisProizvoda,
                                        id,
                                        Integer.parseInt(kolicina),
                                        Float.parseFloat(cijena),
                                        datum,
                                        null);

                                dbProizvodi.add(proizvodi)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                if (!id.isEmpty()
                                                        && !imeProizvoda.isEmpty()
                                                        && !opisProizvoda.isEmpty()
                                                        && !kolicina.isEmpty()
                                                        && !cijena.isEmpty()
                                                        && !datum.isEmpty()){

                                                    mIdEditText.setText("");
                                                    mImeProizvodaEditText.setText("");
                                                    mOpisEditText.setText("");
                                                    mKolicinaEditText.setText("");
                                                    mCijenaEditText.setText("");
                                                    mDatumEditText.setText("");
                                                }

                                                Toasty.success(
                                                        requireContext(),
                                                        "Proizvod je spremljen",
                                                        Toast.LENGTH_SHORT).show();



                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.error(
                                                requireContext(),
                                                "Greška",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });

        }

    }

    public void sakriKeyboard(){
        if (getActivity() != null){
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getActivity().getCurrentFocus() != null && imm != null){
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void bottomSheetDialog(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(v);
        bottomSheetDialog.show();

        if (getActivity() != null){
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
                if (getActivity() != null){
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        bottomSheetDialog();
        return true;
    }

    public void azurirajBtnAnimacija() {
        mAzurirajBtn.setScaleX((float) 0.9);
        mAzurirajBtn.setScaleY((float) 0.9);
        mAzurirajBtn.animate().scaleX(1).scaleY(1).start();
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
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.azurirajBtn:
                azurirajBtnAnimacija();
                spremiProizvod();
                sakriKeyboard();
                break;
        }
    }
}
