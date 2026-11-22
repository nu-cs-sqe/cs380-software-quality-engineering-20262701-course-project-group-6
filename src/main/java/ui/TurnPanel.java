package ui;

import domain.DiceRoll;
import domain.GameSession;
import domain.PlayerColor;
import domain.ResourceCardType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

final class TurnPanel extends VBox {
    private static final double PANEL_WIDTH = 380;
    private static final double PANEL_PADDING = 16;
    private static final double SPACING = 12;
    private static final double DIE_SIZE = 48;
    private static final double SWATCH_SIZE = 12;
    private static final double GRID_GAP = 6;
    private static final Color INK = Color.web("#352820");
    private static final Font TURN_FONT = Font.font("System", FontWeight.BOLD, 20);
    private static final String PEEK_STYLE = "-fx-background-color: rgba(197, 161, 91, 0.25); "
            + "-fx-background-radius: 6;";
    private static final Font HEADER_FONT = Font.font("System", FontWeight.BOLD, 12);

    private final GameSession game;
    private final ResourceBundle messages;
    private final Label turnLabel = new Label();
    private final DieFace firstDie = new DieFace(DIE_SIZE);
    private final DieFace secondDie = new DieFace(DIE_SIZE);
    private final Label rollLabel = new Label();
    private final Button roll;
    private final Button endTurn;
    private final GridPane cards = new GridPane();

    TurnPanel(GameSession game, ResourceBundle messages) {
        super(SPACING);
        this.game = game;
        this.messages = messages;
        setPadding(new Insets(PANEL_PADDING));
        setPrefWidth(PANEL_WIDTH);
        setMinWidth(PANEL_WIDTH);
        setStyle("-fx-background-color: #fff8e7; -fx-border-color: #c5a15b; "
                + "-fx-background-radius: 12; -fx-border-radius: 12;");

        turnLabel.setFont(TURN_FONT);
        turnLabel.setTextFill(INK);
        turnLabel.setWrapText(true);
        rollLabel.setWrapText(true);
        rollLabel.setTextFill(INK);
        roll = new Button(messages.getString("turn.roll"));
        roll.setDefaultButton(true);
        roll.setStyle("-fx-background-color: #a51f24; -fx-text-fill: white; -fx-font-weight: bold;");
        roll.setOnAction(event -> rollDice());
        endTurn = new Button(messages.getString("turn.end"));
        endTurn.setOnAction(event -> endTurn());
        HBox dice = new HBox(SPACING, firstDie, secondDie);
        dice.setAlignment(Pos.CENTER_LEFT);
        HBox actions = new HBox(SPACING, roll, endTurn);
        getChildren().addAll(turnLabel, dice, rollLabel, actions, createCardsSection());
        startTurn();
    }

    private VBox createCardsSection() {
        cards.setHgap(GRID_GAP);
        cards.setVgap(GRID_GAP);
        Label cardsTitle = new Label(messages.getString("turn.cards"));
        cardsTitle.setFont(HEADER_FONT);
        Label peekHint = new Label(messages.getString("turn.peek"));
        peekHint.setWrapText(true);
        peekHint.setTextFill(INK);
        return new VBox(SPACING, cardsTitle, cards, peekHint);
    }

    private void rollDice() {
        DiceRoll result = game.rollDice();
        firstDie.show(result.getFirstDie());
        secondDie.show(result.getSecondDie());
        rollLabel.setText(MessageFormat.format(messages.getString("turn.rolled"),
                colorName(game.getCurrentColor()), result.getTotal()));
        roll.setDisable(true);
        endTurn.setDisable(false);
        endTurn.setDefaultButton(true);
        showCards();
    }

    private void endTurn() {
        game.endTurn();
        startTurn();
    }

    private void startTurn() {
        PlayerColor current = game.getCurrentColor();
        turnLabel.setText(MessageFormat.format(messages.getString("turn.current"), colorName(current)));
        firstDie.clear();
        secondDie.clear();
        rollLabel.setText(messages.getString("turn.rollPrompt"));
        roll.setDisable(false);
        endTurn.setDisable(true);
        endTurn.setDefaultButton(false);
        showCards();
    }

    private void showCards() {
        cards.getChildren().clear();
        ResourceCardType[] types = ResourceCardType.values();
        for (int column = 0; column < types.length; column++) {
            cards.add(header(messages.getString("resource." + types[column].name())), column + 1, 0);
        }
        cards.add(header(messages.getString("turn.total")), types.length + 1, 0);
        cards.add(header(messages.getString("turn.points")), types.length + 2, 0);
        int row = 1;
        for (PlayerColor color : game.getTurnOrder()) {
            addPlayerRow(color, row);
            row++;
        }
        cards.add(header(messages.getString("turn.bank")), 0, row);
        for (int column = 0; column < types.length; column++) {
            cards.add(count(game.getBankResourceCount(types[column])), column + 1, row);
        }
    }

    private void addPlayerRow(PlayerColor color, int row) {
        ResourceCardType[] types = ResourceCardType.values();
        List<Label> hidden = new ArrayList<>();
        int total = 0;
        cards.add(playerName(color), 0, row);
        for (int column = 0; column < types.length; column++) {
            Label card = new Label(messages.getString("turn.hidden"));
            card.setTextFill(INK);
            hidden.add(card);
            cards.add(card, column + 1, row);
            total += game.getResourceCount(color, types[column]);
        }
        cards.add(count(total), types.length + 1, row);
        cards.add(count(game.getVictoryPointCount(color)), types.length + 2, row);
        if (color == game.getCurrentColor()) {
            cards.add(peekArea(color, hidden), 0, row, GridPane.REMAINING, 1);
        }
    }

    private Region peekArea(PlayerColor color, List<Label> hidden) {
        ResourceCardType[] types = ResourceCardType.values();
        Region area = new Region();
        area.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        area.setStyle(PEEK_STYLE);
        area.setOnMouseEntered(event -> {
            for (int column = 0; column < types.length; column++) {
                hidden.get(column).setText(number(game.getResourceCount(color, types[column])));
            }
        });
        area.setOnMouseExited(event -> hidden.forEach(card -> card.setText(messages.getString("turn.hidden"))));
        return area;
    }

    private HBox playerName(PlayerColor color) {
        Rectangle swatch = new Rectangle(SWATCH_SIZE, SWATCH_SIZE, PlayerColors.of(color));
        swatch.setStroke(INK);
        Label name = new Label(colorName(color));
        name.setMinWidth(Region.USE_PREF_SIZE);
        name.setTextFill(INK);
        if (color == game.getCurrentColor()) {
            name.setFont(HEADER_FONT);
        }
        HBox box = new HBox(GRID_GAP, swatch, name);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Label header(String text) {
        Label label = new Label(text);
        label.setFont(HEADER_FONT);
        label.setMinWidth(Region.USE_PREF_SIZE);
        label.setTextFill(INK);
        return label;
    }

    private Label count(int value) {
        Label label = new Label(number(value));
        label.setTextFill(INK);
        return label;
    }

    private String number(int value) {
        return NumberFormat.getIntegerInstance(messages.getLocale()).format(value);
    }

    private String colorName(PlayerColor color) {
        return messages.getString("color." + color.name());
    }
}
