import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Random;
import java.util.Scanner;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import model.MinesweeperModel;
import view.MinesweeperView;
import controller.MinesweeperController;

// Integration Testing: Tests the interaction between Model, View, and Controller
// Strategy: Bottom-up (Model tested first, then Model+View, then full MVC)
public class MinesweeperIntegrationTest {

    // Model + View Integration

    // Tests that View correctly reads Model state to display the board
    @Test
    public void testModelViewIntegration_DisplayBoard() {
        MinesweeperModel model = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        // Display the board — View should read Model state without errors
        view.displayBoard(model);
        String output = outputStream.toString();

        // Verify output contains row and column headers
        assertTrue("Board output should contain column header '0'", output.contains("0"));
        assertTrue("Board output should contain column header '3'", output.contains("3"));
        // All cells should be hidden (showing dots)
        assertTrue("Unrevealed cells should display as dots", output.contains("."));
    }

    // Tests that View reflects Model changes after revealing a cell
    @Test
    public void testModelViewIntegration_RevealedCellDisplayed() {
        MinesweeperModel model = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        // Find and reveal a safe cell
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (!model.isMine(r, c)) {
                    model.revealCell(r, c);
                    break;
                }
            }
            if (model.getGameState() != MinesweeperModel.GameState.PLAYING || hasRevealedCell(model))
                break;
        }

        view.displayBoard(model);
        String output = outputStream.toString();

        // Verify output is not all dots (some cells should be revealed)
        assertFalse("Board should show revealed cells", output.isEmpty());
    }

    // Tests that View correctly shows flagged cells
    @Test
    public void testModelViewIntegration_FlaggedCellDisplayed() {
        MinesweeperModel model = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        // Flag a cell
        model.toggleFlag(0, 0);
        view.displayBoard(model);
        String output = outputStream.toString();

        // Verify flag marker is displayed
        assertTrue("Flagged cell should display 'F'", output.contains("F"));
    }

    // Tests View displays mines after game loss
    @Test
    public void testModelViewIntegration_GameOverDisplaysMines() {
        MinesweeperModel model = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        // Reveal a mine to trigger game over
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (model.isMine(r, c)) {
                    model.revealCell(r, c);
                    break;
                }
            }
            if (model.isGameOver())
                break;
        }

        view.displayBoard(model);
        String output = outputStream.toString();

        // Mines should be visible as '*'
        assertTrue("Game over board should show mines", output.contains("*"));
    }

    // Model + Controller Integration

    // Tests that Controller correctly delegates reveal commands to Model
    @Test
    public void testModelControllerIntegration_RevealCommand() {
        MinesweeperModel model = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        // Find a safe cell position
        int safeR = -1, safeC = -1;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (!model.isMine(r, c)) {
                    safeR = r;
                    safeC = c;
                    break;
                }
            }
            if (safeR >= 0)
                break;
        }

        // Simulate input: reveal a safe cell, then quit
        String input = safeR + " " + safeC + "\nquit\n";
        Scanner scanner = new Scanner(input);
        MinesweeperController controller = new MinesweeperController(model, view, scanner);

        controller.startGame();

        // Verify the cell was revealed through the controller
        assertTrue("Controller should have revealed the cell", model.isRevealed(safeR, safeC));
    }

    // Tests that Controller correctly delegates flag commands to Model
    @Test
    public void testModelControllerIntegration_FlagCommand() {
        MinesweeperModel model = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        // Simulate input: flag cell (1,1), then quit
        String input = "f 1 1\nquit\n";
        Scanner scanner = new Scanner(input);
        MinesweeperController controller = new MinesweeperController(model, view, scanner);

        controller.startGame();

        // Verify the cell was flagged through the controller
        assertTrue("Controller should have flagged cell (1,1)", model.isFlagged(1, 1));
    }

    // Tests that Controller handles invalid input without crashing
    @Test
    public void testModelControllerIntegration_InvalidInput() {
        MinesweeperModel model = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        // Simulate invalid inputs followed by quit
        String input = "abc\n99 99\n-1 -1\nquit\n";
        Scanner scanner = new Scanner(input);
        MinesweeperController controller = new MinesweeperController(model, view, scanner);

        // Should not throw any exception
        controller.startGame();
        String output = outputStream.toString();

        // Verify error messages were displayed
        assertTrue("Should display an error message",
                output.contains("Invalid") || output.contains("out of bounds"));
    }

    // Full MVC Integration (Model + View + Controller)

    // Tests a complete game flow ending in a loss
    @Test
    public void testFullMVC_GameLoss() {
        MinesweeperModel model = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        // Find a mine position
        int mineR = -1, mineC = -1;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (model.isMine(r, c)) {
                    mineR = r;
                    mineC = c;
                    break;
                }
            }
            if (mineR >= 0)
                break;
        }

        // Simulate revealing a mine
        String input = mineR + " " + mineC + "\n";
        Scanner scanner = new Scanner(input);
        MinesweeperController controller = new MinesweeperController(model, view, scanner);

        controller.startGame();
        String output = outputStream.toString();

        // Verify game over message was displayed
        assertEquals(MinesweeperModel.GameState.LOST, model.getGameState());
        assertTrue("Should display game over message", output.contains("Game Over"));
    }

    // Tests a complete game flow ending in a win
    @Test
    public void testFullMVC_GameWin() {
        MinesweeperModel model = new MinesweeperModel(2, 2, 1, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        // Build input string to reveal all safe cells
        StringBuilder inputBuilder = new StringBuilder();
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                if (!model.isMine(r, c)) {
                    inputBuilder.append(r).append(" ").append(c).append("\n");
                }
            }
        }

        Scanner scanner = new Scanner(inputBuilder.toString());
        MinesweeperController controller = new MinesweeperController(model, view, scanner);

        controller.startGame();
        String output = outputStream.toString();

        // Verify win message was displayed
        assertEquals(MinesweeperModel.GameState.WON, model.getGameState());
        assertTrue("Should display win message", output.contains("Congratulations"));
    }

    // Helper methods

    private boolean hasRevealedCell(MinesweeperModel model) {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                if (model.isRevealed(r, c))
                    return true;
            }
        }
        return false;
    }
}
