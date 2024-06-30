package edu.neu.coe.info6205.mcts.hex;
import edu.neu.coe.info6205.mcts.core.Game;

import java.util.Random;
public class Hex implements Game<Hex> {
    private final Random random;

    public Hex(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public HexState start() {
        return new HexState(this);
    }

    @Override
    public int opener() {
        return 1; // Player 1 starts
    }

    public Random getRandom() {
        return random;
    }
}