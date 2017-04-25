package com.example.jcdug.andnonogame;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Puzzle Object Class
 * Stores the id, size, current state, solution state,
 * row and column constraint values, and an int for completion
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
@DynamoDBTable(tableName = "Puzzle")
public class PuzzleUpload implements Serializable {
    private int ID;                 //The unique ID of the puzzle
   /* private int[] size;             //The size of the puzzle in the form {numColumns, numRows}
    private int[][] currentState;   //The current state of the puzzle
    private int[][] solution;       //The solution state
    private int[][] rows;           //The row constraint values
    private int[][] cols;*/           //The column constraint values
    private int completed;          //completed = 1 if puzzle is complete, 0 otherwise

    private List<Integer> size;             //The size of the puzzle in the form {numColumns, numRows}
    private List<List<Integer>> currentState;   //The current state of the puzzle
    private List<List<Integer>> solution;       //The solution state
    private List<List<Integer>> rows;           //The row constraint values
    private List<List<Integer>> cols;


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
    /*
    public PuzzleUpload(int id, int[] s, int[][] sol, int[][] r, int[][] c, int comp) {
        ID = id;
        size = s;
        currentState = new int[s[1]][s[0]];
        solution = sol;
        rows = r;
        cols = c;
        completed = comp;
    }
    */


    public PuzzleUpload(int id, List<Integer> s, List<List<Integer>> sol, List<List<Integer>> r, List<List<Integer>> c, int comp) {
        ID = id;
        size = s;
        currentState = new ArrayList<List<Integer>>(s.get(0));
        for(int i = 0; i < s.get(0); i++){
            currentState.add(new ArrayList<Integer>(Collections.nCopies(s.get(1), 0)));
        }
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
    @DynamoDBHashKey(attributeName = "PuzzleID")
    public int getID() {
        return ID;
    }

    /**
     * Getter method for size
     *
     * @return Puzzle size
     */
    @DynamoDBIndexHashKey(attributeName = "Size")
    public List<Integer> getSize() {
        return size;
    }

    /**
     * Getter method for currentState
     *
     * @return the current state of the puzzle
     */
    //@DynamoDBAttribute(attributeName = "CurrentState")
    public List<List<Integer>> getCurrentState() {
        return currentState;
    }

    /**
     * Getter method for solution
     *
     * @return the solution state of the puzzle
     */
    public List<List<Integer>> getSolution() {
        return solution;
    }

    /**
     * Getter method for rows
     *
     * @return the row constraint values
     */
    @DynamoDBAttribute(attributeName = "RowConstraints")
    public List<List<Integer>> getRows() {
        return rows;
    }

    /**
     * Getter method for cols
     *
     * @return the column constraint values
     */
    @DynamoDBAttribute(attributeName = "ColConstraints")
    public List<List<Integer>> getCols() {
        return cols;
    }

    /**
     * Getter method for completed
     *
     * @return the value of of completed
     */
    @DynamoDBAttribute(attributeName = "Completed")
    public int isCompleted() {
        return completed;
    }

    /**
     * Setter method for currentState
     *
     * @param cs the new state
     */
    public void setCurrentState(List<List<Integer>> cs) {
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