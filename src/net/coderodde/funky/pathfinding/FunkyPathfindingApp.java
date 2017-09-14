package net.coderodde.funky.pathfinding;

public final class FunkyPathfindingApp {

    public FunkyPathfindingApp() {
        FunkyPathfindingFrame frame = new FunkyPathfindingFrame();
        FunkyPathfindingOptionsFrame optionsFrame = 
                new FunkyPathfindingOptionsFrame(frame, frame.getPanel());
        
    }
    
    public static void main(String[] args) {
        FunkyPathfindingApp app = new FunkyPathfindingApp();
    }
}
