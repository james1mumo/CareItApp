package com.dekut.careitapp.technician;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dekut.careitapp.ChatActivity;
import com.dekut.careitapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

public class TechnicianHomeActivity extends AppCompatActivity {
    ListView listView;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_home);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getUid() == null){
            FancyToast.makeText(this, "Login First", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            startActivity(new Intent(this, TechnicianLoginActivity.class));
        }
        listView = findViewById(R.id.listView);
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<String> locations = new ArrayList<>();
        ArrayList<String > images = new ArrayList<>();
        ArrayList<String > users = new ArrayList<>();


        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Loading Customer Uploads");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Client Request").child("Request");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot request : snapshot.getChildren()){
                    descriptions.add(request.child("serviceDescription").getValue().toString());
                    locations.add(request.child("serviceLocation").getValue().toString());
                    images.add(request.child("imageUrl").getValue().toString());
                    users.add(request.child("userId").getValue().toString());
                }

                //create an adapter
                MyListAdapter myListAdapter = new MyListAdapter(TechnicianHomeActivity.this, descriptions, locations, images, users , firebaseAuth.getUid());
                listView.setAdapter(myListAdapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();

            }
        });
    }
}

class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> descriptions;
    private final ArrayList<String> locations;
    private final ArrayList<String> imgid;
    private final ArrayList<String> users;
    private final String technician;

    public MyListAdapter(Activity context, ArrayList<String> descriptions, ArrayList<String> locations,
                         ArrayList<String> imgid, ArrayList<String > users, String technician) {
        super(context, R.layout.mylist, descriptions);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.descriptions =descriptions;
        this.locations = locations;
        this.imgid=imgid;
        this.users=users;
        this.technician=technician;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylist, null,true);

        TextView description = (TextView) rowView.findViewById(R.id.textViewDescription);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        Button buttonLocation = rowView.findViewById(R.id.buttonLocation);
        Button buttonChat = rowView.findViewById(R.id.buttonChat);

        description.setText(descriptions.get(position));
        Glide.with(context).load(imgid.get(position)).into(imageView);

        String location = locations.get(position);
        String user = users.get(position);

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FancyToast.makeText(context, "Opening Google Maps", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q="+location.replace(" ", "+")));
                context.startActivity(intent);
            }
        });

        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("customer", user);
                intent.putExtra("technician", technician);
                intent.putExtra("isTechnician", true);
                context.startActivity(intent);
            }
        });
        return rowView;

    };
}