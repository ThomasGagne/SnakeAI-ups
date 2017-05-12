import java.util.LinkedList;
import java.util.Random;

public class Species {

    public Random rand = new Random();
    public int topFitness;
    public int staleness;
    public double averageFitness;
    public LinkedList<Genome> genomes = new LinkedList<Genome>();

    public static Genome crossover(Genome g1, Genome g2) {
        return null;
    }

    public void cull(boolean cullToTop){

    }

    public double calculateAverageFitness() {
        double sum = 0.0;

        for(int i = 0; i < genomes.size(); i++) {
            sum += genomes.get(i).fitness;
        }

        averageFitness = sum / genomes.size();

        return averageFitness;
    }

    public Genome breedChild(){
        return null;
    }

}
