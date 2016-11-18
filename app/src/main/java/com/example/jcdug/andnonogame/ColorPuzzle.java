package com.example.jcdug.andnonogame;

import java.io.Serializable;

/**
 * Created by jcdug on 11/18/2016.
 */

public class ColorPuzzle implements Serializable {
    private int ID;                 //The unique ID of the puzzle
    private int[] size;             //The size of the puzzle in the form {numColumns, numRows}
    private int[][] currentState;   //The current state of the puzzle
    private int[][] solution;       //The solution state
    private int[][][] rows;         //The row constraint values
    private int[][][] cols;         //The column constraint values
    private int completed;          //completed = 1 if puzzle is complete, 0 otherwise
    private int[] colors;           //colors for the puzzle

    // {{{0,-1},{1,1},{1,2}},{{1,1},{1,2},{1,0}}}   row constraint
    // {{{run value, color index}, {run value, color index}}, {next row same format}}


    /**
     * Constructor for Puzzle object
     * Sets currentState to empty int[][] with the specified size
     *
     * @param id   The id of the puzzle
     * @param s    The size of the puzzle
     * @param sol  The solution state
     * @param r    The row constraint values
     * @param c    The column constraint values
     * @param comp The value for complete
     */
    public ColorPuzzle(int id, int[] s, int[][] sol, int[][][] r, int[][][] c, int[] color, int comp) {
        ID = id;
        size = s;
        currentState = new int[s[1]][s[0]];
        solution = sol;
        rows = r;
        cols = c;
        colors = color;
        completed = comp;
    }

    /**
     * Getter method for id
     *
     * @return Puzzle ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Getter method for size
     *
     * @return Puzzle size
     */
    public int[] getSize() {
        return size;
    }

    /**
     * Getter method for currentState
     *
     * @return the current state of the puzzle
     */
    public int[][] getCurrentState() {
        return currentState;
    }

    /**
     * Getter method for solution
     *
     * @return the solution state of the puzzle
     */
    public int[][] getSolution() {
        return solution;
    }

    /**
     * Getter method for rows
     *
     * @return the row constraint values
     */
    public int[][][] getRows() {
        return rows;
    }

    /**
     * Getter method for cols
     *
     * @return the column constraint values
     */
    public int[][][] getCols() {
        return cols;
    }

    /**
     * Getter method for colors
     * @return the colors
     */
    public int[] getColors() {
        return colors;
    }

    /**
     * Getter method for completed
     *
     * @return the value of of completed
     */
    public int isCompleted() {
        return completed;
    }

    /**
     * Setter method for currentState
     *
     * @param cs the new state
     */
    public void setCurrentState(int[][] cs) {
        currentState = cs;
    }

    /**
     * Setter method for completed
     *
     * @param c the new value
     */
    public void setCompleted(int c) {
        completed = c;
    }
}
