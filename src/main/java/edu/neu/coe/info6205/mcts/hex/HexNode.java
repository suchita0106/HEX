package edu.neu.coe.info6205.mcts.hex;
import edu.neu.coe.info6205.mcts.core.Node;
import edu.neu.coe.info6205.mcts.core.State;
import edu.neu.coe.info6205.mcts.core.Move;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HexNode implements Node<Hex> {
    private final HexState state;
    private final HexMove lastMove;
    private final List<HexMove> moveHistory;
    private final ArrayList<Node<Hex>> children = new ArrayList<>();
    private int wins;
    private int playouts;


    public Node<Hex> parent;

    private int result = -1;

    public HexNode(HexState state) {
        this.state = state;
        this.lastMove = null;
        this.moveHistory = new ArrayList<>();
    }

    public HexNode(HexState state, List<HexMove> history) {
        this.state = state;
        this.lastMove = history.isEmpty() ? null : history.get(history.size() - 1);
        this.moveHistory = new ArrayList<>(history); // Create a new list based on the passed history
    }

    public List<HexMove> getMoveHistory() {
        return moveHistory;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty() && state.isTerminal();
    }

    @Override
    public State<Hex> state() {
        return state;
    }

    @Override
    public boolean white() {
        return state.player() == state.game().opener();
    }

    @Override
    public Collection<Node<Hex>> children() {
        return children;
    }

    @Override
    public void backPropagate(int result) {
        playouts++;
        if (result != -1 && result == state.player()) {
            wins++;
        }
        if (parent != null) {
            parent.backPropagate(result);
        }
        //System.out.println("Backpropagating: Node at " + System.identityHashCode(this) + " playouts=" + playouts + ", wins=" + wins);
    }

    @Override
    public void backPropagate() {
        // Update this node's statistics based on the result stored in the node
        playouts++;
        if (result == state.player()) {
            wins++;
        }
        if (parent != null) {
            ((HexNode) parent).backPropagate();
        }
    }

    public HexMove getLastMove() {
        return lastMove;
    }

    @Override
    public void explore() {
        if (!isLeaf() && children.isEmpty()) { // Only explore if it's not a leaf and children haven't been generated
            Collection<Move<Hex>> moves = state.moves(state.player());
            if (moves.isEmpty()) {
                System.out.println("No valid moves to explore from this node.");
            } else {
                moves.forEach(move -> {
                    State<Hex> newState = state.next(move);
                    addChild(newState, move); // Pass the move along with the new state
                });
            }
        }
    }

    @Override
    public void addChild(State<Hex> state) {
        HexNode child = new HexNode((HexState) state);
        child.parent = this; // Set parent for backpropagation
        children.add(child);
    }

    public void addChild(State<Hex> state, Move<Hex> move) {
        if (state instanceof HexState && move instanceof HexMove) {
            List<HexMove> newHistory = new ArrayList<>(moveHistory); // Copy current history
            newHistory.add((HexMove) move); // Add the new move to the history
            HexNode child = new HexNode((HexState) state, newHistory);
            child.parent = this; // Set the parent for backpropagation
            children.add(child);
            //System.out.println("Added new child node for move: " + ((HexMove) move).describeMove());
        }
    }

    @Override
    public int wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }

    public Node<Hex> getParent() {
        return parent;
    }


    @Override
    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }
}
