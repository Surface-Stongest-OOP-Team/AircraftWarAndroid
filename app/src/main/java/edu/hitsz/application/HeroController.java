package edu.hitsz.application;


import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import edu.hitsz.aircraft.HeroAircraft;


/**
 * 英雄机控制类
 * 监听鼠标，控制英雄机的移动
 *
 * @author hitsz
 */
/**
 * 英雄机控制类
 * 监听鼠标，控制英雄机的移动
 *
 * @author hitsz
 */
public class HeroController{
    private AbstractGame abstractGame;
    private HeroAircraft heroAircraft;
    private boolean selectFlag = false;
    public HeroController(AbstractGame abstractGame, HeroAircraft heroAircraft)
    {

        this.abstractGame = abstractGame;
        this.heroAircraft = heroAircraft;
        setGestureListener();   //  监听鼠标位置，并实时改变英雄机位置
    }
    /**
     * 实时监听鼠标位置
     * @author
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setGestureListener() {
        abstractGame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selectFlag = isSelect(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if ( event.getX()<0 || event.getX()> MySurfaceView.screenWidth || event.getY()<0 ||
                                event.getY()>MySurfaceView.screenHeight){

                        }else{
                            if(selectFlag){
                                heroAircraft.setLocation(event.getX(),event.getY());
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        selectFlag = false;
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        break;
                }
                return true;
            }
        });
    }

    public boolean isSelect(MotionEvent event){
        int hx = heroAircraft.getLocationX();
        int hy = heroAircraft.getLocationY();
        int hWidth = heroAircraft.getWidth();
        int hHeight = heroAircraft.getHeight();


        return hx + hWidth/2 > event.getX()
                && hx - hWidth/2 < event.getX()
                && hy + hHeight/2 > event.getY()
                && hy - hHeight/2< event.getY();

    }

}
