package com.bchilakalapudi.rtrconstruction;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Attachment;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.model.Transaction;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



//import com.squareup.picasso.Picasso;

public class TransactionAdapter implements ListAdapter {
    public ArrayList<Transaction> arrayList;
    public Transaction tr;
    public Context context;
    public TextView datestamp;
    public TextView description;
    public TextView yougavtamt;
    public TextView yougotamt;
    public ImageView imag1;
    public ImageView imag2;
    public ImageView imag3;
    public ImageView imag4;
    public ImageView imag5;
    public String customerId;
    public TextView attsize;
    public TextView cname;
    public FirebaseDatabaseHandler dbhandler;
    public TransactionAdapter(Context context, ArrayList<Transaction> arrayList,String customerId) {
        this.arrayList=arrayList;
        this.context=context;
        this.customerId=customerId;
        dbhandler= SharedData.getDbhandler();
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
    }
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        tr = arrayList.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.ctentries_clview, null);
            //list view item clicked
        /*    convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("customer entry",tr.toString());
                 //   Intent intent = new Intent(context,CustomerTransactionEntryActivity.class);
                 //   intent.putExtra("customerId", tr.getCustomerId());
                    //based on item add info to intent
                  //  context.startActivity(intent);
                }
            });*/
             datestamp = convertView.findViewById(R.id.cte_datetime);
             description=convertView.findViewById(R.id.cte_description);
             yougavtamt=convertView.findViewById(R.id.cte_gave_amt);
             yougotamt=convertView.findViewById(R.id.cte_yougot_amt);
             cname=convertView.findViewById(R.id.cte_cname);
         //    attsize=convertView.findViewById(R.id.cte_attsize);
          //  cname.setText(tr.getCustomer_name()+"\n 0 Seconds Ago");
            imag1=convertView.findViewById(R.id.cte_billimg_1);
            imag2=convertView.findViewById(R.id.cte_billimg_2);
            imag3=convertView.findViewById(R.id.cte_billimg_3);
            imag4=convertView.findViewById(R.id.cte_billimg_4);
            imag5=convertView.findViewById(R.id.cte_billimg_5);
            int counter=1;
            for(Attachment att:dbhandler.getAttachments()){
                if(att.getTransactionId()!=null &&
                        tr.getId()!=null &&
                        att.getTransactionId().equals(tr.getId())){
                    if(counter==1){
                        imag1.setVisibility(View.VISIBLE);
                        Picasso.with(context)
                                .load(att.getDownloadUrl())
                                .into(imag1);
                    }
                    if(counter==2){
                        imag2.setVisibility(View.VISIBLE);
                        Picasso.with(context)
                                .load(att.getDownloadUrl())
                                .into(imag2);
                    }
                    if(counter==3){
                        imag3.setVisibility(View.VISIBLE);
                        Picasso.with(context)
                                .load(att.getDownloadUrl())
                                .into(imag3);
                    }
                    if(counter==4){
                        imag4.setVisibility(View.VISIBLE);
                        Picasso.with(context)
                                .load(att.getDownloadUrl())
                                .into(imag4);
                    }
                    if(counter==5){
                        imag5.setVisibility(View.VISIBLE);
                        Picasso.with(context)
                                .load(att.getDownloadUrl())
                                .into(imag5);
                        break;
                    }
                    counter++;
                }
            }

            String dt=String.valueOf(tr.getDate());
            dt=dt.split("GMT")[0];
           datestamp.setText(""+dt);
           description.setText(tr.getDetails());

           if(tr.getSenderId().equals(customerId)){
               yougavtamt.setText("₹ "+tr.getAmount());
               yougotamt.setText("");
               if(tr.receiverId!=null) {
                   if(dbhandler==null){
                       dbhandler=new FirebaseDatabaseHandler();
                   }
                   Customer cs=dbhandler.getCustomer(tr.receiverId);
                   if(cs!=null){
                       cname.setText("" + cs.getName());
                   }else {
                       cname.setText("");
                   }
               }
           }else{
               yougotamt.setText("₹ "+tr.getAmount());
               yougavtamt.setText("");
               if(tr.senderId!=null) {
                   try {
                       cname.setText("" + dbhandler.getCustomer(tr.senderId).getName());
                   }catch(Exception ex){
                       ex.printStackTrace();
                   }
               }
           }
        //   attsize.setText(""+getAttSize(tr.getId()));
        /*   if(!tr.getYougaveamt().equals(""))
             yougavtamt.setText("₹ "+tr.getYougaveamt());
           else
               yougavtamt.setText("");

           if(!tr.getYougotamt().equals(""))
               yougotamt.setText("₹ "+tr.getYougotamt());
            else
               yougotamt.setText("");*/
          //  TextView quantity = convertView.findViewById(R.id.quantity);
          //  quantity.setText(""+subjectData.Quantity);
        }
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        int size=1;
        if(arrayList!=null && arrayList.size()>1) {
            size = arrayList.size();
        }
        return size;
    }
    @Override
    public boolean isEmpty() {
        return false;
    }
    public int getAttSize(String transactionId){
        int size=0;
        for(Attachment att:dbhandler.getAttachments()){
            if(att!=null &&
                    transactionId!=null &&
                    att.getTransactionId()!=null &&
                    att.getTransactionId().equals(transactionId)){
                size++;
            }
        }
        return size;
    }
}