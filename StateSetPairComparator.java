import java.util.Comparator;

//Allan Gershon - CSCI 363: Artificial Intelligence, Changhe Yuan - March 13, 2021 - Assignment #1 - Eight Puzzle Problem

/**
 * Comparator used to sort priority queue based on each pair object's assigned f-value
 */

public class StateSetPairComparator implements Comparator<F_ValueStateSetPair> {

    /**
     * Comparator implemented by overriding compare method with two state set pair objects as parameters
     */

    @Override
    public int compare(F_ValueStateSetPair stateSetPairOne, F_ValueStateSetPair stateSetPairTwo) {
        return stateSetPairOne.f_Value - stateSetPairTwo.f_Value;
    }
}