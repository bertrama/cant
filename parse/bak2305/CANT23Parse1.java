
import java.util.*;
import java.awt.*;

public class CANT23Parse1 extends CANT23{
  public static String ExperimentXMLFile = "parse/parse1.xml";
  public static CANTNetParse1 nullNet;
  public static CANTExperimentParse experiment;
  

  public static void main(String args[]){
System.out.println("initialize CANT Parse1");
    readNewSystem();
    positionWindows();
  }
  
  private static void connectAllNets() {
    CANTNetParse1  inputNet = (CANTNetParse1)experiment.getNet("BaseNet");
	CANTNetParse1  verbNet = (CANTNetParse1)experiment.getNet("VerbNet");
	CANTNetParse1  stackTopNet = (CANTNetParse1)experiment.getNet("StackTopNet");
	CANTNetParse1  ruleNet = (CANTNetParse1)experiment.getNet("RuleNet");
	
	inputNet.connectInputToVerb(verbNet);
    stackTopNet.connectStackTopToRules(ruleNet);
    ruleNet.connectRulesToStackTop(stackTopNet);
	verbNet.connectVerbToRule(ruleNet);
  }

  protected static void readNewSystem() {
  	nullNet = new CANTNetParse1();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new CANT23Parse1.WorkerThread();
    initializeExperiment();
	experiment.printExpName();
    CANTNetParse1 net = (CANTNetParse1) experiment.getNet("BaseNet");
    workerThread.start();	
	connectAllNets();	
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    Enumeration enum = nets.elements();
    experiment = new CANTExperimentParse();
    System.out.println("initialize ParseVision Experiment");
  }
  
  private static int numSystems = -1;
  public static synchronized void runOneStep() {
    if (experiment.trainingLength == CANTStep) experiment.switchToTest();
    if (experiment.getInTest()) experiment.measure(CANTStep);
	
	if (experiment.isEndEpoch(CANTStep))
	   experiment.endEpoch();

    //This series of loops is really chaotic, but I needed to
	//get all of the propogation done in each net in step.
    Enumeration enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetParse1 net = (CANTNetParse1)enum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetParse1 net = (CANTNetParse1)enum.nextElement();
      net.setExternalActivation(CANTStep);
    }
      //net.propogateChange();  
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetParse1 net = (CANTNetParse1)enum.nextElement();
      net.spontaneousActivate();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetParse1 net = (CANTNetParse1)enum.nextElement();
      net.setNeuronsFired();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetParse1 net = (CANTNetParse1)enum.nextElement();
      net.setDecay ();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetParse1 net = (CANTNetParse1)enum.nextElement();
      net.spreadActivation();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetParse1 net = (CANTNetParse1)enum.nextElement();
      net.setFatigue();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetParse1 net = (CANTNetParse1)enum.nextElement();
      net.learn();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetParse1 net = (CANTNetParse1)enum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetParse1 net = (CANTNetParse1)enum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
      //if (net.getName().compareTo("VerbNet") == 0) net.measure(CANTStep);
    }
	
    CANTStep++;
    //System.out.println("Incremenet cantvis1step"+CANTStep);

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
    CANTNetParse1 baseNet = (CANTNetParse1)experiment.getNet("BaseNet");
    CANTNetParse1 verbNet = (CANTNetParse1)experiment.getNet("VerbNet");
    CANTNetParse1 nounNet = (CANTNetParse1)experiment.getNet("NounNet");
    CANTNetParse1 oWordNet = (CANTNetParse1)experiment.getNet("OWordNet");
    CANTNetParse1 stackTopNet = (CANTNetParse1)experiment.getNet("StackTopNet");
    CANTNetParse1 stackNet = (CANTNetParse1)experiment.getNet("StackNet");
    CANTNetParse1 ruleNet = (CANTNetParse1)experiment.getNet("RuleNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (500,500);
    //retinaNet.cantFrame.matrix.addStringsToPrint ("3x3Off");
    verbNet.cantFrame.setLocation(500,0);
    verbNet.cantFrame.setSize (500,500);
    nounNet.cantFrame.setLocation(500,400);
    nounNet.cantFrame.setSize (500,500);
    nounNet.cantFrame.show();
    oWordNet.cantFrame.setLocation(525,425);
    oWordNet.cantFrame.setSize (500,500);
    oWordNet.cantFrame.show();
    ruleNet.cantFrame.setLocation(550,450);
    ruleNet.cantFrame.setSize (500,500);
//    ruleNet.cantFrame.show();
    stackTopNet.cantFrame.setLocation(0,500);
    stackTopNet.cantFrame.setSize (500,500);
    stackTopNet.cantFrame.show();
    stackNet.cantFrame.setLocation(525,425);
    stackNet.cantFrame.setSize (500,500);
    stackNet.cantFrame.show();
    nounNet.cantFrame.show();
    verbNet.cantFrame.show();
    stackTopNet.cantFrame.show();
    ruleNet.cantFrame.show();
    baseNet.cantFrame.show();
  }
  
  //embedded Thread class
  public static class WorkerThread extends CANT23.WorkerThread{
    public void run(){
      System.out.println("Parse 1 Thread ");
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