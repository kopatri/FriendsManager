package com.abertay.friendsmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username;
    EditText password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.secretWord);
        login=(Button)findViewById(R.id.login);

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
            Toast.makeText(this,"Successful",Toast.LENGTH_SHORT).show();
            Intent goToMainActivity = new Intent(this,MainActivity.class);
            startActivity(goToMainActivity);
        }
        else{
            Toast.makeText(this,"Failed, try again",Toast.LENGTH_LONG).show();
            username.setText("");
            password.setText("");
        }
    }

    //When User returns, fields will be cleared
    @Override
    protected void onResume() {
        username.setText("");
        password.setText("");
        super.onResume();
    }
}
