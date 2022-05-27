package edu.hitsz.application;

import static edu.hitsz.application.MainActivity.myBinder;

import edu.hitsz.aircraft.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;

import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bus.MeEvent;
import edu.hitsz.bus.Publisher;
import edu.hitsz.creator.*;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BloodProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.prop.BulletProp;
import edu.hitsz.user.UserDao;
import edu.hitsz.user.UserDaoImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public abstract class AbstractGame extends MySurfaceView {
    private double backGroundTop = 0;
    UserDao userDao=new UserDaoImpl();
    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private static final int timeInterval = 8;

    public static int getTimeInterval() {
        return timeInterval;
    }

    private final HeroAircraft heroAircraft;
    protected final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;
    protected Boss boss;

    protected Publisher publisher=new Publisher();


    public int score = 0;
    private int time = 0;
    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    protected int[] cycleDuration;
    protected int[] cycleTime = new int[2];

    protected void setCycleDuration(int[] cycleDuration){
        this .cycleDuration=cycleDuration;
    }
    protected final EliteFactory eliteFactory = new EliteFactory();
    protected final MobEnemyFactory mobEnemyFactory = new MobEnemyFactory();
    private final BulletPropFactory bulletPropFactory = new BulletPropFactory();
    private final BloodPropFactory bloodPropFactory = new BloodPropFactory();
    private final BombPropFactory bombPropFactory = new BombPropFactory();
    protected final BossFactory bossFactory = new BossFactory();

    public AbstractGame(Context context) {
        super(context);
        heroAircraft = HeroAircraft.getInstance(
                super.screenWidth / 2,
                super.screenHeight - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 100);

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        //Scheduled 线程池，用于定时任务调度
        executorService = new ScheduledThreadPoolExecutor(2);

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    @Override
    public void run() {
        //userDao.readFromFile();

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定

        Runnable task = () -> {

            time += timeInterval;

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge0()) {
                // 新敌机产生
                enemySpawn();
                // 飞机射出子弹
                enemyShootAction();
            }
            if (timeCountAndNewCycleJudge1()) {
                heroShootAction();
            }

            bossSpawn();

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            propsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            draw();

            // 游戏结束检查
            if (heroAircraft.getHp() <= 0) {
                // 游戏结束

                executorService.shutdown();

                myBinder.stopMusic();
                if(boss!=null){
                    boss.stopMusic();
                }
                MainActivity.gameOverFlag = true;
                notify();
            }

        };

        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */

        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

    }

    //***********************
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge0() {
        cycleTime[0] += timeInterval;
        if (cycleTime[0] >= cycleDuration[0] && cycleTime[0] -timeInterval  < cycleTime[0]) {
            // 跨越到新的周期
            cycleTime[0] %= cycleDuration[0];
            return true;
        } else {
            return false;
        }
    }
    private boolean timeCountAndNewCycleJudge1() {
        cycleTime[1] += timeInterval;
        if (cycleTime[1] >= cycleDuration[1] && cycleTime[1] -timeInterval  < cycleTime[1]) {
            // 跨越到新的周期
            cycleTime[1] %= cycleDuration[1];
            return true;
        } else {
            return false;
        }
    }

    protected int lastscore = 0;
    protected abstract void bossSpawn();
    protected abstract void enemySpawn();

    private void enemyShootAction() {
        //  敌机射击
        for (AbstractAircraft elite : enemyAircrafts) {
            List<BaseBullet> tmp = elite.shoot();
            enemyBullets.addAll(tmp);
            publisher.getSubscribeList().addAll(tmp);
        }
    }
    private void heroShootAction(){
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward(timeInterval);
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward(timeInterval);
        }
    }

    private void propsMoveAction() {
        for(AbstractProp prs : props){
            prs.forward(timeInterval);
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward(timeInterval);
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        //  敌机子弹攻击英雄
        for(BaseBullet bullet : enemyBullets) {
            if(bullet.notValid()) {continue;}
            if(heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }
        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    myBinder.playBulletHit();
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();

                    if (enemyAircraft.notValid()) {
                        //  获得分数，产生道具补给
                        if(enemyAircraft instanceof Elite) {
                            double pnt=Math.random();
                            if(pnt <0.3) {
                                props.add(bloodPropFactory.create(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), 0, enemyAircraft.getSpeedY()/4));
                            }else if(pnt<0.6) {
                                props.add(bombPropFactory.create(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), 0, enemyAircraft.getSpeedY()/4));
                            }else if(pnt<0.9){
                                props.add(bulletPropFactory.create(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), 0, enemyAircraft.getSpeedY()/4));
                            }
                        }
                        score += 10;
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得道具，道具生效
        for(AbstractProp prs : props){
            if(prs.notValid()) {continue;}
            if(prs.crash(heroAircraft)) {
                myBinder.playGetSupply();
                if(prs instanceof BloodProp){
                    heroAircraft.decreaseHp(-40);
                }else if(prs instanceof BombProp){
                    publisher.publish(MeEvent.bombEvent);
                    score+=enemyAircrafts.size()*10;
                    System.out.print("Bomb supply activated!\n");
                }else if(prs instanceof BulletProp){
                    heroAircraft.fireSupply();
                    System.out.print("Fire supply activated!\n");
                }
                prs.vanish();
            }

        }

    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        if(boss!=null){
            if(boss.notValid()){
                boss.stopMusic();
            }
        }
        for(AbstractAircraft enemy: enemyAircrafts){
            if(enemy.notValid()){
                publisher.unsubscribe(enemy);
            }
        }
        for(BaseBullet enemyBullet:enemyBullets){
            if(enemyBullet.notValid()){
                publisher.unsubscribe(enemyBullet);
            }
        }
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param
     */

    @Override
    public void draw(){
        canvas = mSurfaceHolder.lockCanvas();
        if(mSurfaceHolder == null || canvas == null){
            return;
        }
        Bitmap bufferedImage=null;

        switch(Settings.difficulty){
            case Casual:bufferedImage=ImageManager.BACKGROUND_IMAGE;break;
            case Medium:bufferedImage=ImageManager.BACKGROUND_IMAGE2;break;
            case Hard:bufferedImage=ImageManager.BACKGROUND_IMAGE3;break;
            default:bufferedImage=ImageManager.BACKGROUND_IMAGE4;break;
        }
        canvas.drawBitmap(bufferedImage,0, (float) (this.backGroundTop-screenHeight),mPaint);
        canvas.drawBitmap(bufferedImage,0, (float) (this.backGroundTop),mPaint);
        backGroundTop+=1;
        if(backGroundTop==screenHeight){
            this.backGroundTop=0;
        }
        drawAircraft();
        drawScoreAndLife();
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void drawAircraft() {
        for (AbstractProp prop:props){
            if(prop.getClass().equals(BloodProp.class)){
                canvas.drawBitmap(ImageManager.PROP_BLOOD_IMAGE,prop.getLocationX()-ImageManager.PROP_BLOOD_IMAGE.getWidth()/2,prop.getLocationY()-ImageManager.PROP_BLOOD_IMAGE.getHeight()/2,mPaint);
            }
            else if(prop.getClass().equals(BombProp.class)){
                canvas.drawBitmap(ImageManager.PROP_BOMB_IMAGE,prop.getLocationX()-ImageManager.PROP_BOMB_IMAGE.getWidth()/2,prop.getLocationY()-ImageManager.PROP_BOMB_IMAGE.getHeight()/2,mPaint);
            }
            else if(prop.getClass().equals(BulletProp.class)){
                canvas.drawBitmap(ImageManager.PROP_BULLET_IMAGE,prop.getLocationX()-ImageManager.PROP_BULLET_IMAGE.getWidth()/2,prop.getLocationY()-ImageManager.PROP_BULLET_IMAGE.getHeight()/2,mPaint);
            }
            else {}
        }
        for (BaseBullet enemyBullet : enemyBullets) {
            canvas.drawBitmap(ImageManager.ENEMY_BULLET_IMAGE, enemyBullet.getLocationX() - ImageManager.ENEMY_BULLET_IMAGE.getWidth() / 2, enemyBullet.getLocationY()-ImageManager.ENEMY_BULLET_IMAGE.getHeight()/2, mPaint);
        }
        for (BaseBullet heroBullet : heroBullets) {
            canvas.drawBitmap(ImageManager.HERO_BULLET_IMAGE, heroBullet.getLocationX() - ImageManager.HERO_BULLET_IMAGE.getWidth() / 2, heroBullet.getLocationY()-ImageManager.HERO_BULLET_IMAGE.getHeight()/2, mPaint);
        }
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            if (enemyAircraft.getClass().equals(Boss.class)) {
                canvas.drawBitmap(ImageManager.BOSS_IMAGE, enemyAircraft.getLocationX() - ImageManager.BOSS_IMAGE.getWidth() / 2, enemyAircraft.getLocationY()-ImageManager.BOSS_IMAGE.getHeight()/2, mPaint);
            } else if (enemyAircraft.getClass().equals(Elite.class)) {
                canvas.drawBitmap(ImageManager.ELITE_IMAGE, enemyAircraft.getLocationX() - ImageManager.ELITE_IMAGE.getWidth() / 2, enemyAircraft.getLocationY()-ImageManager.ELITE_IMAGE.getHeight()/2, mPaint);
            } else if (enemyAircraft.getClass().equals(MobEnemy.class)) {
                canvas.drawBitmap(ImageManager.MOB_ENEMY_IMAGE, enemyAircraft.getLocationX() - ImageManager.MOB_ENEMY_IMAGE.getWidth() / 2, enemyAircraft.getLocationY()-ImageManager.MOB_ENEMY_IMAGE.getHeight()/2, mPaint);
            } else {
            }
        }
        canvas.drawBitmap(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2, heroAircraft.getLocationY()-ImageManager.HERO_IMAGE.getHeight()/2, mPaint);
    }

    private void drawScoreAndLife(){
        mPaint.setTextSize(40);
        canvas.drawText("Score:"+score,5,35,mPaint);
        canvas.drawText("Life:"+heroAircraft.getHp(),5,80,mPaint);
    }
//    @Override
//    public void paint(Graphics g) {
//        super.paint(g);
//
//        // 绘制背景,图片滚动
//
//        g.drawImage(bufferedImage, 0, (int) (this.backGroundTop - MainActivity.screenHeight), null);
//        g.drawImage(bufferedImage, 0, (int) this.backGroundTop, null);
//        this.backGroundTop += 1.0 * timeInterval/40;
//        if (this.backGroundTop == MainActivity.screenHeight) {
//            this.backGroundTop = 0;
//        }
//
//        // 先绘制子弹，后绘制飞机
//        // 这样子弹显示在飞机的下层
//
//        paintImageWithPositionRevised(g, props);
//
//        paintImageWithPositionRevised(g, enemyBullets);
//        paintImageWithPositionRevised(g, heroBullets);
//
//        paintImageWithPositionRevised(g, enemyAircrafts);
//
//        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
//                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);
//
//        //绘制得分和生命值
//        paintScoreAndLife(g);
//
//    }
//
//    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
//        if (objects.size() == 0) {
//            return;
//        }
//
//        for (AbstractFlyingObject object : objects) {
//            Bitmap image = object.getImage();
//            assert image != null : objects.getClass().getName() + " has no image! ";
//            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
//                    object.getLocationY() - image.getHeight() / 2, null);
//        }
//    }
//
//    private void paintScoreAndLife(Graphics g) {
//        int x = 10;
//        int y = 25;
//        g.setColor(new Color(16711680));
//        g.setFont(new Font("SansSerif", Font.BOLD, 22));
//        g.drawString("SCORE:" + this.score, x, y);
//        y = y + 20;
//        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
//    }
}
