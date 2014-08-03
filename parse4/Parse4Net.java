import java.io.*;
import java.util.*;

public class Parse4Net extends CANTNet {
  
  public Parse4Net(){
  }
  
  public Parse4Net(String name,int cols, int rows,int topology){
    this.cols = cols;
    this.rows = rows;
    setName(name);
    this.topology = topology;
    netFileName = name + ".dat";
    setRecordingActivation(false);
    //super(name,cols,rows,topology);
  }

  private int numNouns = 17;
  private int numVerbs = 7;
  private int numWords = 30;

  //The only subnet that is externally stimulated is the input word (BaseNet).
  //All other subnets have the 0 pattern chosen and 0 neurons externally
  //stimulated.
  public void changePattern(int cantStep)
  {
//curPattern=CANT23.experiment.selectPattern(curPattern, patterns.size(),this);
    if (getName().compareTo("BaseNet") == 0)
      return;//setInputWord();
    else if (getName().compareTo("BarOneNet") == 0)
      return;//this can be removed when we make newinstance selection neural
    else if (getName().compareTo("VerbInstanceNet") == 0)
      return;//this can be removed when we make newinstance selection neural
    else if (getName().compareTo("NounInstanceNet") == 0)
      return;//this can be removed when we make newinstance selection neural
    else {
       setCurrentPattern(0);
    }
  }

  public void setExternalActivation(int cantStep){
    if (getName().compareTo("BaseNet") == 0) {
      CANTPattern pattern = 
        (CANTPattern)patterns.get(Parse4.experiment.currentWord);
      int neuronsToStimulate = getNeuronsToStimulate(); 
      for (int i= 0; i < neuronsToStimulate; i++) {
	int neuronNumber = pattern.getPatternIndex(i);
        double theta = getActivationThreshold();
        neurons[neuronNumber].setActivation(theta+(Math.random()*theta));
      }
    }
    else if ((getName().compareTo("BarOneNet") == 0) ||
             (getName().compareTo("CounterNet") == 0) ||
             (getName().compareTo("NounInstanceNet") == 0) ||
             (getName().compareTo("VerbInstanceNet") == 0)) {
      int curPatt = getCurrentPattern();
      if (curPatt < 0) curPatt = 0;
      CANTPattern pattern = (CANTPattern)patterns.get(curPatt);
      for (int i= 0; i < getNeuronsToStimulate();  i++) {
	int neuronNumber = pattern.getPatternIndex(i);
        double theta = getActivationThreshold();
        neurons[neuronNumber].setActivation(theta+1);
      }
    }
  }

  //hook to call fast bind learning
  private void fastLearn() {
    for (int neuronIndex = 0; neuronIndex < size(); neuronIndex++) 
      {
      if (neurons[neuronIndex] instanceof CANTNeuronFastBind) {
        ((CANTNeuronFastBind)neurons[neuronIndex]).fastLearn();
	}
      }
  }

  //set all fast bind neuron weights to .01
  public void resetBindings() {
    for (int neuronIndex = 0; neuronIndex < size(); neuronIndex++) 
      {
      if (neurons[neuronIndex] instanceof CANTNeuronFastBind) {
        for (int synapse=0; synapse<neurons[neuronIndex].getCurrentSynapses(); 
          synapse++ ) {
            neurons[neuronIndex].synapses[synapse].setWeight(.01);
	  }

	}
      }
  }


  //---------------runall to get the nets working in lock step.
  public void runAllOneStep(int CANTStep) {
    //This series of loops is really chaotic, but I needed to
    //get all of the propogation done in each net in step.
    Parse4.runOneStepStart();
	
    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Parse4Net net = (Parse4Net)eNum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    eNum = CANT23.nets.elements();
      while (eNum.hasMoreElements()) {
        Parse4Net net = (Parse4Net)eNum.nextElement();
        net.setExternalActivation(CANTStep);
	}
      //net.propogateChange();  
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Parse4Net net = (Parse4Net)eNum.nextElement();
      net.spontaneousActivate();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Parse4Net net = (Parse4Net)eNum.nextElement();
      net.setNeuronsFired();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Parse4Net net = (Parse4Net)eNum.nextElement();
      net.setDecay ();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Parse4Net net = (Parse4Net)eNum.nextElement();
      net.spreadActivation();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Parse4Net net = (Parse4Net)eNum.nextElement();
      net.setFatigue();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Parse4Net net = (Parse4Net)eNum.nextElement();
      net.learn();
      if ((net.getName().compareTo("NounInstanceNet") == 0) ||
          (net.getName().compareTo("VerbInstanceNet") == 0))
        net.fastLearn();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Parse4Net net = (Parse4Net)eNum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Parse4Net net = (Parse4Net)eNum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
//if (net.getName().compareTo("VerbNet") == 0)   System.out.println(net.neurons[0].getFatigue() +   " verb Neuron " + net.neurons[0].getActivation());
    }
  }

  public CANTNet getNewNet(String name,int cols, int rows,int topology){
    Parse4Net net = new Parse4Net (name,cols,rows,topology);
    return (net);
  } 

  
  //------------------------Set up patterns for the input words
  private void makeNewWordPattern(int word) {
    CANTPattern readPattern;
    int [] patternPoints = new int[100];
    int cPoints=0;

    //make the pattern 
    for (int i = 0; i < 100; i++) {
	patternPoints[i] = i+(word*100);
    }
    readPattern = new CANTPattern(this,"word",word,100,patternPoints);
 
    //stick the pattern into the pattern vector
    try {
      patterns.add(readPattern);
    }
    catch (ArrayIndexOutOfBoundsException e) {
     System.err.println("problem setting pattern element in modify pattern\n" +
                         e.toString());
    }

  }
  public void setInputPatterns() {
    for (int word = 0; word < numWords; word++) {
      makeNewWordPattern(word);
    }
  }

  //------------------------Topology Specification
  private int inputCASize = 100;
  //A subnet with every fifth neuron inhibitory.  No intra-subnet connections.
  private void setInputTopology() {
    int CASize = inputCASize;
    int totalCAs = getSize()/CASize;
    for (int CA=0; CA < totalCAs; CA++) 
      {
      for (int neuronInCA=0; neuronInCA<CASize;neuronInCA++) {
        int fromNeuron = neuronInCA+(CA*CASize);
        
        //every fifth CA is inhibitory
        if ((neuronInCA%5) == 0)
          {neurons[fromNeuron].setInhibitory(true);}
        else //not inhibitory
          {neurons[fromNeuron].setInhibitory(false);}
      }
    }
  }

  private void setFiftyFiftySubCA(double weight, int fromNeuron,int toOffset) {
    for (int synapse = 0; synapse < 5; synapse++) {
      //synapse to each of the other 5 neurons on the other side of the 
      //simpleCA
      int toNeuron = toOffset + synapse;
      if ((fromNeuron %10 )< 5) toNeuron += 5;
      addConnection(fromNeuron,toNeuron, weight);
      }
  }
  //this creates a topology where the network is broken into subCAs of length
  //featureSize; when one half is activated it will oscillate between the two.
  //it makes all neurons excitatory.
  private void setFiftyFiftyTopology(double weight) {
    int featureSize=10;
    int simpleCAs=getSize()/featureSize;
    for (int simpleCA = 0; simpleCA < simpleCAs; simpleCA++) {
      for (int neuron = 0; neuron < featureSize; neuron++) {
        int fromNeuron = (simpleCA*featureSize) + neuron;
        neurons[fromNeuron].setInhibitory(false);
        setFiftyFiftySubCA(weight,fromNeuron,simpleCA*featureSize);
      }
    }
  }
  private void changeFiftyFiftyTopology(double weight,int start, int finish) {
    int featureSize=10;
    for (int simpleCA = start; simpleCA < finish; simpleCA++) {
      for (int neuron = 0; neuron < featureSize; neuron++) {
        int fromNeuron = (simpleCA*featureSize) + neuron;
        if ((neuron%5) != 0){
          neurons[fromNeuron].setInhibitory(false);
          setFiftyFiftySubCA(weight,fromNeuron,simpleCA*featureSize);
	}
      }
    }
  }
  
  //this creates a topology where the network is broken into subCAs of length
  //featureSize; when one half is activated it will oscillate between the two.
  //It makes 80% of neurons excitatory.
  private void setFiftyFiftyTopology2(double weight) {
    int featureSize=10;
    int simpleCAs=getSize()/featureSize;
    for (int simpleCA = 0; simpleCA < simpleCAs; simpleCA++) {
      for (int neuron = 0; neuron < featureSize; neuron++) {
        int fromNeuron = (simpleCA*featureSize) + neuron;
        if ((neuron%5) == 0){
          //no inhibitory connections.
          neurons[fromNeuron].setInhibitory(true);
	}        
        else {
          neurons[fromNeuron].setInhibitory(false);
          setFiftyFiftySubCA(weight,fromNeuron,simpleCA*featureSize);
	}
      }
    }
  }

  //Of the 20 neuron feature sets they are 4NI4FI4FI4NI. The Ns support
  //the running once 1 set is activated.
  private void setFiftyFiftyBindSubCA(int fromNeuron,int toOffset) {
    double weight=1.1;
    if ((fromNeuron %20) < 5) {
      for (int synapse = 0; synapse < 5; synapse++) {
        //synapse to each of the other 5 neurons on the other side of the 
        //simpleCA
        int toNeuron = toOffset + synapse + 5;
        addConnection(fromNeuron,toNeuron, weight);
        toNeuron += 10;
        addConnection(fromNeuron,toNeuron, weight);
      }
    }
    else if ((fromNeuron %20) > 15) {
      for (int synapse = 0; synapse < 5; synapse++) {
        //synapse to each of the other 5 neurons on the other side of the 
        //simpleCA
        int toNeuron = toOffset + synapse ;
        addConnection(fromNeuron,toNeuron, weight);
        toNeuron -= 10;
        addConnection(fromNeuron,toNeuron, weight);
      }
    }
  }
  
  //Two CAs wordActive, and barOneActive.
  //All excitatory neurons. Two 50/50 CAs so half are always on.
  private int wordActiveSize = 100;
  private void setBarOneTopology() {
    setFiftyFiftyTopology2(1.1);
  }

  //Break it into CAs that are sized 100, with the idea that each will
  //50/50 persist as barone
  //Connections from input (CA specific 4 at .45 on each cycle) and barOne 
  //(general 2 in each cycle at .0) ignite a particular Noun Access CA.
  //With decay 2 neither is sufficient alone, and the 50/50 persist keeps
  //half on with 0,1,or both input and barOne CAs on.
  private void setNounAccessTopology() {
    setFiftyFiftyTopology(0.9);
  }
  
  //Every fifth neuron is inhibitory.
  //This is a simple structure of 50/50 CAs.  They're piecewise in portions
  //of 1.  
  private int nounFeatureSize = 10;
  private void setNounSemTopology() {
    setFiftyFiftyTopology2(1.1);
  }

  private int ruleSize=100;
  private void ruleInhibitsRule(int inhibittingRule,int inhibittedRule) {
    int inhibittingStart = inhibittingRule*ruleSize;
    for (int i = 0; i < ruleSize; i+=5) {
      int fromNeuron = i+ (inhibittingRule*ruleSize);
      for (int synapse=0; synapse<5;synapse++) {
        int toNeuron = i+synapse+(inhibittedRule*ruleSize);
        addConnection(fromNeuron,toNeuron, 1.0); //this is inhibitory
      }
    }
  }
  private void rulesInhibitEachOther(int rule1,int rule2) {
    ruleInhibitsRule(rule1,rule2);
    ruleInhibitsRule(rule2,rule1);
  }

  private void ruleInhibitsRule(int inhibittingRule,int inhibittedRule,
    double val) {
    int inhibittingStart = inhibittingRule*ruleSize;
    for (int i = 0; i < ruleSize; i+=5) {
      int fromNeuron = i+ (inhibittingRule*ruleSize);
      for (int synapse=0; synapse<5;synapse++) {
        int toNeuron = i+synapse+(inhibittedRule*ruleSize);
        addConnection(fromNeuron,toNeuron, val); //this is inhibitory
      }
    }
  }

  private void prepDoneStartsReadNext() {
    for (int i = 0; i < ruleSize; i++) {
      int fromNeuron = 1000+ i;
      int toNeuron = i+300;
      if ((i %10) > 4) toNeuron -=5;
      if ((i%5) != 0) {
        addConnection(fromNeuron,toNeuron, 2.1); 
        if ((i%5) == 1)
          addConnection(fromNeuron,toNeuron-1, 2.1); 
      }
    }
  }

  //Every fifth neuron is inhibitory.
  //This is a simple structure of 50/50 CAs.  They're piecewise in portions
  //of 10.  
  private int ruleFeatureSize = 10;
  private void setRuleOneTopology() {
    setFiftyFiftyTopology2(1.1);
    changeFiftyFiftyTopology(1.15,10,20);
    //simple NP
    ruleInhibitsRule(0,1); //NPInst -( NPFromN
    ruleInhibitsRule(0,2); //NPInst -( NPDone
    ruleInhibitsRule(1,2,0.6); //NPFromN -( NPDone //lesser weight for timing
    ruleInhibitsRule(2,1); //NPDone -( NPFromN
    //simple VP
    //ruleInhibitsRule(4,5); //VPInst -( MainV
    ruleInhibitsRule(4,6); //VPInst -( VPDone
    ruleInhibitsRule(5,6); //MainV -( VPDone
    ruleInhibitsRule(6,5); //VPDone -( MainV 
    //kill read word
    ruleInhibitsRule(0,3); //NPInst -( Read Word
    ruleInhibitsRule(4,3); //NPInst -( Read Word
    //Preps
    ruleInhibitsRule(9,10); //NPAddPrep -( PrepDone
    ruleInhibitsRule(10,9); //PrepDone -( NPAddPrep 

    ruleInhibitsRule(10,2); //PrepDone -( NPDone

    prepDoneStartsReadNext();
  }
  private void setRuleTwoTopology() {
    setFiftyFiftyTopology2(1.1);
    //without preps 
    rulesInhibitEachOther(3,6); //VPFromVPObj 11 )-( VPFromNPActVP 11

    //with preps
    rulesInhibitEachOther(9,12); //VPFromVPLoc 11 )-( NPfromPPNP 12
    rulesInhibitEachOther(10,12); //VPFromVPLoc 12 )-( NPfromPPNP 12
    rulesInhibitEachOther(10,13); //VPFromVPLoc 12 )-( NPfromPPNP 23
    rulesInhibitEachOther(11,13); //VPFromVPLoc 13 )-( NPfromPPNP 23
    rulesInhibitEachOther(15,12); //VPFromVPInst 11 )-( NPfromPPNP 12
    rulesInhibitEachOther(16,12); //VPFromVPInst 12 )-( NPfromPPNP 12
    rulesInhibitEachOther(16,13); //VPFromVPInst 12 )-( NPfromPPNP 23
    rulesInhibitEachOther(17,13); //VPFromVPInst 13 )-( NPfromPPNP 23
    rulesInhibitEachOther(9,15); //VPFromVPLoc 11 )-( VPFromVPInst 11
    rulesInhibitEachOther(10,16); //VPFromVPLoc 12 )-( VPFromVPInst 12
    rulesInhibitEachOther(11,17); //VPFromVPLoc 13 )-( VPFromVPInst 13 

    rulesInhibitEachOther(12,13); //NPfromPPNP 12 )-( NPfromPPNP 23
  }

  private int counterCAFeatures = 10;
  private int counterCAForwardFeatures = 5;
  private void setForwardConnect(int fromCA, int toCA) {
    int counterCASize = counterCAFeatures*10;
    for (int feature = 0; feature < counterCAForwardFeatures; feature++) {
      for (int i = 1; i < 5; i++){
        int fromNeuron = (fromCA*counterCASize) + (feature*10) + i;
        int toNeuron = (toCA*counterCASize) + (feature*10) + i;
        addConnection(fromNeuron, toNeuron, 2.9);
        addConnection(fromNeuron, toNeuron+50, 2.9);
        if (i == 1) {
          addConnection(fromNeuron, toNeuron-1, 2.9);
          addConnection(fromNeuron, toNeuron+49, 2.9);
	}
      }
    }
  }
  private void setBackConnect(int fromCA, int toCA) {
    int counterCASize = counterCAFeatures*10;
    for (int feature = 0; feature < counterCAFeatures; feature++) {
      for (int i = 0; i < 6; i+=5){
        int fromNeuron = (fromCA*counterCASize) + (feature*10) + i;
        for (int j=0;j<5;j++) {
           int toNeuron = (toCA*counterCASize) + (feature*10) + i+j;
           addConnection(fromNeuron, toNeuron, 5.0);
        }
      }
    }
  }

  private void setCounterTopology() {
    //setFiftyFiftyTopology2(1.1);
    int neuron = 0;
    for (int CAs = 0; CAs < 6; CAs ++) {
      for (int feature = 0; feature < 10; feature ++) {
        for (int i = 0; i < 10; i++) {
	  neuron = (CAs*100)+(feature*10) + i;
          if ((neuron%5) == 0) neurons[neuron].setInhibitory(true);
          else neurons[neuron].setInhibitory(false);
          if ((feature < 5) && (!neurons[neuron].getInhibitory())) {
            setFiftyFiftySubCA(1.1,neuron,(CAs*100)+(feature*10));
	  }
        }
      }
    }

    setForwardConnect(0,1);
    setForwardConnect(1,2);
    setForwardConnect(2,3);
    setForwardConnect(4,5);
    setBackConnect(1,0);
    setBackConnect(2,1);
    setBackConnect(3,2);
    setBackConnect(5,4);
  }

  //0-39 are base neurons
  //40-59 is the done feature
  //Every 5th is normal inhibitory
  //The rest are fastbind features that are  
  //20 neuron feature sets they are 4NI4FI4FI4NI
  private boolean nounInstanceNeuronFastBind(int neuron) {
    if (neuron < 80) return false;
    if (neuron >= 260) return false;
    if ((neuron%5) == 0) return false;
    if ((neuron %20) < 5) return false;
    if ((neuron %20) > 15) return false;
    return true;
  }
  //There are 8 features for a NP: prepon, main-noun, prepbind, det, adj1, 
  //adj2, ppmod,relclause; this is incomplete but that's what we're doing now.
  //The 9th feature is not for binding but is on if its done.
  //This gets turned on when the NP is done and ensures the barone features
  //are off. Add an extra feature that binds to the rest to store which
  //are on.
  //We'll do it with a base instance (40 neurons) + 20 for each of the 10
  //features.
  private int nInstCASize = 500;
  private void createNounInstanceNeurons(int synapses) {
    neurons = new CANTNeuron[cols*rows];
    for (int CA=0; CA<getSize()/nInstCASize;CA++) {
      for (int i=0;i< nInstCASize;i++) {
        if (nounInstanceNeuronFastBind(i)) 
          neurons[i+(CA*nInstCASize)]= 
	    //new CANTNeuronFastBind(totalNeurons++,this,0.001);
          new CANTNeuronFastBind(totalNeurons++,this,0.0005,900);
        else {
          neurons[i+(CA*nInstCASize)] = new CANTNeuron(totalNeurons++,this,
            synapses);
          if (((i %5) == 0) && (i < nounInstanceTimeStart))
            neurons[i+(CA*nInstCASize)].setInhibitory(true);
          else
            neurons[i+(CA*nInstCASize)].setInhibitory(false);
	}
      }
    }
  }

  //used for noun instance
  //first 40 are all excitatory, second 20 are 50/50 weith 20% inhibitory
  //rest are fastbind features
  private void setNounFeaturesInstanceTopology(){
    int CASize = nInstCASize;
    int featureSize=10;
    int CAs=getSize()/CASize;
    for (int CA = 0; CA < CAs; CA++) {
      //set up the features with no inhibitory neurons
      for (int feature = 0; feature < 4; feature ++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
	  int fromNeuron = (CA*CASize) + (feature*featureSize) + neuron;
          neurons[fromNeuron].setInhibitory(false);
          setFiftyFiftySubCA(0.9,fromNeuron,
            (CA*CASize) + (feature*featureSize));
        }
      }
      //set up the features 50/50 with inhibitory neurons
      for (int feature = 4; feature < 8; feature ++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
	  int fromNeuron = (CA*CASize) + (feature*featureSize) + neuron;
          if ((neuron%5) == 0){
            //no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
	  }        
          else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftySubCA(1.1,fromNeuron,
              (CA*CASize) + (feature*featureSize));
	  }
        }
      }
      //set up the fastbind features
      for (int feature = 8; feature < 26 ; feature ++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
	  int fromNeuron = (CA*CASize) + (feature*featureSize) + neuron;
          if ((neuron%5) == 0){
            //no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
	  }        
          else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftyBindSubCA(fromNeuron,
              (CA*CASize) + (feature*featureSize));
	  } 
        } 
      } 
      //set up the features 50/50 with inhibitory neurons
      for (int feature = 26; feature < 30; feature ++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
	  int fromNeuron = (CA*CASize) + (feature*featureSize) + neuron;
          if ((neuron%5) == 0){
            //no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
	  }        
          else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftySubCA(1.1,fromNeuron,
              (CA*CASize) + (feature*featureSize));
	  }
        }
      }
} }

  private int nounInstanceTimeStart = 300;
  //This component shows how long the instance has been running by
  //the number of neurons firing. The fewer the longer.
  //There are groups of 8 neurons that go off together.
  private void setNounInstanceTimeTopology(){
    double bigWeight = 2.4;
    double weight2 = 0.8;
    for (int CA = 0; CA < 3; CA++) {
      int offset = nounInstanceTimeStart + (CA*nInstCASize);
      for (int neuronA = 0; neuronA < 40; neuronA++) {
        int neuronB = neuronA + 40;
        int neuronC = neuronA + 80;
        int neuronD = neuronA + 120;
  
        int neuronA2 = neuronA ;
        if ((neuronA2%2) == 1) 
          neuronA2 -= 1;
        else neuronA2 +=1;
        int neuronB2 = neuronA2 + 40;
        int neuronC2 = neuronA2 + 80;
        int neuronD2 = neuronA2 + 120; 
        double weight1=bigWeight+(neuronA/2)*.05;
        addConnection(neuronA+offset,neuronB+offset,weight1);
        addConnection(neuronB+offset,neuronC+offset,weight1);
        addConnection(neuronC+offset,neuronD+offset,weight1);
        addConnection(neuronD+offset,neuronA+offset,weight1);
        addConnection(neuronA+offset,neuronA2+offset,weight2);
        addConnection(neuronA+offset,neuronC+offset,weight2);
        addConnection(neuronA+offset,neuronD+offset,weight2);
        addConnection(neuronB+offset,neuronA+offset,weight2);
        addConnection(neuronB+offset,neuronB2+offset,weight2);
        addConnection(neuronB+offset,neuronD+offset,weight2);
        addConnection(neuronC+offset,neuronA+offset,weight2);
        addConnection(neuronC+offset,neuronB+offset,weight2);
        addConnection(neuronC+offset,neuronC2+offset,weight2);
        addConnection(neuronD+offset,neuronB+offset,weight2);
        addConnection(neuronD+offset,neuronC+offset,weight2);
        addConnection(neuronD+offset,neuronD2+offset,weight2);
      }
    }
  }

  //The base of each word connects to its features so that they get
  //half the strength necessary to ignite.
  //Each neuron (including the fastbind neurons) synapse to all the
  //5 neurons in their group. So each feature neuron has 8 normal and
  //3 fastbind inputs. 8/1.2 = 0.15
  private void connectNounInstanceBaseToFeatures() {
    for (int CA=0; CA<getSize()/nInstCASize;CA++) {
      for (int baseNeuron=0;baseNeuron< 40;baseNeuron++) {
	for (int feature=0;feature<9;feature++) {
          int fromNeuron=baseNeuron+(CA*nInstCASize);
          for (int synapse=0;synapse< 5;synapse++) {
            int toNeuron = ((baseNeuron/5)*5)%20; //0,5,10,15
            toNeuron+=synapse+40+(feature*20);
            toNeuron += CA*nInstCASize;
            if (feature == 2)
              addConnection(fromNeuron,toNeuron, 0.3); 
            else
              addConnection(fromNeuron,toNeuron, 0.15); 
          } } } } }

  //The others on needs to have connections to all the other features
  //(except np done, and prepon) so it can  bind to them and turn them 
  //on during printResults
  private void connectNIOthersOnToOthers() {
    for (int CA=0; CA<getSize()/nInstCASize;CA++) {
      for (int baseNeuron=80;baseNeuron< 100;baseNeuron++) {
	for (int feature=0;feature<6;feature++) {
          if (feature == 4) feature =5; //skip the done feature
          int fromNeuron=(CA*nInstCASize)+baseNeuron;
          for (int synapse=0;synapse< 10;synapse++) {
            int toNeuron = ((baseNeuron/10)*10)%20; //0,10
            toNeuron+=synapse+100+(feature*20)+(CA*nInstCASize);
            addConnection(fromNeuron,toNeuron, 0.01); 
          } } } } }

  //Bind connects to the base to turn it on when
  private void connectBindToBase() {
    for (int CA=0; CA<getSize()/nInstCASize;CA++) {
      for (int baseNeuron=266;baseNeuron< 280;baseNeuron++) {
        if (baseNeuron == 270) baseNeuron=276;
	for (int feature=0;feature<2;feature++) {
          int fromNeuron=(CA*nInstCASize)+baseNeuron;
          if (!neurons[fromNeuron].isInhibitory()) {
            for (int synapse=0;synapse<5;synapse++) {
              int toNeuron = ((baseNeuron/10)*10)%20; //0,10
              toNeuron+=synapse+(feature*20)+(CA*nInstCASize)+5;
              addConnection(fromNeuron,toNeuron, 1.001); 
	    } } } } } }

  //The bound slot turns off the bind slot so no one else can bind to it.
  private void connectBoundToBind() {
    for (int CA=0; CA<getSize()/nInstCASize;CA++) {
      for (int baseNeuron=280;baseNeuron< 300;baseNeuron++) {
        int fromNeuron=(CA*nInstCASize)+baseNeuron;
        if (neurons[fromNeuron].isInhibitory()) {
          for (int synapse=0;synapse<5;synapse++) {
            int toNeuron = (baseNeuron -20) +synapse ;
            toNeuron+=(CA*nInstCASize);
            addConnection(fromNeuron,toNeuron, 4.0); 
          } } } } }

  private void nounInstanceDoneExtinguishBarOneFeatures() {
    for (int CA=0; CA<getSize()/nInstCASize;CA++) {
      for (int doneNeuron=40;doneNeuron< 60;doneNeuron+=5) {
        for (int feature=0;feature<4;feature++) { //the 1st 4 are barone feats
          int fromNeuron=doneNeuron+(CA*nInstCASize);
          for (int synapse=0;synapse< 10;synapse++) {
            int toNeuron = (((doneNeuron/5)*5)+synapse)%20; //0,5,10,15
            toNeuron+=100+(feature*20);
            /*
            int toNeuron = (((doneNeuron/5)*5))%20; //0,5,10,15
            toNeuron+=synapse+100+(feature*20);
            */
            toNeuron += CA*nInstCASize;
            addConnection(fromNeuron,toNeuron, 2.0); 
          } } } } }

  private void prepBindPrimePrepOn() {
    for (int CA=0; CA<getSize()/nInstCASize;CA++) {
      for (int neuron=100;neuron<120;neuron++) {
        if (neuron == 105) neuron=116;
        int fromNeuron=neuron+(CA*nInstCASize);
        if (!neurons[fromNeuron].isInhibitory()) {
          int toNeuron = fromNeuron - 40;
          addConnection(fromNeuron,toNeuron, 0.5); 
          if ((fromNeuron %5) == 1)
            addConnection(fromNeuron,toNeuron-1, 0.5); 
        }
      }
    }
  }

  private void prepOnExtinguishPrepBind() {
    for (int CA=0; CA<getSize()/nInstCASize;CA++) {
      for (int doneNeuron=60;doneNeuron<80;doneNeuron+=5) {
        int fromNeuron=doneNeuron+(CA*nInstCASize);
        for (int synapse=0;synapse< 10;synapse++) {
          int toNeuron = ((doneNeuron/5)*5)%20; //0,5,10,15
          toNeuron+=synapse+100; //100 is the prep bind feature offset
          toNeuron += CA*nInstCASize;
          addConnection(fromNeuron,toNeuron, 2.0); 
        } } } }

  private void connectAPPModToAnInstance(int fromInst, int toInst) {
    for (int baseNeuron=206;baseNeuron< 215;baseNeuron++) {
      int fromNeuron=(fromInst*nInstCASize)+baseNeuron;
      if (!neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 10; synapse++ ){
          int toNeuron = synapse;
          if (synapse >= 5) toNeuron +=5;
          toNeuron += (toInst*nInstCASize)+260;
          addConnection(fromNeuron,toNeuron,0.02);
        } } } }

  private void connectPPModToInstances() {
    connectAPPModToAnInstance(0,1);
    connectAPPModToAnInstance(1,2);
  }
  private void setNounInstanceTopology() {
    setNounFeaturesInstanceTopology();
    setNounInstanceTimeTopology();
    connectNounInstanceBaseToFeatures();
    connectNIOthersOnToOthers();
    connectBindToBase();
    connectBoundToBind();
    nounInstanceDoneExtinguishBarOneFeatures();
    prepBindPrimePrepOn();
    prepOnExtinguishPrepBind();
    connectPPModToInstances();
  }

  //For a CA the first 120 are normal
  //The remaining 180 are in fast bind feature sets of 20 neurons.
  //Every 5th is normal inhibitory.
  //Of the 20 neuron feature sets they are 4NI4FI4FI4NI
  private boolean verbInstanceNeuronFastBind(int neuron) {
    if (neuron < 120) return false;
    if (neuron >= verbInstanceTimeStart) return false;
    if ((neuron%5) == 0) return false;
    if ((neuron %20) < 5) return false;
    if ((neuron %20) > 15) return false;
    return true;
  }
  private int vInstCASize = 500;
  private void createVerbInstanceNeurons(int synapses) {
    neurons = new CANTNeuron[cols*rows];
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int i=0;i< vInstCASize;i++) {
        if (verbInstanceNeuronFastBind(i)) 
          neurons[i+(CA*vInstCASize)] = new CANTNeuronFastBind(totalNeurons++,
            this, 0.0005); 
        else {
          neurons[i+(CA*vInstCASize)] = new CANTNeuron(totalNeurons++,this,
            synapses);
          if (((i %5) == 0) && (i < verbInstanceTimeStart))
            neurons[i+(CA*vInstCASize)].setInhibitory(true);
          else
            neurons[i+(CA*vInstCASize)].setInhibitory(false);
	}

      }
    }
  }

  //First 40 are all excitatory (base); features are 20 long
  //Next 4 features are 50/50 with 20% inhibitory
  //Rest are fastbind features
  private void setVerbFeaturesInstanceTopology() {
    int CASize = vInstCASize;
    int featureSize=10;
    int CAs=getSize()/CASize;
    for (int CA = 0; CA < CAs; CA++) {
      //set up the features with no inhibitory neurons
      for (int feature = 0; feature < 4; feature ++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
	  int fromNeuron = (CA*CASize) + (feature*featureSize) + neuron;
          neurons[fromNeuron].setInhibitory(false);
          setFiftyFiftySubCA(0.9,fromNeuron,(CA*CASize)+(feature*featureSize));
        }
      }
      //set up the features with inhibitory neurons
      for (int feature = 4; feature < 12 ; feature ++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
	  int fromNeuron = (CA*CASize) + (feature*featureSize) + neuron;
          if ((neuron%5) == 0){
            //no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
	  }        
          else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftySubCA(1.1,fromNeuron,
              (CA*CASize) + (feature*featureSize));
	  } } }

      //set up the fastbind features
      for (int feature = 12; feature < 26 ; feature ++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
	  int fromNeuron = (CA*CASize) + (feature*featureSize) + neuron;
          if ((neuron%5) == 0){
            //no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
	  }        
          else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftyBindSubCA(fromNeuron,
              (CA*CASize) + (feature*featureSize));
	  }
        }
      }

      //set up the features with inhibitory neurons
      for (int feature = 26; feature < 28 ; feature ++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
	  int fromNeuron = (CA*CASize) + (feature*featureSize) + neuron;
          if ((neuron%5) == 0){
            //no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
	  }        
          else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftySubCA(1.1,fromNeuron,
              (CA*CASize) + (feature*featureSize));
	  } } }

    }
  }

  private int verbInstanceTimeStart = 300;
  //This component shows how long the instance has been running by
  //the number of neurons firing. The fewer the longer.
  //There are groups of 8 neurons that go off together.
  private void setVerbInstanceTimeTopology() {
    double bigWeight = 2.4;
    double weight2 = 0.8;
    for (int CA = 0; CA < 1; CA++) {
      int offset = verbInstanceTimeStart + (CA*vInstCASize);
      for (int neuronA = 0; neuronA < 40; neuronA++) {
        int neuronB = neuronA + 40;
        int neuronC = neuronA + 80;
        int neuronD = neuronA + 120;
  
        int neuronA2 = neuronA ;
        if ((neuronA2%2) == 1) 
          neuronA2 -= 1;
        else neuronA2 +=1;
        int neuronB2 = neuronA2 + 40;
        int neuronC2 = neuronA2 + 80;
        int neuronD2 = neuronA2 + 120; 
        double weight1=bigWeight+(neuronA/2)*.05;
        addConnection(neuronA+offset,neuronB+offset,weight1);
        addConnection(neuronB+offset,neuronC+offset,weight1);
        addConnection(neuronC+offset,neuronD+offset,weight1);
        addConnection(neuronD+offset,neuronA+offset,weight1);
        addConnection(neuronA+offset,neuronA2+offset,weight2);
        addConnection(neuronA+offset,neuronC+offset,weight2);
        addConnection(neuronA+offset,neuronD+offset,weight2);
        addConnection(neuronB+offset,neuronA+offset,weight2);
        addConnection(neuronB+offset,neuronB2+offset,weight2);
        addConnection(neuronB+offset,neuronD+offset,weight2);
        addConnection(neuronC+offset,neuronA+offset,weight2);
        addConnection(neuronC+offset,neuronB+offset,weight2);
        addConnection(neuronC+offset,neuronC2+offset,weight2);
        addConnection(neuronD+offset,neuronB+offset,weight2);
        addConnection(neuronD+offset,neuronC+offset,weight2);
        addConnection(neuronD+offset,neuronD2+offset,weight2);
      }
    }
  }

  private void connectVerbInstanceBaseToFeatures() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int baseNeuron=0;baseNeuron< 40;baseNeuron++) {
	for (int feature=0;feature<13;feature++) {
          int fromNeuron=baseNeuron+(CA*vInstCASize);
          for (int synapse=0;synapse< 5;synapse++) {
            int toNeuron = ((baseNeuron/5)*5)%20; //0,5,10,15
            toNeuron+=synapse+40+(feature*20);
            toNeuron+=(CA*vInstCASize);
            if (feature == 5)
              addConnection(fromNeuron,toNeuron, 0.30); 
            //skip obj, loc, inst and act done
            else if ((feature != 1) && (feature !=2) && (feature !=3) && 
                     (feature != 11))
              addConnection(fromNeuron,toNeuron, 0.15); 
          }
        }
      }
    }
  }
  private void verbInstanceDoneExtinguishBarOneFeatures() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int doneNeuron=40;doneNeuron< 60;doneNeuron+=5) {
        for (int feature=0;feature<1;feature++) { //just base verb
          int fromNeuron=doneNeuron+(CA*vInstCASize);
          for (int synapse=0;synapse< 10;synapse++) {
            int toNeuron = CA*vInstCASize+(feature*20)+120;
	    toNeuron+=((synapse+doneNeuron)%20);
            addConnection(fromNeuron,toNeuron, 4.0); 
          }
        }
      }
    }
  }
  //The others on needs to have connections to all the other features
  //(except np done) so it can  bind to them and turn them on during
  //printResults
  private void connectVIOthersOnToOthers() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int baseNeuron=146;baseNeuron< 154;baseNeuron++) {
        if (baseNeuron != 150) {
	  for (int feature=0;feature<7;feature++) {
            if (feature != 1) { //skip self
              int fromNeuron=(CA*vInstCASize)+baseNeuron;
              for (int synapse=0;synapse< 5;synapse++) {
                int toNeuron = synapse+120+(feature*20)+(CA*vInstCASize);
                addConnection(fromNeuron,toNeuron, 0.01); //0-4
                toNeuron += 15;
                addConnection(fromNeuron,toNeuron, 0.01); //15-19
              } } } } } } }

  private void vIObjToObjDone() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int neuron=181;neuron< 200;neuron++) {
        if (neuron == 185) neuron=196;
        int fromNeuron = neuron+(CA*vInstCASize);
        int toNeuron = fromNeuron-120;
        addConnection(fromNeuron,toNeuron, 2.45);
        if ((neuron %5) ==1)
          addConnection(fromNeuron,toNeuron-1, 2.45);
      } } }

  private void vIObjDoneExtinguishObj() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int neuron=0;neuron< 4;neuron++) {
        int fromNeuron = (neuron*5)+60+(CA*vInstCASize);
        for (int synapse = 0 ; synapse < 5 ;synapse++) {
          int toNeuron = fromNeuron+120+synapse;
          addConnection(fromNeuron,toNeuron, 10);
	}
      }
    }
  }

  private void vIActToActDone() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int neuron=161;neuron< 180;neuron++) {
        if (neuron == 165) neuron=176;
        int fromNeuron = neuron+(CA*vInstCASize);
        int toNeuron = fromNeuron-80;
        addConnection(fromNeuron,toNeuron, 2.45);
        if ((neuron %5) ==1)
          addConnection(fromNeuron,toNeuron-1, 2.45);
      } } }
  private void vIActDoneExtinguishAct() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int neuron=0;neuron< 4;neuron++) {
        int fromNeuron = (neuron*5)+80+(CA*vInstCASize);
        for (int synapse = 0 ; synapse < 5 ;synapse++) {
          int toNeuron = fromNeuron+80+synapse;
          addConnection(fromNeuron,toNeuron, 10);
	}
      }
    }
  }

  private void vILocToLocDone() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int neuron=241;neuron< 260;neuron++) {
        if (neuron == 245) neuron=256;
        int fromNeuron = neuron+(CA*vInstCASize);
        int toNeuron = fromNeuron-140;
        addConnection(fromNeuron,toNeuron, 2.45);
        if ((neuron %5) ==1)
          addConnection(fromNeuron,toNeuron-1, 2.45);
      } } }

  private void vILocDoneExtinguishLoc() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int neuron=0;neuron< 4;neuron++) {
        int fromNeuron = (neuron*5)+100+(CA*vInstCASize);
        for (int synapse = 0 ; synapse < 5 ;synapse++) {
          int toNeuron = fromNeuron+140+synapse;
          addConnection(fromNeuron,toNeuron, 10);
	} } } }  

  private void vIInstToInstDone() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int neuron=221;neuron< 240;neuron++) {
        if (neuron == 225) neuron=236;
        int fromNeuron = neuron+(CA*vInstCASize);
        int toNeuron = fromNeuron+40;
        addConnection(fromNeuron,toNeuron, 2.45);
        if ((neuron %5) ==1)
          addConnection(fromNeuron,toNeuron-1, 2.45);
      } } }

  private void vIInstDoneExtinguishInst() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int neuron=0;neuron< 4;neuron++) {
        int fromNeuron = (neuron*5)+260+(CA*vInstCASize);
        for (int synapse = 0 ; synapse < 5 ;synapse++) {
          int toNeuron = fromNeuron-40+synapse;
          addConnection(fromNeuron,toNeuron, 10);
	} } } }  

  private void setVerbInstanceTopology() {
    setVerbFeaturesInstanceTopology();
    setVerbInstanceTimeTopology();
    connectVerbInstanceBaseToFeatures();
    connectVIOthersOnToOthers();
    verbInstanceDoneExtinguishBarOneFeatures();
    vIObjToObjDone();
    vIObjDoneExtinguishObj();
    vIActToActDone();
    vIActDoneExtinguishAct();
    vILocToLocDone();
    vILocDoneExtinguishLoc();
    vIInstToInstDone();
    vIInstDoneExtinguishInst();
  }

  private void setOtherWordTopology() {
    setFiftyFiftyTopology(0.9);
  }

  int preferenceCASize = 60;
  private void setRuleSelectionTopology() {
    setFiftyFiftyTopology(0.9);
  }

  private int nextWordSteps=10;
  private void setNextWordTopology() {
    for (int i = 0; i< (nextWordSteps -1) *10; i++) {
      neurons[i].setInhibitory(false);
      addConnection(i,i+10, 4.1);
    }
  }

  public void initializeNeurons() {
      System.out.println("parse input topology "+topology);

    if (topology == 1){
      createNeurons(50);
      System.out.println("parse input topology ");
      setInputTopology();
    }
    else if (topology == 2){
      createNeurons(100);
      System.out.println("bar 1 topology ");
      setBarOneTopology();
    }
    else if (topology == 3){
      createNeurons(60);
      System.out.println("n/v access topology ");
      setNounAccessTopology();
    }
    else if (topology == 4){
      createNeurons(50);
      System.out.println("n/v sem topology ");
      setNounSemTopology();
    }
    else if (topology == 5){
      createNeurons(200);
      System.out.println("rule one topology ");
      setRuleOneTopology();
    }
    else if (topology == 6){
      createNounInstanceNeurons(140);
      System.out.println("noun instance topology ");
      setNounInstanceTopology();
    }
    else if (topology == 7){
      createVerbInstanceNeurons(170);
      System.out.println("verb instance topology ");
      setVerbInstanceTopology();
    }
    else if (topology == 8){
      createNeurons(35);
      System.out.println("other word topology ");
      setOtherWordTopology();
    }
    else if (topology == 9){
      createNeurons(70);
      System.out.println("rule selection topology ");
      setRuleSelectionTopology();
    }
    else if (topology == 10){
      createNeurons(180);
      System.out.println("rule two topology ");
      setRuleTwoTopology();
    }
    else if (topology == 11){
      createNeurons(20);
      System.out.println("counter topology ");
      setCounterTopology();
    }
    else if (topology == 12){
      createNeurons(10);
      System.out.println("next word topology ");
      setNextWordTopology();
    }
    else System.out.println("bad toppology specified "+ topology);
  }

  //-----Connect Nets to Each Other-----------
  //This works for both nounAccess and verb Access
  private void connectOneInputToOneAccess(Parse4Net accessNet,
    int inputCA, int accessCA) {
    for (int neuronNum=0; neuronNum<inputCASize;neuronNum++) {
      int fromNeuron = (inputCA*inputCASize) + neuronNum;
      for (int synapseNum=0;synapseNum < 2; synapseNum++) {
        int toNeuron=neuronNum + (accessCA*100);
        if ((toNeuron%10) >=5) toNeuron-=5;
        neurons[fromNeuron].addConnection(accessNet.neurons[toNeuron],
          0.9);
      }
    }
  }

  public void connectInputToNounAccess(Parse4Net nounAccessNet) {
    connectOneInputToOneAccess (nounAccessNet,1,0); //left
    connectOneInputToOneAccess (nounAccessNet,6,1); //pyramid
    connectOneInputToOneAccess (nounAccessNet,7,2); //it
    connectOneInputToOneAccess (nounAccessNet,8,3); //stalactite
    connectOneInputToOneAccess (nounAccessNet,9,4); //I
    connectOneInputToOneAccess (nounAccessNet,11,5); //gun
    connectOneInputToOneAccess (nounAccessNet,13,6); //girl
    connectOneInputToOneAccess (nounAccessNet,15,7); //telescope
    connectOneInputToOneAccess (nounAccessNet,16,8); //door
    connectOneInputToOneAccess (nounAccessNet,17,9); //handle
    connectOneInputToOneAccess (nounAccessNet,18,10); //right
    connectOneInputToOneAccess (nounAccessNet,19,11); //forward
    connectOneInputToOneAccess (nounAccessNet,20,12); //backward
    connectOneInputToOneAccess (nounAccessNet,21,13); //wrong
    connectOneInputToOneAccess (nounAccessNet,22,14); //that
    connectOneInputToOneAccess (nounAccessNet,28,15); //boy
    connectOneInputToOneAccess (nounAccessNet,29,16); //barrier
  }

  public void connectInputToVerbAccess(Parse4Net verbAccessNet) {
    connectOneInputToOneAccess (verbAccessNet,0,0); //move
    connectOneInputToOneAccess (verbAccessNet,3,1); //turn
    connectOneInputToOneAccess (verbAccessNet,10,2); //found
    connectOneInputToOneAccess (verbAccessNet,12,3); //saw
    connectOneInputToOneAccess (verbAccessNet,24,4); //go
    connectOneInputToOneAccess (verbAccessNet,25,5); //is
    connectOneInputToOneAccess (verbAccessNet,26,6); //center
  }

  public void connectInputToOther(Parse4Net otherNet) {
    connectOneInputToOneAccess (otherNet,2,0); //.period
    connectOneInputToOneAccess (otherNet,4,1); //toward
    connectOneInputToOneAccess (otherNet,5,2); //the
    connectOneInputToOneAccess (otherNet,14,3); //with
    connectOneInputToOneAccess (otherNet,23,4); //to
    connectOneInputToOneAccess (otherNet,27,5); //dangerous
  }

  //This works for nounAccess, verbAccess, and other
  //each noun access is primed by the wordActive CA.  The particular word
  //selects which nounAccess is actually turned on.
  public void connectBarOneToAccess(Parse4Net accessNet) {
    for (int accessCA = 0; accessCA < accessNet.getSize()/100;accessCA++) {
      for (int fromNeuron=0; fromNeuron<wordActiveSize;fromNeuron++) {
        int toNeuron=fromNeuron + (accessCA*100);
        if ((toNeuron%10) >=5) toNeuron-=5;
          neurons[fromNeuron].addConnection(accessNet.neurons[toNeuron],0.9);
      }
    }
  }
  
  //Need to activate the first half of each rule feature above 2. In
  //combination with the NounAccess this ignites it. Each neuron connects
  //to one of those so 2->1.  Each has 1.25 weights so it goes above
  //2 but stays below 3.
  private void connectWordActToActNInstRule(Parse4Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron=(neuron/ruleFeatureSize)*ruleFeatureSize;
      toNeuron += (neuron%(ruleFeatureSize/2));
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.25);
    }
  }
  //reducing just this weight from 1.25 to 1 means it takes an extra cycle
  private void connectWordActToNPFromNRule(Parse4Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      if (!neurons[neuron].isInhibitory()) {
        int toNeuron=(neuron/10)*10;
        toNeuron += (neuron%5)+100;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.1);
        if ((neuron%5)==1) //make up for the inhibitory neuron
          neurons[neuron].addConnection(ruleNet.neurons[toNeuron-1],1.1);
      }
    }
  }

  //The barone CA extinguishes (or prevents) the NInst Rule.
  private void connectBarOneToInhibNInstRule(Parse4Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron+=5) {
      int toNeuron=(neuron - 100);
      for (int synapse = 0; synapse < 10; synapse++) {
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron++],-1.25);
        toNeuron%=100;
      }
    }
  }

  private void connectWordActToNPDoneRule(Parse4Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      if (!neurons[neuron].isInhibitory()) {
        int toNeuron=(neuron/10)*10;
        toNeuron += (neuron%5)+200;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.0);
        if ((neuron%5)==1) //make up for the inhibitory neuron
          neurons[neuron].addConnection(ruleNet.neurons[toNeuron-1],1.0);
      }
    }
  }

  //Need to activate the first half of each rule feature above 2. In
  //combination with the NounAccess this ignites it. Each neuron connects
  //to one of those so 2->1.  Each has 1.25 weights so it goes above
  //2 but stays below 3.
  private void connectWordActToActVInstRule(Parse4Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron=(neuron/ruleFeatureSize)*ruleFeatureSize;
      toNeuron += (neuron%(ruleFeatureSize/2)) + 400;
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.25);
    }
  }

  //The barone CA extinguishes (or prevents) the VInst Rule.
  private void connectBarOneToInhibVInstRule(Parse4Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron+=5) {
      int toNeuron=neuron%100 ;
      for (int synapse = 0; synapse < 10; synapse++) {
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron++ + 400],-1.25);
        toNeuron%=100;
      }
    }
  }
  //like connectWordActToNPFromNRule
  private void connectWordActToMainVerbRule (Parse4Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron=(neuron/ruleFeatureSize)*ruleFeatureSize;
      toNeuron += (neuron%(ruleFeatureSize/2)+500);
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.0);
    }
  }
  private void connectBarOneToVPDoneRule(Parse4Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron++) {
      if (!neurons[neuron].isInhibitory()) {
        int toNeuron=(neuron/10)*10;
        toNeuron += (neuron%5)+500;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.1);
        if ((neuron%5)==1) //make up for the inhibitory neuron
          neurons[neuron].addConnection(ruleNet.neurons[toNeuron-1],1.1);
      }
    }
  }

  private void connectWordActToNPAddAdjRule(Parse4Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron=(neuron/ruleFeatureSize)*ruleFeatureSize;
      toNeuron += (neuron%(ruleFeatureSize/2)+800);
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.25);
    }
  }
  //The barone prevents bar two rules.
  private void connectBarOneToInhibBarTwoRule(int toStart, Parse4Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron+=5) {
      for (int synapse = 0; synapse < 5; synapse++) {
        int toNeuron=neuron%100;
        if ((neuron%10) < 5)
          toNeuron += synapse+toStart;
        else 
          toNeuron += (-synapse) -1 +toStart;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron],-1.25);
      }
    }
  }
  //modified connectWordActToNPFromNRule
  private void connectWordActToNPAddPrepRule(Parse4Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron=(neuron/ruleFeatureSize)*ruleFeatureSize;
      toNeuron += (neuron%(ruleFeatureSize/2)+900);
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.0);
    }
  }
  private void connectBarOneToPrepDoneRule(Parse4Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron++) {
      if (!neurons[neuron].isInhibitory()) {
        int toNeuron=(neuron/10)*10;
        toNeuron += (neuron%5)+900;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.2);
        if ((neuron%5)==1) //make up for the inhibitory neuron
          neurons[neuron].addConnection(ruleNet.neurons[toNeuron-1],1.2);
      }
    }
  }
  private void connectWordActToNPAddDetRule(Parse4Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron=(neuron/ruleFeatureSize)*ruleFeatureSize;
      toNeuron += (neuron%(ruleFeatureSize/2)+1100);
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.25);
    }
  }
  public void connectBarOneToRuleOne(Parse4Net ruleNet) {
    connectWordActToActNInstRule(ruleNet);
    connectWordActToNPFromNRule(ruleNet);
    connectBarOneToInhibNInstRule(ruleNet);
    connectWordActToNPDoneRule(ruleNet);
    connectWordActToActVInstRule(ruleNet);
    connectBarOneToInhibVInstRule(ruleNet);
    connectWordActToMainVerbRule(ruleNet);
    connectBarOneToVPDoneRule(ruleNet);
    connectWordActToNPAddAdjRule(ruleNet);
    connectWordActToNPAddPrepRule(ruleNet);
    connectBarOneToPrepDoneRule(ruleNet);
    connectWordActToNPAddDetRule(ruleNet);
  }
  public void connectBarOneToRuleTwo(Parse4Net ruleNet) {
    connectBarOneToInhibBarTwoRule(300,ruleNet); //VP -> VP NPObj 1 1
    connectBarOneToInhibBarTwoRule(400,ruleNet); //VP -> VP NPObj 1 2
    connectBarOneToInhibBarTwoRule(600,ruleNet); //VP -> NPAct VP 1 1
    connectBarOneToInhibBarTwoRule(900,ruleNet); //VP -> VP PPloc 1 1
    connectBarOneToInhibBarTwoRule(1000,ruleNet); //VP -> VP PPloc 1 2
    connectBarOneToInhibBarTwoRule(1100,ruleNet); //VP -> VP PPloc 1 3
    connectBarOneToInhibBarTwoRule(1200,ruleNet); //NP -> NPPP 1 2
    connectBarOneToInhibBarTwoRule(1300,ruleNet); //NP -> NPPP 2 3
    connectBarOneToInhibBarTwoRule(1500,ruleNet); //VP -> VP PPinst 1 1
    connectBarOneToInhibBarTwoRule(1600,ruleNet); //VP -> VP PPinst 1 2
    connectBarOneToInhibBarTwoRule(1700,ruleNet); //VP -> VP PPinst 1 3
  }

  //10 connections to 1 neuron. .25 gives 2.5 each cycle, so 2.5, 3.75 >4
  private void connectOneAccessToOneSemFeature(Parse4Net semNet,
    int accessCA, int semFeature) {

    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (accessCA*100) + neuron;
      int toNeuron = (neuron%5) + (semFeature*nounFeatureSize);
      neurons[fromNeuron].addConnection(semNet.neurons[toNeuron],0.25);
    }
  }

  private void connectOneAccessToOneSemFeature(Parse4Net semNet,
    int accessCA, int semFeature, int hierStartFeature) {
    for (int feature = semFeature*3; feature < (semFeature*3) +3; feature++ ){
      connectOneAccessToOneSemFeature(semNet,accessCA,
        hierStartFeature+feature);
    }
  }

  //works for both nounaccess->nounSem and verbAccess -> verbSem
  private void connectOneAccessToOneSemWord(Parse4Net semNet,
    int accessCA, int semWord) {
    for (int feature = semWord*6; feature < (semWord*6) +6; feature++ ){
      connectOneAccessToOneSemFeature(semNet,accessCA,feature);
    }
  }

  private int nounHierStart = 240*3; 
  private LineNumberReader openWordFile() {
    DataInputStream dIS;
    InputStreamReader inputSR;
    LineNumberReader inputFile=null;
 
    try{
      dIS = new DataInputStream(new FileInputStream(Parse4.wordFile));
      inputSR = new InputStreamReader(dIS);
      inputFile = new LineNumberReader (inputSR);
    }
   
    catch (IOException e) {
      System.err.println("word file not opened properly\n" +
                          e.toString());
      System.exit(1);
    }
    return inputFile;
  }

  //loop through all the entries until the end of file sticking in all nouns
  private void readNounSems(Parse4Net nounSemNet, 
				LineNumberReader inputFile){
    StringTokenizer tokenizedLine;
    int nounAccessOffset = 0;
    try {
      String inputLine=inputFile.readLine();
      while (inputLine != null) {
	  //System.out.println(inputLine);
        tokenizedLine = new StringTokenizer(inputLine);
        String wordString = tokenizedLine.nextToken();
        String lexClass = tokenizedLine.nextToken();
        if (lexClass.compareTo("noun") == 0) {
          String paramString = tokenizedLine.nextToken();
          int baseSem = Integer.parseInt(paramString);
          connectOneAccessToOneSemWord(nounSemNet,nounAccessOffset,baseSem);
          while (tokenizedLine.hasMoreTokens()) {
            paramString = tokenizedLine.nextToken();
            int hierSem = Integer.parseInt(paramString);
            connectOneAccessToOneSemFeature(nounSemNet,nounAccessOffset,
              hierSem, nounHierStart);
	  }
          nounAccessOffset++;
	}
        inputLine= inputFile.readLine();
	}
    }
    catch (IOException e) {
      System.err.println("word readline problem\n" + e.toString());
      System.exit(1);
    }
  }

  private void readNounSemFile(Parse4Net nounSemNet) {
    LineNumberReader inputFile=openWordFile();
    readNounSems(nounSemNet,inputFile);
    try {
      inputFile.close();
      }
     catch (IOException e) {
      System.err.println("word n file not closed \n" + e.toString());
      System.exit(1);}
  }
  public void connectNounAccessToSem(Parse4Net nounSemNet) {
    readNounSemFile(nounSemNet);
  }

  private int verbHierStart = 120*3; 
  //loop through all the entries until the end of file sticking in all verbs
  private void readVerbSems(Parse4Net verbSemNet, 
				LineNumberReader inputFile){
    StringTokenizer tokenizedLine;
    int verbAccessOffset = 0;
    try {
      String inputLine=inputFile.readLine();
      while (inputLine != null) {
        tokenizedLine = new StringTokenizer(inputLine);
        String wordString = tokenizedLine.nextToken();
        String lexClass = tokenizedLine.nextToken();
        if (lexClass.compareTo("verb") == 0) {
	    //System.out.println(inputLine);
          String paramString = tokenizedLine.nextToken();
          int baseSem = Integer.parseInt(paramString);
          connectOneAccessToOneSemWord(verbSemNet,verbAccessOffset,baseSem);
          while (tokenizedLine.hasMoreTokens()) {
            paramString = tokenizedLine.nextToken();
            int hierSem = Integer.parseInt(paramString);
            connectOneAccessToOneSemFeature(verbSemNet,verbAccessOffset,
              hierSem, verbHierStart);
	  }
          verbAccessOffset++;
	}
        inputLine= inputFile.readLine();
	}
    }
    catch (IOException e) {
      System.err.println("word readline problem\n" + e.toString());
      System.exit(1);
    }
  }
  private void readVerbSemFile(Parse4Net verbSemNet) {
    LineNumberReader inputFile=openWordFile();
    readVerbSems(verbSemNet,inputFile);
    try {
      inputFile.close();
      }
     catch (IOException e) {
      System.err.println("word v file not closed \n" + e.toString());
      System.exit(1);}
  }
  public void connectVerbAccessToSem(Parse4Net verbSemNet) {
    readVerbSemFile(verbSemNet);
  }


  //The active access word primes but does not activate the word.
  //10 connections to 1 neuron. .125 gives 1.25 each cycle, so 1.25, 1.8725,
  // ... < 3
  private void connectNounAccessWordToActNInstRule(int word, 
    Parse4Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  private void connectNounAccessWordToNPFromN(int word, 
    Parse4Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 100;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.11);
      }
    }
  }
  private void connectNounAccessWordToNPDone(int word, 
    Parse4Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 200;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.11);
      }
    }
  }

  public void connectNounAccessToRuleOne(Parse4Net ruleNet) {
    for (int word = 0; word < numNouns; word ++) {
      connectNounAccessWordToActNInstRule(word,ruleNet);
      connectNounAccessWordToNPFromN(word,ruleNet);
      connectNounAccessWordToNPDone(word,ruleNet);
    }
  }
  
  //4 neurons to 5
  private void NewNInstStartBarOne(Parse4Net barOneNet) {
    for (int neuron=1; neuron<100;neuron++) {
      if ((neuron%5) != 0) { //skip inhibitory neuron.
        for (int synapse=0;synapse<5;synapse++) {
          int toNeuron = ((neuron/5)*5)+synapse+100;
          neurons[neuron].addConnection(barOneNet.neurons[toNeuron],1.1);
        }
      }
    }
  }
  //NPDone turns off both bar one and wordon
  private void NPDoneExtinguishBarOne(Parse4Net barOneNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<5;synapse++) {
        int toNeuron = neuron+synapse;
        int fromNeuron = neuron+200; //NPDoneOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
        toNeuron += 100;
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
      }
    }
  }
  //The Read Next Rule turns on the Word On CA by itself
  private void ReadNextIgnitesWordOn(Parse4Net barOneNet) {
    for (int neuron = 300; neuron < 400; neuron++) {
      if ((neuron%5) != 0) { //skip inhibitory neuron.
        for (int synapse=0;synapse<5;synapse++) {
          int toNeuron = ((neuron/5)*5)+synapse-300;
          neurons[neuron].addConnection(barOneNet.neurons[toNeuron],1.1);
        }
      }
    }
  }
  private void NewVInstStartBarOne(Parse4Net barOneNet) {
    for (int neuron=401; neuron<500;neuron++) {
      if ((neuron%5) != 0) { //skip inhibitory neuron.
        for (int synapse=0;synapse<5;synapse++) {
          int toNeuron = ((neuron/5)*5)+synapse-300;
          neurons[neuron].addConnection(barOneNet.neurons[toNeuron],1.1);
        }
      }
    }
  }
  //like NPDoneExtinguishBarOne
  private void VPDoneExtinguishBarOne(Parse4Net barOneNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<5;synapse++) {
        int toNeuron = neuron+synapse;
        int fromNeuron = neuron+600; 
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
        toNeuron += 100;
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
      } } }

  private void NPAddAdjExtinguishWordOn(Parse4Net barOneNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<5;synapse++) {
        int toNeuron = neuron+synapse;
        int fromNeuron = neuron+800; //NPAddDetOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
      }
    }
  }
  private void PrepDoneExtinguishWordOn(Parse4Net barOneNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<5;synapse++) {
        int toNeuron = neuron+synapse;
        int fromNeuron = neuron+1000; //PrepDoneOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
      } } }
  private void NPAddDetExtinguishWordOn(Parse4Net barOneNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<5;synapse++) {
        int toNeuron = neuron+synapse;
        int fromNeuron = neuron+1100; //NPAddDetOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
      }
    }
  }

  public void connectRuleOneToBarOne(Parse4Net barOneNet) {
    NewNInstStartBarOne(barOneNet);
    NPDoneExtinguishBarOne(barOneNet);
    ReadNextIgnitesWordOn(barOneNet);
    NewVInstStartBarOne(barOneNet);
    VPDoneExtinguishBarOne(barOneNet);
    NPAddAdjExtinguishWordOn(barOneNet);
    PrepDoneExtinguishWordOn(barOneNet);
    NPAddDetExtinguishWordOn(barOneNet);
  }

  private void connectNPFromNToMainFeature(Parse4Net nounInstanceNet) {
    for (int neuron=100;neuron<200;neuron++) {
      for (int word=0;word< 3;word ++) {
        if (neuron%5 !=0) { //skip the inhibitory ones
          int toNeuron = (neuron%20);
          //the internal neurons are stimulated by the external
	  if ((toNeuron <5) || (toNeuron>14)) {  
            toNeuron += (word*nInstCASize)+120;
            neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
              0.25);
            if (toNeuron%5 ==1) { //make up for the inhibitory one
              neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron-1]
                ,0.25);
	    }
	  }
	}
      }
    }
  }
  private void connectNPDoneToDoneFeature(Parse4Net nounInstanceNet) {
    for (int neuron=200;neuron<300;neuron++) {
      for (int word=0;word< 3;word ++) {
        if (neuron%5 !=0) { //skip the inhibitory ones
          int toNeuron = (neuron%20);
          toNeuron += (word*nInstCASize)+40;
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
            0.25);
          if (toNeuron%5 ==1) { //make up for the inhibitory one
            neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron-1],
              0.25);
	  }
	}
      }
    }
  }

  private void connectNPAddAdjToAdjFeature(Parse4Net nounInstanceNet) {
    for (int neuron=800;neuron<900;neuron++) {
      for (int word=0;word< 3;word ++) {
        if (neuron%5 !=0) { //skip the inhibitory ones
          int toNeuron = (neuron%20);
          toNeuron += (word*nInstCASize)+140;
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
            0.3); //.25
          if (toNeuron%5 ==1) { //make up for the inhibitory one
            neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron-1],
              0.3);
	  }}}}}
  private void connectNPAddPrepToPrepFeature(Parse4Net nounInstanceNet) {
    for (int neuron=900;neuron<1000;neuron++) {
      for (int word=0;word< 3;word ++) {
        if (neuron%5 !=0) { //skip the inhibitory ones
          int toNeuron = (neuron%20);
          toNeuron += (word*nInstCASize)+100;
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
            0.25);
          if (toNeuron%5 ==1) { //make up for the inhibitory one
            neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron-1],
              0.25);
	  }}}}}
  //To get this feature on you need the base features, the rule and
  //the prep bind feature.
  private void connectPrepDoneToPrepOnFeature(Parse4Net nounInstanceNet) {
    for (int neuron=1000;neuron<1100;neuron++) {
      for (int word=0;word< 3;word ++) {
        if (neuron%5 !=0) { //skip the inhibitory ones
          int toNeuron = (neuron%20);
          toNeuron += (word*nInstCASize)+60;
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
	    0.125); 
          if (toNeuron%5 ==1) { //make up for the inhibitory one
            neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron-1],
              0.125);
	  }}}}}
  private void connectNPAddDetToDetFeature(Parse4Net nounInstanceNet) {
    for (int neuron=1100;neuron<1200;neuron++) {
      for (int word=0;word< 3;word ++) {
        if (neuron%5 !=0) { //skip the inhibitory ones
          int toNeuron = (neuron%20);
          toNeuron += (word*nInstCASize)+140;
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
            0.3); //.25
          if (toNeuron%5 ==1) { //make up for the inhibitory one
            neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron-1],
              0.3);
	  }}}}}

  public void connectRuleOneToNounInstance(Parse4Net nounInstanceNet) {
    connectNPFromNToMainFeature(nounInstanceNet);
    connectNPDoneToDoneFeature(nounInstanceNet);
    connectNPAddAdjToAdjFeature(nounInstanceNet);
    connectNPAddPrepToPrepFeature(nounInstanceNet);
    connectPrepDoneToPrepOnFeature(nounInstanceNet);
    connectNPAddDetToDetFeature(nounInstanceNet);
  }

  //when an actor is added to a vp it should then be closed.  This
  //does not do it quite right.
  private void vPAddActStopsInstance(Parse4Net nounInstanceNet, int ruleStart,
    int inst) {
    for (int neuron=0;neuron<100;neuron+=5) {
      int fromNeuron = neuron + ruleStart;
      int toNeuron=((neuron/5)*2)+(inst*100);
      neurons[fromNeuron].addConnection(nounInstanceNet.neurons[toNeuron],
        -10.01); 
      neurons[fromNeuron].addConnection(nounInstanceNet.neurons[toNeuron+1],
        -10.01); 
    }
  }

  private void connectVPAddNPToABoundFeature(int ruleStart,int inst,
    Parse4Net nounInstanceNet) {
    for (int neuron=ruleStart;neuron<ruleStart+100;neuron++) {
      if (!neurons[neuron].isInhibitory()) {  //80->10
	int toNeuron=(neuron%100)/10;  //0..10
        if (toNeuron >= 5) toNeuron +=5; //0..4,10..14
        toNeuron += 280 + (inst*nInstCASize);
        neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
          0.38); //.34 takes 11 cycles
      }
    }
  }
  private void connectVPAddActToBoundFeature(Parse4Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(600,0,nounInstanceNet);
  }
  private void connectVPAddActToDoneFeature(Parse4Net nounInstanceNet) {
    for (int neuron=600;neuron<700;neuron++) {
      if (!neurons[neuron].isInhibitory()) {  //80->10
	int toNeuron=(neuron%100)/10;  //0..10
        if (toNeuron >= 5) toNeuron +=5; //0..4,10..14
        toNeuron += 180;
        neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],0.6);
      }
    }
  }
  private void connectVPAddObjToBoundFeature(Parse4Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(300,0,nounInstanceNet);
    connectVPAddNPToABoundFeature(400,1,nounInstanceNet);
  }
  private void connectVPAddInstToBoundFeature(Parse4Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(1500,0,nounInstanceNet);
    connectVPAddNPToABoundFeature(1600,1,nounInstanceNet);
    connectVPAddNPToABoundFeature(1700,2,nounInstanceNet);
  }
  private void connectVPAddLocToBoundFeature(Parse4Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(900,0,nounInstanceNet);
    connectVPAddNPToABoundFeature(1000,1,nounInstanceNet);
    connectVPAddNPToABoundFeature(1100,2,nounInstanceNet);
  }

  private void connectNPAddPPToBoundFeature(Parse4Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(1200,1,nounInstanceNet);
    connectVPAddNPToABoundFeature(1300,2,nounInstanceNet);
  }

  private void connectNPAddPPToPPModFeature(int toNounInst, int ruleStart, 
    Parse4Net nounInstanceNet) {
    for (int neuron=ruleStart;neuron<ruleStart+100;neuron++) {
      if (neuron%5 !=0) { //skip the inhibitory ones
        int toNeuron = (neuron%20);
        toNeuron += (toNounInst*nInstCASize)+200;
        neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
           0.5); //.25
        if (toNeuron%5 ==1) { //make up for the inhibitory one
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron-1],
            0.5);
	}
      }
    }
  }

  public void connectRuleTwoToNounInstance(Parse4Net nounInstanceNet) {
    connectVPAddActToBoundFeature(nounInstanceNet);
    connectVPAddActToDoneFeature(nounInstanceNet);
    connectVPAddObjToBoundFeature(nounInstanceNet);
    connectVPAddInstToBoundFeature(nounInstanceNet);
    connectVPAddLocToBoundFeature(nounInstanceNet);
    connectNPAddPPToBoundFeature(nounInstanceNet);
    connectNPAddPPToPPModFeature(0,1200,nounInstanceNet);
    connectNPAddPPToPPModFeature(1,1300,nounInstanceNet);

    //vPAddActStopsInstance(nounInstanceNet,600,0);
  }
  
  private void NPDoneExtinguishNounAccessWord(int word,Parse4Net nounAccessNet)
  {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<10;synapse++) {
        int toNeuron = ((neuron+synapse)%100)+(word*100);
        int fromNeuron = neuron+200; //NPDoneOffset
        neurons[fromNeuron].addConnection(nounAccessNet.neurons[toNeuron],
          -2.0);
      }
    }
  }
  public void connectRuleOneToNounAccess(Parse4Net nounAccessNet) {
    for (int word = 0; word < numNouns; word ++) {
      NPDoneExtinguishNounAccessWord(word,nounAccessNet);
    }
  }

  private void VPDoneExtinguishVerbAccessWord(int word,Parse4Net verbAccessNet)
  {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<10;synapse++) {
	int toNeuron = neuron+synapse+(word*100);
        int fromNeuron = neuron+600; //NPDoneOffset
        neurons[fromNeuron].addConnection(verbAccessNet.neurons[toNeuron],
          -2.0);
      }
    }
  }
  public void connectRuleOneToVerbAccess(Parse4Net verbAccessNet) {
    for (int word = 0; word < numVerbs; word ++) {
      VPDoneExtinguishVerbAccessWord(word,verbAccessNet);
    }
  }
  private void prepDoneExtinguishAPrep(int word,Parse4Net otherNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<10;synapse++) {
	int toNeuron = neuron+synapse+(word*100);
        int fromNeuron = neuron+1000; //PrepDoneOffset
        neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron],-2.0);
      }
    }
  }
  private void prepDoneExtinguishPreps(Parse4Net otherNet) {
    prepDoneExtinguishAPrep(1,otherNet); //toward
    prepDoneExtinguishAPrep(3,otherNet); //with
    prepDoneExtinguishAPrep(4,otherNet); //to
  }

  private void NPAddDetExtinguishADet(int word,Parse4Net otherNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<10;synapse++) {
	int toNeuron = neuron+synapse+(word*100);
        int fromNeuron = neuron+1100; //NPAddDetOffset
        neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron],-2.0);
      }
    }
  }
  private void NPAddDetExtinguishDets(Parse4Net otherNet) {
    NPAddDetExtinguishADet(2,otherNet); //toward
  }

  private void NPAddAdjExtinguishAdjs(Parse4Net otherNet) {
    int word = 5;
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<10;synapse++) {
	int toNeuron = neuron+synapse+(word*100);
        int fromNeuron = neuron+800; //NPAddAdjOffset
        neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron],-2.0);
      }
    }
  }
  public void connectRuleOneToOther(Parse4Net otherNet) {
    prepDoneExtinguishPreps(otherNet);
    NPAddDetExtinguishDets(otherNet);
    NPAddAdjExtinguishAdjs(otherNet);
  }

  //like connectNPFromNToMainFeature.  Connect the rule to the main
  //main feature so that, in conjuction with the active instance,
  //the main feature comes on
  private void connectMainVerbToMainFeature(Parse4Net verbInstanceNet) {
    for (int neuron=500;neuron<600;neuron++) {
      for (int word=0;word< 2;word ++) {
        if (neuron%5 !=0) { //skip the inhibitory ones
          int toNeuron = (neuron%20);
          toNeuron += (word*vInstCASize)+120;
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron],
            0.4); //.25
          if (toNeuron%5 ==1) { //make up for the inhibitory one
            neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron-1],
              0.4);
	  }
	}
      }
    }
  }
  private void connectVPDoneToDoneFeature(Parse4Net verbInstanceNet) {
    for (int neuron=600;neuron<700;neuron++) {
      for (int word=0;word< 2;word ++) {
        if (neuron%5 !=0) { //skip the inhibitory ones
          int toNeuron = (neuron%20);
          toNeuron += (word*vInstCASize)+40;
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron],
            0.3);
          if (toNeuron%5 ==1) { //make up for the inhibitory one
            neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron-1],
              0.3);
	  }
	}
      }
    }
  }
  private void connectVPFromVPNPObjToObjFeature(int instance, int ruleStart,
    Parse4Net verbInstanceNet) {
    for (int neuron=ruleStart;neuron<ruleStart+100;neuron++) {
      if (neuron%5 !=0) { //skip the inhibitory ones
        int toNeuron = (neuron%20);
        toNeuron += (instance*vInstCASize)+180;
        neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron],0.6);
        if (toNeuron%5 ==1) { //make up for the inhibitory one
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron-1],
            0.6);
        } } } }
  private void connectVPFromVPPPLocToLocFeature(int instance, int ruleStart,
    Parse4Net verbInstanceNet) {
    for (int neuron=ruleStart;neuron<ruleStart+100;neuron++) {
      if (neuron%5 !=0) { //skip the inhibitory ones
        int toNeuron = (neuron%20);
        toNeuron += (instance*vInstCASize)+240;
        neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron],0.6);
        if (toNeuron%5 ==1) { //make up for the inhibitory one
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron-1],
            0.6);
        } } } }
  private void connectVPFromVPPPInstToInstFeature(int instance, int ruleStart,
    Parse4Net verbInstanceNet) {
    for (int neuron=ruleStart;neuron<ruleStart+100;neuron++) {
      if (neuron%5 !=0) { //skip the inhibitory ones
        int toNeuron = (neuron%20);
        toNeuron += (instance*vInstCASize)+220;
        neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron],0.4);
        if (toNeuron%5 ==1) { //make up for the inhibitory one
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron-1],
            0.4);
        } } } }

  private void connectVPFromNPActVPToActFeature(int instance, int ruleStart,
    Parse4Net verbInstanceNet) {
    for (int neuron=ruleStart;neuron<ruleStart+100; neuron++) {
      if (neuron%5 !=0) { //skip the inhibitory ones
        int toNeuron = (neuron%20);
        toNeuron += (instance*vInstCASize)+160;
        neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron],0.6);
        if (toNeuron%5 ==1) { //make up for the inhibitory one
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron-1],
            0.6);
        } 
      } 
    } 
  } 

  public void connectRuleOneToVerbInstance(Parse4Net verbInstanceNet) {
    connectMainVerbToMainFeature(verbInstanceNet);
    connectVPDoneToDoneFeature(verbInstanceNet);
  }
  public void connectRuleTwoToVerbInstance(Parse4Net verbInstanceNet) {
    connectVPFromVPNPObjToObjFeature(0,300,verbInstanceNet);
    connectVPFromVPNPObjToObjFeature(0,400,verbInstanceNet);
    connectVPFromNPActVPToActFeature(0,600,verbInstanceNet);
    connectVPFromVPPPLocToLocFeature(0,900,verbInstanceNet);
    connectVPFromVPPPLocToLocFeature(0,1000,verbInstanceNet);
    connectVPFromVPPPLocToLocFeature(0,1100,verbInstanceNet);
    connectVPFromVPPPInstToInstFeature(0,1500,verbInstanceNet);
    connectVPFromVPPPInstToInstFeature(0,1600,verbInstanceNet);
    connectVPFromVPPPInstToInstFeature(0,1700,verbInstanceNet);
  }

  public void connectRuleTwoToNextWord(Parse4Net nextWordNet) {
    connectARuleToNextWord(0,nextWordNet);
    connectARuleToNextWord(3,nextWordNet);
    connectARuleToNextWord(4,nextWordNet);
    connectARuleToNextWord(6,nextWordNet);
    connectARuleToNextWord(9,nextWordNet);
    connectARuleToNextWord(10,nextWordNet);
    connectARuleToNextWord(11,nextWordNet);
    connectARuleToNextWord(12,nextWordNet);
    connectARuleToNextWord(13,nextWordNet);
    connectARuleToNextWord(15,nextWordNet);
    connectARuleToNextWord(16,nextWordNet);
    connectARuleToNextWord(17,nextWordNet);
  }


  //When a new instance rule fires up, send activation to counter.
  //In combination with the current counter, it should move on.
  private void connectARuleToACounter(int rule, int counter,
    Parse4Net counterNet) {
    int ruleSize= 100;
    int counterSize= 100;
    for (int i = 1; i < 100; i++) {
      int fromNeuron = (rule*ruleSize)+i;
      int toNeuron = (counter*counterSize)+i;
      neurons[fromNeuron].addConnection(counterNet.neurons[toNeuron],0.4);
      if ((i%10)==1) 
        neurons[fromNeuron].addConnection(counterNet.neurons[toNeuron-1],0.4);
      if ((i%10)==4) i+=6;
    }
  }
  public void connectRuleOneToCounter(Parse4Net counterNet) {
    connectARuleToACounter(0,1,counterNet);
    connectARuleToACounter(0,2,counterNet);
    connectARuleToACounter(0,3,counterNet);
    connectARuleToACounter(4,5,counterNet);
  }

  private void connectARuleToStartNextWord(int rule, Parse4Net nextWordNet) {
    int ruleSize= 100;
    for (int set = 0; set < 20; set ++) {
      for (int i = 1; i < 5; i++) {
        int fromNeuron = (rule*ruleSize)+(set*5)+i;
        int toNeuron = i;
        neurons[fromNeuron].addConnection(nextWordNet.neurons[toNeuron],4.1);
        neurons[fromNeuron].addConnection(nextWordNet.neurons[toNeuron+5],4.1);
        if (i==1) {
          neurons[fromNeuron].addConnection(nextWordNet.neurons[toNeuron-1],4.1);
          neurons[fromNeuron].addConnection(nextWordNet.neurons[toNeuron+4],4.1);
        }
      }
    }
  }
  private void connectARuleToStopNextWord(int rule, Parse4Net nextWordNet) {
    int ruleSize= 100;
    for (int i = 10; i < 100; i+=10) {
      int fromNeuron = (rule*ruleSize)+i;
      for (int j = 0; j < 10; j++) {
        int toNeuron = i+j;
        neurons[fromNeuron].addConnection(nextWordNet.neurons[toNeuron],-0.5);
      }
    }
    for (int i = 15; i < 100; i+=10) {
      int fromNeuron = (rule*ruleSize)+i;
      for (int j = 0; j < 10; j++) {
        int toNeuron = i+j-5;
        neurons[fromNeuron].addConnection(nextWordNet.neurons[toNeuron],-0.5);
      }
    }
  }
  private void connectARuleToNextWord(int rule, Parse4Net nextWordNet) {
    connectARuleToStartNextWord(rule, nextWordNet);
    connectARuleToStopNextWord(rule, nextWordNet);
  }
  public void connectRuleOneToNextWord(Parse4Net nextWordNet) {
    connectARuleToNextWord(0,nextWordNet);
    connectARuleToNextWord(1,nextWordNet);
    connectARuleToNextWord(2,nextWordNet);
    connectARuleToNextWord(4,nextWordNet);
    connectARuleToNextWord(5,nextWordNet);
    connectARuleToNextWord(6,nextWordNet);
    connectARuleToNextWord(8,nextWordNet);
    connectARuleToNextWord(9,nextWordNet);
    connectARuleToNextWord(11,nextWordNet);
  }

  private void connectMainNounToWord(int instance,int word,
    Parse4Net nounAccessNet) {
    for (int neuron = 126; neuron<135;neuron++) {
      if ((neuron%5) != 0) {
        //each of the 50 access neurons (5-9, 15-19 ...95-99)gets 8 connections
        //connect to the right side of the feature because the left
        //side will activate during binding.
        for (int accessGroup = 0; accessGroup < 5; accessGroup ++) {
          int fromNeuron = neuron + (instance*nInstCASize);
          for (int synapse=0;synapse<5;synapse++) {
            int toNeuron = word*100; 
            toNeuron += accessGroup*20;
            toNeuron+=synapse+5;
            neurons[fromNeuron].addConnection(
              nounAccessNet.neurons[toNeuron],0.01);
	    toNeuron += 10;
            neurons[fromNeuron].addConnection(
              nounAccessNet.neurons[toNeuron],0.01);
	  }
	}
      }
    }
  }  
  public void connectNounInstanceToNounAccess(Parse4Net nounAccessNet) {
    for (int instance = 0; instance < 3; instance++) {
      for (int word = 0; word < numNouns; word++) {
        //connect main verb feature to verbAccess
        connectMainNounToWord(instance,word,nounAccessNet);
      }
    }
  }

  private void prepOnInhibitsPrepDone(Parse4Net ruleOneNet) {
    for (int inst = 0; inst < 3; inst++) {
      int fromStart=inst*nInstCASize;
      for (int neuron = 60; neuron<79;neuron+=5) {
        int fromNeuron = neuron + fromStart;
        for (int synapse = 0; synapse < 13; synapse ++) { //4 neurons to 50
          int toNeuron = (neuron%20)/5; //0,1,2,3
          toNeuron = (toNeuron*13)+synapse;//0-51
          toNeuron = ((toNeuron/5)*10)+(toNeuron%5); //0..4,10..14,
          toNeuron %=100; //keep in the rule
          toNeuron += 1000; //ruleStart;
          neurons[fromNeuron].addConnection(ruleOneNet.neurons[toNeuron],-3);
        } 
      }
    }
  }
  public void connectNounInstanceToRuleOne(Parse4Net ruleOneNet) {
      //    prepOnInhibitsPrepDone(ruleOneNet);
  }

  //Connect base part of noun instance to rule.
  //Then connect the decaying part to the rule.
  private void connectNounInstanceToRule(int fromInstance,double weight,
                                         int toStart, double dynamicWeight,
                                         Parse4Net ruleNet) {
    int fromStart=fromInstance*nInstCASize;
    //for each neuron in the body 40 excitatory neurons
    //half of the rule neurons get 4 connections (the rest 0)
    for (int neuron = 0; neuron<40;neuron++) {
      int fromNeuron = neuron + fromStart;
      for (int synapse = 0; synapse < 5; synapse ++) {
	int toNeuron = ((synapse*40)+neuron); //equally distribute from 0-200
        toNeuron %=100; //equally distribute 0-100
        if ((toNeuron%10) >= 5) toNeuron-=5; //just the first 5 of any 10
        toNeuron += toStart; 
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],weight);
      }
    }
    if (dynamicWeight == 0) return;
    //Connect the dynamic part
    for (int neuronPair = 0; neuronPair < 80; neuronPair++) {
      int fromNeuron = (neuronPair*2) + nounInstanceTimeStart+ fromStart;
      int toNeuron = toStart;
      for (int synapse = 0; synapse < 25; synapse++) {
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],
          dynamicWeight);
        if ((toNeuron%10) == 4) toNeuron += 5;
        toNeuron++;
        neurons[fromNeuron+1].addConnection(ruleNet.neurons[toNeuron],
          dynamicWeight);
        if ((toNeuron%10) == 4) toNeuron += 5;
        toNeuron++;
      
      }
    }
  }

  private void connectPrepOnToRule(int inst,int ruleStart,
    Parse4Net ruleNet) {
    int fromStart=inst*nInstCASize;
    for (int neuron = 61; neuron<80;neuron++) {
      int fromNeuron = neuron + fromStart;
      if (!neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 7; synapse ++) { //16 neurons to 50*2
          int toNeuron = (neuron-60); //1-4, 6-9, 11-14 16-19
          toNeuron -= (toNeuron/5)+1; //0-15
          if (toNeuron < 8) {
            toNeuron %= 4; //
            toNeuron =(toNeuron*7)+synapse;//0-27
            toNeuron = ((toNeuron/5)*10)+(toNeuron%5); //0..4,10..14,
            if (toNeuron >= 50) toNeuron =101;
	  }
          else {
            toNeuron %= 4; //
            toNeuron =(toNeuron*7)+synapse;//0-27
            toNeuron = ((toNeuron/5)*10)+(toNeuron%5); //0..4,10..14,
            if (toNeuron >= 50) toNeuron =101;
            toNeuron += 50; //50..54, 60..64..
	  }
          if (toNeuron < 100) {
            toNeuron += ruleStart;//VPVPPPLoc offset
            neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],.37);
          }
	}
      }
    }
  }

  private void connectPrepOnPreventVPNPRule(int inst,Parse4Net ruleNet,
    int ruleStart) {
    int fromStart=inst*nInstCASize;
    for (int neuron = 60; neuron<79;neuron+=5) {
      int fromNeuron = neuron + fromStart;
      for (int synapse = 0; synapse < 13; synapse ++) { //4 neurons to 50
        int toNeuron = (neuron%20)/5; //0,1,2,3
        toNeuron = (toNeuron*13)+synapse;//0-51
        toNeuron = ((toNeuron/5)*10)+(toNeuron%5); //0..4,10..14,
        toNeuron %=100; //keep in the rule
        toNeuron += ruleStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],-3);
      } } }

  //The bound slot turns off (or prevents) rules that would use it.
  private void boundInstanceStopsRule(int instance, int ruleStart, 
    Parse4Net ruleNet) {
    for (int neuron = 0; neuron<4;neuron ++) {
      int fromNeuron = (neuron*5)+(instance*nInstCASize);
      fromNeuron +=280; //bound slot 280
      for (int synapse = 0; synapse < 25; synapse ++) {
        int toNeuron = (neuron*25)+synapse+ruleStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],-2.0);
      }
    }
  }
  private void boundNounInstancesSlotStopsRules(Parse4Net ruleNet) {
    boundInstanceStopsRule(0,300,ruleNet);
    boundInstanceStopsRule(1,400,ruleNet);
    boundInstanceStopsRule(0,600,ruleNet);
    boundInstanceStopsRule(0,900,ruleNet);
    boundInstanceStopsRule(1,1000,ruleNet);
    boundInstanceStopsRule(2,1100,ruleNet);
    boundInstanceStopsRule(1,1200,ruleNet);
    boundInstanceStopsRule(2,1300,ruleNet);
    boundInstanceStopsRule(0,1500,ruleNet);
    boundInstanceStopsRule(1,1600,ruleNet);
    boundInstanceStopsRule(2,1700,ruleNet);
  }
      
    //this only works for the done slot and the PP rule
  private void doneNounInstanceStopsRule(int ruleStart, Parse4Net ruleNet) {
    for (int neuron = 0; neuron<4;neuron ++) {
      int fromNeuron = (neuron*5);
      fromNeuron +=180; //done slot 180
      for (int synapse = 0; synapse < 25; synapse ++) {
        int toNeuron = (neuron*25)+synapse+ruleStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],-2.0);
      }
    }
  }

  public void connectNounInstanceToRuleTwo(Parse4Net ruleNet) {
    connectNounInstanceToRule(0,0.185,300,0.00125,ruleNet); //VP -> VPObj 1 1
    connectNounInstanceToRule(1,0.185,400,0.00125,ruleNet); //VP -> VPObj 1 2
    connectNounInstanceToRule(0,0.185,600,0.0,ruleNet); //VP -> NPActVP 11

    connectNounInstanceToRule(0,0.1,900,0.0,ruleNet); //VP -> VPPPloc 1 1
    connectNounInstanceToRule(1,0.1,1000,0.0,ruleNet); //VP -> VPPPloc 1 2
    connectNounInstanceToRule(2,0.1,1100,0.0,ruleNet); //VP -> VPPPloc 1 3
    connectPrepOnToRule(0,900,ruleNet);//VP -> VPPPloc 1 1
    connectPrepOnToRule(1,1000,ruleNet);//VP -> VPPPloc 1 2
    connectPrepOnToRule(2,1100,ruleNet);//VP -> VPPPloc 1 3
    connectNounInstanceToRule(0,0.08,1500,0.0,ruleNet); //VP -> VPPPinst 1 1
    connectNounInstanceToRule(1,0.08,1600,0.0,ruleNet); //VP -> VPPPinst 1 2
    connectNounInstanceToRule(2,0.08,1700,0.0,ruleNet); //VP -> VPPPinst 1 3
    connectPrepOnToRule(0,1500,ruleNet);//VP -> VPPPinst 1 1
    connectPrepOnToRule(1,1600,ruleNet);//VP -> VPPPinst 1 2
    connectPrepOnToRule(2,1700,ruleNet);//VP -> VPPPinst 1 3

    connectNounInstanceToRule(0,0.185,1200,0.0,ruleNet); //NP -> NPPP 1 2
    connectNounInstanceToRule(1,0.145,1200,0.0,ruleNet); //NP -> NPPP 1 2 
    connectNounInstanceToRule(1,0.185,1300,0.0,ruleNet); //NP -> NPPP 2 3
    connectNounInstanceToRule(2,0.145,1300,0.0,ruleNet); //NP -> NPPP 2 3
    connectPrepOnToRule(1,1200,ruleNet);//NP -> NPPP 1 2 
    connectPrepOnToRule(2,1300,ruleNet);//NP -> NPPP 2 3 

    connectPrepOnPreventVPNPRule(0,ruleNet,300);
    connectPrepOnPreventVPNPRule(1,ruleNet,400);
    connectPrepOnPreventVPNPRule(0,ruleNet,600);
    boundNounInstancesSlotStopsRules(ruleNet);
    doneNounInstanceStopsRule(1200, ruleNet);
   }

  private void connectPrepFeatureToAPrep(int word,Parse4Net otherNet) {
    for (int instance = 0; instance < 2; instance++) {
      for (int neuron = 106; neuron<114;neuron++) {
        if ((neuron%5) != 0) {
          //each of the 50 access neurons (5-9, 15-19 ...95-99)gets 8 conns
          //connect to the right side of the feature because the left
          //side will activate during binding.
          for (int accessGroup = 0; accessGroup < 5; accessGroup ++) {
            int fromNeuron = neuron + (instance*nInstCASize);
            for (int synapse=0;synapse<5;synapse++) {
              int toNeuron = word*100; 
              toNeuron += accessGroup*20;
              toNeuron+=synapse+5;
              neurons[fromNeuron].addConnection(
                otherNet.neurons[toNeuron],0.01);
	      toNeuron += 10;
              neurons[fromNeuron].addConnection(
                otherNet.neurons[toNeuron],0.01);
            }
	  }
        }
      }
    }
  }
  private void connectPrepFeatureToPreps(Parse4Net otherNet) {
    connectPrepFeatureToAPrep(1,otherNet); //toward
    connectPrepFeatureToAPrep(3,otherNet);  //with
    connectPrepFeatureToAPrep(4,otherNet);  //to
  }
  public void connectNounInstanceToOther(Parse4Net otherNet) {
   connectPrepFeatureToPreps(otherNet); 
  }


  //The active access word primes but does not activate the word.
  //10 connections to 1 neuron. .125 gives 1.25 each cycle, so 1.25, 1.8725,
  // ... < 3
  private void connectVerbAccessWordToActVInstRule(int word,Parse4Net ruleNet) 
  {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        //VInst Rule is 400-500
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 400;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  //like connectNounAccessWordToNPFromN
  private void connectVerbAccessWordToMainVerb(int word, 
    Parse4Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 500;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125); 
      }
    }
  }
  private void connectVerbAccessWordToVPDone(int word, 
    Parse4Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 600;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.11); 
      }
    }
  }
  public void connectVerbAccessToRuleOne(Parse4Net ruleNet) {
    for (int word = 0; word < numVerbs; word ++) {
      connectVerbAccessWordToActVInstRule(word,ruleNet);
      connectVerbAccessWordToMainVerb(word,ruleNet);
      connectVerbAccessWordToVPDone(word,ruleNet);
    }
  }
  
  //Each access has to get connections from either side of the 
  //instance so it can bind.
  public void connectVerbInstanceToVerbAccess(Parse4Net verbAccessNet) {
    for (int instance = 0; instance < 2; instance++) {
      for (int word = 0; word < numVerbs; word++) {
        //connect main verb feature to verbAccess
        for (int neuron = 126; neuron<135;neuron++) {
	    if ((neuron%5) != 0)
            //each of the 50 access neurons (5-9, 15-19 ...)gets 8 connections
            //connect to the right side of the feature because the left
            //side will activate during binding.
            for (int accessGroup = 0; accessGroup < 5; accessGroup ++) {
              int fromNeuron = neuron + (instance*vInstCASize);
              for (int synapse=0;synapse<5;synapse++) {
                int toNeuron = word*100; 
                toNeuron += accessGroup*20;
                toNeuron+=synapse+5;
                neurons[fromNeuron].addConnection(
                  verbAccessNet.neurons[toNeuron],0.01);
	          toNeuron += 10;
                neurons[fromNeuron].addConnection(
                  verbAccessNet.neurons[toNeuron],0.01);
	      }
	    }
	}
      }
    }
  }

  private void connectAVerbInstanceToRule(int fromStart,double weight,
 					  int toStart, double dynamicWeight,
                                          Parse4Net ruleNet) {
    //for each neuron in the body 40 excitatory neurons
    //half of the rule neurons get 4 connections (the rest 0)
    for (int neuron = 0; neuron<40;neuron++) {
      int fromNeuron = neuron + fromStart;
      for (int synapse = 0; synapse < 5; synapse ++) {
	int toNeuron = ((synapse*40)+neuron); //equally distribute from 0-200
        toNeuron %=100; //equally distribute 0-100
        if ((toNeuron%10) >= 5) toNeuron-=5; //just the first 5 of any 10
        toNeuron += toStart; 
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],weight);
      }
    }
    if (dynamicWeight == 0) return;
    //Connect the dynamic part
    for (int neuronPair = 0; neuronPair < 80; neuronPair++) {
      int fromNeuron = (neuronPair*2) + nounInstanceTimeStart+ fromStart;
      int toNeuron = toStart;
      for (int synapse = 0; synapse < 25; synapse++) {
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],
          dynamicWeight);
        if ((toNeuron%10) == 4) toNeuron += 5;
        toNeuron++;
        neurons[fromNeuron+1].addConnection(ruleNet.neurons[toNeuron],
          dynamicWeight);
        if ((toNeuron%10) == 4) toNeuron += 5;
        toNeuron++;
      
      }
    }
  }

  private void verbSlotInhibitsRule(int slotStart,int ruleStart, 
    Parse4Net ruleNet) {
    for (int i = 0; i < 4; i++) {
      int fromNeuron = (i*5) + slotStart;
      for (int synapse = 0; synapse < 25; synapse++) {
        int toNeuron = (i*25) + synapse + ruleStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],-1.5);
      }
    }
  }
    
  public void connectVerbInstanceToRuleTwo(Parse4Net ruleNet) {
    connectAVerbInstanceToRule(0,0.18,0,0.0,ruleNet); //S ->VP-Period .25
    connectAVerbInstanceToRule(0,0.165,300,0.0,ruleNet); //VP -> VPObj 1 1 
    connectAVerbInstanceToRule(0,0.165,400,0.0,ruleNet); //VP -> VPObj 1 2
    connectAVerbInstanceToRule(0,0.17,600,0.001,ruleNet); //VP->NPactVP 1 1 
    connectAVerbInstanceToRule(0,0.14,900,0.0,ruleNet); //VP->VPPPloc 1 1
    connectAVerbInstanceToRule(0,0.14,1000,0.0,ruleNet); //VP->VPPPloc 1 2
    connectAVerbInstanceToRule(0,0.14,1100,0.0,ruleNet); //VP->VPPPloc 1 3
    connectAVerbInstanceToRule(0,0.14,1500,0.0,ruleNet); //VP->VPPPinst 1 1
    connectAVerbInstanceToRule(0,0.14,1600,0.0,ruleNet); //VP->VPPPinst 1 2
    connectAVerbInstanceToRule(0,0.14,1700,0.0,ruleNet); //VP->VPPPinst 1 3

    //undone why don't obj and act done inhibit their rules
    verbSlotInhibitsRule(100,1000,ruleNet); 
  }

  //Each Verb has 5 features that can bind to NInstance (actor, object,
  // instrument,location,time)
  //Each slot binds to the slot 
  //Each neuron needs 10 connections. (5 to each side)
  private void connectAVInstanceSlotToANInstance(int fromOffset,int vInst, 
    int nInst, Parse4Net ruleNet) {
    for (int neuron = 6; neuron < 15; neuron ++) {
      int fromNeuron = (vInst*vInstCASize)+fromOffset+neuron;

      if (!neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 10; synapse++ ){
          int toNeuron = synapse;
          if (synapse >= 5) toNeuron +=5;
          toNeuron += (nInst*nInstCASize)+260;
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.02);
        }
      }
    }
  }
  private void connectAVInstanceToANInstance(int vInst, int nInst, 
    Parse4Net ruleNet) {
    connectAVInstanceSlotToANInstance(160,vInst,nInst,ruleNet); //actor slot
    connectAVInstanceSlotToANInstance(180,vInst,nInst,ruleNet); //object slot
    connectAVInstanceSlotToANInstance(220,vInst,nInst,ruleNet); //instrum. slot
    connectAVInstanceSlotToANInstance(240,vInst,nInst,ruleNet); //loc slot
  }
  public void connectVerbInstanceToNounInstance(Parse4Net ruleNet) {
    for (int vInstance = 0; vInstance < 2; vInstance++) {
      for (int nInstance = 0; nInstance < 3; nInstance++) {
        connectAVInstanceToANInstance(vInstance,nInstance,ruleNet);
      }
    }
  }

  private void  connectAPrepToNInstFromPrep(int otherCA,Parse4Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (otherCA*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  private void connectAPrepToNPAddPrep(int word, Parse4Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 900;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  private void connectAPrepToPrepDone(int word, Parse4Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 1000;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.11); //.41
      }
    }
  }
  private void  connectPrepsToRules(Parse4Net ruleNet) {
    connectAPrepToNInstFromPrep(1,ruleNet);
    connectAPrepToNPAddPrep(1,ruleNet);
    connectAPrepToPrepDone(1,ruleNet);
    connectAPrepToNInstFromPrep(3,ruleNet);
    connectAPrepToNPAddPrep(3,ruleNet);
    connectAPrepToPrepDone(3,ruleNet);
    connectAPrepToNInstFromPrep(4,ruleNet);
    connectAPrepToNPAddPrep(4,ruleNet);
    connectAPrepToPrepDone(4,ruleNet);
  }

  private void connectADetToNPAddDet(int word, Parse4Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 1100;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  private void  connectADetToNInstFromDet(int otherCA,Parse4Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (otherCA*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125); 
      }
    }
  }
  private void  connectDetsToRules(Parse4Net ruleNet) {
    connectADetToNPAddDet(2,ruleNet);
    connectADetToNInstFromDet(2,ruleNet);
  }
  private void connectAAdjToNPAddAdj(int word, Parse4Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 800;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.25);
      }
    }
  }
  private void  connectAdjsToRules(Parse4Net ruleNet) {
    connectAAdjToNPAddAdj(5,ruleNet);
  }
  public void connectOtherToRuleOne(Parse4Net ruleNet) {
    connectPrepsToRules(ruleNet);
    connectDetsToRules(ruleNet);
    connectAdjsToRules(ruleNet);
  }
  private void connectPeriodToSFromVPPeriod(Parse4Net ruleNet) 
  {
    for (int neuron=0; neuron<100;neuron++) {
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*10);
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron],0.1); //.125
      }
    }
  }
  public void connectOtherToRuleTwo(Parse4Net ruleNet) {
    connectPeriodToSFromVPPeriod(ruleNet);
  }

  private void connectASemWordToARuleSel(int word, int pref, double weight,
    Parse4Net vPPPNet) {
    for (int i = 0; i < 60; i++) {
      int fromNeuron = (word*60)+i; 
      if (!neurons[fromNeuron].isInhibitory){
        int toNeuron = i;
        toNeuron += (pref*preferenceCASize);
	neurons[fromNeuron].addConnection(vPPPNet.neurons[toNeuron], weight);
        if ((i%5) == 1)
          neurons[fromNeuron].addConnection(vPPPNet.neurons[toNeuron-1],
            weight);
      }
    }
  }

  private void connectASemFeatureToARuleSel(int feature, int pref, 
    double weight, Parse4Net vPPPNet) {
    for (int i = 0; i < 30; i++) {
      int fromNeuron = (feature*30)+i; 
      if (!neurons[fromNeuron].isInhibitory){
        int toNeuron = i;
        toNeuron += (pref*preferenceCASize);
	neurons[fromNeuron].addConnection(vPPPNet.neurons[toNeuron], weight);
	neurons[fromNeuron].addConnection(vPPPNet.neurons[toNeuron+30],weight);
        if ((i%5) == 1) {
          neurons[fromNeuron].addConnection(vPPPNet.neurons[toNeuron-1],
            weight);
          neurons[fromNeuron].addConnection(vPPPNet.neurons[toNeuron+29],
            weight);
	}
      }
    }
  }

  public void connectVerbSemToVPPP(Parse4Net vPPPNet) {
    connectASemWordToARuleSel(0,0,0.3,vPPPNet); //MOVE it stalactite
    connectASemFeatureToARuleSel(130,0,0.15,vPPPNet); //MOVE it stalactite
    connectASemFeatureToARuleSel(136,0,0.15,vPPPNet); //MOVE it stalactite

    connectASemWordToARuleSel(3,1,0.4,vPPPNet); //SAW girl telescope
  }
  public void connectNounSemToVPPP(Parse4Net vPPPNet) {
    connectASemWordToARuleSel(2,0,0.45,vPPPNet); //move IT stalactite

    //.85 -> .17 .34
    connectASemWordToARuleSel(3,0,0.34,vPPPNet); //move it STALACTITE
    connectASemFeatureToARuleSel(242,0,0.17,vPPPNet); //move it STALACTITE
    connectASemFeatureToARuleSel(253,0,0.17,vPPPNet); //move it STALACTITE
    connectASemFeatureToARuleSel(256,0,0.17,vPPPNet); //move it STALACTITE

    //.5 ->.17 
    connectASemWordToARuleSel(6,1,0.18,vPPPNet); //saw GIRL telescope
    connectASemFeatureToARuleSel(249,1,0.14,vPPPNet); //saw GIRL telescope
    connectASemFeatureToARuleSel(253,1,0.14,vPPPNet); //saw GIRL telescope
    connectASemFeatureToARuleSel(256,1,0.14,vPPPNet); //saw GIRL telescope
    connectASemWordToARuleSel(7,1,0.9,vPPPNet); //saw girl TELESCOPE
  }

  public void connectVerbSemToNPPP(Parse4Net nPPPNet) {
    connectASemWordToARuleSel(0,1,0.41,nPPPNet); //MOVE door handle
  }
  public void connectNounSemToNPPP(Parse4Net nPPPNet) {
     //.4 -> .134 ,.067
    connectASemWordToARuleSel(8,1,0.14,nPPPNet); //move DOOR handle
    connectASemFeatureToARuleSel(242,1,0.19,nPPPNet); //move DOOR handle
    connectASemFeatureToARuleSel(243,1,0.19,nPPPNet); //move DOOR handle
    //skip 253 and 256 as used in handle

    //.8 -> .32, .16
    connectASemWordToARuleSel(9,1,0.32,nPPPNet); //move door HANDLE
    connectASemFeatureToARuleSel(253,1,0.16,nPPPNet); //move door HANDLE
    connectASemFeatureToARuleSel(254,1,0.16,nPPPNet); //move door HANDLE
    connectASemFeatureToARuleSel(256,1,0.16,nPPPNet); //move door HANDLE
  }

  
  private void connectAVPPPToARuleTwo(Parse4Net ruleNet, int vPPP, int rule) {
    for (int i=0;i<preferenceCASize; i++) {
      int fromNeuron = vPPP+i;
      if (i < 50) {
        int toNeuron1 = (i%5) + ((i/5)*10) + rule; 
        int toNeuron2 = toNeuron1+10;
        if ((i%10) > 4) toNeuron2 -=20;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1],0.32);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2],0.32);
        toNeuron1 += 100;          
        toNeuron2 += 100;          
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1],0.32);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2],0.32);
        toNeuron1 += 100;          
        toNeuron2 += 100;          
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1],0.32);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2],0.32);
      }
    }
  }

  public void connectVPPPToRuleTwo(Parse4Net ruleNet) {
    connectAVPPPToARuleTwo(ruleNet,0,900); //Move it toward statlagite loc
    connectAVPPPToARuleTwo(ruleNet,60,1500); //saw girl telescope inst
  }

  public void connectNPPPToRuleTwo(Parse4Net ruleNet) {
    int prefCAs = getSize()/preferenceCASize;
    for (int prefCA = 0; prefCA < prefCAs; prefCA++) {
      for (int i=0;i<preferenceCASize; i++) {
        int fromNeuron = (prefCA*preferenceCASize)+i;
        if (i < 50) {
          int toNeuron1 = (i%5) + ((i/5)*10) + 1200;
          int toNeuron2 = toNeuron1+10;
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1],0.2);
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2],0.2);
	  toNeuron1 += 100;          
          toNeuron2 += 100;          
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1],0.2);
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2],0.2);
	}
      }
    }
  }

  private void aCounterTurnsABaseInstanceOn(int counter, int instance,
    int instCASize,Parse4Net instanceNet) {
    for (int feature = 0; feature < 4; feature ++) {
      for (int i = 0; i < 10; i++) {
        int fromNeuron = (counter*100) + (feature*10) + i;
        if ((i%5) == 0) fromNeuron++;
        int toNeuron = (instance*instCASize) + (feature*10) +i;
        if (i > 4) toNeuron -=5;
        neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron],
          2.5);
      }
    }
  }

  private void aCounterTurnsATimeInstanceOn(int counter, int instance,
    int instCASize,int timeOffset, Parse4Net instanceNet) {
    for (int feature = 5; feature < 9; feature ++) {
      for (int i = 1; i < 5; i++) {
        int fromNeuron = (counter*100) + (feature*10) + i;
        if ((i%5) == 0) fromNeuron++;
        int toNeuron = (instance*instCASize) + timeOffset+ (feature*10) +i-10;
        neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron-40],
          4.1);
        neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron-35],
          4.1);
        neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron],4.1);
        neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron+5],4.1);
        neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron+40],
          4.1);
        neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron+45],
          4.1);
        if (i == 1) {
          neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron-41],
            4.1);
          neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron-36],
            4.1);
          neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron-1],
            4.1);
          neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron+4],
            4.1);
          neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron+39],
            4.1);
          neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron+44],
            4.1);
	}
      }
    }
  }
  private void aCounterTurnsAnInstanceOn(int counter, int instance,
    int instCASize,int timeOffset, Parse4Net instanceNet) {
    aCounterTurnsABaseInstanceOn(counter,instance,instCASize,instanceNet);
    aCounterTurnsATimeInstanceOn(counter,instance,instCASize,
      timeOffset,instanceNet);
  }
  
  //the 40-50 neurons turn on the bind slot
  private void aNounCounterTurnsAnInstanceBindOn(int counter, int instance,
    Parse4Net instanceNet) {
    for (int i = 0; i < 10; i++) {
      int fromNeuron = (counter*100) + 40 + i;
      if ((i%5) == 0) fromNeuron++;
      int toNeuron = (instance*nInstCASize) + 260 +i;
      if (i > 4) toNeuron -=5;
      neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron], 2.5);
      neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron+10],2.5);
    }
  }

  public void connectCounterToNounInstance(Parse4Net nounInstanceNet) {
    for (int instance=0; instance < 3; instance ++) {
      aCounterTurnsAnInstanceOn(instance+1,instance,nInstCASize,
        nounInstanceTimeStart, nounInstanceNet);
      aNounCounterTurnsAnInstanceBindOn(instance+1,instance,nounInstanceNet);
    }
  }
  public void connectCounterToVerbInstance(Parse4Net verbInstanceNet) {
    aCounterTurnsAnInstanceOn(5,0,vInstCASize,verbInstanceTimeStart,
      verbInstanceNet);
  }

  public void connectNextWordToRuleOne(Parse4Net ruleOneNet) {
    for (int i = 0; i < 10; i++) {
      int fromNeuron = ((nextWordSteps -1)*10)+i;
      for (int synapse = 0; synapse < 5 ; synapse ++) {
        int toNeuron = (i*10) + synapse+ 300;
        neurons[fromNeuron].addConnection(ruleOneNet.neurons[toNeuron],4.1);
      }
    }
  }

  //-------------------End interSubnet connections --------

  public void kludge () {
    System.out.println("Parse4 kludge ");
    Enumeration eNum = CANT23.nets.elements();
    Parse4Net tNet= (Parse4Net)eNum.nextElement();
    Parse4Net sNet= (Parse4Net)eNum.nextElement();
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CANTNet net = (CANTNet)eNum.nextElement();
      if (net.getName().compareTo("RuleTwoNet") == 0)
        tNet = (Parse4Net) net;
      if (net.getName().compareTo("VPPPNet") == 0)
        sNet = (Parse4Net) net;
      }
    /*
    for (int i = 0;i < tNet.getSize();i+=1) 
      if (tNet.neurons[i].getFired())
        System.out.println(i+ "test ");
    */
    for (int i = 1200;i < 1205;i+=1)
      System.out.println(i+ "test " + tNet.neurons[i].getActivation());
    for (int i = 1000;i < 1005;i+=1)
      System.out.println(i+ "test " + tNet.neurons[i].getActivation());
  }
  
  public void measure(int currentStep) {
    System.out.println("measure " + neurons[0].getActivation() + " " + 
      neurons[0].getFired() + " " + 
	  currentStep);
  }
}