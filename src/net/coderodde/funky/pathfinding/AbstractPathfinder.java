package net.coderodde.funky.pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static net.coderodde.funky.pathfinding.Configuration.THREAD_SLEEP_MILLISECONDS;

public abstract class AbstractPathfinder {

    protected int frontierNodeCount = 0;
    protected int closedNodeCount = 0;
    protected double pathLength = Double.NaN;
    protected FunkyPathfindingPanel panel;
    protected volatile boolean exit = false;
    protected volatile boolean pause = false;
    
    public void setPanel(FunkyPathfindingPanel panel) {
        this.panel = panel;
    }
    
    public abstract void search(Point source, Point target);
    
    public void togglePause() {
        pause = !pause;
    }
    
    public void requestExit() {
        exit = true;
    }
    
    public int getNumberOfFrontierNodes() {
        return frontierNodeCount;
    }
    
    public int getNumberOfClosedNodes() {
        return closedNodeCount;
    }
    
    protected List<Point> tracebackPath(Point targetPoint,
                                        Map<Point, Point> parents) {
        List<Point> path = new ArrayList<>();
        Point currentPoint = targetPoint;
        
        while (currentPoint != null) {
            path.add(currentPoint);
            currentPoint = parents.get(currentPoint);
        }
        
        Collections.<Point>reverse(path);
        return path;
    }
    
    protected List<Point> tracebackPath(Point meetingPoint,
                                        Map<Point, Point> parentsForward,
                                        Map<Point, Point> parentsBackward) {
        List<Point> prefix = tracebackPath(meetingPoint, parentsForward);
        Point currentPoint = parentsBackward.get(meetingPoint);
        
        while (currentPoint != null) {
            prefix.add(currentPoint);
            currentPoint = parentsBackward.get(currentPoint);
        }
        
        return prefix;
    }
    
    protected void sleep() {
        Utils.sleep(THREAD_SLEEP_MILLISECONDS);
    }
    
    protected double getLength(List<Point> path) {
        double length = 0.0;
        
        for (int i = 0; i < path.size() - 1; ++i) {
            length += path.get(i).distance(path.get(i + 1));
        }
        
        return length;
    }
}
