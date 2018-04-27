package com.abertay.friendsmanager;

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
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static com.abertay.friendsmanager.MainActivity.SAVED_LATEST_FRIEND;
import static com.abertay.friendsmanager.MainActivity.SAVED_TOTAL_FRIENDS;

public class AddFriendsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView addNewFriend;
    EditText name,birthday,mobilePhone,email;
    String friendName,friendBirthday,friendMobilePhone,friendEmail;
    FriendsDatabaseHelper friendsDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        addNewFriend=(ImageView)findViewById(R.id.addNewFriend);
        name=(EditText)findViewById(R.id.nameAdd);
        birthday=(EditText)findViewById(R.id.birthdayAdd);
        mobilePhone=(EditText)findViewById(R.id.mobilePhoneAdd);
        email=(EditText)findViewById(R.id.emailAdd);
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
                    Toast.makeText(this,"Something went wrong with Birthday, try again",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.backButton:
                getInputsFromEditText();
                if(isAnythingInside()) {
                    Toast.makeText(this, "Data not saved", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
        }
    }

    private void getInputsFromEditText(){
        friendName = name.getText().toString();
        friendBirthday= birthday.getText().toString();
        friendMobilePhone=mobilePhone.getText().toString();
        friendEmail= email.getText().toString();
    }

    private boolean isAnythingInside(){
        return !friendName.equals("") || !friendBirthday.equals("")
                || !friendMobilePhone.equals("") || !friendEmail.equals("");
    }


    //checks that all inputs are filled, if not user gets Toast to fullfil inputs
    private boolean isInputCorrect() throws ParseException {
        getInputsFromEditText();
        int checkValue=0; //8checks to pass
        InputCheck input = new InputCheck(this);

        //Inputcheck
        if(input.areInputFieldsFilled(friendName,friendBirthday,friendMobilePhone,friendEmail)){
            Log.d("Inputs filled", "true");
            ++checkValue;
        }
        else{
            Toast.makeText(this, "Complete all entries", Toast.LENGTH_SHORT).show();
        }

        //Emailcheck
        if(input.isEmailValid(friendEmail)){
            Log.d("Email valid", "true");
            email.setTextColor(GREEN);
            ++checkValue;
        }
        else{
            email.setText("");
            email.setHint("Email not valid, try again");
            email.setHintTextColor(RED);
        }

        if(input.isEmailAlreadyUsed(friendEmail)){
            email.setText("");
            email.setHint("Email already used");
            email.setHintTextColor(RED);
        }
        else{
            Log.d("Email already used", "false");
            email.setTextColor(GREEN);
            ++checkValue;
        }

        //Phonecheck
        if(input.isPhoneNumberValid(friendMobilePhone)){
            Log.d("Phone number valid", "true");
            mobilePhone.setTextColor(GREEN);
            ++checkValue;
        }
        else{
            mobilePhone.setText("");
            mobilePhone.setHint("Number not valid");
            mobilePhone.setHintTextColor(RED);
        }

        if(input.isPhoneNumberAlreadyUsed(friendMobilePhone)){
            mobilePhone.setText("");
            mobilePhone.setHint("Number already used");
            mobilePhone.setHintTextColor(RED);
        }
        else{
            Log.d("MoNumber already used", "false");
            mobilePhone.setTextColor(GREEN);
            ++checkValue;
        }

        //Datecheck, first validation of date then other checks
        if(input.isDateFormatValid(friendBirthday)){
            Log.d("Valid Dateformat", "true");
            birthday.setTextColor(GREEN);
            ++checkValue;
        }
        else{
            birthday.setText("");
            birthday.setHint("Date not valid d/m/yyyy");
            birthday.setHintTextColor(RED);
            return false; //jump out to prevent Error in upcoming code below
        }

        if(input.isDateLogicForPast(friendBirthday)){
            birthday.setTextColor(GREEN);
            Log.d("Birthday not to old", "true");
            ++checkValue;
        }
        else{
            Toast.makeText(this, "Is your friend still alive?",Toast.LENGTH_LONG).show();
            birthday.setText(""); //clean up for hint
            birthday.setHint("Your friend cant be\n older than 123 years");
            birthday.setHintTextColor(RED);

        }

        if(input.isFutureDate(friendBirthday)){
            Toast.makeText(this, "Are you MartyMcFly?",Toast.LENGTH_LONG).show();
            birthday.setText(""); //clean up for hint
            birthday.setHint("Your friend is not born yet");
            birthday.setHintTextColor(RED);
        }
        else{
            Log.d("Future birthday", "false");
            birthday.setTextColor(GREEN);
            ++checkValue;
        }

        //Easter Egg if-statement
        if(input.isNewbornDate(friendBirthday)){
            Log.d("Today born", "true");
            birthday.setTextColor(GREEN);
            Toast.makeText(this, "BABY FRIENDSHIP FOREVER",Toast.LENGTH_LONG).show();
        }
        return checkValue == 8;
    }

    //adds friend to list
    private void saveFriend(){
        Friend friend = new Friend(friendName,friendBirthday,friendMobilePhone,friendEmail);
        updateMainStats();

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
    //Asynctasks for database operations

    private class AddFriendTask extends AsyncTask<Friend,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Friend... friends) {
            return friendsDatabaseHelper.addFriend(friends[0]);
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            super.onPostExecute(isSuccessful);
            if(isSuccessful){
                Log.i("Success","Success");
                Toast.makeText(getApplicationContext(), "Friend successfully added", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
