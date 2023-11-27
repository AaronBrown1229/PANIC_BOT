import java.util.Random;

public class PANIC_BOT_MCTS{
	final static String ServerIP1 = "localhost";
	
	final static int myAIID = 1700;
	final static int myTable = 17;
	final static int myPW = 1751;
	final static int size = 4; //Play on a 4 boxes per 4 boxes
	final static int opp = -2;
	final static int depth = 5;
	
	public static void main(String[] args) throws Exception {
		GameSocket gs = new GameSocket();
		board_bot board = new board_bot(4);
		
		int res = gs.connect(ServerIP1, myAIID, myTable, myPW, opp, size);
		
		// if error
		if (res < 0) {
			System.out.println(gs.connMsg);
		}
		
		// reads the fist message
		int[] msg = gs.readMessage();

        long start_time;
		
		while (!((msg[0] == GameSocket.CLOSING))) {

			switch (msg[0]) {
				case GameSocket.PLEASE_PLAY:
                    start_time = System.currentTimeMillis();
                    monte_carlo_bot root_node = new monte_carlo_bot(null, null);
                    // might need to check if whole remaining tree is searched
                    while (System.currentTimeMillis() - start_time < 6000){
                        monte_carlo_bot node = root_node;
                        //,https://webdocs.cs.ualberta.ca/~hayward/396/jem/mcts.html
                        // make a copy of board called sim
                        board_bot sim = board.clone();
                        // this gets us to the node which will be simulated
                        while (! node.is_leaf()){
                            // make find best which uses UCB on all chlidren
                            node = findBestUCB(node);
                            // add move of best node to sim
                            sim.addLine(node.move[0], node.move[1]);
                        }
                        node.make_children(sim);
						                  
						int winInt = 0;
                        // do random path on all poss moves of sim
                        for(int i = 0; node.children[i] != null; i++){
							// walk random path and return if win or loss
							boolean win = PANIC_BOT_MCTS.play_random(sim);
							// updates node and child
							if (win){
								winInt = 1;
							} else {
								winInt = 0;
							}
							node.children[i].update(winInt);
							node.update(winInt);
                        }

                        // propogate from node up to root
						node = node.parent;
                        while (!node.is_root()){
							node.update(winInt);
							node = node.parent;
						}
                    }
					
					// find best move to play
					// update board
					// send move
					minimaxclass minimax = new minimaxclass(board, depth, -1);
					board = minimax.next_minimax(board, depth);
					gs.sendMove(board.move[0], board.move[1], board.move[2]);
					break;
			
				case GameSocket.YOUR_RESULT:
						if (msg[1] == GameSocket.INVALID_MOVE) {
								System.out.println("An Invalid move was sent?????");
						}
						break;
			
				case GameSocket.OPP_RESULT:
					board.addOppMove(msg[1],msg[2],msg[3]);
					break;
			
				case GameSocket.GAME_OVER:
					if (msg[1] > msg[2]) {
						System.out.println("We win");
					} else {
						System.out.println("We either lost or tied but if you are not first you are last :(");
					}
					break;
				//default:
					// TODO run as opp turn
			}
			// reads the next message
			msg = gs.readMessage();
		
		}
	}

    public static monte_carlo_bot findBestUCB(monte_carlo_bot node){
        int bestIndex = 0;
        for(int i = 1; node.children[i] != null; i++){
            if (node.children[i].UCB() > node.children[bestIndex].UCB()){
                bestIndex = i;
            }
        }
        return node.children[bestIndex];
    }

	/*
	 * will recursivly play random moves on the board until the game is over
	 * will return if it won or lost
	 */
	public static boolean play_random(board_bot board){
		if (!board.gameOver()){
			Random rand = new Random();
			// will get a random i and j location
			int i = rand.nextInt(41);
			int j = rand.nextInt(41);

			// while we don't have a valid move get a new move
			while(!board.is_valid_move(i, j)){
				i = rand.nextInt(41);
				j = rand.nextInt(41);
			}
			
			// apply move and play next move
			board.addLine(i, j);
			return play_random(board);
		} else{
			// at game over
			return board.check_win();
		}
	}
}
