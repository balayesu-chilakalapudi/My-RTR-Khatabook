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
import android.widget.TextView;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Transaction;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

public class CreatecustomerActivity extends BaseActivity {
    Toolbar cctbar;
    Button createcustomer_btn;
    Customer newuser;
    Customer loginuser;
    Transaction tr;
    TextInputEditText cname;
    TextInputEditText phone;
    TextInputEditText pin;
    TextInputEditText cpin;
    public FirebaseDatabaseHandler dbhandler;
    public List<Customer> userlist;
    Switch admin_switch;
    public TextView admintxt;
    public Context ctxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createcustomer);
        ctxt=this;
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
              //  backbuttonDialog(ctxt);
                Intent in = new Intent(CreatecustomerActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        dbhandler=SharedData.dbhandler;
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        userlist=dbhandler.getUserlist();
        newuser=new Customer();
        final String loginuserId=SharedData.getLoginuserId();
        loginuser= dbhandler.getCustomer(loginuserId);
        cctbar=findViewById(R.id.createcust_tbar);
        setSupportActionBar(cctbar);

        cctbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(CreatecustomerActivity.this,DashboardActivity.class);
                startActivity(in);
            }
        });

        cname=findViewById(R.id.createcust_name);
        phone=findViewById(R.id.createcust_phone);
        pin=findViewById(R.id.createcust_pin);
//        cpin=findViewById(R.id.createcust_cpin);
        admin_switch=findViewById(R.id.cc_admin_sw);
        admintxt=findViewById(R.id.cc_amintxt);

        if(loginuser!=null &&
                !loginuser.isAdmin()){
            admin_switch.setVisibility(View.GONE);
            admintxt.setVisibility(View.GONE);
        }

        Intent in=getIntent();
        try {
            String selectedcontact = in.getStringExtra("selectedItem");
            if(selectedcontact!=null && selectedcontact.contains("\n")){
                String[] data=selectedcontact.split("\n");
                cname.setText(""+data[0]);
                String phonetxt=""+data[1];
                phonetxt=phonetxt.trim();
                if(phonetxt.contains("+91"))
                    phonetxt=phonetxt.replace("+91","");
                phone.setText(phonetxt);
               // phone.setText(""+data[1]);
            }
        }catch(Exception ex){
            Log.d("Exception",""+ex);
        }

        newuser.admin=false;
        if (admin_switch != null) {
          //  admin_switch.setOnCheckedChangeListener(this);
        }

        createcustomer_btn=findViewById(R.id.createcust_btn);
        createcustomer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(admin_switch!=null) {
                    newuser.admin = admin_switch.isChecked();
                }else{
                    newuser.admin=false;
                }
                newuser.loginPin=""+pin.getText();
                newuser.name=""+cname.getText();
                newuser.phone="+91"+phone.getText();
                newuser.type="customer";
                newuser.createdbyId=SharedData.loginuserId;
                newuser.lastmodified= Calendar.getInstance().getTime();
             //   newuser.companyId=SharedData.selectedCompanyId;
                String newuserId=dbhandler.getRandomId(28);
                newuser.id=newuserId;
                Log.d("newuser",newuser.toString());
            /*    tr=new Transaction();
                tr.id=dbhandler.getRandomId(28);
                tr.receiverId=newuserId;
                tr.senderId=loginuserId;
                tr.amount=0;
                tr.details="NA";
                tr.date= Calendar.getInstance().getTime(); */
                //save new user
                dbhandler.writeUser(newuser.getId(),newuser);
                //dbhandler.writeTransaction(tr.getId(),tr);
                Intent in=new Intent(CreatecustomerActivity.this,DashboardActivity.class);
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
            Intent in = new Intent(CreatecustomerActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }

}