package com.bchilakalapudi.rtrconstruction;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Company;
import com.bchilakalapudi.rtrconstruction.model.Transaction;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DashboardActivity extends BaseActivity {
   public Toolbar dbtoolbar;
   public Customer userobj;
   public Company company;
   public ListView dbcustlistview;
    public CustomerAdapter adapter;
    public ArrayList<CustomerWrapper> cwlist;
    public ArrayList<CustomerWrapper> scwlist;
    public FirebaseDatabaseHandler dbhandler;
    public List<Customer> userlist;
    public Button addcustomer;
    public TextView dashb_custsize_txt;
    public TextView dash_gave_sum;
    public TextView dash_got_sum;
    public List<Transaction> transactions;
    public String account;
    public String logincompanyId;
    public TextView db_account;
    public TextView companytxt;
    public TextView db_home;
    public BottomSheetBehavior sheetBehavior;
    public LinearLayout layoutBottomSheet;
    public String selectedCompanyId;
    public List<Company> companies;
    public Button exppdf;
    public String loginuserId;
    private static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 100;
    public Customer loginuser;
    public List<Customer> companycustomers;
    public Context context;
    public static String TEMP_DIR_PATH;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Allowing Strict mode policy for Nougat support
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        TEMP_DIR_PATH=getApplicationContext().getFilesDir().getPath();

        dbhandler=SharedData.dbhandler;
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        context=this;
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                backbuttonDialog(context);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        loginuserId=SharedData.getLoginuserId();
        loginuser=dbhandler.getCustomer(SharedData.loginuserId);

        userlist=dbhandler.getUserlist();
        transactions=dbhandler.getTransactions();
        companies=dbhandler.getCompanies();
        Log.d("dbuserlist",""+userlist);
        account=SharedData.account;
        logincompanyId=SharedData.getLogincompanyId();
        companycustomers=new ArrayList<Customer>();
     /*   //full screen
       requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
//       getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
*/
        setContentView(R.layout.activity_dashboard);
        context=this;
      //  layoutBottomSheet=findViewById(R.id.bottom_sheet);
       // sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        dbcustlistview=findViewById(R.id.dashboard_clistvew);
        dashb_custsize_txt=findViewById(R.id.dashb_custsize_txt);

        dash_gave_sum=findViewById(R.id.dash_gave_sum);
        dash_got_sum=findViewById(R.id.dash_got_sum);
        dbtoolbar = (Toolbar) findViewById(R.id.db_tbar);
        db_account=findViewById(R.id.db_account);
        companytxt=findViewById(R.id.db_companytxt);
        exppdf=findViewById(R.id.db_pdfexp);
        companytxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
        db_home=findViewById(R.id.db_home);
        db_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(DashboardActivity.this,MainActivity.class);
                startActivity(in);
            }
        });
        String accname="";
        if(account!=null)
            accname=account.toUpperCase();
        db_account.setText(""+accname);
        //setting the title
      //  dbtoolbar.setTitle("RTR");
        //   toolbar.setTitleTextColor(Color.BLUE);
        Intent in=getIntent();
       // userobj=(User)in.getSerializableExtra("userobj");
        final String loginuserId=SharedData.getLoginuserId();
      //  if(account.equals("customer"))
        userobj= dbhandler.getCustomer(loginuserId);

        //userobj= SharedData.getUserobj();
        selectedCompanyId=SharedData.selectedCompanyId;
        if(selectedCompanyId==null){
               // prompt user to create company
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        }
        company=dbhandler.getCompany(selectedCompanyId);
        Set<String> customerIdset=new HashSet<>();
        Log.d("companytxt",""+company);
        if(company!=null) {
            companytxt.setText(("" + company.getName()));
            if(company.getCustomerIdlist()!=null) {
                customerIdset = new HashSet<String>(company.getCustomerIdlist());
            }
        }
        if(customerIdset==null){
            customerIdset=new HashSet<>();
        }
        cwlist=new ArrayList<>();
        scwlist=new ArrayList<>();
        double gavesum=0;
        double gotsum=0;

        if(userlist!=null) {
            Log.d("custlist",""+userlist.size());
            Set<String> custIdset=new HashSet<String>();
            Map<String, ArrayList<Transaction>> gavemap=new HashMap<>();
            Map<String, ArrayList<Transaction>> gotmap=new HashMap<>();


            for(Customer cs:userlist){
                if(customerIdset.contains(cs.getId())){
                    companycustomers.add(cs);
                }
            }
            // prepare  maps

              for(Customer customer:companycustomers) {

                  CustomerWrapper cw=new CustomerWrapper();
                  cw.lastmodified=customer.getLastmodified();
                  cw.id=customer.getId();
                  cw.name=customer.getName();
                  cw.admin=customer.isAdmin();
                  cw.phone=customer.getPhone();
                  double amt=0;
                 if(loginuser!=null &&
                          !loginuser.isAdmin()) {
                     Log.d("customerView",""+customer);
                     //Customer View
                      amt = dbhandler.getAmount(customer.getId());
                      Log.d("amt",""+amt);
                      if(!customer.getId().equals(loginuser.getId())) {
                          if (amt > 0) {
                              cw.gave = true;
                              cw.got = false;
                          } else {
                              cw.got = true;
                              cw.gave = false;
                          }
                      }else{
                          if (amt > 0) {
                              cw.got = true;
                              cw.gave = false;
                          } else {
                              cw.gave = true;
                              cw.got = false;
                          }
                      }
                  }
                  else{
                      //Admin View
                      amt=dbhandler.getAllAmount(customer.getId());
                      if(amt>0){
                          cw.got=true;
                          cw.gave=false;

                      }else {
                          cw.gave=true;
                          cw.got=false;
                      }
                  }

                  cw.balance=Math.abs(amt);
                  cwlist.add(cw);
            }

        }

        Log.d("cwlist",""+cwlist);
        gavesum=0;
        gotsum=0;
        Log.d("loginuserId",""+loginuserId);
        Log.d("selectedCompanyId",""+selectedCompanyId);
        Log.d("transactions",""+transactions);
        for (Transaction t : transactions) {
            if (t.getCompanyId()!=null && t.getCompanyId().equals(selectedCompanyId) && (loginuserId.equals(t.getSenderId()) ||
                    loginuserId.equals(t.getReceiverId()))) {
                if(t.getSenderId().equals(loginuser.getId())){
                    gavesum+=t.getAmount();
                }
                if(t.getReceiverId().equals(loginuser.getId())){
                    gotsum+=t.getAmount();
                }
            }
        }
        dash_gave_sum.setText("₹ "+Math.abs(gavesum));
        dash_got_sum.setText("₹ "+Math.abs(gotsum));
        Collections.sort(cwlist,Collections.reverseOrder());
        scwlist.addAll(cwlist);
        dashb_custsize_txt.setHint(""+cwlist.size()+" CUSTOMERS");
        adapter = new CustomerAdapter(this, cwlist);
        dbcustlistview.setAdapter(adapter);

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
                for(CustomerWrapper cw:scwlist){
                    if((cw.getName().toLowerCase().trim()).contains(textdata)){
                        cwlist.add(cw);
                    }
                }
                adapter = new CustomerAdapter(context, cwlist);
                dbcustlistview.setAdapter(adapter);
                dashb_custsize_txt.setHint(""+cwlist.size()+" CUSTOMERS");
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        };
        dashb_custsize_txt.addTextChangedListener(watcher);


        dbcustlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  Snackbar.make(findViewById(R.id.parentLayout), list.get(position).getName() + " => " + list.get(position).getPhone(), Snackbar.LENGTH_LONG).show();

                   CustomerWrapper fd = cwlist.get(position);
                   Log.d("customerId", "" + fd.getId());
                   String customerId=fd.getId();
                if((loginuser!=null &&
                        loginuser.isAdmin() &&
                        !(loginuser.createdbyId!=null &&
                                loginuser.createdbyId.equals(customerId))) ||
                        (customerId!=null &&
                        customerId.equals(loginuserId)) ||
                    SharedData.logincompanyId!=null) {
                   Intent intent = new Intent(getBaseContext(), CustomerTransactionEntryActivity.class);
                   intent.putExtra("customerId", fd.getId());
                   // intent.putExtra("user", userobj);
                   //based on item add info to intent
                   startActivity(intent);
               }else{
                    if(loginuser!=null &&
                            loginuser.createdbyId!=null &&
                            customerId!=null &&
                            loginuser.createdbyId.equals(customerId)){
                        Toast.makeText(getApplicationContext(), "Admin2 cannot see Admin1 transactions", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Admin access needed to view this account", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    if(userobj!=null) {
        dbtoolbar.setTitle(userobj.getName());
        dbtoolbar.setSubtitle(userobj.getPhone());
    }else if(company!=null){
        dbtoolbar.setTitle(company.getName());
        dbtoolbar.setSubtitle(company.getPhone());
    }
        setSupportActionBar(dbtoolbar);

        addcustomer= findViewById(R.id.dashboard_addcustomer_btn);
        addcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedData.selectedCompanyId!=null) {
                    Intent intent = new Intent(DashboardActivity.this, AddcustomerActivity.class);
                    // intent.putExtra("subjectData", subjectData);
                    //based on item add info to intent
                    startActivity(intent);
                }else{
                    Toast.makeText(getBaseContext(),"Please select company",Toast.LENGTH_LONG).show();
                }
            }
        });
        exppdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //export pdf
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    savePdf();
                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     /*   MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu); */
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
        /*    case R.id.action_refresh:
                //  Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                //        .show();
                break;
            // action with ID action_settings was selected
          case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;*/
            default:
                break;
        }

        return true;
    }

    public void savePdf() {
        //create object of Document class
        try {
        Document mDoc = new Document();
        //pdf file name
        String mFileName = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());

        //pdf file path
        String filename = "rtr_" + mFileName + ".pdf";

        Log.d("filename",""+filename);


         /*   File imagePath = new File(TEMP_DIR_PATH);
            try {
                if(!imagePath.exists())
                    imagePath.mkdir();
            }catch(Exception ex){
                ex.printStackTrace();
            }*/
            File fn =new File(getFilesDir(),filename);

           // Log.d("fn path",""+fn.getPath());
            //create instance of PdfWriter class
            PdfWriter.getInstance(mDoc, new FileOutputStream(fn.getPath()));
            //open the document for writing
            mDoc.open();
            //get text from EditText i.e. mTextEt
            String mText = "";
                    //mTextEt.getText().toString();

            //add author of the document (optional)
            mDoc.addAuthor("RTR");


            //add paragraph to the document
           // mDoc.add(new Paragraph(mText));
// Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
// Header
            // load image

                // get input stream
                InputStream ims = getAssets().open("toolbar_rtrlogo.jpg");
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
               // mDoc.add(image);

            PdfPCell cell1 = new PdfPCell(image,false);
            cell1.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell1.setBorder(PdfPCell.NO_BORDER);
            String username="";
            String phonedata="";
            Company cmp=dbhandler.getCompany(SharedData.selectedCompanyId);
            if(cmp!=null){
                username=cmp.getName();
                phonedata=cmp.getPhone();
            }
            Phrase name=new Phrase(""+username);
            Phrase phone=new Phrase("\nPh:"+phonedata);
            Paragraph p=new Paragraph();
            p.add(name);
            p.add(phone);
            PdfPCell cell2 = new PdfPCell(p);
            cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            cell2.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cell1);
            table.addCell(cell2);
            mDoc.add(table);
            mDoc.add(new Paragraph("\n"));
            mDoc.add(new Paragraph("\n"));
            Paragraph clp=new Paragraph("Customer List Report");
            mDoc.add(clp);
            mDoc.add(new Paragraph("\n"));
            PdfPTable sumtable=new PdfPTable(2);
            sumtable.setWidthPercentage(100);
            PdfPCell rg1=new PdfPCell(new Paragraph("Report Generated At :"));
            rg1.setBorder(PdfPCell.NO_BORDER);
            sumtable.addCell(rg1);
            Date dt= Calendar.getInstance().getTime();
            PdfPCell rg2=new PdfPCell(new Paragraph(""+dt));
            rg2.setBorder(PdfPCell.NO_BORDER);
            sumtable.addCell(rg2);

            PdfPCell nc1=new PdfPCell(new Paragraph("Number of Customers:"));
            nc1.setBorder(PdfPCell.NO_BORDER);
            sumtable.addCell(nc1);
            PdfPCell nc2=new PdfPCell(new Paragraph(""+cwlist.size()+"/"+cwlist.size()));
            nc2.setBorder(PdfPCell.NO_BORDER);
            sumtable.addCell(nc2);
            double theyreceive=0.0;
            double theypay=0.0;
            for(Customer cw:companycustomers){
                double bal=dbhandler.getAmount(cw.getId());
                if(bal>0) {
                    theyreceive += bal;
                } else{
                        theypay += bal;
                }
            }
            PdfPCell tr1=new PdfPCell(new Paragraph("Total Amount They'll Receive:"));
            tr1.setBorder(PdfPCell.NO_BORDER);
            sumtable.addCell(tr1);
            PdfPCell tr2=new PdfPCell(new Paragraph("Rs "+theyreceive));
            tr2.setBorder(PdfPCell.NO_BORDER);
            sumtable.addCell(tr2);

            PdfPCell tp1=new PdfPCell(new Paragraph("Total Amount They'll Pay:"));
            tp1.setBorder(PdfPCell.NO_BORDER);
            sumtable.addCell(tp1);
            PdfPCell tp2=new PdfPCell(new Paragraph("Rs "+Math.abs(theypay)));
            tp2.setBorder(PdfPCell.NO_BORDER);
            sumtable.addCell(tp2);

            PdfPCell fb1=new PdfPCell(new Paragraph("Final Balance :"));
            fb1.setBorder(PdfPCell.NO_BORDER);
            sumtable.addCell(fb1);
            PdfPCell fb2=new PdfPCell(new Paragraph("Rs "+Math.abs(theyreceive)+"(They'll Receive)"));
            fb2.setBorder(PdfPCell.NO_BORDER);
            sumtable.addCell(fb2);          ;
            mDoc.add(sumtable);

            mDoc.add(new Paragraph("\n"));
            mDoc.add(new Paragraph("\n"));
            PdfPTable table2 = new PdfPTable(5);
            table2.setWidthPercentage(100);
            PdfPCell psno=new PdfPCell(new Paragraph("S.NO"));
            table2.addCell(psno);
            PdfPCell pname=new PdfPCell(new Paragraph("Name"));
            table2.addCell(pname);
            PdfPCell addp=new PdfPCell(new Paragraph("ADDRESS/PH"));
            table2.addCell(addp);
            PdfPCell atr=new PdfPCell(new Paragraph("AMOUNT THEY'LL RECEIVE"));
            table2.addCell(atr);
            PdfPCell atp=new PdfPCell(new Paragraph("AMOUNT THEY'LL PAY"));
            table2.addCell(atp);
            int counter=0;
         /*  for(Customer cw:companycustomers){
                    counter++;
                    table2.addCell(new PdfPCell(new Paragraph("" + counter)));
                    table2.addCell(new PdfPCell(new Paragraph("" + cw.getName())));
                    table2.addCell(new PdfPCell(new Paragraph("" + cw.getPhone())));
                    double bal=dbhandler.getAmount(cw.getId());
                    if (bal>0) {
                        table2.addCell(new PdfPCell(new Paragraph("" )));
                        table2.addCell(new PdfPCell(new Paragraph(""+Math.abs(bal))));
                    } else {
                        table2.addCell(new PdfPCell(new Paragraph("" + Math.abs(bal))));
                        table2.addCell(new PdfPCell(new Paragraph("")));
                    }
            }*/
         for(CustomerWrapper cw:cwlist){
             counter++;
             table2.addCell(new PdfPCell(new Paragraph("" + counter)));
             table2.addCell(new PdfPCell(new Paragraph("" + cw.getName())));
             table2.addCell(new PdfPCell(new Paragraph("" + cw.getPhone())));
             double bal=cw.getBalance();
             if (cw.isGave()) {
                 table2.addCell(new PdfPCell(new Paragraph("" )));
                 table2.addCell(new PdfPCell(new Paragraph(""+Math.abs(bal))));
             } else {
                 table2.addCell(new PdfPCell(new Paragraph("" + Math.abs(bal))));
                 table2.addCell(new PdfPCell(new Paragraph("")));
             }
         }
            PdfPCell phc1=new PdfPCell(new Paragraph(""));
            table2.addCell(phc1);
            PdfPCell phc2=new PdfPCell(new Paragraph(""));
            table2.addCell(phc2);
            PdfPCell phc3=new PdfPCell(new Paragraph("Total"));
            table2.addCell(phc3);
            PdfPCell phc4=new PdfPCell(new Paragraph(""+Math.abs(theyreceive)));
            table2.addCell(phc4);
            PdfPCell phc5=new PdfPCell(new Paragraph(""+Math.abs(theypay)));
            table2.addCell(phc5);
            mDoc.add(table2);
            //close the document
            mDoc.close();


            //show message that file is saved, it will show file name and file path too
           // Toast.makeText(this, mFileName +".pdf\nis saved to\n"+ mFilePath, Toast.LENGTH_SHORT).show();


           // Context ctxt=context;
          //  downloadFile(fn,imageUri.getPath());

           /*
                DownloadManager downloadManager = (DownloadManager) ctxt.getSystemService(ctxt.DOWNLOAD_SERVICE);
                downloadManager.addCompletedDownload(fn.getName(), fn.getName(), true, "application/pdf", fn.getAbsolutePath(), fn.length(), true);
           */
            Uri pdf = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", fn);

            Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
            pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfOpenintent.setDataAndType(pdf, "application/pdf");

           //// try {
                startActivity(pdfOpenintent);
          //  } catch (ActivityNotFoundException e) {
                // handle no application here....
           // }

        }
        catch (Exception e){
            //if any thing goes wrong causing exception, get and show exception message
            Log.d("Exception",""+e+"\n"+e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                backbuttonDialog();
            }catch(Exception ex){
                ex.printStackTrace();
            }
              //return true;
        }

        return super.onKeyDown(keyCode, event);
    }*/

    // Method for opening a pdf file
    private void viewPdf(String filename) {


    }
/*
    public void backbuttonDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                context);
        myAlertDialog.setTitle("Are you Sure?");
        myAlertDialog.setMessage("do you want to exit app?");

        myAlertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                        startActivity(intent);
                        finish();
                      //  System.exit(0);

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
    }*/
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
        backbuttonDialog(context);
    }
    return super.onKeyDown(keyCode, event);
}
}