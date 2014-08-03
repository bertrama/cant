//run multiple tests
import java.io.*;
import java.util.*;

public class CountExperiment extends CANTExperiment {

  public CountExperiment (CANTNet net) {
    trainingLength = 0; 
	inTest = false;
	connectAllNets();
  }
  
  //A couple of functions to make synapses between the appropriate nets
  private  void connectAllNets() {
	
    CANTNetCount doneNet = (CANTNetCount)getNet("done");
    CANTNetCount internalNet = (CANTNetCount)getNet("internal");
    CANTNetCount finishNet = (CANTNetCount)getNet("finish");
    CANTNetCount inputNet = (CANTNetCount)getNet("BaseNet");
	CANTNetCount rulesNet = (CANTNetCount)getNet("rules");
	CANTNetCount bindNet = (CANTNetCount)getNet("bind");
	CANTNetCount resetNet = (CANTNetCount)getNet("reset");
	
	//System.out.println(internalNet.getName));
	//System.out.println(inputNet.getName());
	//System.out.println(rulesNet.getName());
	//System.out.println(bindNet.getName());
	//System.out.println(doneNet.getName());
	//System.out.println(finishNet.getName());
	
	connectInputInternal(inputNet,internalNet,50);
    connectToBind((CANTNetCount)internalNet,bindNet,10);
    connectToBind((CANTNetCount)bindNet,internalNet,15);
    connectFinishToBind((CANTNetCount)finishNet,bindNet,15);
    connectToBind((CANTNetCount)bindNet,finishNet,15);
    connectInternalRules(internalNet, rulesNet,20);	 
    connectRulesInternal(rulesNet,internalNet,60);	 
    connectRulesDone(rulesNet,doneNet,10);
    connectDoneRules(doneNet,rulesNet,30);
    connectDoneInput(doneNet,inputNet,100);
    connectResetInternal(resetNet,internalNet,50);
    connectFinishReset(finishNet,resetNet,50);
  }
  
  private static void connectInputInternal(CANTNetCount net1, CANTNet net2, int synapsesPerNeuron) {
  	int toSize = net2.size();
  	for (int neuronIndex = 0; neuronIndex < net1.size(); neuronIndex++){
  	  for (int newConnection = 0; newConnection < synapsesPerNeuron; newConnection++){
  	    int toNeuron = (int)(Math.random()*toSize);
  	    double weight  = net1.getInputInternalWeight(neuronIndex,toNeuron);
  	    net1.neurons[neuronIndex].addConnection(net2.neurons[toNeuron],weight);
  	  }
  	}
  }
  
  //this works for input to and from bind and bind to finish
  private static void connectToBind(CANTNetCount fromNet, CANTNet toNet, int synapsesPerNeuron) {
  	int toSize = toNet.size();
  	int fromSize = fromNet.size();
  	for (int neuronIndex = 0; neuronIndex < fromSize; neuronIndex++){
  	  for (int newConnection = 0; newConnection < synapsesPerNeuron; newConnection++){
  	    int toNeuron = (int)(Math.random()*toSize);
  	    double weight  = fromNet.getDefaultWeight(neuronIndex);
  	    fromNet.neurons[neuronIndex].addConnection(toNet.neurons[toNeuron],weight);
  	  }
  	}
  }

  private static void connectFinishToBind(CANTNetCount fromNet, CANTNet toNet, int synapsesPerNeuron) {
  	int toSize = toNet.size() / 2;
  	int fromSize = fromNet.size();
  	for (int neuronIndex = 0; neuronIndex < fromSize; neuronIndex++){
  	  for (int newConnection = 0; newConnection < synapsesPerNeuron; newConnection++){
  	    int toNeuron = (int)(Math.random()*toSize);
  	    double weight  = fromNet.getDefaultWeight(neuronIndex);
  	    fromNet.neurons[neuronIndex].addConnection(toNet.neurons[toNeuron],weight);
  	  }
  	}
  }


  private static void connectInternalRules(CANTNetCount net1, CANTNetCount net2, int synapsesPerNeuron) {
  	int rulesSize = net2.size();
  	int internalSize = net1.size();
  	for (int neuronIndex = 0; neuronIndex < internalSize; neuronIndex++){
  	  for (int newConnection = 0; newConnection < synapsesPerNeuron; newConnection++){
  	    int toNeuron = (int)(Math.random()*rulesSize);
  	    double weight  = net1.getInternalRulesWeight(neuronIndex,toNeuron);
  	    net1.neurons[neuronIndex].addConnection(net2.neurons[toNeuron],weight);
  	  }
  	}
  }
  
  private static void connectRulesInternal(CANTNetCount rulesNet, 
                                           CANTNetCount internalNet,
										   int synapsesPerNeuron) {
  	int rulesSize = rulesNet.size();
  	int internalSize = internalNet.size();

  	for (int neuronIndex = 0; neuronIndex < rulesSize; neuronIndex++){
  	  for (int newConnection = 0; newConnection < synapsesPerNeuron; newConnection++){
  	    int toNeuron = (int)(Math.random()*internalSize);
  	    double weight  = rulesNet.getRulesInternalWeight(neuronIndex,toNeuron);
  	    rulesNet.neurons[neuronIndex].addConnection(internalNet.neurons[toNeuron],weight);
  	  }
  	}
  }

  private static void connectRulesDone(CANTNetCount ruleNet, CANTNetCount doneNet, int synapsesPerNeuron) {
    int rulesSize = ruleNet.size();
    int doneSize = doneNet.size();
    for (int neuronIndex = 0; neuronIndex < rulesSize; neuronIndex++){
      for (int newConnection = 0; newConnection < synapsesPerNeuron; newConnection++){
        int toNeuron = (int)(Math.random()*doneSize);
        double weight;
		if (ruleNet.neurons[neuronIndex].getInhibitory())
		  weight = -0.01;
		else
		  weight = 0.5;  
        ruleNet.neurons[neuronIndex].addConnection(doneNet.neurons[toNeuron],weight);
      }
    }
  }
  
  private static void connectDoneRules(CANTNetCount doneNet, CANTNetCount rulesNet, int synapsesPerNeuron) {
    int rulesSize = rulesNet.size();
    int doneSize = doneNet.size();
    for (int neuronIndex = 0; neuronIndex < doneSize; neuronIndex++){
      for (int newConnection = 0; newConnection < synapsesPerNeuron; newConnection++){
        int toNeuron = (int)(Math.random()*rulesSize);
        double weight;
      	if (doneNet.neurons[neuronIndex].getInhibitory())
  	      weight = -0.5;
  	    else
  	      weight = 0.01;  
        doneNet.neurons[neuronIndex].addConnection(rulesNet.neurons[toNeuron],weight);
      }
    }
  }
  
  private static void connectDoneInput(CANTNetCount doneNet, CANTNetCount inputNet, int synapsesPerNeuron) {
    int inputSize = inputNet.size();
    int doneSize = doneNet.size();
    for (int neuronIndex = 0; neuronIndex < doneSize; neuronIndex++){
      for (int newConnection = 0; newConnection < synapsesPerNeuron; newConnection++){
        int toNeuron = (int)(Math.random()*inputSize);
        double weight;
      	if (doneNet.neurons[neuronIndex].getInhibitory())
  	      weight = -1.0;
  	    else
  	      weight = 0.01;  
        doneNet.neurons[neuronIndex].addConnection(inputNet.neurons[toNeuron],weight);
      }
    }
  }

  private static void connectResetInternal(CANTNetCount resetNet, CANTNetCount internalNet, int synapsesPerNeuron) {
    int resetSize = resetNet.size();
    int internalSize = internalNet.size();
    for (int neuronIndex = 0; neuronIndex < resetSize; neuronIndex++){
      for (int newConnection = 0; newConnection < synapsesPerNeuron; newConnection++){
        int toNeuron = (int)(Math.random()*internalSize);
        double weight  = resetNet.getResetInternalWeight(neuronIndex,toNeuron);
        resetNet.neurons[neuronIndex].addConnection(internalNet.neurons[toNeuron],weight);
      }
    }
  }

  private static void connectFinishReset(CANTNetCount finishNet, CANTNetCount resetNet, int synapsesPerNeuron) {
    int finishSize = finishNet.size();
    int resetSize = resetNet.size();
    for (int neuronIndex = 0; neuronIndex < resetSize; neuronIndex++){
      for (int newConnection = 0; newConnection < synapsesPerNeuron; newConnection++){
        int toNeuron = (int)(Math.random()*resetSize);
        double weight;
      	if (finishNet.neurons[neuronIndex].getInhibitory())
  	      weight = -1.0;
  	    else
  	      weight = 0.01;  
        finishNet.neurons[neuronIndex].addConnection(resetNet.neurons[toNeuron],weight);
      }
    }
  }

  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
  }
  
  private void printCAsActive(int currentStep, String netName) {
  	int neuronsActive;
  	CANTNet net = getNet(netName);
	
	for (int CANum = 0 ; CANum < 11; CANum ++) 
	  {
	  neuronsActive = 0;
	  for (int neuronNum = CANum*200; 
	           neuronNum < ((CANum+1) * 200);
	           neuronNum ++)
	    {
	    if (net.neurons[neuronNum].getFired())
		  neuronsActive++;
	    }
	  if (neuronsActive > 0)
	    System.out.println(netName + "Active "+ (CANum+1) + 
		  " " + neuronsActive + " " + currentStep);
	  }
  }
  
  private void printTotalConnectionStrength (String netName) {
    CANTNet net = getNet(netName);
	for (int cNeuron =0; cNeuron < net.getTotalNeurons(); cNeuron++) 
	  {
	  System.out.println(netName + " " + cNeuron + " "+ 
	    net.neurons[cNeuron].getTotalConnectionStrength());
	  }
  }

  private void printActivation (String netName) {
    CANTNet net = getNet(netName);
    //for (int cNeuron =0; cNeuron < net.getTotalNeurons(); cNeuron++) 
    for (int cNeuron =0; cNeuron < 605; cNeuron++) 
      {
      System.out.println(netName + " " + cNeuron + " "+ 
       net.neurons[cNeuron].getActivation());
      }
  }

  public void measure(int currentStep) {
 	if (currentStep  == 2000) 
	  {
	  //printTotalConnectionStrength("finish");
	  //printTotalConnectionStrength("bind");
	  }
  	   
  	//if ((currentStep % 400) == 50)
      //printCAsActive(currentStep,"rules");
      //printCAsActive(currentStep,"internal");
  }

  public boolean isEndEpoch(int currentStep) {
    if ((currentStep % 50) == 0) return (true);
	else return (false);
  
  }  
  
  private boolean start (int currentStep) {
  	CANTNet bindNet = getNet("bind");
  	if (currentStep == 0) 
  	  {
  	  bindNet.setSpontaneousActivationOn(true);
      return (true);
      }
	else return (false);
  }
  
  int WarmUpSteps = 2000; 
  
  private boolean warmUp (int currentStep) {

  	if (currentStep >= WarmUpSteps)
	  return (false);
	  
    CANTNet finishNet = getNet("finish");
    CANTNet bindNet = getNet("bind");
	
	finishNet.setCyclesPerRun(50);
	bindNet.setCyclesPerRun(50);	
	  
	if ((currentStep >= 400) && ((currentStep %100) == 0))
  	  {
  	  bindNet.setSpontaneousActivationOn(false);
  	  bindNet.setNeuronsToStimulate(50);
  	  finishNet.setNeuronsToStimulate(50);
  	  bindNet.setCurrentPattern(1);
  	  }
  	else if ((currentStep >= 400) && ((currentStep %100) == 50))
  	  {
  	  finishNet.setNeuronsToStimulate(0);
  	  bindNet.setNeuronsToStimulate(50);
  	  bindNet.setCurrentPattern(0);
  	  }
  	return (true);
  }

  private boolean testRule (int currentStep) {
	CANTNet inputNet = getNet("BaseNet");
	CANTNet internalNet = getNet("internal");
	CANTNet finishNet = getNet("finish");
	CANTNet bindNet = getNet("bind");	
	CANTNet resetNet = getNet("reset");	
	
	currentStep = currentStep - WarmUpSteps;

	//bind F to 8
	if (currentStep == 0) 
	  {
	  finishNet.setCyclesPerRun(200);
	  bindNet.setCyclesPerRun(200);
	  //CANT23.setRunning(false);
	  System.out.println("binding " + inputNet.getCurrentPattern());
	  inputNet.setCurrentPattern(12);
	  finishNet.setNeuronsToStimulate(50);
	  inputNet.setNeuronsToStimulate(50);
	  bindNet.setNeuronsToStimulate(0);
	  }
	else if (currentStep == 50) 
	  {
	  System.out.println("stillbinding ");
	  finishNet.setNeuronsToStimulate(0);
	  inputNet.setNeuronsToStimulate(0);
	  //probably need to select the input pattern
	  }

	//run 2 to 8
	else if (currentStep == 200) 
	  {
	  //test binding (run 8)
	  System.out.println("test counting ");
	  inputNet.setCurrentPattern(4);
	  inputNet.setNeuronsToStimulate(150);
	  resetNet.setNeuronsToStimulate(50);
	  CANT23.setRunning(false);
	  }
	else if (currentStep == 400) 
	  {
	  //test binding (run 8)
	  System.out.println("Unbinding");
	  inputNet.setNeuronsToStimulate(0);
	  resetNet.setNeuronsToStimulate(0);
	  internalNet.setSpontaneousActivationOn(true);
	  bindNet.setSpontaneousActivationOn(true);
	  CANT23.setRunning(false);
	  }
	else if (currentStep == 600) 
	  {
	  //test binding (run 8)
	  System.out.println("Test Unbinding");
	  inputNet.setCurrentPattern(12);
	  inputNet.setNeuronsToStimulate(50);
	  internalNet.setSpontaneousActivationOn(false);
	  bindNet.setSpontaneousActivationOn(false);
	  CANT23.setRunning(false);
	  }
	/*
	else 
	   System.out.println("done binding ");
	*/
	
	return (true);
  }  

  private void resetAllNets() {
    CANTNet inputNet = getNet("BaseNet");
    CANTNet internalNet = getNet("internal");
    CANTNet rulesNet = getNet("rules");
    CANTNet doneNet = getNet("done");
    CANTNet bindNet = getNet("bind");
    CANTNet finishNet = getNet("finish");
    CANTNet resetNet = getNet("reset");	

    internalNet.clear();
    inputNet.clear();
    rulesNet.clear();
    doneNet.clear();
    finishNet.clear();
    bindNet.clear();
    resetNet.clear();
  }

  
  public void endEpoch () {
	
	int currentStep = CANT23.CANTStep;

	if ((currentStep < WarmUpSteps) || 
	   ((currentStep >= WarmUpSteps) && ((currentStep %200) == 0))) 
	
	  {
	  resetAllNets();
	  }
	
	if (start(currentStep))
	  return;
	//warm up (train F and B)
	else if (warmUp(currentStep))
      return;  
	else if (testRule(currentStep))
	  return;  
  }
  
  public void printExpName () {
    System.out.println("count ");
  }
}