package com.bchilakalapudi.rtrconstruction;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;

import java.util.List;

public class MainActivity extends BaseActivity {
    public FirebaseDatabaseHandler dbhandler;
    Toolbar main_toolbar;
    List<Customer> userlist;
    public String loginuserId;
    public Context ctxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbhandler=SharedData.getDbhandler();
        ctxt=this;
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
               backbuttonDialog(ctxt);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        userlist=dbhandler.getUserlist();
        SharedData.loginuserId=null;
        SharedData.selectedCompanyId=null;
        SharedData.logincompanyId=null;
        Log.d("main>userlist",""+userlist);
/*
        //full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
       // getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
*/


       // main_toolbar=findViewById(R.id.main_toolbar);
       // main_toolbar.setTitle("RTR Construction");
      //  setSupportActionBar(main_toolbar);
        Button companytbtn=(Button)findViewById(R.id.main_company_btn);
        companytbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                SharedData.account="company";
                //based on item add info to intent
                startActivity(intent);
            }
        });

        Button customertbtn=(Button)findViewById(R.id.main_customer_btn);
        customertbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                SharedData.account="customer";
                //based on item add info to intent
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
         /*   case R.id.action_refresh:
              //  Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                //        .show();
                break;
            // action with ID action_settings was selected
          case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;*/
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           backbuttonDialog(ctxt);
        }
        return super.onKeyDown(keyCode, event);
    }
}