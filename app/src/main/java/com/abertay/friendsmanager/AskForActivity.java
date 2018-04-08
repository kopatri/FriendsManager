package com.abertay.friendsmanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.abertay.friendsmanager.MainActivity.SAVED_TOTAL_FRIENDS;

public class AskForActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView image;
    TextView name,contactInformation;
    Spinner spinner;
    ScrollView scrollView;
    EditText message;
    ImageButton backButton, sendSMS, sendEmail;
    private String [] friendsName,friendsMobilePhone,friendsEmail;
    int localCounter;
    ArrayAdapter<String> adapter;
    FriendsDatabaseHelper friendsDatabaseHelper;
    ArrayList<Friend> allFriendsData;
    String currentNumber, currentEmail, currentName;
    final int PERMISSIONS_REQUEST_SMS=22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for);
        image=(ImageView)findViewById(R.id.imageAskForActivity);
        name=(TextView)findViewById(R.id.nameStatic);
        contactInformation=(TextView)findViewById(R.id.contactInformation);
        spinner=(Spinner)findViewById(R.id.spinner);
        scrollView=(ScrollView)findViewById(R.id.scrollView);
        message=(EditText)findViewById(R.id.message);
        sendSMS=(ImageButton) findViewById(R.id.sendSMS);
        sendEmail=(ImageButton) findViewById(R.id.sendEmail);
        backButton=(ImageButton)findViewById(R.id.backButton);
        friendsDatabaseHelper=  new FriendsDatabaseHelper(this);

        getFriendsData();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendsName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentNumber=friendsMobilePhone[position];
                currentEmail=friendsEmail[position];
                currentName=friendsName[position];
                contactInformation.setText(currentNumber+"\n"+currentEmail);
                message.setText("Hello " + currentName + ",");
                cursorRight();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentNumber=friendsMobilePhone[0];
                currentEmail=friendsEmail[0];
                currentName=friendsName[0];
                contactInformation.setText(currentNumber+"\n"+currentEmail);
                message.setText("Hello " + currentName + ",");
                cursorRight();
            }
        });

        requestPermissions();

        sendEmail.setOnClickListener(this);
        sendSMS.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.sendSMS:
            if(isMessageInputAvailable()){
                sendSms();
                finish();
            }
            break;
            case R.id.sendEmail:
                if(isMessageInputAvailable()){
                    sendEmail();
                    finish();
                }
                break;
            case R.id.backButton:
                finish();
                break;
        }
    }

    //Cursor after the text, right to the text
    public void cursorRight(){
        int endOfText = message.length();
        Editable etext = message.getText();
        Selection.setSelection(etext, endOfText);
    }

        //Check for input of message field
    public boolean isMessageInputAvailable(){
        String test = message.getText().toString();
        if(test.equals("Hello " + currentName + ",")){
            Toast.makeText(this,"Type some letters for your friend", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }



    //Get friends information, name, email, phone number
    public void getFriendsData(){

        SharedPreferences mainStats =getSharedPreferences("mainStats",0);
        localCounter = mainStats.getInt(SAVED_TOTAL_FRIENDS,0);

        //allFriendsData=friendsDatabaseHelper.getFriendsData(); possible but not good possibility

        try {
            allFriendsData = new AllFriendsTask().execute().get();
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(),"Data transfer was interrupted: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ExecutionException e) {
            Toast.makeText(getApplicationContext(),"Error: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        friendsName = new String[localCounter];
        friendsMobilePhone = new String[localCounter];
        friendsEmail = new String [localCounter];
        for(int i=0; i<localCounter; i++) {
            friendsName[i] = allFriendsData.get(i).name;
            friendsMobilePhone[i] = allFriendsData.get(i).mobilePhone;
            friendsEmail[i]=allFriendsData.get(i).email;
        }
    }


    //send SMS
    protected void sendSms() {
        String sendTo = currentNumber;
        String messageSMS = message.getText().toString();
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(sendTo, null, messageSMS, null, null);
            Toast.makeText(this, "SMS sent to " + sendTo, Toast.LENGTH_LONG).show();
            //Error handling-Email
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error, SMS failed", Toast.LENGTH_LONG).show();
                e.printStackTrace();
        }
        // some ErrorHandling better with Toast
    }

    //Email
    protected void sendEmail() {
        String To = currentEmail;
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, To);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Actvity request");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        //Error handling-Email
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            Toast.makeText(this,
                    "Error: "+ex, Toast.LENGTH_SHORT).show();
        }
    }


    //Although Galaxy S4 doesnt ask (less API), Permission check and request is done
    public void requestPermissions(){
        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if(check!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
                Toast.makeText(getApplicationContext(),"Permission needed to send SMS to your friend",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SMS);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSIONS_REQUEST_SMS:
                if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permission granted",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this,"Permission denied, sending SMS to friend not possible",Toast.LENGTH_LONG).show();
                    Intent goBackToMain = new Intent(this, MainActivity.class);
                    startActivity(goBackToMain);
                }
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AllFriendsTask extends AsyncTask<Void, Void, ArrayList<Friend>>{
        //https://stackoverflow.com/questions/9170228/android-asynctask-dialog-circle
        private ArrayList<Friend> friends;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Friend> doInBackground(Void... voids) {
            friends = new ArrayList <Friend>(friendsDatabaseHelper.getFriendsData());
            return friends;
        }
    }

}
