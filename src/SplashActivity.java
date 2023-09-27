package com.bchilakalapudi.rtrconstruction;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;

import java.util.List;

public class SplashActivity extends BaseActivity {
    public FirebaseDatabaseHandler dbhandler;
    public List<Customer> userlist;
    public Context ctxt;
    public Button sbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sbtn=findViewById(R.id.sp_start_btn);
        dbhandler=SharedData.getDbhandler();
        if(dbhandler==null) {
            dbhandler = new FirebaseDatabaseHandler();
        }
        ctxt=this;
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
        Log.d("splash>ulist",""+userlist);
        SharedData.dbhandler=dbhandler;

        sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        //full screen
       // requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
     //   getSupportActionBar().hide(); // hide the title bar
   ////     this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
           //     WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

/*
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
            }
        }, 10000);*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           backbuttonDialog(ctxt);
        }
        return super.onKeyDown(keyCode, event);
    }

}