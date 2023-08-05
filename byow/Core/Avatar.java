package byow.Core;

public class Avatar {
    private int x;
    private int y;

    public Avatar(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void move(int direction) {
        switch(direction) {
            case 0:
                //Up
                this.y++;
                break;
            case 1:
                //Down
                this.y--;
                break;
            case 2:
                //Left
                this.x--;
                break;
            case 3:
                //Right
                this.x++;
                break;
        }

    }





}
