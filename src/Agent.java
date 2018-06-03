
/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411/9414/9814 Artificial Intelligence
 *  UNSW Session 1, 2018
*/

import java.util.*;
import java.io.*;
import java.net.*;

        /**
         * Summary:
         * 
         * I decided to approach this assignment in a way to try and mimic how I would play it if I was the AI,
         * this led me to write a strategy below which I followed throughout the programming of the AI. 
         *  
         * The main data structure I used was the Map, which was a recording of all of the areas the AI has dicovered.
         * This data structure was crucial for all the smart algorithms used so that it could do more than just randomly
         * roam around.
         * 
         * The algorithms I used to accomplish the strategy included A* search and floodfill. I modified the flood
         * fill algorithm to do a floodsearch instead, which was very easy to implement for exploring unexplored areas.
         * I used two versions of A* with difference costs for navigating. 3 were used for onland navigation to items
         * and points of interest, and one was used to navigate over water. The reason I did this was to streamline
         * the amount of processing required, restricting what the A* could actually search for.
         * 
         * I also used POI (point of interest) and State data struttures to help me keep track of what items I had
         * interated with, as well as to allow me to create paths for the agent to traverse. 
         * 
         * Ultimately, the agent will have a predefined set of rules made using if/else statements which call the appropriate
         * actions to do the best action in that particular scenario.
         * 
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
        
public class Agent {

    public boolean raft = false;
    public int stones = 0;
    public int axe = 0;
    public int keys = 0;

    //Agent states
    public boolean on_water = false;
    public boolean on_raft = false;
    public boolean on_rock = false;

    public int c_y = 0;
    public int c_x = 0;

    //Orientation
    public char orient = 'v';

    //Map object
    Map map = new Map();

    //List of POI
    public ArrayList<POI> pois = new ArrayList<POI>();
    public ArrayList<POI> grabs = new ArrayList<POI>();

    /**
     * Current objective
     * curObj  = 0: explore
     *         = 1: grab
     *         = 2: unlock
     *         = 3: chop
     *         = 4: destination
     *         = 5: sea explore
     * curPOI = co-ords to POI
     */

    public int curObj = 0;
    public POI curPOI = null;
    public int grabsComplete = 0;
    public boolean haveGold = false;

    //Used for more advanced travelling
    public State currentState = null;
    public int stateMove = 0;

    public int time = 0;

    public boolean found_treasure = false;

    public char get_action(char view[][]) {

        map.addMap(view, orient, c_x, c_y);
        System.out.println("current_orient = " + orient);
        //map.printMap();
        System.out.println("AgentPOS = " + c_x + "," + c_y);
        System.out.println("axes = " + axe + " keys = " + keys + " raft = " + raft + " stones = " + stones);
        System.out.println("on water = " + on_water + " on rock = " + on_rock + " on raft = " + on_raft);

        //Scan the view and add POIs
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (view[i][j] == 'T' ||
                    view[i][j] == '-' ||
                    view[i][j] == 'a' ||
                    view[i][j] == 'k' ||
                    view[i][j] == 'o' ||
                    view[i][j] == 'O' ||
                    view[i][j] == 'g' ||
                    view[i][j] == '$') addPOI(view[i][j], j, i);

                //If we find the trasure make found_treasure true
                if (view[i][j] == '$') found_treasure = true;
            }
        }

        System.out.println("curobj= " + curObj + " grabs=" + grabs.size() + " POIs=" + pois.size());
        if (curObj == 1) {
            System.out.println("getting " + curPOI.type + " xy: " + curPOI.x + "," + curPOI.y + "," + curPOI.interacted);
        }
        printPOI();
        System.out.println("grabs gotten = " + grabsComplete);

        //Check if we are at the explored location, this is only required for sea exploration since it works a bit differently
        //to normal exploration

        //If we have the gold jsut go back to starting place
        if (haveGold) {

            //if we right near start
            if ((c_x == 0 && c_y == 1) ||
                (c_x == 0 && c_y == -1) ||
                (c_x == 1 && c_y == 0) ||
                (c_x == -1 && c_y == 0)) {

                //In this case we will just try get to it as a poi;
                curPOI = new POI(' ', 0, 0);
                curObj = 1;
            } else {
                State s = map.SmarterAStarTravel(0, 0, c_x, c_y, this, false);
                currentState = s;
                stateMove = 0;
                curPOI = new POI(' ', 0, 0);
                curObj = 1;                
            }
        }
        
        if (curPOI != null && curObj == 5)
            if (map.explored(curPOI.x, curPOI.y)) {
                curObj = 0;
                curPOI = null;

                //If we are on a raft we want to try explore on the water as much as we can before disembarking
                if (on_water && on_raft) {

                    //Call the water flood search
                    curPOI = map.floodSearchW(c_x, c_y);

                    //If we find more water to explore
                    if (curPOI != null) {
                        curPOI.type = '~';
                        curObj = 5;
                    }
                }
            }

        //If we have no current objective, pop grabable POIs off list and get them
        if (curObj == 0) {

            if (grabsComplete < grabs.size()) {
                
                //If we can't get treasure of there is none found right now
                if (curObj == 0) {
                    //Look for another POI to get
                    for (POI p : grabs) {

                        if (!p.interacted && p.type != '$') {

                            //If not then we check if we can traverse there
                            int waters = map.checkTraversable(p.x, p.y, c_x, c_y, true);
                            
                            //If we find a path
                            if (waters == 0) {
                                curPOI = p;
                                curObj = 1;
                                break;
                            }
                        }
                    }
                }

            } 
            
            //If we couldn't find a grabable
            if (curObj == 0) {

                //Check if our current POI has been explored
                if (curPOI != null) {
    
                    //If the current POI still hasn't been explored yet keep on the same path
                    if (map.map[80-curPOI.y][curPOI.x+80] != '=') {
                        curPOI = map.floodSearch(c_x, c_y, false);
                        curObj = 0;
                    }
                } else {

                    //Otherwisew try find a new land traversal
                    curPOI = map.floodSearch(c_x, c_y, false);
                    curObj = 0;
                }                

                //Check if it's actually traversable
                if (curPOI != null) if (map.checkTraversable(curPOI.x, curPOI.y, c_x, c_y, false) == -1) curPOI = null;

                //If curPOI is returned as NULL, that means we have explored everything
                //We analyse our items and decide what to do
                if (curPOI == null) {

                    System.out.println("making a smart move");

                    //Our first order is always to open a door first if we have keys
                    if (keys > 0) {

                        //If we have a key then we look for any doors we can traverse to
                        for (POI p : pois) {

                            //Check if the poi has been interacted
                            if (!p.interacted && p.type == '-') {
                                
                                //If not then we check if we can traverse there
                                int waters = map.checkTraversableD(p.x, p.y, c_x, c_y);
                                System.out.println(waters);
                                
                                //If we find a path
                                if (waters != -1) {

                                    //Set current POI to this location
                                    curPOI = p;
                                    curObj = 2;
                                    currentState = null;
                                    stateMove = 0;

                                    break;
                                }
                            }
                        }
                    } 

                    if (curPOI == null && axe > 0) {

                        //If we have an axe look for a tree to cut down
                        for (POI p : pois) {

                            //Check if the poi has been interacted
                            if (!p.interacted && p.type == 'T') {
                                
                                //If not then we check if we can traverse there
                                int waters = map.checkTraversableT(p.x, p.y, c_x, c_y);
                                System.out.println("Tree:" + waters);
                                //If we find a path
                                if (waters != -1) {

                                    //Set current POI to this location
                                    curPOI = p;
                                    curObj = 3;
                                    currentState = null;
                                    stateMove = 0;

                                    break;
                                }
                            }
                        }
                    } 
                                        
                    //If we get to this point, it means that there are no easy grabbables (on land no water traversle)
                    //And no more water exploration
                    //Our strategy is to check out of all the grabables, we calculate an associated cost, and whichever one
                    //has the least cost will be our next item to grab
                    if (curObj == 0) {

                        System.out.println("Activating SMART TRAVEL");

                        State bestState = null;
                        POI bestPoi = null;

                        for (POI p : grabs) {

                            if (!p.interacted && p.type != '$') {

                                State s = map.SmarterAStarTravel(p.x, p.y, c_x, c_y, this, false);
                                
                                //If we can find a successful traversal to the goal
                                if (s != null) {
                                    
                                    if (bestState == null) {
                                        bestState = s;
                                        bestPoi = p;
                                    } else {

                                        //Compare the current bestPoi to the new one
                                        if (s.cost < bestState.cost) {
                                            bestState = s;
                                            bestPoi = p;
                                        }
                                    }
                                }
                            }
                        }

                        //Now we check if we ended up finding something valid to traverse to
                        if (bestState != null) {

                            currentState = bestState;
                            stateMove = 0;
                            curPOI = bestPoi;
                            curObj = 1;
                        }
                    }
                                        
                    //Same as above but for interactables
                    if (curObj == 0) {

                        System.out.println("Activating SMART TRAVEL");

                        State bestState = null;
                        POI bestPoi = null;

                        for (POI p : pois) {

                            if (!p.interacted) {

                                //Check if we have required items for it
                                if (p.type == '-' && keys > 0) {
                                    State s = map.SmarterAStarTravel(p.x, p.y, c_x, c_y, this, true);
                                    
                                    //If we can find a successful traversal to the goal
                                    if (s != null) {
                                        
                                        if (bestState == null) {
                                            bestState = s;
                                            bestPoi = p;
                                        } else {

                                            //Compare the current bestPoi to the new one
                                            if (s.cost < bestState.cost) {
                                                bestState = s;
                                                bestPoi = p;
                                            }
                                        }
                                    }
                                }

                                //Check if we have required items for it
                                if (p.type == 'T' && axe > 0) {
                                    State s = map.SmarterAStarTravel(p.x, p.y, c_x, c_y, this, true);
                                    
                                    //If we can find a successful traversal to the goal
                                    if (s != null) {
                                        
                                        if (bestState == null) {
                                            bestState = s;
                                            bestPoi = p;
                                        } else {

                                            //Compare the current bestPoi to the new one
                                            if (s.cost < bestState.cost) {
                                                bestState = s;
                                                bestPoi = p;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        //Now we check if we ended up finding something valid to traverse to
                        if (bestState != null) {

                            currentState = bestState;
                            stateMove = 0;
                            curPOI = bestPoi;
                            if (curPOI.type == 'T') curObj = 3;
                            else curObj = 2;
                        }
                    }
                    
                    if (curObj == 0) {

                        //If we get here it means we have explored all possible land and got every item we can get to :')
                        //Oh my god rankini it is 5am and it's almost donnneneeeeeeeeeeeeeeeeee hentaihavennnn

                        //Now we look towards exploring the sea
                        //This feels like unlocking a new area in an RPG holy crap it feels good

                        //Traversing bodies of water is difficult, so we must use resources carefully,

                        //If we are on a raft we want to try explore on the water as much as we can before disembarking
                        if (on_water && on_raft) {

                            //Call the water flood search
                            curPOI = map.floodSearchW(c_x, c_y);

                            //If we find more water to exploire
                            if (curPOI != null) {
                                curPOI.type = '~';
                                curObj = 5;
                                System.out.println("Explrong waaatterrr");
                            }
                        }
                        
                        //If the current objective is still 0
                        if (curObj == 0) {
                            curPOI = map.floodSearch(c_x, c_y, true);

                            //If we find a body of water to cross
                            if (curPOI != null) {
                                curPOI.type = '~';
                                curObj = 5;
                            }
                        }

                        //If we found treasure we try get that
                        if (found_treasure && curObj == 0) {

                            //Get treassure off the pois list
                            for (POI p : grabs) {

                                if (p.type == '$') {

                                    //Now we chec if it's traversable
                                    //If not then we check if we can traverse there
                                    State s = map.SmarterAStarTravel(p.x, p.y, c_x, c_y, this, false);

                                    currentState = s;
                                    stateMove = 0;
                                    curPOI = p;
                                    curObj = 1;
                                    break;
                                    
                                }
                            }
                        }
                    }

                } else {


                }
            }
        }

        System.out.println("current obj = " + curObj);

        time++;
        //We move to our current objective
        if (time < 2000) {

            //Get gold if it's gold
            if (view[1][2] == '$') {

                System.out.println("getting goldddddddddddddddddddddddddddddddddddddddddddddddd");

                //Update the map
                map.demolishPOI(curPOI.x, curPOI.y);

                haveGold = true;

                if (orient == '^') c_y++;
                if (orient == 'v') c_y--;
                if (orient == '>') c_x++;
                if (orient == '<') c_x--;

                curPOI.interacted = true;
                curPOI = null;
                curObj = 0;
                keys--;

                return 'f';
            }

            //If current objective is to unlock a door and we are facing the door
            if (curObj == 2 && view[1][2] == '-') {

                System.out.println("opening door");

                //Update the map
                map.demolishPOI(curPOI.x, curPOI.y);

                curPOI.interacted = true;
                curPOI = null;
                curObj = 0;
                keys--;

                //Unlock the door
                return 'u';
            }

            //If current objective is to cut a tree and we are facing the tree
            if (curObj == 3 && view[1][2] == 'T') {

                System.out.println("cutting down tree");

                //Update the map
                map.demolishPOI(curPOI.x, curPOI.y);

                curPOI.interacted = true;
                curPOI = null;
                curObj = 0;
                raft = true;

                //Unlock the door
                return 'c';
            }

            //We pass a type in so that it get's ignored by the A* search as a boundary
            char travelDir ;
            
            //If currentState is set, that means we have a calculate path to travel on
            if (currentState != null) {
                travelDir = currentState.moves.get(stateMove);
            }
            //If this isn't water travel
            else if (curObj != 5) travelDir = map.AStarTravel(curPOI.x, curPOI.y, c_x, c_y, curPOI.type);
            //If it is
            else travelDir = map.AStarTravelW(curPOI.x, curPOI.y, c_x, c_y, on_water);

            System.out.println("direction: " + travelDir);
            if (orient == travelDir) {

                //If current state is set we need to increment stateMove
                if (currentState != null) stateMove++;

                if (orient == '^') {
                    c_y++;
                } else if (orient == 'v') {
                    c_y--;
                } else if (orient == '>') {
                    c_x++;
                } else {
                    c_x--;
                }

                //Agent state changes
                //If we are on and and about to embark on water
                if (view[1][2] == '~') {

                    //Set water state
                    on_water = true;

                    //If we aren't on a raft or stone currently we need to use one
                    if (!on_rock && !on_raft) {

                        //If we have stones then we use those
                        if (stones > 0) {

                            on_rock = true;
                            stones--;
                        } else {

                            //Otherwise we need to use a raft
                            on_raft = true;
                            raft = false;
                        }
                    }
                
                //Otherwise if we are on a rock right now and we are going to traverse onto land
                } else if (on_rock && on_water) {

                    if (view[1][2] == ' ') {
                        //Set water state
                        on_water = false;
                        on_rock = false;
                    } else if (view[1][2] == '~') {

                        //If we are going to continue traversing on water we must either use a rock or use a raft
                        //If we have stones then we use those
                        if (stones > 0) {

                            on_rock = true;
                            stones--;
                        } else {

                            on_rock = false;
                            //Otherwise we need to use a raft
                            on_raft = true;
                            raft = false;
                        }
                    }

                //Otherwise if we are on a raft right now and we are going to traverse onto land
                } else if (on_raft && on_water) {

                    if (view[1][2] == ' ') {
                        //Set water state
                        on_water = false;
                        on_raft = false;
                    }
                } else if (view[1][2] == '~') {

                    //If we are on a raft we just keep traversing on the raft
                    on_raft = true;
                    
                }


                //We need to check if we would get an item when moved
                if (view[1][2] == 'k') {

                    keys++;
                    //We need to check if the object we are picking up is our POI
                    if (c_x == curPOI.x && c_y == curPOI.y) {
                    
                        //Reset current state if it's set
                        if (currentState != null) currentState = null;
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
                if (view[1][2] == 'o') {

                    stones++;
                    //We need to check if the object we are picking up is our POI
                    if (c_x == curPOI.x && c_y == curPOI.y) {
                                        
                        //Reset current state if it's set
                        if (currentState != null) currentState = null;
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
                if (view[1][2] == 'a') {

                    axe++;
                    //We need to check if the object we are picking up is our POI
                    if (c_x == curPOI.x && c_y == curPOI.y) {
                    
                        //Reset current state if it's set
                        if (currentState != null) currentState = null;
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
        //System.out.println("uh" + curPOI.x + "," + curPOI.y);
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
            type == 'g' ||
            type == '$') {

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
    private char travelDest(int x, int y) {
        
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

    /**
     * Given a set of goal agent view co-ordinates, finds the quicket way to get there
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
