//run multiple tests
import java.io.*;
import java.util.*;

public class CANTExperimentParse extends CANTExperiment {

  public CANTExperimentParse () {
    trainingLength = 0; 
	inTest = false;
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
/*To test binding of stack to verb.
	else if (currentStep == 40) 
	  {
	  inputNet.setNeuronsToStimulate(0);
	  inputNet.clear();
	  verbNet.clear();
	  stackNet.clear();
	  }
	else if (currentStep == 50) 
	  {
	  stackNet.setNeuronsToStimulate(50);
	  }
*/
	else if (((currentStep %200) == 0) && (currentStep > 100))
	   inputNet.readNextWord();
//System.out.println("isEE " + currentStep + " " + stackTopNet.getNeuronsToStimulate()
//  + " " + inputNet.getNeuronsToStimulate());
    return (false);
  }  
  
  public void printExpName () {
    System.out.println("Parse ");
  }

}