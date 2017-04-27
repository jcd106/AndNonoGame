package com.example.jcdug.andnonogame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public ColorPuzzleUpload convertToUpload(String userID) {
        ArrayList<Integer> newSize = convertArray(size);
        ArrayList<List<Integer>> newCurrState = convert2d(currentState);
        ArrayList<List<List<Integer>>> newRowConst = convert3d(rows);
        ArrayList<List<List<Integer>>> newColConst = convert3d(cols);
        ArrayList<Integer> newColors = convertArray(colors);
        ColorPuzzleUpload pu = new ColorPuzzleUpload(ID, userID, newSize, newCurrState, newRowConst, newColConst, newColors, 0);
        return pu;
    }

    private ArrayList<Integer> convertArray(int[] arr) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for(int i = 0; i < arr.length; i++) {
            arrayList.add(arr[i]);
        }
        return arrayList;
    }

    private ArrayList<List<Integer>> convert2d (int[][] arr) {
        ArrayList<List<Integer>> arrayList = new ArrayList<List<Integer>>();
        for(int i = 0; i < arr.length; i++) {
            ArrayList<Integer> line = new ArrayList<Integer>();
            for(int j = 0; j < arr[i].length; j++) {
                line.add(arr[i][j]);
            }
            arrayList.add(line);
        }
        return arrayList;
    }

    private ArrayList<List<List<Integer>>> convert3d (int[][][] arr) {
        ArrayList<List<List<Integer>>> arrayList = new ArrayList<List<List<Integer>>>();
        for(int i = 0; i < arr.length; i++) {
            ArrayList<List<Integer>> line = new ArrayList<List<Integer>>();
            for(int j = 0; j < arr[i].length; j++) {
                ArrayList<Integer> inner = new ArrayList<Integer>();
                for (int k = 0; k < arr[i][j].length; k++) {
                    inner.add(arr[i][j][k]);
                }
                line.add(inner);
            }
            arrayList.add(line);
        }
        return arrayList;
    }
}
