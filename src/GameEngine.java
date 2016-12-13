
// GameEngine.java - Sudoku Game Engine Class
// Author: Chris Wilcox
// Date: 10/15/2016
// Email: wilcox@cs.colostate.edu

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GameEngine implements GameInterface {

	private final int width = 9; // Puzzle width
	private final int height = 9; // Puzzle height
	private int puzzle[][]; // Puzzle data
	private int constraints[][]; // Puzzle constraints
	private ArrayList<Move> history; // Puzzle history

	// Return puzzle data
	public int[][] getData() {
		return puzzle;
	}
	// Load puzzle from file

	// Return puzzle constraints
	public int[][] getConstraints() {
		return constraints;
	}

	// Return puzzle constraints
	public ArrayList<Move> getSolution() {
		return history;
	}

	public void load(String filename) {

		// STUDENT CODE HERE - read puzzle from file
		puzzle = new int[width][height];
		constraints = new int[width][height];
		history = new ArrayList<>();

		try {
			Scanner read = new Scanner(new File(filename));
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					puzzle[i][j] = read.nextInt();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Update constraints
		updateConstraints();
	}

	// Save game
	public void save(String filename) {

		// STUDENT CODE HERE - write puzzle to file
		try {
			PrintWriter write = new PrintWriter(new File(filename));
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					write.print(puzzle[i][j] + " ");
					if ((j % 3) == 2)
						write.print(" ");
				}
				write.println();
			}
			write.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void ryan(int row, int col, int value) {
		Move move = new Move();
		move.column = col;
		move.row = row;
		move.value = value;
		history.add(move);
	}

	// Step game
	public eStatus step() {
		// STUDENT CODE HERE - find next move
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (puzzle[row][col] != 0)
					continue;
				if (constraints[row][col] == 0b111111110) {
					ryan(row, col, 1);
					puzzle[row][col] = 1;
					updateConstraints();
					return eStatus.eSuccess;
				}
				if (constraints[row][col] == 0b111111101) {
					ryan(row, col, 2);
					puzzle[row][col] = 2;
					updateConstraints();
					return eStatus.eSuccess;
				}
				if (constraints[row][col] == 0b111111011) {
					ryan(row, col, 3);
					puzzle[row][col] = 3;
					updateConstraints();
					return eStatus.eSuccess;
				}
				if (constraints[row][col] == 0b111110111) {
					ryan(row, col, 4);
					puzzle[row][col] = 4;
					updateConstraints();
					return eStatus.eSuccess;
				}
				if (constraints[row][col] == 0b111101111) {
					ryan(row, col, 5);
					puzzle[row][col] = 5;
					updateConstraints();
					return eStatus.eSuccess;
				}
				if (constraints[row][col] == 0b111011111) {
					ryan(row, col, 6);
					puzzle[row][col] = 6;
					updateConstraints();
					return eStatus.eSuccess;
				}
				if (constraints[row][col] == 0b110111111) {
					puzzle[row][col] = 7;
					ryan(row, col, 7);
					updateConstraints();
					return eStatus.eSuccess;
				}
				if (constraints[row][col] == 0b101111111) {
					ryan(row, col, 8);
					puzzle[row][col] = 8;
					updateConstraints();
					return eStatus.eSuccess;
				}
				if (constraints[row][col] == 0b011111111) {
					ryan(row, col, 9);
					puzzle[row][col] = 9;
					updateConstraints();
					return eStatus.eSuccess;
				}
			}
		}
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (puzzle[i][j] == 0) {
					updateConstraints();
					return eStatus.eFailure;
				}
			}
		}
		updateConstraints();
		return eStatus.eSolved;
		// Update constraints
	}

	private void dancer(int row, int col, int i) {
		int mask = 0b000000001;
		mask = mask << puzzle[row][i] - 1;
		constraints[row][col] = mask | constraints[row][col];
	}

	private void johnson(int row, int col, int i) {
		int mask = 0b000000001;
		mask = mask << puzzle[i][col] - 1;
		constraints[row][col] = mask | constraints[row][col];
	}

	// Update constraints for all squares
	private void updateConstraints() {

		// STUDENT CODE HERE - update constraints array
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (puzzle[row][col] == 0) {
					for (int i = 0; i < height; i++) {
						if (puzzle[row][i] != 0) {
							dancer(row, col, i);
						}
						if (puzzle[i][col] != 0) {
							johnson(row, col, i);
						}
						if (puzzle[((row / 3) * 3) + (i / 3)][((col / 3) * 3) + (i % 3)] != 0) {
							int mask = 0b000000001;
							mask = mask << (puzzle[((row / 3) * 3) + (i / 3)][((col / 3) * 3) + (i % 3)] - 1);
							constraints[row][col] = mask | constraints[row][col];
						}
					}
				}
			}
		}
	}

	// Check if puzzle is solved
	private boolean isSolved() {

		// STUDENT CODE HERE - is puzzle solved?
		return true;
	}

	// Number of zeros in integer
	private int numberOfZeros(int constraint) {

		// STUDENT CODE HERE - how many zeros in constraint?
		return 0;
	}

	// Position of zeros in integer
	private int positionOfZero(int constraint) {

		// STUDENT CODE HERE - position of zero in constraint?
		return -1;
	}
}