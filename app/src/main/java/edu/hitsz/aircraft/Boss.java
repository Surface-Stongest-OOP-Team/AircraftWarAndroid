package edu.hitsz.aircraft;

import edu.hitsz.application.MainActivity;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bus.MeEvent;
import edu.hitsz.strategy.FireStrategy;
import edu.hitsz.strategy.Scattered;
import edu.hitsz.strategy.Single;

import java.util.LinkedList;
import java.util.List;

import static edu.hitsz.application.MainActivity.myBinder;
import static edu.hitsz.bullet.BulletType.ENEMY;

public class Boss extends AbstractAircraft{
    FireStrategy fireStrategy=new FireStrategy(new Scattered(ENEMY,1,6,30));

    private int direction = 1;
    private int shootNum = 1;
    private int power = 30;

    public Boss(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        playMusic();
    }

    public void playMusic(){
        myBinder.playBoss();
    }
    public void stopMusic(){
        myBinder.stopBoss();
    }

    @Override
    public void forward(double timeInterval) {
        super.forward(timeInterval);
        // 判定 y 轴向下飞行出界
        if (locationY >= MainActivity.screenHeight) {
            vanish();
        }
    }

    @Override
    public void listen(MeEvent meEvent) {
        return ;
    }

    @Override
    public List<BaseBullet> shoot() {
        return fireStrategy.executeStrategy(this.getLocationX(),this.getLocationY(),this.getSpeedX(),this.getSpeedY());
    }
}
