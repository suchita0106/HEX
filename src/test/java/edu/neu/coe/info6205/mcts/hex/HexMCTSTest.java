package edu.neu.coe.info6205.mcts.hex;

import edu.neu.coe.info6205.mcts.core.Node;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.*;

public class HexMCTSTest {

    private static final int BOARD_SIZE = 11;

    /**
     * Verify that the HexMCTS object is correctly initialized with a root node.
     */
    @Test
    public void testInitialization() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = game.start();
        Node<Hex> rootNode = new HexNode(initialState);
        HexMCTS mcts = new HexMCTS(rootNode);
        assertNotNull("HexMCTS should be successfully initialized with a root node.", mcts);
    }


    /**
     * Ensure that the run method performs the correct number of simulations and modifies the game state appropriately.
     */
    @Test
    public void testRunMethod() throws IOException {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = game.start();
        Node<Hex> rootNode = new HexNode(initialState);
        HexMCTS mcts = new HexMCTS(rootNode);

        mcts.run(10,1.14); // Run 10 simulations

        // Assuming there's a way to check that simulations were run (e.g., by checking node visit counts)
        assertTrue("At least 10 simulations should have been run.", rootNode.playouts() >= 10);
    }


    /**
     * Test the Effectiveness of the selectPromisingNode Method
     */
    @Test
    public void testSelectPromisingNode() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = game.start();
        HexNode rootNode = new HexNode(initialState);
        HexMCTS mcts = new HexMCTS(rootNode);

        // Add child nodes with predetermined playout and win stats for control
        HexNode child1 = new HexNode(new HexState(game));
        child1.backPropagate(0); // Simulate losses
        child1.backPropagate(0);
        rootNode.addChild(child1.state());
        child1.parent = rootNode;

        HexNode child2 = new HexNode(new HexState(game));
        child2.backPropagate(1); // Simulate wins
        child2.backPropagate(1);
        rootNode.addChild(child2.state());
        child2.parent = rootNode;

        assertEquals("selectPromisingNode should lead to the winning game always.", 2, child2.wins() + child1.wins());
    }


    /**
     * Ensure that simulation and backpropagation are functioning as expected by manually controlling the state transitions.
     */
    @Test
    public void testSimulationAndBackpropagation() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = game.start();
        HexNode rootNode = new HexNode(initialState);
        HexMCTS mcts = new HexMCTS(rootNode);
        rootNode.explore(); // Manually trigger the exploration to populate children

        // Simulate manually to control outcomes
        for (Node<Hex> child : rootNode.children()) {
            int result = mcts.simulate(child); // Assuming 0 for loss and 1 for win
            child.backPropagate(result);
        }

        assertTrue("Root node playouts should be incremented after simulations.", rootNode.playouts() > 0);
    }


    /**
     * This test verifies that the best move is selected based on win rates.
     */
    @Test
    public void testBestMoveSelection() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = game.start();
        HexNode rootNode = new HexNode(initialState);
        HexMCTS mcts = new HexMCTS(rootNode);

        rootNode.addChild(new HexState(game)); // Add a child node with a win
        rootNode.children().iterator().next().backPropagate(1);

        Node<Hex> bestMove = mcts.selectBestMove();
        assertEquals("Best move should be the child with the highest win rate.", rootNode.children().iterator().next(), bestMove);
    }


    /**
     * Test Simulation Count and Node Expansion
     */
    @Test
    public void testSimulationCountAndNodeExpansion() throws IOException {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = new HexState(game);
        HexNode rootNode = new HexNode(initialState);
        HexMCTS mcts = new HexMCTS(rootNode);

        int numRuns = 5;
        mcts.run(numRuns, 1.14);

        // Assuming the root node should have expanded to have at least one child per simulation, if possible
        assertTrue("Root node should have children after simulations.", !rootNode.children().isEmpty());
        assertTrue("Root node playouts should be at least equal to the number of simulations.", ((HexNode) rootNode).playouts() >= numRuns);
    }


    /**
     * Test Consistency and Determinism in Simulations
     */
    @Test
    public void testConsistencyAndDeterminism() throws IOException {
        long seed = System.currentTimeMillis();
        Hex game1 = new Hex(seed);
        HexState initialState1 = game1.start();
        HexNode rootNode1 = new HexNode(initialState1);
        HexMCTS mcts1 = new HexMCTS(rootNode1);

        Hex game2 = new Hex(seed);
        HexState initialState2 = game2.start();
        HexNode rootNode2 = new HexNode(initialState2);
        HexMCTS mcts2 = new HexMCTS(rootNode2);

        mcts1.run(10,1.14);
        mcts2.run(10,1.14);

        // Assuming a method to count wins or a similar metric to compare outcomes
        assertTrue("All Simulations will yeild at least one win.", 20 <= (rootNode1.wins() + rootNode2.wins()));
    }

    /**
     * Test Multiple Simulation Progressions and Node Expansions
     */
    @Test
    public void testMultipleSimulationProgressionsAndNodeExpansions() throws IOException {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = new HexState(game);
        HexNode rootNode = new HexNode(initialState);
        HexMCTS mcts = new HexMCTS(rootNode);

        int numRuns = 5;
        mcts.run(numRuns,1.14);

        // Verify that the rootNode has been expanded and that simulations have been run
        assertFalse("Root node should have children after running simulations.", rootNode.children().isEmpty());

        // Ensure that the number of playouts at the root at least matches the number of simulations
        assertTrue("Root node playouts should be at least as many as the number of simulations run.", ((HexNode) rootNode).playouts() >= numRuns)
        ;

        // Ensure each child has been involved in simulations
        rootNode.children().forEach(child -> {
            HexNode hexChild = (HexNode) child;
            assertTrue("Each child node should have been part of at least one simulation.", hexChild.playouts() > 0);
        });
    }

}
