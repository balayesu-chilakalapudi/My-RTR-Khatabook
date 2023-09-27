package com.bchilakalapudi.rtrconstruction;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class OpenattachmentActivity extends BaseActivity {
    public ImageView openimg;
    public Toolbar tbar;
    public String transactionId;
    public Context ctxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openattachment);
        ctxt=this;
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
               // backbuttonDialog(ctxt);
                Intent in = new Intent(OpenattachmentActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        Intent in=getIntent();
        transactionId=in.getStringExtra("transactionId");
        Log.d("transactionId",""+transactionId);
        String imageurl=in.getStringExtra("imageurl");
        openimg=findViewById(R.id.open_imgvw);
        tbar=findViewById(R.id.open_tbar);
        setSupportActionBar(tbar);
        tbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(OpenattachmentActivity.this,TransactiondetailsActivity.class);
                in.putExtra("transactionId",transactionId);
                startActivity(in);
            }
        });

        Picasso.with(this)
                .load(imageurl)
                .into(openimg);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
         //   backbuttonDialog(ctxt);
            Intent in = new Intent(OpenattachmentActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }
}