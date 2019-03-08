import java.util.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.AnimationTimer;
import javafx.scene.*;
import javafx.scene.text.*;
import java.util.Arrays;



public class Snake extends Application {

    private static final double W = 1010, H = 1010;
    private Border[] gameArea;
    private Segment[][] grid;
    private Player player;
    private Scoreboard scoreboard;
    private enum SegType {HEAD, BODY, TAIL, SPACE, APPLE}
    private enum Direction { LEFT, RIGHT, UP, DOWN }

    private void reset() { // Set space, draw body
        for (int i = 0; i < 100; i ++){
            for (int j = 0; j < 100; j++){
                grid[i][j].setType(SegType.SPACE);
            }
        }
        grid[52][50].setType(SegType.HEAD);
        grid[51][50].setType(SegType.BODY);
        grid[51][50].bodyNum = 0;
        grid[50][50].setType(SegType.BODY);
        grid[50][50].bodyNum = 1;
        grid[49][50].setType(SegType.TAIL);

        player.length = 4;
        player.score = 0;
        player.direction = null;

        scoreboard.setText("Score: 0\nLength: 4");
        appleSpawn();
    }

    private List<Coords> getLoc(SegType type){
        List<Coords> loc = new ArrayList<>();
        for (Segment[] row : grid){
            for (Segment item : row){
                if (item.type == type) {
                    loc.add(new Coords(Arrays.asList(grid).indexOf(row), Arrays.asList(row).indexOf(item)));
                }
            }
        }
        return loc;
    }

    private void appleSpawn(){
        int x = (int)(Math.random() * 100);
        int y = (int)(Math.random() * 100);
        while (true) {
            if (grid[x][y].type == SegType.SPACE) {
                grid[x][y].setType(SegType.APPLE);
                break;
            }
            x++;
            y++;
        }
    }

    class Coords {
        int x, y;
        Coords(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    class Player {
        Direction direction;
        int length, score;
        Player(){
            this.length = 4;
            this.score = 0;
        }

        void move() {
            List<Coords> head = getLoc(SegType.HEAD);
            List<Coords> tail = getLoc(SegType.TAIL);
            List<Coords> body = getLoc(SegType.BODY);
            int x = head.get(0).x;
            int y = head.get(0).y;

            if (this.direction == null)
                return;

            switch (this.direction) {
                case LEFT:  x -= 1; break;
                case RIGHT: x += 1; break;
                case UP:    y -= 1; break;
                case DOWN:  y += 1; break;
            }

            if (x < 0 || x > 99 || y < 0 || y > 99) {
                //Hit a wall, game over
                reset();
                return;
            }

            switch (grid[x][y].type){
                case APPLE:
                    this.length++;
                    this.score += 50;
                    appleSpawn();
                    break;
                case BODY:
                    if (grid[x][y].bodyNum != 0)
                        reset();
                    return; // Game over
                case TAIL:  reset(); return; // Game over
                case SPACE: // Can move
                    grid[tail.get(0).x][tail.get(0).y].setType(SegType.SPACE); // Tail becomes space
                    for (Coords bodyseg : body) {
                        if (grid[bodyseg.x][bodyseg.y].bodyNum == player.length - 3) {
                            grid[bodyseg.x][bodyseg.y].setType(SegType.TAIL); // Body becomes tail
                            grid[bodyseg.x][bodyseg.y].bodyNum = -1;
                        }
                    }
                    break;
            }
            grid[head.get(0).x][head.get(0).y].setType(SegType.BODY); // Head becomes body
            grid[head.get(0).x][head.get(0).y].bodyNum = 0;
            grid[x][y].setType(SegType.HEAD); // Movespace becomes head

            for (Coords bodyseg : body){
                    grid[bodyseg.x][bodyseg.y].bodyNum += 1;

            }
        }
    }

    class Segment extends Rectangle {
        private SegType type;
        private int bodyNum;
        Segment(int xindex, int yindex){
            this.setWidth(10);
            this.setHeight(10);
            this.setFill(Color.BLACK);
            this.type = SegType.SPACE;
            this.setX(xindex * 10 + 10);
            this.setY(yindex * 10 + 10);
            this.bodyNum = -1;
        }

        void setType(SegType type){
            this.type = type;
            switch (type){
                case SPACE: this.setFill(Color.BLACK);  break;
                case HEAD:  this.setFill(Color.BLUE);   break;
                case BODY:  this.setFill(Color.BLUE); break;
                case TAIL:  this.setFill(Color.BLUE);  break;
                case APPLE: this.setFill(Color.GREEN);    break;
            }
        }
    }

    class Border extends Rectangle {
        Border(double x, double y, double width, double height, Color color){
            this.setX(x);
            this.setY(y);
            this.setHeight(height);
            this.setWidth(width);
            this.setFill(color);
        }
    }

    class Scoreboard extends Text {
        Scoreboard(){
            this.setFill(Color.WHITE);
            this.setX(10);
            this.setY(H + 40);
            this.setFont(new Font("Tlwg Typewriter", 20.0f));
        }
    }



    @Override
    public void start(Stage stage) throws Exception{
        scoreboard = new Scoreboard();

        player = new Player();

        Group root = new Group(scoreboard);

        grid = new Segment[100][100];
        for (int i = 0; i < 100; i ++){
            for (int j = 0; j < 100; j++){
                grid[i][j] = new Segment(i, j);
                root.getChildren().add(grid[i][j]);
            }
        }

        gameArea = new Border[] {
                new Border(0, 0, 10, H, Color.WHITE), // Left wall
                new Border(0, 0, W, 10, Color.WHITE), // Ceiling
                new Border(0, H, W, 10, Color.WHITE), // Floor
                new Border(W - 10, 0, 10, H, Color.WHITE), // Right wall
        };
        for (Border wall: gameArea)
            root.getChildren().add(wall);

        reset();

        Scene scene = new Scene(root, W, H, Color.BLACK);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    if (player.direction != Direction.DOWN)
                        player.direction = Direction.UP;
                    break;
                case A:
                    if (player.direction != Direction.RIGHT)
                        player.direction = Direction.LEFT;
                    break;
                case S:
                    if (player.direction != Direction.UP)
                        player.direction = Direction.DOWN;
                    break;
                case D:
                    if (player.direction != Direction.LEFT)
                        player.direction = Direction.RIGHT;
                    break;
                case UP:
                    if (player.direction != Direction.DOWN)
                        player.direction = Direction.UP;
                    break;
                case LEFT:
                    if (player.direction != Direction.RIGHT)
                        player.direction = Direction.LEFT;
                    break;
                case DOWN:
                    if (player.direction != Direction.UP)
                        player.direction = Direction.DOWN;
                    break;
                case RIGHT:
                    if (player.direction != Direction.LEFT)
                        player.direction = Direction.RIGHT;
                    break;
            }
        });

        stage.setScene(scene);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {

            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 28_000_000) {
                    scoreboard.setText("Score: " + player.score + "\nLength: " + player.length);
                    player.move();
                    lastUpdate = now ;
                }
            }
        };

        timer.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
