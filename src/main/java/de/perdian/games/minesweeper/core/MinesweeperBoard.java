package de.perdian.games.minesweeper.core;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The board in which the cells are located
 *
 * @author Christian Robert
 */

public class MinesweeperBoard {

    private int height = 0;
    private int width = 0;
    private MinesweeperCell[][] cellArray = null;
    private MinesweeperCellRevelation[][] cellRevelationArray = null;
    private Set<MinesweeperCell> hiddenCells = null;
    private List<MinesweeperCell> revealedCells = null;
    private MinesweeperCell hitCell = null;
    private List<MinesweeperCellRevelationListener> cellRevelationListeners = null;

    MinesweeperBoard(int height, int width) {
        this.setHeight(height);
        this.setWidth(width);
        this.setCellRevelationListeners(new CopyOnWriteArrayList<>());
    }

    /**
     * Reveal the underside of a cell
     *
     * @return
     *      the revelation
     */
    public synchronized MinesweeperCellRevelation reveal(int x, int y) {

        MinesweeperCellRevelation[] revelationRow = this.getCellRevelationArray()[y];
        if (revelationRow == null) {
            revelationRow = new MinesweeperCellRevelation[this.getWidth()];
            this.getCellRevelationArray()[y] = revelationRow;
        }
        MinesweeperCellRevelation revelationCell = revelationRow[x];

        if (revelationCell != null) {
            return revelationCell;
        } else if (this.getHitCell() != null) {
            throw new IllegalStateException("Bomb has been hit, the game is over!");
        } else if (this.getHiddenCells().isEmpty()) {
            throw new IllegalStateException("All cells have been revealed, the game is over!");
        } else {
            return this.revealCell(this.getCellArray()[y][x]);
        }

    }

    MinesweeperCellRevelation revealCell(MinesweeperCell cell) {
        if (cell.isBomb()) {
            return this.revealCellForHit(cell);
        } else {
            return this.revealCellForMiss(cell);
        }
    }

    private MinesweeperCellRevelation revealCellForHit(MinesweeperCell cell) {
        throw new UnsupportedOperationException();
    }

    private MinesweeperCellRevelation revealCellForMiss(MinesweeperCell cell) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the height of the board (the number of rows)
     */
    public int getHeight() {
        return this.height;
    }
    private void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the width of the board (the number of columns)
     */
    public int getWidth() {
        return this.width;
    }
    private void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets all available cells
     */
    public MinesweeperCell[][] getCellArray() {
        return this.cellArray;
    }
    void setCellArray(MinesweeperCell[][] cellArray) {
        this.cellArray = cellArray;
    }

    /**
     * Gets all the cells that have not yet been revealed
     */
    public Set<MinesweeperCell> getHiddenCells() {
        return this.hiddenCells;
    }
    void setHiddenCells(Set<MinesweeperCell> hiddenCells) {
        this.hiddenCells = hiddenCells;
    }

    /**
     * Gets all the cells that have been revealed so far (either by manually revealing the cell or by automatically
     * revealing it)
     */
    public List<MinesweeperCell> getRevealedCells() {
        return this.revealedCells;
    }
    void setRevealedCells(List<MinesweeperCell> revealedCells) {
        this.revealedCells = revealedCells;
    }

    /**
     * Gets the cell that was hit by the user and contains a bomb
     */
    public MinesweeperCell getHitCell() {
        return this.hitCell;
    }
    void setHitCell(MinesweeperCell hitCell) {
        this.hitCell = hitCell;
    }

    MinesweeperCellRevelation[][] getCellRevelationArray() {
        return this.cellRevelationArray;
    }
    void setCellRevelationArray(MinesweeperCellRevelation[][] cellRevelationArray) {
        this.cellRevelationArray = cellRevelationArray;
    }

    public boolean addCellRevelationListener(MinesweeperCellRevelationListener listener) {
        return this.getCellRevelationListeners().add(listener);
    }
    public boolean removeCellRevelationListener(MinesweeperCellRevelationListener listener) {
        return this.getCellRevelationListeners().remove(listener);
    }
    public List<MinesweeperCellRevelationListener> getCellRevelationListeners() {
        return this.cellRevelationListeners;
    }
    private void setCellRevelationListeners(List<MinesweeperCellRevelationListener> cellRevelationListeners) {
        this.cellRevelationListeners = cellRevelationListeners;
    }

}
