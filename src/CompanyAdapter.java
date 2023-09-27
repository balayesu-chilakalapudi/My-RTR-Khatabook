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
import com.bchilakalapudi.rtrconstruction.shared.SharedData;

import java.util.ArrayList;

public class CompanyAdapter implements ListAdapter {
    public ArrayList<Company> cwlist;
    public Company cw;
    public Context context;
    public TextView companyname;
    public TextView numcustomers;
    public FirebaseDatabaseHandler dbhandler;

    public CompanyAdapter(Context context, ArrayList<Company> arrayList) {
        Log.d("CompanyAdapter",""+arrayList);
        this.cwlist=arrayList;
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
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return cwlist.size();
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
        cw = cwlist.get(position);
      //  cw=dbhandler.getCompany(cw.getId());
        Log.d("cw",""+cw);
        Log.d("convertView",""+convertView);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.companylist, null);
            companyname = convertView.findViewById(R.id.cw_name);
            numcustomers=convertView.findViewById(R.id.cw_numcustomers);
            companyname.setText(""+cw.getName());
            if(cw.getCustomerIdlist()!=null)
                numcustomers.setText(""+cw.getCustomerIdlist().size()+" customers");
            else
                numcustomers.setText("0 customers");

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("position",""+position);
                    cw = cwlist.get(position);
                    Intent intent = new Intent(context,DashboardActivity.class);
                    SharedData.selectedCompanyId=cw.getId();
                    context.startActivity(intent);
                }
            });
            }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        int retsize=1;
        if(cwlist.size()>1)
            retsize=cwlist.size();
        return retsize;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
