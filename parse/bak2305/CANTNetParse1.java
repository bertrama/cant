import java.util.*;

public class CANTNetParse1 extends CANTNet {
  private int currentWord = 0;
  private int lastWord = 2;
  
  public CANTNetParse1(){
  }

  public CANTNetParse1(String name,int cols, int rows,int topology){
  	super(name,cols,rows,topology);
	cyclesToStimulatePerRun = 1000;
  }

  public CANTNet getNewNet(String name,int cols, int rows,int topology){
  	CANTNetParse1 net = new CANTNetParse1 (name,cols,rows,topology);
	return (net);
	} 
	
	
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
		    neurons[neuronNum].synapses[synapseNum].setWeight(-0.9);
		  else 
		    neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
		  } 
	    }
	  }
  }
  
  private void setVerbTopology(int CASize) {
  	setConnections(0,size());
    for (int neuronNum=0; neuronNum<size(); neuronNum++) 
    {
    for (int synapseNum=0;synapseNum < neurons[neuronNum].getCurrentSynapses(); synapseNum++) 
      {
  	if ((neuronNum/CASize) == 
  	(neurons[neuronNum].synapses[synapseNum].toNeuron.id /CASize)) 
  	  {
  	  if (neurons[neuronNum].getInhibitory()) 
  	    neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
  	  else 
  	    neurons[neuronNum].synapses[synapseNum].setWeight(0.35-(Math.random()/4));
  	  }
  	else
  	  {
  	  if (neurons[neuronNum].getInhibitory()) 
  	    neurons[neuronNum].synapses[synapseNum].setWeight(-0.9);
  	  else 
  	    neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
  	  } 
      }
    }
  }

  //This is just the one from net with random inhibitory connections
  private void setConnections(int startNeuron, int endNeuron, int inhibitoryConnections) {
    int currentFromNeuron;
    int N1 = -1,N2 = -1,N3 = -1,N4 = -1;
    int cNeurons = size();
  
    //For Each Neuron
    for (currentFromNeuron = startNeuron ; currentFromNeuron < endNeuron;
         currentFromNeuron ++) {
      if (neurons[currentFromNeuron].isInhibitory())    
  	  {
	    setConnectionsRandomly(currentFromNeuron,inhibitoryConnections,-0.1);
  	  }
      else 
  	  {
        //Get it's neighbors Make N1 a long distance connection
        N1 = (int)(N1 + (Math.random() * size()))%size();
        N2 = getRightNeighbor(currentFromNeuron);
        N3 = getTopNeighbor(currentFromNeuron);
        N4 = getBottomNeighbor(currentFromNeuron);
        recursiveSetConnections(currentFromNeuron,N1,1);
        recursiveSetConnections(currentFromNeuron,N2,1);
        recursiveSetConnections(currentFromNeuron,N3,1);
        recursiveSetConnections(currentFromNeuron,N4,1);
  	  }
    }
  }
  
  //A stacktop element has a row (20 neurons) to activate the prior element
  //and a row for the next element.  These are not part of the CA, but take
  //activation from it.  
  private void setStackTopTopology(int CASize) {
  	setConnections(0,size(),80);
    for (int neuronNum=0; neuronNum<size(); neuronNum++) 
    {
    for (int synapseNum=0;synapseNum < neurons[neuronNum].getCurrentSynapses(); synapseNum++) 
      {
	  int fromCA = neuronNum/CASize;
	  int toNeuronNum = neurons[neuronNum].synapses[synapseNum].toNeuron.id;
	  int toCA = toNeuronNum /CASize;

      if (neuronNum%CASize < 20) 
	    //undone incomplete
//System.out.println(neuronNum + " from prior " + toNeuronNum);
  	    {
  	    if (neurons[neuronNum].getInhibitory()) 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
  	    else 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
  	    }
	  //If the next one comes on, it should be incrementing the stacktop
      else if (neuronNum%CASize >= (CASize-20)) 
  	    {
  	    if (fromCA == toCA) 
  	      {
  	      if (neurons[neuronNum].getInhibitory()) 
  	        neurons[neuronNum].synapses[synapseNum].setWeight(-0.5);
  	      else 
  	        neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
  	      }
  	    else if (fromCA == toCA-1) 
		  {
  	      if (neurons[neuronNum].getInhibitory()) 
  	        neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
  	      else 
  	        neurons[neuronNum].synapses[synapseNum].setWeight(0.99);
		  }
  	    }
      else if (toNeuronNum%CASize < 20) 
	    //undone incomplete
//System.out.println(neuronNum + " to prior  " + toNeuronNum);
      {
      if (neurons[neuronNum].getInhibitory()) 
        neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
      else 
        neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
      }
      else if (toNeuronNum%CASize >= (CASize-20)) 
	    //undone incomplete
//System.out.println(neuronNum + " to next " + toNeuronNum);
        {
        if (neurons[neuronNum].getInhibitory()) 
          neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
        else 
          neurons[neuronNum].synapses[synapseNum].setWeight(0.2);
        }
      else if (fromCA  == toCA)
  	    {
  	    if (neurons[neuronNum].getInhibitory()) 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
  	    else 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(0.1 + (Math.random()/2));
  	    }
	//moderate weights to those after
  	else if (fromCA == toCA-1)
  	  {
  	  if (neurons[neuronNum].getInhibitory()) 
  	    neurons[neuronNum].synapses[synapseNum].setWeight(-0.3);
  	  else 
  	    neurons[neuronNum].synapses[synapseNum].setWeight((Math.random()/2));
  	  }
  	else
  	  {
  	  if (neurons[neuronNum].getInhibitory()) 
	    {
  	    neurons[neuronNum].synapses[synapseNum].setWeight(-0.9);
//System.out.println(neuronNum+ " " + neurons[neuronNum].synapses[synapseNum].getTo().getId());
	    }
  	  else 
  	    neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
  	  } 
      }
    }
  }

  public void initializeNeurons() {
    neurons = new CANTNeuron[cols*rows];
    for(int i=0;i< cols*rows;i++)
      neurons[i] = new CANTNeuron(totalNeurons++,this);
    if (topology == 1){
      System.out.println("input parse topology ");
	  setInputTopology(150,0.4);
    }
    else if (topology == 2){
      System.out.println("verb parse topology ");
      setVerbTopology(300);
    }
    else if (topology == 3){
      System.out.println("other parse topology");
    }
    else if (topology == 4){
      System.out.println("stack top parse topology");
      setStackTopTopology(300);
    }
    else if (topology == 5){
      System.out.println("parse  rule topology");
      setVerbTopology(600);
    }
    else System.out.println("bad toppology specified "+ topology);
  }
  
  
  //**********Connect Nets to Each Other***********************************
  private void connectInputWordToVerb(int inputStart, int verbStart, CANTNetParse1 verbNet) {
  	int verbOffset;
    for (int inputNeuron = inputStart; inputNeuron < inputStart + 150; inputNeuron ++) 
	  {
	  for (int synapse = 0; synapse < 7; synapse++)
		{
		verbOffset = (int)(Math.random()*300);
		verbOffset += verbStart;
		if (!neurons[inputNeuron].isInhibitory())
          neurons[inputNeuron].addConnection(verbNet.neurons[verbOffset],0.5);
		}
	  }
  }

  public void connectInputToVerb(CANTNetParse1 verbNet) {
     connectInputWordToVerb(0,0,verbNet);
  }
  
  private void connectVerbToPushRule(CANTNetParse1 ruleNet) {
    for (int verbNeuron = 0; verbNeuron < size(); verbNeuron ++) 
    {
      for (int synapse = 0; synapse < 10; synapse++)
        {
        int ruleOffset = (int)(Math.random()*300);
        if (!neurons[verbNeuron].isInhibitory())
          neurons[verbNeuron].addConnection(ruleNet.neurons[ruleOffset],0.2);
        }
    }
  }
  
  public void connectVerbToRule(CANTNetParse1 ruleNet) {
    connectVerbToPushRule(ruleNet);
  }

  //Set up connections so that the push rule is called when stacktop is zero.
  private void connectStackTopZeroToPush(CANTNetParse1 ruleNet) {
    for (int stackTopNeuron = 0; stackTopNeuron < 200; stackTopNeuron ++) 
      {
      for (int synapse = 0; synapse < 10; synapse++)
  	    {
  	    int ruleOffset = (int)(Math.random()*300);
  	    if (!neurons[stackTopNeuron].isInhibitory())
           neurons[stackTopNeuron].addConnection(ruleNet.neurons[ruleOffset],0.2);
  	    }
      }
  }
  
  public void connectStackTopToRules(CANTNetParse1 ruleNet) {
    connectStackTopZeroToPush(ruleNet);
  }


  private void connectPushRuleToStackTop(CANTNetParse1 stackTopNet) {
    for (int ruleNeuron = 0; ruleNeuron < 300; ruleNeuron ++) 
      {
	  //each neuron needs to connect to each push row
	  //note that elements are 1 based for the maths below.
      for (int stackTopEl = 1; stackTopEl < 5; stackTopEl++)
        for (int synapse = 0; synapse < 3; synapse++)
        {
        int stackTopOffset = (stackTopEl*300 - (int)(Math.random()*20));
        if (!neurons[ruleNeuron].isInhibitory())
          neurons[ruleNeuron].addConnection(stackTopNet.neurons[stackTopOffset],0.4);
      }
    }
  }

  public void connectRulesToStackTop(CANTNetParse1 stackTopNet) {
    connectPushRuleToStackTop(stackTopNet);
  }

  /**** stuff other than connectivity**/
  //reset the word networks for the next word.  This might be cheating.
  private void resetWordNets () {
  	Enumeration enum = CANT23.nets.elements();
	
    while (enum.hasMoreElements()) {
      CANTNetParse1 net = (CANTNetParse1)enum.nextElement();
	  if ((net.getName().compareTo("BaseNet") ==0)
	  )
/*	   ||
          (net.getName().compareTo("NounNet") ==0) ||
          (net.getName().compareTo("VerbNet") ==0) ||
          (net.getName().compareTo("OWordNet") ==0))*/
	    net.clear();
    }  
  }
  
  public void readNextWord() {
  	if (getName().compareTo("BaseNet") != 0)
	  return;
  	if (currentWord < lastWord ) 
	  {
      currentWord++;
	  resetWordNets();
	  }
	else
	  System.out.println("Last Word Read");
  }
  
  public void changePattern(int cantStep)
  {
  	//undone this needs to be fixed up to move on stack reposition.
	if (getName().compareTo("BaseNet") == 0)
	  {
  	  setCurrentPattern(currentWord);
  	  ((CANTPattern)patterns.get(getCurrentPattern())).arrange(getNeuronsToStimulate());
	  }
	else if (getName().compareTo("StackTopNet") == 0)
	  {
	  setCurrentPattern(0);
	  ((CANTPattern)patterns.get(getCurrentPattern())).arrange(getNeuronsToStimulate());
	  }
	else   
  	  setCurrentPattern(0);
  	return;
  }

  public void measure(int currentStep) {
    System.out.println("measure " + neurons[0].getActivation() + " " + 
      neurons[0].getFired() + " " + 
	  currentStep);
  }

}