package com.metrolinq.isaac.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServiceLocationPick extends Service {

    String locationName;
    public ServiceLocationPick() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       locationName = intent.getExtras().getString("LOC_NAME");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PickUpLocation");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                    if (postSnapshot.getKey().equals(locationName)){
                        if (postSnapshot.getValue().equals(true)){
                            buildNotification();
                            locationName = "no Name";
                            break;
                        }
                    }

                }


//                Boolean notify = (Boolean) dataSnapshot.child("Map Clear").getValue();
//
//                if (!notify){
//                    buildNotification();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //buildNotification();



    }

    //Create the persistent notification//

    private void buildNotification() {

        String chanID;


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            chanID = createNotificationChannelId("My_service_3", "my_background_service_location");

        }
        else{
            chanID = "";
        }

        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       // Uri uri = Uri.parse("android.resource://"+this.getPackageName()+"/" + R.raw.mysound);

        NotificationCompat.Builder
                //   if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builder = new NotificationCompat.Builder(this, chanID)//Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Shuttle is now Approaching "+locationName)


                //Make this notification ongoing so it can’t be dismissed by the user//

                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.mcs);



        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            builder.setSound(alarmSound);
        }
        startForeground(1025, builder.build());

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannelId(String channelid, String channelName) {

        NotificationChannel chan = new NotificationChannel(channelid, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.YELLOW);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        chan.setShowBadge(true);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        chan.setSound(alarmSound,att);


        NotificationManager service =  getSystemService(NotificationManager.class);

        service.createNotificationChannel(chan);

        return channelid;


    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//Unregister the BroadcastReceiver when the notification is tapped//

            unregisterReceiver(stopReceiver);

//Stop the Service//

            //
            stopSelf();
        }
    };

}
