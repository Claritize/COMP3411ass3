
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
    int grabsComplete = 0;

    int time = 0;

    public char get_action(char view[][]) {

        /**
         * TO DO:
         * - Add ability to check if POI is accessible
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
        System.out.println("current_orient = " + orient);
        //map.printMap();
        //map.printMap();
        
        System.out.println("AgentPOS = " + c_x + "," + c_y);

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

        System.out.println("curobj= " + curObj + " grabs=" + grabs.size() + " POIs=" + pois.size());
        if (curObj == 1) {
            System.out.println("getting " + curPOI.type + " xy: " + curPOI.x + "," + curPOI.y + "," + curPOI.interacted);
        }
        printPOI();
        System.out.println("grabs gotten = " + grabsComplete);
        //If we have no current objective, pop grabable POIs off list and get them
        if (curObj == 0) {

            if (grabsComplete < grabs.size()) {
                
                //Pop off the list
                System.out.println("getting new obj");
                
                //Look for another POI to get
                for (POI p : grabs) {
                    if (!p.interacted) {
                        curPOI = p;
                        break;
                    }
                }
                curObj = 1;
            } else {

                //If no existing POIs keep exploring
                //TODO
                System.out.println("starting exploration");

                curPOI = map.floodSearch(c_x, c_y);
                
                //If curPOI is returned as NULL, that means we have explored everything
                if (curPOI == null) time = 50;

                System.out.println("current flood search = " + c_x + " ," + c_y);
                curObj = 0;
            }
        }

        time++;
        //We move to our current objective
        if (time < 50) {
            char travelDir = map.AStarTravel(curPOI.x, curPOI.y, c_x, c_y);
            System.out.println("direction: " + travelDir);
            map.printMap();
            if (orient == travelDir || travelDir == 'f') {

                if (orient == '^') {
                    c_y++;
                } else if (orient == 'v') {
                    c_y--;
                } else if (orient == '>') {
                    c_x++;
                } else {
                    c_x--;
                }

                //We need to check if we would get an item when moved
                if (view[1][2] == 'k') {

                    keys++;
                    //We need to check if the object we are picking up is our POI
                    if (c_x == curPOI.x && c_y == curPOI.y) {
                    
                        curPOI.interacted = true;
                        grabsComplete++;
                        curPOI = null;
                        curObj = 0;
                    } else {
                        
                        //Otherwise we have to find it in our POIs and set interactable to false
                        for (POI poi : grabs) {
                            if (c_x == poi.x && c_y == poi.y) {
                                poi.interacted = true;
                                grabsComplete++;
                                break;
                            }
                        }
                    }
                }
                else if (view[1][2] == 'o') {

                    stones++;
                    //We need to check if the object we are picking up is our POI
                    if (c_x == curPOI.x && c_y == curPOI.y) {
                                        
                        curPOI.interacted = true;
                        grabsComplete++;
                        curPOI = null;
                        curObj = 0;
                    } else {
                        
                        //Otherwise we have to find it in our POIs and set interactable to false
                        for (POI poi : grabs) {
                            if (c_x == poi.x && c_y == poi.y) {
                                poi.interacted = true;
                                grabsComplete++;
                                break;
                            }
                        }
                    }
                }
                else if (view[1][2] == 'a') {

                    axe++;
                    //We need to check if the object we are picking up is our POI
                    if (c_x == curPOI.x && c_y == curPOI.y) {
                    
                        curPOI.interacted = true;
                        grabsComplete++;
                        curPOI = null;
                        curObj = 0;
                    } else {
                        
                        //Otherwise we have to find it in our POIs and set interactable to false
                        for (POI poi : grabs) {
                            if (c_x == poi.x && c_y == poi.y) {
                                poi.interacted = true;
                                grabsComplete++;
                                break;
                            }
                        }
                    }
                }
                // else {
                //     if (c_x == curPOI.x && c_y == curPOI.y) {
                //         curPOI = null;
                //         curObj = 0;
                //     }
                        
                // }

                return 'f';

            } else if (travelDir == '^') {
                if (orient == '>') {
                    orient = '^';
                    return 'l';
                } else if (orient == '<') {
                    orient = '^';
                    return 'r';
                } else {
                    orient = '<';
                    return 'r';
                }
            } else if (travelDir == '>') {
                if (orient == '^') {
                    orient = '>';
                    return 'r';
                } else if (orient == '<') {
                    orient = '^';
                    return 'r';
                } else {
                    orient = '>';
                    return 'l';
                }
            } else if (travelDir == '<') {
                if (orient == '>') {
                    orient = '^';
                    return 'l';
                } else if (orient == '^') {
                    orient = '<';
                    return 'l';
                } else {
                    orient = '<';
                    return 'r';
                }
            } else {
                if (orient == '>') {
                    orient = 'v';
                    return 'r';
                } else if (orient == '<') {
                    orient = 'v';
                    return 'l';
                } else {
                    orient = '>';
                    return 'r';
                }
            }
        }
        
        map.printMap();
        printPOI();
        System.out.println("ehhhhhh" + curPOI.x + "," + curPOI.y);
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
            System.out.println("POI: " + poi.type + " xy: " + poi.x + "," + poi.y + "," + poi.interacted);
        }
        for (POI poi : grabs) {
            System.out.println("grab: " + poi.type + " xy: " + poi.x + "," + poi.y + "," + poi.interacted);
        }
    }



    /**
     * Given set of zero scoped co-ordinates, travels there,
     * co-ordinates must be accessible 
     */
    /*private char travelDest(int x, int y) {
        
        //First we check if the goal is directly north/south/east/west of our current location
        if (c_x == x) {

            //This means the destination is directly up or down
            //Now we check which it is
            if (c_y < y) {
                
                //If it is above us
                //Now we check our orientation and move appropriately
                if (orient == '^') {
                    
                    c_y++;

                    //Check if destination is right infront, then we
                    //need to update objectives
                    if (c_y == y) {
                        curObj = 0;
                        //Update item counts
                        if (curPOI.type == 'a') axe++;
                        if (curPOI.type == 'o') stones++;
                        if (curPOI.type == 'k') keys++;
                        curPOI.interacted = true;
                        //Reset curPOI;
                        curPOI = null;
                    }

                    return 'f';
                } else if (orient == 'v') {

                    orient = '>';
                    return 'l';
                } else if (orient == '>') {

                    orient = '^';
                    return 'l';
                } else {

                    orient = '^';
                    return 'r';
                }
            } else {

                //If it is below us
                //Now we check our orientation and move appropriately
                if (orient == '^') {
    
                    orient = '>';
                    return 'r';
                } else if (orient == 'v') {

                    c_y--;

                    //Check if destination is right infront, then we
                    //need to update objectives
                    if (c_y == y) {
                        curObj = 0;
                        //Update item counts
                        if (curPOI.type == 'a') axe++;
                        if (curPOI.type == 'o') stones++;
                        if (curPOI.type == 'k') keys++;
                        curPOI.interacted = true;
                        //Reset curPOI;
                        curPOI = null;
                    }
                    
                    return 'f';
                } else if (orient == '>') {

                    orient = 'v';
                    return 'r';
                } else {

                    orient = 'v';
                    return 'l';
                }
            }
        } else if (c_y == y) {

            //This means the destination is directly left or right
            //Now we check which it is
            if (c_x < x) {
                
                //If it is to our right
                //Now we check our orientation and move appropriately
                if (orient == '^') {
    
                    orient = '>';
                    return 'r';
                } else if (orient == 'v') {

                    orient = '>';
                    return 'l';
                } else if (orient == '>') {

                    c_x++;

                    //Check if destination is right infront, then we
                    //need to update objectives
                    if (c_x == x) {
                        curObj = 0;
                        //Update item counts
                        if (curPOI.type == 'a') axe++;
                        if (curPOI.type == 'o') stones++;
                        if (curPOI.type == 'k') keys++;
                        curPOI.interacted = true;
                        //Reset curPOI;
                        curPOI = null;
                    }
                    
                    return 'f';
                } else {

                    orient = '^';
                    return 'r';
                }
            } else {

                //If it is to our left
                //Now we check our orientation and move appropriately
                if (orient == '^') {
    
                    orient = '<';
                    return 'l';
                } else if (orient == 'v') {

                    orient = '<';
                    return 'r';
                } else if (orient == '>') {

                    orient = 'v';
                    return 'r';
                } else {

                    c_x--;

                    //Check if destination is right infront, then we
                    //need to update objectives
                    if (c_x == x) {
                        curObj = 0;
                        //Update item counts
                        if (curPOI.type == 'a') axe++;
                        if (curPOI.type == 'o') stones++;
                        if (curPOI.type == 'k') keys++;
                        curPOI.interacted = true;
                        //Reset curPOI;
                        curPOI = null;
                    }
                    
                    return 'f';
                }
            }
        } else {
            //If we aren't directly in line with the destination on an axis
            //We attempt to move closer to it.

            //This part uses


            System.out.println("I shouldn't be here :(");
            System.exit(0);
            return 'f';
        }
    }

    
     * Given a set of goal agent view co-ordinates, finds the quickest way to get there
     * Also updates picking up specific items
     */
    private char goDestination(char view[][], int x, int y) {

        //Depending on where the dest is we orientate or go forward

        //First we check if the destination is in front of us
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

        try { // scan 5-by-5 window around current location
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
                //agent.print_view(view); // COMMENT THIS OUT BEFORE SUBMISSION
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
