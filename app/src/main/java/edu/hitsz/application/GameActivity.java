package edu.hitsz.application;

import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    private static MySurfaceView mySurfaceView;
    public static void setMySurfaceView(MySurfaceView x){
        mySurfaceView=x;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mySurfaceView);
    }
}
