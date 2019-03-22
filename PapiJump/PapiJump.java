package PapiJump;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.shape.Rectangle;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import java.util.Random;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.List;
import java.util.ArrayList;

public class PapiJump extends Application {
    private Scoreboard scoreboard;
    private Papi papi;
    private Background bg;
    private Platform platform;
    private List<Platform> platforms;
    private List<String> activeKeys = new ArrayList<>(); // Prevents movement jamming
    private Group root;
    private enum pTypes {REGULAR, FALLING, SUPER}

    private static final double W = 540;
    private static final double H = 960;


    double getRand(double min, double max){ // Gets a random number between min and max inclusive
        return (max - min) * new Random().nextDouble() + min;
    }

    void refresh(){ // Resets the game field after death
        papi.sprite.setCenterX(W / 2);
        papi.sprite.setCenterY(H - 200);
        papi.yvel = 0;
        papi.xvel = 0;
        papi.yacc = 0.1;
        papi.score = 0;

        platform = new Platform(W / 2, H - 50);
        platforms = new ArrayList<>();
        platforms.add(platform);

        root.getChildren().add(platform);
    }


    class Scoreboard extends Text {
        Scoreboard(){
            this.setFill(Color.WHITE);
            this.setFont(new Font("tlwg typewriter", 20));
            this.setX(10);
            this.setY(20);
            this.setViewOrder(1);
        }
    }

    class Background extends Rectangle {
        Background() {
            this.setFill(Color.DARKCYAN);
            this.setWidth(W);
            this.setHeight(H);
            this.setX(0);
            this.setY(0);
            this.setViewOrder(3);
        }
    }

    class Sprite extends ImageView {
        Sprite(Image img){
            this.setImage(img);
        }

        void setCenterX(double x) {
            // Set sprite location
            this.setX(x - (this.getFitWidth() / 2));
        }

        void setCenterY(double y) {
            this.setY(y - (this.getFitHeight() / 2));
        }

        double getCenterX() {
            return this.getX() + (this.getFitWidth() / 2);
        }

        double getCenterY() {
            return this.getY() + (this.getFitHeight() / 2);
        }

    }

    class Platform extends Rectangle {
        double jbonus;
        double xvel;
        boolean falls;

        Platform(double x, double y) {
            this.setViewOrder(2);
            this.setWidth(getRand(50, 100));
            this.setHeight(10);
            this.setCenterX(x);
            this.setCenterY(y);

            this.jbonus = 6.25;
            this.falls = false;


            double rand = getRand(1, 100);
            if (rand <= 90)
                this.xvel = 0;
            else if (rand <= 95)
                this.xvel = -2;
            else
                this.xvel = 2;

            pTypes ptype;
            rand = getRand(1, 100);
            if (rand <= 70)
                ptype = pTypes.REGULAR;
            else if (rand <= 95)
                ptype = pTypes.FALLING;
            else
                ptype = pTypes.SUPER;

            switch (ptype) {
                case REGULAR:
                    this.setFill(Color.GREEN);
                    break;

                case FALLING:
                    this.falls = true;
                    this.setFill(Color.YELLOW);
                    break;

                case SUPER:
                    this.setFill(Color.RED);
                    this.jbonus = 10;
                    break;
            }


        }

        void move() {
            if (this.getCenterX() + xvel < 0) // Horizontal wraparound
                this.setCenterX(W + (this.getCenterX() + xvel));
            else if (this.getCenterX() + xvel > W)
                this.setCenterX(0 + (this.getCenterX() + xvel - W));
            else
                this.setCenterX(this.getCenterX() + xvel);
        }

        void setCenterX(double x) {
            // Set sprite location
            this.setX(x - (this.getWidth() / 2));
        }

        void setCenterY(double y) {
            this.setY(y - (this.getHeight() / 2));
        }

        double getCenterX() {
            return this.getX() + (this.getWidth() / 2);
        }

        double getCenterY() {
            return this.getY() + (this.getHeight() / 2);
        }
    }

    class Papi {
        Image img;
        Sprite sprite;
        int score;
        double xvel, yvel, yacc;
        double xspeed = 3.0;
        double yspeed = 10.0;

        Papi(){
            this.img = new Image("file:///home/amcpeake/Downloads/papi.png");
            this.sprite = new Sprite(img);
            this.sprite.setFitWidth(50);
            this.sprite.setFitHeight(50);
            this.sprite.setViewOrder(1);
        }

        void move() {
            this.yvel += yacc;

            if (this.yvel > this.yspeed)
                this.yvel = this.yspeed;
            else if (this.yvel < -this.yspeed)
                this.yvel = -this.yspeed;

            if (this.sprite.getCenterX() + xvel < 0) // Horizontal wraparound
                this.sprite.setCenterX(W + (this.sprite.getCenterX() + xvel));
            else if (this.sprite.getCenterX() + xvel > W)
                this.sprite.setCenterX(0 + (this.sprite.getCenterX() + xvel - W));
            else
                this.sprite.setCenterX(this.sprite.getCenterX() + xvel);

            if (this.sprite.getCenterY() + yvel < (H / 2) - 100)
                this.sprite.setCenterY((H / 2) - 100);
            else if (this.sprite.getCenterY() + yvel > H)
                this.sprite.setCenterY(H);
            else
                this.sprite.setCenterY(this.sprite.getCenterY() + yvel);

            if (this.sprite.getCenterY() + yvel >= H) {
                for (Platform p : platforms) {
                    root.getChildren().remove(p);
                }
                refresh();
            }
        }

        void jump(double jbonus) {
            this.yvel = -jbonus;
        }
    }

    @Override
    public void start(Stage stage) throws Exception{
        bg = new Background();
        scoreboard = new Scoreboard();

        papi = new Papi();

        Rectangle rcover = new Rectangle(W, 0, 100, H); // "Veil" on right side (pieces pass behind it)
        rcover.setViewOrder(0);
        Rectangle bcover = new Rectangle(0, H, W, 100); // "Veil" on bottom side
        bcover.setViewOrder(0);

        root = new Group(bg, bcover, rcover, papi.sprite, scoreboard);

        refresh();

        stage.setTitle("Papi Jump");

        Scene scene = new Scene(root, W, H, Color.BLACK);

        scene.setOnKeyPressed(event -> {
            if (!activeKeys.contains(event.getCode().toString()))
                activeKeys.add(event.getCode().toString());

            switch (event.getCode()) {
                case A:
                case LEFT:
                    papi.xvel = -papi.xspeed;
                    break;

                case D:
                case RIGHT:
                    papi.xvel = papi.xspeed;
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            activeKeys.remove(event.getCode().toString());
            switch (event.getCode()) {
                case A:
                case LEFT:
                case D:
                case RIGHT:
                    if (activeKeys.isEmpty())
                        papi.xvel = 0;
                    break;
            }
        });

        stage.setScene(scene);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) throws java.util.ConcurrentModificationException {
                papi.move();

                for (Platform p : platforms) { // Hit detection
                    if (p.getBoundsInParent().intersects(papi.sprite.getBoundsInParent()) && papi.yvel > 0 && root.getChildren().contains(p) && papi.sprite.getCenterY() < p.getY()) { // Hit detection
                        papi.jump(p.jbonus);
                        if (p.falls)
                            root.getChildren().remove(p);
                    }

                    if (papi.sprite.getCenterY() == (H / 2) - 100) { // Screen scrolling, all objects move down
                        p.setCenterY(p.getCenterY() - papi.yvel);
                        papi.score ++;
                    }

                    p.move();
                }

                if (platforms.get(0).getY() > H + 10) { // Garbage collection
                    root.getChildren().remove(platforms.get(0));
                    platforms.remove(platforms.get(0));
                }


                double jheight = Math.pow(platforms.get(platforms.size() - 1).jbonus, 2) / (papi.yacc * 2);
                if (platforms.get(platforms.size() - 1).getCenterY() > -jheight) { // Platform generation
                    Platform p = new Platform(getRand(0, W), getRand(platforms.get(platforms.size() - 1).getCenterY() - jheight + 50, platforms.get(platforms.size() - 1).getCenterY() - 100));
                    platforms.add(p);
                    root.getChildren().add(p);
                }

                scoreboard.setText("Score: " + ((Integer)papi.score).toString());


            }
        };

        timer.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
