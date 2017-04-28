package com.example.jcdug.andnonogame;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Puzzle Object Class
 * Stores the id, size, current state, solution state,
 * row and column constraint values, and an int for completion
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
public class Puzzle implements Serializable {
    private int ID;                 //The unique ID of the puzzle
    private int[] size;             //The size of the puzzle in the form {numColumns, numRows}
    private int[][] currentState;   //The current state of the puzzle
    private int[][] solution;       //The solution state
    private int[][] rows;           //The row constraint values
    private int[][] cols;           //The column constraint values
    private int completed;          //completed = 1 if puzzle is complete, 0 otherwise


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
    public Puzzle(int id, int[] s, int[][] sol, int[][] r, int[][] c, int comp) {
        ID = id;
        size = s;
        currentState = new int[s[1]][s[0]];
        solution = sol;
        rows = r;
        cols = c;
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
    public int[][] getRows() {
        return rows;
    }

    /**
     * Getter method for cols
     *
     * @return the column constraint values
     */
    public int[][] getCols() {
        return cols;
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


    public PuzzleUpload convertToUpload(String userID) {
        //ArrayList<Integer> newSize = convertSize(size);
        String newSize = size[0] + "x" + size[1];
        ArrayList<List<Integer>> newCurrState = convert2d(solution);
        ArrayList<List<Integer>> newRowConst = convert2d(rows);
        ArrayList<List<Integer>> newColConst = convert2d(cols);
        PuzzleUpload pu = new PuzzleUpload(ID, userID, newSize, newCurrState, newRowConst, newColConst, 0);
        return pu;
    }

    private ArrayList<Integer> convertSize(int[] s) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(s[0]);
        arrayList.add(s[1]);
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
}