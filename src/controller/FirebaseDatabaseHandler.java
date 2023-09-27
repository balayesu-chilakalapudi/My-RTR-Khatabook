package com.bchilakalapudi.rtrconstruction.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bchilakalapudi.rtrconstruction.model.Attachment;
import com.bchilakalapudi.rtrconstruction.BaseActivity;
import com.bchilakalapudi.rtrconstruction.model.Attachment;
import com.bchilakalapudi.rtrconstruction.model.Company;
import com.bchilakalapudi.rtrconstruction.model.Transaction;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class FirebaseDatabaseHandler extends BaseActivity {
    public List<Customer> userlist;
    public List<Transaction> transactions;
    public List<Company> companies;
    public List<Attachment> attachments;

    private DatabaseReference mDatabaseRef;
    public FirebaseDatabase database;
    public Attachment attachment;

    public FirebaseStorage storage;
    public StorageReference storageReference;
    FirebaseAuth mAuth;
    FirebaseUser user;

    public FirebaseDatabaseHandler(){
        database=FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        signInAnonymously();
        userlist=new ArrayList<>();
        transactions=new ArrayList<>();
        attachments=new ArrayList<>();
        companies=new ArrayList<>();

        dogetAllUsers();
        dogetAllTransactions();
        dogetAllCompanies();
        dogetAllAttachments();
    }


    public void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                         //   Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                         //   updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                          //  Log.w(TAG, "signInAnonymously:failure", task.getException());
                           // Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
                           //         Toast.LENGTH_SHORT).show();
                          //  updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void writeUser(String p_userId, Customer p_user) {
        mDatabaseRef.child("customers").child(p_userId).setValue(p_user);
    }

    public void writeTransaction(String p_transactionId,Transaction p_transaction) {
        mDatabaseRef.child("transactions").child(p_transactionId).setValue(p_transaction);
    }

    public void writeCompany(String p_companyId,Company p_company) {
        mDatabaseRef.child("companies").child(p_companyId).setValue(p_company);
    }

    public void writeAttachment(String p_attachmentId,Attachment p_attachment){
        mDatabaseRef.child("attachments").child(p_attachmentId).setValue(p_attachment);
    }

    public void dogetAllUsers(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("customers");
        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userlist=new ArrayList<>();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Customer usobj = postSnapshot.getValue(Customer.class);
                            userlist.add(usobj);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    public void dogetAllTransactions(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("transactions");
        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        transactions=new ArrayList<>();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Transaction tobj = postSnapshot.getValue(Transaction.class);
                            transactions.add(tobj);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    public void dogetAllCompanies(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("companies");
        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        companies=new ArrayList<>();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Company cobj = postSnapshot.getValue(Company.class);
                            companies.add(cobj);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    public void dogetAllAttachments(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("attachments");
        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        attachments=new ArrayList<>();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Attachment attobj = postSnapshot.getValue(Attachment.class);
                            attachments.add(attobj);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String getRandomId(int count){
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        String strId=builder.toString();
        strId=strId.toLowerCase();
        return strId;
    }

    public Customer getCustomer(String p_cId){
        Customer uobj=null;
        String cname="";
        userlist=getUserlist();
        Log.d("getcnameuserlist",""+userlist);
        for(Customer u:userlist){
            if(u.id.equals(p_cId)){
                cname=u.name;
                uobj=u;
                break;
            }
        }
        Log.d("cname",""+cname);
        return uobj;
    }

    public List<Customer> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<Customer> userlist) {
        this.userlist = userlist;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public DatabaseReference getmDatabaseRef() {
        return mDatabaseRef;
    }

    public void setmDatabaseRef(DatabaseReference mDatabaseRef) {
        this.mDatabaseRef = mDatabaseRef;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    public static String getAlphaNumericString() {
        return ALPHA_NUMERIC_STRING;
    }

    public void uploadImage(Attachment att,Uri filePath,Context ctxt) {
        signInAnonymously();
               attachment=att;

        storageReference = storage.getReference();
        Log.d("filePath",""+filePath);
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(ctxt);
             progressDialog.setTitle("Uploading...");
              progressDialog.show();

            storageReference  = storageReference.child("images/"+attachment.getId()+".jpg");

            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //   att.setDownloadUrl(""+taskSnapshot.getDownloadUrl());

                            //  Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    attachment.setDownloadUrl(downloadUrl.toString());
                                    writeAttachment(attachment.getId(),attachment);
                                   progressDialog.dismiss();
                                   // Log.d("downloadURL", "" + downloadUrl);
                                    //  Toast.makeText(getBaseContext(), "Upload success! URL - " + downloadUrl.toString() , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           progressDialog.dismiss();
                            // Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                               progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }



    public Transaction getTransaction(String p_tId){
        Transaction res=null;
        for(Transaction t:getTransactions()){
            if(t!=null &&
                    p_tId!=null &&
                    t.getId().equals(p_tId)){
                res=t;
                break;
            }
        }
        return res;
    }

    public Attachment getAttachment(String p_tId){
        Log.d("p_tId",""+p_tId);
        Attachment res=null;
        for(Attachment a:getAttachments()){
            if(a.getTransactionId()!=null &&
                    p_tId!=null &&
                    a.getTransactionId().equals(p_tId)){
                res=a;
                break;
            }
        }
        return res;
    }

    public Company getCompany(String p_cId){
        Company uobj=null;
        companies=getCompanies();
        Log.d("companies",""+companies);
        for(Company u:companies){
            if(u.id.equals(p_cId)){
                uobj=u;
                break;
            }
        }
        return uobj;
    }

    public void removeCustomer(String p_userId) {
        mDatabaseRef.child("customers").child(p_userId).removeValue();
    }

    public void removeAttachment(String p_userId) {
        mDatabaseRef.child("attachments").child(p_userId).removeValue();
    }

    public void removeTransaction(String p_userId) {
        mDatabaseRef.child("transactions").child(p_userId).removeValue();
    }

    public void deleteStorageFile(String fileId){
        // Create a storage reference from our app
        //storageRef = storage.getReference();

        storageReference = storage.getReference();
// Create a reference to the file to delete
        StorageReference desertRef = storageReference.child("images/"+fileId+".jpg");

// Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    public void setTransactionIdForAttachment(String attachmentId,String transactionId){
        mDatabaseRef.child("attachments").child(attachmentId).child("transactionId").setValue(transactionId);
    }
    public double getAmount(String customerId){
    //    Log.d("customerId",""+customerId);
        double amt=0.0;
        double gaveamt=0.0;
        double gotamt=0.0;
        for(Transaction t:getTransactions()) {
            if (t.getCompanyId()!=null && t.getCompanyId().equals(SharedData.selectedCompanyId)){
           //     Log.d("transaction",""+t);

                if(!customerId.equals(SharedData.loginuserId)) {
                    if (t.getSenderId().equals(customerId) &&
                            t.getReceiverId().equals(SharedData.loginuserId)) {
                        gotamt += t.getAmount();
                    }
                    else if(t.getReceiverId().equals(customerId) &&
                            t.getSenderId().equals(SharedData.loginuserId)) {
                        gaveamt += t.getAmount();
                    }
                }else{
                    //login user transactions
                    if (t.getSenderId().equals(customerId)) {
                        gaveamt += t.getAmount();
                    }
                    else if(t.getReceiverId().equals(customerId)) {
                        gotamt += t.getAmount();
                    }
                }
            }
        }
        amt=gotamt-gaveamt;
        return amt;
    }

    public double getAllAmount(String customerId){
        double amt=0.0;
        double gaveamt=0.0;
        double gotamt=0.0;
        for(Transaction t:getTransactions()) {
            if (t.getCompanyId()!=null && t.getCompanyId().equals(SharedData.selectedCompanyId)){
                if (t.getSenderId().equals(customerId)) {
                    gaveamt += t.getAmount();
                }
                if (t.getReceiverId().equals(customerId)) {
                    gotamt += t.getAmount();
                }
                //  }
            }
        }
        amt=gotamt-gaveamt;
        return amt;
    }

    //1 minute = 60 seconds
//1 hour = 60 x 60 = 3600
//1 day = 3600 x 24 = 86400
    public void printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
    }

    public long getDateDifferenceHours(Date startDate, Date endDate){
        long elapsedHours=0;
        long different = endDate.getTime() - startDate.getTime();
        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

         elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        return elapsedHours;
    }
}
