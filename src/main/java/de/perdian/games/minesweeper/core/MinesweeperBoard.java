package de.perdian.games.minesweeper.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The board in which the cells are located
 *
 * @author Christian Robert
 */

public class MinesweeperBoard {

    private int rows = 0;
    private int columns = 0;
    private int minesTotal = 0;
    private MinesweeperBoardState boardState = MinesweeperBoardState.RUNNING;
    private Map<MinesweeperCellPosition, MinesweeperCell> cells = null;
    private Map<MinesweeperCellPosition, MinesweeperCell> cellsRevealed = null;
    private Map<MinesweeperCellPosition, MinesweeperCell> cellsClosed = null;
    private List<MinesweeperCellRevelationListener> cellRevelationListeners = null;
    private List<MinesweeperBoardListener> boardListeners = null;

    MinesweeperBoard(int rows, int columns, int minesTotal, Map<MinesweeperCellPosition, MinesweeperCell> cells) {
        this.setRows(rows);
        this.setColumns(columns);
        this.setMinesTotal(minesTotal);
        this.setCells(cells);
        this.setCellsRevealed(new LinkedHashMap<>());
        this.setCellsClosed(new HashMap<>(cells));
        this.setCellRevelationListeners(new CopyOnWriteArrayList<>());
        this.setBoardListeners(new CopyOnWriteArrayList<>());
    }

    /**
     * Reveal the underside of a cell
     */
    public synchronized MinesweeperCellRevelation reveal(MinesweeperCellPosition cellPosition) {
        if (MinesweeperBoardState.COMPLETED_BOMB_HIT.equals(this.getBoardState())) {
            throw new IllegalStateException("Bomb has been hit, the game is over!");
        } else if (MinesweeperBoardState.COMPLETED_WON.equals(this.getBoardState())) {
            throw new IllegalStateException("All cells have been revealed, the game is over!");
        } else if (this.getCellsRevealed().containsKey(cellPosition)) {
            throw new IllegalStateException("Cell is already revealed");
        } else {
            return this.revealCell(this.getCellsClosed().get(cellPosition), MinesweeperCellRevelationType.MANUALLY);
        }
    }

    private MinesweeperCellRevelation revealCell(MinesweeperCell cell, MinesweeperCellRevelationType cellRevelationType) {
        if (cell == null) {
            throw new IllegalStateException("Cell cannot be found or has already been revealed");
        } else {

            this.getCellsClosed().remove(cell.getPosition());
            this.getBoardListeners().forEach(listener -> listener.numberOfClosedCellsUpdated(this.getCellsClosed().size()));
            this.getCellsRevealed().put(cell.getPosition(), cell);
            this.getBoardListeners().forEach(listener -> listener.numberOfRevealedCellsUpdated(this.getCellsRevealed().size()));

            if (cell.isMined()) {
                return this.revealMinedCell(cell, cellRevelationType);
            } else {
                return this.revealEmptyCell(cell, cellRevelationType);
            }

        }
    }

    private MinesweeperCellRevelation revealMinedCell(MinesweeperCell cell, MinesweeperCellRevelationType cellRevelationType) {

        MinesweeperCellRevelation cellRevelation = new MinesweeperCellRevelation(cell);
        cellRevelation.setMined(true);
        cellRevelation.setRevelationType(cellRevelationType);
        this.getCellRevelationListeners().forEach(listener -> listener.cellRevealed(cellRevelation));

        if (MinesweeperCellRevelationType.MANUALLY.equals(cellRevelationType)) {
            this.setBoardState(MinesweeperBoardState.COMPLETED_BOMB_HIT);
            for (MinesweeperCell unrevealedCell : new ArrayList<>(this.getCellsClosed().values())) {
                this.revealCell(unrevealedCell, MinesweeperCellRevelationType.GAME_ENDED);
            }
        }

        return cellRevelation;

    }

    private MinesweeperCellRevelation revealEmptyCell(MinesweeperCell cell, MinesweeperCellRevelationType cellRevelationType) {

        MinesweeperCellRevelation cellRevelation = new MinesweeperCellRevelation(cell);
        cellRevelation.setMined(false);
        cellRevelation.setNeighboringMines(this.computeNumberOfNeighboringMinesAtPosition(cell.getPosition()));
        cellRevelation.setRevelationType(cellRevelationType);
        this.getCellRevelationListeners().forEach(listener -> listener.cellRevealed(cellRevelation));

        if (this.getCellsClosed().size() == this.getMinesTotal()) {
            this.setBoardState(MinesweeperBoardState.COMPLETED_WON);
            for (MinesweeperCell unrevealedCell : new ArrayList<>(this.getCellsClosed().values())) {
                this.revealCell(unrevealedCell, MinesweeperCellRevelationType.GAME_ENDED);
            }
        } else {
            this.revealAdjacentCellsIfNoMines(cell);
        }

        return cellRevelation;

    }

    private void revealAdjacentCellsIfNoMines(MinesweeperCell cell) {
        if (this.computeNumberOfNeighboringMinesAtPosition(cell.getPosition()) == 0) {
            for (MinesweeperCellPosition adjacentPosition : this.computeAdjacentCellPositions(cell.getPosition(), false)) {
                MinesweeperCell adjacentCell = this.getCellsClosed().get(adjacentPosition);
                if (adjacentCell != null && !adjacentCell.isMined()) {
                    this.revealCell(adjacentCell, MinesweeperCellRevelationType.AUTOMATICALLY);
                }
            }
        }
    }

    private int computeNumberOfNeighboringMinesAtPosition(MinesweeperCellPosition sourcePosition) {
        int numberOfMinesNearby = 0;
        for (MinesweeperCellPosition lookupPosition : this.computeAdjacentCellPositions(sourcePosition, true)) {
            if (this.getCells().get(lookupPosition).isMined()) {
                numberOfMinesNearby++;
            }
        }
        return numberOfMinesNearby;
    }

    private List<MinesweeperCellPosition> computeAdjacentCellPositions(MinesweeperCellPosition sourcePosition, boolean includeCorners) {
        List<MinesweeperCellPosition> resultPositions = new LinkedList<>();
        int minX = Math.max(0, sourcePosition.getX() - 1);
        int maxX = Math.min(this.getColumns() - 1, sourcePosition.getX() + 1);
        int minY = Math.max(0, sourcePosition.getY() - 1);
        int maxY = Math.min(this.getRows() - 1, sourcePosition.getY() + 1);
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
     * Gets the number of rows on the board
     */
    public int getRows() {
        return this.rows;
    }
    private void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * Gets the number of columns on the board
     */
    public int getColumns() {
        return this.columns;
    }
    private void setColumns(int columns) {
        this.columns = columns;
    }

    /**
     * Gets the total number of mines placed on the board
     */
    public int getMinesTotal() {
        return this.minesTotal;
    }
    private void setMinesTotal(int minesTotal) {
        this.minesTotal = minesTotal;
    }

    /**
     * Gets the current state of the board
     */
    public MinesweeperBoardState getBoardState() {
        return this.boardState;
    }
    void setBoardState(MinesweeperBoardState boardState) {
        this.boardState = boardState;
        this.getBoardListeners().forEach(listener -> listener.boardStateUpdated(boardState));
    }

    Map<MinesweeperCellPosition, MinesweeperCell> getCells() {
        return this.cells;
    }
    private void setCells(Map<MinesweeperCellPosition, MinesweeperCell> cells) {
        this.cells = cells;
    }

    Map<MinesweeperCellPosition, MinesweeperCell> getCellsRevealed() {
        return this.cellsRevealed;
    }
    private void setCellsRevealed(Map<MinesweeperCellPosition, MinesweeperCell> cellsRevealed) {
        this.cellsRevealed = cellsRevealed;
    }

    Map<MinesweeperCellPosition, MinesweeperCell> getCellsClosed() {
        return this.cellsClosed;
    }
    private void setCellsClosed(Map<MinesweeperCellPosition, MinesweeperCell> cellsClosed) {
        this.cellsClosed = cellsClosed;
    }

    public boolean addCellRevelationListener(MinesweeperCellRevelationListener listener) {
        return this.getCellRevelationListeners().add(listener);
    }
    public boolean removeCellRevelationListener(MinesweeperCellRevelationListener listener) {
        return this.getCellRevelationListeners().remove(listener);
    }
    private List<MinesweeperCellRevelationListener> getCellRevelationListeners() {
        return this.cellRevelationListeners;
    }
    private void setCellRevelationListeners(List<MinesweeperCellRevelationListener> cellRevelationListeners) {
        this.cellRevelationListeners = cellRevelationListeners;
    }

    public boolean addBoardListener(MinesweeperBoardListener listener) {
        return this.getBoardListeners().add(listener);
    }
    public boolean removeBoardListener(MinesweeperBoardListener listener) {
        return this.getBoardListeners().remove(listener);
    }
    private List<MinesweeperBoardListener> getBoardListeners() {
        return this.boardListeners;
    }
    private void setBoardListeners(List<MinesweeperBoardListener> boardListeners) {
        this.boardListeners = boardListeners;
    }

}
