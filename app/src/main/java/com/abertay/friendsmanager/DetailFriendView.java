package com.abertay.friendsmanager;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import static com.abertay.friendsmanager.MainActivity.SAVED_LAST_DELETED_FRIEND;
import static com.abertay.friendsmanager.MainActivity.SAVED_TOTAL_FRIENDS;

public class DetailFriendView extends AppCompatActivity implements View.OnClickListener {

    TextView name, birthday, mobilePhone, email;
    String currentName,currentBirthday,currentMobilePhone,currentEmail;
    ImageButton backButton,deleteFriendButton;
    int actualPosition;
    ArrayList<Friend> friend;
    FriendsDatabaseHelper friendsDatabaseHelper;

    @Override
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
        friend =  friendsDatabaseHelper.getFriendsData();

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
        friendsDatabaseHelper.removeFriends(toDelete);

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
}
