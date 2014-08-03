import java.util.*;

public class VBNet extends CANTNet {
  public VBNet(){
  }

  public VBNet(String name,int cols, int rows,int topology){
  	super(name,cols,rows,topology);
  }

  public CANTNet getNewNet(String name,int cols, int rows,int topology){
  	VBNet net = new VBNet (name,cols,rows,topology);
	return (net);
	}
	

  public void runAllOneStep(int CANTStep) {
    //This series of loops is really chaotic, but I needed to
    //get all of the propogation done in each net in step.
    CANT23VB.runOneStepStart();
	
    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      VBNet net = (VBNet)eNum.nextElement();
      net.changePattern(CANTStep);
    }

    eNum = CANT23.nets.elements();	
    while (eNum.hasMoreElements()) {
      VBNet net = (VBNet)eNum.nextElement();
      net.setExternalActivation(CANTStep);
    }

	  //net.propogateChange();  
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      VBNet net = (VBNet)eNum.nextElement();
      net.spontaneousActivate();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      VBNet net = (VBNet)eNum.nextElement();
      net.setNeuronsFired();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      VBNet net = (VBNet)eNum.nextElement();
      net.setDecay ();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      VBNet net = (VBNet)eNum.nextElement();
      net.spreadActivation();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      VBNet net = (VBNet)eNum.nextElement();
      net.setFatigue();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      VBNet net = (VBNet)eNum.nextElement();
      net.learn();
	  if (CANT23VB.runFastBindTest) 
        net.fastLearn(0,net.getSize());
	  }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      VBNet net = (VBNet)eNum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      VBNet net = (VBNet)eNum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
    }
  }
  
  //for fastbind neurons to learn you need this.
  private void fastLearn(int firstFast, int lastFast) {
    for (int neuronIndex = 0; neuronIndex < size(); neuronIndex++) 
      {
      if (neurons[neuronIndex] instanceof CANTNeuronFastBind) 
        ((CANTNeuronFastBind)neurons[neuronIndex]).fastLearn();
      }
  }
  

  ////////These are the inter net connections for the fb system	
  public void connectNumberToLetter(VBNet letterNet) {
    for (int neuron = 0; neuron < getSize(); neuron ++)   
	  {
	  if (isFastBind(neuron)) 
	    {
	    for (int synapse = 0; synapse < 60; synapse ++)
	      {
	      int toNeuron= (int) (Math.random()*letterNet.getSize());
	      neurons[neuron].addConnection(letterNet.neurons[toNeuron],0.02);
	      }
	    }
	  }
  }
	

  public void connectLetterToNumber(VBNet numberNet) {
    for (int neuron = 0; neuron < getSize(); neuron ++)   
      {
      if (isFastBind(neuron)) 
        {
        for (int synapse = 0; synapse < 60; synapse ++)
          {
          int toNeuron= (int) (Math.random()*numberNet.getSize());
          neurons[neuron].addConnection(numberNet.neurons[toNeuron],0.02);
          }
        }
      }
  }
  
  ////////These are the inter net connections for the ltp system	
  public void connectLetterToBind(VBNet bindNet) {
    for (int neuron = 0; neuron<getSize();neuron++) 
      {
//      if (!neurons[neuron].isInhibitory())        {
     	for (int synapse = 0; synapse < 16; synapse ++)
      	  {
       	  int toNeuron= (int) (Math.random()*bindNet.getSize());
       	  neurons[neuron].addConnection(bindNet.neurons[toNeuron],0.02);
       	  }
//        }
      }
  }
  
  public void connectNumberToBind(VBNet bindNet) {
    for (int neuron = 0; neuron<getSize();neuron++) 
      {
//      if (!neurons[neuron].isInhibitory())        {
   	    for (int synapse = 0; synapse < 16; synapse ++)
    	  {
     	  int toNeuron= (int) (Math.random()*bindNet.getSize());
     	  neurons[neuron].addConnection(bindNet.neurons[toNeuron],0.02);
     	  }
//        }
      }
  }

  public void connectBindToLetter(VBNet letterNet) {
    for (int neuron = 0; neuron<getSize();neuron++) 
      {
  //    if (!neurons[neuron].isInhibitory())        {
   	    for (int synapse = 0; synapse < 15; synapse ++)
    	  {
     	  int toNeuron= (int) (Math.random()*letterNet.getSize());
     	  neurons[neuron].addConnection(letterNet.neurons[toNeuron],0.02);
     	  }
//        }
      }
  }

  public void connectBindToNumber(VBNet numberNet) {
    for (int neuron = 0; neuron<getSize();neuron++) 
      {
//      if (!neurons[neuron].isInhibitory())        {
   	    for (int synapse = 0; synapse < 15; synapse ++)
    	  {
     	  int toNeuron= (int) (Math.random()*numberNet.getSize());
     	  neurons[neuron].addConnection(numberNet.neurons[toNeuron],0.02);
     	  }
//        }
      }
  }

  
  private void createLTPNeurons() {
    totalNeurons = 0;
    neurons = new CANTNeuron[cols*rows];
    for (int i=0;i< cols*rows;i++) 
	  {
      neurons[i] = new CANTNeuron(totalNeurons++,this);
	  if ((i%5) == 4)
	  	neurons[i].setInhibitory(true);
	  else
	  	neurons[i].setInhibitory(false);
	  }
  }
  
  
  private void setLTPConnections(int startNeuron, int endNeuron) {
    int currentFromNeuron;
    int N1 = -1,N2 = -1,N3 = -1,N4 = -1;
    int cNeurons = size();

    //For Each Neuron
    for (currentFromNeuron = startNeuron ; currentFromNeuron < endNeuron;
         currentFromNeuron ++) {
	  if (neurons[currentFromNeuron].isInhibitory) 
	    {
	    for (int connection=0; connection < 60; connection++)
	      {
	      int curConnections=neurons[currentFromNeuron].getCurrentSynapses();
	      addConnection(currentFromNeuron,(int)(Math.random()*size()),0.01);
	      if (curConnections==neurons[currentFromNeuron].getCurrentSynapses())
	        connection--;
	      }
		
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
  
  public void initializeLTPNeurons() {
    createLTPNeurons();
    if (topology < 0){
      setLTPConnections(0,size());
    } 
    else System.out.println("bad topology specified "+ topology);
  }

  private boolean isFastBind(int neuron) {
    if ((neuron %10) ==0)
	  return true;
	else return false;
  }

  private void createFBNeurons() {
    totalNeurons = 0;
    neurons = new CANTNeuron[cols*rows];
    for (int i=0;i< cols*rows;i++) 
    {
	  if (isFastBind(i))
	    neurons[i] = new CANTNeuronFastBind(totalNeurons++,this);
	  else
        neurons[i] = new CANTNeuron(totalNeurons++,this);
		
      if ((i%5) == 4)
     	neurons[i].setInhibitory(true);
      else
    	neurons[i].setInhibitory(false);
    }
  }

  private void setFBConnections(int startNeuron, int endNeuron) {
    int currentFromNeuron;
    int N1 = -1,N2 = -1,N3 = -1,N4 = -1;
    int cNeurons = size();

    //For Each Neuron
    for (currentFromNeuron = startNeuron ; currentFromNeuron < endNeuron;
         currentFromNeuron ++) {
	  if ((currentFromNeuron%10)== 0) {//fastbind has no internal connections
	    }
      else if (neurons[currentFromNeuron].isInhibitory) 
        {
        for (int connection=0; connection < 60; connection++)
          {
          int curConnections=neurons[currentFromNeuron].getCurrentSynapses();
          addConnection(currentFromNeuron,(int)(Math.random()*size()),0.01);
          if (curConnections==neurons[currentFromNeuron].getCurrentSynapses())
            connection--;
          }
  	
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

  public void initializeFBNeurons() {
    createFBNeurons();
    if (topology < 0){
      setFBConnections(0,size());
    } 
    else System.out.println("bad topology specified "+ topology);
  }

  public void initializeNeurons() {
  	if (CANT23VB.runFastBindTest) {initializeFBNeurons();}
	else initializeLTPNeurons();
  }

  public void measure(int currentStep) {
    System.out.println("measure" + currentStep);
  }

}