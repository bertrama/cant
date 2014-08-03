
import java.io.*;
import java.util.*;

public class CABot3Experiment extends CANTExperiment {
  public boolean runWithCrystalSpace=true;
  public boolean runSubsymbolically=true;  
  public boolean testingGoalLearn=true;

  public int timeOfCommand = 0;

  private boolean actionRunning[];
  private int numActions=8;
  private int printLevel = 0;

  public CABot3Experiment (String path) {
    trainingLength = 0; 
    inTest = false;
    if (runWithCrystalSpace) currentSentence = -1;
    setFilePath (path);
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
    //System.out.println("Set cabot3 IO file path " + filePath);
  }

  //*******Functions for interacting with Crystal Space Files
  //parse the command string to get the time the command was issued.  
  private int getCommandTime(String inputString) {
    int time=0;
    int spacePlace= 13;
    String timeString;

    try {
      timeString = inputString.substring(spacePlace,inputString.length());
    }
    catch (Exception e) {
      return 0;
    }
	
    time = Integer.parseInt(timeString);
    return time;
  }

  private boolean lastWord(String inputLine) {
    int space = inputLine.indexOf(" ");
    if (space < 0) return true;
    return false;
  }

  private String spellCorrect(String word) {
    if ((word.compareTo("bac")==0) || (word.compareTo("backward")==0) || 
             (word.compareTo("back")==0) || (word.compareTo("bck")==0))
      return "back";
    else  if ((word.compareTo("before")==0)||(word.compareTo("befor")==0))
      return "before";
    else  if ((word.compareTo("center")==0) || (word.compareTo("centre")==0) 
             || (word.compareTo("centr")==0) || (word.compareTo("cent")==0))
      return "Center";
    else  if ((word.compareTo("dangerous")==0)||(word.compareTo("dangros")==0))
      return "dangerous";
    else  if ((word.compareTo("striped")==0)||(word.compareTo("stripd")==0))
      return "striped";
    else  if ((word.compareTo("barred")==0)||(word.compareTo("bared")==0))
      return "barred";
    else  if ((word.compareTo("door")==0)||(word.compareTo("corridor")==0))
      return "door";
    else  if ((word.compareTo("explore")==0) || (word.compareTo("Explore")==0))
      return "Explore";
    else  if ((word.compareTo("forwar")==0) || (word.compareTo("frward")==0) ||
              (word.compareTo("forward")==0))
      return "forward";
    else  if ((word.compareTo("girl")==0) || (word.compareTo("grl")==0))
      return "girl";
    else if ((word.compareTo("go")==0) || (word.compareTo("g")==0) || 
      (word.compareTo("Go")==0))
      return "go";
    else if ((word.compareTo("I")==0) || (word.compareTo("i")==0))
      return "I";
    else if ((word.compareTo("is")==0) || (word.compareTo("si")==0))
      return "is";
    else if ((word.compareTo("lef")==0) || (word.compareTo("le")==0) ||
             (word.compareTo("left")==0))
      return "left";
    else  if ((word.compareTo("move")==0) || (word.compareTo("mov")==0) ||
      (word.compareTo("Move")==0))
      return "Move";
    else  if ((word.compareTo("pyramid")==0) || (word.compareTo("pyramd")==0))
      return "pyramid";
    else if ((word.compareTo("righ")==0) || (word.compareTo("rt")==0) ||
             (word.compareTo("right")==0))
      return "right";
    else  if ((word.compareTo("saw")==0) || (word.compareTo("sw")==0))
      return "saw";
    else  if ((word.compareTo("stalagtite")==0) || (word.compareTo("stal")==0)
              || (word.compareTo("stalactite")==0))
      return "stalactite";
    else  if ((word.compareTo("stop")==0) || (word.compareTo("stp")==0))
      return "Stop";
    else  if ((word.compareTo("telescope")==0) || (word.compareTo("scope")==0))
      return "telescope";
    else  if ((word.compareTo("the")==0) || (word.compareTo("The")==0))
      return "the";
    else  if ((word.compareTo("that")==0) || (word.compareTo("thta")==0))
      return "that";
    else  if ((word.compareTo("to")==0) || (word.compareTo("t")==0))
      return "to";
    else  if ((word.compareTo("toward")==0) || (word.compareTo("towrd")==0) || 
             (word.compareTo("twrd")==0) || (word.compareTo("towrd")==0))
      return "toward";
    else  if ((word.compareTo("turn")==0) || (word.compareTo("trun")==0) ||
      (word.compareTo("tur")==0) || (word.compareTo("Turn")==0))
      return "Turn";
    else  if ((word.compareTo("wrong")==0) || (word.compareTo("wrng")==0))
      return "wrong";

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
	
    if ((inputLine == null) || (inputLine.contentEquals(""))) {
      word1 = "nop";
      return word1+" .";
    }
    
    if (lastWord(inputLine)) 
      word1 = inputLine.substring(0,inputLine.length()-1);
    else {
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
        if (lastWord(inputLine))
          word5 = inputLine.substring(0,inputLine.length()-1);
        else {       
          space = inputLine.indexOf(" ");
          word5 = inputLine.substring(0,inputLine.length());
          inputLine = inputLine.substring(space+1,inputLine.length());  
          //rest of line ignored
          }//word5
          }//word4
        }//word3
      }//word2
    }//word1
	
    word1 = spellCorrect(word1);
    word2 = spellCorrect(word2);
    word3 = spellCorrect(word3);
    word4 = spellCorrect(word4);
    word5 = spellCorrect(word5);

    if (word5.length() > 0)
      return word1+" "+word2+" "+word3+" "+word4+" "+word5+".";
    else if (word4.length() > 0)
      return word1+" "+word2+" "+word3+" "+word4+".";
    else if (word3.length() > 0)
      return word1+" "+word2+" "+word3+".";
    else if (word2.length() > 0)
      return word1+" "+word2+".";
    else
      return word1+".";

  }

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

      commandTime = getCommandTime(timeLine);
    }
  	
    catch (IOException e) {
      return ("No New Command");
      //System.err.println("Can not read csToCABotText.txt " + e.toString());
    }
	

    //if new command
    if (commandTime <= timeOfCommand)
      return ("No New Command");
    else
      {
      timeOfCommand = commandTime; 
      if (inputLine.compareTo("*Bump*")==0)
        return (inputLine);

      inputLine=transformTextCommand(inputLine);
      return (inputLine);
      }
  }

  private boolean newBumpCommand() {
    int curTimeOfCommand = timeOfCommand;
    String csInput=getUserCommand();
    if (csInput.compareTo("*Bump*")==0)
      return true;
    timeOfCommand = curTimeOfCommand;//leave non-bumps for readUserCommand
    return false;
  }

  private void setBumpFact() {
    CABot3Net factNet = (CABot3Net)getNet("FactNet");
    for (int i = 0; i < 5; i++ ) {
      factNet.neurons[i+1760].setActivation(factNet.neurons[i+1760].
        getActivation() + 3.0);
      factNet.neurons[i+1770].setActivation(factNet.neurons[i+1770].
        getActivation() + 3.0);
      factNet.neurons[i+1780].setActivation(factNet.neurons[i+1780].
        getActivation() + 3.0);
      factNet.neurons[i+1790].setActivation(factNet.neurons[i+1790].
        getActivation() + 3.0);
    }
  }

  private boolean readUserCommand() {
    int curTimeOfCommand = timeOfCommand;
    String userInput=getUserCommand();
    
    /*    if (CABot3.CANTStep%10 == 0) 
	  System.out.println("Read User Command " + CABot3.CANTStep); */

    if (userInput.compareTo("No New Command")==0) 
      {
      return (false);
      }
    else if (userInput.compareTo("*Bump*")==0) {
      timeOfCommand = curTimeOfCommand;  //leave the bump for newBumpCommand
      return (false);
    }
    else if (userInput.compareTo("Turn left.")==0)
      {
      System.out.println("Turn Left ");
      currentSentence = 0; }
    else if (userInput.compareTo("Move forward.")==0)
      {
      System.out.println("Move Forward");
      currentSentence = 1;  }
    else if (userInput.compareTo("Move back.")==0)     {
      System.out.println("Move Back ");
      currentSentence = 2; }
    else if (userInput.compareTo("Turn right.")==0)
      {
      System.out.println("Turn Right ");
      currentSentence = 3; }
    else if (userInput.compareTo("Move left.")==0)
      {
      System.out.println("Move Left ");
      currentSentence = 4; }
    else if (userInput.compareTo("Move right.")==0)
      {
      System.out.println("Move Right ");
      currentSentence = 5; }
    else if (userInput.compareTo("Turn toward the pyramid.")==0)
      {
      System.out.println("Turn toward the pyramid");
      currentSentence = 6; }
    else if (userInput.compareTo("Turn toward the stalactite.")==0)
      {
      System.out.println("Turn toward the stalactite");
      currentSentence = 7; }
    else if (userInput.compareTo("go to the pyramid.")==0)
      {
      System.out.println("Go to the pyramid "); 
      currentSentence = 8; }
    else if (userInput.compareTo("go to the stalactite.")==0)
      {
      System.out.println("Go to the stalactite "); 
      currentSentence = 9; }
    else if ((userInput.compareTo("I saw the girl with the telescope.")==0) ||
             (userInput.compareTo("I saw the.")==0))
      {
      System.out.println("I saw the girl with the telescope");
      currentSentence = 12; }
    else if (userInput.compareTo("that is right.")==0)
      {
      System.out.println("That is right");
      currentSentence = 15; }
    else if (userInput.compareTo("that is wrong.")==0)
      {
      System.out.println("That is wrong");
      currentSentence = 16; }
    else if (userInput.compareTo("Center the stalactite.")==0)
      {
      System.out.println("Center the stalactite");
      currentSentence = 17; }
    else if (userInput.compareTo("Center the pyramid.")==0)
      {
      System.out.println("Center the pyramid");
      currentSentence = 18; }
    else if (userInput.compareTo("Explore.")==0)
      {
      System.out.println("Explore ");
      currentSentence = 19; }
    else if (userInput.compareTo("Stop.")==0)
      {
      System.out.println("Stop ");
      currentSentence = 20; }
    else if (userInput.compareTo("the dangerous pyramid is.")==0)
      {
      System.out.println("The dangerous pyramid is ");
      currentSentence = 24; }
    else if (userInput.compareTo("go to the door.")==0)
      {
      System.out.println("Go to the door ");
      currentSentence = 25; }
    else if (userInput.compareTo("go to the striped pyramid.")==0)
      {
      System.out.println("Go to the striped pyramid ");
      currentSentence = 26; }
    else if (userInput.compareTo("go to the striped stalactite.")==0)
      {
      System.out.println("Go to the striped stalactite ");
      currentSentence = 27; }
    else if (userInput.compareTo("go to the barred pyramid.")==0)
      {
      System.out.println("Go to the barred pyramid ");
      currentSentence = 28; }
    else if (userInput.compareTo("go to the barred stalactite.")==0)
      {
      System.out.println("Go to the barred stalactite ");
      currentSentence = 29; }
    else if (userInput.compareTo("Move before the striped stalactite.")==0)
      {
      System.out.println("move before the striped stalactite ");
      currentSentence = 30; }
    else if (userInput.compareTo("Move before the barred stalactite.")==0)
      {
      System.out.println("move before the barred stalactite ");
      currentSentence = 31; }
    else if (userInput.compareTo("Move before the striped pyramid.")==0)
      {
      System.out.println("move before the striped pyramid ");
      currentSentence = 32; }
    else if (userInput.compareTo("Move before the barred pyramid.")==0)
      {
      System.out.println("move before the barred pyramid ");
      currentSentence = 33; }
    else
      {
      System.out.println("Command Not Understood Assuming Move Backward "+ 
        userInput);
      currentSentence = 2; }
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
    commandsEmitted++;
  }
  

  //****Functions for interacting with Spreading Activation Net
  //0 means 0 neurons on, 2 means more than 10, 1 means other
  private int actionOn(int action) {
    CABot3Net actionNet = (CABot3Net)getNet("ActionNet");
	
    int fired=0;
    for (int neuron=0; neuron<40;neuron++) 
      {
      int testNeuron = neuron+(action*40);
      if (actionNet.neurons[testNeuron].getFired())
        fired++;
      }
      if (fired==0) return 0;
      //if (fired>10) return 2;
      if (fired == 5) return 2;
      else return 1;  
  }
  
  private int moduleNeuronsFired() {
    CABot3Net moduleNet = (CABot3Net)getNet("ModuleNet");
    int moduleNeuronsOn=0;
    for (int i=0;i<moduleNet.getSize();i++){
      if (moduleNet.neurons[i].getFired()) moduleNeuronsOn++;
    }
    return moduleNeuronsOn;
  }


  //is one of the goals 0-5 or 7,8 set
  private boolean goalSet() {
    CABot3Net goalNet = (CABot3Net)getNet("Goal1Net");
    
    int neuronsFired=0;
    for (int neuron = 0; neuron<40*6; neuron++) {
      if (goalNet.neurons[neuron].getFired()) neuronsFired++;
    }
    //skip turn and go
    for (int neuron = 40*8; neuron<40*14; neuron++) {
      if (goalNet.neurons[neuron].getFired()) neuronsFired++;
    }
    if (neuronsFired > 10) { 
	System.out.println("goal set " + CANT23.CANTStep);
      return true;
      }
    return false;
  }
  private boolean exploreOn() {
    CABot3Net goalNet = (CABot3Net)getNet("Goal1Net");
    
    int neuronsFired=0;

    for (int neuron = 40*11; neuron<40*12; neuron++) {
      if (goalNet.neurons[neuron].getFired()) neuronsFired++;
    }
    if (neuronsFired > 10) { 
      return true;
      }
    return false;
  }

  private boolean goalsDone() {
    CABot3Net moduleNet = (CABot3Net)getNet("ModuleNet");
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
    else if (exploreOn()){ 
      System.out.println("exploring ");
        return true;
    }
    return false;
  }

  //Emit the command(s) associated with a new unique action.
  private void emitUniqueAction() {
    //use actionRunning to emit commands when
    for (int action = 0; action < numActions; action ++) 
      {
      if (!actionRunning[action])
        {
        if (actionOn(action) ==2) 
          {
          // System.out.println("emit action "+action);
          emitCommand(action);
          actionRunning[action] = true;
          }
        }
      else if (actionOn(action) == 0) 
        actionRunning[action] = false;
      }
  }


  //*****Functions for interacting with parser nets
  private boolean parsingDone() {
    CABot3Net stackTopNet = (CABot3Net)getNet("StackTopNet");

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
      if (inputLine.length() < 13) return false;
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

  private int  firstActionRight=0;  //0=left, 1= right, 2=other
  private int rightsEmitted = 0;
  private int leftsEmitted = 0;
  public int commandsEmitted = 0;
  private int learnEpoch = 0;   
  public void writeGoalLearnResults() {
    System.out.println(learnEpoch + " Goal Learn Results " 
      + firstActionRight + " " + leftsEmitted + " " + rightsEmitted);
    firstActionRight = 0;
    rightsEmitted = 0;
    leftsEmitted = 0;
  }

  public void writeGoal2Module2Weights() {
    CABot3Net goal2Net = (CABot3Net)getNet("Goal2Net");
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
    CABot3Net factNet = (CABot3Net)getNet("FactNet");
    CABot3Net valueNet = (CABot3Net)getNet("ValueNet");
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
  private boolean runWithParserAndActions(int currentStep){
    //1. parsing (inputNet stimulated)
    //2. setting goal
    //3. clearing stack1
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
      
      //get the next word for text input if asked or no input from last sent   
      if (readNextWordOn(currentStep)) {currentWord = getNextWord();}
      else if (currentSentence == -1)  {
        currentWord = getNextWord();
        if (currentSentence >= 0) {
          currentWordInSentence = 0;
          currentWord = sentences[currentSentence][currentWordInSentence];
	}
      }

      if (currentStep == 1) {
        CABot3.setNetPatternsOn(true,"BarOneNet",50);
        CABot3.setNetPatternsOn(true,"InstanceCounterNet",50);
        CABot3.setNetPatternsOn(true,"CounterNet",20);
        //set up the first word for input 
        if (currentSentence >=0) 
	  {
          currentWordInSentence = 0;
          currentWord = sentences[currentSentence][currentWordInSentence];
	  }
      }
      else if (currentStep == 2) {
        CABot3.setNetPatternsOn(false,"BarOneNet",0);
        CABot3.setNetPatternsOn(false,"InstanceCounterNet",0);
        CABot3.setNetPatternsOn(false,"CounterNet",0);
      }

      else if (parseDone()) {
System.out.println("Parsing Done ");
        parseState=2;
        clearParseNets();
        CABot3Net inputNet = (CABot3Net)getNet("BaseNet");
        inputNet.setNeuronsToStimulate(0);
        CABot3Net controlNet = (CABot3Net)getNet("ControlNet");
        controlNet.setCurrentPattern(2);
        controlNet.setNeuronsToStimulate(20);     
CABot3.setRunning(false);
        }
      }
    else if (parseState==2)
      {
      CABot3Net controlNet = (CABot3Net)getNet("ControlNet");
      controlNet.setNeuronsToStimulate(0);     

      //turn on verb instance 0
      //leave on until goal set then 
    
      if (goalSet()) {
        System.out.println("Goal Set ");
        parseState = 3;
        controlNet.clear();
        CABot3Net value2Net = (CABot3Net)getNet("Value2Net");
        value2Net.clear(); //to stop the subsymbolic timer
        clearNetsAfterGoalSet();
        }
      }
    else if (parseState==3)
      {
System.out.println("Clear Stack ");
      parseState = 1;
      resetInstanceBindings();
      setInstanceCounter();
      setBarOneOn();
      startParsingNextSentence();
      CABot3Net inputNet = (CABot3Net)getNet("BaseNet");
      inputNet.setNeuronsToStimulate(100);
      }
    else
      System.out.println("Bad CABot3 State " + parseState);

    emitUniqueAction();

    return false;
  }

  //This is for input from crystal space.
  private int getNextWordOfUserCommand () {
      //System.out.println("Current Word ");
    int result = -1;
    if (currentSentence == -1) {
      if (readUserCommand()) {
      }
      //undone what about when there is no command?
      else 
        if (printLevel >= 1) System.out.println("No User Command");
        currentWord = -1;
        return -1;
    }

    currentWordInSentence++;
    result = sentences[currentSentence][currentWordInSentence];
    //System.out.println("Current Word " + result);

    //End of the sentence handled by startParsingNextSentence function.
    
    currentWord = result;
    return result;
  }

  /******stuff from parse3Experiment **/
  //the stored sentences for testing
  private int sentences [][] = {
	{4,2,3,-1,-1,-1},  //Turn left.+
	{1,20,3,-1,-1,-1}, //Move forward.+
	{1,21,3,-1,-1,-1}, //Move backward.+
	{4,19,3,-1,-1,-1}, //Turn right.+
        {1,2,3,-1,-1,-1},  //Move left.+
	{1,19,3,-1,-1,-1}, //Move right.+
        {4,5,6,7,3,-1},    //Turn toward the pyramid.+
        {4,5,6,9,3,-1},  //Turn toward the stalactite.+
        {25,24,6,7,3,-1}, //Go to the pyramid. +
        {25,24,6,9,3,-1}, //Go to the stalactite.+
	{1,8,5,6,9,3},    //Move it toward the stalactite.
	{10,11,6,12,3, -1}, //I found the gun.
	{10,13,6,14,15,6,16,3,-1}, //I saw the girl with the telescope.+
	{1,5,6,17,15,6,18,3,-1}, //Move toward the door with the handle.
	{1,5,8,3,-1,-1,-1},	//Move toward it. 
	{23,26,19,3,-1,-1,-1},	//That is right.+
	{23,26,22,3,-1,-1,-1},	//That is wrong.+
	{27,6,9,3,-1,-1},    //Center the stalactite.+
	{27,6,7,3,-1,-1},    //Center the pyramid.+
	{31,3,-1,-1,-1,-1},    //Explore.+
	{32,3,-1,-1,-1,-1},    //Stop.+
	{6,14,13,6,28,7,15,6,9,3,-1}, //The girl saw the dangerous pyramid 
	//with the stalactite.		
        {10,13,6,29,15,6,16,3,-1}, //I saw the boy with the telescope.
	{1,6,30,15,6,18,3,-1}, //Move the gate with the handle.
	{6,28,7,26,3,-1}, //The dangerous pyramid is.
        {25,24,6,17,3,-1}, //Go to the door. +
        {25,24,6,33,7,3,-1},//Go to the striped pyramid.
        {25,24,6,33,9,3,-1},//Go to the striped stal.
        {25,24,6,34,7,3,-1},//Go to the barred pyramid.
        {25,24,6,34,9,3,-1},//Go to the barred stal.
        {1,35,6,33,9,3,-1},//Move before the striped stal.
        {1,35,6,34,9,3,-1},//Move before the barred stal.
        {1,35,6,33,7,3,-1},//Move before the striped pyramid.
        {1,35,6,34,7,3,-1},//Move before the barred pyramid.
	{-1,-1,-1,-1,-1,-1},
	{-1,-1,-1,-1,-1,-1}};

  //----------sentence and word management 
  //---------------handling input via change pattern
  private int totalSentences = 33;
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
    CABot3Net nounInstanceNet = (CABot3Net)getNet("NounInstanceNet");
    CABot3Net verbInstanceNet = (CABot3Net)getNet("VerbInstanceNet");
    nounInstanceNet.resetBindings();
    verbInstanceNet.resetBindings();
  }

  private void setInstanceCounter() {
    CABot3Net instanceCounterNet = (CABot3Net)getNet("InstanceCounterNet");
    instanceCounterNet.clear();
    for (int i = 0; i< 5; i++) {
      instanceCounterNet.neurons[i].setActivation(8.0);
      instanceCounterNet.neurons[i+10].setActivation(8.0);
      instanceCounterNet.neurons[i+20].setActivation(8.0);
      instanceCounterNet.neurons[i+30].setActivation(8.0);
      instanceCounterNet.neurons[i+40].setActivation(8.0);
      instanceCounterNet.neurons[i+2000].setActivation(8.0);
      instanceCounterNet.neurons[i+2010].setActivation(8.0);
      instanceCounterNet.neurons[i+2020].setActivation(8.0);
      instanceCounterNet.neurons[i+2030].setActivation(8.0);
      instanceCounterNet.neurons[i+2040].setActivation(8.0);
    }
  }
  private void setBarOneOn() {
    CABot3Net barOneNet = (CABot3Net)getNet("BarOneNet");
    barOneNet.clear();
    for (int group = 0; group< 10; group++) {
      for (int i = 0; i< 5; i++) {
	barOneNet.neurons[i+ (group*10)].setActivation(8.0);
      }}
  }


  //----------Symbolic new noun instance stuff is for an early version of the
  //----------parser. It should be  replaced by neural stuff later.
  private int nounInstancesSet = 0;
  private int cycleNounInstSet=-1;
  private void setNewNounInstance (int cycle) {
    CABot3Net nounInstanceNet = (CABot3Net)getNet("NounInstanceNet");
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
  
  private boolean printingResults = false;
  private int startPrinting = -1;
  private void startParsingNextSentence() {
    if (printLevel >= 1) System.out.println("Start Parsing Next Sentence ");

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
    CABot3Net verbInstanceNet = (CABot3Net)getNet("VerbInstanceNet");
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
    CABot3Net nounInstanceNet = (CABot3Net)getNet("NounInstanceNet");
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
    CABot3Net verbAccessNet = (CABot3Net)getNet("VerbAccessNet");
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
    CABot3Net nounAccessNet = (CABot3Net)getNet("NounAccessNet");
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
    CABot3Net verbInstanceNet = (CABot3Net)getNet("VerbInstanceNet");
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

  private boolean parseDone() {
    CABot3Net ruleNet = (CABot3Net)getNet("RuleTwoNet");

    int neuronsOn=0;
    for (int i= 0; i < 100;i++) { 
      if(ruleNet.neurons[i].getFired()) neuronsOn++;
    }
    if (neuronsOn>20) return true;
    return false;
  }
  private void parseStop(int currentCycle) {
    System.out.println("Parse Done " + currentCycle);
    CABot3.setRunning(false);
    clearAllNets();
    printingResults = true;
  }

  private boolean goalSettingDone() {
    CABot3Net controlNet = (CABot3Net)getNet("ControlNet");
    boolean result = false;
    for (int i = 160; i < 200; i++) {
      if (controlNet.neurons[i].getFired()) result= true;
    }
    return result;
  }

  private boolean parsing() {
    CABot3Net controlNet = (CABot3Net)getNet("ControlNet");
    boolean result = false;
    for (int i = 0; i < 40; i++) {
      if (controlNet.neurons[i].getFired()) result= true;
    }    return result;
  }

  private void setReadNextWordRule() {
System.out.println("set Read Next Word Rule " + CABot3.CANTStep);
    CABot3Net ruleNet = (CABot3Net)getNet("RuleOneNet");

    if (printLevel >= 1) 
      System.out.println("set Read Next Word Rule " + CABot3.CANTStep);
    //activate the ReadNextWord Rule
    for (int i = 300; i < 305; i++) {
      for (int subCA = 0; subCA <10; subCA++) {
        ruleNet.neurons[i+(subCA*10)].setActivation(6.0);
      }
    }
    
    //change the baseNet input to the new word
    int currentWord = getNextWord();
  }

  private int lastReadStep = 0;
  private boolean readNextWordOn(int step) {
    CABot3Net ruleNet = (CABot3Net)getNet("RuleOneNet");
    int neuronsOn = 0;

    //activate the ReadNextWord Rule
    for (int i = 300; i < 400; i++) {
      if (ruleNet.neurons[i].getFired()) neuronsOn++;
    }
    if (neuronsOn == 50) 
      {
      if ((step - 10) > lastReadStep) {
        lastReadStep = step;
        return true;
        }
      return false;
      }
    else return false;    
  }
  


  private boolean lastControlCAActive = false;
  private boolean processNextSentence() { 
    CABot3Net controlNet = (CABot3Net)getNet("ControlNet");
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

  //This sets words and moves to the next text input, but otherwise,
  //all action is from the neurons.
  private boolean setParamsForSubsymbolic(int currentStep) {
      //get the next word for text input if asked or no input from last sent   
      if (readNextWordOn(currentStep)) {currentWord = getNextWord();}
      else if (currentSentence == -1)  {
        currentWord = getNextWord();
        if (currentSentence >= 0) {
          currentWordInSentence = 0;
          currentWord = sentences[currentSentence][currentWordInSentence];
	}
      }

    if (currentStep == 1) {
      CABot3Net controlNet = (CABot3Net)getNet("ControlNet");
      controlNet.setNeuronsToStimulate(20);     
      CABot3.setNetPatternsOn(true,"InstanceCounterNet",50);
      CABot3.setNetPatternsOn(true,"CounterNet",20);
      setBarOneOn();
      //set up the first word for input 
      if (currentSentence >=0) 
        {
        currentWordInSentence = 0;
        currentWord = sentences[currentSentence][currentWordInSentence];
	}
    }
    else if (currentStep == 2) {
      CABot3Net controlNet = (CABot3Net)getNet("ControlNet");
      controlNet.setNeuronsToStimulate(0);     
      CABot3.setNetPatternsOn(false,"InstanceCounterNet",0);
      CABot3.setNetPatternsOn(false,"CounterNet",0);
    }
    //else if (newInstanceRuleOn()) setNewInstance(currentStep);
    else {
	/*      if (parseDone()) { //just a print stub
        System.out.println("Parsing Done ");
        //CABot3.setRunning(false);
        }
	*/
      if (goalSettingDone()){  
        //System.out.println("Done Setting Goal ");
        //resetInstanceBindings();  
        //setInstanceCounter(); 
        currentSentence = -1;
        currentWord = -1;
      }
    }

    emitUniqueAction();

    return false;
  }

  //****Main entry point for controlling agent
  public boolean isEndEpoch(int currentStep) {
    CABot3Net visualInputNet = (CABot3Net)getNet("VisualInputNet");

    //check if new picture is needed.  If so, set up data for net read 
    //in changePattern;
    if (runWithCrystalSpace) {
      if (newSceneAvailable()) 
        {
        readNewVisualScene = true;
        visualInputNet.setVisualInputFile(getVisualFileName());
	}
      if (newBumpCommand() )
	{
        System.out.println("New Bump ");
        setBumpFact();
	}
    }

    if (runSubsymbolically) 
      {return setParamsForSubsymbolic(currentStep);  }
    else
      return runWithParserAndActions(currentStep);

  }  

  //*******debugging functions
  //the push rule changes the input if it fires.
  //The next word is given to input when the push rule stops firing.
  int cyclePushLastStarted = 0;
  int cyclePushLastFinished = 0;
  private boolean pushRuleOn(int cycle) {
    CABot3Net pushNet = (CABot3Net)getNet("PushNet");
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
    CABot3Net  ruleNet = (CABot3Net)getNet("RuleOneNet");
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
  
    /*
  private void printEvents() {
    printRuleApplied();
    printStackTopChanges();
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
          (net.getName().compareTo("RuleOneNet") == 0) ||
          (net.getName().compareTo("VPPPNet") == 0) ||
          (net.getName().compareTo("NPPPNet") == 0) ||
          (net.getName().compareTo("NounInstanceNet") == 0))
        net.clear();
    }	
  }

  private int isNetOn(int start, int finish, CABot3Net testNet)  {
    int neuronsOn = 0;
    for (int i = start; i<finish; i++) {
	if (testNet.neurons[i].getFired()) neuronsOn++;
    }

    return neuronsOn;
  }

  public void measure(int currentStep) {
    //System.out.println("Measure "+ currentStep);
  }

  public void printExpName () {
    System.out.println("CABot3 ");
  }

}
