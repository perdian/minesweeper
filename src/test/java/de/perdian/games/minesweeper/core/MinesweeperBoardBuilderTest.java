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
            boardBuilder.setWidth(20);
            boardBuilder.setHeight(10);
            boardBuilder.setNumberOfBombs(50);

            MinesweeperBoard board = boardBuilder.build();
            Assertions.assertEquals(20, board.getWidth());
            Assertions.assertEquals(10, board.getHeight());

            int countedNumberOfBombs = 0;
            for (int row = 0; row < 10; row++) {
                for (int column = 0; column < 20; column++) {
                    MinesweeperCell cell = board.getCellArray()[row][column];
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
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setWidth(0));
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setWidth(-1));
            }

            @Test
            public void heightToSmall() {
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setHeight(0));
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setHeight(-1));
            }

            @Test
            public void numberOfBombsToSmall() {
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setNumberOfBombs(0));
                Assertions.assertThrows(IllegalArgumentException.class, () -> new MinesweeperBoardBuilder().setNumberOfBombs(-1));
            }

        }

        @Nested
        class Building {

            @Test
            public void widthToSmall() {
                MinesweeperBoardBuilder boardBuilder = new MinesweeperBoardBuilder();
                boardBuilder.setWidth(7);
                boardBuilder.setHeight(10);
                boardBuilder.setNumberOfBombs(50);
                Assertions.assertThrows(IllegalArgumentException.class, () -> boardBuilder.build());
            }

            @Test
            public void heightToSmall() {
                MinesweeperBoardBuilder boardBuilder = new MinesweeperBoardBuilder();
                boardBuilder.setWidth(10);
                boardBuilder.setHeight(7);
                boardBuilder.setNumberOfBombs(50);
                Assertions.assertThrows(IllegalArgumentException.class, () -> boardBuilder.build());
            }

            @Test
            public void numberOfBombsTooLarge() {
                MinesweeperBoardBuilder boardBuilder = new MinesweeperBoardBuilder();
                boardBuilder.setWidth(10);
                boardBuilder.setHeight(10);
                boardBuilder.setNumberOfBombs(100);
                Assertions.assertThrows(IllegalArgumentException.class, () -> boardBuilder.build());
            }

        }

    }

}
