package com.metrolinq.isaac.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class RegisActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 321;
    DatabaseReference clientDB;
    SharedPreferences sp ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        if(sp.getBoolean("logged",false)){
            startActivity(new Intent(RegisActivity.this, StartPageActivity.class));

        }
        else{
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.PhoneBuilder().build());

// Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);

        }


        clientDB = FirebaseDatabase.getInstance().getReference("RegisteredClients");


//

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


//                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                        .setDisplayName("Test").build();
//
//                user.updateProfile(profileUpdates);


                //Toast.makeText(this, user.updateProfile(new UserProfileChangeRequest().Builder().se), Toast.LENGTH_SHORT).show();

                /*
                 *
                 *
                 *   NEED TO GET THIS EVENTUALY INTO FIREBASE FUNCTIONS TO SAVE CLIENT DATA
                 *
                 *
                 * */



                clientDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                            if (!user.getPhoneNumber().equals(postSnapshot.child("phNum").getValue().toString())){


                                Intent intent = new Intent(RegisActivity.this, NewClientActivity.class);
                                intent.putExtra("USER", user.getPhoneNumber());
                                sp.edit().putBoolean("logged",true).apply();
                                startActivity(intent);



                            }
                            else if(user.getPhoneNumber().equals(postSnapshot.child("phNum").getValue().toString())) {

                                Intent intent = new Intent(RegisActivity.this,StartPageActivity.class);
                                intent.putExtra("USER", user.getPhoneNumber());
                                intent.putExtra("USER_INFO", new ClientInfo(
                                        postSnapshot.child("firstName").getValue().toString(),
                                        postSnapshot.child("lastName").getValue().toString(),
                                        postSnapshot.child("firstName").getValue().toString() +" "+
                                                postSnapshot.child("firstName").getValue().toString(),
                                        postSnapshot.child("phNum").getValue().toString(),
                                        postSnapshot.child("paytype").getValue().toString()
                                ));
                                startActivity(intent);
                                sp.edit().putBoolean("logged",true).apply();

                                break;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
