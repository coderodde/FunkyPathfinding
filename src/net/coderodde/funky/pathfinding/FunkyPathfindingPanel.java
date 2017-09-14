package net.coderodde.funky.pathfinding;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.swing.JPanel;
import static net.coderodde.funky.pathfinding.Configuration.DEFAULT_WALL_COLOR;
import static net.coderodde.funky.pathfinding.Utils.*;
import static net.coderodde.funky.pathfinding.Configuration.DEFAULT_WORLD_COLOR;
import static net.coderodde.funky.pathfinding.Configuration.POINT_RECTANGLE_WIDTH_HEIGHT;

public final class FunkyPathfindingPanel extends JPanel {

    private static final boolean IS_WALL = true;
    private static final boolean IS_TRAVERSABLE = !IS_WALL;
    
    private final int width;
    private final int height;
    private final BufferedImage bufferedImage;
    private final Point sourcePoint;
    private final Point targetPoint;
    private final boolean[][] gridGraphData;
    
    private Color worldColor  = DEFAULT_WORLD_COLOR;
    private Color wallColor   = DEFAULT_WALL_COLOR;
    private Color sourceColor = Configuration.DEFAULT_SOURCE_COLOR;
    private Color targetColor = Configuration.DEFAULT_TARGET_COLOR;
    private DrawingMode drawingMode = DrawingMode.SET_WALL;
    
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
        
        FunkyPathfindingPanelMouseAdapter mouseAdapter = 
                new FunkyPathfindingPanelMouseAdapter(this);
        
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
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
    
    public void clearAllWalls() {
        for (int y = 0; y < gridGraphData.length; ++y) {
            for (int x = 0; x < gridGraphData[0].length; ++x) {
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
    
    @Override
    public void paintComponent(Graphics g) {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                setPixel(x,
                         y,
                         gridGraphData[y][x] == IS_TRAVERSABLE ? 
                                 worldColor : 
                                 wallColor);
            }
        }
        
        setSource(sourcePoint.x, sourcePoint.y);
        setTarget(targetPoint.x, targetPoint.y);
        g.drawImage(bufferedImage, 0, 0, null);
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
}
