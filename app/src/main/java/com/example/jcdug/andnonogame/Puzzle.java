package com.example.jcdug.andnonogame;

import java.io.Serializable;

/**
 * Created by jcdug on 10/22/2016.
 */

public class Puzzle implements Serializable
{
    private static int ID;
    private static int[] size;
    private int[][] currentState;
    private static int[][] solution;
    private static String[] rows;
    private static String[] cols;
    public Puzzle(int id, int[] s, int[][] sol, String[] r, String[] c){
        ID = id;
        size = s;
        currentState = new int[solution.length][solution[0].length];
        solution = sol;
        rows = r;
        cols = c;
    }
    public int getID(){
        return ID;
    }
    public int[] getSize(){
        return size;
    }
    public int[][] getCurrentState(){
        return currentState;
    }
    public int[][] getSolution(){
        return solution;
    }
    public String[] getRows(){
        return rows;
    }
    public String[] getCols(){
        return cols;
    }
    public void setCurrentState(int[][] cs){
        currentState = cs;
    }
}