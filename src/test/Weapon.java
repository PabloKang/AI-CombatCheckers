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

	// Execute Laser
	public CheckersData execute(CheckersData board, Point user, Point target)
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
	public CheckersData execute(CheckersData board, Point user, Point target)
	{
		return board;
	}
}