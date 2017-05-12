import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedList;

/**
 * Allows a human player to play the game of snake, using the game described in the Snake class
 *
 */
public class VisualNEAT extends JFrame implements ActionListener {

    // FINAL VALUES
    private static final int WINDOW_SIZE = 600;
    private static final int MENU_HEIGHT = 20;
    private static final int PIXEL_SIZE = 20;
    private static final int TIMER_TICK_RATE = 100;
    private static final Color[] COLORS = {new Color(137, 204, 195), new Color(0, 0, 0), new Color(204, 10, 10), new Color(195, 195, 195)};

    ////////////////////////////////////////////////////

    // Used for double-buffering
    static private Image offscreenBuffer;
    // The main background of the frame
    static private Image background;
    // The brain of the snake game
    static private Snake snakeGame;
    // A list of key inputs so they can propogate
    static private LinkedList<Snake.Direction> entered;
    // Whether or not the game has begun yet
    static private boolean title;
    // A boolean used to make a flickering effect upon loss
    static private boolean flicker;

    // Repaint the canvas
    public void paint(final Graphics gr) {
        Graphics2D g;

        if(offscreenBuffer == null) {
            offscreenBuffer = createImage(WINDOW_SIZE, WINDOW_SIZE + MENU_HEIGHT);

            background = createImage(WINDOW_SIZE, WINDOW_SIZE);
            g = (Graphics2D) background.getGraphics();

            // Draw onto the background here
            g.setColor(COLORS[0]);
            g.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);
        }

        g = (Graphics2D) offscreenBuffer.getGraphics();

        // Draw color for the bottom menu
        g.setColor(COLORS[3]);
        g.fillRect(0, WINDOW_SIZE, WINDOW_SIZE, MENU_HEIGHT);

        g.setColor(COLORS[1]);
        g.drawLine(0, WINDOW_SIZE, WINDOW_SIZE, WINDOW_SIZE);

        // Always draw the score
        g.drawString("SCORE:", 5, WINDOW_SIZE + 15);
        g.drawString(Integer.toString(snakeGame.score), 52, WINDOW_SIZE + 15);

        // Clear the space with the background
        g.drawImage(background, 0, 0, this);

        if(title) {
            g.drawString("PRESS ENTER TO START", 450, WINDOW_SIZE + 15);
        } else {
            if(snakeGame.lost) {
                g.drawString("GAME OVER. PRESS ENTER TO RESTART", 355, WINDOW_SIZE + 15);
            }

            // Draw the apple
            g.setColor(COLORS[2]);
            g.fillRect(snakeGame.appleX * PIXEL_SIZE, snakeGame.appleY * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);

            // Draw the snake
            if((snakeGame.lost && flicker) || !snakeGame.lost) {
                g.setColor(COLORS[1]);
            }

            int i;
            for(i = 0; i < snakeGame.snakeX.size(); i++) {
                g.fillRect(snakeGame.snakeX.get(i) * PIXEL_SIZE, snakeGame.snakeY.get(i) * PIXEL_SIZE,
                           PIXEL_SIZE, PIXEL_SIZE);
            }

            flicker = !flicker;
        }

        gr.drawImage(offscreenBuffer, 0, 0, this);
    }

    // The action performed with every tick rate
    public void actionPerformed(ActionEvent e) {
        if(!snakeGame.lost && !title) {
            // Process user input and step() game
            if(entered.size() != 0) {
                snakeGame.step(entered.removeFirst());
            } else {
                snakeGame.step(snakeGame.direction);
            }
        }
        repaint();
    }

    // Initializes the game
    public VisualNEAT() {
        // Set the window title
        super("Snake Game");

        Generation generation = new Generation(1);

        for(int i = 0; i < 30; i++) {
            System.out.println("Generating new generation " + i);
            generation = generation.createNextGeneration(5);
        }

        snakeGame = new Snake();
        snakeGame.restart(5);

        title = true;
        flicker = false;

        // A list of all the characters the user has entered, so they can back-propogate.
        entered = new LinkedList<Snake.Direction>();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WINDOW_SIZE, WINDOW_SIZE + MENU_HEIGHT);

        // Trigger running the actionPerformed() method at TIMER_TICK_RATE
        new Timer(TIMER_TICK_RATE, this).start();

        setVisible(true);
    }

    // Starts the game
    public static void main(String[] args) {
        new HumanPlayer();
    }
}
