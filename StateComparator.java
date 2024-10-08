import java.util.Comparator;

//Allan Gershon - CSCI 363: Artificial Intelligence, Changhe Yuan - March 13, 2021 - Assignment #1 - Eight Puzzle Problem

/**
 * Comparator used to compare one state to another based on its f_Value
 * This is used to help sort lists containing State objects
 */

public class StateComparator implements Comparator<State> {

    /**
     * Comparator implemented by overriding compare method with two State objects as parameters
     */

    @Override
    public int compare(State firstState, State secondState) {
        //Find relative ordering of states based on difference of their f_Values
        int firstF_Value  = firstState.costToGoal  + firstState.numberMovesSoFar;
        int secondF_Value = secondState.costToGoal + secondState.numberMovesSoFar;
        return firstF_Value - secondF_Value;
    }
}