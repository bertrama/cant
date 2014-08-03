//run multiple tests
import java.io.*;
import java.util.*;

public class Parse2Experiment extends CANTExperiment {

  public Parse2Experiment () {
    trainingLength = 0; 
	inTest = false;
	ruleOn = new int[5];
	for (int i = 0; i < 5; i++)
	  ruleOn[i]=0;
	sentenceResults = new int [17][2];  
    for (int i = 0; i < 17; i++) 
	  {
	  sentenceResults[i][0]=0;
	  sentenceResults[i][1]=0;
	  }
  }
  
  //sentence 1 Follow me.
  //sentence 2 Move left.
  //sentence 3 Turn toward the pyramid.
  //sentence 4 Move toward it.
  //sentence 5 Go left.
  //sentence 6 Move toward the pyramid.
  //sentence 7 Turn toward it.
  //sentence 8 Move right.
  //sentence 9 Move forward.
  //sentence 10 Move backward.
  //sentence 11 Move toward the stalagtite. 
  //sentence 12 Move toward the door. 
  //sentence 13 Turn toward the stalagtite.
  //sentence 14 Turn toward the door.
  //sentence 15 Go right.
  //sentence 16 Go forward.
  //sentence 17 Go back.
  //undone (to) Go to the door/pyramid/stalagtite
  private int sentence = 15;

  //the push rule changes the input if it fires.
  //The next work is given to input when the push rule stops firing.
  int cyclePushLastStarted = 0;
  int cyclePushLastFinished = 0;
  private boolean pushRuleOn(int cycle) {
  	Parse2Net pushNet = (Parse2Net)getNet("PushNet");
	int pushNeuronsOn=0;
	if (cycle-10 < cyclePushLastFinished) return false;
	
	for (int i = 0; i < 1000; i++)
	  if (pushNet.neurons[i].getFired())
	  	pushNeuronsOn++;
	
	if (pushNeuronsOn > 150) 
	  {
	  cyclePushLastStarted = cycle;
      return (false);
	  }
	if ((pushNeuronsOn < 10) && (cyclePushLastStarted > 0))
	  {
	  cyclePushLastStarted = 0;
	  cyclePushLastFinished = cycle;
	  return (true);
	  }
	else return (false);  
  }
  
  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
  }

  private static boolean inRunAgain = false;
  private int currentWord = -2;
  public boolean isEndEpoch(int currentStep) {
    Parse2Net inputNet = (Parse2Net)getNet("BaseNet");
    Parse2Net verbNet = (Parse2Net)getNet("VerbNet");
    Parse2Net stackTopNet = (Parse2Net)getNet("StackTopNet");
    Parse2Net stackNet = (Parse2Net)getNet("StackNet");

  	if (currentStep == 0) 
	  {
	  System.out.println("isendepoch 0");
	  stackTopNet.setNeuronsToStimulate(40);
	  inputNet.setNeuronsToStimulate(0);
	  }
	else if (currentStep == 10) 
	  {
	  stackTopNet.setNeuronsToStimulate(0);
	  inputNet.setNeuronsToStimulate(50);
	  readNextWord(currentStep);
	  }
	else if (pushRuleOn(currentStep))
	  {
	    readNextWord(currentStep);
	  }
	else if ((stackTopNet.getActives()==0)    || 
             (CANT23.CANTStep > 1500))
	  {
	  	CANT23.setRunning(false);
		if (!inRunAgain) 
		  {
		  inRunAgain=true;
          runAgain();
      	  inRunAgain=false;
		  }
	  }
	else printEvents();
	  
    return (false);
  }  

  private void readNextWord(int currentStep) 
    {
    Parse2Net inputNet = (Parse2Net)getNet("BaseNet");

	if (sentence==1) 
	  {
	  if (currentWord==-2)
	    currentWord=0;
	  else 	
	    currentWord++;
	  if (currentWord==3)
	    inputNet.setNextWord(-1);
	  else	
	    inputNet.setNextWord(currentWord);
	  }
	else if (sentence==2) 
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord==3) 
	    currentWord++;
	  else if (currentWord==4) 
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==3) 
	  {
	  if (currentWord==-2)
	    currentWord=5;
	  else if ((currentWord<8) && (currentWord >= 5))
	    currentWord++;
	  else if (currentWord==8)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==4) 
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord == 3)
	  	currentWord=6;
	  else if (currentWord == 6)
	    currentWord = 10;
	  else if (currentWord==10)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==5) 
	  {
	  if (currentWord==-2)
	    currentWord=11;
	  else if (currentWord == 11)
	  	currentWord=4;
	  else if (currentWord == 4)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==6) 
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord == 3)
	  	currentWord=6;
	  else if (currentWord == 6)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 8;
	  else if (currentWord==8)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==7) 
	  {
	  if (currentWord==-2)
	    currentWord=5;
	  else if (currentWord == 5)
	  	currentWord=6;
	  else if (currentWord == 6)
	    currentWord = 10;
	  else if (currentWord == 10)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==8) 
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord==3) 
	    currentWord = 12;
	  else if (currentWord==12) 
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==9) 
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord==3) 
	    currentWord = 13;
	  else if (currentWord==13) 
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==10) 
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord==3) 
	    currentWord = 14;
	  else if (currentWord==14) 
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==11) //Turn toward the stalagtite
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord == 3)
	  	currentWord=6;
      else if (currentWord == 6)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 9;
	  else if (currentWord==9)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==12)
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord == 3)
	  	currentWord=6;
	  else if (currentWord == 6)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 15;
	  else if (currentWord==15)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==13) 
	  {
	  if (currentWord==-2)
	    currentWord=5;
	  else if (currentWord == 5)
	    currentWord = 6;
	  else if (currentWord == 6)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 9;
	  else if (currentWord==9)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==14) 
	  {
	  if (currentWord==-2)
	    currentWord=5;
	  else if (currentWord == 5)
	    currentWord = 6;
	  else if (currentWord == 6)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 15;
	  else if (currentWord==15)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==15) 
	  {
	  if (currentWord==-2)
	    currentWord=11;
	  else if (currentWord == 11)
	  	currentWord=12;
	  else if (currentWord == 12)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==16) 
	  {
	  if (currentWord==-2)
	    currentWord=11;
	  else if (currentWord == 11)
	  	currentWord=13;
	  else if (currentWord == 13)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else if (sentence==17) 
	  {
	  if (currentWord==-2)
	    currentWord=11;
	  else if (currentWord == 11)
	  	currentWord=14;
	  else if (currentWord == 14)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  
	  inputNet.setNextWord(currentWord);
	  }
	else 
      System.out.println("Bad Sentence " + sentence);
    System.out.println("Next Word Read " + currentStep);
    }  
	
  static int ruleOn[];
  private void printRuleApplied () {
    Parse2Net  ruleNet = (Parse2Net)getNet("RuleNet");
	for (int rule = 0; rule < 5; rule++) 
	  {
	  int neuronsOn = 0;
	  for (int i = 0; i < 300;i++) 
	    {
	    int ruleNeuron = (rule*300)+i;
	    if (ruleNet.neurons[ruleNeuron].getFired())
	      neuronsOn++;
	    }
	  if ((ruleOn[rule] == 0) && (neuronsOn > 0)) ruleOn[rule] = neuronsOn;
	  else if ((ruleOn[rule] > 0) && (neuronsOn == 0)) 
	    {
		if (rule ==0) 
	      System.out.println("VP -> VP Nobj " + CANT23.CANTStep + " " + ruleOn[rule]);
	    else if (rule ==1) 
	      System.out.println("VP -> VP Period " + CANT23.CANTStep+ " " + ruleOn[rule]);
	    else if (rule ==2) 
	      System.out.println("PP -> Prep Det " + CANT23.CANTStep+ " " + ruleOn[rule]);
	    else if (rule ==3) 
	      System.out.println("PP -> PP Noun " + CANT23.CANTStep+ " " + ruleOn[rule]);
	    else if (rule ==4) 
	      System.out.println("VP -> VP PP loc " + CANT23.CANTStep+ " " + ruleOn[rule]);
	    ruleOn[rule] = 0;
	    }
	  else if ((ruleOn[rule] > 0) && (neuronsOn > ruleOn[rule])) ruleOn[rule]=neuronsOn;
	  }
  }
  
  private int stackTopActive=0;
  private void printStackTopChanges (){
    Parse2Net  stackTopNet = (Parse2Net)getNet("StackTopNet");
	int lastStackTopActive=-1;
    int popActive = -1;
	for (int stackEl = 0; stackEl < 5; stackEl++) 
	  {
	  int neuronsFiring=0;
	  for (int neuron =0; neuron<300; neuron++) 
	    {
		int seNeuron=neuron+(stackEl*300);
	    if (stackTopNet.neurons[seNeuron].getFired())
		  neuronsFiring++;
	    }
		//if pushing set lastStackTopActive
		if ((popActive == -1) && (stackEl == stackTopActive) && (neuronsFiring == 0))
		  lastStackTopActive=stackEl;
		//if pushing and lastStackTopActive set new stack
		else if ((lastStackTopActive != -1) && (neuronsFiring > 1)){
		  stackTopActive = stackEl;
		  System.out.println("Pushing from "+ lastStackTopActive + " to " + stackEl + " " + CANT23.CANTStep);
		  }
		
		//if popping set up potential pop
		else if ((stackEl < stackTopActive) && (neuronsFiring > 0)) 
		  popActive = stackEl;
		//do pop  
		else if ((popActive != -1) && (stackEl == stackTopActive) && (neuronsFiring == 0))
		  {
		  stackTopActive = popActive;
		  System.out.println("Popping from "+ stackEl + " to " + stackTopActive + " " + CANT23.CANTStep);
		  }
	  }
  }
  
  private void printEvents() {
  	printRuleApplied();
	printStackTopChanges();
  }
	
  private void clearAllNets() {
    Parse2Net  inputNet = (Parse2Net)getNet("BaseNet");
    Parse2Net  instanceNet = (Parse2Net)getNet("InstanceNet");
    Parse2Net  verbNet = (Parse2Net)getNet("VerbNet");
    Parse2Net  nounNet = (Parse2Net)getNet("NounNet");
    Parse2Net  otherNet = (Parse2Net)getNet("OWordNet");
    Parse2Net  stackTopNet = (Parse2Net)getNet("StackTopNet");
    Parse2Net  stackNet = (Parse2Net)getNet("StackNet");
    Parse2Net  pushNet = (Parse2Net)getNet("PushNet");
    Parse2Net  popNet = (Parse2Net)getNet("PopNet");
    Parse2Net  ruleNet = (Parse2Net)getNet("RuleNet");
    Parse2Net  eraseNet = (Parse2Net)getNet("EraseNet");
    Parse2Net  eraseBoundNet = (Parse2Net)getNet("EraseBoundNet");
    Parse2Net  testNet = (Parse2Net)getNet("TestNet");

    inputNet.clear();
	instanceNet.clear();
	verbNet.clear();
	nounNet.clear();
	otherNet.clear();
	stackTopNet.clear();
	stackNet.clear();
	pushNet.clear();
	popNet.clear();
	ruleNet.clear();
	eraseBoundNet.clear();
	testNet.clear();
  }
  
  private void setStackOn() {
    Parse2Net  stackNet = (Parse2Net)getNet("StackNet");
	for (int i = 0 ; i <300; i++) 
	  {
	  stackNet.neurons[i].setActivation(10.0);
	  }
  }
  
  private int overRan=0;
  private int multipleVerbs = 0;
  private int multipleVerbSlots = 0;
  private int multipleNouns = 0;
  private int multiplePreps = 0;
  private int multiplePrepSlots = 0;
  private int verbOn, verbSlotOn, nounOn, prepOn, prepSlotOn;
  
  private void measureVerb() {
    Parse2Net  verbNet = (Parse2Net)getNet("VerbNet");
	verbOn=-1;
	verbSlotOn = -1;
    for (int verb = 0; verb < 4; verb++) 
	  {
	  int activeNeurons = 0;
	  //measure core verb
	  for (int i = 0; i < 300; i++) 
	    {
		int neuron = (verb*480)+i;
		if (verbNet.neurons[neuron].getFired())
		  activeNeurons ++;
	    }
	  if (activeNeurons > 0)
	    {
	    System.out.println("Verb "+ verb + " " + activeNeurons);
		if (verbOn != -1) multipleVerbs++;
		verbOn=verb;
	    }
		
	  //measure object slot
	  activeNeurons=0;
	  for (int i = 300; i < 360; i++) 
	    {
	    int neuron = (verb*480)+i;
	    if (verbNet.neurons[neuron].getFired())
	      activeNeurons ++;
	    }
	  if (activeNeurons > 0)
	    {
	    System.out.println("Object On "+ verb + " " + activeNeurons);
	    if (verbSlotOn != -1) multipleVerbSlots++;
	    verbSlotOn=0;
	    }
		

	  //measure location slot
	  activeNeurons=0;
	  for (int i = 360; i < 420; i++) 
	    {
	    int neuron = (verb*480)+i;
	    if (verbNet.neurons[neuron].getFired())
	      activeNeurons ++;
	    }
	  if (activeNeurons > 0)
        {
	    System.out.println("Location On "+ verb + " " + activeNeurons);
	    if (verbSlotOn != -1) multipleVerbSlots++;
	    verbSlotOn=1;
	    }
	  }
  }

  private void measureInstance() {
  	int prepStart=1800;
    Parse2Net  instanceNet = (Parse2Net)getNet("InstanceNet");
	nounOn=-1;
	prepOn=-1;
	prepSlotOn=-1;
    for (int noun = 0; noun < 12; noun++) 
      {
      int activeNeurons = 0;
      //measure noun
      for (int i = 0; i < 150; i++) 
        {
  	    int neuron = (noun*150)+i;
  	    if (instanceNet.neurons[neuron].getFired())
  	      activeNeurons ++;
        }
      if (activeNeurons > 0)
	    {
        System.out.println("Noun "+ noun + " " + activeNeurons);
        if (nounOn != -1) multipleNouns++;
        nounOn=noun;
        }
      }	

    for (int prep = 0; prep < 3; prep++) 
      {
      int activeNeurons = 0;
      //measure prep
      for (int i = 0; i < 300; i++) 
        {
        int neuron = prepStart+(prep*360)+i;
        if (instanceNet.neurons[neuron].getFired())
          activeNeurons ++;
        }
      if (activeNeurons > 0)
	    {
        System.out.println("Prep "+ prep + " " + activeNeurons);
        if (prepOn != -1) multiplePreps++;
        prepOn=prep;
	    }
		
    
	  activeNeurons=0;	
      for (int i = 300; i < 360; i++) 
        {
        int neuron = prepStart+(prep*360)+i;
        if (instanceNet.neurons[neuron].getFired())
          activeNeurons ++;
        }
      if (activeNeurons > 0)
	    {
        System.out.println("Prep slot "+ prep + " " + activeNeurons);
        if (prepSlotOn != -1) multiplePrepSlots++;
        prepSlotOn=prep;
	    }
    }	
  }
  
  private int sentenceResults[][];
  private void printSentenceTotals() {
  	if (sentence == 1) 
  	  {
	  if ((verbOn==0) && (verbSlotOn == 0) && (nounOn==0))
	    sentenceResults[0][0]++;
	  else	
  	    sentenceResults[0][1]++;
  	  }
  	else if (sentence == 2) 
  	  {
  	  if ((verbOn==1) && (verbSlotOn == 0) && (nounOn==1))
  	    sentenceResults[1][0]++;
  	  else	
  	    sentenceResults[1][1]++;
  	  }
  	else if (sentence == 3) 
  	  {
  	  if ((verbOn==2) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==2))
  	    sentenceResults[2][0]++;
  	  else	
  	    sentenceResults[2][1]++;
  	  }
  	else if (sentence == 4) 
  	  {
  	  if ((verbOn==1) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==3))
  	    sentenceResults[3][0]++;
  	  else	
  	    sentenceResults[3][1]++;
  	  }
  	else if (sentence == 5) 
  	  {
  	  if ((verbOn==3) && (verbSlotOn == 0) && (nounOn==1))
  	    sentenceResults[4][0]++;
  	  else	
  	    sentenceResults[4][1]++;
  	  }
 	else if (sentence == 6) 
 	  {
 	  if ((verbOn==1) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==2))
 	    sentenceResults[5][0]++;
 	  else	
 	    sentenceResults[5][1]++;
 	  }
 	else if (sentence == 7) 
 	  {
 	  if ((verbOn==2) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==3))
 	    sentenceResults[6][0]++;
 	  else	
 	    sentenceResults[6][1]++;
 	  }
 	else if (sentence == 8) 
 	  {
 	  if ((verbOn==1) && (verbSlotOn == 0) && (nounOn==4))
 	    sentenceResults[7][0]++;
 	  else	
 	    sentenceResults[7][1]++;
 	  }
 	else if (sentence == 9) 
 	  {
 	  if ((verbOn==1) && (verbSlotOn == 0) && (nounOn==5))
 	    sentenceResults[8][0]++;
 	  else	
 	    sentenceResults[8][1]++;
 	  }
 	else if (sentence == 10) 
 	  {
 	  if ((verbOn==1) && (verbSlotOn == 0) && (nounOn==6))
 	    sentenceResults[9][0]++;
 	  else	
 	    sentenceResults[9][1]++;
 	  }
 	else if (sentence == 11) //turn toward the stalagtite
 	  {
 	  if ((verbOn==1) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==7))
 	    sentenceResults[10][0]++;
 	  else	
 	    sentenceResults[10][1]++;
 	  }
 	else if (sentence == 12) 
  	  {
  	  if ((verbOn==1) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==8))
  	    sentenceResults[11][0]++;
  	  else	
  	    sentenceResults[11][1]++;
  	  }
    else if (sentence == 13) 
      {
      if ((verbOn==2) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==7))
        sentenceResults[12][0]++;
      else	
        sentenceResults[12][1]++;
      }
    else if (sentence == 14) 
      {
      if ((verbOn==2) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==8))
        sentenceResults[13][0]++;
      else	
        sentenceResults[13][1]++;
      }
    else if (sentence == 15) 
      {
      if ((verbOn==3) && (verbSlotOn == 0) && (nounOn==4))
        sentenceResults[14][0]++;
      else	
        sentenceResults[14][1]++;
      }
    else if (sentence == 16) 
      {
      if ((verbOn==3) && (verbSlotOn == 0) && (nounOn==5))
        sentenceResults[15][0]++;
      else	
        sentenceResults[15][1]++;
      }
    else if (sentence == 17) 
      {
      if ((verbOn==3) && (verbSlotOn == 0) && (nounOn==6))
        sentenceResults[16][0]++;
      else	
        sentenceResults[16][1]++;
      }
	  
	//print current results
	for (int i = 0; i< 17;i++)   
	  {
	  System.out.println("Sentence " + (i+1) + " " + sentenceResults[i][0] + " " + sentenceResults[i][1]);
	  }
	  
    System.out.println("Errors " + overRan + " " + multipleVerbs + " " + multipleVerbSlots + " " 
	                             + multipleNouns + " " + multiplePreps + " " 
								 + multiplePrepSlots);
  }

  public void printSymbolicResult() {
    //clear all nets
	clearAllNets();
	
	if (CANT23.CANTStep > 1500) overRan++;
	
	//turn stack 1 on for several cycles
	Parse2Net  stackNet = (Parse2Net)getNet("StackNet");
	for (int i = 0; i < 10; i++) 
	  {
	  CANT23.setRunning(true);
	  setStackOn();
	  stackNet.runAllOneStep(CANT23.CANTStep);
	  CANT23.CANTStep++;
	  }
    CANT23.setRunning(false);
	
	
	//measure what's on
	measureVerb();
	measureInstance();
	printSentenceTotals();
  }
  
  private void clearFastBindNeurons(Parse2Net net) {
    for (int neuronIndex = 0; neuronIndex<net.getSize(); neuronIndex++) 
	  {
      if (net.neurons[neuronIndex] instanceof CANTNeuronFastBind) 
	    {
		for (int synapseIndex = 0; synapseIndex < net.neurons[neuronIndex].getCurrentSynapses() ; synapseIndex++)
		  net.neurons[neuronIndex].synapses[synapseIndex].setWeight(0.01);
	    }
	  }
  }

  public void clearFastBindNeurons() {
    Parse2Net  stackNet = (Parse2Net)getNet("StackNet");
    Parse2Net  verbNet = (Parse2Net)getNet("VerbNet");
    Parse2Net  instanceNet = (Parse2Net)getNet("InstanceNet");
	clearFastBindNeurons(stackNet);
	clearFastBindNeurons(verbNet);
  	clearFastBindNeurons(instanceNet);
  }
  
  private void runAgain() {
  	System.out.println("Results of Sentence " + sentence + " " + CANT23.CANTStep);
  	printSymbolicResult();
  	clearFastBindNeurons();
	clearAllNets();
	sentence %= 17;
	sentence ++;
	currentWord = -2 ;
	CANT23.CANTStep=-1;
	currentWord = -2 ;
	cyclePushLastFinished = 0;
    CANT23.setRunning(true);
  }

  public void printExpName () {
    System.out.println("Parse ");
  }

}