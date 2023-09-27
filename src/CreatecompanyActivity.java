package com.bchilakalapudi.rtrconstruction;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Company;
import com.bchilakalapudi.rtrconstruction.model.Transaction;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class CreatecompanyActivity extends BaseActivity {
    Toolbar cctbar;
    Button createcustomer_btn;
    Company newuser;
    Customer loginuser;
    Transaction tr;
    TextInputEditText cname;
    TextInputEditText phone;
    TextInputEditText pin;
    TextInputEditText cpin;
    public FirebaseDatabaseHandler dbhandler;
    public List<Customer> userlist;
    Switch admin_switch;
    Context ctxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createcompany);
        ctxt=this;
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
             //   backbuttonDialog(ctxt);
                Intent in = new Intent(CreatecompanyActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        dbhandler=SharedData.dbhandler;
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        userlist=dbhandler.getUserlist();
        newuser=new Company();
        final String loginuserId=SharedData.getLoginuserId();
        loginuser= dbhandler.getCustomer(loginuserId);
        cctbar=findViewById(R.id.createcust_tbar);
        setSupportActionBar(cctbar);

        cctbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(CreatecompanyActivity.this,DashboardActivity.class);
                startActivity(in);
            }
        });

        cname=findViewById(R.id.ccom_name);
        phone=findViewById(R.id.ccom_phone);
        pin=findViewById(R.id.ccom_pin);
//        cpin=findViewById(R.id.createcust_cpin);
      //  admin_switch=findViewById(R.id.cc_admin_sw);


       // newuser.admin=false;


        createcustomer_btn=findViewById(R.id.ccom_savebtn);
        createcustomer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newuser.loginPin=""+pin.getText();
                newuser.name=""+cname.getText();
                newuser.phone=""+phone.getText();
                String newuserId=dbhandler.getRandomId(28);
                newuser.id=newuserId;
                Log.d("newcompany",newuser.toString());

                //save new user
                dbhandler.writeCompany(newuser.getId(),newuser);
                //dbhandler.writeTransaction(tr.getId(),tr);
                Intent in=new Intent(CreatecompanyActivity.this,DashboardActivity.class);
                startActivity(in);
            }
        });
    }
   /* public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
       // Toast.makeText(this, "The Switch is " + (isChecked ? "on" : "off"),
         //       Toast.LENGTH_SHORT).show();
        if(isChecked) {
            newuser.isAdmin=true;
        } else {
            //do stuff when Switch if OFF
            newuser.isAdmin=false;
        }
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // backbuttonDialog(ctxt);
            Intent in = new Intent(CreatecompanyActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }

}