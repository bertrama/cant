
import java.io.*;
import java.util.*;

public class YeastExperiment extends CANTExperiment {
  CANTNet parentNet;
  int neuronsFiredPerPattern[] = new int [10];
  int totalFired[] = new int [10];
  int category = 0; 
  
  

  public YeastExperiment (CANTNet net) {
  	parentNet = net;
    trainingLength = 2000;//1482*2;
    inTest = false;
  }
  
    
  public void switchToTest () {
System.out.println("Switch To Yeast Test");
	 parentNet.setLearningOn(false);         
	 inTest = true;
	 parentNet.setRecordingActivation(true);
	 parentNet.setChangeEachTime(false);
	 
	 parentNet.setCyclesPerRun(50);
	 
	 float newFat = Float.parseFloat("0.1");
	 //parentNet.setFatigueRate(newFat);
	 
	 CANT23.delayBetweenSteps = 50;
	 parentNet.getNewPatterns("yeast/test0.xml");
  }
  
  private void measureCategory () {
    int neuronsFiredPerPattern[] = new int [10];
    
    for (category = 0; category < 10; category ++) 
      {
        neuronsFiredPerPattern[category] = 0;
        for (int neuron = (category * 100)+800; neuron < (category*100) + 900; neuron ++) 
		{
          if (parentNet.neurons[neuron].getFired())
          {
          	neuronsFiredPerPattern[category] ++;
          	totalFired[category] ++;
          }
            
		}	
		
        //System.out.println("Category " + category  + " " + neuronsFiredPerPattern[category]);
      }
  	}
  
  private String catName (int num)
  {
  	String catName = "";
  
  	if (num == 1)
	{
		catName = "CYT";
	}
	else if (num == 2)
	{
		catName = "NUC";
	}
	else if (num == 3)
	{
		catName = "MIT";
	}
	else if (num == 4)
	{
		catName = "ME3";
	}
	else if (num == 5)
	{
		catName = "ME2";
	}
	else if (num == 6)
	{
		catName = "ME1";
	}
	else if (num == 7)
	{
		catName = "EXC";
	}
	else if (num == 8)
	{
		catName = "VAC";
	}
	else if (num == 9)
	{
		catName = "POX";
	}
	else if (num == 10)
	{
		catName = "ERL";
	}
	return catName;
  }
  
  public void measure(int currentStep) {
  	  	
  	if (((currentStep % 50) >= 5) && ((currentStep % 50) <= 10))
	{		
		//System.out.println("Measure " + currentStep);
      	measureCategory();
	}
	else if ((currentStep % 50) == 16)
	{
		System.out.println("The total neurons of all categories");
		for (int count = 0; count <10; count++)
		{
			System.out.println("Category - "+catName(count+1)+"  = " +totalFired[count]);
		}
		// finding max
		int max = totalFired[0];
		int cat = 0;
		if (totalFired[0]>0)
		{
			cat = 1;
		}
		
		for (int numCount = 0; numCount < 9; numCount++)
		{
			if (max < totalFired[numCount + 1])
			{
				max = totalFired[numCount + 1];
				cat = (numCount + 2);
			}
	
		}
		System.out.println("Maximum number is - "+max);
		System.out.println("Pattern - "+parentNet.getCurrentPattern());
		if (cat==0)
		{
			System.out.println("Category is unknown - no category neurons were fired");
			System.out.println("");
		}
		else
		{
			System.out.println("Category is - "+catName(cat));	
			System.out.println("");
		}
	}
	else if ((currentStep % 50) > 16)
	{
		for (int count = 0; count < 10; count++)
		{
			totalFired[count] = 0;	
		}
		
	}
  /*	if ((currentStep % 50) == 5) 
	{
System.out.println("Measure " + currentStep);
      measureCategory();
	}*/
}  

  public void printExpName () {
    System.out.println("Yeast "+trainingLength);
  }
  
}
	
    
