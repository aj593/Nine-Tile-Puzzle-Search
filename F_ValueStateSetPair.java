import java.util.HashSet;

//Allan Gershon - CSCI 363: Artificial Intelligence, Changhe Yuan - March 13, 2021 - Assignment #1 - Eight Puzzle Problem

/**
 * This class is used as elements in A*'s priority queue so that all states of the same f-value are stored in the same HashSet
 * f_Value: the f_Value of a state (the sum of its cost so far and current heuristic value)
 * statesOfF_Value: set to keep track of states of this f-value
 * Essentially this keeps a priority queue full of sets containing states, instead of individual states.
 * This allows it so that whenever a state is removed from the priority queue, it's removed from the pair/set it's contained in
 * A pair object is only removed from the priority queue if it's empty
 * A new pair object is added to the priority queue only if there already isn't an existing state of the same f_Value in it
 * This reduces the number of O(log n) operations needed to maintain it
 */

public class F_ValueStateSetPair {
    int f_Value; //f_Value for all states in set
    HashSet<State> statesOfF_Value = new HashSet<>(); //contains actual states to be added and removed
}