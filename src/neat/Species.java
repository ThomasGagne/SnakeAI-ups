import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.Collections;

public class Species {

    public Random rand = new Random();
    public int topFitness;
    public int staleness;
    public double averageFitness;
    public double CrossoverChance = 0.75;
    public LinkedList<Genome> genomes = new LinkedList<Genome>();


    public static Genome crossover(Genome g1, Genome g2) {
        if(g2.fitness > g1.fitness) {
            Genome tempg = g1;
            g1 = g2;
            g2 = tempg;
        }

        Genome child = new Genome(g1.genePool);
        HashMap<Integer, ConnectionGene> innovations2 = new HashMap<Integer, ConnectionGene>();
        for(int i = 0; i < g2.connections.size(); i++) {
            ConnectionGene gene = g2.connections.get(i);
            innovations2.put(gene.innovation, gene);
        }

        for(int i = 0; i < g1.connections.size(); i++) {
            ConnectionGene gene1 = g1.connections.get(i);
            ConnectionGene gene2 = innovations2.get(gene1.innovation);
            if(gene2 != null && Math.random() == 1 && gene2.enabled) {
                    child.connections.add(new ConnectionGene(gene2));
            }
            else {
                child.connections.add(new ConnectionGene(gene1));
            }
        }

        child.mutationRates = new HashMap<String, Double>(g1.mutationRates);

    	return child;
    }


    public void cull(boolean cullToTop){
      Collections.sort(genomes);
      if(cullToTop)
      {
        for(int i = 1; i < genomes.size(); i++)
        {
          genomes.remove(i);
        }
      }
      else
      {
        for(int i = 0; i < genomes.size() / 2; i++)
        {
          genomes.remove((genomes.size() - 1) - i);
        }
      }
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

        Genome g3;
        double randChance = rand.nextDouble();
        if(randChance < CrossoverChance) {
          Genome g1 = genomes.get(rand.nextInt(genomes.size()));
          Genome g2 = genomes.get(rand.nextInt(genomes.size()));
          g3 = crossover(g1, g2);
        }
        else
        {
          Genome g1 = genomes.get(rand.nextInt(genomes.size()));
          g3 = new Genome(g1);
        }
        g3.mutate();

        return g3;
    }

}
