
import java.util.*;
import java.awt.*;

public class CANT23Agent1 extends CANT23{
  public static String ExperimentXMLFile = "agent1/agent1.xml";
  public static Agent1Net nullNet;
  public static BmpReader bmpReader = new BmpReader();

  public static void main(String args[]){
System.out.println("initialize CANT Agent 1");
    readNewSystem();
    positionWindows();
  }
  
  private static void connectAllNets() {
    Agent1Net  inputNet = (Agent1Net)experiment.getNet("BaseNet");
    Agent1Net  retinaNet = (Agent1Net)experiment.getNet("RetinaNet");
    Agent1Net  V1Net = (Agent1Net)experiment.getNet("V1Net");
	Agent1Net  V2Net = (Agent1Net)experiment.getNet("V2Net");
	inputNet.connectInputToRetina(retinaNet);
    retinaNet.connectRetinaToV1(V1Net);
    retinaNet.connectRetinaToV2(V2Net);
    V1Net.connectV1ToV2(V2Net);
  }

  protected static void readNewSystem() {
  	nullNet = new Agent1Net();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new CANT23Agent1.WorkerThread();
    initializeExperiment();
	experiment.printExpName();
    Agent1Net net = (Agent1Net) experiment.getNet("BaseNet");
    workerThread.start();	
	//connectAllNets();	
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    Enumeration enum = nets.elements();
//   CANTNet net = (CANTNet)enum.nextElement();
//    experiment = new HierExperiment(net);
    experiment = new CANTExperiment();
    System.out.println("initialize Vision Experiment");

//experiment.printExpName();
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
      Agent1Net net = (Agent1Net)enum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      Agent1Net net = (Agent1Net)enum.nextElement();
      net.setExternalActivation(CANTStep);
    }
      //net.propogateChange();  
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      Agent1Net net = (Agent1Net)enum.nextElement();
      net.spontaneousActivate();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      Agent1Net net = (Agent1Net)enum.nextElement();
      net.setNeuronsFired();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      Agent1Net net = (Agent1Net)enum.nextElement();
      net.setDecay ();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      Agent1Net net = (Agent1Net)enum.nextElement();
      net.spreadActivation();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      Agent1Net net = (Agent1Net)enum.nextElement();
      net.setFatigue();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      Agent1Net net = (Agent1Net)enum.nextElement();
      net.learn();
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      Agent1Net net = (Agent1Net)enum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    enum = nets.elements();
    while (enum.hasMoreElements()) {
      Agent1Net net = (Agent1Net)enum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
      //if (net.getName().compareTo("RetinaNet") == 0) net.printNeuronsFired();
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
    Agent1Net baseNet = (Agent1Net)experiment.getNet("BaseNet");
    Agent1Net retinaNet = (Agent1Net)experiment.getNet("RetinaNet");
    Agent1Net V1Net = (Agent1Net)experiment.getNet("V1Net");
    Agent1Net V2Net = (Agent1Net)experiment.getNet("V2Net");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (500,500);
    retinaNet.cantFrame.setLocation(500,0);
    retinaNet.cantFrame.setSize (500,500);
    retinaNet.cantFrame.matrix.addStringsToPrint ("3x3On");
    retinaNet.cantFrame.matrix.addStringsToPrint ("3x3Off");
    retinaNet.cantFrame.matrix.addStringsToPrint ("6x6On");
    retinaNet.cantFrame.matrix.addStringsToPrint ("6x6Off");
    retinaNet.cantFrame.matrix.addStringsToPrint ("9x9On");
    retinaNet.cantFrame.matrix.addStringsToPrint ("9x9Off");
    retinaNet.cantFrame.show();
    V1Net.cantFrame.setLocation(0,500);
    V1Net.cantFrame.setSize (500,500);
    V1Net.cantFrame.matrix.addStringsToPrint ("HLine");
    V1Net.cantFrame.matrix.addStringsToPrint ("Slash Line");
    V1Net.cantFrame.matrix.addStringsToPrint ("BackSlash Line");
    V1Net.cantFrame.matrix.addStringsToPrint ("And Angle");
    V1Net.cantFrame.matrix.addStringsToPrint ("Less Than Angle");
    V1Net.cantFrame.matrix.addStringsToPrint ("Greater Than Angle");
    V1Net.cantFrame.matrix.addStringsToPrint ("Or Angle");
    V1Net.cantFrame.matrix.addStringsToPrint ("Edge");
    V1Net.cantFrame.show();	
    V2Net.cantFrame.setLocation(500,500);
    V2Net.cantFrame.setSize (500,500);
    V2Net.cantFrame.matrix.addStringsToPrint ("PTriangle");
    V2Net.cantFrame.matrix.addStringsToPrint ("WTriangle");
    V2Net.cantFrame.show();	
    baseNet.cantFrame.show();
  }
  
  //embedded Thread class
  public static class WorkerThread extends CANT23.WorkerThread{
    public void run(){
      System.out.println("Vision 1 Thread ");
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