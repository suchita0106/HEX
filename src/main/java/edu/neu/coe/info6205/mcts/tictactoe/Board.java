

package edu.neu.coe.info6205.mcts.tictactoe;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private char[][] board;
    private int turnCount; // Variable to count the number of turns taken

    public Board() {
        board = new char[3][3];
        // Initialize the board with empty cells
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }

    // Copy constructor
    public Board(Board other) {
        this.board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, this.board[i].length);
        }
    }

    // Method to count turns
    public int getTurnCount() {
        return this.turnCount;
    }

    public List<int[]> getEmptyCells() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        return emptyCells;
    }


    public char[][] getBoard() {
        return board;
    }

    public boolean isFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidMove(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == '-';
    }

    public void makeMove(int row, int col, char player) {
        board[row][col] = player;
    }

    public boolean checkWinner(char player) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
        }
        // Check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] == player && board[1][j] == player && board[2][j] == player) {
                return true;
            }
        }
        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }
        return false;
    }
}
