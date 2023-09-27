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
import android.widget.Switch;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class EditcustomerActivity extends BaseActivity {
    public TextInputEditText cname;
    public TextInputEditText phone;
    public TextInputEditText pin;
    public TextInputEditText cpin;
    public Switch stch;
    public Toolbar tbar;
    public String customerId;
    public FirebaseDatabaseHandler dbhandler;
    public Customer customer;
    public Button editc_savebtn;
    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcustomer);
        dbhandler= SharedData.getDbhandler();
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        context=this;
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
               // backbuttonDialog(context);
                Intent in = new Intent(EditcustomerActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        Intent in=getIntent();
        customerId=in.getStringExtra("customerId");
        customer=dbhandler.getCustomer(customerId);
        cname=findViewById(R.id.editc_name);
        phone=findViewById(R.id.editc_phone);
        pin=findViewById(R.id.editc_pin);
//        cpin=findViewById(R.id.editc_cpin);
        editc_savebtn=findViewById(R.id.editc_savebtn);
        stch=findViewById(R.id.editc_sw);
        tbar=findViewById(R.id.editc_tbar);
        setSupportActionBar(tbar);
        cname.setText(""+customer.getName());
        String phonetxt=""+customer.getPhone();
        if(phonetxt.contains("+91"))
            phonetxt=phonetxt.replace("+91","");
        phone.setText(phonetxt);
        pin.setText(""+customer.getLoginPin());
     //   cpin.setText(""+customer.getLoginPin());
        if(customer.isAdmin()){
            stch.setChecked(true);
        }
        else{
            stch.setChecked(false);
        }
        tbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(EditcustomerActivity.this,CustomerTransactionEntryActivity.class);
                in.putExtra("customerId",""+customerId);
                startActivity(in);
            }
        });
        //save btn clicked
        editc_savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customer.setName(""+cname.getText());
                customer.setPhone("+91"+phone.getText());
                customer.setLoginPin(""+pin.getText());
                customer.setAdmin(stch.isChecked());
                customer.lastmodified= Calendar.getInstance().getTime();
                if(customer.isAdmin()){
                    customer.createdbyId=SharedData.loginuserId;
                }
                dbhandler.writeUser(customerId,customer);
                SharedData.selectedCompanyId=null;
                Intent in=new Intent(EditcustomerActivity.this,DashboardActivity.class);
              //  in.putExtra("customerId",""+customer.getId());
                startActivity(in);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           // backbuttonDialog(context);
            Intent in = new Intent(EditcustomerActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }

}