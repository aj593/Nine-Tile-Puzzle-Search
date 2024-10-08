//Allan Gershon - CSCI 363: Artificial Intelligence, Changhe Yuan - March 13, 2021 - Assignment #1 - Eight Puzzle Problem

/**
 * Tile class to make tile objects that will go within a Board object
 * The number in this tile can be either a regular number, or null for an empty tile
 */

public class Tile {
    public Integer number;

    public Tile(Integer numberLabel){
        this.number = numberLabel;
    }
}