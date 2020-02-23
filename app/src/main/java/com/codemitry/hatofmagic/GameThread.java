package com.codemitry.hatofmagic;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Timer;
import java.util.TimerTask;

// Copyright Dmitriy Sokolov 2020

public class GameThread extends Thread {
    private boolean runned = false;
    private GameActivity game;
    private SurfaceHolder holder;

    private Timer bombTimer, candyTimer;


    private int ballTiming = 1500;
    private float BALL_TIMING_RAND = 0.3f;
    private int bombTiming = 6000;
    private float BOMB_TIMING_RAND = 0.2f;

    GameThread(GameActivity game, GameSurfaceView surface) {
        super("GameThread");
        this.game = game;
        this.holder = surface.getHolder();

        bombTimer = new Timer();
        candyTimer = new Timer();
    }

    @Override
    public void run() {
        Log.d("GameThread", "Runned");
        Canvas canvas;
        int delta;
        long cTime, time = System.currentTimeMillis(), counter = 0L;

        while (runned) {
            cTime = System.currentTimeMillis();
            canvas = null;
            delta = (int) (cTime - time);
            counter += delta;

            game.bombTime += delta;
            game.ballTime += delta;

            if (game.bombTime >= bombTiming + game.bombRand) {
                game.bombTime = 0;
                if (game.isRunned()) {
                    game.playBompAppearanceSound();
                    bombTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (game.isRunned())
                                game.addBomb();
                        }
                    }, 600);

                }
                game.bombRand = (int) ((Math.random() * 2 * bombTiming * BOMB_TIMING_RAND) - bombTiming * BOMB_TIMING_RAND);
            }

            if (game.ballTime >= ballTiming + game.ballRand) {
                game.ballTime = 0;

                if (game.isRunned()) {
                    game.playCandyAppearanceSound();
                    candyTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (game.isRunned())
                                game.addBall();
                        }
                    }, 100);


                    game.ballRand = (int) ((Math.random() * 2 * ballTiming * BALL_TIMING_RAND) - ballTiming * BALL_TIMING_RAND);
                }
            }
            if (counter >= 1000) {

                // Мерим секунды :D
//                game.addBall();
//                game.addBomb();
//                 Прошла 1 секунда
//                System.out.println("1 second!");
                counter -= 1000;
            }
            try {
                canvas = holder.lockCanvas();
                synchronized (holder) {
                    game.draw(canvas);
                    game.update(delta);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            time = cTime;
        }
        Log.d("GameThread", "Stopped");
    }

    void setRunned(boolean run) {
        runned = run;
    }

    boolean getRunned() {
        return this.runned;
    }

    public void decBombTiming(int bombTiming) {
        if (this.bombTiming > 3500)
            this.bombTiming -= bombTiming;
    }
}
