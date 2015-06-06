package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class AI {

	public int player;
	public int playerKing;

	public int opponent;
	public int opponentKing;

	private CheckersMove lastMove;
	private CheckersData lastBoard;

	private HashMap<String, Integer> weightedMoves;
	private File movesFile;
	private FileWriter output;
	private Scanner inputReader;

	private static final double PLUS_INFINITY = Double.MAX_VALUE;
	private static final double MINUS_INFINITY = -1 * Double.MAX_VALUE;
	private static final int LEVEL_LIMIT = 5;
	private static final double WEIGHT_VECTOR[] = {0.7, 0.3};


	private static final int
	GOOD_MOVE = 1,
	BAD_MOVE = -1,
	DRAW_MOVE = 0;

	// Constructor for AI
	AI(int p, int opposition, String file) {
		player = p;
		opponent = opposition;
		if(player == CheckersData.RED) {
			playerKing = CheckersData.RED_KING;
			opponentKing = CheckersData.BLACK_KING;
		}
		else {
			playerKing = CheckersData.BLACK_KING;
			opponentKing = CheckersData.RED_KING;
		}
		weightedMoves = new HashMap<String, Integer>();
		try {
			movesFile = new File(file);
			inputReader = new Scanner(movesFile);
			while(inputReader.hasNextLine()) {
				String s = inputReader.next();
				int w = inputReader.nextInt();
				inputReader.nextLine();
				weightedMoves.put(s, w);
			}
			inputReader.close();
		}
		catch(NullPointerException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}
		catch(FileNotFoundException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}
		finally {
			inputReader.close();
			System.out.println(weightedMoves.size());
		}
	} // end AI()



	// Method to dump learning to file.
	public void dumpToFile() {
		ArrayList<String> OUTPUT = new ArrayList<String>();
		for(String key: weightedMoves.keySet()) {
			String i = weightedMoves.get(key).toString();
			String out = key + " " + i + "\n";
			OUTPUT.add(out);
		}
		try {
			output = new FileWriter(movesFile);
			for(String s: OUTPUT) {
				output.write(s);
			}
			output.close();
		}
		catch(IOException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}
	} // end dumpToFile()


	public void lostGame() {
		weightedMoves.put(lastBoard.hash(), BAD_MOVE);
		lastBoard = null;
		lastMove = null;
	}

	public void wonGame() {
		weightedMoves.put(lastBoard.hash(), GOOD_MOVE);
		lastBoard = null;
		lastMove = null;
	}

	// game was a draw
	public void drawGame() {
		weightedMoves.put(lastBoard.hash(), DRAW_MOVE);
		lastBoard = null;
		lastMove = null;
	}

	// Returns the AI's move for the turn.
	public CheckersMove makeMove(CheckersData currentBoard) {

		CheckersMove[] currentMoves = currentBoard.getLegalMoves(player);
		if(currentMoves == null)
		return null;

		int max_index = -1;		
		double alpha = MINUS_INFINITY;
		double beta = PLUS_INFINITY;

		for(int i = 0; i < currentMoves.length; i++) {
			int[][] boardCopy = currentBoard.getBoardCopy();
			CheckersData copy = new CheckersData();
			copy.setUpGame(boardCopy);
			copy.makeMove(currentMoves[i]);
			String key = copy.hash();
			boolean badMove = false;
			// check to see if the AI has seen this move before
			if(weightedMoves.containsKey(key)) {
				int weight = weightedMoves.get(key);
				switch(weight) {
				case GOOD_MOVE:
					return currentMoves[i];
				case BAD_MOVE:
					badMove = true;
					break;
				case DRAW_MOVE:
					return currentMoves[i];
				}
			}
			// if the move was not found in memory, we do alpha-beta pruning
			if(!badMove) {
				double temp;
				// do something special if the move is a jump and there's only one of them.
				if(currentMoves[i].isJump())
				temp = moveHelper(copy, alpha, beta, true, 0);
				else
				temp = moveHelper(copy, alpha, beta, false, 0);

				if(alpha < temp) {
					alpha = temp;
					max_index = i;
				}
			}
		} // end loop

		if(alpha == MINUS_INFINITY) {
			// only found losing states.
			return null;
		} 

		if(alpha < 0) { // only found negative board states
			weightedMoves.put(lastBoard.hash(), BAD_MOVE);
		}

		// remember the last board played on and the last move made
		lastBoard = new CheckersData();
		lastBoard.setUpGame(currentBoard.getBoardCopy());
		lastMove = currentMoves[max_index];
		return lastMove;

	} // end makeMove()



	// recursive move maker. Alpha-Beta pruning happens in here.
	private double moveHelper(CheckersData board, double alpha, double beta, boolean MAX, int level) {
		level++;
		if(level > LEVEL_LIMIT) {
			CheckersData copy = new CheckersData();
			copy.setUpGame(board.getBoardCopy());
			return evaluateBoard(copy, MAX);
		}

		CheckersMove[] currentMoves;
		if(MAX) {
			currentMoves = board.getLegalMoves(player);
		}
		else {
			currentMoves = board.getLegalMoves(opponent);
		}

		if(currentMoves == null) {
			CheckersData copy = new CheckersData();
			copy.setUpGame(board.getBoardCopy());
			return evaluateBoard(copy, MAX);
		}

		for(int i = 0; i < currentMoves.length; i++) {

			if(alpha < beta) {
				int[][] boardCopy = board.getBoardCopy();
				CheckersData copy = new CheckersData();
				copy.setUpGame(boardCopy);
				copy.makeMove(currentMoves[i]);
				double temp;
				if(currentMoves[i].isJump())
				temp = moveHelper(copy, alpha, beta, MAX, (level-1));
				else
				temp = moveHelper(copy, alpha, beta, !MAX, level);

				if(MAX)
				if(alpha < temp)
				alpha = temp;
				else
				if(beta > temp)
				beta = temp;
			}
		}
		if(MAX)
		return alpha;
		else return beta;
	}

	// heuristic evaluation of a board. Calls on several heuristic functions to make an evaluation,
	// then weighs them with the weight vector.
	private double evaluateBoard(CheckersData board, boolean MAX) {

		double eval = calcPieceDifference(board, MAX)*WEIGHT_VECTOR[0] + 
		calcMoveNumber(board, MAX)*WEIGHT_VECTOR[1];

		return eval;
	}

	private int calcPieceDifference(CheckersData board, boolean MAX) {
		int diff;
		int black = board.numBlackMen();
		int red = board.numRedMen();
		if(player == CheckersData.RED) {
			if(MAX) {
				diff = red - black;
			}
			else
			diff = black - red;
		}
		else {
			if(MAX) {
				diff = black - red;
			}
			else
			diff = red - black;
		}
		return diff;
	}

	private int calcMoveNumber(CheckersData board, boolean MAX) {
		int numMoves = 0;
		try {
			if(MAX)
			numMoves = board.getLegalMoves(player).length;
			else
			numMoves = (-1) * board.getLegalMoves(opponent).length;
		}
		catch(NullPointerException e) {
		}

		return numMoves;
	}

}
