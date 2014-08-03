
//This neuron will have 100 normal synapses and 100 fast bind
//synapses.  
//The fast bind synapses will just increase or decrease by the
//learning rate, but will decay over time.  
public class CANTNeuronFastBind extends CANTNeuron {
  private static int fastSynapseStart = 100;
  private int fastSynapses = 0;
	
  public int getFastSynapses() {return fastSynapses;}
  
  public CANTNeuronFastBind(int neuronID, CANTNet net) {
  	super(neuronID, net);
  }
  
  public void addFastSynapse(CANTNeuron toNeuron) {
  	boolean flag;
	double weight;
	
	if (isInhibitory())
	  weight = -0.01;
	else weight = 0.01;
	  
    //search for an existing connection.
    for (int fSynapse = 0 ; fSynapse < fastSynapses; fSynapse ++){
      if (synapses[fSynapse+fastSynapseStart].getTo() == toNeuron){
        flag= true;
        synapses[fSynapse+fastSynapseStart].setWeight(weight);
        flag=false;
        return;
      }
    }
	//This is a new connection
    synapses[fastSynapses+fastSynapseStart] = new Synapse(parentNet,this,toNeuron,weight);
    fastSynapses++;
  }
  
  public void spreadActivation(){
    //For each Synapse add the weight to its from node.
    for (int synapseIndex = 0; synapseIndex < getCurrentSynapses(); synapseIndex++){
      CANTNeuron toNeuron = synapses[synapseIndex].getTo();
      if(!isInhibitory)
        toNeuron.setActivation(toNeuron.currentActivation + synapses[synapseIndex].getWeight());
      else
        toNeuron.setActivation(toNeuron.currentActivation + (synapses[synapseIndex].getWeight()));
    }

    //spread down the fast bind synapses
    for (int synapseIndex = fastSynapseStart; synapseIndex < getFastSynapses() + fastSynapseStart; synapseIndex++){
      CANTNeuron toNeuron = synapses[synapseIndex].getTo();
      toNeuron.setActivation(toNeuron.currentActivation + synapses[synapseIndex].getWeight());
    }
  }
  
  public void fastLearn() {
    if (getFired())
//System.out.println("fastLearn " + id);	
	  for (int synapseIndex = fastSynapseStart; synapseIndex < getFastSynapses() + fastSynapseStart; synapseIndex++)
	  {
	  	CANTNeuron toNeuron = synapses[synapseIndex].getTo();
	  	if (toNeuron.getFired()) 
		  {
		  double weight = synapses[synapseIndex].getWeight();
		  weight += parentNet.getLearningRate();
	  	  synapses[synapseIndex].setWeight(weight);
		  }
	  } 
	//here's where the weight slowly fades
	else {
	  for (int synapseIndex = fastSynapseStart; synapseIndex < getFastSynapses() + fastSynapseStart; synapseIndex++){
	    double weight = synapses[synapseIndex].getWeight();
	    weight -= 0.01;
	    synapses[synapseIndex].setWeight(weight);
	  }
	}
  }	
  
}
