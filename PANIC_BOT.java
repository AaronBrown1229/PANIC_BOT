// NOTE if I want to play agains my self need a different uid for each
// UID needs to be in the 1000s i think
public class PANIC_BOT {
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

		while (!((msg[0] == GameSocket.CLOSING))) {

			switch (msg[0]) {
				case GameSocket.PLEASE_PLAY:
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
}
