package com.codemitry.hatofmagic;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    private boolean runned = false;
    private boolean losed = false;
    private TextView scoreText;
    private TextView pauseText;
    private Bitmap[] lifesImages;
    private Bitmap losedHealth;
    private GameSurfaceView surface;
    private Bitmap background;
    private Bitmap pauseButton;
    private int height, width;
    private ArrayList<Ball> ball;
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
        width = displayMetrics.widthPixels;

        hat = new Hat(this);
        hat.setCenterX(width / 2);
        hat.setCenterY(height / 2);

        ball = new ArrayList<>();


        //surface = new GameSurfaceView(this);

        setContentView(R.layout.activity_game);
        scoreText = findViewById(R.id.scoreText);
        surface = findViewById(R.id.surface);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background, width, height, false);

        pauseButton = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        pauseButton = Bitmap.createScaledBitmap(pauseButton, height / 8, height / 8, false);

        pauseText = findViewById(R.id.pauseText);

        score = 0;
        lifesImages = new Bitmap[lifes];
        lifesImages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.health);
        lifesImages[0] = Bitmap.createScaledBitmap(lifesImages[0], (int) (0.12 * height), (int) (0.1 * height), false);
        lifesImages[1] = Bitmap.createBitmap(lifesImages[0]);
        lifesImages[2] = Bitmap.createBitmap(lifesImages[0]);

        losedHealth = BitmapFactory.decodeResource(getResources(), R.drawable.losed_health);
        losedHealth = Bitmap.createScaledBitmap(losedHealth, (int) (0.12 * height), (int) (0.1 * height), false);

        runned = true;
    }

    void addBall() {
        ball.add(new Ball(this));
    }


    void update(int delta) {
        if (runned) {
            for (int i = ball.size() - 1; i >= 0; i--) {
                ball.get(i).update(delta);
                if ((intersection(ball.get(i).getCenterX(), ball.get(i).getCenterY(), ball.get(i).getWidth() / 2, hat.getCenterX(), hat.getCenterY(), hat.getWidth() / 3)) && (hat.getMoved()) && ball.get(i).isAlive()) {
                    ball.get(i).setAlive(false);
                    score++;
                    scoreText.setText(String.valueOf(score));

                }
                if (!ball.get(i).isAlive())
                    ball.remove(i);
            }
            hat.update();
            hat.setMoved(false);
        }
    }

    synchronized void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawBitmap(background, 0, 0, null);
            for (int i = 0; i < 3; i++)
                canvas.drawBitmap(lifesImages[i], (width - lifesImages[0].getWidth() * (i + 1)), 10, null);
            for (Ball b : ball)
                b.draw(canvas);
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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                dx = (int) (event.getX() - hat.getCenterX());
//                dy = (int) (event.getY() - hat.getCenterY());
                break;
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
//                System.out.println("dy: " + dy + " dx: " + dx + " angle: " + " dy/dx: " + (double) dy / (double) dx + " angle: " + angle);
                break;

        }
        hat.setMoved(true);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (surface.runned() && !losed) {
            pause();
        } else if (losed)
            finish();
        else if (!surface.runned() && !losed) {
            start();
        }
//    if (surface.runned())
//        onPauseClick(null);
    }

    public void onPauseClick(View v) {
        if (surface.runned()) {
            pause();
        }
    }

    void pause() {
        if (surface.runned()) {
            surface.thread.setRunned(false);
            pauseText.setText(getResources().getString(R.string.pause_text));

            pauseText.setEnabled(false);
            pauseText.setAlpha(0);
            pauseText.setVisibility(View.VISIBLE);
            pauseText.animate().alpha(1.0f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    pauseText.setEnabled(true);
                    pauseText.animate().setListener(null);
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
        surface.thread = new GameThread(this, surface);
        surface.thread.setRunned(true);
        pauseText.animate().alpha(0f).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pauseText.setVisibility(View.GONE);
                pauseText.animate().setListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        surface.thread.start();
    }

    public void onPauseTextClick(View v) {
        if (!surface.runned() && !losed) {
            //if (((TextView) v).getText().toString().equals(getResources().getString(R.string.pause_text))) {
            start();
        }
    }

    void onLose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                losed = true;
                surface.thread.setRunned(false);
                pauseText.setText(getResources().getString(R.string.lose_text));

                pauseText.setEnabled(false);
                pauseText.setAlpha(0);
                pauseText.setVisibility(View.VISIBLE);
                pauseText.animate().alpha(1.0f).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pauseText.setEnabled(true);
                        pauseText.animate().setListener(null);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });


//                pauseText.setVisibility(View.VISIBLE);
            }
        });
    }

    static boolean intersection(double cx1, double cy1, double r1, double cx2, double cy2, double r2) {

        return (Math.sqrt(Math.pow(cx2 - cx1, 2) + Math.pow(cy2 - cy1, 2)) < r1 + r2);
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
//            onLose();
        }
    }
}
