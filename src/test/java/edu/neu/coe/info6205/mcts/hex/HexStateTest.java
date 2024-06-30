package edu.neu.coe.info6205.mcts.hex;

import edu.neu.coe.info6205.mcts.core.Move;
import org.junit.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.*;

public class HexStateTest {
    private static final int BOARD_SIZE = 11;

    /**
     * Test that the board initializes correctly and all cells are empty.
     */
    @Test
    public void testInitialBoardState() {

        Hex game = new Hex(new Random().nextLong());
        HexState state = new HexState(game);
        int emptyCount = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (state.getBoard()[i][j] == -1) {
                    emptyCount++;
                }
            }
        }
        assertEquals("All cells should be initialized to -1 (empty).",BOARD_SIZE * BOARD_SIZE, emptyCount);
    }

    /**
     * Verify that the method returns all possible moves when the board is empty.
     */
    @Test
    public void testMoveGenerationOnEmptyBoard() {
        Hex game = new Hex(new Random().nextLong());
        HexState state = new HexState(game);
        Collection<Move<Hex>> moves = state.moves(0); // Assuming 0 is a valid player ID
        assertEquals("Should generate one move for each cell on an empty board.", BOARD_SIZE * BOARD_SIZE, moves.size());
    }

    /**
     * Check that moves are correctly applied to the board.
     */
    @Test
    public void testMoveApplication() {
        Hex game = new Hex(new Random().nextLong());
        HexState initialState = new HexState(game);
        HexMove move = new HexMove(0, 5, 5); // Assuming player 0 moves to the center of the board
        HexState newState = (HexState) initialState.next(move);

        assertEquals("Move should be applied correctly to the board.", 0, newState.getBoard()[5][5]);
        assertNotEquals("New state should reflect the move.", initialState.getBoard()[5][5], newState.getBoard()[5][5]);
    }

    /**
     * Verify that the game correctly identifies a winning condition.
     */
    @Test
    public void testTerminalState() {
        Hex game = new Hex(new Random().nextLong());
        HexState state = new HexState(game);
        // Manually create a vertical winning state from top to bottom for Player 0
        int column = 5; // Use the middle column for simplicity
        for (int i = 0; i < BOARD_SIZE; i++) {
            state.getBoard()[i][column] = 0; // Set Player 0's pieces vertically down the column
            state.updateUnionFind(i, column);
        }

        // Simulate connections that should occur in a real game scenario
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i > 0) {
                state.getUnionFind().union(
                        i * BOARD_SIZE + column,
                        (i - 1) * BOARD_SIZE + column
                ); // Connect each piece to the piece directly above it
            }
        }

        // Specifically connect the top and bottom virtual nodes in the UnionFind
        state.getUnionFind().union(column, BOARD_SIZE * BOARD_SIZE); // Top virtual node
        state.getUnionFind().union((BOARD_SIZE - 1) * BOARD_SIZE + column, BOARD_SIZE * BOARD_SIZE + 1); // Bottom virtual node

        assertTrue("State should be terminal when there is a winner.", state.isTerminal());
        assertTrue("Winner should be present.",state.winner().isPresent());
        assertEquals("Player 0 should be the winner.",Optional.of(0), state.winner());
    }

    /**
     * Ensure that the deep copy constructor correctly duplicates the state.
     */
    @Test
    public void testDeepCopyConstructor() {
        Hex game = new Hex(new Random().nextLong());
        HexState originalState = new HexState(game);
        originalState.getBoard()[0][0] = 0; // Modify the original state
        HexState copiedState = new HexState(originalState);

        assertNotSame("Boards should not be the same object.", originalState.getBoard(), copiedState.getBoard());
        assertEquals("Copied state should have the same board values.", originalState.getBoard()[0][0], copiedState.getBoard()[0][0]);
    }


    /**
     * Check that illegal moves (moves on already occupied spaces) are properly rejected and that the game state doesn't change after attempting such moves.
     */
    @Test
    public void testIllegalMoveApplication() {
        Hex game = new Hex(new Random().nextLong());
        HexState state = new HexState(game);
        state.getBoard()[0][0] = 0; // Simulate an occupied cell

        HexMove illegalMove = new HexMove(1, 0, 0); // Player 1 attempts to move to an occupied cell
        Exception exception = assertThrows("Should throw an exception when trying to move to an occupied space.", IllegalStateException.class, () -> state.next(illegalMove));
        assertTrue("Exception message should indicate that the cell is occupied.", exception.getMessage().contains("occupied"));
    }

    /**
     * Verify that resetting the game (if such functionality is needed or implemented) correctly clears the board and resets the game state.
     */
    @Test
    public void testGameReset() {
        Hex game = new Hex(new Random().nextLong());
        HexState state = new HexState(game);
        state.getBoard()[0][0] = 0; // Simulate some moves
        state.getBoard()[1][1] = 1;

        state.initializeBoard(); // Assuming a reset method that clears the board and resets the player
        assertEquals("Board should be cleared after reset.",-1, state.getBoard()[0][0]);
        assertEquals("Board should be cleared after reset.", -1, state.getBoard()[1][1]);
        assertEquals("Current player should be reset to the starting player.",0, state.player());
    }

    /**
     * Verify that after copying the game state, changes to the new state do not affect the original state, ensuring true deep copying.
     */
    @Test
    public void testGameStateDeepCopyIntegrity() {
        Hex game = new Hex(new Random().nextLong());
        HexState originalState = new HexState(game);
        HexState copiedState = new HexState(originalState);

        copiedState.getBoard()[0][0] = 1; // Change the copied state
        assertNotEquals("Original state should remain unaffected by changes to the copied state.", originalState.getBoard()[0][0], copiedState.getBoard()[0][0]);
    }

}
