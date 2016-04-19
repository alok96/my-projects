package com.app.zef.activityexample;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void goToWhatsapp(View v){
        Intent launchLink = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
        startActivity(launchLink);;

    }
    public void goToLinkedin(View v){
        try {
            Intent launchLink = getPackageManager().getLaunchIntentForPackage("com.linkedin.android");
            startActivity(launchLink);
        }
        catch (Exception e){
            String web= "http://www.linkedin.com";
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(web));
            startActivity(intent);
        }
    }
    public void goToGmail(View v) {
        try {
            Intent launchGmail = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");

            startActivity(launchGmail);
            }
        catch (Exception e){
            String web= "http://www.gmail.com";
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(web));
            startActivity(intent);
        }
    }
    public void goToFacebook(View v) {
        try {
            Intent launchFacebook = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");

            startActivity(launchFacebook);
        }
        catch (Exception e){
            String web= "http://www.facebook.com";
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(web));
            startActivity(intent);
        }
    }
    public void goToFbMessenger(View v){
        try {
            Intent launchMessenger = getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
            startActivity(launchMessenger);
        }
        catch (Exception e){
            String web= "https://m.facebook.com/messages";
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(web));
            startActivity(intent);
        }

    }














































    
    public void goToInstagram(View v){
       try{
        Intent launchInstagram = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        startActivity(launchInstagram);
    }
       catch (Exception e){
           String web= "https://www.instagram.com";
           Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(web));
           startActivity(intent);
       }
    }
    public void goToMap(View v){
        try{
            Intent launchMap = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
            startActivity(launchMap);
        }
        catch (Exception e){
            String web= "https://maps.google.com";
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(web));
            startActivity(intent);
        }
    }
    public void goToYoutube(View v){
        try{
            Intent launchYoutube = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
            startActivity(launchYoutube);
        }
        catch (Exception e){
            String web= "https://www.youtube.com";
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(web));
            startActivity(intent);
        }
    }
    public void goToTwitter(View v){
        try{
            Intent launchTwitter = getPackageManager().getLaunchIntentForPackage("com.twitter.android");
            startActivity(launchTwitter);
        }
        catch (Exception e){
            String web= "https://www.twitter.com";
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(web));
            startActivity(intent);
        }
    }
}
