package dev.ilankal.taskmaster.Utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

public class SoundPlayer {
    private Context context;
    private MediaPlayer mediaPlayer;
    private Handler mainHandler;

    public SoundPlayer(Context context) {
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void playSound(int resID, boolean loop) {
        if (mediaPlayer == null) {
            mainHandler.post(() -> {
                mediaPlayer = MediaPlayer.create(context, resID);
                mediaPlayer.setLooping(loop);
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.start();
            });
        }
    }

    public void stopSound() {
        if (mediaPlayer != null) {
            mainHandler.post(() -> {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            });
        }
    }
}
