# Software Test Plan and Documentation

## Introduction

This document outlines the comprehensive testing strategy for the MineSweeper CLI application. The project follows **Test-Driven Development (TDD)** using **JUnit** as the testing framework, adhering to the **TDD methodology** (RED-GREEN-REFACTOR cycle). All tests are fully deterministic through the use of a seeded `Random` object injected via the Model constructor. The MVC architecture enables isolated unit testing of each component, pair-wise integration testing, and full system-level validation testing.

---

## 1. Function Testing

Function testing focuses on the `MinesweeperModel` class, which contains all game logic. Two core functions were selected for thorough testing: `revealCell()` for path testing and `countAdjacentMines()` for data flow testing. Additional tests cover `isInBounds()`, `toggleFlag()`, and constructor initialization per the Superpowers checklist requirement that every public method must have a dedicated test.

### 1.1 Path Testing

- **Target Function:** `revealCell(int r, int c)`
- **Test Requirement:** All independent paths through the function must be covered to achieve full path coverage.

**Independent Paths (6 paths for full path coverage):**

| Path | Condition                     | Action                                                    |
| ---- | ----------------------------- | --------------------------------------------------------- |
| A    | `gameState != PLAYING`        | Return false (game already ended)                         |
| B    | Out of bounds (`!isInBounds`) | Return false                                              |
| C    | Already revealed OR flagged   | Return false                                              |
| D    | Cell is a mine                | Set state to LOST, reveal all mines, return true          |
| E    | Adjacent mine count > 0       | Reveal cell, check win condition, return true             |
| F    | Adjacent mine count = 0       | Reveal cell, flood-fill neighbors, check win, return true |

- **Test Cases:**

| Test Case                                        | Input                      | Expected Output                 | Path |
| ------------------------------------------------ | -------------------------- | ------------------------------- | ---- |
| `testRevealCell_PathA_GameAlreadyOver`           | Reveal after game loss     | `false`                         | A    |
| `testRevealCell_PathB_OutOfBounds_NegativeRow`   | `(-1, 0)`                  | `false`                         | B    |
| `testRevealCell_PathB_OutOfBounds_LargeCol`      | `(0, 10)`                  | `false`                         | B    |
| `testRevealCell_PathC_AlreadyRevealed`           | Reveal same cell twice     | `false` on second call          | C    |
| `testRevealCell_PathC_CellIsFlagged`             | Reveal a flagged cell      | `false`                         | C    |
| `testRevealCell_PathD_HitMine`                   | Mine position              | `true`, state = `LOST`          | D    |
| `testRevealCell_PathD_AllMinesRevealedAfterLoss` | Mine position              | All mine cells revealed         | D    |
| `testRevealCell_PathE_AdjacentToMines`           | Cell near mine             | `true`, adjacentMines > 0       | E    |
| `testRevealCell_PathF_FloodFill`                 | Cell with 0 adjacent mines | `true`, multiple cells revealed | F    |

- **Results:** All 9 path tests passed

### 1.2 Data Flow Testing

- **Target Function:** `countAdjacentMines(int r, int c)`
- **Coverage Criterion:** All-Uses (every def-use pair is exercised)
- **Data Variables Tracked:**
  - `count`: Defined (init 0), used (incremented when neighbor is mine), used (returned)
  - `nr`, `nc`: Defined (computed from `r+dr`, `c+dc`), used (bounds check and mine check)

**Variable Definition-Use Table:**

| Variable | Line | Type | Statement                             |
| -------- | ---- | ---- | ------------------------------------- |
| `count`  | 83   | def  | `int count = 0;`                      |
| `dr`     | 84   | def  | `for (int dr = -1; dr <= 1; dr++)`    |
| `dc`     | 85   | def  | `for (int dc = -1; dc <= 1; dc++)`    |
| `nr`     | 87   | def  | `int nr = r + dr;`                    |
| `nc`     | 88   | def  | `int nc = c + dc;`                    |
| `nr`     | 89   | use  | `nr >= 0 && nr < rows` (bounds check) |
| `nc`     | 89   | use  | `nc >= 0 && nc < cols` (bounds check) |
| `nr,nc`  | 89   | use  | `mines[nr][nc]` (mine check)          |
| `count`  | 89   | use  | `count++` (increment)                 |
| `count`  | 94   | use  | `return count;`                       |

**Def-Use Pairs:**

| Pair | Variable | Def Line | Use Line | Description                                 |
| ---- | -------- | -------- | -------- | ------------------------------------------- |
| DU1  | `count`  | 83       | 94       | Defined, returned without increment (zero)  |
| DU2  | `count`  | 83->89    | 94       | Defined, incremented, returned (nonzero)    |
| DU3  | `nr`     | 87       | 89       | Computed neighbor row, used in bounds check |
| DU4  | `nc`     | 88       | 89       | Computed neighbor col, used in bounds check |
| DU5  | `nr,nc`  | 87-88    | 89       | Both in bounds, used in mine array access   |

- **Test Cases:**

| Test Case                           | Description                    | Def-Use Pair Covered                      |
| ----------------------------------- | ------------------------------ | ----------------------------------------- |
| `testCountAdjacentMines_ZeroMines`  | Cell far from mines, returns 0 | DU1: `count` def -> return                 |
| `testCountAdjacentMines_WithMines`  | Cell near mines, returns 1-8   | DU2: `count` def -> increment -> return     |
| `testCountAdjacentMines_CornerCell` | Position (0,0), 3 neighbors    | DU3/DU4: `nr/nc` out of bounds for 5 dirs |
| `testCountAdjacentMines_EdgeCell`   | Position (0,1), 5 neighbors    | DU3/DU4: `nr/nc` partially out of bounds  |
| `testCountAdjacentMines_CenterCell` | Position (1,1), 8 neighbors    | DU5: all `nr,nc` in bounds                |

- **Results:** All 5 data flow tests passed (All-Uses coverage achieved)

### 1.3 Additional Function Tests

#### Constructor & Initialization

| Test Case                                    | Description            | Expected                  |
| -------------------------------------------- | ---------------------- | ------------------------- |
| `testConstructor_GridDimensions`             | Verify rows/cols       | 4×4                       |
| `testConstructor_MineCount`                  | Verify mine count      | 2                         |
| `testConstructor_InitialState`               | Verify PLAYING state   | `PLAYING`, not game over  |
| `testConstructor_CorrectMineCount`           | Count mines on board   | Exactly 2 mines placed    |
| `testConstructor_AllCellsHiddenAndUnflagged` | All cells start hidden | No revealed/flagged cells |
| `testConstructor_DeterministicWithSeed`      | Same seed = same board | Mine positions match      |

#### isInBounds()

| Test Case                     | Input         | Expected |
| ----------------------------- | ------------- | -------- |
| `testIsInBounds_ValidCenter`  | `(2, 2)`      | `true`   |
| `testIsInBounds_ValidCorners` | All 4 corners | `true`   |
| `testIsInBounds_NegativeRow`  | `(-1, 0)`     | `false`  |
| `testIsInBounds_NegativeCol`  | `(0, -1)`     | `false`  |
| `testIsInBounds_RowTooLarge`  | `(4, 0)`      | `false`  |
| `testIsInBounds_ColTooLarge`  | `(0, 4)`      | `false`  |

#### toggleFlag()

| Test Case                               | Description        | Expected            |
| --------------------------------------- | ------------------ | ------------------- |
| `testToggleFlag_FlagUnrevealedCell`     | Flag a hidden cell | `true`, isFlagged   |
| `testToggleFlag_UnflagCell`             | Toggle twice       | `false` (unflagged) |
| `testToggleFlag_CannotFlagRevealedCell` | Flag after reveal  | `false`             |
| `testToggleFlag_OutOfBounds`            | `(-1, 0)`          | `false`             |
| `testToggleFlag_GameOver`               | Flag after loss    | `false`             |

#### Win Condition

| Test Case                               | Description           | Expected  |
| --------------------------------------- | --------------------- | --------- |
| `testWinCondition_AllSafeCellsRevealed` | Reveal all safe cells | `WON`     |
| `testWinCondition_PartialRevealNotWin`  | Reveal one safe cell  | `PLAYING` |

- **Results:** All 19 additional tests passed

---

## 2. Integration Testing

- **Subset of Units Tested:** Model <-> View, Model <-> Controller, and full MVC (Model + View + Controller)
- **Integration Strategy:** Bottom-up -- Model tested in isolation first, then integrated with View, then with Controller, and finally as a complete system.
- **Test Cases:**

| Test Case                                        | Units              | Description                                  | Expected Outcome                         |
| ------------------------------------------------ | ------------------ | -------------------------------------------- | ---------------------------------------- |
| `testModelViewIntegration_DisplayBoard`          | Model + View       | View reads Model state and renders board     | Output contains headers and dots         |
| `testModelViewIntegration_RevealedCellDisplayed` | Model + View       | View shows revealed cells after Model update | Output changes after reveal              |
| `testModelViewIntegration_FlaggedCellDisplayed`  | Model + View       | View shows 'F' for flagged cells             | Output contains "F"                      |
| `testModelViewIntegration_GameOverDisplaysMines` | Model + View       | View shows mines after game loss             | Output contains "\*"                     |
| `testModelControllerIntegration_RevealCommand`   | Model + Controller | Controller parses "row col" and reveals      | Cell is revealed in Model                |
| `testModelControllerIntegration_FlagCommand`     | Model + Controller | Controller parses "f row col" and flags      | Cell is flagged in Model                 |
| `testModelControllerIntegration_InvalidInput`    | Model + Controller | Controller handles bad input gracefully      | Error message displayed, no crash        |
| `testFullMVC_GameLoss`                           | All three          | Full game flow ending in loss                | State = LOST, "Game Over" displayed      |
| `testFullMVC_GameWin`                            | All three          | Full game flow ending in win                 | State = WON, "Congratulations" displayed |

- **Results:** All 9 integration tests passed

---

## 3. Validation Testing

### 3.1 Boundary Value Testing

- **Target Component/Input:** `revealCell(row, col)` coordinate inputs on an 8×8 grid
- **Boundaries Identified:** Row: [0, 7], Col: [0, 7], with invalid values at -1 and 8

| Test Case                           | Input        | Expected Output         |
| ----------------------------------- | ------------ | ----------------------- |
| `testBoundary_MinimumValidPosition` | `(0, 0)`     | `true` (valid)          |
| `testBoundary_MaximumValidPosition` | `(7, 7)`     | `true` (valid)          |
| `testBoundary_BelowMinimumRow`      | `(-1, 0)`    | `false` (out of bounds) |
| `testBoundary_BelowMinimumCol`      | `(0, -1)`    | `false` (out of bounds) |
| `testBoundary_AboveMaximumRow`      | `(8, 0)`     | `false` (out of bounds) |
| `testBoundary_AboveMaximumCol`      | `(0, 8)`     | `false` (out of bounds) |
| `testBoundary_FarOutOfBounds`       | `(100, 100)` | `false` (out of bounds) |

### 3.2 Equivalence Class Testing

- **Target Component/Input:** `revealCell` and `toggleFlag` coordinate and state inputs
- **Equivalence Classes:**
  - **Valid:** Coordinates within [0, rows-1] × [0, cols-1], cell unrevealed and unflagged
  - **Invalid (coordinates):** Negative values, values ≥ grid size
  - **Invalid (state):** Cell already revealed, cell already flagged (for reveal)

| Test Case                                 | Class   | Input                | Expected Output          |
| ----------------------------------------- | ------- | -------------------- | ------------------------ |
| `testEquivalence_ValidReveal`             | Valid   | Safe unrevealed cell | `true`                   |
| `testEquivalence_InvalidNegativeCoords`   | Invalid | `(-5, 3)`            | `false`                  |
| `testEquivalence_InvalidLargeCoords`      | Invalid | `(15, 3)`            | `false`                  |
| `testEquivalence_InvalidAlreadyRevealed`  | Invalid | Revealed cell        | `false`                  |
| `testEquivalence_InvalidFlaggedCell`      | Invalid | Flagged cell         | `false`                  |
| `testEquivalence_ValidFlag`               | Valid   | Unrevealed cell      | `true`, isFlagged = true |
| `testEquivalence_InvalidFlagRevealedCell` | Invalid | Revealed cell        | `false`                  |

### 3.3 Decision Tables Testing

- **Target Business Logic:** `revealCell` outcome based on input conditions

|                           | R1           | R2           | R3   | R4                | R5         |
| ------------------------- | ------------ | ------------ | ---- | ----------------- | ---------- |
| **C1: In bounds?**        | N            | Y            | Y    | Y                 | Y          |
| **C2: Already revealed?** | –            | Y            | N    | N                 | N          |
| **C3: Cell is mine?**     | –            | –            | Y    | N                 | N          |
| **C4: Adjacent > 0?**     | –            | –            | –    | Y                 | N          |
| **Action**                | Return false | Return false | LOST | Reveal with count | Flood-fill |

| Test Case                                 | Rule | Expected                    |
| ----------------------------------------- | ---- | --------------------------- |
| `testDecisionTable_Rule1_OutOfBounds`     | R1   | `false`                     |
| `testDecisionTable_Rule2_AlreadyRevealed` | R2   | `false`                     |
| `testDecisionTable_Rule3_HitMine`         | R3   | State = `LOST`              |
| `testDecisionTable_Rule4_RevealWithCount` | R4   | Revealed, adjacentMines > 0 |
| `testDecisionTable_Rule5_FloodFill`       | R5   | Multiple cells revealed     |

### 3.4 State Transition Testing

- **Target Object:** `GameState` enum in `MinesweeperModel`
- **States:** `PLAYING`, `WON`, `LOST`

**State Transition Table:**

| Current State | Event                    | Next State | Action                     |
| ------------- | ------------------------ | ---------- | -------------------------- |
| PLAYING       | Reveal safe cell         | PLAYING    | Reveal cell, check win     |
| PLAYING       | Reveal last safe cell    | WON        | Reveal cell, set WON       |
| PLAYING       | Reveal mine              | LOST       | Reveal all mines, set LOST |
| PLAYING       | Toggle flag              | PLAYING    | Toggle flag on cell        |
| LOST          | Any action (reveal/flag) | LOST       | Rejected (return false)    |
| WON           | Any action (reveal/flag) | WON        | Rejected (return false)    |

- **Test Cases:**

| Test Case                              | Initial State | Event                 | Expected State |
| -------------------------------------- | ------------- | --------------------- | -------------- |
| `testStateTransition_PlayingToPlaying` | PLAYING       | Reveal safe cell      | PLAYING        |
| `testStateTransition_PlayingToLost`    | PLAYING       | Reveal mine           | LOST           |
| `testStateTransition_PlayingToWon`     | PLAYING       | Reveal all safe cells | WON            |
| `testStateTransition_LostStaysLost`    | LOST          | Attempt reveal        | LOST           |
| `testStateTransition_WonStaysWon`      | WON           | Attempt flag          | WON            |

### 3.5 Use Case Testing

- **Target Use Cases:** All primary game interactions
- **Actor(s):** Player (interacting via CLI)

| Test Case                                | Use Case    | Steps                         | Expected                      |
| ---------------------------------------- | ----------- | ----------------------------- | ----------------------------- |
| `testUseCase_RevealSafeCell_HappyPath`   | Reveal cell | Enter "row col" for safe cell | Cell revealed, game continues |
| `testUseCase_FlagAndUnflag`              | Flag cell   | Enter "f row col" twice       | Cell unflagged after toggle   |
| `testUseCase_HitMine_ExceptionPath`      | Hit mine    | Enter mine position           | Game over, all mines shown    |
| `testUseCase_InvalidInput_ExceptionPath` | Bad input   | Enter "hello world"           | Error message, game continues |
| `testUseCase_WinGame_HappyPath`          | Win game    | Reveal all safe cells         | "Congratulations" displayed   |

---

## 4. Test Suite Execution Summary

| Category                               | Tests  | Passed | Failed |
| -------------------------------------- | ------ | ------ | ------ |
| Constructor & Initialization           | 6      | 6      | 0      |
| isInBounds                             | 6      | 6      | 0      |
| Path Testing (revealCell)              | 9      | 9      | 0      |
| Data Flow Testing (countAdjacentMines) | 5      | 5      | 0      |
| toggleFlag                             | 5      | 5      | 0      |
| Win Condition                          | 2      | 2      | 0      |
| Integration Testing                    | 9      | 9      | 0      |
| Boundary Value Testing                 | 7      | 7      | 0      |
| Equivalence Class Testing              | 7      | 7      | 0      |
| Decision Table Testing                 | 5      | 5      | 0      |
| State Transition Testing               | 5      | 5      | 0      |
| Use Case Testing                       | 5      | 5      | 0      |
| **Total**                              | **71** | **71** | **0**  |
