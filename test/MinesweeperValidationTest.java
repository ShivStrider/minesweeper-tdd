import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.Random;
import java.util.Scanner;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import model.MinesweeperModel;
import view.MinesweeperView;
import controller.MinesweeperController;

// Validation Testing: Boundary Value, Equivalence Class, Decision Table,
// State Transition, and Use Case Testing for the Minesweeper application.
public class MinesweeperValidationTest {

    private MinesweeperModel model;

    // Setup: Creates a deterministic 8x8 board with 5 mines using seed 42
    @Before
    public void setUp() {
        model = new MinesweeperModel(8, 8, 5, new Random(42));
    }

    // 3.1 BOUNDARY VALUE TESTING - Target: revealCell(row, col) input bounds
    // Boundaries: row in [0, rows-1], col in [0, cols-1]
    // Grid is 8x8, so valid range is [0,7] for both row and col

    // Lower boundary: row = 0, col = 0 (minimum valid)
    @Test
    public void testBoundary_MinimumValidPosition() {
        // (0,0) is a valid position — should succeed or fail based on mine status only
        boolean result = model.revealCell(0, 0);
        assertTrue("Position (0,0) should be a valid cell", result);
    }

    // Upper boundary: row = 7, col = 7 (maximum valid)
    @Test
    public void testBoundary_MaximumValidPosition() {
        boolean result = model.revealCell(7, 7);
        assertTrue("Position (7,7) should be a valid cell", result);
    }

    // Just below lower boundary: row = -1 (invalid)
    @Test
    public void testBoundary_BelowMinimumRow() {
        assertFalse("Row -1 should be out of bounds", model.revealCell(-1, 0));
    }

    // Just below lower boundary: col = -1 (invalid)
    @Test
    public void testBoundary_BelowMinimumCol() {
        assertFalse("Col -1 should be out of bounds", model.revealCell(0, -1));
    }

    // Just above upper boundary: row = 8 (invalid)
    @Test
    public void testBoundary_AboveMaximumRow() {
        assertFalse("Row 8 should be out of bounds", model.revealCell(8, 0));
    }

    // Just above upper boundary: col = 8 (invalid)
    @Test
    public void testBoundary_AboveMaximumCol() {
        assertFalse("Col 8 should be out of bounds", model.revealCell(0, 8));
    }

    // Far out of bounds
    @Test
    public void testBoundary_FarOutOfBounds() {
        assertFalse("Row 100 should be out of bounds", model.revealCell(100, 100));
    }

    // 3.2 EQUIVALENCE CLASS TESTING - Target: revealCell and toggleFlag inputs
    // Equivalence Classes:
    // Valid: row in [0,7], col in [0,7], cell not revealed, cell not flagged
    // Invalid: row < 0, row > 7, col < 0, col > 7
    // Invalid: cell already revealed, cell is flagged (for reveal)

    // Valid class: Valid coordinates, unrevealed cell
    @Test
    public void testEquivalence_ValidReveal() {
        int[] safe = findSafeCell();
        assertTrue("Valid unrevealed cell should be revealable", model.revealCell(safe[0], safe[1]));
    }

    // Invalid class: Negative coordinates
    @Test
    public void testEquivalence_InvalidNegativeCoords() {
        assertFalse("Negative row should be invalid", model.revealCell(-5, 3));
        assertFalse("Negative col should be invalid", model.revealCell(3, -5));
    }

    // Invalid class: Coordinates exceeding grid size
    @Test
    public void testEquivalence_InvalidLargeCoords() {
        assertFalse("Row beyond grid should be invalid", model.revealCell(15, 3));
        assertFalse("Col beyond grid should be invalid", model.revealCell(3, 15));
    }

    // Invalid class: Already revealed cell
    @Test
    public void testEquivalence_InvalidAlreadyRevealed() {
        int[] safe = findSafeCell();
        model.revealCell(safe[0], safe[1]);
        assertFalse("Already revealed cell cannot be revealed again", model.revealCell(safe[0], safe[1]));
    }

    // Invalid class: Flagged cell (cannot reveal)
    @Test
    public void testEquivalence_InvalidFlaggedCell() {
        int[] safe = findSafeCell();
        model.toggleFlag(safe[0], safe[1]);
        assertFalse("Flagged cell cannot be revealed", model.revealCell(safe[0], safe[1]));
    }

    // Valid class for toggleFlag: Valid unrevealed cell
    @Test
    public void testEquivalence_ValidFlag() {
        assertTrue("Valid unrevealed cell should be flaggable", model.toggleFlag(0, 0));
        assertTrue("Cell should now be flagged", model.isFlagged(0, 0));
    }

    // Invalid class for toggleFlag: Already revealed cell
    @Test
    public void testEquivalence_InvalidFlagRevealedCell() {
        int[] safe = findSafeCell();
        model.revealCell(safe[0], safe[1]);
        assertFalse("Revealed cell cannot be flagged", model.toggleFlag(safe[0], safe[1]));
    }

    // 3.3 DECISION TABLE TESTING - Target: revealCell business logic
    // Conditions: | R1 | R2 | R3 | R4 | R5 | R6 |
    // ----------------------------|----|----|----|----|----|----|
    // C1: In bounds? | N | Y | Y | Y | Y | Y |
    // C2: Cell is mine? | - | - | Y | N | N | N |
    // C3: Already revealed? | - | Y | N | N | N | N |
    // C4: Adjacent mines > 0? | - | - | - | Y | N | - |
    // ----------------------------|----|----|----|----|----|----|
    // A1: Return false | X | X | | | | |
    // A2: Set LOST, reveal mines | | | X | | | |
    // A3: Reveal cell, show count | | | | X | | |
    // A4: Reveal cell, flood-fill | | | | | X | |

    // Rule 1: Out of bounds -> return false
    @Test
    public void testDecisionTable_Rule1_OutOfBounds() {
        assertFalse(model.revealCell(-1, -1));
    }

    // Rule 2: Already revealed -> return false
    @Test
    public void testDecisionTable_Rule2_AlreadyRevealed() {
        int[] safe = findSafeCell();
        model.revealCell(safe[0], safe[1]);
        assertFalse(model.revealCell(safe[0], safe[1]));
    }

    // Rule 3: In bounds, not revealed, is mine -> LOST
    @Test
    public void testDecisionTable_Rule3_HitMine() {
        int[] mine = findMineCell();
        model.revealCell(mine[0], mine[1]);
        assertEquals(MinesweeperModel.GameState.LOST, model.getGameState());
    }

    // Rule 4: In bounds, not revealed, not mine, adjacent > 0 -> reveal with count
    @Test
    public void testDecisionTable_Rule4_RevealWithCount() {
        int[] cell = findCellWithAdjacentMines();
        model.revealCell(cell[0], cell[1]);
        assertTrue(model.isRevealed(cell[0], cell[1]));
        assertTrue(model.getAdjacentMines(cell[0], cell[1]) > 0);
    }

    // Rule 5: In bounds, not revealed, not mine, adjacent = 0 -> flood-fill
    @Test
    public void testDecisionTable_Rule5_FloodFill() {
        MinesweeperModel largeModel = new MinesweeperModel(8, 8, 1, new Random(42));
        int[] zeroCell = findZeroAdjacentCell(largeModel);

        if (zeroCell != null) {
            largeModel.revealCell(zeroCell[0], zeroCell[1]);
            assertTrue(largeModel.isRevealed(zeroCell[0], zeroCell[1]));

            // Flood-fill should reveal additional cells
            int revealed = countRevealedCells(largeModel);
            assertTrue("Flood-fill should reveal more than 1 cell", revealed > 1);
        }
    }

    // 3.4 STATE TRANSITION TESTING - Target: GameState transitions
    // States: PLAYING, WON, LOST
    // Transitions:
    // PLAYING + reveal safe cell -> PLAYING
    // PLAYING + reveal last safe cell -> WON
    // PLAYING + reveal mine -> LOST
    // WON + any action -> WON (no transition)
    // LOST + any action -> LOST (no transition)

    // Transition: PLAYING -> PLAYING (reveal safe cell, game continues)
    @Test
    public void testStateTransition_PlayingToPlaying() {
        assertEquals(MinesweeperModel.GameState.PLAYING, model.getGameState());
        int[] safe = findSafeCell();
        model.revealCell(safe[0], safe[1]);
        assertEquals(MinesweeperModel.GameState.PLAYING, model.getGameState());
    }

    // Transition: PLAYING -> LOST (reveal a mine)
    @Test
    public void testStateTransition_PlayingToLost() {
        assertEquals(MinesweeperModel.GameState.PLAYING, model.getGameState());
        int[] mine = findMineCell();
        model.revealCell(mine[0], mine[1]);
        assertEquals(MinesweeperModel.GameState.LOST, model.getGameState());
    }

    // Transition: PLAYING -> WON (reveal all safe cells)
    @Test
    public void testStateTransition_PlayingToWon() {
        MinesweeperModel tinyModel = new MinesweeperModel(2, 2, 1, new Random(42));
        assertEquals(MinesweeperModel.GameState.PLAYING, tinyModel.getGameState());

        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                if (!tinyModel.isMine(r, c)) {
                    tinyModel.revealCell(r, c);
                }
            }
        }
        assertEquals(MinesweeperModel.GameState.WON, tinyModel.getGameState());
    }

    // No transition: LOST -> LOST (actions rejected after game over)
    @Test
    public void testStateTransition_LostStaysLost() {
        int[] mine = findMineCell();
        model.revealCell(mine[0], mine[1]);
        assertEquals(MinesweeperModel.GameState.LOST, model.getGameState());

        // Try to reveal another cell — state should remain LOST
        assertFalse(model.revealCell(0, 0));
        assertEquals(MinesweeperModel.GameState.LOST, model.getGameState());
    }

    // No transition: WON -> WON (actions rejected after win)
    @Test
    public void testStateTransition_WonStaysWon() {
        MinesweeperModel tinyModel = new MinesweeperModel(2, 2, 1, new Random(42));
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                if (!tinyModel.isMine(r, c)) {
                    tinyModel.revealCell(r, c);
                }
            }
        }
        assertEquals(MinesweeperModel.GameState.WON, tinyModel.getGameState());

        // Try to flag a cell — state should remain WON
        assertFalse(tinyModel.toggleFlag(0, 0));
        assertEquals(MinesweeperModel.GameState.WON, tinyModel.getGameState());
    }

    // 3.5 USE CASE TESTING

    // Use Case 1: Player reveals a safe cell (Happy Path)
    // Actor: Player
    // Steps: Start game -> Select cell -> Cell is revealed -> Game continues
    @Test
    public void testUseCase_RevealSafeCell_HappyPath() {
        MinesweeperModel ucModel = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        // Find a safe cell
        int safeR = -1, safeC = -1;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (!ucModel.isMine(r, c)) {
                    safeR = r;
                    safeC = c;
                    break;
                }
            }
            if (safeR >= 0)
                break;
        }

        String input = safeR + " " + safeC + "\nquit\n";
        MinesweeperController controller = new MinesweeperController(ucModel, view, new Scanner(input));
        controller.startGame();

        // Verify: cell is revealed, game still playing
        assertTrue(ucModel.isRevealed(safeR, safeC));
        // Game should still be playing (quit exits loop, not the game state)
        assertEquals(MinesweeperModel.GameState.PLAYING, ucModel.getGameState());
    }

    // Use Case 2: Player flags a cell and then unflags it
    // Actor: Player
    // Steps: Start game -> Flag cell -> Cell shows F -> Unflag cell -> Cell shows
    // dot
    @Test
    public void testUseCase_FlagAndUnflag() {
        MinesweeperModel ucModel = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        String input = "f 0 0\nf 0 0\nquit\n";
        MinesweeperController controller = new MinesweeperController(ucModel, view, new Scanner(input));
        controller.startGame();

        // After flagging and unflagging, cell should NOT be flagged
        assertFalse("Cell should be unflagged after toggling twice", ucModel.isFlagged(0, 0));
    }

    // Use Case 3: Player hits a mine (Exception Path)
    // Actor: Player
    // Steps: Start game -> Select mine cell -> All mines revealed -> Game Over
    @Test
    public void testUseCase_HitMine_ExceptionPath() {
        MinesweeperModel ucModel = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        // Find mine position
        int mineR = -1, mineC = -1;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (ucModel.isMine(r, c)) {
                    mineR = r;
                    mineC = c;
                    break;
                }
            }
            if (mineR >= 0)
                break;
        }

        String input = mineR + " " + mineC + "\n";
        MinesweeperController controller = new MinesweeperController(ucModel, view, new Scanner(input));
        controller.startGame();

        // Verify: game is lost, all mines are revealed
        assertEquals(MinesweeperModel.GameState.LOST, ucModel.getGameState());
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (ucModel.isMine(r, c)) {
                    assertTrue("All mines should be revealed after loss", ucModel.isRevealed(r, c));
                }
            }
        }
    }

    // Use Case 4: Player enters invalid input
    // Actor: Player
    // Steps: Start game -> Enter garbage -> Error shown -> Game continues
    @Test
    public void testUseCase_InvalidInput_ExceptionPath() {
        MinesweeperModel ucModel = new MinesweeperModel(4, 4, 2, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        String input = "hello world\nquit\n";
        MinesweeperController controller = new MinesweeperController(ucModel, view, new Scanner(input));
        controller.startGame();

        String output = outputStream.toString();
        assertTrue("Error message should be displayed", output.contains("Invalid"));
        assertEquals("Game should still be playing", MinesweeperModel.GameState.PLAYING, ucModel.getGameState());
    }

    // Use Case 5: Player wins the game (Happy Path)
    // Actor: Player
    // Steps: Start game -> Reveal all safe cells -> Win message displayed
    @Test
    public void testUseCase_WinGame_HappyPath() {
        MinesweeperModel ucModel = new MinesweeperModel(2, 2, 1, new Random(42));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MinesweeperView view = new MinesweeperView(new PrintStream(outputStream));

        StringBuilder inputBuilder = new StringBuilder();
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                if (!ucModel.isMine(r, c)) {
                    inputBuilder.append(r).append(" ").append(c).append("\n");
                }
            }
        }

        MinesweeperController controller = new MinesweeperController(ucModel, view,
                new Scanner(inputBuilder.toString()));
        controller.startGame();

        String output = outputStream.toString();
        assertEquals(MinesweeperModel.GameState.WON, ucModel.getGameState());
        assertTrue("Win message should be displayed", output.contains("Congratulations"));
    }

    // Helper methods

    private int[] findSafeCell() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                if (!model.isMine(r, c))
                    return new int[] { r, c };
            }
        }
        return new int[] { 0, 0 };
    }

    private int[] findMineCell() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                if (model.isMine(r, c))
                    return new int[] { r, c };
            }
        }
        return new int[] { 0, 0 };
    }

    private int[] findCellWithAdjacentMines() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                if (!model.isMine(r, c) && model.countAdjacentMines(r, c) > 0) {
                    return new int[] { r, c };
                }
            }
        }
        return new int[] { 0, 0 };
    }

    private int[] findZeroAdjacentCell(MinesweeperModel m) {
        for (int r = 0; r < m.getRows(); r++) {
            for (int c = 0; c < m.getCols(); c++) {
                if (!m.isMine(r, c) && m.countAdjacentMines(r, c) == 0) {
                    return new int[] { r, c };
                }
            }
        }
        return null;
    }

    private int countRevealedCells(MinesweeperModel m) {
        int count = 0;
        for (int r = 0; r < m.getRows(); r++) {
            for (int c = 0; c < m.getCols(); c++) {
                if (m.isRevealed(r, c))
                    count++;
            }
        }
        return count;
    }
}
