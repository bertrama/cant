
import java.util.*;

public class CANT23VB extends CANT23{
  public static String ExperimentXMLFile = "vb/vbltp.xml";
  public static VBNet nullNet;
  public static boolean runFastBindTest = true;

  public static void main(String args[]){
System.out.println("initialize CANT VB " + runFastBindTest);
    if (runFastBindTest) ExperimentXMLFile = "vb/vbfb.xml";
	  
    makeNewSystem();
	positionWindows();
    delayBetweenSteps=0;
  }

  protected static void makeNewSystem() {
  	nullNet = new VBNet();
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    CANT23VB.WorkerThread workerThread = new CANT23VB.WorkerThread();
    initializeExperiment();
    workerThread.start();	
	connectAllNets();
  }
  
  private static void connectAllNets() {
    VBNet numberNet = (VBNet)experiment.getNet("BaseNet");
    VBNet letterNet = (VBNet)experiment.getNet("letter");
    if (runFastBindTest) 
	  {
	  letterNet.connectLetterToNumber(numberNet);
	  numberNet.connectNumberToLetter(letterNet);
	  }
	else 
      {
      VBNet bindNet = (VBNet)experiment.getNet("bind");
    
      letterNet.connectLetterToBind(bindNet);
      numberNet.connectNumberToBind(bindNet);
      bindNet.connectBindToLetter(letterNet);
      bindNet.connectBindToNumber(numberNet);
      }
  }

  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
System.out.println("initialize VB Experiment ");
  
    Enumeration eNum = nets.elements();
    CANTNet net = (CANTNet)eNum.nextElement();
    experiment = new VBExperiment(net);

    experiment.printExpName();
  }
  
  private static int numSystems = -1;
  public static void runOneStepStart() {
    if (experiment.trainingLength == CANTStep) experiment.switchToTest();
    if (experiment.getInTest()) experiment.measure(CANTStep);
  
    if (experiment.isEndEpoch(CANTStep))
      experiment.endEpoch();
  }
  
  public static synchronized void runOneStep() {
    //runOneStepStart();

    Enumeration eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      VBNet net = (VBNet)eNum.nextElement();
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
  	numSystems++;
  	makeNewSystem();
    }
  }
  
  private static void positionWindows() {
    VBNet numberNet = (VBNet)experiment.getNet("BaseNet");
    VBNet letterNet = (VBNet)experiment.getNet("letter");
	numberNet.cantFrame.setSize (500,400);
	numberNet.cantFrame.setLocation(0,0);
	numberNet.cantFrame.matrix.addStringsToPrint ("0",35,260);
	numberNet.cantFrame.matrix.addStringsToPrint ("1",60);	
	numberNet.cantFrame.matrix.addStringsToPrint ("2",85);	
	numberNet.cantFrame.matrix.addStringsToPrint ("3",110);	
	numberNet.cantFrame.matrix.addStringsToPrint ("4",130);	
	numberNet.cantFrame.matrix.addStringsToPrint ("5",155);	
	numberNet.cantFrame.matrix.addStringsToPrint ("6",180);	
	numberNet.cantFrame.matrix.addStringsToPrint ("7",205);	
	numberNet.cantFrame.matrix.addStringsToPrint ("8",230);	
	numberNet.cantFrame.matrix.addStringsToPrint ("9",250);	
	letterNet.cantFrame.setLocation(550,0);
	letterNet.cantFrame.matrix.addStringsToPrint ("A",35,260);
	letterNet.cantFrame.matrix.addStringsToPrint ("B",60);
	letterNet.cantFrame.matrix.addStringsToPrint ("C",85);
	letterNet.cantFrame.matrix.addStringsToPrint ("D",110);
	letterNet.cantFrame.matrix.addStringsToPrint ("E",130);
	letterNet.cantFrame.matrix.addStringsToPrint ("F",155);
	letterNet.cantFrame.matrix.addStringsToPrint ("G",180);
	letterNet.cantFrame.matrix.addStringsToPrint ("H",205);
	letterNet.cantFrame.matrix.addStringsToPrint ("I",230);
	letterNet.cantFrame.matrix.addStringsToPrint ("J",250);
	letterNet.cantFrame.show();
	numberNet.cantFrame.show();
	
	if (!runFastBindTest) 
	  {
	  VBNet bindNet = (VBNet)experiment.getNet("bind");
	  bindNet.cantFrame.setLocation(250,450);	
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

