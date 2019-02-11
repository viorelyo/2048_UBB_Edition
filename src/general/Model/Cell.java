package general.Model;

public class Cell {
    private int value;
    private boolean merged;

    /**
     * Constructor for a cell
     * @param val
     */
    public Cell(int val) {
        value = val;
    }

    /**
     * Getter for merged state of Cell
     * @return
     */
    public boolean getMerged() {
        return merged;
    }

    /**
     * Setter for merged
     * @param merged
     */
    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    /**
     * Getter for value of the Cell
     * @return
     */
    public int getValue() {
        return value;
    }


    /**
     * Check if the Cell can be merged with another
     * @param other
     * @return
     */
    public boolean canMergeWith(Cell other) {
        return other != null && !this.merged && !other.getMerged() && this.value == other.getValue();
    }

    /**
     * Merge 2 Cells
     * @param other
     * @return value if the cells are merged
     * -1 otherwise
     */
    public int mergeWith(Cell other) {
        if(canMergeWith(other)) {
            this.value *= 2;
            merged = true;
            return value;
        }
        return -1;
    }
}
