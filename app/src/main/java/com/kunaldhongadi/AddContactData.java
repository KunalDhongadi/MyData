package com.kunaldhongadi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class AddContactData extends AppCompatActivity {
    public static final String TAG = "TAG";

    TextView cTitle;
    ImageView cCancelBtn;

    private TextInputLayout cPhoneNumber,cEmailId,cAddress,cPostCode;
    private TextInputEditText cEditPhoneNumber;
    Button cSubmitBtn;

    ProgressDialog progressDialog;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userPhoneNumber;
    //FirebaseUser fUser;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_data);

        cTitle = findViewById(R.id.c_title_text);
        cCancelBtn = findViewById(R.id.c_cancel_btn);
        cSubmitBtn = findViewById(R.id.c_submit_btn);

        cEditPhoneNumber = findViewById(R.id.c_edit_phone_no);
        cPhoneNumber = findViewById(R.id.c_phone_no);
        cEmailId = findViewById(R.id.c_email_address);
        cAddress = findViewById(R.id.c_address);
        cPostCode = findViewById(R.id.c_post_code);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        userPhoneNumber = fAuth.getCurrentUser().getPhoneNumber();


        if(MainActivity.getHasData()){
            cTitle.setText("Update Contact Data");
        }

        cEditPhoneNumber.setText(userPhoneNumber);
        //Getting data from AddPersonalData class.
        final String p_FullName = getIntent().getStringExtra("p_Full_Name");
        final String p_Nationality = getIntent().getStringExtra("p_Nationality");
        final String p_BirthCountry = getIntent().getStringExtra("p_birth_country");
        final String p_BirthDate = getIntent().getStringExtra("p_birth_date");


        cCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(AddContactData.this,MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                finish();
            }
        });

        cSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String c_PhoneNo = cPhoneNumber.getEditText().getText().toString().trim();
                String c_EmailId = cEmailId.getEditText().getText().toString().trim();
                String c_Address = cAddress.getEditText().getText().toString().trim();
                String c_PostCode = cPostCode.getEditText().getText().toString().trim();

                //Removing all errors before submitting again
                removeError(cPhoneNumber);
                removeError(cEmailId);
                removeError(cAddress);
                removeError(cPostCode);

                //Phone Number validation
                if (TextUtils.isEmpty(c_PhoneNo)) {
                    cPhoneNumber.setError("This field cannot be empty");
                    return;
                } else {
                    removeError(cPhoneNumber);
                }

                //Email id validation
                if (TextUtils.isEmpty(c_EmailId)) {
                    cEmailId.setError("This field cannot be empty");
                    return;
                } else {
                    removeError(cEmailId);
                }

                //Address validation
                if (TextUtils.isEmpty(c_Address)) {
                    cAddress.setError("This field cannot be empty");
                    return;
                } else {
                    removeError(cAddress);
                }

                //Post Code validation
                if (TextUtils.isEmpty(c_PostCode)) {
                    cPostCode.setError("This field cannot be empty");
                    return;
                } else {
                    removeError(cPostCode);
                }


                progressDialog = new ProgressDialog(AddContactData.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.activity_progress_dailog);
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent);

                String key = AESEncryption1.getKey();
                Log.v(TAG,"The key is: " + key);

                String encodedBaseKey = AESEncryption1.encodeKey(key);
                Log.v(TAG,"The encoded key is: " + encodedBaseKey);

                String enc_fullName = AESEncryption1.encrypt(p_FullName,encodedBaseKey);
                String enc_nationality = AESEncryption1.encrypt(p_Nationality,encodedBaseKey);
                String enc_birthCountry = AESEncryption1.encrypt(p_BirthCountry,encodedBaseKey);
                String enc_birthDate = AESEncryption1.encrypt(p_BirthDate,encodedBaseKey);
                String enc_phoneNumber = AESEncryption1.encrypt(c_PhoneNo,encodedBaseKey);
                String enc_emailAddress = AESEncryption1.encrypt(c_EmailId,encodedBaseKey);
                String enc_address = AESEncryption1.encrypt(c_Address,encodedBaseKey);
                String enc_postCode = AESEncryption1.encrypt(c_PostCode,encodedBaseKey);

                DocumentReference documentReference = fStore.collection("users").document(userId);

                Map<String,Object> user = new HashMap<>();
                user.put("full_name",enc_fullName);
                user.put("nationality",enc_nationality);
                user.put("birth_country",enc_birthCountry);
                user.put("birth_date",enc_birthDate);
                user.put("phone_no",enc_phoneNumber);
                user.put("email_id",enc_emailAddress);
                user.put("address",enc_address);
                user.put("post_code",enc_postCode);

                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getToken();

                        Log.v(TAG,"Data updated for user: " + userId);
                        Toast.makeText(AddContactData.this,"Data updated",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddContactData.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure: " + e.toString());
                        Toast.makeText(AddContactData.this,"Data could'nt be updated",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddContactData.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

    }


    //Function to set hasretrieved to false
    public void hasRetrieved(){
        //For Retrievals
        DocumentReference documentReference1 = fStore.collection("users").document(userId).
                collection("service").document("XYZBank");

        Map<String,Object> services = new HashMap<>();
        services.put("hasRetrieved",false);

        documentReference1.set(services).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG,"has Retrieved set to false :" + userId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG,"could'nt set to false : " + userId);
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
                        String token = task.getResult().getToken();

                        Log.v(TAG,"The token is: " + token);

                        //storing token to firebase
                        DocumentReference documentReference = fStore.collection("users").document(userId).
                                collection("service").document("retrieved-details");

                        Map<String,Object> services = new HashMap<>();
                        services.put("user-token",token);
//                        services.put("hasRetrieved",false);

                        documentReference.set(services).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.v(TAG,"user-token and hasRetrieved set to false :" + userId);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.v(TAG,"retrieved details couldn't be updated: " + userId);
                            }
                        });


                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG,msg);
                    }
                });
    }


    //Function to remove the previous errors
    public void removeError(TextInputLayout data){
        data.setError(null);
        data.setErrorEnabled(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}