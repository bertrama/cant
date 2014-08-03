//run multiple tests
import java.io.*;
import java.util.*;

public class CANTExperimentParse extends CANTExperiment {

  public CANTExperimentParse () {
    trainingLength = 0; 
	inTest = false;
  }

  //the push rule changes the input if it fires.
  //The next work is given to input when the push rule stops firing.
  int cyclePushLastStarted = 0;
  int cyclePushLastFinished = 0;
  private boolean pushRuleOn(int cycle) {
  	CANTNetParse1 pushNet = (CANTNetParse1)getNet("PushNet");
	int pushNeuronsOn=0;
	if (cycle-10 < cyclePushLastFinished) return false;
	
	for (int i = 0; i < 1200; i++)
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

  public boolean isEndEpoch(int currentStep) {
    CANTNetParse1 inputNet = (CANTNetParse1)getNet("BaseNet");
    CANTNetParse1 verbNet = (CANTNetParse1)getNet("VerbNet");
    CANTNetParse1 stackTopNet = (CANTNetParse1)getNet("StackTopNet");
    CANTNetParse1 stackNet = (CANTNetParse1)getNet("StackNet");

  	if (currentStep == 0) 
	  {
	  stackTopNet.setNeuronsToStimulate(40);
	  inputNet.setNeuronsToStimulate(0);
	  }
	else if (currentStep == 10) 
	  {
	  stackTopNet.setNeuronsToStimulate(0);
	  inputNet.setNeuronsToStimulate(50);
	  }
	else if (pushRuleOn(currentStep))
	  {
	    inputNet.readNextWord();
	    System.out.println("Next Word Read " + currentStep);
	  }
    return (false);
  }  
  
  
  public void printExpName () {
    System.out.println("Parse ");
  }

}