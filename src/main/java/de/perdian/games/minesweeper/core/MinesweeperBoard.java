package de.perdian.games.minesweeper.core;

import java.util.ArrayList;
import java.util.LinkedList;
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
    private int numberOfMines = 0;
    private MinesweeperCell[][] cellArray = null;
    private MinesweeperCellRevelation[][] cellRevelationArray = null;
    private Set<MinesweeperCell> hiddenCells = null;
    private List<MinesweeperCell> revealedCells = null;
    private List<MinesweeperCellRevelationListener> cellRevelationListeners = null;
    private MinesweeperBoardState boardState = MinesweeperBoardState.RUNNING;

    MinesweeperBoard(int height, int width, int numberOfMines) {
        this.setHeight(height);
        this.setWidth(width);
        this.setNumberOfMines(numberOfMines);
        this.setCellRevelationArray(new MinesweeperCellRevelation[height][width]);
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
        } else if (MinesweeperBoardState.COMPLETED_BOMB_HIT.equals(this.getBoardState())) {
            throw new IllegalStateException("Bomb has been hit, the game is over!");
        } else if (MinesweeperBoardState.COMPLETED_WON.equals(this.getBoardState())) {
            throw new IllegalStateException("All cells have been revealed, the game is over!");
        } else {
            return this.revealCell(this.getCellArray()[y][x]);
        }

    }

    MinesweeperCellRevelation revealCell(MinesweeperCell cell) {
        if (cell.isMined()) {
            return this.revealCellForHit(cell);
        } else {
            return this.revealCellForMiss(cell);
        }
    }

    private MinesweeperCellRevelation revealCellForHit(MinesweeperCell cell) {

        MinesweeperCellRevelation cellRevelation = new MinesweeperCellRevelation(this, cell);
        cellRevelation.setMined(true);
        cellRevelation.setRevelationType(MinesweeperCellRevelationType.MANUALLY);
        this.setBoardState(MinesweeperBoardState.COMPLETED_BOMB_HIT);
        this.getRevealedCells().add(cell);
        this.getHiddenCells().remove(cell);
        this.getCellRevelationListeners().forEach(listener -> listener.cellRevealed(cellRevelation));

        // The game is finished, so we reveal all currently unrevealed cells
        for (MinesweeperCell unrevealedCell : new ArrayList<>(this.getHiddenCells())) {
            MinesweeperCellRevelation additionalRevelation = new MinesweeperCellRevelation(this, unrevealedCell);
            additionalRevelation.setNeighboringMines(unrevealedCell.isMined() ? 0 : this.computeNumberOfNeighboringMinesAtPosition(unrevealedCell.getPosition()));
            additionalRevelation.setMined(unrevealedCell.isMined());
            additionalRevelation.setRevelationType(MinesweeperCellRevelationType.GAME_ENDED);
            this.getRevealedCells().add(unrevealedCell);
            this.getHiddenCells().remove(unrevealedCell);
            this.getCellRevelationListeners().forEach(listener -> listener.cellRevealed(additionalRevelation));
        }

        return cellRevelation;

    }

    private MinesweeperCellRevelation revealCellForMiss(MinesweeperCell cell) {

        MinesweeperCellRevelation cellRevelation = new MinesweeperCellRevelation(this, cell);
        cellRevelation.setMined(false);
        cellRevelation.setNeighboringMines(this.computeNumberOfNeighboringMinesAtPosition(cell.getPosition()));
        cellRevelation.setRevelationType(MinesweeperCellRevelationType.MANUALLY);
        this.getRevealedCells().add(cell);
        this.getHiddenCells().remove(cell);
        this.setBoardState(this.getHiddenCells().size() == this.getNumberOfMines() ? MinesweeperBoardState.COMPLETED_WON : MinesweeperBoardState.RUNNING);
        this.getCellRevelationListeners().forEach(listener -> listener.cellRevealed(cellRevelation));

        if (MinesweeperBoardState.RUNNING.equals(this.getBoardState())) {

            // Reveal all adjacent cells that have a nearby bomb count of 0
            this.revealAdjacentCellsIfNoMines(cell);

        } else {

            // Reveal all remaining cells as we're done!
            for (MinesweeperCell remainingCell : new ArrayList<>(this.getHiddenCells())) {
                MinesweeperCellRevelation remainingRevelation = new MinesweeperCellRevelation(this, remainingCell);
                remainingRevelation.setMined(true);
                remainingRevelation.setRevelationType(MinesweeperCellRevelationType.GAME_ENDED);
                this.getRevealedCells().add(remainingCell);
                this.getHiddenCells().add(remainingCell);
                this.getCellRevelationListeners().forEach(listener -> listener.cellRevealed(remainingRevelation));
            }

        }

        return cellRevelation;

    }

    private void revealAdjacentCellsIfNoMines(MinesweeperCell cell) {
        if (this.computeNumberOfNeighboringMinesAtPosition(cell.getPosition()) == 0) {
            for (MinesweeperCellPosition adjacentPosition : this.computeAdjacentCellPositions(cell.getPosition(), false)) {
                MinesweeperCell adjacentCell = this.getCellArray()[adjacentPosition.getY()][adjacentPosition.getX()];
                if (!adjacentCell.isMined() && this.getHiddenCells().contains(adjacentCell)) {
                    int autoRevelationMinesNearby = this.computeNumberOfNeighboringMinesAtPosition(adjacentPosition);
                    MinesweeperCellRevelation autoRevelation = new MinesweeperCellRevelation(this, adjacentCell);
                    autoRevelation.setMined(false);
                    autoRevelation.setNeighboringMines(autoRevelationMinesNearby);
                    autoRevelation.setRevelationType(MinesweeperCellRevelationType.AUTOMATICALLY);
                    this.getRevealedCells().add(adjacentCell);
                    this.getHiddenCells().remove(adjacentCell);
                    this.setBoardState(this.getHiddenCells().isEmpty() ? MinesweeperBoardState.COMPLETED_WON : MinesweeperBoardState.RUNNING);
                    this.getCellRevelationListeners().forEach(listener -> listener.cellRevealed(autoRevelation));
                    if (autoRevelationMinesNearby == 0) {
                        this.revealAdjacentCellsIfNoMines(adjacentCell);
                    }
                }
            }
        }
    }

    private int computeNumberOfNeighboringMinesAtPosition(MinesweeperCellPosition sourcePosition) {
        int numberOfMinesNearby = 0;
        for (MinesweeperCellPosition lookupPosition : this.computeAdjacentCellPositions(sourcePosition, true)) {
            if (this.getCellArray()[lookupPosition.getY()][lookupPosition.getX()].isMined()) {
                numberOfMinesNearby++;
            }
        }
        return numberOfMinesNearby;
    }

    private List<MinesweeperCellPosition> computeAdjacentCellPositions(MinesweeperCellPosition sourcePosition, boolean includeCorners) {
        List<MinesweeperCellPosition> resultPositions = new LinkedList<>();
        int minX = Math.max(0, sourcePosition.getX() - 1);
        int maxX = Math.min(this.getWidth() - 1, sourcePosition.getX() + 1);
        int minY = Math.max(0, sourcePosition.getY() - 1);
        int maxY = Math.min(this.getHeight() - 1, sourcePosition.getY() + 1);
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (y != sourcePosition.getY() || x != sourcePosition.getX()) {
                    if (includeCorners || y == sourcePosition.getY() || x == sourcePosition.getX())  {
                        resultPositions.add(new MinesweeperCellPosition(y, x));
                    }
                }
            }
        }
        return resultPositions;
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
     * Gets the total number of mines on the board
     */
    public int getNumberOfMines() {
        return this.numberOfMines;
    }
    private void setNumberOfMines(int numberOfMines) {
        this.numberOfMines = numberOfMines;
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
     * Gets the current state of the board
     */
    public MinesweeperBoardState getBoardState() {
        return this.boardState;
    }
    void setBoardState(MinesweeperBoardState boardState) {
        this.boardState = boardState;
    }

    MinesweeperCellRevelation[][] getCellRevelationArray() {
        return this.cellRevelationArray;
    }
    private void setCellRevelationArray(MinesweeperCellRevelation[][] cellRevelationArray) {
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
