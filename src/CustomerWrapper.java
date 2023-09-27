package com.bchilakalapudi.rtrconstruction;

import com.bchilakalapudi.rtrconstruction.model.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerWrapper implements Serializable,Comparable<CustomerWrapper> {
    public String id;
    public String name;
    public String phone;
    public boolean admin; //admin or customer
    public String loginPin;
    public String type; //company or customer
    public String uid;   //phone verified Id
    public String createdbyId;
    public double balance;
    public boolean gave;
    public boolean got;
    public Date lastmodified;

    public CustomerWrapper() {
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLoginPin() {
        return loginPin;
    }

    public void setLoginPin(String loginPin) {
        this.loginPin = loginPin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return ""+name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getCreatedbyId() {
        return createdbyId;
    }

    public void setCreatedbyId(String createdbyId) {
        this.createdbyId = createdbyId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getLastmodified() {
        return lastmodified;
    }

    public void setLastmodified(Date lastmodified) {
        this.lastmodified = lastmodified;
    }

    public boolean isGave() {
        return gave;
    }

    public void setGave(boolean gave) {
        this.gave = gave;
    }

    public boolean isGot() {
        return got;
    }

    public void setGot(boolean got) {
        this.got = got;
    }

    @Override
    public int compareTo(CustomerWrapper o) {
        int result=-1;
        if(o.getLastmodified()!=null && this.getLastmodified()!=null){
            result=this.getLastmodified().compareTo(o.getLastmodified());
        }
        return result;
    }

}
