//Allan Gershon - CSCI 363: Artificial Intelligence, Changhe Yuan - March 13, 2021 - Assignment #1 - Eight Puzzle Problem

/**
 * State class used to keep track of the eight-tile puzzle's current configuration, search tree parent pointer, g-value, and h-value
 */

public class State {
    Board currentConfig;            //State's current configuration with its 3 x 3 size board, holding eight tiles
    State parentState;              //Reference to previous state in search tree when search algorithm runs
    int numberMovesSoFar = 0;       //The number of moves made from the starting state, the g-value
    int costToGoal = 0;             //The estimated cost to reach the goal state from the current state, the h-value

    //These values help with the toString() method
    private final int boardRows;
    private final int boardCols;

    /**
     * Constructor to instantiate a new state with a null parent pointer
     */

    public State(){
        this.currentConfig = new Board();
        this.parentState = null;
        this.boardRows = this.currentConfig.tileBoard.length;
        this.boardCols = this.currentConfig.tileBoard[0].length;
    }

    /**
     * Constructor to make a new state with an assigned parent
     */

    public State(State parentPointer){
        this.currentConfig = new Board();
        this.parentState = parentPointer;
        this.boardRows = this.currentConfig.tileBoard.length;
        this.boardCols = this.currentConfig.tileBoard[0].length;
    }

    /**
     * Goal array to help compare a current state's tile configuration to
     */

    private static final Integer[][] goalArray =
                    {{1, 2, 3},
                    {8, null, 4},
                    {7, 6, 5}};

    /**
     * Calculate the misplaced tile heuristic by incrementing the h-value every time a tile is in a place it's not supposed to be
     */

    public void calculateMisplacedTileHeuristic(){
        //Get current state's configuration and add the total cost to goal to the estimated number of steps
        int numMisplaced = 0;
        Tile[][] tileArray = this.currentConfig.tileBoard;
        int totalRows = tileArray.length;
        int totalCols = tileArray[0].length;
        for(int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalCols; j++) {
                //Only add if current tile is not empty
                if(tileArray[i][j].number != null){
                    //Increment if number tile is not in the same place as in the goal array
                    if (!tileArray[i][j].number.equals(goalArray[i][j])) {
                        numMisplaced++;
                    }
                }
            }
        }
        this.costToGoal = numMisplaced;
    }

    /**
     * Hardcoded values for Manhattan Distance heuristic
     * Finding the h-value with this three-dimensional array allows us to find the distance of the current number from its goal tile in constant time
     */

    private static final int[][][] manhattanDistances = {
            {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, // Empty tile's goal placement, not calculated in heuristics
            {{0, 1, 2}, {1, 2, 3}, {2, 3, 4}}, // Proper placements of tile number 1
            {{1, 0, 1}, {2, 1, 2}, {3, 2, 3}}, // Proper placements of tile number 2
            {{2, 1, 0}, {3, 2, 1}, {4, 3, 2}}, // Proper placements of tile number 3
            {{3, 2, 1}, {2, 1, 0}, {3, 2, 1}}, // Proper placements of tile number 4
            {{4, 3, 2}, {3, 2, 1}, {2, 1, 0}}, // Proper placements of tile number 5
            {{3, 2, 3}, {2, 1, 2}, {1, 0, 1}}, // Proper placements of tile number 6
            {{2, 3, 4}, {1, 2, 3}, {0, 1, 2}}, // Proper placements of tile number 7
            {{1, 2, 3}, {0, 1, 2}, {1, 2, 3}}  // Proper placements of tile number 8
    };

    /**
     * Calculate the Manhattan Distance heuristic by summing the individual distances of number tiles from their goal states
     */

    public void calculateManhattanHeuristic(){
        //Get current state's configuration and add the total cost to goal to the estimated number of steps
        int numSteps = 0;
        Tile[][] tileArray = this.currentConfig.tileBoard;
        int totalRows = tileArray.length;
        int totalCols = tileArray[0].length;
        for(int i = 0; i < totalRows; i++){
            for(int j = 0; j < totalCols; j++){
                //Only add to the h-value if the current tile is a number, no need to calculate for the empty tile
                if(tileArray[i][j].number != null) {
                    //For the first array entry, put in the configuration's current number
                    //Then plug in the current row and column to fetch the number of single steps the current number is away from its goal placement
                    numSteps += manhattanDistances[tileArray[i][j].number][i][j];
                }
            }
        }
        this.costToGoal = numSteps;
    }

    /**
     * Overridden toString() method to turn a state's configuration into its string representation
     * The left-to-right ordering of numbers represents the configuration's arrangement from left-to-right, top-to-bottom in the matrix
     * Turning a state's configuration into a string helps with hashing whether a state has already been found in a search algorithm
     */

    @Override
    public String toString(){
        //Use stringbuilder to repeatedly append to before returning it as a string
        StringBuilder currSB = new StringBuilder(9);
        for(int i = 0; i < boardRows; i++){
            for(int j = 0; j < boardCols; j++){
                Integer currNum = this.currentConfig.tileBoard[i][j].number;
                if(currNum != null) currSB.append(currNum);                     //Append number to string
                else currSB.append('0');                                        //Use "0" to represent an empty tile
            }
        }
        return currSB.toString();
    }
}