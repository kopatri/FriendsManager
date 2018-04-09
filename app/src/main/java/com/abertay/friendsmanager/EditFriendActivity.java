package com.abertay.friendsmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditFriendActivity extends AppCompatActivity implements View.OnClickListener {

    EditText changeName, changeMobilePhone, changeBirthday, changeEmail;
    Friend oldToDelete, newToSave;
    ImageButton backButton, commitChangeButton;

    //in case of friend will be edited
    int actualPosition;
    String oldName,oldMobilePhone,oldBirthday,oldEmail;
    String newName, newMobilePhone, newBirthday, newEmail;
    String selectedField;


    //SharedPrefences if latestFriend is renamed
    //////////////////////////////////////////////
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



        //get Intentinputs

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
                Toast.makeText(this, "Possible changes not saved", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.commitChange:
                getEditedData();
                if(isInputCorrect(newName,newMobilePhone,newBirthday,newEmail)){
                    newToSave = new Friend(newName, newMobilePhone, newBirthday, newEmail);
                    ChangeDataTask newData = new ChangeDataTask();
                    newData.execute(oldToDelete, newToSave);
                    //rename latest Friend in Shared Prefences
                    finish();
                    //deletetheoldOne
                    //addthenewone
                    //or Update
                }
                //check duplicate Email and telephone with exception of previous use
                //also input checks for the EdittextField
                //delete old entry, commit new entry
                //go back to DetailfriendActivity and show change stats(on ActivityforResult or so)
                break;

        }
    }

    public void getIntentData(){
            actualPosition = getIntent().getIntExtra("EditPosition", 0);
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

    public void getEditedData(){
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

    public boolean isInputCorrect(String friendName, String friendMobilePhone, String friendBirthday, String friendEmail){


        return true;
    }


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
