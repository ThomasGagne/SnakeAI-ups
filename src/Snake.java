import java.util.Random;
import java.util.LinkedList;

/**
 * Embodies a game of snake
 * Includes all the relevant information about the game state,
 * and includes a step() method for advancing the game
 */
public class Snake {

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    };

    // FINAL STATIC VALUES
    public static final int BOARD_SIZE = 30;

    ////////////////////////////////////////////////////

    // snakeX holds the x-coordinates of the snake's parts, snakeY the y-coordinates
    static public LinkedList<Integer> snakeX;
    static public LinkedList<Integer> snakeY;

    // The current position of the apple
    static public int appleX;
    static public int appleY;

    // The direction the snake is currently going
    // 0 means up, 1 means right, 2 means down, 3 means left
    static public Direction direction;

    // Whether the player has lost the game
    static public boolean lost;

    // Boolean for whether we've started our first movement, to prevent the snake losing
    // from when it's coiled at the start of the game
    static public boolean started;

    // Player's score
    static public int score;

    // Random generator
    static public Random rand;

    public void restart(long seed) {
        rand = new Random(seed);

        snakeX = new LinkedList<Integer>();
        snakeY = new LinkedList<Integer>();
        // Starting position is (15,15)
        snakeX.add(15);
        snakeY.add(15);

        direction = Direction.UP;

        appleX = rand.nextInt(BOARD_SIZE);
        appleY = rand.nextInt(BOARD_SIZE - 1) + 1;

        // Prevent the apple from spawning on top of the snake
        if(appleX == 15) {
            appleX = 10;
        }

        lost = false;
        started = false;

        score = 0;
    }

    // Advance the game by one tick, given the user's input
    // Contains the game's core logic
    public void step(Direction input) {

        // Change the snake's direction depending on the user's input
        // Don't let the snake move in the direction opposite it's traveling though
        if((direction == Direction.LEFT && input != Direction.RIGHT) ||
           (direction == Direction.RIGHT && input != Direction.LEFT) ||
           (direction == Direction.DOWN && input != Direction.UP) ||
           (direction == Direction.UP && input != Direction.DOWN)) {
            direction = input;
        }

        if(!lost) {
            // Move the snake forward
            switch(direction) {
            case UP:
                snakeX.push(snakeX.getFirst());
                snakeY.push(snakeY.getFirst() - 1);
                break;
            case RIGHT:
                snakeX.push(snakeX.getFirst() + 1);
                snakeY.push(snakeY.getFirst());
                break;
            case DOWN:
                snakeX.push(snakeX.getFirst());
                snakeY.push(snakeY.getFirst() + 1);
                break;
            case LEFT:
                snakeX.push(snakeX.getFirst() - 1);
                snakeY.push(snakeY.getFirst());
                break;

            }

            // Add the rest of the snake once we've made our first movement of the game
            if(!started) {
                // Snake length begins at 4
                snakeX.add(15);
                snakeX.add(15);
                snakeX.add(15);
                snakeY.add(15);
                snakeY.add(15);
                snakeY.add(15);

                started = true;
            }

            // If the snake's on top of the fruit, eat it, grow, and spawn a new fruit
            if(snakeX.getFirst() == appleX && snakeY.getFirst() == appleY) {
                score++;

                // Increase snake's length by 4
                Integer endX = snakeX.getLast();
                snakeX.add(endX);
                snakeX.add(endX);
                snakeX.add(endX);
                snakeX.add(endX);
                Integer endY = snakeY.getLast();
                snakeY.add(endY);
                snakeY.add(endY);
                snakeY.add(endY);
                snakeY.add(endY);

                // Randomly choose a position for the apple which isn't inside the snake's body
                // Perhaps could be done a little better; endgame might get slow
                boolean inSnake;
                do {
                    inSnake = false;

                    appleX = rand.nextInt(BOARD_SIZE);
                    appleY = rand.nextInt(BOARD_SIZE - 1) + 1;

                    for(int i = 0; i < snakeX.size(); i++) {
                        if(appleX == snakeX.get(i) && appleY == snakeY.get(i)) {
                            inSnake = true;
                        }
                    }
                } while(inSnake);
            }

            // End the game if the snake hits the border
            if(snakeX.getFirst() == -1 || snakeX.getFirst() == BOARD_SIZE ||
               snakeY.getFirst() == 0 || snakeY.getFirst() == BOARD_SIZE) {
                lost = true;
                snakeX.removeFirst();
                snakeY.removeFirst();
                return;
            }

            // End the game if the snake hits itself
            for(int i = 1; i < snakeX.size(); i++) {
                if(snakeX.getFirst() == snakeX.get(i) &&
                   snakeY.getFirst() == snakeY.get(i)) {
                    lost = true;
                }
            }

            // Remove the last piece of the snake
            snakeX.removeLast();
            snakeY.removeLast();
        }
    }
}
