package com.abertay.friendsmanager;
//Emailcheck: https://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
//Datecheck: https://stackoverflow.com/questions/5369682/get-current-time-and-date-on-android
//both sources are customized, other code is combined from different sources and customized

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static com.abertay.friendsmanager.MainActivity.SAVED_LATEST_FRIEND;
import static com.abertay.friendsmanager.MainActivity.SAVED_TOTAL_FRIENDS;

public class AddFriendsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView addNewFriend;
    EditText name,birthday,mobilePhone,email;
    String friendName,friendBirthday,friendMobilPhone,friendEmail;
    boolean logicDateCheck;
    FriendsDatabaseHelper friendsDatabaseHelper;

    //in case of friend will be edited
    int actualPosition;
    boolean editReason;
    String editName,editMobilePhone,editBirthday,editEmail;
    String selectedField;

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

        //in case of friend will be edited
        /*editReason = getIntent().getBooleanExtra("EditReason", false);
        if(editReason){
            getEditFriendData();
            showFriendToEdit();
            moveCursorToPosition(selectedField);
        }*/

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
                Toast.makeText(this,"Data not saved",Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }

    public void getEditFriendData(){
        if(editReason) {
            actualPosition = getIntent().getIntExtra("EditPosition", 0);
            selectedField = getIntent().getStringExtra("EditSelectedField");
            Log.i("selectedFieldToEdit",selectedField);
            editName = getIntent().getStringExtra("currentName");
            editBirthday= getIntent().getStringExtra("currentBirthday");
            editMobilePhone= getIntent().getStringExtra("currentMobilePhone");
            editEmail=getIntent().getStringExtra("currentEmail");
        }
    }

    public void showFriendToEdit(){
        name.setText(editName);
        birthday.setText(editBirthday);
        mobilePhone.setText(editMobilePhone);
        email.setText(editEmail);
    }

    //Cursor after the text, right to the text
    public void moveCursorToPosition(String selectedField){
        switch(selectedField){
            case "nameEdit":
                name.requestFocus(name.length());
                break;
            case "birthdayEdit":
                birthday.requestFocus(birthday.length());
                break;
            case "mobilePhoneEdit":
                mobilePhone.requestFocus(mobilePhone.length());
                break;
            case "emailEdit":
                email.requestFocus(email.length());
                break;
            default:
                name.requestFocus(name.length());
                break;
        }
    }

    //checks that all inputs are filled, if not user gets Toast to fullfil inputs
    private boolean isInputCorrect() throws ParseException {
        InputCheck input = new InputCheck();

        int checkValue = 0;
        friendName = name.getText().toString();
        friendBirthday= birthday.getText().toString();
        friendMobilPhone=mobilePhone.getText().toString();
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
            Log.d("Email check 1", "Email check passed");
            checkValue++;
        }
        else{
            String notValidMail ="Email not valid, try again";
            email.setText(""); //clean up for hint
            email.setHint(notValidMail);
            email.setHintTextColor(RED);
        }

        //Email already used check
        if(emailAlreadyUsed(friendEmail)){
            String notValidMail ="Email already used";
            email.setText(""); //clean up for hint
            email.setHint(notValidMail);
            email.setHintTextColor(RED);
        }
        else{
            email.setTextColor(GREEN);
            Log.d("Email check 2", "Email used check passed");
            checkValue++;
        }

        //checks if phone number is valid
        if(isPhoneNumberValid(friendMobilPhone)){
            String notValidPhoneNumber ="Number not valid";
            mobilePhone.setText(""); //clean up for hint
            mobilePhone.setHint(notValidPhoneNumber);
            mobilePhone.setHintTextColor(RED);
        }
        else{
            mobilePhone.setTextColor(GREEN);
            Log.d("Phone number 2", "check passed");
            checkValue++;
        }

        //Phone number already used check
        if(phoneNumberAlreadyUsed(friendMobilPhone)){
            String notValidPhoneNumber ="Number already used";
            mobilePhone.setText(""); //clean up for hint
            mobilePhone.setHint(notValidPhoneNumber);
            mobilePhone.setHintTextColor(RED);
        }
        else{
            mobilePhone.setTextColor(GREEN);
            Log.d("Phone number 2", "check passed");
            checkValue++;
        }

        //Date Format Check
        if(isLegalDate(friendBirthday)){
            birthday.setTextColor(GREEN);
            Log.d("Datecheck", "Date check passed");
            checkValue++;
        }
        else{
            String notValidDate ="Date not valid d/m/yyyy";
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
        return checkValue == 7;  //7checks to pass
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isLegalDate(String s) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat birthdayDate =new SimpleDateFormat ("dd/MM/yyyy");
        birthdayDate.setLenient(false);
        return birthdayDate.parse(s, new ParsePosition(0)) != null;
    }

    private boolean isDateLogic(String strDate) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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

    private boolean emailAlreadyUsed(String email){
        boolean checkEmail=false;
        ArrayList<Friend> friend = new ArrayList<>();
        try {
            friend = new AllFriendsTask().execute().get();
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(),"Data transfer was interrupted: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ExecutionException e) {
            Toast.makeText(getApplicationContext(),"Error: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        for(int i=0; i<friend.size(); i++){
            if(email.equals(friend.get(i).email)){
                checkEmail =  true;
            }
            else{
                checkEmail= false;
            }
        }
        return checkEmail;
    }

    private boolean isPhoneNumberValid(String number){
        String expression = "^\\+?\\(?[0-9]{1,3}\\)? ?-?[0-9]{1,3} ?-?[0-9]{3,5} ?-?[0-9]{4}( ?-?[0-9]{3})?";  //regular expression tool
        CharSequence inputString = number;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches())
        {
            return false;
        }
        else{
            return true;
        }
    }

    private boolean phoneNumberAlreadyUsed(String number){
        boolean checkMobilePhone=false;
        ArrayList<Friend> friend = new ArrayList<>();
        try {
            friend = new AllFriendsTask().execute().get();
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(),"Data transfer was interrupted: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ExecutionException e) {
            Toast.makeText(getApplicationContext(),"Error: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        for(int i=0; i<friend.size(); i++){
            if(number.equals(friend.get(i).mobilePhone)){
                checkMobilePhone =  true;
            }
            else{
                checkMobilePhone= false;
            }
        }
        return checkMobilePhone;
    }

    //adds friend to list
    private void saveFriend(){
        Friend friend = new Friend(friendName,friendBirthday,friendMobilPhone,friendEmail);
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
                Log.i("Success","Success");
                Toast.makeText(getApplicationContext(), "Friend successfully added", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AllFriendsTask extends AsyncTask<Void, Void, ArrayList<Friend>>{

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
