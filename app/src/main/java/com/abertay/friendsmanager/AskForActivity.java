package com.abertay.friendsmanager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import static com.abertay.friendsmanager.MainActivity.SAVED_TOTAL_FRIENDS;

public class AskForActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView image;
    TextView name,telephoneNumber;
    Spinner spinner;
    ScrollView scrollView;
    EditText message;
    Button sendSMS;
    ImageButton backButton;
    private String [] friendsName,friendsMobilePhone;
    int localCounter;
    ArrayAdapter<String> adapter;
    FriendsDatabaseHelper friendsDatabaseHelper;
    ArrayList<Friend> allFriendsData;
    String currentNumber;
    final int PERMISSIONS_REQUEST_SMS=22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for);
        image=(ImageView)findViewById(R.id.imageAskForActivity);
        name=(TextView)findViewById(R.id.nameStatic);
        telephoneNumber=(TextView)findViewById(R.id.telephoneNumber);
        spinner=(Spinner)findViewById(R.id.spinner);
        scrollView=(ScrollView)findViewById(R.id.scrollView);
        message=(EditText)findViewById(R.id.message);
        sendSMS=(Button)findViewById(R.id.sendSMS);
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
                telephoneNumber.setText(String.format("Mobile Phone Number\n%s", currentNumber));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentNumber=friendsMobilePhone[0];
                telephoneNumber.setText(String.format("Mobile Phone Number\n%s", currentNumber));
            }
        });
        requestPermissions();

        sendSMS.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.sendSMS:
            if(isMessageInputAvailable()){
                send();
                finish();
            }
            break;
        case R.id.backButton:
            finish();
            break;
        }
    }

        //Check for input of message field
    public boolean isMessageInputAvailable(){
        String test = message.getText().toString();
        if(test.equals("")){
            Toast.makeText(this,"Type some letters for your friend", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }

    //Get friends information
    public void getFriendsData(){
        SharedPreferences mainStats =getSharedPreferences("mainStats",0);
        localCounter = mainStats.getInt(SAVED_TOTAL_FRIENDS,0);
        allFriendsData =  friendsDatabaseHelper.getFriendsData();
        friendsName = new String[localCounter];
        friendsMobilePhone = new String[localCounter];
        for(int i=0; i<localCounter; i++) {
            friendsName[i] = allFriendsData.get(i).name;
            friendsMobilePhone[i] = allFriendsData.get(i).mobilePhone;
        }
    }

    //send SMS
    public void send(){
        String sendTo= currentNumber;
        String messageSMS = message.getText().toString();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sendTo,null,messageSMS, null,null);
        Toast.makeText(this,"SMS sent to "+sendTo, Toast.LENGTH_LONG).show();
    }

    //Although Galaxy S4 doesnt ask (less API), Permission check is done
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
}
