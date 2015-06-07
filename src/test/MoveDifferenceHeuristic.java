package test;

public class MoveDifferenceHeuristic {
	public static double calc(CheckersData state, int player, boolean MAX) {
		
		double score = state.getLegalMoves(player).length;
		
		if(!MAX)
			score *= -1;
		
		return score;
	}
}
