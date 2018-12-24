package com.example.adi.inventorymanagment;

import android.content.Intent;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import es.dmoral.toasty.Toasty;

public class RegistracijaActivity extends AppCompatActivity implements View.OnClickListener{

    private TextInputEditText mLozinka;
    private TextInputEditText mEmail;
    private Button mRegBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registracija);

        mAuth = FirebaseAuth.getInstance();

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");


        mLozinka = findViewById(R.id.regLozinkaEditText);
        mEmail = findViewById(R.id.regEmailEditText);
        mRegBtn = findViewById(R.id.regBtn);

        mRegBtn.setOnClickListener(this);
        mRegBtn.setTypeface(face);
    }

    private Boolean praznoPolje(){
        if (mEmail.getText() != null && mLozinka.getText() != null){
            String email = mEmail.getText().toString().trim();
            String lozinka = mLozinka.getText().toString().trim();

            if (email.isEmpty()){
                mEmail.setError("Email je obavezno unjeti.");
                mEmail.requestFocus();
                mRegBtn.setEnabled(true);
                return true;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                mEmail.setError("Unesite ispravan email.");
                mEmail.requestFocus();
                mRegBtn.setEnabled(true);
                return true;
            }

            if (lozinka.isEmpty()){
                mLozinka.setError("Lozinku je obavezno unjeti.");
                mLozinka.requestFocus();
                mRegBtn.setEnabled(true);
                return true;
            }

            if (lozinka.length() < 6){
                mLozinka.setError("Lozinka mora sadržati najmanje 6 znakova.");
                mLozinka.requestFocus();
                mRegBtn.setEnabled(true);
                return true;
            }
        }
        return false;
    }

    private void registracijaBazePodataka() {
        if (mEmail.getText() != null && mLozinka.getText() != null) {
            String upisaniEmail = mEmail.getText().toString().trim();
            String upisanaLozinka = mLozinka.getText().toString().trim();
            if (!praznoPolje()) {
                mAuth.createUserWithEmailAndPassword(upisaniEmail, upisanaLozinka)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(
                                            RegistracijaActivity.this,
                                            "Registracija završena",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), BottomNavActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toasty.info(
                                                RegistracijaActivity.this,
                                                "Email je u uporabi",
                                                Toast.LENGTH_SHORT).show();
                                        mRegBtn.setEnabled(true);
                                    } else {
                                        //noinspection ConstantConditions
                                        Toast.makeText(RegistracijaActivity.this,
                                                task.getException().getMessage()
                                                , Toast.LENGTH_SHORT).show();
                                        mRegBtn.setEnabled(true);
                                    }

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void animacijaGumbi(@SuppressWarnings("SameParameterValue") String checker) {
        switch (checker){
            case "Registracija":
                mRegBtn.setScaleX((float) 0.9);
                mRegBtn.setScaleY((float) 0.9);
                mRegBtn.animate().scaleX(1).scaleY(1).start();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.regBtn:
                animacijaGumbi("Registracija");
                mRegBtn.setEnabled(false);
                registracijaBazePodataka();
                break;

        }
    }

}
