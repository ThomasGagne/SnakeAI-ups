import java.util.ArrayList;
import java.util.HashMap;

public class GenePool {

    public HashMap<SimplifiedConnectionGene, Integer> geneToInnov;

    private int innovationNumber = 1;

    public GenePool() {
        geneToInnov = new HashMap<SimplifiedConnectionGene, Integer>();
    }

    public int getNewInnovation(ConnectionGene gene) {
        SimplifiedConnectionGene simp = new SimplifiedConnectionGene(gene);
        if(geneToInnov.containsKey(simp)) {
            return geneToInnov.get(simp);
        } else {
            innovationNumber++;
            geneToInnov.put(simp, innovationNumber);
            return innovationNumber;
        }
    }

}
