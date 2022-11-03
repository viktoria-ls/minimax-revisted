import java.util.ArrayList;
import java.util.Scanner;

public class MCO2 {
    static Checkers checkers = new Checkers();
    static Minimax minimax = new Minimax();
    static State currentState = checkers.initGame(true);
    static ArrayList<ActionSequence> userActions;
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println("GAME START!");
        System.out.println("HOW TO PLAY: Enter the row and column indices by separating them with a space.");
        currentState.getBoard().displayBoard();
        aiMove();
    }

    public static void aiMove() {
        currentState.setDepth(0);
        int score = minimax.minimax(currentState, checkers, Integer.MIN_VALUE, Integer.MAX_VALUE, 6);
        System.out.println("No. of States generated: " + currentState.getNumStates());

        if(!currentState.getChildren().isEmpty()) {
            for(State child : currentState.getChildren()) {
                if(child.getScore() == score) {
                    System.out.println();
                    for(Action a : child.getActionSequence().getActionSequence()){
                        checkers.applyAction(currentState.getBoard(), a);
                        System.out.println(a.toString());
                        currentState.getBoard().displayBoard();
                    }
                    currentState = child;
                    break;
                }
            }
            userActions = checkers.actions(currentState);
            userMove();
        }
        else {
            if(score == 0)
                System.out.println("GAME OVER! It's a DRAW!");
            else
                System.out.println("GAME OVER! MIN USER WINS!");
            sc.close();
        }
        
    }

    public static void userMove() {
        if(!userActions.isEmpty()) {
            ArrayList<Action> performed = new ArrayList<>();
            boolean hasMove = true;
    
            while(hasMove) {
                int[] origin = new int[2];
                int[] dest = new int[2];
                System.out.print("Origin space: ");
                origin[0] = sc.nextInt();
                origin[1] = sc.nextInt();
                
                System.out.print("Dest space: ");
                dest[0] = sc.nextInt();
                dest[1] = sc.nextInt();

                Action action = new Action();

                if(Math.abs(origin[0] - dest[0]) > 1) {
                    int[] captured = {Math.max(origin[0], dest[0]) - 1, Math.max(origin[1], dest[1]) - 1};
                    action.newCapture(origin, dest, captured, currentState.getBoard().getTile(captured[0], captured[1]));
                }
                else
                    action.newMove(origin, dest);

                performed.add(action);

                hasMove = false;

                if(isValidMove(performed)) {
                    System.out.println();
                    checkers.applyAction(currentState.getBoard(), performed.get(performed.size() - 1));
                    System.out.println((performed.get(performed.size() - 1).toString()));
                    currentState.getBoard().displayBoard();
        
                    hasMove = false;
        
                    for(ActionSequence a : userActions) {
                        if(a.startsWith(performed)) {
                            if(a.getActionSequence().size() - performed.size() > 0) {
                                hasMove = true;
                                break;
                            }
                        }
                    }
                }
                else {
                    System.out.println("Invalid move");
                    performed.remove(performed.size() - 1);
                    hasMove = true;
                }

            }
    
            for(State child : currentState.getChildren()) {
                if(child.getActionSequence().startsWith(performed)) {
                    currentState = child;
                    break;
                }
            }
    
            aiMove();
        }
        else {
            System.out.println("GAME OVER! MAX USER WINS!");
            sc.close();
        }
        
    }

    public static boolean isValidMove(ArrayList<Action> attempt) {
        for(ActionSequence a : userActions)
            if(a.startsWith(attempt))
                return true;
        return false;
    }
}