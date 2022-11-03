import java.util.*;

public class State {
    
    private static int numStates = 0;

    private boolean maxTurn;
    private Board boardConfig;
    private int score;
    private int depth;
    private ActionSequence sequence;

    private State parentState;
    private ArrayList<State> childrenStates;

    public State(Board board, boolean isMaxTurn, State parent, int depth, ActionSequence sequence) {
        boardConfig = board;
        maxTurn = isMaxTurn;
        score = 0;
        parentState = parent;
        childrenStates = new ArrayList<>();
        this.depth = depth;
        this.sequence = sequence;

        numStates++;
    }

    public boolean isMaxTurn() {
        return maxTurn;
    }

    public Board getBoard() {
        return boardConfig;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public State getParent() {
        return parentState;
    }

    public ArrayList<State> getChildren() {
        return childrenStates;
    }

    public void addChild(State child) {
        childrenStates.add(child);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public ActionSequence getActionSequence() {
        return sequence;
    }

    public int getNumStates() {
        return numStates;
    }

}