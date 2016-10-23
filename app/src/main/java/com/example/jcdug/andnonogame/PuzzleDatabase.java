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
        String[] r = {"1","3","5","2 2","5"};
        String[] c = {"3","4","3 1","4","3"};
        int completed = 0;
        Puzzle firstPuzzle = new Puzzle(id, s, sol, r, c, completed);
        try{
            insertPuzzle(id, firstPuzzle, s, completed);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void insertPuzzle(int id, Puzzle p, int[] s, int completed) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(p);
        byte[] buf = bos.toByteArray();
        out.close();
        bos.close();
        SQLiteDatabase db = getWritableDatabase();
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
