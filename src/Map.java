/**
 * Map being constructed by AI as it is moving around
 */

import java.util.*;

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

        for (int i = 50; i < 110; i++) {
            for (int j = 50; j < 110; j++) {
                System.out.print(mapCopy[i][j]);
            }
            System.out.print('\n');
        }

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
        
        for (int i = 50; i < 110; i++) {
            for (int j = 50; j < 110; j++) {
                System.out.print(map[i][j]);
            }
            System.out.print('\n');
        }
    }

}