package de.perdian.games.minesweeper.core;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinesweeperBoardBuilder {

    private static final Logger log = LoggerFactory.getLogger(MinesweeperBoardBuilder.class);

    private int height = 8;
    private int width = 8;
    private int numberOfBombs = 10;
    private Random random = new SecureRandom();

    public MinesweeperBoard build() {
        if (this.getWidth() < 8 || this.getHeight() < 8) {
            throw new IllegalArgumentException("Minesweeper board must have at least 8x8 cells");
        } else if (this.getNumberOfBombs() >= (this.getWidth() * this.getHeight())) {
            throw new IllegalArgumentException("Minesweeper board must have at least one cell without bombs");
        } else {

            log.debug("Creating board with {} columns and {} rows containing {} bombs", this.getWidth(), this.getHeight(), this.getNumberOfBombs());
            List<Boolean> bombBooleans = IntStream.range(0, this.getWidth() * this.getHeight()).mapToObj(value -> value < this.getNumberOfBombs()).collect(Collectors.toList());
            Collections.shuffle(bombBooleans, this.getRandom());

            Set<MinesweeperCell> cells = new HashSet<>();
            MinesweeperCell[][] cellArray = new MinesweeperCell[this.getHeight()][this.getWidth()];
            UUID ownerId = UUID.randomUUID();
            for (int row=0; row < this.getHeight(); row++) {
                for (int column=0; column < this.getWidth(); column++) {
                    MinesweeperCellPosition cellPosition = new MinesweeperCellPosition(row, column);
                    MinesweeperCell cell = new MinesweeperCell(ownerId, cellPosition, bombBooleans.get((row * this.getWidth()) + column));
                    cellArray[row][column] = cell;
                    cells.add(cell);
                }
            }

            log.info("Created board with {} columns and {} rows containing {} bombs", this.getWidth(), this.getHeight(), this.getNumberOfBombs());
            MinesweeperBoard minesweeperBoard = new MinesweeperBoard(this.getHeight(), this.getWidth(), this.getNumberOfBombs());
            minesweeperBoard.setCellArray(cellArray);
            minesweeperBoard.setHiddenCells(cells);
            minesweeperBoard.setRevealedCells(new ArrayList<>());
            return minesweeperBoard;

        }
    }

    public int getHeight() {
        return this.height;
    }
    public void setHeight(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Property 'height' must be larger than zero");
        } else {
            this.height = height;
        }
    }

    public int getWidth() {
        return this.width;
    }
    public void setWidth(int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Property 'width' must be larger than zero");
        } else {
            this.width = width;
        }
    }

    public int getNumberOfBombs() {
        return this.numberOfBombs;
    }
    public void setNumberOfBombs(int numberOfBombs) {
        if (numberOfBombs <= 0) {
            throw new IllegalArgumentException("Property 'numberOfBombs' must be larger than zero");
        } else {
            this.numberOfBombs = numberOfBombs;
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
