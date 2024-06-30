package edu.neu.coe.info6205.mcts.hex;

import edu.neu.coe.info6205.mcts.core.Move;
import edu.neu.coe.info6205.mcts.core.Node;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class HexMCTS {

    private final Node<Hex> root;
    private static final String bestNodeCsvFile = System.getProperty("user.dir") + "/bestNode.csv";
    private static final String mctsRunCsvFile = System.getProperty("user.dir") + "/mctsRunTime.csv";

    public HexMCTS(Node<Hex> root) {
        this.root = root;
    }

    public static void main(String[] args) throws IOException {

        String bestNodeHeader = "numRuns,explorationFactor,playouts,wins\n";
        Files.deleteIfExists(Paths.get(bestNodeCsvFile));
        Files.write(Paths.get(bestNodeCsvFile), bestNodeHeader.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        String mctsRunHeader = "numRuns,explorationFactor,explore_time,select_time,sim_time,bp_time\n";
        Files.deleteIfExists(Paths.get(mctsRunCsvFile));
        Files.write(Paths.get(mctsRunCsvFile), mctsRunHeader.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);


        Integer[] numRuns = {1, 2, 4, 8, 16, 32, 64, 128, 256};
        double[] explorationFactors = {0.5,1,1.14,2,4,8,16};
        for (int x : numRuns)
        {
            for (double eF : explorationFactors) {
                System.out.println("Number of Runs = " + x + " and Exploration Factor = " + eF);
                Hex game = new Hex(new Random().nextLong()); // Create a new game instance
                HexState initialState = game.start(); // Get the initial state of the game
                Node<Hex> rootNode = new HexNode(initialState); // Create a root node with the initial state

                HexMCTS mcts = new HexMCTS(rootNode); // Initialize MCTS with the root node
                mcts.run(x, eF);
            }
        }
    }

    /**
     * Perform the Monte Carlo Tree Search.
     *
     * @param numRuns the number of simulations to run
     */
    public void run(int numRuns, double explorationFactor) throws IOException {
        Node<Hex> node = null;
        for (int i = 1; i <= numRuns; i++) {
            node = mcts(root, numRuns, explorationFactor);
        }

        // Print last simulation's Move history and board for the video demo
        List<HexMove> history = ((HexNode) node).getMoveHistory();
        String moveList = "";
        for (HexMove move : history) {
            //System.out.println(move.describeMove());
            moveList = moveList + convertXToLetter(move.getX() + 1) + (Integer.toString(move.getY() + 1)) + " ";
        }
        System.out.println("Move history:" + moveList);

        // Display game results after simulations
        displayGameResults(moveList, numRuns, explorationFactor);
    }

    private void displayGameResults(String moveList, int numRuns, double explorationFactor) throws IOException {
        DisplayHexGame hexGame = new DisplayHexGame();
        hexGame.simulateGameFromLine(moveList);
        hexGame.printHexBoard();

        Node<Hex> bestMoveNode = selectBestMove();
        if (bestMoveNode instanceof HexNode) {
            HexMove bestMove = ((HexNode) bestMoveNode).getLastMove();
            if (bestMove != null) {
                System.out.println("Winning Node Statistics: # Traversals = " + bestMoveNode.playouts() + " # Wins = " + bestMoveNode.wins());
                String data = numRuns + "," + explorationFactor + "," + bestMoveNode.playouts() + "," + bestMoveNode.wins() + "\n";
                Files.write(Paths.get(bestNodeCsvFile), data.getBytes(), StandardOpenOption.APPEND);
            } else {
                System.out.println("Root node or no move made.");
            }
        } else {
            System.out.println("No valid moves could be determined from simulations.");
        }
        System.out.println("\n");
    }

    private Node<Hex> mcts(Node<Hex> node, int numRuns, double explorationFactor) {
        long explore_time = 0;
        long select_time = 0;
        long sim_time = 0;
        long bp_time = 0;

        Node<Hex> current = node;
        while (current != null && !current.isLeaf()) {
            // Expansion
            long explore_start_time = System.currentTimeMillis();
            if (current.children().isEmpty()) {
                current.explore();
                explore_time += System.currentTimeMillis() - explore_start_time;

                // Simulation
                for (Node<Hex> child : current.children()) {
                    long sim_start_time = System.currentTimeMillis();
                    int result = simulate(child);
                    //System.out.println("Game over. Winner: " + ((HexState) node.state()).getPlayerName(result));
                    sim_time += System.currentTimeMillis() - sim_start_time;

                    // Backpropagation
                    long bp_start_time = System.currentTimeMillis();
                    child.backPropagate(result);
                    bp_time += System.currentTimeMillis() - bp_start_time;
                }
            }

            // Selection
            long select_start_time = System.currentTimeMillis();
            if (!current.children().isEmpty()) {
                current = selectPromisingNode(current, explorationFactor);
                select_time += System.currentTimeMillis() - select_start_time;
            } else {
                break;
            }
        }

        // Save timings to a file
        try {
            FileOutputStream fis = new FileOutputStream(mctsRunCsvFile, true);
            OutputStreamWriter isr = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(isr);
            String content = String.valueOf(numRuns) + "," + String.valueOf(explorationFactor) + "," + String.valueOf(explore_time) + "," + String.valueOf(select_time) + "," + String.valueOf(sim_time) + "," + String.valueOf(bp_time) + "\n";
            bw.write(content);
            bw.flush();
            bw.close();

        } catch (IOException e) {
            System.err.println("Error handling file operations: " + e.getMessage());
        }

        return current;
    }


    private String convertXToLetter(int x) {
        return String.valueOf((char) ('A' + x - 1));
    }

    public int simulate(Node<Hex> node) {
        // Clone the state using the new copy constructor
        HexState state = new HexState((HexState) node.state());

        Random random = new Random();

        // Continue the game until a terminal condition is met
        while (!state.isTerminal()) {
            Collection<Move<Hex>> moves = state.moves(state.player());

            // Select a random move from the available moves
            List<Move<Hex>> moveList = new ArrayList<>(moves);
            Move<Hex> selectedMove = moveList.get(random.nextInt(moveList.size()));

            // Apply the move to the state
            state = (HexState) state.next(selectedMove);
        }

        // After exiting the loop, check for a winner
        Optional<Integer> winner = state.winner();
        return winner.orElse(-1);
    }


    public Node<Hex> selectPromisingNode(Node<Hex> node, double explorationFactor) {
        Node<Hex> selectedNode = null;

        double bestValue = Double.NEGATIVE_INFINITY;
        for (Node<Hex> child : node.children()) {
            if (child.playouts() > 0) {
                double ucbValue = (double) child.wins() / child.playouts() +
                        explorationFactor * Math.sqrt(Math.log(node.playouts()) / child.playouts());
                if (ucbValue > bestValue) {
                    bestValue = ucbValue;
                    selectedNode = child;
                }
            }
        }

        return selectedNode;
    }


    /**
     * Select the best move from the root based on the simulation outcomes.
     *
     * @return the best move node
     */
    public Node<Hex> selectBestMove() {
        Node<Hex> bestMove = null;
        double bestWinRate = Double.NEGATIVE_INFINITY;

        for (Node<Hex> child : root.children()) {
            if (child.playouts() > 0) {
                double winRate = (double) child.wins() / child.playouts();
                if (winRate > bestWinRate) {
                    bestWinRate = winRate;
                    bestMove = child;
                }
            }
        }
        return bestMove;

    }

}
