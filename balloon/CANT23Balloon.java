
import java.util.*;

public class CANT23Balloon extends CANT23{
  public static String ExperimentXMLFile = "ParamBalloon2.xml";
  public static CANTNet nullNet;
  

  public static void main(String args[]){
System.out.println("initialize CANT Baloon ");
    readNewSystem();
    positionWindows();
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
	
    experiment = new BalloonExperiment(net);
  }
    
  private static void positionWindows() {
    CANTNet baseNet = (CANTNet)experiment.getNet("BaseNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (800,400);
    baseNet.cantFrame.show();
  }
  

  
}