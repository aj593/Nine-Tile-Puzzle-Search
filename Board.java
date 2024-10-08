//Allan Gershon - CSCI 363: Artificial Intelligence, Changhe Yuan - March 13, 2021 - Assignment #1 - Eight Puzzle Problem

/**
 * Class to represent a state's configuration in the eight-puzzle problem
 * This board contains 9 number tile objects arranged in a 3 by 3 board
 */

public class Board {
    public Tile[][] tileBoard;

    public Board(){
        tileBoard = new Tile[3][3];
    }
}