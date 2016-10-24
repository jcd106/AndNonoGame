package com.example.jcdug.andnonogame;

import java.io.Serializable;

/**
 * Created by jcdug on 10/22/2016.
 */

public class Puzzle implements Serializable
{
    private int ID;
    private int[] size;
    private int[][] currentState;
    private int[][] solution;
    private int[][] rows;
    private int[][] cols;
    private int completed;

    public Puzzle(int id, int[] s, int[][] sol, int[][] r, int[][] c, int comp){
        ID = id;
        size = s;
        currentState = new int[s[0]][s[1]];
        solution = sol;
        rows = r;
        cols = c;
        completed = comp;
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
    public int[][] getRows(){
        return rows;
    }
    public int[][] getCols(){
        return cols;
    }
    public void setCurrentState(int[][] cs){
        currentState = cs;
    }

    public int isCompleted() {
        return completed;
    }

    public void setCompleted(int c) {
        completed = c;
    }
}