import java.util.Random;
import java.util.Scanner;
import model.MinesweeperModel;
import view.MinesweeperView;
import controller.MinesweeperController;

// Entry point: Wires together the Model, View, and Controller components and starts the game.
public class App {
    public static void main(String[] args) {
        // Game configuration
        int rows = 8;
        int cols = 8;
        int mines = 5;

        // Initialize MVC components with dependency injection
        MinesweeperModel model = new MinesweeperModel(rows, cols, mines, new Random());
        MinesweeperView view = new MinesweeperView();
        MinesweeperController controller = new MinesweeperController(model, view, new Scanner(System.in));

        // Start the game loop
        controller.startGame();
    }
}
