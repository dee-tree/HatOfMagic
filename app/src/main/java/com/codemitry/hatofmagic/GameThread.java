package com.codemitry.hatofmagic;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


public class GameThread extends Thread {
    private boolean runned = false;
    private GameActivity game;
    private SurfaceHolder holder;

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
            if (counter >= 1000) {
                game.addBall();
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
