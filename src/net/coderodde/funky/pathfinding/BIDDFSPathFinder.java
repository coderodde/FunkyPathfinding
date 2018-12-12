package net.coderodde.funky.pathfinding;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BIDDFSPathFinder extends AbstractPathFinder {
//
//    private final Point source;
//    private final Deque<Point> backwardSearchStack;
//    private final Set<Point> frontier;
//    private boolean exit;
//    
//    public BIDDFSPathFinder() {
//        this.panel = panel;
//        this.source              = null;
//        this.backwardSearchStack = null;
//        this.frontier            = null;
//    }
//
//    private BIDDFSPathFinder(Point source) {
//        this.source              = source;
//        this.backwardSearchStack = new ArrayDeque<>();
//        this.frontier            = new HashSet<>();
//    }
    
    private Point source;
    private Deque<Point> backwardSearchStack;
    private Set<Point> frontier;
    private boolean exit;
    
    public List<Point> search(Point source, Point target) {
        this.source = source;
        this.backwardSearchStack = new ArrayDeque<>();
        this.frontier = new HashSet<>();
        
        if (source.equals(target)) {
            return new ArrayList<>(Arrays.asList(source));
        }
                        
        for (int depth = 0;; ++depth) {
            if (exit) {
                return null;
            }
            
            if (pause) {
                sleep();
                continue;
            }
            // Do a depth limited search in forward direction. Put all nodes at 
            // depth == 0 to the frontier.
            depthLimitedSearchForward(source, depth);

            // Perform a reversed search starting from the target node and 
            // recurring to the depth 'depth'.
            Point meetingNode = depthLimitedSearchBackward(target, depth);

            if (meetingNode != null) {
                List<Point> path = buildPath(meetingNode);
                
                for (Point p : path) {
                    panel.markAsPath(p);
                }
                
                panel.repaint();
                return null;
            }

            backwardSearchStack.clear();

            // Perform a reversed search once again with depth = 'depth + 1'.
            // We need this in case the shortest path has odd number of arcs.
            meetingNode = depthLimitedSearchBackward(target, depth + 1);

            if (meetingNode != null) {
                List<Point> path = buildPath(meetingNode);
                
                for (Point p : path) {
                    panel.markAsPath(p);
                }
                
                this.pathLength = getLength(path);
                panel.repaint();
                return null;
            }

            panel.repaint();
            backwardSearchStack.clear();
            // Wipe out the frontier.
            frontier.clear();
        }
    }

    private void depthLimitedSearchForward(Point node, int depth) {
        panel.markAsClosed(node);
        
        if (depth == 0) {
            frontier.add(node);
            panel.markAsFrontier(node);
            return;
        }
        
        if (exit) {
            pause = false;
            return;
        }
        
        while (pause) {
            sleep();
        }

        for (Point child : panel.expand(node)) {
            depthLimitedSearchForward(child, depth - 1);
        }
        
        panel.unmarkAsClosed(node);
    }

    private Point depthLimitedSearchBackward(Point node, int depth) {
        panel.markAsClosed(node);
        backwardSearchStack.addFirst(node);

        if (depth == 0) {
            if (frontier.contains(node)) {
                return node;
            }

            backwardSearchStack.removeFirst();
            return null;
        }
        
        if (exit) {
            return null;
        }
            
        while (pause) {
            sleep();
        }

        for (Point parent : panel.expand(node)) {
            Point meetingNode = depthLimitedSearchBackward(parent, depth - 1);

            if (meetingNode != null) {
                return meetingNode;
            } 
        }

        backwardSearchStack.removeFirst();
        panel.unmarkAsClosed(node);
        return null;
    }

    private List<Point> buildPath(Point meetingNode) {
        List<Point> path = new ArrayList<>();
        List<Point> prefixPath = 
                new BIDDFSPathFinder()
                        .search(source, 
                                meetingNode);
        path.addAll(prefixPath);
        path.remove(path.size() - 1);
        path.addAll(backwardSearchStack);
        return path;
    }
}
