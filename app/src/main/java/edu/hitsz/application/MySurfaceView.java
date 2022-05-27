package edu.hitsz.application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

import java.io.FileInputStream;

import edu.hitsz.R;

public class MySurfaceView extends SurfaceView implements
        SurfaceHolder.Callback ,Runnable{
    static int screenWidth = 480;
    static int screenHeight = 800;
    boolean mbLoop = false; //控制绘画线程的标志位
    protected SurfaceHolder mSurfaceHolder;
    protected Canvas canvas;  //绘图的画布
    protected Paint mPaint;


    public void loadImage() {
        ImageManager.BACKGROUND_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.bg);

        ImageManager.BACKGROUND_IMAGE2 = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
        ImageManager.BACKGROUND_IMAGE3 = BitmapFactory.decodeResource(getResources(), R.drawable.bg3);
        ImageManager.BACKGROUND_IMAGE4 = BitmapFactory.decodeResource(getResources(), R.drawable.bg4);
        ImageManager.BACKGROUND_IMAGE5 = BitmapFactory.decodeResource(getResources(), R.drawable.bg5);;

        ImageManager.HERO_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.hero);
        ImageManager.MOB_ENEMY_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.mob);
        ImageManager.ELITE_IMAGE=BitmapFactory.decodeResource(getResources(), R.drawable.elite);

        ImageManager.HERO_BULLET_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.bullet_hero);
        ImageManager.ENEMY_BULLET_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.bullet_enemy);
        ImageManager.BOSS_IMAGE=BitmapFactory.decodeResource(getResources(), R.drawable.boss);

        ImageManager.PROP_BLOOD_IMAGE=BitmapFactory.decodeResource(getResources(), R.drawable.prop_blood);
        ImageManager.PROP_BOMB_IMAGE=BitmapFactory.decodeResource(getResources(), R.drawable.prop_bomb);
        ImageManager.PROP_BULLET_IMAGE=BitmapFactory.decodeResource(getResources(), R.drawable.prop_bullet);
        ImageManager.updateMap();
    }

    public MySurfaceView(Context context) {
        super(context);
        loadImage();
        mbLoop = true;
        mPaint = new Paint();  //设置画笔
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
    }

    public void draw(){}

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        new Thread(this).start();
    }
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mbLoop = false;
    }

    @Override
    public void run() {

    }
}
