import java.util.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.text.*;

public class Chess extends Application {
    private static final double W = 800, H = 800;
    private Board[][] board;
    private Border border;
    private Scoreboard scoreboard;

    enum Pieces {Pawn, Rook, Knight, Bishop, Queen, King}
    enum Team {WHITE, BLACK}

    Team turn;
    String baseURL = "https://raw.githubusercontent.com/patosai/chess/master/Sprites/";

    boolean valid(int x, int y) {
        return (x >= 0 && x <= 7 && y >= 0 && y <= 7);
    }

    Coords getHover(){
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                if (board[x][y].hovered)
                    return new Coords(x, y);
            }
        }
        return null;
    }

    Coords getSelected(){
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                if (board[x][y].selected)
                    return new Coords(x, y);
            }
        }
        return null;
    }


    class Coords {
        Integer x, y;
        Coords(int x, int y){
                this.x = x;
                this.y = y;
        }
    }

    class Piece {
        public Image sprite;
        public ImageView spriteview;
        public Team team;
        public Coords location;
        public List<Coords> moves;
        public Pieces type;

        Piece(){this.spriteview = new ImageView();}

        Piece(Pieces type, Coords location){
            this.type = type;
            this.moves = new ArrayList<>();

            this.location = location;

            if (this.location.y == 0 || this.location.y == 1)
                this.team = Team.BLACK;
            else
                this.team = Team.WHITE;

            this.sprite = new Image(baseURL + this.team.toString().toLowerCase() + this.type + ".png");
            this.spriteview = new ImageView(this.sprite);
            this.spriteview.setX(this.location.x * 100 + 30);
            this.spriteview.setY(this.location.y * 100 + 30);
            this.spriteview.toFront();
        }

        List<Coords> getMoves() { // Movement logic
            List<Coords> legalMoves = new ArrayList<>();
            int x = this.location.x;
            int y = this.location.y;

            switch(this.type) {
                case Pawn: // Forward 1, forward 2, diag left, diag right (if capturing) DONE
                    int mult = 1;
                    if (this.team == Team.WHITE)
                        mult = -1;

                    if (valid(x, y + mult) && board[x][y + mult].piece.type == null) // Forward 1
                        legalMoves.add(new Coords(x, y + mult));

                    if (valid(x, y + (2 * mult)) && (y == 6 || y == 1) && board[x][y + (2 * mult)].piece.type == null)
                        legalMoves.add(new Coords(x, y + (2 * mult)));

                    if (valid(x + 1, y + mult) && board[x + 1][y + mult].piece.team != null && board[x + 1][y + mult].piece.team != this.team)
                        legalMoves.add(new Coords(x + 1, y + mult));
                    if (valid(x - 1, y + mult) && board[x - 1][y + mult].piece.team != null && board[x - 1][y + mult].piece.team != this.team)
                        legalMoves.add(new Coords(x - 1, y + mult));

                    break;

                case Rook: // Up down left right any
                    for (int u = 1; u < 7; u++){
                        if (valid(x, y - u) && board[x][y - u].piece.team == this.team)
                            break;
                        else if (valid(x, y - u))
                            legalMoves.add(new Coords(x, y - u));
                    }
                    for (int d = 1; d < 7; d++){
                        if (valid(x, y + d) && board[x][y + d].piece.team == this.team)
                            break;
                        else if (valid(x, y + d))
                            legalMoves.add(new Coords(x, y + d));
                    }
                    for (int l = 1; l < 7; l++){
                        if (valid(x - l, y) && board[x - l][y].piece.team == this.team)
                            break;
                        else if (valid(x - l, y))
                            legalMoves.add(new Coords(x - l, y));
                    }
                    for (int r = 1; r < 7; r++){
                        if (valid(x + r, y) && board[x + r][y].piece.team == this.team)
                            break;
                        else if (valid(x + r, y))
                            legalMoves.add(new Coords(x + r, y));
                    }

                    break;

                case Knight: // DONE
                    if (valid(x - 1, y + 2) && board[x - 1][y + 2].piece.team != this.team) // Down left
                        legalMoves.add(new Coords(x - 1, y + 2));
                    if (valid(x + 1, y + 2) && board[x + 1][y + 2].piece.team != this.team) // Down right
                        legalMoves.add(new Coords(x + 1, y + 2));
                    if (valid(x - 1, y - 2) && board[x - 1][y - 2].piece.team != this.team) // Up left
                        legalMoves.add(new Coords(x - 1, y - 2));
                    if (valid(x + 1, y - 2) && board[x + 1][y - 2].piece.team != this.team) // Up right
                        legalMoves.add(new Coords(x + 1, y - 2));
                    if (valid(x - 2, y - 1) && board[x - 2][y - 1].piece.team != this.team) // Left up
                        legalMoves.add(new Coords(x - 2, y - 1));
                    if (valid(x - 2, y + 1) && board[x - 2][y + 1].piece.team != this.team) // Left down
                        legalMoves.add(new Coords(x - 2, y + 1));
                    if (valid(x + 2, y - 1) && board[x + 2][y - 1].piece.team != this.team) // Right up
                        legalMoves.add(new Coords(x + 2, y - 1));
                    if (valid(x + 2, y + 1) && board[x + 2][y + 1].piece.team != this.team) // Right down
                        legalMoves.add(new Coords(x + 2, y + 1));
                    break;

                case Bishop: // DONE
                    for (int ur = 1; ur < 7; ur++){
                        if (valid(x + ur, y - ur) && board[x + ur][y - ur].piece.team == this.team)
                            break;
                        else if (valid(x + ur, y - ur))
                            legalMoves.add(new Coords(x + ur, y - ur));
                    }
                    for (int ul = 1; ul < 7; ul++){
                        if (valid(x - ul, y - ul) && board[x - ul][y - ul].piece.team == this.team)
                            break;
                        else if (valid(x - ul, y - ul))
                            legalMoves.add(new Coords(x - ul, y - ul));
                    }
                    for (int dr = 1; dr < 7; dr++){
                        if (valid(x + dr, y + dr) && board[x + dr][y + dr].piece.team == this.team)
                            break;
                        else if (valid(x + dr, y + dr))
                            legalMoves.add(new Coords(x + dr, y + dr));
                    }

                    for (int dl = 1; dl < 7; dl++){
                        if (valid(x - dl, y + dl) && board[x - dl][y + dl].piece.team == this.team)
                            break;
                        else if (valid(x - dl, y + dl))
                            legalMoves.add(new Coords(x - dl, y + dl));
                    }
                    break;
                case Queen: // DONE
                    for (int u = 1; u < 7; u++){
                        if (valid(x, y - u) && board[x][y - u].piece.team == this.team)
                            break;
                        else if (valid(x, y - u))
                            legalMoves.add(new Coords(x, y - u));
                    }
                    for (int d = 1; d < 7; d++){
                        if (valid(x, y + d) && board[x][y + d].piece.team == this.team)
                            break;
                        else if (valid(x, y + d))
                            legalMoves.add(new Coords(x, y + d));
                    }
                    for (int l = 1; l < 7; l++){
                        if (valid(x - l, y) && board[x - l][y].piece.team == this.team)
                            break;
                        else if (valid(x - l, y))
                            legalMoves.add(new Coords(x - l, y));
                    }
                    for (int r = 1; r < 7; r++){
                        if (valid(x + r, y) && board[x + r][y].piece.team == this.team)
                            break;
                        else if (valid(x + r, y))
                            legalMoves.add(new Coords(x + r, y));
                    }
                    for (int ur = 1; ur < 7; ur++){
                        if (valid(x + ur, y - ur) && board[x + ur][y - ur].piece.team == this.team)
                            break;
                        else if (valid(x + ur, y - ur))
                            legalMoves.add(new Coords(x + ur, y - ur));
                    }
                    for (int ul = 1; ul < 7; ul++){
                        if (valid(x - ul, y - ul) && board[x - ul][y - ul].piece.team == this.team)
                            break;
                        else if (valid(x - ul, y - ul))
                            legalMoves.add(new Coords(x - ul, y - ul));
                    }
                    for (int dr = 1; dr < 7; dr++){
                        if (valid(x + dr, y + dr) && board[x + dr][y + dr].piece.team == this.team)
                            break;
                        else if (valid(x + dr, y + dr))
                            legalMoves.add(new Coords(x + dr, y + dr));
                    }

                    for (int dl = 1; dl < 7; dl++){
                        if (valid(x - dl, y + dl) && board[x - dl][y + dl].piece.team == this.team)
                            break;
                        else if (valid(x - dl, y + dl))
                            legalMoves.add(new Coords(x - dl, y + dl));
                    }

                    break;
                case King:
                    if (valid(x, y - 1) && board[x][y - 1].piece.team != this.team) // Up
                        legalMoves.add(new Coords(x, y - 1));
                    if (valid(x + 1, y - 1) && board[x + 1][y - 1].piece.team != this.team) // Up right
                        legalMoves.add(new Coords(x + 1, y - 1));
                    if (valid(x + 1, y) && board[x + 1][y].piece.team != this.team) // Right
                        legalMoves.add(new Coords(x + 1, y));
                    if (valid(x + 1, y + 1) && board[x + 1][y + 1].piece.team != this.team) // Down right
                        legalMoves.add(new Coords(x + 1, y + 1));
                    if (valid(x, y + 1) && board[x][y + 1].piece.team != this.team) // Down
                        legalMoves.add(new Coords(x, y + 1));
                    if (valid(x - 1, y + 1) && board[x - 1][y + 1].piece.team != this.team) // Down left
                        legalMoves.add(new Coords(x - 1, y + 1));
                    if (valid(x - 1, y) && board[x - 1][y].piece.team != this.team) // Left
                        legalMoves.add(new Coords(x - 1, y));
                    if (valid(x - 1, y - 1) && board[x - 1][y - 1].piece.team != this.team) // Up left
                        legalMoves.add(new Coords(x - 1, y - 1));
                    break;
            }


            for (Coords move: this.moves) {
                int dx = move.x + this.location.x;
                int dy = move.y + this.location.y;

                if (dx >= 0 && dx <= 7 && dy >= 0 && dy <= 7 && this.team != board[dx][dy].piece.team) // Invert, .add
                    legalMoves.add(new Coords(dx, dy));
            }

            return legalMoves;
        }
    }


    class Scoreboard extends Text {
        Scoreboard(String text){
            this.setText(text);
            this.setFill(Color.WHITE);
            this.setX(W + 50);
            this.setY(H / 2);
        }
    }

    class Border extends Rectangle {
        Border(){
            this.setX(10);
            this.setY(10);
            this.setWidth(W);
            this.setHeight(H);
            this.setFill(Color.TRANSPARENT);
            this.setStroke(Color.GRAY);
            this.setStrokeWidth(20);
        }
    }

    class Board extends Rectangle {
        Piece piece;
        boolean selected;
        boolean hovered;

        Board(double x, double y, Color color){
            this.setFill(color);
            this.setX(x);
            this.setY(y);
            this.setWidth(100);
            this.setHeight(100);
            this.setStrokeWidth(5);
        }

        void hover(){
            for (Board[] row: board) { // Deselect any already selected spaces
                for (Board space : row){
                    if (space.hovered){
                        space.dehover();
                    }
                }
            }

            this.hovered = true;
            this.toFront();
            this.setStroke(Color.YELLOW);
            if (this.piece != null && this.piece.spriteview != null){
                this.piece.spriteview.toFront();
            }
        }

        void dehover() {
            this.hovered = false;
            this.setStroke(Color.TRANSPARENT);
        }

        void select() {
            for (Board[] row: board) { // Deselect any already selected spaces
                for (Board space : row){
                    if (space.selected){
                        space.deselect();
                    }
                }
            }

            this.selected = true;
            this.toFront();
            this.piece.spriteview.toFront();
            this.setStroke(Color.ORANGE);
        }

        void deselect() {
            this.selected = false;
            this.setStroke(Color.TRANSPARENT);
        }
    }

    @Override
    public void start(Stage stage) throws Exception{
        turn = Team.WHITE; // White goes first
        scoreboard = new Scoreboard(turn + " TO MOVE");

        border = new Border();
        Group root = new Group(border, scoreboard);

        board = new Board[8][8];
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                if ((x % 2 == 0 && y % 2 != 0) || (x % 2 != 0 && y % 2 == 0))
                    board[x][y] = new Board(x * 100 + 10, y * 100 + 10, Color.LIGHTGRAY);
                else
                    board[x][y] = new Board(x * 100 + 10, y * 100 + 10, Color.WHITE);

                if (y == 1 || y ==6)
                    board[x][y].piece = new Piece(Pieces.Pawn, new Coords(x, y));
                else if (y == 0 || y == 7) {
                    if (x == 0 || x ==7)
                        board[x][y].piece = new Piece(Pieces.Rook, new Coords(x, y));
                    else if (x == 1 || x == 6)
                        board[x][y].piece = new Piece(Pieces.Knight, new Coords(x, y));
                    else if (x == 2 || x == 5)
                        board[x][y].piece = new Piece(Pieces.Bishop, new Coords(x, y));
                    else if (x == 3)
                        board[x][y].piece = new Piece(Pieces.Queen, new Coords(x, y));
                    else
                        board[x][y].piece = new Piece(Pieces.King, new Coords(x, y));
                }
                else
                    board[x][y].piece = new Piece();

                root.getChildren().add(board[x][y]);
                root.getChildren().add(board[x][y].piece.spriteview);
            }
        }

        board[0][7].hover();

        Scene scene = new Scene(root, W, H, Color.BLACK);


        scene.setOnKeyPressed(event -> {
            int x = 0, y = 0;

            Coords hovLoc = getHover();
            Coords selLoc = getSelected();
            if (getHover() != null) {
                x = hovLoc.x;
                y = hovLoc.y;
            }
            else if (getSelected() != null) {
                x = selLoc.x;
                y = selLoc.y;
            }


            switch (event.getCode()) {
                case W:     y -= 1; break;
                case A:     x -= 1; break;
                case S:     y += 1; break;
                case D:     x += 1; break;
                case UP:    y -= 1; break;
                case LEFT:  x -= 1; break;
                case DOWN:  y += 1; break;
                case RIGHT: x += 1; break;
                case SPACE:
                    if (getSelected() == null && board[x][y].piece.team == turn) { // Select
                        board[x][y].dehover();
                        board[x][y].select();
                    }

                    else if (getSelected() != null){ // Move
                        List<Coords> moves = board[selLoc.x][selLoc.y].piece.getMoves();

                        for (Coords move : moves){
                            if (move.x.equals(hovLoc.x) && move.y.equals(hovLoc.y)){
                                board[hovLoc.x][hovLoc.y].piece = board[selLoc.x][selLoc.y].piece;
                                board[hovLoc.x][hovLoc.y].piece.location = new Coords(hovLoc.x, hovLoc.y);
                                board[hovLoc.x][hovLoc.y].piece.spriteview.setX(hovLoc.x * 100 + 30);
                                board[hovLoc.x][hovLoc.y].piece.spriteview.setY(hovLoc.y * 100 + 30);
                                board[hovLoc.x][hovLoc.y].piece.spriteview.toFront();
                                board[selLoc.x][selLoc.y].piece = new Piece();
                                board[selLoc.x][selLoc.y].deselect();


                                // Swap turns
                                if (turn == Team.WHITE) {
                                    turn = Team.BLACK;
                                    board[0][0].hover();
                                }
                                else {
                                    turn = Team.WHITE;
                                    board[0][7].hover();
                                }

                                scoreboard.setText(turn + " TO MOVE");
                                break;
                            }
                        }
                    }
                    return;
                case ESCAPE:
                    if (getSelected() != null) { // Deselect
                        board[selLoc.x][selLoc.y].deselect();
                        board[selLoc.x][selLoc.y].hover();
                    }
                    return;
            }
            if (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
                if (!board[x][y].selected)
                    board[x][y].hover();
            }
        });


        stage.setScene(scene);
        stage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
