package com.abertay.friendsmanager;
//Emailcheck: https://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
//Datecheck: https://stackoverflow.com/questions/5369682/get-current-time-and-date-on-android
//both sources are customized
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static com.abertay.friendsmanager.MainActivity.SAVED_LATEST_FRIEND;
import static com.abertay.friendsmanager.MainActivity.SAVED_TOTAL_FRIENDS;

public class AddFriendsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView addNewFriend;
    EditText name,birthday,mobilPhone,email;
    String friendName,friendBirthday,friendMobilPhone,friendEmail;
    boolean logicDateCheck;
    FriendsDatabaseHelper friendsDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        addNewFriend=(ImageView)findViewById(R.id.addNewFriend);
        name=(EditText)findViewById(R.id.name);
        birthday=(EditText)findViewById(R.id.birthday);
        mobilPhone=(EditText)findViewById(R.id.mobile);
        email=(EditText)findViewById(R.id.mail);
        ImageButton addFriends = (ImageButton) findViewById(R.id.addFriends);
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);

        friendsDatabaseHelper = new FriendsDatabaseHelper(this);

        addFriends.setOnClickListener(this);
        backButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addFriends:
                try {
                    if(isInputCorrect() ){
                        saveFriend();
                        finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(this,"Something went wrong with Birthday, try again",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.backButton:
                Toast.makeText(this,"Entries not saved",Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }

    //checks that all inputs are filled, if not user gets Toast to fullfil inputs
    private boolean isInputCorrect() throws ParseException {
        int checkValue = 0;
        friendName = name.getText().toString();
        friendBirthday= birthday.getText().toString();
        friendMobilPhone=mobilPhone.getText().toString();
        friendEmail=email.getText().toString();

        //Input fields filled check
        if(friendName.equals("")||friendBirthday.equals("")||friendMobilPhone.equals("")||friendEmail.equals("")){
            Toast.makeText(this, "Complete all entries", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            Log.d("Required Fields","All fields filled");
            checkValue++;
        }

        //Email validcheck
        if(isEmailValid(friendEmail)){
            email.setTextColor(GREEN);
            Log.d("Email check", "Email check passed");
            checkValue++;
        }
        else{
            String notValidMail ="Email not valid, try again";
            email.setText(""); //clean up for hint
            email.setHint(notValidMail);
            email.setHintTextColor(RED);
        }

        //Date Format Check
        if(isLegalDate(friendBirthday)){
            birthday.setTextColor(GREEN);
            Log.d("Datecheck", "Date check passed");
            checkValue++;
        }
        else{
            String notValidDate ="Date not valid dd/MM/yyyy";
            birthday.setText(""); //clean up for hint
            birthday.setHint(notValidDate);
            birthday.setHintTextColor(RED);
        }

        //Date Logic check
        if(isDateLogic(friendBirthday)){
            birthday.setTextColor(GREEN);
            Log.d("No foolish dates", "Date logic confirmed");
            checkValue++;
        }
        return checkValue == 4;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isLegalDate(String s) {
        SimpleDateFormat birthdayDate =new SimpleDateFormat ("dd/MM/yyyy");
        birthdayDate.setLenient(false);
        return birthdayDate.parse(s, new ParsePosition(0)) != null;
    }

    private boolean isDateLogic(String strDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date friendsDate = sdf.parse(strDate);
        Date currentSystemDate = Calendar.getInstance().getTime();

        long diff =  currentSystemDate.getTime() - friendsDate.getTime();
        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));

        // Most common case, friend is older than system date
        //not older than oldest person of all time ~123 years (44895): https://en.wikipedia.org/wiki/List_of_the_verified_oldest_people
        if(numOfDays<44895){
            birthday.setTextColor(GREEN);
            Log.d("Birthdaycheck", "Birthday check passed");
            logicDateCheck = true;
        }
        else{
            String notValidBirthday ="Your friend cant be\n older than 123 years";
            Toast.makeText(this, "Is you friend still alive?",Toast.LENGTH_LONG).show();
            birthday.setText(""); //clean up for hint
            birthday.setHint(notValidBirthday);
            birthday.setHintTextColor(RED);
            logicDateCheck = false;
        }

        //friend is a new born baby, unlikely case, but possible
        if(numOfDays==0){
            birthday.setTextColor(GREEN);
            Log.d("Birthdaycheck", "Birthday check passed");
            Toast.makeText(this, "BABY FRIENDSHIP FOREVER",Toast.LENGTH_LONG).show();
            logicDateCheck = true;
        }

        //friend not born yet, not possible
        if(friendsDate.compareTo(currentSystemDate)>0){
            String notValidBirthday ="Your friend is not born yet";
            Toast.makeText(this, "Are you MartyMcFly?",Toast.LENGTH_LONG).show();
            birthday.setText(""); //clean up for hint
            birthday.setHint(notValidBirthday);
            birthday.setHintTextColor(RED);
            logicDateCheck = false;
        }

        return logicDateCheck;
    }

    //adds friend to list
    private void saveFriend(){
        Friend friend = new Friend(friendName,friendBirthday,friendMobilPhone,friendEmail);

        //add Friend to database with asyncTask
        AddFriendTask addFriendTask = new AddFriendTask();
        addFriendTask.execute(friend);
    }

    private void updateMainStats() {

        SharedPreferences mainStats = getSharedPreferences("mainStats",0);
        SharedPreferences.Editor editor = mainStats.edit();
        editor.putString(SAVED_LATEST_FRIEND,friendName);

        int actualCounterPreference=mainStats.getInt(SAVED_TOTAL_FRIENDS,0);

        if(actualCounterPreference==0){
            editor.putInt(SAVED_TOTAL_FRIENDS,1);
        }
        else{
            ++actualCounterPreference;
            editor.putInt(SAVED_TOTAL_FRIENDS,actualCounterPreference);
        }
        editor.apply();
    }


    @SuppressLint("StaticFieldLeak")
    private class AddFriendTask extends AsyncTask<Friend,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Friend... friends) {
            return friendsDatabaseHelper.addFriend(friends[0]);
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            super.onPostExecute(isSuccessful);
            if(isSuccessful){
                updateMainStats();
                Toast.makeText(getApplicationContext(), "Friend successfully added", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
