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
	
	//System.out.println(internalNet.getName));
	//System.out.println(inputNet.getName());
	//System.out.println(rulesNet.getName());
	//System.out.println(bindNet.getName());
	//System.out.println(doneNet.getName());
	//System.out.println(finishNet.getName());
	
	connectInputInternal(inputNet,internalNet,50);
    connectToBind((CANTNetCount)internalNet,bindNet,10);
    connectToBind((CANTNetCount)bindNet,internalNet,15);
    connectFinishToBind((CANTNetCount)finishNet,bindNet,10);
    connectToBind((CANTNetCount)bindNet,finishNet,10);
    connectInternalRules(internalNet, rulesNet,20);	 
    connectRulesInternal(rulesNet,internalNet,60);	 
    connectRulesDone(rulesNet,doneNet,10);
    connectDoneRules(doneNet,rulesNet,30);
    connectDoneInput(doneNet,inputNet,100);
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
 	if ((currentStep %1000) == 999) 
	  {
	  //System.out.println("crhz"); 
	  //printTotalConnectionStrength("internal");
	  //printTotalConnectionStrength("bind");
	  }
	CANTNet inputNet = getNet("BaseNet");
	CANTNet internalNet = getNet("internal");
	CANTNet finishNet = getNet("finish");
	CANTNet bindNet = getNet("bind");
	
	if (currentStep == 0) 
	  {
	  inputNet.setNeuronsToStimulate(0);  
	  finishNet.setSpontaneousActivationOn(true);
	  bindNet.setSpontaneousActivationOn(true);
	  }
	else if (currentStep >= 6000)
	  {
	  if ((currentStep % 100) == 50)
	    {
        finishNet.setSpontaneousActivationOn(true);
	    bindNet.setSpontaneousActivationOn(true);
	    bindNet.setNeuronsToStimulate(0);
	    finishNet.setNeuronsToStimulate(50);
	    }
	  else if ((currentStep % 100) == 0)
	    finishNet.setNeuronsToStimulate(0);
	  
	  }
	else if ((currentStep >= 400) && ((currentStep %200) == 0))
	  {
	  finishNet.setSpontaneousActivationOn(false);
	  bindNet.setSpontaneousActivationOn(false);
	  bindNet.setNeuronsToStimulate(50);
	  finishNet.setNeuronsToStimulate(50);
	  }
	else if ((currentStep >= 400) && ((currentStep %100) == 50))
	  {
	  //inputNet.setNeuronsToStimulate(50);
	  finishNet.setNeuronsToStimulate(0);
	  bindNet.setNeuronsToStimulate(0);
	  }
	else if ((currentStep >= 400) && ((currentStep %200) == 100))
	  {
	  finishNet.setNeuronsToStimulate(0);
	  bindNet.setNeuronsToStimulate(50);
	  }
/*
	else if (currentStep == 800)
	  {
	  inputNet.setNeuronsToStimulate(50);
	  finishNet.setNeuronsToStimulate(50);
	  }
	else if (currentStep == 1200)
	  {
	  inputNet.setNeuronsToStimulate(50);
	  finishNet.setNeuronsToStimulate(0);
	  }
	else if (currentStep == 2000)
	  {
	  inputNet.setNeuronsToStimulate(150);
	  }
*/
	  	   
  	//if ((currentStep % 400) == 50)
      //printCAsActive(currentStep,"rules");
      //printCAsActive(currentStep,"internal");
  }
  
  public void printExpName () {
    System.out.println("count ");
  }
}