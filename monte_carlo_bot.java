public class monte_carlo_bot {
    public monte_carlo_bot parent;
    public monte_carlo_bot[] children = new monte_carlo_bot[40];
    private int wins = 0;
    private int visits = 0;
    public int[] move = new int[2];
    // TODO make general
    public monte_carlo_bot(int[] move, monte_carlo_bot parent) {
        this.move = move;
        this.parent = parent;
    }

    public void make_children(board_bot board){
        int[][] possmoves = board.possmoves_mcts();
        //for each move in possmoves 
        for(int i = 0; i < possmoves.length; i++){
            this.children[i] = new monte_carlo_bot(possmoves[i], this);
        }
    }

    public void update(int result){
        this.visits += 1;
        this.wins += result;
    }

    public boolean is_leaf(){
        return this.children[0] == null;
    }

    public boolean is_root(){
        return this.parent == null;
    }

    public double UCB(){
        double x = this.wins / this.visits;
        double c = 0.1f;
        double left = Math.sqrt(Math.log(this.visits));
        return x + (c * left);
    }
}