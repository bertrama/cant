
import java.io.*;
import java.util.*;

public class HierExperiment extends CANTExperiment {
  CANTNet parentNet;

  public HierExperiment (CANTNet net) {
  	parentNet = net;
    trainingLength = 800; 
	inTest = false;
    parentNet.setCyclesPerRun(1);     
  }
  
  //This is the test we do of a single network to
  //get pearsons values
  private boolean MammalTest = true;
  
  public boolean experimentDone (int Step) {
  	if (MammalTest) 
	{
       if (Step == 1250) {
         parentNet.getNewPatterns("hier/Mammal.xml");
         parentNet.setNeuronsToStimulate(48);
		 
	     return (false);
       }
       if (Step > 2000) return (true);
	   else return (false);  
	}
    if (Step > 1300) return (true);
	else return (false);  
  }
    
  public void switchToTest () {
//System.out.println("Switch To Test H1");
     parentNet.setChangeEachTime(false);
	 parentNet.setLearningOn(false);     
	 parentNet.setCyclesPerRun(50);     
	 inTest = true;
	 parentNet.setRecordingActivation(true);
  }

  public void printExpName () {
    System.out.println("hier "+trainingLength);
  }

  private void compare (int measure1, int measure2)  {
    double result;
    parentNet.measure.setMeasure1(measure1);
    parentNet.measure.setMeasure2(measure2);
    result = parentNet.measure.Measure();
	System.out.println(measure1+" "+measure2+" "+ result);
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
	
	for (int testPattern = 805; testPattern < 1250; testPattern+=50) 
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

/*
	if (((nearestPattern-805)%150) == 0) 
	  System.out.println("Rat");
	else if (((nearestPattern-805)%150) == 50)
	  System.out.println("Dog");
	else if (((nearestPattern-805)%150) == 100)
      System.out.println("Cat");
    else 
      System.out.println("error"+nearestPattern);
*/
	
	if (((nearestPattern-805)%150) == ((patternCycle -805)%150))
	  return (1);
	else
	  return (0);  
  }
  
  private int categorisePatterns5Steps() {
  	int correctlyCategorised = 0;
  	for (int i = 0; i< 9; i++)
	  correctlyCategorised += categorisePattern(805+(50*i),1);
	
	return (correctlyCategorised);  
  }

  private int categorisePatternsAverages() {
  	int correctlyCategorised = 0;
  	for (int i = 0; i< 9; i++)
      correctlyCategorised += categorisePattern(805+(50*i),2);
	  
    return (correctlyCategorised);  
  }

  private void measureLong(int currentStep) {
    if (currentStep == 1500) {
      compare(805,855); //rd
      compare(805,905); //rc
      compare(805,955); //rr
      compare(805,1005);//rd
      compare(805,1055);//rc
      compare(805,1105);//rr
      compare(805,1155);//rd
      compare(805,1205);//rc
      compare(855,905); 
      compare(855,955); 
      compare(855,1005);
      compare(855,1055);
      compare(855,1105);
      compare(855,1155);
      compare(855,1205);
      compare(905,955); 
      compare(905,1005);
      compare(905,1055);
      compare(905,1105);
      compare(905,1155);
      compare(905,1205);
      compare(955,1005);
      compare(955,1055);
      compare(955,1105);
      compare(955,1155);
      compare(955,1205);
      compare(1005,1055);
      compare(1005,1105);
      compare(1005,1155);
      compare(1005,1205);
      compare(1055,1105);
      compare(1055,1155);
      compare(1055,1205);
      compare(1105,1155);
      compare(1105,1205);
      compare(1155,1205);
	  
      System.out.println("Averages");
	  
      System.out.println("805 855  "+ compareAverage(805,855)); 
      System.out.println("805 905 "+ compareAverage(805,905)); 
      System.out.println("805 955 "+ compareAverage(805,955)); 
      System.out.println("805 1005 "+ compareAverage(805,1005)); 
      System.out.println("805 1055 "+ compareAverage(805,1055)); 
      System.out.println("805 1105 "+ compareAverage(805,1105)); 
      System.out.println("805 1155 "+ compareAverage(805,1155));
      System.out.println("805 1205 "+ compareAverage(805,1205)); 
      System.out.println("855 905 "+ compareAverage(855,905)); 
      System.out.println("855 955 "+ compareAverage(855,955)); 
      System.out.println("855 1005 "+ compareAverage(855,1005)); 
      System.out.println("855 1055 "+ compareAverage(855,1055)); 
      System.out.println("855 1105 "+ compareAverage(855,1105)); 
      System.out.println("855 1155 "+ compareAverage(855,1155));
      System.out.println("855 1205 "+ compareAverage(855,1205)); 
      System.out.println("905 955 "+ compareAverage(905,955)); 
      System.out.println("905 1005 "+ compareAverage(905,1005)); 
      System.out.println("905 1055 "+ compareAverage(905,1055)); 
      System.out.println("905 1105 "+ compareAverage(905,1105)); 
      System.out.println("905 1155 "+ compareAverage(905,1155));
      System.out.println("905 1205 "+ compareAverage(905,1205)); 
      System.out.println("955 1005 "+ compareAverage(955,1005)); 
      System.out.println("955 1055 "+ compareAverage(955,1055)); 
      System.out.println("955 1105 "+ compareAverage(955,1105)); 
      System.out.println("955 1155 "+ compareAverage(955,1155));
      System.out.println("955 1205 "+ compareAverage(955,1205)); 
      System.out.println("1005 1055 "+ compareAverage(1005,1055)); 
      System.out.println("1005 1105 "+ compareAverage(1005,1105)); 
      System.out.println("1005 1155 "+ compareAverage(1005,1155));
      System.out.println("1005 1205 "+ compareAverage(1005,1205)); 
      System.out.println("1055 1105 "+ compareAverage(1055,1105)); 
      System.out.println("1055 1155 "+ compareAverage(1055,1155));
      System.out.println("1055 1205 "+ compareAverage(1055,1205)); 
      System.out.println("1105 1155 "+ compareAverage(1105,1155));
      System.out.println("1105 1205 "+ compareAverage(1105,1205)); 
      System.out.println("1155 1205 "+ compareAverage(1155,1205)); 
	  
	  
      System.out.println("Mammal 5s");
      compare(1255,1305);
      compare(1255,1355);
      compare(1255,805);
      compare(1255,855);
      compare(1255,905);
      compare(1305,1355);
      compare(1305,805);
      compare(1305,855);
      compare(1305,905);
      compare(1355,805);
      compare(1355,855);
      compare(1355,905);

      System.out.println("Mammal Averages");
      System.out.println("1255 1305 "+ compareAverage(1255,1305)); 
      System.out.println("1255 1355 "+ compareAverage(1255,1355)); 
      System.out.println("1255 805 "+ compareAverage(1255,805)); 
      System.out.println("1255 855 "+ compareAverage(1255,855)); 
      System.out.println("1255 905 "+ compareAverage(1255,905)); 
      System.out.println("1305 1355 "+ compareAverage(1305,1355)); 
      System.out.println("1305 805 "+ compareAverage(1305,805)); 
      System.out.println("1305 855 "+ compareAverage(1305,855)); 
      System.out.println("1305 905 "+ compareAverage(1305,905)); 
      System.out.println("1355 805 "+ compareAverage(1355,805)); 
      System.out.println("1355 855 "+ compareAverage(1355,855)); 
      System.out.println("1355 905 "+ compareAverage(1355,905)); 
    }
  }
  
  private void measureShort(int currentStep) {
    System.out.println("5s " +   categorisePatterns5Steps());
    System.out.println("Averages " +  categorisePatternsAverages());
  }

  private float calculateAverageWeight(int fromCat, int toCat) {
  	int fromStart = fromCat*80;
	int toStart = toCat*80;
	float result =(float) 0.0;
	int numberSynapses = 0;
	int toID;
	double weight;
	
	for (int fromNeuron = fromStart; fromNeuron<fromStart+80;fromNeuron++) 
	{
	  for (int fromSynapse = 0; 
	           fromSynapse <parentNet.neurons[fromNeuron].getCurrentSynapses() ;
	           fromSynapse++) 
	  {
	    toID = parentNet.neurons[fromNeuron].synapses[fromSynapse].toNeuron.id;
		if ((toID >= toStart) && (toID < (toStart+80)))
        {
		  weight = parentNet.neurons[fromNeuron].synapses[fromSynapse].getWeight();
		  if (weight > 0.0) 
		  {
		  	result += weight;
			numberSynapses ++;
		  }
		}
	  }
	  
	}
    return (float) (result/numberSynapses);
  }

  private void printWeightMatrix() {
    System.out.println("Weight Matrix ");
	for (int from = 0; from <= 9; from ++) 
	{
	  for (int to = 0; to <= 9; to ++) 
	  {
	     float averageWeight=calculateAverageWeight(from,to);
	     System.out.print(averageWeight + " ");
	  }
	  System.out.println(" ");
	}
  }

  public void measure(int currentStep) {
    if (MammalTest) 
	{
	  if (currentStep == 800)
	  	printWeightMatrix();
	  else if (currentStep == 1250)
	    parentNet.getNewPatterns("hier/Mammal.xml");
	  else if (currentStep == 1500) 
        measureLong(currentStep);
	}
	else 
      if (currentStep == 1250) 
        measureShort(currentStep);
  }
  
  
  public boolean isEndEpoch(int Cycle) {
  	if (Cycle < trainingLength) return (true);
    return (false);
  }
  
}
	