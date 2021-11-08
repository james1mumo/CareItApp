package com.dekut.careitapp.technician;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dekut.careitapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TechnicianHomeActivity extends AppCompatActivity {
    ListView listView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_home);

        progressDialog = new ProgressDialog(this);

        listView = findViewById(R.id.listView);
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<String> locations = new ArrayList<>();
        ArrayList<String > images = new ArrayList<>();

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
                    images.add("my_image_url_here");
                }

                //create an adapter
                MyListAdapter myListAdapter = new MyListAdapter(TechnicianHomeActivity.this, descriptions, locations, images );
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

    public MyListAdapter(Activity context, ArrayList<String> descriptions, ArrayList<String> locations, ArrayList<String> imgid) {
        super(context, R.layout.mylist, descriptions);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.descriptions =descriptions;
        this.locations = locations;
        this.imgid=imgid;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView description = (TextView) rowView.findViewById(R.id.textViewDescription);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        TextView location = (TextView) rowView.findViewById(R.id.textViewLocation);

        description.setText(descriptions.get(position));
//        imageView.setImageResource(imgid.get(position));
        location.setText(locations.get(position));

        return rowView;

    };
}