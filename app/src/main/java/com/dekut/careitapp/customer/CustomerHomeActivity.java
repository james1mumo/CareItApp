package com.dekut.careitapp.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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


import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;
import com.dekut.careitapp.ChatActivity;
import com.dekut.careitapp.R;
import com.dekut.careitapp.technician.TechnicianLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerHomeActivity extends AppCompatActivity {
    private static final int AMOUNT = 100;
    private static final String TAG = "System";
    private static final int IMAGE_REQUEST = 2;
    private EditText serviceEditTxt,locationEditTxt, editTextMobile;
    private Button addButton,uploadButton;
    private Spinner locationSpinner;
    private ImageView serviceImage;
    private Uri uri;
    private String location;
    private FirebaseAuth firebaseAuth;
    private int CAMERA_REQUEST = 12;
    private Button buttonUpload, buttonHistory, buttonChats, buttonLogout;
    private ProgressDialog progressDialog;

    private Bitmap bitmap = null;

    Daraja daraja;
    String CONSUMER_KEY = "V20h0w0WUJUpZBLJFufR5XuScg6czdAQ";
    String CONSUMER_SECRET = "2k7901XCA24exJ6K";

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        serviceEditTxt=findViewById(R.id.serviceDesc);
        locationEditTxt=findViewById(R.id.serviceLocation);
        editTextMobile = findViewById(R.id.editTextMobile);
        serviceImage = findViewById(R.id.imageView);
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

        progressDialog = new ProgressDialog(this);

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

        //for Mpesa
        daraja = Daraja.with(CONSUMER_KEY, CONSUMER_SECRET, new DarajaListener<AccessToken>() {
            @Override
            public void onResult(@NonNull AccessToken accessToken) {
                Log.i(CustomerHomeActivity.this.getClass().getSimpleName(), accessToken.getAccess_token());
            }

            @Override
            public void onError(String error) {
                Log.e(CustomerHomeActivity.this.getClass().getSimpleName(), error);
            }
        });


        uploadButton.setOnClickListener(v -> {
            String serviceDescription=serviceEditTxt.getText().toString();
            String serviceLocation =locationEditTxt.getText().toString();
            String mobile = editTextMobile.getText().toString();

            System.out.println(serviceDescription);
                if(serviceDescription.isEmpty() || serviceLocation.isEmpty() || mobile.isEmpty()){
                    FancyToast.makeText(getApplicationContext(), "Fill All Details First", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }else if (bitmap == null){
                    FancyToast.makeText(getApplicationContext(), "Choose Image First", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }else{
                    makeMpesaPayment(mobile, String.valueOf(AMOUNT));
                }
        });
        serviceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    private void chooseImage() {
       ;if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            FancyToast.makeText(getApplicationContext(), "Allow Camera Permissions", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
            ActivityCompat.requestPermissions(this,  new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent , CAMERA_REQUEST);
        }
    }


    private void makeMpesaPayment(String mobile, String  amount){
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Processing MPesa Request");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        LNMExpress lnmExpress = new LNMExpress(
                "174379",
                "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",  //https://developer.safaricom.co.ke/test_credentials
                TransactionType.CustomerPayBillOnline,
                amount,
                mobile,
                "174379",
                mobile,
                "http://mycallbackurl.com/checkout.php",
                "Care It App",
                "Care It App"
        );

        //For both Sandbox and Production Mode
        daraja.requestMPESAExpress(lnmExpress,
                new DarajaListener<LNMResult>() {
                    @Override
                    public void onResult(@NonNull LNMResult lnmResult) {
                        Log.i(CustomerHomeActivity.this.getClass().getSimpleName(), lnmResult.ResponseDescription);
                        FancyToast.makeText(getApplicationContext(), lnmResult.ResponseDescription, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        progressDialog.dismiss();
                        uploadImage();

                    }

                    @Override
                    public void onError(String error) {
                        Log.i(CustomerHomeActivity.this.getClass().getSimpleName(), error);
                        FancyToast.makeText(getApplicationContext(), error, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        progressDialog.dismiss();
                    }
                }
        );
    }

    private void uploadImage() {
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Uploading Image To Firebase");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String fileName = System.currentTimeMillis()+".jpg";

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("Images").child(fileName);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        storageRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> saveToFirebase(uri.toString()));
            progressDialog.dismiss();
        }).addOnFailureListener(e -> {
            FancyToast.makeText(getApplicationContext(), e.toString(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            progressDialog.dismiss();
        });

        if(uri!= null) {
            StorageReference imageRef= FirebaseStorage.getInstance().getReference().child("Images").child(System.currentTimeMillis()+"."+getFileExtension(uri));
        }
    }

    private void saveToFirebase(String url){

        String serviceDescription=serviceEditTxt.getText().toString();
        String serviceLocation =locationEditTxt.getText().toString();
        String mobile = editTextMobile.getText().toString();

        HashMap<String,Object> map=new HashMap();
        map.put("serviceDescription",serviceDescription);
        map.put("serviceLocation",serviceLocation);
        map.put("userId",firebaseAuth.getUid());
        map.put("imageUrl",url);
        map.put("mobile",mobile);
        map.put("amount",AMOUNT);
        FirebaseDatabase.getInstance().getReference().child("Client Request").child("Request").push().setValue(map);
        FancyToast.makeText(this, "Upload made successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
        serviceEditTxt.setText("");
        locationEditTxt.setText("");
        editTextMobile.setText("");


        buttonUpload.setText("Upload Successfully made");
    }


    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_REQUEST && resultCode== Activity.RESULT_OK){
            bitmap = (Bitmap) data.getExtras().get("data");
            serviceImage.setImageBitmap(bitmap);
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
}