package test;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
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

	public static final double PLUS_INFINITY = Double.MAX_VALUE;
	public static final double MINUS_INFINITY = -1 * Double.MAX_VALUE;
	private static final int LEVEL_LIMIT = 5;
	private double WEIGHT_VECTOR[]; // {0.15, 0.4, 0.20, 0.25}; // { MoveDifferenceHeuristic, PieceDifferenceHeuristic, 
																		//  Distance Heuristic, ProtectedPiecesHeuristic     }
	private double WEIGHT_VECTOR_2[]; // {0.15, 0.7, 0.05, 0.1};

	private static final int late_game = 50;
	
	private boolean isRandom;
	
	private boolean isCombat;
	
	private PowerUp pow;
	
	private static final int
	GOOD_MOVE = 1,
	BAD_MOVE = -1,
	DRAW_MOVE = 0;

	// Constructor for AI
	AI(int p, int opposition, String file, boolean combat, double[] w1, double[] w2) {
		isRandom = false;
		isCombat = combat;
		player = p;
		opponent = opposition;
		int len = w1.length;
		WEIGHT_VECTOR = new double[len];
		WEIGHT_VECTOR_2 = new double[len];
		for(int i = 0; i < len; i++) {
			WEIGHT_VECTOR[i] = w1[i];
			WEIGHT_VECTOR[i] = w2[i];
		}
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
		if(!isRandom) {
			weightedMoves.put(lastBoard.hash(), BAD_MOVE);
			lastBoard = null;
			lastMove = null;
		}
		isRandom = false;
	}

	public void wonGame() {
		if(!isRandom) {
			weightedMoves.put(lastBoard.hash(), GOOD_MOVE);
			lastBoard = null;
			lastMove = null;
		}
		isRandom = false;
	}

	// game was a draw
	public void drawGame() {
		if(!isRandom) {
			weightedMoves.put(lastBoard.hash(), DRAW_MOVE);
			lastBoard = null;
			lastMove = null;
		}
		isRandom = false;
	}
	
	public CheckersMove makeRandomMove(CheckersData currentBoard) {
		isRandom = true;
		CheckersMove[] currentMoves = currentBoard.getLegalMoves(player);
		int move = new Random().nextInt(currentMoves.length);
		return currentMoves[move];
	}
	
	// Returns the AI's move for the turn.
	public CheckersMove makeMove(CheckersData currentBoard) {

		CheckersMove[] currentMoves = currentBoard.getLegalMoves(player);
		if(currentMoves == null)
			return null;
		
		CheckersMove finalMove = null;
		double alpha = MINUS_INFINITY;
		double beta = PLUS_INFINITY;
		int len = currentMoves.length;
		//int[][] boardCopy = currentBoard.getBoardCopy();
		CheckersData copy = new CheckersData();
		int powMoveTracker = 0;
		
		for(int i = 0; i < len; i++) {
			// try initial moves on the board, see what will be best.
			boolean badMove = false;
			copy.setUpGame(currentBoard);
			CheckersMove moveToMake = null;
			if(currentMoves[i].isPower() && !currentMoves[i].isJump()) {
				
				PowerUp p = null;
				int row = currentMoves[i].fromRow;
				int col = currentMoves[i].fromCol;
				switch(player) {
				case CheckersData.RED:
					p = copy.powerUpSys.red_powers.get(new Point(col, row));
					break;
				case CheckersData.BLACK:
					p = copy.powerUpSys.blk_powers.get(new Point(col, row));
					break;
				}
				if(p != null) {
					CheckersMove[] powMoves = p.moves(copy.getBoardCopy(), new Point(col, row));
					if(powMoveTracker <  powMoves.length) {
						--i;
						moveToMake = powMoves[powMoveTracker];
						powMoveTracker++;
					}
					else { i++; powMoveTracker = 0; }
				}
			}
			if(moveToMake == null)
				moveToMake = currentMoves[i];
			copy.makeMove(moveToMake);
			String key = copy.hash();
			// check to see if the AI has seen this move before
			if(weightedMoves.containsKey(key)) {
				int weight = weightedMoves.get(key);
				// if good move or draw move, just make that move.
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
					if(moveToMake.isJump())
						temp = moveHelper(copy, alpha, beta, true, 0);
					else
						temp = moveHelper(copy, alpha, beta, false, 0);

				if(alpha < temp) {
					alpha = temp;
					finalMove = moveToMake;
				}
			}
		} // end loop

		if(alpha == MINUS_INFINITY) {
			// only found losing states.
			weightedMoves.put(lastBoard.hash(), BAD_MOVE);
			return null;
		}

//		if(alpha < 0.0) { // only found negative board states
//			weightedMoves.put(lastBoard.hash(), BAD_MOVE);
//		}

		// remember the last board played on and the last move made
		lastBoard = new CheckersData();
		lastBoard.setUpGame(currentBoard);
		lastMove = finalMove;
//		weightedMoves.put(lastBoard.hash(), GOOD_MOVE);
		return lastMove;

	} // end makeMove()

	
	// recursive move maker. Alpha-Beta pruning happens in here.
	private double moveHelper(CheckersData board, double alpha, double beta, boolean MAX, int level) {
		level++;
		CheckersMove[] currentMoves;
		if(MAX)
			currentMoves = board.getLegalMoves(player);
		else 
			currentMoves = board.getLegalMoves(opponent);
		
		// check to see if we're in a losing board.
		if(currentMoves == null) {
			if(MAX) {
				return MINUS_INFINITY;
			}
			else {
				return PLUS_INFINITY;
			}
		}
		
		if(level > LEVEL_LIMIT) {
			CheckersData copy = new CheckersData();
			copy.setUpGame(board);
			return evaluateBoard(copy, MAX);
		}


		//int[][] boardCopy = board.getBoardCopy();
		CheckersData copy = new CheckersData();
		int powMoveTracker = 0;
		for(int i = 0; i < currentMoves.length; i++) {
			if(alpha < beta) {
				CheckersMove moveToMake = null;
				copy.setUpGame(board);
				/*if(currentMoves[i].isPower() && !currentMoves[i].isJump()) {
					
					PowerUp p = null;
					int row = currentMoves[i].fromRow;
					int col = currentMoves[i].fromCol;
					switch(player) {
					case CheckersData.RED:
						p = CheckersData.powerUpSys.red_powers.get(new Point(col,row));
						break;
					case CheckersData.BLACK:
						p = CheckersData.powerUpSys.blk_powers.get(new Point(col, row));
						break;
					}
					if(p != null) {
						CheckersMove[] powMoves = p.moves(boardCopy, new Point(col, row));
						if(powMoveTracker <  powMoves.length) {
							--i;
							moveToMake = powMoves[powMoveTracker];
							powMoveTracker++;
						}
						else { i++; powMoveTracker = 0; }
					}
				}*/
				if(moveToMake == null) {
					moveToMake = new CheckersMove(currentMoves[i].fromRow, currentMoves[i].fromCol,
							currentMoves[i].toRow, currentMoves[i].toCol, false);
				}
				copy.makeMove(moveToMake);

				double temp = 0.0;
				if(MAX) {
					String key = copy.hash();
					boolean badMove = false;
					// check to see if the AI has seen this move before
					if(weightedMoves.containsKey(key)) {
						int weight = weightedMoves.get(key);
						// if good move or draw move, just make that move.
						switch(weight) {
						case GOOD_MOVE:
							temp = PLUS_INFINITY;
							break;
						case BAD_MOVE:
							temp = MINUS_INFINITY;
							badMove = true;
							break;
						case DRAW_MOVE:
							temp = PLUS_INFINITY;
							break;
						}
					}
					if(!badMove) {
						if(temp == PLUS_INFINITY)
							alpha = temp;
						else {
							if(currentMoves[i].isJump())
								temp = moveHelper(copy, alpha, beta, MAX, (level-1));
							else
								temp = moveHelper(copy, alpha, beta, !MAX, level);
							if(alpha < temp)
								alpha = temp;
						}
					}
				}
				else { // not MAX's turn
					if(moveToMake.isJump())
						temp = moveHelper(copy, alpha, beta, MAX, (level-1));
					else
						temp = moveHelper(copy, alpha, beta, !MAX, level);
					if(beta > temp)
						beta = temp;
				}
			}
		}
		if(MAX)
			return alpha;
		else return beta;
	}

	// heuristic evaluation of a board. Calls on several heuristic functions to make an evaluation,
	// then weighs them with the weight vector.
	private double evaluateBoard(CheckersData board, boolean MAX) {
		int p;
		if(MAX)
			p = player;
		else
			p = opponent;
		double eval = 0.0;
		double protect = ProtectedPiecesHeuristic.calc(board, p, MAX);
		double distance = DistanceHeuristic.calc(board, p, MAX); // gets average distance
		double move = MoveDifferenceHeuristic.calc(board, p, MAX);
		double piece = PieceDifferenceHeuristic.calc(board, p, MAX);
		double numPow = PowerUpNumHeuristic.calc(board, p, MAX);
		
		if(distance != 0.0)
			distance = 1/distance;
		if(!isCombat) {
			if(CheckersCanvas.turnNumber < late_game)
				eval = WEIGHT_VECTOR[0]*move + WEIGHT_VECTOR[1]*piece + WEIGHT_VECTOR[2]*distance + WEIGHT_VECTOR[3]*protect;
			else
				eval = WEIGHT_VECTOR_2[0]*move + WEIGHT_VECTOR_2[1]*piece + WEIGHT_VECTOR_2[2]*distance + WEIGHT_VECTOR_2[3]*protect;
		}
		else {
			if(CheckersCanvas.turnNumber < late_game)
				eval = WEIGHT_VECTOR[0]*move + WEIGHT_VECTOR[1]*piece + WEIGHT_VECTOR[2]*distance
						+ WEIGHT_VECTOR[3]*protect + WEIGHT_VECTOR[4]*numPow;
			else
				eval = WEIGHT_VECTOR_2[0]*move + WEIGHT_VECTOR_2[1]*piece + WEIGHT_VECTOR_2[2]*distance
						+ WEIGHT_VECTOR_2[3]*protect + WEIGHT_VECTOR_2[4]*numPow;
		}
		

		return eval;
	}

}
