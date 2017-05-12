import java.util.LinkedList;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Generation{

    public int STALE_CUTOFF = 15;
    public int POPULATION = 300;
    public int maxFitness;
    public Random rand = new Random();
    public LinkedList<Species> species = new LinkedList<Species>();

    public void rankGlobally() {
        LinkedList<Genome> ranker = new LinkedList<Genome>();
        for (int i = 0; i < species.size(); i++){
            for(int j = 0; j < species.get(i).genomes.size(); j++){
                ranker.add(species.get(i).genomes.get(j));
            }
        }

        Collections.sort(ranker);
        for (int i = 0; i < ranker.size(); i++){
            ranker.get(i).globalRank = i;
        }
    }

    public void removeStaleSpecies() {
        int tempFitness;
        int maxFitness = -1;
        LinkedList<Species> survived = new LinkedList<Species>();

        for (int i = 0; i < species.size(); i++) {
            int max = -1;
            for(int j = 0; j < species.get(i).genomes.size(); j++){
                tempFitness = species.get(i).genomes.get(j).fitness;
                if(max < tempFitness) {
                    max = tempFitness;
                }
            }

            if(max > species.get(i).topFitness) {
                species.get(i).topFitness = max;
                species.get(i).staleness = 0;
            } else{
                species.get(i).staleness++;
            }

            if(species.get(i).staleness < STALE_CUTOFF || max >= maxFitness) {
                survived.add(species.get(i));
            }
        }

        species = survived;
    }

    public void removeWeakSpecies(double totalAverageFitness) {
        LinkedList<Species> survived = new LinkedList<Species>();

        for(int i = 0; i < species.size(); i++) {
            if(Math.floor(species.get(i).averageFitness * POPULATION / totalAverageFitness) >= 1) {
                survived.add(species.get(i));
            }
        }

        species = survived;
    }

    public Generation createNextGeneration(List<Species> species, int maxFitness) {
        this.maxFitness=maxFitness;

        for(int i = 0; i < species.size(); i++) {
            species.get(i).cull(false);
        }

        rankGlobally();
        removeStaleSpecies();
        rankGlobally();
        Genome tempGenome;
        double sum = 0.0;

        for(int i = 0; i < species.size(); i++){
            sum += species.get(i).calculateAverageFitness();
        }

        removeWeakSpecies(sum);
        LinkedList<Genome> children = new LinkedList<Genome>();
        int childNumber;

        for(int i = 0; i < species.size(); i++) {
            childNumber = (int) Math.floor(species.get(i).averageFitness * POPULATION / sum) - 1;

            for(int j = 0; j < childNumber; j++) {
                children.add(species.get(i).breedChild());
            }
        }

        for(int i = 0; i < species.size(); i++) {
            species.get(i).cull(true);
        }

        while(children.size() + species.size() < POPULATION) {
            children.add(species.get(rand.nextInt(species.size())).breedChild());
        }

        for(int i = 0; i < children.size(); i++) {
            addToSpecies(children.get(i));
        }

        return this;
    }

    public void addToSpecies(Genome child) {
        boolean found = false;

        for(int i = 0; i < species.size(); i++){
            if(!found && species.get(i).genomes.get(0).sameSpecies(child)){//match!
                found=true;
                species.get(i).genomes.add(child);
            }
        }

        if (!found){//no match
            Species newSpecies = new Species();
            newSpecies.genomes.add(child);
            species.add(newSpecies);
        }

    }

}
