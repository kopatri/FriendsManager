package com.abertay.friendsmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.abertay.friendsmanager.MainActivity.SAVED_LAST_DELETED_FRIEND;
import static com.abertay.friendsmanager.MainActivity.SAVED_TOTAL_FRIENDS;

public class DetailFriendView extends AppCompatActivity implements View.OnClickListener,GestureDetector.OnGestureListener, View.OnTouchListener {

    TextView name, birthday, mobilePhone, email;
    String currentName,currentBirthday,currentMobilePhone,currentEmail;
    ImageButton backButton,deleteFriendButton;
    int actualPosition;
    ArrayList<Friend> friend;
    FriendsDatabaseHelper friendsDatabaseHelper;
    RelativeLayout layoutDetailFriendView;
    private GestureDetectorCompat mDetector;

    //for edit when user wants do change data
    String selectedField;

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

        mDetector = new GestureDetectorCompat(this,this);
        backButton.setOnClickListener(this);
        deleteFriendButton.setOnClickListener(this);


        name.setOnTouchListener(this);
        birthday.setOnTouchListener(this);
        mobilePhone.setOnTouchListener(this);
        email.setOnTouchListener(this);

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
        }
    }

    public void showFriendInDetailView(){

        try {
            friend = new AllFriendsTask().execute().get();
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
        super.onResume();
    }

    //Gesture and methods which belongs to it
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Toast.makeText(getApplicationContext(), "onLongPress: Edit inputs", Toast.LENGTH_LONG).show();
        Intent goToEditFriendActivity = new Intent(this,EditFriendActivity.class);
        //sending data about actual postion and that user want to edit friend
        //Going to add FriendActivity

        goToEditFriendActivity.putExtra("currentName",currentName);
        goToEditFriendActivity.putExtra("currentBirthday",currentBirthday);
        goToEditFriendActivity.putExtra("currentMobilePhone",currentMobilePhone);
        goToEditFriendActivity.putExtra("currentEmail",currentEmail);
        goToEditFriendActivity.putExtra("actualPosition", actualPosition);
        goToEditFriendActivity.putExtra("EditSelectedField", selectedField);
        startActivity(goToEditFriendActivity);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //get selected field
        switch(v.getId()){
            case R.id.name:
                selectedField="nameEdit";
                break;
            case R.id.birthday:
                selectedField="birthdayEdit";
                break;
            case R.id.mobilePhone:
                selectedField="mobilePhoneEdit";
                break;
            case R.id.email:
                selectedField="emailEdit";
                break;
        }
        Log.i("Touched", selectedField);
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }


    //Asynctask transfer from and to database
    //get Data
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

    //delete friend
    @SuppressLint("StaticFieldLeak")
    private class DeleteFriendTask extends AsyncTask<Friend, Void, Void> {
        //FriendsDatabaseHelper friendsDatabaseHelper;

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
