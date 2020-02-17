package com.codemitry.hatofmagic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

class Hat {
    GameActivity game;
    Bitmap sprite;
    private float widthScale = 0.12f;
    private float heightScale = 0.17f;
    private int width, height;
    private int x, y;
    private Matrix rotate, position;
    //    private int rotAngle;
    private boolean isMoved = false;


    Hat(GameActivity game) {

        this.game = game;

        width = (int) (game.getWidth() * widthScale);
        height = (int) (game.getHeight() * heightScale);
        sprite = BitmapFactory.decodeResource(game.getResources(), R.drawable.hat);
        sprite = Bitmap.createScaledBitmap(sprite, width, height, false);

        setCenterX(game.getWidth() / 2);
        setCenterY(game.getHeight() / 2);

        rotate = new Matrix();
        position = new Matrix();
        position.postTranslate(x, y);
    }

    void update() {
    }

    void rotate(int angle) {

        //rotAngle = angle;
        rotate.reset();
        rotate.setRotate(angle, width / 2, height / 2);
        rotate.postTranslate(x, y);
        position.set(rotate);
    }

    void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawBitmap(sprite, position, null);
        }
    }

    void setMoved(boolean moved) {
        this.isMoved = moved;
    }

    boolean getMoved() {
        return isMoved;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    int getCenterX() {
        return x + width / 2;
    }

    int getCenterY() {
        return y + height / 2;
    }

    void setCenterX(int centerX) {
        x = centerX - width / 2;
    }

    void setCenterY(int centerY) {
        y = centerY - height / 2;
    }

    int getHeight() {
        return this.height;
    }

    int getWidth() {
        return this.width;
    }

    void setX(int x) {
        this.x = x;
    }

    void setY(int y) {
        this.y = y;
    }

}
