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
    @Override
    public void start(Stage stage) {
        Image boxArt = new Image(Objects.requireNonNull(
                Main.class.getResource("/Catan-2015-boxart.jpg"),
                "Missing Catan box art resource").toExternalForm());
        ImageView cover = new ImageView(boxArt);
        cover.setFitWidth(285);
        cover.setPreserveRatio(true);
        cover.setAccessibleText("Catan box art");

        StackPane imageBox = new StackPane(cover);
        imageBox.setPadding(new Insets(8));
        imageBox.setMaxWidth(303);
        imageBox.setStyle("-fx-background-color: #fff8e7; -fx-border-color: #c5a15b;");

        // TODO: Connect this button to the game setup screen.
        Button newGame = new Button("New Game");
        newGame.setPrefSize(303, 46);
        newGame.setDefaultButton(true);
        newGame.setStyle("-fx-background-color: #a51f24; -fx-text-fill: white; "
                + "-fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand;");

        Label credits = new Label("Omar Coleman, Ryan Rosu, Daniel Shoshin");
        credits.setFont(Font.font(16));
        credits.setStyle("-fx-text-fill: #665747;");
        credits.setWrapText(true);
        credits.setTextAlignment(TextAlignment.CENTER);
        credits.setMaxWidth(303);

        VBox root = new VBox(18, imageBox, newGame, credits);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: #f5ecdc;");

        stage.setTitle("Catan");
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.setScene(new Scene(root, 480, 600));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
