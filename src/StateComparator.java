import java.util.*;

class StateComparator implements Comparator<State> {

    /**
     * Compares two States and returns comparison
     * @pre s1 and s2 are two valid state objects
     * @post Retuns -1, 0, or 1 depending on whether s1 is smaller, the same or bigger than s2 respectively
     */
    public int compare(State s1, State s2) {
        if (s1.cost < s2.cost) {
            return -1;
        } else if (s1.cost > s2.cost) {
            return 1;
        } else {
            return 0;
        }
    }
}