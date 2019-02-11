package general.Beans;

import general.Model.Cell;
import general.Model.GameState;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Random;


@ManagedBean(name = "gbean")
@SessionScoped
public class GameBean {
    private int size = 4;
    private Cell[][] cells;

    private Random rand = new Random();
    private boolean tryForNewMoves;

    private int highest;
    private int target;
    private GameState gamestate;

    /**
     * Constructor for Game Bean
     */
    public GameBean() {
        this.target = 8;
        this.tryForNewMoves = false;
        launchGame();
    }

    /**
     * Getter for image
     * @param cell
     * @return
     */
    public String translateToImg(Cell cell) {
        String img = null;
        if (cell == null)
            img = "empty.jpg";
        else {
            switch (cell.getValue()) {
                case 2:
                    return "mic_b.jpg";
                case 4:
                    return "tomas_a.jpg";
                case 8:
                    return "rusu_c.jpg";
                case 16:
                    return "modoi_c.jpg";
                case 32:
                    return "trambitas_g.jpg";
                case 64:
                    return "sanda_dr.jpg";
                case 128:
                    return "dragos_r.jpg";
                case 256:
                    return "cristea_d.jpg";
                case 512:
                    return "lisei_h.jpg";
                case 1024:
                    return "breckner_b.jpg";
                case 2048:
                    return "sacarea_c.jpg";
            }
        }
        return img;
    }

    /**
     * Convert Highest score to corresponding Name
     * @return
     */
    public String getScoreConverted() {
        switch (highest) {
            case 2:
                return "Mic";
            case 4:
                return "Tomas";
            case 8:
                return "Rusu";
            case 16:
                return "Modoi";
            case 32:
                return "Trambitas";
            case 64:
                return "Avram";
            case 128:
                return "Dragos";
            case 256:
                return "Cristea";
            case 512:
                return "Lisei";
            case 1024:
                return "Breckner";
            case 2048:
                return "Sacarea";
            }
        return "student";
    }

    /**
     * start game function
     * initialize the first gameBoard state
     * used for restarting game
     */
    public void launchGame() {
        cells = new Cell[size][size];
        this.highest = 2;
        gamestate = GameState.running;
        addRandomCell();
        addRandomCell();
    }

    /**
     * Check if game is not over
     * @return
     */
    public String getGameOver() {
        if (gamestate == GameState.over)
            return "true";
        return "false";
    }

    /**
     * Check if won the game
     * @return
     */
    public String getGameWon() {
        if (gamestate == GameState.won)
            return "true";
        return "false";
    }

    /**
     * Getter for Cells
     * @return
     */
    public Cell[][] getCells() {
        return cells;
    }

    /**
     * Up key pressed event Handler
     */
    public void UPkeypress () {
        //System.out.println( "UP" );
        moveUp();
    }

    /**
     * Down key pressed event Handler
     */
    public void DOWNkeypress () {
        //System.out.println( "DOWN" );
        moveDown();
    }

    /**
     * Left key pressed event Handler
     */
    public void LEFTkeypress () {
        //System.out.println( "LEFT" );
        moveLeft();
    }

    /**
     * Right key pressed event Handler
     */
    public void RIGHTkeypress () {
        //System.out.println( "RIGHT" );
        moveRight();
    }

    /**
     * Random generate Cells(2/4) on gameBoard
     */
    private void addRandomCell() {
        int pos = rand.nextInt(size * size);
        int row, col;

        do {
            pos = (pos + 1) % (size * size);
            row = pos / size;
            col = pos % size;
        } while (cells[row][col] != null);

        //chance to get a 4 or 2 randomly
        int val = rand.nextInt(10) == 0 ? 4 : 2;
        cells[row][col] = new Cell(val);
    }


    /**
     * Move all Cells on the gameBoard
     * @param cntFrom - enumerate Cells from a given side(from last / from first)
     * @param yInc - move down / move up
     * @param xInc - move left / move right
     * @return
     */
    private boolean move(int cntFrom, int yInc, int xInc) {
        boolean moved = false;

        for (int i = 0; i < size * size; i++) {
            int j = Math.abs(cntFrom - i);

            int row = j / size;
            int col = j % size;

            if (cells[row][col] == null)
                continue;

            int nextR = row + yInc;
            int nextC = col + xInc;

            while (nextR >= 0 && nextR < size && nextC >= 0 && nextC < size) {
                Cell next = cells[nextR][nextC];
                Cell crt = cells[row][col];

                if (next == null) {
                    //used in checking for any more moves
                    if (tryForNewMoves)
                        return true;

                    cells[nextR][nextC] = crt;
                    cells[row][col] = null;
                    row = nextR;
                    col = nextC;
                    nextR += yInc;
                    nextC += xInc;
                    moved = true;
                } else if (next.canMergeWith(crt)) {
                    //used in checking for any more moves
                    if (tryForNewMoves)
                        return true;

                    int value = next.mergeWith(crt);
                    //update the highest value achieved
                    if (value > highest)
                        highest = value;

                    cells[row][col] = null;
                    moved = true;
                    break;
                }
                else
                    break;
            }
        }

        //check for game state depending on current state of game board
        if (moved) {
            if (highest < target) {
                clearMerged();
                addRandomCell();
                if (!movesAvailable())
                    gamestate = GameState.over;
            } else if (highest == target)
                gamestate = GameState.won;
        }

        return moved;
    }


    /**
     * Check for available moves
     * @return true if exist
     * false otherwise
     */
    private boolean movesAvailable() {
        tryForNewMoves = true;
        //try to move each Cell to a new position in each direction
        boolean existMoves = moveUp() || moveDown() || moveLeft() || moveRight();
        tryForNewMoves = false;
        return existMoves;
    }


    /**
     * Mark all Cells as not merged
     * Prepare for the next move
     */
    private void clearMerged() {
        for (Cell[] row : cells) {
            for (Cell cell : row)
                if (cell != null)
                    cell.setMerged(false);
        }
    }


    /**
     * Move the cell up
     * @return
     */
    boolean moveUp() {
        return move(0, -1, 0);
    }

    /**
     * Move the cell down
     * @return
     */
    boolean moveDown() {
        return move(size * size - 1, 1, 0);
    }

    /**
     * Move the cell left
     * @return
     */
    boolean moveLeft() {
        return move(0, 0, -1);
    }

    /**
     * Move the cell right
     * @return
     */
    boolean moveRight() {
        return move(size * size - 1, 0, 1);
    }
}
