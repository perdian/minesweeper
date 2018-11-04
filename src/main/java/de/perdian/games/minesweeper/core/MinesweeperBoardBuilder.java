package de.perdian.games.minesweeper.core;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinesweeperBoardBuilder {

    private static final Logger log = LoggerFactory.getLogger(MinesweeperBoardBuilder.class);

    private int rows = 8;
    private int columns = 8;
    private int mines = 10;
    private Random random = new SecureRandom();

    public MinesweeperBoard build() {
        if (this.getRows() < 8 || this.getColumns() < 8) {
            throw new IllegalArgumentException("Minesweeper board must have at least 8x8 cells");
        } else if (this.getMines() >= (this.getRows() * this.getColumns())) {
            throw new IllegalArgumentException("Minesweeper board must have at least one cell without mines");
        } else {

            log.debug("Creating board with {} rows and {} columns containing {} mines", this.getRows(), this.getColumns(), this.getMines());
            List<Boolean> mineBooleans = IntStream.range(0, this.getRows() * this.getColumns()).mapToObj(value -> value < this.getMines()).collect(Collectors.toList());
            Collections.shuffle(mineBooleans, this.getRandom());

            Map<MinesweeperCellPosition, MinesweeperCell> cells = new LinkedHashMap<>();
            UUID ownerId = UUID.randomUUID();
            for (int row=0; row < this.getRows(); row++) {
                for (int column=0; column < this.getColumns(); column++) {
                    MinesweeperCellPosition cellPosition = new MinesweeperCellPosition(row, column);
                    MinesweeperCell cell = new MinesweeperCell(ownerId, cellPosition, mineBooleans.get((row * this.getColumns()) + column));
                    cells.put(cellPosition, cell);
                }
            }

            log.info("Created board with {} rows and {} columns containing {} mines", this.getRows(), this.getColumns(), this.getMines());
            return new MinesweeperBoard(this.getRows(), this.getColumns(), this.getMines(), cells);

        }
    }

    public int getRows() {
        return this.rows;
    }
    public void setRows(int rows) {
        if (rows<= 0) {
            throw new IllegalArgumentException("Property 'rows' must be larger than zero");
        } else {
            this.rows = rows;
        }
    }

    public int getColumns() {
        return this.columns;
    }
    public void setColumns(int columns) {
        if (columns<= 0) {
            throw new IllegalArgumentException("Property 'columns' must be larger than zero");
        } else {
            this.columns = columns;
        }
    }

    public int getMines() {
        return this.mines;
    }
    public void setMines(int mines) {
        if (mines <= 0) {
            throw new IllegalArgumentException("Property 'mines' must be larger than zero");
        } else {
            this.mines = mines;
        }
    }

    public Random getRandom() {
        return this.random;
    }
    public void setRandom(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Property 'random' must not be null");
        } else {
            this.random = random;
        }
    }

}
