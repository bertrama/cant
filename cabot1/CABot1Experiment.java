//run multiple tests
import java.io.*;
import java.util.*;

public class CABot1Experiment extends CANTExperiment {
  public boolean runWithCrystalSpace=true;
  //at most one of these four below is true
  public boolean runWithParser=false;
  public boolean groundWords=false;
  public boolean runSentenceTest=false;
  public boolean runSimpleCommandTest=false;

  public boolean runSubsymbolically=true;  //can do simple command test subsymb

  public int timeOfCommand = 0;

  private boolean actionRunning[];
  private int numActions=6;

  public CABot1Experiment () {
    trainingLength = 0; 
    inTest = false;
    ruleOn = new int[5];
    for (int i = 0; i < 5; i++)
      ruleOn[i]=0;

    actionRunning = new boolean[numActions];
    for (int i = 0; i < numActions; i++)
      actionRunning[i]=false;

    //output for parsing test
    sentenceResults = new int [21][2];  
    for (int i = 0; i < 21; i++) 
      {
      sentenceResults[i][0]=0;
      sentenceResults[i][1]=0;
      }

  }
  
  //sentence 1 Follow me.
  //sentence 2 Move left.
  //sentence 3 Turn toward the pyramid.
  //sentence 4 Move toward it.
  //sentence 5 Go left.
  //sentence 6 Move toward the pyramid.
  //sentence 7 Turn toward it.
  //sentence 8 Move right.
  //sentence 9 Move forward.
  //sentence 10 Move backward.
  //sentence 11 Move toward the stalagtite. 
  //sentence 12 Move toward the door. 
  //sentence 13 Turn toward the stalagtite.
  //sentence 14 Turn toward the door.
  //sentence 15 Go right.
  //sentence 16 Go forward.
  //sentence 17 Go back.
  //sentence 18 Turn left.
  //sentence 19 Turn right.
  //sentence 20 Go to the pyramid.
  //sentence 21 Go to the stalagtite.
  //undone Stop.
  //undone (to) Go to the door/pyramid/stalagtite
  private int sentence=19;
  
  static  String filePath;
  
  public void setFilePath(String newPath) {filePath=newPath;}

  //*******Functions for interacting with Crystal Space Files
  //parse the command string to get the time the command was issued.  
  private int getCommandTime(String inputString) {
    int time=0;
    int spacePlace= 13;
    String timeString = inputString.substring(spacePlace,inputString.length());
	
    time = Integer.parseInt(timeString);
    return time;
  }

  private boolean lastWord(String inputLine) {
    int space = inputLine.indexOf(" ");
    if (space < 0) return true;
    return false;
  }

  private String spellCorrect(String word) {
    if ((word.compareTo("go")==0) || (word.compareTo("g")==0) || 
      (word.compareTo("Go")==0))
      return "Go";
    else  if ((word.compareTo("move")==0) || (word.compareTo("mov")==0) ||
      (word.compareTo("Move")==0))
      return "Move";
    else  if ((word.compareTo("turn")==0) || (word.compareTo("trun")==0) ||
      (word.compareTo("tur")==0) || (word.compareTo("Turn")==0))
      return "Turn";
    else if ((word.compareTo("lef")==0) || (word.compareTo("le")==0) ||
             (word.compareTo("left")==0))
      return "left";
    else if ((word.compareTo("righ")==0) || (word.compareTo("rt")==0) ||
             (word.compareTo("right")==0))
      return "right";
    else  if ((word.compareTo("bac")==0) || (word.compareTo("backward")==0) || 
             (word.compareTo("back")==0) || (word.compareTo("bck")==0))
      return "back";
    else  if ((word.compareTo("forwar")==0) || (word.compareTo("frward")==0) ||
              (word.compareTo("forward")==0))
      return "forward";
    else  if ((word.compareTo("pyramid")==0) || (word.compareTo("pyramd")==0))
      return "pyramid";
    else  if ((word.compareTo("stalagtite")==0) || (word.compareTo("stal")==0)
              || (word.compareTo("stalactite")==0))
      return "stalagtite";
    else  if ((word.compareTo("to")==0) || (word.compareTo("t")==0))
      return "to";
    else  if ((word.compareTo("the")==0) || (word.compareTo("th")==0))
      return "the";
    else  if ((word.compareTo("toward")==0) || (word.compareTo("towrd")==0) || 
             (word.compareTo("twrd")==0) || (word.compareTo("towrd")==0))
      return "toward";

    else {
      if (word.length() > 0) 
        System.out.println("Spell Check Failed " + word);
      return "";
    }
  }

  private String transformTextCommand(String inputLine) {
    String word1="";
    String word2="";
    String word3="";
    String word4="";
    String word5="";
	
    int space;
	
    space = inputLine.indexOf(" ");
    word1 = inputLine.substring(0,space);
    inputLine = inputLine.substring(space+1,inputLine.length());
    if (lastWord(inputLine)) 
      word2 = inputLine.substring(0,inputLine.length()-1);
    else {   
      space = inputLine.indexOf(" ");
      word2 = inputLine.substring(0,space);
      inputLine = inputLine.substring(space+1,inputLine.length());
      if (lastWord(inputLine))
        word3 = inputLine.substring(0,inputLine.length()-1);
      else {       
        space = inputLine.indexOf(" ");
        word3 = inputLine.substring(0,space);
        inputLine = inputLine.substring(space+1,inputLine.length());
        if (lastWord(inputLine))
          word4 = inputLine.substring(0,inputLine.length()-1);
        else {       
          space = inputLine.indexOf(" ");
          word4 = inputLine.substring(0,space);
          inputLine = inputLine.substring(space+1,inputLine.length());
	}//word4
      }//word3
    }//word2
	
    word1 = spellCorrect(word1);
    word2 = spellCorrect(word2);
    word3 = spellCorrect(word3);
    word4 = spellCorrect(word4);

    if (word4.length() > 0)
      return word1+" "+word2+" "+word3+" "+word4+".";
    else if (word3.length() > 0)
      return word1+" "+word2+" "+word3+".";
    else
      return word1+" "+word2+".";

  }
  ///******Yulei's test code
  private int currentCommandId=1;
  private String testMultipleCommandOutput="";
  private String getUserCommandFromMultipleCommandFile() {
    int commandTime;
    String inputLine="";
    String timeLine="";
    String thisLine;
   
     //Open the file for reading
     //System.out.println("i="+currentCommandId);
     if (currentCommandId>120) {
       writeMultipleCommandResult();
       System.exit(0);
     }
  
    //read command
    try {
      BufferedReader br = new BufferedReader(new FileReader(
        filePath+"testTextCommands.txt"));
      for (int i=0; i<currentCommandId; i++) {
        inputLine=br.readLine();
      }
    } // end try
  
    catch (IOException e) {
      writeMultipleCommandResult();
      System.err.println("Error: " + e);
    }

    currentCommandId++;   
    inputLine=transformTextCommand(inputLine);
    return(inputLine);
  }

  private void writeMultipleCommandResult() {
    PrintWriter outputCommandFile = null;
    try {
      outputCommandFile = new PrintWriter(new FileWriter
        (filePath+"TestAgentCommand.txt"));
      outputCommandFile.println(testMultipleCommandOutput+'\n');
    }
    catch (IOException e) {
      System.err.println("Could not properly write  csToAgentCommand.txt " + 
        e.toString());
    }
    finally {
      if (outputCommandFile != null) {
        outputCommandFile.close();
      }
    }
    testMultipleCommandOutput="";
  }

  private int multipleCommandsCorrect=0;
  private int multipleCommandsIncorrect=0;
  private boolean testMultipleCommandResult() {
    String cABotOutput="";
    try {
      BufferedReader br = new BufferedReader(new FileReader(
        filePath+"TestAgentCommand.txt"));
      cABotOutput=br.readLine();
    }
    catch (IOException e) {
      System.err.println("Error: " + e);
    }

    String answer="";
    try {
      BufferedReader br = new BufferedReader(new FileReader(
        filePath+"testTextAnswers.txt"));
      for (int i=0; i<currentCommandId-1; i++) {
        answer=br.readLine();
      }
    }
    catch (IOException e) {
      System.err.println("Error: " + e);
    }
    answer = answer+" ";
System.out.println(answer + " testing multiple commands "+ cABotOutput + "a");
    if (answer.compareTo(cABotOutput) == 0)
      return true;
    else
      return false;
  }

  //*****End Yulei's test code    

  private String getUserCommand() {
    int commandTime;
    String inputLine="";
    String timeLine="";
    //open file
    try {
      DataInputStream inputCommandFile = new DataInputStream(
        new FileInputStream(filePath+"csToCABotText.txt"));
  	
      inputLine = inputCommandFile.readLine();
      //read time
      timeLine = inputCommandFile.readLine();
      //read command
      inputLine = inputCommandFile.readLine();
    }
  	
    catch (IOException e) {
      System.err.println("Can not read csToCABotText.txt " + e.toString());
    }
	
    commandTime = getCommandTime(timeLine);

    //if new command
    if (commandTime <= timeOfCommand)
      return ("No New Command");
    else
      {
      timeOfCommand = commandTime; 
      inputLine=transformTextCommand(inputLine);
      return (inputLine);
      }
  }

  private boolean readUserCommand(boolean fileHasMultipleCommands) {
    String userInput;
    if (fileHasMultipleCommands) 
      userInput=getUserCommandFromMultipleCommandFile();
    else userInput=getUserCommand();

    if (userInput.compareTo("No New Command")==0) 
      {
      return (false);
      }
    else if (userInput.compareTo("Turn toward the pyramid.")==0)
      {
      System.out.println("Turn toward the pyramid");
      sentence = 3;  
      }
    else if (userInput.compareTo("Go left.")==0)
      {
      System.out.println("Go Left ");
      sentence = 5;  
      }
    else if (userInput.compareTo("Move forward.")==0)
      {
      System.out.println("Move Forward ");
      sentence = 9;  
      }
    else if (userInput.compareTo("Move back.")==0)     {
      System.out.println("Move Back ");
      sentence = 10;   }
    else if (userInput.compareTo("Turn toward the stalagtite.")==0)
      {
      System.out.println("Turn toward the stalagtite");
      sentence = 13;  
      }
    else if (userInput.compareTo("Go right.")==0)
      {
      System.out.println("Go Right ");
      sentence = 15;  
      }
    else if (userInput.compareTo("Turn left.")==0)
      {
      System.out.println("Turn Left ");
      sentence = 18;  
      }
    else if (userInput.compareTo("Turn right.")==0)
      {
      System.out.println("Turn Right ");
      sentence = 19;  
      }
    else if (userInput.compareTo("Go to the pyramid.")==0)
      {
      System.out.println("Go to the pyramid "); 
      sentence = 20;  
      }
    else if (userInput.compareTo("Go to the stalagtite.")==0)
      {
      System.out.println("Go to the stalagtite "); 
      sentence = 21;  
      }
    else
      {
      System.out.println("Command Not Understood Assuming Move Backward " + 
        userInput);
      sentence = 10;
      }
    return (true);
  }

  
  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
  }
  private static String[] actions = {"Left", "Right", "Forward", "Back", 
    "Error1","Error2"};
  
  public void emitCommand(int action) {
    //open file
    PrintWriter outputCommandFile = null;
    try {
	outputCommandFile = new PrintWriter(new FileWriter
          (filePath+"cABotToCSCommand.txt"));

        outputCommandFile.println("Time+ "+ timeOfCommand);
        timeOfCommand++;
        outputCommandFile.println(actions[action]+"\n");
    }
    catch (IOException e) {
      System.err.println("Could not properly write  cABotToCSCommand.txt " + e.toString());
    }
    finally {
	if (outputCommandFile != null) {
	    outputCommandFile.close();
	    System.out.println("emit Command " + actions[action]);
	}
    }
  }
  

  //****Functions for interacting with Spreading Activation Net
  //0 means 0 neurons on, 2 means more than 50, 1 means other
  private int actionOn(int action) {
    CABot1Net actionNet = (CABot1Net)getNet("ActionNet");
	
    int fired=0;
    for (int neuron=0; neuron<100;neuron++) 
      {
      int testNeuron = neuron+(action*100);
      if (actionNet.neurons[testNeuron].getFired())
        fired++;
      }
      if (fired==0) return 0;
      if (fired>50) return 2;
      else return 1;  
  }
  
  private int moduleNeuronsFired() {
    CABot1Net moduleNet = (CABot1Net)getNet("ModuleNet");
    int moduleNeuronsOn=0;
    for (int i=0;i<moduleNet.getSize();i++){
      if (moduleNet.neurons[i].getFired()) moduleNeuronsOn++;
    }
    return moduleNeuronsOn;
  }


  private int possibleGoals = 11;
  private boolean goalSet() {
    CABot1Net factNet = (CABot1Net)getNet("FactNet");
    for (int goalTest = 0; goalTest<possibleGoals; goalTest++) {
      int neuronsFired=0;
      for (int neuron = 0; neuron<100; neuron++) {
        int neuronToTest = goalTest*100+neuron;
        if (factNet.neurons[neuronToTest].getFired()) neuronsFired++;
      }
      if (neuronsFired > 50) { 
        System.out.println("goal set " + goalTest);
        return true;
      }
    }
    return false;
  }

  private boolean goalsDone() {
    CABot1Net factNet = (CABot1Net)getNet("FactNet");
    CABot1Net moduleNet = (CABot1Net)getNet("ModuleNet");
    int goalNeuronsFired = 0;
    int moduleNeuronsFired = 0;
    for (int neuron = 0; neuron<possibleGoals*100; neuron++) {
      if (factNet.neurons[neuron].getFired()) goalNeuronsFired++;
    }
    for (int neuron = 0; neuron<moduleNet.getSize(); neuron++) {
      if (moduleNet.neurons[neuron].getFired()) moduleNeuronsFired++;
    }
    if ((goalNeuronsFired == 0) && (moduleNeuronsFired == 0)){ 
      System.out.println("goals done ");
        return true;
    }
    return false;
  }

  //Emit the command(s) associated with a new unique action.
  private void emitUniqueAction(boolean runningMultipleCommandTest) {
    //use actionRunning to emit commands when
    for (int action = 0; action < numActions; action ++) 
      {
      if (!actionRunning[action])
        {
        if (actionOn(action) ==2) 
          {
          if (runningMultipleCommandTest) 
	    testMultipleCommandOutput = testMultipleCommandOutput+ 
              actions[action]+" ";
          else emitCommand(action);
          actionRunning[action] = true;
          }
        }
      else if (actionOn(action) == 0) 
        actionRunning[action] = false;
      }
  }


  //*****Functions for interacting with parser nets
  private boolean parsingDone() {
    CABot1Net stackTopNet = (CABot1Net)getNet("StackTopNet");

    if (stackTopNet.getActives()==0) return true;
    //if  (CANT23.CANTStep > 1500) return true;
	return false;
  }

  public int parseState = 1;
  private int newSentenceStartStep = -1;
  private boolean gotNewSentence(int currentStep) {
    if ((newSentenceStartStep >=0 ) && 
	   ((newSentenceStartStep + 10) == currentStep))
	  return (true);
	else  
      return (false);
  }

  private boolean clearingDone() {
    return (false);
  }

  //private static boolean inRunAgain = false;
  public int currentWord = -2;

  //----functions for moving through sentences
  private void readNextWord(int currentStep) 
    {
    CABot1Net inputNet = (CABot1Net)getNet("BaseNet");
    if (sentence==1)    {
      if (currentWord==-2)  currentWord=0;
      else currentWord++;
      if (currentWord==3) currentWord = -1;
      }
    else if (sentence==2) 
      {
      if (currentWord==-2) currentWord=3;
      else if (currentWord==3)  currentWord++;
      else if (currentWord==4)  currentWord = 2;	
      else if (currentWord==2)  currentWord=-1;
      }
    else if (sentence==3) 
      {
      if (currentWord==-2) currentWord=5;
      else if ((currentWord<8) && (currentWord >= 5))
	 currentWord++;
      else if (currentWord==8) currentWord = 2;	
      else if (currentWord==2)  currentWord=-1;
      }
    else if (sentence==4)   {
      if (currentWord==-2) currentWord=3;
      else if (currentWord == 3) currentWord=6;
      else if (currentWord == 6) currentWord = 10;
      else if (currentWord==10)  currentWord = 2;	
      else if (currentWord==2)   currentWord=-1;
      }
	else if (sentence==5) 
	  {
	  if (currentWord==-2)
	    currentWord=11;
	  else if (currentWord == 11)
	  	currentWord=4;
	  else if (currentWord == 4)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==6) //Move toward the pyramid
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord == 3)
	  	currentWord=6;
	  else if (currentWord == 6)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 8;
	  else if (currentWord==8)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==7) 
	  {
	  if (currentWord==-2)
	    currentWord=5;
	  else if (currentWord == 5)
	  	currentWord=6;
	  else if (currentWord == 6)
	    currentWord = 10;
	  else if (currentWord == 10)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==8) 
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord==3) 
	    currentWord = 12;
	  else if (currentWord==12) 
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==9) 
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord==3) 
	    currentWord = 13;
	  else if (currentWord==13) 
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==10) 
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord==3) 
	    currentWord = 14;
	  else if (currentWord==14) 
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==11) //Move toward the stalagtite
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord == 3)
	  	currentWord=6;
      else if (currentWord == 6)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 9;
	  else if (currentWord==9)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==12)//Move toward the door
	  {
	  if (currentWord==-2)
	    currentWord=3;
	  else if (currentWord == 3)
	  	currentWord=6;
	  else if (currentWord == 6)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 15;
	  else if (currentWord==15)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==13) //Turn toward the stalagtite
	  {
	  if (currentWord==-2)
	    currentWord=5;
	  else if (currentWord == 5)
	    currentWord = 6;
	  else if (currentWord == 6)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 9;
	  else if (currentWord==9)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==14) //Turn toward the door
	  {
	  if (currentWord==-2)
	    currentWord=5;
	  else if (currentWord == 5)
	    currentWord = 6;
	  else if (currentWord == 6)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 15;
	  else if (currentWord==15)
	    currentWord = 2;	
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==15) //Go right
	  {
	  if (currentWord==-2)
	    currentWord=11;
	  else if (currentWord == 11)
	  	currentWord=12;
	  else if (currentWord == 12)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==16) //Go forward
	  {
	  if (currentWord==-2)
	    currentWord=11;
	  else if (currentWord == 11)
	  	currentWord=13;
	  else if (currentWord == 13)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==17) //Go back
	  {
	  if (currentWord==-2)
	    currentWord=11;
	  else if (currentWord == 11)
	  	currentWord=14;
	  else if (currentWord == 14)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==18) //Turn left
	  {
	  if (currentWord==-2)
	    currentWord=5;
	  else if (currentWord == 5)
	  	currentWord=4;
	  else if (currentWord == 4)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==19) //Turn right.
	  {
	  if (currentWord==-2)
	    currentWord=5;
	  else if (currentWord == 5)
	  	currentWord=12;
	  else if (currentWord == 12)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==20) //Go to the pyramid.
	  {
	  if (currentWord==-2)
	    currentWord=11;
	  else if (currentWord ==11)
            currentWord=16;
	  else if (currentWord == 16)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 8;
	  else if (currentWord == 8)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  }
	else if (sentence==21) //Go to the stalagtite.
	  {
	  if (currentWord==-2)
	    currentWord=11;
	  else if (currentWord ==11)
            currentWord=16;
	  else if (currentWord == 16)
	    currentWord = 7;
	  else if (currentWord == 7)
	    currentWord = 9;
	  else if (currentWord == 9)
	    currentWord = 2;
	  else if (currentWord==2)
	    currentWord=-1;
	  }
    else 
      System.out.println("Bad Sentence " + sentence);
    inputNet.setNextWord(currentWord);
    System.out.println("Next Word Read " + currentStep + " " +currentWord);
    }  

  private int timeOfLastScene = 0;
  //based on the time in the command file, check if a new scene is available
  private boolean newSceneAvailable() {
    String inputLine = "bob";
    String timeString = "";
    try {
      DataInputStream inputCommandFile = new DataInputStream(
       new FileInputStream(filePath+"csToCABotPicture.txt"));
  
      inputLine = inputCommandFile.readLine();
      inputLine = inputCommandFile.readLine();
    }
  
    catch (IOException e) {
      System.err.println("Can not read csToCABotPicture.txt " + e.toString());
    }

    //trim off the first 13 characters for "current time "
    //convert the remainder to an integer
    try {
      //consider this occasionally crashes during cs interaction.
      //I can't force it to reproduce.
      //It may be a file locking problem.  Perhaps the best thing
      //to do is catch the error (null pointer) and return false.
      //I think it's fixed 18/06/07 CRH 
      //It seems to have happened again 6/07/07 CRH
      timeString = inputLine.substring(13);
    }
    catch (NullPointerException e) {
      System.out.println("Error in newSceneAvailable " + timeString);
      return false;
    }
    if (timeString.length() <= 0) return false;
    int time = Integer.parseInt(timeString);
    if (time > timeOfLastScene) {
      timeOfLastScene = time;
      return true;
      }
    return false;
  }

  public boolean readNewVisualScene = false;

  private String getVisualFileName(){
    String result = "";
    String inputLine = "bob";

    try {
      DataInputStream inputCommandFile = new DataInputStream(
       new FileInputStream(filePath+"csToCABotPicture.txt"));
  
      inputLine = inputCommandFile.readLine();
      inputLine = inputCommandFile.readLine();
      inputLine = inputCommandFile.readLine();
    }
  
    catch (IOException e) {
      System.err.println("Can not read csToCABotPicture.txt " + e.toString());
    }
  
    result = result.concat(inputLine);
    System.out.println(result);
  
    return(result);
  }

    
  //This is for grounding words in V2 items.  It has not been tested and
  //probably needs substantial modification.
  private boolean setGroundWordsParams() {
    CABot1Net inputNet = (CABot1Net)getNet("BaseNet");
    CABot1Net instanceNet = (CABot1Net)getNet("InstanceNet");
    CABot1Net stackTopNet = (CABot1Net)getNet("StackTopNet");
    CABot1Net controlNet = (CABot1Net)getNet("ControlNet");

    if (CANT23.CANTStep == 0) {
      inputNet.setNeuronsToStimulate(0);
      controlNet.setNeuronsToStimulate(0);
      stackTopNet.setNeuronsToStimulate(0);
      instanceNet.setNeuronsToStimulate(50);
    }
      
    if ((CANT23.CANTStep%50) == 1)
      instanceNet.clear();

    //change pattern
    if ((CANT23.CANTStep%100) > 50)
      instanceNet.setCurrentPattern(1);
    else 
      instanceNet.setCurrentPattern(0);
    return (false);
  }

  private boolean runWithParserAndActions(int currentStep,
    boolean runningMultipleCommandTest) {
    CABot1Net inputNet = (CABot1Net)getNet("BaseNet");
    CABot1Net verbNet = (CABot1Net)getNet("VerbNet");
    CABot1Net instanceNet = (CABot1Net)getNet("InstanceNet");
    CABot1Net stackTopNet = (CABot1Net)getNet("StackTopNet");
    CABot1Net controlNet = (CABot1Net)getNet("ControlNet");

    //the states are 
    //1. waiting for input
    //2. got new sentence (stacktop stimulated but not inputNet)
    //3. parsing (inputNet stimulated)
    //4. setting goal
    //5. processing goal
    //6. clearing stack1
    if (parseState==1)
      {
      boolean commandAvailable = readUserCommand(runningMultipleCommandTest);
      if (commandAvailable)
        {
        System.out.println("start new sentence "+currentStep);
        stackTopNet.setNeuronsToStimulate(40);
        inputNet.setNeuronsToStimulate(0);
        newSentenceStartStep = currentStep;
        parseState=2;
        currentWord = -2;
        }
      else 
        {
        //wait
	try{CANT23.workerThread.sleep(1000);}
        catch(InterruptedException ie){ie.printStackTrace();}
          System.out.println("Waiting for new sentence ");
        }
      }
    else if (parseState == 2) 
      {
      if (gotNewSentence(currentStep))
        {
        stackTopNet.setNeuronsToStimulate(0);
        inputNet.setNeuronsToStimulate(50);
        readNextWord(currentStep);
        parseState = 3;
        }
      }
    else if (pushRuleOn(currentStep))
      readNextWord(currentStep);
    else if (parseState==3)
      {
      if (parsingDone())
        {
System.out.println("Parsing Done ");
//CANT23.setRunning(false);
        parseState=4;
        clearAllNets();
        }
      printEvents();
      }
    else if (parseState==4)
      {
      //stimulate stack 0 to activate action
      setStackOn();

      //switch control to acting
      controlNet.setCurrentPattern(1);
      if (goalSet()) {
        parseState=5;
        controlNet.setCurrentPattern(2);
        controlNet.clear();
        verbNet.clear();
        instanceNet.clear();
	clearFastBindNeurons();
        }
      }
    else if (parseState==5)
      {
      emitUniqueAction(runningMultipleCommandTest);
      if (goalsDone()) {
        parseState=6;
      }
    }

    else if (parseState==6)
      {
      controlNet.setCurrentPattern(0);
      inputNet.setNeuronsToStimulate(0);
      clearAllNets();
      //controlNet.clear();
      parseState=1;
      currentWord = -2;
      if (runningMultipleCommandTest) {
        writeMultipleCommandResult();
        if (testMultipleCommandResult())
          multipleCommandsCorrect++;
        else {
	  multipleCommandsIncorrect++;
	}
        System.out.println("Complete Actions Correct " + 
          multipleCommandsCorrect + " " + multipleCommandsIncorrect);
        }
      }
    else
      System.out.println("Bad CABot1 State");
    return false;
  }

  private int measureCycle = 0;
  private boolean setParamsForJustParsing (int currentStep) {
    CABot1Net inputNet = (CABot1Net)getNet("BaseNet");
    CABot1Net verbNet = (CABot1Net)getNet("VerbNet");
    CABot1Net instanceNet = (CABot1Net)getNet("InstanceNet");
    CABot1Net stackTopNet = (CABot1Net)getNet("StackTopNet");

    //the states are 
    //1. waiting for input
    //2. got new sentence (stacktop stimulated but not inputNet)
    //3. parsing (inputNet stimulated)
    //4. activating the semantic result and measuring it
    //5. clearing stack1
    if (parseState == 1)
      {
      if (currentStep == 0) {
        clearAllNets();
        stackTopNet.setNeuronsToStimulate(40);
        inputNet.setNeuronsToStimulate(0);
        sentence++;
        sentence %= 22;
        if (sentence == 0) sentence = 1;
        System.out.println("start new sentence "+sentence);
      }
      else if (currentStep == 10) 
        parseState=2;
      }
    else if (parseState == 2) 
      {
      stackTopNet.setNeuronsToStimulate(0);
      inputNet.setNeuronsToStimulate(50);
      readNextWord(currentStep);
      parseState = 3;
      }
    else if ((parseState == 3) && (pushRuleOn(currentStep)))
      readNextWord(currentStep);
    else if (parseState==3)
      {
      if (parsingDone() || (currentStep > 3000))
        {
	    if (currentStep > 3000) overRan++;
System.out.println("Parsing Done " + sentence + " " + currentStep);
//CANT23.setRunning(false);
        parseState=4;
        clearAllNets();
        }
      printEvents();
      }
    else if (parseState==4)
      {
      setStackOn();
      if (measureCycle == 0) {
        measureCycle=currentStep;
        //stimulate stack 0 to activate action
      }
      if (measureCycle == currentStep-50) {
	//measure what's on
	measureVerb();
	measureInstance();
        //print measurement
       
	printSemanticParseResults();

        measureCycle = 0;
        parseState=5;
        }
      }
    else if (parseState==5)
      {
      clearFastBindNeurons();
      parseState=1;
      currentWord = -2;
      CANT23.CANTStep=-1;
      cyclePushLastFinished = 0;
      }
    else
      System.out.println("Bad CABot1 Parse Testing State");

    return false;
  }

  private boolean setParamsForSimpleCommandTest (int currentStep) {
    return runWithParserAndActions(currentStep,true);
  }


  //this is a shortcut to test the spread activation and action nets.
  //It depends on the pattern in factNet.
  private boolean runWithoutParser () {
    CABot1Net inputNet = (CABot1Net)getNet("BaseNet");
    CABot1Net controlNet = (CABot1Net)getNet("ControlNet");
    CABot1Net factNet = (CABot1Net)getNet("FactNet");

    //System.out.println("Run without Parser ");
    inputNet.setNeuronsToStimulate(0);
    controlNet.setCurrentPattern(2);
    factNet.setCurrentPattern(0);
    factNet.setNeuronsToStimulate(200);
    emitUniqueAction(false);
    if (goalsDone()) {
      System.out.println("No more goals");
    }
    return false;
  }

  private boolean lastControlCAActive = false;
  private boolean processNextSentence() { 
    CABot1Net controlNet = (CABot1Net)getNet("ControlNet");
    int neuronsFired = 0;
    for (int neuron = 1200; neuron < 1400; neuron ++) 
      if (controlNet.neurons[neuron].getFired()) neuronsFired++;

    if ((neuronsFired == 0) && (lastControlCAActive)) {
      lastControlCAActive = false;
System.out.println("Process Next Sentence ");
      return true;
    }
    else if ((neuronsFired > 10 ) && (!lastControlCAActive ))
      lastControlCAActive = true;
 
    return false;
  }

  //fail out of current test
  public void resetTest() {
    parseState = 1;
    clearFastBindNeurons();
    currentWord = -2;

    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CANTNet net = (CANTNet)eNum.nextElement();
      net.clear();
    }	
  }

  //CABot can get stuck in a test.  Use this to fail out of the test.
  private int startedParsing = 0;
  private void checkOverRun (int currentStep) {
    if (currentStep > startedParsing+5000) {
      resetTest();
      startedParsing = currentStep;
    }
  }

  private boolean firstTestRun = true;
  private boolean setParamsForSubsymbolic(int currentStep) {
    CABot1Net inputNet = (CABot1Net)getNet("BaseNet");
    CABot1Net verbNet = (CABot1Net)getNet("VerbNet");
    CABot1Net instanceNet = (CABot1Net)getNet("InstanceNet");
    CABot1Net stackTopNet = (CABot1Net)getNet("StackTopNet");
    CABot1Net controlNet = (CABot1Net)getNet("ControlNet");

    //the states are 
    //1. waiting for input
    //2. got new sentence (stacktop stimulated but not inputNet)
    //3. parsing, goal setting, action, and erasing
    if (parseState==1)
      {
      if (runSimpleCommandTest) {
        if (firstTestRun){firstTestRun=false;}
        else {
System.out.println("Test Commands ");
          writeMultipleCommandResult();
          if (testMultipleCommandResult())
            multipleCommandsCorrect++;
          else {
	    multipleCommandsIncorrect++;
            resetTest();
	  } 

          System.out.println("Complete Actions Correct " + 
            multipleCommandsCorrect + " " + multipleCommandsIncorrect);
          }
        }
      
      //get the command from Crystal Space or a file if runSimpleCommandTest
      boolean commandAvailable = readUserCommand(runSimpleCommandTest);
      if (commandAvailable)
        {
        System.out.println("start new sentence "+currentStep+ " " + sentence);
        stackTopNet.setNeuronsToStimulate(50);
        controlNet.setNeuronsToStimulate(40);
        inputNet.setNeuronsToStimulate(0);
        newSentenceStartStep = currentStep;
        parseState=2;
        }
      else 
        {
        //wait
	try{CANT23.workerThread.sleep(1000);}
        catch(InterruptedException ie){ie.printStackTrace();}
          System.out.println("Waiting for new sentence ");
        }
      }
    else if (parseState == 2) 
      {
      if (gotNewSentence(currentStep))
        {
        stackTopNet.setNeuronsToStimulate(0);
        controlNet.setNeuronsToStimulate(0);
        inputNet.setNeuronsToStimulate(50);
        readNextWord(currentStep);
        startedParsing = currentStep;
        parseState = 3;
        }
      }
    else if (pushRuleOn(currentStep))
      readNextWord(currentStep);
    else if (processNextSentence()) 
      {
      parseState = 1;
      currentWord = -2;
      }
    else 
	{
        printEvents();
        emitUniqueAction(runSimpleCommandTest);
        }
    if (parsingDone()) {inputNet.setNeuronsToStimulate(0);}
    if (runSimpleCommandTest) {checkOverRun(currentStep);}

    return false;
  }

	
  //****Main entry point for controlling agent
  public boolean isEndEpoch(int currentStep) {
    CABot1Net visualInputNet = (CABot1Net)getNet("VisualInputNet");

    //check if new picture is needed.  If so, set up data for net read 
    //in changePattern;
    if (runWithCrystalSpace)
      if (newSceneAvailable()) 
        {
        readNewVisualScene = true;
        visualInputNet.setVisualInputFile(getVisualFileName());
	}

    if (runSubsymbolically) 
      {return setParamsForSubsymbolic(currentStep);  }

    else if (groundWords) {return setGroundWordsParams();  }

    else if (runWithParser){return runWithParserAndActions(currentStep,false);}

    else if (runSentenceTest) { return setParamsForJustParsing(currentStep); }

    else if (runSimpleCommandTest) {
      return setParamsForSimpleCommandTest(currentStep); }

    else { return runWithoutParser(); }
  }  

  //*******debugging functions
  //the push rule changes the input if it fires.
  //The next word is given to input when the push rule stops firing.
  int cyclePushLastStarted = 0;
  int cyclePushLastFinished = 0;
  private boolean pushRuleOn(int cycle) {
    CABot1Net pushNet = (CABot1Net)getNet("PushNet");
    int pushNeuronsOn=0;
    if (cycle-10 < cyclePushLastFinished) return false;
  
    for (int i = 0; i < 1000; i++)
      if (pushNet.neurons[i].getFired())
    	pushNeuronsOn++;
  
    if (pushNeuronsOn > 150) 
      {
      cyclePushLastStarted = cycle;
      return (false);
      }
    if ((pushNeuronsOn < 10) && (cyclePushLastStarted > 0))
      {
      cyclePushLastStarted = 0;
      cyclePushLastFinished = cycle;
      return (true);
      }
    else return (false);  
  }

  static int ruleOn[];
  private void printRuleApplied () {
    CABot1Net  ruleNet = (CABot1Net)getNet("RuleNet");
	for (int rule = 0; rule < 5; rule++) 
	  {
	  int neuronsOn = 0;
	  for (int i = 0; i < 300;i++) 
	    {
	    int ruleNeuron = (rule*300)+i;
	    if (ruleNet.neurons[ruleNeuron].getFired())
	      neuronsOn++;
	    }
	  if ((ruleOn[rule] == 0) && (neuronsOn > 0)) ruleOn[rule] = neuronsOn;
	  else if ((ruleOn[rule] > 0) && (neuronsOn == 0)) 
	    {
		if (rule ==0) 
	      System.out.println("VP -> VP Nobj " + CANT23.CANTStep + " " + ruleOn[rule]);
	    else if (rule ==1) 
	      System.out.println("VP -> VP Period " + CANT23.CANTStep+ " " + ruleOn[rule]);
	    else if (rule ==2) 
	      System.out.println("PP -> Prep Det " + CANT23.CANTStep+ " " + ruleOn[rule]);
	    else if (rule ==3) 
	      System.out.println("PP -> PP Noun " + CANT23.CANTStep+ " " + ruleOn[rule]);
	    else if (rule ==4) 
	      System.out.println("VP -> VP PP loc " + CANT23.CANTStep+ " " + ruleOn[rule]);
	    ruleOn[rule] = 0;
	    }
	  else if ((ruleOn[rule] > 0) && (neuronsOn > ruleOn[rule])) ruleOn[rule]=neuronsOn;
	  }
  }
  
  private int stackTopActive=0;
  private void printStackTopChanges (){
    CABot1Net  stackTopNet = (CABot1Net)getNet("StackTopNet");
    int lastStackTopActive=-1;
    int popActive = -1;
	for (int stackEl = 0; stackEl < 5; stackEl++) 
	  {
	  int neuronsFiring=0;
	  for (int neuron =0; neuron<300; neuron++) 
	    {
		int seNeuron=neuron+(stackEl*300);
	    if (stackTopNet.neurons[seNeuron].getFired())
		  neuronsFiring++;
	    }
		//if pushing set lastStackTopActive
		if ((popActive == -1) && (stackEl == stackTopActive) && (neuronsFiring == 0))
		  lastStackTopActive=stackEl;
		//if pushing and lastStackTopActive set new stack
		else if ((lastStackTopActive != -1) && (neuronsFiring > 1)){
		  stackTopActive = stackEl;
		  System.out.println("Pushing from "+ lastStackTopActive + " to " + stackEl + " " + CANT23.CANTStep);
		  }
		
		//if popping set up potential pop
		else if ((stackEl < stackTopActive) && (neuronsFiring > 0)) 
		  popActive = stackEl;
		//do pop  
		else if ((popActive != -1) && (stackEl == stackTopActive) && (neuronsFiring == 0))
		  {
		  stackTopActive = popActive;
		  System.out.println("Popping from "+ stackEl + " to " + stackTopActive + " " + CANT23.CANTStep);
		  }
	  }
  }
  
  private void printEvents() {
    printRuleApplied();
    printStackTopChanges();
  }

  private int overRan=0;
  private int multipleVerbs = 0;
  private int multipleVerbSlots = 0;
  private int multipleNouns = 0;
  private int multiplePreps = 0;
  private int multiplePrepSlots = 0;
  private int verbOn, verbSlotOn, nounOn, prepOn, prepSlotOn;
  
  private void measureVerb() {
    CABot1Net  verbNet = (CABot1Net)getNet("VerbNet");
    verbOn=-1;
    verbSlotOn = -1;
    for (int verb = 0; verb < 4; verb++) 
      {
      int activeNeurons = 0;
      //measure core verb
      for (int i = 0; i < 300; i++) 
        {
      	int neuron = (verb*480)+i;
      	if (verbNet.neurons[neuron].getFired())
          activeNeurons ++;
        }
        if (activeNeurons > 0)
          {
          System.out.println("Verb "+ verb + " " + activeNeurons);
          if (verbOn != -1) multipleVerbs++;
	    verbOn=verb;
	  }
		
	//measure object slot
        activeNeurons=0;
	for (int i = 300; i < 360; i++) 
	  {
	  int neuron = (verb*480)+i;
	  if (verbNet.neurons[neuron].getFired())
	    activeNeurons ++;
	  }
        if (activeNeurons > 0)
	  {
	  System.out.println("Object On "+ verb + " " + activeNeurons);
	  if (verbSlotOn != -1) multipleVerbSlots++;
	  verbSlotOn=0;
	  }
		

        //measure location slot
        activeNeurons=0;
        for (int i = 360; i < 420; i++) 
	  {
	  int neuron = (verb*480)+i;
	  if (verbNet.neurons[neuron].getFired())
	    activeNeurons ++;
	  }
	if (activeNeurons > 0)
          {
	  System.out.println("Location On "+ verb + " " + activeNeurons);
          if (verbSlotOn != -1) multipleVerbSlots++;
          verbSlotOn=1;
          }
        }
  }

  private void measureInstance() {
    int prepStart=1800;
    CABot1Net  instanceNet = (CABot1Net)getNet("InstanceNet");
    nounOn=-1;
    prepOn=-1;
    prepSlotOn=-1;
    for (int noun = 0; noun < 12; noun++) 
      {
      int activeNeurons = 0;
      //measure noun
      for (int i = 0; i < 150; i++) 
        {
        int neuron = (noun*150)+i;
  	if (instanceNet.neurons[neuron].getFired())
  	  activeNeurons ++;
        }
      if (activeNeurons > 0)
	{
        System.out.println("Noun "+ noun + " " + activeNeurons);
        if (nounOn != -1) multipleNouns++;
        nounOn=noun;
        }
      }	

    for (int prep = 0; prep < 3; prep++) 
      {
      int activeNeurons = 0;
      //measure prep
      for (int i = 0; i < 300; i++) 
        {
        int neuron = prepStart+(prep*360)+i;
        if (instanceNet.neurons[neuron].getFired())
          activeNeurons ++;
        }
      if (activeNeurons > 0)
        {
        System.out.println("Prep "+ prep + " " + activeNeurons);
        if (prepOn != -1) multiplePreps++;
        prepOn=prep;
	}
		
    
      activeNeurons=0;	
      for (int i = 300; i < 360; i++) 
        {
        int neuron = prepStart+(prep*360)+i;
        if (instanceNet.neurons[neuron].getFired())
          activeNeurons ++;
        }
      if (activeNeurons > 0)
	    {
        System.out.println("Prep slot "+ prep + " " + activeNeurons);
        if (prepSlotOn != -1) multiplePrepSlots++;
        prepSlotOn=prep;
	}
    }	
  }
  

  private int sentenceResults[][];
  private void printSemanticParseResults() {
    if (sentence == 1) {
      if ((verbOn==0) && (verbSlotOn == 0) && (nounOn==0))
        sentenceResults[0][0]++;
      else	
        sentenceResults[0][1]++;
      }
    else if (sentence == 2) {
      if ((verbOn==1) && (verbSlotOn == 0) && (nounOn==1))
        sentenceResults[1][0]++;
      else	
        sentenceResults[1][1]++;
      }
    else if (sentence == 3)  {
      if ((verbOn==2) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)
        &&(nounOn==2))
        sentenceResults[2][0]++;
      else	
        sentenceResults[2][1]++;
      }
    else if (sentence == 4) {
        if ((verbOn==1) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==3))
  	  sentenceResults[3][0]++;
  	else	
          sentenceResults[3][1]++;
        }
      else if (sentence == 5) {
  	  if ((verbOn==3) && (verbSlotOn == 0) && (nounOn==1))
  	    sentenceResults[4][0]++;
  	  else	
  	    sentenceResults[4][1]++;
  	  }
 	else if (sentence == 6) 
 	  {
 	  if ((verbOn==1) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==2))
 	    sentenceResults[5][0]++;
 	  else	
 	    sentenceResults[5][1]++;
 	  }
 	else if (sentence == 7) 
 	  {
 	  if ((verbOn==2) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==3))
 	    sentenceResults[6][0]++;
 	  else	
 	    sentenceResults[6][1]++;
 	  }
 	else if (sentence == 8) 
 	  {
 	  if ((verbOn==1) && (verbSlotOn == 0) && (nounOn==4))
 	    sentenceResults[7][0]++;
 	  else	
 	    sentenceResults[7][1]++;
 	  }
 	else if (sentence == 9) 
 	  {
 	  if ((verbOn==1) && (verbSlotOn == 0) && (nounOn==5))
 	    sentenceResults[8][0]++;
 	  else	
 	    sentenceResults[8][1]++;
 	  }
 	else if (sentence == 10) //move backward
 	  {
 	  if ((verbOn==1) && (verbSlotOn == 0) && (nounOn==6))
 	    sentenceResults[9][0]++;
 	  else	
 	    sentenceResults[9][1]++;
 	  }
 	else if (sentence == 11) //turn toward the stalagtite
 	  {
 	  if ((verbOn==1) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)&&(nounOn==7))
 	    sentenceResults[10][0]++;
 	  else	
 	    sentenceResults[10][1]++;
 	  }
    else if (sentence == 12) {
      if ((verbOn==1) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)
        &&(nounOn==8))
        sentenceResults[11][0]++;
      else	
        sentenceResults[11][1]++;
      }
    else if (sentence == 13) { //move toward the door
      if ((verbOn==2) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)
        &&(nounOn==7))
        sentenceResults[12][0]++;
      else	
        sentenceResults[12][1]++;
      }
    else if (sentence == 14)  { //Turn toward the door
      if ((verbOn==2) && (verbSlotOn == 1) && (prepOn ==0) && (prepSlotOn==0)
        &&(nounOn==8))
        sentenceResults[13][0]++;
      else	
        sentenceResults[13][1]++;
      }
    else if (sentence == 15) {
      if ((verbOn==3) && (verbSlotOn == 0) && (nounOn==4))
        sentenceResults[14][0]++;
      else	
        sentenceResults[14][1]++;
      }
    else if (sentence == 16) {
      if ((verbOn==3) && (verbSlotOn == 0) && (nounOn==5))
        sentenceResults[15][0]++;
      else	
        sentenceResults[15][1]++;
      }
    else if (sentence == 17)  {
      if ((verbOn==3) && (verbSlotOn == 0) && (nounOn==6))
        sentenceResults[16][0]++;
      else	
        sentenceResults[16][1]++;
      }
    else if (sentence == 18)  { //turn left
      if ((verbOn==2) && (verbSlotOn == 0) && (nounOn==1))
        sentenceResults[17][0]++;
      else sentenceResults[17][1]++; }
    else if (sentence == 19)  {  //turn right
      if ((verbOn==2) && (verbSlotOn == 0) && (nounOn==4))
        sentenceResults[18][0]++;
      else sentenceResults[18][1]++; }
    else if (sentence == 20)  { // go to the pyramid
      if ((verbOn==3) && (verbSlotOn == 1) && (prepOn ==1) && (prepSlotOn==1)
        &&(nounOn==2))
        sentenceResults[19][0]++;
      else sentenceResults[19][1]++; }
    else if (sentence == 21)  { //go to the stalagtite
      if ((verbOn==3) && (verbSlotOn == 1) && (prepOn ==1) && (prepSlotOn==1)
        &&(nounOn==7))
        sentenceResults[20][0]++;
      else sentenceResults[20][1]++; } 
    else 
      System.out.println("Error Sentence Not Considered In " + sentence);
	  
    //print current results
    for (int i = 0; i< 21;i++)   
      {
      System.out.println("Sentence " + (i+1) + " " + sentenceResults[i][0] + 
        " " + sentenceResults[i][1]);
      }
	  
    System.out.println("Errors " + overRan + " " + multipleVerbs + " " + 
      multipleVerbSlots + " " + multipleNouns + " " + multiplePreps + " " +
      multiplePrepSlots);
  }
    /*
  public void printSymbolicResult() {
    //clear all nets
    clearAllNets();
	
    if (CANT23.CANTStep > 1500) overRan++;
	
    //turn stack 1 on for several cycles
    CABot1Net  stackNet = (CABot1Net)getNet("StackNet");
    for (int i = 0; i < 10; i++) 
      {
      CANT23.setRunning(true);
      setStackOn();
      stackNet.runAllOneStep(CANT23.CANTStep);
      CANT23.CANTStep++;
      }
    CANT23.setRunning(false);
	
    //measure what's on
    measureVerb();
    measureInstance();
  }
    */
  
  //----functions for manpipulating nets 	
  private void clearAllNets() {
    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CANTNet net = (CANTNet)eNum.nextElement();
      net.clear();
    }	
  }
  
  private void setStackOn() {
    CABot1Net  stackNet = (CABot1Net)getNet("StackNet");
    for (int i = 0 ; i <300; i++) 
      {
      stackNet.neurons[i].setActivation(10.0);
      }
  }
  
  public void clearFastBindNeurons(CABot1Net net) {
    for (int neuronIndex = 0; neuronIndex<net.getSize(); neuronIndex++) 
      {
      if (net.neurons[neuronIndex] instanceof CANTNeuronFastBind) 
        {
      	for (int syn=0;syn<net.neurons[neuronIndex].getCurrentSynapses(); 
          syn++)
	  net.neurons[neuronIndex].synapses[syn].setWeight(0.01);
        }
      }
  }

  public void clearFastBindNeurons() {
    System.out.println("ClearFastBind");
    CABot1Net  stackNet = (CABot1Net)getNet("StackNet");
    CABot1Net  verbNet = (CABot1Net)getNet("VerbNet");
    CABot1Net  instanceNet = (CABot1Net)getNet("InstanceNet");
    clearFastBindNeurons(stackNet);
    clearFastBindNeurons(verbNet);
    clearFastBindNeurons(instanceNet);
  }
  
    /*
  private void runAgain() {
    System.out.println("Results of Sentence " + sentence + " " + CANT23.CANTStep);
    printSymbolicResult();
    clearFastBindNeurons();
    clearAllNets();
    currentWord = -2 ;
    CANT23.CANTStep=-1;
    cyclePushLastFinished = 0;
    CANT23.setRunning(true);
  }
    */

  public void printExpName () {
    System.out.println("CABot1 ");
  }

}
