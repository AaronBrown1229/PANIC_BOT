import java.util.Random;

public class PANIC_BOT_MCTS{
	final static String ServerIP1 = "localhost";
	
	final static int myAIID = 1600;
	final static int myTable = 17;
	final static int myPW = 1751;
	final static int size = 4; //Play on a 4 boxes per 4 boxes
	final static int opp = 0;
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
                    monte_carlo_bot root_node = new monte_carlo_bot(null, null, true);
                    // might need to check if whole remaining tree is searched
					// TODO make the time good
                    while (System.currentTimeMillis() - start_time < 3000){
                        monte_carlo_bot node = root_node;
                        //,https://webdocs.cs.ualberta.ca/~hayward/396/jem/mcts.html
                        // make a copy of board called sim
                        board_bot sim = board.clone();
                        // this gets us to the node which will be simulated
                        while (!node.is_leaf()){
                            // make find best which uses UCB on all chlidren
							if(!node.max){
                            	node = findBestUCB(node);
							} else{
								node = findWorstUCB(node);
							}
                            // add move of best node to sim
                            sim.addLine(node.move[0], node.move[1]);
                        }
                        node.make_children(sim);
						                  
						int winInt = 0;
						int visits = 0;
                        // do random path on all poss moves of sim
                        for(int i = 0; i < 40 && node.children[i] != null; i++){
							int max;
							// changes node max from a boolean to a int
							if(node.children[i].max){
								max = 1;
							} else{
								max = 2;
							}
							// makes it do 5 random paths
							for(int j = 0; j < 10; j++){
								// walk random path and return if win or loss
								int win = PANIC_BOT_MCTS.start_play_random(sim, max);
								// updates node and child
								node.children[i].update(win);
								node.update(win);
								winInt += win;
								visits++;
							}
                        }

                        // propogate from node up to root
						node = node.parent;
                        while (node != null){
							node.propogate_update(visits, winInt);
							node = node.parent;
						}
						// updates root
						// node.propogate_update(visits, winInt);
                    }
					
					// find best move to play
					// TODO could check the top 5 runs and compute their value
					int num_children = root_node.children.length;
					int bestIndex = 0;
					for(int i = 1; i < num_children; i++){
						if(root_node.children[bestIndex].wins < root_node.children[i].wins){
							bestIndex = i;
						}
					}
					// update board
					board.checkScoreAndLine(root_node.children[bestIndex].move[0], root_node.children[bestIndex].move[1], 1);
					// send move
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
        for(int i = 0; i < 40 && node.children[i] != null; i++){
            if (node.children[i].UCB() > node.children[bestIndex].UCB()){
                bestIndex = i;
            }
        }
        return node.children[bestIndex];
    }

	public static monte_carlo_bot findWorstUCB(monte_carlo_bot node){
		int worstIndex = 0;
		for(int i = 0; i < 40 && node.children[i] != null; i++){
			if(node.children[i].UCB() < node.children[worstIndex].UCB()){
				worstIndex = i;
			}
		}
		return node.children[worstIndex];
	}

	/*
	 * will recursivly play random moves on the board until the game is over
	 * will return if it won or lost
	 */
	public static int play_random(board_bot board, int max, int[][] possmoves, int[] index, int i){
		if (i < 40 && !board.gameOver()){
			// adds the next random line from possmoves
			boolean next_player = board.checkScoreAndLine(possmoves[index[i]][0], possmoves[index[i]][1], max);

			// update who's turn it is if a box wasn't formed
			if (!next_player){
				if (max == 1){
					max = 2;
				} else{
					max = 1;
				}
			}

			// play next move
			return play_random(board, max, possmoves, index, i + 1);
		} else{
			// at game over
			if(board.check_win()){
				return 1;
			}
			return 0;
		}
	}

	public static int start_play_random(board_bot board, int max){
		// TODO clone board
        board_bot randoboard = board.clone();
		int[][] possmoves = randoboard.possmoves_mcts();
		int len = possmoves.length;

		// an array that holds what element to of possmoves to play next
		int[] index = new int[len];
		// make an array filled with sequential numbers
		for(int i = 0; i < len; i++){
			index[i] = i;
		}

		Random rand = new Random();

		// will shuffle index to be random
		for(int i = len - 1; i >= 0; i--){
			int j = rand.nextInt(len);
			int temp = index[i];
			index[i] = index[j];
			index[j] = temp;
		}

		return play_random(randoboard, max, possmoves, index, 0);
	}
}
