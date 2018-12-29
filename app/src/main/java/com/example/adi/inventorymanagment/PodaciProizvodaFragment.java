package com.example.adi.inventorymanagment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Objects;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class PodaciProizvodaFragment extends Fragment implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private LinearLayout linlay1;
    private LinearLayout linlay2;
    private LinearLayout linlay3;
    private LinearLayout linlay4;
    private LinearLayout linlay5;
    private LinearLayout linlay6;
    private LinearLayout linlayBtn;
    private LinearLayout linlayProgress;
    private TextView mCijena;
    private TextView mCijenaSum;
    private TextView mKolicina;
    private TextView mId;
    private TextView mImeProizvoda;
    private TextView mOpisProizvoda;
    private TextView mDatum;
    private Button mObrisiBtn;
    private Button mZbrojBtn;
    private Button mOduzmiBtn;
    private Button mSpremiSlikuBtn;
    private Button mDaBtn;
    private Button mNeBtn;
    private EditText mBrojEditText;
    private ImageView mSlikaProizvoda;
    private Uri mImageUri;
    private ProgressBar mProgressBar;

    @ServerTimestamp
    Date time;

    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_podaci_proizvoda, container, false);
        mStorageRef = FirebaseStorage.getInstance().getReference("slike");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("slike");

        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            Toolbar toolbar = v.findViewById(R.id.toolbar);
            toolbar.setTitle("Podaci Proizvoda");
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
        }

        AppBarLayout appBarLayout = v.findViewById(R.id.appBarLayout);
        disableImageScroll(appBarLayout);

        linlay1 = v.findViewById(R.id.linlay1);
        linlay2 = v.findViewById(R.id.linlay2);
        linlay3 = v.findViewById(R.id.linlay3);
        linlay4 = v.findViewById(R.id.linlay4);
        linlay5 = v.findViewById(R.id.linlay5);
        linlay6 = v.findViewById(R.id.linlay6);
        linlayBtn = v.findViewById(R.id.linlayBtn);
        linlayProgress = v.findViewById(R.id.linlayProgress);
        mId = v.findViewById(R.id.ppId);
        mImeProizvoda = v.findViewById(R.id.ppImeProizvoda);
        mOpisProizvoda = v.findViewById(R.id.ppOpis);
        mCijena = v.findViewById(R.id.ppCijena);
        mDatum = v.findViewById(R.id.ppDatum);
        mCijenaSum = v.findViewById(R.id.ppCijenaSum);
        mKolicina = v.findViewById(R.id.ppKolicina);
        mSlikaProizvoda = v.findViewById(R.id.imageViewPP);
        mObrisiBtn = v.findViewById(R.id.ppObrisiBtn);
        mSpremiSlikuBtn = v.findViewById(R.id.spremiSlikuBtn);
        mProgressBar = v.findViewById(R.id.progressBarPP);

        linlay1.setOnClickListener(this);
        linlay2.setOnClickListener(this);
        linlay3.setOnClickListener(this);
        linlay4.setOnClickListener(this);
        linlay5.setOnClickListener(this);
        linlay6.setOnClickListener(this);
        mObrisiBtn.setOnClickListener(this);
        mSpremiSlikuBtn.setOnClickListener(this);
        registerForContextMenu(mSlikaProizvoda);

        slanjePodataka();
        postavljanjeSlike();
        setHasOptionsMenu(true);

        return v;
    }

    private void obrisiProizvod(){
        db = FirebaseFirestore.getInstance();

        Bundle bundle = this.getArguments();
        if (bundle != null){
            String documentId = bundle.getString("documentId");
            if (documentId != null){
                FirebaseFirestore.getInstance().collection("Proizvodi")
                        .document(documentId).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@Nonnull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(
                                            requireContext(),
                                            "Proizvod je obrisan",
                                            Toast.LENGTH_LONG).show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (getActivity() != null){
                                                getActivity().onBackPressed();
                                            }
                                        }
                                    },350);
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

    private void postavljanjeSlike() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            db = FirebaseFirestore.getInstance();
            String documentId = bundle.getString("documentId");
            if (documentId != null) {
                DocumentReference docRef = db.collection("Proizvodi").document(documentId);
                docRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String urlSlike = documentSnapshot.getString("urlSlike");
                                if (mSlikaProizvoda != null) {
                                    Picasso.get()
                                            .load(urlSlike)
                                            .fit()
                                            .centerCrop(Gravity.CENTER)
                                            .into(mSlikaProizvoda);
                                }
                            }
                        });
            }
        }
    }

    private String getFileExtension(Uri uri) {
        //noinspection ConstantConditions
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void spremiSliku() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            CharSequence kodProizvoda = bundle.getCharSequence("id");
            if (mImageUri != null) {
                final StorageReference fileRef = mStorageRef.child(kodProizvoda
                        + "."
                        + getFileExtension(mImageUri));

                mUploadTask = fileRef.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProgressBar.setProgress(0);
                                            }
                                        }, 500);

                                        Toasty.success(requireContext(),
                                                "Slika spremljena",
                                                Toast.LENGTH_SHORT).show();
                                        db = FirebaseFirestore.getInstance();

                                        Bundle bundle = getArguments();
                                        if (bundle != null) {
                                            String documentId = bundle.getString("documentId");
                                            String uploadId = mDatabaseRef.push().getKey();
                                            String urlSlike = uri.toString();

                                            if (uploadId != null) {
                                                mDatabaseRef.child(uploadId).setValue(urlSlike);
                                            }
                                            if (documentId != null) {
                                                db.collection("Proizvodi").document(documentId)
                                                        .update("urlSlike", urlSlike);
                                            } else {
                                                Toasty.warning(requireContext(),
                                                        "Pokušajte ponovno",
                                                        Toast.LENGTH_SHORT).show();
                                            }

                                            linlayBtn.setVisibility(View.GONE);
                                            linlayProgress.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                mProgressBar.setProgress((int) progress);
                            }
                        });
            } else {
                Toasty.info(requireContext(),
                        "Niste odabrali sliku",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    //ta nema toasty nakon brisanja bez slike
    private void obrisiSlikuProizvod(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            String documentId = bundle.getString("documentId");
            if (documentId != null) {
                DocumentReference docRef = db.collection("Proizvodi").document(documentId);
                docRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String urlSlike = documentSnapshot.getString("urlSlike");
                                    if (urlSlike != null) {
                                        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(urlSlike);
                                        imageRef.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        }
    }

    private void obrisiSliku() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String documentId = bundle.getString("documentId");
            if (documentId != null) {
                DocumentReference docRef = db.collection("Proizvodi").document(documentId);
                docRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String urlSlike = documentSnapshot.getString("urlSlike");
                                    if (urlSlike != null) {
                                        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(urlSlike);
                                        imageRef.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toasty.success(requireContext(),
                                                                "Slika obrisana",
                                                                Toast.LENGTH_SHORT).show();
                                                        Picasso.get()
                                                                .load(R.drawable.placeholder)
                                                                .fit()
                                                                .into(mSlikaProizvoda);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@Nonnull Exception e) {
                                                Toasty.info(requireContext(),
                                                        "Slika ne postoji",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toasty.error(requireContext(),
                                                "Slike nema u bazi podataka",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@Nonnull Exception e) {
                        Toasty.error(requireContext(),
                                "Neuspjelo brisanje slike",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void odaberiSliku() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @SuppressLint("SetTextI18n")
    private void slanjePodataka() {
        final Bundle bundle = this.getArguments();
        final String kn = " KN";

        if (bundle != null) {
            CharSequence id = bundle.getCharSequence("id");
            mId.setText(id);

            CharSequence imeProizvoda = bundle.getCharSequence("imeProizvoda");
            mImeProizvoda.setText(imeProizvoda);

            CharSequence opisProizvoda = bundle.getCharSequence("opisProizvoda");
            mOpisProizvoda.setText(opisProizvoda);

            int kolicina = bundle.getInt("kolicina");
            mKolicina.setText(String.valueOf(kolicina));

            float cijena = bundle.getFloat("cijena");
            mCijena.setText(String.valueOf(cijena));

            float cijenaSum = bundle.getFloat("cijenaSum");
            mCijenaSum.setText(String.valueOf(cijenaSum) + kn);

            CharSequence datum = bundle.getCharSequence("datum");
            mDatum.setText(datum);
        }

        mId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                db = FirebaseFirestore.getInstance();

                if (bundle != null) {
                    String documentId = bundle.getString("documentId");
                    String id = mId.getText().toString().trim();

                    Proizvodi p = new Proizvodi(
                            null,
                            null,
                            id,
                            0,
                            0,
                            null,
                            null);

                    if (documentId != null) {
                        db.collection("Proizvodi").document(documentId)
                                .update("id", p.getId());
                    } else {
                        Toasty.warning(requireContext(),
                                "Pokušajte ponovno",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mImeProizvoda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                db = FirebaseFirestore.getInstance();

                if (bundle != null) {
                    String documentId = bundle.getString("documentId");
                    String imeProizvoda = mImeProizvoda.getText().toString().trim();

                    Proizvodi p = new Proizvodi(
                            imeProizvoda,
                            null,
                            null,
                            0,
                            0,
                            null,
                            null);

                    if (documentId != null) {
                        db.collection("Proizvodi").document(documentId)
                                .update("imeProizvoda", p.getImeProizvoda());
                    } else {
                        Toasty.warning(requireContext(),
                                "Pokušajte ponovno",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mOpisProizvoda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                db = FirebaseFirestore.getInstance();

                if (bundle != null) {
                    String documentId = bundle.getString("documentId");
                    String opisProizvoda = mOpisProizvoda.getText().toString().trim();

                    Proizvodi p = new Proizvodi(
                            null,
                            opisProizvoda,
                            null,
                            0,
                            0,
                            null,
                            null);

                    if (documentId != null) {
                        db.collection("Proizvodi").document(documentId)
                                .update("opisProizvoda", p.getOpisProizvoda());
                    } else {
                        Toasty.warning(requireContext(),
                                "Pokušajte ponovno",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mCijena.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String kolicina = mKolicina.getText().toString().trim();
                String cijena = mCijena.getText().toString().trim();
                String kn = " KN";

                Proizvodi p = new Proizvodi(
                        null,
                        null,
                        null,
                        Integer.parseInt(kolicina),
                        Float.parseFloat(cijena),
                        null,
                        null);

                int kolicinaBroj = p.getKolicina();
                float cijenaBroj = p.getCijena();
                float cijenaSum = kolicinaBroj * cijenaBroj;

                mCijenaSum.setText(String.valueOf(cijenaSum) + kn);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                db = FirebaseFirestore.getInstance();

                if (bundle != null) {
                    String documentId = bundle.getString("documentId");

                    String kolicina = mKolicina.getText().toString().trim();
                    String cijena = mCijena.getText().toString().trim();

                    Proizvodi p = new Proizvodi(
                            null,
                            null,
                            null,
                            Integer.parseInt(kolicina),
                            Float.parseFloat(cijena),
                            null,
                            null);

                    if (documentId != null) {
                        db.collection("Proizvodi").document(documentId)
                                .update("cijena", p.getCijena());
                    } else {
                        Toasty.warning(requireContext(),
                                "Pokušajte ponovno",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mKolicina.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String kolicina = mKolicina.getText().toString().trim();
                String cijena = mCijena.getText().toString().trim();
                String kn = " KN";

                Proizvodi p = new Proizvodi(
                        null,
                        null,
                        null,
                        Integer.parseInt(kolicina),
                        Float.parseFloat(cijena),
                        null,
                        null);

                int kolicinaBroj = p.getKolicina();
                float cijenaBroj = p.getCijena();
                float cijenaSum = kolicinaBroj * cijenaBroj;

                mCijenaSum.setText(String.valueOf(cijenaSum) + kn);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                db = FirebaseFirestore.getInstance();

                if (bundle != null) {
                    String documentId = bundle.getString("documentId");

                    String kolicina = mKolicina.getText().toString().trim();
                    String cijena = mCijena.getText().toString().trim();

                    Proizvodi p = new Proizvodi(
                            null,
                            null,
                            null,
                            Integer.parseInt(kolicina),
                            Float.parseFloat(cijena),
                            null,
                            null);

                    if (documentId != null) {
                        db.collection("Proizvodi").document(documentId)
                                .update("kolicina", p.getKolicina());
                    } else {
                        Toasty.warning(requireContext(),
                                "Pokušajte ponovno",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mDatum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                db = FirebaseFirestore.getInstance();

                if (bundle != null) {
                    String documentId = bundle.getString("documentId");
                    String datum = mDatum.getText().toString().trim();

                    Proizvodi p = new Proizvodi(
                            null,
                            null,
                            null,
                            0,
                            0,
                            datum,
                            null);

                    if (documentId != null) {
                        db.collection("Proizvodi").document(documentId)
                                .update("datum", p.getDatum());
                    } else {
                        Toasty.warning(requireContext(),
                                "Pokušajte ponovno",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void bottomSheetDialog() {
        final BottomSheetDialog bDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.podaci_proizvoda_bottom_sheet, null);
        bDialog.setContentView(v);
        bDialog.show();

        mBrojEditText = v.findViewById(R.id.BSeditText);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                100
        );
        params.setMargins(60, 20, 60, 40);

        if (getActivity() != null) {
            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-SemiBold.ttf");

            mZbrojBtn = v.findViewById(R.id.BSzbrojBtn);
            mOduzmiBtn = v.findViewById(R.id.BSoduzmiBtn);
            mZbrojBtn.setTypeface(face);
            mOduzmiBtn.setTypeface(face);
        }

        TextView mBSTV = v.findViewById(R.id.BSTV);
        String promjenaBtn = "Promijenite";
        //kod
        String promjenaKoda = "Promijenite kod";
        String upisKoda = "Upišite kod";
        //ime
        String promjenaImena = "Promijenite ime";
        String upisImena = "Upišite ime";
        //opis
        String promjenaOpisa = "Promijenite opis";
        String upisOpisa = "Upišite opis";
        //cijena
        String promjenaCijene = "Promijenite cijenu";
        String upisCijene = "Upišite cijenu";
        //datum
        String promjenaDatuma = "Promijenite datum";
        String upisDatuma = "Upišite datum";

        if (linlay1.isPressed()) {
            mBSTV.setText(promjenaKoda);
            mBSTV.setPadding(0, 0, 20, 0);
            mBrojEditText.setHint(upisKoda);
            mZbrojBtn.setText(promjenaBtn);
            mZbrojBtn.setLayoutParams(params);
            mOduzmiBtn.setVisibility(View.GONE);

            String stariKod = mId.getText().toString();
            mBrojEditText.setFilters(new InputFilter[]
                    {new InputFilter.LengthFilter(13)});
            mBrojEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            mBrojEditText.setText(stariKod);

            mZbrojBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animacijeGumbi("Promijenite");
                    mijenjanjeKoda(bDialog);
                }
            });
        } else if (linlay2.isPressed()) {
            mBSTV.setText(promjenaImena);
            mBSTV.setPadding(0, 0, 20, 0);
            mBrojEditText.setHint(upisImena);
            mZbrojBtn.setText(promjenaBtn);
            mZbrojBtn.setLayoutParams(params);
            mOduzmiBtn.setVisibility(View.GONE);

            String staroIme = mImeProizvoda.getText().toString();
            mBrojEditText.setFilters(new InputFilter[]
                    {new InputFilter.LengthFilter(120)});
            mBrojEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            mBrojEditText.setText(staroIme);

            mZbrojBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animacijeGumbi("Promijenite");
                    mijenjanjeImena(bDialog);
                }
            });
        } else if (linlay3.isPressed()) {
            mBSTV.setText(promjenaOpisa);
            mBSTV.setPadding(0, 0, 20, 0);
            mBrojEditText.setHint(upisOpisa);
            mZbrojBtn.setText(promjenaBtn);
            mZbrojBtn.setLayoutParams(params);
            mOduzmiBtn.setVisibility(View.GONE);

            String stariOpis = mOpisProizvoda.getText().toString();
            mBrojEditText.setFilters(new InputFilter[]
                    {new InputFilter.LengthFilter(110)});
            mBrojEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            mBrojEditText.setText(stariOpis);

            mZbrojBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animacijeGumbi("Promijenite");
                    mijenjanjeOpisa(bDialog);
                }
            });
        } else if (linlay4.isPressed()) {
            mBSTV.setText(promjenaCijene);
            mBSTV.setPadding(0, 0, 20, 0);
            mBrojEditText.setHint(upisCijene);
            mZbrojBtn.setText(promjenaBtn);
            mZbrojBtn.setLayoutParams(params);
            mOduzmiBtn.setVisibility(View.GONE);

            mBrojEditText.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            mZbrojBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animacijeGumbi("Promijenite");
                    mijenjanjeCijene(bDialog);
                }
            });

        } else if (linlay5.isPressed()) {
            mBrojEditText.setHint("Upišite količinu");

            mZbrojBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animacijeGumbi("Izrađeno");
                    zbrajanjeKolicine();
                }
            });

            mOduzmiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animacijeGumbi("Prodano");
                    oduzimanjeKolicine();
                }
            });
        } else if (linlay6.isPressed()) {
            mBSTV.setText(promjenaDatuma);
            mBSTV.setPadding(0, 0, 20, 0);
            mBrojEditText.setHint(upisDatuma);
            mZbrojBtn.setText(promjenaBtn);
            mZbrojBtn.setLayoutParams(params);
            mOduzmiBtn.setVisibility(View.GONE);

            String stariDatum = mDatum.getText().toString();
            mBrojEditText.setFilters(new InputFilter[]
                    {new InputFilter.LengthFilter(10)});
            mBrojEditText.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
            mBrojEditText.setText(stariDatum);

            mZbrojBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animacijeGumbi("Promijenite");
                    mijenjanjeDatuma(bDialog);
                }
            });
        }

    }

    private void bottomSheetDialogBrisanjeProizvoda() {
        final BottomSheetDialog bDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bDialog.setContentView(v);
        bDialog.show();

        if (getActivity() != null) {
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
                animacijeGumbi("Da");
                mDaBtn.setEnabled(false);
                obrisiSlikuProizvod();

                Bundle bundle = getArguments();
                if (bundle != null) {
                    String documentId = bundle.getString("documentId");
                    if (documentId != null) {
                        DocumentReference docRef = db.collection("Proizvodi").document(documentId);
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    obrisiProizvod();
                                }
                            }
                        });
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bDialog.hide();
                    }
                }, 250);
            }
        });

        mNeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animacijeGumbi("Ne");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bDialog.hide();
                    }
                }, 250);
            }
        });
    }

    private void bottomSheetDialogBrisanjeSlike() {
        final BottomSheetDialog bDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bDialog.setContentView(v);
        bDialog.show();

        if (getActivity() != null) {
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
                animacijeGumbi("Da");
                obrisiSliku();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bDialog.hide();
                    }
                }, 250);
            }
        });

        mNeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animacijeGumbi("Ne");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bDialog.hide();
                    }
                }, 250);
            }
        });
    }

    private void mijenjanjeKoda(final BottomSheetDialog bDialog) {
        String text = mBrojEditText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            mBrojEditText.setError("Polje je prazno");
            mBrojEditText.requestFocus();
        } else {
            mId.setText(text);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bDialog.hide();
                }
            }, 500);
        }
    }

    private void mijenjanjeImena(final BottomSheetDialog bDialog) {
        String text = mBrojEditText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            mBrojEditText.setError("Polje je prazno");
            mBrojEditText.requestFocus();
        } else {
            mImeProizvoda.setText(text);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bDialog.hide();
                }
            }, 500);
        }
    }

    private void mijenjanjeOpisa(final BottomSheetDialog bDialog) {
        String text = mBrojEditText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            mBrojEditText.setError("Polje je prazno");
            mBrojEditText.requestFocus();
        } else {
            mOpisProizvoda.setText(text);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bDialog.hide();
                }
            }, 500);
        }
    }

    private void mijenjanjeCijene(final BottomSheetDialog bDialog) {
        String text = mBrojEditText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            mBrojEditText.setError("Polje je prazno");
            mBrojEditText.requestFocus();
        } else {
            float upisaniBroj = Float.parseFloat(mBrojEditText.getText().toString().trim());
            mCijena.setText(String.valueOf(upisaniBroj));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bDialog.hide();
                }
            }, 500);
        }
    }

    private void mijenjanjeDatuma(final BottomSheetDialog bDialog) {
        String text = mBrojEditText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            mBrojEditText.setError("Polje je prazno");
            mBrojEditText.requestFocus();
        } else {
            mDatum.setText(text);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bDialog.hide();
                }
            }, 500);
        }
    }

    private void zbrajanjeKolicine() {
        String text = mBrojEditText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            mBrojEditText.setError("Polje je prazno");
            mBrojEditText.requestFocus();
        } else {
            int upisaniBroj = Integer.parseInt(mBrojEditText.getText().toString().trim());
            String kolicinaBroj = mKolicina.getText().toString();
            int broj = Integer.parseInt(kolicinaBroj);
            int kolicinaHolder = upisaniBroj + broj;

            mKolicina.setText(String.valueOf(kolicinaHolder));
        }
    }

    private void oduzimanjeKolicine() {
        String text = mBrojEditText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            mBrojEditText.setError("Polje je prazno");
            mBrojEditText.requestFocus();
        } else {
            int upisaniBroj = Integer.parseInt(mBrojEditText.getText().toString().trim());
            String kolicinaBroj = mKolicina.getText().toString();
            int broj = Integer.parseInt(kolicinaBroj);
            int kolicinaHolder = broj - upisaniBroj;

            if (kolicinaHolder < 0) {
                mKolicina.setText("0");
            } else {
                mKolicina.setText(String.valueOf(kolicinaHolder));
            }
        }
    }

    private void disableImageScroll(AppBarLayout appBarLayout) {
        CoordinatorLayout.LayoutParams paramss = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        paramss.setBehavior(new AppBarLayout.Behavior());

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        //noinspection ConstantConditions
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get()
                    .load(mImageUri)
                    .fit()
                    .centerCrop(Gravity.CENTER)
                    .into(mSlikaProizvoda);
        }
    }

    @Override
    public void onCreateOptionsMenu(@Nonnull Menu menu,@Nonnull MenuInflater inflater) {
        inflater.inflate(R.menu.context_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@Nonnull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.promjenaSlike:
                odaberiSliku();
                Animation animation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in);
                linlayBtn.setAnimation(animation);
                linlayBtn.setVisibility(View.VISIBLE);
                linlayProgress.setAnimation(animation);
                linlayProgress.setVisibility(View.VISIBLE);
                return true;
            case R.id.brisanjeSlike:
                bottomSheetDialogBrisanjeSlike();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void animacijeGumbi(String checker) {
        switch (checker) {
            case "Izrađeno":
                mZbrojBtn.setScaleX((float) 0.9);
                mZbrojBtn.setScaleY((float) 0.9);
                mZbrojBtn.animate().scaleX(1).scaleY(1).start();
                break;
            case "Prodano":
                mOduzmiBtn.setScaleX((float) 0.9);
                mOduzmiBtn.setScaleY((float) 0.9);
                mOduzmiBtn.animate().scaleX(1).scaleY(1).start();
                break;
            case "Promijenite":
                mZbrojBtn.setScaleX((float) 0.9);
                mZbrojBtn.setScaleY((float) 0.9);
                mZbrojBtn.animate().scaleX(1).scaleY(1).start();
                break;
            case "Kod":
                linlay1.setScaleX((float) 0.9);
                linlay1.setScaleY((float) 0.9);
                linlay1.animate().scaleX(1).scaleY(1).start();
                break;
            case "Ime":
                linlay2.setScaleX((float) 0.9);
                linlay2.setScaleY((float) 0.9);
                linlay2.animate().scaleX(1).scaleY(1).start();
                break;
            case "Opis":
                linlay3.setScaleX((float) 0.9);
                linlay3.setScaleY((float) 0.9);
                linlay3.animate().scaleX(1).scaleY(1).start();
                break;
            case "Cijena":
                linlay4.setScaleX((float) 0.9);
                linlay4.setScaleY((float) 0.9);
                linlay4.animate().scaleX(1).scaleY(1).start();
                break;
            case "Količina":
                linlay5.setScaleX((float) 0.9);
                linlay5.setScaleY((float) 0.9);
                linlay5.animate().scaleX(1).scaleY(1).start();
                break;
            case "Datum":
                linlay6.setScaleX((float) 0.9);
                linlay6.setScaleY((float) 0.9);
                linlay6.animate().scaleX(1).scaleY(1).start();
                break;
            case "Spremi":
                mSpremiSlikuBtn.setScaleX((float) 0.9);
                mSpremiSlikuBtn.setScaleY((float) 0.9);
                mSpremiSlikuBtn.animate().scaleX(1).scaleY(1).start();
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
            case "Izbriši":
                mObrisiBtn.setScaleX((float) 0.9);
                mObrisiBtn.setScaleY((float) 0.9);
                mObrisiBtn.animate().scaleX(1).scaleY(1).start();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linlay1:
                animacijeGumbi("Kod");
                bottomSheetDialog();
                break;
            case R.id.linlay2:
                animacijeGumbi("Ime");
                bottomSheetDialog();
                break;
            case R.id.linlay3:
                animacijeGumbi("Opis");
                bottomSheetDialog();
                break;
            case R.id.linlay4:
                animacijeGumbi("Cijena");
                bottomSheetDialog();
                break;
            case R.id.linlay5:
                animacijeGumbi("Količina");
                bottomSheetDialog();
                break;
            case R.id.linlay6:
                animacijeGumbi("Datum");
                bottomSheetDialog();
                break;
            case R.id.ppObrisiBtn:
                animacijeGumbi("Izbriši");
                bottomSheetDialogBrisanjeProizvoda();
                break;
            case R.id.spremiSlikuBtn:
                animacijeGumbi("Spremi");
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toasty.info(requireContext(),
                            "Slika se sprema",
                            Toast.LENGTH_SHORT).show();
                } else {
                    spremiSliku();
                }
                break;
        }
    }
}
