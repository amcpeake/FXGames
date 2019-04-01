# FXGames
A series of simple games built using JavaFX

## Installing JavaFX
As the name implies, all of these games use JavaFX for graphics. JavaFX was previously bundled with Oracle's JDK 10 or earlier, however no version of OpenJDK includes JavaFX. 

That said, JavaFX is not required to run the fat JAR files as they include all required dependencies.
If for whatever reason you wish to run these games via the Java files, you will need to have JavaFX installed.
You can do so by following [these instructions](https://openjfx.io/openjfx-docs/#install-javafx).

## Compatibility
These games were written in JDK 11 and JavaFX 11. Backwards/forwards compatibility is unknown, so for best results they should be run with the aforementioned versions.

## Running a game
### Via JAR
To run a JAR file, simply use:

```java -jar <JAR FILE>```

i.e.

```java -jar Pong.jar```

### Via Java
First, compile the Java file:

```javac --module-path <PATH TO JAVAFX LIB FOLDER> --add-modules=javafx.controls,javafx.fxml <JAVA FILE>```

i.e. 

```javac --module-path ~/Downloads/javafx-sdk-11.0.2/lib --add-modules=javafx.controls,javafx.fxml Pong.java```

Then, run the class file:

```java --module-path <PATH TO JAVAFX LIB FOLDER> --add-modules=javafx.controls,javafx.fxml <GAME NAME>```

i.e.

```javac --module-path ~/Downloads/javafx-sdk-11.0.2/lib --add-modules=javafx.controls,javafx.fxml Pong```

## Games

### Pong
#### Description
Pong consists of two vertical paddles (rectangles) placed on opposing ends of the screen, and a ball which moves between said paddles. 
Each player controls one paddle, and uses it to reflect the incoming ball. 
If a player fails to reflect the incoming ball, and it strikes their side of the screen, the opponent gains a point.

The goal is to manipulate the ball in such a way that the opponent is unable to reflect it, thus gaining you a point.

#### Controls
Player 1:
* W - Move paddle up
* S - Move paddle down

Player 2:
* UP - Move paddle up
* DOWN - Move paddle down

### Atari Breakout
#### Description
Atari Breakout is a simple game in which the player's goal is to destroy all the bricks by hitting them with the ball. 
The player controls a paddle at the bottom of the screen which is used to reflect the ball back up towards the bricks.
The game is won when no bricks remain. 
If a player fails to reflect the ball, they lose a life. 
The game is over when the player has no lives remaining. 

#### Controls
* D || RIGHT - Move paddle right
* A || LEFT - Move paddle left

### Snake
#### Description
In Snake, you control a "snake" (small blue line) whose goal it is to grow as large as possible. 
To achieve this goal, you eat "apples" (small green squares) to grow longer.
Each apple eaten increases your score.

#### Controls
* W || UP - Move snake up
* A || LEFT - Move snake left
* S || DOWN - Move snake down
* D || RIGHT - Move snake right

### Chess
#### Description
In chess, your goal is to put your opponent in a state of checkmate, wherein they have no legal moves which would prevent your from capturing their King.

Pieces are moved by first hovering over a peice (yellow square), selecting the piece (orange square), then selecting a location to move it to (yellow square).

#### Controls
* W || UP - Move selection up
* A || LEFT - Move selection left
* S || DOWN - Move selection down
* D || RIGHT - Move selector right
* SPACE - Select piece || Place piece
* ESCAPE - Deselect piece

### Papi Jump
#### Description
Papi Jump is an age-old spin on a classic gaming format. 
The original Papi Jump is regarded by many as the cleanest, best executed "jump" game, and was voted best app in 2005.

In the words of the original creators, Sunflat games, "PapiJump is a simple but addictive jumping action game!
Mr.Papi (red ball guy) wants to go up higher, is jumping eternally."

The goal of Papi Jump is to climb as high as possible through bouncing on various colored platforms. 
Each color represents a different type of platform; Green platforms do nothing special while yellow platforms dissapear after one bounce, and red platforms will bounce you extra high.

#### Controls
* A || LEFT - Move Papi left
* D || RIGHT - Move Papi right

### Tetris
#### Description
In Tetris, the goal is to obtain as high a score as possible without losing.
A game is lost when so many pieces (technically called "Tetrominos") have accumulated on the board that one spills out over the top of the board.

To prevent this accumulation, the player must clear rows by arranging tetrominos in such a way that they form a full horizontal line across the board. When this happens, said row is cleared from the board and points are added to the player's score.

On the right side of the board is your "queue".
The queue represents the upcoming tetrominos that you will have to control, in descending order.

#### Controls
* W || UP - Rotate tetromino
* A || LEFT - Move tetromino left
* S || DOWN - Move tetromino down
* D || RIGHT - Move tetromino right
