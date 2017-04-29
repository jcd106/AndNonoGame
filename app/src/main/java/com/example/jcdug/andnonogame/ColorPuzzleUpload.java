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
 * Color Puzzle Upload Object Class
 * Stores the id, size, solution state, colors,
 * row and column constraint values, and an int for completion
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
@DynamoDBTable(tableName = "ColorPuzzles")
public class ColorPuzzleUpload implements Serializable {
    private int ID;                 //The unique ID of the puzzle
    private int completed;          //completed = 1 if puzzle is complete, 0 otherwise

    private String userID;

    private String size;             //The size of the puzzle in the form "numColumnsxnumRows"
    private List<List<Integer>> solution;       //The solution state
    private List<List<List<Integer>>> rows;           //The row constraint values
    private List<List<List<Integer>>> cols;
    private List<Integer> colors;
    private List<Float> ratings;
    private List<String> ratingsUser;
    private Float averageRating;

    /**
     * Constructor for Uploading color puzzles to DynamoDB
     * @param id     The id of the puzzle
     * @param s      The size of the puzzle
     * @param sol    The solution state
     * @param r      The row constraint values
     * @param c      The column constraint values
     * @param colors The colors for the puzzle
     * @param comp   The value for complete
     */
    public ColorPuzzleUpload(int id, String user, String s, List<List<Integer>> sol, List<List<List<Integer>>> r, List<List<List<Integer>>> c, List<Integer> colors, int comp) {
        ID = id;
        userID = user;
        size = s;
        solution = sol;
        rows = r;
        cols = c;
        completed = comp;
        this.colors = colors;
        ratings = new ArrayList<Float>();
        ratingsUser = new ArrayList<String>();
        averageRating = 0f;
    }

    public ColorPuzzleUpload(){

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

    public void setID(int id) { ID = id; }

    /**
     * Getter method for userID
     *
     * @return UserID
     */
    @DynamoDBHashKey(attributeName = "UserID")
    public String getUserID() { return userID; }

    public void setUserID(String id) { userID = id; }

    /**
     * Getter method for size
     *
     * @return Puzzle size
     */
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "Size-PuzzleID-index", attributeName = "Size")
    public String getSize() {
        return size;
    }

    public void setSize(String s) { size = s; }

    /**
     * Getter method for solution
     *
     * @return the solution state of the puzzle
     */
    @DynamoDBAttribute(attributeName = "Solution")
    public List<List<Integer>> getSolution() {
        return solution;
    }

    public void setSolution(List<List<Integer>>  s) { solution = s; }

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
    public List<List<List<Integer>>> getRows() {
        return rows;
    }

    public void setRows(List<List<List<Integer>>>  r) { rows = r; }

    /**
     * Getter method for cols
     *
     * @return the column constraint values
     */
    @DynamoDBAttribute(attributeName = "ColConstraints")
    public List<List<List<Integer>>> getCols() {
        return cols;
    }

    public void setCols(List<List<List<Integer>>>  c) { cols = c;}

    /**
     * Getter method for colors
     * @return the color values
     */
    @DynamoDBAttribute(attributeName = "Colors")
    public List<Integer> getColors() { return colors; }

    public void setColors(List<Integer> c) { colors = c; }

    /**
     * Getter method for completed
     *
     * @return the value of of completed
     */
    @DynamoDBAttribute(attributeName = "Completed")
    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int c) { completed = c; }

    @Override
    public String toString(){
        return "Color: " + userID + ", " + ID + "," + size;
    }

    public ColorPuzzle convertToPuzzle() {
        int[] pColors = new int[colors.size()];
        for (int i = 0; i < colors.size(); i++) {
            pColors[i] = colors.get(i);
        }
        int[][] pSolution;
        int[][][] pRows, pCols;
        String[] splitSize = size.split("x");
        int[] pSize = {Integer.parseInt(splitSize[0]),Integer.parseInt(splitSize[1])};
        pSolution = convert2d(solution);
        pRows = convert3d(rows);
        pCols = convert3d(cols);
        ColorPuzzle p = new ColorPuzzle(ID, userID, pSize, pSolution, pRows, pCols, pColors, 0);
        return p;
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

    public int[][] convert2d(List<List<Integer>> list) {
        int[][] arr = new int[list.size()][list.get(0).size()];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                arr[i][j] = list.get(i).get(j);
            }
        }
        return arr;
    }

    public int[][][] convert3d(List<List<List<Integer>>> list) {
        int[][][] arr = new int[list.size()][list.get(0).size()][list.get(0).get(0).size()];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                for (int k = 0; k < list.get(i).get(j).size(); k++) {
                    arr[i][j][k] = list.get(i).get(j).get(k);
                }
            }
        }
        return arr;
    }
}