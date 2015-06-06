package test;

import java.awt.Point;

// Abstract base weapon class
public abstract class Weapon extends PowerUp {

	Weapon(int prob) {
		super(prob);
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
	public CheckersData execute(CheckersData board, Point user, Point trajectory)
	{
		Point p = user;
		int pCode;
		
		p.x += trajectory.x;
		p.y += trajectory.y;
		
		while(p.x < board.WIDTH && p.x >= 0 && p.y < board.HEIGHT && p.y >= 0) {
			pCode = board.pieceAt(p.y, p.x);
			
			if (pCode % 10 != 0) {
				board.removePieceAt(p);
				break;
			}
			else{
				p.x += trajectory.x;
				p.y += trajectory.y;
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

	// Execute Laser
	public CheckersData execute(CheckersData board, Point user)
	{
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

	// Execute Laser
	public CheckersData execute(CheckersData board, Point user)
	{
		return board;
	}
}