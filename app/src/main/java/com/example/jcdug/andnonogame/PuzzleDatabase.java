package com.example.jcdug.andnonogame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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
    static final String comp = "Complete";
    public PuzzleDatabase(Context context) {
        super(context, dbName, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + puzzleTable + " (" + colID + " INTEGER PRIMARY KEY , " + puzzle + " BLOB , "
                + row + " INTEGER , " + col + " INTEGER , " + comp + " INTEGER)");
        int id = 1;
        int[] s = {5,5};
        int[][] sol =   {{0,0,1,0,0},
                         {0,1,1,1,0},
                         {1,1,1,1,1},
                         {1,1,0,1,1},
                         {1,1,1,1,1}};
        //String[] r = {"1","3","5","2 2","5"};
        int[][] r = {{0,1},{0,3},{0,5},{2,2},{0,5}};
        //String[] c = {"3","4","3 1","4","3"};
        int[][] c = {{0,0,3,0,0},
                     {3,4,1,4,3}};
        int completed = 0;
        Puzzle firstPuzzle = new Puzzle(id, s, sol, r, c, completed);
        int id2 = 2;
        int[] s2 = {10,10};
        int[][] sol2 =  {{0,0,0,0,0,1,1,1,1,0},
                         {0,0,0,0,0,1,0,0,1,0},
                         {0,1,0,1,1,1,1,0,1,0},
                         {1,1,1,1,0,0,1,1,1,1},
                         {1,0,0,1,1,1,1,1,0,1},
                         {1,1,1,1,0,0,1,1,1,1},
                         {1,1,1,0,1,1,0,1,1,1},
                         {1,1,1,0,1,1,0,1,1,1},
                         {1,1,1,1,0,0,1,1,1,1},
                         {1,1,1,1,1,1,1,1,1,1}};
        //String[] r2 = {"4","1 1","1 4 1","4 4","1 5 1","4 4","3 2 3","3 2 3","4 4","10"};
        int[][] r2 = {{0,0,4},{0,1,1},{1,4,1},{0,4,4},{1,5,1},{0,4,4},{3,2,3},{3,2,3},{0,4,4},{0,0,10}};
        //String[] c2 = {"7","2 5","1 5","4 2","1 1 2 1","3 1 2 1","1 4 2","1 7","4 5","7"};
        int[][] c2 =    {{0,0,0,0,1,3,0,0,0,0},
                         {0,0,0,0,1,1,1,0,0,0},
                         {0,2,1,4,2,2,4,1,4,0},
                         {7,5,5,2,1,1,2,7,5,7}};
        Puzzle second = new Puzzle(id2,s2,sol2,r2,c2,0);
        int id3 = 3;
        int[] s3 = {5,5};
        int[][] sol3 =  {{0,1,0,1,0},
                         {1,1,1,1,1},
                         {1,1,1,1,1},
                         {0,1,1,1,0},
                         {0,0,1,0,0}};
        //String[] r3 = {"1 1","5","5","3","1"};
        int[][] r3 = {{1,1},{0,5},{0,5},{0,3},{0,1}};
        //String[] c3 = {"2","4","4","4","2"};
        int[][] c3 = {{2},{4},{4},{4},{2}};
        Puzzle third = new Puzzle(id3,s3,sol3,r3,c3,0);
        try{
            insertPuzzle(id, firstPuzzle, s, completed, db);
            insertPuzzle(id2, second, s2, 0, db);
            insertPuzzle(id3, third, s3, 0, db);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void insertPuzzle(int id, Puzzle p, int[] s, int completed, SQLiteDatabase db) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(p);
        byte[] buf = bos.toByteArray();
        out.close();
        bos.close();
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(colID, id);
        contentValues.put(puzzle, buf);
        contentValues.put(row, s[0]);
        contentValues.put(col, s[1]);
        contentValues.put(comp, completed);
        db.insert(puzzleTable, null, contentValues);
    }

    public Cursor getPuzzleByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery("SELECT * FROM " + puzzleTable + " WHERE " + colID + " = ? ", new String[] {Integer.toString(id)});
        return curs;
    }

    public Cursor getPuzzlesBySize(int r, int c) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery("SELECT * FROM " + puzzleTable + " WHERE " + row + " = ? AND " + col + " = ?", new String[] {Integer.toString(r), Integer.toString(c)});
        return curs;
    }

    public Cursor getCountBySize() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery("SELECT " + row + ", " + col + ", COUNT(*) AS numPuzzles FROM " + puzzleTable + " GROUP BY " + row + ", " + col, new String[] {});
        return curs;
    }
    public int getCountBySize(int r, int c) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery("SELECT COUNT(*) AS numPuzzles FROM " + puzzleTable + " WHERE " + row + " = ? AND " + col + " = ?", new String[] {Integer.toString(r), Integer.toString(c)});
        curs.moveToFirst();
        return curs.getInt(curs.getColumnIndex("numPuzzles"));
    }

    public Cursor getCountCompletedBySize() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery("SELECT " + row + ", " + col + ", COUNT(*) AS numComplete FROM " + puzzleTable + " WHERE " + comp + " = ? GROUP BY " + row + ", " + col, new String[] {Integer.toString(1)});
        return curs;
    }


    public void updatePuzzle(Integer id, int[][] currState, int completed) throws IOException, ClassNotFoundException{
        Cursor curs = getPuzzleByID(id);
        int index = curs.getColumnIndex(puzzle);
        byte[] b = curs.getBlob(index);
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        ObjectInputStream in = new ObjectInputStream(bis);
        Puzzle p = (Puzzle) in.readObject();
        bis.close();
        in.close();
        p.setCurrentState(currState);
        p.setCompleted(completed);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(p);
        byte[] buf = bos.toByteArray();
        out.close();
        bos.close();
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(puzzle,buf);
        contentValues.put(comp, completed);
        db.update(puzzleTable, contentValues, colID + " = ? ", new String[] {Integer.toString(id)});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
