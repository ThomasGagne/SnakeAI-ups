
public class ConnectionGene {

    // The input node to this connection
    public NodeGene input;
    // The output node to this connection
    public NodeGene output;
    // The weight of the connection
    public double weight;
    // Whether or not this connection is enabled
    public boolean enabled;
    // The innovation number of this connection gene
    public int innovation;

    public ConnectionGene() {}

    // Clone constructor
    public ConnectionGene(ConnectionGene cloneFrom) {
        input = cloneFrom.input;
        output = cloneFrom.output;
        weight = cloneFrom.weight;
        enabled = cloneFrom.enabled;
        innovation = cloneFrom.innovation;
    }
}
