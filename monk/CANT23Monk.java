import java.util.*;

public class CANT23Monk extends CANT23{
  public static String ExperimentXMLFile = "monk/trainDataSet1.xml";
  public static MonkNet nullNet;
  

  public static void main(String args[]){
System.out.println("initialize CANT Monk ");
    readNewSystem();
    positionWindows();
  }

  private static void readNewSystem() {
  	nullNet = new MonkNet();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    CANT23Monk.WorkerThread workerThread = new CANT23Monk.WorkerThread();
    initializeExperiment();
    MonkNet net = (MonkNet) experiment.getNet("BaseNet");
	CANT23.delayBetweenSteps = 0;
    workerThread.start();	
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    Enumeration enum = nets.elements();
    CANTNet net = (CANTNet)enum.nextElement();
	
    experiment = new MonkExperiment(net);
    experiment.printExpName();
  }
    
  private static void positionWindows() {
    MonkNet baseNet = (MonkNet)experiment.getNet("BaseNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (700,400);
    baseNet.cantFrame.show();
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