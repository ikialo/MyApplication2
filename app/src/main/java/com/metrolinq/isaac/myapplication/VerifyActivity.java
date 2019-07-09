package com.metrolinq.isaac.myapplication;

import android.app.Activity;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
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

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {


    EditText otp;
    Button login;
    String no;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    SharedPreferences sp ;
    public static Activity fa;

    DatabaseReference clientDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);



        sp = getSharedPreferences("login", MODE_PRIVATE);

        otp = (EditText) findViewById(R.id.otp);

        mAuth = FirebaseAuth.getInstance();

        no = getIntent().getStringExtra("mobile");

        sendVerificationCode(no);

        login = (Button) findViewById(R.id.login);
        clientDB = FirebaseDatabase.getInstance().getReference("RegisteredClients");
       clientDB.child("initialvalue").child("phNum").setValue("10000001");


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = otp.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    otp.setError("Enter valid code");
                    otp.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);



            }
        });
    }


    private void sendVerificationCode(String no) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                 no,
                30,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                otp.setText(code);
                //verifying the code
                verifyVerificationCode(code);


            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
//                            Intent intent = new Intent(VerifyActivity.this, StartPageActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);


                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                            clientDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {



                                   if(user.getPhoneNumber().equals(postSnapshot.child("phNum").getValue().toString())) {

                                        Log.d("CHECK_USER", "onDataChange: " + postSnapshot.child("phNum").getValue().toString() + " exist");


                                        Intent intent = new Intent(VerifyActivity.this,StartPageActivity.class);
                                        intent.putExtra("USER", user.getPhoneNumber());
                                        intent.putExtra("USER_INFO", new ClientInfo(
                                                postSnapshot.child("firstName").getValue().toString(),
                                                postSnapshot.child("lastName").getValue().toString(),
                                                postSnapshot.child("firstName").getValue().toString() +" "+
                                                        postSnapshot.child("firstName").getValue().toString(),
                                                postSnapshot.child("phNum").getValue().toString(),
                                                postSnapshot.child("gender").getValue().toString()
                                        ));
                                        sp.edit().putBoolean("logged",true).apply();

                                        startActivity(intent);
                                        finish();

                                        break;
                                    }

                                    else if (!user.getPhoneNumber().equals(postSnapshot.child("phNum").getValue().toString())){

                                        // Toast.makeText(VerifyActivity.this, "New CLient", Toast.LENGTH_SHORT).show();

                                        Log.d("CHECK_USER", "onDataChange: " + postSnapshot.child("phNum").getValue().toString() + "not exist");


                                        Intent intent = new Intent(VerifyActivity.this, NewClientActivity.class);
                                        intent.putExtra("USER", user.getPhoneNumber());
                                        sp.edit().putBoolean("logged",true).apply();
                                        startActivity(intent);

                                        finish();



                                    }
                                }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }


                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity( new Intent( VerifyActivity.this, RegisActivity.class));
    }
}
