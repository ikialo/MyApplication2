package com.metrolinq.isaac.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewClientActivity extends AppCompatActivity {
    EditText phoneET, firstNameET,lastNameET, defaulPayType;
    Button register;
    RadioButton payType, post, pre;
    RadioGroup group;

    DatabaseReference addClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);


        final String phone = getIntent().getExtras().getString("USER");

        Toast.makeText(this, phone, Toast.LENGTH_SHORT).show();

        phoneET = findViewById(R.id.newPhoneNumber);
        firstNameET = findViewById(R.id.newFirstName);
        lastNameET = findViewById(R.id.newLastName);
        register = findViewById(R.id.registerBtn);

        addClient = FirebaseDatabase.getInstance().getReference("RegisteredClients");


        phoneET.setText(phone);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                group = findViewById(R.id.radioBut);

                int selectId = group.getCheckedRadioButtonId();

                payType = findViewById(selectId);

                Toast.makeText(NewClientActivity.this, payType.getText(), Toast.LENGTH_SHORT).show();


                ClientInfo clientInfo = new ClientInfo(firstNameET.getText().toString(), lastNameET.getText().toString(),
                        firstNameET.getText().toString()+" "+lastNameET.getText().toString(),phone, payType.getText().toString());

                String uploadId = addClient.push().getKey();


                addClient.child(uploadId).setValue(clientInfo);

                Intent intent = new Intent(NewClientActivity.this, MapsActivity2.class);
                startActivity(intent);

            }
        });


    }
}
