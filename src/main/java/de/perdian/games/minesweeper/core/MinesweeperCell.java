package de.perdian.games.minesweeper.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A single cell
 *
 * @author Christian Robert
 */

public class MinesweeperCell {

    private int y = 0;
    private int x = 0;
    private boolean bomb = false;

    MinesweeperCell(int y, int x, boolean bomb) {
        this.setY(y);
        this.setX(x);
        this.setBomb(bomb);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof MinesweeperCell) {
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(this.getX(), ((MinesweeperCell)that).getX());
            equalsBuilder.append(this.getY(), ((MinesweeperCell)that).getY());
            return equalsBuilder.isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.getX());
        hashCodeBuilder.append(this.getY());
        return hashCodeBuilder.toHashCode();
    }

    public int getY() {
        return this.y;
    }
    private void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }
    private void setX(int x) {
        this.x = x;
    }

    boolean isBomb() {
        return this.bomb;
    }
    private void setBomb(boolean bomb) {
        this.bomb = bomb;
    }

}
