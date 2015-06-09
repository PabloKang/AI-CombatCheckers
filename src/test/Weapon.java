package test;

import java.awt.Point;

// Abstract base weapon class
public class Weapon extends PowerUp {

	Weapon(int prob) {
		super(prob);
		id = PowerUpSystem.WEAPON;
		type = "weapon";
	}

	@Override
	public CheckersData execute(CheckersData board, Point user, Point target) {
		// TODO Auto-generated method stub
		return null;
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

	// Execute Laser
	public CheckersData execute(CheckersData board, Point user, Point target)
	{
		Point p = user;
		int pCode;
		
		p.x += target.x;
		p.y += target.y;
		
		// Find the first piece along the target path and kill it
		while(p.x < board.WIDTH && p.x >= 0 && p.y < board.HEIGHT && p.y >= 0) {
			pCode = board.pieceAt(p.y, p.x);
			
			if (pCode % 10 != 0) {
				board.removePieceAt(p);
				break;
			}
			else{
				p.x += target.x;
				p.y += target.y;
			}
		}
		return board;
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

	// Execute Bomb
	public CheckersData execute(CheckersData board, Point user, Point target)
	{
		Point p = user;
		int pCode;
		
		// Kill yourself
		board.removePieceAt(p);
		
		// Kill neighbors
		if(p.x > 0 && p.y > 0) { 						// Top left
			board.removePieceAt(p.x-1,p.y-1);
		}
		if(p.x < board.WIDTH && p.y > 0) { 				// Top right
			board.removePieceAt(p.x+1,p.y-1);
		}
		if(p.x > 0 && p.y < board.HEIGHT) { 			// Bottom left
			board.removePieceAt(p.x-1,p.y+1);
		}
		if(p.x < board.WIDTH && p.y < board.HEIGHT) { 	// Bottom right
			board.removePieceAt(p.x+1,p.y+1);
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

	// Execute Air Strike
	public CheckersData execute(CheckersData board, Point user, Point target)
	{
		// Kill the target
		board.removePieceAt(target);
		
		return board;
	}
}