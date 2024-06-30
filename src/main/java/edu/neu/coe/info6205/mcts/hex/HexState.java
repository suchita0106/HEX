package edu.neu.coe.info6205.mcts.hex;

import edu.neu.coe.info6205.mcts.core.Move;
import edu.neu.coe.info6205.mcts.core.State;
import edu.neu.coe.info6205.union_find.WQUPC;

import java.util.*;

public class HexState implements State<Hex> {
    private final Hex game;
    private final int[][] board;
    private final int currentPlayer;

    private final WQUPC unionFind;
    private final Random random;
    private static final int BOARD_SIZE = 11;

    /**
     * Constructor for HexState initializes the board and sets up union-find structures.
     */
    public HexState(Hex game) {
        this.game = game;
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        this.currentPlayer = 0; // Player 1 starts
        this.unionFind = new WQUPC(BOARD_SIZE * BOARD_SIZE + 2);
        this.random = new Random(); // Initialize a Random object for this state
        initializeBoard();
    }

    // Copy constructor
    public HexState(HexState other) {
        this.game = other.game;  // Assuming game config/info is immutable
        this.board = deepCopyBoard(other.board);
        this.currentPlayer = 1 - other.currentPlayer;
        this.unionFind = new WQUPC(other.unionFind);
        this.random = new Random();
    }

    private int[][] deepCopyBoard(int[][] original) {
        int[][] newBoard = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            newBoard[i] = original[i].clone();  // Cloning each sub-array
        }
        return newBoard;
    }

    public HexState(HexState other, int currentPlayer) {
        this.game = other.game;
        this.board = copyBoard(other.board);
        this.currentPlayer = currentPlayer;
        this.unionFind = new WQUPC(other.unionFind);  // Assuming deep copy
        this.random = new Random();
    }

    private static int[][] copyBoard(int[][] original) {
        int[][] newBoard = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            newBoard[i] = original[i].clone();
        }
        return newBoard;
    }

    public void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = -1; // -1 indicates empty
            }
        }
        //printBoard();
    }

    public void printBoard() {
        for (int[] ints : board) {
            for (int anInt : ints) {
                System.out.print((anInt == -1 ? "." : anInt) + " ");
            }
            System.out.println();
        }
    }

    @Override
    public Hex game() {
        return game;
    }

    @Override
    public boolean isTerminal() {
        return checkWinner() != -1;
    }

    @Override
    public int player() {
        return currentPlayer;
    }

    @Override
    public Optional<Integer> winner() {
        int winner = checkWinner();
        return winner == -1 ? Optional.empty() : Optional.of(winner);
    }

    @Override
    public Collection<Move<Hex>> moves(int player) {
        //System.out.println("Generating moves for player " + player);
        List<Move<Hex>> validMoves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (this.board[i][j] == -1) {  // Check if the cell is empty
                    validMoves.add(new HexMove(player, i, j));
                }
            }
        }
//        if (validMoves.isEmpty()) {
//            System.out.println("No valid moves available for player " + player);
//        } else {
//            System.out.println("Generated " + validMoves.size() + " valid moves for player " + player);
//        }
        return validMoves;
    }


    @Override
    public State<Hex> next(Move<Hex> move) {
        //System.out.println("Entering next() method in HexState");
        HexMove hexMove = (HexMove) move;
        HexState newState = new HexState(this, 1 - this.currentPlayer); // Make sure this creates a genuinely new state that reflects the move
        newState.applyMove(hexMove); // This method should alter the newState's board
        //System.out.println(hexMove.describeMove());
        return newState;
    }

    public void applyMove(HexMove move) {
        int x = move.getX();
        int y = move.getY();
        if (board[x][y] == -1) {
            board[x][y] = move.player();
            updateUnionFind(x, y);  // Update the UnionFind structure for connectivity
            //System.out.println(move.describeMove() + " by " + getPlayerName(move.player()));
        } else {
            //System.out.println("Failed to apply move at (" + x + ", " + y + "), cell already occupied.");
            throw new IllegalStateException("Attempt to apply move to occupied cell");  // This line throws the error
        }
    }


    /**
     * sj
     * Checks for a winner using the union-find structure.
     *
     * @return the winning player's index, or -1 if no winner.
     */
    private int checkWinner() {
        // Check if top virtual node is connected to bottom virtual node
        int topVirtual = BOARD_SIZE * BOARD_SIZE;
        int bottomVirtual = topVirtual + 1;
        if (unionFind.connected(topVirtual, bottomVirtual)) {
            return currentPlayer; // Current player wins
        }
        return -1; // No winner yet
    }

    /**
     * Updates the Union-Find structure after a move is made.
     */
    public void updateUnionFind(int x, int y) {
        int index = x * BOARD_SIZE + y; // Convert 2D position to 1D for UnionFind
        // Directions array for 6 possible Hex neighbors
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, -1}, {-1, 1}};
        for (int[] dir : directions) {
            int nx = x + dir[0], ny = y + dir[1];
            if (nx >= 0 && nx < BOARD_SIZE && ny >= 0 && ny < BOARD_SIZE && board[nx][ny] == board[x][y]) {
                unionFind.union(index, nx * BOARD_SIZE + ny); // Union this cell with its neighbor
            }
        }

        // Special cases for border connections
        if (currentPlayer == 0) { // Assuming player 0 connects top to bottom
            if (x == 0) { // Connect to virtual top node
                unionFind.union(index, BOARD_SIZE * BOARD_SIZE);
            }
            if (x == BOARD_SIZE - 1) { // Connect to virtual bottom node
                unionFind.union(index, BOARD_SIZE * BOARD_SIZE + 1);
            }
        } else { // Assuming player 1 connects left to right
            if (y == 0) { // Connect to virtual left node
                unionFind.union(index, BOARD_SIZE * BOARD_SIZE);
            }
            if (y == BOARD_SIZE - 1) { // Connect to virtual right node
                unionFind.union(index, BOARD_SIZE * BOARD_SIZE + 1);
            }
        }
    }

    // Method to get player's name
    public String getPlayerName(int player) {
        return switch (player) {
            case 0 -> "Player Red";
            case 1 -> "Player Blue";
            default -> "Unknown Player";
        };
    }

    public int[][] getBoard() {
        return board;
    }

    public WQUPC getUnionFind() {
        return unionFind;
    }


    @Override
    public Random random() {
        return random; // Return the Random object associated with this state
    }
}