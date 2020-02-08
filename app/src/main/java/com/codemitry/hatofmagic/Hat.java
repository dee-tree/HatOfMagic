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
    private Matrix rotate;
    private int rotAngle;

    Hat(GameActivity game) {

        this.game = game;

        width = (int) (game.getWidth() * widthScale);
        height = (int) (game.getHeight() * heightScale);
        sprite = BitmapFactory.decodeResource(game.getResources(), R.drawable.hat);
        sprite = Bitmap.createScaledBitmap(sprite, width, height, false);

        x = (game.getWidth() - width) / 2;
        y = game.getHeight() * 3 / 4;

        rotate = new Matrix();
    }

    void update() {
    }

    void rotate(int angle) {
        rotate.reset();
        rotate.setRotate(angle);
//        rotAngle += angle;
//
        sprite = Bitmap.createBitmap(sprite, 0, 0, sprite.getWidth(), sprite.getHeight(), rotate, true);
        sprite = Bitmap.createScaledBitmap(sprite, width, height, false);
    }

    void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawBitmap(sprite, x, y, null);
        }
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

    void setX(int x) {
        this.x = x;
    }

    void setY(int y) {
        this.y = y;
    }

}
