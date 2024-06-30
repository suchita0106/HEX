package edu.neu.coe.info6205.mcts.hex;

import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class HexNodeTest {

    private static final int BOARD_SIZE = 11;

    /**
     * Verify that the node initializes correctly and that move history is accurately maintained.
     */
    @Test
    public void testInitializationAndMoveHistory() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = new HexState(game);
        HexNode node = new HexNode(initialState);

        assertTrue("Move history should be initially empty.", node.getMoveHistory().isEmpty());
        assertNull("There should be no last move on initialization.", node.getLastMove());
    }

    /**
     * Check if the isLeaf method correctly identifies leaf nodes based on whether the node has children or if the state is terminal.
     */
    @Test
    public void testIsNotLeafWhenChildrenPresent() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = new HexState(game);
        HexNode node = new HexNode(initialState);

        // Check if the node is a leaf when it has no children and the state is not terminal
        assertFalse("State should not be terminal initially.", initialState.isTerminal());

        // Now simulate children addition
        node.addChild(new HexState(initialState));  // Assuming addChild correctly sets up a new HexNode
        assertFalse("Node should not be a leaf when it has children.", node.isLeaf());
    }


    /**
     * Ensure that backpropagation correctly updates the node's statistics and propagates up the tree.
     */
    @Test
    public void testBackPropagation() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = new HexState(game);
        HexNode rootNode = new HexNode(initialState);
        HexNode childNode = new HexNode(initialState);
        rootNode.addChild(new HexState(game));
        childNode.parent = rootNode;

        childNode.backPropagate(0);  // Assume player 0 is the root's player

        assertEquals("Root node playouts should be incremented.",1, rootNode.playouts());
        assertEquals("Root node wins should be incremented for winning result.",1, rootNode.wins());
        assertEquals("Child node playouts should be incremented.",1, childNode.playouts());
    }


    /**
     * Check that children are added correctly and that parent-child relationships are properly established.
     */
    @Test
    public void testAddChild() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = new HexState(game);
        HexNode parentNode = new HexNode(initialState);
        HexState childState = new HexState(game);
        parentNode.addChild(childState);

        assertEquals("Parent node should have one child.",1, parentNode.children().size());
        HexNode childNode = (HexNode) parentNode.children().iterator().next();
        assertEquals("Child's parent should be set correctly.",parentNode, childNode.getParent());
    }

    /**
     * Verify that the explore method correctly generates children based on available moves.
     */
    @Test
    public void testExploreFunctionality() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = new HexState(game);
        HexNode node = new HexNode(initialState);

        // Initially, the board is empty, so exploring should generate children for each possible move
        node.explore();
        int expectedMoves = BOARD_SIZE * BOARD_SIZE;
        assertEquals("Exploring an initial state should generate moves for every cell.",expectedMoves, node.children().size());

        // Ensure children are correctly instantiated as HexNodes
        assertTrue("All children should be instances of HexNode.",node.children().stream().allMatch(child -> child instanceof HexNode));
    }


    /**
     * Verify that the result setting and subsequent retrieval logic are functioning as expected.
     */
    @Test
    public void testSetAndGetResult() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = new HexState(game);
        HexNode node = new HexNode(initialState);

        node.setResult(1);  // Assume player 1 wins in this scenario
        assertEquals("The result should be retrievable and match the set value.",1, node.getResult());
    }


    /**
     * Verify that the transition between nodes via moves reflects correctly in the game state.
     */
    @Test
    public void testNodeTransitionByMove() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = new HexState(game);
        HexNode initialNode = new HexNode(initialState);
        HexMove move = new HexMove(0, 0, 0); // Player 0 moves at top-left corner

        HexState newState = (HexState) initialState.next(move);
        initialNode.addChild(newState, move); // Adding child directly using newState

        HexNode addedNode = (HexNode) initialNode.children().iterator().next(); // Assuming there's one child added for simplicity

        assertTrue("There should be a child node added.", initialNode.children().size() == 1);
        assertEquals("The move should be applied in the new state.", 0, ((HexState)addedNode.state()).getBoard()[0][0]);
    }


    /**
     * Ensure that the move history is accurately maintained across transitions and does not alter unexpectedly.
     */
    @Test
    public void testMoveHistoryConsistency() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = new HexState(game);
        HexNode initialNode = new HexNode(initialState);

        HexMove firstMove = new HexMove(0, 0, 0);
        HexMove secondMove = new HexMove(1, 1, 1);

        // Simulate two moves leading to two states
        HexState firstState = (HexState) initialState.next(firstMove);
        HexNode firstNode = new HexNode(firstState, List.of(firstMove));

        HexState secondState = (HexState) firstState.next(secondMove);
        HexNode secondNode = new HexNode(secondState, List.of(firstMove, secondMove));

        assertEquals("Move history should include both moves.",2, secondNode.getMoveHistory().size());
        assertEquals("First move should be correctly recorded in the history.",firstMove, secondNode.getMoveHistory().get(0));
        assertEquals("Second move should be correctly recorded in the history.",secondMove, secondNode.getMoveHistory().get(1));
    }

}
