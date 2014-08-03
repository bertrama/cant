
import java.util.*;

public class CANT23Count extends CANT23{
  public static String ExperimentXMLFile = "count/count.xml";
  public static CANTNetCount nullNet;
  
  

  public static void main(String args[]){
System.out.println("initialize CANT Count ");
    makeNewSystem();
	positionWindows();
  }

  protected static void makeNewSystem() {
  	nullNet = new CANTNetCount();
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    CANT23.WorkerThread workerThread = new CANT23.WorkerThread();
    initializeExperiment();
    workerThread.start();	
  }

  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
System.out.println("initialize Count Experiment ");
  
    Enumeration enum = nets.elements();
    CANTNet net = (CANTNet)enum.nextElement();
    experiment = new CountExperiment(net);

    experiment.printExpName();
  }
 
  private static void positionWindows() {
    CANTNetCount doneNet = (CANTNetCount)experiment.getNet("done");
    CANTNetCount internalNet = (CANTNetCount)experiment.getNet("internal");
    CANTNetCount finishNet = (CANTNetCount)experiment.getNet("finish");
    CANTNetCount inputNet = (CANTNetCount)experiment.getNet("BaseNet");
    CANTNetCount rulesNet = (CANTNetCount)experiment.getNet("rules");
    CANTNetCount bindNet = (CANTNetCount)experiment.getNet("bind");
  
	bindNet.cantFrame.setLocation(400,200);
	finishNet.cantFrame.setLocation(0,0);
	internalNet.cantFrame.setLocation(500,0);
	inputNet.cantFrame.setLocation(0,300);
	rulesNet.cantFrame.setLocation(600,400);
	doneNet.cantFrame.setLocation(200,0);
  }
  
}

