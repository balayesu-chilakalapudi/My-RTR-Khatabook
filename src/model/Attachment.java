package com.bchilakalapudi.rtrconstruction.model;

import android.net.Uri;

import java.io.Serializable;

public class Attachment implements Serializable {
    public String id;
    public String downloadUrl;
    public String transactionId;
    public String filename;
    public String size;

    public Attachment(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }



    @Override
    public String toString() {
        return "Attachment{" +
                "id='" + id + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", filename='" + filename + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
