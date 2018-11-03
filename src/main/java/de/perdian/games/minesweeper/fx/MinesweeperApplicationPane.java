package de.perdian.games.minesweeper.fx;

import org.apache.commons.lang3.exception.ExceptionUtils;

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

    private IntegerProperty boardWidth = new SimpleIntegerProperty();
    private IntegerProperty boardHeight = new SimpleIntegerProperty();
    private IntegerProperty numberOfBombs = new SimpleIntegerProperty();

    MinesweeperApplicationPane() {

        Label boardWidthLabel = new Label("Board width");
        TextField boardWidthField = new TextField();
        boardWidthField.textProperty().addListener((o, oldValue, newValue) -> this.getBoardWidth().setValue(Integer.parseInt(newValue)));
        boardWidthField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(8, 100, 8)));
        boardWidthField.setText("8");
        GridPane.setHgrow(boardWidthField, Priority.ALWAYS);

        Label boardHeightLabel = new Label("Board height");
        TextField boardHeightField = new TextField();
        boardHeightField.textProperty().addListener((o, oldValue, newValue) -> this.getBoardHeight().setValue(Integer.parseInt(newValue)));
        boardHeightField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(8, 100, 8)));
        boardHeightField.setText("8");
        GridPane.setHgrow(boardHeightField, Priority.ALWAYS);

        Label numberOfBombsLabel = new Label("Number of bombs");
        TextField numberOfBombsField = new TextField("1");
        numberOfBombsField.textProperty().addListener((o, oldValue, newValue) -> this.getNumberOfBombs().setValue(Integer.parseInt(newValue)));
        numberOfBombsField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(1, 100, 1)));
        numberOfBombsField.setText("10");
        GridPane.setHgrow(numberOfBombsField, Priority.ALWAYS);

        Button executeButton = new Button("Start new game");
        executeButton.setOnAction(event -> this.createBoard());
        executeButton.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(executeButton, Priority.ALWAYS);

        GridPane topPane = new GridPane();
        topPane.setHgap(4);
        topPane.setVgap(2);
        topPane.setPadding(new Insets(6, 6, 6, 6));
        topPane.add(boardWidthLabel, 0, 0, 1, 1);
        topPane.add(boardHeightLabel, 1, 0, 1, 1);
        topPane.add(numberOfBombsLabel, 2, 0, 1, 1);
        topPane.add(boardWidthField, 0, 1, 1, 1);
        topPane.add(boardHeightField, 1, 1, 1, 1);
        topPane.add(numberOfBombsField, 2, 1, 1, 1);
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
            boardBuilder.setHeight(this.getBoardHeight().getValue());
            boardBuilder.setWidth(this.getBoardWidth().getValue());
            boardBuilder.setNumberOfBombs(this.getNumberOfBombs().getValue());

            MinesweeperBoardPane boardPane = new MinesweeperBoardPane(boardBuilder.build());
            boardPane.setPadding(new Insets(4, 4, 4, 4));
            String boardTitle = "Minesweeper (" + this.getBoardWidth().getValue() + "x" + this.getBoardHeight().getValue() + " cells, " + this.getNumberOfBombs().getValue() + " bombs)";
            TitledPane boardPaneWrapper = new TitledPane(boardTitle, boardPane);
            boardPaneWrapper.setCollapsible(false);
            boardPaneWrapper.setMaxHeight(Double.MAX_VALUE);
            BorderPane.setMargin(boardPaneWrapper, new Insets(4, 0, 0, 0));

            Platform.runLater(() -> {
                this.setCenter(boardPaneWrapper);
            });

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

    private IntegerProperty getBoardWidth() {
        return this.boardWidth;
    }

    private IntegerProperty getBoardHeight() {
        return this.boardHeight;
    }

    private IntegerProperty getNumberOfBombs() {
        return this.numberOfBombs;
    }

}
