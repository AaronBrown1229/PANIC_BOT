public class monte_carlo_bot {
    public monte_carlo_bot parent;
    public monte_carlo_bot[] children = new monte_carlo_bot[40];
    public int wins = 0;
    public int visits = 0;
    public int[] move = new int[2];
    public boolean max;
    // TODO make general
    public monte_carlo_bot(int[] move, monte_carlo_bot parent, boolean max) {
        this.move = move;
        this.parent = parent;
        this.max = max;
    }

    public void make_children(board_bot board){
        int[][] possmoves = board.possmoves_mcts();
        //for each move in possmoves 
        for(int i = 0; i < possmoves.length; i++){
            this.children[i] = new monte_carlo_bot(possmoves[i], this, !this.max);
        }
    }

    public void update(int result){
        this.visits += 1;
        this.wins += result;
    }

    public void propogate_update(int visits, int wins){
        this.visits += visits;
        this.wins += wins;
    }

    public boolean is_leaf(){
        return this.children[0] == null;
    }

    public boolean is_root(){
        return this.parent == null;
    }

    public double UCB(){
        double x = this.wins / this.visits;
        double c = 1;
        double left = Math.sqrt(Math.log(this.parent.visits)/this.visits);
        return x + (c * left);
    }
}