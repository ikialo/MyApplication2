package com.metrolinq.isaac.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegisActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 321;
    DatabaseReference clientDB;
    SharedPreferences sp ;
    Button continu;
    EditText phoneNumber;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;
    FirebaseAuth mAuth;

    Button button;
    EditText mobile;
    String no;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        mobile = findViewById(R.id.phone_et);
        button = findViewById(R.id.cont_btn);


        mAuth = FirebaseAuth.getInstance();

        sp = getSharedPreferences("login", MODE_PRIVATE);
        if (sp.getBoolean("logged", false)) {
            startActivity(new Intent(RegisActivity.this, StartPageActivity.class));

            finish();

        } else {


            mobile =  findViewById(R.id.phone_et);

            button = findViewById(R.id.cont_btn);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    no =  "+675"+mobile.getText().toString();
                    validNo(no);
                    Intent intent = new Intent(RegisActivity.this,VerifyActivity.class);
                    intent.putExtra("mobile",no);
                    startActivity(intent);
                    Toast.makeText(RegisActivity.this,no,Toast.LENGTH_LONG).show();
                }
            });


//            continu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                            phoneNumber.getText().toString(),        // Phone number to verify
//                            60,                 // Timeout duration
//                            TimeUnit.SECONDS,   // Unit of timeout
//                            this,               // Activity (for callback binding)
//                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                                @Override
//                                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//
//                                }
//
//                                @Override
//                                public void onVerificationFailed(FirebaseException e) {
//
//                                }
//                            }
//                    )       // OnVerificationStateChangedCallbacks
//                }
//            });

//        String phoneNum = "+67571201481";
//        String testVerificationCode = "123456";
//
//// Whenever verification is triggered with the whitelisted number,
//// provided it is not set for auto-retrieval, onCodeSent will be triggered.
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNum, 30L /*timeout*/, TimeUnit.SECONDS,
//                this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//                    @Override
//                    public void onCodeSent(String verificationId,
//                                           PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                        // Save the verification id somewhere
//                        // ...
//
//                        // The corresponding whitelisted code above should be used to complete sign-in.
//                       // MainActivity.this.enableUserManuallyInputCode();
//                        mVerificationId = verificationId;
//                    }
//
//                    @Override
//                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                        // Sign in with the credential
//                        // ...
//
//                        signInWithPhoneAuthCredential(phoneAuthCredential);
//                    }
//
//                    @Override
//                    public void onVerificationFailed(FirebaseException e) {
//                        // ...
//                    }
//
//                });
//
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, testVerificationCode);
//       // mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//            @Override
//            public void onVerificationCompleted(PhoneAuthCredential credential) {
//                // This callback will be invoked in two situations:
//                // 1 - Instant verification. In some cases the phone number can be instantly
//                //     verified without needing to send or enter a verification code.
//                // 2 - Auto-retrieval. On some devices Google Play services can automatically
//                //     detect the incoming verification SMS and perform verification without
//                //     user action.
//                Log.d("AUTH_TEST", "onVerificationCompleted:" + credential);
//
//                signInWithPhoneAuthCredential(credential);
//            }
//
//            @Override
//            public void onVerificationFailed(FirebaseException e) {
//                // This callback is invoked in an invalid request for verification is made,
//                // for instance if the the phone number format is not valid.
//                Log.w("AUTH_TEST", "onVerificationFailed", e);
//
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                    // ...
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                    // ...
//                }
//
//                // Show a message and update the UI
//                // ...
//            }
//
//            @Override
//            public void onCodeSent(String verificationId,
//                                   PhoneAuthProvider.ForceResendingToken token) {
//                // The SMS verification code has been sent to the provided phone number, we
//                // now need to ask the user to enter the code and then construct a credential
//                // by combining the code with a verification ID.
//                Log.d("AUTH_TEST", "onCodeSent:" + verificationId);
//
//                // Save verification ID and resending token so we can use them later
//                mVerificationId = verificationId;
//                mResendToken = token;
//
//                // ...
//            }
//        };


            // Choose authentication providers
//            List<AuthUI.IdpConfig> providers = Arrays.asList(
//                    new AuthUI.IdpConfig.EmailBuilder().build(),
//                    new AuthUI.IdpConfig.PhoneBuilder().build()
//                );
//
//// Create and launch sign-in intent
//            startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setAvailableProviders(providers)
//                            .build(),
//                    RC_SIGN_IN);
//            Log.d("AUTH_TESTS", "onCreate: Auth frag launch");
//
//
//        }
//
//        Toast.makeText(this, "out of if else", Toast.LENGTH_LONG).show();
//
//        clientDB = FirebaseDatabase.getInstance().getReference("RegisteredClients");
//        clientDB.child("initialvalue").child("phNum").setValue("10000001");


//

        }
    }

//
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("AUTH_TEST", "signInWithCredential:success");
//
//                            FirebaseUser user = task.getResult().getUser();
//                            // ...
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            Log.w("AUTH_TEST", "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                            }
//                        }
//                    }
//                });
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//
//            if (resultCode == RESULT_OK) {
//                // Successfully signed in
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                Toast.makeText(this,  user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
//                // ...
//            } else {
//                // Sign in failed. If response is null the user canceled the
//                // sign-in flow using the back button. Otherwise check
//                // response.getError().getErrorCode() and handle the error.
//                // ...
//            }
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("AUTH_TESTS", "onActivityResult: enterd onActivityResult");
//
//        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//
//            Toast.makeText(this, "RC_SIGN_IN", Toast.LENGTH_SHORT).show();
//            Log.d("AUTH_TESTS", "onCreate: RC_SIGN IN");
//
//            if (resultCode == RESULT_OK) {
//                // Successfully signed in
//                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                Toast.makeText(this, "RESULT_OK", Toast.LENGTH_SHORT).show();
//                Log.d("AUTH_TESTS", "onCreate: RESULT_OKAY");
//
//
//
//
////                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
////                        .setDisplayName("Test").build();
////
////                user.updateProfile(profileUpdates);
//
//
//                //Toast.makeText(this, user.updateProfile(new UserProfileChangeRequest().Builder().se), Toast.LENGTH_SHORT).show();
//
//                /*
//                 *
//                 *
//                 *   NEED TO GET THIS EVENTUALY INTO FIREBASE FUNCTIONS TO SAVE CLIENT DATA
//                 *
//                 *
//                 * */
//
//
//
//
//
//
//                clientDB.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//
//
//
//                            if (!user.getPhoneNumber().equals(postSnapshot.child("phNum").getValue().toString())){
//
//                                Toast.makeText(RegisActivity.this, "New CLient", Toast.LENGTH_SHORT).show();
//
//
//                                Intent intent = new Intent(RegisActivity.this, NewClientActivity.class);
//                                intent.putExtra("USER", user.getPhoneNumber());
//                                sp.edit().putBoolean("logged",true).apply();
//                                startActivity(intent);
//
//                               // finish();
//
//
//
//                            }
//                            else if(user.getPhoneNumber().equals(postSnapshot.child("phNum").getValue().toString())) {
//
//                                Intent intent = new Intent(RegisActivity.this,StartPageActivity.class);
//                                intent.putExtra("USER", user.getPhoneNumber());
//                                intent.putExtra("USER_INFO", new ClientInfo(
//                                        postSnapshot.child("firstName").getValue().toString(),
//                                        postSnapshot.child("lastName").getValue().toString(),
//                                        postSnapshot.child("firstName").getValue().toString() +" "+
//                                                postSnapshot.child("firstName").getValue().toString(),
//                                        postSnapshot.child("phNum").getValue().toString(),
//                                        postSnapshot.child("paytype").getValue().toString()
//                                ));
//                                sp.edit().putBoolean("logged",true).apply();
//
//                                startActivity(intent);
//                              //  finish();
//
//                                break;
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//
//                // ...
//            } else {
//                // Sign in failed. If response is null the user canceled the
//                // sign-in flow using the back button. Otherwise check
//                // response.getError().getErrorCode() and handle the error.
//                // ...
//
//
//                Toast.makeText(this, "FAIL", Toast.LENGTH_SHORT).show();
//                Log.d("AUTH_TESTS", "onCreate: RC_SIGN IN_FAIL");
//
//
//
//            }
//        }

    private void validNo(String no){
        if(no.isEmpty() || no.length() < 12){
            mobile.setError("Enter a valid mobile");
            mobile.requestFocus();
            return;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
