
public class connect_bot {
	final static String ServerIP1 = "localhost";
	final static String ServerIP2 = "x.x.x.x";
	
	final static int myAIID = 1700;
	final static int myTable = 17;
	final static int myPW = 1751;
	final static int size = 4; //Play on a 4 boxes per 4 boxes
	final static int opp = -4;
	
	public static void main(String[] args) throws Exception {
		
		GameSocket gs = new GameSocket();
		
		int res = gs.connect(ServerIP1, myAIID, myTable, myPW, opp, size);
		
		if (res < 0) {
			System.out.println(gs.connMsg);
			return;
		}
		
		while(!gs.isMyTurn());
		
		//start game:
		int[] msg = gs.readMessage();
		while (!((msg[0] == GameSocket.CLOSING) || (msg[0] == GameSocket.GAME_OVER))) {
			
		}
	}
}
