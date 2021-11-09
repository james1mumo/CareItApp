package com.dekut.careitapp.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.dekut.careitapp.ChatActivity;
import com.dekut.careitapp.R;
import com.dekut.careitapp.technician.TechnicianLoginActivity;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerHomeActivity extends AppCompatActivity {
    private static final String TAG = "System";
    private static final int IMAGE_REQUEST = 2;
    private EditText serviceEditTxt,locationEditTxt;
    private Button addButton,uploadButton;
    private Spinner locationSpinner;
    private ImageView serviceImage;
    private Uri uri;
    private String location;
    private FirebaseAuth firebaseAuth;
    private int CAMERA_REQUEST = 12;

    private Button buttonUpload, buttonHistory, buttonChats, buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        serviceEditTxt=findViewById(R.id.serviceDesc);
        locationEditTxt=findViewById(R.id.serviceLocation);
        addButton=findViewById(R.id.chooseBtn);
//        locationSpinner=findViewById(R.id.locationspinner);
        uploadButton=findViewById(R.id.uploadBtn);
//        ArrayList<String> locations=new ArrayList<>();
//        locations.add("Choose YourLocation");
//        locations.add("Nyeri");
//        locations.add("Kirinyaga");
//        locations.add("Murang'a");
//        locations.add("Kiambu");
//        locations.add("Nairobi");
//
//        ArrayAdapter locationAdapter=new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,locations);
//        locationAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//        locationSpinner.setAdapter(locationAdapter);


        buttonUpload = findViewById(R.id.buttonUpload);
        buttonHistory = findViewById(R.id.buttonHistory);
        buttonChats = findViewById(R.id.buttonChats);
        buttonLogout = findViewById(R.id.buttonLogout);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            FancyToast.makeText(this, "Login First", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            startActivity(new Intent(this, CustomerLoginActivity.class));
        }


        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), CustomerLoginActivity.class));
            }
        });

        buttonChats.setOnClickListener(v -> {
            startActivity(new Intent( getApplicationContext(),CustomerChatActivity.class));
        });

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CustomerHistoryActivity.class));
            }
        });


        uploadButton.setOnClickListener(v -> {
            String serviceDescription=serviceEditTxt.getText().toString();
            String serviceLocation =locationEditTxt.getText().toString();

            System.out.println(serviceDescription);
                if(serviceDescription.isEmpty() || serviceLocation.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Fill all the text field",Toast.LENGTH_LONG).show();
                }
                else{
                    HashMap<String,Object> map=new HashMap();
                    map.put("serviceDescription",serviceDescription);
                    map.put("serviceLocation",serviceLocation);
                    map.put("userId",firebaseAuth.getUid());
                    FirebaseDatabase.getInstance().getReference().child("Client Request").child("Request").push().updateChildren(map);
                    Toast.makeText(getApplicationContext(),"Adding your claim",Toast.LENGTH_LONG).show();
                }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    private void chooseImage() {
       ;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent , CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && requestCode== Activity.RESULT_OK){
            uri=data.getData();
            getUploadImage();
        }
    }

    private void getUploadImage() {
        ProgressDialog progress=new ProgressDialog(this);
        progress.setTitle("Getting Upload Image");
        progress.show();

        if(uri!= null) {
            StorageReference imageRef= FirebaseStorage.getInstance().getReference().child("Images").child(System.currentTimeMillis()+"."+getFileExtension(uri));
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
}