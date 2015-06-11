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
		Point[] paths = new Point[4];
		paths[0] = new Point(-1,-1);
		paths[1] = new Point(1,-1);
		paths[2] = new Point(-1,1);
		paths[3] = new Point(1,1);
		
<<<<<<< HEAD
		while(p.x < board.WIDTH && p.x >= 0 && p.y < board.HEIGHT && p.y >= 0) {
			pCode = board.pieceAt(p.y, p.x);
=======
		for(int i = 0; i < 4; i++) {
			Point path = paths[i];
			Point p = user;
			int pCode;
>>>>>>> 1a04a6b374235e02e452fc2418c7c21565ce7c00
			
			while(p.x < 8 && p.x >= 0 && p.y < 8 && p.y >= 0) {
				pCode = board[p.y][p.x];
				
				if (pCode % 10 != 0) {
					laserMoveList.add(new CheckersMove(user.x,user.y,p.x,p.y,true));
					break;
				}
				else{
					p.x += path.x;
					p.y += path.y;
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

<<<<<<< HEAD
	// Execute Laser
	public CheckersData execute(CheckersData board, Point user, Point target)
	{
=======
	// Find and return all possible moves for this bomb
	@Override
	public CheckersMove[] moves(int[][] board, Point user) 
	{
		CheckersMove[] bombMoves = new CheckersMove[1];
		bombMoves[0] = new CheckersMove(user.x,user.y,user.x,user.y,true);
		
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
		if(p.x > 0 && p.y > 0) { 						// Top left
			board[p.x-1][p.y-1] = CheckersData.EMPTY;
		}
		if(p.x < 8 && p.y > 0) { 				// Top right
			board[p.x+1][p.y-1] = CheckersData.EMPTY;
		}
		if(p.x > 0 && p.y < 8) { 			// Bottom left
			board[p.x-1][p.y+1] = CheckersData.EMPTY;
		}
		if(p.x < 8 && p.y < 8) { 	// Bottom right
			board[p.x+1][p.y+1] = CheckersData.EMPTY;
		}
		
>>>>>>> 1a04a6b374235e02e452fc2418c7c21565ce7c00
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

<<<<<<< HEAD
	// Execute Laser
	public CheckersData execute(CheckersData board, Point user, Point target)
	{
		return board;
=======
	// Find and return all possible moves for this air strike
	@Override
	public CheckersMove[] moves(int[][] board, Point user) 
	{
		ArrayList<CheckersMove> aStrikeMoveList = new ArrayList<CheckersMove>();
		int pCode = board[user.y][user.x];
		
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				int tCode = CheckersData.parsePiece(board[row][col]);
				
				if (row % 2 == col % 2 && tCode != CheckersData.EMPTY) {
					if((pCode == CheckersData.BLACK || pCode == CheckersData.BLACK_KING) && (tCode == CheckersData.RED || tCode == CheckersData.RED_KING))
						aStrikeMoveList.add(new CheckersMove(user.y, user.x, row, col, true));
					if((pCode == CheckersData.RED || pCode == CheckersData.RED_KING) && (tCode == CheckersData.BLACK || tCode == CheckersData.BLACK_KING))
						aStrikeMoveList.add(new CheckersMove(user.y, user.x, row, col, true));
				}
			}
		}

		CheckersMove[] aStrikeMoves = new CheckersMove[aStrikeMoveList.size()];
		aStrikeMoves = aStrikeMoveList.toArray(aStrikeMoves);
		
		return aStrikeMoves;
>>>>>>> 1a04a6b374235e02e452fc2418c7c21565ce7c00
	}
}