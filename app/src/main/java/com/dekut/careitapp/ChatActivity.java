package com.dekut.careitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import me.himanshusoni.chatmessageview.ChatMessageView;

public class ChatActivity extends AppCompatActivity {
    private EditText editTextMessage;
    private FloatingActionButton floatingActionButtonSendMessage;
    private String customer, technician;
    private boolean isCustomer = false, isTechnician = false;
    private ChatActivityAdapter chatActivityAdapter;
    private ArrayList<Map<String , String >> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        editTextMessage = findViewById(R.id.editTextMessage);
        floatingActionButtonSendMessage = findViewById(R.id.fabSendMessage);

        customer = getIntent().getStringExtra("customer");
        technician = getIntent().getStringExtra("technician");
        if (getIntent().hasExtra("isCustomer")) isCustomer = true;
        else isTechnician = true;

        floatingActionButtonSendMessage.setOnClickListener(v -> sendMessage());


        messages = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatActivityAdapter = new ChatActivityAdapter(this, messages);
        recyclerView.setAdapter(chatActivityAdapter);

        loadMessages();

    }

    private void sendMessage() {
        String message = editTextMessage.getText().toString();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
        String date = dateFormat.format(new Date());

        if (message.equalsIgnoreCase("")){
            FancyToast.makeText(getBaseContext(), "Enter message to send", FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(customer).child(technician);
        HashMap<String ,String > hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("date", date);
        if (isCustomer) hashMap.put("sender", "customer");
        else hashMap.put("sender", "technician");

        databaseReference.push().setValue(hashMap);

        HashMap<String ,String > hashMapMessage = new HashMap<>();
        hashMapMessage.put("message", message);
        hashMapMessage.put("date", date);
        if (isCustomer) hashMapMessage.put("sender", "customer");
        else hashMapMessage.put("sender", "technician");

        if (isCustomer) hashMapMessage.put("userType", "customer");
        else hashMapMessage.put("userType", "technician");


        messages.add(hashMapMessage);
        chatActivityAdapter.notifyDataSetChanged();

        editTextMessage.setText("");
        FancyToast.makeText(getBaseContext(), "Message send Successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS,false).show();


    }

    private void loadMessages(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(customer).child(technician);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot dataSnapshotMessage: snapshot.getChildren()){
                    HashMap<String ,String > hashMap = new HashMap<>();
                    hashMap.put("message", dataSnapshotMessage.child("message").getValue().toString());
                    hashMap.put("date", dataSnapshotMessage.child("date").getValue().toString());
                    hashMap.put("sender", dataSnapshotMessage.child("sender").getValue().toString());
                    if (isCustomer) hashMap.put("userType", "customer");
                    else hashMap.put("userType", "technician");

                    messages.add(hashMap);
                    chatActivityAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

class ChatActivityAdapter extends RecyclerView.Adapter<ChatActivityAdapter.ViewHolder>{

    private ArrayList<Map<String, String>> mData;
    private LayoutInflater mInflater;
    private ChatActivityAdapter.ItemClickListener mClickListener;
    private Map<String, String> message;
    private Context context;
    private String applicationID = "", applicationUsername = "";
    private ProgressDialog progressDialog;

    // data is passed into the constructor
    public ChatActivityAdapter(Context context, ArrayList<Map<String, String>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ChatActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_chat_message, parent, false);
        return new ChatActivityAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ChatActivityAdapter.ViewHolder holder, int position) {
        message = mData.get(position);


        if (!message.get("userType").equalsIgnoreCase(message.get("sender"))){
            holder.chatMessageView.setArrowPosition(ChatMessageView.ArrowPosition.LEFT);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.START;
            holder.chatMessageView.setLayoutParams(layoutParams);
            holder.linearLayoutMessage.setGravity(Gravity.START);


        }

        holder.textViewMessage.setText(message.get("message"));
        holder.textViewDate.setText(message.get("date"));



    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewMessage, textViewDate;
        ChatMessageView chatMessageView;
        LinearLayout linearLayoutMessage;

        ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            chatMessageView = itemView.findViewById(R.id.chatMessageView);
            linearLayoutMessage = itemView.findViewById(R.id.linearLayoutMessage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());

        }
    }

    // convenience method for getting data at click position
    Map<String, String> getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ChatActivityAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
