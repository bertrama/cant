
//This neuron has fast and slow bind synapses.  
//The fast bind synapses will just increase or decrease by the
//learning rate, but also loose weight each time they don't fire. 
public class CANTNeuronFastSlowBind extends CANTNeuron {
  private double fastBindWeightChange = 0.004; 
  int fastBindStart = -1; //The first fastBind synapse is here and all others
                           //at the offset specified by this var.
  
  public CANTNeuronFastSlowBind(int neuronID, CANTNet net) {
    super(neuronID, net);
  } 

  public CANTNeuronFastSlowBind(int neuronID,CANTNet net,double weightChange) {
    super(neuronID, net);
    fastBindWeightChange = weightChange; 
  } 

  public CANTNeuronFastSlowBind(int neuronID, CANTNet net, int synapses) {
    super(neuronID, net, synapses);
  } 

  public CANTNeuronFastSlowBind(int neuronID, CANTNet net, int synapses,
    double weightChange) {
    super(neuronID, net, synapses);
    fastBindWeightChange = weightChange; 
  } 

  public void addFastBindConnection(CANTNeuron toNeuron) {
    if (fastBindStart == -1) fastBindStart = getCurrentSynapses();
    addConnection(toNeuron,0.02);
  }

  //this is just learn4 from CANTNeuron modified to work on just
  //the slowbind synapses.
  public void slowLearn(){
    double totalConnectionStrength;
    double modification;
    double fromCompensatoryStrengthModifier,fromCompensatoryWeakModifier;

    totalConnectionStrength = getTotalConnectionStrength();
    fromCompensatoryWeakModifier = 
	   getWeakCompensatoryModifier(totalConnectionStrength);
    fromCompensatoryStrengthModifier = 
	   getStrengthCompensatoryModifier(totalConnectionStrength);
    //System.out.println("mods "+totalConnectionStrength+" "+fromCompensatoryWeakModifier+" "+fromCompensatoryStrengthModifier);

    //Just go up through the slow bind synapses which are first.
    int lastSlowBindSynapse = getCurrentSynapses();
    if (fastBindStart != -1) lastSlowBindSynapse = fastBindStart;

    //Test each Synapse from the active neuron
    for (int synap=0; synap < lastSlowBindSynapse; synap++) {
      double connectionStrength = synapses[synap].getWeight();
      CANTNeuron toNeuron = synapses[synap].getTo();

      //If both Neurons were active,
      if (toNeuron.getFired()) {
        if (!isInhibitory){
          modification = getIncreaseBase(connectionStrength);
          modification *= fromCompensatoryStrengthModifier;
          connectionStrength = connectionStrength+modification;
          synapses[synap].setWeight(connectionStrength);
          //System.out.println("Inc Exc "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification);
        }

        else{//decrease inhibition
          modification =getDecreaseBase(connectionStrength);
          modification *= fromCompensatoryWeakModifier;
          connectionStrength = connectionStrength-modification;
          synapses[synap].setWeight(connectionStrength);
          //System.out.println("dec Inh "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification);
        }
      } //end of to neuron active


      //if to Neuron is inactive
      else {
        if (!isInhibitory){
          modification =getDecreaseBase(connectionStrength);
          modification *= fromCompensatoryWeakModifier;
          connectionStrength = connectionStrength-modification;
          synapses[synap].setWeight(connectionStrength);
          //System.out.println("dec Exc "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification);
        }

        else {
          modification = getIncreaseBase(connectionStrength);
          modification *= fromCompensatoryStrengthModifier;
          connectionStrength = (connectionStrength)-modification;
          synapses[synap].setWeight(connectionStrength);
          //System.out.println("Inc Inh"+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification);
        }
      } // end of to Neuron inactive
    }
  }


  public void learn() {
    fastLearn();
    if( !getFired()) return;
    slowLearn();
  }

  public void fastLearn() {
    double weight = 0.0;
    if (fastBindStart == -1) return;
    if (getFired() && (!getInhibitory())) {
      for (int synapseIndex=fastBindStart;synapseIndex<getCurrentSynapses();
        synapseIndex++)
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
    } 
    //here's where the weight slowly fades
    else { //not fired 
      for (int synapseIndex=fastBindStart;synapseIndex<getCurrentSynapses();
        synapseIndex++)
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
