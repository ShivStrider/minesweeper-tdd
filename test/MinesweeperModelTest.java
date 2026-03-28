import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.Random;
import model.MinesweeperModel;

// Function Testing: Path Testing and Data Flow Testing for MinesweeperModel
// Tests the core game logic methods in isolation using a seeded Random for determinism.
// Follows Superpowers TDD methodology: every public method has a test, no conditional skips,
// tests use real code (no mocks), edge cases covered.
public class MinesweeperModelTest {

    private MinesweeperModel model;

    // Setup: Creates a deterministic 4x4 board with 2 mines using seed 42
    @Before
    public void setUp() {
        model = new MinesweeperModel(4, 4, 2, new Random(42));
    }

    // CONSTRUCTOR & INITIALIZATION TESTS
    // Superpowers: Every function must have a test

    // Verifies grid dimensions are set correctly
    @Test
    public void testConstructor_GridDimensions() {
        assertEquals(4, model.getRows());
        assertEquals(4, model.getCols());
    }

    // Verifies mine count is stored correctly
    @Test
    public void testConstructor_MineCount() {
        assertEquals(2, model.getMineCount());
    }

    // Verifies initial game state is PLAYING
    @Test
    public void testConstructor_InitialState() {
        assertEquals(MinesweeperModel.GameState.PLAYING, model.getGameState());
        assertFalse(model.isGameOver());
        assertFalse(model.isGameWon());
    }

    // Verifies exact number of mines are placed on the board
    @Test
    public void testConstructor_CorrectMineCount() {
        int mineCount = 0;
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                if (model.isMine(r, c))
                    mineCount++;
            }
        }
        assertEquals("Board should have exactly 2 mines", 2, mineCount);
    }

    // Verifies all cells start unrevealed and unflagged
    @Test
    public void testConstructor_AllCellsHiddenAndUnflagged() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                assertFalse("Cell (" + r + "," + c + ") should not be revealed", model.isRevealed(r, c));
                assertFalse("Cell (" + r + "," + c + ") should not be flagged", model.isFlagged(r, c));
            }
        }
    }

    // Verifies seeded Random produces deterministic mine placement
    @Test
    public void testConstructor_DeterministicWithSeed() {
        MinesweeperModel model1 = new MinesweeperModel(4, 4, 2, new Random(42));
        MinesweeperModel model2 = new MinesweeperModel(4, 4, 2, new Random(42));

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                assertEquals("Mine placement should match with same seed at (" + r + "," + c + ")",
                        model1.isMine(r, c), model2.isMine(r, c));
            }
        }
    }

    // isInBounds() TESTS
    // Superpowers: Every public method needs a test

    @Test
    public void testIsInBounds_ValidCenter() {
        assertTrue(model.isInBounds(2, 2));
    }

    @Test
    public void testIsInBounds_ValidCorners() {
        assertTrue("Top-left", model.isInBounds(0, 0));
        assertTrue("Top-right", model.isInBounds(0, 3));
        assertTrue("Bottom-left", model.isInBounds(3, 0));
        assertTrue("Bottom-right", model.isInBounds(3, 3));
    }

    @Test
    public void testIsInBounds_NegativeRow() {
        assertFalse(model.isInBounds(-1, 0));
    }

    @Test
    public void testIsInBounds_NegativeCol() {
        assertFalse(model.isInBounds(0, -1));
    }

    @Test
    public void testIsInBounds_RowTooLarge() {
        assertFalse(model.isInBounds(4, 0));
    }

    @Test
    public void testIsInBounds_ColTooLarge() {
        assertFalse(model.isInBounds(0, 4));
    }

    // 1.1 PATH TESTING - Target Function: revealCell(int r, int c)
    // Control Flow Paths:
    // Path A: gameState != PLAYING -> return false
    // Path B: out of bounds -> return false
    // Path C: already revealed or flagged -> return false
    // Path D: cell is a mine -> set LOST, reveal all mines, return true
    // Path E: cell has adjacent mines > 0 -> reveal, check win, return true
    // Path F: cell has 0 adjacent mines -> reveal, flood-fill, check win, return
    // true

    // Path A: Game is already over, revealCell should return false
    @Test
    public void testRevealCell_PathA_GameAlreadyOver() {
        // First, find and reveal a mine to end the game
        forceLoss();
        assertEquals(MinesweeperModel.GameState.LOST, model.getGameState());

        // Now try to reveal another cell — should return false
        assertFalse(model.revealCell(0, 0));
    }

    // Path B: Row or column is out of bounds
    @Test
    public void testRevealCell_PathB_OutOfBounds_NegativeRow() {
        assertFalse(model.revealCell(-1, 0));
    }

    @Test
    public void testRevealCell_PathB_OutOfBounds_LargeCol() {
        assertFalse(model.revealCell(0, 10));
    }

    // Path C: Cell is already revealed
    @Test
    public void testRevealCell_PathC_AlreadyRevealed() {
        int[] safe = findSafeCell();
        assertTrue(model.revealCell(safe[0], safe[1]));

        // Try to reveal the same cell again — should return false
        assertFalse(model.revealCell(safe[0], safe[1]));
    }

    // Path C: Cell is flagged — should not reveal
    @Test
    public void testRevealCell_PathC_CellIsFlagged() {
        int[] safe = findSafeCell();
        model.toggleFlag(safe[0], safe[1]);

        // Flagged cell should not be revealable
        assertFalse(model.revealCell(safe[0], safe[1]));
    }

    // Path D: Revealing a mine triggers game loss
    @Test
    public void testRevealCell_PathD_HitMine() {
        int[] mine = findMineCell();
        assertTrue(model.revealCell(mine[0], mine[1]));
        assertEquals(MinesweeperModel.GameState.LOST, model.getGameState());
        assertTrue(model.isGameOver());
        assertFalse(model.isGameWon());
    }

    // Path D: After loss, all mines should be revealed
    @Test
    public void testRevealCell_PathD_AllMinesRevealedAfterLoss() {
        int[] mine = findMineCell();
        model.revealCell(mine[0], mine[1]);

        // Every mine cell should now be revealed
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                if (model.isMine(r, c)) {
                    assertTrue("Mine at (" + r + "," + c + ") should be revealed after loss",
                            model.isRevealed(r, c));
                }
            }
        }
    }

    // Path E: Revealing a cell adjacent to mines shows count (no flood-fill)
    @Test
    public void testRevealCell_PathE_AdjacentToMines() {
        int[] cell = findCellWithAdjacentMines();
        assertTrue(model.revealCell(cell[0], cell[1]));
        assertTrue(model.isRevealed(cell[0], cell[1]));
        assertTrue(model.getAdjacentMines(cell[0], cell[1]) > 0);
        assertEquals(MinesweeperModel.GameState.PLAYING, model.getGameState());
    }

    // Path F: Revealing a cell with 0 adjacent mines triggers flood-fill
    // Superpowers fix: removed conditional skip — use a board guaranteed to have
    // zero cells
    @Test
    public void testRevealCell_PathF_FloodFill() {
        // 8x8 board with 1 mine guarantees many zero-adjacent cells
        MinesweeperModel largeModel = new MinesweeperModel(8, 8, 1, new Random(42));
        int[] zeroCell = findZeroAdjacentCell(largeModel);

        // With 63 safe cells and 1 mine, zero-adjacent cells must exist
        assertNotNull("Board should have zero-adjacent cells", zeroCell);

        largeModel.revealCell(zeroCell[0], zeroCell[1]);

        // After flood-fill, multiple cells should be revealed
        int revealedCount = countRevealed(largeModel);
        assertTrue("Flood-fill should reveal multiple cells, got: " + revealedCount, revealedCount > 1);
    }

    // 1.2 DATA FLOW TESTING - Target Function: countAdjacentMines(int r, int c)
    // Tracked Variables: count, r, c, dr, dc, nr, nc
    // Def-Use Pairs:
    // count: defined (init 0), used (incremented when neighbor is mine), used
    // (returned)
    // nr/nc: defined (r+dr, c+dc), used (bounds check and mine check)

    // Tests count = 0 (no adjacent mines) — def -> return without increment
    @Test
    public void testCountAdjacentMines_ZeroMines() {
        MinesweeperModel largeModel = new MinesweeperModel(8, 8, 1, new Random(42));
        int[] zeroCell = findZeroAdjacentCell(largeModel);
        assertNotNull("Should find a cell with zero adjacent mines", zeroCell);
        assertEquals(0, largeModel.countAdjacentMines(zeroCell[0], zeroCell[1]));
    }

    // Tests count > 0 (at least one adjacent mine) — def -> increment -> return
    @Test
    public void testCountAdjacentMines_WithMines() {
        int[] cell = findCellWithAdjacentMines();
        int count = model.countAdjacentMines(cell[0], cell[1]);
        assertTrue("Should have at least 1 adjacent mine", count > 0);
        assertTrue("Should have at most 8 adjacent mines", count <= 8);
    }

    // Tests corner cell (0,0) — nr/nc goes out of bounds for 5 of 8 directions
    @Test
    public void testCountAdjacentMines_CornerCell() {
        int count = model.countAdjacentMines(0, 0);
        assertTrue("Corner cell can have 0-3 adjacent mines", count >= 0 && count <= 3);
    }

    // Tests edge cell (0,1) — nr/nc goes out of bounds for 3 of 8 directions
    @Test
    public void testCountAdjacentMines_EdgeCell() {
        int count = model.countAdjacentMines(0, 1);
        assertTrue("Edge cell can have 0-5 adjacent mines", count >= 0 && count <= 5);
    }

    // Tests center cell (1,1) — all 8 neighbors are in bounds
    @Test
    public void testCountAdjacentMines_CenterCell() {
        int count = model.countAdjacentMines(1, 1);
        assertTrue("Center cell can have 0-8 adjacent mines", count >= 0 && count <= 8);
    }

    // toggleFlag() TESTS
    // Superpowers: Every public method needs a test with edge cases

    // Flag an unrevealed cell — should succeed
    @Test
    public void testToggleFlag_FlagUnrevealedCell() {
        assertTrue(model.toggleFlag(0, 0));
        assertTrue(model.isFlagged(0, 0));
    }

    // Unflag a flagged cell — should toggle back
    @Test
    public void testToggleFlag_UnflagCell() {
        model.toggleFlag(0, 0);
        assertTrue(model.isFlagged(0, 0));

        model.toggleFlag(0, 0);
        assertFalse("Cell should be unflagged after second toggle", model.isFlagged(0, 0));
    }

    // Flag a revealed cell — should fail
    @Test
    public void testToggleFlag_CannotFlagRevealedCell() {
        int[] safe = findSafeCell();
        model.revealCell(safe[0], safe[1]);
        assertFalse(model.toggleFlag(safe[0], safe[1]));
    }

    // Flag out of bounds — should fail
    @Test
    public void testToggleFlag_OutOfBounds() {
        assertFalse(model.toggleFlag(-1, 0));
        assertFalse(model.toggleFlag(0, 99));
    }

    // Flag after game over — should fail
    @Test
    public void testToggleFlag_GameOver() {
        forceLoss();
        assertFalse(model.toggleFlag(0, 0));
    }

    // WIN CONDITION TESTS

    // Tests that revealing all safe cells triggers WIN state
    @Test
    public void testWinCondition_AllSafeCellsRevealed() {
        MinesweeperModel tinyModel = new MinesweeperModel(2, 2, 1, new Random(42));

        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                if (!tinyModel.isMine(r, c)) {
                    tinyModel.revealCell(r, c);
                }
            }
        }

        assertEquals(MinesweeperModel.GameState.WON, tinyModel.getGameState());
        assertTrue(tinyModel.isGameWon());
        assertTrue(tinyModel.isGameOver());
    }

    // Tests that partially revealing safe cells does NOT trigger win
    @Test
    public void testWinCondition_PartialRevealNotWin() {
        int[] safe = findSafeCell();
        model.revealCell(safe[0], safe[1]);
        assertEquals(MinesweeperModel.GameState.PLAYING, model.getGameState());
    }

    // Helper methods for finding specific cell types

    private int[] findSafeCell() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                if (!model.isMine(r, c))
                    return new int[] { r, c };
            }
        }
        fail("No safe cell found on board");
        return null;
    }

    private int[] findMineCell() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                if (model.isMine(r, c))
                    return new int[] { r, c };
            }
        }
        fail("No mine cell found on board");
        return null;
    }

    private int[] findCellWithAdjacentMines() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                if (!model.isMine(r, c) && model.countAdjacentMines(r, c) > 0) {
                    return new int[] { r, c };
                }
            }
        }
        fail("No cell with adjacent mines found");
        return null;
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

    private int countRevealed(MinesweeperModel m) {
        int count = 0;
        for (int r = 0; r < m.getRows(); r++) {
            for (int c = 0; c < m.getCols(); c++) {
                if (m.isRevealed(r, c))
                    count++;
            }
        }
        return count;
    }

    private void forceLoss() {
        int[] mine = findMineCell();
        model.revealCell(mine[0], mine[1]);
    }
}
