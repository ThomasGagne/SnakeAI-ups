import java.util.ArrayList;

public class NodeGene implements Comparable {

    // The number of the given node
    public int number;

    // The value of the neuron, used when passing values through the network to compute outputs
    public double value = 0.0;

    // All input nodes feeding into this NodeGene
    // Not filled until network computation occurs
    public ArrayList<NodeGene> inputs = new ArrayList<NodeGene>();

    public boolean hasBeenComputed = false;

    public boolean isOutput = false;

    public NodeGene(int number) {
        this.number = number;
    }

    public NodeGene(NodeGene nodeGene) {
        this.number = nodeGene.number;
        this.isOutput = nodeGene.isOutput;
    }

    @Override
    public int compareTo(Object o) {
        return this.number - ((NodeGene)o).number;
    }
}
