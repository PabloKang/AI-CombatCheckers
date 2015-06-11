package test;

public class PowerUpNumHeuristic {
	public static int calc(CheckersData state, int player, boolean MAX) {
		int score = 0;
		int[][] b = state.getBoardCopy();
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++)
				if(b[i][j] / 10 == 1 && b[i][j] % 10 != 0)
					score++;
		if(!MAX)
			score *= -1;
		return score;
	}
}
