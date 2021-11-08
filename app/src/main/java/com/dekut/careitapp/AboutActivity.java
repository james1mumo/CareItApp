package com.dekut.careitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.shashank.sony.fancytoastlib.FancyToast;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    private String[] getMobileAndEmail(int id) {
        String mobile = "", email = "";

        if (id == R.id.paul_whatsapp || id == R.id.paul_phone || id == R.id.paul_gmail){
            mobile = "+254 795 005436";
            email = "paulbrian254@gmail.com";
        }else if (id == R.id.nzangi_whatsapp || id == R.id.nzangi_phone || id == R.id.nzangi_gmail){
            mobile = "+254 706306779";
            email = "paulbrian254@gmail.com";
        }else if (id == R.id.victor_whatsapp || id == R.id.victor_phone || id == R.id.victor_gmail){
            mobile = "+254 752564495";
            email = "paulbrian254@gmail.com";
        }else if (id == R.id.musili_whatsapp || id == R.id.musili_phone || id == R.id.musili_gmail){
            mobile = "+254 768687334";
            email = "paulbrian254@gmail.com";
        }else if (id == R.id.james_whatsapp || id == R.id.james_phone || id == R.id.james_gmail){
            mobile = "+254 746287172";
            email = "james1mumo@gmail.com";
        }

        return new String[]{mobile, email};

    }

    public void launchWhatsApp(View view) {
        String mobile = getMobileAndEmail(view.getId())[0];

        String url = "https://api.whatsapp.com/send?phone=" + mobile;
        try {
            PackageManager pm = getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            FancyToast.makeText(getApplicationContext(), "Opening WhatsApp", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
            startActivity(i);

        } catch (PackageManager.NameNotFoundException e) {
            FancyToast.makeText(getApplicationContext(), "WhatsApp app not installed in your phone", FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
            e.printStackTrace();
        }

    }

    public void launchPhone(View view) {
        String mobile = getMobileAndEmail(view.getId())[0];

        FancyToast.makeText(getApplicationContext(), "Opening Phone App", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();

    }

    public void launchGmail(View view) {
        String mobile = getMobileAndEmail(view.getId())[0];
        String email = getMobileAndEmail(view.getId())[1];


        FancyToast.makeText(getApplicationContext(), "Opening Gmail App", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();

    }
}