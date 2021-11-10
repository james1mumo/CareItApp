package com.dekut.careitapp.customer;

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

import com.dekut.careitapp.ChatActivity;
import com.dekut.careitapp.R;
import com.dekut.careitapp.technician.TechnicianHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

public class CustomerChatActivity extends AppCompatActivity {
    ListView listView;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String user;
    private Button buttonUpload;

    ArrayList<String> arrayListTechniciansIds = new ArrayList<>();
    ArrayList<String> arrayListTechnicians = new ArrayList<>();
    ArrayList<String> arrayListMobile = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_chat);


        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getUid();
        listView = findViewById(R.id.listView);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), CustomerHomeActivity.class));
        });

        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Loading Chats");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //create an adapter
        CustomerChatsListAdapter customerChatsListAdapter = new CustomerChatsListAdapter(this, arrayListTechnicians, arrayListTechniciansIds, arrayListMobile, firebaseAuth.getUid());
        listView.setAdapter(customerChatsListAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(user)){
                    FancyToast.makeText(getApplicationContext(), "No Chats Found", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(),CustomerHomeActivity.class));
                }
                for (DataSnapshot chat : snapshot.child(user).getChildren()){
                    String technicianId = chat.getKey().toString();

                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Technicians").child(technicianId);
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            arrayListTechnicians.add(snapshot.child("firstName").getValue().toString()+" "+snapshot.child("secondName").getValue().toString());
                            arrayListMobile.add(snapshot.child("mobile").getValue().toString());
                            arrayListTechniciansIds.add(snapshot.getKey().toString());
                            customerChatsListAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }


                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();

            }
        });
    }
}

class CustomerChatsListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private  ArrayList<String> technicians, techniciansIds;
    private  ArrayList<String> mobiles;
    private String user;

    public CustomerChatsListAdapter(Activity context, ArrayList<String> technicians, ArrayList<String> techniciansIds, ArrayList<String> mobiles, String user) {
        super(context, R.layout.mylist, technicians);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.technicians =technicians;
        this.techniciansIds =techniciansIds;
        this.mobiles = mobiles;

        this.user = user;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView description = (TextView) rowView.findViewById(R.id.textViewDescription);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        Button buttonLocation = rowView.findViewById(R.id.buttonLocation);
        Button buttonChat = rowView.findViewById(R.id.buttonChat);

        buttonLocation.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        description.setText(technicians.get(position)+"\n"+mobiles.get(position));
//        imageView.setImageResource(imgid.get(position));

        String technician = techniciansIds.get(position);



        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("customer", user);
                intent.putExtra("technician", technician);
                intent.putExtra("isCustomer", true);
                context.startActivity(intent);
            }
        });
        return rowView;

    };
}