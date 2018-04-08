package com.abertay.friendsmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username,password;
    String usernameRestored,passwordRestored;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.secretWord);
        login=(Button)findViewById(R.id.login);

        checkRotation(usernameRestored,passwordRestored);

        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login:
                checkPassword();
                break;
        }
    }

    //Method checks password, if wrong both Edittext will be set back
    //if correct User gets to MainActivity
    public void checkPassword(){

        String u = username.getText().toString();
        String p = password.getText().toString();

        if(u.equals("patrick") && p.equals("abertay")){
            deleteInputs();
            Toast.makeText(this,"Successful",Toast.LENGTH_SHORT).show();
            Intent goToMainActivity = new Intent(this,MainActivity.class);
            startActivity(goToMainActivity);
        }
        else{
            Toast.makeText(this,"Failed, try again",Toast.LENGTH_LONG).show();
            deleteInputs();
        }
    }

    public void deleteInputs(){
        username.setText("");
        password.setText("");
    }

    //restore inputs fields
    public void checkRotation(String usernameRestored,String passwordRestored){
        if(usernameRestored != null){
            username.setText(usernameRestored);
        }
        if(passwordRestored !=null){
            password.setText(passwordRestored);
        }
    }

    // Save UI state changes to the savedInstanceState.
    // This bundle will be passed to onCreate if the process is killed and restarted.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("MyUsername", "Welcome back to Android");
        savedInstanceState.putString("MyPassword", "Welcome back to Android");
    }

    // Restore UI state from the savedInstanceState.
    // This bundle has also been passed to onCreate.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        usernameRestored = savedInstanceState.getString("MyUsername");
        passwordRestored = savedInstanceState.getString("MyString");
    }


}
