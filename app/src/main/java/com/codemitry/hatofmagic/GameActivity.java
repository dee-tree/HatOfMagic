package com.codemitry.hatofmagic;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


// Copyright Dmitriy Sokolov 2020

public class GameActivity extends AppCompatActivity {

    static final String BEST_SCORE_PREF = "BestScore";

    private SoundPool soundPool;
    private int bestScoreSound;
    private int eatCandySound;
    private int candyAppearanceSound;
    private int bombAppearanceSound;
    private int bombFlyingSound, bombFlyingStream;
    private int bombCatch;
    private MediaPlayer mediaPlayer;
    private boolean runned = false;
    private boolean losed = false;
    private boolean isSoundEnabled;

    private ConstraintLayout loseLayout, pauseLayout;
    private TextView scoreText;

    private ImageView confettiLeftPicture, confettiRightPicture;
    private Bitmap[] lifesImages;
    private Bitmap losedHealth;
    private GameSurfaceView surface;
    private Bitmap background;
    private Bitmap hitPicture;
    private ImageView pauseView;
    private int height, width;
    private ArrayList<Candy> candies;
    private ArrayList<Bomb> bombs;

    int bombTime = 0, ballTime = 0;
    int bombRand = 0, ballRand = 0;

    private int DAMAGE_COUNT = 15;
    private int damage = 0;
    private Paint damagePaint;

    private Locale myLocale;
    private String lang;

    private Hat hat;
    private int dx, dy;
    private int angle;
    private int score, bestScore;
    private int lifes = 3;
    private int cutOutheight = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            cutOutheight = extras.getInt("cutout");

        lang = MainActivity.getLanguage(this);
        if (!lang.equals(""))
            changeLang(lang);


        setContentView(R.layout.activity_game);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        width = point.x - cutOutheight;
        height = point.y;


        hat = new Hat(this);
        hat.setCenterX(width / 2);
        hat.setCenterY(height / 2);

        candies = new ArrayList<>();
        bombs = new ArrayList<>();


        isSoundEnabled = MainActivity.isEnabledAudio(this);

        surface = null;

        scoreText = findViewById(R.id.scoreText);
        surface = findViewById(R.id.surface);

        confettiLeftPicture = findViewById(R.id.confetti_left);
        confettiRightPicture = findViewById(R.id.confetti_right);

        loseLayout = findViewById(R.id.loseLayout);
        pauseLayout = findViewById(R.id.pauseLayout);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background, width, height, false);

        pauseView = findViewById(R.id.pauseView);

        hitPicture = BitmapFactory.decodeResource(getResources(), R.drawable.hit);
        hitPicture = Bitmap.createScaledBitmap(hitPicture, width, height, false);
        damagePaint = new Paint();

        score = 0;

        lifesImages = new Bitmap[lifes];
        lifesImages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.health);
        lifesImages[0] = Bitmap.createScaledBitmap(lifesImages[0], (int) (0.12 * height), (int) (0.1 * height), false);
        lifesImages[1] = Bitmap.createBitmap(lifesImages[0]);
        lifesImages[2] = Bitmap.createBitmap(lifesImages[0]);

        losedHealth = BitmapFactory.decodeResource(getResources(), R.drawable.losed_health);
        losedHealth = Bitmap.createScaledBitmap(losedHealth, (int) (0.12 * height), (int) (0.1 * height), false);


        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        eatCandySound = soundPool.load(this, R.raw.eat_candy, 2);
        candyAppearanceSound = soundPool.load(this, R.raw.candy_appearance, 1);
        bombAppearanceSound = soundPool.load(this, R.raw.bomp_appearance, 1);
        bombFlyingSound = soundPool.load(this, R.raw.bomb_flying, 0);
        bombCatch = soundPool.load(this, R.raw.bomb_catch, 2);
        bestScoreSound = soundPool.load(this, R.raw.best_score, 2);

        mediaPlayer = MediaPlayer.create(this, R.raw.background_game_sound);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });

        if (isSoundEnabled) {
            mediaPlayer.setVolume(1, 1);
        } else
            mediaPlayer.setVolume(0, 0);

        runned = true;
    }

    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase("")) return;
        myLocale = new Locale(lang);
        Locale.setDefault(myLocale);

        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, null);
        getResources().updateConfiguration(config, null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (losed || runned) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        if (runned)
            pause();
    }

    void addBall() {
        candies.add(new Candy(this));
    }

    void addBomb() {
        bombs.add(new Bomb(this));
        playBombFlyingSound();
    }


    void update(int delta) {
        if (runned) {
            for (int i = candies.size() - 1; i >= 0; i--) {
                candies.get(i).update(delta);
                if ((intersection(candies.get(i).getCenterX(), candies.get(i).getCenterY(), candies.get(i).getWidth() / 2, hat.getCenterX(), hat.getCenterY(), hat.getWidth() / 2)) && (hat.getMoved()) && candies.get(i).isAlive()) {

                    candies.get(i).setAlive(false);
                    playEatCandySound();
                    score++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scoreText.setText(String.valueOf(score));
                        }
                    });
                    if (score % 5 == 0) {
                        surface.decBombTiming(45);
                    }
                }
                if (!candies.get(i).isAlive()) {
                    candies.remove(i);
                }
            }

            for (int i = bombs.size() - 1; i >= 0; i--) {
                bombs.get(i).update(delta);
                if ((intersection(bombs.get(i).getCenterX(), bombs.get(i).getCenterY(), bombs.get(i).getWidth() / 2, hat.getCenterX(), hat.getCenterY(), hat.getWidth() / 2)) && (hat.getMoved() && bombs.get(i).isAlive())) {
                    bombs.get(i).setAlive(false);
                    playBombCatch();
                    damage();
                    decLife();
                }

                if (!bombs.get(i).isAlive()) {
                    bombs.remove(i);
                    stopBombFlyingSound();
                }
            }
        }
    }

    synchronized void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawBitmap(background, 0, 0, null);
            for (int i = 0; i < 3; i++)
                canvas.drawBitmap(lifesImages[i], (width - lifesImages[0].getWidth() * (i + 1)), 10, null);
            for (Candy b : candies)
                b.draw(canvas);
            for (Bomb bomb : bombs)
                bomb.draw(canvas);
            if (hat.getMoved())
                hat.draw(canvas);

            if (damage > 0) {
                if (damage > DAMAGE_COUNT * 4 / 5)
                    damagePaint.setAlpha(damagePaint.getAlpha() + 255 / (DAMAGE_COUNT / 5) <= 255 ? damagePaint.getAlpha() + 255 / (DAMAGE_COUNT / 5) : 255);
                else if (damage < DAMAGE_COUNT * 3 / 5)
                    damagePaint.setAlpha(damagePaint.getAlpha() - 255 / (DAMAGE_COUNT / 5) >= 0 ? damagePaint.getAlpha() - 255 / (DAMAGE_COUNT * 3 / 5) : 0);


                canvas.drawBitmap(hitPicture, 0, 0, damagePaint);
                damage--;
            }
        }
    }

    void damage() {
        damage = DAMAGE_COUNT;
        damagePaint.setAlpha(0);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        angle = 0;
        if (runned) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    hat.setCenterX(hat.getCenterX() + dx);
                    hat.setCenterY(hat.getCenterY() + dy);

                    dx = (int) (event.getX() - hat.getCenterX());
                    dy = (int) (event.getY() - hat.getCenterY());
                    if (dx != 0)
                        angle = -90 + (int) (Math.atan((double) dy / (double) dx) * 180 / Math.PI);
                    if (dx < 0) {
                        angle += 180;
                    }
                    if ((dx != 0))
                        hat.rotate(angle);
                    hat.setMoved(true);
                    break;
                case MotionEvent.ACTION_UP:
                    hat.setMoved(false);
                    break;
                case MotionEvent.ACTION_DOWN:
                    dx = (int) (event.getX() - hat.getCenterX());
                    dy = (int) (event.getY() - hat.getCenterY());

                    hat.setCenterX(hat.getCenterX() + dx);
                    hat.setCenterY(hat.getCenterY() + dy);
                    hat.rotate(0);
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (runned && !losed && surface.runned()) {
            pause();
        } else if (losed)
            finish();
        else if (!runned && !surface.runned()) {
            start();
        }
    }

    public void onPauseClick(View v) {
        if (runned) {
            pause();
        }
    }

    public void onRestartClick(View v) {
        recreate();
    }

    public void onToMenuClick(View v) {
        finish();
    }

    void pause() {
        if (!losed) {
            runned = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                pauseView.setImageDrawable(getDrawable(R.drawable.paused));
            else
                pauseView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paused));

            surface.thread.setRunned(false);

            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();

            soundPool.autoPause();

            pauseLayout.setAlpha(0);

            pauseLayout.setVisibility(View.VISIBLE);
            pauseLayout.animate().alpha(1.0f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    void start() {
        if (!runned) {

            runned = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                pauseView.setImageDrawable(getDrawable(R.drawable.pause));

            if (surface.runned())
                surface.setRunned(false);

            soundPool.autoResume();

            surface.thread = new GameThread(this, surface);

            pauseLayout.animate().alpha(0f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    pauseLayout.setVisibility(View.GONE);

                    if (!mediaPlayer.isPlaying())
                        mediaPlayer.start();

                    surface.thread.setRunned(true);
                    surface.thread.start();

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    public void onContinueClick(View v) {
        start();
    }


    void onLose() {

        soundPool.autoPause();
        losed = true;
        runned = false;
        surface.thread.setRunned(false);
        bestScore = loadBestScore(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (score > bestScore) {
                    mediaPlayer.stop();
                    playBestScoreSound();
                    confetti();
                    saveBestScore(score);
                    ((TextView) findViewById(R.id.loseText)).setText(getString(R.string.new_best_score));
                } else {
                    mediaPlayer.release();
                    mediaPlayer = MediaPlayer.create(GameActivity.super.getApplicationContext(), R.raw.lose_sound);
                    mediaPlayer.setVolume(isSoundEnabled ? 1 : 0, isSoundEnabled ? 1 : 0);
                    mediaPlayer.start();
                }

                loseLayout.setAlpha(0);
                loseLayout.setVisibility(View.VISIBLE);
                pauseView.setVisibility(View.GONE);
                loseLayout.animate().alpha(1.0f).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        soundPool.autoPause();

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            }
        });
    }

    static boolean intersection(double cx1, double cy1, double r1, double cx2, double cy2, double r2) {

        return (Math.sqrt(Math.pow(cx2 - cx1, 2) + Math.pow(cy2 - cy1, 2)) < (r1 + r2) / 2);
    }

    void decLife() {
        if (lifes > 0) {
            lifes--;
            lifesImages[lifes] = Bitmap.createBitmap(losedHealth);
            if (lifes == 0) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        onLose();
                    }
                }, 500);
            }
        }
    }


    static int loadBestScore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(BEST_SCORE_PREF, 0);
    }

    void saveBestScore(int score) {
        SharedPreferences prefs = this.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(BEST_SCORE_PREF, score);
        editor.apply();
    }

    boolean isRunned() {
        return runned;
    }


    void playBombCatch() {
        soundPool.play(bombCatch, isSoundEnabled ? 1 : 0, isSoundEnabled ? 1 : 0, 2, 0, 1);
    }

    void playEatCandySound() {
        soundPool.play(eatCandySound, isSoundEnabled ? 1 : 0, isSoundEnabled ? 1 : 0, 2, 0, 1);
    }

    void playCandyAppearanceSound() {
        soundPool.play(candyAppearanceSound, isSoundEnabled ? 0.8f : 0, isSoundEnabled ? 0.8f : 0, 1, 0, 1);
    }

    void playBompAppearanceSound() {
        soundPool.play(bombAppearanceSound, isSoundEnabled ? 1 : 0, isSoundEnabled ? 1 : 0, 1, 0, 1);
    }

    void playBombFlyingSound() {
        bombFlyingStream = soundPool.play(bombFlyingSound, isSoundEnabled ? 1 : 0, isSoundEnabled ? 1 : 0, 1, -1, 1);
    }

    void stopBombFlyingSound() {
        soundPool.stop(bombFlyingStream);
    }

    void playBestScoreSound() {
        soundPool.play(bestScoreSound, isSoundEnabled ? 1 : 0, isSoundEnabled ? 1 : 0, 2, 0, 1);
    }


    void confetti() {
        confettiLeftPicture.setVisibility(View.VISIBLE);
        confettiRightPicture.setVisibility(View.VISIBLE);

        Animation animLeft = AnimationUtils.loadAnimation(this, R.anim.left_confetti);
        animLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                confettiLeftPicture.animate().alpha(0).setDuration(1000).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        confettiLeftPicture.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation animRight = AnimationUtils.loadAnimation(this, R.anim.right_confetti);
        animRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                confettiRightPicture.animate().alpha(0).setDuration(800).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        confettiRightPicture.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        confettiLeftPicture.startAnimation(animLeft);
        confettiRightPicture.startAnimation(animRight);
    }

}
