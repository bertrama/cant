
import java.io.*;
import java.util.*;

public class MonkExperiment extends CANTExperiment {
  CANTNet parentNet;
  int neuronsFiredPerPattern[] = new int [10];
  int totalFired[] = new int [10];
  int category = 0; 
  String correctCat[] = new String [500];
  String result ="";
  
  // Defining the number of patterns for the program to present and end the program
  int endPatNum = 432;
  
  // variables used to count the results
  int correctAns = 0;
  int totalpatterns = 0;
  
  // SPECIFY THE STARTING PATTERN NUMBER OF THE TESTING PATTERN SET HERE
  int startingValue = 0;//742;
  
  // Variable to count correct and total number of answers  
  int M1,M2,M3 = 0;
  int correctM1,correctM2,correctM3 = 0;
  
  // Turning category counting on (this will count the number of neurons fired at each time step for each category)
  boolean catCountCheck = true;
  String catCounts[] = new String [10];
  
  // varible to reset the writting file
  boolean firstRun = true;
   

  public MonkExperiment (CANTNet net) {
  	parentNet = net;
    trainingLength = 123;
    inTest = false;
  }
  
    
  public void switchToTest () {
	 System.out.println("Switch To Monk Test");
	 parentNet.setLearningOn(false);         
	 inTest = true;
	 parentNet.setRecordingActivation(true);
	 parentNet.setChangeEachTime(false);
	 
	 parentNet.setCyclesPerRun(50);
	 
	 float newFat = Float.parseFloat("0.1");
	 //parentNet.setFatigueRate(newFat);
	 
	 // Specifying the delay between steps at testing half
	 CANT23.delayBetweenSteps = 0;
	 parentNet.getNewPatterns("monk/testDataSet1.xml");
	 
	 	try
		{
			// reading the file to count and store all the correct answers
			FileReader file = new FileReader ("monk/answerstest1.txt");
			BufferedReader buffer=new BufferedReader (file);
			String textline = null;
			int position = 0;
			while ((textline = buffer.readLine()) !=null)
			{
				//String tempCat = "";
				//tempCat = textline.charAt(0)+"" +textline.charAt(1)+"" +textline.charAt(2);
				correctCat[position] = textline;
			
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
  }
  
  private void measureCategory () {
  	int neuronsFiredPerPattern[] = new int [3];
    
    for (category = 0; category < 3; category ++) 
      {
        neuronsFiredPerPattern[category] = 0;
        for (int neuron = (category * 40)+280; neuron < (category*40) + 320; neuron ++) 
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
		catName = "Monk 1";
	}
	else if (num == 2)
	{
		catName = "Monk 2";
	}
	else if (num == 3)
	{
		catName = "Monk 3";
	}
	return catName;
  }
  
  public void measure(int currentStep) {
  	if (totalpatterns == endPatNum)
  	{
  		System.out.println("Correct Monk 1 answers = " +getCatCount("Monk 1")+"/"+correctM1);
  		System.out.println("Correct Monk 2 answers = " +getCatCount("Monk 2")+"/"+correctM2);
  		System.out.println("Correct Monk 3 answers = " +getCatCount("Monk 3")+"/"+correctM3);

  		result=result+"\t\n" +("Correct Monk 1 answers = " +getCatCount("CYT")+"/"+correctM1);
  		result=result+"\t\n" +("Correct Monk 2 answers = " +getCatCount("NUC")+"/"+correctM2);
  		result=result+"\t\n" +("Correct Monk 3 answers = " +getCatCount("MIT")+"/"+correctM3);

  		
		// Writting all results in to a file
  		try
		{
			BufferedWriter outputFile = new BufferedWriter(new FileWriter("monk/results.txt", true));
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
  	
  	if((totalpatterns == 50)||(totalpatterns == 100)||(totalpatterns == 200)||(totalpatterns == 300)||(totalpatterns == 400)||(totalpatterns == 500)||(totalpatterns == 600)||(totalpatterns == 700))
  	{
  		if (firstRun == true)
  		{
  			// Writting all results in to a file
	  		try
			{
				BufferedWriter outputFile = new BufferedWriter(new FileWriter("param/results.txt"));
				PrintWriter printstream = new PrintWriter(outputFile);
					
				printstream.println(result);
				printstream.close();
				outputFile.close();
			}
		
			catch (Exception e)
			{
				System.out.println(e);
			}
			result = "";
			firstRun = false;
  		}
  		else
  		{
  			// Writting all results in to a file
	  		try
			{
				BufferedWriter outputFile = new BufferedWriter(new FileWriter("param/results.txt", true));
				PrintWriter printstream = new PrintWriter(outputFile);
					
				printstream.println(result);
				printstream.close();
				outputFile.close();
			}
			
			catch (Exception e)
			{
				System.out.println(e);
			}
			result = "";
  			
  		}

  	}
  	
  	
  	if (((currentStep % 50) >= 11 && ((currentStep % 50) <= 13)))
	{		
		//System.out.println("Measure " + currentStep);
      	measureCategory();
	}
	else if ((currentStep % 50) == 16)
	{
		totalpatterns++;
		
		System.out.println("The total neurons of all categories");
		result=result+"\t\n" +("The total neurons of all categories");
		for (int count = 0; count <3; count++)
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
		
		for (int numCount = 0; numCount < 3; numCount++)
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
			correctCatCount(correctCat[(parentNet.getCurrentPattern()+startingValue)]);
			result=result+"\t\n" +("Category is unknown - no category neurons were fired");
			result=result+"\t\n" +("Correct Category is  - "+correctCat[(parentNet.getCurrentPattern()+startingValue)]);				
			result=result+"\t\n" +("Number of correct answers - "+correctAns+ "/" +totalpatterns);
		}
		else
		{
			System.out.println("Selected Category is - "+catName(cat));
			System.out.println("Correct Category is  - "+correctCat[(parentNet.getCurrentPattern()+startingValue)]);
			correctCatCount(correctCat[(parentNet.getCurrentPattern()+startingValue)]);				
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
			for (int counter = 0; counter < 3; counter++)
			{
				System.out.println("The neurons fired in category "+catName(counter+1)+ " in each time step - "+catCounts[counter]);
				result=result+"\t\n" +("The neurons fired in category "+catName(counter+1)+ " in each time step - "+catCounts[counter]);
				catCounts[counter] = "";
			}

		}
		System.out.println("");
		if ((parentNet.getCurrentPattern()+1)!=endPatNum)
		{
			
			System.out.println("Next Category Should Be  - "+correctCat[(parentNet.getCurrentPattern()+startingValue+1)]);	
			System.out.println("");
		}
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
  	if (cat.equalsIgnoreCase("Monk 1"))
  	{
  		M1++;
  	}
  	else if (cat.equalsIgnoreCase("Monk 2"))
  	{
  		M2++;
  	}
  	else if (cat.equalsIgnoreCase("Monk 3"))
  	{
  		M3++;
  	}
  	
  }
  
  // Displays the total count of categories which are correctly answered
  public int getCatCount(String cat)
  {
  	if (cat.equalsIgnoreCase("Monk 1"))
  	{
  		return M1;
  	}
  	else if (cat.equalsIgnoreCase("Monk 2"))
  	{
  		return M2;
  	}
  	else if (cat.equalsIgnoreCase("Monk 3"))
  	{
  		return M3;
  	}
  	
  	return 0;
  	
  }
  
  //Count the number of categories
  public void correctCatCount (String cat)
  {
  	if (cat.equalsIgnoreCase("Monk 1"))
  	{
  		correctM1++;
  	}
  	else if (cat.equalsIgnoreCase("Monk 2"))
  	{
  		correctM2++;
  	}
  	else if (cat.equalsIgnoreCase("Monk 3"))
  	{
  		correctM3++;
  	}
  }

  public void printExpName () {
    System.out.println("Monk "+trainingLength);
  }
  
}
	
    
