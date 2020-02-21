package com.codemitry.hatofmagic;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    static final String BEST_SCORE_PREF = "BestScore";

    private SoundPool soundPool;
    private int eatCandySound;
    private int candyAppearanceSound;
    private int bombAppearanceSound;
    private int bombFlyingSound, bombFlyingStream;
    private MediaPlayer mediaPlayer;
    private boolean runned = false;
    private boolean losed = false;
    private boolean isSoundEnabled;

    private ConstraintLayout loseLayout, pauseLayout;
    private TextView scoreText;

    private Bitmap[] lifesImages;
    private Bitmap losedHealth;
    private GameSurfaceView surface;
    private Bitmap background;
    private ImageView pauseView;
    private int height, width;
    private ArrayList<Candy> candies;
    private ArrayList<Bomb> bombs;

    int bombTime = 0, ballTime = 0;
    int bombRand = 0, ballRand = 0;

    private Hat hat;
    private int dx, dy;
    private int angle;
    private int score;
    private int lifes = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels + getNavBarHeight(this);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        width = point.x;

        hat = new Hat(this);
        hat.setCenterX(width / 2);
        hat.setCenterY(height / 2);

        candies = new ArrayList<>();
        bombs = new ArrayList<>();


        isSoundEnabled = MainActivity.isEnabledAudio(this);

        surface = null;

        setContentView(R.layout.activity_game);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        scoreText = findViewById(R.id.scoreText);
        surface = findViewById(R.id.surface);


        loseLayout = findViewById(R.id.loseLayout);
        pauseLayout = findViewById(R.id.pauseLayout);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background, width, height, false);

        pauseView = findViewById(R.id.pauseView);

        score = 0;

        lifesImages = new Bitmap[lifes];
        lifesImages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.health);
        lifesImages[0] = Bitmap.createScaledBitmap(lifesImages[0], (int) (0.12 * height), (int) (0.1 * height), false);
        lifesImages[1] = Bitmap.createBitmap(lifesImages[0]);
        lifesImages[2] = Bitmap.createBitmap(lifesImages[0]);

        losedHealth = BitmapFactory.decodeResource(getResources(), R.drawable.losed_health);
        losedHealth = Bitmap.createScaledBitmap(losedHealth, (int) (0.12 * height), (int) (0.1 * height), false);

        runned = true;

        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        eatCandySound = soundPool.load(this, R.raw.eat_candy, 2);
        candyAppearanceSound = soundPool.load(this, R.raw.candy_appearance, 1);
        bombAppearanceSound = soundPool.load(this, R.raw.bomp_appearance, 1);
        bombFlyingSound = soundPool.load(this, R.raw.bomb_flying, 0);

        mediaPlayer = MediaPlayer.create(this, R.raw.background_game_sound);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });

        if (isSoundEnabled) {
            mediaPlayer.setVolume(0, 1);
        } else
            mediaPlayer.setVolume(0, 0);
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
        if (runned) {
            mediaPlayer.start();
            pause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
//        if (runned)
//            pause();
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
                        surface.decBombTiming(50);
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
                    // TODO: Добавить эффект дамага при ловле бомбы
                    decLife();
                }

                if (!bombs.get(i).isAlive()) {
                    bombs.remove(i);
                    stopBombFlyingSound();
                }
            }
//            hat.update();
//            hat.setMoved(false);
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

        }
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
//                System.out.println("dy: " + dy + " dx: " + dx + " angle: " + " dy/dx: " + (double) dy / (double) dx + " angle: " + angle);
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
        else if (!runned && !losed && !surface.runned()) {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (score > loadBestScore())
                    saveBestScore(score);

                loseLayout.setAlpha(0);
                loseLayout.setVisibility(View.VISIBLE);
                pauseView.setVisibility(View.GONE);
                loseLayout.animate().alpha(1.0f).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        soundPool.autoPause();
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


    int loadBestScore() {
        SharedPreferences prefs = this.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
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


    void playEatCandySound() {
        soundPool.play(eatCandySound, 0, isSoundEnabled ? 1 : 0, 0, 0, 2);
    }

    void playCandyAppearanceSound() {
        soundPool.play(candyAppearanceSound, 0, isSoundEnabled ? 0.8f : 0, 1, 0, 1);
    }

    void playBompAppearanceSound() {
        soundPool.play(bombAppearanceSound, 0, isSoundEnabled ? 1 : 0, 1, 0, 1);
    }

    void playBombFlyingSound() {
        bombFlyingStream = soundPool.play(bombFlyingSound, 0, isSoundEnabled ? 1 : 0, 2, -1, 0);
    }

    void stopBombFlyingSound() {
        soundPool.stop(bombFlyingStream);
    }


    public int getNavBarHeight(Context c) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = c.getResources();

            int orientation = resources.getConfiguration().orientation;
            int resourceId;
            resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");

            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

}
