public class LineCounter {
    /**
     * Class Attributes
     */
    private int nbLines;

    /**
     * Class constructor
     */
    LineCounter () {
        nbLines = 0;
    }

    /**
     * Gets number of lines.
     */
    public synchronized int getNbLines() {
        return nbLines;
    }

    /**
     * Increments number of lines by n.
     */
    public synchronized void incrementByNbLines(int n) {
        nbLines = nbLines + n;
    }
}