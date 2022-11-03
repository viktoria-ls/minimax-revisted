import java.util.ArrayList;

public class Minimax {
    public int minimax(State state, Checkers game, int alpha, int beta, int limit) {
        ArrayList<ActionSequence> foundSequences = game.actions(state);
        if(foundSequences.isEmpty() || (state.getDepth() > 0 && state.getDepth() % limit == 0)) { // terminal test and cut off test
            int score;
            if(state.getDepth() > 0 && state.getDepth() % limit == 0)
                score = game.eval(state, true);
            else
                score = game.eval(state, false);
            state.setScore(score);
            return score;
        }

        if(state.isMaxTurn()) {
            int bestVal = Integer.MIN_VALUE;
            for(ActionSequence a : foundSequences) {
                State resultState = game.result(state, a);
                bestVal = Math.max(bestVal, minimax(resultState, game, alpha, beta, limit));
                if(bestVal >= beta) {
                    state.setScore(bestVal);
                    return bestVal;
                }
                alpha = Math.max(alpha, bestVal);
                if(beta <= alpha)
                    break;
            }
            state.setScore(bestVal);
            return bestVal;
        }

        else {
            int bestVal = Integer.MAX_VALUE;
            for(ActionSequence a : foundSequences) {
                State resultState = game.result(state, a);
                bestVal = Math.min(bestVal, minimax(resultState, game, alpha, beta, limit));
                if(bestVal <= alpha) {
                    state.setScore(bestVal);
                    return bestVal;
                }
                beta = Math.min(beta, bestVal);
                if(beta <= alpha)
                    break;
            }
            state.setScore(bestVal);
            return bestVal;
        }
    }

}
