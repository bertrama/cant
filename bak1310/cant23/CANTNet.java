import java.io.*;
import java.util.*;
import java.net.*;  //for URL

public class CANTNet {

  private static int LONGEST_TIME_NEURON_ACTIVE =0;

  protected int cols = 10;
  protected int rows = 10;
  private String name;
  protected int topology;

  protected CANTFrame cantFrame;
  private Vector patterns = new Vector();
  public CANTNeuron neurons[];

  protected int totalNeurons =0;
  private int curPattern = -1;
  boolean associationTest = false;
  boolean allowRunOn=true;
  public String netFileName="Net.dat";

  private double activationThreshold;
  private double axonalStrengthMedian;
  private boolean changeEachTime;
  private int compensatoryDivisor;
  private boolean compensatoryLearningOn;
  private double connectionStrength;
  private double connectivity;
  private float decay;
  private float fatigueRecoveryRate;
  private int learningOn; // 0 no, 1 yes, 2 only to nets with 1
  private float learningRate;
  private int likelihoodOfInhibitoryNeuron;
  private boolean neuronsFatigue;
  private int neuronsToStimulate;
  private float saturationBase;
  private boolean spontaneousActivationOn;
  private float fatigueRate;
  private int cyclesPerRun;
  private boolean isBaseNet;
  public boolean recordingActivation=false;
  public Measure measure;
	
	
  public int getTotalNeurons() {return totalNeurons;}
  public int getCurrentPattern() {return curPattern;}
  public void setCurrentPattern(int newPattern) 
    {curPattern = newPattern;}
  public CANTPattern getPattern(int index ) 
	  {return (CANTPattern)patterns.get(index);}
  public int getCols() {return cols;}
  public String getName() {return name;}
  public void setLearningOn(int newLearningOn) 
    {learningOn=newLearningOn;}
  public void setLearningOn(boolean newLearningOn) {
	if (newLearningOn)
	  learningOn = 1;
	else
	  learningOn = 0;
    }	
  public int getLearningOn() {return learningOn;}
  public boolean isLearningOn() {
	  if (learningOn == 1) return true;
	  else return false;
	  }
  public void setCompensatoryLearningOn(boolean newCompensatoryLearningOn) 
    {compensatoryLearningOn = newCompensatoryLearningOn;}
  public boolean isCompensatoryLearningOn() 
	{return compensatoryLearningOn;}
    public void setSpontaneousActivationOn(boolean newSpontaneousActivationOn) {
      spontaneousActivationOn = newSpontaneousActivationOn;}
    public boolean isSpontaneousActivationOn() {return spontaneousActivationOn;}
    public void setChangeEachTime(boolean newChangeEachTime) {
      changeEachTime = newChangeEachTime;}
    public boolean isChangeEachTime() {return changeEachTime;}
    public void setNeuronsFatigue(boolean newNeuronsFatigue) {
      neuronsFatigue = newNeuronsFatigue;}
    public boolean isNeuronsFatigue() {return neuronsFatigue;}
    public void setLikelihoodOfInhibitoryNeuron(int newLikelihoodOfInhibitoryNeuron) {
      likelihoodOfInhibitoryNeuron = newLikelihoodOfInhibitoryNeuron;}
    public int getLikelihoodOfInhibitoryNeuron() {
	 return likelihoodOfInhibitoryNeuron;}
    public void setDecay(float newDecay) {decay = newDecay;}
    public float getDecay() {return decay;}
    public void setFatigueRate(float newFatigueRate) {
      fatigueRate = newFatigueRate;}
    public float getFatigueRate() {return fatigueRate;}
    public void setFatigueRecoveryRate(float newFatigueRecoveryRate) {
      fatigueRecoveryRate = newFatigueRecoveryRate;}
    public float getFatigueRecoveryRate() {
      return fatigueRecoveryRate;}
    public void setLearningRate(float newLearningRate) {
      learningRate = newLearningRate;}
    public float getLearningRate() {
      return learningRate;}
    public void setCompensatoryDivisor(int newCompensatoryDivisor) {
      compensatoryDivisor = newCompensatoryDivisor;}
    public int getCompensatoryDivisor() {
      return compensatoryDivisor;}
    public void setSaturationBase(float newSaturationBase) {
      saturationBase = newSaturationBase;}
    public float getSaturationBase() {
      return saturationBase;}
    public void setAxonalStrengthMedian(double newAxonalStrengthMedian) {
      axonalStrengthMedian = newAxonalStrengthMedian;}
    public double getAxonalStrengthMedian() {
      return axonalStrengthMedian;}
    public void setActivationThreshold(double newActivationThreshold) {
      activationThreshold = newActivationThreshold;}
    public double getActivationThreshold() {return activationThreshold;}
    public void setConnectivity(double newConnectivity) {
	  connectivity = newConnectivity;}
    public double getConnectivity() {return connectivity;}
    public void setConnectionStrength(double newConnectionStrength) 
	  {connectionStrength = newConnectionStrength;}
    public double getConnectionStrength() {return connectionStrength;}
    public void setNeuronsToStimulate(int newNeuronsToStimulate) 
	  {neuronsToStimulate = newNeuronsToStimulate;}
    public int getNeuronsToStimulate() {return neuronsToStimulate;}
    public int getCyclesPerRun() {return cyclesPerRun;}
    public void setCyclesPerRun(int cycles) {cyclesPerRun = cycles;}
    public void setRecordingActivation(boolean rA) {recordingActivation = rA;}
	
	//----real code starts here----
    public CANTNet(){
    }

  public CANTNet(String name,int cols, int rows,int topology){
    this.cols = cols;
    this.rows = rows;
    this.name = name;
    this.topology = topology;
	measure = new Measure(cols*rows);
  }
  
  public CANTNet getNewNet(String name,int cols, int rows,int topology){
  	System.out.println("get new base net ");
  	CANTNet net = new CANTNet (name,cols,rows,topology);
  return (net);
  }
  
	
  public void initializeNeurons() {
    neurons = new CANTNeuron[cols*rows];
    for(int i=0;i< cols*rows;i++)
      neurons[i] = new CANTNeuron(totalNeurons++,this);
    if (topology < 0){
      setConnections(0,size());
    } 
	else System.out.println("bad topology specified "+ topology);
  }
	
  public void write(){
    DataOutputStream output;
   
    try {    
      output = new DataOutputStream(new FileOutputStream(netFileName));

      output.writeBytes(Integer.toString(rows)+"\n"+Integer.toString(cols) +"\n");

      for ( int i=0;i<(size()); i++ )  {
        output.writeBytes( Integer.toString(i) + " Neuron\n");
	  
	    if (neurons[i].currentSynapses==0)
	      output.writeBytes("0 Axons\n");
        else
          for (int j=0; j< neurons[i].currentSynapses ; j++ ) {
            if (j==0)
              output.writeBytes(Integer.toString(neurons[i].currentSynapses) + " Axons\n");
	          CANTNeuron toNeuron = neurons[i].synapses[j].toNeuron;
              output.writeBytes(toNeuron.parentNet.getName() + " ");
              output.writeBytes(Integer.toString(toNeuron.id) +
                " " +  Double.toString(neurons[i].synapses[j].weight) + "\n");
          }
	}
    System.out.println("Network saved");
    output.close();
  }
  catch (IOException e) {
      System.err.println("output file not opened properly\n" +
                           e.toString());
      System.exit(1);  }
}


    public void makeFrame() {
      boolean isBase = name.equalsIgnoreCase("BaseNet");
      cantFrame = new CANTFrame(this,cols,rows,isBase);

      cantFrame.setVisible(true);
    }

    public void addPattern(CANTPattern pattern) {
      patterns.add(pattern);
    }
	
  public int size() {return (cols*rows);}
	
    public void clear() {
       for (int cNeuron = 0 ; cNeuron < size(); cNeuron++)
           neurons[cNeuron].clear();
    }
	
  protected int getLeftNeighbor(int neuronID) {
    if ((neuronID % cols) == 0)
      return (neuronID + cols - 1);
    else
      return (neuronID - 1);
  }
  
  protected int getRightNeighbor(int neuronID) {
    if ((neuronID % cols) == (cols -1))
       return (neuronID - cols + 1);
    else
       return (neuronID + 1);
  }
  
  protected int getTopNeighbor(int neuronID) {
    if (neuronID < cols)
      return (neuronID + ((rows-1) * cols));
    else
      return (neuronID - cols);
  }
  
  protected int getBottomNeighbor(int neuronID) {
    if ((neuronID / cols) == (rows -1))
      return (neuronID - ((rows-1) * cols));
    else
      return (neuronID + cols);
  }
    private void addConnection(int fromNeuron, int toNeuron, double weight) {
      if (toNeuron == fromNeuron) return;
      Assert(toNeuron < size());

      weight = neurons[fromNeuron].isInhibitory()? weight*-1:weight;
      neurons[fromNeuron].addConnection(neurons[toNeuron],weight);
    }
//Set the connection strength of this axon, then recursively
//call to set up connections (at a lower likelihood) to other connections.
    private void recursiveSetConnections (int fromNeuron, int toNeuron, int distance) {
            int N1,N2,N3,N4;
            double weight;

            //Set up the initial Weight
         weight = (float)(((Math.random()) + 1) * connectionStrength);

         //with a probability lessening as distance increases
          if (Math.random() < (1.0 / (distance * connectivity)))
             addConnection(fromNeuron,toNeuron,weight);

                //call for children if they're close.
          if (distance < 4) {
             N1 = getLeftNeighbor(toNeuron);
             N2 = getRightNeighbor(toNeuron);
             N3 = getTopNeighbor(toNeuron);
             N4 = getBottomNeighbor(toNeuron);
             recursiveSetConnections(fromNeuron,N1,distance + 1);
             recursiveSetConnections(fromNeuron,N2,distance + 1);
             recursiveSetConnections(fromNeuron,N3,distance + 1);
             recursiveSetConnections(fromNeuron,N4,distance + 1);
            }
        }

//Set up a distance bias set of
//connections.  Assume that the Neurons are
//in a 2-D space.  Those next to it (lr and td) are
//likely to be connected, next step away less so.
     private void setConnections(int startNeuron, int endNeuron) {
             int currentFromNeuron;
             int N1 = -1,N2 = -1,N3 = -1,N4 = -1;
             int cNeurons = size();

             Assert(cNeurons >= endNeuron);

             //For Each Neuron
             for (currentFromNeuron = startNeuron ; currentFromNeuron < endNeuron;
             currentFromNeuron ++) {
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

  public int getActives() {
    int totalActives = 0;
    for (int index = 0; index < size(); index++) {
      if (neurons[index].getFired())
        totalActives++;
    }
    return totalActives;
  }
  
  //call measure for recording the state of the net.
  private void setMeasure(int cantStep) {
    for (int index = 0; index < size(); index++)
      if (neurons[index].getFired())
	    measure.setActiveState(cantStep,index,1);
      else 
        measure.setActiveState(cantStep,index,0);
  }

  public void runOneStep(int cantStep) {
    changePattern(cantStep);
    setExternalActivation(cantStep);
    propogateChange();
    learn();
    cantFrame.runOneStep(cantStep+1);
    printAverageFatigue();
	if (recordingActivation) setMeasure(cantStep);
  }
	
  private void learn() {
    if (learningOn == 0) return;
//System.out.println(getName() + " " +  axonalStrengthMedian + " " + saturationBase);

    int totalNeurons = size();
    for (int neuronIndex = 0; neuronIndex < totalNeurons; neuronIndex++) 
	  {
	  if (learningOn == 2)  //only learn if the to neurons net is of learn type 1
	    neurons[neuronIndex].restrictedLearn();
	  else
        neurons[neuronIndex].learn4();
	  }
  }

  //New activation may have come externally.  Current is that activation +
  //any remaining from last time.
  private void propogateChange(){
    int neuronIndex;
    int totalNeurons = size();

    //spontaneously activate
    for (neuronIndex = 0; neuronIndex < totalNeurons; neuronIndex++) {
      if (neurons[neuronIndex].spontaneouslyActivate())
          activate(neuronIndex,(activationThreshold*2));
    }
    //Set whether neuron has fired.
    for (neuronIndex = 0; neuronIndex < totalNeurons; neuronIndex++)
         neurons[neuronIndex].setFired();
    //resetActivation and apply decay
    for (neuronIndex = 0; neuronIndex < totalNeurons; neuronIndex++)
         neurons[neuronIndex].resetActivation();
    //for each formally active neuron spread activation
    for (neuronIndex = 0; neuronIndex < totalNeurons; neuronIndex++){
      if ((!associationTest) && (neurons[neuronIndex].getFired()))
        neurons[neuronIndex].spreadActivation();
      }
    //modify fatigue
    if (neuronsFatigue)
      for (neuronIndex = 0; neuronIndex < totalNeurons; neuronIndex++) {
           neurons[neuronIndex].modifyFatigue();
      }
  }
  
    private void activate (int neuronNumber, double activation) {
      Assert(neuronNumber < size());
	  if (neuronNumber >= size()) 
        System.out.println(getName());
	  
      neurons[neuronNumber].setActivation(activation);
    }
	
    private void setExternalActivation(int cantStep){

      if ((cantStep% cyclesPerRun > 10) &&(allowRunOn))
        return;
       CANTPattern pattern = (CANTPattern)patterns.get(curPattern);
       neuronsToStimulate = neuronsToStimulate > pattern.size()?pattern.size():neuronsToStimulate;
       for (int i= 0; i < neuronsToStimulate; i++) {
          activate(pattern.getPatternIndex(i),
                     (activationThreshold+(Math.random()*activationThreshold)));
       }
    }
  public void changePattern(int cantStep)
  {
    if (changeEachTime || (cantStep %cyclesPerRun)==0){	  
	  curPattern = CANT23.experiment.selectPattern(curPattern, patterns.size(),this);
      ((CANTPattern)patterns.get(curPattern)).arrange(neuronsToStimulate);
    }
  }
  
  //read in a new file of patterns and set the patterns for this
  //net to them.
  public void getNewPatterns(String fileName) {
  	patterns = new Vector();
	NetManager.readPatternFile(fileName,this);
  }
  

    private void checkExcitatoryConnections() {
  	int totalNeurons = size();
  	int inhibitoryCount =0;
  	for (int neuronIndex = 0; neuronIndex < totalNeurons; neuronIndex++)
    	if (!neurons[neuronIndex].isInhibitory()) {
      	inhibitoryCount++;
     CANTNeuron neuron = neurons[neuronIndex];
     for (int synIndex =0; synIndex < neuron.currentSynapses; synIndex++ ) {
       double weight = neuron.synapses[synIndex].getWeight();
       CANTNeuron toNeuron = neuron.synapses[synIndex].getTo();
       System.out.println("Exc "+neuronIndex+ "---"+toNeuron.getId()+" = "+ weight);
     }
    }
System.out.println("total excitatory neurons = "+inhibitoryCount);
}

    private void checkInhibitoryConnections() {
      int totalNeurons = size();
      int inhibitoryCount =0;
      for (int neuronIndex = 0; neuronIndex < totalNeurons; neuronIndex++)
        if (neurons[neuronIndex].isInhibitory()) {
          inhibitoryCount++;
         CANTNeuron neuron = neurons[neuronIndex];
         for (int synIndex =0; synIndex < neuron.currentSynapses; synIndex++ ) {
           double weight = neuron.synapses[synIndex].getWeight();
           CANTNeuron toNeuron = neuron.synapses[synIndex].getTo();
           System.out.println("Inh "+neuronIndex+ "---"+toNeuron.getId()+" = "+ weight);
         }
        }
 System.out.println("total inhibitory neurons = "+inhibitoryCount);
    }

  //Set Connections from this net to another net.
  public void setOtherConnections(CANTNet otherNet, int connectionsPerNeuron) {

    int toSize = otherNet.size();
    for (int neuronIndex = 0; neuronIndex < size(); neuronIndex++){
      for (int newConnection = 0; newConnection < connectionsPerNeuron; newConnection++){
        int toNeuron = (int)(Math.random()*toSize);
        double weight  = neurons[neuronIndex].isInhibitory()? -0.1:0.1;
        neurons[neuronIndex].addConnection(otherNet.neurons[toNeuron],weight);
      }
    }
  }

    public void recordNeuronActiveTime(int time){
      if (time > LONGEST_TIME_NEURON_ACTIVE)
        LONGEST_TIME_NEURON_ACTIVE = time;
    }
    private boolean Assert(boolean test) {
      int x = -1;
      if (! test)
      try{
        x = (1 / (1 +x));
      }
      catch(Exception e){
        System.out.println("Nettest = "+test);
        return false;
      }
      return true;
    }

private void printAverageFatigue(){
  float averageFatigue=0;
  float maxfatigue =0;
  int maxI=-1;
  for(int i=0;i<size();i++){
    if (maxfatigue< neurons[i].getFatigue()){
 maxfatigue = neurons[i].getFatigue();
 maxI = i;
    }

  }
//  System.out.println("Max Fatigue = "+maxfatigue+ "Neuron= "+maxI);


}


//undone it should be removed
    public boolean selectPattern(int patternNum) {
      if (patternNum >= patterns.size() || patternNum<0)
        return false ;
      curPattern = patternNum;
      ((CANTPattern)patterns.get(curPattern)).arrange(neuronsToStimulate);
      return true;
    }
    public void writeParameters() {
           int LearningValue;
           DataOutputStream OutputFile;
           String outString="";

           outString=Integer.toString(likelihoodOfInhibitoryNeuron);
           outString=outString + " Likelihood of Inhibitory Neuron\n";
           outString=outString + Float.toString(decay);
           outString=outString + " Decay\n";
           outString=outString + Float.toString(fatigueRate);
           outString=outString +" Fatigue Rate\n";
           outString=outString + Float.toString(fatigueRecoveryRate);
           outString=outString + " Fatigue Recovery RAte\n";
           outString=outString + Float.toString(learningRate);
           outString=outString + " Learning Rate\n";
           outString=outString + Integer.toString(compensatoryDivisor);
           outString=outString + " Compensatory Divisor\n";
           outString=outString + Float.toString(saturationBase);
           outString=outString + " Saturation Base\n";
           outString=outString + Double.toString(axonalStrengthMedian);
           outString=outString + " Axonal Strength Median\n";
           outString=outString + Double.toString(activationThreshold);
           outString=outString + " Activation Threshold\n";
           outString=outString + Double.toString(connectivity);
           outString=outString + " Connectivity\n";
           outString=outString + Double.toString(connectionStrength);
           outString=outString + " Connection Strength\n";

           if (learningOn == 0) LearningValue = 0;
           else if (!compensatoryLearningOn) LearningValue = 1;
           else LearningValue = 2;

           outString=outString + Integer.toString(LearningValue);
           outString=outString + " Learning On\n";
           outString=outString + Integer.toString(neuronsToStimulate);
           outString=outString + " Neurons To Stimulate\n";
           outString=outString + changeEachTime;
           outString=outString + " Change Each Time\n";
           outString=outString + neuronsFatigue;
           outString=outString + " Neurons Fatigue\n";
           outString=outString + spontaneousActivationOn;
           outString=outString + " Spontaneous Activation\n";
           outString=outString + cyclesPerRun;
           outString=outString + " Cycles Per Run\n";
           System.out.println(outString);
   }
   
   
}
