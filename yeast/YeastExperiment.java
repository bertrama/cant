
import java.io.*;
import java.util.*;

public class YeastExperiment extends CANTExperiment {
  CANTNet parentNet;
  int neuronsFiredPerPattern[] = new int [10];
  int totalFired[] = new int [10];
  int category = 0; 
  String correctCat[] = new String [1484];
  String result ="";
  
  // Defining the number of patterns for the program to present and end the program
  int endPatNum = 100;//742;
  
  // variables used to count the results
  int correctAns = 0;
  int totalpatterns = 0;
  
  // SPECIFY THE STARTING PATTERN NUMBER HERE
  int startingValue = 742;
  
  
  
  int CYT,NUC,MIT,ME3,ME2,ME1,EXC,VAC,POX,ERL = 0;
  
  // Turning category counting on (this will count the number of neurons fired at each time step for each category)
  boolean catCountCheck = true;
  String catCounts[] = new String [10];
   

  public YeastExperiment (CANTNet net) {
  	parentNet = net;
    trainingLength = 742;//1484;//*2;
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
	 
	 //CANT23.delayBetweenSteps = 50;
	 parentNet.getNewPatterns("yeast/testDataSet1.xml");
	 
	 	 
	 try
		{
			FileReader file = new FileReader ("yeast/allPatterns.txt");
			BufferedReader buffer=new BufferedReader (file);
			String textline = null;
			int position = 0;
			while ((textline = buffer.readLine()) !=null)
			{
				String tempCat = "";
				tempCat = textline.charAt(0)+"" +textline.charAt(1)+"" +textline.charAt(2);
				correctCat[position] = tempCat;
			
				position ++;
							
			}	
			buffer.close();
			System.out.println("numebr of items in answers array -- "+ position);
			System.out.println("");
			result=result+"\t\n" +("numebr of items in answers array -- "+ position);
			result=result+"\t\n" +("");

			
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		
		

		
		
		//int vecPos = 0;
		
		/*System.out.println("hi im here");
		while(vecPos < 1484)
		{
			System.out.println(correctCat[vecPos]);
			System.out.println("vec positon -- "+vecPos);
			vecPos++;
		}*/
		
	 
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
		if (catCountCheck == true)
		{
			catCounts[category]=catCounts[category]+String.valueOf(neuronsFiredPerPattern[category]+",");
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
  	if (totalpatterns == endPatNum)
  	{
  		System.out.println("Correct CYT answers = " +getCatCount("CYT"));
  		System.out.println("Correct NUC answers = " +getCatCount("NUC"));
  		System.out.println("Correct MIT answers = " +getCatCount("MIT"));
  		System.out.println("Correct ME3 answers = " +getCatCount("ME3"));
  		System.out.println("Correct ME2 answers = " +getCatCount("ME2"));
  		System.out.println("Correct ME1 answers = " +getCatCount("ME1"));
  		System.out.println("Correct EXC answers = " +getCatCount("EXC"));
  		System.out.println("Correct VAC answers = " +getCatCount("VAC"));
  		System.out.println("Correct POX answers = " +getCatCount("POX"));
  		System.out.println("Correct ERL answers = " +getCatCount("ERL"));
  		result=result+"\t\n" +("Correct CYT answers = " +getCatCount("CYT"));
  		result=result+"\t\n" +("Correct NUC answers = " +getCatCount("NUC"));
  		result=result+"\t\n" +("Correct MIT answers = " +getCatCount("MIT"));
  		result=result+"\t\n" +("Correct ME3 answers = " +getCatCount("ME3"));
  		result=result+"\t\n" +("Correct ME2 answers = " +getCatCount("ME2"));
  		result=result+"\t\n" +("Correct ME1 answers = " +getCatCount("ME1"));
  		result=result+"\t\n" +("Correct EXC answers = " +getCatCount("EXC"));
  		result=result+"\t\n" +("Correct VAC answers = " +getCatCount("VAC"));
  		result=result+"\t\n" +("Correct POX answers = " +getCatCount("POX"));
  		result=result+"\t\n" +("Correct ERL answers = " +getCatCount("ERL"));
  		
  		
  		
  		// Writting all results in to a file
  		try
		{
			BufferedWriter outputFile = new BufferedWriter(new FileWriter("yeast/results.txt"));
			PrintWriter printstream = new PrintWriter(outputFile);
				
			printstream.println(result);
			printstream.close();
			outputFile.close();
		}
		
		catch (Exception e)
		{
			System.out.println(e);
		}
  		
  		System.exit(0);
  	}
  	  	  	
  	if (((currentStep % 50) >= 3 && ((currentStep % 50) <= 7)))
	{		
		//System.out.println("Measure " + currentStep);
      	measureCategory();
	}
	else if ((currentStep % 50) == 16)
	{
		totalpatterns++;
		
		System.out.println("The total neurons of all categories");
		result=result+"\t\n" +("The total neurons of all categories");
		for (int count = 0; count <10; count++)
		{
			System.out.println("Category - "+catName(count+1)+"  = " +totalFired[count]);
			result=result+"\t\n" +("Category - "+catName(count+1)+"  = " +totalFired[count]);
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
		System.out.println("Pattern - "+(parentNet.getCurrentPattern()+startingValue));
		result=result+"\t\n" +("Maximum number is - "+max);
		result=result+"\t\n" +("Pattern - "+(parentNet.getCurrentPattern()+startingValue));
		
		if (cat==0)
		{
			System.out.println("Category is unknown - no category neurons were fired");
			System.out.println("Number of correct answers - "+correctAns+ "/" +totalpatterns);
			System.out.println("Correct Category is  - "+correctCat[(parentNet.getCurrentPattern()+startingValue)]);				
			result=result+"\t\n" +("Category is unknown - no category neurons were fired");
			result=result+"\t\n" +("Correct Category is  - "+correctCat[(parentNet.getCurrentPattern()+startingValue)]);				
			result=result+"\t\n" +("Number of correct answers - "+correctAns+ "/" +totalpatterns);
		}
		else
		{
			System.out.println("Selected Category is - "+catName(cat));
			System.out.println("Correct Category is  - "+correctCat[(parentNet.getCurrentPattern()+startingValue)]);				
			result=result+"\t\n" +("Selected Category is - "+catName(cat));
			result=result+"\t\n" +("Correct Category is  - "+correctCat[(parentNet.getCurrentPattern()+startingValue)]);				
			
			
			if (catName(cat).equalsIgnoreCase(correctCat[(parentNet.getCurrentPattern()+startingValue)]))
			{
				correctAns++;
				catCount(catName(cat));
			}
				System.out.println("Number of correct answers - "+correctAns+ "/" +totalpatterns);
				result=result+"\t\n" +("Number of correct answers - "+correctAns+ "/" +totalpatterns);
				
				Integer int1 = new Integer(correctAns);
				Integer int2 = new Integer(totalpatterns);
				double d1 = int1.doubleValue();
				double d2 = int2.doubleValue();
				double d3 = (d1/d2)*100;
				System.out.println("Percentage of correct answers - "+Math.round(d3)+"%");
				result=result+"\t\n" +("Percentage of correct answers - "+Math.round(d3)+"%");
			}
		if (catCountCheck == true)
		{
			// displaying the number of neurons fired in each time step	
			for (int counter = 0; counter < 10; counter++)
			{
				System.out.println("The neurons fired in category "+catName(counter+1)+ " in each time step - "+catCounts[counter]);
				result=result+"\t\n" +("The neurons fired in category "+catName(counter+1)+ " in each time step - "+catCounts[counter]);
				catCounts[counter] = "";
			}

		}	
				
		System.out.println("");
		result=result+"\t\n" +("");
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

//Count the number of correct answer for each category	
  public void catCount (String cat)
  {
  	if (cat.equalsIgnoreCase("CYT"))
  	{
  		CYT++;
  	}
  	else if (cat.equalsIgnoreCase("NUC"))
  	{
  		NUC++;
  	}
  	else if (cat.equalsIgnoreCase("MIT"))
  	{
  		MIT++;
  	}
  	else if (cat.equalsIgnoreCase("ME3"))
  	{
  		ME3++;
  	}
  	else if (cat.equalsIgnoreCase("ME2"))
  	{
  		ME2++;
  	}
  	else if (cat.equalsIgnoreCase("ME1"))
  	{
  		ME1++;
  	}
  	else if (cat.equalsIgnoreCase("EXC"))
  	{
  		EXC++;
  	}
  	else if (cat.equalsIgnoreCase("VAC"))
  	{
  		VAC++;
  	}
  	else if (cat.equalsIgnoreCase("POX"))
  	{
  		POX++;
  	}
  	else if (cat.equalsIgnoreCase("ERL"))
  	{
  		ERL++;
  	}
  }
  
  // Displays the total count of categories which are correctly answered
  public int getCatCount(String cat)
  {
  	if (cat.equalsIgnoreCase("CYT"))
  	{
  		return CYT;
  	}
  	else if (cat.equalsIgnoreCase("NUC"))
  	{
  		return NUC;
  	}
  	else if (cat.equalsIgnoreCase("MIT"))
  	{
  		return MIT;
  	}
  	else if (cat.equalsIgnoreCase("ME3"))
  	{
  		return ME3;
  	}
  	else if (cat.equalsIgnoreCase("ME2"))
  	{
  		return ME2;
  	}
  	else if (cat.equalsIgnoreCase("ME1"))
  	{
  		return ME1;
  	}
  	else if (cat.equalsIgnoreCase("EXC"))
  	{
  		return EXC;
  	}
  	else if (cat.equalsIgnoreCase("VAC"))
  	{
  		return VAC;
  	}
  	else if (cat.equalsIgnoreCase("POX"))
  	{
  		return POX;
  	}
  	else if (cat.equalsIgnoreCase("ERL"))
  	{
  		return ERL;
  	}
  	return 0;
  	
  }

  public void printExpName () {
    System.out.println("Yeast "+trainingLength);
  }
  
}
	
    
