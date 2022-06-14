package edu.hitsz.application;

import static java.lang.Thread.sleep;
import static edu.hitsz.application.Settings.Difficulty.*;
import static edu.hitsz.application.Settings.difficulty;
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
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.hitsz.R;
import edu.hitsz.aircraft.PhantomHero;
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

    public static Socket socket;
    public static PrintWriter writer;
    String receivedMessage;
    protected class NetConn extends Thread {
        @Override
        public void run() {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress
                        (/*"192.168.137.1"*/"10.249.63.162", 9999), 5000);
                writer = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream(), "ISO-8859-1")), true);
                Log.i("client", "connect to server");
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    class Client implements Runnable{
        private BufferedReader in = null;

        public Client(Socket socket){
            try{
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while ((receivedMessage = in.readLine()) != null) {
                    System.out.println(receivedMessage);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    protected void startGame() throws InterruptedException {
        getScreenHW();
        MySurfaceView.screenWidth = screenWidth;
        MySurfaceView.screenHeight = screenHeight;
        Settings.systemMusicState = ON;

        //Music Service
        Connect conn = new Connect();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        GameActivity.setMySurfaceView(mySurfaceView);
        intent.setClass(this,GameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menulayout);
        Button button1=(Button) findViewById(R.id.button1);
        Button button2=(Button) findViewById(R.id.button2);
        Button button3=(Button) findViewById(R.id.button3);
        Button buttonCon=(Button) findViewById(R.id.buttonCon);
        Button buttonSend=(Button) findViewById(R.id.buttonSend);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        buttonCon.setOnClickListener(this);
        buttonSend.setOnClickListener(this);

        new Thread(new NetConn()).start();
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button1:
                try {
                    difficulty=Casual;
                    mySurfaceView = new CasualMode(this);
                    startGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button2:
                try {
                    difficulty=Medium;
                    mySurfaceView = new MediumMode(this);
                    startGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button3:
                try {
                    difficulty=Hard;
                    mySurfaceView = new HardMode(this);
                    startGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.buttonCon:
                new Thread(()->{
                    Log.i("client","waiting for connection");
                    while(!socket.isConnected());
                    new Thread(new Client(socket)).start();
                }).start();
                break;
            case R.id.buttonSend:
                String sss= null;
                try {
                    sss = (new PhantomHero(233,233,0,0,0).serializeToString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    PhantomHero xxx= PhantomHero.deserializeToObject(sss);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
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
            setContentView(R.layout.name);
        }
        return true;
    }
}