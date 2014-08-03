
import java.util.*;

public class CANT23Hier extends CANT23{
  public static String ExperimentXMLFile = "hier/hier.xml";
  public static CANTNet nullNet;
  
  

  public static void main(String args[]){
System.out.println("initialize CANT Hier ");
    makeNewSystem();
  }

  //switch between the two versions of the experiment
  private static int hierExperimentVersion = 2;

  protected static void makeNewSystem() {
  	nullNet = new CANTNet();
    if (hierExperimentVersion == 2) 
       ExperimentXMLFile = "hier/ParamHier2.xml";
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    CANT23Hier.WorkerThread workerThread = new CANT23Hier.WorkerThread();
    initializeExperiment();
    CANTNet net = experiment.getNet("BaseNet");
	net.setCyclesToStimulatePerRun(20);
    workerThread.start();	
  }

  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
System.out.println("initialize Hier Experiment ");
  
    Enumeration enum = nets.elements();
    CANTNet net = (CANTNet)enum.nextElement();
	if (hierExperimentVersion == 1)
       experiment = new HierExperiment(net);
    else if (hierExperimentVersion == 2) 
       experiment = new HierExperiment2(net);
	else   
       System.out.println("initialize Hier Experiment error");

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