package com.bchilakalapudi.rtrconstruction;

import android.net.Uri;

import java.io.Serializable;

public class AttachmentWrapper implements Serializable {
    public String id;
    public String downloadUrl;
    public String transactionId;
    public String filename;
    public String size;
    public Uri filePath;

    public AttachmentWrapper(){

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

    public Uri getFilePath() {
        return filePath;
    }

    public void setFilePath(Uri filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "AttachmentWrapper{" +
                "id='" + id + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", filename='" + filename + '\'' +
                ", size='" + size + '\'' +
                ", filePath=" + filePath +
                '}';
    }
}
