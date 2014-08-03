
//This neuron will have fast bind synapses.  
//The fast bind synapses will just increase or decrease by the
//learning rate, but also loose weight each time they don't fire. 
public class CANTNeuronFastBind extends CANTNeuron {
  
  public CANTNeuronFastBind(int neuronID, CANTNet net) {
  	super(neuronID, net);
  } 

  public CANTNeuronFastBind(int neuronID, CANTNet net, double weightChange) {
    super(neuronID, net);
    fastBindWeightChange = weightChange; 
  } 

  public CANTNeuronFastBind(int neuronID ,CANTNet net, double weightChange, 
    int synapsesPerNeuron) {
    super(neuronID,net,synapsesPerNeuron);
    fastBindWeightChange = weightChange; 
  }

  private double fastBindWeightChange = 0.004;
  
  public void fastLearn() {
    double weight = 0.0;
    if (getFired() && (!getInhibitory()))
//System.out.println("fastLearn " + id);	
      for (int synapseIndex=0;synapseIndex<getCurrentSynapses();synapseIndex++)
	  {
	  CANTNeuron toNeuron = synapses[synapseIndex].getTo();
	  if (toNeuron.getFired()) 
	     {
		  weight = synapses[synapseIndex].getWeight();
		  weight += parentNet.getLearningRate();
		  if (weight > 1.0) weight = 1.0;
	  	  synapses[synapseIndex].setWeight(weight);
		  }
	  } 
	//here's where the weight slowly fades
	else //not fired
	{
	  for (int synapseIndex = 0; synapseIndex < getCurrentSynapses(); synapseIndex++)
	  {
	    weight = synapses[synapseIndex].getWeight();
		if (isInhibitory()) 
		  {
		  weight += fastBindWeightChange;
		  if (weight > 0) weight = -.001;
		  }
		else   
		  {
          weight -= fastBindWeightChange;
	      if (weight < 0) weight = .001;
	      }
	    synapses[synapseIndex].setWeight(weight);
	  }
	}
  }	
  
}
