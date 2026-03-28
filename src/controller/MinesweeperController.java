package controller;

import java.util.Scanner;
import model.MinesweeperModel;
import view.MinesweeperView;

// Controller: Manages the game loop, user input parsing, and coordination between Model and View.
// Uses a Scanner for input (injectable for testing purposes).
public class MinesweeperController {

    private MinesweeperModel model;
    private MinesweeperView view;
    private Scanner scanner;

    // Constructor: Accepts Model, View, and Scanner dependencies (supports
    // dependency injection)
    public MinesweeperController(MinesweeperModel model, MinesweeperView view, Scanner scanner) {
        this.model = model;
        this.view = view;
        this.scanner = scanner;
    }

    // Main game loop: displays board, prompts for input, processes move, repeats
    // until game ends
    public void startGame() {
        view.displayWelcome(model.getRows(), model.getCols(), model.getMineCount());

        // Continue looping while game is still in progress
        while (model.getGameState() == MinesweeperModel.GameState.PLAYING) {
            view.displayBoard(model);
            view.displayPrompt();

            String input = scanner.nextLine().trim().toLowerCase();

            // Allow player to exit gracefully
            if (input.equals("quit")) {
                view.displayMessage("Thanks for playing!");
                return;
            }

            // Delegate input handling to processInput
            processInput(input);
        }

        // Game has ended — display final board and result message
        view.displayBoard(model);

        if (model.isGameWon()) {
            view.displayMessage("Congratulations! You cleared all the mines!");
        } else {
            view.displayMessage("BOOM! You hit a mine. Game Over!");
        }
    }

    // Parses and executes a player's input command
    // Supports two formats: "row col" (reveal) and "f row col" (flag/unflag)
    private void processInput(String input) {
        String[] parts = input.split("\\s+");

        try {
            if (parts[0].equals("f")) {
                // Flag command: expects exactly 3 parts (f, row, col)
                handleFlagCommand(parts);
            } else {
                // Reveal command: expects exactly 2 parts (row, col)
                handleRevealCommand(parts);
            }
        } catch (NumberFormatException e) {
            // Player entered non-numeric values for row or column
            view.displayMessage("Invalid input. Please enter numbers for row and column.");
        }
    }

    // Handles the flag/unflag command: "f row col"
    private void handleFlagCommand(String[] parts) {
        if (parts.length != 3) {
            view.displayMessage("Invalid flag command. Use: f row col");
            return;
        }

        int row = Integer.parseInt(parts[1]);
        int col = Integer.parseInt(parts[2]);

        // Validate that the position is within grid bounds
        if (!isValidPosition(row, col)) {
            view.displayMessage("Position out of bounds. Row: 0-" + (model.getRows() - 1)
                    + ", Col: 0-" + (model.getCols() - 1));
            return;
        }

        // Attempt to toggle flag; fails if cell is already revealed
        if (!model.toggleFlag(row, col)) {
            view.displayMessage("Cannot flag that cell (already revealed).");
        }
    }

    // Handles the reveal command: "row col"
    private void handleRevealCommand(String[] parts) {
        if (parts.length != 2) {
            view.displayMessage("Invalid command. Use: row col  OR  f row col");
            return;
        }

        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);

        // Validate that the position is within grid bounds
        if (!isValidPosition(row, col)) {
            view.displayMessage("Position out of bounds. Row: 0-" + (model.getRows() - 1)
                    + ", Col: 0-" + (model.getCols() - 1));
            return;
        }

        // Attempt to reveal cell; fails if cell is already revealed or flagged
        if (!model.revealCell(row, col)) {
            view.displayMessage("Cannot reveal that cell (already revealed or flagged).");
        }
    }

    // Validates whether the given row and column are within the grid boundaries
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < model.getRows() && col >= 0 && col < model.getCols();
    }
}
