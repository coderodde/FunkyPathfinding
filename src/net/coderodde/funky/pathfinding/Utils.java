package net.coderodde.funky.pathfinding;

import static net.coderodde.funky.pathfinding.Configuration.*;

public final class Utils {
    
    private Utils() {}
    
    static int checkWidth(int width) {
        if (width < MINIMUM_WIDTH) {
            throw new IllegalArgumentException(
                "The input width is too small: " + width + ". Must be at " +
                "least " + MINIMUM_WIDTH + ".");
        }
        
        return width;
    }
    
    static int checkHeight(int height) {
        if (height < MINIMUM_HEIGHT) {
            throw new IllegalArgumentException(
                "The input height is too small: " + height + ". Must be at " +
                "least " + MINIMUM_HEIGHT + ".");
        }
        
        return height;
    }
    
    static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            
        }
    }
}
