
import java.util.*;
import java.awt.*;

public class CANT23FastBind extends CANT23{
  public static String ExperimentXMLFile = "fastbind/fastbind.xml";
  public static CANTNetFastBind nullNet;
  public static CANTExperimentFastBind experiment;
  

  public static void main(String args[]){
System.out.println("initialize CANT Fast Bind");
    delayBetweenSteps = 0;
    readNewSystem();
    positionWindows();
  }
  
  private static void connectAllNets() {
    CANTNetFastBind  inputNet = (CANTNetFastBind)experiment.getNet("BaseNet");
	CANTNetFastBind  otherNet = (CANTNetFastBind)experiment.getNet("OtherNet");

	inputNet.connectInputToOther(otherNet);
  }

  protected static void readNewSystem() {
  	nullNet = new CANTNetFastBind();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new CANT23FastBind.WorkerThread();
    initializeExperiment();
	experiment.printExpName();
    CANTNetFastBind net = (CANTNetFastBind) experiment.getNet("BaseNet");
    workerThread.start();	
	connectAllNets();	
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    Enumeration enum = nets.elements();
    experiment = new CANTExperimentFastBind();
    System.out.println("initialize Fast Bind Experiment");
  }
  
  private static int numSystems = -1;
  public static synchronized void runOneStep() {
    if (experiment.trainingLength == CANTStep) experiment.switchToTest();
    if (experiment.getInTest()) experiment.measure(CANTStep);
	
	if (experiment.isEndEpoch(CANTStep))
	   experiment.endEpoch();

    Enumeration enum = nets.elements();
    while (enum.hasMoreElements()) {
      CANTNetFastBind net = (CANTNetFastBind)enum.nextElement();
	  if (net.getName().compareTo("BaseNet") == 0)
	     net.runAllOneStep(CANTStep++); 
    }
   
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
    CANTNetFastBind baseNet = (CANTNetFastBind)experiment.getNet("BaseNet");
    CANTNetFastBind otherNet = (CANTNetFastBind)experiment.getNet("OtherNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (500,800);
    otherNet.cantFrame.setLocation(500,0);
    otherNet.cantFrame.setSize (500,800);
    otherNet.cantFrame.show();
    baseNet.cantFrame.show();
  }
  
  //embedded Thread class
  public static class WorkerThread extends CANT23.WorkerThread{
    public void run(){
      System.out.println("Fast Bind 1 Thread ");
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