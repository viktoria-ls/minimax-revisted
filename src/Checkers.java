import java.util.ArrayList;

public class Checkers {
    private boolean ordering;

    public State initGame(boolean ordering) {
        this.ordering = ordering;
        State initState = new State(new Board(), true, null, 0, null);
        return initState;
    }

    public State result(State s, ActionSequence sequence) {
        Board newBoard = new Board(s.getBoard().copyTiles());
        for(Action a : sequence.getActionSequence())
            applyAction(newBoard, a);

        State result = new State(newBoard, !s.isMaxTurn(), s, s.getDepth() + 1, sequence);
        s.addChild(result);
        return result;
    }

    public void applyAction(Board board, Action action) {
        char movedPiece = board.getTile(action.getOrigin()[0], action.getOrigin()[1]);
        board.setTile(action.getOrigin()[0], action.getOrigin()[1], ' ');
        board.setTile(action.getDestination()[0], action.getDestination()[1], movedPiece);

        if(board.isMaxEdge(action.getDestination()[0]) != null && board.isMaxEdge(action.getDestination()[0]) != board.isMax(action.getDestination()))
            board.setKing(action.getDestination()[0], action.getDestination()[1]);

        if(action.isCapture())
            board.setTile(action.getCaptured()[0], action.getCaptured()[1], ' ');
    }

    public int eval(State s, boolean cutoff) {
        Board board = s.getBoard();
        int minScore = 0;
        int maxScore = 0;

        int minPieces = 0;
        int maxPieces = 0;

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                if(board.getTile(i, j) != ' ') {
                    int[] piece = {i, j};

                    if(s.getBoard().isMax(piece)) {
                        if(s.getBoard().isKing(piece))
                            maxScore += 5;
                        maxPieces++;
                        if(hasBackUp(i-1, j-1, true, board)) // NW
                            maxScore++;
                        if(hasBackUp(i-1, j+1, true, board)) // NE
                            maxScore++;
                        if(piece[1] != 0 && piece[1] != 7)
                            maxScore += 3;
                        if(piece[0] == 0)
                            maxScore += 6;
                    }

                    else {
                        if(s.getBoard().isKing(piece))
                            minScore += 5;
                        minPieces++;
                        if(hasBackUp(i+1, j-1, false, board)) // SW
                            minScore++;
                        if(hasBackUp(i+1, j+1, false, board)) // SE
                            minScore++;
                        if(piece[1] != 0 && piece[1] != 7)
                            minScore += 3;
                        if(piece[0] == 7)
                            minScore += 6;
                    }
                }


        maxScore += maxPieces;
        minScore += minPieces;

        if(cutoff == true) {
            if(minScore == maxScore)
                return 0;
            else if(Math.max(maxScore, minScore) == minScore)
                return minScore * -1;
            else
                return maxScore;
        }

        else { // if terminal node
            if(maxPieces == 0) // min wins
                return minScore * -1;
            if(minPieces == 0) // max wins
                return maxScore;
            else // draw
                return 0;
        }
    }

    public boolean hasBackUp(int diagRow, int diagCol, boolean isMax, Board board) {
        if(!isOutOfBounds(diagRow, diagCol)) {
            int[] diagPiece = {diagRow, diagCol};
            return board.isMax(diagPiece) == isMax;
        }
        return false;
    }

    public ArrayList<ActionSequence> actions(State s) {
        ArrayList<ActionSequence> actions = new ArrayList<>();

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                int[] currTile = {i, j};
                char piece = s.getBoard().getTile(i, j);
                if(piece != ' ' && s.getBoard().isMax(currTile) == s.isMaxTurn())
                    actions.addAll(getValidMoves(currTile, s.getBoard(), new ArrayList<>()));
            }
        }

        boolean hasCapture = false;

        for(ActionSequence a : actions) {
            if(a.getActionSequence().get(0).isCapture())
                hasCapture = true;
            computeOrdering(a, s.getBoard());
        }

        if(hasCapture)
            actions.removeIf(action -> !action.getActionSequence().get(0).isCapture());

        ordering(actions, ordering, s.getBoard());

        return actions;
    }

    private boolean isOutOfBounds(int row, int col) {
        return (row >= 8 || col >= 8 || row < 0 || col < 0);
    }

    private int[][] getDir(char piece) {
        int[][] maxDir = {{1, -1}, {1, 1}};
        int[][] minDir = {{-1, -1}, {-1, 1}};
        int[][] kingDir = {{1, -1}, {1, 1}, {-1, -1}, {-1, 1}};


        if(piece == 'R' || piece == 'W')
            return kingDir;
        if(piece == 'r')
            return maxDir;
        else
            return minDir;
    }

    // Returns an array of direction values that have captures
    private ArrayList<int[]> getAttackMoves(int[] origin, Board board) {
        char originPiece = board.getTile(origin[0], origin[1]);
        int[][] possibleDirections = getDir(originPiece);
        ArrayList<int[]> attackMoves = new ArrayList<>();
        
        for(int[] dir : possibleDirections) {
            int destRow = origin[0] + dir[0];
            int destCol = origin[1] + dir[1];

            if(!isOutOfBounds(destRow, destCol)) {
                int[] dest = {destRow, destCol};
                if(board.getTile(destRow, destCol) != ' ') {
                    if(board.isMax(dest) != board.isMax(origin)) {
                        int landingRow = origin[0] + dir[0] * 2;
                        int landingCol = origin[1] + dir[1] * 2;
                        if(!isOutOfBounds(landingRow, landingCol) && board.getTile(landingRow, landingCol) == ' ')
                            attackMoves.add(dir); 
                    }
                }
            }
                       
        }

        return attackMoves;
    }

    private boolean expandActionSequence(int[] origin, Board board, ArrayList<Action> actionSequence, ArrayList<ActionSequence> allMoves) {
        ArrayList<int[]> attackMoves = getAttackMoves(origin, board);
        boolean hasMoves = false;

        if (!attackMoves.isEmpty()) {
            hasMoves = true;
            for (int[] dir: attackMoves) {
                ArrayList<Action> localActionSequence = new ArrayList<>(actionSequence);
                int[] captureTile = {origin[0] + dir[0], origin[1] + dir[1]};
                int[] landingTile = {origin[0] + dir[0] * 2, origin[1] + dir[1] * 2};
                Action attackAction = new Action();
                attackAction.newCapture(origin, landingTile, captureTile, board.getTile(captureTile[0], captureTile[1]));
                localActionSequence.add(attackAction);
                
                board.doMove(attackAction);
                expandActionSequence(landingTile, board, localActionSequence, allMoves);
                board.undoMove();
                
            }
        }
        if(!hasMoves) { // Terminal Node
            if (!actionSequence.isEmpty())
                allMoves.add(new ActionSequence(actionSequence));
            return false;
        }
        return true;
    }

    private ArrayList<ActionSequence> getValidMoves(int[] origin, Board board, ArrayList<ActionSequence> allMoves) {
        if(!expandActionSequence(origin, board, new ArrayList<Action>(), allMoves)) { // If there are no possible capture moves
            int[][] directions = getDir(board.getTile(origin[0], origin[1]));
            for (int[] dir : directions) {
                ArrayList<Action> actionSequence = new ArrayList<>();
                int destRow = origin[0] + dir[0];
                int destCol = origin[1] + dir[1];

                if(!isOutOfBounds(destRow, destCol)) {
                    int[] destinationTile = {destRow, destCol};
                    if(board.getTile(destRow, destCol) == ' ') {
                        Action newAction = new Action();
                        newAction.newMove(origin, destinationTile);
                        actionSequence.add(newAction);
                        allMoves.add(new ActionSequence(actionSequence));
                    }
                }
            }
        }
        
        return allMoves;
    }

    private void computeOrdering(ActionSequence sequence, Board board) {
        int value = 0;
        int[] origin = sequence.getActionSequence().get(0).getOrigin();
        int[] lastDest = sequence.getActionSequence().get(sequence.getActionSequence().size() - 1).getDestination();

        for(Action a : sequence.getActionSequence()) {
            if(board.isMaxEdge(lastDest[0]) != null && board.isMaxEdge(lastDest[0]) != board.isMax(origin))
                value += 10;
            if(a.isCapture())
                value += 10;
            if(a.getDestination()[1] != 0 || a.getDestination()[1] != 7)
                value += 5;

            int[] diag = new int[2];
            if(a.getOrigin()[0] < a.getDestination()[0])
                diag[0] = a.getOrigin()[0] + 2;
            else
                diag[0] = a.getOrigin()[0] - 2;
            if(a.getOrigin()[1] < a.getDestination()[1])
                diag[1] = a.getOrigin()[1] + 2;
            else
                diag[1] = a.getOrigin()[1] - 2;
            
            if(!isOutOfBounds(diag[0], diag[1]) && board.isMax(diag) != board.isMax(origin))
                value -= 7;
        }

        sequence.setOrderingValue(value);
    }

    private void ordering(ArrayList<ActionSequence> sequence, boolean withOrdering, Board board) {
        if(!sequence.isEmpty()) {
            int[] piece = sequence.get(0).getActionSequence().get(0).getOrigin();
            if(withOrdering) {
                boolean isMax = board.isMax(piece);
                if(isMax)
                    sequence.sort((a1, a2) -> a2.getOrderingValue().compareTo(a1.getOrderingValue()));
                else
                    sequence.sort((a1, a2) -> a1.getOrderingValue().compareTo(a2.getOrderingValue()));
            }
        }
        
    }
}