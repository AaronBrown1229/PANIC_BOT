// will have a lot of 0 in it as the nodes can only connect with the 4 adg nodes
// might be able to save on memory somehow.
public class board_bot {
	private boolean board[][];
	// number of nodes per row
	private int numb_nodes;
	private int bot_score;
	private int enemy_score;
	public static final int bot = 1, enemy = 2;
	
	public board_bot(int numb_boxes) {
		// might be more better to say square **2
		this.numb_nodes = numb_boxes + 1;
		board = new boolean[this.numb_nodes * this.numb_nodes][this.numb_nodes * this.numb_nodes];
		this.bot_score = 0;
		this.enemy_score = 0;
	}
	
	//stole from https://www.programiz.com/dsa/graph-adjacency-matrix
	// must be a valid line given
	public void addLine(int i, int j) {
		board[i][j] = true;
		board[j][i] = true;
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
	}
}
