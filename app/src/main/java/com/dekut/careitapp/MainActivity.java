package com.dekut.careitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.dekut.careitapp.customer.CustomerHomeActivity;
import com.dekut.careitapp.customer.CustomerLoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(MainActivity.this, CustomerLoginActivity.class));
    }
}