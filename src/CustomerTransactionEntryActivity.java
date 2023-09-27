package com.bchilakalapudi.rtrconstruction;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Attachment;
import com.bchilakalapudi.rtrconstruction.model.Transaction;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CustomerTransactionEntryActivity extends BaseActivity {
    public Toolbar tbar;
    public Button yougavebtn;
    public Button yougotbtn;
    public String customerId;
    public Customer customer;
    public FirebaseDatabaseHandler dbhandler;
    public List<Customer> userlist;
    public ListView cte_lvw;
    ArrayList<Transaction> ewlist;
    public TransactionAdapter entryadapter;
    public Customer loginuser;
    public List<Transaction> transactions;
    private static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 100;
    public TextView cte_bal_amt;
    public TextView cte_bal_gave;
    public TextView cte_bal_got;
    public static String TEMP_DIR_PATH;
    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_customer_transaction_entry);
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
              //  backbuttonDialog(context);
                Intent in = new Intent(CustomerTransactionEntryActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        TEMP_DIR_PATH=getApplicationContext().getFilesDir().getPath();
        File imagePath=new File(TEMP_DIR_PATH);
        try {
            if(!imagePath.exists())
                imagePath.mkdir();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        tbar=findViewById(R.id.ctebar);
        dbhandler=SharedData.dbhandler;
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        userlist=dbhandler.getUserlist();
        transactions=dbhandler.getTransactions();
        cte_bal_amt=findViewById(R.id.cte_balamt);
        cte_bal_gave=findViewById(R.id.cte_bal_gave);
        cte_bal_got=findViewById(R.id.cte_bal_got);
        Log.d("userlist",""+userlist);
        Log.d("transactions",""+transactions);
        final String loginuserId=SharedData.getLoginuserId();
        loginuser= dbhandler.getCustomer(loginuserId);
        //loginuser=SharedData.getUserobj();
        cte_lvw=findViewById(R.id.cte_lvw);
        ewlist=new ArrayList<>();
        Intent in=getIntent();
        customerId=in.getStringExtra("customerId");
        Log.d("customerId",""+customerId);
        customer=dbhandler.getCustomer(customerId);
        Log.d("customer",""+customer);
        double gave_bal=0;
        double got_bal=0;
        if(customer!=null) {
            String name = customer.getName();
            String phone = customer.getPhone();
            tbar.setTitle("" + name);
            tbar.setSubtitle("" + phone);
          //  for(User u:userlist) {
                for (Transaction t : transactions) {
                    if (t.getCompanyId()!=null && t.getCompanyId().equals(SharedData.selectedCompanyId) && (customerId.equals(t.getSenderId()) ||
                        customerId.equals(t.getReceiverId()))) {
                        ewlist.add(t);
                        if(t.getSenderId().equals(customer.getId())){
                            gave_bal+=t.getAmount();
                        }
                        if(t.getReceiverId().equals(customer.getId())){
                            got_bal+=t.getAmount();
                        }
                    }
                }
        }
        cte_bal_gave.setText("You Gave \n ₹"+gave_bal);
        cte_bal_got.setText("You Got \n ₹"+got_bal);
        double amtbal=got_bal-gave_bal;
        if(amtbal>0) {
            cte_bal_amt.setText("Amount You Got \n₹" + amtbal);
            cte_bal_amt.setTextColor(Color.parseColor("#4CAF50"));
        }
        else {
            cte_bal_amt.setText("Amount You Gave \n₹" + Math.abs(amtbal));
            cte_bal_amt.setTextColor(Color.parseColor("#FF0000"));
        }
        setSupportActionBar(tbar);
        Collections.sort(ewlist, Collections.reverseOrder());
        entryadapter=new TransactionAdapter(this,ewlist,customerId);
        cte_lvw.setAdapter(entryadapter);

        tbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intn=new Intent(getBaseContext(), DashboardActivity.class);
                startActivity(intn);
                //Toast.makeText(getApplicationContext(),"your icon was clicked",Toast.LENGTH_SHORT).show();
            }
        });

        yougavebtn=findViewById(R.id.cte_yougave_btn);
        yougavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intn=new Intent(getBaseContext(), EditTransactionActivity.class);
                String senderId="";
                String receiverId="";
                senderId=customerId;
               // receiverId=loginuserId;
                intn.putExtra("senderId",senderId);
              //  intn.putExtra("receiverId",receiverId);
                startActivity(intn);
            }
        });

        yougotbtn=findViewById(R.id.cte_yougot_btn);
        yougotbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String senderId="";
                String receiverId="";

                    receiverId=customerId;
                 //   senderId=loginuserId;

                Intent intn=new Intent(getBaseContext(), EditTransactionActivity.class);
             //   intn.putExtra("senderId",senderId);
                intn.putExtra("receiverId",receiverId);
                startActivity(intn);
            }
        });

        cte_lvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transaction ew = ewlist.get(position);
                Intent in=new Intent(CustomerTransactionEntryActivity.this,TransactiondetailsActivity.class);
                in.putExtra("transactionId",ew.getId());
                startActivity(in);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customer_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.cte_action_view:
             Intent in=new Intent(CustomerTransactionEntryActivity.this,ViewcustomerActivity.class);
             in.putExtra("customerId",customerId);
             startActivity(in);
                break;

            case R.id.cte_action_pdf:
                Log.d("PDF","cte_action_pdf called");
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    savePdf();
                }
                break;
            // action with ID action_settings was selected
         /* case R.id.cte_action_edit:
            //    Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                    //    .show();
              Intent inp=new Intent(CustomerTransactionEntryActivity.this,EditcustomerActivity.class);
              inp.putExtra("customerId",customerId);
              startActivity(inp);
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
            String filename = "rtr_customer_" + mFileName + ".pdf";
                    //getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/rtr_customer_" + mFileName + ".pdf";
          //  Log.d("mFilePath", "" + mFilePath);


            File fn = new File(getFilesDir(),filename);

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

            PdfPCell cell1 = new PdfPCell(image, false);
            cell1.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell1.setBorder(PdfPCell.NO_BORDER);
            Log.d("customerpdf",""+customer);
            if (customer != null) {
                Phrase name = new Phrase("" + customer.getName());
                Phrase phone = new Phrase("\nPh:" + customer.getPhone());

                Paragraph p = new Paragraph();
                p.add(name);
                p.add(phone);
                PdfPCell cell2 = new PdfPCell(p);

                cell2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell2.setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell1);
                table.addCell(cell2);
            }
            mDoc.add(table);
            mDoc.add(new Paragraph("\n"));
            mDoc.add(new Paragraph("\n"));
            PdfPTable table2 = new PdfPTable(3);
            table2.setWidthPercentage(100);
            PdfPCell psno=new PdfPCell(new Paragraph("ENTRIES"));
            table2.addCell(psno);
            PdfPCell pname=new PdfPCell(new Paragraph("YOU GAVE"));
            table2.addCell(pname);
            PdfPCell addp=new PdfPCell(new Paragraph("YOU GOT"));
            table2.addCell(addp);
            for (Transaction t : transactions) {
                if (t.getCompanyId()!=null && t.getCompanyId().equals(SharedData.selectedCompanyId) && (customerId.equals(t.getSenderId()) ||
                        customerId.equals(t.getReceiverId()))) {
                    String dt=String.valueOf(t.getDate());
                    dt=dt.split("GMT")[0];
                    String scname=dbhandler.getCustomer(t.senderId).getName();
                    String rcname=dbhandler.getCustomer(t.receiverId).getName();
                    //table2.addCell(new PdfPCell(new Paragraph("" +dt+"\n"+scname+"\n"+t.getDetails())));
                    if(t.getSenderId().equals(customerId)) {
                        table2.addCell(new PdfPCell(new Paragraph("" +dt+"\n"+rcname+"\n"+t.getDetails())));
                        table2.addCell(new PdfPCell(new Paragraph("Rs " +t.getAmount())));
                        table2.addCell(new PdfPCell(new Paragraph("")));
                    }else{
                        table2.addCell(new PdfPCell(new Paragraph("" +dt+"\n"+scname+"\n"+t.getDetails())));
                        table2.addCell(new PdfPCell(new Paragraph("")));
                        table2.addCell(new PdfPCell(new Paragraph("Rs " +t.getAmount())));
                    }
                }
                }
            mDoc.add(table2);
            //close the document
            mDoc.close();

            Uri imageUri = FileProvider.getUriForFile(
                   context,
                    "com.bchilakalapudi.rtrconstruction.provider",
                    fn
            );

                //Context ctxt=context;
                //DownloadManager downloadManager = (DownloadManager) ctxt.getSystemService(ctxt.DOWNLOAD_SERVICE);
                //downloadManager.addCompletedDownload(fn.getName(), fn.getName(), true, "application/pdf", fn.getAbsolutePath(), fn.length(), true);
            Uri pdf = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", fn);

            Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
            pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfOpenintent.setDataAndType(pdf, "application/pdf");

            //// try {
            startActivity(pdfOpenintent);
        }catch (Exception ex){
            Log.d("Exception",""+ex);
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
            }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          //  backbuttonDialog(context);
            Intent in = new Intent(CustomerTransactionEntryActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }

}