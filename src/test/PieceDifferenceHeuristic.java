package test;

public class PieceDifferenceHeuristic {
	public static double calc(CheckersData state, int player, boolean MAX) {
		int score = 0;
		int black = state.numBlackMen() + 2*state.numBlackKings();
		int red = state.numRedMen() + 2*state.numRedKings();

		if(player == CheckersData.RED)
			score = red - black;
		else
			score = black - red;
		
		if(!MAX)
			score *= -1;
		
		return score;
	}
}
