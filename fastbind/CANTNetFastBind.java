import java.util.*;

public class CANTNetFastBind extends CANTNet {
  private int currentWord = 0;
  private int lastWord = 2;
  
  public CANTNetFastBind(){
  }
  
  public CANTNetFastBind(String name,int cols, int rows,int topology){
  	super(name,cols,rows,topology);
  }
  
  public void initializeNeurons() {
    if (topology == 1){
      neurons = new CANTNeuron[cols*rows];
      for(int i=0;i< cols*rows;i++)
	  	if ((i % 200) > 100)
		  neurons[i] = new CANTNeuronFastBind(totalNeurons++,this);
		else
          neurons[i] = new CANTNeuron(totalNeurons++,this);
      setConnections(0,size());
    } 
    else if (topology == 2){
      neurons = new CANTNeuron[cols*rows];
      for(int i=0;i< cols*rows;i++)
        neurons[i] = new CANTNeuron(totalNeurons++,this);
      setConnections(0,size());
    } 
  else System.out.println("bad topology specified "+ topology);
  }
  
  public void runAllOneStep(int CANTStep) {
    //This series of loops is really chaotic, but I needed to
	//get all of the propogation done in each net in step.
    Enumeration enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetFastBind net = (CANTNetFastBind)enum.nextElement();
      net.changePattern(CANTStep);
    }
    enum = CANT23.nets.elements();
	while (enum.hasMoreElements()) {
      CANTNetFastBind net = (CANTNetFastBind)enum.nextElement();
      net.setExternalActivation(CANTStep);
	}
      //net.propogateChange();  
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetFastBind net = (CANTNetFastBind)enum.nextElement();
      net.spontaneousActivate();
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetFastBind net = (CANTNetFastBind)enum.nextElement();
      net.setNeuronsFired();
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetFastBind net = (CANTNetFastBind)enum.nextElement();
      net.setDecay ();
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetFastBind net = (CANTNetFastBind)enum.nextElement();
      net.spreadActivation();
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetFastBind net = (CANTNetFastBind)enum.nextElement();
      net.setFatigue();
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetFastBind net = (CANTNetFastBind)enum.nextElement();
      if (net.getName().compareTo("BaseNet") != 0) 
        net.learn();
	  else {
	    net.learn();
		net.fastLearn();
	  }	
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetFastBind net = (CANTNetFastBind)enum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetFastBind net = (CANTNetFastBind)enum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
      //if (net.getName().compareTo("VerbNet") == 0) net.measure(CANTStep);
    }
  }

  private void fastLearn() {
    for (int neuronIndex = 0; neuronIndex < size(); neuronIndex++) 
      {
	  if ((neuronIndex % 200) > 100) 
        ((CANTNeuronFastBind)neurons[neuronIndex]).fastLearn();
      }
  }

  public CANTNet getNewNet(String name,int cols, int rows,int topology){
  	CANTNetFastBind net = new CANTNetFastBind (name,cols,rows,topology);
	return (net);
	} 	

  
  //**********Connect Nets to Each Other***********************************
  public void connectInputToOther(CANTNetFastBind otherNet) {
    for (int inputNeuron = 0; inputNeuron < size(); inputNeuron ++) 
	  {
	  if ((inputNeuron % 200) > 100) 
	  {
	    for (int synapse = 0; synapse < 80; synapse++)
		  {
		  int otherNeuron;
		  otherNeuron = (int)(Math.random()*otherNet.size());
          neurons[inputNeuron].addConnection(otherNet.neurons[otherNeuron],0.5);
		  }
	    }
	  }
  }

  
  public void changePattern(int cantStep)
  {
  	int fbPattern;
    if (isChangeEachTime() || (cantStep %getCyclesPerRun())==0){	 
    CANT23FastBind.experiment.endEpoch();
    fbPattern = CANT23FastBind.experiment.selectPattern(getCurrentPattern(), 
	  patterns.size(),this);
	setCurrentPattern(fbPattern);
    ((CANTPattern)patterns.get(getCurrentPattern())).arrange(getNeuronsToStimulate());
    }
  }
  
  public void spreadActivationWithFast() {
    for (int neuronNum = 0; neuronNum < size(); neuronNum++)
	  if (neurons[neuronNum].getFired())
	    ((CANTNeuronFastBind)neurons[neuronNum]).spreadActivation();
  }
}