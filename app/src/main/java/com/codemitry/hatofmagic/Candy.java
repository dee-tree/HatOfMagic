package com.codemitry.hatofmagic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.util.Random;


class Candy {
    Bitmap sprite;
    GameActivity game;
    private int[] resources = {R.drawable.candy_blue, R.drawable.candy_gray, R.drawable.candy_green, R.drawable.candy_violet, R.drawable.candy_yellow};
    private boolean alive;
    private float widthScale = 0.095f;
    //    private float xBorderRand = 0.1f;
    private float yBorderRand = 0.02f;
    private int x, y;
    private int width, height;
    private double speedX, dx, speedY, dy, acceleration;
    private int directionX;

    private int angle = 0, dAngle;
    private Matrix rotate, position;

    Candy(GameActivity game) {

        this.game = game;
        width = (int) (widthScale * (Math.min(game.getHeight(), game.getWidth())));
        height = (int) Math.round(width * 8.5 / 13);

        sprite = BitmapFactory.decodeResource(game.getResources(), resources[new Random().nextInt(resources.length)]);
        sprite = Bitmap.createScaledBitmap(sprite, width, height, false);

        alive = true;
        //xBorderRand *= game.getWidth();
        yBorderRand *= game.getHeight();

        speedX = 0;

        // Генерация начальных координат объекта
        x = (int) (Math.random() * (1 + game.getWidth()));

//        y = (int) ((Math.random() * (1 + yBorderRand + yBorderRand) + game.getHeight() - yBorderRand));
        y = (int) (game.getHeight() * 1.2);

        directionX = (x + width / 2 < game.getWidth() / 2) ? 1 : -1;
        //speedX += ((Math.random() * 0.5) + 0.2);   SAVE
        speedX += ((Math.random() * 0.0003907) + 0.0001563) * game.getWidth();
        dx = speedX;

//        speedY = -((Math.random() * 0.5) + 1.8);    SAVE
        speedY = -((Math.random() * 0.0007) + 0.0023) * game.getHeight();   // 0.0027
        dy = speedY;
//        acceleration = 0.0028;    SAVE
        acceleration = 0.0000038 * game.getHeight();

        angle = new Random().nextInt(360) + 1;
        dAngle = new Random().nextInt(3) + 1;

        rotate = new Matrix();
        position = new Matrix();
        position.postTranslate(x, y);

    }

    void rotate(int angle) {

        rotate.reset();
        rotate.setRotate(angle, width / 2, height / 2);
        rotate.postTranslate(x, y);
        position.set(rotate);
    }

    void draw(Canvas canvas) {
        if (alive)
            canvas.drawBitmap(sprite, position, null);
    }

    void update(int delta) {
        if (alive) {
            if (!isAlive() || y > game.getHeight() && (speedY > 0)) {
                this.clear();
                // Забираем одну жизнь
                game.decLife();
            }
            dx = speedX * delta * directionX;
            x += dx;
            if ((dx > 0) && (x + width >= game.getWidth()))
                directionX = -1;
            else if ((dx < 0) && (x <= 0))
                directionX = 1;

            speedY += delta * acceleration;
            dy = delta * speedY;
            y += dy;

            angle += dAngle;
            rotate(directionX * angle);


//        System.out.println("x: " + x + " dx: " + dx + " speedX: " + speedX + " delta: " + delta);
//        System.out.println("y: " + y + " dy: " + dy + " speedY: " + speedY + " delta: " + delta + " acceleration: " + acceleration);
        }
    }

    private void clear() {
        alive = false;
        sprite = null;

    }

    void setAlive(boolean alive) {
        this.alive = alive;
    }


    boolean isAlive() {
        return alive;
    }

    public int getY() {
        return y;
    }

    int getCenterX() {
        return x + width / 2;
    }

    int getCenterY() {
        return y + width / 2;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    int getWidth() {
        return this.width;
    }
}
