package com.kunaldhongadi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Retrievals extends AppCompatActivity {
    public static final String TAG = "TAG";

    MaterialToolbar toolbar;
    TextView retrievedMessage,serviceName,retrievedDate;
    RecyclerView recyclerView;

    MyRecyclerViewAdapter adapter;
    private ArrayList<Services> services = new ArrayList<>();

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;


    Boolean hasRetrievedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrievals);

        toolbar = findViewById(R.id.r_top_toolBar);
        retrievedDate = findViewById(R.id.retrieved_date);
        retrievedMessage = findViewById(R.id.retrieval_message);

        recyclerView = findViewById(R.id.r_recycler_view);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getUid();

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Retrievals.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        initRecyclerView();


//        services.add(new Services("kunal","jkfdjkdf"));
//        services.add(new Services("kunal","jkfdjkdf"));
//        services.add(new Services("kunal","jkfdjkdf"));
//        services.add(new Services("kunal","jkfdjkdf"));
//        services.add(new Services("kunal","jkfdjkdf"));
//        services.add(new Services("kunal","jkfdjkdf"));
//        services.add(new Services("kunal","jkfdjkdf"));
//        services.add(new Services("kunal","jkfdjkdf"));
//        services.add(new Services("kunal","jkfdjkdf"));
//        services.add(new Services("kunal","jkfdjkdf"));
//        services.add(new Services("kunal","jkfdjkdf"));
//        services.add(new Services("kunal","jkfdjkdf"));


//        retrievedMessage.setVisibility(View.GONE);
//        recyclerView.setVisibility(View.VISIBLE);


//        final DocumentReference documentReference1 = fStore.collection("users").document(userId).
//                collection("service").document("XYZBank");
////
//        documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot document = task.getResult();
//
////                    Log.v(TAG,"The retrievals class documemt" + document);
//                    if(document.exists()){
//                        if(document.getBoolean("hasRetrieved")){
//
//                            retrievedMessage.setVisibility(View.GONE);
//                            recyclerView.setVisibility(View.VISIBLE);
//
////                            services.add(new Services("some0","thing0"));
////                            getTimeStamp();
//
//                            List<Long> timeStamps = (List<Long>) document.get("time_stamp");
//                            Log.v(TAG,"The time stamp variable: " + timeStamps);
//
//                            Collections.reverse(timeStamps);
//
//                            //This list will store the formatted dates in an array
//                            List<String> formattedTimeStamp = new ArrayList<>();
//                            for(Long timestamp:timeStamps){
//                                SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy HH:mm");
//                                String dateString = formatter.format(new Date(timestamp));
//                                formattedTimeStamp.add(dateString);
//                                services.add(new Services("XYZBank",dateString));
//                            }
//                            //--------------------------------------------
//                        }
//
//                    }else{
//                        Toast.makeText(Retrievals.this,
//                                "No Retrievals yet", Toast.LENGTH_SHORT).show();
//                    }
//
//                }else{
//                    Toast.makeText(Retrievals.this,
//                            "There was a problem.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//

//        ================================
        final DocumentReference documentReference = fStore.collection("users").document(userId).
                collection("service").document("retrieved-list");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    Log.v(TAG,"The doc " + doc);

                    if(doc.exists()){

                        retrievedMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        List<Map<String,Long>> timeStamps = (List<Map<String,Long>>) doc.get("time_stamp");
//                        Log.v(TAG,"The time stamp variable: " + timeStamps);

                        Collections.reverse(timeStamps);

                        for(Map<String,Long> values :timeStamps){
//                            Log.v(TAG,"value: " + value.keySet());
                            for(String value: values.keySet()){
//                                Log.v(TAG,"k: " + i + ",v: " + value.get(i));
                                SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy HH:mm");
                                String dateString = formatter.format(new Date(values.get(value)));
                                services.add(new Services(value,dateString));
                            }
                            Log.v(TAG,"Working fine");
                        }
                    }else{
                        Toast.makeText(Retrievals.this,
                                "No Retrievals yet", Toast.LENGTH_SHORT).show();
                        Log.v(TAG,"There was an error:" + task.getException());
                    }
                }else{
                    Toast.makeText(Retrievals.this,
                            "There was a problem.", Toast.LENGTH_SHORT).show();
                    Log.v(TAG,"There was an error:" + task.getException());
                }
            }
        });



//        initRecyclerView();
    }

    //get time-stamp list
    public void getTimeStamp() {
        DocumentReference documentReference = fStore.collection("users").document(userId).
                collection("service").document("retrieved-list");

//        services.add(new Services("some2","thing2"));

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
//                    services.add(new Services("some3","thing3"));

                    if(doc.exists()){
                        List<Map<String,Long>> timeStamps = (List<Map<String,Long>>) doc.get("time_stamp");
//                        Log.v(TAG,"The time stamp variable: " + timeStamps);

//                        services.add(new Services("some4","thing4"));


                        for(Map<String,Long> values :timeStamps){
//                            Log.v(TAG,"value: " + value.keySet());
                            for(String value: values.keySet()){
//                                Log.v(TAG,"k: " + i + ",v: " + value.get(i));
                                SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy HH:mm");
                                String dateString = formatter.format(new Date(values.get(value)));
                                services.add(new Services(value,dateString));
//                                services.add(new Services("some5","thing5"));
                            }
                            Log.v(TAG,"Working fine");
                        }
                    }else{
                        Toast.makeText(Retrievals.this,
                                "No Retrievals yet", Toast.LENGTH_SHORT).show();
                        Log.v(TAG,"There was an error:" + task.getException());
                    }
                }else{
                    Toast.makeText(Retrievals.this,
                            "There was a problem.", Toast.LENGTH_SHORT).show();
                    Log.v(TAG,"There was an error:" + task.getException());
                }
//                services.add(new Services("some5","problem"));
            }
        });
    }


    //Initializing recycler view
    public void initRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new MyRecyclerViewAdapter(getApplicationContext(),services);
        recyclerView.setAdapter(adapter);
    }


    public void onScrolled(){
        final int SCROLL_DIRECTION_UP = -1;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

// ...
// Put this into your RecyclerView.OnScrollListener > onScrolled() method
                if (recyclerView.canScrollVertically(SCROLL_DIRECTION_UP)) {
                    // Remove elevation
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        toolbar.setElevation(0f);
                    }
                } else {
                    // Show elevation
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        toolbar.setElevation(50f);
                    }
                }
            }
        });

    }

}