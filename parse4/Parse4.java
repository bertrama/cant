
import java.util.*;
import java.awt.*;

public class Parse4 extends CANT23{
  public static String ExperimentXMLFile = "parse4/parse4.xml";
  public static String wordFile = "parse4/words.def";
  public static Parse4Net nullNet;
  public static Parse4Experiment experiment; 

  public static void main(String args[]){
System.out.println("initialize CANT Parse4");
    initRandom();
    readNewSystem();
    positionWindows();
    delayBetweenSteps=5;
  }

  public static void setBarOneOn(boolean on) {
    Parse4Net  barOneNet = (Parse4Net)experiment.getNet("BarOneNet");
    if (on) {
      barOneNet.setNeuronsToStimulate(50);
      barOneNet.setCurrentPattern(0);
    }
    else
      barOneNet.setNeuronsToStimulate(0);
  }
  public static void setInstanceCounterOn(boolean on) {
    Parse4Net  counterNet = (Parse4Net)experiment.getNet("CounterNet");
    if (on) {
      counterNet.setNeuronsToStimulate(50);
      counterNet.setCurrentPattern(0);
    }
    else
      counterNet.setNeuronsToStimulate(0);
  }
  
  protected static void readNewSystem() {
    nullNet = new Parse4Net();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new Parse4.WorkerThread();
    initializeExperiment();
    experiment.printExpName();
    workerThread.start();
    Parse4Net inputNet = (Parse4Net) experiment.getNet("BaseNet");
    inputNet.setInputPatterns();
    connectAllNets();	
  }
  
  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    experiment = new Parse4Experiment();
    System.out.println("initialize Parse4 Experiment");
    experiment.printExpName();
  }
  
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
      Parse4Net net = (Parse4Net)eNum.nextElement();
    if (net.getName().compareTo("BaseNet") == 0)
      {
      net.runAllOneStep(CANTStep); 
      CANTStep++;
      }
    }
   
  if (experiment.experimentDone(CANTStep)) 
    {
//System.out.println("experiment done"+CANTStep);
        closeSystem();
  	//numSystems++;
  	readNewSystem();
  	//makeNewSystem(numSystems);
    }
  }
  
  private static void connectAllNets() {
    Parse4Net  inputNet = (Parse4Net)experiment.getNet("BaseNet");
    Parse4Net  barOneNet = (Parse4Net)experiment.getNet("BarOneNet");
    Parse4Net  nounAccessNet = (Parse4Net)experiment.getNet("NounAccessNet");
    Parse4Net  nounSemNet = (Parse4Net)experiment.getNet("NounSemNet");
    Parse4Net  ruleOneNet = (Parse4Net)experiment.getNet("RuleOneNet");
    Parse4Net  ruleTwoNet = (Parse4Net)experiment.getNet("RuleTwoNet");
    Parse4Net  nounInstanceNet = 
      (Parse4Net)experiment.getNet("NounInstanceNet");
    Parse4Net  verbAccessNet = (Parse4Net)experiment.getNet("VerbAccessNet");
    Parse4Net  verbSemNet = (Parse4Net)experiment.getNet("VerbSemNet");
    Parse4Net  verbInstanceNet = 
      (Parse4Net)experiment.getNet("VerbInstanceNet");
    Parse4Net  otherNet = (Parse4Net)experiment.getNet("OtherNet");
    Parse4Net  nPPPNet = (Parse4Net)experiment.getNet("NPPPNet");
    Parse4Net  vPPPNet = (Parse4Net)experiment.getNet("VPPPNet");
    Parse4Net  counterNet = (Parse4Net)experiment.getNet("CounterNet");
    Parse4Net  nextWordNet = (Parse4Net)experiment.getNet("NextWordNet");

    inputNet.connectInputToNounAccess(nounAccessNet);
    inputNet.connectInputToVerbAccess(verbAccessNet);
    inputNet.connectInputToOther(otherNet);
    barOneNet.connectBarOneToAccess(nounAccessNet);
    barOneNet.connectBarOneToAccess(verbAccessNet);
    barOneNet.connectBarOneToAccess(otherNet);
    barOneNet.connectBarOneToRuleOne(ruleOneNet);
    barOneNet.connectBarOneToRuleTwo(ruleTwoNet);
    nounAccessNet.connectNounAccessToSem(nounSemNet);
    nounAccessNet.connectNounAccessToRuleOne(ruleOneNet);
    ruleOneNet.connectRuleOneToBarOne(barOneNet);
    ruleOneNet.connectRuleOneToNounInstance(nounInstanceNet);
    ruleOneNet.connectRuleOneToNounAccess(nounAccessNet);
    ruleOneNet.connectRuleOneToVerbInstance(verbInstanceNet);
    ruleOneNet.connectRuleOneToVerbAccess(verbAccessNet);
    ruleOneNet.connectRuleOneToOther(otherNet);
    ruleOneNet.connectRuleOneToCounter(counterNet);
    ruleOneNet.connectRuleOneToNextWord(nextWordNet);
    ruleTwoNet.connectRuleTwoToNounInstance(nounInstanceNet);
    ruleTwoNet.connectRuleTwoToVerbInstance(verbInstanceNet);
    ruleTwoNet.connectRuleTwoToNextWord(nextWordNet);
    nounInstanceNet.connectNounInstanceToNounAccess(nounAccessNet);
    nounInstanceNet.connectNounInstanceToRuleOne(ruleOneNet);
    nounInstanceNet.connectNounInstanceToRuleTwo(ruleTwoNet);
    //nounInstanceNet.connectNounInstanceToOther(otherNet);
    verbAccessNet.connectVerbAccessToSem(verbSemNet);
    verbAccessNet.connectVerbAccessToRuleOne(ruleOneNet);
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
    counterNet.connectCounterToNounInstance(nounInstanceNet);
    counterNet.connectCounterToVerbInstance(verbInstanceNet);
    nextWordNet.connectNextWordToRuleOne(ruleOneNet);
  }
  
  private  static void positionWindows() {
    Parse4Net inputNet = (Parse4Net)experiment.getNet("BaseNet");
    Parse4Net barOneNet = (Parse4Net)experiment.getNet("BarOneNet");
    Parse4Net nounAccessNet = (Parse4Net)experiment.getNet("NounAccessNet");
    Parse4Net nounSemNet = (Parse4Net)experiment.getNet("NounSemNet");
    Parse4Net ruleOneNet = (Parse4Net)experiment.getNet("RuleOneNet");
    Parse4Net ruleTwoNet = (Parse4Net)experiment.getNet("RuleTwoNet");
    Parse4Net nounInstanceNet =(Parse4Net)experiment.getNet("NounInstanceNet");
    Parse4Net verbAccessNet = (Parse4Net)experiment.getNet("VerbAccessNet");
    Parse4Net verbSemNet = (Parse4Net)experiment.getNet("VerbSemNet");
    Parse4Net verbInstanceNet =(Parse4Net)experiment.getNet("VerbInstanceNet");
    Parse4Net otherNet = (Parse4Net)experiment.getNet("OtherNet");
    Parse4Net vPPPNet = (Parse4Net)experiment.getNet("VPPPNet");
    Parse4Net nPPPNet = (Parse4Net)experiment.getNet("NPPPNet");
    Parse4Net counterNet = (Parse4Net)experiment.getNet("CounterNet");
    Parse4Net nextWordNet = (Parse4Net)experiment.getNet("NextWordNet");
  
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
    ruleOneNet.cantFrame.show();

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
    ruleTwoNet.cantFrame.show();

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
    nPPPNet.cantFrame.matrix.addStringsToPrint ("MDH",40,140);
    nPPPNet.cantFrame.show();

    vPPPNet.cantFrame.setLocation(300,320);
    vPPPNet.cantFrame.setSize (200,300);
    vPPPNet.cantFrame.matrix.addStringsToPrint ("MIS",30,140);
    vPPPNet.cantFrame.matrix.addStringsToPrint ("SGT",40,140);
    vPPPNet.cantFrame.show();

    counterNet.cantFrame.setLocation(300,320);
    counterNet.cantFrame.setSize (200,300);
    counterNet.cantFrame.matrix.addStringsToPrint ("Noun Instance 0",30,140);
    counterNet.cantFrame.matrix.addStringsToPrint ("Verb Instance 0",150,140);
    counterNet.cantFrame.show();

    nextWordNet.cantFrame.setLocation(300,320);
    nextWordNet.cantFrame.setSize (200,300);
    nextWordNet.cantFrame.show();
  }
  
  //embedded Thread class
  public static class WorkerThread extends CANT23.WorkerThread{
    public void run(){
      System.out.println("Parse4 Thread ");
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