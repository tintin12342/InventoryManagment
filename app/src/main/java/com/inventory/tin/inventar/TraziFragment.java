package com.inventory.tin.inventar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import es.dmoral.toasty.Toasty;

public class TraziFragment extends Fragment implements View.OnClickListener {

    private Button mTraziBtn;
    private Button mScanBtn;
    private Button mDodajBtn;
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
    private LinearLayout layout1, layout2, layout3, layout4, layout5, layoutKalendar;
    private CalendarView mCalendar;
    private Boolean checker = false;

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
        mTraziEditText = v.findViewById(R.id.traziEditText);

        mProgressBar.setVisibility(View.GONE);
        mProgressBar.getIndeterminateDrawable()
                .setColorFilter(Color.parseColor("#083E8E"), PorterDuff.Mode.SRC_IN);

        Bundle bundle = this.getArguments();

        if (bundle != null){
            String kod = bundle.getString("Kod");
            mTraziEditText.setText(kod);
        }

        mTraziBtn.setOnClickListener(this);
        mScanBtn.setOnClickListener(this);
        mDodajBtn.setOnClickListener(this);
        mDodajBtn.setTypeface(face);
        mScanBtn.setTypeface(face);
        mTraziBtn.setTypeface(face);

        return v;
    }

    private void traziProizvod(){
        String idProizvoda = mTraziEditText.getText().toString();


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
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                                    transaction.setCustomAnimations(
                                            //drugi in
                                            R.anim.slide_bot_to_top,
                                            //prvi in
                                            R.anim.fade_out_trazi,
                                            //prvi out
                                            R.anim.fade_in_trazi,
                                            // drugi out
                                            R.anim.slide_top_to_bot);
                                    transaction.addToBackStack(null);
                                    transaction.replace(R.id.fragmentContainer, ppFragment);
                                    transaction.commit();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mTraziBtn.setEnabled(true);
                                        }
                                    },1275);
                                }

                                mProgressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Toasty.error(
                                    requireContext(),
                                    "Proizvod ne postoji",
                                    Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                            mTraziBtn.setEnabled(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void dodajProizvod(final BottomSheetDialog bDialog){
        final String id = mTraziEditText.getText().toString().trim();
        final String imeProizvoda = mIme.getText().toString().trim();
        final String opisProizvoda = mOpis.getText().toString().trim();
        final String kolicina = mKolicina.getText().toString().trim();
        final String cijena = mCijena.getText().toString().trim();
        final String datum = mDatum.getText().toString().trim();

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
                                                bDialog.dismiss();

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
    //Bottom sheet dialog za spremanje proizvoda
    private void spremiBottomSheetDialog(){
        final BottomSheetDialog bDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.dodaj_proizvod_bottom_sheet, null);
        bDialog.setContentView(v);
        bDialog.show();

        FrameLayout bottomSheet = bDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null){
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        mIme = v.findViewById(R.id.imeProizvodaET);
        mOpis = v.findViewById(R.id.opisET);
        mKolicina = v.findViewById(R.id.kolicinaET);
        mCijena = v.findViewById(R.id.cijenaET);
        mDatum = v.findViewById(R.id.datumET);
        mCalendar = v.findViewById(R.id.calendarView);

        layout1 = v.findViewById(R.id.layout1);
        layout2 = v.findViewById(R.id.layout2);
        layout3 = v.findViewById(R.id.layout3);
        layout4 = v.findViewById(R.id.layout4);
        layout5 = v.findViewById(R.id.layout5);
        layoutKalendar = v.findViewById(R.id.layoutKalenar);

        final Button mOtvoriKalendarBtn = v.findViewById(R.id.openCalendarBtn);
        mOtvoriKalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker == false){
                    InputMethodManager imm = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    mOtvoriKalendarBtn.setBackgroundResource(R.drawable.minus);
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    layout4.setVisibility(View.GONE);
                    layout5.setVisibility(View.GONE);
                    layoutKalendar.setVisibility(View.VISIBLE);

                    mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                            String datum = dayOfMonth + "-" + (month + 1) + "-" + year;
                            mDatum.setText(datum);
                        }
                    });
                    checker = true;
                }else if (checker == true){
                    checker = false;
                    mOtvoriKalendarBtn.setBackgroundResource(R.drawable.plus);
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.VISIBLE);
                    layout3.setVisibility(View.VISIBLE);
                    layout4.setVisibility(View.VISIBLE);
                    layout5.setVisibility(View.VISIBLE);
                    layoutKalendar.setVisibility(View.GONE);
                }
            }
        });

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
                animacijaGumbi("SpremiBSD");
                dodajProizvod(bDialog);
            }
        });

        mOdustaniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animacijaGumbi("OdustaniBSD");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bDialog.dismiss();
                    }
                },250);
            }
        });

    }
    //Bottom sheet dialog za odjavu
    private void bottomSheetDialog(){
        final BottomSheetDialog bDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bDialog.setContentView(v);
        bDialog.show();

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
                animacijaGumbi("Da");
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
                animacijaGumbi("Ne");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bDialog.dismiss();
                    }
                }, 250);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@Nonnull Menu menu,@Nonnull MenuInflater inflater) {
        inflater.inflate(R.menu.odjava_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@Nonnull MenuItem item) {
        bottomSheetDialog();
        return true;
    }

    private void animacijaGumbi(String checker){
        switch (checker){
            case "Spremi":
                mDodajBtn.setScaleX((float) 0.9);
                mDodajBtn.setScaleY((float) 0.9);
                mDodajBtn.animate().scaleX(1).scaleY(1).start();
                break;
            case "Skeniraj":
                mScanBtn.setScaleX((float) 0.9);
                mScanBtn.setScaleY((float) 0.9);
                mScanBtn.animate().scaleX(1).scaleY(1).start();
                break;
            case "Traži":
                mTraziBtn.setScaleX((float) 0.9);
                mTraziBtn.setScaleY((float) 0.9);
                mTraziBtn.animate().scaleX(1).scaleY(1).start();
                break;
            case "SpremiBSD":
                mSpremiBtn.setScaleX((float) 0.9);
                mSpremiBtn.setScaleY((float) 0.9);
                mSpremiBtn.animate().scaleX(1).scaleY(1).start();
                break;
            case "OdustaniBSD":
                mOdustaniBtn.setScaleX((float) 0.9);
                mOdustaniBtn.setScaleY((float) 0.9);
                mOdustaniBtn.animate().scaleX(1).scaleY(1).start();
                break;
            case "Da":
                mDaBtn.setScaleX((float) 0.9);
                mDaBtn.setScaleY((float) 0.9);
                mDaBtn.animate().scaleX(1).scaleY(1).start();
                break;
            case "Ne":
                mNeBtn.setScaleX((float) 0.9);
                mNeBtn.setScaleY((float) 0.9);
                mNeBtn.animate().scaleX(1).scaleY(1).start();
                break;
        }
    }

    private void otvoriCameraFragment(final Fragment CameraFragment){
        if (getFragmentManager() != null){
            final FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    //drugi in
                    R.anim.slide_bot_to_top,
                    //prvi in
                    R.anim.fade_out_trazi,
                    //prvi out
                    R.anim.fade_in_trazi,
                    // drugi out
                    R.anim.slide_top_to_bot);
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragmentContainer, CameraFragment);
            transaction.commit();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanBtn.setEnabled(true);
                }
            },1275);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.dodajBtn:
                animacijaGumbi("Spremi");

                String id = mTraziEditText.getText().toString().trim();
                if (TextUtils.isEmpty(id)){
                    mTraziEditText.setError("Id je obavezno unjeti");
                    mTraziEditText.requestFocus();
                }else {
                    mDodajBtn.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            spremiBottomSheetDialog();
                            mDodajBtn.setEnabled(true);
                        }
                    }, 275);
                }
                break;
            case R.id.traziBtn:
                mProgressBar.setVisibility(View.VISIBLE);
                animacijaGumbi("Traži");
                String idProizvoda = mTraziEditText.getText().toString();

                if (TextUtils.isEmpty(idProizvoda)){
                    mProgressBar.setVisibility(View.GONE);
                    mTraziEditText.setError("Polje je prazno");
                    mTraziEditText.requestFocus();
                }else {
                    mTraziBtn.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            traziProizvod();
                        }
                    },275);
                }
                break;
            case R.id.skenirajBtn:
                mScanBtn.setEnabled(false);
                animacijaGumbi("Skeniraj");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Fragment fragment = new CameraViewFragment();
                        otvoriCameraFragment(fragment);
                    }
                }, 275);
                break;
        }
    }
}
