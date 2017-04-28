package com.example.jcdug.andnonogame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

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
    static final String colorTable = "ColorPuzzles";//Name of color puzzle table in database
    static final String yourTable = "YourPuzzles";  //Name of puzzle table in database for Create Your Own Puzzles
    static final String yourColorTable = "YourColorPuzzles";//Name of puzzle table in database for Create Your Own Color Puzzles
    static final String downTable = "DownloadedPuzzles";    //Name of puzzle table in database for Downloaded Puzzles
    static final String downColorTable = "DownloadedColorPuzzles";  //Name of puzzle table in database for Downloaded Color Puzzles
    static final String colID = "PuzzleID";         //ID attribute name in puzzle table
    static final String puzzle = "Puzzle";          //Puzzle attribute name in puzzle table
    static final String row = "Rows";               //Row size attribute name in puzzle table
    static final String col = "Cols";               //Column size attribute name in puzzle table
    static final String color = "Color";            //Color attribute name in puzzle table
    static final String comp = "Complete";          //Complete attribute name in puzzle table
    static final String userID = "UserID";          //User id for downloaded puzzles

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
        db.execSQL("CREATE TABLE " + colorTable + " (" + colID + " INTEGER PRIMARY KEY , " + puzzle + " BLOB , "
                + row + " INTEGER , " + col + " INTEGER , " + comp + " INTEGER)");
        db.execSQL("CREATE TABLE " + yourTable + " (" + colID + " INTEGER PRIMARY KEY , " + puzzle + " BLOB , "
                + row + " INTEGER , " + col + " INTEGER , " + comp + " INTEGER)");
        db.execSQL("CREATE TABLE " + yourColorTable + " (" + colID + " INTEGER PRIMARY KEY , " + puzzle + " BLOB , "
                + row + " INTEGER , " + col + " INTEGER , " + comp + " INTEGER)");
        db.execSQL("CREATE TABLE " + downTable + " (" + userID + " INTEGER NOT NULL , " + colID + " INTEGER NOT NULL , " + puzzle + " BLOB , "
                + row + " INTEGER , " + col + " INTEGER , " + comp + " INTEGER, PRIMARY KEY (" + userID + ", " + colID + "))");
        db.execSQL("CREATE TABLE " + downColorTable + " (" + userID + " INTEGER NOT NULL , " + colID + " INTEGER NOT NULL , " + puzzle + " BLOB , "
                + row + " INTEGER , " + col + " INTEGER , " + comp + " INTEGER, PRIMARY KEY (" + userID + ", " + colID + "))");
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
    public void insertPuzzle(String table, int id, Puzzle p, int[] s, int completed, SQLiteDatabase db) throws IOException {
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
        if (table.equals(puzzleTable)) {
            db.insert(puzzleTable, null, contentValues);
        } else if (table.equals(yourTable)) {
            db.insert(yourTable, null, contentValues);
        } else if (table.equals(downTable)) {
            db.insert(downTable, null, contentValues);
        }
    }

    public void insertPuzzle(String table, int id, ColorPuzzle p, int[] s, int completed, SQLiteDatabase db) throws IOException {
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
        if (table.equals(colorTable)) {
            db.insert(colorTable, null, contentValues);
        } else if (table.equals(yourColorTable)) {
            db.insert(yourColorTable, null, contentValues);
        } else if (table.equals(downColorTable)) {
            db.insert(downColorTable, null, contentValues);
        }
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
    public void insertDownloadedPuzzle(String userid, int id, Puzzle p, int[] s, int completed, SQLiteDatabase db) throws IOException {
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
        contentValues.put(userID, userid);
        contentValues.put(colID, id);
        contentValues.put(puzzle, buf);
        contentValues.put(col, s[0]);
        contentValues.put(row, s[1]);
        contentValues.put(comp, completed);
        db.insertOrThrow(downTable, null, contentValues);
    }

    public void insertDownloadedPuzzle(String userid, int id, ColorPuzzle p, int[] s, int completed, SQLiteDatabase db) throws IOException {
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
        contentValues.put(userID, userid);
        contentValues.put(colID, id);
        contentValues.put(puzzle, buf);
        contentValues.put(col, s[0]);
        contentValues.put(row, s[1]);
        contentValues.put(comp, completed);
        db.insertOrThrow(downColorTable, null, contentValues);
    }

    public void deleteAllYourPuzzles() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + yourTable);
    }

    /**
     * Queries the database for the puzzle with the specified id
     *
     * @param id the puzzle id
     * @return a cursor with the tuple
     */
    public Cursor getPuzzleByID(String table, int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (table) {
            case puzzleTable:
                return db.rawQuery("SELECT * FROM " + puzzleTable + " WHERE " + colID + " = ? ", new String[]{Integer.toString(id)});
            case yourTable:
                return db.rawQuery("SELECT * FROM " + yourTable + " WHERE " + colID + " = ? ", new String[]{Integer.toString(id)});
            case colorTable:
                return db.rawQuery("SELECT * FROM " + colorTable + " WHERE " + colID + " = ? ", new String[]{Integer.toString(id)});
            case yourColorTable:
                return db.rawQuery("SELECT * FROM " + yourColorTable + " WHERE " + colID + " = ? ", new String[]{Integer.toString(id)});
            case downTable:
                return db.rawQuery("SELECT * FROM " + downTable + " WHERE " + colID + " = ? ", new String[]{Integer.toString(id)});
            case downColorTable:
                return db.rawQuery("SELECT * FROM " + downColorTable + " WHERE " + colID + " = ? ", new String[]{Integer.toString(id)});
            default:
                return null;
        }
    }

    /**
     * Queries the database for puzzles with the specified column and row sizes
     *
     * @param c the column size
     * @param r the row size
     * @return a cursor with the tuples
     */
    public Cursor getPuzzlesBySize(String table, int c, int r) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (table) {
            case puzzleTable:
                return db.rawQuery("SELECT * FROM " + puzzleTable + " WHERE " + col + " = ? AND " + row + " = ?", new String[]{Integer.toString(c), Integer.toString(r)});
            case colorTable:
                return db.rawQuery("SELECT * FROM " + colorTable + " WHERE " + col + " = ? AND " + row + " = ?", new String[]{Integer.toString(c), Integer.toString(r)});
            case yourTable:
                return db.rawQuery("SELECT * FROM " + yourTable + " WHERE " + col + " = ? AND " + row + " = ?", new String[]{Integer.toString(c), Integer.toString(r)});
            case yourColorTable:
                return db.rawQuery("SELECT * FROM " + yourColorTable + " WHERE " + col + " = ? AND " + row + " = ?", new String[]{Integer.toString(c), Integer.toString(r)});
            case downTable:
                return db.rawQuery("SELECT * FROM " + downTable + " WHERE " + col + " = ? AND " + row + " = ?", new String[]{Integer.toString(c), Integer.toString(r)});
            case downColorTable:
                return db.rawQuery("SELECT * FROM " + downColorTable + " WHERE " + col + " = ? AND " + row + " = ?", new String[]{Integer.toString(c), Integer.toString(r)});
            default:
                return null;
        }
    }

    /**
     * Queries the database for your puzzles
     *
     * @return a cursor with the tuples
     */
    public Cursor getAllYourPuzzles(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (table) {
            case yourTable:
                return db.rawQuery("SELECT * FROM " + yourTable, new String[]{});
            case yourColorTable:
                return db.rawQuery("SELECT * FROM " + yourColorTable, new String[]{});
            default:
                return null;
        }
    }

    public Cursor getAllDownPuzzles(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (table) {
            case downTable:
                return db.rawQuery("SELECT * FROM " + downTable, new String[]{});
            case downColorTable:
                return db.rawQuery("SELECT * FROM " + downColorTable, new String[]{});
            default:
                return null;
        }
    }

    /**
     * Queries the database for the count of the puzzles grouped by the size
     *
     * @return a cursor with the tuples
     */
    public Cursor getCountBySize(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (table) {
            case puzzleTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numPuzzles FROM " + puzzleTable + " GROUP BY " + col + ", " + row, new String[]{});
            case colorTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numPuzzles FROM " + colorTable + " GROUP BY " + col + ", " + row, new String[]{});
            case yourTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numPuzzles FROM " + yourTable + " GROUP BY " + col + ", " + row, new String[]{});
            case yourColorTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numPuzzles FROM " + yourColorTable + " GROUP BY " + col + ", " + row, new String[]{});
            case downTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numPuzzles FROM " + downTable + " GROUP BY " + col + ", " + row, new String[]{});
            case downColorTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numPuzzles FROM " + downColorTable + " GROUP BY " + col + ", " + row, new String[]{});
            default:
                return null;
        }
    }

    /**
     * Queries the database for the count completed grouped by the size
     *
     * @return a cursor with the tuples
     */
    public Cursor getCountCompletedBySize(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (table) {
            case puzzleTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numComplete FROM " + puzzleTable + " WHERE " + comp + " = ? GROUP BY " + col + ", " + row, new String[]{Integer.toString(1)});
            case colorTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numComplete FROM " + colorTable + " WHERE " + comp + " = ? GROUP BY " + col + ", " + row, new String[]{Integer.toString(1)});
            case yourTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numComplete FROM " + yourTable + " WHERE " + comp + " = ? GROUP BY " + col + ", " + row, new String[]{Integer.toString(1)});
            case yourColorTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numComplete FROM " + yourColorTable + " WHERE " + comp + " = ? GROUP BY " + col + ", " + row, new String[]{Integer.toString(1)});
            case downTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numComplete FROM " + downTable + " WHERE " + comp + " = ? GROUP BY " + col + ", " + row, new String[]{Integer.toString(1)});
            case downColorTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numComplete FROM " + downColorTable + " WHERE " + comp + " = ? GROUP BY " + col + ", " + row, new String[]{Integer.toString(1)});
            default:
                return null;
        }
    }

    /**
     * Queries the database for the count of the puzzles
     *
     * @return a cursor with the tuples
     */
    public Cursor getCountYour(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (table) {
            case yourTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numPuzzles FROM " + yourTable, new String[]{});
            case yourColorTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numPuzzles FROM " + yourColorTable, new String[]{});
            default:
                return null;
        }
    }

    public Cursor getCountDown(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (table) {
            case downTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numPuzzles FROM " + downTable, new String[]{});
            case downColorTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numPuzzles FROM " + downColorTable, new String[]{});
            default:
                return null;
        }
    }

    /**
     * Queries the database for the count completed
     *
     * @return a cursor with the tuples
     */
    public Cursor getCountCompletedYour(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (table) {
            case yourTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numComplete FROM " + yourTable + " WHERE " + comp + " = ?", new String[]{Integer.toString(1)});
            case yourColorTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numComplete FROM " + yourColorTable + " WHERE " + comp + " = ?", new String[]{Integer.toString(1)});
            default:
                return null;
        }
    }

    public Cursor getCountCompletedDown(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (table) {
            case downTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numComplete FROM " + downTable + " WHERE " + comp + " = ?", new String[]{Integer.toString(1)});
            case downColorTable:
                return db.rawQuery("SELECT " + col + ", " + row + ", COUNT(*) AS numComplete FROM " + downColorTable + " WHERE " + comp + " = ?", new String[]{Integer.toString(1)});
            default:
                return null;
        }
    }

    /**
     * Queries the database for the ids of all puzzles
     *
     * @return an array with all of the puzzle ids
     */
    public int[] getAllPuzzleIDs(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs;
        switch (table) {
            case puzzleTable:
                curs = db.rawQuery("SELECT " + colID + " FROM " + puzzleTable, new String[]{});
                break;
            case colorTable:
                curs = db.rawQuery("SELECT " + colID + " FROM " + colorTable, new String[]{});
                break;
            case yourTable:
                curs = db.rawQuery("SELECT " + colID + " FROM " + yourTable, new String[]{});
                break;
            case yourColorTable:
                curs = db.rawQuery("SELECT " + colID + " FROM " + yourColorTable, new String[]{});
                break;
            case downTable:
                curs = db.rawQuery("SELECT " + colID + " FROM " + downTable, new String[]{});
                break;
            case downColorTable:
                curs = db.rawQuery("SELECT " + colID + " FROM " + downColorTable, new String[]{});
                break;
            default:
                return null;
        }
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
    public void updatePuzzle(String table, Integer id, int[][] currState, int completed) throws IOException, ClassNotFoundException {
        //Query the database for the puzzle with the specified id
        Cursor curs = getPuzzleByID(table, id);

        //Get the index of puzzle attribute in the query
        int index = curs.getColumnIndex(puzzle);

        //Move the cursor to the first tuple
        curs.moveToFirst();

        //Get the serialized puzzle
        byte[] b = curs.getBlob(index);

        //Create input streams to deserialize the puzzle object
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        ObjectInputStream in = new ObjectInputStream(bis);

        if (table.equals(puzzleTable) || table.equals(yourTable) || table.equals(downTable)) {
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
            db.update(table, contentValues, colID + " = ? ", new String[]{Integer.toString(id)});
        } else {
            //Deserialize the puzzle object and close the input streams
            ColorPuzzle p = (ColorPuzzle) in.readObject();
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
            db.update(table, contentValues, colID + " = ? ", new String[]{Integer.toString(id)});
        }
    }

    /**
     * Resets the puzzle with the specified id in the database
     *
     * @param id the id of the puzzle to be reset
     * @throws IOException            can be thrown by the input stream
     * @throws ClassNotFoundException can be thrown by the input stream
     */
    public void resetPuzzle(String table, int id) throws IOException, ClassNotFoundException {
        //Queries the database for the puzzle with the specified id
        Cursor curs = getPuzzleByID(table, id);

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
        if (table.equals(puzzleTable) || table.equals(yourTable)) {
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
            db.update(table, contentValues, colID + " = ? ", new String[]{Integer.toString(id)});
        } else if (table.equals(colorTable) || table.equals(yourColorTable)) {
            ColorPuzzle p = (ColorPuzzle) in.readObject();
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
            db.update(table, contentValues, colID + " = ? ", new String[]{Integer.toString(id)});
        }
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
        int[][] r = {{0, 1}, {0, 3}, {0, 5}, {2, 2}, {0, 5}};
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
        int[][] r2 = {{0, 0, 4}, {0, 1, 1}, {1, 4, 1}, {0, 4, 4}, {1, 5, 1}, {0, 4, 4}, {3, 2, 3}, {3, 2, 3}, {0, 4, 4}, {0, 0, 10}};
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
        int[][] r3 = {{1, 1}, {0, 5}, {0, 5}, {0, 3}, {0, 1}};
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
        int[][] sol5 = {{0, 0, 1, 0, 0},
                {0, 1, 1, 1, 0},
                {0, 1, 0, 1, 0},
                {1, 1, 1, 1, 1},
                {1, 0, 1, 0, 1}};
        int[][] r5 = {{0, 0, 1}, {0, 0, 3}, {0, 1, 1}, {0, 0, 5}, {1, 1, 1}};
        int[][] c5 = {{0, 0, 2, 0, 0}, {2, 3, 2, 3, 2}};
        Puzzle fifth = new Puzzle(id5, s5, sol5, r5, c5, 0);

        int id6 = 6;
        int[] s6 = {5, 5};
        int[][] sol6 = {{0, 0, 1, 1, 0},
                {0, 1, 0, 0, 1},
                {1, 1, 1, 0, 0},
                {1, 0, 1, 0, 0},
                {1, 1, 1, 0, 0}};
        int[][] r6 = {{0, 2}, {1, 1}, {0, 3}, {1, 1}, {0, 3}};
        int[][] c6 = {{0, 2, 1, 0, 0},{3, 1, 3, 1, 1}};
        Puzzle sixth = new Puzzle(id6, s6, sol6, r6, c6, 0);

        int id7 = 7;
        int[] s7 = {5, 5};
        int[][] sol7 = {{0, 1, 1, 0, 0},
                {0, 0, 0, 1, 1},
                {0, 1, 1, 1, 1},
                {1, 1, 1, 1, 0},
                {0, 1, 1, 1, 1}};
        int[][] r7 = {{2}, {2}, {4}, {4}, {4}};
        int[][] c7 = {{0, 1, 1, 0, 2},{1, 3, 3, 4, 1}};
        Puzzle seventh = new Puzzle(id7, s7, sol7, r7, c7, 0);

        int id8 = 8;
        int[] s8 = {5, 5};
        int[][] sol8 = {{0, 1, 1, 0, 0},
                {1, 1, 0, 0, 1},
                {1, 1, 1, 1, 0},
                {0, 1, 1, 0, 0},
                {1, 0, 0, 1, 0}};
        int[][] r8 = {{0, 2}, {2, 1}, {0, 4}, {0, 2}, {1, 1}};
        int[][] c8 = {{2, 0, 1, 1, 0},{1, 4, 2, 1, 1}};
        Puzzle eighth= new Puzzle(id8, s8, sol8, r8, c8, 0);

        int id9 = 9;
        int[] s9 = {5, 5};
        int[][] sol9 = {{0, 0, 1, 0, 0},
                {0, 1, 1, 0, 0},
                {1, 0, 1, 0, 1},
                {1, 0, 0, 1, 1},
                {1, 1, 0, 1, 1}};
        int[][] r9 = {{0, 0, 1}, {0, 0, 2}, {1, 1, 1}, {0, 1, 2}, {0, 2, 2}};
        int[][] c9 = {{0, 1, 0, 0, 0}, {3, 1, 3, 2, 3}};
        Puzzle ninth = new Puzzle(id9, s9, sol9, r9, c9, 0);

        int id10 = 10;
        int[] s10 = {5, 5};
        int[][] sol10 = {{0, 1, 0, 0, 0},
                {1, 1, 1, 0, 1},
                {1, 1, 1, 1, 0},
                {1, 0, 1, 0, 0},
                {1, 0, 1, 0, 0}};
        int[][] r10 = {{0, 1}, {3, 1}, {0, 4}, {1, 1}, {1, 1}};
        int[][] c10 = {{4, 3, 4, 1, 1}};
        Puzzle tenth = new Puzzle(id10, s10, sol10, r10, c10, 0);

        int id11 = 11;
        int[] s11 = {5, 5};
        int[][] sol11 = {{0, 1, 0, 0, 0},
                {1, 1, 0, 1, 1},
                {0, 1, 1, 1, 0},
                {0, 1, 1, 1, 0},
                {0, 0, 1, 0, 0}};
        int[][] r11 = {{0, 1}, {2, 2}, {0, 3}, {0, 3}, {0, 1}};
        int[][] c11 = {{1, 4, 3, 3, 1}};
        Puzzle eleventh = new Puzzle(id11, s11, sol11, r11, c11, 0);

        int id12 = 12;
        int[] s12 = {10, 10};
        int[][] sol12 = {{0, 1, 1, 0, 0, 0, 0, 1, 1, 0},
                {1, 0, 0, 1, 0, 0, 1, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
                {1, 1, 0, 0, 0, 0, 0, 0, 1, 1},
                {0, 1, 0, 1, 0, 0, 1, 0, 1, 0},
                {0, 1, 0, 1, 0, 0, 1, 0, 1, 0},
                {0, 1, 0, 0, 1, 1, 0, 0, 1, 0},
                {0, 1, 0, 1, 1, 1, 1, 0, 1, 0},
                {0, 0, 1, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 1, 1, 1, 1, 0, 0, 0}};
        int[][] r12 = {{0, 0, 2, 2}, {1, 1, 1, 1}, {0, 1, 6, 1}, {0, 0, 2, 2}, {1, 1, 1, 1}, {1, 1, 1, 1}, {0, 1, 2, 1}, {0, 1, 4, 1}, {0, 0, 1, 1}, {0, 0, 0, 4}};
        int[][] c12 = {{0, 0, 0, 2, 0, 0, 2, 0, 0, 0},
                {0, 0, 1, 2, 1, 1, 2, 1, 0, 0},
                {0, 1, 1, 1, 2, 2, 1, 1, 1, 0},
                {3, 5, 1, 1, 1, 1, 1, 1, 5, 3}};
        Puzzle twelfth = new Puzzle(id12, s12, sol12, r12, c12, 0);

        int id13 = 13;
        int[] s13 = {10, 10};
        int[][] sol13 = {{1, 1, 0, 0, 0, 0, 0, 0, 1, 1},
                {1, 1, 1, 0, 0, 0, 0, 1, 1, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
                {1, 1, 0, 0, 1, 1, 0, 0, 1, 1},
                {1, 0, 0, 1, 0, 0, 1, 0, 0, 1},
                {1, 1, 0, 0, 1, 1, 0, 0, 1, 1},
                {1, 1, 1, 0, 1, 0, 0, 1, 1, 1},
                {1, 0, 0, 1, 1, 1, 1, 0, 0, 1},
                {1, 1, 0, 0, 0, 0, 0, 0, 1, 1},
                {0, 1, 1, 1, 0, 0, 1, 1, 1, 0}};
        int[][] r13 = {{0, 0, 2, 2}, {0, 0, 3, 3}, {0, 1, 6, 1}, {0, 2, 2, 2}, {1, 1, 1, 1}, {0, 2, 2, 2}, {0, 3, 1, 3}, {0, 1, 4, 1}, {0, 0, 2, 2}, {0, 0, 3, 3}};
        int[][] c13 = {{0, 2, 0, 1, 0, 0, 1, 0, 2, 0},
                {0, 1, 2, 1, 0, 2, 1, 2, 1, 0},
                {0, 2, 1, 1, 2, 1, 1, 1, 2, 0},
                {9, 2, 1, 1, 3, 1, 1, 1, 2, 9}};
        Puzzle thirteenth = new Puzzle(id13, s13, sol13, r13, c13, 0);

        int id14 = 14;
        int[] s14 = {10, 10};
        int[][] sol14 = {{0, 0, 0, 0, 0, 0, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
                {0, 1, 1, 0, 0, 0, 0, 0, 1, 1},
                {1, 1, 1, 0, 0, 0, 1, 0, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 0, 1, 1},
                {0, 1, 1, 1, 1, 1, 0, 0, 1, 1},
                {0, 0, 1, 1, 1, 0, 0, 0, 1, 1},
                {0, 0, 0, 1, 0, 0, 0, 0, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1}};
        int[][] r14 = {{0, 0, 4}, {0, 0, 3}, {0, 2, 2}, {3, 1, 2}, {0, 6, 2}, {0, 5, 2}, {0, 3, 2}, {0, 1, 2}, {0, 0, 10}, {0, 0, 2}};
        int[][] c14 = {{0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                {1, 4, 5, 0, 3, 2, 2, 2, 0, 0},
                {1, 1, 1, 5, 1, 1, 1, 1, 10, 10}};
        Puzzle fourteenth = new Puzzle(id14, s14, sol14, r14, c14, 0);

        int idC1 = 1;
        int[] sC1 = {10,10};
        int[] colorsC1 = {Color.WHITE,Color.RED,Color.DKGRAY,Color.YELLOW};
        int[][] solC1 = {{0,2,2,2,0,0,2,2,0,0},
                         {2,2,0,0,0,0,0,2,2,0},
                         {2,2,2,2,0,0,2,2,2,0},
                         {2,0,0,0,0,0,0,0,2,2},
                         {1,0,2,2,0,2,2,0,0,1},
                         {1,1,2,3,1,2,3,2,0,1},
                         {0,1,1,1,1,1,1,1,1,1},
                         {0,1,1,1,2,2,1,1,0,0},
                         {1,1,0,1,1,1,1,1,1,0},
                         {1,0,1,0,0,0,1,0,1,0}};
        int[][][] rC1 = {{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{3,2},{2,2}},
                         {{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{2,2},{2,2}},
                         {{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{4,2},{3,2}},
                         {{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{1,2},{2,2}},
                         {{0,0},{0,0},{0,0},{0,0},{1,1},{2,2},{2,2},{1,1}},
                         {{2,1},{1,2},{1,3},{1,1},{1,2},{1,3},{1,2},{1,1}},
                         {{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{9,1}},
                         {{0,0},{0,0},{0,0},{0,0},{0,0},{3,1},{2,2},{2,1}},
                         {{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{2,1},{6,1}},
                         {{0,0},{0,0},{0,0},{0,0},{1,1},{1,1},{1,1},{1,1}}};
        int[][][] cC1 = {{{0,0},{0,0},{1,2},{1,2},{0,0},{0,0},{1,2},{0,0},{0,0},{0,0}},
                         {{0,0},{0,0},{1,2},{1,2},{0,0},{2,2},{1,2},{0,0},{0,0},{0,0}},
                         {{3,2},{0,0},{2,2},{1,2},{2,1},{1,1},{1,2},{3,2},{3,2},{0,0}},
                         {{2,1},{3,2},{2,1},{1,3},{1,2},{1,2},{1,3},{1,2},{1,1},{1,2}},
                         {{2,1},{4,1},{1,1},{3,1},{1,1},{1,1},{4,1},{3,1},{2,1},{3,1}}};
        ColorPuzzle color1 = new ColorPuzzle(idC1, sC1, solC1, rC1, cC1, colorsC1, 0);

        int idC2 = 2;
        int[] sC2 = {5, 5};
        int[] colorsC2 = {Color.WHITE, Color.BLUE, Color.GREEN, Color.DKGRAY};
        int[][] solC2 = {{0,0,0,2,0},
                         {2,0,0,2,2},
                         {2,1,1,0,0},
                         {3,1,1,0,0},
                         {1,1,0,0,0}};
        int[][][] rC2 = {{{0,0},{1,2}},
                         {{1,2},{2,2}},
                         {{1,2},{2,1}},
                         {{1,3},{2,1}},
                         {{0,0},{2,1}}};
        int[][][] cC2 = {{{2,2},{0,0},{0,0},{0,0},{0,0}},
                         {{1,3},{0,0},{0,0},{0,0},{0,0}},
                         {{1,1},{3,1},{2,1},{2,2},{1,2}}};
        ColorPuzzle color2 = new ColorPuzzle(idC2, sC2, solC2, rC2, cC2, colorsC2, 0);

        int idC3 = 3;
        int[] sC3 = {5, 10};
        int[] colorsC3 = {Color.WHITE, Color.GREEN, Color.RED, Color.YELLOW};
        int[][] solC3 = {{2, 2, 0, 2, 2},
                         {2, 2, 2, 2, 2},
                         {0, 2, 3, 2, 0},
                         {2, 2, 2, 2, 2},
                         {2, 2, 1, 2, 2},
                         {0, 0, 1, 0, 0},
                         {0, 0, 1, 0, 1},
                         {1, 0, 1, 1, 1},
                         {1, 1, 1, 1, 0},
                         {0, 1, 1, 0, 0}};
        int[][][] rC3 = {{{0,0}, {2,2}, {2,2}},
                         {{0,0}, {0,0}, {5,2}},
                         {{1,2}, {1,3}, {1,2}},
                         {{0,0}, {0,0}, {5,2}},
                         {{2,2}, {1,1}, {2,2}},
                         {{0,0}, {0,0}, {1,1}},
                         {{0,0}, {1,1}, {1,1}},
                         {{0,0}, {1,1}, {3,1}},
                         {{0,0}, {0,0}, {4,1}},
                         {{0,0}, {0,0}, {2,1}}};
        int[][][] cC3 = {{{0,0}, {0,0}, {1,2}, {0,0}, {0,0}},
                         {{2,2}, {0,0}, {1,3}, {0,0}, {2,2}},
                         {{2,2}, {5,2}, {1,2}, {5,2}, {2,2}},
                         {{2,1}, {2,1}, {6,1}, {2,1}, {2,1}}};
        ColorPuzzle color3 = new ColorPuzzle(idC3, sC3, solC3, rC3, cC3, colorsC3, 0);

        try {
            insertPuzzle(puzzleTable, id, firstPuzzle, s, completed, db);
            insertPuzzle(puzzleTable, id2, second, s2, 0, db);
            insertPuzzle(puzzleTable, id3, third, s3, 0, db);
            insertPuzzle(puzzleTable, id4, fourth, s4, 0, db);
            insertPuzzle(puzzleTable, id5, fifth, s5, 0, db);
            insertPuzzle(puzzleTable, id6, sixth, s6, 0, db);
            insertPuzzle(puzzleTable, id7, seventh, s7, 0, db);
            insertPuzzle(puzzleTable, id8, eighth, s8, 0, db);
            insertPuzzle(puzzleTable, id9, ninth, s9, 0, db);
            insertPuzzle(puzzleTable, id10, tenth, s10, 0, db);
            insertPuzzle(puzzleTable, id11, eleventh, s11, 0, db);
            insertPuzzle(puzzleTable, id12, twelfth, s12, 0, db);
            insertPuzzle(puzzleTable, id13, thirteenth, s13, 0, db);
            insertPuzzle(puzzleTable, id14, fourteenth, s14, 0, db);
            insertPuzzle(colorTable, idC1, color1, sC1, 0, db);
            insertPuzzle(colorTable, idC2, color2, sC2, 0, db);
            insertPuzzle(colorTable, idC3, color3, sC3, 0, db);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
