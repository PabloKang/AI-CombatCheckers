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
		type = "laser";
	}

	// Execute Laser
	public CheckersData execute(CheckersData board, Point user)
	{
		return board;
	}
}


// WEAPON - Bomb
class Bomb extends Weapon 
{

	Bomb(int prob) 
	{
		super(prob);
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
		type = "air strike";
	}

	// Execute Laser
	public CheckersData execute(CheckersData board, Point user)
	{
		return board;
	}
}