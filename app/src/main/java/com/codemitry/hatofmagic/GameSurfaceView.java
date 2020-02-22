package com.codemitry.hatofmagic;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    GameActivity game;
    GameThread thread;
//    int dx, dy;

    public GameSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        this.game = (GameActivity) context;
        getHolder().addCallback(this);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        if (thread == null) {
            thread = new GameThread(game, this);
            thread.setRunned(true);
            thread.start();
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunned(false);
        while (retry) {
            try {
                if (thread.isAlive())
                    thread.join();
                retry = false;
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }

        Log.d("Surface", "Surface Destroyed");
    }

    boolean runned() {
        return thread.getRunned();
    }

    void setRunned(boolean run) {
        thread.setRunned(run);
    }

    public void decBombTiming(int bombTiming) {
        thread.decBombTiming(bombTiming);
    }


}
