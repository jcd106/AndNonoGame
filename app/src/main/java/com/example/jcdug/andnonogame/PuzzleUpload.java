package com.example.jcdug.andnonogame;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
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
@DynamoDBTable(tableName = "Puzzles")
public class PuzzleUpload implements Serializable {
    private int ID;                 //The unique ID of the puzzle
    private int completed;          //completed = 1 if puzzle is complete, 0 otherwise

    private String userID;

    private List<Integer> size;             //The size of the puzzle in the form {numColumns, numRows}
    private List<List<Integer>> solution;       //The solution state
    private List<List<Integer>> rows;           //The row constraint values
    private List<List<Integer>> cols;


    public PuzzleUpload(int id, String user, List<Integer> s, List<List<Integer>> sol, List<List<Integer>> r, List<List<Integer>> c, int comp) {
        ID = id;
        size = s;
        userID = user;
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
    @DynamoDBRangeKey(attributeName = "PuzzleID")
    public int getID() {
        return ID;
    }

    /**
     * Getter method for userID
     *
     * @return UserID
     */
    @DynamoDBHashKey(attributeName = "UserID")
    public String getUserID() { return userID; }

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
     * Setter method for completed
     *
     * @param c the new value
     */
    public void setCompleted(int c) {
        completed = c;
    }
}