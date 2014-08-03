//This can either run as an applet in which case init is used
//or as an application, in which case main is used.  The environment
//it runs from chooses (java debugger application web browser=applet)
//In Kawa 16/10/02

import java.io.*;
import java.util.*;
import java.applet.*;
import java.net.*;  //for URL


public class CANT23 extends Applet{

  public static String ExperimentXMLFile = "Param.xml";
  public static CANTNet nullNet;
  public static Hashtable nets;
  public static boolean isRunning=false;
  public static int CANTStep = 0;
  public static int delayBetweenSteps = 0;
  public static CANTExperiment experiment;

  public static void setRunning(boolean status) {isRunning = status;}

  public static void main(String args[]){
System.out.println("Start CANT ");
    makeNewSystem();
  }
  

  protected static void makeNewSystem() {
  	nullNet = new CANTNet();
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    CANT23.WorkerThread workerThread = new CANT23.WorkerThread();
    initializeExperiment();
    workerThread.start();	
  }
  
  protected static void closeSystem() {
    Enumeration enum = nets.elements();
	CANTNet net;
    do	{
      net = (CANTNet)enum.nextElement();
      net.cantFrame.dispose();
	}
    while (enum.hasMoreElements());
	
    CANTStep=0;  
  }
  
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
System.out.println("initialize Experiment ");
  
    experiment = new CANTExperiment();  
    experiment.printExpName();
  }

  //run one step of the simulation.
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

  public void init() {System.out.println("Init");}

  private static void connectNets(CANTNet net1, CANTNet net2) {
    net1.setOtherConnections(net2,5);
    net2.setOtherConnections(net1,5);
  }

/*
    public static void setConnectionToOther(CANTNet fromNet, int fromNeuronID, double weight,
                                                  String toNetName, int toNeuronID) {
       CANTNet toNet;
       toNet = (CANTNet)nets.get(toNetName);
       fromNet.setConnectionToOther(fromNeuronID, weight, toNet, toNeuronID);
    }
    public static void reconnectFromOtherNets(CANTNet newNet) {
            String toNetName = newNet.getName();
            Enumeration enum = nets.elements();
            while (enum.hasMoreElements()) {
              CANTNet net =(CANTNet)enum.nextElement();
              if (!net.getName().equals(toNetName) )
                newNet.resetConnectionsFromOther(net);
            }
    }
*/

  //embedded Thread class
  public static class WorkerThread extends Thread{
    public void run(){
System.out.println("Thread ");
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
