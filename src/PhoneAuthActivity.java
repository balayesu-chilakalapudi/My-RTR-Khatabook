package com.bchilakalapudi.rtrconstruction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Company;
import com.bchilakalapudi.rtrconstruction.model.Customer;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;


import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    public FirebaseAuth mAuth;
    // [END declare_auth]
    FirebaseDatabaseHandler dbhandler;
    public boolean mVerificationInProgress = false;
    public String mVerificationId;
    public PhoneAuthProvider.ForceResendingToken mResendToken;
    public  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    Toolbar phone_auth_tbar;
  //  private ViewGroup mPhoneNumberViews;
  //  private ViewGroup mSignedInViews;

  //  private TextView mStatusText;
  //  private TextView mDetailText;

   // private EditText mPhoneNumberField;
    private EditText mVerificationField;

   // private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;
  //  private Button mSignOutButton;
    public String name;
    public String phone;
    public String pin;
    public String usertype;
    public TextView phonetxt;
    public Context ctxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        dbhandler=SharedData.getDbhandler();
        if(dbhandler==null) {
            dbhandler = new FirebaseDatabaseHandler();
        }
        ctxt=this;
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
               // backbuttonDialog(ctxt);
                Intent in = new Intent(PhoneAuthActivity.this, DashboardActivity.class);
                startActivity(in);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        Intent in=getIntent();
        name=in.getStringExtra("NAME");
        phone="+91"+in.getStringExtra("PHONE");
        pin=in.getStringExtra("PIN");
        usertype=in.getStringExtra("USER_TYPE");
        phonetxt=findViewById(R.id.phone_num_txt);
        mAuth = FirebaseAuth.getInstance();
        phone_auth_tbar=findViewById(R.id.phone_auth_tbar);
        setSupportActionBar(phone_auth_tbar);
        phone_auth_tbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intn=new Intent(getBaseContext(), MainActivity.class);
                startActivity(intn);
                //Toast.makeText(getApplicationContext(),"your icon was clicked",Toast.LENGTH_SHORT).show();
            }
        });

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        mVerificationField = findViewById(R.id.fieldVerificationCode);
        mVerifyButton = findViewById(R.id.buttonVerifyPhone);
        mResendButton = findViewById(R.id.buttonResend);
        // Assign click listeners
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                mVerificationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mVerificationField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        // [END phone_auth_callbacks]
        if (validatePhoneNumber()) {
            phonetxt.setText("Verification Code Sent to phone: "+phone);
            startPhoneNumberVerification(phone);
        }
    }


  /*
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(phone);
        }
    }
    */


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
         // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                          //  mVerificationField.setError("");
                            FirebaseUser fuser = task.getResult().getUser();
                            String account= SharedData.getAccount();
                            if(account.equals("customer")) {
                                Customer us = new Customer();
                                //check if  phone already exists
                                for (Customer u : dbhandler.userlist) {
                                    if (u.getPhone().equals(phone)) {
                                        us = u;
                                        break;
                                    }
                                }
                                if (us.id == null) {
                                    us.id = dbhandler.getRandomId(28);
                                }
                                us.uid = fuser.getUid();
                                us.phone = phone;
                                us.name = name;
                                us.loginPin = pin;
                                us.type = usertype;
                                us.admin = false;
                                //write new user
                                dbhandler.writeUser(us.getId(), us);
                            }else{
                                //company
                                Company us = new Company();
                                //check if  phone already exists
                                for (Company u : dbhandler.companies) {
                                    if (u.getPhone().equals(phone)) {
                                        us = u;
                                        break;
                                    }
                                }
                                if (us.id == null) {
                                    us.id = dbhandler.getRandomId(28);
                                }
                                us.uid = fuser.getUid();
                                us.phone = phone;
                                us.name = name;
                                us.loginPin = pin;
                                us.type = usertype;
                                us.admin = true;
                                //write new user
                                dbhandler.writeCompany(us.getId(), us);
                            }
                            Intent in=new Intent(PhoneAuthActivity.this,LoginActivity.class);
                            startActivity(in);
                            // [START_EXCLUDE]
                         //   updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                mVerificationField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                       //     updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]


/*
    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
              //  enableViews(mStartButton, mPhoneNumberField);
              //  disableViews(mVerifyButton, mResendButton, mVerificationField);
//                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
             //   enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField);
             //   disableViews(mStartButton);
             //   mDetailText.setText(R.string.status_code_sent);
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
             //   enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                //        mVerificationField);
           //     mDetailText.setText(R.string.status_verification_failed);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
           //     disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
            //            mVerificationField);
            //    mDetailText.setText(R.string.status_verification_succeeded);

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                    } else {
                        mVerificationField.setText(R.string.instant_validation);
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
             //   mDetailText.setText(R.string.status_sign_in_failed);
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }

        if (user == null) {
            // Signed out
         //   mPhoneNumberViews.setVisibility(View.VISIBLE);
         //   mSignedInViews.setVisibility(View.GONE);

      //      mStatusText.setText(R.string.signed_out);
        } else {
            // Signed in
        //    mPhoneNumberViews.setVisibility(View.GONE);
        //    mSignedInViews.setVisibility(View.VISIBLE);

         //   enableViews(mPhoneNumberField, mVerificationField);
         //   m.setText(null);
            mVerificationField.setText(null);

         //   mStatusText.setText(R.string.signed_in);
          //  mDetailText.setText(""+getString(R.string.firebase_status_fmt, user.getUid()));
        }
    }
*/
    private boolean validatePhoneNumber() {
        String phoneNumber = phone;
        if (TextUtils.isEmpty(phoneNumber)) {
            mVerificationField.setError("Invalid phone number.");
            return false;
        }
        return true;
    }
/*
    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

 */

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
         //   case R.id.buttonStartVerification:



             //   break;
            case R.id.buttonVerifyPhone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.buttonResend:
                resendVerificationCode(phone, mResendToken);
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          //  backbuttonDialog(ctxt);
            Intent in = new Intent(PhoneAuthActivity.this, DashboardActivity.class);
            startActivity(in);
        }
        return super.onKeyDown(keyCode, event);
    }

}
