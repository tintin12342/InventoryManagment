package com.example.adi.inventorymanagment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import es.dmoral.toasty.Toasty;

public class TraziFragment extends Fragment implements View.OnClickListener {

    private Button mTraziBtn;
    private Button mScanBtn;
    private Button mDodajBtn;
    private Button mAzurirajBtn;
    private EditText mTraziEditText;
    private CollectionReference cRef;

    private ProgressBar mProgressBar;
    private EditText mIme;
    private EditText mOpis;
    private EditText mKolicina;
    private EditText mCijena;
    private EditText mDatum;
    private Button mSpremiBtn;
    private Button mOdustaniBtn;
    private Button mDaBtn;
    private Button mNeBtn;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trazi, container, false);
        if (getActivity() != null){
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
        setHasOptionsMenu(true);

        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle("Pretraživanje Proizvoda");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-SemiBold.ttf");

        mProgressBar = v.findViewById(R.id.traziProgressBar);
        mTraziBtn = v.findViewById(R.id.traziBtn);
        mScanBtn = v.findViewById(R.id.skenirajBtn);
        mDodajBtn = v.findViewById(R.id.dodajBtn);
        mAzurirajBtn = v.findViewById(R.id.azurirajBtn);
        mTraziEditText = v.findViewById(R.id.traziEditText);

        mProgressBar.setVisibility(View.GONE);
        mProgressBar.getIndeterminateDrawable()
                .setColorFilter(Color.parseColor("#083E8E"), PorterDuff.Mode.SRC_IN);

        Bundle bundle = this.getArguments();

        if (bundle != null){
            String kod = bundle.getString("Kod");
            mTraziEditText.setText(kod);
        }

        mAzurirajBtn.setOnClickListener(this);
        mTraziBtn.setOnClickListener(this);
        mScanBtn.setOnClickListener(this);
        mDodajBtn.setOnClickListener(this);
        mDodajBtn.setTypeface(face);
        mScanBtn.setTypeface(face);
        mTraziBtn.setTypeface(face);
        mAzurirajBtn.setTypeface(face);

        return v;
    }

    public void azurirajProizvod(){
        String idProizvoda = mTraziEditText.getText().toString();

        if (TextUtils.isEmpty(idProizvoda)){
            mProgressBar.setVisibility(View.GONE);
            mTraziEditText.setError("Polje je prazno");
            mTraziEditText.requestFocus();
            return;
        }

        final CollectionReference dbProizvodi = db.collection("Proizvodi");
        dbProizvodi.whereEqualTo("id", idProizvoda)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){

                            for (QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots){
                                Proizvodi proizvodi = documentSnapshots.toObject(Proizvodi.class);
                                proizvodi.setDocumentId(documentSnapshots.getId());

                                String documentId = proizvodi.getDocumentId();
                                String id = proizvodi.getId();
                                String imeProizvoda = proizvodi.getImeProizvoda();
                                String opisProizvoda = proizvodi.getOpisProizvoda();
                                int kolicina = proizvodi.getKolicina();
                                float cijena = proizvodi.getCijena();
                                String datum = proizvodi.getDatum();
                                String urlSlike = proizvodi.getImageUrl();

                                Bundle bundle = new Bundle();
                                bundle.putString("UdocumentId", documentId);
                                bundle.putCharSequence("Uid", id);
                                bundle.putCharSequence("UimeProizvoda", imeProizvoda);
                                bundle.putCharSequence("UopisProizvoda", opisProizvoda);
                                bundle.putInt("Ukolicina", kolicina);
                                bundle.putFloat("Ucijena", cijena);
                                bundle.putCharSequence("Udatum", datum);
                                bundle.putCharSequence("UurlSlike", urlSlike);

                                UpdateProizvodiFragment upFragment = new UpdateProizvodiFragment();
                                upFragment.setArguments(bundle);

                                if (getActivity() != null){
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                    fragmentTransaction.replace(R.id.fragmentContainer, upFragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }

                                mProgressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Toasty.error(
                                    requireContext(),
                                    "Proizvod ne postoji",
                                    Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void traziProizvod(){
        String idProizvoda = mTraziEditText.getText().toString();

        if (TextUtils.isEmpty(idProizvoda)){
            mProgressBar.setVisibility(View.GONE);
            mTraziEditText.setError("Polje je prazno");
            mTraziEditText.requestFocus();
            return;
        }

        final CollectionReference dbProizvodi = db.collection("Proizvodi");
        dbProizvodi.whereEqualTo("id", idProizvoda)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                Proizvodi proizvodi = documentSnapshot.toObject(Proizvodi.class);
                                proizvodi.setDocumentId(documentSnapshot.getId());

                                String documentId = proizvodi.getDocumentId();
                                String id = proizvodi.getId();
                                String imeProizvoda = proizvodi.getImeProizvoda();
                                String opisProizvoda = proizvodi.getOpisProizvoda();
                                int kolicina = proizvodi.getKolicina();
                                float cijena = proizvodi.getCijena();
                                String datum = proizvodi.getDatum();

                                float cijenaSum = kolicina * cijena;

                                Bundle bundle = new Bundle();
                                bundle.putCharSequence("documentId", documentId);
                                bundle.putCharSequence("id", id);
                                bundle.putCharSequence("imeProizvoda", imeProizvoda);
                                bundle.putCharSequence("opisProizvoda", opisProizvoda);
                                bundle.putInt("kolicina", kolicina);
                                bundle.putFloat("cijena", cijena);
                                bundle.putFloat("cijenaSum", cijenaSum);
                                bundle.putCharSequence("datum", datum);
                                PodaciProizvodaFragment ppFragment = new PodaciProizvodaFragment();
                                ppFragment.setArguments(bundle);

                                if (getActivity() != null){
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                    fragmentTransaction.replace(R.id.fragmentContainer, ppFragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }

                                mProgressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Toasty.error(
                                    requireContext(),
                                    "Proizvod ne postoji",
                                    Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void dodajProizvod(){
        final String id = mTraziEditText.getText().toString().trim();
        final String imeProizvoda = mIme.getText().toString().trim();
        final String opisProizvoda = mOpis.getText().toString().trim();
        final String kolicina = mKolicina.getText().toString().trim();
        final String cijena = mCijena.getText().toString().trim();
        final String datum = mDatum.getText().toString().trim();

        if (TextUtils.isEmpty(id)){
            mTraziEditText.setError("Id je obavezno unjeti");
            mTraziEditText.requestFocus();
        }

        if (TextUtils.isEmpty(imeProizvoda)){
            mIme.setText("-");
            return;
        }else{
            mIme.setText(imeProizvoda);
        }

        if (TextUtils.isEmpty(opisProizvoda)) {
            mOpis.setText("-");
            return;
        }else {
            mOpis.setText(opisProizvoda);
        }

        if (TextUtils.isEmpty(kolicina)) {
            mKolicina.setText("0");
            return;
        }else {
            mKolicina.setText(kolicina);
        }

        if (TextUtils.isEmpty(cijena)) {
            mCijena.setText("0");
            return;
        }else {
            mCijena.setText(cijena);
        }
        if (TextUtils.isEmpty(datum)){
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY).format(new Date());
            mDatum.setText(date);
            return;
        }else {
            mDatum.setText(datum);
        }

        if (!TextUtils.isEmpty(id)){
            mSpremiBtn.setEnabled(false);
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
                                mSpremiBtn.setEnabled(true);
                            }
                            else {
                                db = FirebaseFirestore.getInstance();
                                cRef = db.collection("Proizvodi");

                                Proizvodi proizvodi = new Proizvodi(
                                        imeProizvoda,
                                        opisProizvoda,
                                        id,
                                        Integer.parseInt(kolicina),
                                        Float.parseFloat(cijena),
                                        datum,
                                        null);

                                cRef.add(proizvodi)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toasty.success(
                                                        requireContext(),
                                                        "Proizvod je spremljen",
                                                        Toast.LENGTH_SHORT).show();
                                                mSpremiBtn.setEnabled(true);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.error(
                                                requireContext(),
                                                "Greška",
                                                Toast.LENGTH_SHORT).show();
                                        mSpremiBtn.setEnabled(true);
                                    }
                                });
                            }
                        }
                    });

        }
    }

    private void spremiBottomSheetDialog(){
        final BottomSheetDialog bDialog = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.dodaj_proizvod_bottom_sheet, null);
        bDialog.setContentView(v);
        bDialog.show();

        FrameLayout bottomSheet = bDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
        if (bottomSheet != null){
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        mIme = v.findViewById(R.id.imeProizvodaET);
        mOpis = v.findViewById(R.id.opisET);
        mKolicina = v.findViewById(R.id.kolicinaET);
        mCijena = v.findViewById(R.id.cijenaET);
        mDatum = v.findViewById(R.id.datumET);

        if (getActivity() != null){
            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-SemiBold.ttf");

            mSpremiBtn = v.findViewById(R.id.spremiButton);
            mOdustaniBtn = v.findViewById(R.id.odustaniButton);
            mSpremiBtn.setTypeface(face);
            mOdustaniBtn.setTypeface(face);
        }


        mSpremiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spremiBtnAnimacija();
                dodajProizvod();
            }
        });

        mOdustaniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                odustaniBtnAnimacija();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        bDialog.dismiss();
                    }
                }, 250);
            }
        });

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

        String provjera = "Jeste li sigurni?";
        TextView mTV = v.findViewById(R.id.TV);
        mTV.setText(provjera);



        mDaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daBtnAnimacija();
                if (getActivity() != null){
                    FirebaseAuth.getInstance().signOut();
                    getActivity().finish();
                    Intent intent = new Intent(requireContext(), LogInActivity.class);
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

    private void dodajBtnAnimacija() {
        mDodajBtn.setScaleX((float) 0.9);
        mDodajBtn.setScaleY((float) 0.9);
        mDodajBtn.animate().scaleX(1).scaleY(1).start();
    }

    private void azurirajBtnAnimacija() {
        mAzurirajBtn.setScaleX((float) 0.9);
        mAzurirajBtn.setScaleY((float) 0.9);
        mAzurirajBtn.animate().scaleX(1).scaleY(1).start();
    }

    private void traziBtnAnimacija() {
        mTraziBtn.setScaleX((float) 0.9);
        mTraziBtn.setScaleY((float) 0.9);
        mTraziBtn.animate().scaleX(1).scaleY(1).start();
    }

    private void skenirajBtnAnimacija() {
        mScanBtn.setScaleX((float) 0.9);
        mScanBtn.setScaleY((float) 0.9);
        mScanBtn.animate().scaleX(1).scaleY(1).start();
    }

    private void spremiBtnAnimacija() {
        mSpremiBtn.setScaleX((float) 0.9);
        mSpremiBtn.setScaleY((float) 0.9);
        mSpremiBtn.animate().scaleX(1).scaleY(1).start();
    }

    private void odustaniBtnAnimacija() {
        mOdustaniBtn.setScaleX((float) 0.9);
        mOdustaniBtn.setScaleY((float) 0.9);
        mOdustaniBtn.animate().scaleX(1).scaleY(1).start();
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

    private void otvoriCameraFragment(Fragment CameraFragment){
        if (getFragmentManager() != null){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, CameraFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.dodajBtn:
                dodajBtnAnimacija();
                spremiBottomSheetDialog();
                break;
            case R.id.azurirajBtn:
                mProgressBar.setVisibility(View.VISIBLE);
                azurirajBtnAnimacija();
                azurirajProizvod();
                break;
            case R.id.traziBtn:
                mProgressBar.setVisibility(View.VISIBLE);
                traziBtnAnimacija();
                traziProizvod();
                break;
            case R.id.skenirajBtn:
                skenirajBtnAnimacija();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Fragment fragment = new CameraViewFragment();
                        otvoriCameraFragment(fragment);
                    }
                }, 200);
                break;
        }
    }

}
