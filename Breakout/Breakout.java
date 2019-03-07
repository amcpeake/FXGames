package Breakout;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;

public class Breakout extends Application {

    class Item extends Rectangle {
        double xvelocity;
        double yvelocity;

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

        boolean inRangeX(double x1, double x2){
            double minX = this.getBoundsInParent().getMinX();
            double maxX = this.getBoundsInParent().getMinX();

            return ((x1 <= minX && minX <= x2) || (x1 <= maxX && maxX <= x2));
        }

        boolean inRangeY(double y1, double y2){
            double minY = this.getBoundsInParent().getMinY();
            double maxY = this.getBoundsInParent().getMinY();

            return ((y1 <= minY && minY <= y2) || (y1 <= maxY && maxY <= y2));
        }

        void move() {
            if (this.xvelocity == 0 && this.yvelocity == 0) return;

            final double length = this.getHeight() / 2;
            final double width = this.getWidth() / 2;

            double x = this.getCenterX() + this.xvelocity;
            double y = this.getCenterY() + this.yvelocity;

            if (y - length > 0 && y + length < H) {
                this.setCenterY(y);
            }

            if (x - width > 0 && x + width < W) {
                this.setCenterX(x);
            }
        }
    }

    private class Paddle extends Item {
        private int score;
        private int lives = 3;
        private final static double speed = 3.0;

        private Paddle() {
            this.setWidth(150.0f);
            this.setHeight(25.0f);
            this.setFill(Color.BLUE);
            this.setId("player");
        }
    }

    private class Ball extends Item {
        private final static double speed = 5.0;

        private Ball() {
            this.setWidth(10.0f);
            this.setHeight(10.0f);
            this.setFill(Color.WHITE);
            this.setId("ball");
        }
    }

    private class Brick extends Item {
        private Brick(Color color, double x, double y) {
            this.setWidth(50.0f);
            this.setHeight(25.0f);
            this.setX(x);
            this.setY(y);
            this.setFill(color);
            this.setId("brick");
        }
    }

    private class Border extends Item {
        private Border(double x1, double y1, double x2, double y2, Color color, String ID) {
            this.setX(x1);
            this.setY(y1);
            this.setWidth(x2 - x1);
            this.setHeight(y2 - y1);
            this.setFill(color);
            this.setId(ID);
        }


    }

    private class Scoreboard extends Text {
        private Scoreboard(double x) {
            this.setX(x);
            this.setY(50);
            this.setFill(Color.WHITE);
            this.setFont(new Font("Tlwg Typewriter", 20.0f));
        }
    }

    private static final double W = 850, H = 1200;
    private Paddle player;
    private Ball ball;
    private Brick[] bricks;
    private Color[] colors = new Color[] {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.VIOLET};
    private Border[] gameArea;
    private Scoreboard score;
    private Scoreboard lives;


    private void refresh() {
        player.setCenterX(W / 2);
        player.setCenterY(H - 30);


        ball.setCenterX(W / 2);
        ball.setCenterY(H / 2);
        ball.xvelocity = 0;
        ball.yvelocity = Ball.speed;


        try {
            Thread.sleep(1000);
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start(Stage stage) throws Exception{
        player = new Paddle();

        ball = new Ball();

        score = new Scoreboard(15);
        lives = new Scoreboard(W - 250);

        Group root = new Group(player, ball, score, lives);
        refresh();


        gameArea = new Border[] {
                new Border(0, 0, 10, H, Color.WHITE, "left"), // Left wall
                new Border(0, 0, W, 10, Color.WHITE, "top"), // Top Wall
                new Border(W - 10, 0, W, H, Color.WHITE, "right"), // Right Wall
                new Border(0, H / 7, W, H / 7 + 10, Color.WHITE, "ceiling"), // Scoreboard divider
                new Border(0, H - 10, W, H, Color.WHITE, "floor"),
        };

        for (Border border: gameArea) {
            root.getChildren().add(border);
        }

        bricks = new Brick[84];
        for (Color color: colors) {
            int cindex = java.util.Arrays.asList(colors).indexOf(color);
            for (int i = 0; i < 14; i++) {
                int bindex = i + (cindex * 14);
                bricks[bindex] = new Brick(color, i * 60 + 10, cindex * 35 + 200);
                root.getChildren().add(bricks[bindex]);
            }
        }

        Scene scene = new Scene(root, W, H, Color.BLACK);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case A:     player.xvelocity = -Paddle.speed; break;
                case D:     player.xvelocity = Paddle.speed; break;
                case LEFT:  player.xvelocity = -Paddle.speed; break;
                case RIGHT: player.xvelocity = Paddle.speed; break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case A:     player.xvelocity = 0; break;
                case D:     player.xvelocity = 0; break;
                case LEFT:  player.xvelocity = 0; break;
                case RIGHT: player.xvelocity = 0; break;
            }
        });

        stage.setScene(scene);
        stage.show();
        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                player.move();
                ball.move();
                score.setText("Score: " + player.score);
                lives.setText("Lives Remaining: " + player.lives);
                for (Node object: root.getChildren()) {
                    if (ball.getBoundsInParent().intersects(object.getBoundsInParent())) { // Collision detection
                        if (!object.getId().isEmpty() && ! object.getId().equals("ball")) {
                            switch (object.getId()) {
                                case "floor":
                                    player.lives -= 1;
                                    refresh();
                                    break;
                                    
                                case "ceiling":
                                    ball.yvelocity *= -1;
                                    break;
                                    
                                case "brick":
                                    double minx = object.getBoundsInParent().getMinX();
                                    double miny = object.getBoundsInParent().getMinY();
                                    double maxx = object.getBoundsInParent().getMaxX();
                                    double maxy = object.getBoundsInParent().getMaxY();
                                    if (ball.inRangeX(minx, maxx))
                                        ball.yvelocity *= -1;
                                    if (ball.inRangeY(miny, maxy))
                                        ball.xvelocity *= -1;
                                    root.getChildren().remove(object);
                                    player.score += 10;
                                    break;
                                    
                                case "player":
                                    ball.yvelocity *= -1;
                                    ball.xvelocity = (ball.getCenterX() - player.getCenterX()) / Ball.speed;
                                    break;
                                    
                                default:
                                    ball.xvelocity *= -1;
                                    break;
                            }
                            break;
                        }
                    }
                }
            }
        };

        timer.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
