
import java.util.*;

public class CANT23Hier extends CANT23{
  public static String ExperimentXMLFile = "hier/hier.xml";
  public static CANTNet nullNet;
  
  //switch between the two versions of the experiment
  private static int hierExperimentVersion = 1;
  

  public static void main(String args[]){
System.out.println("initialize CANT Hier ");
    readNewSystem();
    positionWindows();
  }

  protected static void readNewSystem() {
  	nullNet = new CANTNet();
    if (hierExperimentVersion == 2) 
       ExperimentXMLFile = "hier/ParamHier2.xml";
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new CANT23Hier.WorkerThread();
    initializeExperiment();
	experiment.printExpName();
    CANTNet net = experiment.getNet("BaseNet");
    workerThread.start();	
  }
  
  private static void setNewParams(int runNum, CANTNet baseNet) {
    int testsPerSet = 10;
    int set = runNum /testsPerSet;
	
	//Inhib 20-50 by 5s so 7
	//connectivity .2 to .8 by .1 so 7
	//median .5 to 1.0 by .1 so 6
	//satBase 15-45 by 3 so 10
	int inhib = 20 + ((set % 7)*5);
	double connectivity = 0.2 + (((set%49)/7)*0.1);
    double median = 0.5 + (((set/49)%6)*0.1);
	int satBase = 18+ ((set/(7*7*6)) *3);
    baseNet.setLikelihoodOfInhibitoryNeuron(inhib);		
    baseNet.setConnectivity(connectivity);		
	baseNet.setAxonalStrengthMedian(median);		
	baseNet.setSaturationBase(satBase);		
	if ((runNum % testsPerSet) == 0)
	  System.out.println("Inhib " + inhib + 
                         " conn " + connectivity +	  
	                     " median " + median +	  
	                     " satBase " + satBase	  
	  );
	  
	if (set >= 7*7*6*10*10) CANT23.setRunning(false);
  }

  private static void makeNewSystem(int numSystems) {
//System.out.println("makeNew System"); 

  	nullNet = new CANTNet();
  	CANTNet net = nullNet.getNewNet("BaseNet",20,20,-1);
  	
  	net.setActivationThreshold(4.0);
  	net.setChangeEachTime(true);
  	net.setCompensatoryDivisor(1);

    net.setLearningOn(true);
    net.setCompensatoryLearningOn(true);

  	net.setConnectionStrength(0.02);
  	net.setFatigueRate((float)0.4);
  	net.setFatigueRecoveryRate((float)0.8);
  	net.setLearningRate((float)0.1);
  	net.setNeuronsFatigue(true);
  	net.setNeuronsToStimulate(40);
  	net.setSpontaneousActivationOn(false);
  	net.setCyclesPerRun(50);

  	net.setAxonalStrengthMedian((float) 0.7);
  	net.setConnectivity((float) 0.2);
  	net.setDecay((float)1.2);
  	net.setLikelihoodOfInhibitoryNeuron(40);
  	net.setSaturationBase(40);
    setNewParams(numSystems,net);

  	net.initializeNeurons();
  	net.makeFrame();
	
	net.getNewPatterns("hier/hier1Pattern.xml");

    nets.put(net.getName(),net);
	
	CANT23.WorkerThread oldThread = workerThread;
    workerThread = new CANT23Hier.WorkerThread();
    initializeExperiment();
    workerThread.start();		
    oldThread.stop();
  }

  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    Enumeration enum = nets.elements();
    CANTNet net = (CANTNet)enum.nextElement();
	if (hierExperimentVersion == 1)
       experiment = new HierExperiment(net);
    else if (hierExperimentVersion == 2) 
       experiment = new HierExperiment2(net);
	else   
       System.out.println("initialize Hier Experiment error");

//experiment.printExpName();
  }
  
  
  private static int numSystems = -1;
  public static synchronized void runOneStep() {
  
    if (experiment.trainingLength == CANTStep) experiment.switchToTest();
    if (experiment.getInTest()) experiment.measure(CANTStep);
	
	if (experiment.isEndEpoch(CANTStep))
	   experiment.endEpoch();

    Enumeration enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNet net = (CANTNet)enum.nextElement();
      net.runOneStep(CANTStep);
    }
    CANTStep++;
    //System.out.println("Incremenet canthierstep"+CANTStep);

	if (experiment.experimentDone(CANTStep)) 
	  {
//System.out.println("experiment done"+CANTStep);
        closeSystem();
		numSystems++;
		readNewSystem();
		//makeNewSystem(numSystems);
	  }
  }
  
  private static void positionWindows() {
    CANTNet baseNet = (CANTNet)experiment.getNet("BaseNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (800,400);
    baseNet.cantFrame.show();
  }
  
  //embedded Thread class
  public static class WorkerThread extends CANT23.WorkerThread{
    public void run(){
      //System.out.println("Thread ");
      while(true){
         if(isRunning){
           runOneStep();
         }
         else{
           try{sleep(delayBetweenSteps);}
		   catch(InterruptedException ie){ie.printStackTrace();}
             }//else
       }//while
    }//run
  }//WorkerThread class
  
}