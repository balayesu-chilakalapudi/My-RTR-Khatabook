package com.bchilakalapudi.rtrconstruction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Company;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    public ListView clistvw;
    public Button btn;
    public CompanyAdapter companyadapter ;
    public ArrayList<Company> cwlist;
    public FirebaseDatabaseHandler dbhandler;
    public List<Customer> customerlist;
    public ArrayList<Company> companylist;
    public Map<String,List<Customer>> cwmap;
    public Map<String,String> cmap;
    public String loginuserId;

    public BottomSheetFragment() {
        // Required empty public constructor
        dbhandler= SharedData.getDbhandler();
        loginuserId=SharedData.getLoginuserId();
        //customerlist=dbhandler.getUserlist();
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        companylist=(ArrayList)dbhandler.getCompanies();
        cwmap=new HashMap<String,List<Customer>>();
        cmap=new HashMap<String,String>();
        cwlist=new ArrayList<>();
        Log.d("companylist",""+companylist);
     /*   for(Company c:companylist){
            cmap.put(c.getId(),c.getName());
            cwmap.put(c.getId(),getCustomersByCompanyId(c.getCustomerIdlist()));
        }*/
      //  Log.d("customerlist",""+customerlist);
      /*  for(Customer cs:customerlist){
         //   if(!cwmap.containsKey(cs.getCompanyId()))
           //     cwmap.put(cs.getCompanyId(),new ArrayList<Customer>());
            if(!cs.getId().equals(loginuserId) &&
                    cwmap.containsKey(cs.getCompanyId())) {
                cwmap.get(cs.getCompanyId()).add(cs);
            }
        }*/
        for(String cId:cwmap.keySet()){
            Company cw=new Company();
            cw.id=cId;
            cw.name=cmap.get(cId);
            cwlist.add(cw);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                backbuttonDialog();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.bottom_sheet_modal, container, false);
        clistvw=(ListView)view.findViewById(R.id.bm_listViewOptions);
        btn=view.findViewById(R.id.bm_cncbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(getContext(),CreatecompanyActivity.class);
                startActivity(in);
            }
        });
      //  Log.d("cwlist",""+cwlist);
        Log.d("ocvcwlist",""+companylist);
        companyadapter = new CompanyAdapter(getContext(), companylist);
        clistvw.setAdapter(companyadapter);

     /*   clistvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  Snackbar.make(findViewById(R.id.parentLayout), list.get(position).getName() + " => " + list.get(position).getPhone(), Snackbar.LENGTH_LONG).show();
                CompanyWrapper cw = cwlist.get(position);
                Log.d("companyId",""+cw.getId());
                Intent intent = new Intent(getContext(), DashboardActivity.class);
                SharedData.selectedCompanyId=cw.getId();
                startActivity(intent);
            }
        }); */

        // Inflate the layout for this fragment
        return view;
    }

    public ArrayList<Customer> getCustomersByCompanyId(List<String> customerIdset){
        ArrayList<Customer> cslist=new ArrayList<>();
        if(customerIdset!=null) {
            Set<String> cidset=new HashSet<String>(customerIdset);
            for (Customer cs : customerlist) {
                if (cidset.contains(cs.getId())) {
                    cslist.add(cs);
                }
            }
        }
        return cslist;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        try {
           backbuttonDialog();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        // Add you codition
    }

    public void backbuttonDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                getContext());
        myAlertDialog.setTitle("Are you Sure?");
        myAlertDialog.setMessage("do you want to exit app?");

        myAlertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        myAlertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        try {
            myAlertDialog.show();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
