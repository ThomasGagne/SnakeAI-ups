
// A simplified version of a ConnectionGene, which holds only the start and endpoints of the connection gene
public class SimplifiedConnectionGene {

    public int input;
    public int output;

    public SimplifiedConnectionGene(ConnectionGene gene) {
        input = gene.input.number;
        output = gene.output.number;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() == this.getClass()) {
            SimplifiedConnectionGene other = (SimplifiedConnectionGene)obj;
            return other.input == this.input && other.output == this.output;
        }

        return false;
    }

}
