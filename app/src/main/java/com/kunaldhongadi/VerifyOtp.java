package com.kunaldhongadi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyOtp extends AppCompatActivity {

    public static final String TAG = "TAG";

    private OtpView otpView;
    TextView otpHeading;
    TextView otpMsg;
    Button verifyBtn;
    Button resendOtpBtn;
    ProgressDialog progressDialog1;
    ProgressDialog progressDialog2;

    FirebaseAuth fAuth;

    //The OTP code send by system.
    String verificationCode;


    private static String token;

    public static String get_token(){
        return token;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        otpHeading = findViewById(R.id.otp_heading);
        otpView = findViewById(R.id.otp_input);
        otpMsg = findViewById(R.id.otp_msg);
        verifyBtn = findViewById(R.id.verify_btn);
        resendOtpBtn = findViewById(R.id.resend_otp_btn);

        fAuth = FirebaseAuth.getInstance();

        otpView.requestFocus();

        final String phone_number = getIntent().getStringExtra("phoneNo");

        //This function sends the verification code to user's phone
        sendVerificationCodeToUser(phone_number);

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                verifyBtn.setEnabled(true);
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = otpView.getText().toString();
                if(otp.length() != 6){
                    otpMsg.setText("Please enter valid OTP");
                    otpMsg.setVisibility(View.VISIBLE);
                    verifyBtn.setEnabled(false);
                    return;
                }
                verifyCode(otp);
            }
        });

        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCodeToUser(phone_number);
                Toast.makeText(VerifyOtp.this,
                        "OTP was resent",
                        Toast.LENGTH_SHORT).show();
                resendOtpBtn.setVisibility(View.GONE);
            }
        });
    }

    //This function requests the OTP to the given phone number.
    private void sendVerificationCodeToUser(final String phoneNumber) {
        //This will show progress bar
        progressDialog1 = new ProgressDialog(VerifyOtp.this);
        progressDialog1.show();
        progressDialog1.setContentView(R.layout.activity_progress_dailog);
        progressDialog1.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60L,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    //When the otp code is sent to the phone
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        progressDialog1.dismiss();
                        otpHeading.setText("Please type the verification code sent to " + phoneNumber);

                    }

                    //When the otp is not entered in given time frame
                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        resendOtpBtn.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String code = phoneAuthCredential.getSmsCode();
                        if(code != null){

//Instead of directly verifying after getting the Otp,Instead let user verify with button click
//                            otpView.setText(code);
//                            verifyCode(code);

                            otpView.setText(code);
                            otpMsg.setText("OTP Retrieved, Verify to continue");
                            otpMsg.setVisibility(View.VISIBLE);
                        }
                    }


                    //When otp code could not be sent
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.d(TAG,"There was a problem!" + e.getMessage());

                        Intent loginIntent = new Intent(VerifyOtp.this,Login.class);
                        finish();
                        startActivity(loginIntent);


                        Toast.makeText(VerifyOtp.this,
                                "Please enter correct phone number,Please!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //This function is to verify the OTP code.
    //Is called when verify button is clicked.
    private void verifyCode(String code) {
        //This will show progress bar
        progressDialog2 = new ProgressDialog(VerifyOtp.this);
        progressDialog2.show();
        progressDialog2.setContentView(R.layout.activity_progress_dailog);
        progressDialog2.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,code);
        signInWithCredential(credential);
    }

    //This function is to sign In the user using credentials given.
    private void signInWithCredential(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    progressDialog2.dismiss();
                    getToken();

                    Toast.makeText(VerifyOtp.this,"Authentication is successfull",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Authentication is successfull");
                    Intent intent = new Intent(VerifyOtp.this,MainActivity.class);
                    finish();
                    startActivity(intent);

                }else {

                    progressDialog2.dismiss();

                    otpMsg.setText("Authentication is failed");
                    otpMsg.setVisibility(View.VISIBLE);

                    Toast.makeText(VerifyOtp.this,"Enter correct OTP.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Function to get user token for push notification
    public void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();

//                        Log.v(TAG,"The token from verify otp func is: " + token);

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG,msg);
                    }
                });
    }




    //BUG REPORT
    //AFTER ENTERING OTP AND SIGNING IN SUCCESSFULLY ,IF PRESSED BACK INSTEAD OF EXITING THE APP
    //IT GOES TO LOGIN ACTIVITY

    //22 aug
    //Pressing back takes to the same screen with previously entered values instead of exiting.


}