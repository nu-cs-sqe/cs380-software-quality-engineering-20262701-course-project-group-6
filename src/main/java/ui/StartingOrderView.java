package ui;

import domain.DiceRoll;
import domain.GameSession;
import domain.PlayerColor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

final class StartingOrderView extends StackPane {
    private static final double SCREEN_PADDING = 24;
    private static final double FORM_PADDING = 24;
    private static final double FORM_WIDTH = 420;
    private static final double SECTION_SPACING = 18;
    private static final double OPTION_SPACING = 8;
    private static final Font TITLE_FONT = Font.font("System", FontWeight.BOLD, 24);
    private static final Font PROMPT_FONT = Font.font("System", FontWeight.BOLD, 16);

    private final GameSession game;
    private final ResourceBundle messages;
    private final Runnable startPlaying;
    private final Button roll = new Button();
    private final VBox rollLog = new VBox(OPTION_SPACING);
    private final Label orderLabel = new Label();
    private List<Map<Integer, DiceRoll>> rounds = List.of();
    private List<Integer> pickOrder = List.of();
    private int roundIndex;
    private int seatIndex;
    private final Label prompt = new Label();
    private final FlowPane colorButtons = new FlowPane(OPTION_SPACING, OPTION_SPACING);

    StartingOrderView(GameSession game, ResourceBundle messages, Runnable startPlaying, Runnable goBack) {
        this.game = game;
        this.messages = messages;
        this.startPlaying = startPlaying;
        setPadding(new Insets(SCREEN_PADDING));
        setStyle("-fx-background-color: #f5ecdc;");
        getChildren().add(createForm(goBack));
    }

    private VBox createForm(Runnable goBack) {
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

        VBox form = new VBox(SECTION_SPACING, title, explanation, actions, rollLog, orderLabel, prompt,
                colorButtons);
        form.setPadding(new Insets(FORM_PADDING));
        form.setMaxSize(FORM_WIDTH, USE_PREF_SIZE);
        form.setStyle("-fx-background-color: #fff8e7; -fx-border-color: #c5a15b; "
                + "-fx-background-radius: 12; -fx-border-radius: 12;");
        return form;
    }

    // Every seat's dice are rolled on the first click; each click then shows the next seat's roll.
    private void showNextRoll() {
        if (rounds.isEmpty()) {
            pickOrder = game.rollForStartingOrder();
            rounds = game.getStartingRolls();
        }
        Map<Integer, DiceRoll> round = rounds.get(roundIndex);
        List<Integer> seats = new ArrayList<>(round.keySet());
        int seat = seats.get(seatIndex);
        DiceRoll dice = round.get(seat);
        rollLog.getChildren().add(new Label(MessageFormat.format(messages.getString("order.rolled"), seat,
                dice.getFirstDie(), dice.getSecondDie(), dice.getTotal())));
        seatIndex++;
        if (seatIndex == seats.size()) {
            seatIndex = 0;
            roundIndex++;
            if (roundIndex == rounds.size()) {
                roll.setDisable(true);
                roll.setVisible(false);
                roll.setManaged(false);
                orderLabel.setText(MessageFormat.format(messages.getString("order.result"), seatNames(pickOrder)));
                showNextPick();
                return;
            }
            List<Integer> tied = new ArrayList<>(rounds.get(roundIndex).keySet());
            rollLog.getChildren().add(new Label(MessageFormat.format(messages.getString("order.tie"),
                    seatNames(tied))));
        }
        int nextSeat = new ArrayList<>(rounds.get(roundIndex).keySet()).get(seatIndex);
        roll.setText(MessageFormat.format(messages.getString("order.roll"), nextSeat));
    }

    private String seatNames(List<Integer> seats) {
        return seats.stream()
                .map(seat -> MessageFormat.format(messages.getString("order.seat"), seat))
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
            Button pick = new Button(messages.getString("color." + color.name()));
            pick.setStyle("-fx-base: " + PlayerColors.web(color) + "; -fx-font-weight: bold;");
            pick.setOnAction(event -> {
                game.pickColor(color);
                showNextPick();
            });
            colorButtons.getChildren().add(pick);
        }
    }
}
