package Crossword;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.*;

public class Crossword extends Application {
    private static double W, H;
    private static int numRows, numCols;
    private static int numHints = 1000;
    private String key = null;
    private Cursor cursor;
    private List<List<Box>> board;
    private Group root;

    private enum reqMethod {GET, POST, DELETE, HEAD}
    private enum gameStage {getDate, play, done}
    private gameStage currStage = gameStage.getDate;

    class Box extends Rectangle {
        int row, col;
        char real, given;
        boolean filled; // As in, black box

        Label label, character;

        Box(int row, int col) {
            this.filled = true;
            this.setFill(Color.BLACK);
            this.init(row, col);
        }

        Box(int row, int col, char real) {
            this.filled = false;

            this.setFill(Color.WHITE);
            this.setStroke(Color.BLACK);
            this.setStrokeWidth(1);
            this.setStrokeType(StrokeType.INSIDE);

            this.real = real;

            this.label = new Label(10, Color.BLACK);

            this.character = new Label(20, Color.BLACK);

            root.getChildren().add(this.label);
            root.getChildren().add(this.character);

            this.init(row, col);
        }

        void init(int row, int col) { // Apply default parameters to filled and unfilled boxes
            double size = (H / 2) / numRows;
            this.row = row;
            this.col = col;
            this.setWidth(size);
            this.setHeight(size);
            this.setX(W / 2 - (numRows / 2 - col)*size);
            this.setY(row * size + 50);
            this.setViewOrder(1);

            root.getChildren().add(this);
        }

        void setChar(char c) {
            this.given = c;
            this.character.setText(Character.toString(c));
            this.character.setCenterLocation(new Location(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2));
        }

        void setLabel(int i) {
            this.label.setText(Integer.toString(i));
            this.label.setLocation(new Location(this.getX() + 5, this.getY() + this.label.getBoundsInParent().getHeight()));
        }

    }

    class Cursor {
        Location location;

        Cursor(Location loc) {
            this.location = new Location(loc.x, loc.y);
            board.get((int)loc.y).get((int)loc.x).setStroke(Color.GOLD);
        }

        void move(Location delta) {
            if (this.location.x + delta.x >= 0
                    && this.location.x + delta.x < numCols
                    && this.location.y + delta.y >= 0
                    && this.location.y + delta.y < numRows
            ) {
                board.get((int) this.location.y).get((int) this.location.x).setStroke(Color.BLACK);
                this.location = new Location((int) this.location.x + delta.x, (int) this.location.y + delta.y);
                board.get((int) this.location.y).get((int) this.location.x).setStroke(Color.GOLD);
            }
        }
    }

    class Location {
        double x, y;
        Location(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    class Parameters {
        reqMethod method;
        private String URL, USER_AGENT;

        Parameters() {
            this.USER_AGENT = "Mozilla/5.0";
        }
    }

    class Label extends Text {
        int size;

        Label(int size, Color color) {
            this.size = size;
            this.setFont(new Font("twlg typewriter", size));
            this.setFill(color);
            this.setViewOrder(0);
        }

        void setLocation(Location loc) {
            this.setX(loc.x);
            this.setY(loc.y);
        }

        void setCenterLocation(Location loc) {
            this.setX(loc.x - this.getBoundsInParent().getWidth() / 2);
            this.setY(loc.y + this.getBoundsInParent().getHeight() / 2);
        }
    }

    private void parse(String date) throws IOException { // Get request parameters, build request, pass to send
        Parameters request = new Parameters();
        request.method = reqMethod.GET;
        request.URL = "https://www.xwordinfo.com/JSON/Data.aspx?date=" + date;

        send(request);
    }

    private void send(Parameters request) throws IOException { // Take request parameters, send request, pass reponse to build
        URL url = new URL(request.URL);
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

        connection.setRequestMethod(request.method.toString());
        connection.setRequestProperty("User-Agent", request.USER_AGENT);
        connection.setRequestProperty("Referer", "https://www.xwordinfo.com/JSON/Sample1");
        connection.connect();

        int status = connection.getResponseCode();
        if (status == HttpsURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String JSONString = br.readLine();
            br.close();

            System.out.println(JSONString);
            build(new JsonParser().parse(JSONString).getAsJsonObject());
        }
    }

    private void build(JsonObject JSON) { // Take response, build crossword board
        numRows = JSON.get("size").getAsJsonObject().get("rows").getAsInt();
        numCols = JSON.get("size").getAsJsonObject().get("cols").getAsInt();

        root.getChildren().clear();

        // Title
        Label title = new Label(25, Color.BLACK);
        title.setText(JSON.get("title").getAsString());
        title.setCenterLocation(new Location(W / 2, H / 2 + 100));

        // Across Clues
        Label aClues = new Label(50, Color.BLACK);
        aClues.setText("Across:\n");
        for (JsonElement aclues : JSON.get("clues").getAsJsonObject().get("across").getAsJsonArray()) {
            aClues.setText(aClues.getText() + aclues.getAsString().replaceAll("&quot;", "\"") + "\n");
        }
        while (aClues.getBoundsInParent().getHeight() > H * 0.75 || aClues.getBoundsInParent().getWidth() > W / 4) {
            aClues.setFont(new Font("twlg typewriter", aClues.size--));
        }
        aClues.setLocation(new Location(50, 100));

        // Down Clues
        Label dClues = new Label(50, Color.BLACK);
        dClues.setText("Down:\n");
        for (JsonElement dclues : JSON.get("clues").getAsJsonObject().get("down").getAsJsonArray()) {
            dClues.setText(dClues.getText() + dclues.getAsString().replaceAll("&quot;", "\"") + "\n");
        }
        while (dClues.getBoundsInParent().getHeight() > H * 0.75 || dClues.getBoundsInParent().getWidth() > W / 4) {
            dClues.setFont(new Font("twlg typewriter", dClues.size--));
        }
        dClues.setLocation(new Location(W - dClues.getBoundsInParent().getWidth() - 50, 100));

        root.getChildren().addAll(aClues, dClues, title);

        board = new ArrayList<>();
        int index = 0;
        for (int x = 0; x < numRows; x++) { // Building board; Assign true characters, fill boxes, etc.
            board.add(new ArrayList<>());
            for (int y = 0; y < numCols; y++) {
                char c = JSON.get("grid").getAsJsonArray().get(index).getAsCharacter();
                if (c == '.') {
                    board.get(x).add(new Box(x, y));
                } else {
                    board.get(x).add(new Box(x, y, c));
                }

                index++;
            }
        }
        int label = 1;
        for (List<Box> row : board) { // Assign labels
            for (Box box : row) {
                if (! box.filled) { // we are assigning a label!
                    if (box.row == 0 || box.col == 0 || board.get(box.row - 1).get(box.col).filled || board.get(box.row).get(box.col - 1).filled) { // IF LEFT IS INVALID OR FILLED
                        box.setLabel(label++);
                    }
                }
            }
        }

        cursor = new Cursor(new Location(0, 0));
        currStage = gameStage.play;
    }

    private void checkWin() {
        for (List<Box> row : board) {
            for (Box box : row) {
                if (box.given != box.real)
                    return;
            }
        }
        currStage = gameStage.done;
    }

    @Override
    public void start(Stage stage) throws Exception{
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        W = screen.getWidth();
        H = screen.getHeight();

        Label welcomeText = new Label(50, Color.BLACK);
        welcomeText.setText("1. Random puzzle\n2. Daily puzzle");
        welcomeText.setCenterLocation(new Location(W / 2, H / 2));

        root = new Group(welcomeText);

        Scene scene = new Scene(root, W, H, Color.DARKCYAN);

        stage.setTitle("New York Times Crossword");
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(event -> key = event.getCode().toString());

        AnimationTimer control = new AnimationTimer() {
            public void handle(long now) {
                    switch (currStage) {
                        case getDate:
                            try {
                                Thread.sleep(125);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            welcomeText.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                            if (key == "DIGIT1") {
                                try {
                                    parse("random");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (key == "DIGIT2") {
                                try {
                                    parse("current");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;

                        case play:
                            if (key == null)
                                break;
                            switch(key) {
                                case "LEFT": // Move cursor left
                                    cursor.move(new Location(-1, 0));
                                    break;
                                case "RIGHT": // Move cursor right
                                    cursor.move(new Location(1, 0));
                                    break;
                                case "UP":
                                    cursor.move(new Location(0, -1));
                                    break;
                                case "DOWN":
                                    cursor.move(new Location(0, 1));
                                    break;
                                case "SPACE": // Use hint
                                    if (numHints > 0) {
                                        board.get((int)cursor.location.y).get((int)cursor.location.x).setChar(board.get((int)cursor.location.y).get((int)cursor.location.x).real);
                                        numHints--;
                                    }
                                    break;
                                case "ESCAPE": // Highlight correct/incorrect letters
                                    for (List<Box> row : board) {
                                        for (Box box : row) {
                                            if (! box.filled && box.given != 0) {
                                                if (box.given == box.real)
                                                    box.setFill(Color.GREEN);
                                                else
                                                    box.setFill(Color.RED);
                                            }
                                        }
                                    }
                                    break;
                                default: // Check if letter, then fill
                                    if (key.length() == 1)
                                        board.get((int)cursor.location.y).get((int)cursor.location.x).setChar(key.charAt(0));
                                    break;
                            }
                            checkWin();
                            break;

                        case done:
                            root.getChildren().clear();
                            welcomeText.setText("You win! Press any key to exit");

                            root.getChildren().add(welcomeText);

                            if (key == null) {
                                try {
                                    Thread.sleep(125);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                welcomeText.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                            }
                            else
                                Platform.exit();

                            break;
                    }

                    key = null;
                }
        };
        control.start();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
