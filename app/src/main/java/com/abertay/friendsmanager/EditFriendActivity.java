package com.abertay.friendsmanager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static com.abertay.friendsmanager.MainActivity.SAVED_LATEST_FRIEND;

public class EditFriendActivity extends AppCompatActivity implements View.OnClickListener {

    EditText changeName, changeMobilePhone, changeBirthday, changeEmail;
    Friend oldToDelete, newToSave;
    ImageButton backButton,commitChangeButton;

    static  String oldName,oldMobilePhone,oldBirthday,oldEmail;
    String newName, newMobilePhone, newBirthday, newEmail;
    String selectedField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friend);
        changeName=(EditText)findViewById(R.id.name);
        changeMobilePhone = (EditText) findViewById(R.id.mobilePhone);
        changeBirthday = (EditText) findViewById(R.id.birthday);
        changeEmail = (EditText) findViewById(R.id.email);
        backButton = (ImageButton) findViewById(R.id.backButton);
        commitChangeButton = (ImageButton)findViewById(R.id.commitChange);

        getIntentData();
        showFriendToEdit();
        moveCursorToPosition(selectedField);

        backButton.setOnClickListener(this);
        commitChangeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch ((v.getId())){
            case R.id.backButton:
                getDataFromEditText();
                if(isInputChanged()) {
                    Toast.makeText(this, "Changes not saved", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            case R.id.commitChange:
                getDataFromEditText();
                if(isInputCorrect(newName, newMobilePhone, newBirthday, newEmail)){
                    newToSave = new Friend(newName, newBirthday, newMobilePhone, newEmail);
                    ChangeDataTask newData = new ChangeDataTask();
                    newData.execute(oldToDelete, newToSave);
                    updateMainStats();
                    finish();
                }
                break;
        }
    }

    public void getIntentData(){
            selectedField = getIntent().getStringExtra("EditSelectedField");
            oldName = getIntent().getStringExtra("currentName");
            oldBirthday= getIntent().getStringExtra("currentBirthday");
            oldMobilePhone= getIntent().getStringExtra("currentMobilePhone");
            oldEmail=getIntent().getStringExtra("currentEmail");
            oldToDelete = new Friend(oldName,oldBirthday,oldMobilePhone,oldEmail);
    }

    public void showFriendToEdit(){
        changeName.setText(oldName);
        changeBirthday.setText(oldBirthday);
        changeMobilePhone.setText(oldMobilePhone);
        changeEmail.setText(oldEmail);
    }

    public void getDataFromEditText(){
        newName= changeName.getText().toString();
        newBirthday= changeBirthday.getText().toString();
        newMobilePhone= changeMobilePhone.getText().toString();
        newEmail= changeEmail.getText().toString();
        Log.d("Changed", newName+" "+newBirthday+" "+newMobilePhone+" "+newEmail );
    }

    //Cursor after the text, right to the text which was touched
    public void moveCursorToPosition(String selectedField){
        switch(selectedField){
            case "nameEdit":
                changeName.requestFocus(changeName.length());
                break;
            case "birthdayEdit":
                changeBirthday.requestFocus(changeBirthday.length());
                break;
            case "mobilePhoneEdit":
                changeMobilePhone.requestFocus(changeMobilePhone.length());
                break;
            case "emailEdit":
                changeEmail.requestFocus(changeEmail.length());
                break;
            default:
                changeName.requestFocus(changeName.length());
                break;
        }
    }

    //checks if any input has changed
    public boolean isInputChanged(){
        return oldName!=newName && oldBirthday!=newBirthday
                && oldMobilePhone!=newMobilePhone && oldEmail!=newEmail;
    }

    //Logic check
    public boolean isInputCorrect(String newName, String newMobilePhone, String newBirthday, String newEmail){

        int checkValue=0; //8checks to pass
        InputCheck input = new InputCheck(this);

        //Inputcheck
        if(input.areInputFieldsFilled(newName,newBirthday,newMobilePhone,newEmail)){
            Log.d("Inputs filled", "true");
            ++checkValue;
        }
        else{
            Toast.makeText(this, "Complete all entries", Toast.LENGTH_SHORT).show();

        }

        //Emailcheck
        if(input.isEmailValid(newEmail)){
            Log.d("Email valid", "true");
            changeEmail.setTextColor(GREEN);
            ++checkValue;
        }
        else{
            changeEmail.setText("");
            changeEmail.setHint("Email not valid, try again");
            changeEmail.setHintTextColor(RED);
        }


        if (input.isEmailAlreadyUsedOldAllowed(oldEmail,newEmail)) {
            changeEmail.setText("");
            changeEmail.setHint("Email already used");
            changeEmail.setHintTextColor(RED);
        } else {
            Log.d("Email already used", "false");
            changeEmail.setTextColor(GREEN);
            ++checkValue;
        }

        //Phonecheck
        if(input.isPhoneNumberValid(newMobilePhone)){
            Log.d("Phone number valid", "true");
            changeMobilePhone.setTextColor(GREEN);
            ++checkValue;
        }
        else{
            changeMobilePhone.setText("");
            changeMobilePhone.setHint("Number not valid");
            changeMobilePhone.setHintTextColor(RED);
        }

        //new new mobile phone number has to be checked if other mobile phone numbers
        if (input.isPhoneNumberAlreadyUsedOldAllowed(oldMobilePhone,newMobilePhone)) {
            changeMobilePhone.setText("");
            changeMobilePhone.setHint("Number already used");
            changeMobilePhone.setHintTextColor(RED);
        } else {
            Log.d("Number already used", "false");
            changeMobilePhone.setTextColor(GREEN);
            ++checkValue;
        }

        //Datecheck, first validation of date then other checks
        if(input.isDateFormatValid(newBirthday)){
            Log.d("Valid Dateformat", "true");
            changeBirthday.setTextColor(GREEN);
            ++checkValue;
        }
        else{
            changeBirthday.setText("");
            changeBirthday.setHint("Date not valid d/m/yyyy");
            changeBirthday.setHintTextColor(RED);
            return false; //jump out to prevent Error in upcoming code below
        }

        if(input.isDateLogicForPast(newBirthday)){
            changeBirthday.setTextColor(GREEN);
            Log.d("Birthday not to old", "true");
            ++checkValue;
        }
        else{
            Toast.makeText(this, "Is your friend still alive?",Toast.LENGTH_LONG).show();
            changeBirthday.setText(""); //clean up for hint
            changeBirthday.setHint("Your friend cant be\n older than 123 years");
            changeBirthday.setHintTextColor(RED);

        }

        if(input.isFutureDate(newBirthday)){
            Toast.makeText(this, "Are you MartyMcFly?",Toast.LENGTH_LONG).show();
            changeBirthday.setText(""); //clean up for hint
            changeBirthday.setHint("Your friend is not born yet");
            changeBirthday.setHintTextColor(RED);
        }
        else{
            Log.d("Future birthday", "false");
            changeBirthday.setTextColor(GREEN);
            ++checkValue;
        }

        //Easter Egg if-statement
        if(input.isNewbornDate(newBirthday)){
            Log.d("Today born", "true");
            changeBirthday.setTextColor(GREEN);
            Toast.makeText(this, "BABY FRIENDSHIP FOREVER",Toast.LENGTH_LONG).show();
        }
        return checkValue == 8;
    }

    //Update latest Friend if latest friend is renamed
    public void updateMainStats(){
        SharedPreferences mainStats = getSharedPreferences("mainStats",0);
        String latestFriend = mainStats.getString(SAVED_LATEST_FRIEND,"No friends in friendslist");
        if(latestFriend.equals(oldName)){
            SharedPreferences.Editor editor = mainStats.edit();
            editor.putString(SAVED_LATEST_FRIEND,newName);
            editor.apply();
        }
    }

    //Database operation, change friends data
    @SuppressLint("StaticFieldLeak")
    public class ChangeDataTask extends AsyncTask<Object, Void, Void>{
        @Override
        protected Void doInBackground(Object ... params) {
            Friend oldToDelete = (Friend) params[0];
            Friend newToSave = (Friend) params[1];
            FriendsDatabaseHelper friendsDatabaseHelper = new FriendsDatabaseHelper(getApplicationContext());
            friendsDatabaseHelper.commitChanges(oldToDelete,newToSave);
            return null;
        }

    }
}
