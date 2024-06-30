package edu.neu.coe.info6205.mcts.tictactoe;

import edu.neu.coe.info6205.mcts.core.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MCTS {
    private static final int SIMULATIONS = 1000;
    private static final Random random = new Random();
    private Node<TicTacToe> root;


    // Define a node for the MCTS algorithm
    static class TreeNode {
        int visits;
        double score;
        Board state;
        List<TreeNode> children;
        TreeNode parent; // Add a reference to the parent

        public TreeNode(Board state, TreeNode parent) {
            this.state = state;
            this.parent = parent;
            visits = 0;
            score = 0.0;
            children = new ArrayList<>();
        }
    }

    public static int[] findBestMove(Board board) {
        TreeNode root = new TreeNode(board, null);
        for (int i = 0; i < SIMULATIONS; i++) {
            TreeNode node = selectNode(root);
            expandNode(node);
            double score = simulatePlayout(node);
            backpropagate(node, score);
        }
        return getBestMove(root);
    }

    static TreeNode selectNode(TreeNode node) {
        TreeNode selected = null;
        double bestValue = Double.MIN_VALUE;
        while (!node.children.isEmpty()) {
            for (TreeNode child : node.children) {
                double ucbValue = ucbValue(child, node.visits);
                if (ucbValue > bestValue) {
                    bestValue = ucbValue;
                    selected = child;
                }
            }
            node = selected;
        }
        return node;
    }

    private static double ucbValue(TreeNode child, int totalVisits) {
        if (child.visits == 0) {
            return Double.MAX_VALUE; // to ensure that every node is visited at least once
        }
        double winRate = child.score / child.visits;
        double explorationFactor = Math.sqrt(Math.log(totalVisits) / child.visits);
        double c = Math.sqrt(2); // exploration parameter, typical values are between 1/sqrt(2) and sqrt(2)
        return winRate + c * explorationFactor;
    }

    static double simulatePlayout(TreeNode node) {
        // Simulate a playout from the given node and return the result
        Board state = node.state;
        while (!state.isFull() && !state.checkWinner('X') && !state.checkWinner('O')) {
            char player = (state.getBoard()[0][0] == 'X') ? 'O' : 'X'; // Alternating players
            List<int[]> emptyCells = state.getEmptyCells(); // Call getEmptyCells() from an instance of Board
            int[] randomCell = emptyCells.get(random.nextInt(emptyCells.size()));
            state.makeMove(randomCell[0], randomCell[1], player);
        }
        if (state.checkWinner('X')) {
            return 1.0; // X wins
        } else if (state.checkWinner('O')) {
            return 0.0; // O wins
        } else {
            return 0.5; // Draw
        }
    }


    static void backpropagate(TreeNode node, double score) {
        // Update the visit count and score of the current node and its ancestors
        while (node != null) {
            node.visits++;
            node.score += score;
            node = node.parent; // Move to the parent node until the root
        }
    }


    static void expandNode(TreeNode node) {
        Board state = node.state;
        char player = (node.children.size() % 2 == 0) ? 'X' : 'O';
        List<int[]> possibleMoves = state.getEmptyCells();
        for (int[] move : possibleMoves) {
            Board newState = new Board(state); // Assuming copy constructor
            newState.makeMove(move[0], move[1], player);
            TreeNode childNode = new TreeNode(newState, node);
            node.children.add(childNode);
        }
    }

    public static void printWinner(Board board) {
        while (!board.isFull() && !board.checkWinner('X') && !board.checkWinner('O')) {
            int[] bestMove = findBestMove(board);
            char currentPlayer = (board.getTurnCount() % 2 == 0) ? 'X' : 'O'; // Assuming you have a method to count turns
            board.makeMove(bestMove[0], bestMove[1], currentPlayer);
            System.out.println("Move made by " + currentPlayer + " at (" + bestMove[0] + ", " + bestMove[1] + ")");
        }

        if (board.checkWinner('X')) {
            System.out.println("X wins!");
        } else if (board.checkWinner('O')) {
            System.out.println("O wins!");
        } else {
            System.out.println("It's a draw!");
        }
    }


    private static int[] getBestMove(TreeNode root) {
        TreeNode bestNode = null;
        double bestScore = -Double.MAX_VALUE;
        for (TreeNode child : root.children) {
            double childScore = child.score / child.visits; // Assuming more score is better
            if (childScore > bestScore) {
                bestScore = childScore;
                bestNode = child;
            }
        }
        if (bestNode == null) return new int[] {-1, -1}; // No valid move found
        return findMoveDifference(root.state, bestNode.state);
    }

    private static int[] findMoveDifference(Board original, Board newState) {
        // Implement a method to find which move was made between original and newState
        // This typically checks the board for differences
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (original.getBoard()[i][j] != newState.getBoard()[i][j]) {
                    return new int[] {i, j};
                }
            }
        }
        return new int[] {-1, -1};
    }

    public static void main(String[] args) {
        MCTS mcts = new MCTS(new TicTacToeNode(new TicTacToe().new TicTacToeState()));
        Node<TicTacToe> root = mcts.root;

        Board board = new Board();
        int[] bestMove = findBestMove(board);
        System.out.println("Best move: " + bestMove[0] + ", " + bestMove[1]);
        printWinner(board);
    }
public MCTS(Node<TicTacToe> root) {
    this.root = root;
}
}
