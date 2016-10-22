package com.example.jcdug.andnonogame;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;

/**
 * Created by jcdug on 10/22/2016.
 */

public class PuzzleDatabase extends SQLiteOpenHelper {
    static final String dbName = "puzzleDB";
    static final String puzzleTable = "Puzzles";
    static final String colID = "PuzzleID";
    static final String puzzle = "Puzzle";
    static final String row = "Rows";
    static final String col = "Cols";
    public PuzzleDatabase(Context context) {
        super(context, dbName, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + puzzleTable + " (" + colID + " INTEGER PRIMARY KEY , " + puzzle + " BLOB , "
                + row + " INTEGER , " + col + " INTEGER)");
        int id = 1;
        int[] s = {5,5};
        int[][] sol =   {{0,0,1,0,0},
                         {0,1,1,1,0},
                         {1,1,1,1,1},
                         {1,1,0,1,1},
                         {1,1,1,1,1}};
        String[] r = {"1","3","5","2 2","5"};
        String[] c = {"3","4","3 1","4","3"};
        Puzzle firstPuzzle = new Puzzle(id,s,sol,r,c);
    }

    public boolean insertPuzzle(int id, Puzzle p, int[] s){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(colID, id);
        contentValues.put(row, s[0]);
        contentValues.put(col, s[1]);
        db.insert(puzzleTable, null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
