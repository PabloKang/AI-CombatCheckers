package test;

public class ProtectedPiecesHeuristic {
	public static int calc (CheckersData state, int player, boolean MAX) {
		int score = 0;
		int[][] board = state.getBoardCopy();
		int king;
		if(player == CheckersData.RED)
			king = CheckersData.RED_KING;
		else
			king = CheckersData.BLACK_KING;
		for (int x = 0; x < 8; ++x) {
			for (int y = 0; y < 8; ++y) {
				if (board[x][y] == player) {
					if (x == 0) {
						score += 1;
					} else if (x == 7) {
						score += 1;
					} else if (y == 0) {
						score += 1;
					} else if (y == 7) {
						score += 1;
					} else if (board[x - 1][y - 1] == player) {
						score += 1;
					} else if (board[x - 1][y + 1] == player) {
						score += 1;
					} else if (board[x + 1][y - 1] == player) {
						score += 1;
					} else if (board[x + 1][y + 1] == player) {
						score += 1;
					}
				}
				else if(board[x][y] == king) {
					if (x == 0) {
						score += 2;
					} else if (x == 7) {
						score += 2;
					} else if (y == 0) {
						score += 2;
					} else if (y == 7) {
						score += 2;
					} else if (board[x - 1][y - 1] == player) {
						score += 2;
					} else if (board[x - 1][y + 1] == player) {
						score += 2;
					} else if (board[x + 1][y - 1] == player) {
						score += 2;
					} else if (board[x + 1][y + 1] == player) {
						score += 2;
					}
					
				}
			}
		}

		if (!MAX) {
			score *= -1;
		}

		return score;
	}
}
