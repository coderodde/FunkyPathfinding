package net.coderodde.funky.pathfinding;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

public final class FunkyPathfindingFrame extends JFrame {

    private static final String TITLE = "FunkyPathfinding 1.6";
    
    private final FunkyPathfindingPanel funkyPathfindingPanel;
    
    public FunkyPathfindingFrame() {
        super(TITLE);
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.funkyPathfindingPanel =
               new FunkyPathfindingPanel(screenDimension.width,
                                         screenDimension.height);
        setSize(screenDimension.width, screenDimension.height);
        getContentPane().add(funkyPathfindingPanel);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    public FunkyPathfindingPanel getPanel() {
        return funkyPathfindingPanel;
    }
}
