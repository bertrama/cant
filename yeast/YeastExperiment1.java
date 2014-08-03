
import java.io.*;
import java.util.*;

public class YeastExperiment extends CANTExperiment {
  CANTNet parentNet;

  public YeastExperiment (CANTNet net) {
  	parentNet = net;
    trainingLength = 500; 
	inTest = false;
  }
  
    
  public void switchToTest () {
System.out.println("Switch To Yeast Test");
	 parentNet.setLearningOn(false);         
	 inTest = true;
	 parentNet.setRecordingActivation(true);
	 parentNet.setChangeEachTime(false);
	 parentNet.getNewPatterns("yeast/test0.xml");
  }
  
  private void measureCategory () {
    int neuronsFiredPerPattern[] = new int [10];
    for (int pattern = 0; pattern < 10; pattern ++) 
      {
        neuronsFiredPerPattern[pattern] = 0;
        for (int neuron = (pattern * 100)+800; neuron < (pattern*100) + 900; neuron ++) 
		{
          if (parentNet.neurons[neuron].getFired())
            neuronsFiredPerPattern[pattern] ++;
		}	
        System.out.println("pattern " + pattern  + " " + neuronsFiredPerPattern[pattern]);
      }
  	}
  
  public void measure(int currentStep) {
  	if ((currentStep % 50) == 15) 
	{
System.out.println("Measure " + currentStep);
      measureCategory();
	}
}  

  public void printExpName () {
    System.out.println("Yeast "+trainingLength);
  }
  
}
	