package com.abertay.friendsmanager;

import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AllFriendsTask extends AsyncTask<Void, Void, ArrayList<Friend>> {
    FriendsDatabaseHelper friendsDatabaseHelper;
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
