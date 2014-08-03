
import java.util.*;
import java.awt.*;

public class CABot3 extends CANT23{
  public static String ExperimentXMLFile = "cabot3/cabot3.xml";
  public static String wordFile = "words.def";
  public static CABot3Net nullNet;
  public static CABot3Experiment experiment;
  public static BmpReader bmpReader = new BmpReader();
  public static String inputPath;
 
  // Kailash
  public static CogMap cog = new CogMap();

  public static void main(String args[]){
    //Huyck path
    String path = "c:/progs/cant/CANT23/cabot3/data/";
    //ELB Path 
    //String path = "c:/EclipseWorkspace/CABot3/cabot3/data/";
    inputPath = path;
	  
    seed = 25;
    initRandom();
    makeNewSystem(path);
    positionWindows();
    delayBetweenSteps=0;
   
    bmpReader.setFilePath (path);
  }
  
  public static void setNetPatternsOn(boolean on, String netName, int numOn) {
    CABot3Net  net = (CABot3Net)experiment.getNet(netName);
    if (on) {
      net.setNeuronsToStimulate(numOn);
      net.setCurrentPattern(0);
    }
    else
      net.setNeuronsToStimulate(0);
  }
  
  private static void connectParseNets() {
    CABot3Net  inputNet = (CABot3Net)experiment.getNet("BaseNet");
    CABot3Net  barOneNet = (CABot3Net)experiment.getNet("BarOneNet");
    CABot3Net  nounAccessNet = (CABot3Net)experiment.getNet("NounAccessNet");
    CABot3Net  nounSemNet = (CABot3Net)experiment.getNet("NounSemNet");
    CABot3Net  ruleOneNet = (CABot3Net)experiment.getNet("RuleOneNet");
    CABot3Net  ruleTwoNet = (CABot3Net)experiment.getNet("RuleTwoNet");
    CABot3Net  nounInstanceNet = 
      (CABot3Net)experiment.getNet("NounInstanceNet");
    CABot3Net  verbAccessNet = (CABot3Net)experiment.getNet("VerbAccessNet");
    CABot3Net  verbSemNet = (CABot3Net)experiment.getNet("VerbSemNet");
    CABot3Net  verbInstanceNet = 
      (CABot3Net)experiment.getNet("VerbInstanceNet");
    CABot3Net  otherNet = (CABot3Net)experiment.getNet("OtherNet");
    CABot3Net  nPPPNet = (CABot3Net)experiment.getNet("NPPPNet");
    CABot3Net  vPPPNet = (CABot3Net)experiment.getNet("VPPPNet");
    CABot3Net  instanceCounterNet = 
      (CABot3Net)experiment.getNet("InstanceCounterNet");
    CABot3Net  nextWordNet = 
      (CABot3Net)experiment.getNet("NextWordNet");

    inputNet.connectInputToNounAccess(nounAccessNet);
    inputNet.connectInputToVerbAccess(verbAccessNet);
    inputNet.connectInputToOther(otherNet);
    barOneNet.connectBarOneToAccess(nounAccessNet);
    barOneNet.connectBarOneToAccess(verbAccessNet);
    barOneNet.connectBarOneToAccess(otherNet);
    barOneNet.connectBarOneToRuleOne(ruleOneNet);
    barOneNet.connectBarOneToRuleTwo(ruleTwoNet);
    nounAccessNet.connectNounAccessToSem(nounSemNet);
    nounAccessNet.connectNounAccessToRule(ruleOneNet);
    ruleOneNet.connectRuleOneToBarOne(barOneNet);
    ruleOneNet.connectRuleOneToNounInstance(nounInstanceNet);
    ruleOneNet.connectRuleToNounAccess(nounAccessNet);
    ruleOneNet.connectRuleOneToVerbInstance(verbInstanceNet);
    ruleOneNet.connectRuleToVerbAccess(verbAccessNet);
    ruleOneNet.connectRuleOneToOther(otherNet);
    ruleOneNet.connectRuleOneToInstanceCounter(instanceCounterNet);
    ruleOneNet.connectRuleOneToNextWord(nextWordNet);
    instanceCounterNet.connectICounterToVerbInstance(verbInstanceNet);
    instanceCounterNet.connectICounterToNounInstance(nounInstanceNet);
    ruleTwoNet.connectRuleTwoToNounInstance(nounInstanceNet);
    ruleTwoNet.connectRuleTwoToVerbInstance(verbInstanceNet);
    ruleTwoNet.connectRuleTwoToNextWord(nextWordNet);
    nextWordNet.connectNextWordToRuleOne(ruleOneNet);
    nounInstanceNet.connectNounInstanceToNounAccess(nounAccessNet);
    nounInstanceNet.connectNounInstanceToRuleOne(ruleOneNet);
    nounInstanceNet.connectNounInstanceToRuleTwo(ruleTwoNet);
    nounInstanceNet.connectNounInstanceToOther(otherNet);
    verbAccessNet.connectVerbAccessToSem(verbSemNet);
    verbAccessNet.connectVerbAccessToRule(ruleOneNet);
    verbInstanceNet.connectVerbInstanceToVerbAccess(verbAccessNet);
    verbInstanceNet.connectVerbInstanceToRuleTwo(ruleTwoNet);
    verbInstanceNet.connectVerbInstanceToNounInstance(nounInstanceNet);
    otherNet.connectOtherToRuleOne(ruleOneNet);
    otherNet.connectOtherToRuleTwo(ruleTwoNet);
    verbSemNet.connectVerbSemToVPPP(vPPPNet);
    nounSemNet.connectNounSemToVPPP(vPPPNet);
    verbSemNet.connectVerbSemToNPPP(nPPPNet);
    nounSemNet.connectNounSemToNPPP(nPPPNet);
    vPPPNet.connectVPPPToRuleTwo(ruleTwoNet);
    nPPPNet.connectNPPPToRuleTwo(ruleTwoNet);
  }

  private static void connectVisionNets() {
    CABot3Net  visualInputNet = (CABot3Net)experiment.getNet("VisualInputNet");
    CABot3Net  retinaNet = (CABot3Net)experiment.getNet("RetinaNet");
    CABot3Net  V1Net = (CABot3Net)experiment.getNet("V1Net");
    CABot3Net  objRecNet = (CABot3Net)experiment.getNet("ObjRecNet");
    CABot3Net  v1LinesNet = (CABot3Net)experiment.getNet("V1LinesNet");
    CABot3Net  v1v2GratingsNet = (CABot3Net)experiment.getNet("GratingsNet");
    visualInputNet.connectInputToRetina(retinaNet);
    retinaNet.connectRetinaToV1(V1Net);
    retinaNet.connectRetinaToUnknownObject(objRecNet);
    v1v2GratingsNet.connectGratingToV1(V1Net);
    V1Net.connectV1ToObjRec(objRecNet); 
    //DoorDetection objectless class
    DoorDetection.connectV1FeatureToV2Jamb(V1Net,objRecNet);
    DoorDetection.connectGratingsToV2Jamb(v1v2GratingsNet, objRecNet);
    DoorDetection.connecJambtoDoor(objRecNet);
    retinaNet.connectRetinaToV1Lines(v1LinesNet);
    visualInputNet.connectVisInputToV1Slashes(v1LinesNet);
    v1v2GratingsNet.connectV1LinestoGratings(v1LinesNet);
    v1v2GratingsNet.connectGratingToObjRec(objRecNet);
  }

  private static void connectAllNets() {
    CABot3Net controlNet = (CABot3Net)experiment.getNet("ControlNet");
    CABot3Net actionNet = (CABot3Net)experiment.getNet("ActionNet");
    CABot3Net verbSemNet = (CABot3Net)experiment.getNet("VerbSemNet");
    CABot3Net nounSemNet = (CABot3Net)experiment.getNet("NounSemNet");
    CABot3Net goalSetNet = (CABot3Net)experiment.getNet("GoalSetNet");
    CABot3Net goal1Net = (CABot3Net)experiment.getNet("Goal1Net");
    CABot3Net factNet = (CABot3Net)experiment.getNet("FactNet");
    CABot3Net moduleNet = (CABot3Net)experiment.getNet("ModuleNet");
    CABot3Net objRecNet = (CABot3Net)experiment.getNet("ObjRecNet");
    CABot3Net goal2Net = (CABot3Net)experiment.getNet("Goal2Net");
    CABot3Net exploreNet = (CABot3Net)experiment.getNet("ExploreNet");
    CABot3Net valueNet = (CABot3Net)experiment.getNet("ValueNet");
    CABot3Net module2Net = (CABot3Net)experiment.getNet("Module2Net");
    CABot3Net value2Net = (CABot3Net)experiment.getNet("Value2Net");
    CABot3Net ruleOneNet = (CABot3Net)experiment.getNet("RuleOneNet");
    CABot3Net ruleTwoNet = (CABot3Net)experiment.getNet("RuleTwoNet");
    CABot3Net barOneNet = (CABot3Net)experiment.getNet("BarOneNet");
    CABot3Net nounAccessNet = (CABot3Net)experiment.getNet("NounAccessNet");
    CABot3Net verbAccessNet = (CABot3Net)experiment.getNet("VerbAccessNet");
    CABot3Net nounInstanceNet= (CABot3Net)experiment.getNet("NounInstanceNet");
    CABot3Net verbInstanceNet= (CABot3Net)experiment.getNet("VerbInstanceNet");
    CABot3Net otherNet= (CABot3Net)experiment.getNet("OtherNet");
    CABot3Net roomNet = (CABot3Net)experiment.getNet("RoomNet");
    CABot3Net room2Net = (CABot3Net)experiment.getNet("Room2Net");
    CABot3Net counterNet = (CABot3Net)experiment.getNet("CounterNet");
    CABot3Net cogSeqNet = (CABot3Net)experiment.getNet("CogSeqNet");
    CABot3Net gratingsNet = (CABot3Net)experiment.getNet("GratingsNet");
    CABot3Net instanceCounterNet = (CABot3Net)experiment.
      getNet("InstanceCounterNet");
    CABot3Net nextWordNet = (CABot3Net)experiment.getNet("NextWordNet");
	
    connectParseNets(); 

    connectVisionNets();

    //integrate parse3 with goal setting
    verbSemNet.connectVerbSemToGoalSet(goalSetNet);
    nounSemNet.connectNounSemToGoalSet(goalSetNet);
    otherNet.connectOtherToGoalSet(goalSetNet);
    goalSetNet.connectGoalSetToGoal1(goal1Net);

    //connect visual items to facts
    objRecNet.connectObjRecToFact(factNet);
    factNet.connectFactToObjRec(objRecNet);
    gratingsNet.connectGratingToFact(factNet);

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
    ruleTwoNet.connectRuleTwoToControl(controlNet);
    controlNet.connectControlToBarOne(barOneNet);
    controlNet.connectControlToRuleOne(ruleOneNet);
    controlNet.connectControlToRuleTwo(ruleTwoNet);
    controlNet.connectControlToOther(otherNet);
    controlNet.connectControlToNounAccess(nounAccessNet);
    controlNet.connectControlToVerbAccess(verbAccessNet);
    controlNet.connectControlToNounSem(nounSemNet);
    controlNet.connectControlToVerbSem(verbSemNet);
    controlNet.connectControlToNounInstance(nounInstanceNet);
    controlNet.connectControlToVerbInstance(verbInstanceNet);
    controlNet.connectControlToInstanceCounter(instanceCounterNet);
    controlNet.connectControlToNextWord(nextWordNet);

    //hook control up to goal setting.
    controlNet.connectControlToGoalSet(goalSetNet);
    controlNet.connectControlToValue2(value2Net);
    value2Net.connectValue2ToControl(controlNet);

    //connect planning to cognitive mapping
    factNet.connectFactToRoomFact(roomNet);
    factNet.connectFactToRoom2(room2Net);
    counterNet.connectCounterToCogSeq (cogSeqNet); 
    factNet.connectFactToCogSeq(cogSeqNet);
    factNet.connectFactToCounter(counterNet);
    roomNet.connectRoomToFact(factNet);
    counterNet.connectCounterToFact (factNet); 
    goal1Net.connectGoal1ToRoom1(roomNet);
    room2Net.connectRoom2ToFact(factNet);
  }

  private static void readNets() {
  }

  protected static void makeNewSystem(String path) {
    nullNet = new CABot3Net();
	
    System.out.println("Make CABot3 Nets");
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);

    workerThread = new CABot3.WorkerThread();
    initializeExperiment(path);
    experiment.printExpName();
    CABot3Net inputNet = (CABot3Net) experiment.getNet("BaseNet");
    workerThread.start();	
    inputNet.setInputPatterns();
    connectAllNets();	

    // Kailash
    cog.init();
    cog.setConnections();
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment(String path) {
    experiment = new CABot3Experiment(path);
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

    cog.cogRun(CANTStep);     // Kailash

    Enumeration eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net)eNum.nextElement();
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
    CABot3Net instanceNet = (CABot3Net)experiment.getNet("InstanceNet");
    CABot3Net verbNet = (CABot3Net)experiment.getNet("VerbNet");
    CABot3Net nounNet = (CABot3Net)experiment.getNet("NounNet");
    CABot3Net oWordNet = (CABot3Net)experiment.getNet("OWordNet");
    CABot3Net stackTopNet = (CABot3Net)experiment.getNet("StackTopNet");
    CABot3Net stackNet = (CABot3Net)experiment.getNet("StackNet");
    CABot3Net pushNet = (CABot3Net)experiment.getNet("PushNet");
    CABot3Net popNet = (CABot3Net)experiment.getNet("PopNet");
    CABot3Net ruleNet = (CABot3Net)experiment.getNet("RuleOneNet");
    CABot3Net eraseNet = (CABot3Net)experiment.getNet("EraseNet");
    CABot3Net eraseBoundNet = (CABot3Net)experiment.getNet("EraseBoundNet");
    CABot3Net testNet = (CABot3Net)experiment.getNet("TestNet");

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
    CABot3Net inputNet = (CABot3Net)experiment.getNet("BaseNet");
    CABot3Net barOneNet = (CABot3Net)experiment.getNet("BarOneNet");
    CABot3Net nounAccessNet = (CABot3Net)experiment.getNet("NounAccessNet");
    CABot3Net nounSemNet = (CABot3Net)experiment.getNet("NounSemNet");
    CABot3Net ruleOneNet = (CABot3Net)experiment.getNet("RuleOneNet");
    CABot3Net ruleTwoNet = (CABot3Net)experiment.getNet("RuleTwoNet");
    CABot3Net nounInstanceNet =(CABot3Net)experiment.getNet("NounInstanceNet");
    CABot3Net verbAccessNet = (CABot3Net)experiment.getNet("VerbAccessNet");
    CABot3Net verbSemNet = (CABot3Net)experiment.getNet("VerbSemNet");
    CABot3Net verbInstanceNet =(CABot3Net)experiment.getNet("VerbInstanceNet");
    CABot3Net iCounterNet =(CABot3Net)experiment.getNet("InstanceCounterNet");
    CABot3Net otherNet = (CABot3Net)experiment.getNet("OtherNet");
    CABot3Net vPPPNet = (CABot3Net)experiment.getNet("VPPPNet");
    CABot3Net nPPPNet = (CABot3Net)experiment.getNet("NPPPNet");
  
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
    inputNet.cantFrame.matrix.addStringsToPrint ("dangerous",560,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("explore",620,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("stop",640,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("striped",660,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("barred",680,200);
    inputNet.cantFrame.matrix.addStringsToPrint ("before",700,200);
    inputNet.cantFrame.show();
	
    barOneNet.cantFrame.setLocation(800,0);
    barOneNet.cantFrame.setSize (200,200);
    barOneNet.cantFrame.matrix.addStringsToPrint ("WordActive",30,140);
    barOneNet.cantFrame.matrix.addStringsToPrint ("Bar One Active",60,140);
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
    verbAccessNet.cantFrame.matrix.addStringsToPrint ("explore",160,200);
    verbAccessNet.cantFrame.matrix.addStringsToPrint ("stop",180,200);
    verbAccessNet.cantFrame.show();

    otherNet.cantFrame.setLocation(0,360);
    otherNet.cantFrame.setSize (300,360);
    otherNet.cantFrame.matrix.addStringsToPrint (". Period",20,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("toward",40,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("the",60,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("with",80,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("to",100,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("dangerous",120,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("striped",140,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("barred",160,200);
    otherNet.cantFrame.matrix.addStringsToPrint ("before",180,200);
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
    verbSemNet.cantFrame.matrix.addStringsToPrint ("found",45,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("saw",57,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("go",69,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("is",81,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("center",93,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("explore",105,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("stop",117,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("10",560,200);
    verbSemNet.cantFrame.matrix.addStringsToPrint ("FEATURES",750,200);
    verbSemNet.cantFrame.show();

    ruleOneNet.cantFrame.setLocation(500,0);
    ruleOneNet.cantFrame.setSize (300,350);
    ruleOneNet.cantFrame.matrix.addStringsToPrint ("New Noun Instance",30,200);
    ruleOneNet.cantFrame.matrix.addStringsToPrint ("NP from N",50,200);
    ruleOneNet.cantFrame.matrix.addStringsToPrint ("NP Done",70,200);
    ruleOneNet.cantFrame.matrix.addStringsToPrint ("Read Next Word",85,200);
    ruleOneNet.cantFrame.matrix.addStringsToPrint ("New Verb Inst",105,200);
    ruleOneNet.cantFrame.matrix.addStringsToPrint ("Main Verb",125,200);
    ruleOneNet.cantFrame.matrix.addStringsToPrint ("VP Done",140,200);
    ruleOneNet.cantFrame.matrix.addStringsToPrint ("NP adds Adj",180,200);
    ruleOneNet.cantFrame.matrix.addStringsToPrint ("NP adds Prep",200,200);
    ruleOneNet.cantFrame.matrix.addStringsToPrint ("Prep Done",220,200);
    ruleOneNet.cantFrame.matrix.addStringsToPrint ("NP adds det",240,200);
    //ruleOneNet.cantFrame.show();

    ruleTwoNet.cantFrame.setLocation(500,350);
    ruleTwoNet.cantFrame.setSize (300,400);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("S -> VP Period",30,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("VP -> VPNPObj",85,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("1,2",105,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("1,3NI",125,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("VP -> NPActVP",145,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("1,2NI",165,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("1,3NI",180,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("VP->VPPPloc",200,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("1,2",220,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("1,3",240,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("NP -> NPPP 1 2",260,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("2,3",280,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("VP->VPPPinst",320,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("1,2",340,200);
    ruleTwoNet.cantFrame.matrix.addStringsToPrint ("1,3",360,200);
    //ruleTwoNet.cantFrame.show();

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
    
    iCounterNet.cantFrame.setLocation(300,320);
    iCounterNet.cantFrame.setSize (200,300);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Noun 1 0",30,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Noun 1 1",60,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Noun 1 2",90,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Noun 1 3",120,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Noun 2 0",150,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Noun 3 0",270,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Noun 4 0",390,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Noun 5 0",510,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Verb 1 0",630,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Verb 1 1",670,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Verb 2 0",700,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Verb 3 0",760,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Verb 4 0",820,140);
    iCounterNet.cantFrame.matrix.addStringsToPrint ("Verb 5 0",880,140);
    iCounterNet.cantFrame.show();

    ruleOneNet.cantFrame.show();
    ruleTwoNet.cantFrame.show();
  }
  
  //ELB - added v1Lines and Gratings net positions
  private  static void positionVisionWindows() {
    CABot3Net retinaNet = (CABot3Net)experiment.getNet("RetinaNet");
    CABot3Net V1Net = (CABot3Net)experiment.getNet("V1Net");
    CABot3Net objRecNet = (CABot3Net)experiment.getNet("ObjRecNet");
    CABot3Net v1LinesNet = (CABot3Net)experiment.getNet("V1LinesNet");
    CABot3Net v1v2GratingsNet = (CABot3Net)experiment.getNet("GratingsNet");
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
    V1Net.cantFrame.setLocation(0,200);
    V1Net.cantFrame.setSize (400,500);
    V1Net.cantFrame.matrix.addStringsToPrint ("HEdgeD",hTextStart,vTextStart);
    V1Net.cantFrame.matrix.addStringsToPrint ("SEdgeU",hTextStart*2);
    V1Net.cantFrame.matrix.addStringsToPrint ("BEdgeU",hTextStart*3);
    V1Net.cantFrame.matrix.addStringsToPrint ("And Angle",hTextStart*4);
    V1Net.cantFrame.matrix.addStringsToPrint ("Less Than Angle",hTextStart*5);
    V1Net.cantFrame.matrix.addStringsToPrint ("Greater Than Angle",
       hTextStart*6);
    V1Net.cantFrame.matrix.addStringsToPrint ("Or Angle",hTextStart*7);
    V1Net.cantFrame.matrix.addStringsToPrint ("HEdgeU",hTextStart*8);
    V1Net.cantFrame.matrix.addStringsToPrint ("SEdgeD",hTextStart*9);
    V1Net.cantFrame.matrix.addStringsToPrint ("BEdgeD",hTextStart*10);
    V1Net.cantFrame.matrix.addStringsToPrint ("VEdgeL",hTextStart*11);
    V1Net.cantFrame.matrix.addStringsToPrint ("VEdgeR",hTextStart*12);
    objRecNet.cantFrame.setLocation(400,200);
    objRecNet.cantFrame.setSize (400,500);
    objRecNet.cantFrame.matrix.addStringsToPrint("Pyramid",hTextStart,
       vTextStart);
    objRecNet.cantFrame.matrix.addStringsToPrint ("Stalactite",hTextStart*2);
    objRecNet.cantFrame.matrix.addStringsToPrint ("Jamb",hTextStart*3);
    objRecNet.cantFrame.matrix.addStringsToPrint ("Door",hTextStart*4);
    objRecNet.cantFrame.setVisible(true);
    
    v1LinesNet.cantFrame.setLocation(0,200);
    v1LinesNet.cantFrame.setSize (470,500);
    v1LinesNet.cantFrame.matrix.addStringsToPrint ("3x3 HLines",
      (int)(0.5*vTextStart), hTextStart);
    v1LinesNet.cantFrame.matrix.addStringsToPrint ("<------",(vTextStart),hTextStart);
    v1LinesNet.cantFrame.matrix.addStringsToPrint ("3x3 VLines",(int)(1.5*vTextStart),hTextStart);
	v1LinesNet.cantFrame.matrix.addStringsToPrint ("<------",(2*vTextStart),hTextStart);
	v1LinesNet.cantFrame.matrix.addStringsToPrint ("3x3 fSlash",(int)(2.5*vTextStart), hTextStart);
	v1LinesNet.cantFrame.matrix.addStringsToPrint ("<------",(3*vTextStart),hTextStart);
	v1LinesNet.cantFrame.matrix.addStringsToPrint ("3x3 bSlash",(int)(3.5*vTextStart),hTextStart);
	v1LinesNet.cantFrame.matrix.addStringsToPrint ("<------",(4*vTextStart),hTextStart);
	v1LinesNet.cantFrame.matrix.addStringsToPrint ("6x6 HLines",(int)(4.5*vTextStart), hTextStart);
	v1LinesNet.cantFrame.matrix.addStringsToPrint ("<------",(5*vTextStart),hTextStart);
	v1LinesNet.cantFrame.matrix.addStringsToPrint ("6x6 VLines",(int)(5.5*vTextStart),hTextStart);
  v1LinesNet.cantFrame.setVisible(true);
	
    v1v2GratingsNet.cantFrame.setLocation(0,200);
    v1v2GratingsNet.cantFrame.setSize (470,500);
    v1v2GratingsNet.cantFrame.matrix.addStringsToPrint ("HBars3",hTextStart, 
      vTextStart);
    v1v2GratingsNet.cantFrame.matrix.addStringsToPrint ("VBars3",hTextStart*2);
    v1v2GratingsNet.cantFrame.matrix.addStringsToPrint ("HBars6",hTextStart*3);
    v1v2GratingsNet.cantFrame.matrix.addStringsToPrint ("VBars6",hTextStart*4);
    v1v2GratingsNet.cantFrame.matrix.addStringsToPrint ("SBars3",hTextStart*5);
    v1v2GratingsNet.cantFrame.matrix.addStringsToPrint ("BBars3",hTextStart*6);
    v1v2GratingsNet.cantFrame.matrix.addStringsToPrint ("SBars5",hTextStart*7);
    v1v2GratingsNet.cantFrame.matrix.addStringsToPrint ("BBars6",hTextStart*8);
  }

  private  static void positionWindows() {
    CABot3Net visionInputNet = (CABot3Net)experiment.getNet("VisualInputNet");
    CABot3Net controlNet = (CABot3Net)experiment.getNet("ControlNet");
    CABot3Net goalSetNet = (CABot3Net)experiment.getNet("GoalSetNet");
    CABot3Net goal1Net = (CABot3Net)experiment.getNet("Goal1Net");
    CABot3Net factNet = (CABot3Net)experiment.getNet("FactNet");
    CABot3Net moduleNet = (CABot3Net)experiment.getNet("ModuleNet");
    CABot3Net actionNet = (CABot3Net)experiment.getNet("ActionNet");
    CABot3Net goal2Net = (CABot3Net)experiment.getNet("Goal2Net");
    CABot3Net exploreNet = (CABot3Net)experiment.getNet("ExploreNet");
    CABot3Net valueNet = (CABot3Net)experiment.getNet("ValueNet");
    CABot3Net module2Net = (CABot3Net)experiment.getNet("Module2Net");
    CABot3Net value2Net = (CABot3Net)experiment.getNet("Value2Net");

    controlNet.cantFrame.matrix.addStringsToPrint ("parse",20,150);
    controlNet.cantFrame.matrix.addStringsToPrint ("clear parse",32,150);
    controlNet.cantFrame.matrix.addStringsToPrint ("goal set",44,150);
    controlNet.cantFrame.matrix.addStringsToPrint ("clear after goal",56,150);
    controlNet.cantFrame.matrix.addStringsToPrint ("symbolic reset/inc instances",68,150);

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
    goal1Net.cantFrame.matrix.addStringsToPrint ("explore",152,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("stop",164,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("id room",176,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("find (door)",188,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("to corridor",200,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("through corridor",212,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("move before striped pyramid",
                                                   224,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("mb barred pyramid",236,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("mb barred stal",248,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("mb striped stal",260,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("get target room",272,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("find target room",284,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("target found",296,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("front jam",308,150);
    goal1Net.cantFrame.matrix.addStringsToPrint ("move after through",320,150);

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
    factNet.cantFrame.matrix.addStringsToPrint ("pyramid",188,150);
    factNet.cantFrame.matrix.addStringsToPrint ("stalactite",200,150);
    factNet.cantFrame.matrix.addStringsToPrint ("V-Stripes",212,150);
    factNet.cantFrame.matrix.addStringsToPrint ("H-Stripes",224,150);
    factNet.cantFrame.matrix.addStringsToPrint ("No Object",236,150);
    factNet.cantFrame.matrix.addStringsToPrint ("Seen Room",248,150);
    factNet.cantFrame.matrix.addStringsToPrint ("Room 1 Off",260,150);
    factNet.cantFrame.matrix.addStringsToPrint ("barredPyr",272,150);
    factNet.cantFrame.matrix.addStringsToPrint ("stripedPyr",284,150);
    factNet.cantFrame.matrix.addStringsToPrint ("barredStal",296,150);
    factNet.cantFrame.matrix.addStringsToPrint ("stripedStal",308,150);
    factNet.cantFrame.matrix.addStringsToPrint ("target door",320,150);
    factNet.cantFrame.matrix.addStringsToPrint ("door absent",332,150);
    factNet.cantFrame.matrix.addStringsToPrint ("door ahead",344,150);
    factNet.cantFrame.matrix.addStringsToPrint ("door seen",356,150);
    factNet.cantFrame.matrix.addStringsToPrint ("next (room)",368,150);
    factNet.cantFrame.matrix.addStringsToPrint ("inc count",380,150);
    factNet.cantFrame.matrix.addStringsToPrint ("Cog Seq Start",392,150);
    factNet.cantFrame.matrix.addStringsToPrint ("Room2 On Start",404,150);
    factNet.cantFrame.matrix.addStringsToPrint ("Explore Done",416,150);
    factNet.cantFrame.matrix.addStringsToPrint ("unknown object",428,150);
    factNet.cantFrame.matrix.addStringsToPrint ("find shape",440,150);
    factNet.cantFrame.matrix.addStringsToPrint ("unk-obj center",452,150);
    factNet.cantFrame.matrix.addStringsToPrint ("no-obj scene",464,150);
    factNet.cantFrame.matrix.addStringsToPrint ("moved toward unkObj",476,150);
    factNet.cantFrame.matrix.addStringsToPrint ("after move",488,150);
    factNet.cantFrame.matrix.addStringsToPrint ("door still seen",500,150);
    factNet.cantFrame.matrix.addStringsToPrint ("through door",512,150);
    factNet.cantFrame.matrix.addStringsToPrint ("Room2 On Done",526,150);
    factNet.cantFrame.matrix.addStringsToPrint ("explore Done stop",538,150);
    factNet.cantFrame.matrix.addStringsToPrint ("Bump",550,150);
    factNet.cantFrame.matrix.addStringsToPrint ("Find Rm w Barred Pyramid",
      562,150);
    factNet.cantFrame.matrix.addStringsToPrint ("Find Rm w SP",574,150);
    factNet.cantFrame.matrix.addStringsToPrint ("Find Rm w BS",586,150);
    factNet.cantFrame.matrix.addStringsToPrint ("Find Rm w SS",598,150);
    factNet.cantFrame.matrix.addStringsToPrint ("ForwardTo&ThrghCorr",610,150);
    factNet.cantFrame.matrix.addStringsToPrint ("BackJamSeen",622,150);
    factNet.cantFrame.matrix.addStringsToPrint ("rtAfterCor",634,150);
    factNet.cantFrame.matrix.addStringsToPrint ("jamLeft",646,150);
    factNet.cantFrame.matrix.addStringsToPrint ("leftBeforeJam",658,150);
    factNet.cantFrame.matrix.addStringsToPrint ("rtFrontJam",670,150);
    factNet.cantFrame.matrix.addStringsToPrint ("backJamLeft",682,150);
    factNet.cantFrame.matrix.addStringsToPrint ("rtAfterCor",694,150);
    factNet.cantFrame.matrix.addStringsToPrint ("rtStart",706,150);
    factNet.cantFrame.matrix.addStringsToPrint ("rtDone",718,150);
    factNet.cantFrame.matrix.addStringsToPrint ("rt2AfterCor",730,150);
    factNet.cantFrame.matrix.addStringsToPrint ("rt2Start",742,150);
    factNet.cantFrame.matrix.addStringsToPrint ("rt2Done",754,150);
    factNet.cantFrame.matrix.addStringsToPrint ("rt3AfterCor",766,150);
    factNet.cantFrame.matrix.addStringsToPrint ("rt3Start",778,150);
    factNet.cantFrame.matrix.addStringsToPrint ("rt3Done",790,150);

    moduleNet.cantFrame.matrix.addStringsToPrint ("turnleft",20,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("turnright",32,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("moveforward",44,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("movebackward",56,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("moveNoTarget",68,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("turnCenter",80,150);
    moduleNet.cantFrame.matrix.addStringsToPrint ("stop",92,150);
    
    actionNet.cantFrame.matrix.addStringsToPrint ("turnleft",20,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("turnright",32,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("forward",44,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("backward",56,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("turn toward nil",68,140);
    actionNet.cantFrame.matrix.addStringsToPrint ("turn centre",80,140);
    
    goal2Net.cantFrame.matrix.addStringsToPrint ("left",20,140);
    goal2Net.cantFrame.matrix.addStringsToPrint ("right",80,140);

    positionVisionWindows();

    visionInputNet.cantFrame.setLocation(600,0);
    visionInputNet.cantFrame.setSize (400,450);
    visionInputNet.cantFrame.show();
    controlNet.cantFrame.setLocation(0,400);
    controlNet.cantFrame.setSize (300,300);
    controlNet.cantFrame.show();

    goalSetNet.cantFrame.setLocation(800,0);
    goalSetNet.cantFrame.setSize (250,300);
    goalSetNet.cantFrame.matrix.addStringsToPrint ("Nouns",20,140);
    goalSetNet.cantFrame.matrix.addStringsToPrint ("Verbs",80,140);
    goalSetNet.cantFrame.matrix.addStringsToPrint ("Adjs",120,140);
    goalSetNet.cantFrame.show();

    goal1Net.cantFrame.setLocation(0,300);
    goal1Net.cantFrame.setSize (300,400);
    //goal1Net.cantFrame.show();

    factNet.cantFrame.setLocation(250,200);
    factNet.cantFrame.setSize (300,500);
    //factNet.cantFrame.show();

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

    cog.makeWindows();     // Kailash
    positionParseWindows();

    factNet.cantFrame.show();
    goal1Net.cantFrame.show();
  }
  
  //embedded Thread class
  public static class WorkerThread extends CANT23.WorkerThread{
    public void run(){
      System.out.println("CABot3 Thread ");
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
