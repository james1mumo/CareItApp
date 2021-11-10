package com.dekut.careitapp.technician;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dekut.careitapp.R;
import com.dekut.careitapp.customer.CustomerHomeActivity;
import com.dekut.careitapp.customer.CustomerLoginActivity;
import com.dekut.careitapp.models.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

public class TechnicianRegistrationActivity extends AppCompatActivity {
    private EditText editTextFirstName, editTextSecondName, editTextEmail, editTextMobile,
            editTextPassword, editTextPasswordConfirm;
    private Button buttonLogin, buttonRegister;
    private TextView textViewError;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_registration);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextSecondName = findViewById(R.id.editTextSecondName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonSignUp);
        textViewError = findViewById(R.id.textViewError);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), CustomerLoginActivity.class));
            }
        });
    }

    private void register() {
        String firstName = editTextFirstName.getText().toString();
        String secondName = editTextSecondName.getText().toString();
        String email = editTextEmail.getText().toString();
        String mobile = editTextMobile.getText().toString();
        String password = editTextPassword.getText().toString();
        String passwordConfirm = editTextPasswordConfirm.getText().toString();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(secondName) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm)){
            FancyToast.makeText(getApplicationContext(), "Fill all details first", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
        }else if (!password.equals(passwordConfirm)){
            FancyToast.makeText(getApplicationContext(), "Passwords do not match", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
            editTextPasswordConfirm.setError("not matching");
            editTextPasswordConfirm.requestFocus();
        }else {
            progressDialog.setTitle("Please Wait...");
            progressDialog.setMessage("Creating Account");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Technicians");
                        String uid = firebaseAuth.getUid();
                        Customer customer = new Customer(firstName,secondName,email,mobile,"");
                        if (uid != null){
                            databaseReference.child(uid).setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    FancyToast.makeText(getApplicationContext(), "Account Created Successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                                    startActivity(new Intent(getApplicationContext(), TechnicianHomeActivity.class));
                                    finish();
                                }
                            });
                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    FancyToast.makeText(getApplicationContext(), "Error Occurred", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                    textViewError.setText(e.toString());
                    textViewError.requestFocus();
                }
            });
        }
    }
}