package edu.neu.coe.info6205.mcts.hex;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class HexTest {

    /**
     *  Test Constructor and Random Initialization
     */
    @Test
    public void testConstructorAndRandomInitialization() {
        long seed = 12345L;
        Hex game = new Hex(seed);
        assertNotNull("Random object should not be null.", game.getRandom());

        // Test random consistency
        Random expectedRandom = new Random(seed);
        assertEquals("Random should generate consistent results with the same seed.",expectedRandom.nextLong(), game.getRandom().nextLong());
    }

    /**
     *   Test Game Start Method
     */
    @Test
    public void testGameStartMethod() {
        Hex game = new Hex(123L);
        HexState state = game.start();
        assertNotNull("start() should return a non-null HexState.",state);
        assertEquals("The returned HexState should be associated with the Hex game instance.",game, state.game());
    }

    /**
     *  Test Opener Method
     */
    @Test
    public void testOpener() {
        Hex game = new Hex(123L);
        int opener = game.opener();
        assertEquals("opener() should return 1 indicating that player 1 starts the game.",1, opener);
    }

    /**
     *  Test Random Object Consistency
     */
    @Test
    public void testRandomObjectConsistency() {
        long seed = 123L;
        Hex game1 = new Hex(seed);
        Hex game2 = new Hex(seed);

        long randomValue1 = game1.getRandom().nextLong();
        long randomValue2 = game2.getRandom().nextLong();

        assertEquals("Random objects initialized with the same seed should produce the same output.",randomValue1, randomValue2);
    }
}
