import java.util.*;

//Allan Gershon - CSCI 363: Artificial Intelligence, Changhe Yuan - March 13, 2021 - Assignment #1 - Eight Puzzle Problem

/**
 * SearchAlgorithms class full of static methods to call the implemented state search techniques with some helper methods
 */

public class SearchAlgorithms {

    /**
     * A* search algorithm used to find goal state in least number of steps, given start and goal states
     * String parameter for the heuristic used to decide which heuristic function is called when generating new child states
     */

    public static void A_StarSearch(State startState, State goalState, String heuristicUsed){
        //Start time counter
        long startTime = System.nanoTime();
        long endTime = 0;
        //Priority queue full of f-value-state pair class objects (look at F_ValueStateSetPair class for more explanation)
        //This priority queue essentially groups states of the same f-value into sets, which have states removed and added while still in the PQ
        PriorityQueue<F_ValueStateSetPair> openList = new PriorityQueue<>(new StateSetPairComparator());
        //List of already expanded nodes implemented as a set
        HashSet<State> closedList = new HashSet<>();
        //Two hashmaps to keep track of what states are in what lists and their string representation (made possible by overrided toString() method)
        HashMap<String,State> closedListStates = new HashMap<>();
        HashMap<String,State> openListStates   = new HashMap<>();
        //Map to keep track of which f-values correspond to which state pair objects.
        //These pair objects will be in the priority queue, so that accessing them through this map will allow removal and addition of states in O(1) time
        HashMap<Integer, F_ValueStateSetPair> F_ValueToPairMap = new HashMap<>();
        //Find f-value of start state to put it into a pair object's hashmap field and add it to the open list
        int startStateF_Value = startState.costToGoal + startState.numberMovesSoFar;
        F_ValueStateSetPair firstPair = new F_ValueStateSetPair();
        firstPair.f_Value = startStateF_Value;
        firstPair.statesOfF_Value.add(startState);
        openList.add(firstPair);
        //Keep track of first state in open list with its string representation and reference to its pair object
        openListStates.put(startState.toString(), startState);
        F_ValueToPairMap.put(firstPair.f_Value, firstPair);
        //Make the number of board rows and columns equal to whatever's
        Integer boardRows = startState.currentConfig.tileBoard.length;
        Integer boardCols = startState.currentConfig.tileBoard[0].length;
        //boolean variable to keep track of whether goal state was found, to stop loop when it is actually found
        boolean reachedGoal = false;
        State currState = null;
        State foundGoalState = null;
        int numNodesExpanded = 0;
        while(!reachedGoal){
            //Pull the current state from the priority queue's top element to check whether it's a goal state
            if(!openList.isEmpty()){
                //Get a state object by getting any element from the PQ's top element's map, which should have the lowest available f-value
                currState = openList.peek().statesOfF_Value.iterator().next();
                F_ValueStateSetPair minF_ValuePair = openList.peek();
                //Remove the state object reference from both maps at the same time so there's only one reference of it while algorithm runs
                assert openList.peek() != null;
                minF_ValuePair.statesOfF_Value.remove(currState);   //Remove state object from top of priority queue
                openListStates.remove(currState.toString());        //Remove state from map for open states based on its string representation
                //If the pair object on top of the priority queue now has an empty hashmap, remove the pair object from the priority queue and reference from pair map
                int lowestF_Value = minF_ValuePair.f_Value;
                if(minF_ValuePair.statesOfF_Value.isEmpty()){
                    openList.remove();                              //Removes pair object from top of priority queue
                    F_ValueToPairMap.remove(lowestF_Value);         //Remove entry from pair object map based on its f-value key
                }
            }
            //Call isSameConfig helper method to see if current state is a goal state
            //If it's a goal state, we've reached the goal, so set boolean variable to true to end loop after this
            if(isSameConfig(currState, goalState, boardRows, boardCols)) {
                endTime = System.nanoTime();
                reachedGoal = true;
                assert currState != null;
                foundGoalState = currState;
            }
            //If it's not a goal state, we expand this node to generate child states and put the children in the open list depending on whether their states already were found
            else{
                //Add current state to closed list map and a reference to it using its string representation for later querying in constant time
                closedList.add(currState);
                numNodesExpanded++; //A node is about to be expanded so increment the count
                assert currState != null;
                closedListStates.put(currState.toString(), currState);
                //Finding neighbors of current state by checking the current empty tile location and making children from it for possible successor states (expansion)
                Integer[] emptyTileLocation = findEmptyTile(currState, boardRows, boardCols);
                ArrayList<State> potentialChildren = expandCurrentNode(currState, emptyTileLocation[0], emptyTileLocation[1], boardRows, boardCols, heuristicUsed);

                //Search all found potential children to see whether they are already in the open or closed lists
                for(State childState : potentialChildren){
                    //First search for child state from closed list, return whether same state was found and if the same state has a lower estimated cost
                    StateFoundInListInfo closedListInfo = searchOpenOrClosedList(childState, closedListStates);
                    boolean foundSameStateInClosedList = closedListInfo.wasFound[0];
                    boolean foundLowerCostSameStateInClosedList = closedListInfo.wasFound[1];
                    State stateFoundInClosedList = closedListInfo.foundState;

                    //Search for same state in open list also, only if same state was not found in closed list, assigning similar variables as right above
                    boolean foundSameStateInOpenList = false;
                    boolean foundLowerCostSameStateInOpenList = false;
                    State stateFoundInOpenList = null;
                    if(!foundSameStateInClosedList){
                        StateFoundInListInfo openListInfo = searchOpenOrClosedList(childState, openListStates);
                        foundSameStateInOpenList = openListInfo.wasFound[0];
                        foundLowerCostSameStateInOpenList = openListInfo.wasFound[1];
                        stateFoundInOpenList = openListInfo.foundState;
                    }

                    //If a lower cost same state was found in the closed list, move it to the open list and change its parent pointer
                    if(foundLowerCostSameStateInClosedList){
                        //Remove references to states from closed list and map containing the state's string representation
                        closedList.remove(stateFoundInClosedList);
                        closedListStates.remove(stateFoundInClosedList.toString());
                        //This newly found state has the same state (same h-value), but took less steps to get here, so reassign the g-value and add it to the open list
                        stateFoundInClosedList.numberMovesSoFar = childState.numberMovesSoFar;
                        addStateToOpenList(openList, F_ValueToPairMap, currState, stateFoundInClosedList, openListStates);
                    }
                    //If a lower cost same state was found in the open list, we must put the state back into the open list into its proper place based on its f-value
                    else if(foundLowerCostSameStateInOpenList){
                        //Remove found state from open list in its old spot in priority queue based on f-value by getting hashmap corresponding to it
                        int oldOpenStateF_Value = stateFoundInOpenList.numberMovesSoFar + stateFoundInOpenList.costToGoal;
                        HashSet<State> F_ValueStates = F_ValueToPairMap.get(oldOpenStateF_Value).statesOfF_Value;
                        //Remove the state from its old spot in the open list and its string at the same time, even if it will be put back later, to be safe
                        F_ValueStates.remove(stateFoundInOpenList);
                        openListStates.remove(stateFoundInOpenList.toString());
                        //Remove the pair object from the open list if its hashmap field is empty
                        if(F_ValueStates.isEmpty()){
                            F_ValueStateSetPair removePairFromOpenList = F_ValueToPairMap.remove(oldOpenStateF_Value);
                            openList.remove(removePairFromOpenList);
                        }
                        //Set values and re-add open list state to proper spot
                        stateFoundInOpenList.numberMovesSoFar = childState.numberMovesSoFar;
                        addStateToOpenList(openList, F_ValueToPairMap, currState, stateFoundInOpenList, openListStates);
                    }
                    //If the same state was not found in either the open or closed list, it was never generated then, so simply add it to the open list
                    else if(!foundSameStateInOpenList && !foundSameStateInClosedList){
                        addStateToOpenList(openList, F_ValueToPairMap, currState, childState, openListStates);
                    }
                }
            }
        }
        printStats(foundGoalState, numNodesExpanded, endTime - startTime , boardRows, boardCols);
    }

    /**
     * Helper method to add the state to the open list based on an existing state and a potential child state
     */

    private static void addStateToOpenList(PriorityQueue<F_ValueStateSetPair> openList, HashMap<Integer, F_ValueStateSetPair> f_ValueToPairMap, State currState, State childState, HashMap<String,State> openListStates) {
        //Find f-value of new child state to use it later to add to f-value-to-pair map
        int stateF_Value = childState.numberMovesSoFar + childState.costToGoal;
        //Find pair object corresponding to f-value, or make a new pair object and add it to the priority queue and map if it doesn't exist
        F_ValueStateSetPair statePair;
        //Found existing pair object based on f-value
        if(f_ValueToPairMap.containsKey(stateF_Value)){
            statePair = f_ValueToPairMap.get(stateF_Value);
        }
        //Pair object for f-value doesn't exist yet, must make one and add it to the open list priority queue and map
        else{
            statePair = new F_ValueStateSetPair();
            statePair.f_Value = stateF_Value;
            openList.add(statePair);
            f_ValueToPairMap.put(stateF_Value, statePair);
        }
        //Add the child state to the pair object it belongs to as well as its corresponding map and assign (or reassign) its parent pointer
        statePair.statesOfF_Value.add(childState);
        openListStates.put(childState.toString(), childState);
        childState.parentState = currState;
    }

    /**
     * Find potential child states based on current state node
     */

    public static ArrayList<State> expandCurrentNode(State currState, Integer emptyTileRow, Integer emptyTileCol, Integer boardRows, Integer boardCols, String heuristicUsed){
        //There are at most four possible child child states from the current state, assign different new column and row values based on the current empty tile placement
        Integer[][] emptyChildSpaces = new Integer[4][];
        emptyChildSpaces[0] = new Integer[]{emptyTileRow - 1, emptyTileCol};
        emptyChildSpaces[1] = new Integer[]{emptyTileRow + 1, emptyTileCol};
        emptyChildSpaces[2] = new Integer[]{emptyTileRow, emptyTileCol - 1};
        emptyChildSpaces[3] = new Integer[]{emptyTileRow, emptyTileCol + 1};

        //Make a list of potential children to add newly generated children to
        ArrayList<State> potentialChildren = new ArrayList<>();
        //Traverse through array of possible child empty tile arrangements to generate children from
        for(int i = 0; i < emptyChildSpaces.length; i++){
            //Can only generate child if child empty tile arrangement keeps within bounds of the 3 x 3 board, so check for that
            if(emptyChildSpaces[i][0] >= 0 && emptyChildSpaces[i][0] < boardRows && emptyChildSpaces[i][1] >= 0 && emptyChildSpaces[i][1] < boardCols){
                //Generate possible child state and add it to the list if it's not null
                State childState = generateChild(currState, boardRows, boardCols, emptyTileRow, emptyTileCol, emptyChildSpaces[i][0], emptyChildSpaces[i][1], heuristicUsed);
                if(childState != null){
                    potentialChildren.add(childState);
                }
            }
        }
        return potentialChildren;
    }

    /**
     * Generate new child if it's valid within the boundaries of the 3 x 3 board
     */

    public static State generateChild(State currState, int boardRows, int boardCols, int emptyTileRow, int emptyTileCol, int newEmptyRow, int newEmptyCol, String heuristicUsed){
        //This new state will be the child state, and has only a cost greater by 1 than its predecessor
        State newChildState = new State();
        newChildState.numberMovesSoFar = currState.numberMovesSoFar + 1;

        //Populate array of child based on current state's values, tiles to be swapped later
        for(int i = 0; i < boardRows; i++){
            for(int j = 0; j < boardCols; j++){
                newChildState.currentConfig.tileBoard[i][j] = new Tile(currState.currentConfig.tileBoard[i][j].number);
            }
        }

        //Swap tile objects within board based on provided new empty tile row and column values
        Tile temp = newChildState.currentConfig.tileBoard[emptyTileRow][emptyTileCol];
        newChildState.currentConfig.tileBoard[emptyTileRow][emptyTileCol] = newChildState.currentConfig.tileBoard[newEmptyRow][newEmptyCol];
        newChildState.currentConfig.tileBoard[newEmptyRow][newEmptyCol] = temp;

        //If the new child state with swapped tiles has the same configuration as the current state's parent, it's definitely a repeat state so return null
        if(currState.parentState != null && isSameConfig(newChildState, currState.parentState, boardRows, boardCols)){
            return null;
        }

        //Calculate heuristic value of new child based on whatever string was passed in
        if(heuristicUsed.equals("manhattan")) {
            newChildState.calculateManhattanHeuristic();
        }
        else if(heuristicUsed.equals("misplaced")){
            newChildState.calculateMisplacedTileHeuristic();
        }
        else{
            System.out.println("Please provide either \"manhattan\" or \"misplaced\" heuristic!");
        }
        return newChildState;
    }

    /**
     * Search either the open or closed list, depending on what's passed into it for the child
     * Return what state was found if any same state was found, and whether the state had a lower cost in a StateFoundInListInfo object
     */

    public static StateFoundInListInfo searchOpenOrClosedList(State childState, HashMap<String,State> currentListStates){
        //Turn child state into its string representation, which will be used to find whether the state exists in the passed-in hashmap
        StateFoundInListInfo listInfo = new StateFoundInListInfo();
        String stringifiedChild = childState.toString();
        //If the same state is found, assign that the same state was found get a reference to it
        if(currentListStates.containsKey(stringifiedChild)){
            //Say that the same state was found
            State sameStateButOlder = currentListStates.get(stringifiedChild);
            listInfo.wasFound[0] = true;
            //If the child state is the same and has a lower cost, say we found a lower cost same state and get its reference
            if(childState.numberMovesSoFar < sameStateButOlder.numberMovesSoFar){
                listInfo.wasFound[1] = true;
                listInfo.foundState = sameStateButOlder;
            }
        }
        //If no same state was found, simply return the list info object with its boolean fields set to false and State reference field set to null, as first initialized
        return listInfo;
    }

    /**
     * Run Iterative-Deepening A* algorithm, by repeatedly running DFBnB as the underlying search algorithm
     * Repeatedly start the search from scratch if all states up to depth based on f-value are exhausted
     */

    public static void A_StarIterativeDeepeningSearch(State startState, State givenGoalState){
        //Initiate time counter
        long startTime = System.nanoTime();
        long endTime = 0;
        //Upper-bound the first iteration's depth by the start state's f-value
        int upperBound_F_Value = startState.costToGoal + startState.numberMovesSoFar;
        //Set goalReached variable to false, to later end loop when it's found to return the goal state, to later derive path
        boolean goalReached = false;
        State foundGoalState = null;
        State currState; //This state will keep track of the state we're at in the inner loop
        int numNodesExpanded = 0;
        int boardRows = startState.currentConfig.tileBoard.length;
        int boardCols = startState.currentConfig.tileBoard[0].length;
        //Loop until goal is reached
        while(!goalReached){
            //Use a linked list as a stack to make it easier to inspect inner elements, and push the first element
            LinkedList<State> openListStack = new LinkedList<>();
            openListStack.push(startState);
            int overallLowest_F_Value = Integer.MAX_VALUE;      //This variable will keep track of the lowest f-value available that's still higher than the current bound
            //Keep running loop as long as not all states have been checked yet and the goal wasn't reached
            while(!openListStack.isEmpty() && !goalReached){
                //Assign current state was whatever was on top of the stack
                currState = openListStack.pop();
                //If the current state happens to be a goal state, set goalReached to true and foundGoalState to the current state, to find path later
                if(isSameConfig(currState, givenGoalState, boardRows, boardCols)){
                    endTime = System.nanoTime();
                    goalReached = true;
                    foundGoalState = currState;
                }
                //If the current state is not a goal state, expand it and generate children from it
                //Find the lowest f-value still greater than the current bound from this function also
                else{
                    numNodesExpanded++; //Node about to be expanded so increment count
                    int lowest_F_ValueGreaterThanBound = branchAndBound(upperBound_F_Value, currState, openListStack, boardRows, boardCols);
                    //If this value is greater than the lowest f-value, but still greater than the current upper-bound, assign it to be the lowest f-value
                    if(lowest_F_ValueGreaterThanBound < overallLowest_F_Value && lowest_F_ValueGreaterThanBound > upperBound_F_Value){
                        overallLowest_F_Value = lowest_F_ValueGreaterThanBound;
                    }
                }
            }
            //If the current lowest f-value is greater than the current upper-bound, set it to be the new bound
            if(overallLowest_F_Value > upperBound_F_Value) {
                upperBound_F_Value = overallLowest_F_Value;
            }
        }
        //Goal state was found, so derive path to it from starting state
        printStats(foundGoalState, numNodesExpanded, endTime - startTime, boardRows, boardCols);
    }

    /**
     * Regular depth-first branch and bound, use depth-first search in hopes of in finding a solution before getting stuck in an infinite loop
     * No infinite loop if we find goal state first, and from there we can narrow down the optimal path to the goal state
     */

    public static void depthFirstBranchAndBoundSearch(State startState, State goalState, Integer L_ValueBound){
        //Initiate time counter
        long startTime = System.nanoTime();
        long endOptSolTime = 0;
        //Linked List used as a stack
        LinkedList<State> openListStack = new LinkedList<>();
        openListStack.push(startState);
        ArrayList<State> goalStatesList = new ArrayList<>();
        int costOfBestSolutionSoFar = L_ValueBound;
        State currState; //Keep track of current state popped from stack here
        State foundGoalState;
        int numNodesExpanded = 0;
        int boardRows = startState.currentConfig.tileBoard.length;
        int boardCols = startState.currentConfig.tileBoard[0].length;
        while(!openListStack.isEmpty()){
            //Get current state by popping it off the stack, should be the state of lowest f-value from children generated in last iteration
            currState = openListStack.pop();

            //If the current state is a goal state, we found a possible solution, and can use the cost of this solution to bound the solutions found after this
            if(isSameConfig(currState, goalState, boardRows, boardCols) && currState.numberMovesSoFar < costOfBestSolutionSoFar){
                endOptSolTime = System.nanoTime();
                //New L-value is equal to the cost of the current goal state
                costOfBestSolutionSoFar = currState.numberMovesSoFar;
                goalStatesList.add(currState); //Add to list of possible goal states
            }
            //If the current state is not a goal state, expand it and generate children (branch)
            //Only add children to open list if their cost is less than that of the previously found goal state
            else{
                numNodesExpanded++; //Node about to be expanded so increment count
                branchAndBound(costOfBestSolutionSoFar, currState, openListStack, boardRows, boardCols);
            }
        }
        long endSolveTime = System.nanoTime();
        //Get last goal state from goal state array
        int size = goalStatesList.size();
        if(!goalStatesList.isEmpty()) {
            foundGoalState = goalStatesList.get(size - 1);
            System.out.println(goalStatesList.get(size - 1).numberMovesSoFar + " steps to get to goal! final solution");
            printStats(foundGoalState, numNodesExpanded, endOptSolTime - startTime, boardRows, boardCols);
            System.out.println("Took " + ((double)(endSolveTime - startTime))/1000000 + " milliseconds to solve puzzle");
        }
    }

    /**
     * Helper method to generate children that have cost less than the upperBound
     * Used for IDA* and DFBnB
     * Returns a value based on greatest f-value found to update bound for IDA*
     */

    public static int branchAndBound(int upperBound, State currState, LinkedList<State> openListStack, int boardRows, int boardCols){
        //Find the empty tile location in the current state and generate children based on it
        Integer[] emptyTileLocation = findEmptyTile(currState, boardRows, boardCols);
        ArrayList<State> potentialChildren = expandCurrentNode(currState, emptyTileLocation[0], emptyTileLocation[1], boardRows, boardCols, "manhattan");

        //Use this value to find the smallest f-value still larger than the current upper-bound
        int min_F_ValueGreaterThanBound = Integer.MAX_VALUE;
        //Bound possible children based on cost of best solution so far, remove them if they're greater than bound
        for(int i = potentialChildren.size() - 1; i >= 0; i--){
            State childState = potentialChildren.get(i);
            int f_Value = childState.numberMovesSoFar + childState.costToGoal;
            if(f_Value > upperBound && f_Value < min_F_ValueGreaterThanBound){
                min_F_ValueGreaterThanBound = f_Value;
            }
            //If the f-value is greater than the upper-bound, delete it
            if(f_Value > upperBound){
                potentialChildren.remove(i);
            }
        }

        //Sort the list in ascending order based on their f-values
        potentialChildren.sort(new StateComparator());
        //Empty the list of children by adding them to stack so the child with the lowest f-value just generated is on top of the stack
        while(!potentialChildren.isEmpty()){
            //Remove last element, which should have the greatest f-value
            int currSize = potentialChildren.size();
            State newChildState = potentialChildren.remove(currSize - 1);
            //Set the parent pointer to the new child state to the current state and push it to the open list stack
            newChildState.parentState = currState;
            openListStack.push(newChildState);
        }
        //Value returned for possibly new upper-bound for IDA*
        return min_F_ValueGreaterThanBound;
    }

    /**
     * Compare one state to another to see whether they have the same configuration in the 3 x 3 puzzle
     */

    public static boolean isSameConfig(State currState, State otherState, int boardRows, int boardCols){
        //In a for-loop, compare each tile entry to see whether they are both empty or the same number
        for(int i = 0; i < boardRows; i++){
            for(int j = 0; j < boardCols; j++){
                Integer currStateVal = currState.currentConfig.tileBoard[i][j].number;
                Integer goalStateVal = otherState.currentConfig.tileBoard[i][j].number;
                //If both current tiles are not empty, compare their values, and if they're different, return false
                if(currStateVal != null && goalStateVal != null) {
                    if (!currStateVal.equals(goalStateVal)) {
                        return false;
                    }
                }
                //If both of them are not empty as shown by if statement above, at most one of them can be empty
                //If one tile is empty in one configuration, and the other isn't, they are not the same configuration overall
                else if(currStateVal != null || goalStateVal != null){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Helper method to find the current state's current empty tile placement
     */

    public static Integer[] findEmptyTile(State currState, Integer boardRows, Integer boardCols){
        Integer[] emptyTileLocation = new Integer[2];
        //Traverse through array until null found
        for(int i = 0; i < boardRows; i++){
            for(int j = 0; j < boardCols; j++){
                Integer currStateVal = currState.currentConfig.tileBoard[i][j].number;
                //If null is found, assign values to array based on current indices and break out of loop
                if(currStateVal == null){
                    emptyTileLocation[0] = i;
                    emptyTileLocation[1] = j;
                    return emptyTileLocation;
                }
            }
        }
        return emptyTileLocation;
    }

    /**
     * Class to help return multiple pieces of data at the same time
     * Contains array to say whether the same state was found, and if so, was it a lower cost
     */

    public static class StateFoundInListInfo{
        //Array to hold boolean variables, where first variable says whether the same state was found
        //The second variable says whether that same state happens to have a lower cost to reach from the start state
        Boolean[] wasFound = new Boolean[] {false, false};
        //Contains reference for state found of same configuration, initalized to null in case none is found
        State foundState = null;
    }

    /**
     * Helper method to print stats once the algorithm finishes
     * Prints the optimal path from start to finish, the number of nodes expanded, and the time elapsed in the meantime
     */

    public static void printStats(State foundGoalState, int numNodesExpanded, long timeElapsed, int boardRows, int boardCols){
        //Will start with goal state, so backtrack from it to get to start state with parent pointers, store states on stack
        Stack<State> pathToGoal = new Stack<>();
        State currPathState = foundGoalState;
        //Push onto the stack until start state reached
        while(currPathState != null){
            pathToGoal.push(currPathState);
            currPathState = currPathState.parentState;
        }
        //Print configuration of board for each state as well as the number state for it until stack is empty
        int stateCounter = 1;
        while(!pathToGoal.empty()){
            State currentState = pathToGoal.pop();
            System.out.println("State #" + stateCounter++);
            for(int i = 0; i < boardRows; i++){
                for(int j = 0; j < boardCols; j++){
                    Integer currNumber = currentState.currentConfig.tileBoard[i][j].number;
                    if(currNumber != null){
                        System.out.print(currNumber + " ");
                    }
                    else{
                        System.out.print("  ");
                    }
                }
                System.out.println();
            }
            System.out.println("-----");
        }
        //Print out the number of moves to get to the goal state via the optimal path, amount of time taken before, and how many nodes were expanded
        assert foundGoalState != null;
        System.out.println("Shortest path is " + foundGoalState.numberMovesSoFar + " moves to get to goal state");
        System.out.println("Took " + ((double)timeElapsed)/1000000 + " milliseconds to find optimal goal state");
        System.out.println(numNodesExpanded + " nodes expanded to find goal state");
    }
}