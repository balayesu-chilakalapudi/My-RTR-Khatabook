package com.bchilakalapudi.rtrconstruction;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class GeneratepinActivity extends BaseActivity {
Toolbar latoolbar;
public String account;
Button genpinbtn;
TextInputEditText genpin_name;
TextInputEditText genpin_phone;
TextInputEditText genpin_pin;
TextInputEditText genpin_cpin;
public FirebaseDatabaseHandler dbhandler;
public List<Customer> userlist;
public Context ctxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generatepin);
        dbhandler= SharedData.dbhandler;
        ctxt=this;
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
               // backbuttonDialog(ctxt);
                Intent in = new Intent(GeneratepinActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        userlist=dbhandler.getUserlist();
        Intent in=getIntent();
        account=SharedData.getAccount();
        latoolbar=findViewById(R.id.genpin_tbar);
        genpin_name=findViewById(R.id.genpin_name);
        genpin_phone=findViewById(R.id.genpin_phone);
        genpin_pin=findViewById(R.id.genpin_pin);
//        genpin_cpin=findViewById(R.id.genpin_cpin);

        latoolbar.setTitle(account);
        setSupportActionBar(latoolbar);
        latoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intn=new Intent(getBaseContext(), LoginActivity.class);
                intn.putExtra("account",account);
                startActivity(intn);
                //Toast.makeText(getApplicationContext(),"your icon was clicked",Toast.LENGTH_SHORT).show();
            }
        });
        genpinbtn=findViewById(R.id.genpin_btn);
        genpinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intn=new Intent(getBaseContext(), PhoneAuthActivity.class);
                intn.putExtra("USER_TYPE",account);
                intn.putExtra("NAME",""+genpin_name.getText());
                intn.putExtra("PIN",""+genpin_pin.getText());
                intn.putExtra("PHONE",""+genpin_phone.getText());
                startActivity(intn);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           // backbuttonDialog(ctxt);
            Intent in = new Intent(GeneratepinActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }

}