
public class NEAT {


    public static void main(String[] args) {
        Generation generation = new Generation(1);

        for(int i = 0; i < 30; i++) {
            System.out.println("Generating new generation " + i);
            generation = generation.createNextGeneration(5);
        }

        for(int i = 0; i < generation.species.size(); i++) {
            for(int j = 0; j < generation.species.get(i).genomes.size(); j++) {
                System.out.println("fitness: " + generation.species.get(i).genomes.get(j).fitness);
                System.out.println("size: " + generation.species.get(i).genomes.get(j).connections.size());
                System.out.println();
            }
        }

        System.out.println("NUm species: " + generation.species.size());
    }
}
