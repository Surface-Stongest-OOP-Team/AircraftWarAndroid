package edu.hitsz.aircraft;

import edu.hitsz.bullet.*;
import edu.hitsz.bus.MeEvent;
import edu.hitsz.strategy.FireStrategy;
import edu.hitsz.strategy.Scattered;
import edu.hitsz.strategy.Single;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import static edu.hitsz.application.MainActivity.myBinder;
import static edu.hitsz.bullet.BulletType.*;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class PhantomHero extends AbstractAircraft implements Serializable {

    public PhantomHero(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public List<BaseBullet> shoot() {
        return null;
    }

    /**
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
}
