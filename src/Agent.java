
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

    public char get_action(char view[][]) {

        /**
         * Strategy
         * 
         * 1. Look around to pick up items and record points of interests, the AI also maps out its own grid
         *    where the starting location is 0,0 and this is updated as simulation continues
         */
        
        map.addMap(view, orient, c_x, c_y);
        System.out.println(c_x + " , " + c_y);
        map.printMap();

        // Pick up the key otherwise randomly wander
        if (keys == 0) {

            boolean keyFound = false;
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (view[i][j] == 'k') {
                        return goDestination(view, i, j);
                    }
                }
            }
        }
        
        System.exit(0);
        return 'f';
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
