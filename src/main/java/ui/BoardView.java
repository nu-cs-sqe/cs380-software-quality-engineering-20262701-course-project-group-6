package ui;

import domain.GameSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ResourceBundle;

final class BoardView extends BorderPane {
    private static final double SCREEN_PADDING = 20;
    private static final double SECTION_SPACING = 14;
    private static final Color INK = Color.web("#352820");
    private static final Font TITLE_FONT = Font.font("System", FontWeight.BOLD, 23);

    private final ResourceBundle messages;

    BoardView(GameSession game, ResourceBundle messages, Runnable goBack) {
        this.messages = messages;
        setPadding(new Insets(SCREEN_PADDING));
        setStyle("-fx-background-color: #f5ecdc;");

        HBox header = createHeader(goBack);
        setTop(header);
        setMargin(header, new Insets(0, 0, SECTION_SPACING, 0));
        setCenter(new BoardMap(game.getBoard(), messages));
        TurnPanel turnPanel = new TurnPanel(game, messages);
        setRight(turnPanel);
        setMargin(turnPanel, new Insets(0, 0, 0, SECTION_SPACING));

        Label legend = new Label(messages.getString("board.startingResources"));
        legend.setTextFill(INK);
        legend.setWrapText(true);
        setBottom(legend);
        setAlignment(legend, Pos.CENTER);
        setMargin(legend, new Insets(SECTION_SPACING, 0, 0, 0));
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
