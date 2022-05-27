package edu.hitsz.application;

import static java.lang.Thread.sleep;
import static edu.hitsz.application.Settings.Difficulty.*;
import static edu.hitsz.application.Settings.difficulty;
import static edu.hitsz.application.Settings.Difficulty.*;
import static edu.hitsz.application.Settings.SystemMusicState.ON;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.IpSecManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

import edu.hitsz.R;
import edu.hitsz.application.game.CasualMode;
import edu.hitsz.application.game.HardMode;
import edu.hitsz.application.game.MediumMode;
import edu.hitsz.user.UserDao;
import edu.hitsz.user.UserDaoImpl;
//test message
//hello Liu Yu
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MySurfaceView mySurfaceView;
    public static int screenWidth;
    public static int screenHeight;
    public static UserDao userDao = new UserDaoImpl();
    public static boolean gameOverFlag = false;
    public static String name;
    public static MusicService.MyBinder myBinder;

    protected void startGame() throws InterruptedException {
        getScreenHW();
        MySurfaceView.screenWidth =screenWidth;
        MySurfaceView.screenHeight =screenHeight;
        Settings.systemMusicState=ON;

        //Music Service
        Connect conn = new Connect();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        setContentView(mySurfaceView);
        System.out.println("!");
        System.out.println("!");
        System.out.println("!");
        System.out.println("!");
        System.out.println("!");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menulayout);
        Button button1=(Button) findViewById(R.id.button1);
        Button button2=(Button) findViewById(R.id.button2);
        Button button3=(Button) findViewById(R.id.button3);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }



    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button1:
                difficulty=Casual;
                mySurfaceView = new CasualMode(this);
                break;
            case R.id.button2:
                difficulty=Medium;
                mySurfaceView = new MediumMode(this);
                break;
            case R.id.button3:
                difficulty=Hard;
                mySurfaceView = new HardMode(this);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
        try {
            startGame();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Connect implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            Log.i("music demo","Service Connnected");
            myBinder = (MusicService.MyBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
    public void getScreenHW() {
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth=displayMetrics.widthPixels;
        screenHeight=displayMetrics.heightPixels;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
        }
        return true;
    }
}