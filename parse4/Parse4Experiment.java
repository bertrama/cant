//run multiple tests
import java.io.*;
import java.util.*;

public class Parse4Experiment extends CANTExperiment {

  //the stored sentences for testing
  private int sentences [][] = {{1,2,3,-1,-1,-1},  //Move left.
                                {4,5,6,7,3,-1},    //Turn toward the pyramid.
				{1,8,5,6,9,3},    //Move it toward the 
				                     //stalactite.
				{1,8,5,6,7,3},    //Move it toward the 
				                     //pyramid.
				{10,13,6,14,15,6,16,3,-1}, //I saw the girl 
                 				  //with the telescope.
				{10,13,6,29,15,6,16,3,-1}, //I saw the boy 
                 				  //with the telescope.
                                {4,6,16,15,6,7,3,-1},   //Turn the telescope 
                                                        //with the pyramid.
				{1,6,17,15,6,18,3,-1}, //Move the door
				                     //with the handle.
				{1,6,30,15,6,18,3,-1}, //Move the gate with
				                     //the handle.
				{1,5,6,17,15,6,18,3,-1}, //Move toward the door
				                     //with the handle.
				{4,2,3,-1,-1,-1},	//Turn left.
				{4,19,3,-1,-1,-1},	//Turn right.
				{1,20,3,-1,-1,-1},	//Move forward.
				{1,21,3,-1,-1,-1},	//Move backward.
				{1,5,8,3,-1,-1,-1},	//Move toward it. 
				{1,19,3,-1,-1,-1},	//Move right.
                                {4,5,6,9,3,-1},   //Turn toward the stalactite.
                                {25,24,6,7,3,-1}, 	//Go to the pyramid. 
                                {25,24,6,9,3,-1}, 	//Go to the stalactite.
				{23,26,19,3,-1,-1,-1},	//That is right.
				{23,26,22,3,-1,-1,-1},	//That is wrong.
				{27,6,9,3,-1,-1},    //Center the stalactite.
				{10,11,6,12,3, -1}, //I found the gun.
				{27,6,7,3,-1,-1},    //Center the pyramid.
                                {6,7,26,24,6,2,3,-1},//The pyr is to the left.
				{6,14,13,6,7,15,6,9,3,-1}, //The girl saw
                                //the pyramid with the stalactite.
				{6,14,13,6,28,7,15,6,9,3,-1}, //The girl saw
                                //the dangerous pyramid with the stalactite.
				{6,-2,-2,6,-2,-2,15,6,-2,3,-1}, //The hunter 
                                //killed the dangerous poacher with the rifle.
				{-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1}
                               };

  public Parse4Experiment () {
    trainingLength = 0; 
    inTest = false;
    getNextWord();
  }
  
  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
  }

  //----------sentence and word management 
  //---------------handling input via change pattern
  private int totalSentences = 27;
  private int longestSentence =9;
  private int currentSentence = 26;
  private int currentWordInSentence = -1;
  public int currentWord = -1;
  private int getNextWord() {
    System.out.println("set Read Next Word Rule " + Parse4.CANTStep);
    int nextWord = sentences[currentSentence][++currentWordInSentence];
    if (nextWord < 0) {
      System.out.println("Read Beyond End of Sentence " + nextWord);
    }
    currentWord=nextWord;
    return nextWord;
  }

  private void resetInstanceBindings(){
    Parse4Net nounInstanceNet = (Parse4Net)getNet("NounInstanceNet");
    Parse4Net verbInstanceNet = (Parse4Net)getNet("VerbInstanceNet");
    nounInstanceNet.resetBindings();
    verbInstanceNet.resetBindings();
  }

  //----------Symbolic new noun instance stuff is for an early version of the
  //----------parser. It should be  replaced by neural stuff later.
  private int nounInstancesSet = 0;
  private int cycleNounInstSet=-1;
  private void setNewNounInstance (int cycle) {
    Parse4Net nounInstanceNet = (Parse4Net)getNet("NounInstanceNet");
    if ((cycleNounInstSet == -1) || ((cycleNounInstSet+10) < cycle)) {
      cycleNounInstSet = cycle;
      nounInstanceNet.setCurrentPattern(nounInstancesSet);
      nounInstanceNet.setNeuronsToStimulate(150);
      nounInstancesSet ++;
    }
    else if (cycleNounInstSet == (cycle-1))    {   
      nounInstanceNet.setNeuronsToStimulate(0);
    }
 }
  private int verbInstancesSet = 0;
  private int cycleVerbInstSet=-1;
  private void setNewVerbInstance (int cycle) {
    //System.out.println("new verb instance " + cycle + cycleVerbInstSet);
    Parse4Net verbInstanceNet = (Parse4Net)getNet("VerbInstanceNet");
    if (cycleVerbInstSet == -1) {
      cycleVerbInstSet = cycle;
      verbInstanceNet.setCurrentPattern(verbInstancesSet);
      verbInstanceNet.setNeuronsToStimulate(140);
      verbInstancesSet ++;
    }
    else if (cycleVerbInstSet == (cycle-1))       
      verbInstanceNet.setNeuronsToStimulate(0);
  }

  private void setNewInstance (int cycle) {
    if (newNounInstanceRuleOn()) 
      setNewNounInstance(cycle);
    else
      setNewVerbInstance(cycle);
  }

  private boolean newNounInstanceRuleOn() {
    Parse4Net ruleOneNet = (Parse4Net)getNet("RuleOneNet");

    int neuronsOn=0;
    for (int i= 0; i < 100;i++) { //new noun instance
      if(ruleOneNet.neurons[i].getFired()) neuronsOn++;
    }

    if (neuronsOn>20) return true;

    return false;
  }
  private boolean newInstanceRuleOn() {
    Parse4Net ruleOneNet = (Parse4Net)getNet("RuleOneNet");

    int neuronsOn=0;
    for (int i= 0; i < 100;i++) { //new noun instance
      if(ruleOneNet.neurons[i].getFired()) neuronsOn++;
    }

    for (int i= 400; i < 500;i++) { //new verb instance
      if(ruleOneNet.neurons[i].getFired()) neuronsOn++;
    }

    if (neuronsOn>20) return true;

    return false;
  }
  
  private boolean printingResults = false;
  private int startPrinting = -1;
  private void startParsingNextSentence() {
    System.out.println("Start Parsing Next Sentence ");
    resetInstanceBindings();
    currentSentence ++;
    currentSentence %= totalSentences;
    currentWordInSentence = -1;
    cycleVerbInstSet=-1;
    cycleNounInstSet=-1;
    nounInstancesSet=0;
    verbInstancesSet=0;
  }

  private void turnVPInstanceOn(int step) {
    Parse4Net verbInstanceNet = (Parse4Net)getNet("VerbInstanceNet");
    for (int neuron = 0; neuron < 5; neuron++) {
      int first = 0;
      if ((step%2) == 1) first =5;
      for (int feature = 0; feature < 4; feature++) {
        int neuronAct = neuron+(feature*10)+first;
        //neuronAct += (verbInstancesSet -1)*vInstCASize;
        verbInstanceNet.neurons[neuronAct].setActivation(5.0);
      }
    }
  }


  private int nInstCASize=500;//note this is also defined in net
  private void printNounInstance() {
    Parse4Net nounInstanceNet = (Parse4Net)getNet("NounInstanceNet");
    int neuronsFired = 0;
    int instances = nounInstanceNet.getSize()/nInstCASize; 
    for (int instance = 0; instance < instances; instance++) {
      for (int neuron=0; neuron < 40; neuron ++) {
	if (nounInstanceNet.neurons[(instance*nInstCASize)+neuron].getFired())
          neuronsFired++;
      }
      if (neuronsFired > 0) {
        System.out.println("Noun Instance " + instance + " On "+neuronsFired);
        neuronsFired = 0;
      }
      for (int feature = 2; feature < 12; feature ++) {
        for (int neuron=0; neuron < 20; neuron ++) {
	  if (nounInstanceNet.neurons[
            (instance*nInstCASize)+neuron+(feature*20)].getFired())
            neuronsFired++;
        }
        if (neuronsFired > 0) {
          System.out.print("N Instance " + instance );
          if (feature == 6) 
            System.out.print(" Base " );
          else if (feature == 4) 
            System.out.print(" Others " );
          else if (feature == 5) 
            System.out.print(" PrepBind " );
          else if (feature == 7) 
            System.out.print(" Det " );
          else if (feature == 10) 
            System.out.print(" PP Mod " );
          else
            System.out.print(" Feature " + feature );
          System.out.println(" On "+neuronsFired);
          neuronsFired = 0;
        }
      }
    }
  }
  private void printVerbAccess() {
    Parse4Net verbAccessNet = (Parse4Net)getNet("VerbAccessNet");
    int neuronsFired = 0;
    int instances = verbAccessNet.getSize()/100; 
    for (int instance = 0; instance < instances; instance++) {
      for (int neuron=0; neuron < 100; neuron ++) {
	if (verbAccessNet.neurons[(instance*100)+neuron].getFired())
          neuronsFired++;
      }
      if (neuronsFired > 0) {
        System.out.println("Verb Access " + instance + " On "+neuronsFired);
        neuronsFired = 0;
      }
    }
  }
  private void printNounAccess() {
    Parse4Net nounAccessNet = (Parse4Net)getNet("NounAccessNet");
    int neuronsFired = 0;
    int instances = nounAccessNet.getSize()/100; 
    for (int instance = 0; instance < instances; instance++) {
      for (int neuron=0; neuron < 100; neuron ++) {
	if (nounAccessNet.neurons[(instance*100)+neuron].getFired())
          neuronsFired++;
      }
      if (neuronsFired > 0) {
        System.out.println("Noun Access " + instance + " On "+neuronsFired);
        neuronsFired = 0;
      }
    }
  }
  private int vInstCASize=300;//note this is also defined in net
  private void printVerbInstance(){
    Parse4Net verbInstanceNet = (Parse4Net)getNet("VerbInstanceNet");
    int neuronsFired = 0;
    int instances = verbInstanceNet.getSize()/vInstCASize; 
    for (int instance = 0; instance < instances; instance++) {
      for (int neuron=0; neuron < 40; neuron ++) {
	if (verbInstanceNet.neurons[(instance*vInstCASize)+neuron].getFired())
          neuronsFired++;
      }
      if (neuronsFired > 0) {
        System.out.println("Verb Instance " + instance + " On "+neuronsFired);
        neuronsFired = 0;
      }
      for (int feature = 2; feature < 14; feature ++) {
        for (int neuron=0; neuron < 20; neuron ++) {
	  if (verbInstanceNet.neurons[
            (instance*vInstCASize)+neuron+(feature*20)].getFired())
            neuronsFired++;
        }
        if (neuronsFired > 0) {
          System.out.print("Instance " + instance );
          if (feature == 6) 
            System.out.print(" Base " );
          else if (feature == 7) 
            System.out.print(" Others " );
          else if (feature == 8) 
            System.out.print(" Actor " );
          else if (feature == 9) 
            System.out.print(" Object " );
          else if (feature == 11) 
            System.out.print(" Instrument " );
          else if (feature == 12) 
            System.out.print(" Location " );
          else
            //note that feature 3 and 4 (objdone actdone) can come on
            //because they are activated by their obj and act
            System.out.print(" Feature " + feature );
          System.out.println(" On "+neuronsFired);
          neuronsFired = 0;
        }
      }
        
    }
  }
  private void printResults(int step) {
    Parse4Net inputNet = (Parse4Net)getNet("BaseNet");
    Parse4Net verbInstanceNet = (Parse4Net)getNet("VerbInstanceNet");
    if (startPrinting == -1) {
      startPrinting = step;
      inputNet.setNeuronsToStimulate(0);
      verbInstanceNet.setLearningRate((float)0.0001);
    }
    else if ((startPrinting+ 8) == step) 
      printVerbInstance();
    else if ((startPrinting+ 45) == step) {
	//Parse4.setRunning(false);
      printNounInstance();
      printVerbAccess();
      printNounAccess();
      clearAllNets();
      printingResults = false;
      inputNet.setNeuronsToStimulate(100);
      verbInstanceNet.setLearningRate((float)0.4);
      startParsingNextSentence();
      startPrinting=-1;
      Parse4.setInstanceCounterOn(true);
      setReadNextWordRule();
      }
    else {
      turnVPInstanceOn(step);
      Parse4Net ruleOneNet = (Parse4Net)getNet("RuleOneNet");
      ruleOneNet.clear();
    }
  }

  //Consider should move to CANTExperiment
  private void clearAllNets() {
    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CANTNet net = (CANTNet)eNum.nextElement();
      net.clear();
    }	
  }
  private boolean parseDone() {
    Parse4Net ruleTwoNet = (Parse4Net)getNet("RuleTwoNet");

    int neuronsOn=0;
    for (int i= 0; i < 100;i++) { 
      if(ruleTwoNet.neurons[i].getFired()) neuronsOn++;
    }
    if (neuronsOn>20) return true;
    return false;
  }
  private void parseStop(int currentCycle) {
    System.out.println("Parse Done " + currentCycle + " " + currentSentence);
    //    Parse4.setRunning(false);
    clearAllNets();
    printingResults = true;
  }

  //undone symbolic hook for no rule active
  private int lastCycleRuleActive = 0;
  private boolean noRuleActive (int step) {
    Parse4Net ruleOneNet = (Parse4Net)getNet("RuleOneNet");
    for (int i = 0; i< ruleOneNet.getSize(); i++ ) {
      if (ruleOneNet.neurons[i].getFired()) {
	lastCycleRuleActive = step;
	return (false);
      }
    }
    Parse4Net ruleTwoNet = (Parse4Net)getNet("RuleTwoNet");
    for (int i = 0; i< ruleTwoNet.getSize(); i++ ) {
      if (ruleTwoNet.neurons[i].getFired()) {
	lastCycleRuleActive = step;
	return (false);
      }
    }
    if (lastCycleRuleActive <= (step -10)) return true;
    return false;
  }
  private void setReadNextWordRule() {
    Parse4Net ruleNet = (Parse4Net)getNet("RuleOneNet");

    System.out.println("set Read Next Word Rule " + Parse4.CANTStep);
    //activate the ReadNextWord Rule
    for (int i = 300; i < 305; i++) {
      for (int subCA = 0; subCA <10; subCA++) {
        ruleNet.neurons[i+(subCA*10)].setActivation(6.0);
      }
    }
  }

  private int lastReadStep = 0;
  private boolean readNextWordOn(int step) {
    Parse4Net ruleNet = (Parse4Net)getNet("RuleOneNet");
    int neuronsOn = 0;

    //activate the ReadNextWord Rule
    for (int i = 300; i < 400; i++) {
      if (ruleNet.neurons[i].getFired()) neuronsOn++;
    }
    if (neuronsOn == 50) 
      {
      if ((step -10) > lastReadStep) {
        lastReadStep = step;
        return true;
        }
      return false;
      }
    else return false;    
  }
  
  private void printRaster(int currentStep) {
    Parse4Net ruleTwoNet = (Parse4Net)getNet("RuleTwoNet");
    Parse4Net ruleOneNet = (Parse4Net)getNet("RuleOneNet");
    Parse4Net verbInstanceNet = (Parse4Net)getNet("VerbInstanceNet");
    Parse4Net nounInstanceNet = (Parse4Net)getNet("NounInstanceNet");
    for (int i = 0; i < ruleTwoNet.getSize(); i++) {
      if (ruleTwoNet.neurons[i].getFired()) 
        System.out.println("Raster " + ruleTwoNet.getName() + " " + 
        currentStep + " " + i);
    }
    for (int i = 0; i < ruleOneNet.getSize(); i++) {
      if (ruleOneNet.neurons[i].getFired()) 
        System.out.println("Raster " + ruleOneNet.getName() + " " + 
        currentStep + " " + i);
    }
    for (int i = 0; i < 500; i++) {
      if (verbInstanceNet.neurons[i].getFired()) 
        System.out.println("Raster " + verbInstanceNet.getName() + " " + 
        currentStep + " " + i);
    }
    for (int i = 0; i < 500; i++) {
      if (nounInstanceNet.neurons[i].getFired()) 
        System.out.println("Raster " + nounInstanceNet.getName() + " " + 
        currentStep + " " + i);
    }
  }

  public boolean isEndEpoch(int currentStep) {
      //    printRaster(currentStep);
    if (startPrinting == -1) Parse4.setInstanceCounterOn(false);

    if (printingResults) {
      printResults(currentStep);
      return(false);
    }

    //if (noRuleActive(currentStep)) setReadNextWordRule();
    if (readNextWordOn(currentStep)) {int currentWord = getNextWord();}

    if (currentStep == 1) {
      Parse4.setBarOneOn(false);
      Parse4.setInstanceCounterOn(true);
    }
    //else if (newInstanceRuleOn()) setNewInstance(currentStep);
    else if (parseDone()) parseStop(currentStep);

    return (false);
  }  

  public void printExpName () {
    System.out.println("Parse 4");
  }

}


