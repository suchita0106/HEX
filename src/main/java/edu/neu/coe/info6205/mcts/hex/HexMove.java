package edu.neu.coe.info6205.mcts.hex;

import edu.neu.coe.info6205.mcts.core.Move;

public class HexMove implements Move<Hex> {
    private final int player;
    private final int x, y;

    public HexMove(int player, int x, int y) {
        this.player = player;
        this.x = x;
        this.y = y;
    }

    @Override
    public int player() {
        return player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String describeMove() {
        return "Player " + player + " moves at coordinates (" + String.valueOf((char) ('A' + x)) + (y + 1) + ")";
    }

}
