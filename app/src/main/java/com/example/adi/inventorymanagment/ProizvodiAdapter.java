package com.example.adi.inventorymanagment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
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

        animacijaDijelovaListe(holder);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
            Animation animation = AnimationUtils.loadAnimation(mCtx, R.anim.slide_bot_to_top);
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
        TextView imeTV, kodTV;

        private ProizvodiViewHolder(View v){
            super(v);

            mImeProizvoda = v.findViewById(R.id.imeProizvoda);
            mId = v.findViewById(R.id.id);
            mImage = v.findViewById(R.id.imageViewProizvod);
            imeTV = v.findViewById(R.id.textView3);
            kodTV = v.findViewById(R.id.textView2);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
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
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    transaction.setCustomAnimations(
                                            //drugi in
                                            R.anim.slide_bot_to_top,
                                            //prvi in
                                            R.anim.fade_out,
                                            //prvi out
                                            R.anim.fade_in,
                                            // drugi out
                                            R.anim.slide_top_to_bot);
                                    transaction.addToBackStack(null);
                                    transaction.replace(R.id.fragmentContainer, ppFragment);
                                    transaction.commit();
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

    private void animacijaDijelovaListe(ProizvodiViewHolder holder){
        holder.mImage.setScaleX((float) 0.0);
        holder.mImage.setScaleY((float) 0.0);
        holder.mImage.setAlpha(0f);
        holder.mId.setAlpha(0f);
        holder.mImeProizvoda.setAlpha(0f);
        holder.imeTV.setAlpha(0f);
        holder.kodTV.setAlpha(0f);

        holder.mImage.animate()
                .setStartDelay(200)
                .setDuration(250)
                .scaleX(1).scaleY(1)
                .alpha(1f)
                .start();

        holder.mId.animate()
                .setDuration(250)
                .alpha(1f)
                .start();

        holder.mImeProizvoda.animate()
                .setDuration(250)
                .alpha(1f)
                .start();

        holder.imeTV.animate()
                .setDuration(250)
                .alpha(1f)
                .start();

        holder.kodTV.animate()
                .setDuration(250)
                .alpha(1f)
                .start();
    }
}