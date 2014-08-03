
import java.io.*;
import java.util.*;

public class HierExperiment2 extends CANTExperiment {
  CANTNet parentNet;

  double netCorrelations[];
  int indexCorrelations = 0;

  public HierExperiment2 (CANTNet net) {
  	parentNet = net;
    trainingLength = 1400; 
	inTest = false;
	netCorrelations = new double[300];
  }
  
  public void switchToTest () {
     parentNet.setChangeEachTime(false);
	 parentNet.setLearningOn(false);     
	 inTest = true;
	 parentNet.setRecordingActivation(true);
  }

  public void printExpName () {
    System.out.println("hier "+trainingLength);
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
        //System.out.println(baseCat+" "+indexLargest+" "+valueLargest+"\n");
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
  
  public void measure(int currentStep) {
    if (currentStep == 1700) {
      for (int i = 0; i < 15; i++)
	    for (int j = 0; j< 15; j++)
		  compare(1405+(i*20),1405+(j*20));
    }
	
    else if (currentStep == 1800) {
	  showBaseResults();
      parentNet.getNewPatterns("hier/Pattern1.xml");
      //parentNet.getNewPatterns("Pattern2.xml");
      System.out.println("pattern switch"+"\n");
    }
	
	/*Check higher order category*/
    else if (currentStep == 2000) {
	  indexCorrelations = 0;
      for (int i = 0; i < 9; i++)
        for (int j = 0; j< 9; j++)
	      compare(1805+(i*20),1805+(j*20));
	  showSuperResults();  
    }
	
  /*check for omnivore
  else if ((currentStep > 1800)  && ((currentStep%20) == 5)
       && (currentStep < 2000)){
  	printOmnivore();
    }
	*/
  }
}
	