import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

//Allan Gershon - CSCI 363: Artificial Intelligence, Changhe Yuan - March 13, 2021 - Assignment #1 - Eight Puzzle Problem

/**
 * Main class containing main method to run all other code
 * Run this class to run all the implemented algorithms from the SearchAlgorithms class
 * When run, user will be given the option to run which algorithms, if they want to run all the algorithms, and which start states they want to use.
 */

public class Main {

    //Following matrices are hardcoded to store given start states and goal state

    //Given goal state matrix
    public static final Integer[][] goalArray =
            {{1, 2, 3},
            {8, null, 4},
            {7, 6, 5}};

    //Given easy start state matrix
    public static final Integer[][] easyStartArray =
            {{1, 3, 4},
            {8, 6, 2},
            {7, null, 5}};

    //Given medium start state matrix
    public static final Integer[][] mediumStartArray =
            {{2, 8, 1},
            {null, 4, 3},
            {7, 6, 5}};

    //Given hard start state matrix
    public static final Integer[][] hardStartArray =
            {{2, 8, 1},
            {4, 6, 3},
            {null, 7, 5}};

    //Given worst start state matrix
    public static final Integer[][] worstStartArray =
            {{5, 6, 7},
            {4, null, 8},
            {3, 2, 1}};

    //Map to keep track of which start state is being used for an algorithm, helps with adding details to output
    public static HashMap<Integer[][],String> arrayToCaseNameMap = new HashMap<>();

    /**
     * main() method to run all code in different java classes
     * As code runs, type in different numbers corresponding to different choices to run different algorithms with different start states
     * WARNING: Not all algorithms run efficiently. Some take minutes or even hours to run (e.g. IDA*, DFBnB)
     */

    public static void main(String[] args) {
        //Define goal state object
        State goalState = new State(null);
        populateStateArray(goalArray, goalState);

        arrayToCaseNameMap.put(easyStartArray, "Easy");
        arrayToCaseNameMap.put(mediumStartArray, "Medium");
        arrayToCaseNameMap.put(hardStartArray, "Hard");
        arrayToCaseNameMap.put(worstStartArray, "Worst");

        //Set to keep track of which number inputs should be put in for algorithms to choose from
        //Can run one of four algorithms at a time (first four choices), or run all four at once (fifth choice)
        HashSet<String> algoNumberSet = new HashSet<>();
        for(int i = 1; i <= 5; i++){
            algoNumberSet.add(Integer.toString(i));
        }

        //Map to associate each string input to a start state matrix
        //5 and 6 are valid inputs, they just don't have a single specific matrix assigned to them
        HashMap<String, Integer[][]> caseNumberMap = new HashMap<>();
        caseNumberMap.put("1", easyStartArray);
        caseNumberMap.put("2", mediumStartArray);
        caseNumberMap.put("3", hardStartArray);
        caseNumberMap.put("4", worstStartArray);
        caseNumberMap.put("5", null);
        caseNumberMap.put("6", null);

        //Starting here, users can repeatedly put in inputs to see what algorithm they want seen run on what starting state
        while(true) {
            System.out.println("Hi, choose which algorithm and starting state/configuration you want to run!\n");
            Scanner scanner = new Scanner(System.in);
            String inputForAlgo;
            //Provide input for algorithm(s) you want to run
            while(true) {
                System.out.println("Choose an algorithm to run by typing in the number corresponding to it.\n" +
                        "1. A* with Misplaced Tile Heuristic\n" +
                        "2. A* with Manhattan Distance Heuristic\n" +
                        "3. Iterative-Deepening A* (Manhattan)\n" +
                        "4. Depth-First Branch and Bound (Manhattan)\n" +
                        "5. Run all algorithms");
                inputForAlgo = scanner.next();
                //Only single-digit numbers accepted as input
                if(!algoNumberSet.contains(inputForAlgo)){
                    System.out.println("Invalid input, please enter in single character for single-digit number corresponding to one of options above");
                }
                else break;
            }
            String inputForState;
            //Provide input for state(s) you want to start with
            while(true){
                System.out.println("Choose a starting state to run by entering in the number corresponding to it, or run them all, or make your own state\n" +
                        "1. Easy\n" +
                        "2. Medium\n" +
                        "3. Hard\n" +
                        "4. Worst (Warning: may run for long time with Iterative-Deepening A* and DFBnB)\n" +
                        "5. Run all given states with chosen algorithm(s) (This will run them in order of states for #1-4 above)\n" +
                        "6. Make custom starting state with own number tile configuration of your own choosing\n");
                inputForState = scanner.next();
                //Again, only single-digit numbers accepted as input
                if(!caseNumberMap.containsKey(inputForState)){
                    System.out.println("Invalid input, please enter a single character for the single-digit number corresponding to one of the previously given options");
                }
                else break;
            }
            //Fill list of matrices to be used as starting states
            ArrayList<Integer[][]> runAlgoOnTheseStates = new ArrayList<>();
            //Provide custom start state matrix
            if(inputForState.equals("6")){
                Integer[][] customStateArray = new Integer[3][3];
                arrayToCaseNameMap.put(customStateArray, "custom");
                populateCustomStateArray(customStateArray, scanner);
                runAlgoOnTheseStates.add(customStateArray);
            }
            //Run algorithm(s) on all given states
            else if(inputForState.equals("5")){
                for(int i = 1; i <= 4; i++){
                    //Obtain matrix from hardcoded references in caseNumberMap
                    Integer[][] oneOfArraysUsed = caseNumberMap.get(Integer.toString(i));
                    runAlgoOnTheseStates.add(oneOfArraysUsed);
                }
            }
            //Run algorithm in only one state, the chosen state
            else{
                Integer[][] usedStateArray = caseNumberMap.get(inputForState);
                runAlgoOnTheseStates.add(usedStateArray);
            }
            //Run different algorithm(s) based on input before
            switch (inputForAlgo) {
                //A* algorithm with misplaced tile heuristic
                case "1" -> run_A_StarMisplacedTile(goalState, runAlgoOnTheseStates);

                //A* algorithm with Manhattan Distance Heuristic
                case "2" -> run_A_StarManhattanDistance(goalState, runAlgoOnTheseStates);

                //Iterative-Deepening A* algorithm
                case "3" -> runIterativeDeepening_A_Star(goalState, runAlgoOnTheseStates);

                // Depth-First Branch and Bound algorithm
                case "4" -> runDepthFirstBranchAndBound(goalState, runAlgoOnTheseStates);

                //Run all the algorithms
                case "5" -> {
                    //Run A* Misplaced Tile algorithm
                    run_A_StarMisplacedTile(goalState, runAlgoOnTheseStates);

                    //Run A* Manhattan Distance algorithm
                    run_A_StarManhattanDistance(goalState, runAlgoOnTheseStates);

                    //Run Iterative-Deepening A* algorithm
                    runIterativeDeepening_A_Star(goalState, runAlgoOnTheseStates);

                    //Run Depth-First Branch and Bound algorithm
                    runDepthFirstBranchAndBound(goalState, runAlgoOnTheseStates);
                }
            }
            //Decide whether to keep running program here
            System.out.println("Would you still like to run more search algorithms? Type either 'y' or 'n'");
            String input = scanner.next();
            while(!input.equals("y") && !input.equals("n")){
                System.out.println("Please type in only either 'y' or 'n'");
                input = scanner.next();
            }
            if(input.equals("n")) break;
        }
        System.out.println("Finished! Run this program again to use more algorithms!");
    }

    /**
     * The following four functions are used to call the different state search algorithms depending on what was input and called above
     * Each method runs in a loop to run from all desired starting states by populating its start array and calling the algorithms from SearchAlgorithms class
     */

    private static void run_A_StarMisplacedTile(State goalState, ArrayList<Integer[][]> runAlgoOnTheseStates){
        System.out.println("Running A* algorithm with misplaced tile heuristic");
        for (Integer[][] currStateArr : runAlgoOnTheseStates) {
            State A_StarMisplacedStartState = new State();
            populateStateArray(currStateArr, A_StarMisplacedStartState);
            A_StarMisplacedStartState.calculateMisplacedTileHeuristic();
            SearchAlgorithms.A_StarSearch(A_StarMisplacedStartState, goalState, "misplaced");
            System.out.println("Here are the results for A* Misplaced Tile for the " + arrayToCaseNameMap.get(currStateArr) + " start state, enter any input to continue.");
            Scanner scanner = new Scanner(System.in);
            scanner.next();
        }
    }

    private static void run_A_StarManhattanDistance(State goalState, ArrayList<Integer[][]> runAlgoOnTheseStates){
        System.out.println("Running A* algorithm with manhattan distance heuristic");
        for (Integer[][] currStateArr : runAlgoOnTheseStates) {
            State A_StarManhattanStartState = new State();
            populateStateArray(currStateArr, A_StarManhattanStartState);
            A_StarManhattanStartState.calculateManhattanHeuristic();
            SearchAlgorithms.A_StarSearch(A_StarManhattanStartState, goalState, "manhattan");
            System.out.println("Here are the results for A* Manhattan Distance for the " + arrayToCaseNameMap.get(currStateArr) + " start state, enter any input to continue.");
            Scanner scanner = new Scanner(System.in);
            scanner.next();
        }
    }

    private static void runIterativeDeepening_A_Star(State goalState, ArrayList<Integer[][]> runAlgoOnTheseStates){
        System.out.println("Running Iterative-Deepening A* algorithm with manhattan distance heuristic");
        for (Integer[][] currStateArr : runAlgoOnTheseStates) {
            State IterativeDeepening_A_StarStartState = new State();
            populateStateArray(currStateArr, IterativeDeepening_A_StarStartState);
            IterativeDeepening_A_StarStartState.calculateManhattanHeuristic();
            SearchAlgorithms.A_StarIterativeDeepeningSearch(IterativeDeepening_A_StarStartState, goalState);
            System.out.println("Here are the results for Iterative-Deepening A* for the " + arrayToCaseNameMap.get(currStateArr) + " start state, enter any input to continue.");
            Scanner scanner = new Scanner(System.in);
            scanner.next();
        }
    }

    private static void runDepthFirstBranchAndBound(State goalState, ArrayList<Integer[][]> runAlgoOnTheseStates) {
        System.out.println("Running Depth-First Branch and Bound algorithm with manhattan distance heuristic");
        for (Integer[][] currStateArr : runAlgoOnTheseStates) {
            State DepthFirstBranchBoundStartState = new State();
            populateStateArray(currStateArr, DepthFirstBranchBoundStartState);
            DepthFirstBranchBoundStartState.calculateManhattanHeuristic();
            int L_ValueBound;
            //Solution found for worst case if L does not start as infinite, optimal solution found with starting value 5000
            if(currStateArr == worstStartArray) {
                System.out.println("Had to hard-code max L-value to be 5000, instead of infinity for worst-case starting state");
                L_ValueBound = 5000;
            }
            //If it's any other case, it should run fine with L starting as infinity
            else {
                System.out.println("L-value begins as infinity (Integer.MAX_VALUE)");
                L_ValueBound = Integer.MAX_VALUE;
            }
            SearchAlgorithms.depthFirstBranchAndBoundSearch(DepthFirstBranchBoundStartState, goalState, L_ValueBound);
            System.out.println("Here are the results for Depth-First Branch and Bound for the " + arrayToCaseNameMap.get(currStateArr) + " start state, enter any input to continue.");
            Scanner scanner = new Scanner(System.in);
            scanner.next();
        }
    }

    /**
     * Helper static method to fill the state object's configuration board with the passed-in matrix
     */

    public static void populateStateArray(Integer[][] inputArr, State givenState){
        for(int i = 0; i < inputArr.length; i++){
            for(int j = 0; j < inputArr[i].length; j++){
                //Make new tile object in board's current tile space with input matrix's value
                givenState.currentConfig.tileBoard[i][j] = new Tile(inputArr[i][j]);
            }
        }
    }

    /**
     * Populate array for custom start state by entering numbers one at a time
     */

    public static void populateCustomStateArray(Integer[][] customStateArray, Scanner scanner){
        System.out.println("Input numbers from 1 to 8 to put numbers into the array. Type in '0' for the empty tile.\nEach number or tile type should appear only once. Make sure it's solvable!, or may get infinite loop\n");
        HashSet<String> allowableInputs = new HashSet<>();
        for(int i = 0; i <= 8; i++) {
            allowableInputs.add(Integer.toString(i));
        }
        HashSet<String> numbersInputSoFar = new HashSet<>(); //Set to keep track of numbers input already
        String customArrayInput;
        //Fill in entries one at a time for all nine tiles
        for(int i = 0; i < customStateArray.length; i++){
            for(int j = 0; j < customStateArray[0].length; j++){
                //Error checking for input is proper or if number was already put in
                while(true){
                    System.out.println("Input a value for row " + (i + 1) + ", column " + (j + 1));
                    customArrayInput = scanner.next();
                    if(!allowableInputs.contains(customArrayInput)){
                        System.out.println("Please put in valid inputs, only single-digit numbers from 0 to 8 allowed (no repeats either");
                        printArraySoFar(customStateArray);
                    }
                    else if(numbersInputSoFar.contains(customArrayInput)){
                        System.out.println("This number was already put into the board!");
                        printArraySoFar(customStateArray);
                    }
                    else{
                        numbersInputSoFar.add(customArrayInput);
                        //Tile assigned "0" is empty, and assigned null
                        if(!customArrayInput.equals("0"))
                            customStateArray[i][j] = Integer.parseInt(customArrayInput);
                        else
                            customStateArray[i][j] = null;
                        printArraySoFar(customStateArray);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Print array entries put in so far
     */

    public static void printArraySoFar(Integer[][] customStateArray){
        System.out.println("Here's the custom start state board so far");
        for(int a = 0; a < customStateArray.length; a++){
            for(int b = 0; b < customStateArray.length; b++){
                //Print number only if it's not an empty tile, if it is empty, print a blank space
                if(customStateArray[a][b] != null)
                    System.out.print(customStateArray[a][b] + " ");
                else
                    System.out.print("  ");
            }
            System.out.println();
        }
    }
}