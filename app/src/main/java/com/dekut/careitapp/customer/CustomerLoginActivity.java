package com.dekut.careitapp.customer;

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
import android.widget.Toast;

import com.dekut.careitapp.AboutActivity;
import com.dekut.careitapp.R;
import com.dekut.careitapp.technician.TechnicianLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerLoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonRegister, buttonAboutUs;
    private TextView textViewError;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonSignUp);
        buttonAboutUs = findViewById(R.id.buttonAboutUs);

        textViewError = findViewById(R.id.textViewError);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

//        editTextEmail.setText("james1mumo@gmail.com");
//        editTextPassword.setText("123456");

        textViewError.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), TechnicianLoginActivity.class));
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), CustomerRegistrationActivity.class));
            }
        });

        buttonAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), AboutActivity.class));

            }
        });
    }

    private void login() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            editTextEmail.setError("Required");
        }else if (TextUtils.isEmpty(password)){
            editTextPassword.setError("Required");
        }else if (password.length() < 6){
            Toast.makeText(getBaseContext(), "Password needs to be more than 6 characters", Toast.LENGTH_LONG).show();
            editTextPassword.setError("use > 6 chars");
        }else{
            progressDialog.setTitle("Please Wait...");
            progressDialog.setMessage("Checking Credentials");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Successfully Logged In",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), CustomerHomeActivity.class);
                                startActivity(intent);
                            } else {
                                //if there is an error it will be handled by addOnFailureListener
//                            Toast.makeText(getApplicationContext(),"Could not Sign In...",Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"ERROR occurred ",Toast.LENGTH_LONG).show();
                    textViewError.setText(e.toString());
                }
            });


        }



    }

}