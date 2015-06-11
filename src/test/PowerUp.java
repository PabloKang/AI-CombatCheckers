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

	// Return possible targets for powerUp
	public abstract CheckersMove[] moves(int[][] board, Point user);
	
	// Main execution function
	public abstract int[][] execute(int[][] board, Point target);
	

}
