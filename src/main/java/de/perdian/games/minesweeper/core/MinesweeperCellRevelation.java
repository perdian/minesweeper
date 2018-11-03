package de.perdian.games.minesweeper.core;

import java.util.List;

public class MinesweeperCellRevelation {

    private MinesweeperBoardState boardState = null;
    private MinesweeperCell touchedCell = null;
    private List<MinesweeperCell> automaticallyRevealedCells = null;
    private int neighboringBombs = 0;

    MinesweeperCellRevelation() {
    }

    public MinesweeperBoardState getBoardState() {
        return this.boardState;
    }
    void setBoardState(MinesweeperBoardState boardState) {
        this.boardState = boardState;
    }

    public MinesweeperCell getTouchedCell() {
        return this.touchedCell;
    }
    void setTouchedCell(MinesweeperCell touchedCell) {
        this.touchedCell = touchedCell;
    }

    public List<MinesweeperCell> getAutomaticallyRevealedCells() {
        return this.automaticallyRevealedCells;
    }
    void setAutomaticallyRevealedCells(List<MinesweeperCell> automaticallyRevealedCells) {
        this.automaticallyRevealedCells = automaticallyRevealedCells;
    }

    public int getNeighboringBombs() {
        return this.neighboringBombs;
    }
    void setNeighboringBombs(int neighboringBombs) {
        this.neighboringBombs = neighboringBombs;
    }

}
