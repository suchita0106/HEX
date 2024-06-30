package edu.neu.coe.info6205.mcts.hex;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DisplayHexGameTest {

    private static final int BOARD_SIZE = 11;

    private DisplayHexGame hexGame;

    @Before
    public void setUp() {
        hexGame = new DisplayHexGame();
    }

    @Test
    public void testInitializeHexBoard() {
        hexGame.initializeHexBoard();

        // Check if the board is correctly initialized with dots
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                assertEquals(".", (hexGame.getBoard())[i][j]);
            }
        }
    }

    @Test
    public void testResetBoardAndCounter() {
        hexGame.resetBoardAndCounter();

        // Check if the board is reset to dots and move counter is reset to 1
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                assertEquals(".", hexGame.getBoard()[i][j]);
            }
        }
        assertEquals(1, hexGame.getMoveCounter());
    }

    @Test
    public void testSimulateGameFromLine() {
        hexGame.resetBoardAndCounter();

        // Simulate a game with known moves
        hexGame.simulateGameFromLine("A1 B2 C3 D4 E5 F6 G7 H8 I9 J10 K11");

        // Check if the board is updated correctly based on the moves
        assertEquals("R1", hexGame.getBoard()[0][0]);
        assertEquals("B1", hexGame.getBoard()[1][1]);
        assertEquals("R2", hexGame.getBoard()[2][2]);
        assertEquals("B2", hexGame.getBoard()[3][3]);
        assertEquals("R3", hexGame.getBoard()[4][4]);
        assertEquals("B3", hexGame.getBoard()[5][5]);
        assertEquals("R4", hexGame.getBoard()[6][6]);
        assertEquals("B4", hexGame.getBoard()[7][7]);
        assertEquals("R5", hexGame.getBoard()[8][8]);
        assertEquals("B5", hexGame.getBoard()[9][9]);
        assertEquals("R6", hexGame.getBoard()[10][10]);
    }


}
