package com.abertay.friendsmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import static com.abertay.friendsmanager.MainActivity.SAVED_LAST_DELETED_FRIEND;
import static com.abertay.friendsmanager.MainActivity.SAVED_TOTAL_FRIENDS;
// shows data of selected friend in detail, all information
public class DetailFriendView extends AppCompatActivity implements View.OnClickListener {

    TextView name, birthday, mobilePhone, email;
    String currentName,currentBirthday,currentMobilePhone,currentEmail;
    ImageButton backButton,deleteFriendButton;
    int actualPosition;
    ArrayList<Friend> friend;
    FriendsDatabaseHelper friendsDatabaseHelper;
    //for edit when user wants do change data
    String selectedField;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_friend_view);
        name = (TextView)findViewById(R.id.name);
        birthday = (TextView) findViewById(R.id.birthday);
        mobilePhone = (TextView) findViewById(R.id.mobilePhone);
        email = (TextView) findViewById(R.id.email);
        backButton = (ImageButton) findViewById(R.id.backButton);
        deleteFriendButton = (ImageButton) findViewById(R.id.deleteFriend);
        friendsDatabaseHelper = new FriendsDatabaseHelper(this);

        actualPosition = getIntent().getIntExtra("Position",0);
        showFriendInDetailView();

        backButton.setOnClickListener(this);
        deleteFriendButton.setOnClickListener(this);
        name.setOnClickListener(this);
        birthday.setOnClickListener(this);
        mobilePhone.setOnClickListener(this);
        email.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backButton:
                finish();
                break;
            case R.id.deleteFriend:
                deleteFriend();
                finish();
                break;
            case R.id.name:
                selectedField="nameEdit";
                fireIntentWithData();
                break;
            case R.id.birthday:
                selectedField="birthdayEdit";
                fireIntentWithData();
                break;
            case R.id.mobilePhone:
                selectedField="mobilePhoneEdit";
                fireIntentWithData();
                break;
            case R.id.email:
                selectedField="emailEdit";
                fireIntentWithData();
                break;
        }
    }

    public void showFriendInDetailView(){

        try {
            friend = new AllFriendsTask(this).execute().get();
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(),"Data transfer was interrupted: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ExecutionException e) {
            Toast.makeText(getApplicationContext(),"Error: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        currentName=friend.get(actualPosition).name;
        currentBirthday=friend.get(actualPosition).birthday;
        currentMobilePhone=friend.get(actualPosition).mobilePhone;
        currentEmail=friend.get(actualPosition).email;

        name.setText(String.format("Name\n%s", currentName));
        birthday.setText(String.format("Birthday\n%s", currentBirthday));
        mobilePhone.setText(String.format("Mobile Phone\n%s", currentMobilePhone));
        email.setText(String.format("Email\n%s", currentEmail));
    }

    public void deleteFriend(){

        Friend toDelete = new Friend(currentName,currentBirthday,currentMobilePhone,currentEmail);

        DeleteFriendTask delete = new DeleteFriendTask();
        delete.execute(toDelete);

        //friendsDatabaseHelper.removeFriends(toDelete);   also possbile but not good method

        //Update mainStats with SharedPrefences
        SharedPreferences mainStats = getSharedPreferences("mainStats", 0);
        SharedPreferences.Editor editor = mainStats.edit();
        editor.putString(SAVED_LAST_DELETED_FRIEND,currentName);

        int actualCounterPreference=mainStats.getInt(SAVED_TOTAL_FRIENDS,0);
        --actualCounterPreference;
        editor.putInt(SAVED_TOTAL_FRIENDS,actualCounterPreference);
        editor.apply();
    }

    @Override
    protected void onResume() {
        showFriendInDetailView();
        super.onResume();
    }


    public void fireIntentWithData(){
        //sending data about actual position and that user want to edit friend
        //Going to add FriendActivity
        Toast.makeText(getApplicationContext(), selectedField, Toast.LENGTH_LONG).show();
        Intent goToEditFriendActivity = new Intent(this,EditFriendActivity.class);
        goToEditFriendActivity.putExtra("currentName",currentName);
        goToEditFriendActivity.putExtra("currentBirthday",currentBirthday);
        goToEditFriendActivity.putExtra("currentMobilePhone",currentMobilePhone);
        goToEditFriendActivity.putExtra("currentEmail",currentEmail);
        goToEditFriendActivity.putExtra("actualPosition", actualPosition);
        goToEditFriendActivity.putExtra("EditSelectedField", selectedField);
        startActivity(goToEditFriendActivity);
    }

    //Asynctask transfer from and to database, delete friend
    @SuppressLint("StaticFieldLeak")
    private class DeleteFriendTask extends AsyncTask<Friend, Void, Void> {

        @Override
        protected Void doInBackground(Friend... friend) {
            friendsDatabaseHelper.removeFriends(friend[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
