import java.io.*;
import java.util.*;

public class CANTExperiment {
  public int trainingLength = -1;
  boolean inTest = false;
  
  public CANTExperiment () {}
  
  public boolean getInTest(){return inTest;}
  
  protected CANTNet getNet(String otherNetName) {
    Enumeration enum = CANT23.nets.elements();
    CANTNet net = (CANTNet)enum.nextElement();
    String netName = net.getName();
    while (netName.compareTo(otherNetName) != 0) 
      {
      net = (CANTNet)enum.nextElement();
      netName = net.getName();
      }
    return (net);
  }
  
  public boolean experimentDone (int Step) {
    return (false);  
  }

  public void switchToTest () {}
  
  public void measure(int currentStep) {}
  
  public int selectPattern (int curPattern, int numPatterns, CANTNet net) {
    curPattern++;
    curPattern %= numPatterns;
	return (curPattern);
  }
  
  public boolean isEndEpoch(int Cycle) {
    return (false);
  }

  public void endEpoch() {
    Enumeration enum = CANT23.nets.elements();
	CANTNet net;
    do	{
      net = (CANTNet)enum.nextElement();
      net.clear();
    }
    while (enum.hasMoreElements());
  }
  
 
  public void printExpName () {
     System.out.println("base experiment");
  }
}
	
