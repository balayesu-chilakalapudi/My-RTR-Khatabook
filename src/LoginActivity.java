package com.bchilakalapudi.rtrconstruction;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Company;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoginActivity extends BaseActivity {
Toolbar latoolbar;
Button genpintxt;
Button signinbtn;
public FirebaseDatabaseHandler dbhandler;
TextInputEditText login_pintxt;
public AppCompatAutoCompleteTextView login_phone;
List<Customer> userlist;
List<Company> companies;

public ArrayAdapter<String> adapter;
public List<String> phones;
public String account;
public Context ctxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbhandler=SharedData.dbhandler;
        ctxt=this;
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
              //  backbuttonDialog(ctxt);
                Intent in = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        if(dbhandler!=null) {
            userlist = dbhandler.getUserlist();
            companies = dbhandler.getCompanies();
        }else{
            dbhandler=new FirebaseDatabaseHandler();
            userlist = dbhandler.getUserlist();
            companies = dbhandler.getCompanies();
        }
        Log.d("loginact>userlist",""+userlist);
        Intent in=getIntent();
        account=SharedData.getAccount();
        Log.d("account",""+account);
        SharedData.account=account;
        latoolbar=findViewById(R.id.latoolbar);
        latoolbar.setTitle(account);
        setSupportActionBar(latoolbar);
      /*  if(account.equals("company")){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        }else{
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0D6E0D")));
        }*/

        login_phone=findViewById(R.id.login_phone);
        phones=new ArrayList<>();
        Set<String> dupphones=new HashSet<>();
        if(account!=null &&
                account.equals("customer")) {
            for (Customer c : userlist) {
                if (c.getPhone() != null &&
                        !c.getPhone().equals(" ") &&
                        !c.getPhone().equals("") &&
                        !dupphones.contains(c.getPhone())) {
                    dupphones.add(c.getPhone());
                    String p = c.getPhone();
                    if (p.contains("+91")) {
                        p = p.replace("+91", "");
                    }
                    p = p.trim();
                    phones.add(p);

                }
            }
        }else {
            for (Company c : companies) {
                if (c.getPhone() != null &&
                        !c.getPhone().equals(" ") &&
                        !c.getPhone().equals("") &&
                        !dupphones.contains(c.getPhone())) {
                    dupphones.add(c.getPhone());
                    String p = c.getPhone();
                    if (p.contains("+91")) {
                        p = p.replace("+91", "");
                    }
                    p = p.trim();
                    phones.add(p);
                }
            }
        }
        Log.d("phones",""+phones);
        String[] phonestr=new String[phones.size()];
        for(int x=0;x<phones.size();x++){
            phonestr[x]=phones.get(x);
        }
        adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, phonestr);
        login_phone.setThreshold(1); //will start working from first character
        login_phone.setAdapter(adapter);
        latoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intn=new Intent(getBaseContext(), MainActivity.class);
                startActivity(intn);
                //Toast.makeText(getApplicationContext(),"your icon was clicked",Toast.LENGTH_SHORT).show();
            }
        });
        genpintxt=findViewById(R.id.genpintxt);
        genpintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intn=new Intent(getBaseContext(), GeneratepinActivity.class);
                startActivity(intn);
            }
        });

        login_pintxt=findViewById(R.id.login_pintxt);

        signinbtn=findViewById(R.id.login_signinbtn);
        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin=""+login_pintxt.getText();
                String phone="+91"+login_phone.getText();

                        Log.d("Login","userlist>"+userlist);
                boolean flag=false;
                if(account!=null &&
                        account.equals("customer")) {
                    for (Customer u : userlist) {
                        Log.d("pin", "" + u.getLoginPin());
                        Log.d("phone", "" + u.getPhone());
                        if (pin.equals(u.getLoginPin()) &&
                                phone.equals(u.getPhone())) {
                            flag = true;
                            Intent in = new Intent(LoginActivity.this, DashboardActivity.class);
                            SharedData.loginuserId = u.getId();
                            SharedData.account="customer";
                            //in.putExtra("userobj",u);
                            startActivity(in);
                            break;
                        }
                    }
                }else if(account.equals("company")){
                    for (Company u : companies) {
                        Log.d("pin", "" + u.getLoginPin());
                        Log.d("phone", "" + u.getPhone());
                        if (pin.equals(u.getLoginPin()) &&
                                phone.equals(u.getPhone())) {
                            flag = true;
                            Intent in = new Intent(LoginActivity.this, DashboardActivity.class);
                            SharedData.logincompanyId = u.getId();
                            SharedData.account="company";
                            startActivity(in);
                            break;
                        }
                    }
                }
                if(!flag){
                    login_pintxt.setError("Invalid PIN number");
                   // login_phone.setError("Invalid Credentials");
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          //  backbuttonDialog(ctxt);
            Intent in = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }

}