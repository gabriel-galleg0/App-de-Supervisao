package Model;

import android.graphics.Bitmap;

import com.google.firebase.storage.StorageReference;

public class ImageItem {
    private Bitmap capturedImage;
    private String nomePdv;
    private String pendencia;
    private StorageReference storageReference;

    public ImageItem(String nomePdv, String pendencia, StorageReference storageReference) {
        this.nomePdv = nomePdv;
        this.pendencia = pendencia;
        this.storageReference = storageReference;
    }
    public String getNomePdv() {
        return nomePdv;
    }
    public void setNomePdv(String nomePdv) {
        this.nomePdv = nomePdv;
    }
    public String getPendencia() {
        return pendencia;
    }
    public void setPendencia(String pendencia) {
        this.pendencia = pendencia;
    }
    public StorageReference getStorageReference() {
        return storageReference;
    }
    public void setStorageReference(StorageReference storageReference) {
        this.storageReference = storageReference;
    }
    public void setCapturedImage(Bitmap capturedImage){
        this.capturedImage = capturedImage;
    }
    public Bitmap getCapturedImage(){
        return capturedImage;
    }
}