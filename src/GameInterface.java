// GameInterface.java - Sudoku Game Interface
// Author: Chris Wilcox
// Date: 10/15/2016
// Email: wilcox@cs.colostate.edu

import java.util.ArrayList;

public interface GameInterface {

	// Load puzzle from file
	public void load(String filename);

	// Save puzzle to file
	public void save(String filename);

	// Return values
	public enum eStatus {
		eSuccess, // Made successful move
		eFailure, // Cannot solve puzzle!
		eSolved   // Solved entire puzzle
	}
	
	// Fill in one square, if possible
	public eStatus step();
	
	// Getter for puzzle data
	public int[][] getData();
	
	// Getter for puzzle constraints
	public int[][] getConstraints();
	
	// Inner class for puzzle move
	public class Move {
		int row;
		int column;
		int value;
	}

	// Getter for puzzle solution
	public ArrayList<Move> getSolution();
}
