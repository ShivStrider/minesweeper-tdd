package model;

import java.util.Random;

// Model: Handles all game logic, state, and data for Minesweeper.
// This class has no I/O dependencies, making it fully testable in isolation.
public class MinesweeperModel {

    // Enum representing the three possible game states
    public enum GameState {
        PLAYING, WON, LOST
    }

    // Grid dimensions and mine configuration
    private int rows;
    private int cols;
    private int mineCount;

    // Core game data structures
    private boolean[][] mines; // true if cell contains a mine
    private boolean[][] revealed; // true if cell has been revealed by player
    private boolean[][] flagged; // true if cell has been flagged by player
    private int[][] adjacentCounts; // number of mines surrounding each cell (-1 if cell is a mine)

    // Game state tracking
    private GameState gameState;
    private int tilesRevealed; // count of revealed non-mine tiles
    private int safeCells; // total non-mine cells (used for win condition)
    private Random random;

    // Constructor: Initializes the game board with given dimensions, mine count,
    // and Random instance
    // Accepts a Random object for dependency injection (enables deterministic
    // testing with seeded Random)
    public MinesweeperModel(int rows, int cols, int mineCount, Random random) {
        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;
        this.random = random;

        // Initialize all grid arrays
        this.mines = new boolean[rows][cols];
        this.revealed = new boolean[rows][cols];
        this.flagged = new boolean[rows][cols];
        this.adjacentCounts = new int[rows][cols];

        // Set initial game state
        this.gameState = GameState.PLAYING;
        this.tilesRevealed = 0;
        this.safeCells = (rows * cols) - mineCount;

        // Build the board
        placeMines();
        calculateAdjacentCounts();
    }

    // Randomly places mines on the grid, ensuring no duplicates
    private void placeMines() {
        int placed = 0;
        while (placed < mineCount) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            // Only place a mine if the cell doesn't already have one
            if (!mines[r][c]) {
                mines[r][c] = true;
                placed++;
            }
        }
    }

    // Pre-computes the adjacent mine count for every cell on the grid
    private void calculateAdjacentCounts() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (mines[r][c]) {
                    adjacentCounts[r][c] = -1; // mark mine cells with -1
                } else {
                    adjacentCounts[r][c] = countAdjacentMines(r, c);
                }
            }
        }
    }

    // Counts the number of mines in the 8 cells surrounding the given position
    public int countAdjacentMines(int r, int c) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0)
                    continue; // skip the cell itself
                int nr = r + dr;
                int nc = c + dc;
                if (isInBounds(nr, nc) && mines[nr][nc]) {
                    count++;
                }
            }
        }
        return count;
    }

    // Checks whether the given row and column are within the grid boundaries
    public boolean isInBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    // Reveals a cell at the given position
    // Returns true if the action was performed, false if it was invalid
    public boolean revealCell(int r, int c) {
        if (gameState != GameState.PLAYING)
            return false;
        if (!isInBounds(r, c))
            return false;
        if (revealed[r][c] || flagged[r][c])
            return false;

        revealed[r][c] = true;
        tilesRevealed++;

        // If the player hit a mine, the game is lost
        if (mines[r][c]) {
            gameState = GameState.LOST;
            revealAllMines();
            return true;
        }

        // If no adjacent mines, recursively reveal neighbors (flood-fill)
        if (adjacentCounts[r][c] == 0) {
            floodReveal(r, c);
        }

        // Check if all safe cells have been revealed
        checkWinCondition();

        return true;
    }

    // Flood-fill: recursively reveals all neighboring cells with zero adjacent
    // mines
    private void floodReveal(int r, int c) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0)
                    continue;
                revealCell(r + dr, c + dc); // recursive call handles bounds and already-revealed checks
            }
        }
    }

    // Checks if the player has revealed all safe (non-mine) cells
    private void checkWinCondition() {
        if (tilesRevealed == safeCells) {
            gameState = GameState.WON;
        }
    }

    // Reveals all mine positions on the board (called when the game is lost)
    private void revealAllMines() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (mines[r][c]) {
                    revealed[r][c] = true;
                }
            }
        }
    }

    // Toggles the flag on a cell (flag prevents accidental reveal)
    // Returns true if the action was performed, false if invalid
    public boolean toggleFlag(int r, int c) {
        if (gameState != GameState.PLAYING)
            return false;
        if (!isInBounds(r, c))
            return false;
        if (revealed[r][c])
            return false; // cannot flag an already-revealed cell

        flagged[r][c] = !flagged[r][c];
        return true;
    }

    // --- Getters for View and Controller access ---

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMineCount() {
        return mineCount;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isGameOver() {
        return gameState != GameState.PLAYING;
    }

    public boolean isGameWon() {
        return gameState == GameState.WON;
    }

    // Cell-level queries
    public boolean isRevealed(int r, int c) {
        return revealed[r][c];
    }

    public boolean isMine(int r, int c) {
        return mines[r][c];
    }

    public boolean isFlagged(int r, int c) {
        return flagged[r][c];
    }

    public int getAdjacentMines(int r, int c) {
        return adjacentCounts[r][c];
    }
}
