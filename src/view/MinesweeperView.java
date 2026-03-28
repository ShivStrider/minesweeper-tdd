package view;

import java.io.PrintStream;
import model.MinesweeperModel;

// View: Handles all CLI output and display logic for the Minesweeper game.
// Uses ANSI escape codes for colored output and Unicode box-drawing for a polished grid.
// Uses a PrintStream for output (injectable for testing purposes).
public class MinesweeperView {

    private PrintStream out;

    // ANSI color codes for terminal output
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";

    // Number colors matching classic Minesweeper (1=blue, 2=green, 3=red, etc.)
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    // Background and special colors
    private static final String BG_RED = "\u001B[41m";

    // Unicode box-drawing characters for the grid
    private static final String TOP_LEFT = "\u250C";
    private static final String TOP_RIGHT = "\u2510";
    private static final String BOTTOM_LEFT = "\u2514";
    private static final String BOTTOM_RIGHT = "\u2518";
    private static final String HORIZONTAL = "\u2500";
    private static final String VERTICAL = "\u2502";
    private static final String T_DOWN = "\u252C";
    private static final String T_UP = "\u2534";
    private static final String T_RIGHT = "\u251C";
    private static final String T_LEFT = "\u2524";
    private static final String CROSS = "\u253C";

    // Constructor: Accepts a PrintStream for dependency injection (testability)
    public MinesweeperView(PrintStream out) {
        this.out = out;
    }

    // Default constructor: Uses System.out for standard console output
    public MinesweeperView() {
        this(System.out);
    }

    // Displays the full game board with styled grid and colored cells
    public void displayBoard(MinesweeperModel model) {
        int rows = model.getRows();
        int cols = model.getCols();

        out.println();

        // Print column headers with highlighting
        out.print("     ");
        for (int c = 0; c < cols; c++) {
            out.printf(BOLD + CYAN + " %-2d " + RESET, c);
        }
        out.println();

        // Print top border
        out.print("    " + TOP_LEFT);
        for (int c = 0; c < cols; c++) {
            out.print(HORIZONTAL + HORIZONTAL + HORIZONTAL);
            out.print(c < cols - 1 ? T_DOWN : TOP_RIGHT);
        }
        out.println();

        // Print each row with cell contents
        for (int r = 0; r < rows; r++) {
            // Row label
            out.printf(" " + BOLD + CYAN + "%-2d" + RESET + " " + VERTICAL, r);

            // Cell contents
            for (int c = 0; c < cols; c++) {
                String cell = getCellDisplay(model, r, c);
                out.print(cell + RESET + VERTICAL);
            }
            out.println();

            // Print row separator or bottom border
            if (r < rows - 1) {
                out.print("    " + T_RIGHT);
                for (int c = 0; c < cols; c++) {
                    out.print(HORIZONTAL + HORIZONTAL + HORIZONTAL);
                    out.print(c < cols - 1 ? CROSS : T_LEFT);
                }
            } else {
                out.print("    " + BOTTOM_LEFT);
                for (int c = 0; c < cols; c++) {
                    out.print(HORIZONTAL + HORIZONTAL + HORIZONTAL);
                    out.print(c < cols - 1 ? T_UP : BOTTOM_RIGHT);
                }
            }
            out.println();
        }
        out.println();
    }

    // Returns the styled display string for a single cell based on its state
    private String getCellDisplay(MinesweeperModel model, int r, int c) {
        // Flagged cells show a red flag marker
        if (model.isFlagged(r, c)) {
            return BOLD + YELLOW + " F ";
        }
        // Unrevealed cells show a dim block
        if (!model.isRevealed(r, c)) {
            return DIM + " . ";
        }
        // Revealed mine cells show a red mine marker
        if (model.isMine(r, c)) {
            return BG_RED + BOLD + WHITE + " * " + RESET;
        }
        // Revealed safe cells show the adjacent mine count with color coding
        int adjacentMines = model.getAdjacentMines(r, c);
        if (adjacentMines == 0) {
            return "   "; // blank for zero
        }
        return " " + getNumberColor(adjacentMines) + BOLD + adjacentMines + " ";
    }

    // Returns the ANSI color code for a given mine count (classic Minesweeper
    // colors)
    private String getNumberColor(int count) {
        switch (count) {
            case 1:
                return BLUE;
            case 2:
                return GREEN;
            case 3:
                return RED;
            case 4:
                return MAGENTA;
            case 5:
                return YELLOW;
            case 6:
                return CYAN;
            case 7:
                return WHITE;
            case 8:
                return DIM + WHITE;
            default:
                return RESET;
        }
    }

    // Displays a general message to the player
    public void displayMessage(String message) {
        out.println(BOLD + message + RESET);
    }

    // Displays the input prompt for the player's next move
    public void displayPrompt() {
        out.print(CYAN + "> " + RESET + "Enter move " + DIM + "(row col)" + RESET
                + " or " + DIM + "(f row col)" + RESET + " to flag: ");
    }

    // Displays the welcome banner with game configuration and instructions
    public void displayWelcome(int rows, int cols, int mines) {
        out.println();
        out.println(BOLD + CYAN
                + "  \u250C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510"
                + RESET);
        out.println(BOLD + CYAN + "  \u2502" + RESET + BOLD + "       M I N E S W E E P E R     " + CYAN + "\u2502"
                + RESET);
        out.println(BOLD + CYAN + "  \u2502" + RESET + BOLD + "            CLI Edition           " + CYAN + "\u2502"
                + RESET);
        out.println(BOLD + CYAN
                + "  \u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518"
                + RESET);
        out.println();
        out.println("  Grid: " + BOLD + rows + "x" + cols + RESET + "  |  Mines: " + BOLD + RED + mines + RESET);
        out.println();
        out.println(DIM + "  Commands:" + RESET);
        out.println("    row col     " + DIM + "- reveal a cell" + RESET);
        out.println("    f row col   " + DIM + "- flag/unflag a cell" + RESET);
        out.println("    quit        " + DIM + "- exit the game" + RESET);
        out.println();
    }
}
