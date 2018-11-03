package de.perdian.games.minesweeper.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A single cell
 *
 * @author Christian Robert
 */

public class MinesweeperCell {

    private Object owner = null;
    private MinesweeperCellPosition position = null;
    private boolean mined = false;

    MinesweeperCell(Object owner, MinesweeperCellPosition position, boolean mined) {
        this.setOwner(owner);
        this.setPosition(position);
        this.setMined(mined);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof MinesweeperCell) {
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(this.getOwner(), ((MinesweeperCell)that).getOwner());
            equalsBuilder.append(this.getPosition(), ((MinesweeperCell)that).getPosition());
            return equalsBuilder.isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.getOwner());
        hashCodeBuilder.append(this.getPosition());
        return hashCodeBuilder.toHashCode();
    }

    private Object getOwner() {
        return this.owner;
    }
    private void setOwner(Object owner) {
        this.owner = owner;
    }

    public MinesweeperCellPosition getPosition() {
        return this.position;
    }
    private void setPosition(MinesweeperCellPosition position) {
        this.position = position;
    }

    boolean isMined() {
        return this.mined;
    }
    private void setMined(boolean mined) {
        this.mined = mined;
    }

}
