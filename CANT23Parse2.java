
import java.util.*;
import java.awt.*;

public class CANT23Parse2 extends CANT23{
  public static String ExperimentXMLFile = "parse2.xml";
  public static Parse2Net nullNet;
  public static Parse2Experiment experiment;
  

  public static void main(String args[]){
System.out.println("initialize CANT Parse 2");
    readNewSystem();
    positionWindows();
	delayBetweenSteps=0;
  }
  
  private static void connectAllNets() {
    Parse2Net  inputNet = (Parse2Net)experiment.getNet("BaseNet");
	Parse2Net  instanceNet = (Parse2Net)experiment.getNet("InstanceNet");
	Parse2Net  verbNet = (Parse2Net)experiment.getNet("VerbNet");
	Parse2Net  nounNet = (Parse2Net)experiment.getNet("NounNet");
	Parse2Net  otherNet = (Parse2Net)experiment.getNet("OWordNet");
	Parse2Net  stackTopNet = (Parse2Net)experiment.getNet("StackTopNet");
	Parse2Net  stackNet = (Parse2Net)experiment.getNet("StackNet");
	Parse2Net  pushNet = (Parse2Net)experiment.getNet("PushNet");
	Parse2Net  popNet = (Parse2Net)experiment.getNet("PopNet");
	Parse2Net  ruleNet = (Parse2Net)experiment.getNet("RuleNet");
	Parse2Net  eraseNet = (Parse2Net)experiment.getNet("EraseNet");
	Parse2Net  eraseBoundNet = (Parse2Net)experiment.getNet("EraseBoundNet");
	Parse2Net  testNet = (Parse2Net)experiment.getNet("TestNet");
	
	inputNet.connectInputToVerb(verbNet);
    inputNet.connectInputToNoun(nounNet);
    inputNet.connectInputToOther(otherNet);
    inputNet.connectInputToPush(pushNet);
    nounNet.connectNounToRule(ruleNet);
    nounNet.connectNounToInstance(instanceNet);
    otherNet.connectOtherToRule(ruleNet);
    otherNet.connectOtherToInstance(instanceNet);
    verbNet.connectVerbToRule(ruleNet);
    verbNet.connectVerbToInstance(instanceNet);
    instanceNet.connectInstanceToRule(ruleNet);
    stackTopNet.connectStackTopToPush(pushNet);
    stackTopNet.connectStackTopToStack(stackNet);
    stackTopNet.connectStackTopToEraseBound(eraseBoundNet);
    stackNet.connectStackToVerb(verbNet);
    stackNet.connectStackToOther(otherNet);
    stackNet.connectStackToNoun(nounNet);
    popNet.connectPopToRule(ruleNet);
    popNet.connectPopToVerb(verbNet);
    popNet.connectPopToNoun(nounNet);
    popNet.connectPopToOther(otherNet);
    popNet.connectPopToStack(stackNet);
    popNet.connectPopToStackTop(stackTopNet);
    popNet.connectPopToErase(eraseNet);
    pushNet.connectPushToVerbNet(verbNet);
    pushNet.connectPushToOtherNet(otherNet);
    pushNet.connectPushToNounNet(nounNet);
    pushNet.connectPushToStack(stackNet);
    pushNet.connectPushToStackTop(stackTopNet);
    pushNet.connectPushToTest(testNet);
	eraseNet.connectEraseToBound(eraseBoundNet);
	eraseNet.connectEraseToPop(popNet);
	eraseNet.connectEraseToStack(stackNet);
	eraseNet.connectEraseToVerb(verbNet);
	eraseNet.connectEraseToNoun(nounNet);
	eraseNet.connectEraseToOther(otherNet);
	eraseNet.connectEraseToRule(ruleNet);
	eraseNet.connectEraseToTest(testNet);
	eraseBoundNet.connectErBoundToStack(stackNet);
	testNet.connectTestToPush(pushNet);
    testNet.connectTestToStack(stackNet);
    testNet.connectTestToVerb(verbNet);
    testNet.connectTestToNoun(nounNet);
	testNet.connectTestToOther(otherNet);
	testNet.connectTestToInstance(instanceNet);
	testNet.connectTestToErase(eraseNet);
	ruleNet.connectRuleToTest(testNet);
	ruleNet.connectRuleToStack(stackNet);
	ruleNet.connectRuleToInput(inputNet);
	ruleNet.connectRuleToStackTop(stackTopNet);
	ruleNet.connectRuleToVerb(verbNet);
	ruleNet.connectRuleToInstance(instanceNet);
	ruleNet.connectRuleToPop(popNet);
  }
  
  protected static void readNewSystem() {
  	nullNet = new Parse2Net();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
	//setting the initial topologies comes via a netmgr call to net.initializeNeurons
    workerThread = new CANT23Parse2.WorkerThread();
    initializeExperiment();
	experiment.printExpName();
    Parse2Net net = (Parse2Net) experiment.getNet("BaseNet");
    workerThread.start();
	connectAllNets();	
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    Enumeration enum = nets.elements();
    experiment = new Parse2Experiment();
    System.out.println("initialize Parse Experiment");
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

    Enumeration enum = nets.elements();
    while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
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
		readNewSystem();
		//makeNewSystem(numSystems);
	  }
  }
  
  private static void positionWindows() {
    Parse2Net baseNet = (Parse2Net)experiment.getNet("BaseNet");
    Parse2Net instanceNet = (Parse2Net)experiment.getNet("InstanceNet");
    Parse2Net verbNet = (Parse2Net)experiment.getNet("VerbNet");
    Parse2Net nounNet = (Parse2Net)experiment.getNet("NounNet");
    Parse2Net oWordNet = (Parse2Net)experiment.getNet("OWordNet");
    Parse2Net stackTopNet = (Parse2Net)experiment.getNet("StackTopNet");
    Parse2Net stackNet = (Parse2Net)experiment.getNet("StackNet");
    Parse2Net pushNet = (Parse2Net)experiment.getNet("PushNet");
    Parse2Net popNet = (Parse2Net)experiment.getNet("PopNet");
    Parse2Net ruleNet = (Parse2Net)experiment.getNet("RuleNet");
    Parse2Net eraseNet = (Parse2Net)experiment.getNet("EraseNet");
    Parse2Net testNet = (Parse2Net)experiment.getNet("TestNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (500,500);
    baseNet.cantFrame.matrix.addStringsToPrint ("Follow",70);
    baseNet.cantFrame.matrix.addStringsToPrint ("me",130);
    baseNet.cantFrame.matrix.addStringsToPrint ("Period",190);
    baseNet.cantFrame.matrix.addStringsToPrint ("Go",250);
    baseNet.cantFrame.matrix.addStringsToPrint ("Turn",310);
    baseNet.cantFrame.matrix.addStringsToPrint ("Move",370);
    baseNet.cantFrame.matrix.addStringsToPrint ("it",430);
    baseNet.cantFrame.matrix.addStringsToPrint ("left",490);
    baseNet.cantFrame.matrix.addStringsToPrint ("right",550);
    baseNet.cantFrame.matrix.addStringsToPrint ("forward",610);
    baseNet.cantFrame.matrix.addStringsToPrint ("back",670);
    baseNet.cantFrame.matrix.addStringsToPrint ("pyramid",730);
    baseNet.cantFrame.matrix.addStringsToPrint ("stalagtite",790);
    baseNet.cantFrame.matrix.addStringsToPrint ("door",850);
    baseNet.cantFrame.matrix.addStringsToPrint ("the",910);
    baseNet.cantFrame.matrix.addStringsToPrint ("toward",970);
    instanceNet.cantFrame.setLocation(50,500);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Nouns",720);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Preps",1100);
    instanceNet.cantFrame.setSize (500,300);
    verbNet.cantFrame.setLocation(500,0);
    verbNet.cantFrame.setSize (500,500);
    verbNet.cantFrame.matrix.addStringsToPrint ("Follow",300);
    verbNet.cantFrame.matrix.addStringsToPrint ("Move",600);
    verbNet.cantFrame.matrix.addStringsToPrint ("Turn",900);
    verbNet.cantFrame.matrix.addStringsToPrint ("Go",1100);
    nounNet.cantFrame.setLocation(500,400);
    nounNet.cantFrame.setSize (500,500);
    nounNet.cantFrame.matrix.addStringsToPrint ("me",180);
    nounNet.cantFrame.matrix.addStringsToPrint ("left",360);
    nounNet.cantFrame.matrix.addStringsToPrint ("right",540);
    nounNet.cantFrame.matrix.addStringsToPrint ("forward",720);
    nounNet.cantFrame.matrix.addStringsToPrint ("back",900);
    nounNet.cantFrame.matrix.addStringsToPrint ("pyramid",1080);
    nounNet.cantFrame.matrix.addStringsToPrint ("stalagtite",1260);
    nounNet.cantFrame.matrix.addStringsToPrint ("door",1440);
    nounNet.cantFrame.matrix.addStringsToPrint ("it",1620);
    nounNet.cantFrame.show();
    oWordNet.cantFrame.setLocation(525,425);
    oWordNet.cantFrame.setSize (500,500);
    oWordNet.cantFrame.matrix.addStringsToPrint ("period",180);
    oWordNet.cantFrame.matrix.addStringsToPrint ("the",360);
    oWordNet.cantFrame.matrix.addStringsToPrint ("toward",540);
    oWordNet.cantFrame.show();
    ruleNet.cantFrame.setLocation(550,450);
    ruleNet.cantFrame.setSize (500,500);
    ruleNet.cantFrame.matrix.addStringsToPrint ("VP->VP NP-obj",120);
    ruleNet.cantFrame.matrix.addStringsToPrint ("VP->VP period",240);
    ruleNet.cantFrame.matrix.addStringsToPrint ("PP->PP det",360);
    ruleNet.cantFrame.matrix.addStringsToPrint ("PP->PP noun",480);
    ruleNet.cantFrame.matrix.addStringsToPrint ("VP->VP PP-loc",600);
    ruleNet.cantFrame.matrix.addStringsToPrint ("PP->PP prep",720);
    pushNet.cantFrame.setLocation(350,450);
    pushNet.cantFrame.setSize (500,500);
    popNet.cantFrame.setLocation(300,550);
    popNet.cantFrame.setSize (500,400);
//    ruleNet.cantFrame.show();
    stackTopNet.cantFrame.setLocation(0,500);
    stackTopNet.cantFrame.setSize (500,500);
    stackTopNet.cantFrame.matrix.addStringsToPrint ("zero",120);
    stackTopNet.cantFrame.matrix.addStringsToPrint ("one",240);
    stackTopNet.cantFrame.matrix.addStringsToPrint ("two",360);
    stackTopNet.cantFrame.matrix.addStringsToPrint ("three",480);
    stackTopNet.cantFrame.matrix.addStringsToPrint ("four",600);
    stackTopNet.cantFrame.show();
    stackNet.cantFrame.setLocation(600,400);
    stackNet.cantFrame.setSize (500,500);
    eraseNet.cantFrame.setLocation(800,0);
    eraseNet.cantFrame.setSize (500,1000);
    testNet.cantFrame.setLocation(800,0);
    testNet.cantFrame.setSize (500,1000);
    testNet.cantFrame.matrix.addStringsToPrint ("Test 0",120);
    testNet.cantFrame.matrix.addStringsToPrint ("Stop 1 Test 1 ",240);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 3-1  Test 2 ",360);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 3-2 Test 3",480);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 3-3 Test 4",600);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 3-4 Test 5",720);
    testNet.cantFrame.matrix.addStringsToPrint ("Stop 2 Test 6",840);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 2-1 Test 7",960);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 2-2 Test 8",1080);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 2-3 Test 9",1200);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 2-4 Test 10 ",1320);
    testNet.cantFrame.matrix.addStringsToPrint ("Stop 3 Test 11",1440);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 1-1 Test 12",1560);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 1-2 Test 13",1680);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 1-3 Test 14",1800);
    testNet.cantFrame.matrix.addStringsToPrint ("Stack 1-4 Test 15",1920);
    testNet.cantFrame.matrix.addStringsToPrint ("Stop 4 Test 16",2040);
    testNet.cantFrame.matrix.addStringsToPrint ("Stop 5 Test 17",2160);
    testNet.cantFrame.matrix.addStringsToPrint ("Test 18 Push",2280);
    nounNet.cantFrame.show();
    verbNet.cantFrame.show();
    eraseNet.cantFrame.show();
    testNet.cantFrame.show();
    stackTopNet.cantFrame.show();
    stackNet.cantFrame.show();
    ruleNet.cantFrame.show();
    pushNet.cantFrame.show();
    popNet.cantFrame.show();
    instanceNet.cantFrame.show();
    baseNet.cantFrame.show();
  }
  
  //embedded Thread class
  public static class WorkerThread extends CANT23.WorkerThread{
    public void run(){
      System.out.println("Parse 1 Thread ");
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