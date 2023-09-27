package com.bchilakalapudi.rtrconstruction;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Attachment;
import com.bchilakalapudi.rtrconstruction.model.Transaction;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactiondetailsActivity extends BaseActivity {
    public String transactionId;
    public Toolbar td_tbar;
    public TextView sender;
    public TextView receiver;
    public TextView tdate;
    public TextView amount;
    public Button editbtn;
    public Button delbtn;
    public ImageView imag;
    public TextView details;
    public FirebaseDatabaseHandler dbhandler;
    public Transaction transactionobj;
    public Customer senderobj;
    public Customer receiverobj;
    public Customer loginuser;
    public String loginuserId;
    public String senderId;
    public String receiverId;
    public Attachment att;
    public ArrayList<Attachment> attlist;
    public ListView vwattlvw;
    public ViewAttachmentAdapter vadapter;
    public TextView td_helptxt;
    public Context ctxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactiondetails);
        dbhandler = SharedData.dbhandler;
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
                Intent in = new Intent(TransactiondetailsActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        td_tbar = findViewById(R.id.td_tbar);
        setSupportActionBar(td_tbar);
        Intent in = getIntent();
        transactionId = in.getStringExtra("transactionId");
        transactionobj=dbhandler.getTransaction(transactionId);
        senderId = transactionobj.getSenderId();
        receiverId = transactionobj.getReceiverId();
        loginuser=dbhandler.getCustomer(SharedData.loginuserId);
        editbtn = findViewById(R.id.td_editbtn);
        delbtn=findViewById(R.id.td_delbtn);
        td_helptxt=findViewById(R.id.td_helptxt);
        if(loginuser!=null &&
                !loginuser.isAdmin()){

            Date sdt=transactionobj.getCreateddate();
            Date edt=new Date();
            long hrs=0;
            if(sdt!=null)
                hrs=dbhandler.getDateDifferenceHours(sdt,edt);
            Log.d("hrs",""+hrs);
            if(hrs>0) {
                editbtn.setVisibility(View.GONE);
                delbtn.setVisibility(View.GONE);
                td_helptxt.setVisibility(View.GONE);
            }
        }else{

        }
        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete transaction
                if(transactionId!=null) {
                    dbhandler.removeTransaction(transactionId);
                    //remove attachments
                    List<Attachment> attlst=new ArrayList<Attachment>();
                    for(Attachment att:dbhandler.getAttachments()){
                        if(att.getId()!=null &&
                                att.getTransactionId()!=null &&
                                transactionId!=null &&
                                att.getTransactionId().equals(transactionId)){
                            dbhandler.removeAttachment(att.getId());
                            dbhandler.deleteStorageFile(att.getId());
                        }
                    }
                    Intent in=new Intent(TransactiondetailsActivity.this,DashboardActivity.class);
                    startActivity(in);
                }
            }
        });
        Log.d("transactionId",""+transactionId);
        attlist=new ArrayList<>();
        for(Attachment att:dbhandler.getAttachments()){
            if(att!=null &&
                    transactionId!=null &&
                    att.getTransactionId()!=null &&
                    att.getTransactionId().equals(transactionId)){
                attlist.add(att);
            }
        }
        Log.d("attlist",""+attlist.size());
        vwattlvw=findViewById(R.id.td_attlistvw);
        vadapter=new ViewAttachmentAdapter(this,attlist,vwattlvw);
        vwattlvw.setAdapter(vadapter);


        td_tbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(TransactiondetailsActivity.this, DashboardActivity.class);
                //   in.putExtra("customerId",customerId);
                startActivity(in);
            }
        });

        Log.d("transactionId", "" + transactionId);
        Log.d("senderId", "" + senderId);
        Log.d("receiverId", "" + receiverId);
        transactionobj = dbhandler.getTransaction(transactionId);
        senderobj = dbhandler.getCustomer(senderId);
        receiverobj = dbhandler.getCustomer(receiverId);

        sender = findViewById(R.id.td_sender);
        tdate = findViewById(R.id.td_date);
        amount = findViewById(R.id.td_amount);
        receiver = findViewById(R.id.td_receiver);


//        imag=findViewById(R.id.td_photo);
        details = findViewById(R.id.td_details);

        loginuserId = SharedData.getLoginuserId();
        Log.d("att", "" + att);

        if (senderobj != null) {
            sender.setText("" + senderobj.getName());
        }
        if (receiverobj != null) {
            receiver.setText("" + receiverobj.getName());
        }
        String dt=""+transactionobj.getDate();
        tdate.setText("" + dt.split("GMT")[0]);
        amount.setText("" + transactionobj.getAmount());
        details.setText("" + transactionobj.getDetails());

        editbtn.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick (View v){
        Intent in = new Intent(TransactiondetailsActivity.this, EditTransactionActivity.class);
        in.putExtra("transactionId", transactionId);
        startActivity(in);
    }
    });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          //  backbuttonDialog(ctxt);
            Intent in = new Intent(TransactiondetailsActivity.this, DashboardActivity.class);
          //  in.putExtra("customerId",customerId);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }

}


