
import java.util.*;
import java.awt.*;

public class CABot2 extends CANT23{
  public static String ExperimentXMLFile = "cabot2/cabot2.xml";
  public static CABot2Net nullNet;
  public static CABot2Experiment experiment;
  public static BmpReader bmpReader = new BmpReader();

  public static void main(String args[]){
    initRandom();
    seed = 25;
    makeNewSystem();
    positionWindows();
    delayBetweenSteps=0;
    bmpReader.setFilePath ("c:/progs/cant/CANT23/cabot2/data/");
  }
  
  public static void setBarOneOn(boolean on) {
    CABot2Net  barOneNet = (CABot2Net)experiment.getNet("BarOneNet");
    if (on) {
      barOneNet.setNeuronsToStimulate(50);
      barOneNet.setCurrentPattern(0);
    }
    else
      barOneNet.setNeuronsToStimulate(0);
  }
  
  private static void connectParseNets() {
    CABot2Net  inputNet = (CABot2Net)experiment.getNet("BaseNet");
    CABot2Net  barOneNet = (CABot2Net)experiment.getNet("BarOneNet");
    CABot2Net  nounAccessNet = (CABot2Net)experiment.getNet("NounAccessNet");
    CABot2Net  nounSemNet = (CABot2Net)experiment.getNet("NounSemNet");
    CABot2Net  ruleNet = (CABot2Net)experiment.getNet("RuleNet");
    CABot2Net  nounInstanceNet = 
      (CABot2Net)experiment.getNet("NounInstanceNet");
    CABot2Net  verbAccessNet = (CABot2Net)experiment.getNet("VerbAccessNet");
    CABot2Net  verbSemNet = (CABot2Net)experiment.getNet("VerbSemNet");
    CABot2Net  verbInstanceNet = 
      (CABot2Net)experiment.getNet("VerbInstanceNet");
    CABot2Net  otherNet = (CABot2Net)experiment.getNet("OtherNet");
    CABot2Net  nPPPNet = (CABot2Net)experiment.getNet("NPPPNet");
    CABot2Net  vPPPNet = (CABot2Net)experiment.getNet("VPPPNet");

    inputNet.connectInputToNounAccess(nounAccessNet);
    inputNet.connectInputToVerbAccess(verbAccessNet);
    inputNet.connectInputToOther(otherNet);
    barOneNet.connectBarOneToAccess(nounAccessNet);
    barOneNet.connectBarOneToAccess(verbAccessNet);
    barOneNet.connectBarOneToAccess(otherNet);
    barOneNet.connectBarOneToRule(ruleNet);
    nounAccessNet.connectNounAccessToSem(nounSemNet);
    nounAccessNet.connectNounAccessToRule(ruleNet);
    ruleNet.connectRuleToBarOne(barOneNet);
    ruleNet.connectRuleToNounInstance(nounInstanceNet);
    ruleNet.connectRuleToNounAccess(nounAccessNet);
    ruleNet.connectRuleToVerbInstance(verbInstanceNet);
    ruleNet.connectRuleToVerbAccess(verbAccessNet);
    ruleNet.connectRuleToOther(otherNet);
    nounInstanceNet.connectNounInstanceToNounAccess(nounAccessNet);
    nounInstanceNet.connectNounInstanceToRule(ruleNet);
    nounInstanceNet.connectNounInstanceToOther(otherNet);
    verbAccessNet.connectVerbAccessToSem(verbSemNet);
    verbAccessNet.connectVerbAccessToRule(ruleNet);
    verbInstanceNet.connectVerbInstanceToVerbAccess(verbAccessNet);
    verbInstanceNet.connectVerbInstanceToRule(ruleNet);
    verbInstanceNet.connectVerbInstanceToNounInstance(nounInstanceNet);
    otherNet.connectOtherToRule(ruleNet);
    verbSemNet.connectVerbSemToVPPP(vPPPNet);
    nounSemNet.connectNounSemToVPPP(vPPPNet);
    verbSemNet.connectVerbSemToNPPP(nPPPNet);
    nounSemNet.connectNounSemToNPPP(nPPPNet);
    vPPPNet.connectVPPPToRule(ruleNet);
    nPPPNet.connectNPPPToRule(ruleNet);
  }

  private static void connectVisionNets() {
    CABot2Net  visualInputNet = (CABot2Net)experiment.getNet("VisualInputNet");
    CABot2Net  retinaNet = (CABot2Net)experiment.getNet("RetinaNet");
    CABot2Net  V1Net = (CABot2Net)experiment.getNet("V1Net");
    CABot2Net  V2Net = (CABot2Net)experiment.getNet("V2Net");
    visualInputNet.connectInputToRetina(retinaNet);
    retinaNet.connectRetinaToV1(V1Net);
    V1Net.connectV1ToV2(V2Net); 
  }

  private static void connectAllNets() {
    CABot2Net controlNet = (CABot2Net)experiment.getNet("ControlNet");
    CABot2Net actionNet = (CABot2Net)experiment.getNet("ActionNet");
    CABot2Net verbSemNet = (CABot2Net)experiment.getNet("VerbSemNet");
    CABot2Net nounSemNet = (CABot2Net)experiment.getNet("NounSemNet");
    CABot2Net goalSetNet = (CABot2Net)experiment.getNet("GoalSetNet");
    CABot2Net goal1Net = (CABot2Net)experiment.getNet("Goal1Net");
    CABot2Net factNet = (CABot2Net)experiment.getNet("FactNet");
    CABot2Net moduleNet = (CABot2Net)experiment.getNet("ModuleNet");
    CABot2Net V2Net = (CABot2Net)experiment.getNet("V2Net");
    CABot2Net goal2Net = (CABot2Net)experiment.getNet("Goal2Net");
    CABot2Net exploreNet = (CABot2Net)experiment.getNet("ExploreNet");
    CABot2Net valueNet = (CABot2Net)experiment.getNet("ValueNet");
    CABot2Net module2Net = (CABot2Net)experiment.getNet("Module2Net");
    CABot2Net value2Net = (CABot2Net)experiment.getNet("Value2Net");
    CABot2Net ruleNet = (CABot2Net)experiment.getNet("RuleNet");
    CABot2Net barOneNet = (CABot2Net)experiment.getNet("BarOneNet");
    CABot2Net nounAccessNet = (CABot2Net)experiment.getNet("NounAccessNet");
    CABot2Net verbAccessNet = (CABot2Net)experiment.getNet("VerbAccessNet");
    CABot2Net nounInstanceNet= (CABot2Net)experiment.getNet("NounInstanceNet");
    CABot2Net verbInstanceNet= (CABot2Net)experiment.getNet("VerbInstanceNet");
    CABot2Net otherNet= (CABot2Net)experiment.getNet("OtherNet");
	
    connectParseNets(); 

    connectVisionNets();

    //integrate parse3 with goal setting
    verbSemNet.connectVerbSemToGoalSet(goalSetNet);
    nounSemNet.connectNounSemToGoalSet(goalSetNet);
    goalSetNet.connectGoalSetToGoal1(goal1Net);

    //connect visual items to facts
    V2Net.connectV2ToFact(factNet);
    factNet.connectFactToV2(V2Net);

    //connect facts, modules and actions
    goal1Net.connectGoal1ToModule(moduleNet);
    goal1Net.connectGoal1ToFact(factNet);
    factNet.connectFactToGoal1(goal1Net);
    factNet.connectFactToModule(moduleNet);
    moduleNet.connectModuletoGoal1(goal1Net);
    moduleNet.connectModuletoFact(factNet);
    moduleNet.connectModuleToAction(actionNet);
    actionNet.connectActionToModule(moduleNet);

    //set up goal learning
    goal1Net.connectGoal1ToGoal2(goal2Net);
    factNet.connectFactToGoal2(goal2Net);
    goal2Net.connectGoal2ToModule2(module2Net);
    valueNet.connectValueToExplore(exploreNet);
    exploreNet.connectExploreToModule2(module2Net);
    factNet.connectFactToValue(valueNet);
    module2Net.connectModule2ToModule1(moduleNet);
    goal2Net.connectGoal2ToModule1(moduleNet);
    valueNet.connectValueToValue2(value2Net);
    value2Net.connectValue2ToGoal2(goal2Net);
    value2Net.connectValue2ToFact(factNet);
    value2Net.connectValue2ToValue(valueNet);

    //hook control up to parsing
    ruleNet.connectRuleToControl(controlNet);
    controlNet.connectControlToBarOne(barOneNet);
    controlNet.connectControlToRule(ruleNet);
    controlNet.connectControlToOther(otherNet);
    controlNet.connectControlToNounAccess(nounAccessNet);
    controlNet.connectControlToVerbAccess(verbAccessNet);
    controlNet.connectControlToNounSem(nounSemNet);
    controlNet.connectControlToVerbSem(verbSemNet);
    controlNet.connectControlToNounInstance(nounInstanceNet);
    controlNet.connectControlToVerbInstance(verbInstanceNet);

    //hook control up to goal setting.
    controlNet.connectControlToGoalSet(goalSetNet);
    controlNet.connectControlToValue2(value2Net);
    value2Net.connectValue2ToControl(controlNet);
  }

  private static void readNets() {
  }

  protected static void makeNewSystem() {
    nullNet = new CABot2Net();
	
    System.out.println("Make CABot2 Nets");
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);

    workerThread = new CABot2.WorkerThread();
    initializeExperiment();
    experiment.printExpName();
    CABot2Net inputNet = (CABot2Net) experiment.getNet("BaseNet");
    workerThread.start();	
    inputNet.setInputPatterns();
    connectAllNets();	
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    experiment = new CABot2Experiment();
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
      CABot2Net net = (CABot2Net)eNum.nextElement();
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
    CABot2Net instanceNet = (CABot2Net)experiment.getNet("InstanceNet");
    CABot2Net verbNet = (CABot2Net)experiment.getNet("VerbNet");
    CABot2Net nounNet = (CABot2Net)experiment.getNet("NounNet");
    CABot2Net oWordNet = (CABot2Net)experiment.getNet("OWordNet");
    CABot2Net stackTopNet = (CABot2Net)experiment.getNet("StackTopNet");
    CABot2Net stackNet = (CABot2Net)experiment.getNet("StackNet");
    CABot2Net pushNet = (CABot2Net)experiment.getNet("PushNet");
    CABot2Net popNet = (CABot2Net)experiment.getNet("PopNet");
    CABot2Net ruleNet = (CABot2Net)experiment.getNet("RuleNet");
    CABot2Net eraseNet = (CABot2Net)experiment.getNet("EraseNet");
    CABot2Net eraseBoundNet = (CABot2Net)experiment.getNet("EraseBoundNet");
    CABot2Net testNet = (CABot2Net)experiment.getNet("TestNet");

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
    CABot2Net inputNet = (CABot2Net)experiment.getNet("BaseNet");
    CABot2Net barOneNet = (CABot2Net)experiment.getNet("BarOneNet");
    CABot2Net nounAccessNet = (CABot2Net)experiment.getNet("NounAccessNet");
    CABot2Net nounSemNet = (CABot2Net)experiment.getNet("NounSemNet");
    CABot2Net ruleNet = (CABot2Net)experiment.getNet("RuleNet");
    CABot2Net nounInstanceNet =(CABot2Net)experiment.getNet("NounInstanceNet");
    CABot2Net verbAccessNet = (CABot2Net)experiment.getNet("VerbAccessNet");
    CABot2Net verbSemNet = (CABot2Net)experiment.getNet("VerbSemNet");
    CABot2Net verbInstanceNet =(CABot2Net)experiment.getNet("VerbInstanceNet");
    CABot2Net otherNet = (CABot2Net)experiment.getNet("OtherNet");
    CABot2Net vPPPNet = (CABot2Net)experiment.getNet("VPPPNet");
    CABot2Net nPPPNet = (CABot2Net)experiment.getNet("NPPPNet");
  
    inputNet.cantFrame.setLocation(0,0);
    inputNet.cantFrame.setSize (500,300);
    inputNet.cantFrame.matrix.addStringsToPrint ("move",20,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("left",40,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("period .",60,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("turn",80,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("toward",100,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("the",120,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("pyramid",140,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("it",160,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("stalactite",180,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("I",200,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("found",220,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("gun",240,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("saw",260,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("girl",280,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("with",300,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("telescope",320,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("door",340,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("handle",360,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("right",380,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("forward",400,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("backward",420,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("wrong",440,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("that",460,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("to",480,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("go",500,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("is",520,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("center",540,200);
    inputNet.cantFrame.show();
	
    barOneNet.cantFrame.setLocation(800,0);
    barOneNet.cantFrame.setSize (200,200);
    barOneNet.cantFrame.matrix.addStringsToPrint ("WordActive",30,200);
    barOneNet.cantFrame.matrix.addStringsToPrint ("Bar One Active",130,200);
    barOneNet.cantFrame.show();

    nounAccessNet.cantFrame.setLocation(0,300);
    nounAccessNet.cantFrame.setSize (300,360);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("left",30,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("pyramid",45,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("it",60,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("stalactite",80,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("I",100,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("gun",120,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("girl",140,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("telescope",160,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("door",180,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("handle",200,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("right",220,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("forward",240,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("backward",260,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("wrong",280,200);
    nounAccessNet.cantFrame.matrix.addStringsToPrint ("that",300,200);
    nounAccessNet.cantFrame.show();

    verbAccessNet.cantFrame.setLocation(0,330);
    verbAccessNet.cantFrame.setSize (300,360);
    verbAccessNet.cantFrame.matrix.addStringsToPrint ("move",30,200);
    verbAccessNet.cantFrame.matrix.addStringsToPrint ("turn",45,200);
    verbAccessNet.cantFrame.matrix.addStringsToPrint ("found",60,200);
    verbAccessNet.cantFrame.matrix.addStringsToPrint ("saw",80,200);
    verbAccessNet.cantFrame.matrix.addStringsToPrint ("go",100,200);
    verbAccessNet.cantFrame.matrix.addStringsToPrint ("is",120,200);
    verbAccessNet.cantFrame.matrix.addStringsToPrint ("center",140,200);
    verbAccessNet.cantFrame.show();

    otherNet.cantFrame.setLocation(0,360);
    otherNet.cantFrame.setSize (300,360);
    otherNet.cantFrame.matrix.addStringsToPrint (". Period",30,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("toward",45,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("the",60,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("with",75,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("to",90,200);
    otherNet.cantFrame.show();

    nounSemNet.cantFrame.setLocation(500,0);
    nounSemNet.cantFrame.setSize (300,670);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("NOUNS",10,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("left",20,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("pyramid",33,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("it",46,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("stalactite",59,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("I",70,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("gun",81,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("girl",92,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("telescope",103,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("door",114,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("handle",125,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("hello",600,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("lawyer",1200,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("FEATURES",1460,200);
    nounSemNet.cantFrame.matrix.addStringsToPrint ("ADJECTIVES",1710,200);
    nounSemNet.cantFrame.show();

    verbSemNet.cantFrame.setLocation(500,30);
    verbSemNet.cantFrame.setSize (300,670);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("VERBS",10,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("move",20,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("turn",33,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("found",46,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("saw",59,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("center",85,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("10",560,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("FEATURES",750,200);
    verbSemNet.cantFrame.show();

    ruleNet.cantFrame.setLocation(500,0);
    ruleNet.cantFrame.setSize (300,600);
    ruleNet.cantFrame.matrix.addStringsToPrint ("New Noun Instance",30,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("NP from N",50,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("NP Done",70,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("Read Next Word",85,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("New Verb Instance",105,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("Main Verb",125,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("VP Done",140,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("S -> VP Period",180,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("NP adds Prep",200,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("Prep Done",220,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("NP adds det",240,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("VP->VPPPloc",260,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("1,2",280,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("1,3",300,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("VP -> VPNPObj",320,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("1,2",340,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("1,3",360,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("VP -> NPActVP",380,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("1,2",400,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("1,3",420,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("NP -> NPPP 1 2",440,200);
    ruleNet.cantFrame.matrix.addStringsToPrint ("2,3",460,200);
    ruleNet.cantFrame.show();

    nounInstanceNet.cantFrame.setLocation(800,200);
    nounInstanceNet.cantFrame.setSize (200,500);
    nounInstanceNet.cantFrame.matrix.addStringsToPrint ("Instance 0",30,140);
    nounInstanceNet.cantFrame.matrix.addStringsToPrint ("Instance 1",175,140);
    nounInstanceNet.cantFrame.matrix.addStringsToPrint ("Instance 2",330,140);
    nounInstanceNet.cantFrame.show();

    verbInstanceNet.cantFrame.setLocation(800,230);
    verbInstanceNet.cantFrame.setSize (200,300);
    verbInstanceNet.cantFrame.show();

    nPPPNet.cantFrame.setLocation(300,300);
    nPPPNet.cantFrame.setSize (200,300);
    nPPPNet.cantFrame.matrix.addStringsToPrint ("x",30,140);
    nPPPNet.cantFrame.matrix.addStringsToPrint ("MDH",120,140);
    nPPPNet.cantFrame.show();

    vPPPNet.cantFrame.setLocation(300,320);
    vPPPNet.cantFrame.setSize (200,300);
    vPPPNet.cantFrame.matrix.addStringsToPrint ("MIS",30,140);
    vPPPNet.cantFrame.matrix.addStringsToPrint ("SGT",120,140);
    vPPPNet.cantFrame.show();
  }

  private  static void positionVisionWindows() {
    CABot2Net retinaNet = (CABot2Net)experiment.getNet("RetinaNet");
    CABot2Net V1Net = (CABot2Net)experiment.getNet("V1Net");
    CABot2Net V2Net = (CABot2Net)experiment.getNet("V2Net");
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
    V2Net.cantFrame.setLocation(400,200);
    V2Net.cantFrame.setSize (650,350);
    V2Net.cantFrame.matrix.addStringsToPrint("SPyramid",hTextStart,vTextStart);
    V2Net.cantFrame.matrix.addStringsToPrint ("SStalagtite",hTextStart*2);
    V2Net.cantFrame.matrix.addStringsToPrint ("MPyramid",hTextStart*3);
    V2Net.cantFrame.matrix.addStringsToPrint ("MStalagtite",hTextStart*4);
    V2Net.cantFrame.matrix.addStringsToPrint ("LPyramid",hTextStart*5);
    V2Net.cantFrame.matrix.addStringsToPrint ("LStalagtite",hTextStart*6);
    V2Net.cantFrame.show();
  }

  private  static void positionWindows() {
    CABot2Net visionInputNet = (CABot2Net)experiment.getNet("VisualInputNet");
    CABot2Net controlNet = (CABot2Net)experiment.getNet("ControlNet");
    CABot2Net goalSetNet = (CABot2Net)experiment.getNet("GoalSetNet");
    CABot2Net goal1Net = (CABot2Net)experiment.getNet("Goal1Net");
    CABot2Net factNet = (CABot2Net)experiment.getNet("FactNet");
    CABot2Net moduleNet = (CABot2Net)experiment.getNet("ModuleNet");
    CABot2Net actionNet = (CABot2Net)experiment.getNet("ActionNet");
    CABot2Net goal2Net = (CABot2Net)experiment.getNet("Goal2Net");
    CABot2Net exploreNet = (CABot2Net)experiment.getNet("ExploreNet");
    CABot2Net valueNet = (CABot2Net)experiment.getNet("ValueNet");
    CABot2Net module2Net = (CABot2Net)experiment.getNet("Module2Net");
    CABot2Net value2Net = (CABot2Net)experiment.getNet("Value2Net");

    controlNet.cantFrame.matrix.addStringsToPrint ("parse",20,150);
    controlNet.cantFrame.matrix.addStringsToPrint ("clear parse",32,150);
    controlNet.cantFrame.matrix.addStringsToPrint ("goal set",44,150);
    controlNet.cantFrame.matrix.addStringsToPrint ("clear after goal",56,150);
    controlNet.cantFrame.matrix.addStringsToPrint ("symbolic reset",68,150);

    //add for goal1, factNet, moduleNet and actionNet
    goal1Net.cantFrame.matrix.addStringsToPrint ("turn+left",20,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("turn+right",32,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("move+forward",44,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("move+backward",56,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("move+left",68,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("move+right",80,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("turn",92,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("go",104,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("pyramid",116,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("stalactite",128,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("center",140,150);

    factNet.cantFrame.matrix.addStringsToPrint ("turn+left1",20,150);
    factNet.cantFrame.matrix.addStringsToPrint ("turn+left2",32,150);
    factNet.cantFrame.matrix.addStringsToPrint ("turn+right1",44,150);
    factNet.cantFrame.matrix.addStringsToPrint ("turn+right2",56,150);
    factNet.cantFrame.matrix.addStringsToPrint ("target pyramid",68,150);
    factNet.cantFrame.matrix.addStringsToPrint ("target stal",80,150);
    factNet.cantFrame.matrix.addStringsToPrint ("target on left",92,150);
    factNet.cantFrame.matrix.addStringsToPrint ("target on right",104,150);
    factNet.cantFrame.matrix.addStringsToPrint ("target in center",116,150);
    factNet.cantFrame.matrix.addStringsToPrint ("target big",128,150);
    factNet.cantFrame.matrix.addStringsToPrint ("target absent",140,150);
    factNet.cantFrame.matrix.addStringsToPrint ("center on left",152,150);
    factNet.cantFrame.matrix.addStringsToPrint ("center on right",164,150);
    factNet.cantFrame.matrix.addStringsToPrint ("center in center",176,150);
    
    moduleNet.cantFrame.matrix.addStringsToPrint ("turnleft",20,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("turnright",32,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("moveforward",44,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("movebackward",56,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("moveNoTarget",68,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("turnCenter",80,150);
    
    actionNet.cantFrame.matrix.addStringsToPrint ("turnleft",20,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("turnright",32,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("forward",44,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("backward",56,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("turn toward nil",68,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("turn centre",80,140);
    
    goal2Net.cantFrame.matrix.addStringsToPrint ("left",20,140);
    goal2Net.cantFrame.matrix.addStringsToPrint ("right",80,140);

    positionParseWindows();
    positionVisionWindows();

    visionInputNet.cantFrame.setLocation(600,0);
    visionInputNet.cantFrame.setSize (400,450);
    visionInputNet.cantFrame.show();
    controlNet.cantFrame.setLocation(0,400);
    controlNet.cantFrame.setSize (300,300);
    controlNet.cantFrame.show();

    goalSetNet.cantFrame.setLocation(800,0);
    goalSetNet.cantFrame.setSize (250,300);
    //goalSetNet.cantFrame.show();

    goal1Net.cantFrame.setLocation(0,300);
    goal1Net.cantFrame.setSize (300,400);
    goal1Net.cantFrame.show();

    factNet.cantFrame.setLocation(300,450);
    factNet.cantFrame.setSize (300,300);
    factNet.cantFrame.show();

    moduleNet.cantFrame.setLocation(600,500);
    moduleNet.cantFrame.setSize (300,250);
    moduleNet.cantFrame.show();

    actionNet.cantFrame.setLocation(800,500);
    actionNet.cantFrame.setSize (300,250);

    goal2Net.cantFrame.setLocation(500,0);
    goal2Net.cantFrame.setSize (200,250);
    goal2Net.cantFrame.show();
    actionNet.cantFrame.show();

    exploreNet.cantFrame.setLocation(300,250);
    exploreNet.cantFrame.setSize (200,250);
    exploreNet.cantFrame.show();

    valueNet.cantFrame.setLocation(500,250);
    valueNet.cantFrame.setSize (200,250);
    valueNet.cantFrame.show();

    module2Net.cantFrame.setLocation(700,250);
    module2Net.cantFrame.setSize (200,250);
    module2Net.cantFrame.show();

    value2Net.cantFrame.setLocation(900,0);
    value2Net.cantFrame.setSize (200,750);
    value2Net.cantFrame.show();
  }
  
  //embedded Thread class
  public static class WorkerThread extends CANT23.WorkerThread{
    public void run(){
      System.out.println("CABot2 Thread ");
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
