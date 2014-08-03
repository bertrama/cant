import java.util.*;

public class LearnNet extends CANTNet {

  public static int goalsActions = 2;

  public LearnNet(){
  }

  public LearnNet(String name, int cols, int rows, int topology){
    super(name,cols,rows,topology);
  }

  //need this to subclass experiment
   public void changePattern(int cantStep)
  {
      if (getCurrentPattern() < 0) setCurrentPattern(0);
      ((CANTPattern)patterns.get(getCurrentPattern())).arrange(getNeuronsToStimulate());
  }


  public void runAllOneStep(int CANTStep) {
    //This series of loops is really chaotic, but I needed to
    //get all of the propogation done in each net in step.
    CANT23Learn.runOneStepStart();

    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      LearnNet net = (LearnNet)eNum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      LearnNet net = (LearnNet)eNum.nextElement();
      net.setExternalActivation(CANTStep);
	}
    //net.propogateChange();  
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      LearnNet net = (LearnNet)eNum.nextElement();
      net.spontaneousActivate();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      LearnNet net = (LearnNet)eNum.nextElement();
      net.setNeuronsFired();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      LearnNet net = (LearnNet)eNum.nextElement();
      net.setDecay ();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      LearnNet net = (LearnNet)eNum.nextElement();
      net.spreadActivation();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      LearnNet net = (LearnNet)eNum.nextElement();
      net.setFatigue();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      LearnNet net = (LearnNet)eNum.nextElement();
      net.learn();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      LearnNet net = (LearnNet)eNum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      LearnNet net = (LearnNet)eNum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
//if (net.getName().compareTo("VerbNet") == 0)   System.out.println(net.neurons[0].getFatigue() +   " verb Neuron " + net.neurons[0].getActivation());
    }
  }


  public CANTNet getNewNet(String name,int cols, int rows,int topology) {
    LearnNet net = new LearnNet (name,cols,rows,topology);
    return (net);
  } 

  public void subclassLearn() {
    if (getName().compareTo("BaseNet") != 0)  return;
   
    for (int neuronNum = 0; neuronNum<getSize(); neuronNum++) {
      if (neurons[neuronNum].getFired()) { //only learn if it fires
	for (int synapseNum = 0; 
          synapseNum < neurons[neuronNum].getCurrentSynapses(); synapseNum++) {
          //only learn if it's from fact to module
          CANTNeuron toNeuron=neurons[neuronNum].synapses[synapseNum].toNeuron;
          if (toNeuron.parentNet.getName().compareTo("ActionNet") == 0) {
            //modify weight
            neurons[neuronNum].modifySynapticWeight(synapseNum);
	  }    
	}
      }
    }
  }

  //****set up connections between nets ***//
    private int neuronsInFactCA = 200;

    private void connectToOne(LearnNet toNet,
                              int fromCA,
                              int toCA,
                              int neuronsInFromCA,
                              int neuronsInToCA,
                              int numSynapses,
                              double weight) {
        for (int fromNeuron=fromCA*neuronsInFromCA;
             fromNeuron<(fromCA + 1)*neuronsInFromCA;fromNeuron++) {
            if (!neurons[fromNeuron].isInhibitory()) {
                for (int synapse=0;
                     synapse < numSynapses; synapse++) {
                    int toNeuron = ((int)(Math.random()*neuronsInToCA))
                        +(toCA*neuronsInToCA);
                    neurons[fromNeuron].addConnection(toNet.neurons[toNeuron],weight);
                }
            }
        }
    }


    public void connectFactToAction(LearnNet actionNet) {
        for (int fromCA = 0; fromCA<goalsActions; fromCA++) {
            for (int toCA = 0; toCA<goalsActions; toCA++) {
                connectToOne(actionNet,fromCA,toCA,200,200,10,0.1);
            }
        }
    }

    public void connectFactToExplore(LearnNet exploreNet) {
        for (int fromCA = 0; fromCA<goalsActions; fromCA++) {
            connectToOne(exploreNet,fromCA,0,200,400,10,0.1);
        }
    }

    public void connectExploreToAction(LearnNet actionNet) {
        for (int toCA = 0; toCA<goalsActions; toCA++) {
            connectToOne(actionNet,0,toCA,400,200,10,1.0);//0.8
        }
    }

    public void connectValueToExplore(LearnNet exploreNet) {
        connectToOne(exploreNet,0,0,400,400,10,-1.0);//Value -> Explore
    }


  //**** Set up initial topologies of nets.*****/
  private void setInputTopology(int CASize,double exciteWeight) {
    setConnections(0,size());
    for (int neuronNum=0; neuronNum<size(); neuronNum++) 
      {
      for (int synapseNum=0;synapseNum < neurons[neuronNum].getCurrentSynapses(); synapseNum++) 
        {
     	if ((neuronNum/CASize) == 
          (neurons[neuronNum].synapses[synapseNum].toNeuron.id /CASize)) 
	  {
      	  if (neurons[neuronNum].getInhibitory()) 
       	    neurons[neuronNum].synapses[synapseNum].setWeight(-0.1);
       	  else 
       	    neurons[neuronNum].synapses[synapseNum].setWeight(exciteWeight);
       	  }
       	else
       	  {
       	  if (neurons[neuronNum].getInhibitory()) 
       	    neurons[neuronNum].synapses[synapseNum].setWeight(-0.11);
       	  else 
       	    neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
       	  } 
        }
      }
  }


  //right from cabot1
    private void setActionTopology() {
        int neuronsInCA = 200;
        for (int neuron = 0; neuron < getSize(); neuron ++) {
            int CA = neuron/neuronsInCA;
            if (neurons[neuron].isInhibitory) {
                for (int synapse=0; synapse < 40*(1+goalsActions/2); synapse++) {
                    int toNeuron = (int) (Math.random()*getSize());
                    if ((toNeuron/neuronsInCA) != CA)
                        addConnection(neuron,toNeuron,3.0);//2.0
                }
            }
            else {
                for (int synapse=0; synapse < 20; synapse++) {
                    int toNeuron = (int)(Math.random()*neuronsInCA)+(CA*neuronsInCA);
                    addConnection(neuron,toNeuron,2.0);
                }
            }
        }
    }

    private static double normalRand (float mean, float variance) {
        double p = Math.random();
        return mean + Math.sqrt(variance) * Math.log(p / (1-p));
    }

    private void setGaussTopology() {
        for (int neuron = 0; neuron < getSize(); neuron++) {
            for (int synapse = 0; synapse < 100; synapse++) {
                // Normal (Gaussian) connectivity
                int toNeuron = (int) normalRand(neuron,getSize()/2) % getSize();
                if (toNeuron >= 0) toNeuron = toNeuron;
                else toNeuron = toNeuron + getSize();
                addConnection(neuron,toNeuron,1.0);
            }
        }
    }

  public void initializeNeurons() {
    //set up topologies.
    createNeurons();

    if (topology == 1){
      System.out.println("input topology ");
      setInputTopology(neuronsInFactCA,0.45);
    }
    /*
    else if (topology == 15){
      System.out.println("control topology ");
      setControlTopology();
    }
    */
    else if (topology == 16){
      System.out.println("action topology ");
      setActionTopology();
    }
    /*
    else if (topology == 18){
      System.out.println("module topology ");
      setModuleTopology();
    }
    */
    else if (topology == 25){
      System.out.println("explore topology ");
      setGaussTopology();
      }
    else System.out.println("bad toppology specified "+ topology);
  }


  public void kludge () {
  	System.out.println("Learn kludge ");
  }
  
  public void measure(int currentStep) {
    System.out.println("measure " + neurons[0].getActivation() + " " + 
      neurons[0].getFired() + " " + 
	  currentStep);
  }
}
