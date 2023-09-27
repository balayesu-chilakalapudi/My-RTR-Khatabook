package com.bchilakalapudi.rtrconstruction;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Company;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddcustomerActivity extends BaseActivity {
    ListView listView ;
    public  List<String> contacts;
    public Context context;
    Button addcust_addcust_button;
    public List<Customer> customerslist;
    ArrayList<Customer> cwlist;
    ArrayList<Customer> scwlist;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public FirebaseDatabaseHandler dbhanlder;
    public Company company;
    ArrayList<String> StoreContacts ;
    AddCustomerAdapter adapter ;
    Cursor cursor ;
    String name, phonenumber ;
    public  static final int RequestPermissionCode  = 1 ;
    Toolbar addcustbar;
    public TextInputEditText addc_typename;
    public Button addcontact;
  //  Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcustomer);
        context=this;
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
              //  backbuttonDialog(context);
                Intent in = new Intent(AddcustomerActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        //  contacts =new ArrayList<>();
        cwlist=new ArrayList<>();
        scwlist=new ArrayList<>();
        addcustbar=findViewById(R.id.addcustbar);
        setSupportActionBar(addcustbar);
        dbhanlder= SharedData.getDbhandler();
        if(dbhanlder==null){
            dbhanlder=new FirebaseDatabaseHandler();
        }
        addc_typename=findViewById(R.id.addc_typename);
        addcontact=findViewById(R.id.addcontact_button);
        company=dbhanlder.getCompany(SharedData.selectedCompanyId);
        customerslist=dbhanlder.getUserlist();
        addcustbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intn=new Intent(getBaseContext(), DashboardActivity.class);
                startActivity(intn);
                //Toast.makeText(getApplicationContext(),"your icon was clicked",Toast.LENGTH_SHORT).show();
            }
        });
        listView = (ListView)findViewById(R.id.addcustomer_contacts_listview);
        addcust_addcust_button=findViewById(R.id.addcust_addcust_button);
        addcust_addcust_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(AddcustomerActivity.this,CreatecustomerActivity.class);
                startActivity(in);
            }
        });
        showContacts();
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textdata = s.toString();
                textdata=textdata.trim();
                textdata=textdata.toLowerCase();
                Log.d("textdata",""+textdata);
             //   Log.d("scwlist",""+scwlist);
                cwlist.clear();
                for(Customer cw:scwlist){
                    if((cw.getName().toLowerCase().trim()).startsWith(textdata)){
                        cwlist.add(cw);
                    }
                }
                adapter = new AddCustomerAdapter(context, cwlist);
                listView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        };
        addc_typename.addTextChangedListener(watcher);
       /* addc_typename.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String textdata=""+addc_typename.getText();
                textdata=textdata.trim();
                textdata=textdata.toLowerCase();
                Log.d("textdata",""+textdata);
                Log.d("scwlist",""+scwlist);
                cwlist.clear();
               for(CustomerWrapper cw:scwlist){
                   if(cw.getCustomerName().toLowerCase().startsWith(textdata)){
                        cwlist.add(cw);
                   }
               }
                return false;
            }
        });*/
        addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(AddcustomerActivity.this,AddfromcontactsActivity.class);
                startActivity(in);
            }
        });
    }
    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
    /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {*/
            // Android version is lesser than 6.0 or the permission is already granted.

                    //getContactNames();
            Log.d("contact",""+contacts);
            //get existing customers too
            Set<String> cidset;
            if(company!=null && company.getCustomerIdlist()!=null) {
                cidset = new HashSet<String>(company.getCustomerIdlist());
            }else{
                cidset=new HashSet<>();
            }
            for(Customer c:customerslist){
                if(!cidset.contains(c.getId())){
                    Customer cw=new Customer();
                    cw.id=c.getId();
                    cw.name=c.getName();
                    cw.phone=c.getPhone();
                    cwlist.add(cw);
                }
            }
            scwlist.addAll(cwlist);
            adapter = new AddCustomerAdapter(this, cwlist);
            listView.setAdapter(adapter);
           // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);
          //  listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = (String) parent.getItemAtPosition(position);
                    Log.d("selectedItem",""+selectedItem);
                    Intent intent = new Intent(AddcustomerActivity.this,CreatecustomerActivity.class);
                    intent.putExtra("selectedItem", selectedItem);
                    //based on item add info to intent
                    startActivity(intent);
                   // textView.setText("The best football player is : " + selectedItem);
                }
            });
      //  }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Read the name of all the contacts.
     *
     * @return a list of names.
     */
    private List<String> getContactNames() {
        List<String> contacts = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver cr = getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor ;
                //= cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

            // Iterate through the cursor
            while (cursor.moveToNext()) {
                // Get the contacts name
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
              //  String hasnumber=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));
               //
              //  if(hasnumber!="0")
              phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.d("phonenumber",phonenumber);

                String constr =  name+" \n"+phonenumber;
                        //cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if(phonenumber!=null && (phonenumber.startsWith("+91") || phonenumber.length()==10)) {
                    phonenumber=phonenumber.trim();
                    if(phonenumber.contains("-"))
                        phonenumber=phonenumber.replace("-","");
                    if(phonenumber.contains(" "))
                        phonenumber=phonenumber.replace(" ","");
                    constr=name+" \n"+phonenumber;
                    contacts.add(constr);
                }
            }

        // Close the curosor
        cursor.close();

        return contacts;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          //  backbuttonDialog(context);
            Intent in = new Intent(AddcustomerActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }

}