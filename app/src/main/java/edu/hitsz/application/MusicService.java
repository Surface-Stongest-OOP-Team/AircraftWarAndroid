package edu.hitsz.application;

import static edu.hitsz.application.Settings.SystemMusicState.*;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;

import edu.hitsz.R;

public class MusicService extends Service {
    private static  final String TAG = "MusicService";
    private HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();
    private SoundPool mSoundPool;
    private MediaPlayer player;
    public MusicService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "==== MusicService onCreate ===");
        mSoundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
        soundID.put(1, mSoundPool.load(this, R.raw.bullet_hit, 1));
        soundID.put(2, mSoundPool.load(this, R.raw.game_over, 1));
        soundID.put(3, mSoundPool.load(this, R.raw.bgm_boss, 1));
        soundID.put(4, mSoundPool.load(this, R.raw.bomb_explosion, 1));
        soundID.put(5, mSoundPool.load(this, R.raw.get_supply, 1));
        soundID.put(6, mSoundPool.load(this, R.raw.bullet, 1));
    }

    @Override
    public IBinder onBind(Intent intent){
        if(Settings.systemMusicState==ON) {
            playBGM();
        }
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public void playBulletHit(){
            mSoundPool.play(soundID.get(1), 1, 1, 0,0,1);
        }
        public void stopBulletHit(){mSoundPool.stop(soundID.get(1));}
        public void playGameOver(){mSoundPool.play(soundID.get(2), 1, 1, 0, 0, 1);}
        public void stopGameOver(){mSoundPool.stop(soundID.get(2));}
        public void playBoss(){mSoundPool.play(soundID.get(3), 1, 1, 0, 0, 1);}
        public void stopBoss(){mSoundPool.stop(soundID.get(3));}
        public void playBombExplosion(){mSoundPool.play(soundID.get(4), 1, 1, 0, 0, 1);}
        public void stopBombExplosion(){mSoundPool.stop(soundID.get(4));}
        public void playGetSupply(){mSoundPool.play(soundID.get(5), 1, 1, 0, 0, 1);}
        public void stopGetSupply(){mSoundPool.stop(soundID.get(5));}
        public void playBullet(){mSoundPool.play(soundID.get(6), 1, 1, 0, 0, 1);}
        public void stopBullet(){mSoundPool.stop(soundID.get(6));}

        public void stopMusic() {MusicService.this.stopMusic();}
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    //播放音乐
    public void playBGM(){
        if(player == null){
            player = MediaPlayer.create(this, R.raw.bgm);
            player.setLooping(true);
        }
        player.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
    }
    /**
     * 停止播放
     */
    public void stopMusic() {
        if (player != null) {
            player.stop();
            player.reset();//重置
            player.release();//释放
            player = null;
        }
    }
}