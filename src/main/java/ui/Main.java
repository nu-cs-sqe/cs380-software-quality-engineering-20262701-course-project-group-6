package ui;

import domain.BeginnerBoard;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import language.LanguageOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

public class Main extends Application {
    private static final double COVER_WIDTH = 285;
    private static final double CONTENT_WIDTH = 303;
    private static final double COVER_PADDING = 8;
    private static final double BUTTON_HEIGHT = 46;
    private static final double CREDITS_FONT_SIZE = 16;
    private static final double SECTION_SPACING = 18;
    private static final double LANGUAGE_ROW_SPACING = 10;
    private static final double WINDOW_PADDING = 32;
    private static final double WINDOW_WIDTH = 480;
    private static final double WINDOW_HEIGHT = 640;
    private static final double BOARD_WINDOW_WIDTH = 820;
    private static final double BOARD_WINDOW_HEIGHT = 740;
    private static final double MIN_WINDOW_WIDTH = 400;
    private static final String LANGUAGE_FILE = "/languages.properties";
    private static final String MESSAGES_BUNDLE = "messages";

    private List<Locale> locales;

    @Override
    public void start(Stage stage) {
        locales = new LanguageOptions(loadLanguageFile()).getLocales();

        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setScene(new Scene(new VBox(), WINDOW_WIDTH, WINDOW_HEIGHT));
        showHome(stage, locales.get(0));
        stage.show();
    }

    private static Properties loadLanguageFile() {
        Properties languageFile = new Properties();
        try (InputStream input = Objects.requireNonNull(
                Main.class.getResourceAsStream(LANGUAGE_FILE), "Missing " + LANGUAGE_FILE)) {
            languageFile.load(input);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + LANGUAGE_FILE, e);
        }
        return languageFile;
    }

    private void showHome(Stage stage, Locale locale) {
        ResourceBundle messages = ResourceBundle.getBundle(MESSAGES_BUNDLE, locale,
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));

        VBox root = new VBox(SECTION_SPACING, createCover(messages), createLanguagePicker(stage, messages, locale),
                createNewGameButton(messages, () -> showBoard(stage, messages, locale)), createCredits(messages));
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(WINDOW_PADDING));
        root.setStyle("-fx-background-color: #f5ecdc;");

        stage.setTitle(messages.getString("window.title"));
        stage.getScene().setRoot(root);
    }

    private void showBoard(Stage stage, ResourceBundle messages, Locale locale) {
        stage.getScene().setRoot(new BoardView(new BeginnerBoard(), messages, () -> showHome(stage, locale)));
        stage.setWidth(Math.max(stage.getWidth(), BOARD_WINDOW_WIDTH));
        stage.setHeight(Math.max(stage.getHeight(), BOARD_WINDOW_HEIGHT));
        stage.centerOnScreen();
    }

    private StackPane createCover(ResourceBundle messages) {
        Image boxArt = new Image(Objects.requireNonNull(
                Main.class.getResource("/Catan-2015-boxart.jpg"),
                "Missing Catan box art resource").toExternalForm());
        ImageView cover = new ImageView(boxArt);
        cover.setFitWidth(COVER_WIDTH);
        cover.setPreserveRatio(true);
        cover.setAccessibleText(messages.getString("home.coverDescription"));

        StackPane coverFrame = new StackPane(cover);
        coverFrame.setPadding(new Insets(COVER_PADDING));
        coverFrame.setMaxWidth(CONTENT_WIDTH);
        coverFrame.setStyle("-fx-background-color: #fff8e7; -fx-border-color: #c5a15b;");
        return coverFrame;
    }

    private HBox createLanguagePicker(Stage stage, ResourceBundle messages, Locale selected) {
        ComboBox<String> picker = new ComboBox<>();
        for (Locale locale : locales) {
            picker.getItems().add(nativeName(locale));
        }
        picker.getSelectionModel().select(locales.indexOf(selected));
        picker.setOnAction(event -> showHome(stage, locales.get(picker.getSelectionModel().getSelectedIndex())));

        Label label = new Label(messages.getString("home.languageLabel"));
        label.setLabelFor(picker);
        HBox row = new HBox(LANGUAGE_ROW_SPACING, label, picker);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    // Each language is listed in its own language ("English", "Español") so a player can find theirs.
    private static String nativeName(Locale locale) {
        String name = locale.getDisplayLanguage(locale);
        return name.substring(0, 1).toUpperCase(locale) + name.substring(1);
    }

    private Button createNewGameButton(ResourceBundle messages, Runnable startGame) {
        Button newGame = new Button(messages.getString("home.newGameButton"));
        newGame.setPrefSize(CONTENT_WIDTH, BUTTON_HEIGHT);
        newGame.setDefaultButton(true);
        newGame.setOnAction(event -> startGame.run());
        newGame.setStyle("-fx-background-color: #a51f24; -fx-text-fill: white; "
                + "-fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand;");
        return newGame;
    }

    private Label createCredits(ResourceBundle messages) {
        Label credits = new Label(messages.getString("home.credits"));
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
