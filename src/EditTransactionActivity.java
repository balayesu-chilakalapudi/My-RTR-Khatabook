package com.bchilakalapudi.rtrconstruction;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Attachment;
import com.bchilakalapudi.rtrconstruction.model.Company;
import com.bchilakalapudi.rtrconstruction.model.Transaction;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.getExternalStorageDirectory;


public class EditTransactionActivity extends BaseActivity {
    public Button yougotdatebtn;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    Toolbar tbar;
    public AutoCompleteTextView sender_spin;
    public AutoCompleteTextView receiver_spin;
    public Button yougot_attach_btn;
    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<Customer> alist;
    public Uri filePath;
    public final int PICK_IMAGE_REQUEST = 71;
    private static final int PERMISSIONS_CAMERA = 101;
    /** Declaring an ArrayAdapter to set items to ListView */
    public ArrayAdapter<Customer> spinadapter;
    public FirebaseDatabaseHandler dbhandler;
    public List<Customer> userlist;
    ImageView yougot_attprvw;
    public Customer loginuser;
    public Button yougot_save_btn;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private Intent pictureActionIntent = null;
   // public FirebaseStorage storage;
   // public StorageReference storageReference;
    public Map<String,String> cIdnamemp;
    TextInputEditText amounttxt;
    TextInputEditText detailstxt;
   // public Attachment att;
    public String transactionId;
    public Transaction transaction;
    public Attachment attachment;
    public Switch gaveorgot_stch;
    public String loginuserId;
    public String senderId;
    public String receiverId;
    public ListView atttlvw;
    public ArrayList<AttachmentWrapper> awlist;
    public ArrayList<Attachment> attlist;
    public AttachmentAdapter attadapter;
    public Context context;
    public Company company;
    public Set<String> customerIdset;
    public TextView sendertxt;
    public TextView receivertxt;
    public LinearLayout sendercontainer;
    public LinearLayout receivercontainer;
    public Bitmap bitmap;
    public String selectedImagePath;
    public Customer sender;
    public Customer receiver;
    public String mFileName;
    Uri imageUri;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int IMAGE_RESULT = 200;
    public static String TEMP_DIR_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Allowing Strict mode policy for Nougat support
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.activity_edit_transaction);
        TEMP_DIR_PATH=getApplicationContext().getFilesDir().getPath();
        context=this;
        mFileName= new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis())+".png";

        dbhandler= SharedData.getDbhandler();
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
             //   backbuttonDialog(context);
                Intent in = new Intent(EditTransactionActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        loginuserId=SharedData.getLoginuserId();
        atttlvw=findViewById(R.id.et_attlvw);
        awlist=new ArrayList<>();
        attlist=new ArrayList<>();
        Intent in=getIntent();
        transactionId=in.getStringExtra("transactionId");
        senderId=in.getStringExtra("senderId");
        receiverId=in.getStringExtra("receiverId");
        Log.d("senderId",""+senderId);
        Log.d("receiverId",""+receiverId);
        if(SharedData.selectedCompanyId!=null)
            company=dbhandler.getCompany(SharedData.selectedCompanyId);
        tbar=findViewById(R.id.et_tbar);
        customerIdset=new HashSet<>(company.getCustomerIdlist());
        amounttxt=findViewById(R.id.et_amount);
        detailstxt=findViewById(R.id.et_details);
        sendertxt=findViewById(R.id.et_sendertxt);
        receivertxt=findViewById(R.id.et_receivertxt);
        sendercontainer=findViewById(R.id.et_sender_container);
        receivercontainer=findViewById(R.id.et_rec_container);

        if(transactionId==null){
            transaction=new Transaction();
            if(senderId!=null){
                sendercontainer.setVisibility(View.GONE);
                receivertxt.setText("Customer");
                tbar.setTitle("You Gave Amount");
                sender=dbhandler.getCustomer(senderId);
            }else{
                receivercontainer.setVisibility(View.GONE);
                sendertxt.setText("Customer");
                tbar.setTitle("You Got Amount");
                receiver=dbhandler.getCustomer(receiverId);
            }
        }else{
            tbar.setTitle("Edit Transaction");
            transaction=dbhandler.getTransaction(transactionId);
            senderId=transaction.getSenderId();
            receiverId=transaction.getReceiverId();
            sender=dbhandler.getCustomer(senderId);
            receiver=dbhandler.getCustomer(receiverId);
            amounttxt.setText("" + transaction.getAmount());
            detailstxt.setText("" + transaction.getDetails());
            for(Attachment att:dbhandler.getAttachments()){
                if(transactionId!=null &&
                        att.getTransactionId()!=null &&
                        att.getTransactionId().equals(transactionId)){
                    AttachmentWrapper aw=new AttachmentWrapper();
                    aw.filename=att.filename;
                    aw.id=att.id;
                    aw.size=att.size;
                    aw.downloadUrl=att.downloadUrl;
                    awlist.add(aw);
                    attlist.add(att);
                }
            }
            attadapter=new AttachmentAdapter(this,awlist,atttlvw);
            atttlvw.setAdapter(attadapter);
        }
        setSupportActionBar(tbar);

      //  transaction=dbhandler.getTransaction(transactionId);
      //  attachment=dbhandler.getAttachment(transactionId);
        userlist=dbhandler.getUserlist();
        loginuser= dbhandler.getCustomer(loginuserId);

        yougot_attach_btn=findViewById(R.id.et_attach_button);
        yougot_save_btn=findViewById(R.id.et_save_btn);
      //  yougot_attprvw=findViewById(R.id.et_img);



        yougotdatebtn=(Button)findViewById(R.id.et_dateview);


        //storage = FirebaseStorage.getInstance();
      //  storageReference = storage.getReference();
        alist=new ArrayList<>();
        // alist.add("Select Customer");

        //yougotdatebtn.setText(""+dt.get);

        calendar = Calendar.getInstance();
        Log.d("transactiondate",""+transaction.getDate());
        if(transaction.getDate()!=null) {
            Date dt = transaction.getDate();
            calendar.setTime(dt);
        }
        year = calendar.get(Calendar.YEAR);
       month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("year",""+year);
        Log.d("month",""+month);
        Log.d("day",""+day);

       String data=""+(new StringBuilder().append(day).append("/").append(month+1).append("/").append(year));
        Log.d("data",""+data);
        yougotdatebtn.setText(data);
       // showDate(year, month+1, day);

        sender_spin=findViewById(R.id.etsend_spinner);
        receiver_spin=findViewById(R.id.etrec_spinner);

        // setSupportActionBar(tbar);
        Set<String> uidset=new HashSet<String>();
        //skip login user

       // uidset.add(loginuser.getId());
       /* if(senderId!=null){
            uidset.add(senderId);
        }
        if(receiverId!=null){
            uidset.add(receiverId);
        }*/
      // alist.add(new Customer());
        for(Customer u:userlist){
            if(!uidset.contains(u.getId()) && customerIdset.contains(u.getId())) {
                uidset.add(u.getId());
                alist.add(u);
            }
        }

        Log.d("alist",""+alist);
        /** Defining the ArrayAdapter to set items to Spinner Widget */
        spinadapter = new ArrayAdapter<Customer>(this, android.R.layout.select_dialog_item, alist);
        sender_spin.setAdapter(spinadapter);
        receiver_spin.setAdapter(spinadapter);
        sender_spin.setThreshold(1); //will start working from first character
        receiver_spin.setThreshold(1); //will start working from first character
        sender_spin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sender = (Customer) parent.getAdapter().getItem(position);
                Log.d("sender",""+sender.getId());
                senderId=sender.getId();
            }
        });
        receiver_spin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                receiver = (Customer) parent.getAdapter().getItem(position);
                Log.d("receiver",""+receiver.getId());
                receiverId=receiver.getId();
            }
        });
     /*   int sposition=0;
        int rposition=0;
        for(Customer u:alist){
            sposition++;
            if(u.getId()!=null && senderId!=null &&
                    u.getId().equals(senderId)){
                break;
            }
        }
        if(sposition!=0){
            sposition=sposition-1;
        }

        //receiver selection
        for(Customer u:alist){
            rposition++;
            if(u.getId()!=null &&
                    receiverId!=null &&
                    u.getId().equals(receiverId)){
                break;
            }
        }
        if(rposition!=0){
            rposition=rposition-1;
        }*/
        if(transactionId!=null) {
            if(dbhandler==null){
                dbhandler=new FirebaseDatabaseHandler();
            }
            Customer s=dbhandler.getCustomer(senderId);
            Customer r=dbhandler.getCustomer(receiverId);
            if(senderId!=null) {
                String sname="";
                if(s!=null)
                    sname=s.getName();
                sender_spin.setText("" +sname);
            }
            if(receiverId!=null) {
                String rname="";
                if(r!=null)
                    rname=r.getName();
                receiver_spin.setText("" + rname);
            }
        }
        tbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(EditTransactionActivity.this,DashboardActivity.class);
                startActivity(in);
            }
        });






        yougot_attach_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // chooseImage();
                try {
                    mFileName= new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(System.currentTimeMillis())+".png";
                    mFileName = mFileName.replace("_", "");
                    mFileName = mFileName.trim();
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_CAMERA);
                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                    } else {
                        startDialog();
                    }

                    //  startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
                }catch(Exception ex){
                    Toast.makeText(context,""+ex.getMessage(),Toast.LENGTH_LONG).show();
                    ex.printStackTrace();

                }

            }
        });

        yougot_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Customer sender = (Customer) sender_spin.get
               // Customer receiver = (Customer) receiver_spin.getText();
                Log.d("sender",""+sender);
                Log.d("receiver",""+receiver);
                if (sender != null &&
                        receiver != null &&
                        !sender.getId().equals(receiver.getId())) {
                    transaction.amount = Double.valueOf("" + amounttxt.getText());
                    transaction.details = "" + detailstxt.getText();
                    transaction.date = calendar.getTime();
                    Log.d("transaction date:", "" + transaction.getDate());
                    //   boolean isgot=gaveorgot_stch.isChecked();
                    transaction.receiverId = receiver.getId();
                    transaction.senderId = sender.getId();
                    if (transaction.getId() == null) {
                        transaction.id = dbhandler.getRandomId(28);
                        transaction.createddate=Calendar.getInstance().getTime();
                    }
                    transaction.companyId = SharedData.selectedCompanyId;
                    dbhandler.writeTransaction(transaction.getId(), transaction);
                    //update sender balance
                    sender.balance = sender.balance - transaction.getAmount();
                    sender.lastmodified = transaction.getDate();
                    dbhandler.writeUser(sender.getId(), sender);
                    //update receiver balance
                    receiver.balance = receiver.balance + transaction.getAmount();
                    receiver.lastmodified = transaction.getDate();
                    dbhandler.writeUser(receiver.getId(), receiver);
                    for (AttachmentWrapper aw : awlist) {
                        Attachment att = new Attachment();
                        att.id = aw.getId();
                        att.filename = aw.getFilename();
                        att.size = aw.getSize();
                        att.downloadUrl = aw.getDownloadUrl();
                        att.transactionId = transaction.getId();
                        dbhandler.setTransactionIdForAttachment(att.getId(), att.getTransactionId());
                    }

                    Intent in = new Intent(EditTransactionActivity.this, DashboardActivity.class);
                    startActivity(in);
                }else {
                    if (sender!=null &&
                            receiver!=null &&
                            sender.getId()!=null &&
                            receiver.getId()!=null &&
                            sender.getId().equals(receiver.getId())) {
                        Toast.makeText(getApplicationContext(), "please select different customer, Sender and Receiver are matched, ",
                                Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please select customer",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });
    }



    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    public void showDate(int year, int month, int day) {
        String data="";
        calendar.set(year,month-1,day);
        data=""+(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
        Log.d("data",""+data);
        yougotdatebtn.setText(data);
    }


    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
               context);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;

                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent,
                                GALLERY_PICTURE);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        File imagePath = new File(TEMP_DIR_PATH, "images");
                        try {
                            if(!imagePath.exists())
                            imagePath.mkdir();
                        }catch(Exception ex){
                           ex.printStackTrace();
                        }
                        File file = new File(imagePath, mFileName);
                        try {
                            file.createNewFile();
                        }catch(Exception ex){
                           ex.printStackTrace();
                        }
                        imageUri = FileProvider.getUriForFile(
                                context,
                                "com.bchilakalapudi.rtrconstruction.provider",
                                file
                        );

                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                imageUri);
                        getApplicationContext().grantUriPermission(
                                "com.google.android.GoogleCamera",
                                imageUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                        startActivityForResult(intent,
                                CAMERA_REQUEST);

                    }
                });
        myAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            bitmap = null;
            selectedImagePath = null;

            if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

                File f;
            /*= new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals(mFileName)) {
                    f = temp;
                    break;
                }
            }

            if (!f.exists()) {

                Toast.makeText(getBaseContext(),

                        "Error while capturing image", Toast.LENGTH_LONG)

                        .show();

                return;

            }*/

                try {
                    String fileimgpath = getFilePathFromURI(context, imageUri);
                    Log.d("fileimgpath", "" + fileimgpath);
                    f = new File(fileimgpath);
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                    bitmap = Bitmap.createScaledBitmap(bitmap, 1400, 1400, true);

                    int rotate = 0;
                    try {
                        ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotate = 270;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotate = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotate = 90;
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotate);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);


                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    doUploadImage(f.getPath(), Uri.fromFile(f));

                    //   img_logo.setImageBitmap(bitmap);
                    //storeImageTosdCard(bitmap);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
                try {
                    if (data != null) {

                        Uri selectedImage = data.getData();
                        Log.d("selectedImage", "" + selectedImage);
                  /*  String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath,
                            null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    selectedImagePath = c.getString(columnIndex);
                    c.close();
                */
                        //   if (selectedImagePath != null) {
                        //    txt_image_path.setText(selectedImagePath);
                        //   }
                        String fileimgpath = getFilePathFromURI(context, selectedImage);
                        Log.d("fileimgpath", "" + fileimgpath);
                        File f = new File(fileimgpath);
                        bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                        //  bitmap = BitmapFactory.decodeFile(selectedImage.getPath()); // load
                        // preview image
                        bitmap = Bitmap.createScaledBitmap(bitmap, 1400, 1400, false);

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file

                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();

                        doUploadImage(f.getPath(), Uri.fromFile(f));

                        //   img_logo.setImageBitmap(bitmap);

                    } else {
                        Toast.makeText(getApplicationContext(), "Cancelled",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        }catch(Exception exc){
            exc.printStackTrace();
        }
    }

    public  void doUploadImage(String realPath,Uri filePath){
        File fl = new File(realPath);
        int file_size = Integer.parseInt(String.valueOf(fl.length() / 1024));
        if(file_size>0) {
            Attachment att = new Attachment();
            att.id = dbhandler.getRandomId(28);
            att.filename = fl.getName();
            att.size = file_size + " KB";
            attlist.add(att);
            dbhandler.uploadImage(att, filePath, context);

            // wrapper needed, primitive types not able to send to firebase
            AttachmentWrapper aw = new AttachmentWrapper();
            aw.id = att.id;
            aw.filename = att.filename;
            aw.downloadUrl = att.getDownloadUrl();
            aw.size = att.size;
            aw.filePath = filePath;
            awlist.add(aw);
            attadapter = new AttachmentAdapter(this, awlist, atttlvw);
            atttlvw.setAdapter(attadapter);
        }else{
            Toast.makeText(context,"Please try again",Toast.LENGTH_LONG).show();
        }
    }

    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(TEMP_DIR_PATH + File.separator + fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copyStream(inputStream, outputStream);

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           // backbuttonDialog(context);
            Intent in = new Intent(EditTransactionActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }

}