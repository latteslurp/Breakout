import objectdraw.*;
import java.awt.*;
import java.util.*;
import java.lang.*;

public class BreakoutGame extends ActiveObject{

    /*General*/
    private DrawingCanvas canvas;
    private Driver window;

    /*TURNS (Life)*/
    private int life;
    private Text lifeCount;

    /*SCORE*/
    private int score;
    private Text scoreCount;

    /*Invisible Frames*/
    private FilledRect leftFrame;
    private FilledRect rightFrame;
    private FilledRect topFrame;

    /*Paddle stuff*/
    private FilledRect paddle;
    private int edgePoint;

    /*Bricks stuff*/
    private ArrayList <FilledRect> bricks;
    private static int bricksRow = 10;
    private static int bricksColumn = 10;
    private static int totalBricks;

    /*ball stuff*/
    private static FilledOval ball;
    private double speedX;
    private double speedY;
    private double delayTime = 17;
    private Random rng;


    public BreakoutGame(Driver myWindow, Location initialPosition, DrawingCanvas myCanvas) {
        /*General*/
        this.window = myWindow;
        canvas = myCanvas;

        /*SCORE*/
        score = 0;
        scoreCount = new Text("Score Count: " + score,
                57*canvas.getWidth()/100, canvas.getHeight()/10, canvas);

        /*TURNS (LIFE)*/
        life = BreakoutProgram.NTURNS;
        lifeCount = new Text("Life Count: " + life,canvas.getWidth()/10, canvas.getHeight()/10, canvas);

        /*"Invisible" Frame*/
        leftFrame = new FilledRect(-10,0, 10,canvas.getHeight() - BreakoutProgram.PADDLE_HEIGHT,canvas);
        rightFrame = new FilledRect(canvas.getWidth(),0,
                10,canvas.getHeight() - BreakoutProgram.PADDLE_HEIGHT,canvas);
        topFrame = new FilledRect(0, -10, canvas.getWidth(), 10, canvas);

        /*Bricks*/
        Color clr = new Color (222, 204, 91);
        Color clr2 = new Color(225, 107, 109);
        Color clr3 = new Color (198, 161, 225);
        Color clr4 = new Color (114, 222, 147);
        Color clr5 = new Color (55, 225, 225);

        bricksColumn = BreakoutProgram.NBRICK_COLUMNS;
        bricksRow = BreakoutProgram.NBRICK_ROWS;
        totalBricks =  bricksColumn * bricksRow;
        bricks = new ArrayList<FilledRect>(totalBricks);
        double brickSide = 5;
        double brickTop = BreakoutProgram.BRICK_Y_OFFSET;
        edgePoint = canvas.getWidth()- (int)BreakoutProgram.PADDLE_WIDTH;


        for(int brickCountRow = 0; brickCountRow < bricksRow; brickCountRow++){
            for( int brickCountColumn = 0 ; brickCountColumn < bricksColumn; brickCountColumn++){
                bricks.add(new FilledRect(brickSide, brickTop, BreakoutProgram.BRICK_WIDTH,
                        BreakoutProgram.BRICK_HEIGHT, canvas));
                brickSide += BreakoutProgram.BRICK_WIDTH + BreakoutProgram.BRICK_SEP;
                /*COLOR PER ROWS*/
                for(int k = 0; k<bricks.size(); k++){
                    if(k<20){
                        bricks.get(k).setColor(clr);
                    }
                    else if(k<40){
                        bricks.get(k).setColor(clr2);
                    }
                    else if(k<60){
                        bricks.get(k).setColor(clr3);
                    }
                    else if(k<80){
                        bricks.get(k).setColor(clr4);
                    }
                    else{
                        bricks.get(k).setColor(clr5);
                    }
                }
            }
            brickTop = brickTop + BreakoutProgram.BRICK_HEIGHT + BreakoutProgram.BRICK_SEP;
            brickSide = 5;
        }

        /*Ball*/
        ball = new FilledOval(canvas.getWidth()/2, canvas.getHeight()/2,
                BreakoutProgram.BALL_RADIUS, BreakoutProgram.BALL_RADIUS, canvas);
        speedX = BreakoutProgram.VELOCITY_X_MIN;
        speedY = BreakoutProgram.VELOCITY_Y;


        /*Paddle*/
        paddle = new FilledRect((canvas.getWidth()-BreakoutProgram.PADDLE_WIDTH)/2,
                canvas.getHeight()-BreakoutProgram.PADDLE_HEIGHT,
                BreakoutProgram.PADDLE_WIDTH, BreakoutProgram.PADDLE_HEIGHT+ BreakoutProgram.BRICK_SEP, canvas);

        start(); // execute program
    }

    /*MENU CHECK*/
    public boolean isReady(){
        return true;
    };

    /*!!THE GAME!!
    !!!!!!!!!!!!!!*/
    public void run(){
        isReady();
        while (isReady()){
            //isReady = false;
            /*Paddle Movement*/
            Location mouseLocation = window.getMouseLocation(); //get mouse location with getter from Driver.java
            paddle.moveTo(Math.min(mouseLocation.getX(), edgePoint), paddle.getY());

            /*Ball Movement*/
            ball.move(speedX, speedY);
            pause(BreakoutProgram.DELAY);
            /*Collisions!!*/

            //PADDLE Collision
            if (ball.overlaps(paddle)){
                speedY *= -1.03;

                for(int i = 0; i<rng.nextInt(8)+9; i++){
                    int dx;
                    if(i*rng.nextInt()%3 == 0){
                        dx = -1;
                    }
                    else{
                        dx = 1;
                    }
                    speedX *= dx*1.009;
                }

                if(ball.getX() >= paddle.getWidth() || ball.getWidth() <= paddle.getX()){
                    //touches the outermost paddle side, slide the ball out!
                    speedY *= 1.03;
                    speedX *= 1.03;
                }

                ball.move(speedX, speedY);
            }

            //Frame Collisions
            if(ball.overlaps(leftFrame) || ball.overlaps(rightFrame)){
                speedX = speedX * -1;
                ball.move(speedX, speedY);
            }
            if(ball.overlaps(topFrame)){
                speedY = speedY*-1;
                ball.move(speedX, speedY);
            }

            //!!Bricks Collision!!
            Iterator <FilledRect> iter = bricks.iterator(); //iterate object!!
            while(iter.hasNext()){
                FilledRect f = iter.next();
                if(ball.overlaps(f)){
                    speedY *= -1;
                    iter.remove();
                    f.removeFromCanvas();
                    totalBricks -= 1;
                    score += 50;
                    int oldScore = score-50;
                    if(oldScore != score){
                        scoreCount.removeFromCanvas();
                    }
                    scoreCount = new Text("Score Count: " + score,
                            57*canvas.getWidth()/100, canvas.getHeight()/10, canvas);
                    System.out.println("Current total Bricks: " + totalBricks);
                    if(totalBricks == 0){
                        Color bgr = new Color(116, 222, 136);
                        canvas.setBackground(bgr);
                        ball.removeFromCanvas();
                        scoreCount.removeFromCanvas();
                        new Text("YOU WON!!! :D", 3*canvas.getWidth()/7, canvas.getHeight()/2, canvas);
                        new Text("Your score: " + score,
                                3*canvas.getWidth()/7, .54*canvas.getHeight(), canvas);
                        pause(200);
                        new Text("\"Click\" anywhere to reset the game.",
                                .3*canvas.getWidth(), canvas.getHeight()/3, canvas);
                        stop();
                        life = 3;
                    }
                }
            }

            /*TURNS (Life)*/
            if(ball.getY()>canvas.getHeight()){
                if (life != 0) {
                    life -=1;
                    ball.removeFromCanvas();
                    lifeCount.removeFromCanvas();
                    speedX = BreakoutProgram.VELOCITY_X_MIN;
                    speedY = BreakoutProgram.VELOCITY_Y;
                    lifeCount = new Text("Life Count: " + life,canvas.getWidth()/10, canvas.getHeight()/10, canvas);
                    pause(2000);
                    ball = new FilledOval(canvas.getWidth()/2, canvas.getHeight()/2,
                            BreakoutProgram.BALL_RADIUS, BreakoutProgram.BALL_RADIUS, canvas);
                }
                if(life == 0){
                    lifeCount.removeFromCanvas();
                    ball.removeFromCanvas();
                    scoreCount.removeFromCanvas();
                    new Text("GAME OVER!!", 3*canvas.getWidth()/7, 3*canvas.getHeight()/7, canvas);
                    pause(3000);
                    new Text("Your score: " + score,
                            3*canvas.getWidth()/7, .54*canvas.getHeight(), canvas);
                    new Text("\"Click\" anywhere to reset the game.",
                            .3*canvas.getWidth(), .62*canvas.getHeight(), canvas);
                    stop();
                    life = 3;
                }
            }
        }

    }

}
