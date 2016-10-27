package com.example.jcdug.andnonogame;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jcdug on 10/26/2016.
 */

public class PuzzleTest {
    private final int id = 1;
    private final int[] s = {5, 5};
    private final int[][] sol = {{0, 0, 1, 0, 0},
            {0, 1, 1, 1, 0},
            {1, 1, 1, 1, 1},
            {1, 1, 0, 1, 1},
            {1, 1, 1, 1, 1}};
    private final int[][] r = {{0, 1}, {0, 3}, {0, 5}, {2, 2}, {0, 5}};
    private final int[][] c = {{0, 0, 3, 0, 0}, {3, 4, 1, 4, 3}};
    private int completed = 0;

    @Test
    public void testGetters() throws Exception {
        Puzzle puzzle = new Puzzle(id, s, sol, r, c, completed);
        assertEquals(id, puzzle.getID());
        assertArrayEquals(s, puzzle.getSize());
        assertArrayEquals(sol, puzzle.getSolution());
        assertArrayEquals(new int[s[1]][s[0]], puzzle.getCurrentState());
        assertArrayEquals(r, puzzle.getRows());
        assertArrayEquals(c, puzzle.getCols());
        assertEquals(completed, puzzle.isCompleted());
    }

    @Test
    public void testSetters() throws Exception {
        Puzzle puzzle = new Puzzle(id, s, sol, r, c, completed);
        int[][] newState = new int[s[1]][s[0]];
        newState[1][1] = 1;
        puzzle.setCurrentState(newState);
        assertArrayEquals(newState, puzzle.getCurrentState());

        puzzle.setCompleted(1);
        assertEquals(1, puzzle.isCompleted());
    }
}
