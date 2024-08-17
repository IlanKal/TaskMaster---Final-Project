package dev.ilankal.taskmaster;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

import dev.ilankal.taskmaster.Utilities.SoundPlayer;

public class SplashActivity extends AppCompatActivity {
    private LottieAnimationView taskAnimation;
    public SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        findViews();
        taskAnimation.resumeAnimation();
        soundPlayer = new SoundPlayer(SplashActivity.this);
        soundPlayer.playSound(R.raw.intro_logo, false);
        taskAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                //pass
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                transactToMainActivity();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
                //pass
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
                //pass
            }
        });

    }
    private void transactToMainActivity() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    private void findViews() {
        taskAnimation = findViewById(R.id.taskAnimation);
    }
}