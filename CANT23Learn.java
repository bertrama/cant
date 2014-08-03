
import java.util.*;
import java.awt.*;

public class CANT23Learn extends CANT23{
  public static int goalsActions = 2;
    //public static String ExperimentXMLFile = "learn/learn.xml";
    //  public static String ExperimentXMLFile = "learn/learn8.xml";
  public static String ExperimentXMLFile = "rein/learn.xml";
  public static LearnNet nullNet;
  public static LearnExperiment experiment;

  public static void main(String args[]){
    System.out.println("initialize CANT Learning");
    readNewSystem();
    positionWindows();
    delayBetweenSteps=10;
  }

  private static void connectAllNets() {
      // Define nets
    LearnNet factNet = (LearnNet)experiment.getNet("BaseNet");
    LearnNet valueNet = (LearnNet)experiment.getNet("ValueNet");
    LearnNet actionNet = (LearnNet)experiment.getNet("ActionNet");
    LearnNet exploreNet = (LearnNet)experiment.getNet("ExploreNet");
    // Connect nets
    factNet.connectFactToAction(actionNet);
    valueNet.connectValueToExplore(exploreNet);
    exploreNet.connectExploreToAction(actionNet);
  }

  protected static void readNewSystem() {
    nullNet = new LearnNet();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new CANT23Learn.WorkerThread();
    initializeExperiment();
    experiment.printExpName();
    LearnNet net = (LearnNet) experiment.getNet("BaseNet");
    workerThread.start();	
    connectAllNets();	
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    experiment = new LearnExperiment();
    System.out.println("Initialize learning experiment");
    experiment.printExpName();
  }

  public static void runOneStepStart() {
    if (experiment.trainingLength == CANTStep) experiment.switchToTest();
    if (experiment.getInTest()) experiment.measure(CANTStep);
    if (experiment.isEndEpoch(CANTStep)) experiment.endEpoch();
  }

  public static synchronized void runOneStep() {
    //runOneStepStart();

    Enumeration eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      LearnNet net = (LearnNet)eNum.nextElement();
    if (net.getName().compareTo("BaseNet") == 0)
      {
      net.runAllOneStep(CANTStep); 
      CANTStep++;
      }
    }
   
    //System.out.println("Incremenet cantvis1step"+CANTStep);

  if (experiment.experimentDone(CANTStep)) 
    {
//System.out.println("experiment done"+CANTStep);
        closeSystem();
  	//numSystems++;
  	readNewSystem();
  	//makeNewSystem(numSystems);
    }
  }
  
  
  private static void positionWindows() {
    LearnNet factNet = (LearnNet)experiment.getNet("BaseNet");
    LearnNet valueNet = (LearnNet)experiment.getNet("ValueNet");
    LearnNet actionNet = (LearnNet)experiment.getNet("ActionNet");
    LearnNet exploreNet = (LearnNet)experiment.getNet("ExploreNet");

    factNet.cantFrame.setLocation(0,0);
    if (goalsActions == 2) {
        factNet.cantFrame.setSize (600,250);
        factNet.cantFrame.matrix.addStringsToPrint ("Goal 1",30,140);
        factNet.cantFrame.matrix.addStringsToPrint ("Goal 2",100,140);
    }
    else {
        factNet.cantFrame.setSize (600,600);
        factNet.cantFrame.matrix.addStringsToPrint ("Goal 1",30,140);
        factNet.cantFrame.matrix.addStringsToPrint ("Goal 2",90,140);
        factNet.cantFrame.matrix.addStringsToPrint ("Goal 3",150,140);
        factNet.cantFrame.matrix.addStringsToPrint ("Goal 4",210,140);
        factNet.cantFrame.matrix.addStringsToPrint ("Goal 5",270,140);
        factNet.cantFrame.matrix.addStringsToPrint ("Goal 6",330,140);
        factNet.cantFrame.matrix.addStringsToPrint ("Goal 7",390,140);
        factNet.cantFrame.matrix.addStringsToPrint ("Goal 8",450,140);
    };
    factNet.cantFrame.show();

    actionNet.cantFrame.setLocation(900,0);
    if (goalsActions == 2) {
        actionNet.cantFrame.setSize (300,250);
        actionNet.cantFrame.matrix.addStringsToPrint ("Act 1",30,140);
        actionNet.cantFrame.matrix.addStringsToPrint ("Act 2",100,140);
    }
    else {
        actionNet.cantFrame.setSize (300,600);
        actionNet.cantFrame.matrix.addStringsToPrint ("Act 1",30,140);
        actionNet.cantFrame.matrix.addStringsToPrint ("Act 2",90,140);
        actionNet.cantFrame.matrix.addStringsToPrint ("Act 3",150,140);
        actionNet.cantFrame.matrix.addStringsToPrint ("Act 4",210,140);
        actionNet.cantFrame.matrix.addStringsToPrint ("Act 5",270,140);
        actionNet.cantFrame.matrix.addStringsToPrint ("Act 6",330,140);
        actionNet.cantFrame.matrix.addStringsToPrint ("Act 7",390,140);
        actionNet.cantFrame.matrix.addStringsToPrint ("Act 8",450,140);
    };
    actionNet.cantFrame.show();

    exploreNet.cantFrame.setLocation(600,0);
    exploreNet.cantFrame.setSize (300,250);
    exploreNet.cantFrame.show();

    valueNet.cantFrame.setLocation(600,250);
    valueNet.cantFrame.setSize (300,250);
    valueNet.cantFrame.show();
  }
  
  //embedded Thread class
  public static class WorkerThread extends CANT23.WorkerThread{
    public void run(){
      System.out.println("Learn Thread ");
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
