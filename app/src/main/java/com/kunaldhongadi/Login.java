package com.kunaldhongadi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    public static final String TAG = "TAG";

    CountryCodePicker ccp;
    EditText phoneNo;
    TextView phoneNoMsg;
    Button submitBtn;

    FirebaseAuth auth;
    FirebaseUser fUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ccp = (CountryCodePicker)findViewById(R.id.ccp);
        phoneNo = findViewById(R.id.phone_no);
        phoneNoMsg = findViewById(R.id.phone_no_msg);
        submitBtn = findViewById(R.id.submit_btn);

        auth = FirebaseAuth.getInstance();
        fUser = auth.getCurrentUser();

        //when the user opens the app for the first time
        SharedPreferences preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        Boolean isFirstRun = preferences.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity
            MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(Login.this);
            builder1.setTitle("Welcome User!");
            builder1.setMessage("With MyData you enter your data only" +
                    " once which can be retrieved when filling out forms." +
                    " Login now and set up your MyData account in just few minutes.");
            builder1.setBackground(getResources().getDrawable(R.drawable.dailog_box_bg));
            builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder1.show();

            SharedPreferences preferences1 = getSharedPreferences("PREFERENCE",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences1.edit();
            editor.putBoolean("isFirstRun",false);
            editor.apply();
        }


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String getPhoneNumber = phoneNo.getText().toString().trim();

                if(TextUtils.isEmpty(getPhoneNumber)) {
                    phoneNoMsg.setText("Phone number cannot be empty :(");
                    phoneNoMsg.setVisibility(View.VISIBLE);
                    return;
                } else {
                    phoneNoMsg.setVisibility(View.GONE);
                }

                String phoneNumber = "+" + ccp.getSelectedCountryCode() + getPhoneNumber;
                Log.d(TAG,"Phone no:" + phoneNumber);

                Intent intent = new Intent(Login.this,VerifyOtp.class);

                //Passing phone number to the next activity.
                intent.putExtra("phoneNo",phoneNumber);

                startActivity(intent);

            }
        });
    }
}