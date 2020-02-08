package com.codemitry.hatofmagic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private boolean started = false;
    private GameSurfaceView surface;
    private int height, width;
    private ArrayList<Ball> ball;
    private Hat hat;
    private int dx, dy;
    private int angle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        hat = new Hat(this);
        ball = new ArrayList<>();

        surface = new GameSurfaceView(this);
        setContentView(surface);

        surface.setOnTouchListener(new SwipeTouchListener(this));
    }

    void addBall() {
        ball.add(new Ball(this));
    }


    void update(int delta) {
        for (int i = ball.size() - 1; i >= 0; i--) {
            ball.get(i).update(delta);
            if (!ball.get(i).isAlive())
                ball.remove(i);
        }
        hat.rotate(angle);
        hat.update();
        //ball.update(delta);
    }

    synchronized void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            for (Ball b : ball)
                b.draw(canvas);
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
                dx = (int) (event.getX() - hat.getCenterX());
                dy = (int) (event.getY() - hat.getCenterY());
                break;
            case MotionEvent.ACTION_MOVE:
                hat.setCenterX(hat.getCenterX() + dx / 5);
                hat.setCenterY(hat.getCenterY() + dy / 5);

                dx = (int) (event.getX() - hat.getCenterX());
                dy = (int) (event.getY() - hat.getCenterY());
                if ((dx > 0) && (dy <= 0))
                    angle = (int) Math.abs(Math.atan((double) dy / (double) dx) * 180 / Math.PI);
                else if (dx > 0)
                    angle = (int) (-1 * Math.abs(Math.atan((double) dy / (double) dx) * 180 / Math.PI));
                else if ((dx < 0) && (dy <= 0))
                    angle = 90 + (int) Math.abs(Math.atan((double) dy / (double) dx) * 180 / Math.PI);
                else if (dx < 0)
                    angle = -90 - (int) Math.abs(Math.atan((double) dy / (double) dx) * 180 / Math.PI);
//                if (dx != 0)
//                    angle =(int) (Math.atan((double)dy/(double)dx) * 180 / Math.PI);
                System.out.println("dy: " + dy + " dx: " + dx + " angle: " + " dy/dx: " + (double) dy / (double) dx + " angle: " + angle);
                break;

        }
        return super.onTouchEvent(event);
    }
}
