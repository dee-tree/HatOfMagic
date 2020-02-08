package com.codemitry.hatofmagic;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    GameActivity game;
    GameThread thread;
    int dx, dy;

    public GameSurfaceView(GameActivity game) {
        super(game);

        this.game = game;
        getHolder().addCallback(this);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new GameThread(game, this);
        thread.setRunned(true);
        thread.start();
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

//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case (MotionEvent.ACTION_DOWN):
//                dx =
//        }
//
//        return super.onTouchEvent(event);
//
//
//    }
}
