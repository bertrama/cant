
import java.io.*;
import java.util.*;

public class BalloonExperiment extends CANTExperiment {
  CANTNet parentNet;

  public BalloonExperiment (CANTNet net) {
  	parentNet = net;
    trainingLength = 800; 
	inTest = false;
  }
  
    
  public void switchToTest () {
System.out.println("Switch To Balloon Test");
	 parentNet.setLearningOn(false);         
	 inTest = true;
	 parentNet.setRecordingActivation(true);
	 parentNet.getNewPatterns("TestPatterns.xml");
  }

  public void measure(int currentStep) {
    System.out.println("correct 5s");
	}

  public void printExpName () {
    System.out.println("baloon "+trainingLength);
  }
  
}
	