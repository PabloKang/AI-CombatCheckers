package test;

import java.awt.Point;

public abstract class PowerUp {

	private int probability;
	public int id = 0;
	public String type = "unknown";

	// Constructor
	PowerUp(int prob)
	{
		probability = prob;
	}
	
	// Accessors
	public int getProbability() 
	{
		return probability;
	}

	// Main execution function
	public CheckersData execute(CheckersData board, Point user) 
	{
		return board;
	}
}
