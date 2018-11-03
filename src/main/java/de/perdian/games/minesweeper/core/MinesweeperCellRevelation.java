package de.perdian.games.minesweeper.core;

public class MinesweeperCellRevelation {

    private MinesweeperBoard board = null;
    private MinesweeperCell revealedCell = null;
    private MinesweeperCellRevelationType revelationType = null;
    private boolean mined = false;
    private int neighboringMines = 0;

    MinesweeperCellRevelation(MinesweeperBoard board, MinesweeperCell revealedCell) {
        this.setBoard(board);
        this.setRevealedCell(revealedCell);
    }

    public MinesweeperBoard getBoard() {
        return this.board;
    }
    private void setBoard(MinesweeperBoard board) {
        this.board = board;
    }

    public MinesweeperCell getRevealedCell() {
        return this.revealedCell;
    }
    private void setRevealedCell(MinesweeperCell revealedCell) {
        this.revealedCell = revealedCell;
    }

    public MinesweeperCellRevelationType getRevelationType() {
        return this.revelationType;
    }
    void setRevelationType(MinesweeperCellRevelationType revelationType) {
        this.revelationType = revelationType;
    }

    public boolean isMined() {
        return this.mined;
    }
    void setMined(boolean mined) {
        this.mined = mined;
    }

    public int getNeighboringMines() {
        return this.neighboringMines;
    }
    void setNeighboringMines(int neighboringMines) {
        this.neighboringMines = neighboringMines;
    }

}
