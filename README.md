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
