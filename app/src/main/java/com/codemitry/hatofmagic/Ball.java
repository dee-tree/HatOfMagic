package com.codemitry.hatofmagic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;


class Ball {
    Bitmap sprite;
    GameActivity game;
    private boolean alive;
    private float widthScale = 0.08f;
    private float xBorderRand = 0.2f;
    private float yBorderRand = 0.2f;
    private int x, y;
    private int width;
    private double speedX, dx, speedY, dy, acceleration;
    private int directionX;

    Ball(GameActivity game) {

        this.game = game;
        width = (int) (widthScale * (Math.min(game.getHeight(), game.getWidth())));

        sprite = BitmapFactory.decodeResource(game.getResources(), R.drawable.ball);
        sprite = Bitmap.createScaledBitmap(sprite, width, width, false);

        //System.out.println("width: " + game.getWidth() + "  height: " + game.getHeight());

        alive = true;
        xBorderRand *= game.getWidth();
        yBorderRand *= game.getHeight();
//        for (int i = 0; i < 150; i ++ ) {

        // Генерация начальных координат объекта
        x = (int) ((Math.random() * (1 + xBorderRand + game.getWidth() + xBorderRand)) - xBorderRand);
        if (x >= -width || x <= game.getWidth())
            y = (int) ((Math.random() * (1 + yBorderRand) + game.getHeight()));
        else
            y = (int) ((Math.random() * (1 + yBorderRand + yBorderRand) + game.getHeight() - yBorderRand));
//        y = 300;

        directionX = (x + width / 2 < game.getWidth() / 2) ? 1 : -1;
        speedX = ((Math.random() * 0.5) + 0.2);
        dx = speedX;

        // MAX: -2
        // MIN: -1.7
        speedY = -((Math.random() * 0.3) + 1.7);
        dy = speedY;
        acceleration = 0.002;

    }

    void draw(Canvas canvas) {
        if (alive)
            canvas.drawBitmap(sprite, x, y, null);
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
