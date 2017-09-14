package net.coderodde.funky.pathfinding;

import java.awt.Color;

public final class Configuration {

    private Configuration() {}
    
    static final int MINIMUM_WIDTH = 1;
    static final int MINIMUM_HEIGHT = 1;
    static final int POINT_RECTANGLE_WIDTH_HEIGHT = 10;
    static final double MAXIMUM_DISTANCE_FROM_POINT = 5.0;
    static final Color DEFAULT_WORLD_COLOR = Color.WHITE;
    static final Color DEFAULT_WALL_COLOR = Color.BLACK;
    static final Color DEFAULT_SOURCE_COLOR = Color.GREEN;
    static final Color DEFAULT_TARGET_COLOR = Color.RED;
    static final Color DEFAULT_CLOSED_COLOR = Color.LIGHT_GRAY;
    static final Color DEFAULT_FRONTIER_COLOR = Color.YELLOW;
    static final Color DEFAULT_PATH_COLOR     = Color.BLUE;
    static final int NODES_EXPANSIONS_PER_REPAINT = 50;
    static final int THREAD_SLEEP_MILLISECONDS = 100;
}
