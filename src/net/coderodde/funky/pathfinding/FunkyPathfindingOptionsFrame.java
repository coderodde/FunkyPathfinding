package net.coderodde.funky.pathfinding;

import java.awt.GridLayout;
import java.awt.Label;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;

public final class FunkyPathfindingOptionsFrame extends JDialog {

    private final JButton buttonDrawWorld;
    private final JButton buttonDrawWalls;
    private final JButton buttonClear;
    private final JButton buttonRun;
    private final JButton buttonStop;
    private final JButton buttonReset;
    private final String[] comboBoxOptions = { "A*", 
                                               "NBA*", 
                                               "PHBA",
                                               "Dijkstra", 
                                               "Bidirectional Dijkstra" 
    };
    
    private final JComboBox comboBoxAlgorithm;
    private final FunkyPathfindingPanel funkyPathfindingPanel;
    
    public FunkyPathfindingOptionsFrame(
            FunkyPathfindingFrame frame,
            FunkyPathfindingPanel funkyPathfindingPanel) {
        super(frame);
        this.funkyPathfindingPanel =
                Objects.requireNonNull(funkyPathfindingPanel, 
                                       "The input panel is null.");
        
        this.comboBoxAlgorithm = new JComboBox(comboBoxOptions);
        this.buttonDrawWalls   = new JButton("Draw walls");
        this.buttonDrawWorld   = new JButton("Erase walls");
        this.buttonClear       = new JButton("Clear all walls");
        this.buttonRun         = new JButton("Run");
        this.buttonStop        = new JButton("Stop");
        this.buttonReset       = new JButton("Reset");
        
        this.buttonDrawWalls.addActionListener((e) -> {
            funkyPathfindingPanel.setDrawingMode(DrawingMode.SET_WALL);
        });
        
        this.buttonDrawWorld.addActionListener((e) -> {
            funkyPathfindingPanel.setDrawingMode(DrawingMode.REMOVE_WALL);
        });
        
        this.buttonClear.addActionListener((e) -> {
            funkyPathfindingPanel.clearAllWalls();
        });
        
        GridLayout layout = new GridLayout(9, 1);   
        setLayout(layout);
        
        getContentPane().add(buttonDrawWorld);
        getContentPane().add(buttonDrawWalls);
        getContentPane().add(buttonClear);
        getContentPane().add(new Label());
        getContentPane().add(buttonRun);
        getContentPane().add(buttonStop);
        getContentPane().add(buttonReset);
        getContentPane().add(new Label());
        getContentPane().add(comboBoxAlgorithm);
        
        pack();
        setVisible(true);
    }
}
