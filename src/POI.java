/**
 * Stores an instance of a point of interest on the map
 */

public class POI {

    public boolean interacted = false;
    public char type; //Default letter for POI on the map
    public int x; //x co-ordinate of POI
    public int y; //y co-ordinate of POI

    public POI(char type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }
}