import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tetris extends Application {
    private Text scoreboard;
    private List<List<Block>> board;
    private Tetronimo piece;
    private int score = 0;
    private enum Direction {DOWN, LEFT, RIGHT}

    void clear(int rowIndex) { // Clears a row and moves all settled pieces above it down
        score += 1000;

        for (int x = 0; x <= 9; x++) {
            board.get(x).get(rowIndex).setFill(Color.TRANSPARENT);
            board.get(x).get(rowIndex).settled = false;
        }

        /*while (rowIndex >= 0) {
            for (int x = 0; x <= 9; x++) {
                if (board.get(x).get(rowIndex).settled) {
                    board.get(x).get(rowIndex).fall();
                }
            }
            rowIndex--;
        }*/
        for (int x = 0; x <= 9; x++) {
            for (int y = rowIndex - 1; y >= 0; y--) {
                if (board.get(x).get(y).settled) {
                    int yCoord = board.get(x).get(y).getFall();
                    board.get(x).get(yCoord).setFill(board.get(x).get(y).getFill());
                    board.get(x).get(yCoord).settled = true;
                    board.get(x).get(y).setFill(Color.TRANSPARENT);
                    board.get(x).get(y).settled = false;
                }
            }
        }
    }

    void checkRows() { // Scans the board bottom up to see if any rows are full
        int i = board.get(0).size() - 1;
        while (i >= 0) {
            boolean full = true;

            for (int x = 0; x <= 9; x++) {
                Block b = board.get(x).get(i);
                if (!b.settled) {
                    i--;
                    full = false;
                    break;
                }
            }

            if (full) {
                clear(i);
                break;
            }
        }
    }

    private enum pType {
        I, O, T, J, L, S, Z;

        private static pType getRand() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    class Location {
        int x, y;
        Location(int x, int y) {this.x = x; this.y = y; }
        boolean valid() {
            return (this.x >= 0 && this.x <= 9 && this.y >= 0 && this .y <= 23);
        }
    }

    class Block extends Rectangle {
        Location location;
        boolean settled;

        Block(Location l) {
            this.setViewOrder(2);
            this.settled = false;
            this.location = l;

            this.setFill(Color.TRANSPARENT);
            this.setStrokeType(StrokeType.INSIDE);
            this.setStroke(Color.WHITE);
            this.setStrokeWidth(2.5);
            this.setWidth(50);
            this.setHeight(50);
            this.setX(l.x * 50);
            this.setY(l.y * 50);
        }

        int getFall() { // Return the minimum Y coordinate the piece can "fall" to
            int dy = 1;
            for (dy = 1; dy <= 22; dy++) {
                if (this.location.y + dy > 23)
                    break;

                else if (board.get(this.location.x).get(this.location.y + dy).settled || this.location.y + dy == 24) { // Falling position found
                    break;
                }
            }
            return this.location.y + dy - 1;
        }
    }

    class Tetronimo {
        List<Location> bLocs; // List of locations of the blocks making up the Tetronimo
        Location axis;
        Color color;
        Tetronimo(){ // 4, 4
            pType type = pType.getRand();

            bLocs = new ArrayList<>();

            switch(type) {
                case I:
                    bLocs.add(new Location(2, 4)); // 0
                    bLocs.add(new Location(3, 4)); // 1
                    bLocs.add(new Location(4, 4)); // 2
                    bLocs.add(new Location(5, 4)); // 3
                    this.color = Color.CYAN;
                    break;

                case O:
                    bLocs.add(new Location(4, 5)); // 0
                    bLocs.add(new Location(5, 5)); // 1
                    bLocs.add(new Location(4, 4)); // 2
                    bLocs.add(new Location(5, 4)); // 3
                    this.color = Color.YELLOW;
                    break;

                case T:
                    bLocs.add(new Location(4, 5)); // 0
                    bLocs.add(new Location(3, 4)); // 1
                    bLocs.add(new Location(4, 4)); // 2
                    bLocs.add(new Location(5, 4)); // 3
                    this.color = Color.MAGENTA;
                    break;

                case J:
                    bLocs.add(new Location(5, 5)); // 0
                    bLocs.add(new Location(5, 4)); // 1
                    bLocs.add(new Location(4, 4)); // 2
                    bLocs.add(new Location(3, 4)); // 3
                    this.color = Color.BLUE;
                    break;

                case L:
                    bLocs.add(new Location(3, 4)); // 0
                    bLocs.add(new Location(3, 5)); // 1
                    bLocs.add(new Location(4, 4)); // 2
                    bLocs.add(new Location(5, 4)); // 3
                    this.color = Color.ORANGE;
                    break;

                case S:
                    bLocs.add(new Location(3, 5)); // 0
                    bLocs.add(new Location(4, 5)); // 1
                    bLocs.add(new Location(4, 4)); // 2
                    bLocs.add(new Location(5, 4)); // 3
                    this.color = Color.GREEN;
                    break;

                case Z:
                    bLocs.add(new Location(5, 5)); // 0
                    bLocs.add(new Location(4, 5)); // 1
                    bLocs.add(new Location(4, 4)); // 2
                    bLocs.add(new Location(3, 4)); // 3
                    this.color = Color.RED;
                    break;

            }
            this.axis = this.bLocs.get(2);
            // Check list of blocks, if any are filled, game over

            if (this.check(bLocs)) {
                for(Location l : bLocs) {
                    board.get(l.x).get(l.y).setFill(this.color);
                }
            }
        }

        void control(Direction d){ // Move the entire piece in a given direction
            List<Location> toMove = new ArrayList<>();
            int dx = 0, dy = 0;
            switch(d) {
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;
                case DOWN:
                    dy = 1;
                    break;
            }

            for (Location l : this.bLocs) { // Find
                toMove.add(new Location(l.x + dx, l.y + dy));
            }

            this.move(toMove);
        }

        void rotate() {
            List<Location> toMove = new ArrayList<>();
            for (Location l : this.bLocs) {// Figure out where blocks will move to rotate
                int dx = l.x - this.axis.x;
                int dy = l.y - this.axis.y;
                toMove.add(new Location(l.x - (dx + dy), l.y + (dx - dy)));
            }

            this.move(toMove);
        }

        boolean check(List<Location> toMove) { // Check if every block in a piece can successfully move into a valid, unfilled space
            for (Location l : toMove) {
                if (! l.valid() || board.get(l.x).get(l.y).settled) {
                    return false;
                }
            }
            return true;
        }

        void move(List<Location> toMove) {
            for (List<Block> row : board) { // Remove highlight from board every time the piece moves
                for (Block b : row)
                    b.setStroke(Color.WHITE);
            }

            if (this.check(toMove)) {
                for (Location l : this.bLocs) {
                    board.get(l.x).get(l.y).setFill(Color.TRANSPARENT);
                }
                for (Location m : toMove) {
                    board.get(m.x).get(m.y).setFill(this.color);
                    board.get(m.x).get(m.y).setStroke(Color.WHITE);
                    this.bLocs.set(toMove.indexOf(m), m);
                }
                this.axis = this.bLocs.get(2);
            }

            this.highlight();
        }

        void highlight() { // Highlights where the piece will end up
            int yCoord = 23;
            for (int dy = 1; dy < 24; dy++) {
                for (Location l : this.bLocs) { // Keep shifting piece down until one part hits a settled piece or the bottom
                    if ((l.y + dy <= 23 && board.get(l.x).get(l.y + dy).settled) || l.y + dy == 24) {
                        // We've hit something captain...
                        for (Location m : this.bLocs) {
                            if (m.y + dy <= 23)
                                board.get(m.x).get(m.y + dy - 1).setStroke(this.color);
                            else
                                board.get(m.x).get(23).setStroke(this.color);
                        }
                        return;
                    }
                }
            }
        }
    }


    @Override
    public void start(Stage stage) throws Exception{
        Rectangle cover = new Rectangle(0, 0, 500, 250);
        cover.setFill(Color.BLUE);
        cover.setViewOrder(1);

        scoreboard = new Text();
        scoreboard.setFont(new Font("tlwg typewriter", 20));
        scoreboard.setViewOrder(0);
        scoreboard.setX(150);
        scoreboard.setY(100);
        scoreboard.setFill(Color.WHITE);

        Group root = new Group(cover, scoreboard);

        board = new ArrayList<List<Block>>();
        for (int x = 0; x < 10; x++) {
            board.add(new ArrayList<Block>());
            for (int y = 0; y < 24; y++) {
                board.get(x).add(new Block(new Location(x, y)));
            }
            root.getChildren().addAll(board.get(x));
        }

        piece = new Tetronimo();

        stage.setTitle("Tetris");

        Scene scene = new Scene(root, 300, 275, Color.BLACK);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                case W:     piece.rotate(); break;

                case DOWN:
                case S:     piece.control(Direction.DOWN); break;

                case LEFT:
                case A:     piece.control(Direction.LEFT); break;

                case RIGHT:
                case D:     piece.control(Direction.RIGHT); break;
            }
        });

        stage.setScene(scene);
        stage.show();


        Thread check = new Thread() {
            public void run() {
                while (true) {
                    checkRows();
                }
            }
        };
        check.setDaemon(true);
        check.start();


        Thread control = new Thread() {
            int t = 1000;
            public void run() {
                while (true) {
                    piece.control(Direction.DOWN);
                    for (int x = 0; x <= 9; x++) {
                        if (board.get(x).get(4).settled) {
                            scoreboard.setFont(new Font("tlwg typewriter", 30));
                            scoreboard.setText("GAME OVER\nFinal Score: " + score);
                            return; // Game over
                        }
                    }
                    scoreboard.setText("Score: " + score);

                    try {
                        Thread.sleep(t); // Will speed up as game progresses
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (Location l : piece.bLocs) {
                        if ((l.y <= 22 && board.get(l.x).get(l.y + 1).settled) || l.y == 23) {

                            for (Location m : piece.bLocs) { // Set piece as settled
                                board.get(m.x).get(m.y).settled = true;
                            }

                            piece = new Tetronimo();
                            break;
                        }
                    }
                }
            }
        };

        control.setDaemon(true);
        control.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
