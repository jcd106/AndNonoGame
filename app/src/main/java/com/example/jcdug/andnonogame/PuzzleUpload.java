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

    private String size;             //The size of the puzzle in the form "numColumnsxnumRows"
    private List<List<Integer>> solution;       //The solution state
    private List<List<Integer>> rows;           //The row constraint values
    private List<List<Integer>> cols;
    private List<Float> ratings;
    private List<String> ratingsUser;
    private Float averageRating;

    public PuzzleUpload() {

    }

    public PuzzleUpload(int id, String user, String s, List<List<Integer>> sol, List<List<Integer>> r, List<List<Integer>> c, int comp) {
        ID = id;
        size = s;
        userID = user;
        solution = sol;
        rows = r;
        cols = c;
        completed = comp;
        ratings = new ArrayList<Float>();
        ratingsUser = new ArrayList<String>();
        averageRating = 0f;
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

    public void setID(int id) {
        ID = id;
    }

    /**
     * Getter method for userID
     *
     * @return UserID
     */
    @DynamoDBHashKey(attributeName = "UserID")
    public String getUserID() {
        return userID;
    }

    public void setUserID(String id) {
        userID = id;
    }

    /**
     * Getter method for size
     *
     * @return Puzzle size
     */
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "Size-PuzzleID-index", attributeName = "Size")
    public String getSize() {
        return size;
    }

    public void setSize(String s) {
        size = s;
    }

    /**
     * Getter method for solution
     *
     * @return the solution state of the puzzle
     */
    @DynamoDBAttribute(attributeName = "Solution")
    public List<List<Integer>> getSolution() {
        return solution;
    }

    public void setSolution(List<List<Integer>> s) {
        solution = s;
    }

    /**
     * Getter method for ratings
     *
     * @return the ratings of a puzzle
     */
    @DynamoDBAttribute(attributeName = "Ratings")
    public List<Float> getRatings() {
        return ratings;
    }

    public void setRatings(List<Float> r) {
        ratings = r;
    }

    /**
     * Getter method for ratingsUser
     *
     * @return the userIDs of the users who rated a puzzle
     */
    @DynamoDBAttribute(attributeName = "RatingsUserIDs")
    public List<String> getRatingsUser() {
        return ratingsUser;
    }

    public void setRatingsUser(List<String> ru) {
        ratingsUser = ru;
    }

    /**
     * Getter method for average rating
     *
     * @return the average rating of a puzzle
     */
    @DynamoDBAttribute(attributeName = "AverageRating")
    public Float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Float aR) {
        averageRating = aR;
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

    public void setRows(List<List<Integer>> r) {
        rows = r;
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

    public void setCols(List<List<Integer>> c) {
        cols = c;
    }

    /**
     * Getter method for completed
     *
     * @return the value of of completed
     */
    @DynamoDBAttribute(attributeName = "Completed")
    public int getCompleted() {
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

    @Override
    public String toString() {
        return "Binary: " + userID.substring(0,8) + ", " + ID + ", " + size + ", " + averageRating + " star(s)";
    }

    public boolean updateRatings(Float newRating, String user) {
        for (int i = 0; i < ratingsUser.size(); i++) {
            if (user.equals(ratingsUser.get(i)))
                return false;
        }

        ratings.add(newRating);
        ratingsUser.add(user);
        averageRating = ((averageRating * (ratings.size() - 1)) + newRating) / (ratings.size());

        return true;
    }

    public Puzzle convertToPuzzle() {
        int[][] pSolution, pRows, pCols;
        String[] splitSize = size.split("x");
        int[] pSize = {Integer.parseInt(splitSize[0]), Integer.parseInt(splitSize[1])};
        pSolution = convert2d(solution);
        pRows = convert2d(rows);
        pCols = convert2d(cols);
        Puzzle p = new Puzzle(ID, userID, pSize, pSolution, pRows, pCols, 0);
        return p;
    }

    public int[][] convert2d(List<List<Integer>> list) {
        int[][] arr = new int[list.size()][list.get(0).size()];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                arr[i][j] = list.get(i).get(j);
            }
        }
        return arr;
    }
}