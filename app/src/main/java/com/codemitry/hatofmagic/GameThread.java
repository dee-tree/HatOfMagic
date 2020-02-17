package com.codemitry.hatofmagic;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


public class GameThread extends Thread {
    private boolean runned = false;
    private GameActivity game;
    private SurfaceHolder holder;

    private int bombTime = 0, ballTime = 0;
    private int bombRand = 0, ballRand = 0;

    private int BALL_TIMING = 1000;
    private float BALL_TIMING_RAND = 0.1f;
    private int BOMB_TIMING = 3000;
    private float BOMB_TIMING_RAND = 0.1f;

    public GameThread(GameActivity game, GameSurfaceView surface) {
        super("GameThread");
        this.game = game;
        this.holder = surface.getHolder();
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

            bombTime += delta;
            ballTime += delta;

            if (bombTime >= BOMB_TIMING + bombRand) {
                bombTime = 0;
                game.addBomb();
                bombRand = (int) ((Math.random() * 2 * BOMB_TIMING * BOMB_TIMING_RAND) - BOMB_TIMING * BOMB_TIMING_RAND);
            }

            if (ballTime >= BALL_TIMING + ballRand) {
                ballTime = 0;
                game.addBall();
                ballRand = (int) ((Math.random() * 2 * BALL_TIMING * BALL_TIMING_RAND) - BALL_TIMING * BALL_TIMING_RAND);
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
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    canvas = holder.lockHardwareCanvas();
                canvas = holder.lockCanvas();
                synchronized (holder) {
                    game.draw(canvas);
                    game.update(delta);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
//                try {
//                    sleep(30);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
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


}
