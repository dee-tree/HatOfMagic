package com.codemitry.hatofmagic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;


class Bomb {
    Bitmap[] sprite;
    int[] resources = {R.drawable.bomb1, R.drawable.bomb2, R.drawable.bomb3, R.drawable.bomb4};
    int number, showCount;
    final int SHOW = 6;
    GameActivity game;
    private boolean alive;
    private float widthScale = 0.095f;
    private float xBorderRand = 0.2f;
    private float yBorderRand = 0.1f;
    private int x, y;
    private int width, height;
    private double speedX, dx, speedY, dy, acceleration;
    private int directionX;

    private int angle = 0;
    private Matrix rotate, position;

    Bomb(GameActivity game) {

        this.game = game;
        width = (int) (widthScale * (Math.min(game.getHeight(), game.getWidth())));
        height = (int) Math.round(width * 1.25);

        sprite = new Bitmap[4];
        for (int i = 0; i < sprite.length; i++) {
            sprite[i] = BitmapFactory.decodeResource(game.getResources(), resources[i]);
            sprite[i] = Bitmap.createScaledBitmap(sprite[i], width, height, false);
        }

        number = 0;
        showCount = 0;
        alive = true;
        xBorderRand *= game.getWidth();
        yBorderRand *= game.getHeight();

        speedX = 0;

        // Генерация начальных координат объекта
        x = (int) (Math.random() * (1 + game.getWidth()));
        y = (int) ((Math.random() * (1 + yBorderRand) + game.getHeight()));


        directionX = (x + width / 2 < game.getWidth() / 2) ? 1 : -1;
        speedX += ((Math.random() * 0.5) + 0.2);
        dx = speedX;


        speedY = -((Math.random() * 0.4) + 2);
        dy = speedY;
        acceleration = 0.003;

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
        if (alive) {
            canvas.drawBitmap(sprite[number], position, null);

            if (showCount > SHOW) {
                number = (number + 1) % sprite.length;
                showCount = 0;
            } else
                showCount++;
        }
    }

    void update(int delta) {
        if (alive) {
            if (!isAlive() || y > game.getHeight() && (speedY > 0)) {
                this.clear();
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
            angle++;
            rotate(directionX * angle++);

            // Проверка выхода за нижнюю границу

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


    public boolean isAlive() {
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
