package com.abertay.friendsmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView latestFriend,lastDeletedFriend,totalFriends;

    //SharedPrefencesKeys for Stats
    public static final String SAVED_LATEST_FRIEND="latestFriendKey";
    public static final String SAVED_TOTAL_FRIENDS ="totalFriendsKey";
    public static final String SAVED_LAST_DELETED_FRIEND="lastDeletedFriendKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latestFriend=(TextView)findViewById(R.id.latestFriend);
        lastDeletedFriend=(TextView)findViewById(R.id.lastDeletedFriend);
        totalFriends=(TextView)findViewById(R.id.totalFriends);
        Button askForActivity = (Button) findViewById(R.id.askForActivity);
        Button showFriends=(Button)findViewById(R.id.showFriends);
        ImageButton addFriends=(ImageButton)findViewById(R.id.addFriends);

        askForActivity.setOnClickListener(this);
        showFriends.setOnClickListener(this);
        addFriends.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.askForActivity:
                if(numberOfFriends()!=0) {
                    Intent goToAskForActivity = new Intent(this,AskForActivity.class);
                    startActivity(goToAskForActivity);
                }
                else{
                    Toast.makeText(this,"No friends in friendslist",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.showFriends:
                if(numberOfFriends()!=0) {
                    Intent goToAllFriends = new Intent(this,AllFriends.class);
                    startActivity(goToAllFriends);
                }
                else{
                    Toast.makeText(this,"No friends in friendslist",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.addFriends:
                Intent goToAddFriendsActivity = new Intent(this,AddFriendsActivity.class);
                startActivity(goToAddFriendsActivity);
                break;
        }
    }

    //Methods that set Statistics for MainActivity
    private void setMainStats() {
       SharedPreferences mainStats = getSharedPreferences("mainStats",0);

        String localCounter ="Total friends\n"+numberOfFriends();
        totalFriends.setText(localCounter);

        String latestFriendLogicCheck = mainStats.getString(SAVED_LATEST_FRIEND,"No friends in friendslist");
        String lastDeletedFriendLogicCheck = mainStats.getString(SAVED_LAST_DELETED_FRIEND,"No friends in friendslist");

        latestFriend.setText(String.format("Latest friend\n%s", mainStats.getString(SAVED_LATEST_FRIEND, "No friends in friendslist")));

        lastDeletedFriend.setText(String.format("Last deleted friend\n%s", mainStats.getString(SAVED_LAST_DELETED_FRIEND, "No friends in friendslist")));

        //Logic Checks
        //more important logic checks at the end fo this Method
        //latest friends get deleted by user
        if(lastDeletedFriendLogicCheck.equals(latestFriendLogicCheck)){
            String delete = "You deleted your latest friend";
            latestFriend.setText(String.format("Latest friend\n%s", delete));
            SharedPreferences.Editor editor = mainStats.edit();
            editor.putString(SAVED_LATEST_FRIEND,delete);
            editor.apply();

        }
        //Added friends, deleted all, no more friends
        if(numberOfFriends()==0){
            latestFriend.setText("Latest friend\n No friends in friendslist");
        }
    }

    public int numberOfFriends(){
        SharedPreferences mainStats = getSharedPreferences("mainStats",0);
        return mainStats.getInt(SAVED_TOTAL_FRIENDS,0);
    }

    //When User returns, main Stats are updated
    @Override
    protected void onResume() {
        setMainStats();
        super.onResume();
    }

}
