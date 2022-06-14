package edu.hitsz.application;

import android.content.AbstractThreadedSyncAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.R;
import edu.hitsz.application.MainActivity.NetConn;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText username;
    Button button,button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        button=(Button) findViewById(R.id.button5);
        button2=(Button) findViewById(R.id.button6);
        username=(EditText) findViewById(R.id.usename);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button2.setEnabled(false);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button5:
                MainActivity.port = Integer.parseInt(username.getText().toString());
                new Thread(new NetConn()).start();
                button.setClickable(false);
                button.setEnabled(false);
                button2.setEnabled(true);
                break;
            case R.id.button6:
                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                break;
        }
    }
}