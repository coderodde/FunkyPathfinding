package net.coderodde.funky.pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static net.coderodde.funky.pathfinding.Configuration.NODES_EXPANSIONS_PER_REPAINT;
import static net.coderodde.funky.pathfinding.Configuration.REPAINTS_PER_PATH_DRAWING;

public final class BidirectionalDijkstraPathfinder 
extends AbstractPathfinder {

    @Override
    public void search(Point sourcePoint, Point targetPoint) {
        Objects.requireNonNull(sourcePoint, "The source point is null.");
        Objects.requireNonNull(targetPoint, "The target point is null.");
        
        List<Point> previousPartialForwardPath  = new ArrayList<>();
        List<Point> previousPartialBackwardPath = new ArrayList<>();
        
        this.pathLength = Double.NaN;
        
        IndexedBinaryHeap<Point, Double> openForward = new IndexedBinaryHeap<>();
        IndexedBinaryHeap<Point, Double> openBackward = new IndexedBinaryHeap<>();
        
        Set<Point> closedForward  = new HashSet<>();
        Set<Point> closedBackward = new HashSet<>();
        
        Map<Point, Point> parentsForward  = new HashMap<>();
        Map<Point, Point> parentsBackward = new HashMap<>();
        
        Map<Point, Double> distancesForward  = new HashMap<>();
        Map<Point, Double> distancesBackward = new HashMap<>();
        
        double bestPathLength = Double.POSITIVE_INFINITY;
        Point meetingPoint = null;
        
        openForward.add(sourcePoint, 0.0);
        openBackward.add(targetPoint, 0.0);
        
        parentsForward.put(sourcePoint, null);
        parentsBackward.put(targetPoint, null);
        
        distancesForward.put(sourcePoint, 0.0);
        distancesBackward.put(targetPoint, 0.0);
        
        int repaints = 0;
        
        while (openForward.size() > 0 && openBackward.size() > 0) {
            if (exit) {
                return;
            }
            
            if (pause) {
                sleep();
                continue;
            }
            
            double mtmp = distancesForward.get(openForward.top()) +
                          distancesBackward.get(openBackward.top());
            
            if (mtmp >= bestPathLength) {
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
                return;
            }
            
            if ((closedForward.size() + closedBackward.size())
                    % NODES_EXPANSIONS_PER_REPAINT == 0) {
                repaints++;
                
                if (repaints % REPAINTS_PER_PATH_DRAWING == 0) {
                    for (Point p : previousPartialForwardPath) {
                        panel.markAsClosed(p);
                    }

                    for (Point p : previousPartialBackwardPath) {
                        panel.markAsClosed(p);
                    }

                    List<Point> partialForwardPath = 
                            tracebackPath(openForward.top(),
                                          parentsForward);

                    List<Point> partialBackwardPath = 
                            tracebackPath(openBackward.top(),
                                          parentsBackward);

                    // Not necessarily required, but what the hell.
                    Collections.reverse(partialBackwardPath);

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
            
            int totalForwardNodes  = openForward.size() + closedForward.size();
            int totalBackwardNodes = openBackward.size() + 
                                     closedBackward.size();
            
            if (totalForwardNodes < totalBackwardNodes) {
                Point currentPoint = openForward.extractMinimum();
                panel.markAsClosed(currentPoint);
                this.closedNodeCount++;
                closedForward.add(currentPoint);
                
                for (Point childPoint : panel.expand(currentPoint)) {
                    if (closedForward.contains(childPoint)) {
                        continue;
                    }
                    
                    double tentativeDistance = 
                            distancesForward.get(currentPoint) +
                            currentPoint.distance(childPoint);
                    
                    if (!distancesForward.containsKey(childPoint)) {
                        distancesForward.put(childPoint, tentativeDistance);
                        parentsForward.put(childPoint, currentPoint);
                        openForward.add(childPoint, tentativeDistance);
                        panel.markAsFrontier(childPoint);
                        
                        if (closedBackward.contains(childPoint)) {
                            double pathLength = 
                                    distancesBackward.get(childPoint) +
                                    tentativeDistance;
                            
                            if (bestPathLength > pathLength) {
                                bestPathLength = pathLength;
                                meetingPoint = childPoint;
                                this.pathLength = pathLength;
                            }
                        }
                    } else if (distancesForward.get(childPoint) 
                            > tentativeDistance) {
                        distancesForward.put(childPoint, tentativeDistance);
                        parentsForward.put(childPoint, currentPoint);
                        openForward.decreasePriority(childPoint, 
                                                     tentativeDistance);
                        
                        if (closedBackward.contains(childPoint)) {
                            double pathLength = 
                                    distancesBackward.get(childPoint) +
                                    tentativeDistance;
                            
                            if (bestPathLength > pathLength) {
                                bestPathLength = pathLength;
                                meetingPoint = childPoint;
                                this.pathLength = pathLength;
                            }
                        }
                    }
                }
            } else {
                Point currentPoint = openBackward.extractMinimum();
                panel.markAsClosed(currentPoint);
                this.closedNodeCount++;
                closedBackward.add(currentPoint);
                
                for (Point parentPoint : panel.expand(currentPoint)) {
                    if (closedBackward.contains(parentPoint)) {
                        continue;
                    }
                    
                    double tentativeDistance = 
                            distancesBackward.get(currentPoint) +
                            parentPoint.distance(currentPoint);
                    
                    if (!distancesBackward.containsKey(parentPoint)) {
                        distancesBackward.put(parentPoint, tentativeDistance);
                        parentsBackward.put(parentPoint, currentPoint);
                        openBackward.add(parentPoint, tentativeDistance);
                        panel.markAsFrontier(parentPoint);
                        
                        if (closedForward.contains(parentPoint)) {
                            double pathLength = 
                                    distancesForward.get(parentPoint) +
                                    tentativeDistance;
                            
                            if (bestPathLength > tentativeDistance) {
                                bestPathLength = tentativeDistance;
                                meetingPoint = parentPoint;
                                this.pathLength = pathLength;
                            }
                        }
                    } else if (distancesBackward.get(parentPoint) 
                            > tentativeDistance) {
                        distancesBackward.put(parentPoint, tentativeDistance);
                        parentsBackward.put(parentPoint, currentPoint);
                        
                        openBackward.decreasePriority(parentPoint,
                                                      tentativeDistance);
                        
                        if (closedForward.contains(parentPoint)) {
                            double pathLength = 
                                    distancesForward.get(parentPoint) +
                                    tentativeDistance;
                            
                            if (bestPathLength > tentativeDistance) {
                                bestPathLength = tentativeDistance;
                                meetingPoint = parentPoint;
                                this.pathLength = pathLength;
                            }
                        }
                    }
                }
            }
        }
    }
}
