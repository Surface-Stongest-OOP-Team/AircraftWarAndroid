package edu.hitsz.application;

import static edu.hitsz.application.Settings.Difficulty.Casual;
import static edu.hitsz.application.Settings.SystemMusicState.OFF;
import static edu.hitsz.application.Settings.SystemMusicState.ON;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.R;
import edu.hitsz.application.game.CasualMode;
import edu.hitsz.user.UserDao;
import edu.hitsz.user.UserDaoImpl;
//test message
//hello Liu Yu
public class MainActivity extends AppCompatActivity {
    private MySurfaceView mySurfaceView;
    public static int screenWidth;
    public static int screenHeight;
    public static UserDao userDao = new UserDaoImpl();
    public static String name;
    public MusicService.MyBinder myBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySurfaceView = new CasualMode(this);
        getScreenHW();
        MySurfaceView.screenWidth =screenWidth;
        MySurfaceView.screenHeight =screenHeight;
        Settings.difficulty= Casual;
        Settings.systemMusicState=ON;

        setContentView(mySurfaceView);
        //Music Service
        Connect conn = new Connect();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

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