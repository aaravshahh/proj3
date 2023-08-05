package byow.Core;

public class UnionFind {

    private int[] ds;

    /* Creates a UnionFind data structure holding N items. Initially, all
       items are in disjoint sets. */
    public UnionFind(int N) {
        ds = new int[N];
        for (int i = 0; i < N; i++) {
            ds[i] = -1;
        }
    }

    /* Returns the size of the set V belongs to. */
    public int sizeOf(int v) {
        int root = find(v);

        return Math.abs(ds[root]);
    }

    /* Returns the parent of V. If V is the root of a tree, returns the
       negative size of the tree for which V is the root. */
    public int parent(int v) {

        return ds[v];
    }

    /* Returns true if nodes V1 and V2 are connected. */
    public boolean connected(int v1, int v2) {
        if (find(v1) == find(v2)) {
            return true;
        } else {
            return false;
        }
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. If invalid items are passed into this
       function, throw an IllegalArgumentException. */
    public int find(int v) {
        if (v < 0 || v > ds.length-1) {
            throw new IllegalArgumentException();
        }
        if (ds[v] < 0) {
            return v;
        }

        return find(parent(v));
    }

    /* Connects two items V1 and V2 together by connecting their respective
       sets. V1 and V2 can be any element, and a union-by-size heuristic is
       used. If the sizes of the sets are equal, tie break by connecting V1's
       root to V2's root. Union-ing a item with itself or items that are
       already connected should not change the structure. */
    public void union(int v1, int v2) {
        if (v1 - v2 == 16 || v1-v2 == -16) {
            System.out.print("");
        }
        if (connected(v1,v2)) {
            return;
        }
        if (sizeOf(v1) <= sizeOf(v2)) {
            int root = find(v2);
            ds[root]-=sizeOf(v1);
            ds[find(v1)] = v2;
        } else if (sizeOf(v1) > sizeOf(v2)) {
            int root = find(v1);
            ds[root]-=sizeOf(v2);
            ds[find(v2)] = v1;
        }

    }

    public int numAreas() {
        int count = 0;
        for (int i: ds) {
            if(i<0) {
                count++;
            }
        }
        return count;
    }
    public int smallestRoot() {
        int smallRoot = 0;
        for (int i=0; i < ds.length; i++) {
            if(ds[i] < 0 && ds[i] > ds[smallRoot]) {
                smallRoot = i;
            } else if(ds[smallRoot] >= 0) {
                smallRoot = i;
            }
        }
        return smallRoot;
    }


    public String toString() {
        String s = "";
        for (int i: ds) {
            s+=i;
            s+=" ";
        }
        return s;
    }

}
