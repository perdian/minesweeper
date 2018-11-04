package de.perdian.games.minesweeper.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MinesweeperBoardBuilderTest {

    @Nested
    class ValidInputParameters {

        @Test
        public void build() {

            MinesweeperBoardBuilder boardBuilder = new MinesweeperBoardBuilder();
            boardBuilder.setRows(10);
            boardBuilder.setColumns(20);
            boardBuilder.setMines(50);

            MinesweeperBoard board = boardBuilder.build();
            Assertions.assertEquals(10, board.getRows());
            Assertions.assertEquals(20, board.getColumns());

            int countedNumberOfBombs = 0;
            for (int row = 0; row < 10; row++) {
                for (int column = 0; column < 20; column++) {
                    MinesweeperCell cell = board.getCells().get(new MinesweeperCellPosition(row, column));
                    if (cell.isMined()) {
                        countedNumberOfBombs++;
                    }
                    Assertions.assertEquals(row, cell.getPosition().getY());
                    Assertions.assertEquals(column, cell.getPosition().getX());
                }
            }
            Assertions.assertEquals(50, countedNumberOfBombs);

        }

    }

    @Nested
    class InvalidInputParameters {

        @Nested
        class Setters {

            @Test
            public void widthToSmall() {
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setColumns(0));
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setColumns(-1));
            }

            @Test
            public void heightToSmall() {
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setRows(0));
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setRows(-1));
            }

            @Test
            public void numberOfBombsToSmall() {
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setMines(0));
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setMines(-1));
            }

        }

        @Nested
        class Building {

            @Test
            public void widthToSmall() {
                MinesweeperBoardBuilder boardBuilder = new MinesweeperBoardBuilder();
                boardBuilder.setColumns(7);
                boardBuilder.setRows(10);
                boardBuilder.setMines(50);
                Assertions.assertThrows(IllegalArgumentException.class, () -> boardBuilder.build());
            }

            @Test
            public void heightToSmall() {
                MinesweeperBoardBuilder boardBuilder = new MinesweeperBoardBuilder();
                boardBuilder.setColumns(10);
                boardBuilder.setRows(7);
                boardBuilder.setMines(50);
                Assertions.assertThrows(IllegalArgumentException.class, () -> boardBuilder.build());
            }

            @Test
            public void numberOfBombsTooLarge() {
                MinesweeperBoardBuilder boardBuilder = new MinesweeperBoardBuilder();
                boardBuilder.setColumns(10);
                boardBuilder.setRows(10);
                boardBuilder.setMines(100);
                Assertions.assertThrows(IllegalArgumentException.class, () -> boardBuilder.build());
            }

        }

    }

}
