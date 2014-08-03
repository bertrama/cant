
import java.util.*;
import java.awt.*;

public class CABot1 extends CANT23{
  public static String ExperimentXMLFile = "CABot1/cabot1.xml";
  public static CABot1Net nullNet;
  public static CABot1Experiment experiment;
  public static BmpReader bmpReader = new BmpReader();

  public static void main(String args[]){
    makeNewSystem();
    positionWindows();
    delayBetweenSteps=0;
    bmpReader.setFilePath ("c:/progs/cant/CANT23/CABot1/data/");
    experiment.setFilePath ("c:/progs/cant/CANT23/CABot1/data/");
  }
  
  private static void connectParseNets() {
    CABot1Net  inputNet = (CABot1Net)experiment.getNet("BaseNet");
    CABot1Net  instanceNet = (CABot1Net)experiment.getNet("InstanceNet");
    CABot1Net  verbNet = (CABot1Net)experiment.getNet("VerbNet");
    CABot1Net  nounNet = (CABot1Net)experiment.getNet("NounNet");
    CABot1Net  otherNet = (CABot1Net)experiment.getNet("OWordNet");
    CABot1Net  stackTopNet = (CABot1Net)experiment.getNet("StackTopNet");
    CABot1Net  stackNet = (CABot1Net)experiment.getNet("StackNet");
    CABot1Net  pushNet = (CABot1Net)experiment.getNet("PushNet");
    CABot1Net  popNet = (CABot1Net)experiment.getNet("PopNet");
    CABot1Net  ruleNet = (CABot1Net)experiment.getNet("RuleNet");
    CABot1Net  eraseNet = (CABot1Net)experiment.getNet("EraseNet");
    CABot1Net  eraseBoundNet = (CABot1Net)experiment.getNet("EraseBoundNet");
    CABot1Net  testNet = (CABot1Net)experiment.getNet("TestNet");
  
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

  private static void connectVisionNets() {
    CABot1Net  visualInputNet = (CABot1Net)experiment.getNet("VisualInputNet");
    CABot1Net  retinaNet = (CABot1Net)experiment.getNet("RetinaNet");
    CABot1Net  V1Net = (CABot1Net)experiment.getNet("V1Net");
    CABot1Net  V2Net = (CABot1Net)experiment.getNet("V2Net");
    visualInputNet.connectInputToRetina(retinaNet);
    retinaNet.connectRetinaToV1(V1Net);
    V1Net.connectV1ToV2(V2Net); 
  }

  private static void connectAllNets() {
    CABot1Net  controlNet = (CABot1Net)experiment.getNet("ControlNet");
    CABot1Net  actionNet = (CABot1Net)experiment.getNet("ActionNet");
    CABot1Net  verbNet = (CABot1Net)experiment.getNet("VerbNet");
    CABot1Net  instanceNet = (CABot1Net)experiment.getNet("InstanceNet");
    CABot1Net  ruleNet = (CABot1Net)experiment.getNet("RuleNet");
    CABot1Net  testNet = (CABot1Net)experiment.getNet("TestNet");
    CABot1Net  eraseNet = (CABot1Net)experiment.getNet("EraseNet");
    CABot1Net  eraseBoundNet = (CABot1Net)experiment.getNet("EraseBoundNet");
    CABot1Net  stackNet = (CABot1Net)experiment.getNet("StackNet");
    CABot1Net  stackTopNet = (CABot1Net)experiment.getNet("StackTopNet");
    CABot1Net  factNet = (CABot1Net)experiment.getNet("FactNet");
    CABot1Net  moduleNet = (CABot1Net)experiment.getNet("ModuleNet");
    CABot1Net  V2Net = (CABot1Net)experiment.getNet("V2Net");
	
    connectParseNets();

    connectVisionNets();

    //label visual items
    //V2Net.connectV2ToInstance(instanceNet);

    //connect words to facts
    verbNet.connectVerbToFact(factNet);
    instanceNet.connectInstanceToFact(factNet);

    //connect visual items to facts
    V2Net.connectV2ToFact(factNet);

    //connect facts, modules and actions
    factNet.connectFactToModule(moduleNet);
    moduleNet.connectModuletoFact(factNet);
    moduleNet.connectModuleToAction(actionNet);
    actionNet.connectActionToModule(moduleNet);
    
    //manage the control structure
    controlNet.connectControlToFact(factNet); 
    stackTopNet.connectStackTopToControl(controlNet);
    controlNet.connectControlToRule(ruleNet); 
    controlNet.connectControlToStack(stackNet);
    factNet.connectFactToControl(controlNet);
    controlNet.connectControlToInstance(instanceNet);
    controlNet.connectControlToVerb(verbNet);
    controlNet.connectControlToErase(eraseNet);
    controlNet.connectControlToEraseBound(eraseBoundNet);
    eraseNet.connectEraseToControl(controlNet);
    controlNet.connectControlToTest(testNet);
  }

  private static void readNets() {
  }

  protected static void makeNewSystem() {
    nullNet = new CABot1Net();
	
    System.out.println("Make CABot1 Nets");
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);

    workerThread = new CABot1.WorkerThread();
    initializeExperiment();
    experiment.printExpName();
    CABot1Net net = (CABot1Net) experiment.getNet("BaseNet");
    workerThread.start();	
    connectAllNets();	
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    experiment = new CABot1Experiment();
    System.out.println("initialize CABot 1 Experiment");
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
      CABot1Net net = (CABot1Net)eNum.nextElement();
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
  
  private  static void minimizeParseWindows() {
    CABot1Net instanceNet = (CABot1Net)experiment.getNet("InstanceNet");
    CABot1Net verbNet = (CABot1Net)experiment.getNet("VerbNet");
    CABot1Net nounNet = (CABot1Net)experiment.getNet("NounNet");
    CABot1Net oWordNet = (CABot1Net)experiment.getNet("OWordNet");
    CABot1Net stackTopNet = (CABot1Net)experiment.getNet("StackTopNet");
    CABot1Net stackNet = (CABot1Net)experiment.getNet("StackNet");
    CABot1Net pushNet = (CABot1Net)experiment.getNet("PushNet");
    CABot1Net popNet = (CABot1Net)experiment.getNet("PopNet");
    CABot1Net ruleNet = (CABot1Net)experiment.getNet("RuleNet");
    CABot1Net eraseNet = (CABot1Net)experiment.getNet("EraseNet");
    CABot1Net eraseBoundNet = (CABot1Net)experiment.getNet("EraseBoundNet");
    CABot1Net testNet = (CABot1Net)experiment.getNet("TestNet");

    instanceNet.cantFrame.setState(Frame.ICONIFIED);
    verbNet.cantFrame.setState(Frame.ICONIFIED);
    nounNet.cantFrame.setState(Frame.ICONIFIED);
    oWordNet.cantFrame.setState(Frame.ICONIFIED);
    stackTopNet.cantFrame.setState(Frame.ICONIFIED);
    stackNet.cantFrame.setState(Frame.ICONIFIED);
    pushNet.cantFrame.setState(Frame.ICONIFIED);
    popNet.cantFrame.setState(Frame.ICONIFIED);
    ruleNet.cantFrame.setState(Frame.ICONIFIED);
    eraseNet.cantFrame.setState(Frame.ICONIFIED);
    eraseBoundNet.cantFrame.setState(Frame.ICONIFIED);
    testNet.cantFrame.setState(Frame.ICONIFIED);
  }
  
  private  static void positionParseWindows() {
    CABot1Net instanceNet = (CABot1Net)experiment.getNet("InstanceNet");
    CABot1Net verbNet = (CABot1Net)experiment.getNet("VerbNet");
    CABot1Net nounNet = (CABot1Net)experiment.getNet("NounNet");
    CABot1Net oWordNet = (CABot1Net)experiment.getNet("OWordNet");
    CABot1Net stackTopNet = (CABot1Net)experiment.getNet("StackTopNet");
    CABot1Net stackNet = (CABot1Net)experiment.getNet("StackNet");
    CABot1Net pushNet = (CABot1Net)experiment.getNet("PushNet");
    CABot1Net popNet = (CABot1Net)experiment.getNet("PopNet");
    CABot1Net ruleNet = (CABot1Net)experiment.getNet("RuleNet");
    CABot1Net eraseNet = (CABot1Net)experiment.getNet("EraseNet");
    CABot1Net testNet = (CABot1Net)experiment.getNet("TestNet");

    minimizeParseWindows();	
    instanceNet.cantFrame.setLocation(300,0);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Me",30,210);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Left",60,210);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Pyramid",90,210);
    instanceNet.cantFrame.matrix.addStringsToPrint ("It",120,210);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Right",150,210);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Forward",180,210);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Backward",210,210);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Stalagtite",240,210);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Door",270,210);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Preps",360,210);
    instanceNet.cantFrame.matrix.addStringsToPrint ("Toward",420,210);
    instanceNet.cantFrame.matrix.addStringsToPrint ("To",480,210);
    instanceNet.cantFrame.setSize (500,300);
    verbNet.cantFrame.setLocation(500,0);
    verbNet.cantFrame.setSize (500,500);
    verbNet.cantFrame.matrix.addStringsToPrint ("Follow",150,210);
    verbNet.cantFrame.matrix.addStringsToPrint ("Move",300,210);
    verbNet.cantFrame.matrix.addStringsToPrint ("Turn",450,210);
    verbNet.cantFrame.matrix.addStringsToPrint ("Go",600,210);
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
    oWordNet.cantFrame.setLocation(525,425);
    oWordNet.cantFrame.setSize (500,500);
    oWordNet.cantFrame.matrix.addStringsToPrint ("period",90,210);
    oWordNet.cantFrame.matrix.addStringsToPrint ("the",180,210);
    oWordNet.cantFrame.matrix.addStringsToPrint ("toward",270,210);
    oWordNet.cantFrame.matrix.addStringsToPrint ("to",360,210);
    ruleNet.cantFrame.setLocation(550,450);
    ruleNet.cantFrame.setSize (500,500);
    ruleNet.cantFrame.matrix.addStringsToPrint ("VP->VP NP-obj",60,210);
    ruleNet.cantFrame.matrix.addStringsToPrint ("VP->VP period",120,210);
    ruleNet.cantFrame.matrix.addStringsToPrint ("PP->PP det",180,210);
    ruleNet.cantFrame.matrix.addStringsToPrint ("PP->PP noun",240,210);
    ruleNet.cantFrame.matrix.addStringsToPrint ("VP->VP PP-loc",300,210);
    ruleNet.cantFrame.matrix.addStringsToPrint ("PP->PP prep",360,210);
    pushNet.cantFrame.setLocation(350,450);
    pushNet.cantFrame.setSize (500,500);
    popNet.cantFrame.setLocation(300,550);
    popNet.cantFrame.setSize (500,400);
    stackTopNet.cantFrame.setLocation(0,500);
    stackTopNet.cantFrame.setSize (300,500); 
    stackTopNet.cantFrame.matrix.addStringsToPrint ("zero",60,210);
    stackTopNet.cantFrame.matrix.addStringsToPrint ("one",120,210);
    stackTopNet.cantFrame.matrix.addStringsToPrint ("two",180,210);
    stackTopNet.cantFrame.matrix.addStringsToPrint ("three",240,210);
    stackTopNet.cantFrame.matrix.addStringsToPrint ("four",300,210);
    stackNet.cantFrame.setLocation(600,400);
    stackNet.cantFrame.setSize (500,500);
    eraseNet.cantFrame.setLocation(900,0);
    eraseNet.cantFrame.setSize (300,700);
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
    //stackTopNet.cantFrame.show();
    //nounNet.cantFrame.show();
    //verbNet.cantFrame.show();
    //oWordNet.cantFrame.show();
    //eraseNet.cantFrame.show();
    //testNet.cantFrame.show();
    //stackNet.cantFrame.show();
    //ruleNet.cantFrame.show();
    //pushNet.cantFrame.show();
    //popNet.cantFrame.show();
    //instanceNet.cantFrame.show();
  }

  private  static void positionVisionWindows() {
    CABot1Net retinaNet = (CABot1Net)experiment.getNet("RetinaNet");
    CABot1Net V1Net = (CABot1Net)experiment.getNet("V1Net");
    CABot1Net V2Net = (CABot1Net)experiment.getNet("V2Net");
    //for 100x100
    //int hTextStart = 600;
    //int vTextStart = 650;
    //for 50x50 input
    int hTextStart = 300;
    int vTextStart = 350;
  
    retinaNet.cantFrame.setLocation(600,0);
    retinaNet.cantFrame.setSize (650,400);
    retinaNet.cantFrame.matrix.addStringsToPrint ("3x3On",hTextStart,vTextStart);
    retinaNet.cantFrame.matrix.addStringsToPrint ("3x3Off",hTextStart*2);
    retinaNet.cantFrame.matrix.addStringsToPrint ("6x6On",hTextStart*3); 
    retinaNet.cantFrame.matrix.addStringsToPrint ("6x6Off",hTextStart*4);
    retinaNet.cantFrame.matrix.addStringsToPrint ("9x9On",hTextStart*5);
    retinaNet.cantFrame.matrix.addStringsToPrint ("9x9Off",hTextStart*6);
    V1Net.cantFrame.setLocation(0,400);
    V1Net.cantFrame.setSize (600,350);
    V1Net.cantFrame.matrix.addStringsToPrint ("HLine",hTextStart,vTextStart);
    V1Net.cantFrame.matrix.addStringsToPrint ("Slash Line",hTextStart*2);
    V1Net.cantFrame.matrix.addStringsToPrint ("BackSlash Line",hTextStart*3);
    V1Net.cantFrame.matrix.addStringsToPrint ("And Angle",hTextStart*4);
    V1Net.cantFrame.matrix.addStringsToPrint ("Less Than Angle",hTextStart*5);
    V1Net.cantFrame.matrix.addStringsToPrint ("Greater Than Angle",hTextStart*6);
    V1Net.cantFrame.matrix.addStringsToPrint ("Or Angle",hTextStart*7);
    V1Net.cantFrame.matrix.addStringsToPrint ("HEdge",hTextStart*8);
    V1Net.cantFrame.matrix.addStringsToPrint ("SEdge",hTextStart*9);
    V1Net.cantFrame.matrix.addStringsToPrint ("BEdge",hTextStart*10);
    V2Net.cantFrame.setLocation(600,400);
    V2Net.cantFrame.setSize (650,350);
    V2Net.cantFrame.matrix.addStringsToPrint("SPyramid",hTextStart,vTextStart);
    V2Net.cantFrame.matrix.addStringsToPrint ("SStalagtite",hTextStart*2);
    V2Net.cantFrame.matrix.addStringsToPrint ("MPyramid",hTextStart*3);
    V2Net.cantFrame.matrix.addStringsToPrint ("MStalagtite",hTextStart*4);
    V2Net.cantFrame.matrix.addStringsToPrint ("LPyramid",hTextStart*5);
    V2Net.cantFrame.matrix.addStringsToPrint ("LStalagtite",hTextStart*6);
  }

  private  static void positionWindows() {
    CABot1Net baseNet = (CABot1Net)experiment.getNet("BaseNet");
    CABot1Net visionInputNet = (CABot1Net)experiment.getNet("VisualInputNet");
    CABot1Net controlNet = (CABot1Net)experiment.getNet("ControlNet");
    CABot1Net factNet = (CABot1Net)experiment.getNet("FactNet");
    CABot1Net moduleNet = (CABot1Net)experiment.getNet("ModuleNet");
    CABot1Net actionNet = (CABot1Net)experiment.getNet("ActionNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (500,300);
    baseNet.cantFrame.matrix.addStringsToPrint ("Follow",30,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("me",60,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("Period",90,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("Go",120,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("Turn",150,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("Move",180,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("it",210,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("left",240,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("right",270,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("forward",300,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("back",330,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("pyramid",360,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("stalagtite",390,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("door",420,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("the",450,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("toward",480,210);
    baseNet.cantFrame.matrix.addStringsToPrint ("to",510,210);
    baseNet.cantFrame.show();
	
    //add for factNet, moduleNet and actionNet
    factNet.cantFrame.matrix.addStringsToPrint ("turn+left",30,150);
    factNet.cantFrame.matrix.addStringsToPrint ("turn+right",60,150);
    factNet.cantFrame.matrix.addStringsToPrint ("move+forward",90,150);
    factNet.cantFrame.matrix.addStringsToPrint ("move+backward",120,150);
    factNet.cantFrame.matrix.addStringsToPrint ("goleft1",150,150);
    factNet.cantFrame.matrix.addStringsToPrint ("forward after turn",180,150);
    factNet.cantFrame.matrix.addStringsToPrint ("go right1",210,150); //7
    factNet.cantFrame.matrix.addStringsToPrint ("turn toward",240,150);
    factNet.cantFrame.matrix.addStringsToPrint ("goal pyramid",270,150);
    factNet.cantFrame.matrix.addStringsToPrint ("goal stal",300,150);
    factNet.cantFrame.matrix.addStringsToPrint ("go to",330,150);
    factNet.cantFrame.matrix.addStringsToPrint ("pyramid in scene",360,150);
    factNet.cantFrame.matrix.addStringsToPrint ("stal in scene",390,150);
    factNet.cantFrame.matrix.addStringsToPrint ("object in left",420,150);
    factNet.cantFrame.matrix.addStringsToPrint ("object in centre",450,150);
    factNet.cantFrame.matrix.addStringsToPrint ("object in right",480,150);
    factNet.cantFrame.matrix.addStringsToPrint ("object big",520,150);
    
    moduleNet.cantFrame.matrix.addStringsToPrint ("turnleft",30,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("turnright",60,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("moveforward",90,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("movebackward",120,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("goleft1",150,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("forward aft turn",180,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("go right1",210,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("turn centre",240,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("turn toward nil",270,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("inactive",300,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("go to done",330,150);
    
    actionNet.cantFrame.matrix.addStringsToPrint ("turnleft",30,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("turnright",60,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("forward",90,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("backward",120,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("turn centre",150,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("turn toward nil",180,140);
    
    positionParseWindows();
    positionVisionWindows();

    visionInputNet.cantFrame.setLocation(900,0);
    visionInputNet.cantFrame.setSize (400,450);
    visionInputNet.cantFrame.show();
    controlNet.cantFrame.setLocation(0,600);
    controlNet.cantFrame.setSize (300,300);
    controlNet.cantFrame.show();

    factNet.cantFrame.setLocation(0,300);
    factNet.cantFrame.setSize (300,400);
    factNet.cantFrame.show();
    moduleNet.cantFrame.setLocation(300,300);
    moduleNet.cantFrame.setSize (300,400);
    moduleNet.cantFrame.show();

    actionNet.cantFrame.setLocation(600,300);
    actionNet.cantFrame.setSize (300,300);
    actionNet.cantFrame.show();
  }
  
  //embedded Thread class
  public static class WorkerThread extends CANT23.WorkerThread{
    public void run(){
      System.out.println("CABot1 Thread ");
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
