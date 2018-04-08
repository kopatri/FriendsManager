package com.abertay.friendsmanager;

import android.annotation.SuppressLint;
import android.util.Log;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Patrick Kornek on 07.04.2018.
 */

public class InputCheck  {

    private boolean areInputFieldsFilled(String friendName, String friendBirthday, String friendMobilPhone, String friendEmail) {
        if (friendName.equals("") || friendBirthday.equals("") || friendMobilPhone.equals("") || friendEmail.equals("")) {
            Log.d("Required Fields", "All fields are not filled");
            return false;
        } else {
            Log.d("Required Fields", "All fields filled");
            return true;
        }
    }

    private boolean isEmailValid(String friendEmail){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(friendEmail).matches();
    }

    private boolean isEmailAlreadyUsed(String friendEmail) {
        boolean checkEmail=false;
        ArrayList<Friend> friend = new ArrayList<>();
        try {
            friend = new AllFriendsTask().execute().get();
        } catch (InterruptedException e) {
            //Toast.makeText(getApplicationContext(),"Data transfer was interrupted: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ExecutionException e) {
           // Toast.makeText(getApplicationContext(),"Error: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        for(int i=0; i<friend.size(); i++){
            if(friendEmail.equals(friend.get(i).email)){
                checkEmail =  true;
            }
            else{
                checkEmail= false;
            }
        }
        return checkEmail;
    }

    private boolean isPhoneNumberAlreadyUsed(String friendMobilPhone) {
        boolean checkMobilePhone=false;
        ArrayList<Friend> friend = new ArrayList<>();
        try {
            friend = new AllFriendsTask().execute().get();
        } catch (InterruptedException e) {
           // Toast.makeText(getApplicationContext(),"Data transfer was interrupted: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ExecutionException e) {
            //Toast.makeText(getApplicationContext(),"Error: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        for(int i=0; i<friend.size(); i++){
            if(friendMobilPhone.equals(friend.get(i).mobilePhone)){
                checkMobilePhone =  true;
            }
            else{
                checkMobilePhone= false;
            }
        }
        return checkMobilePhone;
    }

    private boolean isPhoneNumberValid(String friendMobilPhone){
        String expression = "^\\+?\\(?[0-9]{1,3}\\)? ?-?[0-9]{1,3} ?-?[0-9]{3,5} ?-?[0-9]{4}( ?-?[0-9]{3})?";  //regular expression tool
        CharSequence inputString = friendMobilPhone;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches())
        {
            return false;
        }
        else{
            return true;
        }
    }

    private boolean isLegalDate(String friendBirthday) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat birthdayDate =new SimpleDateFormat ("dd/MM/yyyy");
        birthdayDate.setLenient(false);
        return birthdayDate.parse(friendBirthday, new ParsePosition(0)) != null;
    }

    private int parseDay(String friendBirthday) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date friendsDate = null;
        try {
            friendsDate = sdf.parse(friendBirthday);
        } catch (ParseException e) {
            //e.printStackTrace();   //Log handling
        }
        Date currentSystemDate = Calendar.getInstance().getTime();
        long diff =  currentSystemDate.getTime() - friendsDate.getTime();
        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        return numOfDays;
    }

    // Most common case, friend is older than system date
    //not older than oldest person of all time ~123 years (44895): https://en.wikipedia.org/wiki/List_of_the_verified_oldest_people
    private boolean isDateLogicForPast(String friendBirthday) {
        int numOfDays = parseDay(friendBirthday);
        return numOfDays<44895;
    }

    //friend is a new born baby, unlikely case, but possible
    private boolean isNewbornDate(String friendBirthday) {
        int numOfDays = parseDay(friendBirthday);
        return numOfDays==0;
    }

    //friend not born yet, not possible
    private boolean isFutureDate(String friendBirthday){
        Date currentSystemDate = Calendar.getInstance().getTime();
        if(friendBirthday.compareTo(String.valueOf(currentSystemDate))>0){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean emailAlreadyUsed(String email){
        boolean checkEmail=false;
        ArrayList<Friend> friend = new ArrayList<>();
        try {
            friend = new AllFriendsTask().execute().get();
        } catch (InterruptedException e) {
            //Toast.makeText(getApplicationContext(),"Data transfer was interrupted: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ExecutionException e) {
            //Toast.makeText(getApplicationContext(),"Error: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        for(int i=0; i<friend.size(); i++){
            if(email.equals(friend.get(i).email)){
                checkEmail =  true;
            }
            else{
                checkEmail= false;
            }
        }
        return checkEmail;
    }

    private boolean phoneNumberAlreadyUsed(String number){
        boolean checkMobilePhone=false;
        ArrayList<Friend> friend = new ArrayList<>();
        try {
            friend = new AllFriendsTask().execute().get();
        } catch (InterruptedException e) {
            //Toast.makeText(getApplicationContext(),"Data transfer was interrupted: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ExecutionException e) {
            //Toast.makeText(getApplicationContext(),"Error: "+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        for(int i=0; i<friend.size(); i++){
            if(number.equals(friend.get(i).mobilePhone)){
                checkMobilePhone =  true;
            }
            else{
                checkMobilePhone= false;
            }
        }
        return checkMobilePhone;
    }
}

