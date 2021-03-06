package net.coderodde.funky.pathfinding;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Objects;
import static net.coderodde.funky.pathfinding.Configuration.MAXIMUM_DISTANCE_FROM_POINT;

public final class FunkyPathfindingPanelMouseAdapter 
implements MouseListener, MouseMotionListener {

    private final FunkyPathfindingPanel funkyPathfindingPanel;
    private Point currentPoint;
    private int previousX;
    private int previousY;
    
    public FunkyPathfindingPanelMouseAdapter(
            FunkyPathfindingPanel funkyPathfindingPanel) {
        this.funkyPathfindingPanel = 
                Objects.requireNonNull(funkyPathfindingPanel, 
                                       "The input panel is null.");
    }
    
    public void forgetCurrentPoint() {
        currentPoint = null;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (funkyPathfindingPanel
                .getSourcePoint()
                .distance(e.getPoint()) < MAXIMUM_DISTANCE_FROM_POINT) {
            currentPoint = funkyPathfindingPanel.getSourcePoint();
        } else if (funkyPathfindingPanel
                .getTargetPoint()
                .distance(e.getPoint()) < MAXIMUM_DISTANCE_FROM_POINT) {
            currentPoint = funkyPathfindingPanel.getTargetPoint();
        }
        
        Point p = e.getPoint();
        previousX = p.x;
        previousY = p.y;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        currentPoint = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        Point point = e.getPoint();
        
        if (currentPoint != null) {
            funkyPathfindingPanel.unsetSource(currentPoint.x, currentPoint.y);
            funkyPathfindingPanel.unsetTarget(currentPoint.x, currentPoint.y);
            
            currentPoint.x = point.x;
            currentPoint.y = point.y;
            
            if (currentPoint == funkyPathfindingPanel.getSourcePoint()) {
                funkyPathfindingPanel.setSource(currentPoint.x, currentPoint.y);
            } else {
                funkyPathfindingPanel.setTarget(currentPoint.x, currentPoint.y);
            }
            
            funkyPathfindingPanel.repaint();
        } else {
            funkyPathfindingPanel.draw(point.x, point.y, previousX, previousY);
            funkyPathfindingPanel.repaint();
            previousX = point.x;
            previousY = point.y;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
}
