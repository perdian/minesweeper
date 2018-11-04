package de.perdian.games.minesweeper.core;

public interface MinesweeperBoardListener {

    default void numberOfRevealedCellsUpdated(int newNumberOfRevealedCells) {
    }

    default void numberOfClosedCellsUpdated(int newNumberOfClosedCells) {
    }

    default void boardStateUpdated(MinesweeperBoardState newBoardState) {
    }

}
