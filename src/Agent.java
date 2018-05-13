
/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411/9414/9814 Artificial Intelligence
 *  UNSW Session 1, 2018
*/

import java.util.*;
import java.io.*;
import java.net.*;

public class Agent {

    private int rafts = 0;
    private int stones = 0;
    private int axe = 0;
    private int keys = 0;

    private int c_y = 0;
    private int c_x = 0;

    //Orientation
    private char orient = 'v';

    //Map object
    Map map = new Map();

    //List of POI
    ArrayList<POI> pois = new ArrayList<POI>();
    ArrayList<POI> grabs = new ArrayList<POI>();

    /**
     * Current objective
     * curObj = 0: explore
     *         = 1: grab
     *         = 2: unlock
     *         = 3: chop
     *         = 4: destination
     * curPOI = co-ords to POI
     */

    int curObj = 0;
    POI curPOI = null;

    public char get_action(char view[][]) {

        /**
         * TODO:
         * - Pop off grabables and head towards them
         * - Make exploring more dynamic, traverse to unexplored areas until all areas explored
         *   before figuring out what to do next
         */

        /**
         * Strategy
         * 
         * 1. Look around to pick up items and record points of interests, will attempt to explore
         *    entire traversable area before crossing waters
         * 2. With area explored, attempts logical steps in this fashion
         *    1. If have key try to unlock door
         *      1. If door covered by small (1 square) water, try cross water
         *        1. Use rock first otherwise
         *        2. No rocks then we use raft
         *      2. If larger body of water we must use raft
         *    2. If we don't have a key, try cut down all near by trees and explore new explorable area
         *       then go back to 1.
         *    3. If we have explored all areas, unlocked all doors and picked everythingu p, we attempt to cross
         *       water and explore the all explorable water before embarking on land
         *      1. Attempt to formulate plan to get back on water, look for more trees
         *    4. Embark on land and go back to 1. 
         *    5. If we see treasure, make sure we get it
         *      1. If we need to cross water to go back, go back to step 1 and redo all proceeding steps 
         *         with going back to [0,0] as objective
         */
        
        map.addMap(view, orient, c_x, c_y);
        System.out.println(c_x + " , " + c_y);

        //Scan the view and add POIs
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (view[i][j] == 'T' ||
                    view[i][j] == '-' ||
                    view[i][j] == 'a' ||
                    view[i][j] == 'k' ||
                    view[i][j] == 'o' ||
                    view[i][j] == 'O' ||
                    view[i][j] == 'g') addPOI(view[i][j], j, i);
            }
        }

        //If we have no current objective, pop grabable POIs off list and get them
        if (curObj == 0) {

            if (pois.size() > 0) {


            } else {

                //If no existing POIs keep exploring
                //TODO
                return 'f';
            }
        }

        /*
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (view[i][j] == 'k') return goDestination(view, i, j);
            }
        }
        */
        
        map.printMap();
        printPOI();
        System.exit(0);
        return 'f';
    }

    /**
     * Given local scope co-ordinates, adds general co-ordinate of POI to ArrayList
     */
    private void addPOI(char type, int x, int y) {

        int tx, ty;

        //We now need to calculate the general co-ordinates
        if (orient == '^') {
            tx = c_x + x - 2;
            ty = c_y - y + 2;
        } else if (orient == 'v') {
            tx = c_x - x + 2;
            ty = c_y + y - 2;
        } else if (orient == '>') {
            tx = c_x - y + 2;
            ty = c_y - x + 2;
        } else {
            tx = c_x + y - 2;
            ty = c_y + x - 2;
        }

        //We add it to different lists for simplicity's sake
        if (type == 'a' ||
            type == 'k' ||
            type == 'o' ||
            type == 'g') {

            //See if the POI already exists in the set
            for (POI poi : grabs) {
                if (poi.type == type && poi.x == tx && poi.y == ty) return;
            }

            //List of grabables
            grabs.add(new POI(type, tx, ty));
        } else {

            //See if the POI already exists in the set
            for (POI poi : pois) {
                if (poi.type == type && poi.x == tx && poi.y == ty) return;
            }

            //List of grabables
            pois.add(new POI(type, tx, ty));
        }
    }

    /**
     * Print out current POIs
     */
    private void printPOI() {
        
        for (POI poi : pois) {
            System.out.println("POI: " + poi.type + " xy: " + poi.x + "," + poi.y);
        }
        for (POI poi : grabs) {
            System.out.println("POI: " + poi.type + " xy: " + poi.x + "," + poi.y);
        }
    }

    /**
     * Given a set of goal co-ordinates, finds the quicket way to get there
     * Also updates picking up specific items
     */
    private char goDestination(char view[][], int y, int x) {

        //Depending on where the dest is we orientate or go forward

        //First we check if the key is in front of us
        if (y < 2) {

            //Checks if an item is directly infront and pick its up if so
            if (view[1][2] == 'k') keys++;
            if (view[1][2] == 'a') axe++;
            if (view[1][2] == 'o') stones++;

            //We also update the current co-ordinate
            if (orient == '^') {
                c_y++;
            } else if (orient == 'v') {
                c_y--;
            } else if (orient == '<') {
                c_x--;
            } else {
                c_x++;
            }

            //If so we could jsut walk towards it until it isnt
            return 'f';
        
        //Otherwise we check if its directly to our side
        } else if (y == 2) {

            //If to our left
            if (x < 2) {

                //We also adjust the current orientation
                if (orient == '^') {
                    orient = '<';
                } else if (orient == 'v') {
                    orient = '>';
                } else if (orient == '<') {
                    orient = 'v';
                } else {
                    orient = '^';
                }

                //Turn left
                return 'l';
            
            //If to our right
            } else{

                //We also adjust the current orientation
                if (orient == '^') {
                    orient = '>';
                } else if (orient == 'v') {
                    orient = '<';
                } else if (orient == '<') {
                    orient = '^';
                } else {
                    orient = 'v';
                }

                //Turn right
                return 'r';
            }

        //Finally if the destination is behind us we turn depending on which which quadrant it is in
        } else {

            //If left
            if (x < 2) {

                //We also adjust the current orientation
                if (orient == '^') {
                    orient = '<';
                } else if (orient == 'v') {
                    orient = '>';
                } else if (orient == '<') {
                    orient = 'v';
                } else {
                    orient = '^';
                }

                //Turn left
                return 'l';
            
            //If right
            } else {

                //We also adjust the current orientation
                if (orient == '^') {
                    orient = '>';
                } else if (orient == 'v') {
                    orient = '<';
                } else if (orient == '<') {
                    orient = '^';
                } else {
                    orient = 'v';
                }
                
                //Turn right
                return 'r';
            }
        }
    }

    void print_view(char view[][]) {
        int i, j;

        System.out.println("\n+-----+");
        for (i = 0; i < 5; i++) {
            System.out.print("|");
            for (j = 0; j < 5; j++) {
                if ((i == 2) && (j == 2)) {
                    System.out.print('^');
                } else {
                    System.out.print(view[i][j]);
                }
            }
            System.out.println("|");
        }
        System.out.println("+-----+");
    }

    public static void main(String[] args) {
        InputStream in = null;
        OutputStream out = null;
        Socket socket = null;
        Agent agent = new Agent();
        char view[][] = new char[5][5];
        char action = 'F';
        int port;
        int ch;
        int i, j;

        if (args.length < 2) {
            System.out.println("Usage: java Agent -p <port>\n");
            System.exit(-1);
        }

        port = Integer.parseInt(args[1]);

        try { // open socket to Game Engine
            socket = new Socket("localhost", port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Could not bind to port: " + port);
            System.exit(-1);
        }

        try { // scan 5-by-5 wintow around current location
            while (true) {
                for (i = 0; i < 5; i++) {
                    for (j = 0; j < 5; j++) {
                        if (!((i == 2) && (j == 2))) {
                            ch = in.read();
                            if (ch == -1) {
                                System.exit(-1);
                            }
                            view[i][j] = (char) ch;
                        }
                    }
                }
                agent.print_view(view); // COMMENT THIS OUT BEFORE SUBMISSION
                action = agent.get_action(view);
                out.write(action);
            }
        } catch (IOException e) {
            System.out.println("Lost connection to port: " + port);
            System.exit(-1);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
