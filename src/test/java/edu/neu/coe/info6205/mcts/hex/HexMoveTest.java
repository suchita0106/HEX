package edu.neu.coe.info6205.mcts.hex;

import org.junit.Test;
import static org.junit.Assert.*;

public class HexMoveTest {

    /**
     * Test Constructor and Property Accessors
     */
    @Test
    public void testConstructorAndAccessors() {
        int player = 0;
        int x = 3;
        int y = 5;
        HexMove move = new HexMove(player, x, y);

        assertEquals("Constructor should set player correctly.",player, move.player());
        assertEquals("Constructor should set x coordinate correctly.",x, move.getX());
        assertEquals("Constructor should set y coordinate correctly.",y, move.getY());
    }

    /**
     * Test Move Description
     */
    @Test
    public void testMoveDescription() {
        HexMove move = new HexMove(1, 2, 3); // Player 1 moves at coordinates (C4)
        String expectedDescription = "Player 1 moves at coordinates (C4)";
        assertEquals("describeMove should return a correctly formatted string.",expectedDescription, move.describeMove());
    }

    /**
     * Test Coordinate Conversion
     */
    @Test
    public void testCoordinateConversion() {
        HexMove move = new HexMove(1, 0, 0); // Player 1 moves at top-left corner, which should be A1
        String expectedDescription = "Player 1 moves at coordinates (A1)";
        assertEquals("describeMove should correctly convert coordinates.",expectedDescription, move.describeMove());

        HexMove moveAnother = new HexMove(1, 25, 99); // Example for larger indices
        String anotherDescription = "Player 1 moves at coordinates (Z100)";
        assertEquals("describeMove should correctly convert larger indices.",anotherDescription, moveAnother.describeMove());
    }

    /**
     * Negative Test for Coordinate Conversion
     */
    @Test
    public void testNegativeCoordinates() {
        HexMove move = new HexMove(1, -1, -1);
        String expectedDescription = "Player 1 moves at coordinates (@0)"; // Checking bounds for char calculation
        assertEquals("describeMove should handle negative coordinates by wrapping character calculations.",expectedDescription, move.describeMove());
    }
}
