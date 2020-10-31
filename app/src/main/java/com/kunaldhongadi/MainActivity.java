package com.kunaldhongadi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.widget.NestedScrollView;

import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TAG";

    private static String uFullName,uNationality,uBirthCountry,uBirthDate,uPhoneNo,uEmailId,uAddress,uPostCode;
    private ListView listView1,listView2;
    TextView addDataMsg;
    ExtendedFloatingActionButton addData;
    MaterialToolbar toolbar;

    CoordinatorLayout bodyLayout;
    LinearLayout mainLayout;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    String token;

    ProgressDialog progressDialog1;

    NestedScrollView nestedScrollView;

    //If user has entered/has the data on the app
    private static Boolean hasData = false;

    public static Boolean getHasData() {
        return hasData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.top_toolBar);
        setSupportActionBar(toolbar);
        addData = findViewById(R.id.add_data_fab);
        addDataMsg = findViewById(R.id.add_data_msg);

        bodyLayout = findViewById(R.id.coordinator_layout);
        mainLayout = findViewById(R.id.list_views_layout);
        listView1 = findViewById(R.id.list_view1);
        listView2 = findViewById(R.id.list_view2);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getUid();

        nestedScrollView = findViewById(R.id.nested_scroll_view);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    addData.shrink();
                } else {
                    addData.extend();
                }
            }
        });
        //=========

        //Gets the user Entered data if any
        getUserData();


        //get token from verifyOtp activity
        token = VerifyOtp.get_token();
        Log.v(TAG,"The token from main activity is :" + token);

        if(token != null){
            //store to firestore
            sendToDb();
        }

        //Intent to the next activity to add the data
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //For the shared animation when fab clicked
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String>(addData,"add_data");

                Intent intent = new Intent(MainActivity.this,AddPersonalData.class);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                    startActivity(intent,options.toBundle());
                }else{
                    startActivity(intent);
                }

            }
        });

        //---------
        //Push Notifications

        //This creates a notification channel for version above 8
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("MyNotifications","MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("General")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successfull";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                        Log.d(TAG, msg);
                    }
                });
    }

    //This function gets data from firestore to display in list
    public void getUserData(){

        //This will show progress bar
        progressDialog1 = new ProgressDialog(MainActivity.this);
//        ----
//        Sprite doubleBounce = new DoubleBounce();
//        progressDialog1.setIndeterminateDrawable(doubleBounce);
//        =====

        progressDialog1.show();
        progressDialog1.setContentView(R.layout.activity_progress_dailog);
        progressDialog1.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);

        //Displaying the user input data
        DocumentReference docRef = fStore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {

//                    Toast.makeText(MainActivity.this, "REtrieved ....", Toast.LENGTH_SHORT).show();

                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        hasData = true;
                        hasDataStatus();
                        progressDialog1.dismiss();

                        uFullName = document.getString("full_name");
                        uNationality = document.getString("nationality");
                        uBirthCountry = document.getString("birth_country");
                        uBirthDate = document.getString("birth_date");
                        uPhoneNo = document.getString("phone_no");
                        uEmailId = document.getString("email_id");
                        uAddress = document.getString("address");
                        uPostCode = document.getString("post_code");

                        Log.v(TAG,"encrypted birth : " + uBirthDate);

                        String key = AESEncryption1.getKey();
                        Log.v(TAG,"key : " + key);

                        String encodedBaseKey = AESEncryption1.encodeKey(key);
                        Log.v(TAG,"Encoded key : " + encodedBaseKey);

                        String dec_fullName = AESEncryption1.decrypt(uFullName,encodedBaseKey);
                        String dec_nationality = AESEncryption1.decrypt(uNationality,encodedBaseKey);
                        String dec_birthCountry = AESEncryption1.decrypt(uBirthCountry,encodedBaseKey);
                        String dec_birthDate = AESEncryption1.decrypt(uBirthDate,encodedBaseKey);
                        String dec_phoneNo = AESEncryption1.decrypt(uPhoneNo,encodedBaseKey);
                        String dec_emailId = AESEncryption1.decrypt(uEmailId,encodedBaseKey);
                        String dec_address = AESEncryption1.decrypt(uAddress,encodedBaseKey);
                        String dec_postCode = AESEncryption1.decrypt(uPostCode,encodedBaseKey);

                        Log.v(TAG,"decrypted name : " + dec_birthDate);

                        DataListView fullName = new DataListView("Full Name",dec_fullName);
                        DataListView nationality = new DataListView("Nationality",dec_nationality);
                        DataListView birthCountry = new DataListView("Country of Birth",dec_birthCountry);
                        DataListView birthDate = new DataListView("Birth Date",dec_birthDate);
                        DataListView phoneNo = new DataListView("Phone No",dec_phoneNo);
                        DataListView emailId = new DataListView("Email Address",dec_emailId);
                        DataListView address = new DataListView("Address",dec_address);
                        DataListView postCode = new DataListView("Post Code",dec_postCode);


                        final ArrayList<DataListView> personalList = new ArrayList<>();
                        personalList.add(fullName);
                        personalList.add(nationality);
                        personalList.add(birthCountry);
                        personalList.add(birthDate);

                        final ArrayList<DataListView> contactList = new ArrayList<>();
                        contactList.add(phoneNo);
                        contactList.add(emailId);
                        contactList.add(address);
                        contactList.add(postCode);

                        DataListAdapter adapter1 = new DataListAdapter(MainActivity.this,
                                R.layout.activity_list_view_layout,personalList);
                        listView1.setAdapter(adapter1);

                        DataListAdapter adapter2 = new DataListAdapter(MainActivity.this,
                                R.layout.activity_list_view_layout,contactList);
                        listView2.setAdapter(adapter2);

                        ListUtils.setDynamicHeight(listView1);
                        ListUtils.setDynamicHeight(listView2);


                    } else {
                        Log.d(TAG, "No such document");

                        hasDataStatus();
                        progressDialog1.dismiss();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                    hasDataStatus();
                    progressDialog1.dismiss();
                }
            }
        });
    }

    //Settings menu for toolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu,menu);
        return true;
    }

    //Onclick listeners for the menu items
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

//-----------------------------
//            For Dark Mode
//------------------------------
//            case R.id.dark_mode:
//                //Check if already checked
//                if(item.isChecked()){
//                    item.setChecked(false);
//                    Toast.makeText(MainActivity.this,
//                            "Light Mode Activated",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    item.setChecked(true);
//                    Toast.makeText(MainActivity.this,
//                            "Dark Mode Activated",
//                            Toast.LENGTH_SHORT).show();
//                }
//                Log.v(TAG,"Dark mode selected");
//                return true;


            case R.id.retrievals_info:
                Intent intent = new Intent(MainActivity.this, Retrievals.class);
                startActivity(intent);
                return true;

            //when Delete data option clicked.
            case R.id.delete_data:
                MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(MainActivity.this);
                builder1.setTitle("Delete Data");
                builder1.setMessage("Are you sure you want to delete all your data?");
                builder1.setIcon(R.drawable.ic_delete_icon);
                builder1.setBackground(getResources().getDrawable(R.drawable.dailog_box_bg));

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.

                builder1.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //If the user doesn't have data,toast a message to add first
                        if(!hasData){
                            Toast.makeText(MainActivity.this,
                                    "Add some data before deleting!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        fStore.collection("users").document(userId)
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d(TAG,"Data for user " + userId + "Deleted");
                                        Toast.makeText(MainActivity.this,
                                                "Your Data was Deleted",Toast.LENGTH_SHORT).show();
                                        hasData = false;
                                        hasDataStatus();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this,
                                                "There was an error deleting the data",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                // A null listener allows the button to dismiss the dialog and take no further action.
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder1.show();
                return true;


            //When clicked on logoff option
            case R.id.log_off:
                MaterialAlertDialogBuilder builder2 = new MaterialAlertDialogBuilder(MainActivity.this);
                builder2.setTitle("Log Off");
                builder2.setMessage("Are you sure you want to Log Off?");
                builder2.setIcon(R.drawable.ic_alert_icon);
                builder2.setBackground(getResources().getDrawable(R.drawable.dailog_box_bg));

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                builder2.setPositiveButton("Log Off", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fAuth.signOut();
                        Log.v(TAG,"User logged off");
                        Intent loginIntent = new Intent(MainActivity.this,Login.class);
                        startActivity(loginIntent);
                        finish();
                    }
                });
                // A null listener allows the button to dismiss the dialog and take no further action.
                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder2.show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }


    //This function checks if the user has data on the app
    public void hasDataStatus(){

        if(hasData) {
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            params.weight = 1.0f;
//            params.gravity = Gravity.END|Gravity.BOTTOM;
//
//            addData.setLayoutParams(params);

            ((CoordinatorLayout.LayoutParams) addData.getLayoutParams()).gravity = Gravity.END|Gravity.BOTTOM;
            addData.setText("Update Data");
            addData.setIconResource(R.drawable.ic_edit_data_icon);
            addData.setVisibility(View.VISIBLE);

            addDataMsg.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);

        }else{

            ((CoordinatorLayout.LayoutParams) addData.getLayoutParams()).gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
            addData.setText("Add Data");
            addData.setIconResource(R.drawable.ic_add_icon);

            addDataMsg.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
            addData.setVisibility(View.VISIBLE);
        }

    }


    //Function to store token in the firestore
    public void sendToDb(){

        DocumentReference documentReference = fStore.collection("users").document(userId).
                collection("service").document("retrieved-details");

        Map<String,Object> services = new HashMap<>();
        services.put("user-token",token);

        documentReference.set(services).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG,"user-token updated :" + userId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG,"retrieved details couldn't be updated: " + userId);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.v(TAG,"hasData"+ hasData);
//        hasDataStatus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}