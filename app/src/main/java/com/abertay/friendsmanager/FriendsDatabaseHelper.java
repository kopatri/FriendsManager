package com.abertay.friendsmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

/**
 * Created by Patrick Kornek on 28.02.2018.
 * Based on lecture documents, customized
 */

public class FriendsDatabaseHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION=1;
    private static final String DB_NAME="FriendsDB";
    private static final String FRIENDS_TABLE_NAME="friends";
    private static final String[] COLUMN_NAMES={"Name","Birthday","MobilePhone","Email"};

    private static final String FRIENDS_TABLE_CREATE =
            "CREATE TABLE " + FRIENDS_TABLE_NAME + " (" +
                    COLUMN_NAMES[0] + " TEXT, "+
                    COLUMN_NAMES[1] + " TEXT, "+
                    COLUMN_NAMES[2] + " TEXT, "+
                    COLUMN_NAMES[3] + " TEXT);";

    FriendsDatabaseHelper(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FRIENDS_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Method to add Friends
    public boolean addFriend(Friend f){
        ContentValues row = new ContentValues();
        row.put(this.COLUMN_NAMES[0], f.name);
        row.put(this.COLUMN_NAMES[1], f.birthday);
        row.put(this.COLUMN_NAMES[2], f.mobilePhone);
        row.put(this.COLUMN_NAMES[3], f.email);

        SQLiteDatabase db = this.getReadableDatabase();
        long result = db.insert(FRIENDS_TABLE_NAME, null, row);
        db.close();
        if(result== -1){
            return false;
        }
        else{
            return true;
        }
    }

    //Arraylist with data of all Friends
    public ArrayList getFriendsData(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor result = db.query(FRIENDS_TABLE_NAME,null,null,null,
                null,null,null);
        ArrayList<Friend> friends = new ArrayList<Friend>();
        for(int i=0;i<result.getCount();i++){
            result.moveToPosition(i);
            friends.add(new Friend(result.getString(0),result.getString(1),
                    result.getString(2),result.getString(3)));
        }
        return friends;
    }

    //Method to delete Friends
    public int removeFriends(Friend f){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "Name = ? AND Birthday = ? AND MobilePhone = ? AND Email = ?";
        String[] whereArgs ={f.name,f.birthday,f.mobilePhone,f.email};  //Where Args are same, will be deleted
        return  db.delete(FRIENDS_TABLE_NAME,whereClause,whereArgs);
    }
}
