package com.bchilakalapudi.rtrconstruction;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.CustomerWrapper;
import com.bchilakalapudi.rtrconstruction.model.Transaction;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;

import org.w3c.dom.Text;

import java.util.ArrayList;



//import com.squareup.picasso.Picasso;

public class CustomerAdapter implements ListAdapter {
    ArrayList<CustomerWrapper> arrayList;
    CustomerWrapper tr;
    Context context;
    TextView cname;
    TextView amount;
    TextView dblv_role;
    TextView yougavegot;
    public String loginuserId;
    public FirebaseDatabaseHandler dbhandler;

    public CustomerAdapter(Context context, ArrayList<CustomerWrapper> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
        dbhandler=SharedData.getDbhandler();
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        loginuserId=SharedData.getLoginuserId();
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
        Log.d("position",""+position);
        tr = arrayList.get(position);

        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.dashboard_clview, null);
            /*convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("customer",tr.toString());
                    Intent intent = new Intent(context,CustomerTransactionEntryActivity.class);
                    intent.putExtra("customerId", tr.getCustomerId());
                    //based on item add info to intent
                    context.startActivity(intent);
                }
            });*/
             cname = convertView.findViewById(R.id.dblv_customername);
             amount=convertView.findViewById(R.id.dblv_amounts);
             dblv_role=convertView.findViewById(R.id.dblv_role);
             yougavegot=convertView.findViewById(R.id.dblv_yougavegot);
          //  cname.setText(tr.getCustomer_name()+"\n 0 Seconds Ago");
           /* Picasso.with(context)
                    .load(subjectData.Image)
                    .into(imag);
*/
           cname.setText(""+tr.getName());
            amount.setText("₹" + tr.getBalance());

                if (tr.isGot()) {
                    yougavegot.setText("You Got");
                    yougavegot.setTextColor(Color.parseColor("#4CAF50"));
                    amount.setTextColor(Color.parseColor("#4CAF50"));
                } else {
                    yougavegot.setText("You Gave");
                    yougavegot.setTextColor(Color.parseColor("#FF0000"));
                    amount.setTextColor(Color.parseColor("#FF0000"));
                }

           //set admin text
           if(tr.isAdmin())
            dblv_role.setText("admin");
           else
               dblv_role.setText("");
           loginuserId= SharedData.getLoginuserId();
        }
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        int retsize=1;
        if(arrayList.size()>1)
            retsize=arrayList.size();
        return retsize;
    }
    @Override
    public boolean isEmpty() {
        return false;
    }


}