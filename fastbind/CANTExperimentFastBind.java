//run multiple tests
import java.io.*;
import java.util.*;

public class CANTExperimentFastBind extends CANTExperiment {

  public CANTExperimentFastBind () {
    trainingLength = 3000; 
	inTest = false;
  }

  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
  }
  
  public boolean isEndEpoch(int currentStep) {
    CANTNetFastBind inputNet = (CANTNetFastBind)getNet("BaseNet");
    CANTNetFastBind otherNet = (CANTNetFastBind)getNet("OtherNet");
  	if (currentStep == trainingLength/2) 
	  {
	  inputNet.setNeuronsToStimulate(0);
	  otherNet.setNeuronsToStimulate(50);
	  }
	else if (currentStep == trainingLength) 
	{
	  inputNet.setCurrentPattern(1);
	  inputNet.setNeuronsToStimulate(50);
	  otherNet.setCurrentPattern(1);
	  otherNet.setNeuronsToStimulate(50);
	}
	else if (currentStep == (trainingLength+50)) 
	{
	  inputNet.setCurrentPattern(1);
	  otherNet.setNeuronsToStimulate(0);
	}
	
	if ((currentStep % 50) == 0) return (true);
    return (false);
  }  
  
  private void printPatternActivities() {
    int neuronsFiredPerPattern[] = new int [10];
    CANTNetFastBind otherNet = (CANTNetFastBind)getNet("OtherNet");

    for (int pattern = 0; pattern < 10; pattern ++) 
    {
    for (int neuron = pattern * 200; neuron < (pattern*200) + 200; neuron ++)
      if (otherNet.neurons[neuron].getFired())
        neuronsFiredPerPattern[pattern] ++;
      System.out.println("pattern " + pattern  + " " + neuronsFiredPerPattern[pattern]);
    }
  }
  
  
  public void measure(int currentStep) {
    if ((currentStep == 3075) || (currentStep == 3275)  || (currentStep == 3575)) 
      {
	  System.out.println("At Step " + currentStep);
	  printPatternActivities();	
      }
  }


  public void printExpName () {
    System.out.println("Fast Bind");
  }

}