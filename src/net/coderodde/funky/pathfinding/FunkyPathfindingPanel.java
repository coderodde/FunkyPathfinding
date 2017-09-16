package net.coderodde.funky.pathfinding;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JPanel;
import static net.coderodde.funky.pathfinding.Configuration.DEFAULT_CLOSED_COLOR;
import static net.coderodde.funky.pathfinding.Configuration.DEFAULT_FRONTIER_COLOR;
import static net.coderodde.funky.pathfinding.Configuration.DEFAULT_PATH_COLOR;
import static net.coderodde.funky.pathfinding.Configuration.DEFAULT_SOURCE_COLOR;
import static net.coderodde.funky.pathfinding.Configuration.DEFAULT_TARGET_COLOR;
import static net.coderodde.funky.pathfinding.Configuration.DEFAULT_WALL_COLOR;
import static net.coderodde.funky.pathfinding.Utils.*;
import static net.coderodde.funky.pathfinding.Configuration.DEFAULT_WORLD_COLOR;
import static net.coderodde.funky.pathfinding.Configuration.POINT_RECTANGLE_WIDTH_HEIGHT;

public final class FunkyPathfindingPanel extends JPanel {

    private static final boolean IS_WALL = true;
    private static final boolean IS_TRAVERSABLE = !IS_WALL;
    private static final int STAT_WIDTH = 200;
    
    private final int width;
    private final int height;
    private final BufferedImage bufferedImage;
    private final Point sourcePoint;
    private final Point targetPoint;
    private final boolean[][] gridGraphData;
    private final FunkyPathfindingPanelMouseAdapter mouseAdapter;
    private final Font statisticsFont = new Font("Monospaced", Font.BOLD, 14);
    
    private Color worldColor    = DEFAULT_WORLD_COLOR;
    private Color wallColor     = DEFAULT_WALL_COLOR;
    private Color sourceColor   = DEFAULT_SOURCE_COLOR;
    private Color targetColor   = DEFAULT_TARGET_COLOR;
    private Color closedColor   = DEFAULT_CLOSED_COLOR;
    private Color frontierColor = DEFAULT_FRONTIER_COLOR;
    private Color pathColor     = DEFAULT_PATH_COLOR;
    private DrawingMode drawingMode = DrawingMode.SET_WALL;
    private PathfinderRunningThread currentThread;
    
    public FunkyPathfindingPanel(int width, 
                                 int height) {
        this.width = checkWidth(width);
        this.height = checkHeight(height);
        this.sourcePoint = new Point();
        this.targetPoint = new Point();
        this.gridGraphData = new boolean[height][width];
        this.bufferedImage = new BufferedImage(width, 
                                               height, 
                                               BufferedImage.TYPE_INT_RGB);
        this.setSize(width, height);
        
        for (int y = 0; y < gridGraphData.length; ++y) {
            for (int x = 0; x < gridGraphData[0].length; ++x) {
                setPixel(x, y, worldColor);
            }
        }
        
        setSource(width / 10, height / 10);
        setTarget(9 * width / 10, 9 * height / 10);
        
        this.mouseAdapter = new FunkyPathfindingPanelMouseAdapter(this);
        setInteractive(true);
    }
    
    public void setInteractive(boolean interactive) {
        if (interactive == false) {
            mouseAdapter.forgetCurrentPoint();
            removeMouseListener(mouseAdapter);
            removeMouseMotionListener(mouseAdapter);
        } else {
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }
    }
    
    public void search(AbstractPathfinder pathfinder) {
        this.currentThread = new PathfinderRunningThread(pathfinder, 
                                                         sourcePoint, 
                                                         targetPoint);
        pathfinder.setPanel(this);
        
        try {
            this.currentThread.start();
        } catch (TargetNotReachableException ex) {}   
    }
    
    public List<Point> expand(Point point) {
        int x = point.x;
        int y = point.y;
        List<Point> list = new ArrayList<>(8);
        list.add(new Point(x - 1, y - 1));
        list.add(new Point(x - 1, y));
        list.add(new Point(x - 1, y + 1));
        list.add(new Point(x, y - 1));
        list.add(new Point(x, y + 1));
        list.add(new Point(x + 1, y - 1));
        list.add(new Point(x + 1, y));
        list.add(new Point(x + 1, y + 1));
        return pruneNeighborList(list);
    }
    
    public void setDrawingMode(DrawingMode drawingMode) {
        this.drawingMode = 
                Objects.requireNonNull(drawingMode, 
                                       "The input drawing mode is null.");
    }
    
    public DrawingMode getDrawingMode() {
        return drawingMode;
    }
    
    public void setWorldColor(Color worldColor) {
        this.worldColor = Objects.requireNonNull(worldColor, 
                                                 "The world color is null.");
    }
    
    public void setWallColor(Color wallColor) {
        this.wallColor = Objects.requireNonNull(wallColor, 
                                                "The wall color is null.");
    }
    
    public void setSourceColor(Color sourceColor) {
        this.sourceColor = Objects.requireNonNull(sourceColor,
                                                  "The source color is null.");
    }
    
    public void setTargetColor(Color targetColor) {
        this.targetColor = Objects.requireNonNull(targetColor, 
                                                  "The target color is null.");
    }
    
    public void setFrontierColor(Color frontierColor) {
        this.frontierColor = 
                Objects.requireNonNull(frontierColor, 
                                       "The frontier color is null.");
    }
    
    public void setClosedColor(Color closedColor) {
        this.closedColor = Objects.requireNonNull(closedColor, 
                                                  "The closed color is null.");
    }
    
    public void setPathColor(Color pathColor) {
        this.pathColor = Objects.requireNonNull(pathColor, 
                                                "The path color is null.");
    }
    
    public void setWall(int x, int y) {
        setPixel(x, y, wallColor);
        gridGraphData[y][x] = IS_WALL;
    }
    
    public void removeWall(int x, int y) {
        setPixel(x, y, worldColor);
        gridGraphData[y][x] = IS_TRAVERSABLE;
    }
    
    public void setSource(int x, int y) {
        sourcePoint.x = x;
        sourcePoint.y = y;
        paintPoint(x, y, sourceColor);
    }
    
    public void setTarget(int x, int y) {
        targetPoint.x = x;
        targetPoint.y = y;
        paintPoint(x, y, targetColor);
    }
    
    public void unsetSource(int x, int y) {
        unsetTerminal(x, y);
    }
    
    public void unsetTarget(int x, int y) {
        unsetTerminal(x, y);
    }
    
    public void clearAllWalls() {
        for (int y = 0; y < gridGraphData.length; ++y) {
            for (int x = 0; x < gridGraphData[0].length; ++x) {
                setPixel(x, y, worldColor);
                gridGraphData[y][x] = IS_TRAVERSABLE;
            }
        }
        
        repaint();
    }
    
    public void draw(int x, int y) {
        switch (drawingMode) {
            
            case SET_WALL:
                drawWall(x, y);
                break;
                
            case REMOVE_WALL:
                drawWorld(x, y);
                break;
                
            default:
                throw new IllegalStateException(
                        "Unknown drawing mode: " + drawingMode);
        }
    }
    
    private void draw(int x, int y, Color color, boolean traversability) {
        int halfWidth  = POINT_RECTANGLE_WIDTH_HEIGHT / 2;
        int halfHeight = POINT_RECTANGLE_WIDTH_HEIGHT / 2;
        int startX = x - halfWidth;
        int startY = y - halfHeight;
        
        for (int yy = 0; yy < POINT_RECTANGLE_WIDTH_HEIGHT; ++yy) {
            if (startY + yy < 0 || startY + yy >= height) {
                continue;
            }
            
            for (int xx = 0; xx < POINT_RECTANGLE_WIDTH_HEIGHT; ++xx) {
                if (startX + xx < 0 || startX + xx >= width) {
                    continue;
                }
                
                setPixel(startX + xx, startY + yy, color);
                gridGraphData[startY + yy][startX + xx] = traversability;
            }
        }
        
    }
    
    private void drawWall(int x, int y) {
        draw(x, y, wallColor, IS_WALL);
    }
    
    private void drawWorld(int x, int y) {
        draw(x, y, worldColor, IS_TRAVERSABLE);
    }
    
    public void reset() {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                setPixel(x, y, gridGraphData[y][x] == IS_TRAVERSABLE ?
                        worldColor :
                        wallColor);
            }
        }
    }
    
    public void togglePause() {
        if (currentThread != null) {
            currentThread.togglePause();
        }
    }
    
    public void requestExit() {
        if (currentThread != null) {
            currentThread.halt();
            
            try {
                currentThread.join();
            } catch (InterruptedException ex) {
                
            }
            
            Graphics g = bufferedImage.getGraphics();
            writeStatistics(g);
            repaint();
            currentThread = null;
        }
    }
    
    private void writeStatistics(Graphics g) {
        if (currentThread == null) {
            return;
        }
        
        AbstractPathfinder pathfinder = currentThread.getPathfinder();
        int closedNodes   = pathfinder.getNumberOfClosedNodes();
        int frontierNodes = pathfinder.getNumberOfFrontierNodes();
        int totalNodes    = closedNodes + frontierNodes;
        
        g.setColor(worldColor);
        g.fillRect(getWidth() - STAT_WIDTH, 0, STAT_WIDTH, 45);
        g.setColor(Color.BLACK);
        g.setFont(statisticsFont);
        
        g.drawString("Closed nodes:   " + closedNodes, 
                     getWidth() - STAT_WIDTH,
                     15);
        g.drawString("Frontier nodes: " + frontierNodes,
                     getWidth() - STAT_WIDTH,
                     30);
        g.drawString("Total nodes:    " + totalNodes,
                     getWidth() - STAT_WIDTH,
                     45);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        setSource(sourcePoint.x, sourcePoint.y);
        setTarget(targetPoint.x, targetPoint.y);
        writeStatistics(bufferedImage.getGraphics());
        g.drawImage(bufferedImage, 0, 0, null);
    }
    
    public void markAsClosed(Point point) {
        setPixel(point.x, point.y, closedColor);
    }
    
    public void markAsFrontier(Point point) {
        setPixel(point.x, point.y, frontierColor);
    }
    
    public void markAsPath(Point point) {
        setPixel(point.x, point.y, pathColor);
    }
    
    Point getSourcePoint() {
        return sourcePoint;
    }
    
    Point getTargetPoint() {
        return targetPoint;
    }
    
    private void setPixel(int x, int y, Color color) {
        bufferedImage.setRGB(x, y, color.getRGB());
    }
    
    private void paintPoint(int x, int y, Color color) {
        int halfWidth = POINT_RECTANGLE_WIDTH_HEIGHT / 2;
        int halfHeight = POINT_RECTANGLE_WIDTH_HEIGHT / 2;
        int startX = x - halfWidth;
        int startY = y - halfHeight;
        
        for (int yy = 0; yy < POINT_RECTANGLE_WIDTH_HEIGHT; ++yy) {
            if (startY + yy < 0) {
                continue;
            }
            
            if (startY + yy >= height) {
                continue;
            }
            
            for (int xx = 0; xx < POINT_RECTANGLE_WIDTH_HEIGHT; ++xx) {
                if (startX + xx < 0) {
                    continue;
                }
                
                if (startX + xx >= width) {
                    continue;
                }
                
                setPixel(startX + xx, startY + yy, color);
            }
        }
    }
    
    private List<Point> pruneNeighborList(List<Point> list) {
        List<Point> prunedList = new ArrayList<>(8);
        
        for (Point point : list) {
            if (point.x < 0 || point.x >= width) {
                continue;
            }
            
            if (point.y < 0 || point.y >= height) {
                continue;
            }
            
            if (gridGraphData[point.y][point.x] == IS_WALL) {
                continue;
            }
            
            prunedList.add(point);
        }
        
        return prunedList;
    }
    
    void draw(int x, int y, int previousX, int previousY) {
        double dx = previousX - x;
        double dy = previousY - y;
        double length = Math.sqrt(dx * dx + dy * dy);
        double angle = Math.atan2(dy, dx);
        
        if (drawingMode == DrawingMode.SET_WALL) {
            for (int i = 1; i < (int) Math.ceil(length); ++i) {
                drawWall(x + (int)(i * Math.cos(angle)),
                         y + (int)(i * Math.sin(angle)));
            }
        } else {
            for (int i = 1; i < (int) Math.ceil(length); ++i) {
                drawWorld(x + (int)(i * Math.cos(angle)),
                          y + (int)(i * Math.sin(angle)));
            }
        }
    }
    
    private void unsetTerminal(int x, int y) {
        int halfWidth = POINT_RECTANGLE_WIDTH_HEIGHT / 2;
        int halfHeight = POINT_RECTANGLE_WIDTH_HEIGHT / 2;
        int startX = x - halfWidth;
        int startY = y - halfHeight;
        
        for (int yy = 0; yy < POINT_RECTANGLE_WIDTH_HEIGHT; ++yy) {
            if (startY + yy < 0) {
                continue;
            }
            
            if (startY + yy >= height) {
                continue;
            }
            
            for (int xx = 0; xx < POINT_RECTANGLE_WIDTH_HEIGHT; ++xx) {
                if (startX + xx < 0) {
                    continue;
                }
                
                if (startX + xx >= width) {
                    continue;
                }
                
                if (gridGraphData[startY + yy][startX + xx] == IS_WALL) {
                    setPixel(startX + xx, startY + yy, wallColor);
                } else {
                    setPixel(startX + xx, startY + yy, worldColor);
                }
            }
        }
    }
}
