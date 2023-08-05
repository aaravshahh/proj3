package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.Random;
import byow.TileEngine.Tileset;
import javassist.bytecode.stackmap.TypeData;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;

/**
 * @Source: Used a similar world generation algorithm as Sean Lin found here: https://seanlin.dev/2021/01/16/build-your-own-world/
 */
public class WorldGenerator {
    public static final int WIDTH = 70;
    public static final int HEIGHT = 30;
    public TETile[][] tiles = new TETile[WIDTH][HEIGHT];
    public int numRooms = 0;
    public Random RANDOM;
    public ArrayList<Room> roomsList = new ArrayList<Room>();
    public ArrayList<Hallway> hallsList = new ArrayList<Hallway>();
    public UnionFind dis;
    public Avatar player;
    public Avatar toilet;

    public void fillWithEmptyTiles() {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }
    private TETile randomTile(Random RANDOM) {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.NOTHING;
            default: return Tileset.NOTHING;
        }
    }
    public void generateStartSeeds() {
        int w = tiles.length;
        int h = tiles[0].length;
        int[][] seeds = new int[numRooms][2];
        int curr = 0;
        int currQuad = -1;

        while(curr < numRooms) {
            currQuad++;
            currQuad = currQuad % 4;
            int newW, newH;
            if(currQuad == 1) {
                newW = RANDOM.nextInt(1, w/2);
                newH = RANDOM.nextInt(1, h/2);
            } else if (currQuad == 2) {
                newW = RANDOM.nextInt(w/2, w-1);
                newH = RANDOM.nextInt(1, h/2);
            } else if (currQuad == 3) {
                newW = RANDOM.nextInt(1, w/2);
                newH = RANDOM.nextInt(h/2, h-1);
            } else {
                newW = RANDOM.nextInt(w/2, w-1);
                newH = RANDOM.nextInt(h/2, h-1);
            }
            boolean tooClose = false;
            for (int[] entry: seeds) {
                if(Math.abs(entry[0] - newW) < 5 && Math.abs(entry[1] - newH) < 5) {
                    tooClose = true;
                }
            }
            if (!tooClose) {
                seeds[curr][0] = newW;
                seeds[curr][1] = newH;
                curr++;
                roomsList.add(new Room(newH+1, newH-1, newW-1, newW+1));
            }
        }
        for (Room room: roomsList) {
            makeRoom(room);
        }
    }

    public void blossomRooms() {
        Boolean[] stoppedRooms = new Boolean[roomsList.size()];
        for (int i = 0; i < stoppedRooms.length; i++) {
            stoppedRooms[i] = false;
        }
        int currRoom = 0;
        int numLoops = 0;
        while (!allTrue(stoppedRooms)) {
            if(!stoppedRooms[currRoom]) {
                //GROW THE ROOM IN A RANDOM DIRECTION
                int direction = RANDOM.nextInt(4);
                if (!growRoom(roomsList.get(currRoom), direction) || RANDOM.nextInt(4) == 1) {
                    stoppedRooms[currRoom] = true;
                }
            }
            currRoom = (currRoom+1) % roomsList.size();
            if (currRoom == 0) {
                numLoops++;
            }

        }
    }
    public boolean allTrue(Boolean[] values) {
        for (Boolean i: values) {
            if(!i) {
                return false;
            }
        }
        return true;
    }
    public boolean growRoom(Room room, int direction) {
        //0 = Up
        switch(direction) {
            case 0:
                //Up
                if (room.getTop() >= HEIGHT-1) {
                    return false;
                }
                for(int i = room.getLeft(); i <= room.getRight(); i++) {
                    if (!tiles[i][room.getTop()+1].equals(Tileset.NOTHING)) {
                        return false;
                    }
                }
                break;
            case 1:
                //Down
                if (room.getBottom() <= 0) {
                    return false;
                }
                for(int i = room.getLeft(); i <= room.getRight(); i++) {
                    if (!tiles[i][room.getBottom()-1].equals(Tileset.NOTHING)) {
                        return false;
                    }
                }
                break;
            case 2:
                //Left
                if (room.getLeft() <= 0) {
                    return false;
                }
                for(int i = room.getBottom(); i <= room.getTop(); i++) {
                    if (!tiles[room.getLeft() - 1][i].equals(Tileset.NOTHING)) {
                        return false;
                    }
                }
                break;
            case 3:
                //Right
                if (room.getRight() >= WIDTH-1) {
                    return false;
                }
                for(int i = room.getBottom(); i <= room.getTop(); i++) {
                    if (!tiles[room.getRight() + 1][i].equals(Tileset.NOTHING)) {
                        return false;
                    }
                }
                break;
        }
        room.incRoom(direction);
        makeRoom(room);

        return true;

    }
    public void makeRoom(Room room) {
        for (int i = room.getLeft(); i <= room.getRight(); i++) {
            for (int j = room.getBottom(); j <= room.getTop(); j++) {
                if (i==room.getLeft() || i == room.getRight() || j == room.getBottom() || j == room.getTop()) {
                    tiles[i][j] = Tileset.WALL;
                } else {
                    tiles[i][j] = Tileset.FLOOR;
                }
            }
        }
    }
    public int locateRoom(int width, int height) {
        for (int i = 0; i < roomsList.size(); i++) {
            if(roomsList.get(i).getLeft() <= width && roomsList.get(i).getRight() >= width) {
                if(roomsList.get(i).getBottom() <= height && roomsList.get(i).getTop() >= height) {
                    return i;
                }
            }
        }
        return -1;
    }
    public ArrayList<Integer> locateHallway(int width, int height) {
        ArrayList<Integer> ans= new ArrayList<Integer>();
        for (int i = 0; i < hallsList.size(); i++) {
            Hallway hall = hallsList.get(i);
            if(hall.getStartX() == hall.getEndX()) {
                //Vertical
                if(hall.getEndX() == width && height > Math.min(hall.getStartY(), hall.getEndY()) && height < Math.max(hall.getStartY(), hall.getEndY())) {
                    ans.add(i);
                }
            } else {
                if(hall.getEndY() == height && width > Math.min(hall.getStartX(), hall.getEndX()) && width < Math.max(hall.getStartX(), hall.getEndX())) {
                    ans.add(i);
                }
            }

        }
        return ans;
    }

    public int findRoom(Room r) {
        for (int i = 0; i < roomsList.size(); i++) {
            if (roomsList.get(i).equals(r)) {
                return i;
            }
        }
        return -1;
    }


    public void horizontalHallways() {
        int numHallways = (int)((double)roomsList.size() * 0.5);
        int madeHallways = 0;
        while (madeHallways < numHallways) {
            int roomInd = RANDOM.nextInt(roomsList.size());
            Room randomRoom = roomsList.get(roomInd);
            ArrayList<int[]> poss = new ArrayList<int[]>();
            for (int j = randomRoom.getBottom() + 1; j < randomRoom.getTop(); j++) {
                for(int i = randomRoom.getRight(); i < WIDTH; i++) {
                    if (tiles[i][j].equals(Tileset.FLOOR)) {
                        //Oth index is x of left room, 1st index is y of left room
                        //2nd index is x of right room, 3rd index is y of right room
                        int[] entry = new int[]{randomRoom.getRight(), j, i-1, j};
                        poss.add(entry);
                        break;
                    }
                }
            }

            for (int j = randomRoom.getBottom() + 1; j < randomRoom.getTop(); j++) {
                for(int i = randomRoom.getLeft(); i >= 0; i--) {
                    if (tiles[i][j].equals(Tileset.FLOOR)) {
                        //Oth index is x of left room, 1st index is y of left room
                        //2nd index is x of right room, 3rd index is y of right room
                        int[] entry = new int[]{randomRoom.getLeft(), j, i+1, j};
                        poss.add(entry);
                        break;
                    }
                }
            }



            if (poss.size() == 0) {
                continue;
            }
            int[] newH = poss.get(RANDOM.nextInt(poss.size()));
            Hallway hall = new Hallway(newH[0], newH[1], newH[2], newH[3], 1);
            hall.setFirst(randomRoom);
            int secondRoomInd = locateRoom(newH[2], newH[3]);
            hall.setSecond(roomsList.get(secondRoomInd));

            for (int i=Math.min(newH[0], newH[2]); i <=Math.max(newH[0], newH[2]); i++) {
                if(tiles[i][newH[3]+1].equals(Tileset.NOTHING)) {
                    tiles[i][newH[3]+1] = Tileset.FLOWER;
                } else if(tiles[i][newH[3]+1].equals(Tileset.FLOOR)) {
                    if (locateRoom(i, newH[3]+1) >= 0) {
                        dis.union(roomInd, locateRoom(i, newH[3]+1));
                        /**
                        System.out.println("Unioned above"+ roomInd + " and " + locateRoom(i, newH[3]+1, tiles));
                        System.out.println("Num areas" + dis.numAreas());
                        System.out.println(dis);
                         **/

                    }
                }
                tiles[i][newH[3]] = Tileset.GRASS;
                if(tiles[i][newH[3]-1].equals(Tileset.NOTHING)) {
                    tiles[i][newH[3]-1] = Tileset.FLOWER;
                } else if(tiles[i][newH[3]+-1].equals(Tileset.FLOOR)) {
                    if (locateRoom(i, newH[3]-1) >= 0) {
                        dis.union(roomInd, locateRoom(i, newH[3]-1));
                        /**
                        System.out.println("Unioned below"+ roomInd + " and " + locateRoom(i, newH[3]-1, tiles));
                        System.out.println("Num areas" + dis.numAreas());
                        System.out.println(dis);
                        **/

                    }
                }
            }
            madeHallways++;
            hallsList.add(hall);
            dis.union(roomInd, secondRoomInd);
            /**
             System.out.println("Unioned at end"+ roomInd + " and " + secondRoomInd);
             System.out.println("Num areas" + dis.numAreas());
             System.out.println(dis);

             if (madeHallways > 12) {
             return;
             }
             **/




        }
    }
    public void verticalHallways() {
        int numHallways = (int)((double)roomsList.size() * 0.5);
        int madeHallways = 0;
        while (madeHallways < numHallways) {
            int roomInd = RANDOM.nextInt(roomsList.size());
            Room randomRoom = roomsList.get(roomInd);
            ArrayList<int[]> poss = new ArrayList<int[]>();
            for (int i = randomRoom.getLeft() + 1; i < randomRoom.getRight(); i++) {
                for(int j = randomRoom.getTop(); j < HEIGHT; j++) {
                    if (tiles[i][j].equals(Tileset.FLOOR)) {
                        //Oth index is x of left room, 1st index is y of left room
                        //2nd index is x of right room, 3rd index is y of right room
                        int[] entry = new int[]{i, randomRoom.getTop(), i, j-1};
                        poss.add(entry);
                        break;
                    }
                }
            }

            for (int i = randomRoom.getLeft() + 1; i < randomRoom.getRight(); i++) {
                for(int j = randomRoom.getBottom(); j >= 0; j--) {
                    if (tiles[i][j].equals(Tileset.FLOOR)) {
                        //Oth index is x of left room, 1st index is y of left room
                        //2nd index is x of right room, 3rd index is y of right room
                        int[] entry = new int[]{i, randomRoom.getBottom(), i, j+1};
                        poss.add(entry);
                        break;
                    }
                }
            }

            if (poss.size() == 0) {
                continue;
            }
            int[] newH = poss.get(RANDOM.nextInt(poss.size()));

            Hallway hall = new Hallway(newH[0], newH[1], newH[2], newH[3], 1);
            hall.setFirst(randomRoom);
            int secondRoomInd = locateRoom(newH[2], newH[3]);
            hall.setSecond(roomsList.get(secondRoomInd));
            for (int j = Math.min(newH[1], newH[3]); j <= Math.max(newH[1], newH[3]); j++) {
                if(tiles[newH[0]-1][j].equals(Tileset.NOTHING)) {
                    tiles[newH[0]-1][j] = Tileset.FLOWER;
                } else if(tiles[newH[0]-1][j].equals(Tileset.FLOOR)) {
                    if(locateRoom(newH[0]-1, j) >= 0) {
                        dis.union(roomInd, locateRoom(newH[0]-1, j));
                    }
                }
                if (tiles[newH[0]][j].equals(Tileset.GRASS)) {
                    ArrayList<Integer> ans = locateHallway(newH[0], j);
                    while(ans.size() > 0) {
                        int ind = ans.get(0);
                        Hallway oldH = hallsList.get(ind);
                        dis.union(findRoom(oldH.getFirst()), roomInd);
                        ans.remove(0);
                    }
                }



                tiles[newH[0]][j] = Tileset.GRASS;
                if(tiles[newH[0]+1][j].equals(Tileset.NOTHING)) {
                    tiles[newH[0]+1][j] = Tileset.FLOWER;
                } else if(tiles[newH[0]+1][j].equals(Tileset.FLOOR)) {
                    if(locateRoom(newH[0]+1, j) >= 0) {
                        dis.union(roomInd, locateRoom(newH[0]+1, j));
                    }
                }
            }
            madeHallways++;
            hallsList.add(hall);
            dis.union(roomInd, secondRoomInd);

            /**
             * System.out.println("Unioned at end "+ roomInd + " and " + secondRoomInd);
             *             System.out.println("Num areas " + dis.numAreas());
             *             System.out.println(dis);
             *             if (madeHallways > 3) {
             *             return;
             *             }
             **/


        }
    }
    public void finalizeHallways() {
        boolean flag = true;
        int count = 0;
        while(flag) {
            int smal = dis.smallestRoot();
            //System.out.println(smal);
            Room randomRoom = roomsList.get(smal);
            //Up
            ArrayList<int[]> poss = new ArrayList<int[]>();
            for (int i = randomRoom.getLeft() + 1; i < randomRoom.getRight(); i++) {
                for(int j = randomRoom.getTop(); j < HEIGHT; j++) {
                    if (tiles[i][j].equals(Tileset.FLOOR)) {
                        //Oth index is x of left room, 1st index is y of left room
                        //2nd index is x of right room, 3rd index is y of right room
                        int[] entry = new int[]{i, randomRoom.getTop(), i, j-1};
                        poss.add(entry);
                        break;
                    }
                }
            }
            //Down
            for (int i = randomRoom.getLeft() + 1; i < randomRoom.getRight(); i++) {
                for(int j = randomRoom.getBottom(); j >= 0; j--) {
                    if (tiles[i][j].equals(Tileset.FLOOR)) {
                        //Oth index is x of left room, 1st index is y of left room
                        //2nd index is x of right room, 3rd index is y of right room
                        int[] entry = new int[]{i, randomRoom.getBottom(), i, j+1};
                        poss.add(entry);
                        break;
                    }
                }
            }
            int numVert = poss.size();
            //Right
            for (int j = randomRoom.getBottom() + 1; j < randomRoom.getTop(); j++) {
                for(int i = randomRoom.getRight(); i < WIDTH; i++) {
                    if (tiles[i][j].equals(Tileset.FLOOR)) {
                        //Oth index is x of left room, 1st index is y of left room
                        //2nd index is x of right room, 3rd index is y of right room
                        int[] entry = new int[]{randomRoom.getRight(), j, i-1, j};
                        poss.add(entry);
                        break;
                    }
                }
            }
            //Left
            for (int j = randomRoom.getBottom() + 1; j < randomRoom.getTop(); j++) {
                for (int i = randomRoom.getLeft(); i >= 0; i--) {
                    if (tiles[i][j].equals(Tileset.FLOOR)) {
                        //Oth index is x of left room, 1st index is y of left room
                        //2nd index is x of right room, 3rd index is y of right room
                        int[] entry = new int[]{randomRoom.getLeft(), j, i + 1, j};
                        poss.add(entry);
                        break;
                    }
                }
            }
            int secondRoomInd;
            for (int i =poss.size()-1; i >= 0; i--) {
                secondRoomInd = locateRoom(poss.get(i)[2], poss.get(i)[3]);
                if (dis.connected(smal, secondRoomInd)) {
                    poss.remove(i);
                    if(i < numVert) {
                        numVert--;
                    }
                }
            }
            boolean endsInRoom = true;
            if (poss.size() == 0) {
                //System.out.println("ERROR CANT CONNECT TO ROOT");
                endsInRoom = false;
                //Up
                for (int i = randomRoom.getLeft() + 1; i < randomRoom.getRight(); i++) {
                    for (int j = randomRoom.getTop(); j < HEIGHT; j++) {
                        if (tiles[i][j].equals(Tileset.GRASS)) {
                            //Oth index is x of left room, 1st index is y of left room
                            //2nd index is x of right room, 3rd index is y of right room
                            int[] entry = new int[]{i, randomRoom.getTop(), i, j};
                            poss.add(entry);
                            break;
                        }
                    }
                }
                //Down
                for (int i = randomRoom.getLeft() + 1; i < randomRoom.getRight(); i++) {
                    for(int j = randomRoom.getBottom(); j >= 0; j--) {
                        if (tiles[i][j].equals(Tileset.GRASS)) {
                            //Oth index is x of left room, 1st index is y of left room
                            //2nd index is x of right room, 3rd index is y of right room
                            int[] entry = new int[]{i, randomRoom.getBottom(), i, j};
                            poss.add(entry);
                            break;
                        }
                    }
                }
                numVert = poss.size();
                //Right
                for (int j = randomRoom.getBottom() + 1; j < randomRoom.getTop(); j++) {
                    for(int i = randomRoom.getRight(); i < WIDTH; i++) {
                        if (tiles[i][j].equals(Tileset.GRASS)) {
                            //Oth index is x of left room, 1st index is y of left room
                            //2nd index is x of right room, 3rd index is y of right room
                            int[] entry = new int[]{randomRoom.getRight(), j, i, j};
                            poss.add(entry);
                            break;
                        }
                    }
                }
                //Left
                for (int j = randomRoom.getBottom() + 1; j < randomRoom.getTop(); j++) {
                    for (int i = randomRoom.getLeft(); i >= 0; i--) {
                        if (tiles[i][j].equals(Tileset.GRASS)) {
                            //Oth index is x of left room, 1st index is y of left room
                            //2nd index is x of right room, 3rd index is y of right room
                            int[] entry = new int[]{randomRoom.getLeft(), j, i, j};
                            poss.add(entry);
                            break;
                        }
                    }
                }
            }






            int posIndex = RANDOM.nextInt(poss.size());
            int[] newH = poss.get(posIndex);

            Hallway hall = new Hallway(newH[0], newH[1], newH[2], newH[3], 1);
            hall.setFirst(randomRoom);
            secondRoomInd = locateRoom(newH[2], newH[3]);
            if (endsInRoom) {
                hall.setSecond(roomsList.get(secondRoomInd));
            }
            //System.out.println("Start x " + newH[0] + "Start y " + newH[1] + "End x " + newH[2] + "End Y " + newH[3]);




            if (posIndex >= numVert) {
                //ADD HORIZONTAL HALLWAY
                for (int i=Math.min(newH[0], newH[2]); i <= Math.max(newH[0], newH[2]); i++) {
                    if(tiles[i][newH[3]+1].equals(Tileset.NOTHING)) {
                        tiles[i][newH[3]+1] = Tileset.FLOWER;
                    } else if(tiles[i][newH[3]+1].equals(Tileset.FLOOR)) {
                        if (locateRoom(i, newH[3]+1) >= 0) {
                            dis.union(smal, locateRoom(i, newH[3]+1));
                            /**
                             System.out.println("Unioned above"+ roomInd + " and " + locateRoom(i, newH[3]+1, tiles));
                             System.out.println("Num areas" + dis.numAreas());
                             System.out.println(dis);
                             **/

                        }
                    }
                    if (!tiles[i][newH[3]].equals(Tileset.FLOOR)) {
                        tiles[i][newH[3]] = Tileset.GRASS;
                        //System.out.println("New Grass Position"+i+" "+ newH[3]);
                    }
                    if(tiles[i][newH[3]-1].equals(Tileset.NOTHING)) {
                        tiles[i][newH[3]-1] = Tileset.FLOWER;
                    } else if(tiles[i][newH[3]+-1].equals(Tileset.FLOOR)) {
                        if (locateRoom(i, newH[3]-1) >= 0) {
                            dis.union(smal, locateRoom(i, newH[3]-1));
                            /**
                             System.out.println("Unioned below"+ roomInd + " and " + locateRoom(i, newH[3]-1, tiles));
                             System.out.println("Num areas" + dis.numAreas());
                             System.out.println(dis);
                             **/

                        }
                    }
                }
                hallsList.add(hall);
                if (endsInRoom) {
                    dis.union(smal, secondRoomInd);
                    //System.out.println("Unioned at end " + smal + " and " + secondRoomInd);
                } else {
                    //System.out.println("HOWDONE");
                    for (int i: locateHallway(newH[2], newH[3]) ) {
                        Hallway secondH = hallsList.get(i);
                        //System.out.println("HOWDT");
                        Room r1 = secondH.getFirst();
                        for (int j = 0; j <roomsList.size(); j++) {
                            if (roomsList.get(j).equals(r1)) {
                                dis.union(j, smal);
                                //System.out.println("Unioned at end with Hallway " + smal + " and " + j);
                                break;
                            }
                        }
                    }

                }

//                System.out.println("Num areas " + dis.numAreas());
//                System.out.println(dis);
            } else {
                //ADD VERTICAL HALLWAY
                for (int j = Math.min(newH[1], newH[3]); j <= Math.max(newH[1], newH[3]); j++) {
                    if (tiles[newH[0] - 1][j].equals(Tileset.NOTHING)) {
                        tiles[newH[0] - 1][j] = Tileset.FLOWER;
                    } else if (tiles[newH[0] - 1][j].equals(Tileset.FLOOR)) {
                        if (locateRoom(newH[0] - 1, j) >= 0) {
                            dis.union(smal, locateRoom(newH[0] - 1, j));
                        }
                    }
                    if (tiles[newH[0]][j].equals(Tileset.GRASS)) {
                        ArrayList<Integer> ans = locateHallway(newH[0], j);
                        while (ans.size() > 0) {
                            int ind = ans.get(0);
                            Hallway oldH = hallsList.get(ind);
                            dis.union(findRoom(oldH.getFirst()), smal);
                            ans.remove(0);
                        }
                    }
                    if (!tiles[newH[0]][j].equals(Tileset.FLOOR)) {
                        tiles[newH[0]][j] = Tileset.GRASS;
                    }
                    if (tiles[newH[0] + 1][j].equals(Tileset.NOTHING)) {
                        tiles[newH[0] + 1][j] = Tileset.FLOWER;
                    } else if (tiles[newH[0] + 1][j].equals(Tileset.FLOOR)) {
                        if (locateRoom(newH[0] + 1, j) >= 0) {
                            dis.union(smal, locateRoom(newH[0] + 1, j));
                        }
                    }
                }
                hallsList.add(hall);
                if (endsInRoom) {
                    dis.union(smal, secondRoomInd);
                    //System.out.println("Unioned at end " + smal + " and " + secondRoomInd);
                } else {
                    for (int i:locateHallway(newH[2], newH[3]) ) {
                        Hallway secondH = hallsList.get(i);
                        Room r1 = secondH.getFirst();
                        for (int j = 0; j <roomsList.size(); j++) {
                            if (roomsList.get(j).equals(r1)) {
                                dis.union(j, smal);
                                //System.out.println("Unioned at end with Hallway " + smal + " and " + j);
                                break;
                            }
                        }
                    }


                }

            }
            flag = !(dis.numAreas() == 1);
//            if (count > 1) {
//                flag = false;
//            }
            //if( count > 0){
                //System.out.print("One Done");
            //}
            //count++;
        }

    }

    public void moveAvatar(int direction) {
        if(!tiles[player.getX()][player.getY()].equals(Tileset.AVATAR)) {
            throw new IllegalArgumentException("NOT IN RIGHT PLACE");
        }

        switch(direction) {
            case 0:
                //Up
                if(tiles[player.getX()][player.getY()+1].equals(Tileset.FLOOR) || tiles[player.getX()][player.getY()+1].equals(Tileset.SAND)) {
                    tiles[player.getX()][player.getY()] = Tileset.FLOOR;
                    tiles[player.getX()][player.getY()+1] = Tileset.AVATAR;
                    player.move(direction);
                }
                break;
            case 1:
                //Down
                if(tiles[player.getX()][player.getY()-1].equals(Tileset.FLOOR) || tiles[player.getX()][player.getY()-1].equals(Tileset.SAND)) {
                    tiles[player.getX()][player.getY()] = Tileset.FLOOR;
                    tiles[player.getX()][player.getY()-1] = Tileset.AVATAR;
                    player.move(direction);
                }
                break;
            case 2:
                //Left
                if(tiles[player.getX()-1][player.getY()].equals(Tileset.FLOOR) || tiles[player.getX() - 1][player.getY()].equals(Tileset.SAND)) {
                    tiles[player.getX()][player.getY()] = Tileset.FLOOR;
                    tiles[player.getX()-1][player.getY()] = Tileset.AVATAR;
                    player.move(direction);
                }
                break;
            case 3:
                //Right
                if(tiles[player.getX()+1][player.getY()].equals(Tileset.FLOOR) || tiles[player.getX()+1][player.getY()].equals(Tileset.SAND)) {
                    tiles[player.getX()][player.getY()] = Tileset.FLOOR;
                    tiles[player.getX()+1][player.getY()] = Tileset.AVATAR;
                    player.move(direction);
                }
                break;
        }
    }





    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        String input = args[1];
        TETile[][] tiles = new Engine().interactWithInputString(input);
        ter.renderFrame(tiles);

    }
    public TETile[][] inputString(long SEED) {
        RANDOM = new Random(SEED);
        //System.out.println(SEED);
        roomsList = new ArrayList<>();
        hallsList = new ArrayList<>();

        tiles = new TETile[WIDTH][HEIGHT];
        fillWithEmptyTiles();
        //Generate number of rooms
        numRooms = RANDOM.nextInt(20, 35);
        //System.out.println("Num rooms: " + numRooms);
        generateStartSeeds();
        blossomRooms();


        dis = new UnionFind(roomsList.size());

        horizontalHallways();
        verticalHallways();
        finalizeHallways();

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j].equals(Tileset.GRASS)) {
                    tiles[i][j] = Tileset.FLOOR;
                } else if (tiles[i][j].equals(Tileset.FLOWER)) {
                    tiles[i][j] = Tileset.WALL;
                }
            }
        }
        Room bottomLeftRoom = roomsList.get(0);
        for (Room r : roomsList) {
            if ((r.getLeft() + r.getBottom()) < (bottomLeftRoom.getBottom() + bottomLeftRoom.getLeft())) {
                bottomLeftRoom = r;
            }
        }
        Room topRight = roomsList.get(0);
        for (Room r : roomsList) {
            if ((r.getTop() + r.getRight()) >= (topRight.getTop() + topRight.getRight())) {
                topRight = r;
            }
        }
        player = new Avatar(bottomLeftRoom.getLeft() + 1, bottomLeftRoom.getBottom() + 1);
        tiles[player.getX()][player.getY()] = Tileset.AVATAR;

        toilet = new Avatar(topRight.getRight() - 1, topRight.getTop() - 1);
        tiles[toilet.getX()][toilet.getY()] = Tileset.SAND;











        //System.out.println("Avatar x and y "+player.getX() + " " + player.getY());
        //System.out.println(dis.numAreas());
        //ter.renderFrame(tiles);
        return tiles;
    }

    public TETile[][] playGame(Character c) {
        if(c=='W') {
            //Up
            moveAvatar(0);
        } else if(c == 'S') {
            //Down
            moveAvatar(1);
        } else if(c == 'A') {
            //Left
            moveAvatar(2);
        } else if(c == 'D') {
            //Right
            moveAvatar(3);
        }
        return tiles;

    }







}
