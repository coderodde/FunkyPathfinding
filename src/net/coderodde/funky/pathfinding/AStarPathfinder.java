package net.coderodde.funky.pathfinding;

import java.awt.Point;

public final class AStarPathfinder extends AStarTemplatePathfinder {
    
    public AStarPathfinder() {
        super((Point p1, Point p2) -> { return p1.distance(p2); });
    }
}
