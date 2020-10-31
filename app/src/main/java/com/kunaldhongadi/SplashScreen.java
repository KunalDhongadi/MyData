package com.kunaldhongadi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class SplashScreen extends AppCompatActivity {
    public static final String TAG = "TAG";

    Animation topAnim;
    ImageView logoView;
    TextView logoText;

    //Declaring Firebase instances
    private FirebaseAuth fauth;
    private FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        logoView = findViewById(R.id.logo_view);
        logoText = findViewById(R.id.main_textView);

        //Initializing the instances
        fauth = FirebaseAuth.getInstance();
        fuser = fauth.getCurrentUser();

//        logoView.setAnimation(topAnim);
//        logoText.setAnimation(topAnim);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View,String>(logoView,"logo");
                pairs[1] = new Pair<View,String>(logoText,"logo_text");

//                Intent i = new Intent(SplashScreen.this,Login.class);
//                startActivity(i);
//                finish();

                if(fuser != null) {
                    //If user is logged in
                    Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                    nextIntent(mainIntent,pairs);

                }else if(fuser == null) {
                    //If user is not logged in
                    Intent loginIntent = new Intent(SplashScreen.this,Login.class);
                    nextIntent(loginIntent,pairs);
                }
            }
        },1000);

    }

    public void nextIntent(Intent intent,Pair[] pairs) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,pairs);
            startActivity(intent,options.toBundle());
        }else{
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }
}