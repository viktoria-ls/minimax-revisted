import java.util.ArrayList;

public class Board {
    private char[][] tiles;
    public ArrayList<Action> moveList = new ArrayList<>();

    public Board(char[][] tiles) {
        this.tiles = tiles;
    }

    public Board() {
        tiles = new char[8][8];
        initBoard();
    }

    private void initBoard() {
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                tiles[i][j] = ' ';
        
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 8; j++)
                if((i + j) % 2 == 1)
                    tiles[i][j] = 'r';

        for(int i = 5; i < 8; i++)
            for(int j = 0; j < 8; j++)
                if((i + j) % 2 == 1)
                    tiles[i][j] = 'w';
    }

    public char getTile(int row, int col) {
        return tiles[row][col];
    }

    public void setTile(int row, int col, char val) {
        tiles[row][col] = val;
    }

    public Boolean isMaxEdge(int row) {
        if(row == 0)
            return true;
        else if(row == 7)
            return false;
        else
            return null;
    }

    public boolean isMax(int[] piece) {
        return (tiles[piece[0]][piece[1]] == 'r' || tiles[piece[0]][piece[1]] == 'R');
    }

    public boolean isKing(int[] piece) {
        return (tiles[piece[0]][piece[1]] == 'R' || tiles[piece[0]][piece[1]] == 'W');
    }

    public void setKing(int row, int col) {
        char piece = tiles[row][col];
        if(piece == 'r')
            tiles[row][col] = 'R';
        else
            tiles[row][col] = 'W';
    }

    public void doMove(Action action) {
        moveList.add(action);
        tiles[action.getDestination()[0]][action.getDestination()[1]] = tiles[action.getOrigin()[0]][action.getOrigin()[1]];
        tiles[action.getOrigin()[0]][action.getOrigin()[1]] = ' ';
        if(action.isCapture())
            tiles[action.getCaptured()[0]][action.getCaptured()[1]] = ' ';
    }

    public void undoMove() {
        Action action = moveList.get(moveList.size() - 1);
        moveList.remove(moveList.size()- 1);
        tiles[action.getOrigin()[0]][action.getOrigin()[1]] = tiles[action.getDestination()[0]][action.getDestination()[1]];
        tiles[action.getDestination()[0]][action.getDestination()[1]] = ' ';
        if(action.isCapture())
            tiles[action.getCaptured()[0]][action.getCaptured()[1]] = action.getCapturedPiece();
    }

    public void displayBoard() {
        System.out.println("******************************************");
        System.out.println("    0   1   2   3   4   5   6   7");
        System.out.println("  ---------------------------------");
        for(int i = 0; i < 8; i++) {
            System.out.print(i + " |");
            for(int j = 0; j < 8; j++) {
                System.out.print(" " + tiles[i][j] + " |");
                if(j == 7)
                    System.out.println();
            }
            System.out.println("  ---------------------------------");
        }
        
    }

    public char[][] copyTiles() {
        char[][] dest = new char[8][8];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                dest[i][j] = tiles[i][j];

        return dest;
    }

}