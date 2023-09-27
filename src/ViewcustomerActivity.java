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
import com.bchilakalapudi.rtrconstruction.model.Company;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.model.Transaction;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ViewcustomerActivity extends BaseActivity {
    public String customerId;
    public Customer customer;
    public FirebaseDatabaseHandler dbhandler;
    public TextInputEditText viewc_name;
    public TextInputEditText viewc_phone;
    public TextInputEditText viewc_pin;
    public Switch viewc_sw;
    public Button viewc_edit;
    public Toolbar viewc_tbar;
    public Customer loginuser;
    public Button viewc_delete;
    public Company company;
    public Context ctxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewcustomer);
        dbhandler= SharedData.getDbhandler();
        ctxt=this;
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                //backbuttonDialog(ctxt);
                Intent in = new Intent(ViewcustomerActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        Intent in=getIntent();
        customerId=in.getStringExtra("customerId");
        customer=dbhandler.getCustomer(customerId);
        loginuser=dbhandler.getCustomer(SharedData.loginuserId);
        viewc_tbar=findViewById(R.id.viewc_tbar);
        company=dbhandler.getCompany(SharedData.selectedCompanyId);
        setSupportActionBar(viewc_tbar);
        //initialize views
        viewc_name=findViewById(R.id.viewc_name);
        viewc_phone=findViewById(R.id.viewc_phone);
        viewc_pin=findViewById(R.id.viewc_pin);
        viewc_sw=findViewById(R.id.viewc_sw);
        viewc_edit=findViewById(R.id.viewc_edit);
        viewc_delete=findViewById(R.id.viewc_delete);
        if(loginuser!=null &&
                !loginuser.isAdmin()){
            viewc_edit.setVisibility(View.GONE);
            viewc_delete.setVisibility(View.GONE);
        }
        //set values
        viewc_name.setText(""+customer.getName());
        String phone=""+customer.getPhone();
        if(phone.contains("+91"))
            phone=phone.replace("+91","");
        viewc_phone.setText(phone);
        viewc_pin.setText(""+customer.getLoginPin());
        if(customer.isAdmin()){
            viewc_sw.setChecked(true);
        }else{
            viewc_sw.setChecked(false);
        }
        viewc_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent in=new Intent(ViewcustomerActivity.this,EditcustomerActivity.class);
               in.putExtra("customerId",customerId);
               startActivity(in);
        }
    });
        viewc_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete customer

                dbhandler.removeCustomer(customerId);
                // delete from comapany Id list
                ArrayList<String> cidlist=(ArrayList)company.getCustomerIdlist();
                for(int i=0;i<cidlist.size();i++){
                    if(customerId.equals(cidlist.get(i))){
                        cidlist.remove(i);
                        break;
                    }
                }
                company.customerIdlist=cidlist;
                dbhandler.writeCompany(company.getId(),company);
                //delete transactions
                for(Transaction t:dbhandler.getTransactions()){
                    if(t.getId()!=null &&
                            t.getSenderId()!=null &&
                            t.getSenderId().equals(customerId)) {
                        dbhandler.removeTransaction(t.getId());
                    }
                    else if(t.getId()!=null &&
                            t.getReceiverId()!=null &&
                            t.getReceiverId().equals(customerId)) {
                        dbhandler.removeTransaction(t.getId());
                    }
                }
                Intent in=new Intent(ViewcustomerActivity.this,DashboardActivity.class);
                startActivity(in);
            }
        });
        viewc_tbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(ViewcustomerActivity.this,CustomerTransactionEntryActivity.class);
                in.putExtra("customerId",""+customerId);
                startActivity(in);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           // backbuttonDialog(ctxt);
            Intent in = new Intent(ViewcustomerActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }
}