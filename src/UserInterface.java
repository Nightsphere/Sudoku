// UserInterface.java - Sudoku Graphical User Interface
// Author: Chris Wilcox
// Date: 10/15/2016
// Email: wilcox@cs.colostate.edu

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class UserInterface extends JFrame implements ActionListener {

	// User interface variables
	private static final long serialVersionUID = 1L;
	private JPanel topPanel; // Status window
	private JPanel middlePanel; // Game area
	private JPanel bottomPanel; // Buttons window
	private JLabel stepsLabel;
	private JLabel statusLabel;
	private Font font = new Font("Serif", Font.PLAIN, 24);
	private Color topColor = new Color(0x0076A3);
	private Color middleColor = new Color(0x808080);
	private Color bottomColor = new Color(0x6B8E23);
	private ImageIcon numberIcons[] = new ImageIcon[10];
	private JButton loadButton = new JButton("Load");
	private JButton saveButton = new JButton("Save");
	private JButton exitButton = new JButton("Exit");
	private JButton stepButton = new JButton("Step");
	private JButton solveButton = new JButton("Solve");
	private JButton showButton = new JButton("Show");
	private final int width = 9;
	private final int height = 9;
	private JLabel gameBoard[][] = new JLabel[width][height];
	private int numberSteps = 0;
	private GameInterface.eStatus status;
	private boolean bShow = false; // show constraints

	// Instantiate game engine
	private GameInterface game = new GameEngine();

	// User interface constructor
	public UserInterface() {

		// Platform customization
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setupBoard(); // Setup icons and game board
		setupButtons(); // Setup user interface buttons
		setupTopPanel(); // Setup status area (top panel)
		setupMiddlePanel(); // Setup puzzle area (middle panel)
		setupBottomPanel(); // Setup buttons area (bottom panel)

		// Combine panels
		add(topPanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);

		// Window setup
		setupWindow();
	}

	// Main program
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				// Instantiate user interface
				UserInterface ui = new UserInterface();
				ui.setVisible(true);

				// Instantiate student program
				ui.game = new GameEngine();
			}
		});
	}

	// Refresh game board
	public void redrawBoard() {

		// Get data from student
		int data[][] = game.getData();

		// Get data from student
		int constraints[][] = game.getConstraints();
		
		// Get moves from student
		ArrayList<GameInterface.Move> history = game.getSolution();
		if (!history.isEmpty()) {
			GameInterface.Move move = history.get(history.size()-1);
			System.err.println("Filled in " + move.value + " at row " + move.row + ", column " + move.column);
		}
		
		// Update status
		if (status == GameInterface.eStatus.eSolved)
			statusLabel.setText("\t\tStatus: Puzzle Solved");
		else if (status == GameInterface.eStatus.eFailure)
			statusLabel.setText("\t\tStatus: Failed Puzzle");

		// Redraw board
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				
				// Show number for square
				gameBoard[i][j].setIcon(numberIcons[data[i][j]]);
				if (bShow && data[i][j] == 0) {

						// Show constraints for square
						BufferedImage image = buildIcon(constraints[i][j]);
						ImageIcon iconNew = new ImageIcon(image);
						gameBoard[i][j].setIcon(iconNew);
				}
			}
		}

		// Update steps
		stepsLabel.setText("Steps: " + numberSteps);
	}
	
	// Button handler
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == loadButton)
			loadGame();
		else if (e.getSource() == saveButton)
			saveGame();
		else if (e.getSource() == stepButton)
			stepGame();
		else if (e.getSource() == exitButton)
			exitGame();
		else if (e.getSource() == solveButton)
			solveGame();
		else if (e.getSource() == showButton)
			showConstraints();
	}

	// Student methods
	public void loadGame() {
		String filename = selectFile("Select file to load", true);
		game.load(filename);
		redrawBoard();
	}

	public void saveGame() {
		String filename = selectFile("Select file to save", true);
		game.save(filename);
		redrawBoard();
	}

	public void exitGame() {
		setVisible(false); // hide window
		dispose(); // destroy window
	}

	public void stepGame() {
		status = game.step();
		numberSteps++;
		redrawBoard();
		if (status != GameInterface.eStatus.eSuccess)
			endOfProgram(status);
	}

	public void solveGame() {
		while (true) {
			status = game.step();
			numberSteps++;
			redrawBoard();
			if (status != GameInterface.eStatus.eSuccess) {
				endOfProgram(status);
				break;
			}
		}
	}

	public void showConstraints() {
		bShow = !bShow;
		redrawBoard();
	}

	// Initialize game board
	private void setupBoard() {

		// Load icons
		for (int i = 0; i < 10; i++) {
			numberIcons[i] = new ImageIcon("images/" + i + ".png");
			Image image = numberIcons[i].getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
			numberIcons[i] = new ImageIcon(image);
		}

		// Initialize board
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				gameBoard[i][j] = new JLabel(numberIcons[0]);
			}
		}
	}

	// Setup buttons for user interface
	private void setupButtons() {

		loadButton.setToolTipText("Load puzzle from file");
		saveButton.setToolTipText("Save puzzle to file");
		exitButton.setToolTipText("Exit puzzle");
		stepButton.setToolTipText("Single puzzle step");
		solveButton.setToolTipText("Solve entire puzzle");
		showButton.setToolTipText("Show constraints");
		loadButton.addActionListener(this);
		saveButton.addActionListener(this);
		exitButton.addActionListener(this);
		stepButton.addActionListener(this);
		solveButton.addActionListener(this);
		showButton.addActionListener(this);
	}

	// Setup top panel for status area
	private void setupTopPanel() {

		stepsLabel = new JLabel("Steps: " + numberSteps);
		stepsLabel.setFont(font);
		stepsLabel.setForeground(new Color(0xFFFFFF));
		statusLabel = new JLabel("\t\tStatus: In Progress");
		statusLabel.setFont(font);
		statusLabel.setForeground(new Color(0xFFFFFF));
		topPanel = new JPanel();
		topPanel.add(stepsLabel);
		topPanel.add(statusLabel);
		topPanel.setBackground(topColor);
	}

	// Setup middle panel for puzzle area
	private void setupMiddlePanel() {

		middlePanel = new JPanel();
		middlePanel.setLayout(new GridLayout(3, 3, 5, 5));
		middlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		middlePanel.setBackground(Color.black);
		JPanel[][] gamePanels = new JPanel[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				gamePanels[i][j] = new JPanel();
				gamePanels[i][j].setLayout(new GridLayout(3, 3, 3, 3));
				gamePanels[i][j].setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
				gamePanels[i][j].setBackground(middleColor);
				for (int k = 0; k < 3; k++) {
					for (int l = 0; l < 3; l++) {
						int r = i * 3 + k;
						int c = j * 3 + l;
						gamePanels[i][j].add(gameBoard[r][c]);
					}
				}
				middlePanel.add(gamePanels[i][j]);
			}
		}
	}

	// Setup bottom panel for buttons area
	private void setupBottomPanel() {

		bottomPanel = new JPanel();
		bottomPanel.add(loadButton);
		bottomPanel.add(saveButton);
		bottomPanel.add(exitButton);
		bottomPanel.add(stepButton);
		bottomPanel.add(solveButton);
		bottomPanel.add(showButton);
		bottomPanel.setBackground(bottomColor);
	}

	// Setup window attributes
	private void setupWindow() {

		setSize(550, 650);
		setTitle("Sudoku Game");
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	// File selector
	private String selectFile(String title, boolean open) {

		String fileName = null;
		JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File("."));
		jfc.setDialogTitle(title);

		int result;
		if (open)
			result = jfc.showOpenDialog(this);
		else
			result = jfc.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			fileName = file.getAbsolutePath();
		}
		return fileName;
	}
	
	// Handle end of program
	private void endOfProgram(GameInterface.eStatus status) {
		
		// Dialog box
		ImageIcon icon;
		if (status == GameInterface.eStatus.eFailure) {
			statusLabel = new JLabel("\t\tStatus: Failed Puzzle");
			repaint();
			icon = new ImageIcon("images/failure.png");
			Image image = icon.getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH);
			icon = new ImageIcon(image);
	        	JOptionPane.showMessageDialog(null,
		    "Sorry, puzzle not solved!",
		    "Puzzle Failure",
		    JOptionPane.WARNING_MESSAGE, icon);
	} else {
			icon = new ImageIcon("images/success.png");
			Image image = icon.getImage().getScaledInstance(175, 175, Image.SCALE_SMOOTH);
			icon = new ImageIcon(image);
			JOptionPane.showMessageDialog(null,
		    "Congratulations, you solved the puzzle!",
		    "Puzzle Success",
		    JOptionPane.INFORMATION_MESSAGE, icon);
		}
		
		// Update status
	
		
		exitGame();
	}
	// Construct icon for constraints
	private BufferedImage buildIcon(int constraint) {

		// Setup for text rendering
		BufferedImage image = new BufferedImage(80, 80, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D)image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setFont(new Font("TimesRoman", Font.BOLD, 15));
		graphics.setColor(Color.white);
		
		// Display grid of constraints
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				int bit = (row * 3 + col);
				if ((constraint & (1 << bit)) == 0) {
					String s = Integer.toString(bit + 1);
					graphics.drawString(s, 22 + row * 15, 30 + col * 15);
				}
			}
		}
		return image;
	}
}
