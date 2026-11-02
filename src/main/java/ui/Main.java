package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    private static final double COVER_WIDTH = 285;
    private static final double CONTENT_WIDTH = 303;
    private static final double COVER_PADDING = 8;
    private static final double BUTTON_HEIGHT = 46;
    private static final double CREDITS_FONT_SIZE = 16;
    private static final double SECTION_SPACING = 18;
    private static final double WINDOW_PADDING = 32;
    private static final double WINDOW_WIDTH = 480;
    private static final double WINDOW_HEIGHT = 600;
    private static final double MIN_WINDOW_WIDTH = 400;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(SECTION_SPACING, createCover(), createNewGameButton(), createCredits());
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(WINDOW_PADDING));
        root.setStyle("-fx-background-color: #f5ecdc;");

        stage.setTitle("Catan");
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }

    private StackPane createCover() {
        Image boxArt = new Image(Objects.requireNonNull(
                Main.class.getResource("/Catan-2015-boxart.jpg"),
                "Missing Catan box art resource").toExternalForm());
        ImageView cover = new ImageView(boxArt);
        cover.setFitWidth(COVER_WIDTH);
        cover.setPreserveRatio(true);
        cover.setAccessibleText("Catan box art");

        StackPane coverFrame = new StackPane(cover);
        coverFrame.setPadding(new Insets(COVER_PADDING));
        coverFrame.setMaxWidth(CONTENT_WIDTH);
        coverFrame.setStyle("-fx-background-color: #fff8e7; -fx-border-color: #c5a15b;");
        return coverFrame;
    }

    private Button createNewGameButton() {
        // TODO: Connect this button to the game setup screen.
        Button newGame = new Button("New Game");
        newGame.setPrefSize(CONTENT_WIDTH, BUTTON_HEIGHT);
        newGame.setDefaultButton(true);
        newGame.setStyle("-fx-background-color: #a51f24; -fx-text-fill: white; "
                + "-fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand;");
        return newGame;
    }

    private Label createCredits() {
        Label credits = new Label("Omar Coleman, Ryan Rosu, Daniel Shoshin");
        credits.setFont(Font.font(CREDITS_FONT_SIZE));
        credits.setStyle("-fx-text-fill: #665747;");
        credits.setWrapText(true);
        credits.setTextAlignment(TextAlignment.CENTER);
        credits.setMaxWidth(CONTENT_WIDTH);
        return credits;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
