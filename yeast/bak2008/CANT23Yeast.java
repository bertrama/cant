
import java.util.*;

public class CANT23Yeast extends CANT23{
  public static String ExperimentXMLFile = "yeast/train0.xml";
  public static YeastNet nullNet;
  

  public static void main(String args[]){
System.out.println("initialize CANT Yeast ");
    readNewSystem();
    positionWindows();
  }

  private static void readNewSystem() {
  	nullNet = new YeastNet();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    CANT23Yeast.WorkerThread workerThread = new CANT23Yeast.WorkerThread();
    initializeExperiment();
    YeastNet net = (YeastNet) experiment.getNet("BaseNet");
	
    workerThread.start();	
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    Enumeration enum = nets.elements();
    CANTNet net = (CANTNet)enum.nextElement();
	
    experiment = new YeastExperiment(net);
    experiment.printExpName();
  }
    
  private static void positionWindows() {
    YeastNet baseNet = (YeastNet)experiment.getNet("BaseNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (800,600);
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