package com.example.adi.inventorymanagment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProizvodiAdapter extends RecyclerView.Adapter<ProizvodiAdapter.ProizvodiViewHolder> {

    private Context mCtx;
    private List<Proizvodi> proizvodiList;
    @ServerTimestamp
    Date time;
    private int lastPosition = -1;
    private FirebaseFirestore db;

    public ProizvodiAdapter(Context mCtx, List<Proizvodi> proizvodiList){
        this.mCtx = mCtx;
        this.proizvodiList = proizvodiList;
    }


    @NonNull
    @Override
    public ProizvodiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProizvodiViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_proizvod, parent ,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final ProizvodiViewHolder holder, final int position) {
        setAnimation(holder.itemView, position);
        Proizvodi proizvodi = proizvodiList.get(position);

        holder.mImeProizvoda.setText(proizvodi.getImeProizvoda());
        holder.mId.setText(proizvodi.getId());

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Proizvodi").document(proizvodi.getDocumentId());
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String urlSlike = documentSnapshot.getString("urlSlike");
                        Picasso.get()
                                .load(urlSlike)
                                .placeholder(R.drawable.placeholder)
                                .fit()
                                .centerCrop()
                                .into(holder.mImage);
                    }
                });
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mCtx, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return proizvodiList.size();
    }


    class ProizvodiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mImeProizvoda, mId;
        ImageView mImage;

        public ProizvodiViewHolder(View v){
            super(v);

            mImeProizvoda = v.findViewById(R.id.imeProizvoda);
            mId = v.findViewById(R.id.id);
            mImage = v.findViewById(R.id.imageViewProizvod);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Vibrator vibrator = (Vibrator) mCtx.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (vibrator != null){
                    vibrator.vibrate(25);
            }

            final CollectionReference dbProizvodi = db.collection("Proizvodi");
            dbProizvodi.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()){
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                    Proizvodi proizvodi = documentSnapshot.toObject(Proizvodi.class);
                                    proizvodi.setDocumentId(documentSnapshot.getId());
                                    proizvodi = proizvodiList.get(getAdapterPosition());

                                    String documentId = proizvodi.getDocumentId();
                                    String id = proizvodi.getId();
                                    String imeProizvoda = proizvodi.getImeProizvoda();
                                    String opisProizvoda = proizvodi.getOpisProizvoda();
                                    int kolicina = proizvodi.getKolicina();
                                    float cijena = proizvodi.getCijena();
                                    String datum = proizvodi.getDatum();
                                    String urlSlike = proizvodi.getImageUrl();

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
                                    bundle.putCharSequence("urlSlike", urlSlike);
                                    PodaciProizvodaFragment ppFragment = new PodaciProizvodaFragment();
                                    ppFragment.setArguments(bundle);

                                    FragmentManager fragmentManager = ((FragmentActivity)mCtx).getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                    fragmentTransaction.replace(R.id.fragmentContainer, ppFragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    fragmentTransaction.commit();
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

    public void filtriranaLista(ArrayList<Proizvodi> filtriranaLista){
        proizvodiList = filtriranaLista;
        notifyDataSetChanged();
    }

}