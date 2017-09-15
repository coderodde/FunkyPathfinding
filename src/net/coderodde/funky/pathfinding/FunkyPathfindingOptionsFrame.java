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

    private static final String ASTAR_NAME      = "A*";
    private static final String NBASTAR_NAME    = "NBA*";
    private static final String PHBA_NAME       = "PHBA";
    private static final String DIJKSTRA_NAME   = "Dijkstra";
    private static final String BIDIJKSTRA_NAME = "Bidirectional Dijkstra";
    
    private final JButton buttonDrawWorld;
    private final JButton buttonDrawWalls;
    private final JButton buttonClear;
    private final JButton buttonRun;
    private final JButton buttonReset;
    private final String[] comboBoxOptions = {
                            ASTAR_NAME,
                            NBASTAR_NAME,
                            PHBA_NAME,
                            DIJKSTRA_NAME,
                            BIDIJKSTRA_NAME 
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
        this.buttonRun         = new JButton("Start");
        this.buttonReset       = new JButton("Reset");
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        buttonDrawWalls.setEnabled(false);
        buttonReset.setEnabled(false);
        
        setButtonActionListeners();
        
        GridLayout layout = new GridLayout(8, 1);   
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
            this.buttonReset.setEnabled(true);
            
            
            funkyPathfindingPanel.search(getPathfinderFromSelection());
        });
        
        this.buttonReset.addActionListener((e) -> {
            this.buttonRun.setEnabled(true);
            this.buttonReset.setEnabled(false);
            
            funkyPathfindingPanel.requestExit();
            funkyPathfindingPanel.reset();
        });
    }
    
    private AbstractPathfinder getPathfinderFromSelection() {
        switch ((String) comboBoxAlgorithm.getSelectedItem()) {
            case ASTAR_NAME:
                return new AStarPathfinder();
                
            case DIJKSTRA_NAME:
                return new DijkstraPathfinder();
                
            default:
                throw new UnsupportedOperationException(
                        "Unknown pathfinder name.");
        }
    }
}
