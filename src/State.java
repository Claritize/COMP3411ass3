import java.util.*;

//Used for pathfinding algorithms
class State {

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