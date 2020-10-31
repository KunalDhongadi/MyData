package com.kunaldhongadi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MyMessagingService extends FirebaseMessagingService {
    public static final String TAG = "TAG";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getClickAction());
//        showNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"));

    }


    //---------
    //Push Notifications


    public void showNotification(String title,String message,String click_action){
        Intent intent;
        if(click_action.equals("ACTIVITYRETRIEVAL")){
            intent = new Intent(this,Retrievals.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }else{
            intent = new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this,99,intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"MyNotifications")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_logo_icon)
                .setAutoCancel(true)
                .setContentText(message)
                .setContentIntent(pendingIntent);;

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(99,builder.build());

    }



}
