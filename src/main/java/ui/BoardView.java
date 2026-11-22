package ui;

import domain.GameSession;
import domain.PlayerColor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.MessageFormat;
import java.util.ResourceBundle;

final class BoardView extends BorderPane {
    private static final double SCREEN_PADDING = 20;
    private static final double SECTION_SPACING = 14;
    private static final Color INK = Color.web("#352820");
    private static final Font TITLE_FONT = Font.font("System", FontWeight.BOLD, 23);

    private final GameSession game;
    private final ResourceBundle messages;
    private final BoardMap boardMap;
    private final TurnPanel turnPanel;

    BoardView(GameSession game, ResourceBundle messages, Runnable goBack) {
        this.game = game;
        this.messages = messages;
        setPadding(new Insets(SCREEN_PADDING));
        setStyle("-fx-background-color: #f5ecdc;");

        HBox header = createHeader(goBack);
        setTop(header);
        setMargin(header, new Insets(0, 0, SECTION_SPACING, 0));
        boardMap = new BoardMap(game.getBoard(), messages);
        setCenter(boardMap);
        turnPanel = new TurnPanel(game, messages, this::chooseBuild, boardMap::clearSpots);
        setRight(turnPanel);
        setMargin(turnPanel, new Insets(0, 0, 0, SECTION_SPACING));

        Label legend = new Label(messages.getString("board.startingResources"));
        legend.setTextFill(INK);
        legend.setWrapText(true);
        setBottom(legend);
        setAlignment(legend, Pos.CENTER);
        setMargin(legend, new Insets(SECTION_SPACING, 0, 0, 0));
    }

    private void chooseBuild(BuildMode mode) {
        String piece = messages.getString("piece." + mode.name());
        if (boardMap.showSpots(mode, game.getCurrentColor(), spot -> build(mode, spot))) {
            turnPanel.showMessage(MessageFormat.format(messages.getString("build.pick"), piece));
        } else {
            turnPanel.showMessage(MessageFormat.format(messages.getString("build.noSpots"), piece));
        }
    }

    // The board only offers legal spots, so a failed build means the player is short of cards or pieces.
    private void build(BuildMode mode, int spot) {
        PlayerColor builder = game.getCurrentColor();
        String piece = messages.getString("piece." + mode.name());
        try {
            if (mode == BuildMode.ROAD) {
                game.buildRoad(spot);
            } else if (mode == BuildMode.SETTLEMENT) {
                game.buildSettlement(spot);
            } else {
                game.buildCity(spot);
            }
            turnPanel.showMessage(MessageFormat.format(messages.getString("build.built"),
                    messages.getString("color." + builder.name()), piece));
        } catch (IllegalStateException error) {
            turnPanel.showMessage(MessageFormat.format(messages.getString("build.cannot"), piece));
        }
        boardMap.clearSpots();
        turnPanel.refresh();
    }

    private HBox createHeader(Runnable goBack) {
        Button back = new Button(messages.getString("board.back"));
        back.setOnAction(event -> goBack.run());

        Label title = new Label(messages.getString("board.title"));
        title.setFont(TITLE_FONT);
        title.setTextFill(INK);
        title.setWrapText(true);
        HBox header = new HBox(SECTION_SPACING, back, title);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }
}
