package com.abertay.friendsmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

@SuppressLint("StaticFieldLeak")
public class AllFriendsTask extends AsyncTask<Void, Void, ArrayList<Friend>> {

    private Context mContext;

    AllFriendsTask(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Friend> doInBackground(Void... voids) {
        FriendsDatabaseHelper friendsDatabaseHelper = new FriendsDatabaseHelper(mContext);
        ArrayList<Friend> friends = new ArrayList<Friend>(friendsDatabaseHelper.getFriendsData());
        return friends;
    }
}
