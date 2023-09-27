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
import com.bchilakalapudi.rtrconstruction.model.Company;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.model.Transaction;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//import com.squareup.picasso.Picasso;

public class AddCustomerAdapter implements ListAdapter {
    ArrayList<Customer> arrayList;
    Customer tr;
    Context context;
    TextView name;
    TextView phone;
    public String loginuserId;
    public FirebaseDatabaseHandler dbhandler;

    public AddCustomerAdapter(Context context, ArrayList<Customer> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
        dbhandler=SharedData.getDbhandler();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d("position",""+position);
        if(convertView == null) {
            tr = arrayList.get(position);
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.add_existing_customer, null);
            name = convertView.findViewById(R.id.aec_name);
            phone=convertView.findViewById(R.id.aec_phone);
            name.setText(""+tr.getName());
            phone.setText(""+tr.getPhone());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tr = arrayList.get(position);
                    Log.d("customer",tr.toString());
                    Intent intent = new Intent(context,DashboardActivity.class);
                    String selectedCompanyId=SharedData.selectedCompanyId;
                    Log.d("selectedCompanyId",""+selectedCompanyId);
                    Company c=dbhandler.getCompany(selectedCompanyId);
                    Set<String> customerIdset;
                    if(c!=null && c.getCustomerIdlist()!=null) {
                        customerIdset = new HashSet<>(c.getCustomerIdlist());
                    }else{
                        customerIdset=new HashSet<>();
                    }
                    customerIdset.add(tr.getId());
                    List<String> cidlist=new ArrayList<>();
                    cidlist.addAll(customerIdset);
                    if(c!=null) {
                        c.customerIdlist = cidlist;
                        dbhandler.writeCompany(c.getId(), c);
                    }
                 //   intent.putExtra("customerId", tr.getCustomerId());
                    //based on item add info to intent
                    context.startActivity(intent);
                }
            });

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