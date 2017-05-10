import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import Java.util.Hashmap;
import java.util.Set;
import java.util.Iterator;
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
    public Hashmap<String,Float> mutationRates;
    public float PERTURB_CHANCE = 0.90;
    public GenePool g;

    /**
     * Builds an initial *blank* genome
     * This is just a genome with no connections or nodes beyond sensor and output nodes
     *
     */
    public Genome(List<NodeGene> sensorNodes, List<NodeGene> outputNodes, GenePool pool) {
        nodes = new LinkedList<NodeGene>();
        nodes.addAll(sensorNodes);
        nodes.addAll(outputNodes);
        g=pool;
        connections = new LinkedList<ConnectionGene>();
        //mutationRates
        mutationRates= new Hashmap<String,Float>();
        mutationRates.put("connection",.75);
        mutationRates.put("link",2);
        mutationRates.put("node",.5);
        mutationRates.put("step",.1);
        mutationRates.put("disable",.4);
        mutationRates.put("enable",.2);
      }
    /**
     * Take in a seed for a snake game and play it to determine the fitness of this genome on that game
     *
     */
    public int evaluateNetwork(int snakeSeed) {
        return 0;
    }
    //mutates the genome by adding a node
    public Genome nodeMutate() {
      if connections.size()==0; // don't do anything if there are no genes
      return null;
      Randomrand = new Random();
      ConnectionGene oldGene;
      do{
      oldGene = connections.get(rand.nextInt(connections.size()));
      }
      while (!oldGene.enabled) // grab a random enabled node
      oldGene.enabled=false;

      NodeGene newNode = new NodeGene();
      nodes.add(newNode);

      ConnectionGene newGene1 = oldGene.clone();
      newGene1.output= newNode;
      newGene1.weight= 1.0;
      newGene1.enabled=true;
      g.genes.add(newGene1);
      connections.add(newGene1);

      ConnectionGene newGene2 = oldGene.clone();
      newGene2.input= newNode;
      newGene2.enabled=true;
      connections.add(newGene2);
      return this;
    }
    //mutates weights
    public Genome pointMutate() {
      Random rand = new Random();
      ConnectionGene gene;
        for (int i=0; i<connections.size();i++){
          gene = connections.get(i);
          if (rand.nextFloat()<PERTURB_CHANCE)
          gene.weight=gene.weight+(rand.nextFloat()*2-1)*mutationRates.get("step");
          else
          gene.weight = rand.nextFloat()*4-2;
        }

      return this;
    }
    // adds a random new link
    public Genome linkMutate(forceBias) {
      //get a random two random nodes, one that can be an input and one that can't
      Random rand = new Random();
      NodeGene node1;
      NodeGene node2;
      ConnectionGene newGene;
      do{
        node1= nodes.get(Math.round(rand.nextFloat()*nodes.size()));//can be input
        node2= nodes.get(Math.round(rand.nextFloat()*(nodes.size-inputs)+inputs));//cannot be input
        newGene= new ConnectionGene();
        newGene.input= node1;
        newGene.output= node2;
        newGene.enabled=true;
        newGene.weight = rand.nextFloat()*4-2;
        if (forceBias)
          newGene.input=connections.get(0);
      }while (!containsLink(newGene)) //do until we get a new link
      g.genes.add(newGene);
      connections.add(newGene);
      return this;
    }
  //checks if there is already a link between two nodes
    public boolean containsLink(ConnectionGene link){
      ConnectionGene tempGene;
        for (int i=0; i<connections.size();i++){
              tempGene= connections.get(i);
                if (tempGene.input == link.input && gene.output == link.output)
                        return true;
      }
      else
        return false;
    }
    //enables or disables a node
    public Genome enableDisableMutate(boolean enables) {
      LinkedList candidates =new LinkedList<ConnectionGene>();
      ConnectionGene tempGene;
      for (int i=0; i<connections.size();i++){
          tempGene= connections.get(i);
          if (tempGene.enabled == (!enables))
          candidates.add(tempGene);
      }
    Random rand = new Random();
    ConnectionGene selected = candidates.get(Math.round(rand.nextFloat()*candidates.size()));
    selected.enabled = enables;
    return this;
    }



    public Genome mutate(genome){
      Random rand = new Random;
      float p;
      //edit the mutation rates
      Iterator keys = mutationRates.keys().iterator();
      while (keys.hasNext()){
        String key = keys.next()
        if (rand.nextFloat()-1>0)
          mutationRates.put(key,mutationRates.get(key)*.95);
        else
          mutationRates.put(key,mutationRates.get(key)*1.05263);
      }
      //mutate weights
      if (math.random() < genome.mutationRates.get("connection"))
              pointMutate();
      //mutate link
      p = mutationRates.get("link");
      while (p>0) {
        if (rand.nextFloat()<p)
        linkMutate();
        p--;
      }
      p = mutationRates.get("node");
      while (p>0) {
        if (rand.nextFloat()<p)
        nodeMutate();
        p--;
      }
      p = mutationRates.get("enable");
      while (p>0) {
        if (rand.nextFloat()<p)
        enableDisableMutate(true);
        p--;
      }
      p = mutationRates.get("disable");
      while (p>0) {
        if (rand.nextFloat()<p)
        enableDisableMutate(false);
        p--;
      }
      return this;
    }

}
