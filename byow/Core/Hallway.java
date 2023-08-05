package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import java.util.Random;
import byow.TileEngine.Tileset;

public class Hallway {
    private Room first;
    private Room second;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    private int width;

    public Hallway(Room first, Room second, int width) {
        this.first = first;
        this.second = second;
        this.width = width;
    }
    public Hallway(int startX, int startY, int endX, int endY, int width) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public Room getFirst() {
        return first;
    }

    public Room getSecond() {
        return second;
    }

    public void setFirst(Room first) {
        this.first = first;
    }
    public void setSecond(Room second) {
        this.second = second;
    }
}
