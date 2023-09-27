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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddfromcontactsActivity extends BaseActivity {
    ListView listView ;
    public  List<String> contacts;
    public List<String> scontacts;
    Button addcust_addcust_button;
    public List<Customer> customerslist;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public FirebaseDatabaseHandler dbhanlder;
    public Company company;
    ArrayList<String> StoreContacts ;
    ArrayAdapter<String> arrayAdapter ;
    Cursor cursor ;
    String name, phonenumber ;
    public  static final int RequestPermissionCode  = 1 ;
    Toolbar addcustbar;
    public TextInputEditText addc_typename;
    Context ctxt;
    //  Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfromcontacts);
        ctxt=this;
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
              //  backbuttonDialog(ctxt);
                Intent in = new Intent(AddfromcontactsActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        contacts =new ArrayList<>();
        scontacts=new ArrayList<>();
        addcustbar=findViewById(R.id.afc_addcustbar);
        setSupportActionBar(addcustbar);
        dbhanlder= SharedData.getDbhandler();
        if(dbhanlder==null){
            dbhanlder=new FirebaseDatabaseHandler();
        }
        addc_typename=findViewById(R.id.afc_typename);
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
        listView = (ListView)findViewById(R.id.afc_contacts_listview);
        addcust_addcust_button=findViewById(R.id.afc_newbutton);
        addcust_addcust_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(AddfromcontactsActivity.this,CreatecustomerActivity.class);
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
                contacts.clear();
                for(String cw:scontacts){
                    String name=cw.split("\n")[0];
                    if((name.toLowerCase().trim()).startsWith(textdata)){
                        contacts.add(cw);
                    }
                }
                arrayAdapter = new ArrayAdapter<String>(
                        AddfromcontactsActivity.this,
                        R.layout.contact_items_listview,
                        R.id.contact_name, contacts
                );

                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        };
        addc_typename.addTextChangedListener(watcher);

    }
    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            contacts=getContactNames();
            if(contacts!=null) {
                scontacts.addAll(contacts);
            }
            Log.d("contact",""+contacts);

            arrayAdapter = new ArrayAdapter<String>(
                    AddfromcontactsActivity.this,
                    R.layout.contact_items_listview,
                    R.id.contact_name, contacts
            );

            listView.setAdapter(arrayAdapter);
            // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);
            //  listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = (String) parent.getItemAtPosition(position);
                    Log.d("selectedItem",""+selectedItem);
                    Intent intent = new Intent(AddfromcontactsActivity.this,CreatecustomerActivity.class);
                    intent.putExtra("selectedItem", selectedItem);
                    //based on item add info to intent
                    startActivity(intent);
                    // textView.setText("The best football player is : " + selectedItem);
                }
            });
        }
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
          //  backbuttonDialog(ctxt);
            Intent in = new Intent(AddfromcontactsActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }

}