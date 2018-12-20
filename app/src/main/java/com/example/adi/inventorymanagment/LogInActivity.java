package com.example.adi.inventorymanagment;

import android.content.Intent;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import es.dmoral.toasty.Toasty;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mKorisnickoIme;
    private EditText mKorisnickaLozinka;
    private Button mPrijava;
    private TextView mRegistracija;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        mKorisnickoIme = findViewById(R.id.prijavaImeEditText);
        mKorisnickaLozinka = findViewById(R.id.prijavaLozinkaEditText);
        mPrijava = findViewById(R.id.prijavaBtn);
        mRegistracija = findViewById(R.id.registracijaTV);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");


        mPrijava.setOnClickListener(this);
        mRegistracija.setOnClickListener(this);
        mPrijava.setTypeface(face);
        mRegistracija.setTypeface(face);

        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), BottomNavActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void prijava(){
        if (!praznoPolje()) {
            String email = mKorisnickoIme.getText().toString().trim();
            String lozinka = mKorisnickaLozinka.getText().toString().trim();

            mAuth.signInWithEmailAndPassword(email, lozinka)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), BottomNavActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toasty.error(
                                            LogInActivity.this,
                                            "Krivo korisničko ime ili lozinka",
                                            Toast.LENGTH_SHORT).show();
                                }else if (task.getException() instanceof FirebaseAuthEmailException){
                                    Toasty.error(
                                            LogInActivity.this,
                                            "Neispravan email",
                                            Toast.LENGTH_SHORT).show();
                                }else if (task.getException() instanceof FirebaseAuthInvalidUserException){
                                    Toasty.error(
                                            LogInActivity.this,
                                            "Korisnik ne postoji",
                                            Toast.LENGTH_SHORT).show();
                                }else {
                                    Toasty.error(
                                            LogInActivity.this,
                                            task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }

    private Boolean praznoPolje() {
        String ime = mKorisnickoIme.getText().toString().trim();
        String lozinka = mKorisnickaLozinka.getText().toString().trim();

        if (ime.isEmpty()){
            mKorisnickoIme.setError("Korisničko ime je obavezno unjeti.");
            mKorisnickoIme.requestFocus();
            return true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(ime).matches()){
            mKorisnickoIme.setError("Unesite ispravan email.");
            mKorisnickoIme.requestFocus();
            return true;
        }

        if (lozinka.isEmpty()){
            mKorisnickaLozinka.setError("Lozinku je obavezno unjeti.");
            mKorisnickaLozinka.requestFocus();
            return true;
        }

        if (lozinka.length() < 6){
            mKorisnickaLozinka.setError("Lozinka mora sadržati najmanje 6 znakova.");
            mKorisnickaLozinka.requestFocus();
            return true;
        }

        return false;
    }

    private void otvoriRegistraciju(){
        final Intent intent = new Intent(this, RegistracijaActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 200);
    }

    private void animacijaGumbi(String checker){
        switch (checker){
            case "Prijava":
                mPrijava.setScaleX((float) 0.9);
                mPrijava.setScaleY((float) 0.9);
                mPrijava.animate().scaleX(1).scaleY(1).start();
                break;
            case "Registracija":
                mRegistracija.setScaleX((float) 0.9);
                mRegistracija.setScaleY((float) 0.9);
                mRegistracija.animate().scaleX(1).scaleY(1).start();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.prijavaBtn:
                animacijaGumbi("Prijava");
                prijava();
                break;
            case R.id.registracijaTV:
                animacijaGumbi("Registracija");
                otvoriRegistraciju();
                break;
        }
    }
}
