package com.metrolinq.isaac.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class StartPageActivity extends AppCompatActivity {

    private final static int RC_SIGN_IN =400;

    Button bus;
    DatabaseReference clientDB;
    Button Chauffeur;
    TextView privacyPolicy, logout;

    SharedPreferences sharedPreferences;
    String locationName;
    FirebaseAuth auth;
    SharedPreferences sp ;
    private static final int PLACE_PICKER_REQUEST = 20;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        bus = findViewById(R.id.bus);

        auth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("isNewClient", MODE_PRIVATE);






        sp = getSharedPreferences("login",MODE_PRIVATE);

        Chauffeur = findViewById(R.id.chauffeur);
         logout = findViewById(R.id.logout);



         logout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 auth.signOut();
                 sp.edit().putBoolean("logged",false).apply();
                 FirebaseAuth.getInstance().signOut();


                 startActivity(new Intent(StartPageActivity.this, RegisActivity.class));

//                 AuthUI.getInstance()
//                         .signOut(StartPageActivity.this)
//                         .addOnCompleteListener(new OnCompleteListener<Void>() {
//                             public void onComplete(@NonNull Task<Void> task) {
//                                 // ...
//
//                             }
//                         });

             }
         });

         ImageView line1 = findViewById(R.id.line_center_start);

        Animation animation_line = AnimationUtils.loadAnimation(this, R.anim.anim_left2right);


        line1.setAnimation(animation_line);

         Chauffeur.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                // Toast.makeText(StartPageActivity.this, "Chauffeur Pressed: No Schedule Uploaded", Toast.LENGTH_SHORT).show();
                 startActivity(new Intent(StartPageActivity.this, MapsActivity2.class));
             }
         });


        privacyPolicy = findViewById(R.id.privacy_policy);

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/mcsmetroprivacypolicy/home"));
                startActivity(browserIntent);
            }
        });





        bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(StartPageActivity.this, "Bus Pressed", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(StartPageActivity.this, MapsActivity.class));



            }
        });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        sharedPreferences.edit().putBoolean("isNC", false);
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);



    }


}
