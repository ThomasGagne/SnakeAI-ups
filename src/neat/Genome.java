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

    public double fitness = -1;
    public HashMap<String, Double> mutationRates;
    public GenePool genePool;

    // A Random object to use so we don't have to keep making new Random's
    private Random rand;
    private double DeltaDisjoint = 2.0
    private double DeltaWeights = 0.4
    private double DeltaThreshold = 1.0


    /**
     * Builds an initial *blank* genome
     * This is just a genome with no connections or nodes beyond sensor and output nodes
     *
     */
    public Genome(List<NodeGene> sensorNodes, List<NodeGene> outputNodes, GenePool pool) {
        nodes = new LinkedList<NodeGene>();
        nodes.addAll(sensorNodes);
        nodes.addAll(outputNodes);
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
     * Take in a seed for a snake game and play it to determine the fitness of this genome on that game
     *
     */
    public int evaluateNetwork(int snakeSeed) {
        return 0;
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

        do{
            oldGene = connections.get(rand.nextInt(connections.size()));
        }
        while(!oldGene.enabled); // grab a random enabled node

        oldGene.enabled = false;

        NodeGene newNode = new NodeGene();
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
    public int compareTo(Genome g2){
      return this.fitness-g2.fitness;
    }
    public boolean sameSpecies(){
        double dd = DeltaDisjoint*disjoint(genome1.connections, genome2.connections);
        double dw = DeltaWeights*weights(genome1.connections, genome2.connnections);
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
        ConectionGene gene = genes2.get(i);
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
      int n = Math.max(genes1.size(), genes2.size())
      return disjointGenes / n;
    }
    private double weights(LinkedList<ConnectionGene> genes1, LinkedList<ConnectionGene> genes2)
    {
      HashMap<Integer, ConnectionGene> i2 = new HashMap<Integer, ConnectionGene>();
      for(int i = 0; i < genes2.size())
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
          sum = sum + Math.abs(gene.weight - gene2.weight);
          coincident = coincident + 1;
        }
      }
      return sum / coincident;
    }

}
