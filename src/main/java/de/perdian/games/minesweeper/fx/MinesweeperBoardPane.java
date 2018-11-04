package de.perdian.games.minesweeper.fx;

import de.perdian.games.minesweeper.core.MinesweeperBoard;
import de.perdian.games.minesweeper.core.MinesweeperBoardState;
import de.perdian.games.minesweeper.core.MinesweeperCellPosition;
import de.perdian.games.minesweeper.core.MinesweeperCellRevelation;
import de.perdian.games.minesweeper.core.MinesweeperCellRevelationListener;
import de.perdian.games.minesweeper.core.MinesweeperCellRevelationType;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

class MinesweeperBoardPane extends BorderPane implements MinesweeperCellRevelationListener {

    private Button[][] buttons = null;
    private boolean userInputActive = true;
    private boolean gameFinished = false;
    private Object userInputMonitor = new Object();

    MinesweeperBoardPane(MinesweeperBoard board) {

        board.addCellRevelationListener(this);

        GridPane cellsPane = new GridPane();
        Button[][] buttonArray = new Button[board.getRows()][board.getColumns()];
        for (int y = 0; y < board.getRows(); y++) {
            for (int x = 0; x < board.getColumns(); x++) {
                MinesweeperCellPosition cellPosition = new MinesweeperCellPosition(y, x);
                Button cellButton = new Button("?");
                cellButton.setFocusTraversable(false);
                cellButton.setMinSize(25, 25);
                cellButton.setPrefSize(35, 35);
                cellButton.setOnAction(event -> this.revealCell(cellButton, board, cellPosition));
                cellsPane.add(cellButton, x, y, 1, 1);
                buttonArray[y][x] = cellButton;
            }
        }
        cellsPane.setAlignment(Pos.CENTER);
        this.setButtons(buttonArray);

        ScrollPane cellsPaneScroller = new ScrollPane(cellsPane);
        cellsPaneScroller.setFitToWidth(true);
        cellsPaneScroller.setFitToHeight(true);
        this.setCenter(cellsPaneScroller);

    }

    private void revealCell(Button sourceButton, MinesweeperBoard board, MinesweeperCellPosition cellPosition) {
        synchronized (this.getUserInputMonitor()) {
            if (this.isUserInputActive()) {
                this.setUserInputActive(false);
                sourceButton.setDisable(true);
                new Thread(() -> {
                    try {
                        board.reveal(cellPosition);
                        this.setGameFinished(!MinesweeperBoardState.RUNNING.equals(board.getBoardState()));
                    } finally {
                        synchronized (this.getUserInputMonitor()) {
                            this.setUserInputActive(!this.isGameFinished());
                        }
                    }
                }).start();
            }
        }
    }

    @Override
    public void cellRevealed(MinesweeperCellRevelation cellRevelation) {
        Platform.runLater(() -> {
            Button buttonForCell = this.getButtons()[cellRevelation.getRevealedCell().getPosition().getY()][cellRevelation.getRevealedCell().getPosition().getX()];
            buttonForCell.setDisable(true);
            if (cellRevelation.isMined()) {
                buttonForCell.setText("X");
                if (MinesweeperCellRevelationType.MANUALLY.equals(cellRevelation.getRevelationType())) {
                    buttonForCell.setStyle("-fx-background-color: #FF0000");
                } else {
                    buttonForCell.setStyle("-fx-font-weight: bold");
                }
            } else {
                buttonForCell.setText(cellRevelation.getNeighboringMines() == 0 ? "" : String.valueOf(cellRevelation.getNeighboringMines()));
            }
        });
    }

    private Button[][] getButtons() {
        return this.buttons;
    }
    private void setButtons(Button[][] buttons) {
        this.buttons = buttons;
    }

    private boolean isUserInputActive() {
        return this.userInputActive;
    }
    private void setUserInputActive(boolean userInputActive) {
        this.userInputActive = userInputActive;
    }

    private boolean isGameFinished() {
        return this.gameFinished;
    }
    private void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    private Object getUserInputMonitor() {
        return this.userInputMonitor;
    }

}
