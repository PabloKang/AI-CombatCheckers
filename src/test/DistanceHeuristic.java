package test;

public class DistanceHeuristic {
	public static int calc (CheckersData state, int player, boolean MAX) {
		int score = 0;
		int[][] board = state.getBoardCopy();

		if (player == CheckersData.RED) {
			for (int x = 0; x < 8; ++x) {
				for (int y = 0; y < 8; ++y) {
					if (board[x][y] == player) {
						score += y;
					}
				}
			}
		} else {
			for (int x = 0; x < 8; ++x) {
				for (int y = 7; y >= 0; --y) {
					if (board[x][y] == player) {
						score += 8 - y;
					}
				}
			}
		}

		return score;
	}
}
