package edu.hitsz.application;

import static java.lang.Thread.sleep;
import static edu.hitsz.application.AbstractGame.score;
import static edu.hitsz.application.MainActivity.userDao;
import static edu.hitsz.application.Settings.Difficulty.*;
import static edu.hitsz.application.Settings.difficulty;
import static edu.hitsz.application.Settings.Difficulty.*;
import static edu.hitsz.application.Settings.SystemMusicState.ON;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.GnssAntennaInfo;
import android.media.MediaCas;
import android.net.IpSecManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Set;

import edu.hitsz.R;
import edu.hitsz.application.game.CasualMode;
import edu.hitsz.application.game.HardMode;
import edu.hitsz.application.game.MediumMode;
import edu.hitsz.user.User;
import edu.hitsz.user.UserDao;
import edu.hitsz.user.UserDaoImpl;

public class NamePanelActivity extends AppCompatActivity implements View.OnClickListener {
    String name= new String("player");
    Date date = new Date(System.currentTimeMillis());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name);
        EditText editText =(EditText)findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                name = editText.getText().toString();
            }
        });
        Button button=(Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button){
            userDao.addUser(new User((int) (Math.random() * 1000000), name, score, date));
//            userDao.writeToFile();
            Intent intent =new Intent(this,RankListActivity.class);
            startActivity(intent);
        }
    }
}