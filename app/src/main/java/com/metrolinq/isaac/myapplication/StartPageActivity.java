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
  ImageView chauffeur;
    Button bus;
    DatabaseReference clientDB;
    Button schedule;
    TextView privacyPolicy, logout;
    String locationName;
    SharedPreferences sp ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        bus = findViewById(R.id.bus);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        schedule = findViewById(R.id.chauffeur);
         logout = findViewById(R.id.logout);

         logout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 AuthUI.getInstance()
                         .signOut(StartPageActivity.this)
                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                             public void onComplete(@NonNull Task<Void> task) {
                                 // ...
                                 sp.edit().putBoolean("logged",false).apply();

                                 startActivity(new Intent(StartPageActivity.this, RegisActivity.class));


                             }
                         });
             }
         });

         ImageView line1 = findViewById(R.id.line_center_start);

        Animation animation_line = AnimationUtils.loadAnimation(this, R.anim.anim_left2right);


        line1.setAnimation(animation_line);

         schedule.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                 Toast.makeText(StartPageActivity.this, "Chauffeur Pressed: No Schedule Uploaded", Toast.LENGTH_SHORT).show();
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

               // choosePickLocation();

            }
        });
//        chauffeur.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                List<AuthUI.IdpConfig> providers = Arrays.asList(
//                        new AuthUI.IdpConfig.PhoneBuilder().build());
//
//// Create and launch sign-in intent
//                startActivityForResult(
//                        AuthUI.getInstance()
//                                .createSignInIntentBuilder()
//                                .setAvailableProviders(providers)
//                                .build(),
//                        RC_SIGN_IN);
//
//            }
//        });




    }



    private void choosePickLocation(){
        Toast.makeText(StartPageActivity.this, "AmendClick", Toast.LENGTH_SHORT).show();

        final String [] amendOption = {"Waikele Bus Stop", "Gerehu Stage 2 Main Bus Stop", "Rainbow Main Bus Stop"
                ,"Ensisi LTI Bus Stop","Waigani Main Bus Stop", "Sir Hubert Murray Bus Stop", "Defence Haus Town", "Cuthberthson Haus Town"};



        AlertDialog.Builder builder = new AlertDialog.Builder(StartPageActivity.this)  ;
        builder.setCancelable(true);
        builder.setSingleChoiceItems(amendOption, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(StartPageActivity.this, amendOption[which], Toast.LENGTH_SHORT).show();

                locationName = amendOption[which];


            }
        });



        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(StartPageActivity.this, "Cancel", Toast.LENGTH_SHORT).show();


            }
        });

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
