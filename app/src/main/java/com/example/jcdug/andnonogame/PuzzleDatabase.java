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

/**
 * Puzzle Database class
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
public class PuzzleDatabase extends SQLiteOpenHelper {
    static final String dbName = "puzzleDB";        //The name of the Database
    static final String puzzleTable = "Puzzles";    //Name of puzzle table in database
    static final String colID = "PuzzleID";         //ID attribute name in puzzle table
    static final String puzzle = "Puzzle";          //Puzzle attribute name in puzzle table
    static final String row = "Rows";               //Row size attribute name in puzzle table
    static final String col = "Cols";               //Column size attribute name in puzzle table
    static final String comp = "Complete";          //Complete attribute name in puzzle table

    /**
     * Constructor for PuzzleDatabase
     *
     * @param context the context attached to the database
     */
    public PuzzleDatabase(Context context) {
        super(context, dbName, null, 1);
    }

    /**
     * Adds the puzzle table with its attributes to the database
     *
     * @param db the writable database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + puzzleTable + " (" + colID + " INTEGER PRIMARY KEY , " + puzzle + " BLOB , "
                + row + " INTEGER , " + col + " INTEGER , " + comp + " INTEGER)");
        addPuzzles(db);
    }

    /**
     * Inserts a puzzle to the database
     *
     * @param id        the id of the puzzle
     * @param p         the puzzle to be put into the database
     * @param s         the size of the puzzle
     * @param completed the completed value of the puzzle (should be 0)
     * @param db        the database
     * @throws IOException Could be thrown using the output stream
     */
    public void insertPuzzle(int id, Puzzle p, int[] s, int completed, SQLiteDatabase db) throws IOException {
        //Create an output stream to serialize the puzzle
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);

        //Serialize the puzzle into a byte array
        out.writeObject(p);
        byte[] buf = bos.toByteArray();
        out.close();
        bos.close();

        //Put the values of the puzzle into the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(colID, id);
        contentValues.put(puzzle, buf);
        contentValues.put(col, s[0]);
        contentValues.put(row, s[1]);
        contentValues.put(comp, completed);
        db.insert(puzzleTable, null, contentValues);
    }

    /**
     * Queries the database for the puzzle with the specified id
     *
     * @param id the puzzle id
     * @return a cursor with the tuple
     */
    public Cursor getPuzzleByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery("SELECT * FROM " + puzzleTable + " WHERE " + colID + " = ? ", new String[]{Integer.toString(id)});
        return curs;
    }

    /**
     * Queries the database for puzzles with the specified column and row sizes
     *
     * @param c the column size
     * @param r the row size
     * @return a cursor with the tuples
     */
    public Cursor getPuzzlesBySize(int c, int r) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery("SELECT * FROM " + puzzleTable + " WHERE " + col + " = ? AND " + row + " = ?", new String[]{Integer.toString(c), Integer.toString(r)});
        return curs;
    }

    /**
     * Queries the database for the count of the puzzles grouped by the size
     *
     * @return a cursor with the tuples
     */
    public Cursor getCountBySize() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numPuzzles FROM " + puzzleTable + " GROUP BY " + col + ", " + row, new String[]{});
        return curs;
    }

    /**
     * Queries the database for the count completed grouped by the size
     *
     * @return a cursor with the tuples
     */
    public Cursor getCountCompletedBySize() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numComplete FROM " + puzzleTable + " WHERE " + comp + " = ? GROUP BY " + col + ", " + row, new String[]{Integer.toString(1)});
        return curs;
    }

    /**
     * Queries the database for the ids of all puzzles
     *
     * @return an array with all of the puzzle ids
     */
    public int[] getAllPuzzleIDs() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery("SELECT " + colID + " FROM " + puzzleTable, new String[]{});
        curs.moveToFirst();
        int[] puzzleIDs = new int[curs.getCount()];
        int i = curs.getColumnIndex(colID);
        int count = 0;
        while (!curs.isAfterLast()) {
            puzzleIDs[count] = curs.getInt(i);
            count++;
            curs.moveToNext();
        }
        return puzzleIDs;
    }

    /**
     * Updates a puzzle in the database
     *
     * @param id        the puzzle id to be changed
     * @param currState the new state
     * @param completed the new completed value
     * @throws IOException            Can be thrown by the input stream
     * @throws ClassNotFoundException Can be thrown by the input stream
     */
    public void updatePuzzle(Integer id, int[][] currState, int completed) throws IOException, ClassNotFoundException {
        //Query the database for the puzzle with the specified id
        Cursor curs = getPuzzleByID(id);

        //Get the index of puzzle attribute in the query
        int index = curs.getColumnIndex(puzzle);

        //Move the cursor to the first tuple
        curs.moveToFirst();

        //Get the serialized puzzle
        byte[] b = curs.getBlob(index);

        //Create input streams to deserialize the puzzle object
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        ObjectInputStream in = new ObjectInputStream(bis);

        //Deserialize the puzzle object and close the input streams
        Puzzle p = (Puzzle) in.readObject();
        bis.close();
        in.close();

        //Set the puzzle object's current state and complete value
        p.setCurrentState(currState);
        p.setCompleted(completed);

        //Create output streams to serialize the puzzle object
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);

        //Serialize the puzzle object and close the output streams
        out.writeObject(p);
        byte[] buf = bos.toByteArray();
        out.close();
        bos.close();

        //Get the database
        SQLiteDatabase db = getWritableDatabase();

        //Update the puzzle values in the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(puzzle, buf);
        contentValues.put(comp, completed);
        db.update(puzzleTable, contentValues, colID + " = ? ", new String[]{Integer.toString(id)});
    }

    /**
     * Resets the puzzle with the specified id in the database
     *
     * @param id the id of the puzzle to be reset
     * @throws IOException            can be thrown by the input stream
     * @throws ClassNotFoundException can be thrown by the input stream
     */
    public void resetPuzzle(int id) throws IOException, ClassNotFoundException {
        //Queries the database for the puzzle with the specified id
        Cursor curs = getPuzzleByID(id);

        //Gets the index of puzzle attribute in the query
        int index = curs.getColumnIndex(puzzle);

        //Moves the cursor to the first tuple
        curs.moveToFirst();

        //Gets the serialized puzzle object
        byte[] b = curs.getBlob(index);

        //Creates input streams to deserialize the puzzle object
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        ObjectInputStream in = new ObjectInputStream(bis);

        //Deserializes the puzzle object and closes the input stream
        Puzzle p = (Puzzle) in.readObject();
        bis.close();
        in.close();

        //Gets the size of the puzzle
        int[] size = p.getSize();

        //Creates a new empty state with the size and sets complete to 0
        int[][] currState = new int[size[1]][size[0]];
        int completed = 0;

        //Sets the current state to the new empty state and complete to 0
        p.setCurrentState(currState);
        p.setCompleted(completed);

        //Creates output streams to serialize the puzzle object
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);

        //Serialize the puzzle object and close the output streams
        out.writeObject(p);
        byte[] buf = bos.toByteArray();
        out.close();
        bos.close();

        //Get the database and update the puzzle in the it
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(puzzle, buf);
        contentValues.put(comp, completed);
        db.update(puzzleTable, contentValues, colID + " = ? ", new String[]{Integer.toString(id)});
    }

    // Database is never upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Adds puzzles to the given database
     *
     * @param db the database
     */
    private void addPuzzles(SQLiteDatabase db) {
        int id = 1;
        int[] s = {5, 5};
        int[][] sol = {{0, 0, 1, 0, 0},
                {0, 1, 1, 1, 0},
                {1, 1, 1, 1, 1},
                {1, 1, 0, 1, 1},
                {1, 1, 1, 1, 1}};
        //String[] r = {"1","3","5","2 2","5"};
        int[][] r = {{0, 1}, {0, 3}, {0, 5}, {2, 2}, {0, 5}};
        //String[] c = {"3","4","3 1","4","3"};
        int[][] c = {{0, 0, 3, 0, 0},
                {3, 4, 1, 4, 3}};
        int completed = 0;
        Puzzle firstPuzzle = new Puzzle(id, s, sol, r, c, completed);
        int id2 = 2;
        int[] s2 = {10, 10};
        int[][] sol2 = {{0, 0, 0, 0, 0, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 1, 0, 0, 1, 0},
                {0, 1, 0, 1, 1, 1, 1, 0, 1, 0},
                {1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
                {1, 0, 0, 1, 1, 1, 1, 1, 0, 1},
                {1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
                {1, 1, 1, 0, 1, 1, 0, 1, 1, 1},
                {1, 1, 1, 0, 1, 1, 0, 1, 1, 1},
                {1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
        //String[] r2 = {"4","1 1","1 4 1","4 4","1 5 1","4 4","3 2 3","3 2 3","4 4","10"};
        int[][] r2 = {{0, 0, 4}, {0, 1, 1}, {1, 4, 1}, {0, 4, 4}, {1, 5, 1}, {0, 4, 4}, {3, 2, 3}, {3, 2, 3}, {0, 4, 4}, {0, 0, 10}};
        //String[] c2 = {"7","2 5","1 5","4 2","1 1 2 1","3 1 2 1","1 4 2","1 7","4 5","7"};
        int[][] c2 = {{0, 0, 0, 0, 1, 3, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 0, 0, 0},
                {0, 2, 1, 4, 2, 2, 4, 1, 4, 0},
                {7, 5, 5, 2, 1, 1, 2, 7, 5, 7}};
        Puzzle second = new Puzzle(id2, s2, sol2, r2, c2, 0);
        int id3 = 3;
        int[] s3 = {5, 5};
        int[][] sol3 = {{0, 1, 0, 1, 0},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {0, 1, 1, 1, 0},
                {0, 0, 1, 0, 0}};
        //String[] r3 = {"1 1","5","5","3","1"};
        int[][] r3 = {{1, 1}, {0, 5}, {0, 5}, {0, 3}, {0, 1}};
        //String[] c3 = {"2","4","4","4","2"};
        int[][] c3 = {{2, 4, 4, 4, 2}};
        Puzzle third = new Puzzle(id3, s3, sol3, r3, c3, 0);

        int id4 = 4;
        int[] s4 = {10, 5};
        int[][] sol4 = {{0, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 1, 1, 1, 1, 0, 0}};
        int[][] r4 = {{0, 8}, {0, 10}, {1, 8}, {0, 8}, {0, 4}};
        int[][] c4 = {{0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, {2, 1, 4, 4, 5, 5, 5, 5, 4, 3}};
        Puzzle fourth = new Puzzle(id4, s4, sol4, r4, c4, 0);

        int id5 = 5;
        int[] s5 = {5, 5};
        int[][] sol5 = {{0, 1, 0, 1, 0},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {0, 1, 1, 1, 0},
                {0, 0, 1, 0, 0}};
        //String[] r3 = {"1 1","5","5","3","1"};
        int[][] r5 = {{1, 1}, {0, 5}, {0, 5}, {0, 3}, {0, 1}};
        //String[] c3 = {"2","4","4","4","2"};
        int[][] c5 = {{2, 4, 4, 4, 2}};
        Puzzle fifth = new Puzzle(id5, s5, sol5, r5, c5, 0);

        int id6 = 6;
        int[] s6 = {5, 5};
        int[][] sol6 = {{0, 1, 0, 1, 0},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {0, 1, 1, 1, 0},
                {0, 0, 1, 0, 0}};
        //String[] r3 = {"1 1","5","5","3","1"};
        int[][] r6 = {{1, 1}, {0, 5}, {0, 5}, {0, 3}, {0, 1}};
        //String[] c3 = {"2","4","4","4","2"};
        int[][] c6 = {{2, 4, 4, 4, 2}};
        Puzzle sixth = new Puzzle(id6, s6, sol6, r6, c6, 0);

        int id7 = 7;
        int[] s7 = {5, 5};
        int[][] sol7 = {{0, 1, 0, 1, 0},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {0, 1, 1, 1, 0},
                {0, 0, 1, 0, 0}};
        //String[] r3 = {"1 1","5","5","3","1"};
        int[][] r7 = {{1, 1}, {0, 5}, {0, 5}, {0, 3}, {0, 1}};
        //String[] c3 = {"2","4","4","4","2"};
        int[][] c7 = {{2, 4, 4, 4, 2}};
        Puzzle seventh = new Puzzle(id7, s7, sol7, r7, c7, 0);

        try {
            insertPuzzle(id, firstPuzzle, s, completed, db);
            insertPuzzle(id2, second, s2, 0, db);
            insertPuzzle(id3, third, s3, 0, db);
            insertPuzzle(id4, fourth, s4, 0, db);
            insertPuzzle(id5, fifth, s5, 0, db);
            insertPuzzle(id6, sixth, s6, 0, db);
            insertPuzzle(id7, seventh, s7, 0, db);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
