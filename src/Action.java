import java.util.Arrays;

public class Action {
    
    private int[] origin;
    private int[] destination;
    private int[] captured;
    private boolean isCaptureMove;
    char capturedPiece;

    public void newMove(int[] origin, int[] destination) {
        this.origin = origin;
        this.destination = destination;
        captured = null;
        isCaptureMove = false;
    }

    public void newCapture(int[] origin, int[] destination, int[] captured, char capturedPiece) {
        this.origin = origin;
        this.destination = destination;
        this.captured = captured;
        isCaptureMove = true;
        this.capturedPiece = capturedPiece;
    }

    public int[] getOrigin() {
        return origin;
    }

    public int[] getDestination() {
        return destination;
    }

    public int[] getCaptured() {
        return captured;
    }

    public boolean isCapture() {
        return isCaptureMove;
    }

    public char getCapturedPiece() {
        return capturedPiece;
    }

    public boolean equals(Object other) {
        Action otherAction = (Action) other;
        return Arrays.equals(origin, otherAction.getOrigin()) && Arrays.equals(destination, otherAction.getDestination());
    }

    public String toString() {
        String toString = "";
        if(isCaptureMove)
            toString += "CAPTURE >> ";
        else
            toString += "MOVE >> ";
        toString += origin[0] + ", " + origin[1] + " to " + destination[0] + ", " + destination[1];

        return toString;
    }

}