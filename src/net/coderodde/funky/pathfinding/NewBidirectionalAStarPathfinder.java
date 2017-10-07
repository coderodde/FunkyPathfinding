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

public final class NewBidirectionalAStarPathfinder 
extends AbstractPathfinder {

    @Override
    public void search(Point sourcePoint, Point targetPoint) {
        Objects.requireNonNull(sourcePoint, "The source point is null.");
        Objects.requireNonNull(targetPoint, "The target point is null.");
        
        IndexedBinaryHeap<Point, Double> openForward = new IndexedBinaryHeap<>();
        IndexedBinaryHeap<Point, Double> openBackward = new IndexedBinaryHeap<>();
        
        Set<Point> closed = new HashSet<>();
        
        Map<Point, Point> parentsForward  = new HashMap<>();
        Map<Point, Point> parentsBackward = new HashMap<>();
        
        Map<Point, Double> distancesForward  = new HashMap<>();
        Map<Point, Double> distancesBackward = new HashMap<>();
        
        double bestPathLength = Double.POSITIVE_INFINITY;
        double fForward  = sourcePoint.distance(targetPoint);
        double fBackward = targetPoint.distance(sourcePoint);
        Point meetingPoint = null;
        
        openForward.add(sourcePoint, fForward);
        openBackward.add(targetPoint, fBackward);
        parentsForward.put(sourcePoint, null);
        parentsBackward.put(targetPoint, null);
        distancesForward.put(sourcePoint, 0.0);
        distancesBackward.put(targetPoint, 0.0);
        
        List<Point> previousPartialForwardPath  = new ArrayList<>(0);
        List<Point> previousPartialBackwardPath = new ArrayList<>();
        this.pathLength = Double.NaN;
        
        int repaints = 0;
        
        while (openForward.size() > 0 && openBackward.size() > 0) {
            if (exit) {
                return;
            }
            
            if (pause) {
                sleep();
                continue;
            }
            
            if (closed.size() % NODES_EXPANSIONS_PER_REPAINT == 0) {
                repaints++;
                
                if (repaints % REPAINTS_PER_PATH_DRAWING == 0) {
                    for (Point p : previousPartialForwardPath) {
                        panel.markAsClosed(p);
                    }

                    for (Point p : previousPartialBackwardPath) {
                        panel.markAsClosed(p);
                    }

                    List<Point> partialForwardPath = 
                            tracebackPath(openForward.top(), parentsForward);

                    List<Point> partialBackwardPath = 
                            tracebackPath(openBackward.top(), parentsBackward);

                    for (Point p : partialForwardPath) {
                        panel.markAsPath(p);
                    }

                    for (Point p : partialBackwardPath) {
                        panel.markAsPath(p);
                    }

                    previousPartialForwardPath = partialForwardPath;
                    previousPartialBackwardPath = partialBackwardPath;

                    this.pathLength = getLength(partialForwardPath) + 
                                      getLength(partialBackwardPath);
                }
                    
                this.frontierNodeCount = openForward.size() + 
                                         openBackward.size();
                panel.repaint();
            }
            
            Point currentPoint = openForward.extractMinimum();
            closed.add(currentPoint);
            panel.markAsClosed(currentPoint);
            this.closedNodeCount++;

            double distance1 = distancesForward.get(currentPoint) +
                               currentPoint.distance(targetPoint);

            double distance2 = distancesForward.get(currentPoint) +
                               fBackward -
                               currentPoint.distance(sourcePoint);

            if (Math.max(distance1, distance2) >= bestPathLength) {
                continue;
            }

            for (Point childPoint : panel.expand(currentPoint)) {
                if (closed.contains(childPoint)) {
                    continue;
                }

                double tentativeDistance = 
                        distancesForward.get(currentPoint) +
                        currentPoint.distance(childPoint);

                if (!distancesForward.containsKey(childPoint)) {
                    distancesForward.put(childPoint, tentativeDistance);
                    parentsForward.put(childPoint, currentPoint);

                    openForward.add(childPoint,
                                    tentativeDistance +
                                    childPoint.distance(targetPoint));

                    panel.markAsFrontier(childPoint);

                    if (distancesBackward.containsKey(childPoint)) {
                        double pathLength = 
                                tentativeDistance +
                                distancesBackward.get(childPoint);

                        if (bestPathLength > pathLength) {
                            bestPathLength = pathLength;
                            meetingPoint = childPoint;
                        }
                    }
                } else if (distancesForward.get(childPoint) 
                        > tentativeDistance) {
                    distancesForward.put(childPoint, tentativeDistance);
                    parentsForward.put(childPoint, currentPoint);

                    openForward.decreasePriority(
                            childPoint,
                            tentativeDistance +
                                    childPoint.distance(targetPoint));

                    if (distancesBackward.containsKey(childPoint)) {
                        double pathLength = 
                                tentativeDistance +
                                distancesBackward.get(childPoint);

                        if (bestPathLength > pathLength) {
                            bestPathLength = pathLength;
                            meetingPoint = childPoint;
                        }
                    }
                }
            }

            if (openForward.size() > 0) {
                Point point = openForward.top();
                fForward = distancesForward.get(point) + 
                        point.distance(targetPoint);
            }
                
            currentPoint = openBackward.extractMinimum();
            closed.add(currentPoint);
            panel.markAsClosed(currentPoint);
            this.closedNodeCount++;

            distance1 = distancesBackward.get(currentPoint) +
                               currentPoint.distance(sourcePoint);

            distance2 = distancesBackward.get(currentPoint) +
                               fForward -
                               currentPoint.distance(targetPoint);

            if (Math.max(distance1, distance2) >= bestPathLength) {
                continue;
            }

            for (Point parentPoint : panel.expand(currentPoint)) {
                if (closed.contains(parentPoint)) {
                    continue;
                }

                double tentativeDistance = 
                        distancesBackward.get(currentPoint) +
                        parentPoint.distance(currentPoint);

                if (!distancesBackward.containsKey(parentPoint)) {
                    distancesBackward.put(parentPoint, tentativeDistance);
                    parentsBackward.put(parentPoint, currentPoint);

                    openBackward.add(parentPoint,
                                     tentativeDistance + 
                                        parentPoint.distance(sourcePoint));

                    panel.markAsFrontier(parentPoint);

                    if (distancesForward.containsKey(parentPoint)) {
                        double pathLength = 
                                tentativeDistance + 
                                distancesForward.get(parentPoint);

                        if (bestPathLength > pathLength) {
                            bestPathLength = pathLength;
                            meetingPoint = parentPoint;
                        }
                    }
                } else if (distancesBackward.get(parentPoint) 
                        > tentativeDistance) {
                    distancesBackward.put(parentPoint, tentativeDistance);
                    parentsBackward.put(parentPoint, currentPoint);

                    openBackward.decreasePriority(
                            parentPoint,
                            tentativeDistance +
                                    parentPoint.distance(sourcePoint));

                    if (distancesForward.containsKey(parentPoint)) {
                        double pathLength = 
                                tentativeDistance + 
                                distancesForward.get(parentPoint);

                        if (bestPathLength > pathLength) {
                            bestPathLength = pathLength;
                            meetingPoint = parentPoint;
                        }
                    }
                }
            }

            if (openBackward.size() > 0) {
                Point point = openBackward.top();
                fBackward = distancesBackward.get(point) + 
                            point.distance(sourcePoint);
            }
        }
        
        if (meetingPoint != null) {
            for (Point p : previousPartialForwardPath) {
                panel.markAsClosed(p);
            }
            
            for (Point p : previousPartialBackwardPath) {
                panel.markAsClosed(p);
            }
            
            List<Point> shortestPath = tracebackPath(meetingPoint,
                                                     parentsForward,
                                                     parentsBackward);
            
            for (Point p : shortestPath) {
                panel.markAsPath(p);
            }
            
            this.frontierNodeCount = openForward.size() + 
                                     openBackward.size();
            this.pathLength = getLength(shortestPath);
            panel.repaint();
        }
    }
}
