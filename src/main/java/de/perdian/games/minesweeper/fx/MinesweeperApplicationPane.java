package de.perdian.games.minesweeper.fx;

import org.apache.commons.lang3.exception.ExceptionUtils;

import de.perdian.games.minesweeper.core.MinesweeperBoard;
import de.perdian.games.minesweeper.core.MinesweeperBoardBuilder;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

class MinesweeperApplicationPane extends BorderPane {

    private IntegerProperty boardColumns = new SimpleIntegerProperty();
    private IntegerProperty boardRows = new SimpleIntegerProperty();
    private IntegerProperty boardMines = new SimpleIntegerProperty();

    MinesweeperApplicationPane() {

        Label boardColumnsLabel = new Label("Board width");
        TextField boardColumnsField = new TextField();
        boardColumnsField.textProperty().addListener((o, oldValue, newValue) -> this.getBoardColumns().setValue(Integer.parseInt(newValue)));
        boardColumnsField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(8, 100, 8)));
        boardColumnsField.setText("8");
        GridPane.setHgrow(boardColumnsField, Priority.ALWAYS);

        Label boardRowsLabel = new Label("Board height");
        TextField boardRowsField = new TextField();
        boardRowsField.textProperty().addListener((o, oldValue, newValue) -> this.getBoardRows().setValue(Integer.parseInt(newValue)));
        boardRowsField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(8, 100, 8)));
        boardRowsField.setText("8");
        GridPane.setHgrow(boardRowsField, Priority.ALWAYS);

        Label boardMinesLabel = new Label("Number of bombs");
        TextField boardMinesField = new TextField("1");
        boardMinesField.textProperty().addListener((o, oldValue, newValue) -> this.getBoardMines().setValue(Integer.parseInt(newValue)));
        boardMinesField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(1, 100, 1)));
        boardMinesField.setText("10");
        GridPane.setHgrow(boardMinesField, Priority.ALWAYS);

        Button executeButton = new Button("Start new game");
        executeButton.setOnAction(event -> this.createBoard());
        executeButton.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(executeButton, Priority.ALWAYS);

        GridPane topPane = new GridPane();
        topPane.setHgap(4);
        topPane.setVgap(2);
        topPane.setPadding(new Insets(6, 6, 6, 6));
        topPane.add(boardColumnsLabel, 0, 0, 1, 1);
        topPane.add(boardRowsLabel, 1, 0, 1, 1);
        topPane.add(boardMinesLabel, 2, 0, 1, 1);
        topPane.add(boardColumnsField, 0, 1, 1, 1);
        topPane.add(boardRowsField, 1, 1, 1, 1);
        topPane.add(boardMinesField, 2, 1, 1, 1);
        topPane.add(executeButton, 3, 0, 1, 2);

        TitledPane topPaneWrapper = new TitledPane("Game settings", topPane);
        topPaneWrapper.setCollapsible(false);

        Label mainPane = new Label("No game started yet");
        TitledPane mainPaneWrapper = new TitledPane("No game started yet", mainPane);
        mainPaneWrapper.setCollapsible(false);
        mainPaneWrapper.setMaxHeight(Double.MAX_VALUE);
        BorderPane.setMargin(mainPaneWrapper, new Insets(4, 0, 0, 0));

        this.setTop(topPaneWrapper);
        this.setCenter(mainPaneWrapper);
        this.setPadding(new Insets(6, 6, 6, 6));

    }

    private void createBoard() {
        try {

            MinesweeperBoardBuilder boardBuilder = new MinesweeperBoardBuilder();
            boardBuilder.setRows(this.getBoardRows().getValue());
            boardBuilder.setColumns(this.getBoardColumns().getValue());
            boardBuilder.setMines(this.getBoardMines().getValue());

            MinesweeperBoard board = boardBuilder.build();
            String boardTitle = "Minesweeper (" + this.getBoardColumns().getValue() + "x" + this.getBoardRows().getValue() + " cells, " + this.getBoardMines().getValue() + " bombs)";
            Label boardPaneLoadingLabel = new Label("Preparing new game...");
            TitledPane boardPaneWrapper = new TitledPane(boardTitle, boardPaneLoadingLabel);
            boardPaneWrapper.setCollapsible(false);
            boardPaneWrapper.setMaxHeight(Double.MAX_VALUE);
            BorderPane.setMargin(boardPaneWrapper, new Insets(4, 0, 0, 0));
            this.setCenter(boardPaneWrapper);

            new Thread(() -> {
                MinesweeperBoardPane boardPane = new MinesweeperBoardPane(board);
                boardPane.setPadding(new Insets(4, 4, 4, 4));
                Platform.runLater(() -> {
                    boardPaneWrapper.setContent(boardPane);
                });
            }).start();

        } catch (Exception e) {

            TextArea errorArea = new TextArea(ExceptionUtils.getStackTrace(e));
            errorArea.setEditable(false);
            errorArea.setFont(Font.font("Courier New", 14f));
            errorArea.setMaxWidth(Double.MAX_VALUE);
            ScrollPane errorPane = new ScrollPane(errorArea);
            errorPane.setFitToWidth(true);
            errorPane.setFitToHeight(true);
            errorPane.setMinHeight(400);

            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setTitle("Invalid input");
            errorAlert.setHeaderText("Invalid input values");
            errorAlert.setContentText("Cannot create a new board with the entered values");
            errorAlert.getDialogPane().setExpandableContent(errorPane);
            errorAlert.getDialogPane().setExpanded(true);
            errorAlert.getDialogPane().setMinWidth(640);
            errorAlert.showAndWait();

        }
    }

    static class IntegerStringConverter extends StringConverter<Integer> {

        private int minValue = 0;
        private int maxValue = 0;
        private int defaultValue = 0;

        IntegerStringConverter(int minValue, int maxValue, int defaultValue) {
            this.setMinValue(minValue);
            this.setMaxValue(maxValue);
            this.setDefaultValue(defaultValue);
        }

        @Override
        public String toString(Integer object) {
            if (object == null || object.intValue() < this.getMinValue() || object.intValue() > this.getMaxValue()) {
                return String.valueOf(this.getDefaultValue());
            } else {
                return object.toString();
            }
        }

        @Override
        public Integer fromString(String string) {
            try {
                int intValue = Integer.parseInt(string.trim(), 10);
                if (intValue >= this.getMinValue() && intValue <= this.getMaxValue()) {
                    return Integer.valueOf(intValue);
                } else {
                    throw new IllegalArgumentException("Out of bounds");
                }
            } catch (Exception e) {
                return this.getDefaultValue();
            }
        }

        private int getMaxValue() {
            return this.maxValue;
        }
        private void setMaxValue(int maxValue) {
            this.maxValue = maxValue;
        }

        private int getMinValue() {
            return this.minValue;
        }
        private void setMinValue(int minValue) {
            this.minValue = minValue;
        }

        private int getDefaultValue() {
            return this.defaultValue;
        }
        private void setDefaultValue(int defaultValue) {
            this.defaultValue = defaultValue;
        }

    }

    private IntegerProperty getBoardColumns() {
        return this.boardColumns;
    }

    private IntegerProperty getBoardRows() {
        return this.boardRows;
    }

    private IntegerProperty getBoardMines() {
        return this.boardMines;
    }

}
