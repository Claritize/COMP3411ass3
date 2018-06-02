import java.util.*;

//Used for pathfinding algorithms
class State {

    public boolean raft;
    public int stones;
    public int axe;
    public int keys;

    //Agent states
    public boolean on_water;
    public boolean on_raft;
    public boolean on_rock;
    
    public boolean found_treasure;

    int waters = 0;

    ArrayList<Character> moves = new ArrayList<Character>();
    
    //Current co-ordinates
    int x;
    int y;

    int cost;

    public State(int x, int y, int cost) {
        this.x = x;
        this.y = y;
        this.cost = cost;
    }

}