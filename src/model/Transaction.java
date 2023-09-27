package com.bchilakalapudi.rtrconstruction.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Transaction implements Serializable,Comparable<Transaction> {
    public String id;
    public String senderId;
    public String receiverId;
    public double amount;
    public Date date;     //Date currentTime = Calendar.getInstance().getTime();
    public String details;
    public String companyId;
    public Date createddate;

    public Transaction(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", details='" + details + '\'' +
                ", companyId='" + companyId + '\'' +
                '}';
    }

    @Override
    public int compareTo(Transaction o) {
        int resp=-1;
        if(this.getCreateddate()!=null && o.getCreateddate()!=null){
            resp=this.getCreateddate().compareTo(o.getCreateddate());
        }
        return resp;
    }
}

