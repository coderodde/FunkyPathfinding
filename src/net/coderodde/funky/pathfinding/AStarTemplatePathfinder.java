package net.coderodde.funky.pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static net.coderodde.funky.pathfinding.Configuration.NODES_EXPANSIONS_PER_REPAINT;
import static net.coderodde.funky.pathfinding.Configuration.REPAINTS_PER_PATH_DRAWING;

public class AStarTemplatePathfinder extends AbstractPathfinder {

    public interface Heuristic {
        double estimate(Point p1, Point p2);
    }
    
    private final Heuristic heuristic;
    
    public AStarTemplatePathfinder(Heuristic heuristic) {
        this.heuristic = heuristic;
    }
    
    @Override
    public void search(Point sourcePoint, Point targetPoint) {
        Objects.requireNonNull(sourcePoint, "The source point is null.");
        Objects.requireNonNull(targetPoint, "The target point is null.");
        
        this.pathLength = Double.NaN;
        
        FibonacciHeap<Point, Double> open = new FibonacciHeap<>();
        Set<Point> closed = new HashSet<>();
        Map<Point, Point> parents = new HashMap<>();
        Map<Point, Double> distances = new HashMap<>();
        List<Point> previousPartialPath = new ArrayList<>();
        
        open.add(sourcePoint, 0.0);
        parents.put(sourcePoint, null);
        distances.put(sourcePoint, 0.0);
        int repaints = 0;
        
        while (open.size() > 0) {
            if (exit) {
                return;
            }
            
            if (pause) {
                sleep();
                continue;
            }
            
            Point currentPoint = open.extractMinimum();
            panel.markAsClosed(currentPoint);
            this.closedNodeCount++;
            
            if (currentPoint.equals(targetPoint)) {
                for (Point p : previousPartialPath) {
                    panel.markAsClosed(p);
                }
                
                List<Point> shortestPath = tracebackPath(currentPoint, parents);
                
                for (Point p : shortestPath) {
                    panel.markAsPath(p);
                }
                
                this.frontierNodeCount = open.size();
                this.pathLength = getLength(shortestPath);
                panel.repaint();
                return;
            }
            
            closed.add(currentPoint);
            
            if (closed.size() % NODES_EXPANSIONS_PER_REPAINT == 0) {
                repaints++;
                
                if (repaints % REPAINTS_PER_PATH_DRAWING == 0) {
                    for (Point p : previousPartialPath) {
                        panel.markAsClosed(p);
                    }

                    List<Point> partialPath = tracebackPath(open.top(), parents);

                    for (Point p : partialPath) {
                        panel.markAsPath(p);
                    }

                    previousPartialPath = partialPath;
                    this.pathLength = getLength(partialPath);
                }
                    
                this.frontierNodeCount = open.size();
                panel.repaint();
            }
            
            for (Point childPoint : panel.expand(currentPoint)) {
                if (closed.contains(childPoint)) {
                    continue;
                }
                
                double tentativeDistance = distances.get(currentPoint) +
                                           currentPoint.distance(childPoint);
                
                if (!distances.containsKey(childPoint)) {
                    panel.markAsFrontier(childPoint);
                    open.add(childPoint, 
                             tentativeDistance + 
                                heuristic.estimate(childPoint, targetPoint));
                    
                    parents.put(childPoint, currentPoint);
                    distances.put(childPoint, tentativeDistance);
                } else if (distances.get(childPoint) > tentativeDistance) {
                    open.decreasePriority(
                            childPoint,
                            tentativeDistance +
                                heuristic.estimate(childPoint, targetPoint));
                    
                    parents.put(childPoint, currentPoint);
                    distances.put(childPoint, tentativeDistance);
                }
            }
        }
    }
}
