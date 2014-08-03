//run multiple tests
import java.io.*;
import java.util.*;

public class CABot2Experiment extends CANTExperiment {
  public boolean runWithCrystalSpace=true;
  //at most one of these three below should be true
  public boolean runWithParser=true;
  public boolean runSentenceTest=false;
  public boolean runSimpleCommandTest=false;

  public boolean runSubsymbolically=false;  
  public boolean testingGoalLearn=true;

  public int timeOfCommand = 0;

  private boolean actionRunning[];
  private int numActions=8;
  private int printLevel = 0;

  public CABot2Experiment () {
    trainingLength = 0; 
    inTest = false;
    if (runWithCrystalSpace) currentSentence = -1;
    setFilePath ("c:/progs/cant/CANT23/cabot2/data/");
    getNextWord();

    ruleOn = new int[5];
    for (int i = 0; i < 5; i++)
      ruleOn[i]=0;

    actionRunning = new boolean[numActions];
    for (int i = 0; i < numActions; i++)
      actionRunning[i]=false;
  }
  
  private int sentence=19;
  
  static  String filePath;
  
  public void setFilePath(String newPath) {
    filePath=newPath;
    //System.out.println("Set cabot2 IO file path " + filePath);
  }

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
      return "stalactite";
    else  if ((word.compareTo("to")==0) || (word.compareTo("t")==0))
      return "to";
    else  if ((word.compareTo("the")==0) || (word.compareTo("th")==0))
      return "the";
    else  if ((word.compareTo("toward")==0) || (word.compareTo("towrd")==0) || 
             (word.compareTo("twrd")==0) || (word.compareTo("towrd")==0))
      return "toward";
    else  if ((word.compareTo("center")==0) || (word.compareTo("centre")==0) 
             || (word.compareTo("centr")==0) || (word.compareTo("cent")==0))
      return "Center";

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
	
    if ((inputLine == null) || (inputLine.isEmpty())) {
      word1 = "nop";
      return word1+" "+word2+".";
    }
    
    space = inputLine.indexOf(" ");
    if (space == -1)
      word1 = inputLine.substring(0,inputLine.length());
    else 
      word1 = inputLine.substring(0,space);
    inputLine = inputLine.substring(space+1,inputLine.length());
    if (lastWord(inputLine)) 
      word2 = inputLine.substring(0,inputLine.length()-1);
    else {   
      space = inputLine.indexOf(" ");
      if (space == -1)
        word2 = inputLine.substring(0,inputLine.length());
      else 
        word2 = inputLine.substring(0,space);
      inputLine = inputLine.substring(space+1,inputLine.length());
      if (lastWord(inputLine))
        word3 = inputLine.substring(0,inputLine.length()-1);
      else {       
        space = inputLine.indexOf(" ");
        if (space == -1)
          word3 = inputLine.substring(0,inputLine.length());
        else 
          word3 = inputLine.substring(0,space);
        inputLine = inputLine.substring(space+1,inputLine.length());
        if (lastWord(inputLine))
          word4 = inputLine.substring(0,inputLine.length()-1);
        else {       
          space = inputLine.indexOf(" ");
          if (space == -1)
            word4 = inputLine.substring(0,inputLine.length());
          else 
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
      currentSentence = 6; }
    else if (userInput.compareTo("Move left.")==0)
      {
      System.out.println("Move Left ");
      currentSentence = 4; }
    else if (userInput.compareTo("Move forward.")==0)
      {
      System.out.println("Move Forward");
      currentSentence = 1;  }
    else if (userInput.compareTo("Move back.")==0)     {
      System.out.println("Move Back ");
      currentSentence = 2; }
    //move it toward the stalactite
    //I found the gun
    //I saw the girl with the telescope
    //move toward the door with the handle
    //move toward it
    //That is right
    //That is wrong
    else if (userInput.compareTo("Turn toward the stalactite.")==0)
      {
      System.out.println("Turn toward the stalactite");
      currentSentence = 7; }
    else if (userInput.compareTo("Move right.")==0)
      {
      System.out.println("Move Right ");
      currentSentence = 5; }
    else if (userInput.compareTo("Turn left.")==0)
      {
      System.out.println("Turn Left ");
      currentSentence = 0; }
    else if (userInput.compareTo("Turn right.")==0)
      {
      System.out.println("Turn Right ");
      currentSentence = 3; }
    else if (userInput.compareTo("Go to the pyramid.")==0)
      {
      System.out.println("Go to the pyramid "); 
      currentSentence = 8; }
    else if (userInput.compareTo("Go to the stalactite.")==0)
      {
      System.out.println("Go to the stalactite "); 
      currentSentence = 9; }
    else if (userInput.compareTo("Center the stalactite.")==0)
      {
      System.out.println("Center the stalactite");
      currentSentence = 17; }
    else if (userInput.compareTo("Center the pyramid.")==0)
      {
      System.out.println("Center the pyramid");
      currentSentence = 18; }
    else
      {
      System.out.println("Command Not Understood Assuming Move Backward CTS "+ 
        userInput);
      currentSentence = 17; }
    currentWordInSentence = -1;
    return (true);
  }

  
  public void switchToTest () {
    System.out.println("switchtotest ");
    inTest = true;
  }
  private static String[] actions = {"Left", "Right", "Forward", "Back", 
    "Error1","Error2","Reset","Reset2"};
  
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
            if (printLevel >= 1) 
	      System.out.println("emit Command " + actions[action] + " " +
                CANT23.CANTStep + " " + timeOfCommand);
	}
    }
    if (action == 0) leftsEmitted++;
    else if (action == 1) rightsEmitted++;
  }
  

  //****Functions for interacting with Spreading Activation Net
  //0 means 0 neurons on, 2 means more than 10, 1 means other
  private int actionOn(int action) {
    CABot2Net actionNet = (CABot2Net)getNet("ActionNet");
	
    int fired=0;
    for (int neuron=0; neuron<40;neuron++) 
      {
      int testNeuron = neuron+(action*40);
      if (actionNet.neurons[testNeuron].getFired())
        fired++;
      }
      if (fired==0) return 0;
      if (fired>10) return 2;
      else return 1;  
  }
  
  private int moduleNeuronsFired() {
    CABot2Net moduleNet = (CABot2Net)getNet("ModuleNet");
    int moduleNeuronsOn=0;
    for (int i=0;i<moduleNet.getSize();i++){
      if (moduleNet.neurons[i].getFired()) moduleNeuronsOn++;
    }
    return moduleNeuronsOn;
  }


  //is one of the goals 0-5 or 7,8 set
  private boolean goalSet() {
    CABot2Net goalNet = (CABot2Net)getNet("Goal1Net");
    
    int neuronsFired=0;
    for (int neuron = 0; neuron<40*6; neuron++) {
      if (goalNet.neurons[neuron].getFired()) neuronsFired++;
    }
    for (int neuron = 40*8; neuron<40*10; neuron++) {
      if (goalNet.neurons[neuron].getFired()) neuronsFired++;
    }
    if (neuronsFired > 10) { 
      //System.out.println("goal set " + CANT23.CANTStep);
      return true;
      }
    return false;
  }

  private boolean goalsDone() {
    CABot2Net moduleNet = (CABot2Net)getNet("ModuleNet");
    int moduleNeuronsFired = 0;
    boolean goalsDone = !goalSet();
    for (int neuron = 0; neuron<moduleNet.getSize(); neuron++) {
      if (moduleNet.neurons[neuron].getFired()) moduleNeuronsFired++;
    }
    //System.out.println(goalsDone+" goals not done "+moduleNeuronsFired);
    if ((goalsDone) && (moduleNeuronsFired == 0)){ 
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
    CABot2Net stackTopNet = (CABot2Net)getNet("StackTopNet");

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
    if (printLevel >= 1) 
      System.out.println("Reading Picture "+result);
  
    return(result);
  }

  //undone from CABot1
  private void readNextWord(int x) {}

  private int  firstActionRight=0;  //0=left, 1= right, 2=other
  private int rightsEmitted = 0;
  private int leftsEmitted = 0;
  private int learnEpoch = 0;   
  public void writeGoalLearnResults() {
    System.out.println(learnEpoch + " Goal Learn Results " 
      + firstActionRight + " " + leftsEmitted + " " + rightsEmitted);
    firstActionRight = 0;
    rightsEmitted = 0;
    leftsEmitted = 0;
  }

  public void writeGoal2Module2Weights() {
    CABot2Net goal2Net = (CABot2Net)getNet("Goal2Net");
    double goalLeftTurnLeftWt = 0.0;
    int goalLeftTurnLeftSynapses = 0;
    double goalLeftTurnRightWt = 0.0;
    int goalLeftTurnRightSynapses = 0;
    double goalRightTurnLeftWt = 0.0;
    int goalRightTurnLeftSynapses = 0;
    double goalRightTurnRightWt = 0.0;
    int goalRightTurnRightSynapses = 0;
    for (int i = 0; i < 200; i++) {
      if (!goal2Net.neurons[i].isInhibitory()) {
        for (int synapse = 0; synapse < 
          goal2Net.neurons[i].getCurrentSynapses();synapse++) {
          CANTNeuron toNeuron = goal2Net.neurons[i].synapses[synapse].getTo();
          if (toNeuron.parentNet.getName().compareTo("Module2Net") == 0 ) {
            if (toNeuron.getId() < 200) {
              goalLeftTurnLeftWt += 
                goal2Net.neurons[i].synapses[synapse].getWeight();
                goalLeftTurnLeftSynapses ++;
	    }
            else {
              goalLeftTurnRightWt += 
                goal2Net.neurons[i].synapses[synapse].getWeight();
                goalLeftTurnRightSynapses ++;
	    }
	  }
	}
      }
    }
    for (int i = 200; i < 400; i++) {
      if (!goal2Net.neurons[i].isInhibitory()) {
        for (int synapse = 0; synapse < 
          goal2Net.neurons[i].getCurrentSynapses();synapse++) {
          CANTNeuron toNeuron = goal2Net.neurons[i].synapses[synapse].getTo();
          if (toNeuron.parentNet.getName().compareTo("Module2Net") == 0 ) {
            if (toNeuron.getId() < 200) {
              goalRightTurnLeftWt += 
                goal2Net.neurons[i].synapses[synapse].getWeight();
                goalRightTurnLeftSynapses ++;
	    }
            else {
              goalRightTurnRightWt += 
                goal2Net.neurons[i].synapses[synapse].getWeight();
                goalRightTurnRightSynapses ++;
	    }
	  }
	}
      }
    }

    System.out.println("Weights "+ goalLeftTurnLeftWt/goalLeftTurnLeftSynapses
      + " " + goalLeftTurnRightWt/goalLeftTurnRightSynapses 
      + " " + goalRightTurnLeftWt/goalRightTurnLeftSynapses 
      + " " + goalRightTurnRightWt/goalRightTurnRightSynapses );

  }

  //0 for neither on, 1 for both, 2 otherwise
  private int centerAndValueOn() {
    CABot2Net factNet = (CABot2Net)getNet("FactNet");
    CABot2Net valueNet = (CABot2Net)getNet("ValueNet");
    boolean valueOn=false;
    boolean centerOn=false;
    int result = 0;

    for (int i = 0; i < valueNet.getSize(); i ++) {
      if (valueNet.neurons[i].getFired()) valueOn = true;
    }
    //check the center fact
    for (int i = 520; i < 560; i ++) {
      if (factNet.neurons[i].getFired()) centerOn = true;
    }
    if (valueOn && centerOn) return 1;
    else if (valueOn || centerOn) return 2;
    else return 0;
  }
  private int goalLearnTestState=0; //0 waiting to start, 1 started, 2 finished
  private boolean runWithParserAndActions(int currentStep,
    boolean runningMultipleCommandTest) {
    //I broke this from CABot1, it only works if runningMultipleCommandTest is false
    //1. parsing (inputNet stimulated)
    //2. setting goal
    //3. processing goal
    //4. clearing stack1
    if (parseState==1)
      {
      if ((testingGoalLearn) && (goalLearnTestState == 1)) {
        if (centerAndValueOn() == 0) {
          goalLearnTestState=2;
          //send crystal space the reset command
          writeGoalLearnResults();
          writeGoal2Module2Weights();
          learnEpoch++;
          learnEpoch %= 4;
          if (learnEpoch < 2) 
            emitCommand(6); //reset
          else 
            emitCommand(7); //reset2
          //set sentence to center the stalactite
          currentSentence = 17;
          goalLearnTestState=0;
        }
      }
      
      if (noRuleActive(currentStep)) setReadNextWordRule();

      if (currentStep == 1) {
        CABot2.setBarOneOn(true);
      }
      else if (currentStep == 2) {
        CABot2.setBarOneOn(false);
      }
      else if (newInstanceRuleOn()) setNewInstance(currentStep);
      else if (parseDone()) {
System.out.println("Parsing Done ");
        parseState=2;
        clearParseNets();
        CABot2Net inputNet = (CABot2Net)getNet("BaseNet");
        inputNet.setNeuronsToStimulate(0);
        CABot2Net verbInstanceNet = (CABot2Net)getNet("VerbInstanceNet");
        verbInstanceNet.setCurrentPattern(0);
        verbInstanceNet.setNeuronsToStimulate(20);     
        CABot2Net controlNet = (CABot2Net)getNet("ControlNet");
        controlNet.setCurrentPattern(2);
        controlNet.setNeuronsToStimulate(20);     
	//CABot2.setRunning(false);
        }
      }
    else if (parseState==2)
      {
      CABot2Net verbInstanceNet = (CABot2Net)getNet("VerbInstanceNet");
      verbInstanceNet.setNeuronsToStimulate(0);

      CABot2Net controlNet = (CABot2Net)getNet("ControlNet");
      controlNet.setNeuronsToStimulate(0);     

      //turn on verb instance 0
      //leave on until goal set then 
      if (goalSet()) {
        parseState = 3;
        controlNet.clear();
        CABot2Net value2Net = (CABot2Net)getNet("Value2Net");
        value2Net.clear(); //to stop the subsymbolic timer
        clearNetsAfterGoalSet();
        }
      }
    else if (parseState==3)
      {
      if (testingGoalLearn) {
	  if ((centerAndValueOn()==1) && (goalLearnTestState==0))
	    {
            writeGoal2Module2Weights();
            goalLearnTestState=1;
	    }
      }
      //System.out.println("Process Goal ");
      if (goalsDone()) {
        parseState = 4;
        }
      }
    else if (parseState==4)
      {
System.out.println("Clear Stack ");
      parseState = 1;
      resetInstanceBindings();
      startParsingNextSentence();
      CABot2Net inputNet = (CABot2Net)getNet("BaseNet");
      inputNet.setNeuronsToStimulate(100);
      }
    else
      System.out.println("Bad CABot2 State " + parseState);

    emitUniqueAction(runningMultipleCommandTest);

    return false;
  }

  //This is for input from crystal space.
  private int getNextWordOfUserCommand () {
    int result = -1;
    if (currentSentence == -1) {
      if (readUserCommand(false)) {
      }
      //undone what about when there is no command?
      else 
        if (printLevel >= 1) System.out.println("No User Command");
        currentWord = -1;
        return -1;
    }

    currentWordInSentence++;
    result = sentences[currentSentence][currentWordInSentence];

    //End of the sentence handled by startParsingNextSentence function.
    
    currentWord = result;
    return result;
  }

  /******stuff from parse3Experiment **/
  //the stored sentences for testing
  private int sentences [][] = {
				{4,2,3,-1,-1,-1},  //Turn left.
				{1,20,3,-1,-1,-1}, //Move forward.
				{1,21,3,-1,-1,-1}, //Move backward.
				{4,19,3,-1,-1,-1}, //Turn right.
                                {1,2,3,-1,-1,-1},  //Move left.
				{1,19,3,-1,-1,-1}, //Move right.
                                {4,5,6,7,3,-1},    //Turn toward the pyramid.
                                {4,5,6,9,3,-1},   //Turn toward the stalactite.
                                {25,24,6,7,3,-1}, //Go to the pyramid. 
                                {25,24,6,9,3,-1}, //Go to the stalactite.
				{1,8,5,6,9,3},    //Move it toward the 
				                     //stalactite.
				{10,11,6,12,3, -1}, //I found the gun.
				{10,13,6,14,15,6,16,3,-1}, //I saw the girl 
                 				  //with the telescope.
				{1,5,6,17,15,6,18,3,-1}, //Move toward the door
				                     //with the handle.
				{1,5,8,3,-1,-1,-1},	//Move toward it. 
				{23,26,19,3,-1,-1,-1},	//That is right.
				{23,26,22,3,-1,-1,-1},	//That is wrong.
				{27,6,9,3,-1,-1},    //Center the stalactite.
				{27,6,7,3,-1,-1},    //Center the pyramid.
				{-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1}
                               };

  //----------sentence and word management 
  //---------------handling input via change pattern
  private int totalSentences = 19;
  private int longestSentence =9;
  private int currentSentence = 0;
  private int currentWordInSentence = -1;
  public int currentWord = -1;
  private int getNextWord() {
    if (runWithCrystalSpace) return getNextWordOfUserCommand();

    int nextWord = sentences[currentSentence][++currentWordInSentence];
    if (nextWord < 0) {
      System.out.println("Read Beyond End of Sentence " + nextWord);
    }
    currentWord=nextWord;
    return nextWord;
  }

  private void resetInstanceBindings(){
    CABot2Net nounInstanceNet = (CABot2Net)getNet("NounInstanceNet");
    CABot2Net verbInstanceNet = (CABot2Net)getNet("VerbInstanceNet");
    nounInstanceNet.resetBindings();
    verbInstanceNet.resetBindings();
  }

  //----------Symbolic new noun instance stuff is for an early version of the
  //----------parser. It should be  replaced by neural stuff later.
  private int nounInstancesSet = 0;
  private int cycleNounInstSet=-1;
  private void setNewNounInstance (int cycle) {
    CABot2Net nounInstanceNet = (CABot2Net)getNet("NounInstanceNet");
    if ((cycleNounInstSet == -1) || ((cycleNounInstSet+10) < cycle)) {
      cycleNounInstSet = cycle;
      nounInstanceNet.setCurrentPattern(nounInstancesSet);
      nounInstanceNet.setNeuronsToStimulate(150);
      nounInstancesSet ++;
    }
    else if (cycleNounInstSet == (cycle-1))    {   
      nounInstanceNet.setNeuronsToStimulate(0);
    }
 }
  private int verbInstancesSet = 0;
  private int cycleVerbInstSet=-1;
  private void setNewVerbInstance (int cycle) {
    //System.out.println("new verb instance " + cycle + cycleVerbInstSet);
    CABot2Net verbInstanceNet = (CABot2Net)getNet("VerbInstanceNet");
    if (cycleVerbInstSet == -1) {
      cycleVerbInstSet = cycle;
      verbInstanceNet.setCurrentPattern(verbInstancesSet);
      verbInstanceNet.setNeuronsToStimulate(20);
      verbInstancesSet ++;
    }
    else if (cycleVerbInstSet == (cycle-1))       
      verbInstanceNet.setNeuronsToStimulate(0);
  }

  private void setNewInstance (int cycle) {
    if (newNounInstanceRuleOn()) 
      setNewNounInstance(cycle);
    else
      setNewVerbInstance(cycle);
  }

  private boolean newNounInstanceRuleOn() {
    CABot2Net ruleNet = (CABot2Net)getNet("RuleNet");

    int neuronsOn=0;
    for (int i= 0; i < 100;i++) { //new noun instance
      if(ruleNet.neurons[i].getFired()) neuronsOn++;
    }

    if (neuronsOn>20) return true;

    return false;
  }
  private boolean newInstanceRuleOn() {
    CABot2Net ruleNet = (CABot2Net)getNet("RuleNet");

    int neuronsOn=0;
    for (int i= 0; i < 100;i++) { //new noun instance
      if(ruleNet.neurons[i].getFired()) neuronsOn++;
    }

    for (int i= 400; i < 500;i++) { //new verb instance
      if(ruleNet.neurons[i].getFired()) neuronsOn++;
    }

    if (neuronsOn>20) return true;

    return false;
  }
  
  private boolean printingResults = false;
  private int startPrinting = -1;
  private void startParsingNextSentence() {
    if (printLevel >= 1) System.out.println("Start Parsing Next Sentence ");
    resetInstanceBindings();
    if (runWithCrystalSpace) {
      currentSentence = -1;
      currentWord = -1;
    }
    else {
      currentSentence ++;
      currentSentence %= totalSentences;
    }
    currentWordInSentence = -1;
    cycleVerbInstSet=-1;
    cycleNounInstSet=-1;
    nounInstancesSet=0;
    verbInstancesSet=0;
  }

  private void turnVPInstanceOn(int step) {
    CABot2Net verbInstanceNet = (CABot2Net)getNet("VerbInstanceNet");
    for (int neuron = 0; neuron < 5; neuron++) {
      int first = 0;
      if ((step%2) == 1) first =5;
      for (int feature = 0; feature < 4; feature++) {
        int neuronAct = neuron+(feature*10)+first;
        neuronAct += (verbInstancesSet -1)*vInstCASize;
        verbInstanceNet.neurons[neuronAct].setActivation(5.0);
      }
    }
  }


  private int nInstCASize=500;//note this is also defined in net
  private void printNounInstance() {
    CABot2Net nounInstanceNet = (CABot2Net)getNet("NounInstanceNet");
    int neuronsFired = 0;
    int instances = nounInstanceNet.getSize()/nInstCASize; 
    for (int instance = 0; instance < instances; instance++) {
      for (int neuron=0; neuron < 40; neuron ++) {
	if (nounInstanceNet.neurons[(instance*nInstCASize)+neuron].getFired())
          neuronsFired++;
      }
      if (neuronsFired > 0) {
        System.out.println("Noun Instance " + instance + " On "+neuronsFired);
        neuronsFired = 0;
      }
      for (int feature = 2; feature < 12; feature ++) {
        for (int neuron=0; neuron < 20; neuron ++) {
	  if (nounInstanceNet.neurons[
            (instance*nInstCASize)+neuron+(feature*20)].getFired())
            neuronsFired++;
        }
        if (neuronsFired > 0) {
          System.out.print("N Instance " + instance );
          if (feature == 6) 
            System.out.print(" Base " );
          else if (feature == 4) 
            System.out.print(" Others " );
          else if (feature == 5) 
            System.out.print(" PrepBind " );
          else if (feature == 7) 
            System.out.print(" Det " );
          else if (feature == 10) 
            System.out.print(" PP Mod " );
          else
            System.out.print(" Feature " + feature );
          System.out.println(" On "+neuronsFired);
          neuronsFired = 0;
        }
      }
    }
  }
  private void printVerbAccess() {
    CABot2Net verbAccessNet = (CABot2Net)getNet("VerbAccessNet");
    int neuronsFired = 0;
    int instances = verbAccessNet.getSize()/100; 
    for (int instance = 0; instance < instances; instance++) {
      for (int neuron=0; neuron < 100; neuron ++) {
	if (verbAccessNet.neurons[(instance*100)+neuron].getFired())
          neuronsFired++;
      }
      if (neuronsFired > 0) {
        System.out.println("Verb Access " + instance + " On "+neuronsFired);
        neuronsFired = 0;
      }
    }
  }
  private void printNounAccess() {
    CABot2Net nounAccessNet = (CABot2Net)getNet("NounAccessNet");
    int neuronsFired = 0;
    int instances = nounAccessNet.getSize()/100; 
    for (int instance = 0; instance < instances; instance++) {
      for (int neuron=0; neuron < 100; neuron ++) {
	if (nounAccessNet.neurons[(instance*100)+neuron].getFired())
          neuronsFired++;
      }
      if (neuronsFired > 0) {
        System.out.println("Noun Access " + instance + " On "+neuronsFired);
        neuronsFired = 0;
      }
    }
  }
  private int vInstCASize=300;//note this is also defined in net
  private void printVerbInstance(){
    CABot2Net verbInstanceNet = (CABot2Net)getNet("VerbInstanceNet");
    int neuronsFired = 0;
    int instances = verbInstanceNet.getSize()/vInstCASize; 
    for (int instance = 0; instance < instances; instance++) {
      for (int neuron=0; neuron < 40; neuron ++) {
	if (verbInstanceNet.neurons[(instance*vInstCASize)+neuron].getFired())
          neuronsFired++;
      }
      if (neuronsFired > 0) {
        System.out.println("Verb Instance " + instance + " On "+neuronsFired);
        neuronsFired = 0;
      }
      for (int feature = 2; feature < 14; feature ++) {
        for (int neuron=0; neuron < 20; neuron ++) {
	  if (verbInstanceNet.neurons[
            (instance*vInstCASize)+neuron+(feature*20)].getFired())
            neuronsFired++;
        }
        if (neuronsFired > 0) {
          System.out.print("Instance " + instance );
          if (feature == 6) 
            System.out.print(" Base " );
          else if (feature == 7) 
            System.out.print(" Others " );
          else if (feature == 8) 
            System.out.print(" Actor " );
          else if (feature == 9) 
            System.out.print(" Object " );
          else if (feature == 12) 
            System.out.print(" Location " );
          else
            //note that feature 3 and 4 (objdone actdone) can come on
            //because they are activated by their obj and act
            System.out.print(" Feature " + feature );
          System.out.println(" On "+neuronsFired);
          neuronsFired = 0;
        }
      }
        
    }
  }
  private void printResults(int step) {
    CABot2Net inputNet = (CABot2Net)getNet("BaseNet");
    CABot2Net verbInstanceNet = (CABot2Net)getNet("VerbInstanceNet");
    if (startPrinting == -1) {
      startPrinting = step;
      inputNet.setNeuronsToStimulate(0);
      verbInstanceNet.setLearningRate((float)0.0001);
    }
    else if ((startPrinting+ 8) == step) 
      printVerbInstance();
    else if ((startPrinting+ 45) == step) {
CABot2.setRunning(false);
      printNounInstance();
      printVerbAccess();
      printNounAccess();
      clearAllNets();
      printingResults = false;
      inputNet.setNeuronsToStimulate(100);
      verbInstanceNet.setLearningRate((float)0.2);
      startParsingNextSentence();
      startPrinting=-1;
      }
    else {
      turnVPInstanceOn(step);
      CABot2Net ruleNet = (CABot2Net)getNet("RuleNet");
      ruleNet.clear();
    }
  }

  private boolean parseDone() {
    CABot2Net ruleNet = (CABot2Net)getNet("RuleNet");

    int neuronsOn=0;
    for (int i= 800; i < 900;i++) { 
      if(ruleNet.neurons[i].getFired()) neuronsOn++;
    }
    if (neuronsOn>20) return true;
    return false;
  }
  private void parseStop(int currentCycle) {
    System.out.println("Parse Done " + currentCycle);
    CABot2.setRunning(false);
    clearAllNets();
    printingResults = true;
  }

  private boolean goalSettingDone() {
    CABot2Net controlNet = (CABot2Net)getNet("ControlNet");
    boolean result = false;
    for (int i = 160; i < 200; i++) {
      if (controlNet.neurons[i].getFired()) result= true;
    }
    return result;
  }

  private boolean parsing() {
    CABot2Net controlNet = (CABot2Net)getNet("ControlNet");
    boolean result = false;
    for (int i = 0; i < 40; i++) {
      if (controlNet.neurons[i].getFired()) result= true;
    }
    return result;
  }

  //undone symbolic hook for no rule active
  private int lastCycleRuleActive = 0;
  private boolean noRuleActive (int step) {
    CABot2Net ruleNet = (CABot2Net)getNet("RuleNet");
    for (int i = 0; i< ruleNet.getSize(); i++ ) {
      if (ruleNet.neurons[i].getFired()) {
	lastCycleRuleActive = step;
	return (false);
      }
    }
    if (lastCycleRuleActive <= (step -10)) return true;
    return false;
  }
  private void setReadNextWordRule() {
    CABot2Net ruleNet = (CABot2Net)getNet("RuleNet");

    if (printLevel >= 1) 
      System.out.println("set Read Next Word Rule " + CABot2.CANTStep);
    //activate the ReadNextWord Rule
    for (int i = 300; i < 305; i++) {
      for (int subCA = 0; subCA <10; subCA++) {
        ruleNet.neurons[i+(subCA*10)].setActivation(6.0);
      }
    }
    
    //change the baseNet input to the new word
    int currentWord = getNextWord();
  }

  private int measureCycle = 0;
  private boolean setParamsForJustParsing (int currentStep) {
    if (printingResults) {
      printResults(currentStep);
      return(false);
    }

    if (noRuleActive(currentStep)) {
      setReadNextWordRule();
    }

    if (currentStep == 1) {
      CABot2.setBarOneOn(true);
    }
    else if (currentStep == 2) {
      CABot2.setBarOneOn(false);
    }
    else if (newInstanceRuleOn()) setNewInstance(currentStep);
    else if (parseDone()) parseStop(currentStep);

    return (false);
  }

  private boolean setParamsForSimpleCommandTest (int currentStep) {
    return runWithParserAndActions(currentStep,true);
  }


  //this is a shortcut to test the spread activation and action nets.
  //It depends on the pattern in factNet.
  private boolean runWithoutParser () {
    CABot2Net inputNet = (CABot2Net)getNet("BaseNet");
    CABot2Net controlNet = (CABot2Net)getNet("ControlNet");
    CABot2Net factNet = (CABot2Net)getNet("FactNet");

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
    CABot2Net controlNet = (CABot2Net)getNet("ControlNet");
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
    //clearFastBindNeurons();
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
    if (parsing() && noRuleActive(currentStep)) {
      setReadNextWordRule();
    }

    if (currentStep == 1) {
      CABot2Net controlNet = (CABot2Net)getNet("ControlNet");
      controlNet.setNeuronsToStimulate(20);     
      CABot2.setBarOneOn(true);
    }
    else if (currentStep == 2) {
      CABot2Net controlNet = (CABot2Net)getNet("ControlNet");
      controlNet.setNeuronsToStimulate(0);     
      CABot2.setBarOneOn(false);
    }
    else if (newInstanceRuleOn()) setNewInstance(currentStep);
    else if (parseDone()) {
	//System.out.println("Parsing Done ");
      CABot2Net inputNet = (CABot2Net)getNet("BaseNet");
      inputNet.setNeuronsToStimulate(0);
      //CABot2.setRunning(false);
      }
    else if (goalSettingDone()){
      resetInstanceBindings();
      startParsingNextSentence();
      CABot2Net inputNet = (CABot2Net)getNet("BaseNet");
      inputNet.setNeuronsToStimulate(100);
    }

    emitUniqueAction(false);

    return false;
  }

  //****Main entry point for controlling agent
  public boolean isEndEpoch(int currentStep) {
    CABot2Net visualInputNet = (CABot2Net)getNet("VisualInputNet");

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
    CABot2Net pushNet = (CABot2Net)getNet("PushNet");
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
    CABot2Net  ruleNet = (CABot2Net)getNet("RuleNet");
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
    CABot2Net  stackTopNet = (CABot2Net)getNet("StackTopNet");
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

  //----functions for manpipulating nets 	
  private void clearAllNets() {
    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CANTNet net = (CANTNet)eNum.nextElement();
      net.clear();
    }	
  }

  private void clearNetsAfterGoalSet() {
    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CANTNet net = (CANTNet)eNum.nextElement();
      if ((net.getName().compareTo("VerbInstanceNet") == 0) ||
          (net.getName().compareTo("NounAccessNet") == 0) ||
          (net.getName().compareTo("VerbAccessNet") == 0) ||
          (net.getName().compareTo("NounSemNet") == 0) ||
          (net.getName().compareTo("VerbSemNet") == 0) ||
          (net.getName().compareTo("GoalSetNet") == 0) ||
          (net.getName().compareTo("NounInstanceNet") == 0))
        net.clear();
    }	
  }

  private void clearParseNets() {
    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CANTNet net = (CANTNet)eNum.nextElement();
      if ((net.getName().compareTo("BaseNet") == 0) ||
          (net.getName().compareTo("BarOneNet") == 0) ||
          (net.getName().compareTo("NounAccessNet") == 0) ||
          (net.getName().compareTo("VerbAccessNet") == 0) ||
          (net.getName().compareTo("NounSemNet") == 0) ||
          (net.getName().compareTo("VerbSemNet") == 0) ||
          (net.getName().compareTo("OtherNet") == 0) ||
          (net.getName().compareTo("VerbInstanceNet") == 0) ||
          (net.getName().compareTo("RuleNet") == 0) ||
          (net.getName().compareTo("VPPPNet") == 0) ||
          (net.getName().compareTo("NPPPNet") == 0) ||
          (net.getName().compareTo("NounInstanceNet") == 0))
        net.clear();
    }	
  }

  private void printVisObjColRowOn(int shape, int lCol, int lRow) {
    CABot2Net  visObjNet = (CABot2Net)getNet("V2Net");
    int neuronsOn = 0;
    int offSet = (lRow*500) + (lCol*10) + (shape*2500);
    for (int row = 0; row< 10;row++) {
      for (int col = 0; col < 10; col++) {
        int neuron = (row*50) + col + offSet;
        if (visObjNet.neurons[neuron].getFired())
          neuronsOn++;
      }
    }
    if (neuronsOn > 0) 
      System.out.println(shape+ " "+  lCol + " "+ lRow +" "+ neuronsOn);
  }
  private void printVisObjects() {
    for (int shape = 0; shape < 5; shape++) {
      for (int row = 0; row< 5;row++) {
        for (int col = 0; col < 5; col++) {
          printVisObjColRowOn(shape,col,row);
	}
      }
    }
  }

  private boolean isBigOn() {
    CABot2Net  factNet = (CABot2Net)getNet("FactNet");
    int bigNeuronsOn = 0;
    for (int i = 360; i<400; i++) {
	if (factNet.neurons[i].getFired()) bigNeuronsOn++;
    }
    if (bigNeuronsOn > 0) return true;

    return false;
  }

  private int isNetOn(int start, int finish, CABot2Net testNet)  {
    int neuronsOn = 0;
    for (int i = start; i<finish; i++) {
	if (testNet.neurons[i].getFired()) neuronsOn++;
    }

    return neuronsOn;
  }

  public void measure(int currentStep) {
    CABot2Net  valueNet = (CABot2Net)getNet("ValueNet");
    int neuronsOn;
    if (isBigOn()) {
      System.out.println("BigOn "+ currentStep);
      }

    /*    
    neuronsOn=isNetOn(0,400,valueNet);
    if (neuronsOn>0) {
      CABot2Net  exploreNet = (CABot2Net)getNet("ExploreNet");
      CABot2Net  goal2Net = (CABot2Net)getNet("Goal2Net");
      CABot2Net  module2Net = (CABot2Net)getNet("Module2Net");
      int exploreNeuronsOn=isNetOn(0,400,exploreNet);
      int goalLeftOn=isNetOn(0,200,goal2Net);
      int goalRightOn=isNetOn(200,400,goal2Net);
      int moduleLeftOn=isNetOn(0,200,module2Net);
      int moduleRightOn=isNetOn(200,400,module2Net);
      System.out.print(neuronsOn+ "\t" + exploreNeuronsOn + "\t" +
        goalLeftOn+ "\t" + goalRightOn + "\t" +
        moduleLeftOn+ "\t" + moduleRightOn + " " +
        " ValueOn "+ currentStep);
      System.out.println(" ");
      }
    */
  }

  public void printExpName () {
    System.out.println("CABot2 ");
  }

}
