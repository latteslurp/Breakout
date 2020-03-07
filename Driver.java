import objectdraw.*;

import java.awt.*;

public class Driver extends WindowController {

    private Location mouseLocation;
    private boolean click;
    private int count = 1;
    private boolean gameCheck;

    private Text beginPrompt;

    public void begin(){
        beginPrompt = new Text("Click to begin!", 3*canvas.getWidth()/7, canvas.getHeight()/2, canvas);
    }

    public void onMouseClick(Location point){
        canvas.setBackground(Color.white);
        canvas.clear();
        if(true){
            BreakoutGame startGame = new BreakoutGame(this, point, canvas);
        }
        else{
            canvas.clear();
            new Text("Error encountered!" ,canvas.getWidth()/3, canvas.getHeight()/2, canvas);
            System.out.println("Error encountered.");
        }
        //click = false;
    };

    public void onMouseMove(Location point) {
        mouseLocation = new Location (point);
    }

    public Location getMouseLocation(){//return method to track the mouse movement
        return mouseLocation;
    }

    public static void main(String[] args) {
        new Driver().startController(420, 600);
    }
}
