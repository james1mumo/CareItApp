package com.dekut.careitapp.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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


import com.dekut.careitapp.R;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        Intent intent=new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && requestCode==RESULT_OK){
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