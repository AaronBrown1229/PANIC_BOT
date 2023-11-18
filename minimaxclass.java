public class minimaxclass implements Runnable{
	
	// This is higher than bestVal can ever be so is adequate for posInfinity
	static int posInfinity = 17;
	int[] bestVals = null;
	private board_bot start_board;
	private int start_depth;
	private int index;
	
	public minimaxclass(board_bot board, int depth, int index) {
		this.start_board = board;
		this.start_depth = depth;
		this.index = index;
	}
	
	/*
	 * modeled after https://www.geeksforgeeks.org/minimax-algorithm-in-game-theory-set-4-alpha-beta-pruning/
	 * This will be used for all future states past the next move ie more than 2 moves in the future
	 * only used by the run method
	 */
	private int minimax(board_bot board, int depth, boolean max, int alpha, int beta) {
		// when it is done searching a value of how good the move was is returned
		if (depth == 0 || board.is_game_over()){
			return heuristic(board);
		}
		int bestVal;
		// if trying to maximize the value
		if (max) {
			// essentially -Infinity
			bestVal = -1;
			board_bot[] moves = board.possmoves();
			int counter = 0;
			int value;
			// for each generated move and run minimax again
			while(moves[counter] != null) {
				value = this.minimax(moves[counter], depth - 1, false, alpha, beta);
				bestVal = Math.max(value, bestVal);
				alpha = Math.max(alpha, bestVal);
				if (beta <= alpha) {
					break;
				}
			}
			return bestVal;
			
		} else {
			bestVal = posInfinity;
			board_bot[] moves = board.possmoves();
			int counter = 0;
			int value;
			while(moves[counter] != null) {
				value = this.minimax(moves[counter], depth - 1, true, alpha, beta);
				bestVal = Math.min(bestVal, value);
				beta = Math.min(beta, bestVal);
				if (beta <= alpha) {
					break;
				}
			}
		}
		return bestVal;
	}
	
	/*
	 * This method is used to run the threads created by next_minimax
	 */
	public void run() {
		// alpha is set to -1 as that is essentially -infinity
		// beta is set to posInfinity as that is essentially +infinity
		int bestVal = this.minimax(this.start_board, this.start_depth, true, -1, posInfinity);
		this.bestVals[this.index] = bestVal;
	}
	
	/*
	 * This method is what should be called to start minimax
	 * It will generate all the next possible moves then make a new thread for each possible move running 
	 * minimax in each next move
	 * 
	 * Input is 
	 * current board 
	 * How deep of a tree to search through
	 * 
	 * output
	 * The board with the best move
	 * TODO might have to be the move to make as we need to send it to the server
	 */
	public board_bot next_minimax(board_bot board, int depth) {
		// don't need the base case as if the game was over then this won't be run
		// get all the possible moves
		board_bot[] moves = board.possmoves();
		int counter = 0;
		// TODO Not sure if this is cool or not
		Thread[] threads = null;
		
		// runs minimax for all subsequent moves of this tree in a new thread
		while(moves[counter] != null) {
			//make thread for each possible board state
			minimaxclass mini = new minimaxclass(moves[counter], depth, counter);
			threads[counter] = new Thread(mini);
			threads[counter].start();
			counter++;
			// get array of all the bestVal's from each of the minimax
		}
		
		// rejoins all the threads
		counter = 0;
		while(moves[counter] != null) {
			try {
				threads[counter].join();
			} catch (Exception e) {
				System.out.println(counter + " was unable to join");
			}
			counter++;
		}
		
		// Determines the best move to make based on the result of each of the threads
		int bestIndex = this.bestVals[0];
		for (int i = 1; i < this.bestVals.length; i++) {
			if (this.bestVals[i] > this.bestVals[bestIndex]) {
				bestIndex = i;
			}
		}
		
		return moves[bestIndex];
	}	
	
	private int heuristic(board_bot board) {
		return 10;
	}
	
}