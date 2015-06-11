package test;

import java.awt.Point;
import java.util.ArrayList;

// Abstract base weapon class
public class Weapon extends PowerUp {

	Weapon(int prob) 
	{
		super(prob);
		id = PowerUpSystem.WEAPON;
		type = "weapon";
	}

	@Override
	public CheckersMove[] moves(int[][] board, Point user) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[][] execute(int[][] board, Point target) 
	{
		System.out.println(type + " -> " + target.x + "," + target.y);

		// Kill the target
		board[target.y][target.x] = CheckersData.EMPTY;	
		
		return board;
	}
	
}


// WEAPON - Laser
class Laser extends Weapon 
{
	
	Laser(int prob) 
	{
		super(prob);
		id = PowerUpSystem.LASER;
		type = "laser";
	}
	
	// Find and return all possible moves for this laser
	@Override
	public CheckersMove[] moves(int[][] board, Point user) 
	{
		ArrayList<CheckersMove> laserMoveList = new ArrayList<CheckersMove>();
		int x = user.x;
		int y = user.y;

		for (int i = -1; i <= 1; i += 2) {
			for (int j = -1; j <= 1; j += 2) {
					int pCode;

					int xi = x + i;
					int yj = y + j;

					while (xi < 8 && xi >= 0 && yj < 8 && yj >= 0) {
						pCode = CheckersData.parsePiece(board[yj][xi]);

						if (pCode != CheckersData.EMPTY) {
							laserMoveList.add(new CheckersMove(user.y, user.x, yj, xi, true));
							break;
						} else {
							xi += i;
							yj += j;
						}
					}
			}
		}

		CheckersMove[] laserMoves = new CheckersMove[laserMoveList.size()];
		laserMoves = laserMoveList.toArray(laserMoves);
		
		return laserMoves;
	}
	
}


// WEAPON - Bomb
class Bomb extends Weapon 
{

	Bomb(int prob) 
	{
		super(prob);
		id = PowerUpSystem.BOMB;
		type = "bomb";
	}

	// Find and return all possible moves for this bomb
	@Override
	public CheckersMove[] moves(int[][] board, Point user) 
	{
		CheckersMove[] bombMoves = new CheckersMove[1];
		bombMoves[0] = new CheckersMove(user.y,user.x,user.y,user.x,true);
		
		return bombMoves;
	}
	
	// Execute Bomb
	@Override
	public int[][] execute(int[][] board, Point target)
	{
		Point p = target;
		
		// Kill yourself
		board[p.y][p.x] = CheckersData.EMPTY;
		
		// Kill neighbors
		if(p.y > 0 && p.x > 0) { 						// Top left
			board[p.y-1][p.x-1] = CheckersData.EMPTY;
		}
		if(p.y < 8 && p.x > 0) { 				// Top right
			board[p.y+1][p.x-1] = CheckersData.EMPTY;
		}
		if(p.y > 0 && p.x < 8) { 			// Bottom left
			board[p.y-1][p.x+1] = CheckersData.EMPTY;
		}
		if(p.y < 8 && p.x < 8) { 	// Bottom right
			board[p.y+1][p.x+1] = CheckersData.EMPTY;
		}
		
		return board;
	}
}


// WEAPON - AirStrike
class AirStrike extends Weapon 
{

	AirStrike(int prob) 
	{
		super(prob);
		id = PowerUpSystem.AIR_STRIKE;
		type = "air strike";
	}

	// Find and return all possible moves for this air strike
	@Override
	public CheckersMove[] moves(int[][] board, Point user) 
	{
		ArrayList<CheckersMove> aStrikeMoveList = new ArrayList<CheckersMove>();
		int pCode = CheckersData.parsePiece(board[user.y][user.x]);

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				int tCode = CheckersData.parsePiece(board[row][col]);

				if (row % 2 == col % 2 && tCode != CheckersData.EMPTY) {
					if((pCode == CheckersData.BLACK || pCode == CheckersData.BLACK_KING) && (tCode == CheckersData.RED || tCode == CheckersData.RED_KING)) {
						aStrikeMoveList.add(new CheckersMove(user.y, user.x, row, col, true));
					}

					if((pCode == CheckersData.RED || pCode == CheckersData.RED_KING) && (tCode == CheckersData.BLACK || tCode == CheckersData.BLACK_KING)) {
						aStrikeMoveList.add(new CheckersMove(user.y, user.x, row, col, true));
					}
				}
			}
		}

		CheckersMove[] aStrikeMoves = new CheckersMove[aStrikeMoveList.size()];
		aStrikeMoves = aStrikeMoveList.toArray(aStrikeMoves);

		return aStrikeMoves;
	}
}
