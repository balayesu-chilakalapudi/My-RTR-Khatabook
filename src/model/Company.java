package com.bchilakalapudi.rtrconstruction.model;

import java.util.List;
import java.util.Set;

public class Company {
    public String id;
    public String name;
    public String phone;
    public boolean admin; //admin or customer
    public String loginPin;
    public String type; //company or customer
    public String uid;   //phone verified Id
    public List<String> customerIdlist;
    public Company(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getCustomerIdlist() {
        return customerIdlist;
    }

    public void setCustomerIdlist(List<String> customerIdlist) {
        this.customerIdlist = customerIdlist;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", admin=" + admin +
                ", loginPin='" + loginPin + '\'' +
                ", type='" + type + '\'' +
                ", uid='" + uid + '\'' +
                ", customerIdset=" + customerIdlist +
                '}';
    }
}
