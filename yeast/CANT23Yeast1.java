
import java.util.*;

public class CANT23Yeast extends CANT23{
  public static String ExperimentXMLFile = "yeast/train0.xml";
  public static CANTNet nullNet;
  

  public static void main(String args[]){
System.out.println("initialize CANT Yeast ");
    readNewSystem();
    positionWindows();
	delayBetweenSteps = 0;
  }

  protected static void readNewSystem() {
  	nullNet = new CANTNet();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new CANT23.WorkerThread();
    initializeExperiment();
	experiment.printExpName();
    CANTNet net = experiment.getNet("BaseNet");
    workerThread.start();	
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    Enumeration enum = nets.elements();
    CANTNet net = (CANTNet)enum.nextElement();
	
    experiment = new YeastExperiment(net);
  }
    
  private static void positionWindows() {
    CANTNet baseNet = (CANTNet)experiment.getNet("BaseNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (800,600);
    baseNet.cantFrame.show();
  }
  

  
}