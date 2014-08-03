
import java.io.*;
import java.util.*;

public class HierExperiment2 extends CANTExperiment {
  CANTNet parentNet;

  double netCorrelations[];
  int indexCorrelations = 0;
  private int version = 3; //1 just base test, 2 base and sup, 3 bear

  public HierExperiment2 (CANTNet net) {
  	parentNet = net;
    trainingLength = 1400; 
	inTest = false;
	netCorrelations = new double[300];
	parentNet.setCyclesPerRun(1);     
	
  }
  
  public boolean experimentDone (int step) {
  	if (version == 1) 
	{
  	  if (step > 2200) return (true);
	  else return (false);  
	}
	else if (version == 2) 
	{
	  if (step > 3100) return (true);
	  else return (false); 
	} 
	else if (version == 3) 
	{
      if (step > 1900) return (true);
      else return (false); 
	} 
	System.out.println("Error: bad version ");
	return (false);
  }
  
  public void switchToTest () {
     parentNet.setChangeEachTime(false);
	 parentNet.setLearningOn(false);     
	 inTest = true;
	 parentNet.setRecordingActivation(true);
	 parentNet.setCyclesPerRun(50);     
	 if (version == 3) 
       parentNet.getNewPatterns("hier/bear.xml");
  }

  public void printExpName () {
    System.out.println("hier2 "+trainingLength);
  }

  
  //compare two networks and store the measure
  private void compare (int measure1, int measure2)  {
    double result;
    parentNet.measure.setMeasure1(measure1);
    parentNet.measure.setMeasure2(measure2);
    result = parentNet.measure.Measure();
	netCorrelations[indexCorrelations++] = result;
	//System.out.println(measure1+" "+measure2+" "+ result+"\n");
  }

  private void showBaseResults ()  {
     double valueLargest;
     int indexLargest;
     for (int baseCat = 0; baseCat < 15; baseCat++){
	 	valueLargest = 0.0;
		indexLargest = 0;
		for (int compareCat = 0; compareCat < 15; compareCat++) {
			if ((netCorrelations[baseCat*15+compareCat] > valueLargest) &&
			   (netCorrelations[baseCat*15+compareCat] < 1.0))
		    {
				indexLargest = compareCat;
				valueLargest = netCorrelations[baseCat*15+compareCat];		
			}
		}
        System.out.println(baseCat+" "+indexLargest+" "+valueLargest+"\n");
		if ((indexLargest % 5) == 0)
			System.out.println("Dog\n");
		else if ((indexLargest % 5) == 1)
		    System.out.println("Cat\n");
		else if ((indexLargest % 5) == 2)
		    System.out.println("Rat\n");
		else if ((indexLargest % 5) == 3)
		    System.out.println("Goose\n");
		else if ((indexLargest % 5) == 4)
	 	    System.out.println("Pigeon\n");
	 }
  }
  
  private void showSuperResults ()  {
    double valueLargest;
    int indexLargest = 0;;
    for (int baseCat = 0; baseCat < 9; baseCat++){
  	  valueLargest = 0.0;
      indexLargest = 0;
      for (int compareCat = 0; compareCat < 9; compareCat++) {
  	    if ((netCorrelations[baseCat*9+compareCat] > valueLargest) &&
  	       (netCorrelations[baseCat*9+compareCat] < 1.0))
          {
  		  indexLargest = compareCat;
  		  valueLargest = netCorrelations[baseCat*9+compareCat];		
  	      }
      }
      //System.out.println(baseCat+" "+indexLargest+" "+valueLargest+"\n");
      if ((indexLargest % 3) == 0)
        System.out.println("Animal\n");
      else if ((indexLargest % 3) == 1)
        System.out.println("Mammal\n");
      else if ((indexLargest % 3) == 2)
        System.out.println("Bird\n");
    }
  }
  
  private void printOmnivore () {
  	int numOmnivore = 0;
  	int numCarnivore = 0;
  	int numHerbivore = 0;
  	for (int i = 0; i < 40; i ++) {
	  if (parentNet.neurons[i+520].getFired()) numOmnivore++;
  	  if (parentNet.neurons[i+560].getFired()) numCarnivore++;
  	  if (parentNet.neurons[i+600].getFired()) numHerbivore++;
  	  }
    System.out.println(numOmnivore + " "+numCarnivore + " "+numHerbivore + " "+"\n");
  }
  
  private double compareAverage (int measure1, int measure2)  {
  	double average = 0.0;
    double result;
    int time1, time2;
    for (int i = 10; i < 35; i++) 
    {
      time1 = measure1+i;
      time2 = measure2+i;
      parentNet.measure.setMeasure1(time1);
      parentNet.measure.setMeasure2(time2);
      result =  parentNet.measure.Measure();
      if (Double.isNaN(result)) 
      {
        //System.out.println(time1+" "+time2+" "+ result);
  	    result = 0;
      }
      average +=  result;
    }
    average = average/24;

  return average;
  }
  
  
  //returns 1 if correctly categorised 0 if not
  private int categorisePattern(int patternCycle,int measureType) {
    int nearestPattern = 0;
    double nearestValue = -1.0;
    double result = -1.0;
    parentNet.measure.setMeasure1(patternCycle);
  
    for (int testPattern = 1405; testPattern < 2200; testPattern+=50) 
    {
      if (testPattern != patternCycle) 
        {
        parentNet.measure.setMeasure2(testPattern);
  	    if (measureType == 1)
          result = parentNet.measure.Measure();
  	    else  if (measureType == 2)
  	      result = compareAverage(patternCycle,testPattern);
  	  
        if (result > nearestValue) 	
  	      {
          nearestValue = result;
  	      nearestPattern=testPattern;
  	      }	
        }	
     }

  
    if (((nearestPattern-1405)%250) == ((patternCycle -1405)%250))
      return (1);
    else
      return (0);  
  }
  
  
  //returns 1 if correctly categorised 0 if not
  private int categoriseSupPattern(int patternCycle,int measureType) {
    int nearestPattern = 0;
    double nearestValue = -1.0;
    double result = -1.0;
    parentNet.measure.setMeasure1(patternCycle);
  
    for (int testPattern = 2205; testPattern < 3050; testPattern+=50) 
    {
      if (testPattern != patternCycle) 
        {
        parentNet.measure.setMeasure2(testPattern);
  	    if (measureType == 1)
          result = parentNet.measure.Measure();
  	    else  if (measureType == 2)
  	      result = compareAverage(patternCycle,testPattern);
  	  
        if (result > nearestValue) 	
  	      {
          nearestValue = result;
  	      nearestPattern=testPattern;
  	      }	
        }	
     }

  
    if (((nearestPattern-2255)%150) == ((patternCycle -2255)%150))
      return (1);
    else
      return (0);  
  }

  private int categorisePatterns5Steps() {
    int correctlyCategorised = 0;
    for (int i = 0; i< 15; i++)
      correctlyCategorised += categorisePattern(1405+(50*i),1);
  
    return (correctlyCategorised);  
  }

  private int categoriseSupPatterns(int type) {
    int correctlyCategorised = 0;
    for (int i = 0; i< 15; i++) 
	{
      correctlyCategorised += categoriseSupPattern(2255+(50*i),type);
	}    
  
    return (correctlyCategorised);  
  }

  private int categorisePatternsAverages() {
    int correctlyCategorised = 0;
    for (int i = 0; i< 15; i++)
      correctlyCategorised += categorisePattern(1405+(50*i),2);
  
    return (correctlyCategorised);  
  }

  private void printEatNeurons(int step) {
    int carnNeurons = 0;
    int herbNeurons = 0;
    int omniNeurons = 0;
	
	for (int i=0; i< 80; i++) {
	  if (parentNet.neurons[1040+i].getFired())
	    omniNeurons++;   
	  if (parentNet.neurons[1120+i].getFired())
	    carnNeurons++;   
	  if (parentNet.neurons[1200+i].getFired())
	    herbNeurons++;   
	}
	
     System.out.println(omniNeurons + " " + carnNeurons + " " +
	                    herbNeurons + " " + step);
  }
  
  public void measure(int currentStep) {
    if ((version == 3) && ((currentStep % 50) == 5))
       printEatNeurons(currentStep);
	     
  	//train for 1400 + (5types of pattern 3 times 50 cycles each)=750
    if (currentStep == 2200) {
	   System.out.println("correct 5s" + categorisePatterns5Steps());
       System.out.println("correct Averages" + categorisePatternsAverages());
    }
	
    else if (currentStep == 2249) {
      parentNet.getNewPatterns("hier/Pattern1.xml");
      //parentNet.getNewPatterns("Pattern2.xml");
//      parentNet.setNeuronsToStimulate(32);
      System.out.println("pattern switch"+"\n");
    }
	
	//Check higher order category
    else if (currentStep == 3050) {
      System.out.println("correct 5s" + categoriseSupPatterns(1));
      System.out.println("correct Averages" + categoriseSupPatterns(2));
    }
	
  /*check for omnivore
  else if ((currentStep > 1800)  && ((currentStep%20) == 5)
       && (currentStep < 2000)){
  	printOmnivore();
    }
	*/
  }
}
	