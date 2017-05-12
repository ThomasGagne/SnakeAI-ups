
public class NodeGene {

    // The number of the given node
    public int number;

    // The value of the neuron, used when passing values through the network to compute outputs
    public double value = 0.0;

    // All input nodes feeding into this NodeGene
    // Not filled until network computation occurs
    public ArrayList<NodeGene> inputs;

    public boolean hasBeenComputed = false;

    public NodeGene(int number) {
        this.number = number;
        inputs = new ArrayList<NodeGene>();
    }

    public NodeGene(NodeGene nodeGene) {
        this.number = nodeGene.number;
    }
}
