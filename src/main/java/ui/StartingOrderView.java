package ui;

import domain.DiceRoll;
import domain.GameSession;
import domain.PlayerColor;
import domain.ResourceCardType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

final class StartingOrderView extends BorderPane {
    private static final double SCREEN_PADDING = 20;
    private static final double PANEL_PADDING = 20;
    private static final double PANEL_WIDTH = 380;
    private static final double SECTION_SPACING = 14;
    private static final double OPTION_SPACING = 8;
    private static final Font TITLE_FONT = Font.font("System", FontWeight.BOLD, 22);
    private static final Font PROMPT_FONT = Font.font("System", FontWeight.BOLD, 16);

    private final GameSession game;
    private final ResourceBundle messages;
    private final Runnable startPlaying;
    private final Button roll = new Button();
    private final VBox rollLog = new VBox(OPTION_SPACING);
    private final Label orderLabel = new Label();
    private final Label prompt = new Label();
    private final VBox colorButtons = new VBox(OPTION_SPACING);
    private List<Map<Integer, DiceRoll>> rounds = List.of();
    private List<Integer> pickOrder = List.of();
    private int roundIndex;
    private int playerIndex;

    StartingOrderView(GameSession game, ResourceBundle messages, Runnable startPlaying, Runnable goBack) {
        this.game = game;
        this.messages = messages;
        this.startPlaying = startPlaying;
        setPadding(new Insets(SCREEN_PADDING));
        setStyle("-fx-background-color: #f5ecdc;");
        setCenter(new BoardMap(game.getBoard(), messages));
        VBox panel = createPanel(goBack);
        setRight(panel);
        setMargin(panel, new Insets(0, 0, 0, SECTION_SPACING));
    }

    private VBox createPanel(Runnable goBack) {
        Label title = new Label(messages.getString("order.title"));
        title.setFont(TITLE_FONT);
        title.setWrapText(true);
        Label explanation = new Label(messages.getString("order.explanation"));
        explanation.setWrapText(true);

        roll.setText(MessageFormat.format(messages.getString("order.roll"), 1));
        roll.setDefaultButton(true);
        roll.setStyle("-fx-background-color: #a51f24; -fx-text-fill: white; -fx-font-weight: bold;");
        roll.setOnAction(event -> showNextRoll());

        orderLabel.setWrapText(true);
        prompt.setFont(PROMPT_FONT);
        prompt.setWrapText(true);
        Button back = new Button(messages.getString("setup.cancel"));
        back.setCancelButton(true);
        back.setOnAction(event -> goBack.run());
        HBox actions = new HBox(OPTION_SPACING, back, roll);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox panel = new VBox(SECTION_SPACING, title, explanation, actions, rollLog, orderLabel, prompt,
                colorButtons);
        panel.setPadding(new Insets(PANEL_PADDING));
        panel.setPrefWidth(PANEL_WIDTH);
        panel.setMinWidth(PANEL_WIDTH);
        panel.setStyle("-fx-background-color: #fff8e7; -fx-border-color: #c5a15b; "
                + "-fx-background-radius: 12; -fx-border-radius: 12;");
        return panel;
    }

    // Every player's dice are rolled on the first click; each click then shows the next player's roll.
    private void showNextRoll() {
        if (rounds.isEmpty()) {
            pickOrder = game.rollForStartingOrder();
            rounds = game.getStartingRolls();
        }
        Map<Integer, DiceRoll> round = rounds.get(roundIndex);
        List<Integer> players = new ArrayList<>(round.keySet());
        int player = players.get(playerIndex);
        DiceRoll dice = round.get(player);
        rollLog.getChildren().add(new Label(MessageFormat.format(messages.getString("order.rolled"), player,
                dice.getFirstDie(), dice.getSecondDie(), dice.getTotal())));
        playerIndex++;
        if (playerIndex == players.size()) {
            playerIndex = 0;
            roundIndex++;
            if (roundIndex == rounds.size()) {
                roll.setDisable(true);
                roll.setVisible(false);
                roll.setManaged(false);
                orderLabel.setText(MessageFormat.format(messages.getString("order.result"), playerNames(pickOrder)));
                showNextPick();
                return;
            }
            List<Integer> tied = new ArrayList<>(rounds.get(roundIndex).keySet());
            rollLog.getChildren().add(new Label(MessageFormat.format(messages.getString("order.tie"),
                    playerNames(tied))));
        }
        int nextPlayer = new ArrayList<>(rounds.get(roundIndex).keySet()).get(playerIndex);
        roll.setText(MessageFormat.format(messages.getString("order.roll"), nextPlayer));
    }

    private String playerNames(List<Integer> players) {
        return players.stream()
                .map(player -> MessageFormat.format(messages.getString("order.player"), player))
                .collect(Collectors.joining(messages.getString("order.separator")));
    }

    private void showNextPick() {
        List<PlayerColor> available = game.getAvailableColors();
        if (available.isEmpty()) {
            startPlaying.run();
            return;
        }
        prompt.setText(MessageFormat.format(messages.getString("order.pick"), game.getSeatToPick()));
        colorButtons.getChildren().clear();
        for (PlayerColor color : available) {
            Button pick = new Button(MessageFormat.format(messages.getString("order.colorChoice"),
                    messages.getString("color." + color.name()), startingResources(color)));
            pick.setStyle("-fx-base: " + PlayerColors.web(color) + "; -fx-font-weight: bold;");
            pick.setMaxWidth(Double.MAX_VALUE);
            pick.setOnAction(event -> {
                game.pickColor(color);
                showNextPick();
            });
            colorButtons.getChildren().add(pick);
        }
    }

    private String startingResources(PlayerColor color) {
        return game.getBoard().getStartingResources(color).stream()
                .map(ResourceCardType::name)
                .map(type -> messages.getString("resource." + type))
                .collect(Collectors.joining(messages.getString("order.separator")));
    }
}
