import java.util.LinkedList;
import java.util.List;

/**
 * A class to embody a single genome in the genetic algorithm
 *
 */
public class Genome {

    // The list of all the node genes
    // The structure has the first `inputs` nodes being input nodes and the last `outputs` nodes being outputs
    public LinkedList<NodeGene> nodes;
    // An index indicating the last input node in `nodes`
    public int inputs;
    // An index indicating the first input node in `nodes`
    public int outputs;

    // The list of all connection genes
    public LinkedList<ConnectionGene> connections;

    // The stored fitness
    public int fitness = -1;


    /**
     * Builds an initial *blank* genome
     * This is just a genome with no connections or nodes beyond sensor and output nodes
     *
     */
    public Genome(List<NodeGene> sensorNodes, List<NodeGene> outputNodes) {
        nodes = new LinkedList<NodeGene>();
        nodes.addAll(sensorNodes);
        nodes.addAll(outputNodes);

        connections = new LinkedList<ConnectionGene>();
    }

    /**
     * Take in a seed for a snake game and play it to determine the fitness of this genome on that game
     *
     */
    public int evaluateNetwork(int snakeSeed) {
        return 0;
    }
}
