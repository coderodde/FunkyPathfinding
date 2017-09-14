package net.coderodde.funky.pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static net.coderodde.funky.pathfinding.Configuration.THREAD_SLEEP_MILLISECONDS;

public abstract class AbstractPathfinder {

    protected int expandedNodeCount = 0;
    protected FunkyPathfindingPanel panel;
    protected volatile boolean exit = false;
    protected volatile boolean pause = false;
    
    public void setPanel(FunkyPathfindingPanel panel) {
        this.panel = panel;
    }
    
    public abstract List<Point> search(Point source, Point target);
    
    public void togglePause() {
        pause = !pause;
    }
    
    public void requestExit() {
        exit = true;
    }
    
    public int getNumberOfExpandedNodes() {
        return expandedNodeCount;
    }
    
    protected List<Point> tracebackPath(Point targetPoint,
                                        Map<Point, Point> parents) {
        List<Point> path = new ArrayList<>();
        Point currentPoint = targetPoint;
        
        while (currentPoint != null) {
            path.add(currentPoint);
            currentPoint = parents.get(currentPoint);
        }
        
        return path;
    }
    
    protected void sleep() {
        Utils.sleep(THREAD_SLEEP_MILLISECONDS);
    }
}
