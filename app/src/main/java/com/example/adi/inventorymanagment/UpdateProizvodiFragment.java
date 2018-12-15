package com.example.adi.inventorymanagment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class UpdateProizvodiFragment extends Fragment implements View.OnClickListener{

    private EditText mId;
    private EditText mImeProizvoda;
    private EditText mOpisProizvoda;
    private EditText mKolicina;
    private EditText mCijena;
    private EditText mDatum;
    private Button mObrisiBtn;
    private Button mAzurirajBtn;
    private Button mDaBtn;
    private Button mNeBtn;

    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update_proizvodi, container, false);

        if (getActivity() != null & ((AppCompatActivity) getActivity()).getSupportActionBar() != null){
            Toolbar toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle("Ažuriranje Proizvoda");
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
        }

        mId = v.findViewById(R.id.id);
        mImeProizvoda = v.findViewById(R.id.imeProizvodaEditText);
        mOpisProizvoda = v.findViewById(R.id.opisProizvodaEditText);
        mKolicina = v.findViewById(R.id.kolicinaEditText);
        mCijena = v.findViewById(R.id.cijenaEditText);
        mDatum = v.findViewById(R.id.datumUpdateEditText);

        if (getActivity() != null){
            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-SemiBold.ttf");

            mAzurirajBtn = v.findViewById(R.id.azurirajBtn);
            mObrisiBtn = v.findViewById(R.id.obrisiBtn);
            mAzurirajBtn.setTypeface(face);
            mObrisiBtn.setTypeface(face);
        }

        mAzurirajBtn.setOnClickListener(this);
        mObrisiBtn.setOnClickListener(this);

        Bundle bundle = this.getArguments();

        if (bundle != null){
            CharSequence id = bundle.getCharSequence("Uid");
            mId.setText(id);

            CharSequence imeProizvoda = bundle.getCharSequence("UimeProizvoda");
            mImeProizvoda.setText(imeProizvoda);

            CharSequence opisProizvoda = bundle.getCharSequence("UopisProizvoda");
            mOpisProizvoda.setText(opisProizvoda);

            int kolicina = bundle.getInt("Ukolicina");
            mKolicina.setText(String.valueOf(kolicina));

            float cijena = bundle.getFloat("Ucijena");
            mCijena.setText(String.valueOf(cijena));

            CharSequence datum = bundle.getCharSequence("Udatum");
            mDatum.setText(datum);
        }

        return v;
    }

    private boolean updateValidateInput(String id,
                                        String imeProizvoda,
                                        String opisProizvoda,
                                        String kolicina,
                                        String cijena,
                                        String datum){

        if (id.isEmpty()){
            mId.setError("Id je obavezno unjeti.");
            mId.requestFocus();
            return true;
        }

        if (TextUtils.isEmpty(imeProizvoda)){
            mImeProizvoda.setText("Nije upisano");
            return true;
        }else{
            mImeProizvoda.setText(imeProizvoda);
        }

        if (TextUtils.isEmpty(opisProizvoda)) {
            mOpisProizvoda.setText("Nije upisano");
            return true;
        }else {
            mOpisProizvoda.setText(opisProizvoda);
        }

        if (TextUtils.isEmpty(kolicina)) {
            mKolicina.setText("0");
            return true;
        }else {
            mKolicina.setText(kolicina);
        }

        if (TextUtils.isEmpty(cijena)) {
            mCijena.setText("0");
            return true;
        }else {
            mCijena.setText(cijena);
        }

        if (TextUtils.isEmpty(datum)){
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY).format(new Date());
            mDatum.setText(date);
            return true;
        }else {
            mDatum.setText(datum);
        }

        return false;
    }

    private void azurirajProizvod(){
        db = FirebaseFirestore.getInstance();

        String id = mId.getText().toString().trim();
        String imeProizvoda = mImeProizvoda.getText().toString().trim();
        String opisProizvoda = mOpisProizvoda.getText().toString().trim();
        String kolicina = mKolicina.getText().toString().trim();
        String cijena = mCijena.getText().toString().trim();
        String datum = mDatum.getText().toString().trim();

        if (!updateValidateInput(id, imeProizvoda, opisProizvoda, kolicina, cijena, datum)){

            Bundle bundle = this.getArguments();
            if (bundle != null){
                String documentId = bundle.getString("UdocumentId");

                if (documentId != null){
                    db.collection("Proizvodi").document(documentId)
                            .update(
                                    "cijena", Float.parseFloat(cijena),
                                    "id", id,
                                    "imeProizvoda", imeProizvoda,
                                    "kolicina", Integer.parseInt(kolicina),
                                    "opisProizvoda", opisProizvoda,
                                    "datum", datum
                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toasty.success(requireContext(),
                                    "Proizvod je ažuriran.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(
                                    requireContext(),
                                    "Greška",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    public void obrisiProizvod() {
        db = FirebaseFirestore.getInstance();

        Bundle bundle = this.getArguments();
        if (bundle != null){
            String documentId = bundle.getString("UdocumentId");

            if (documentId != null){
                FirebaseFirestore.getInstance().collection("Proizvodi")
                        .document(documentId).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(
                                            requireContext(),
                                            "Proizvod je obrisan",
                                            Toast.LENGTH_LONG).show();
                                    otvoriTraziFragment();
                                }else {
                                    Toasty.error(
                                            requireContext(),
                                            "Greška",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private void bottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View parentView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(parentView);
        bottomSheetDialog.show();

        mDaBtn = parentView.findViewById(R.id.daBtn);
        mNeBtn = parentView.findViewById(R.id.neBtn);

        mDaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daBtnAnimacija();
                obrisiProizvod();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        bottomSheetDialog.dismiss();
                    }
                }, 250);
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

    private void azurirajBtnAnimacija() {
        mAzurirajBtn.setScaleX((float) 0.9);
        mAzurirajBtn.setScaleY((float) 0.9);
        mAzurirajBtn.animate().scaleX(1).scaleY(1).start();
    }

    private void obrisiBtnAnimacija() {
        mObrisiBtn.setScaleX((float) 0.9);
        mObrisiBtn.setScaleY((float) 0.9);
        mObrisiBtn.animate().scaleX(1).scaleY(1).start();
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

    private void otvoriTraziFragment(){
        TraziFragment traziFragment = new TraziFragment();

        if (getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.fragmentContainer, traziFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.azurirajBtn:
                azurirajBtnAnimacija();
                azurirajProizvod();
                break;
            case R.id.obrisiBtn:
                obrisiBtnAnimacija();
                bottomSheetDialog();
                break;
        }
    }
}
