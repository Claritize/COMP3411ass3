/**
 * Map being constructed by AI as it is moving around
 */

import java.util.*;

public class Map {

    //Made to be 160x160 big so if agent starts on any corner, still large enough to fit entire map
    //Agent starts at grid [80,80] by default
    private char map[][];

    public Map() {

        map = new char[162][162];
        for (int i = 0; i < 162; i++) {
            Arrays.fill(map[i], '*');
        }
    }

    /**
     * Expand the map by giving a view, current coordinates, and current orientation
     */
    public void addMap(char view[][], char orient, int x, int y) {

        //Convert co-ordinates to map's scope
        int c_x = 80 + x;
        int c_y = 80 + y;

        //Different subroutines depending on orientation
        if (orient == '^') {
System.out.println("lol");
            //Writing to map
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    map[c_y+i-2][c_x+j-2] = view[i][j];
                }
            }
        }
    }

    public void printMap() {
        
        for (int i = 0; i < 162; i++) {
            for (int j = 0; j < 162; j++) {
                System.out.print(map[i][j]);
            }
        }
    }

}