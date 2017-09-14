package net.coderodde.funky.pathfinding;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static net.coderodde.funky.pathfinding.Configuration.NODES_EXPANSIONS_PER_REPAINT;

public final class AStarPathfinder extends AbstractPathfinder {

    @Override
    public List<Point> search(Point sourcePoint, Point targetPoint) {
        Objects.requireNonNull(sourcePoint, "The source point is null.");
        Objects.requireNonNull(targetPoint, "The target point is null.");
        
        FibonacciHeap<Point, Double> open = new FibonacciHeap<>();
        Set<Point> closed = new HashSet<>();
        Map<Point, Point> parents = new HashMap<>();
        Map<Point, Double> distances = new HashMap<>();
        
        open.add(sourcePoint, 0.0);
        parents.put(sourcePoint, null);
        distances.put(sourcePoint, 0.0);
        
        while (open.size() > 0) {
            if (pause) {
                System.out.println("Pause on...");
                sleep();
                continue;
            }
            
            if (exit) {
                System.out.println("Exiting...");
                return null;
            }
            
            Point currentPoint = open.extractMinimum();
            panel.markAsClosed(currentPoint);
            
            if (currentPoint.equals(targetPoint)) {
                return tracebackPath(targetPoint, parents);
            }
            
            closed.add(currentPoint);
            
            if (closed.size() % NODES_EXPANSIONS_PER_REPAINT == 0) {
                
            }
            
            for (Point childPoint : panel.expand(currentPoint)) {
                if (closed.contains(childPoint)) {
                    continue;
                }
                
                double tentativeDistance = distances.get(currentPoint) +
                                           currentPoint.distance(childPoint);
                
                if (!distances.containsKey(childPoint)) {
                    open.add(childPoint, 
                             tentativeDistance + 
                                     childPoint.distance(targetPoint));
                    parents.put(childPoint, currentPoint);
                    distances.put(childPoint, tentativeDistance);
                } else if (distances.get(childPoint) > tentativeDistance) {
                    open.decreasePriority(
                            childPoint,
                            tentativeDistance +
                                    childPoint.distance(targetPoint));
                    parents.put(childPoint, currentPoint);
                    distances.put(childPoint, tentativeDistance);
                }
            }
        }
        
        throw new TargetNotReachableException(
                "Target " + targetPoint + " not reachable from " + sourcePoint + ".");
    }
}
