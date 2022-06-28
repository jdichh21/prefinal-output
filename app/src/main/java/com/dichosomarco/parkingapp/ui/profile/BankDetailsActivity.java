package com.dichosomarco.parkingapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dichosomarco.parkingapp.R;
import com.dichosomarco.parkingapp.classes.BankInfo;
import com.dichosomarco.parkingapp.classes.User;
import com.dichosomarco.parkingapp.utils.AppConstants;
import com.dichosomarco.parkingapp.utils.BasicUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BankDetailsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase db;

    AppCompatEditText bankIdText,bankNameText;
    Button bt_submit;

    BankInfo bankInfo;
    User userObj;
    String userID;

    BasicUtils utils=new BasicUtils();

    AppConstants globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        initComponents();
        attachListeners();
        if(!utils.isNetworkAvailable(getApplication())){
            Toast.makeText(BankDetailsActivity.this, "Network unavailable.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);  //slide from left to right
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);  //slide from left to right
    }

    private void initComponents() {
        getSupportActionBar().setTitle("Bank Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        auth= FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance();

        globalClass=(AppConstants)getApplicationContext();
        userID=auth.getCurrentUser().getUid();

        userObj=globalClass.getUserObj();

        bankIdText=findViewById(R.id.bankIdText);
        bankNameText=findViewById(R.id.bankNameText);
        bt_submit=findViewById(R.id.bt_submit);
    }

    private void attachListeners() {
        db.getReference().child("bankInfo").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bankInfo=snapshot.getValue(BankInfo.class);
                if(bankInfo!=null){
                    bankIdText.setText(bankInfo.bankId);
                    bankIdText.setSelection(bankIdText.getText().length());
                    bankNameText.setText(bankInfo.bankName);
                    bankNameText.setSelection(bankNameText.getText().length());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bankId = bankIdText.getText().toString();
                String bankName = bankNameText.getText().toString();

                if(bankId.isEmpty() || bankName.isEmpty()){
                    Toast.makeText(BankDetailsActivity.this, "Please fill all details!", Toast.LENGTH_SHORT).show();
                }else{
                    bankInfo=new BankInfo(bankId,bankName);
                    db.getReference("bankInfo")
                            .child(userID)
                            .setValue(bankInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(BankDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                finish();
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);  //slide from left to right
                            } else {
                                Toast.makeText(BankDetailsActivity.this, "Failed to add bank details", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}