package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        WorldGenerator world = new WorldGenerator();
        ter.initialize(WIDTH, HEIGHT+5);
        drawMainMenu();

        //Start Music
        //Sound.RunMusic("/Users/aaravshah/Downloads/can-you-hear-the-music.wav");
        boolean inMainMenu = true;
        Long seed = 298218L;
        while (inMainMenu) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if(c == 'n' || c == 'N') {
                    seed = promptForSeed();
                    System.out.println(seed);
                    inMainMenu = false;
                }

            }
        }

        StdDraw.clear(Color.BLACK);
        Font f = new JLabel().getFont();
        StdDraw.setFont(f);
        TETile[][] tiles = world.inputString(seed);
        ter.renderFrame(tiles);
        boolean gameOver = false;
        while(!gameOver) {
            if(StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                tiles = world.playGame(c);
                StdDraw.text(WIDTH / 2, HEIGHT+2, "POOPENHEIMER: THE GAME");
                StdDraw.show();
                ter.renderFrame(tiles);
                StdDraw.text(WIDTH / 2, HEIGHT / 2 + 3, "POOPENHEIMER: THE GAME");
                StdDraw.show();
                if(world.player.getX()==world.toilet.getX() && world.toilet.getY() == world.player.getY()) {
                    gameOver = true;
                }
            }
        }
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 3, "YOU HAVE BECOME DEATH DESTROYER OF TOILETS");
        StdDraw.show();


    }
    public void drawMainMenu() {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 3, "POOPENHEIMER: THE GAME");

        Font fontSmall = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH/2, HEIGHT/2, "New Game (N)");
        StdDraw.text(WIDTH/2, HEIGHT/2 - 2, "Load Game (L)");
        StdDraw.text(WIDTH/2, HEIGHT/2 - 4, "Quit Game (Q)");
        StdDraw.show();
    }
    public Long promptForSeed() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 3, "Please enter a seed Below");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, "Press S to confirm");
        StdDraw.show();
        boolean flag = true;
        String s="";
        while(flag) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if(Character.isDigit(c)) {
                    s+=c;
                } else if(c == 'S') {
                    flag = false;
                }
                if (!s.equals("")) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(fontBig);
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 + 3, "Please enter a seed Below");
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, "Press S to confirm");
                    StdDraw.text(WIDTH/2, HEIGHT/2 -1, s);
                    StdDraw.show();
                }
            }
        }
        return Long.parseLong(s);
        //69 doesn't work


    }







    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        Long seed = Long.parseLong(input.substring(1, input.length() - 1));
        WorldGenerator world = new WorldGenerator();
        TETile[][] finalWorldFrame = world.inputString(seed);
        return finalWorldFrame;
    }
    public static void main(String[] args) {
        new Engine().interactWithKeyboard();
    }
}
