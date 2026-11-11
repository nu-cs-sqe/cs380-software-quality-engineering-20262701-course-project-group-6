package ui;

import domain.BeginnerBoard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ResourceBundle;
import java.util.function.Consumer;

final class GameSetupView extends StackPane {
    private static final double SCREEN_PADDING = 24;
    private static final double FORM_PADDING = 24;
    private static final double FORM_WIDTH = 360;
    private static final double SECTION_SPACING = 20;
    private static final double OPTION_SPACING = 8;
    private static final double PLAYER_OPTION_SPACING = 24;
    private static final Font TITLE_FONT = Font.font("System", FontWeight.BOLD, 24);

    private final ResourceBundle messages;
    private final ToggleGroup playerCounts = new ToggleGroup();

    GameSetupView(ResourceBundle messages, Consumer<BeginnerBoard> startGame, Runnable goBack) {
        this.messages = messages;
        setPadding(new Insets(SCREEN_PADDING));
        setStyle("-fx-background-color: #f5ecdc;");
        getChildren().add(createForm(startGame, goBack));
    }

    private VBox createForm(Consumer<BeginnerBoard> startGame, Runnable goBack) {
        RadioButton threePlayers = createOption("setup.threePlayers", playerCounts);
        RadioButton fourPlayers = createOption("setup.fourPlayers", playerCounts);
        HBox counts = new HBox(PLAYER_OPTION_SPACING, threePlayers, fourPlayers);

        ToggleGroup layouts = new ToggleGroup();
        RadioButton beginner = createOption("setup.beginner", layouts);
        RadioButton manual = createOption("setup.manual", layouts);
        beginner.setSelected(true);
        VBox layoutOptions = new VBox(OPTION_SPACING, beginner, manual, createManualNotice(manual));

        Label title = new Label(messages.getString("setup.title"));
        title.setFont(TITLE_FONT);
        title.setWrapText(true);

        Button start = createStartButton(threePlayers, beginner, startGame);
        VBox form = new VBox(SECTION_SPACING, title, createSection("setup.playerCount", counts),
                createSection("setup.layout", layoutOptions), createActions(start, goBack));
        form.setPadding(new Insets(FORM_PADDING));
        form.setMaxSize(FORM_WIDTH, USE_PREF_SIZE);
        form.setStyle("-fx-background-color: #fff8e7; -fx-border-color: #c5a15b; "
                + "-fx-background-radius: 12; -fx-border-radius: 12;");
        return form;
    }

    private RadioButton createOption(String key, ToggleGroup group) {
        RadioButton option = new RadioButton(messages.getString(key));
        option.setToggleGroup(group);
        option.setWrapText(true);
        return option;
    }

    private VBox createSection(String key, Node options) {
        Label label = new Label(messages.getString(key));
        label.setStyle("-fx-font-weight: bold;");
        return new VBox(OPTION_SPACING, label, options);
    }

    private Label createManualNotice(RadioButton manual) {
        Label notice = new Label(messages.getString("setup.manualNotice"));
        notice.setWrapText(true);
        notice.setStyle("-fx-text-fill: #665747;");
        notice.visibleProperty().bind(manual.selectedProperty());
        notice.managedProperty().bind(notice.visibleProperty());
        return notice;
    }

    private Button createStartButton(RadioButton threePlayers, RadioButton beginner,
            Consumer<BeginnerBoard> startGame) {
        Button start = new Button(messages.getString("setup.start"));
        start.setDefaultButton(true);
        start.disableProperty().bind(playerCounts.selectedToggleProperty().isNull()
                .or(beginner.selectedProperty().not()));
        start.setOnAction(event -> startGame.accept(threePlayers.isSelected()
                ? BeginnerBoard.createForThreePlayers() : new BeginnerBoard()));
        start.setStyle("-fx-background-color: #a51f24; -fx-text-fill: white; -fx-font-weight: bold;");
        return start;
    }

    private HBox createActions(Button start, Runnable goBack) {
        Button cancel = new Button(messages.getString("setup.cancel"));
        cancel.setCancelButton(true);
        cancel.setOnAction(event -> goBack.run());
        HBox actions = new HBox(OPTION_SPACING, cancel, start);
        actions.setAlignment(Pos.CENTER_RIGHT);
        return actions;
    }
}
