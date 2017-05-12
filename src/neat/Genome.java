import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
/**
 * A class to embody a single genome in the genetic algorithm
 *
 */
public class Genome implements Comparable{

    // CONSTANTS
    public static double PERTURB_CHANCE = 0.90;
    // Should be odd
    public static int SNAKE_VIEW_SIZE = 9;
    // The number of steps the snake can go without getting the apple before we kill it
    public static int TOO_LONG_ALIVE = 1000;

    // The list of all the node genes
    // The structure has the first `inputs` nodes being input nodes and the last `outputs` nodes being outputs
    public LinkedList<NodeGene> nodes;

    // An index indicating the last input node in `nodes`
    public int inputs;
    // An index indicating the last output node in `nodes`
    // Note that `inputs + 1` is the index of the first output node
    public int outputs;

    // The list of all connection genes
    public LinkedList<ConnectionGene> connections;

    // The stored fitness
    public int fitness = -1;
    public HashMap<String, Double> mutationRates;
    public int globalRank;
    public GenePool genePool;

    // A Random object to use so we don't have to keep making new Random's
    private Random rand;
    private double DeltaDisjoint = 2.0;
    private double DeltaWeights = 0.4;
    private double DeltaThreshold = 1.0;


    /**
     * Builds an initial *blank* genome
     * This is just a genome with no connections or nodes beyond sensor and output nodes
     *
     */
    public Genome(GenePool pool) {
        nodes = new LinkedList<NodeGene>();

        // Add all the sensor and output nodes
        // We have (SNAKE_VIEW_SIZE * SNAKE_VIEW_SIZE) snake view nodes, 4 apple indicator nodes, and 2 output nodes
        for(int i = 0; i < (SNAKE_VIEW_SIZE * SNAKE_VIEW_SIZE) + 6; i++) {
            NodeGene inputNode = new NodeGene(i);
            nodes.add(inputNode);
        }

        inputs = (SNAKE_VIEW_SIZE * SNAKE_VIEW_SIZE) + 4;
        outputs = (SNAKE_VIEW_SIZE * SNAKE_VIEW_SIZE) + 6;

        ////////

        genePool = pool;
        connections = new LinkedList<ConnectionGene>();

        rand = new Random();

        // mutationRates
        mutationRates = new HashMap<String,Double>();
        mutationRates.put("connection", .75);
        mutationRates.put("link", (double)2);
        mutationRates.put("node", .5);
        mutationRates.put("step", .1);
        mutationRates.put("disable", .4);
        mutationRates.put("enable", .2);
    }

    /**
     * Builds a copy of the given genome
     *
     */
    public Genome(Genome other) {
        // Copy nodes
        nodes = new LinkedList<NodeGene>();
        for(NodeGene ng : other.nodes) {
            nodes.add(new NodeGene(ng));
        }

        genePool = other.genePool;

        // Copy connection genes
        connections = new LinkedList<ConnectionGene>();
        for(ConnectionGene cg : other.connections) {
            connections.add(cg);
        }

        rand = new Random();

        mutationRates = new HashMap<String, Double>(other.mutationRates);
    }

    /**
     * Take in a seed for a snake game and play it to determine the fitness of this genome on that game
     *
     */
    public int evaluateNetwork(int snakeSeed) {
        Snake snakeGame = new Snake();
        snakeGame.restart(snakeSeed);

        int score = 0;
        int timeSinceLastApple = 0;

        while(!snakeGame.lost) {
            // If snake gets the apple on this turn, add 100
            if(snakeGame.snakeX.getFirst() == snakeGame.appleX && snakeGame.snakeY.getFirst() == snakeGame.appleY) {
                score += 100;
                timeSinceLastApple = 0;
            }
            score++; // Increment score for staying alive
            timeSinceLastApple++;

            if(timeSinceLastApple > TOO_LONG_ALIVE) {
                // Kill the snake if it's been alive too long before getting another apple
                break;
            }

            // Step game
            snakeGame.step(getNetworkDecision(snakeGame));
        }

        return score;
    }

    public Snake.Direction getNetworkDecision(Snake snakeGame) {
        // What we're going to do is use recursion.
        // Starting with the two output nodes, we call a method which takes in a node
        // and it
        return Snake.Direction.UP;
    }

    public int[] getSnakeView(Snake snakeGame) {
        int[][] initialView = new int[SNAKE_VIEW_SIZE][SNAKE_VIEW_SIZE];

        // The Snake's head is at the center of the view size
        // So, when we have 5 for our view size, the snake's head is at (2,2)
        for(int i = 0; i < snakeGame.snakeX.size(); i++) {
            int gameX = snakeGame.snakeX.get(i);
            int gameY = snakeGame.snakeY.get(i);

            int snakeX = snakeGame.snakeX.getFirst();
            int snakeY = snakeGame.snakeY.getFirst();

            int snakeOffset = (int)SNAKE_VIEW_SIZE / 2;

            int distX = gameX - snakeX;
            int distY = gameY - snakeY;

            if(distX >= -snakeOffset && distX <= snakeOffset &&
               distY >= -snakeOffset && distY <= snakeOffset) {
                initialView[distX + snakeOffset][distY + snakeOffset] = 1;
            }
        }

        int[] finalView = new int[SNAKE_VIEW_SIZE * SNAKE_VIEW_SIZE];

        // Ugly code for turning game to match snake's head
        for(int i = 0; i < SNAKE_VIEW_SIZE; i++) {
            for(int j = 0; j < SNAKE_VIEW_SIZE; j++) {
                if(snakeGame.direction == Snake.Direction.UP) { // Up
                    finalView[j * SNAKE_VIEW_SIZE + i] = initialView[i][j];
                } else if(snakeGame.direction == Snake.Direction.RIGHT) { // Right
                    finalView[j * SNAKE_VIEW_SIZE + i] = initialView[j][SNAKE_VIEW_SIZE - 1 - i];
                } else if(snakeGame.direction == Snake.Direction.DOWN) { // Down
                    finalView[j * SNAKE_VIEW_SIZE + i] = initialView[SNAKE_VIEW_SIZE - 1 - i][SNAKE_VIEW_SIZE - 1 - j];
                } else if(snakeGame.direction == Snake.Direction.LEFT) { // Left
                    finalView[j * SNAKE_VIEW_SIZE + i] = initialView[SNAKE_VIEW_SIZE - 1 - j][i];
                }
            }
        }

        return finalView;
    }

    /**
     * Mutate the current node by splitting a connection and adding a new node
     *
     */
    public void nodeMutate() {
        if(connections.size() == 0) { // don't do anything if there are no genes
            return;
        }

        ConnectionGene oldGene;

        do {
            oldGene = connections.get(rand.nextInt(connections.size()));
        } while(!oldGene.enabled); // grab a random enabled node

        oldGene.enabled = false;

        NodeGene newNode = new NodeGene(oldGene.innovation);
        nodes.add(newNode);

        ConnectionGene newGene1 = new ConnectionGene(oldGene);
        newGene1.output = newNode;
        newGene1.weight = 1.0;
        newGene1.enabled = true;
        //genePool.genes.add(newGene1);
        genePool.getNewInnovation(newGene1);
        connections.add(newGene1);

        ConnectionGene newGene2 = new ConnectionGene(oldGene);
        newGene2.input= newNode;
        newGene2.enabled=true;
        connections.add(newGene2);
    }

    //mutates weights
    public void pointMutate() {
        ConnectionGene gene;
        for(int i = 0; i < connections.size(); i++){
            gene = connections.get(i);
            if (rand.nextDouble() < PERTURB_CHANCE) {
                gene.weight=gene.weight + (rand.nextDouble() * 2 - 1) * mutationRates.get("step");
            } else {
                gene.weight = rand.nextDouble() * 4 - 2;
            }
        }
    }

    // adds a random new link
    public void linkMutate() {
        //get a random two random nodes, one that can be an input and one that can't
        NodeGene node1;
        NodeGene node2;
        ConnectionGene newGene;
        do {
            node1 = nodes.get(rand.nextInt(nodes.size()));
            node2 = nodes.get(rand.nextInt(nodes.size() - inputs) + inputs); // Do not grab an input node

            newGene = new ConnectionGene();
            newGene.input = node1;
            newGene.output = node2;
            newGene.enabled = true;
            newGene.weight = rand.nextDouble() * 4 - 2;

            /*
            if(forceBias) {
                newGene.input = connections.get(0);
            }
            */

        } while(!containsLink(newGene)); //do until we get a new link

        //genePool.genes.add(newGene);
        genePool.getNewInnovation(newGene);
        connections.add(newGene);
    }

    //checks if there is already a link between two nodes
    public boolean containsLink(ConnectionGene link){
        ConnectionGene tempGene;
        for(int i = 0; i < connections.size(); i++) {
            tempGene= connections.get(i);

            if(tempGene.input == link.input && tempGene.output == link.output) {
                return true;
            }
        }

        return false;
    }

    //enables or disables a node
    public Genome enableDisableMutate(boolean enables) {
        LinkedList<ConnectionGene> candidates =new LinkedList<ConnectionGene>();
        ConnectionGene tempGene;

        for(int i = 0; i < connections.size(); i++) {
            tempGene= connections.get(i);
            if (tempGene.enabled == (!enables))
                candidates.add(tempGene);
        }
        ConnectionGene selected = candidates.get(rand.nextInt(candidates.size()));
        selected.enabled = enables;
        return this;
    }



    public void mutate(){
        double p;

        // edit the mutation rates
        Iterator keys = mutationRates.keySet().iterator();
        while (keys.hasNext()){
            String key = (String)keys.next();
            if (rand.nextDouble() - 1 > 0) {
                mutationRates.put(key, mutationRates.get(key) * .95);
            } else {
                mutationRates.put(key, mutationRates.get(key) * 1.05263);
            }
        }

        //mutate weights
        if(rand.nextDouble() < mutationRates.get("connection")) {
            pointMutate();
        }

        //mutate link
        p = mutationRates.get("link");
        while(p > 0) {
            if(rand.nextDouble() < p) {
                linkMutate();
            }
            p--;
        }

        p = mutationRates.get("node");
        while(p > 0) {
            if(rand.nextDouble() < p) {
                nodeMutate();
            }
            p--;
        }

        p = mutationRates.get("enable");
        while(p > 0) {
            if(rand.nextDouble() < p) {
                enableDisableMutate(true);
            }
            p--;
        }

        p = mutationRates.get("disable");
        while(p > 0) {
            if(rand.nextDouble() < p) {
                enableDisableMutate(false);
            }
            p--;
        }
    }

    @Override
    public int compareTo(Object o){

        return this.fitness - ((Genome)o).fitness;
    }

    public boolean sameSpecies(Genome genome2){
        double dd = DeltaDisjoint*disjoint(this.connections, genome2.connections);
        double dw = DeltaWeights*weights(this.connections, genome2.connections);
        return dd + dw < DeltaThreshold;
    }

    private double disjoint(LinkedList<ConnectionGene> genes1, LinkedList<ConnectionGene> genes2)
    {
      HashMap<Integer, Boolean> i1 = new HashMap<Integer, Boolean>();
      for(int i = 0; i < genes1.size(); i++)
      {
        ConnectionGene gene = genes1.get(i);
        i1.put(gene.innovation, true);
      }
      HashMap<Integer, Boolean> i2 = new HashMap<Integer, Boolean>();
      for(int i = 0; i < genes2.size(); i++)
      {
        ConnectionGene gene = genes2.get(i);
        i2.put(gene.innovation, true);
      }
      int disjointGenes = 0;
      for(int i = 0; i < genes1.size(); i++)
      {
        ConnectionGene gene = genes1.get(i);
        if(i2.get(gene.innovation) == null)
        {
          disjointGenes = disjointGenes + 1;
        }
      }
      for(int i = 0; i < genes2.size(); i++)
      {
        ConnectionGene gene = genes2.get(i);
        if(i1.get(gene.innovation) == null)
        {
          disjointGenes = disjointGenes + 1;
        }
      }
      int n = Math.max(genes1.size(), genes2.size());
      return disjointGenes / n;
    }

    private double weights(LinkedList<ConnectionGene> genes1, LinkedList<ConnectionGene> genes2)
    {
      HashMap<Integer, ConnectionGene> i2 = new HashMap<Integer, ConnectionGene>();
      for(int i = 0; i < genes2.size(); i++)
      {
        ConnectionGene gene = genes2.get(i);
        i2.put(gene.innovation, gene);
      }
      int sum = 0;
      int coincident = 0;
      for(int i = 0; i < genes1.size(); i++)
      {
        ConnectionGene gene = genes1.get(i);
        if(i2.get(gene.innovation) != null)
        {
          ConnectionGene gene2 = i2.get(gene.innovation);
          sum = sum + (int) Math.abs(gene.weight - gene2.weight);
          coincident = coincident + 1;
        }
      }
      return sum / coincident;
    }
}
