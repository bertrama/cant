//run multiple tests
import java.io.*;
import java.util.*;

public class VBExperiment extends CANTExperiment {

  private int trainBaseCAs=0;  //set in constructor
  private int bindEpochLength=0;
  
  public VBExperiment (CANTNet net) {
    trainingLength = 0; 
	inTest = false;
	
	if (CANT23VB.runFastBindTest) 
	  {
	  trainBaseCAs = 20000;
	  bindEpochLength = 50;
	  }
	else
	  {
	  trainBaseCAs = 20000;
	  bindEpochLength = 1500;
	  }
  }
  
  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
  }
  
  private void measureNet(VBNet net) {
    int neuronsActive;
	for (int CA = 0; CA < 10; CA++) {
	  neuronsActive=0;
	  for (int neuron=0; neuron< 160; neuron++) 
	    {
		int testNeuron=(CA*160) + neuron;
		if (net.neurons[testNeuron].getFired()) neuronsActive++;
	    }
	  if (neuronsActive > 0) 
	    System.out.println(net.getName() + " " + CA+" " + neuronsActive );
	  }
  }
  
  private void measureNets() {
    VBNet numberNet = (VBNet)getNet("BaseNet");
    VBNet letterNet = (VBNet)getNet("letter");
	
	measureNet(numberNet);
	measureNet(letterNet);
  }
  
  public void measure2(int currentStep) {
  	if (currentStep < trainBaseCAs) 
	  {
	  if (currentStep % 50 == 45) 
	    {
        System.out.println("measure base " + currentStep);
	    measureNets();
	    }
	  }
	else if (((currentStep-trainBaseCAs) % bindEpochLength) == (bindEpochLength -5))
	  {
      System.out.println("measure bind" + currentStep);
	  measureNets();
	  }
  }
  
  private int numCorrectNeuronsFiring(int correctCA) {
    VBNet numberNet = (VBNet)getNet("BaseNet");
    VBNet letterNet = (VBNet)getNet("letter");
	VBNet correctNet;
	
	if ((correctCA%2) == 0) correctNet=letterNet;
    else correctNet=numberNet;

    int neuronsActive = 0;
    neuronsActive=0;
	correctCA/=2;
    for (int neuron=0; neuron< 160; neuron++) 
      {
  	  int testNeuron=(correctCA*160) + neuron;
  	  if (correctNet.neurons[testNeuron].getFired()) neuronsActive++;
      }
	  
	return neuronsActive;
  }

  private int numNeuronsFiring(int correctCA) {
  	VBNet numberNet = (VBNet)getNet("BaseNet");
  	VBNet letterNet = (VBNet)getNet("letter");
	int neuronsActive=0;
	
  	for (int neuron=0; neuron< numberNet.getSize(); neuron++) 
  	  {
  	  if (numberNet.neurons[neuron].getFired()) neuronsActive++;
  	  if (letterNet.neurons[neuron].getFired()) neuronsActive++;
  	  }
  	  
  	return neuronsActive;
  }
  
  private int onThreshold=5;
  //here -1 means no CA active, -2 means more than one CA active
  private int getBaseCAOn(CANTNet net) {
  	int CAOn=-1;
	for (int CA = 0; CA < 10; CA++)  {
	  int neuronsActive=0;
	  for (int neuron=0; neuron< 160; neuron++) 
	    {
	    int testNeuron=(CA*160) + neuron;
	    if (net.neurons[testNeuron].getFired()) neuronsActive++;
	    }
	  if (neuronsActive > onThreshold) 
	    {
		if (CAOn == -1) CAOn=CA;
		else return -2;
	    }
	  }
  
    return CAOn;
  }
  
  private void printBindEpochTest2(int bindingTypeEpoch, int letterCAActive, 
  		                          int numberCAActive) {
    if (bindingTypeEpoch == 0) 
      {
      if ((letterCAActive == boundLetter) && (numberCAActive == boundNumber))
        System.out.println("correct Initial Binding");
      else	
        System.out.println("incorrect Initial Binding");
      }
    else if (bindingTypeEpoch == 1) 
      {
      if ((letterCAActive == boundLetter) && (numberCAActive == boundNumber))
        System.out.println("correct Number Binding");
      else	
        System.out.println("incorrect Number Binding");
      }
    else if (bindingTypeEpoch == 2) 
      {
      if ((letterCAActive == boundLetter) && (numberCAActive == boundNumber))
        System.out.println("correct Letter Binding");
      else	
        System.out.println("incorrect Letter Binding");
      }
    else if (bindingTypeEpoch == 3) 
      {
      if ((letterCAActive == -1) && (numberCAActive == unboundNumber))
        System.out.println("correct Number unbound");
      else	
        System.out.println("incorrect Number unbound");
      }
    else if (bindingTypeEpoch == 4) 
      {
      if ((letterCAActive == unboundLetter) && (numberCAActive == -1))
        System.out.println("correct Letter unbound");
      else	
        System.out.println("incorrect Letter unbound");
      }
    else if ((bindingTypeEpoch >= 5) && (bindingTypeEpoch<=9))
      {
      if ((letterCAActive == -1) && (numberCAActive == -1))
        System.out.println("correct Erase");
      else	
        System.out.println("incorrect Erase");
      }
    else if (bindingTypeEpoch == 10) 
      {
      if ((letterCAActive == -1) && (numberCAActive == boundNumber))
        System.out.println("correct Number postbind");
      else	
        System.out.println("incorrect Number postbind");
      }
    else if (bindingTypeEpoch == 11) 
      {
      if ((letterCAActive == boundLetter) && (numberCAActive == -1))
        System.out.println("correct Letter postbind");
      else	
        System.out.println("incorrect Letter postbind");
      }
    else if (bindingTypeEpoch == 12) 
      {
      if ((letterCAActive == -1) && (numberCAActive == unboundNumber))
        System.out.println("correct Number unboundpbind");
      else	
        System.out.println("incorrect Number unboundpbind");
      }
    else if (bindingTypeEpoch == 13) 
      {
      if ((letterCAActive == unboundLetter) && (numberCAActive == -1))
        System.out.println("correct Letter unboundpbind");
      else	
        System.out.println("incorrect Letter unboundpbind");
      }
  }
  
  private int bind,bindN,bindL,bindIN,bindIL,erase,unbindN,unbindL,unbindIN,unbindIL;
  private void printBindEpochTest(int bindingTypeEpoch, int letterCAActive, 
  		                          int numberCAActive) {
    if (bindingTypeEpoch == 0) 
	  {
	  bind=bindN=bindL=bindIN=bindIL=erase=unbindN=unbindL=unbindIN=unbindIL=0;
	  
      if ((letterCAActive == boundLetter) && (numberCAActive == boundNumber))
        bind++;
	  }	
    else if (bindingTypeEpoch == 1) {
      if ((letterCAActive == boundLetter) && (numberCAActive == boundNumber))
        bindN++; }
    else if (bindingTypeEpoch == 2) 	{
      if ((letterCAActive == boundLetter) && (numberCAActive == boundNumber))
		bindL++;	}
    else if (bindingTypeEpoch == 3) 	{
      if ((letterCAActive == -1) && (numberCAActive == unboundNumber))
		bindIN++;	}
    else if (bindingTypeEpoch == 4) 	{
      if ((letterCAActive == unboundLetter) && (numberCAActive == -1))
		bindIL++;	}
    else if ((bindingTypeEpoch >= 5) && (bindingTypeEpoch<=9))	{
      if ((letterCAActive == -1) && (numberCAActive == -1))
	  	erase++;	}
    else if (bindingTypeEpoch == 10) 	{
      if ((letterCAActive == -1) && (numberCAActive == boundNumber))
		unbindN++;	}
    else if (bindingTypeEpoch == 11) 	{
      if ((letterCAActive == boundLetter) && (numberCAActive == -1))
	  	unbindL++;	}
    else if (bindingTypeEpoch == 12) 	{
      if ((letterCAActive == -1) && (numberCAActive == unboundNumber))
		unbindIN++;	}
    else if (bindingTypeEpoch == 13) 
	  {
      if ((letterCAActive == unboundLetter) && (numberCAActive == -1))
	  	unbindIL++;
	  System.out.println(bind + " " + bindN + " " + bindL + " " +bindIN + " " +
	                     bindIL + " " +erase + " " +unbindN + " " +unbindL + " " +
	                     unbindIN + " " +unbindIL);
	  }
  }
  

  private int numCorrectNeuronsFiring, numIncorrectNeuronsFiring;
  public void measure(int currentStep) {
  if (currentStep < trainBaseCAs) 
    {
    if ((currentStep % 50) == 45) 
      {
	  if ((currentStep%1000) < 50) 
	    {
	 	numCorrectNeuronsFiring = 0;
		numIncorrectNeuronsFiring = 0;
	    }
	  
	  int correctCA=currentStep/50;
	  correctCA%=20;
      int numCorrectNeuronsFiringThis = numCorrectNeuronsFiring(correctCA);
	  numCorrectNeuronsFiring += numCorrectNeuronsFiringThis;
      numIncorrectNeuronsFiring += numNeuronsFiring(correctCA)-numCorrectNeuronsFiringThis;      

      if ((currentStep%1000) > 950) 
        System.out.println(numCorrectNeuronsFiring + " " + numIncorrectNeuronsFiring + 
		  " " + currentStep);
      }
    }
  else if (((currentStep-trainBaseCAs) % bindEpochLength) == (bindEpochLength -5))
    {
	CANTNet letterNet = getNet("letter");
	CANTNet numberNet = getNet("BaseNet");
	//get the correct test epoch 
	int bindingType = getBindingEpochType(currentStep);

	int letterCAActive = getBaseCAOn(letterNet);
	int numberCAActive = getBaseCAOn(numberNet);

    printBindEpochTest(bindingType,letterCAActive,numberCAActive);	
    }
  }

  public boolean isEndEpoch(int currentStep) {
  	if (currentStep < trainBaseCAs) 
	  {
      if ((currentStep % 50) == 0) return (true);
	  else return (false);
	  }
	else
	  {
	  if ((currentStep % bindEpochLength) == 0) return (true);
	  else return (false);
	  }
	
  }  
  

  private void resetAllNets() {
    CANTNet numberNet = getNet("BaseNet");
    CANTNet letterNet = getNet("letter");
	
    numberNet.clear();
    letterNet.clear();

    if (!CANT23VB.runFastBindTest)     
	  {
      CANTNet bindNet = getNet("bind");
      bindNet.clear();
	  }
  }
  
  
  private void switchToBinding() {
    System.out.println("switchToBinding ");
    CANTNet numberNet = getNet("BaseNet");
    CANTNet letterNet = getNet("letter");
		
	letterNet.setCyclesPerRun(bindEpochLength);
    numberNet.setCyclesPerRun(bindEpochLength);

    if (!CANT23VB.runFastBindTest) 
	  {
      CANTNet bindNet = getNet("bind");
      bindNet.setCyclesPerRun(bindEpochLength);
	  }
//CANT23.setRunning(false);	
  }
  
  private int typesOfBindingPresentation = 14;
  private int getBindingEpochType(int step) {

    int bindingSteps = step-trainBaseCAs;	
    int bindingType = bindingSteps/bindEpochLength;
    bindingType %= typesOfBindingPresentation;
	
	return bindingType;
  }
  
  int boundLetter, boundNumber, unboundLetter, unboundNumber;
  private void setBindingInputs(int step) {
	int bindingType = getBindingEpochType(step);
	
	CANTNet numberNet = getNet("BaseNet");
	CANTNet letterNet = getNet("letter");

    if (bindingType == 0) 	
      {
	    //bind
	    boundLetter = (int) (Math.random() *10);
	    boundNumber = (int) (Math.random() *10);
	    do {
	      unboundLetter = (int) (Math.random() *10);
	    } while (boundLetter == unboundLetter);
	    do {
	      unboundNumber = (int) (Math.random() *10);
	    } while (boundNumber == unboundNumber);
	  letterNet.setNeuronsToStimulate(50);
	  numberNet.setNeuronsToStimulate(50);

	  letterNet.setCurrentPattern(boundLetter);
	  numberNet.setCurrentPattern(boundNumber);
     }
    else if ((bindingType == 1) || (bindingType ==10))
	  {
	  //test bound number
	  numberNet.setCurrentPattern(boundNumber);
	  letterNet.setNeuronsToStimulate(0);
	  numberNet.setNeuronsToStimulate(50);
	  }
	else if ((bindingType == 2) || (bindingType == 11))
	  {
	  //test bound letter
	  letterNet.setCurrentPattern(boundLetter);
	  letterNet.setNeuronsToStimulate(50);
	  numberNet.setNeuronsToStimulate(0);
	  }
	else if ((bindingType == 3) || (bindingType ==12))
	  {
	  //test unbound number
	  numberNet.setCurrentPattern(unboundNumber);
	  letterNet.setNeuronsToStimulate(0);
	  numberNet.setNeuronsToStimulate(50);
	  }
    else if ((bindingType == 4) || (bindingType ==13))
	  {
	  //test unbound letter
	  letterNet.setCurrentPattern(unboundLetter);
	  letterNet.setNeuronsToStimulate(50);
	  numberNet.setNeuronsToStimulate(0);
	  }
	else if ((bindingType >= 5) && (bindingType <=9))
	  {
	  //unbind
	  letterNet.setNeuronsToStimulate(0);
	  numberNet.setNeuronsToStimulate(0);
	  }
	else System.out.println("Error in set binding inputs ");
  }
	
  public void endEpoch () {
  	int numPatterns = 10;
  	CANTNet numberNet = getNet("BaseNet");
  	CANTNet letterNet = getNet("letter");
  	int step = CANT23.CANTStep;
		
	resetAllNets();
	if (step < trainBaseCAs) 
	  {
	  if ((step %100) == 0)  {
	    //switch pattern to letter
		letterNet.setNeuronsToStimulate(50);
		numberNet.setNeuronsToStimulate(0);

		int curPatt=step/100;
		curPatt%=numPatterns;
        letterNet.setCurrentPattern(curPatt);
	    }
	  else	    {
	    //switch pattern to number
	    letterNet.setNeuronsToStimulate(0);
	    numberNet.setNeuronsToStimulate(50);
	    int curPatt=step/100;
	    curPatt%=numPatterns;
	    numberNet.setCurrentPattern(curPatt);
	  }
	}
	  
	else {
	  if (step == trainBaseCAs) switchToBinding();
	  setBindingInputs(step);
	  }
  }
  
  public void printExpName () {
    System.out.println("VB Experiment ");
  }

  public boolean experimentDone (int Step) {
  	int bindingTestLength = bindEpochLength*typesOfBindingPresentation;
  	int length = trainBaseCAs+(10*bindingTestLength);
	
  	if (Step == length)
      return (true);
    return (false);  
  }

}