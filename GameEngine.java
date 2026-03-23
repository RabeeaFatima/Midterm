package edu.txst.midterm;

public class GameEngine {
	private Board board;
	private int playerRow;
	private int playerCol;
	private int exitRow;
	private int exitCol;
	private int coinsCollected;

	// Cell Type Constants
	private static final int FLOOR = 0;
	private static final int WALL = 1;
	private static final int COIN = 2;
	private static final int EXIT = 5;
	private static final int PLAYER = 6;

	public GameEngine(Board board) {
		this.board = board;
		this.coinsCollected = 0;
		findPlayer();
		findExit();
	}

	public boolean playerWins() {
		return playerRow == exitRow && playerCol == exitCol;
	}

	private void findPlayer() {
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 10; c++) {
				if (board.getCell(r, c) == PLAYER) {
					playerRow = r;
					playerCol = c;
					return;
				}
			}
		}
	}
  
	private void findExit() {
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 10; c++) {
				if (board.getCell(r, c) == EXIT) {
					exitRow = r;
					exitCol = c;
					return;
				}
			}
		}
	}


	public boolean movePlayer(int dRow, int dCol) {
		int targetRow = playerRow + dRow;
		int targetCol = playerCol + dCol;
		int targetCell = board.getCell(targetRow, targetCol);

		if (targetCell == WALL || targetCell == -1) {
			return false;
		}

		if (targetCell == COIN) {
			coinsCollected++;
		}

		if (playerRow == exitRow && playerCol == exitCol) {
			board.setCell(playerRow, playerCol, EXIT);
		} else {
			board.setCell(playerRow, playerCol, FLOOR);
		}

		playerRow = targetRow;
		playerCol = targetCol;
		board.setCell(playerRow, playerCol, PLAYER);

		board.stepCounter.increaseSteps();

		return true;
	}
  
	public int getSteps() {
		return board.stepCounter.getSteps();
	}

	
	public int getCoinsCollected() {
		return coinsCollected;
	}

	public int getScore() {
		return getSteps() * -1 + coinsCollected * 5;
	}
}
