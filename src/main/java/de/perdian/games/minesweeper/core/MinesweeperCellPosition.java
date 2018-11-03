package de.perdian.games.minesweeper.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MinesweeperCellPosition {

    private int y = 0;
    private int x = 0;

    public MinesweeperCellPosition(int y, int x) {
        this.setY(y);
        this.setX(x);
    }

    @Override
    public String toString() {
        return "Y:" + this.getY() + ", X:" + this.getX();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof MinesweeperCellPosition) {
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(this.getY(), ((MinesweeperCellPosition)that).getY());
            equalsBuilder.append(this.getX(), ((MinesweeperCellPosition)that).getX());
            return equalsBuilder.isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.getY());
        hashCodeBuilder.append(this.getX());
        return hashCodeBuilder.toHashCode();
    }

    public int getY() {
        return this.y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }
    public void setX(int x) {
        this.x = x;
    }

}
