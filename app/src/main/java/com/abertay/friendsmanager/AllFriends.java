package com.abertay.friendsmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import static com.abertay.friendsmanager.MainActivity.SAVED_TOTAL_FRIENDS;

public class AllFriends extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    ImageButton backButton;
    TextView totalFriends;
    ArrayList<Friend> friends;
    int localCounter;
    ArrayAdapter<String> adapter;
    FriendsDatabaseHelper friendsDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_friends);
        listView = (ListView)findViewById(R.id.listView);
        backButton =(ImageButton)findViewById(R.id.backButton);
        totalFriends=(TextView)findViewById(R.id.totalFriends);
        friendsDatabaseHelper = new FriendsDatabaseHelper(this);

        updateTotalFriends();
        updateListView();

        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backButton:
                finish();
                break;
        }
    }

    //Update the frienscounter
    public void updateTotalFriends(){
        SharedPreferences mainStats =getSharedPreferences("mainStats",0);
        localCounter =mainStats.getInt(SAVED_TOTAL_FRIENDS,0);
        String showCounter = "Total Friends: "+localCounter;
        totalFriends.setText(showCounter);
    }

    public void updateListView(){
        updateTotalFriends();
        try {
            friends = new AllFriendsTask().execute().get();
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(),"Data transfer was interrupted: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ExecutionException e) {
            Toast.makeText(getApplicationContext(),"Error: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        String[] friendsName = new String[localCounter];
        for(int i=0; i<localCounter; i++){
            friendsName[i]=friends.get(i).name;
        }

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, friendsName);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToDetailFriendView = new Intent(getApplicationContext(),DetailFriendView.class);
                goToDetailFriendView.putExtra("Position",position);
                startActivity(goToDetailFriendView);
            }
        });
    }

    //keep friendslist updated
    @Override
    protected void onResume() {
        updateListView();
        super.onResume();
    }


    //Asynctask for database connection
    private class AllFriendsTask extends AsyncTask<Void, Void, ArrayList<Friend>> {

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
