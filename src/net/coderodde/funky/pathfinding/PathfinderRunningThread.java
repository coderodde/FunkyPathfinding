package net.coderodde.funky.pathfinding;

import java.awt.Point;

public final class PathfinderRunningThread extends Thread {

    private final AbstractPathfinder pathfinder;
    private final Point sourcePoint;
    private final Point targetPoint;
    
    public PathfinderRunningThread(AbstractPathfinder pathfinder,
                                   Point sourcePoint,
                                   Point targetPoint) {
        this.pathfinder = pathfinder;
        this.sourcePoint = sourcePoint;
        this.targetPoint = targetPoint;
    }
    
    @Override
    public void run() {
        pathfinder.search(sourcePoint, targetPoint);
    }
    
    public AbstractPathfinder getPathfinder() {
        return pathfinder;
    }
    
    public void togglePause() {
        pathfinder.togglePause();
    }
    
    public void halt() {
        pathfinder.requestExit();
    }
}
