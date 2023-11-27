// will have a lot of 0 in it as the nodes can only connect with the 4 adg nodes
// might be able to save on memory somehow.

public class board_bot implements Cloneable {
	private boolean board[][];
	// number of nodes per row
	public int numb_nodes;
	private int bot_score;
	private int enemy_score;
	public int[] move = new int[3];
	public static final int bot = 1, enemy = 2;
	public static final int hor = 0, vert = 1;
	
 	public board_bot(int numb_boxes) {
		// might be more better to say square **2
		this.numb_nodes = numb_boxes + 1;
		board = new boolean[this.numb_nodes * this.numb_nodes][this.numb_nodes * this.numb_nodes];
		this.bot_score = 0;
		this.enemy_score = 0;
	}
	
	/*
	 * returns true if all boxes have been created and false otherwise
	 */
	public boolean is_game_over() {
		// checks if the total score is equal to the total number of boxes.
		return this.bot_score + this.enemy_score == (this.numb_nodes - 1) * (this.numb_nodes - 1);
	}
	
	public int bot_score() {
		return bot_score;
	}
	
	//stole from https://www.programiz.com/dsa/graph-adjacency-matrix
	// must be a valid line given
	public void addLine(int i, int j) {
		// add move to our board
		board[i][j] = true;
		board[j][i] = true;

		// convert move to server format
		if (i > j) {
			int temp = i;
			i = j;
			j = temp;
		}
		if (i + 1 == j) {
			this.move[0] = 0;
		} else {
			this.move[0] = 1;
		}
		// row
		this.move[1] = i / this.numb_nodes;
		// col
		this.move[2] = i % this.numb_nodes;
	}
	
	public void addOppMove(int HorV, int row, int col) {
		int nodeone = this.numb_nodes * row + col;
		int nodetwo;
		
		// if it is a horizontal node
		if (HorV == 0) {
			nodetwo = nodeone + 1;
		} else {
			// it is a vertical line
			nodetwo = nodeone + this.numb_nodes;
		}
		this.addLine(nodeone, nodetwo);
	}
	
	// returns false if not turn again true if turn again
	public boolean checkScore(int i, int j, int player) {
		int foundBox = this.checkBox(i, j);
		if (foundBox > 0) {
			if (player == board_bot.bot) {
				this.bot_score += foundBox;
			} else {
				this.enemy_score += foundBox;
			}
			return true;
		}
		return false;
	}
	
	//assumes that there is a line between i and j
	private int checkBox(int i, int j) {
		int foundBox = 0;
		//horizontal line
		if (i == j - 1 || i == j + 1) {
			int leftNode;
			int rightNode;
			// makes it for we are always dealing with the bottom left corner
			if ( i > j) {
				leftNode = j;
				rightNode = i;
			} else {
				leftNode = i;
				rightNode = j;
			}
			
			//top box
			//if not in top row
			if (leftNode >= this.numb_nodes) {
				if (this.board[leftNode][leftNode - this.numb_nodes] && this.board[rightNode][rightNode - this.numb_nodes] && this.board[leftNode - this.numb_nodes][rightNode - this.numb_nodes]) {
					foundBox += 1;
				}
			}
			//if not in bottom row
			if (leftNode < this.numb_nodes * (this.numb_nodes - 1)) {
				if (this.board[leftNode][leftNode + this.numb_nodes] && this.board[rightNode][rightNode + this.numb_nodes] && this.board[leftNode + this.numb_nodes][rightNode + this.numb_nodes]) {
					foundBox += 1;
				}
			}
			
		} else {
			//vertical line
			int botNode;
			int topNode;
			// get bottom node
			if (i > j) {
				botNode = i;
				topNode = j;
			} else {
				botNode = j;
				topNode = i;
			}
			//left box
			//if not in left column
			if (botNode % this.numb_nodes != 0) {
				if (this.board[botNode][botNode - 1] && this.board[topNode][topNode - 1] && this.board[botNode - 1][topNode - 1]) {
					foundBox += 1;
				}
			}
			//if not in right column
			if ((botNode + 1) % this.numb_nodes != 0) {
				if (this.board[botNode][botNode + 1] && this.board[topNode][topNode + 1] && this.board[botNode + 1][topNode + 1]) {
					foundBox += 1;
				}
			}
		}
		return foundBox;
	}
	
	public boolean checkScoreAndLine(int i, int j, int player) {
		this.addLine(i, j);
		return this.checkScore(i, j, player);
	}
	
	// stole from https://www.programiz.com/dsa/graph-adjacency-matrix
	public String print() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < (numb_nodes * numb_nodes); i++) {
			s.append(i + ": ");
			for (boolean j : board[i]) {
				s.append((j ? 1 : 0) + " " );
			}
			s.append("\n");
		}
		return s.toString();
	}
	
	//checks if all lines have been placed
	public boolean gameOver() {

		if (this.bot_score + this.enemy_score == (this.numb_nodes - 1) * (this.numb_nodes - 1)) {
			return true;
		}
		return false;
	}
	
	public boolean checkIfLine(int i, int j) {
		return !this.board[i][j];
	}
	
	public board_bot clone()
    { 
		board_bot new_board = new board_bot(this.numb_nodes - 1);
		new_board.bot_score = this.bot_score;
		new_board.enemy_score = this.enemy_score;
		for(int i = 0; i < numb_nodes * numb_nodes; i++) {
			for(int j = 0; j< numb_nodes * numb_nodes; j++) {
				new_board.board[j][i] = this.board[j][i];
			}
		}
		return new_board;
    } 

	/*
	 * Will return an array with copies of the current board each with one additional move added
	 */
	public board_bot[] possmoves() {
		// makes an array to hold boards which have the next move added to them
		board_bot moves[];
		moves = new board_bot[2 * (this.numb_nodes * this.numb_nodes) - 2 * this.numb_nodes];

		// used to reference the moves array
		int numb_moves = 0;

		// looks at every node
		for (int i = 0; i < this.numb_nodes * this.numb_nodes - 1; i++) {
			// looks to the right
			// prevents looking at far right column
			if ((i+1) % (this.numb_nodes) != 0 && !this.board[i][i+1]) {
				//make new board 
				board_bot new_board = this.clone();
				new_board.addLine(i, i + 1);
				moves[numb_moves] = new_board;
				numb_moves++;
			}

			// looks down
			// prevents looking with the last row
			if (i < this.numb_nodes * (this.numb_nodes - 1) && !this.board[i][i+this.numb_nodes]) {
				// makes new board ad adds the move
				board_bot new_board = (board_bot)this.clone();
				new_board.addLine(i, i + this.numb_nodes);
				moves[numb_moves] = new_board;
				numb_moves++;
			}
		}
		return moves;
	}

	/*
	 * Will return an array with all possible moves
	 */
	public int[][] possmoves_mcts() {
		// makes an array to hold all moves
		int[][] moves;
		moves = new int[2 * (this.numb_nodes * this.numb_nodes) - 2 * this.numb_nodes][2];

		// used to reference the moves array
		int numb_moves = 0;

		// looks at every node
		for (int i = 0; i < this.numb_nodes * this.numb_nodes - 1; i++) {
			// looks to the right
			// prevents looking at far right column
			if ((i+1) % (this.numb_nodes) != 0 && !this.board[i][i+1]) {
				//make new board 
				moves[numb_moves][0] = i;
				moves[numb_moves][1] = i + 1;
				numb_moves++;
			}

			// looks down
			// prevents looking with the last row
			if (i < this.numb_nodes * (this.numb_nodes - 1) && !this.board[i][i+this.numb_nodes]) {
				// makes new board ad adds the move
				moves[numb_moves][0] = i;
				moves[numb_moves][1] = i + this.numb_nodes;
				numb_moves++;
			}
		}
		return moves;
	}
	
	/*
	 * will return if it is a valid move
	 * checks if the line is already created and if i and j are next to each other
	 */
	public boolean is_valid_move(int i, int j){
		// if there is already a line at location return false
		if (board[i][j]){
			return false;
		}

		// if i and j are not next to each other return false
		if (i != j - 1 || i != j + 1 || i != j - this.numb_nodes || i != j + this.numb_nodes){
			return false;
		}
		return true;
	}

	/*
	 * will return true if we won and false otherwise
	 */
	public boolean check_win(){
		return this.bot_score > this.enemy_score;
	}
	public static void main(String[] args) throws Exception {
		// for testing
		board_bot board = new board_bot(4);
		
		board.addLine(1, 0);
		System.out.print(board.print());
		board.addLine(0, 5);
		board.addLine(5, 6);
		board.addLine(1, 6);
		System.out.print(board.print());
		boolean turn_again = board.checkScore(1, 6, board_bot.bot);
		System.out.print(turn_again);
		turn_again = board.checkScore(0, 1, board_bot.bot);
		System.out.print(turn_again);
		turn_again = board.checkScore(1, 0, board_bot.bot);
		System.out.print(turn_again);
		turn_again = board.checkScore(0, 5, board_bot.bot);
		System.out.print(turn_again);
		turn_again = board.checkScore(5, 0, board_bot.bot);
		System.out.print(turn_again);
		turn_again = board.checkScore(6, 1, board_bot.bot);
		System.out.print(turn_again);
		turn_again = board.checkScore(5, 6, board_bot.bot);
		System.out.print(turn_again);
		
		board.addLine(18, 19);
		board.addLine(23, 24);
		board.addLine(18, 23);
		board.addLine(19, 24);
		System.out.print("\n");
		turn_again = board.checkScore(18, 19, board_bot.bot);
		System.out.print(turn_again);
		turn_again = board.checkScore(19, 18, board_bot.bot);
		System.out.print(turn_again);
		turn_again = board.checkScore(18, 23, board_bot.bot);
		System.out.print(turn_again);
		turn_again = board.checkScore(19, 24, board_bot.bot);
		System.out.print(turn_again);
		turn_again = board.checkScore(23, 24, board_bot.bot);
		System.out.print(turn_again);
		turn_again = board.checkScore(24, 23, board_bot.bot);
		System.out.print(turn_again);
		System.out.print(board.print());
		
		System.out.print("-------------------------------------------------\n");
		
		board_bot[] moves = board.possmoves();
		System.out.print(moves[0].print());
		System.out.print(moves[1].print());
	}
}
