package Pong;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.*;

public class Main extends Application {

    class Item extends Rectangle {
        double xvelocity = 0.0;
        double yvelocity = 0.0;

        double getCenterX() {
            return (this.getX() + this.getWidth() / 2);
        }

        void setCenterX(double x) {
            this.setX(x - (this.getWidth() / 2));
        }

        double getCenterY() {
            return (this.getY() + this.getHeight() / 2);
        }

        void setCenterY(double y) {
            this.setY(y - (this.getHeight() / 2));
        }

        void move() {
            if (this.xvelocity == 0 && this.yvelocity == 0) return;

            final double length = this.getHeight() / 2;
            final double width = this.getWidth() / 2;

            double x = this.getCenterX() + this.xvelocity;
            double y = this.getCenterY() + this.yvelocity;

            if (y - length >= 0 && y + length <= H) {
                this.setCenterY(y);
            }

            if (x - width >= 0 && x + width <= W) {
                this.setCenterX(x);
            }
        }
    }

    private class Paddle extends Item {
        private int score;
        private final static double speed = 3.0;

        private Paddle() {
            this.setWidth(10.0f);
            this.setHeight(75.0f);
            this.setFill(Color.WHITE);
            this.score = 0;
        }
    }

    private class Ball extends Item {
        private final static double speed = 5.0;

        private Ball() {
            this.setWidth(10.0f);
            this.setHeight(10.0f);
            this.setFill(Color.WHITE);
        }
    }

    private class Scoreboard extends Text {
        private Scoreboard() {
            this.setFill(Color.WHITE);
            this.setFont(new Font("Tlwg Typewriter", 20.0f));
            this.setY(20.0f);
        }

        private double getWidth() {
            return this.getBoundsInLocal().getWidth();
        }

        private void setCenterX(double x) {
            this.setX(x - (this.getWidth() / 2));
        }

    }

    private static final double W = 1920, H = 1080;

    private Paddle player1;
    private Paddle player2;
    private Scoreboard scoreboard;
    private Ball ball;
    private Line divider;

    private void refresh() {
        player1.setCenterX(10.0f);
        player1.setCenterY(H / 2);

        player2.setCenterX(W - 20.0f);
        player2.setCenterY(H / 2);

        ball.setCenterX(W / 2);
        ball.setCenterY(H / 2);
        ball.yvelocity = 0;

        if (player1.score > player2.score)
            ball.xvelocity = Ball.speed;
        else
            ball.xvelocity = -Ball.speed;

        scoreboard.setText(player1.score + "   " + player2.score);
        scoreboard.setCenterX(W / 2);

        try {
            Thread.sleep(1000);
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start(Stage stage) {
        player1 = new Paddle();
        player2 = new Paddle();

        scoreboard = new Scoreboard();

        ball = new Ball();

        divider = new Line(W / 2, H, W / 2, 0);
        divider.getStrokeDashArray().addAll(20d, 50d);
        divider.setStroke(Color.WHITE);
        divider.setStrokeWidth(5.0f);

        Group root = new Group(player1, player2, scoreboard, ball, divider);

        refresh();

        Scene scene = new Scene(root, W, H, Color.BLACK);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:     player1.yvelocity = -Paddle.speed; break;
                case S:     player1.yvelocity = Paddle.speed; break;
                case UP:    player2.yvelocity = -Paddle.speed; break;
                case DOWN:  player2.yvelocity = Paddle.speed; break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W:     player1.yvelocity = 0; break;
                case S:     player1.yvelocity = 0; break;
                case UP:    player2.yvelocity = 0; break;
                case DOWN:  player2.yvelocity = 0; break;
            }
        });

        stage.setScene(scene);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                player1.move();
                player2.move();
                ball.move();

                if (ball.getBoundsInParent().intersects(player1.getBoundsInParent())) {
                    ball.xvelocity = Ball.speed;
                    ball.yvelocity = (player1.getCenterY() - ball.getCenterY()) / Ball.speed;
                }

                else if (ball.getBoundsInParent().intersects(player2.getBoundsInParent())) {
                    ball.xvelocity = -Ball.speed;
                    ball.yvelocity = (player2.getCenterY() - ball.getCenterY()) / Ball.speed;
                }

                else if (ball.getCenterX() - ball.getWidth() <= 0.0) { // Off left side
                    player2.score += 1;
                    refresh();
                }

                else if (ball.getCenterX() + ball.getWidth() >= W) { // Off right side, point for p1
                    player1.score += 1;
                    refresh();
                }

                if (ball.getCenterY() - ball.getHeight() <= 0.0 || ball.getCenterY() + ball.getHeight() >= H)
                    ball.yvelocity *= -1;

            }
        };

        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
