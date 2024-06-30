package edu.neu.coe.info6205.mcts.hex;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class DisplayHexGame {
    private static final int BOARD_SIZE = 11;
    private String[][] board;
    private int moveCounter = 1;

    public String[][] getBoard() {
        return board;
    }

    public int getMoveCounter() {
        return moveCounter;
    }

    public DisplayHexGame() {
        board = new String[BOARD_SIZE][BOARD_SIZE];
        initializeHexBoard();
    }

    public void initializeHexBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            Arrays.fill(board[i], ".");
        }
    }

    public void readAndSimulateMoves(String filePath) {
        File inputFile = new File(filePath);
        if (!inputFile.exists()) {
            System.out.println("File not found: " + filePath);
            return; // Exit if file does not exist to avoid FileNotFoundException
        }

        int simNum = 0;
        try (Scanner scanner = new Scanner(inputFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) { // Check if the line is not empty
                    simNum++;
                    System.out.println("Simulation # = " + simNum);
                    resetBoardAndCounter();
                    simulateGameFromLine(line);
                    printHexBoard();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error processing moves: " + e.getMessage());
        }
    }

    public void resetBoardAndCounter() {
        initializeHexBoard(); // Reset the board for the next game
        moveCounter = 1; // Reset the move counter
    }

    public void simulateGameFromLine(String line) {
        Scanner lineScanner = new Scanner(line);
        while (lineScanner.hasNext()) {
            String move = lineScanner.next();
            int x = move.charAt(0) - 'A'; // Convert 'A' to 'K' into 0 to 10
            int y = Integer.parseInt(move.substring(1)) - 1; // Convert 1-11 to 0-10
            String moveSymbol = moveCounter % 2 == 1 ? "R" + (moveCounter + 1) / 2 : "B" + moveCounter / 2;
            board[x][y] = moveSymbol;
            moveCounter++;
        }
        lineScanner.close();
    }

    public void printHexBoard() {
        System.out.println("Board state:");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                String value = board[i][j];
                if (value.matches("B\\d")) {
                    value = "B0" + value.substring(1); // Prepend "0" to the digit part
                }else if(value.matches("R\\d")) {
                    value = "R0" + value.substring(1); // Prepend "0" to the digit part
                }
                String colorCode = value.startsWith("R") ? "\u001B[31m" : (value.startsWith("B") ? "\u001B[34m" : "");
                System.out.print(colorCode + value + "\u001B[0m" + " ");
            }
            System.out.println();
        }
        System.out.println(); // Print an empty line after each board for better separation
    }
}
