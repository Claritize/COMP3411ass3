/**
 * Map being constructed by AI as it is moving around
 */

import java.util.*;
import java.lang.Math;

public class Map {

    //Made to be 160x160 big so if agent starts on any corner, still large enough to fit entire map
    //Agent starts at grid [80,80] by default
    private char map[][];

    //Used for floodfill algorithm
    private boolean found;

    public Map() {

        map = new char[162][162];
        for (int i = 0; i < 162; i++) {
            Arrays.fill(map[i], '=');
        }
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
                    if (i == 2 && j == 2) map[c_y+i-2][c_x+j-2] = ' ';
                    else map[c_y+i-2][c_x+j-2] = view[i][j];
                }
            }
        } else if (orient == 'v') {

            //Writing to map
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (i == 2 && j == 2) map[c_y+i-2][c_x+j-2] = ' ';
                    else map[c_y-i+2][c_x-j+2] = view[i][j];
                }
            }
        } else if (orient == '>') {

            //Writing to map
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (i == 2 && j == 2) map[c_y+i-2][c_x+j-2] = ' ';
                    else map[c_y+j-2][c_x-i+2] = view[i][j];
                }
            }
        } else {

            //Writing to map
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (i == 2 && j == 2) map[c_y+i-2][c_x+j-2] = ' ';
                    else map[c_y-j+2][c_x+i-2] = view[i][j];
                }
            }
        }
    }

    /**
     * Uses A* to travel from c to x,y
     */
    public char AStarTravel(int x, int y, int c_x, int c_y) {
        
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
                mapCopy[current.y+1][current.x] != '~' &&
                mapCopy[current.y+1][current.x] != 'T' &&
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
                mapCopy[current.y-1][current.x] != '~' &&
                mapCopy[current.y-1][current.x] != 'T' &&
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
                mapCopy[current.y][current.x+1] != '~' &&
                mapCopy[current.y][current.x+1] != 'T' &&
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
                mapCopy[current.y][current.x-1] != '~' &&
                mapCopy[current.y][current.x-1] != 'T' &&
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
        }

        if (current != null) return current.moves.get(0);
        return 'f';
    }

    /**
     * Flood searches for unexplored areas
     * Takes a 0,0 origin scoped co-ordinate
     */
    public POI floodSearch(int x, int y) {

        //Convert co-ordinates to map's scope
        int c_x = 80 + x;
        int c_y = 80 - y;

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
            mapCopy[y][x] == '~' ||
            mapCopy[y][x] == '&') return null;
        if (map[y][x] == '=') {
            found = true;
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

    public void printMap() {
        
        for (int i = 65; i < 95; i++) {
            for (int j = 65; j < 95; j++) {
                System.out.print(map[i][j]);
            }
            System.out.print('\n');
        }
    }

}