package net.coderodde.funky.pathfinding;

import java.awt.Point;

public final class DijkstraPathfinder extends AStarTemplatePathfinder {

    public DijkstraPathfinder() {
        super((Point p1, Point p2) -> { return 0.0; });
    }
}
