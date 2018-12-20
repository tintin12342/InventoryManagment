package com.example.adi.inventorymanagment;

import com.google.firebase.firestore.Exclude;
import java.io.Serializable;

public class Proizvodi implements Serializable {

    @Exclude
    private String documentId;
    private String imeProizvoda;
    private String opisProizvoda;
    private String id;
    private int kolicina;
    private float cijena;
    private String datum;
    private String urlSlike;
    @Exclude
    private String key;

    public Proizvodi(){
        //potrebno
    }

    public Proizvodi(String imeProizvoda,
                     String opisProizvoda,
                     String id,
                     int kolicina,
                     float cijena,
                     String datum,
                     String urlSlike) {
        this.imeProizvoda = imeProizvoda;
        this.opisProizvoda = opisProizvoda;
        this.id = id;
        this.kolicina = kolicina;
        this.cijena = cijena;
        this.datum = datum;
        this.urlSlike = urlSlike;
    }


    public String getImeProizvoda() {
        return imeProizvoda;
    }

    public String getOpisProizvoda() {
        return opisProizvoda;
    }

    public String getId() {
        return id;
    }

    public int getKolicina() {
        return kolicina;
    }

    public float getCijena() {
        return cijena;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDatum() {
        return datum;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getImageUrl() {
        return urlSlike;
    }
}
