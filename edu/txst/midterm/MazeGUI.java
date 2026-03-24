package edu.txst.midterm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Graphical user interface for the maze game.
 * This class displays the board, handles keyboard input,
 * loads levels, resets the game, and shows step and coin counts.
 */
public class MazeGUI extends JFrame {
	private Board originalBoard;
	private Board currentBoard;
	private GameEngine engine;
	private GamePanel gamePanel;
	private InfoPanel infoPanel;
	private JMenuItem resetItem;
/**
 * Creates the maze game window and initializes
 * the menu, information panel, game panel, and keyboard controls.
 */
	public MazeGUI() {
		setTitle("16-Bit Maze");
		setSize(640, 480); // Adjusted for 10x5 grid with scaling
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
/**
 * Creates and configures the game menu bar,
 * including the Open and Reset options.
 */
		initMenu();

		infoPanel = new InfoPanel();
		gamePanel = new GamePanel();
		add(infoPanel, BorderLayout.NORTH);
		add(gamePanel, BorderLayout.CENTER);

		// Handle Keyboard Input
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (engine == null)
					return;

				boolean moved = false;

switch (e.getKeyCode()) {
	case KeyEvent.VK_UP -> moved = engine.movePlayer(-1, 0);
	case KeyEvent.VK_DOWN -> moved = engine.movePlayer(1, 0);
	case KeyEvent.VK_LEFT -> moved = engine.movePlayer(0, -1);
	case KeyEvent.VK_RIGHT -> moved = engine.movePlayer(0, 1);
}

if (moved) {
	infoPanel.setInfoSteps(engine.getSteps());
	infoPanel.setInfoCoins(engine.getCoinsCollected());
	gamePanel.repaint();
}

// Check for victory
if (engine.playerWins()) {
	int points = infoPanel.getInfoSteps() * -1 + infoPanel.getInfoCoins() * 5;

	JOptionPane.showMessageDialog(MazeGUI.this,
			"Congratulations! You found the exit.\nYou got "
					+ points + " points",
			"Level Complete", JOptionPane.INFORMATION_MESSAGE);

	engine = null;
	resetItem.setEnabled(false);
}
			}
		});
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");

		JMenuItem openItem = new JMenuItem("Open");
		resetItem = new JMenuItem("Reset");
		resetItem.setEnabled(false); // Disabled by default

		openItem.addActionListener(e -> openFile());
		resetItem.addActionListener(e -> resetGame());

		gameMenu.add(openItem);
		gameMenu.add(resetItem);
		menuBar.add(gameMenu);
		setJMenuBar(menuBar);
	}
/**
 * Opens a file chooser so the user can load a maze level from a CSV file.
 * After loading, a new game starts with counters reset.
 */
	private void openFile() {
	JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
	int result = fileChooser.showOpenDialog(this);

	if (result == JFileChooser.APPROVE_OPTION) {
		File selectedFile = fileChooser.getSelectedFile();
		CSVBoardLoader loader = new CSVBoardLoader();

		originalBoard = loader.load(selectedFile.getAbsolutePath());
		currentBoard = originalBoard.clone();
		engine = new GameEngine(currentBoard);

		infoPanel.setInfoSteps(0);
		infoPanel.setInfoCoins(0);

		resetItem.setEnabled(true);
		gamePanel.setBoard(currentBoard);
		gamePanel.repaint();
		requestFocusInWindow();
	}
}
/**
 * Resets the current game to the original loaded board
 * and resets the displayed step and coin counters.
 */
	private void resetGame() {
	if (originalBoard != null) {
		currentBoard = originalBoard.clone();
		engine = new GameEngine(currentBoard);
		infoPanel.setInfoSteps(0);
		infoPanel.setInfoCoins(0);
		gamePanel.setBoard(currentBoard);
		gamePanel.repaint();
		requestFocusInWindow();
	}
}
/**
 * Panel that displays the current number of steps
 * and collected coins.
 */
	// Inner class for information panel
	private class InfoPanel extends JPanel {
		private JLabel infoSteps;
		private JLabel infoCoins;
/**
 * Creates the information panel and initializes
 * the step and coin labels.
 */
		public InfoPanel() {
			this.setLayout(new FlowLayout());
			this.add(new JLabel("Steps: "));
			// infoRemainingSteps is a label which value can be changed using its method called
			// setText
			infoSteps = new JLabel("0");
			this.add(infoSteps);
			this.add(new JLabel("Coins: "));
			// infoCoins is a label which value can be changed using its method called setText
			infoCoins = new JLabel("0");
			this.add(infoCoins);
		}
/**
 * Updates the step counter label.
 *
 * @param remainingSteps the number of steps to display
 */
		public void setInfoSteps(int remainingSteps) {
			this.infoSteps.setText(Integer.toString(remainingSteps));
		}
/**
 * Returns the current step value shown in the label.
 *
 * @return the displayed step count
 */
		public int getInfoSteps() {
			return Integer.parseInt(this.infoSteps.getText());
		}
/**
 * Updates the coin counter label.
 *
 * @param infoCoins the number of coins to display
 */
		public void setInfoCoins(int infoCoins) {
			this.infoCoins.setText(Integer.toString(infoCoins));
		}
/**
 * Returns the current coin value shown in the label.
 *
 * @return the displayed coin count
 */
		public int getInfoCoins() {
			return Integer.parseInt(this.infoCoins.getText());
		}
	}

	// Inner class for custom rendering
	private class GamePanel extends JPanel {
		private Board board;
		private final int TILE_SIZE = 64; // Scale up for visibility
/**
 * Sets the board that will be drawn on the panel.
 *
 * @param board the board to display
 */
		public void setBoard(Board board) {
			this.board = board;
		}
/**
 * Draws the maze board and all of its cells.
 *
 * @param g the graphics object used for drawing
 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (board == null)
				return;

			for (int r = 0; r < 6; r++) {
				for (int c = 0; c < 10; c++) {
					int cell = board.getCell(r, c);
					drawTile(g, cell, c * TILE_SIZE, r * TILE_SIZE);
				}
			}
		}
/**
 * Draws one tile of the maze using a color based on the cell type.
 *
 * @param g the graphics object used for drawing
 * @param type the tile type value
 * @param x the x-coordinate of the tile
 * @param y the y-coordinate of the tile
 */
		private void drawTile(Graphics g, int type, int x, int y) {
			// Placeholder colors until you link the sprite loading logic
			switch (type) {
				case 0 -> g.setColor(Color.LIGHT_GRAY); // Floor
				case 1 -> g.setColor(Color.DARK_GRAY); // Wall
				case 2 -> g.setColor(Color.YELLOW); // Coin
				case 5 -> g.setColor(Color.MAGENTA); // Exit
				case 6 -> g.setColor(Color.BLUE); // Player
				default -> g.setColor(Color.BLACK);
			}
			g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
			g.setColor(Color.WHITE);
			g.drawRect(x, y, TILE_SIZE, TILE_SIZE); // Grid lines
		}
	}
/**
 * Starts the maze game application.
 *
 * @param args command-line arguments
 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MazeGUI().setVisible(true));
	}
}
