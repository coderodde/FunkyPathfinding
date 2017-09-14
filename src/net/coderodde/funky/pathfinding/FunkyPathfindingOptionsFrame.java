
package net.coderodde.funky.pathfinding;

import java.awt.GridLayout;
import java.awt.Label;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

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
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        buttonDrawWalls.setEnabled(false);
        buttonStop.setEnabled(false);
        buttonReset.setEnabled(false);
        
        setButtonActionListeners();
        
        GridLayout layout = new GridLayout(9, 1);   
        setLayout(layout);
        addButtons();
        
        pack();
        setVisible(true);
    }
    
    private void addButtons() {
        getContentPane().add(buttonDrawWorld);
        getContentPane().add(buttonDrawWalls);
        getContentPane().add(buttonClear);
        getContentPane().add(new Label());
        getContentPane().add(buttonRun);
        getContentPane().add(buttonStop);
        getContentPane().add(buttonReset);
        getContentPane().add(new JLabel("Choose algorithm:", 
                                        SwingConstants.CENTER));
        getContentPane().add(comboBoxAlgorithm);
    }
    
    private void setButtonActionListeners() {
        this.buttonDrawWalls.addActionListener((e) -> {
            this.buttonDrawWalls.setEnabled(false);
            this.buttonDrawWorld.setEnabled(true);
            this.buttonDrawWorld.setFocusable(true);
            funkyPathfindingPanel.setDrawingMode(DrawingMode.SET_WALL);
        });
        
        this.buttonDrawWorld.addActionListener((e) -> {
            this.buttonDrawWorld.setEnabled(false);
            this.buttonDrawWalls.setEnabled(true);
            this.buttonDrawWalls.setFocusable(true);
            funkyPathfindingPanel.setDrawingMode(DrawingMode.REMOVE_WALL);
        });
        
        this.buttonClear.addActionListener((e) -> {
            funkyPathfindingPanel.clearAllWalls();
        });
        
        this.buttonRun.addActionListener((e) -> {
            this.buttonRun.setEnabled(false);
            this.buttonStop.setEnabled(true);
            this.buttonReset.setEnabled(true);
        });
        
        this.buttonReset.addActionListener((e) -> {
            this.buttonRun.setEnabled(true);
            this.buttonStop.setEnabled(false);
            this.buttonReset.setEnabled(false);
        });
        
        this.buttonStop.addActionListener((e) -> {
            this.buttonRun.setEnabled(true);
            this.buttonStop.setEnabled(false);
            this.buttonReset.setEnabled(true);
        });
    }
}
