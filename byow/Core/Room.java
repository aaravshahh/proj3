package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import java.util.Random;
import byow.TileEngine.Tileset;


public class Room {
    private int top;
    private int bottom;
    private int left;
    private int right;

    public Room(int top, int bottom, int left, int right) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getTop() {
        return top;
    }

    public TETile[][] addRoom(TETile[][] tiles) {
        return tiles;
    }
    public void incRoom(int direction) {
        switch(direction) {
            case 0:
                //Up
                this.top ++;
                break;
            case 1:
                //Down
                this.bottom --;
                break;
            case 2:
                //Left
                this.left --;
                break;
            case 3:
                //Right
                this.right ++;
                break;
        }
    }

}
