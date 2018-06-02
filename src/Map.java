/**
 * Map being constructed by AI as it is moving around
 */

import java.util.*;
import java.lang.Math;

public class Map {

    //Made to be 160x160 big so if agent starts on any corner, still large enough to fit entire map
    //Agent starts at grid [80,80] by default
    public char map[][];

    //Used for floodfill algorithm
    private boolean found;
    private boolean water;

    public Map() {

        map = new char[162][162];
        for (int i = 0; i < 162; i++) {
            Arrays.fill(map[i], '=');
        }
    }

    /**
     * Checks if a location has been explored
     */
    public boolean explored(int x, int y) {
        if (map[80-y][80+x] != '=') return true;
        return false;
    }

    /**
     * Expand the map by giving a view, current coordinates, and current orientation
     */
    public void addMap(char view[][], char orient, int x, int y) {

        //Convert co-ordinates to map's scope
        int c_x = 80 + x;
        int c_y = 80 - y;

        System.out.println(c_x + " , " + c_y);
        //Different subroutines depending on orientation
        if (orient == '^') {

            //Writing to map
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (i == 2 && j == 2) map[c_y+i-2][c_x+j-2] = '%';
                    else map[c_y+i-2][c_x+j-2] = view[i][j];
                }
            }
        } else if (orient == 'v') {

            //Writing to map
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (i == 2 && j == 2) map[c_y+i-2][c_x+j-2] = '%';
                    else map[c_y-i+2][c_x-j+2] = view[i][j];
                }
            }
        } else if (orient == '>') {

            //Writing to map
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (i == 2 && j == 2) map[c_y+i-2][c_x+j-2] = '%';
                    else map[c_y+j-2][c_x-i+2] = view[i][j];
                }
            }
        } else {

            //Writing to map
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (i == 2 && j == 2) map[c_y+i-2][c_x+j-2] = '%';
                    else map[c_y-j+2][c_x+i-2] = view[i][j];
                }
            }
        }
    }

    /**
     * When we open a door or cut down a tree we have to delete the poi from the map
     */
    public void demolishPOI(int x, int y) {

        //Convert co-ordinates to map's scope
        int mx = 80 + x;
        int my = 80 - y;

        //Change the map
        map[my][mx] = ' ';
    }

    /**
     * FOR UNIVERSAL CHECKS
     * Uses A* to find traversability and returns how many blocks of water
     * are needed to be trverssed to get to the destination
     * Returns -1 if no path traverssable
     * Have to give a boolean as to wether this is checking for an item or not
     */
    public int checkTraversable(int x, int y, int c_x, int c_y, boolean item) {
                
        //Convert co-ordinates to map's scope
        int mx = 80 + x;
        int my = 80 - y;
        int mc_x = 80 + c_x;
        int mc_y = 80 - c_y;

        //System.out.println("from " + mc_x + " , " + mc_y + " to " + mx + " , " + my);
        //Copy map used to mark searched 
        char [][] mapCopy = new char[map.length][];
        for(int i = 0; i < map.length; i++)
            mapCopy[i] = map[i].clone();

        //Queue of states
        PriorityQueue<State> states = new PriorityQueue<State>(new StateComparator());

        //Add initial state
        states.offer(new State(mc_x, mc_y, 0));
        //Mark initial state as visited
        mapCopy[mc_y][mc_x] = '&';

        State current = null;

        //Looping through priority queue
        while (states.size() > 0) {
            /*
            for (int i = 65; i < 95; i++) {
                for (int j = 65; j < 95; j++) {
                    System.out.print(mapCopy[i][j]);
                }
                System.out.print('\n');
            }
            */

            //Pop off from the queue
            current = states.poll();
            //System.out.println("wew " + current.x + " , " + current.y + " to " + mx + " , " + my);

            //Set current tile as explored
            mapCopy[current.y][current.x] = '&';

            //Check if we are at the goal state
            if (current.x == mx && current.y == my) break;

            //Check if current location is a water block
            if (mapCopy[current.y][current.x] == '~') current.waters++;

            //Expand states around current and add to queue
            if (mapCopy[current.y+1][current.x] != '&' &&
                mapCopy[current.y+1][current.x] != '*' &&
                mapCopy[current.y+1][current.x] != 'T' &&
                mapCopy[current.y+1][current.x] != '-' &&
                mapCopy[current.y+1][current.x] != '.' &&
                (mapCopy[current.y+1][current.x] != '=' || !item)) {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y+1 > 0 && my > 0) cost += Math.abs(current.y+1-my);
                else if (current.y+1 < 0 && my < 0) cost += Math.abs(current.y+1-my);
                else cost += Math.abs(Math.abs(current.y+1)+Math.abs(my));
                
                State newState = new State(current.x, current.y+1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('v');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y-1][current.x] != '&' &&
                mapCopy[current.y-1][current.x] != '*' &&
                mapCopy[current.y-1][current.x] != 'T' &&
                mapCopy[current.y-1][current.x] != '-' &&
                mapCopy[current.y-1][current.x] != '.' &&
                (mapCopy[current.y-1][current.x] != '=' || !item)) {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y-1 > 0 && my > 0) cost += Math.abs(current.y-1-my);
                else if (current.y-1 < 0 && my < 0) cost += Math.abs(current.y-1-my);
                else cost += Math.abs(Math.abs(current.y-1)+Math.abs(my));
                
                State newState = new State(current.x, current.y-1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('^');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x+1] != '&' &&
                mapCopy[current.y][current.x+1] != '*' &&
                mapCopy[current.y][current.x+1] != 'T' &&
                mapCopy[current.y][current.x+1] != '-' &&
                mapCopy[current.y][current.x+1] != '.' &&
                (mapCopy[current.y][current.x+1] != '=' || !item)) {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x+1 > 0 && mx > 0) cost += Math.abs(current.x+1-mx);
                else if (current.x+1 < 0 && mx < 0) cost += Math.abs(current.x+1-mx);
                else cost += Math.abs(Math.abs(current.x+1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x+1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('>');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x-1] != '&' &&
                mapCopy[current.y][current.x-1] != '*' &&
                mapCopy[current.y][current.x-1] != 'T' &&
                mapCopy[current.y][current.x-1] != '-' &&
                mapCopy[current.y][current.x-1] != '.' &&
                (mapCopy[current.y][current.x-1] != '=' || !item)) {
                
                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x-1 > 0 && mx > 0) cost += Math.abs(current.x-1-mx);
                else if (current.x-1 < 0 && mx < 0) cost += Math.abs(current.x-1-mx);
                else cost += Math.abs(Math.abs(current.x-1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x-1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('<');
                states.offer(newState);
            }

            //Set current to null for our algorithm
            current = null;
        }

        //If we found a successful path, return the amount of water we need to cross
        if (current != null) {
            return current.waters;
        }

        //Otherwise return -1;
        return -1;
    }
        
    /**
     * FOR DOOR CHECKS
     * Uses A* to find traversability and returns how many blocks of water
     * are needed to be trverssed to get to the destination
     * Returns -1 if no path traverssable
     */
    public int checkTraversableD(int x, int y, int c_x, int c_y) {
                
        //Convert co-ordinates to map's scope
        int mx = 80 + x;
        int my = 80 - y;
        int mc_x = 80 + c_x;
        int mc_y = 80 - c_y;

        //System.out.println("from " + mc_x + " , " + mc_y + " to " + mx + " , " + my);
        //Copy map used to mark searched 
        char [][] mapCopy = new char[map.length][];
        for(int i = 0; i < map.length; i++)
            mapCopy[i] = map[i].clone();

        //Queue of states
        PriorityQueue<State> states = new PriorityQueue<State>(new StateComparator());

        //Add initial state
        states.offer(new State(mc_x, mc_y, 0));
        //Mark initial state as visited
        mapCopy[mc_y][mc_x] = '&';

        State current = null;

        //Looping through priority queue
        while (states.size() > 0) {
            /*
            for (int i = 65; i < 95; i++) {
                for (int j = 65; j < 95; j++) {
                    System.out.print(mapCopy[i][j]);
                }
                System.out.print('\n');
            }
            */

            //Pop off from the queue
            current = states.poll();
            //System.out.println("wew " + current.x + " , " + current.y + " to " + mx + " , " + my);

            //Set current tile as explored
            mapCopy[current.y][current.x] = '&';

            //Check if we are at the goal state
            if (current.x == mx && current.y == my) break;

            //Check if current location is a water block
            if (mapCopy[current.y][current.x] == '~') current.waters++;

            //Expand states around current and add to queue
            if (mapCopy[current.y+1][current.x] != '&' &&
                mapCopy[current.y+1][current.x] != '*' &&
                mapCopy[current.y+1][current.x] != 'T' &&
                mapCopy[current.y+1][current.x] != '.' &&
                mapCopy[current.y+1][current.x] != '=') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y+1 > 0 && my > 0) cost += Math.abs(current.y+1-my);
                else if (current.y+1 < 0 && my < 0) cost += Math.abs(current.y+1-my);
                else cost += Math.abs(Math.abs(current.y+1)+Math.abs(my));
                
                State newState = new State(current.x, current.y+1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('v');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y-1][current.x] != '&' &&
                mapCopy[current.y-1][current.x] != '*' &&
                mapCopy[current.y-1][current.x] != 'T' &&
                mapCopy[current.y-1][current.x] != '.' &&
                mapCopy[current.y-1][current.x] != '=') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y-1 > 0 && my > 0) cost += Math.abs(current.y-1-my);
                else if (current.y-1 < 0 && my < 0) cost += Math.abs(current.y-1-my);
                else cost += Math.abs(Math.abs(current.y-1)+Math.abs(my));
                
                State newState = new State(current.x, current.y-1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('^');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x+1] != '&' &&
                mapCopy[current.y][current.x+1] != '*' &&
                mapCopy[current.y][current.x+1] != 'T' &&
                mapCopy[current.y][current.x+1] != '.' &&
                mapCopy[current.y][current.x+1] != '=') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x+1 > 0 && mx > 0) cost += Math.abs(current.x+1-mx);
                else if (current.x+1 < 0 && mx < 0) cost += Math.abs(current.x+1-mx);
                else cost += Math.abs(Math.abs(current.x+1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x+1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('>');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x-1] != '&' &&
                mapCopy[current.y][current.x-1] != '*' &&
                mapCopy[current.y][current.x-1] != 'T' &&
                mapCopy[current.y][current.x-1] != '.' &&
                mapCopy[current.y][current.x-1] != '=') {
                
                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x-1 > 0 && mx > 0) cost += Math.abs(current.x-1-mx);
                else if (current.x-1 < 0 && mx < 0) cost += Math.abs(current.x-1-mx);
                else cost += Math.abs(Math.abs(current.x-1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x-1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('<');
                states.offer(newState);
            }

            //Set current to null for our algorithm
            current = null;
        }

        //If we found a successful path, return the amount of water we need to cross
        if (current != null) {
            return current.waters;
        }

        //Otherwise return -1;
        return -1;
    }

    /**
     * FOR TREE CHECKS
     * Uses A* to find traversability and returns how many blocks of water
     * are needed to be trverssed to get to the destination
     * Returns -1 if no path traverssable
     */
    public int checkTraversableT(int x, int y, int c_x, int c_y) {
                
        //Convert co-ordinates to map's scope
        int mx = 80 + x;
        int my = 80 - y;
        int mc_x = 80 + c_x;
        int mc_y = 80 - c_y;

        //System.out.println("from " + mc_x + " , " + mc_y + " to " + mx + " , " + my);
        //Copy map used to mark searched 
        char [][] mapCopy = new char[map.length][];
        for(int i = 0; i < map.length; i++)
            mapCopy[i] = map[i].clone();

        //Queue of states
        PriorityQueue<State> states = new PriorityQueue<State>(new StateComparator());

        //Add initial state
        states.offer(new State(mc_x, mc_y, 0));
        //Mark initial state as visited
        mapCopy[mc_y][mc_x] = '&';

        State current = null;

        //Looping through priority queue
        while (states.size() > 0) {
            /*
            for (int i = 65; i < 95; i++) {
                for (int j = 65; j < 95; j++) {
                    System.out.print(mapCopy[i][j]);
                }
                System.out.print('\n');
            }
            */

            //Pop off from the queue
            current = states.poll();
            //System.out.println("wew " + current.x + " , " + current.y + " to " + mx + " , " + my);

            //Set current tile as explored
            mapCopy[current.y][current.x] = '&';

            //Check if we are at the goal state
            if (current.x == mx && current.y == my) break;

            //Check if current location is a water block
            if (mapCopy[current.y][current.x] == '~') current.waters++;

            //Expand states around current and add to queue
            if (mapCopy[current.y+1][current.x] != '&' &&
                mapCopy[current.y+1][current.x] != '*' &&
                mapCopy[current.y+1][current.x] != '-' &&
                mapCopy[current.y+1][current.x] != '.' &&
                mapCopy[current.y+1][current.x] != '=') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y+1 > 0 && my > 0) cost += Math.abs(current.y+1-my);
                else if (current.y+1 < 0 && my < 0) cost += Math.abs(current.y+1-my);
                else cost += Math.abs(Math.abs(current.y+1)+Math.abs(my));
                
                State newState = new State(current.x, current.y+1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('v');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y-1][current.x] != '&' &&
                mapCopy[current.y-1][current.x] != '*' &&
                mapCopy[current.y-1][current.x] != '-' &&
                mapCopy[current.y-1][current.x] != '.' &&
                mapCopy[current.y-1][current.x] != '=') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y-1 > 0 && my > 0) cost += Math.abs(current.y-1-my);
                else if (current.y-1 < 0 && my < 0) cost += Math.abs(current.y-1-my);
                else cost += Math.abs(Math.abs(current.y-1)+Math.abs(my));
                
                State newState = new State(current.x, current.y-1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('^');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x+1] != '&' &&
                mapCopy[current.y][current.x+1] != '*' &&
                mapCopy[current.y][current.x+1] != '-' &&
                mapCopy[current.y][current.x+1] != '.' &&
                mapCopy[current.y][current.x+1] != '=') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x+1 > 0 && mx > 0) cost += Math.abs(current.x+1-mx);
                else if (current.x+1 < 0 && mx < 0) cost += Math.abs(current.x+1-mx);
                else cost += Math.abs(Math.abs(current.x+1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x+1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('>');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x-1] != '&' &&
                mapCopy[current.y][current.x-1] != '*' &&
                mapCopy[current.y][current.x-1] != '-' &&
                mapCopy[current.y][current.x-1] != '.' &&
                mapCopy[current.y][current.x-1] != '=') {
                
                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x-1 > 0 && mx > 0) cost += Math.abs(current.x-1-mx);
                else if (current.x-1 < 0 && mx < 0) cost += Math.abs(current.x-1-mx);
                else cost += Math.abs(Math.abs(current.x-1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x-1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('<');
                states.offer(newState);
            }

            //Set current to null for our algorithm
            current = null;
        }

        //If we found a successful path, return the amount of water we need to cross
        if (current != null) {
            return current.waters;
        }

        //Otherwise return -1;
        return -1;
    }

    /**
     * Uses A* to travel from c to x,y
     * Also needs a type it is travelling to so it doesn't mess with the algorithm
     */
    public char AStarTravel(int x, int y, int c_x, int c_y, char type) {
        
        //Convert co-ordinates to map's scope
        int mx = 80 + x;
        int my = 80 - y;
        int mc_x = 80 + c_x;
        int mc_y = 80 - c_y;

        System.out.println("from " + mc_x + " , " + mc_y + " to " + mx + " , " + my);
        //Copy map used to mark searched 
        char [][] mapCopy = new char[map.length][];
        for(int i = 0; i < map.length; i++)
            mapCopy[i] = map[i].clone();

        //Queue of states
        PriorityQueue<State> states = new PriorityQueue<State>(new StateComparator());

        //Add initial state
        states.offer(new State(mc_x, mc_y, 0));
        //Mark initial state as visited
        mapCopy[mc_y][mc_x] = '&';

        State current = null;

        //Looping through priority queue
        while (states.size() > 0) {
            /*
            for (int i = 65; i < 95; i++) {
                for (int j = 65; j < 95; j++) {
                    System.out.print(mapCopy[i][j]);
                }
                System.out.print('\n');
            }
            */

            //Pop off from the queue
            current = states.poll();
            //System.out.println("wew " + current.x + " , " + current.y + " to " + mx + " , " + my);

            //Set current tile as explored
            mapCopy[current.y][current.x] = '&';

            //Check if we are at the goal state
            if (current.x == mx && current.y == my) break;

            //Expand states around current and add to queue
            if (mapCopy[current.y+1][current.x] != '&' &&
                mapCopy[current.y+1][current.x] != '*' &&
                mapCopy[current.y+1][current.x] != '.' &&
                (mapCopy[current.y+1][current.x] != '~' || type == '~') &&
                (mapCopy[current.y+1][current.x] != 'T' || type == 'T') &&
                (mapCopy[current.y+1][current.x] != '-' || type == '-')) {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y+1 > 0 && my > 0) cost += Math.abs(current.y+1-my);
                else if (current.y+1 < 0 && my < 0) cost += Math.abs(current.y+1-my);
                else cost += Math.abs(Math.abs(current.y+1)+Math.abs(my));
                
                State newState = new State(current.x, current.y+1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('v');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y-1][current.x] != '&' &&
                mapCopy[current.y-1][current.x] != '*' &&
                mapCopy[current.y-1][current.x] != '.' &&
                (mapCopy[current.y-1][current.x] != '~' || type == '~') &&
                (mapCopy[current.y-1][current.x] != 'T' || type == 'T') &&
                (mapCopy[current.y-1][current.x] != '-' || type == '-')) {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y-1 > 0 && my > 0) cost += Math.abs(current.y-1-my);
                else if (current.y-1 < 0 && my < 0) cost += Math.abs(current.y-1-my);
                else cost += Math.abs(Math.abs(current.y-1)+Math.abs(my));
                
                State newState = new State(current.x, current.y-1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('^');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x+1] != '&' &&
                mapCopy[current.y][current.x+1] != '*' &&
                mapCopy[current.y][current.x+1] != '.' &&
                (mapCopy[current.y][current.x+1] != '~' || type == '~') &&
                (mapCopy[current.y][current.x+1] != 'T' || type == 'T') &&
                (mapCopy[current.y][current.x+1] != '-' || type == '-')) {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x+1 > 0 && mx > 0) cost += Math.abs(current.x+1-mx);
                else if (current.x+1 < 0 && mx < 0) cost += Math.abs(current.x+1-mx);
                else cost += Math.abs(Math.abs(current.x+1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x+1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('>');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x-1] != '&' &&
                mapCopy[current.y][current.x-1] != '*' &&
                mapCopy[current.y][current.x-1] != '.' &&
                (mapCopy[current.y][current.x-1] != '~' || type == '~') &&
                (mapCopy[current.y][current.x-1] != 'T' || type == 'T') &&
                (mapCopy[current.y][current.x-1] != '-' || type == '-')) {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x-1 > 0 && mx > 0) cost += Math.abs(current.x-1-mx);
                else if (current.x-1 < 0 && mx < 0) cost += Math.abs(current.x-1-mx);
                else cost += Math.abs(Math.abs(current.x-1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x-1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('<');
                states.offer(newState);
            }

            //Reset current to null so it works with our algorithm
            current = null;
        }

        //If we have found a successful path return the next move we should make
        if (current != null){
            if (current.moves.size() > 0) return current.moves.get(0);
        }

        System.out.println("we shouldn't be here");
        System.exit(0);
        return 'f';
    }

    /**
     * Uses A* for water exploration
     * Also needs a type it is travelling to so it doesn't mess with the algorithm
     */
    public char AStarTravelW(int x, int y, int c_x, int c_y) {
        
        //Convert co-ordinates to map's scope
        int mx = 80 + x;
        int my = 80 - y;
        int mc_x = 80 + c_x;
        int mc_y = 80 - c_y;

        System.out.println("from " + mc_x + " , " + mc_y + " to " + mx + " , " + my);
        //Copy map used to mark searched 
        char [][] mapCopy = new char[map.length][];
        for(int i = 0; i < map.length; i++)
            mapCopy[i] = map[i].clone();

        //Queue of states
        PriorityQueue<State> states = new PriorityQueue<State>(new StateComparator());

        //Add initial state
        states.offer(new State(mc_x, mc_y, 0));
        //Mark initial state as visited
        mapCopy[mc_y][mc_x] = '&';

        State current = null;

        //Looping through priority queue
        while (states.size() > 0) {
            /*
            for (int i = 65; i < 95; i++) {
                for (int j = 65; j < 95; j++) {
                    System.out.print(mapCopy[i][j]);
                }
                System.out.print('\n');
            }
            */

            //Pop off from the queue
            current = states.poll();
            //System.out.println("wew " + current.x + " , " + current.y + " to " + mx + " , " + my);

            //Set current tile as explored
            mapCopy[current.y][current.x] = '&';

            //Check if we are at the goal state
            if (current.x == mx && current.y == my) break;

            //Expand states around current and add to queue
            if (mapCopy[current.y+1][current.x] != '&' &&
                mapCopy[current.y+1][current.x] != '*' &&
                mapCopy[current.y+1][current.x] != 'T' &&
                mapCopy[current.y+1][current.x] != '-' &&
                mapCopy[current.y+1][current.x] != '.' &&
                mapCopy[current.y+1][current.x] != ' ') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y+1 > 0 && my > 0) cost += Math.abs(current.y+1-my);
                else if (current.y+1 < 0 && my < 0) cost += Math.abs(current.y+1-my);
                else cost += Math.abs(Math.abs(current.y+1)+Math.abs(my));
                
                State newState = new State(current.x, current.y+1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('v');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y-1][current.x] != '&' &&
                mapCopy[current.y-1][current.x] != '*' &&
                mapCopy[current.y-1][current.x] != 'T' &&
                mapCopy[current.y-1][current.x] != '-' &&
                mapCopy[current.y-1][current.x] != '.' &&
                mapCopy[current.y-1][current.x] != ' ') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y-1 > 0 && my > 0) cost += Math.abs(current.y-1-my);
                else if (current.y-1 < 0 && my < 0) cost += Math.abs(current.y-1-my);
                else cost += Math.abs(Math.abs(current.y-1)+Math.abs(my));
                
                State newState = new State(current.x, current.y-1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('^');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x+1] != '&' &&
                mapCopy[current.y][current.x+1] != '*' &&
                mapCopy[current.y][current.x+1] != 'T' &&
                mapCopy[current.y][current.x+1] != '-' &&
                mapCopy[current.y][current.x+1] != '.' &&
                mapCopy[current.y][current.x+1] != ' ') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x+1 > 0 && mx > 0) cost += Math.abs(current.x+1-mx);
                else if (current.x+1 < 0 && mx < 0) cost += Math.abs(current.x+1-mx);
                else cost += Math.abs(Math.abs(current.x+1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x+1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('>');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x-1] != '&' &&
                mapCopy[current.y][current.x-1] != '*' &&
                mapCopy[current.y][current.x-1] != 'T' &&
                mapCopy[current.y][current.x-1] != '-' &&
                mapCopy[current.y][current.x-1] != '.' &&
                mapCopy[current.y][current.x-1] != ' ') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x-1 > 0 && mx > 0) cost += Math.abs(current.x-1-mx);
                else if (current.x-1 < 0 && mx < 0) cost += Math.abs(current.x-1-mx);
                else cost += Math.abs(Math.abs(current.x-1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x-1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('<');
                states.offer(newState);
            }

            //Reset current to null so it works with our algorithm
            current = null;
        }

        //If we have found a successful path return the next move we should make
        if (current != null){
            if (current.moves.size() > 0) return current.moves.get(0);
        }

        System.out.println("we shouldn't be here WATER");
        System.exit(0);
        return 'f';
    }

    /**
     * Smarter A* to travel from c to x,y
     * This algorithm takes into account bodies of water that need to e traversed
     * and used limited resources in the most intelligent way
     */
    public char SmarterAStarTravel(int x, int y, int c_x, int c_y, Agent a) {
        
        //Convert co-ordinates to map's scope
        int mx = 80 + x;
        int my = 80 - y;
        int mc_x = 80 + c_x;
        int mc_y = 80 - c_y;

        System.out.println("from " + mc_x + " , " + mc_y + " to " + mx + " , " + my);
        //Copy map used to mark searched 
        char [][] mapCopy = new char[map.length][];
        for(int i = 0; i < map.length; i++)
            mapCopy[i] = map[i].clone();

        //Queue of states
        PriorityQueue<State> states = new PriorityQueue<State>(new StateComparator());

        //Add initial state
        states.offer(new State(mc_x, mc_y, 0));
        //Mark initial state as visited
        mapCopy[mc_y][mc_x] = '&';

        //Add the current tools/etc stats of the agent to the state
        states.peek().raft = a.raft;
        states.peek().stones = a.stones;
        states.peek().axe = a.axe;
        states.peek().keys = a.keys;
        states.peek().on_water = a.on_water;
        states.peek().on_raft = a.on_raft;
        states.peek().on_rock = a.on_rock;
        states.peek().found_treasure = a.found_treasure;

        State current = null;

        //Looping through priority queue
        while (states.size() > 0) {
            /*
            for (int i = 65; i < 95; i++) {
                for (int j = 65; j < 95; j++) {
                    System.out.print(mapCopy[i][j]);
                }
                System.out.print('\n');
            }
            */

            //Pop off from the queue
            current = states.poll();
            //System.out.println("wew " + current.x + " , " + current.y + " to " + mx + " , " + my);

            //Set current tile as explored
            mapCopy[current.y][current.x] = '&';

            //Check if we are at the goal state
            if (current.x == mx && current.y == my) break;

            //Expand states around current and add to queue
            if (mapCopy[current.y+1][current.x] != '&' &&
                mapCopy[current.y+1][current.x] != '*' &&
                mapCopy[current.y+1][current.x] != '.' &&
                mapCopy[current.y+1][current.x] != '~' &&
                mapCopy[current.y+1][current.x] != 'T' &&
                mapCopy[current.y+1][current.x] != '-') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y+1 > 0 && my > 0) cost += Math.abs(current.y+1-my);
                else if (current.y+1 < 0 && my < 0) cost += Math.abs(current.y+1-my);
                else cost += Math.abs(Math.abs(current.y+1)+Math.abs(my));
                
                State newState = new State(current.x, current.y+1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('v');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y-1][current.x] != '&' &&
                mapCopy[current.y-1][current.x] != '*' &&
                mapCopy[current.y-1][current.x] != '.' &&
                mapCopy[current.y-1][current.x] != '~' &&
                mapCopy[current.y-1][current.x] != 'T' &&
                mapCopy[current.y-1][current.x] != '-') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x > 0 && mx > 0) cost += Math.abs(current.x-mx);
                else if (current.x < 0 && mx < 0) cost += Math.abs(current.x-mx);
                else cost += Math.abs(Math.abs(current.x)+Math.abs(mx));
                if (current.y-1 > 0 && my > 0) cost += Math.abs(current.y-1-my);
                else if (current.y-1 < 0 && my < 0) cost += Math.abs(current.y-1-my);
                else cost += Math.abs(Math.abs(current.y-1)+Math.abs(my));
                
                State newState = new State(current.x, current.y-1, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('^');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x+1] != '&' &&
                mapCopy[current.y][current.x+1] != '*' &&
                mapCopy[current.y][current.x+1] != '.' &&
                mapCopy[current.y][current.x+1] != '~' &&
                mapCopy[current.y][current.x+1] != 'T' &&
                mapCopy[current.y][current.x+1] != '-') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x+1 > 0 && mx > 0) cost += Math.abs(current.x+1-mx);
                else if (current.x+1 < 0 && mx < 0) cost += Math.abs(current.x+1-mx);
                else cost += Math.abs(Math.abs(current.x+1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x+1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('>');
                states.offer(newState);
            }
            //Expand states around current and add to queue
            if (mapCopy[current.y][current.x-1] != '&' &&
                mapCopy[current.y][current.x-1] != '*' &&
                mapCopy[current.y][current.x-1] != '.' &&
                mapCopy[current.y][current.x-1] != '~' &&
                mapCopy[current.y][current.x-1] != 'T' &&
                mapCopy[current.y][current.x-1] != '-') {

                //If traversable then make a state for it
                //Calculate manhattan distance
                int cost = current.moves.size() + 1;
                if (current.x-1 > 0 && mx > 0) cost += Math.abs(current.x-1-mx);
                else if (current.x-1 < 0 && mx < 0) cost += Math.abs(current.x-1-mx);
                else cost += Math.abs(Math.abs(current.x-1)+Math.abs(mx));
                if (current.y > 0 && my > 0) cost += Math.abs(current.y-my);
                else if (current.y < 0 && my < 0) cost += Math.abs(current.y-my);
                else cost += Math.abs(Math.abs(current.y)+Math.abs(my));
                
                State newState = new State(current.x-1, current.y, cost);
                //Add the new path
                for (Character c : current.moves) {
                    newState.moves.add(c);
                }
                //Add the upper movement
                newState.moves.add('<');
                states.offer(newState);
            }

            //Reset current to null so it works with our algorithm
            current = null;
        }

        //If we have found a successful path return the next move we should make
        if (current != null){
            if (current.moves.size() > 0) return current.moves.get(0);
        }

        System.out.println("we shouldn't be here");
        System.exit(0);
        return 'f';
    }


    /**
     * Flood searches for unexplored areas
     * Takes a 0,0 origin scoped co-ordinate
     * Also needs a boolean value as to whether we are crossing water or not
     */
    public POI floodSearch(int x, int y, boolean water) {

        //Convert co-ordinates to map's scope
        int c_x = 80 + x;
        int c_y = 80 - y;

        this.water = water;

        char [][] mapCopy = new char[map.length][];
        for(int i = 0; i < map.length; i++)
            mapCopy[i] = map[i].clone();

        found = false;
        POI retVal = RfloodSearch(mapCopy, c_x, c_y);

        /*
        for (int i = 50; i < 110; i++) {
            for (int j = 50; j < 110; j++) {
                System.out.print(mapCopy[i][j]);
            }
            System.out.print('\n');
        }
        */

        if (retVal == null) return null;

        //Convverting back to general co-ordinates
        System.out.println("found = at = " + retVal.x + ", " + retVal.y);
        retVal.x = retVal.x - 80;
        retVal.y = 80 - retVal.y;
        
        return retVal;
    }

    private POI RfloodSearch(char mapCopy[][], int x, int y) {

        //Base cases
        if (found == true) return null;
        if (mapCopy[y][x] == '*' ||
            mapCopy[y][x] == 'T' ||
            mapCopy[y][x] == '-' ||
            mapCopy[y][x] == '.' ||
            (mapCopy[y][x] == '~' && !water) ||
            mapCopy[y][x] == '&') return null;
        if (map[y][x] == '=') {
            found = true;
            System.out.println("found = at = " + x + ", " + y);
            return new POI('=', x, y);
        }

        mapCopy[y][x] = '&';
        
        //Recursive call
        POI retVal = null;
        retVal = RfloodSearch(mapCopy, x+1, y);
        if (retVal != null) return retVal;
        retVal = RfloodSearch(mapCopy, x-1, y);
        if (retVal != null) return retVal;
        retVal = RfloodSearch(mapCopy, x, y+1);
        if (retVal != null) return retVal;
        retVal = RfloodSearch(mapCopy, x, y-1);
        if (retVal != null) return retVal;

        return null;
    }

    /**
    * Flood searches for unexplored water areas on a raft
    * Takes a 0,0 origin scoped co-ordinate
    * Also needs a boolean value as to whether we are crossing water or not
    */
   public POI floodSearchW(int x, int y) {

       //Convert co-ordinates to map's scope
       int c_x = 80 + x;
       int c_y = 80 - y;

       char [][] mapCopy = new char[map.length][];
       for(int i = 0; i < map.length; i++)
           mapCopy[i] = map[i].clone();

       found = false;
       POI retVal = RfloodSearchW(mapCopy, c_x, c_y);

        /*       
       for (int i = 50; i < 110; i++) {
           for (int j = 50; j < 110; j++) {
               System.out.print(mapCopy[i][j]);
           }
           System.out.print('\n');
       }
       */

       if (retVal == null) return null;

       //Convverting back to general co-ordinates
       System.out.println("found = at = " + retVal.x + ", " + retVal.y);
       retVal.x = retVal.x - 80;
       retVal.y = 80 - retVal.y;
       
       return retVal;
   }

   private POI RfloodSearchW(char mapCopy[][], int x, int y) {

       //Base cases
       if (found == true) return null;
       if (mapCopy[y][x] == '*' ||
           mapCopy[y][x] == 'T' ||
           mapCopy[y][x] == '-' ||
           mapCopy[y][x] == '&' ||
           mapCopy[y][x] == '.' ||
           (mapCopy[y][x] == ' ' && mapCopy[y][x] == '%')) return null;
       if (map[y][x] == '=') {
           found = true;
           System.out.println("found = at = " + x + ", " + y);
           return new POI('=', x, y);
       }

       mapCopy[y][x] = '&';
       
       //Recursive call
       POI retVal = null;
       retVal = RfloodSearchW(mapCopy, x+1, y);
       if (retVal != null) return retVal;
       retVal = RfloodSearchW(mapCopy, x-1, y);
       if (retVal != null) return retVal;
       retVal = RfloodSearchW(mapCopy, x, y+1);
       if (retVal != null) return retVal;
       retVal = RfloodSearchW(mapCopy, x, y-1);
       if (retVal != null) return retVal;

       return null;
   }

    public void printMap() {
        
        for (int i = 65; i < 95; i++) {
            for (int j = 65; j < 120; j++) {
                System.out.print(map[i][j]);
            }
            System.out.print('\n');
        }
    }

}