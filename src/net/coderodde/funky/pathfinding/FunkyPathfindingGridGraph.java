package net.coderodde.funky.pathfinding;

import static net.coderodde.funky.pathfinding.Utils.checkHeight;
import static net.coderodde.funky.pathfinding.Utils.checkWidth;

public final class FunkyPathfindingGridGraph {

    private final boolean[][] grid;
    
    public FunkyPathfindingGridGraph(int width, int height) {
        checkWidth(width);
        checkHeight(height);
        this.grid = new boolean[height][width];
    }
}
