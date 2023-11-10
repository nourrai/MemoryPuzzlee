package com.example.demo3;
//https://gist.github.com/Da9el00/1b5199491460f433ba15f0eff772d0cc
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class MemoryPuzzle extends Application {
    // Constants for the game grid
    private static final int SIZE = 4;
    private static final int TOTAL_CARDS = SIZE * SIZE / 2;

    // List to store card values (pairs)
    private List<String> cardValues = new ArrayList<>();

    // List to store buttons representing cards
    private List<Button> buttons = new ArrayList<>();

    // Counter to track the number of card clicks
    private int count = 0;

    // Variables to store the currently selected card buttons
    private Button sButton1;
    private Button sButton2;

    // Variable to store the player's score
    private int score = 0;

    // Label to display the current score
    private Label scoreLabel;

    // ArrayList to store scores (not currently in use)
    private ArrayList<Integer> scores;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the starting window
        initializeStartingWindow(primaryStage);
    }

    // Method to set up the starting window
    private void initializeStartingWindow(Stage primaryStage) {
        BorderPane startLayout = new BorderPane();
        startLayout.setPadding(new Insets(20));

        // Game Title
        Text gameTitle = new Text("Memory Puzzle Game");
        gameTitle.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        gameTitle.setFill(Color.BLUE);
        startLayout.setTop(gameTitle);
        BorderPane.setAlignment(gameTitle, Pos.CENTER);

        // Buttons
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        // Start Game Button
        Button startButton = new Button("Start Game");
        startButton.setOnAction(event -> {
            initializeGame(primaryStage);
            primaryStage.setScene(getGameScene());
        });

        buttonBox.getChildren().addAll(startButton);
        startLayout.setCenter(buttonBox);

        // Create the starting scene
        Scene startScene = new Scene(startLayout, 400, 200);
        primaryStage.setTitle("Memory Puzzle Game");
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    // Method to initialize the game
    private void initializeGame(Stage primaryStage) {
        // Reset game-specific variables
        cardValues.clear();
        buttons.clear();
        count = 0;
        score = 0;

        // Create pairs of matching card values
        for (int i = 1; i <= TOTAL_CARDS; i++) {
            cardValues.add(String.valueOf(i));
            cardValues.add(String.valueOf(i));
        }
        Collections.shuffle(cardValues);

        // Create the game grid
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        initializeCards(gridPane);

        // Create the game layout
        BorderPane gameLayout = new BorderPane();
        gameLayout.setPadding(new Insets(20));

        // Current Score Label
        scoreLabel = new Label("Current Score: " + score);
        gameLayout.setTop(scoreLabel);
        BorderPane.setAlignment(scoreLabel, Pos.CENTER);

        // Add the game grid to the layout
        gameLayout.setCenter(gridPane);

        // Create the game scene
        Scene gameScene = new Scene(gameLayout, 400, 400);
        primaryStage.setScene(gameScene);
    }

    // Method to get the current game scene
    private Scene getGameScene() {
        return scoreLabel.getScene();
    }

    // Method to create and initialize the game grid
    private void initializeCards(GridPane gridPane) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Button button = createCardButton();
                buttons.add(button);
                gridPane.add(button, j, i);
            }
        }
    }

    // Method to create a card button
    private Button createCardButton() {
        Button button = new Button();
        button.setMinSize(80, 80);
        button.setOnAction(event -> handleCardClick(button));
        return button;
    }

    // Method to handle a click on a card button
    private void handleCardClick(Button button) {
        if (count < 2) {
            button.setText(cardValues.get(buttons.indexOf(button)));
            button.setDisable(true);

            if (count == 0) {
                sButton1 = button;
            } else {
                sButton2 = button;
            }
            count++;
        }
        if (count == 2) {
            checkMatch();
            checkGameCompletion();
        }
    }

    // Method to check if two selected cards match
    private void checkMatch() {
        if (sButton1.getText().equals(sButton2.getText())) {
            sButton1.setDisable(true);
            sButton2.setDisable(true);
            sButton1 = null;
            sButton2 = null;
            count = 0;

            // Increase score on match
            score += 10;
            updateScoreLabel();
        } else {
            // Delay before hiding the cards
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                sButton1.setText("");
                sButton2.setText("");
                sButton1.setDisable(false);
                sButton2.setDisable(false);

                sButton1 = null;
                sButton2 = null;
                count = 0;
            });
            pause.play();
        }
    }

    // Method to check if the game is completed
    private void checkGameCompletion() {
        boolean isCompleted = buttons.stream()
                .allMatch(Button::isDisabled);

        if (isCompleted) {
            showGameCompleteAlert();
        }
    }

    // Method to show the game completion alert
    private void showGameCompleteAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText("You've completed the game!");
        alert.setContentText("Do you want to play again?");

        ButtonType playAgainButton = new ButtonType("Play Again", ButtonBar.ButtonData.YES);
        ButtonType exitButton = new ButtonType("Exit", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(playAgainButton, exitButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == playAgainButton) {
            resetGame();
        } else {
            System.exit(0);
        }
    }

    // Method to reset the game
    private void resetGame() {
        initializeGame((Stage) scoreLabel.getScene().getWindow());
    }

    // Method to update the displayed score label
    private void updateScoreLabel() {
        scoreLabel.setText("Current Score: " + score);
    }

    // Entry point of the application
    public static void main(String[] args) {
        launch(args);
    }
}