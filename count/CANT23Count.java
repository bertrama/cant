
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
    CANT23Count.WorkerThread workerThread = new CANT23Count.WorkerThread();
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
//    System.out.println("Incremenet cantstep"+CANTStep);

	if (experiment.experimentDone(CANTStep)) 
	  {
System.out.println("experiment Done"+CANTStep);
        closeSystem();
		makeNewSystem();
	  }
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
 
  //embedded Thread class
  public static class WorkerThread extends Thread{
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

