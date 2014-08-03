
import java.util.*;

public class CABot2Net extends CANTNet {
  private int currentWord = 0;
  
  public CABot2Net(){
  }
  
  public CABot2Net(String name,int cols, int rows,int topology){
    this.cols = cols;
    this.rows = rows;
    setName(name);
    this.topology = topology;
    netFileName = name + ".dat";
    setRecordingActivation(false);
    //    super(name,cols,rows,topology);
    cyclesToStimulatePerRun = 1000;
  }

  //set all fast bind neuron weights to .01
  public void resetBindings() {
    for (int neuronIndex = 0; neuronIndex < size(); neuronIndex++) 
      {
      if (neurons[neuronIndex] instanceof CANTNeuronFastBind) {
        for (int synapse=0; synapse<neurons[neuronIndex].getCurrentSynapses(); 
          synapse++ ) {
            neurons[neuronIndex].synapses[synapse].setWeight(.01);
	  } } } }

  //***Parsing patterns
  private int numNouns = 15;
  private int numVerbs = 7;

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
    for (int word = 0; word < 27; word++) {
      makeNewWordPattern(word);
    }
  }

  public void setExternalActivation(int cantStep){
    if (getName().compareTo("BaseNet") == 0) {
      int wordToStimulate = CABot2.experiment.currentWord;
      if (wordToStimulate == -1) return;
      CANTPattern pattern = 
        (CANTPattern)patterns.get(wordToStimulate);
      int neuronsToStimulate = getNeuronsToStimulate(); 
      for (int i= 0; i < neuronsToStimulate; i++) {
	int neuronNumber = pattern.getPatternIndex(i);
        double theta = getActivationThreshold();
        neurons[neuronNumber].setActivation(theta+(CANT23.random.nextFloat()*
          theta));
      }
    }
    else if ((getName().compareTo("BarOneNet") == 0) ||
             (getName().compareTo("ControlNet") == 0) ||
             (getName().compareTo("VisualInputNet") == 0) ||
             (getName().compareTo("NounInstanceNet") == 0) ||
             (getName().compareTo("VerbInstanceNet") == 0)) {
      int curPatt = getCurrentPattern();
      if (curPatt < 0) curPatt = 0;
      CANTPattern pattern = (CANTPattern)patterns.get(curPatt);
      for (int i= 0; i < getNeuronsToStimulate();  i++) {
        if (i == pattern.size()) return;
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

  public void subclassLearn() {
    if (getName().compareTo("Goal2Net") != 0)  return;
   
    for (int neuronNum = 0; neuronNum<getSize(); neuronNum++) {
      if (neurons[neuronNum].getFired()) { //only learn if it fires
	for (int synapseNum = 0; 
          synapseNum < neurons[neuronNum].getCurrentSynapses(); synapseNum++) {
          //only learn if it's from fact to module
          CANTNeuron toNeuron=neurons[neuronNum].synapses[synapseNum].toNeuron;
          if (toNeuron.parentNet.getName().compareTo("Module2Net") == 0) {
            //modify weight
            neurons[neuronNum].modifySynapticWeight(synapseNum);
	  }    
	}
      }
    }
  }


  //*******Parsing Topology
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
  private void setSemTopology() {
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

  //Every fifth neuron is inhibitory.
  //This is a simple structure of 50/50 CAs.  They're piecewise in portions
  //of 10.  
  private int ruleFeatureSize = 10;
  private void setRuleTopology() {
    setFiftyFiftyTopology2(1.1);
    //simple NP
    ruleInhibitsRule(0,1); //NPInst -( NPFromN
    ruleInhibitsRule(0,2); //NPInst -( NPDone
    ruleInhibitsRule(1,2,0.6); //NPFromN -( NPDone //lesser weight for timing
    ruleInhibitsRule(2,1); //NPDone -( NPFromN
    //simple VP
    ruleInhibitsRule(4,5); //VPInst -( MainV
    ruleInhibitsRule(4,6); //VPInst -( VPDone
    ruleInhibitsRule(5,6); //MainV -( VPDone
    ruleInhibitsRule(6,5); //VPDone -( MainV 
    //kill read word
    ruleInhibitsRule(0,3); //NPInst -( Read Word
    ruleInhibitsRule(4,3); //NPInst -( Read Word
    //bar 2 rules stop NPDone
    ruleInhibitsRule(7,2); //VPFromVPNPObj -( NPDone 
    //NPDone prevents bar2 rules
    //ruleInhibitsRule(2,18); //NPDone -( VPFromVPNPAct
    //ruleInhibitsRule(2,15); //NPDone -( VPFromVPNPObj
    //ruleInhibitsRule(2,12); //NPDone -( VPFromVPPPLoc
    //Preps
    ruleInhibitsRule(9,10); //NPAddPrep -( PrepDone
    ruleInhibitsRule(10,9); //PrepDone -( NPAddPrep 
    //BarTwoRules Inhibit Each Other
    ruleInhibitsRule(12,7); //VPFromVPNPLoc -( VPFromVPNPObj  
    ruleInhibitsRule(12,8); //VPFromVPNPLoc -( VPFromVPPeriod
    ruleInhibitsRule(15,18); //VPFromVPObj -( VPFromNPActVP
    ruleInhibitsRule(18,15); //VPFromNPActVP -( VPFromVPObj
    ruleInhibitsRule(13,21); //VPFromVPLoc -( NPfromPPNP
    ruleInhibitsRule(21,13); //NPfromPPNP -( VPFromVPLoc 
    ruleInhibitsRule(14,22); //VPFromVPLoc -( NPfromPPNP
    ruleInhibitsRule(22,14); //NPfromPPNP -( VPFromVPLoc 
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
          new CANTNeuronFastBind(totalNeurons++,this,0.0005,750);
        else {
          neurons[i+(CA*nInstCASize)] = new CANTNeuron(totalNeurons++,this,
            synapses);
          if (((i %5) == 0) && (i < 300))
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

  //This component shows how long the instance has been running by
  //the number of neurons firing. The fewer the longer.
  //There are groups of 8 neurons that go off together.
  private void setNounInstanceTimeTopology(){
    double bigWeight = 2.4;
    double weight2 = 0.8;
    for (int CA = 0; CA < 3; CA++) {
	int offset = 300 + (CA*nInstCASize);
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
          int fromNeuron=(CA*nInstCASize)+baseNeuron;
          for (int synapse=0;synapse< 10;synapse++) {
            int toNeuron = ((baseNeuron/10)*10)%20; //0,10
            toNeuron+=synapse+100+(feature*20)+(CA*nInstCASize);
            addConnection(fromNeuron,toNeuron, 0.01); 
          } } } } }

  //Bind connects to the base to turn it on when
  private void connectBindToBase() {
    for (int CA=0; CA<getSize()/nInstCASize;CA++) {
      for (int baseNeuron=260;baseNeuron< 280;baseNeuron++) {
	for (int feature=0;feature<2;feature++) {
          int fromNeuron=(CA*nInstCASize)+baseNeuron;
          if (!neurons[fromNeuron].isInhibitory()) {
            for (int synapse=0;synapse<5;synapse++) {
              int toNeuron = ((baseNeuron/10)*10)%20; //0,10
              toNeuron+=synapse+(feature*20)+(CA*nInstCASize);
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
        for (int feature=0;feature<5;feature++) { //the 1st 5 are barone feats
          int fromNeuron=doneNeuron+(CA*nInstCASize);
          for (int synapse=0;synapse< 10;synapse++) {
            int toNeuron = ((doneNeuron/5)*5)%20; //0,5,10,15
            toNeuron+=synapse+100+(feature*20);
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
    if ((neuron%5) == 0) return false;
    if ((neuron %20) < 5) return false;
    if ((neuron %20) > 15) return false;
    return true;
  }
  private int vInstCASize = 300;
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
          if ((i %5) == 0)
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
	  }
        }
      }

      //set up the fastbind features
      for (int feature = 12; feature < 30 ; feature ++) {
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
            //skip obj, loc and act done
            else if ((feature != 1) && (feature !=2) && (feature !=3))
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
        addConnection(fromNeuron,toNeuron, 2.25);
        if ((neuron %5) ==1)
          addConnection(fromNeuron,toNeuron-1, 2.25);
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
        addConnection(fromNeuron,toNeuron, 2.25);
        if ((neuron %5) ==1)
          addConnection(fromNeuron,toNeuron-1, 2.25);
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
        addConnection(fromNeuron,toNeuron, 2.25);
        if ((neuron %5) ==1)
          addConnection(fromNeuron,toNeuron-1, 2.25);
      } } }

  private void vILocDoneExtinguishLoc() {
    for (int CA=0; CA<getSize()/vInstCASize;CA++) {
      for (int neuron=0;neuron< 4;neuron++) {
        int fromNeuron = (neuron*5)+100+(CA*vInstCASize);
        for (int synapse = 0 ; synapse < 5 ;synapse++) {
          int toNeuron = fromNeuron+140+synapse;
          addConnection(fromNeuron,toNeuron, 10);
	} } } }  

  private void setVerbInstanceTopology() {
    setVerbFeaturesInstanceTopology();
    connectVerbInstanceBaseToFeatures();
    connectVIOthersOnToOthers();
    verbInstanceDoneExtinguishBarOneFeatures();
    vIObjToObjDone();
    vIObjDoneExtinguishObj();
    vIActToActDone();
    vIActDoneExtinguishAct();
    vILocToLocDone();
    vILocDoneExtinguishLoc();
  }

  private void setOtherWordTopology() {
    setFiftyFiftyTopology(0.9);
  }

  int preferenceCASize = 60;
  private void setRuleSelectionTopology() {
    setFiftyFiftyTopology(0.9);
  }

//*****InterParse Subnet Connections
  //This works for both nounAccess and verb Access
  private void connectOneInputToOneAccess(CABot2Net accessNet,
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

  public void connectInputToNounAccess(CABot2Net nounAccessNet) {
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
  }

  public void connectInputToVerbAccess(CABot2Net verbAccessNet) {
    connectOneInputToOneAccess (verbAccessNet,0,0); //move
    connectOneInputToOneAccess (verbAccessNet,3,1); //turn
    connectOneInputToOneAccess (verbAccessNet,10,2); //found
    connectOneInputToOneAccess (verbAccessNet,12,3); //saw
    connectOneInputToOneAccess (verbAccessNet,24,4); //go
    connectOneInputToOneAccess (verbAccessNet,25,5); //is
    connectOneInputToOneAccess (verbAccessNet,26,6); //center
  }

  public void connectInputToOther(CABot2Net otherNet) {
    connectOneInputToOneAccess (otherNet,2,0); //.period
    connectOneInputToOneAccess (otherNet,4,1); //toward
    connectOneInputToOneAccess (otherNet,5,2); //the
    connectOneInputToOneAccess (otherNet,14,3); //with
    connectOneInputToOneAccess (otherNet,23,4); //to
  }

  //This works for nounAccess, verbAccess, and other
  //each noun access is primed by the wordActive CA.  The particular word
  //selects which nounAccess is actually turned on.
  public void connectBarOneToAccess(CABot2Net accessNet) {
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
  private void connectWordActToActNInstRule(CABot2Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron=(neuron/ruleFeatureSize)*ruleFeatureSize;
      toNeuron += (neuron%(ruleFeatureSize/2));
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.25);
    }
  }
  //reducing just this weight from 1.25 to 1 means it takes an extra cycle
  private void connectWordActToNPFromNRule(CABot2Net ruleNet) {
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
  private void connectBarOneToInhibNInstRule(CABot2Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron+=5) {
      int toNeuron=(neuron - 100);
      for (int synapse = 0; synapse < 10; synapse++) {
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron++],-1.25);
        toNeuron%=100;
      }
    }
  }

  private void connectWordActToNPDoneRule(CABot2Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      if (!neurons[neuron].isInhibitory()) {
        int toNeuron=(neuron/10)*10;
        toNeuron += (neuron%5)+200;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1);
        if ((neuron%5)==1) //make up for the inhibitory neuron
          neurons[neuron].addConnection(ruleNet.neurons[toNeuron-1],1);
      }
    }
  }

  //Need to activate the first half of each rule feature above 2. In
  //combination with the NounAccess this ignites it. Each neuron connects
  //to one of those so 2->1.  Each has 1.25 weights so it goes above
  //2 but stays below 3.
  private void connectWordActToActVInstRule(CABot2Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron=(neuron/ruleFeatureSize)*ruleFeatureSize;
      toNeuron += (neuron%(ruleFeatureSize/2)) + 400;
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.25);
    }
  }

  //The barone CA extinguishes (or prevents) the VInst Rule.
  private void connectBarOneToInhibVInstRule(CABot2Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron+=5) {
      int toNeuron=neuron%100 ;
      for (int synapse = 0; synapse < 10; synapse++) {
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron++ + 400],-1.25);
        toNeuron%=100;
      }
    }
  }
  //like connectWordActToNPFromNRule
  private void connectWordActToMainVerbRule (CABot2Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron=(neuron/ruleFeatureSize)*ruleFeatureSize;
      toNeuron += (neuron%(ruleFeatureSize/2)+500);
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.0);
    }
  }
  private void connectBarOneToVPDoneRule(CABot2Net ruleNet) {
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

  //The barone prevents bar two rules.
  private void connectBarOneToInhibBarTwoRule(int toStart, CABot2Net ruleNet) {
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
  private void connectWordActToNPAddPrepRule(CABot2Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron=(neuron/ruleFeatureSize)*ruleFeatureSize;
      toNeuron += (neuron%(ruleFeatureSize/2)+900);
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.0);
    }
  }
  private void connectBarOneToPrepDoneRule(CABot2Net ruleNet) {
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
  private void connectWordActToNPAddDetRule(CABot2Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron=(neuron/ruleFeatureSize)*ruleFeatureSize;
      toNeuron += (neuron%(ruleFeatureSize/2)+1100);
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron],1.25);
    }
  }
  public void connectBarOneToRule(CABot2Net ruleNet) {
    connectWordActToActNInstRule(ruleNet);
    connectWordActToNPFromNRule(ruleNet);
    connectBarOneToInhibNInstRule(ruleNet);
    connectWordActToNPDoneRule(ruleNet);
    connectWordActToActVInstRule(ruleNet);
    connectBarOneToInhibVInstRule(ruleNet);
    connectWordActToMainVerbRule(ruleNet);
    connectBarOneToVPDoneRule(ruleNet);
    connectBarOneToInhibBarTwoRule(1500,ruleNet); //VP -> VP NPObj 1 1
    connectBarOneToInhibBarTwoRule(1600,ruleNet); //VP -> VP NPObj 1 2
    connectBarOneToInhibBarTwoRule(1200,ruleNet); //VP -> VP PPloc 1 1
    connectBarOneToInhibBarTwoRule(1300,ruleNet); //VP -> VP PPloc 1 2
    connectBarOneToInhibBarTwoRule(1400,ruleNet); //VP -> VP PPloc 1 3
    connectBarOneToInhibBarTwoRule(1800,ruleNet); //VP -> NPAct VP 1 1
    connectBarOneToInhibBarTwoRule(2100,ruleNet); //NP -> NPPP 1 2
    connectBarOneToInhibBarTwoRule(2200,ruleNet); //NP -> NPPP 2 3
    connectWordActToNPAddPrepRule(ruleNet);
    connectBarOneToPrepDoneRule(ruleNet);
    connectWordActToNPAddDetRule(ruleNet);
  }

  //10 connections to 1 neuron. .25 gives 2.5 each cycle, so 2.5, 3.75 >4
  private void connectOneAccessToOneSemFeature(CABot2Net semNet,
    int accessCA, int semFeature) {

    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (accessCA*100) + neuron;
      int toNeuron = (neuron%5) + (semFeature*nounFeatureSize);
      neurons[fromNeuron].addConnection(semNet.neurons[toNeuron],0.25);
    }
  }

  private void connectOneAccessToOneSemFeature(CABot2Net semNet,
    int accessCA, int semFeature, int hierStartFeature) {
    for (int feature = semFeature*3; feature < (semFeature*3) +3; feature++ ){
      connectOneAccessToOneSemFeature(semNet,accessCA,
        hierStartFeature+feature);
    }
  }

  //works for both nounaccess->nounSem and verbAccess -> verbSem
  private void connectOneAccessToOneSemWord(CABot2Net semNet,
    int accessCA, int semWord) {
    for (int feature = semWord*6; feature < (semWord*6) +6; feature++ ){
      connectOneAccessToOneSemFeature(semNet,accessCA,feature);
    }
  }

  private int nounHierStart = 240*3; 
  public void connectNounAccessToSem(CABot2Net nounSemNet) {
    //undone, this needs to be data driven.
    //left position=17 location=10 object=13 physical-entity=16
    connectOneAccessToOneSemWord(nounSemNet,0,0);
    connectOneAccessToOneSemFeature(nounSemNet,0,17,nounHierStart);
    connectOneAccessToOneSemFeature(nounSemNet,0,10,nounHierStart);
    connectOneAccessToOneSemFeature(nounSemNet,0,13,nounHierStart);
    connectOneAccessToOneSemFeature(nounSemNet,0,16,nounHierStart);
 
    //pyramid artefact=2 object=13 physical-entity=16 
    connectOneAccessToOneSemWord(nounSemNet,1,1);
    connectOneAccessToOneSemFeature(nounSemNet,1,2,nounHierStart);
    connectOneAccessToOneSemFeature(nounSemNet,1,13,nounHierStart);
    connectOneAccessToOneSemFeature(nounSemNet,1,16,nounHierStart);
    //it
    connectOneAccessToOneSemWord(nounSemNet,2,2);
    //stalactite  artefact=2 object=13 physical-entity=16 
    connectOneAccessToOneSemWord(nounSemNet,3,3);
    connectOneAccessToOneSemFeature(nounSemNet,3,2,nounHierStart);
    connectOneAccessToOneSemFeature(nounSemNet,3,13,nounHierStart);
    connectOneAccessToOneSemFeature(nounSemNet,3,16,nounHierStart);
    //I?
    //gun weapon= device=5 artefact=2 object=13 physical-entity=16 
    connectOneAccessToOneSemWord(nounSemNet,5,5);
    connectOneAccessToOneSemFeature(nounSemNet,5,5,nounHierStart);
    connectOneAccessToOneSemFeature(nounSemNet,5,2,nounHierStart);
    connectOneAccessToOneSemFeature(nounSemNet,5,13,nounHierStart);
    connectOneAccessToOneSemFeature(nounSemNet,5,16,nounHierStart);
    //girl undone
    connectOneAccessToOneSemWord(nounSemNet,6,6);
    //telescope
    connectOneAccessToOneSemWord(nounSemNet,7,7);
    //door
    connectOneAccessToOneSemWord(nounSemNet,8,8);
    //handle
    connectOneAccessToOneSemWord(nounSemNet,9,9);

    connectOneAccessToOneSemWord(nounSemNet,10,10); //right
    connectOneAccessToOneSemWord(nounSemNet,11,11); //forward
    connectOneAccessToOneSemWord(nounSemNet,12,12); //backward
  }

  private int verbHierStart = 120*3; 
  public void connectVerbAccessToSem(CABot2Net verbSemNet) {
    //undone, this needs to be data driven.
    //move physical=16 interpersonal-volitional=10
    connectOneAccessToOneSemWord(verbSemNet,0,0);
    connectOneAccessToOneSemFeature(verbSemNet,0,16,verbHierStart);
    connectOneAccessToOneSemFeature(verbSemNet,0,10,verbHierStart);
    //turn physical=16 interpersonal-volitional=10
    connectOneAccessToOneSemWord(verbSemNet,1,1);
    connectOneAccessToOneSemFeature(verbSemNet,1,16,verbHierStart);
    connectOneAccessToOneSemFeature(verbSemNet,1,10,verbHierStart);
    //undone add 2 found, 3 saw, 4 go, 6 center
    connectOneAccessToOneSemWord(verbSemNet,2,2);
    connectOneAccessToOneSemWord(verbSemNet,3,3);
    connectOneAccessToOneSemWord(verbSemNet,4,4);
    connectOneAccessToOneSemWord(verbSemNet,6,6);
  }


  //The active access word primes but does not activate the word.
  //10 connections to 1 neuron. .125 gives 1.25 each cycle, so 1.25, 1.8725,
  // ... < 3
  private void connectNounAccessWordToActNInstRule(int word, 
    CABot2Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  private void connectNounAccessWordToNPFromN(int word, 
    CABot2Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 100;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.11);
      }
    }
  }
  private void connectNounAccessWordToNPDone(int word, 
    CABot2Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 200;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.11);
      }
    }
  }

  public void connectNounAccessToRule(CABot2Net ruleNet) {
    for (int word = 0; word < numNouns; word ++) {
      connectNounAccessWordToActNInstRule(word,ruleNet);
      connectNounAccessWordToNPFromN(word,ruleNet);
      connectNounAccessWordToNPDone(word,ruleNet);
    }
  }
  
  //4 neurons to 5
  private void NewNInstStartBarOne(CABot2Net barOneNet) {
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
  private void NPDoneExtinguishBarOne(CABot2Net barOneNet) {
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
  private void ReadNextIgnitesWordOn(CABot2Net barOneNet) {
    for (int neuron = 300; neuron < 400; neuron++) {
      if ((neuron%5) != 0) { //skip inhibitory neuron.
        for (int synapse=0;synapse<5;synapse++) {
          int toNeuron = ((neuron/5)*5)+synapse-300;
          neurons[neuron].addConnection(barOneNet.neurons[toNeuron],1.1);
        }
      }
    }
  }
  private void NewVInstStartBarOne(CABot2Net barOneNet) {
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
  private void VPDoneExtinguishBarOne(CABot2Net barOneNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<5;synapse++) {
        int toNeuron = neuron+synapse;
        int fromNeuron = neuron+600; //NPDoneOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
        toNeuron += 100;
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
      } } }

  private void PrepDoneExtinguishWordOn(CABot2Net barOneNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<5;synapse++) {
        int toNeuron = neuron+synapse;
        int fromNeuron = neuron+1000; //PrepDoneOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
      } } }
  private void NPAddDetExtinguishWordOn(CABot2Net barOneNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<5;synapse++) {
        int toNeuron = neuron+synapse;
        int fromNeuron = neuron+1100; //NPAddDetOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
      }
    }
  }

  public void connectRuleToBarOne(CABot2Net barOneNet) {
    NewNInstStartBarOne(barOneNet);
    NPDoneExtinguishBarOne(barOneNet);
    ReadNextIgnitesWordOn(barOneNet);
    NewVInstStartBarOne(barOneNet);
    VPDoneExtinguishBarOne(barOneNet);
    PrepDoneExtinguishWordOn(barOneNet);
    NPAddDetExtinguishWordOn(barOneNet);
  }

  private void connectNPFromNToMainFeature(CABot2Net nounInstanceNet) {
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
  private void connectNPDoneToDoneFeature(CABot2Net nounInstanceNet) {
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

  private void connectNPAddPrepToPrepFeature(CABot2Net nounInstanceNet) {
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
  private void connectPrepDoneToPrepOnFeature(CABot2Net nounInstanceNet) {
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
  private void connectNPAddDetToDetFeature(CABot2Net nounInstanceNet) {
    for (int neuron=1100;neuron<1200;neuron++) {
      for (int word=0;word< 3;word ++) {
        if (neuron%5 !=0) { //skip the inhibitory ones
          int toNeuron = (neuron%20);
          toNeuron += (word*nInstCASize)+140;
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
            0.25);
          if (toNeuron%5 ==1) { //make up for the inhibitory one
            neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron-1],
              0.25);
	  }}}}}

  private void connectVPAddNPToABoundFeature(int ruleStart,int inst,
    CABot2Net nounInstanceNet) {
    for (int neuron=ruleStart;neuron<ruleStart+100;neuron++) {
      if (!neurons[neuron].isInhibitory()) {  //80->10
	int toNeuron=(neuron%100)/10;  //0..10
        if (toNeuron >= 5) toNeuron +=5; //0..4,10..14
        toNeuron += 280 + (inst*nInstCASize);
        neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
          0.34); //should take 11 cycles
      }
    }
  }
  private void connectVPAddObjToBoundFeature(CABot2Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(1500,0,nounInstanceNet);
  }
  private void connectVPAddLocToBoundFeature(CABot2Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(1200,0,nounInstanceNet);
    connectVPAddNPToABoundFeature(1300,1,nounInstanceNet);
    connectVPAddNPToABoundFeature(1400,2,nounInstanceNet);
  }
  private void connectVPAddActToBoundFeature(CABot2Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(1800,0,nounInstanceNet);
  }

  private void connectNPAddPPToBoundFeature(CABot2Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(2100,1,nounInstanceNet);
    connectVPAddNPToABoundFeature(2200,2,nounInstanceNet);
  }

  private void connectNPAddPPToPPModFeature(int toNounInst, int ruleStart, 
    CABot2Net nounInstanceNet) {
    for (int neuron=ruleStart;neuron<ruleStart+100;neuron++) {
      if (neuron%5 !=0) { //skip the inhibitory ones
        int toNeuron = (neuron%20);
        toNeuron += (toNounInst*nInstCASize)+200;
        neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
          0.25);
        if (toNeuron%5 ==1) { //make up for the inhibitory one
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron-1],
            0.25);
	}
      }
    }
  }

  public void connectRuleToNounInstance(CABot2Net nounInstanceNet) {
    connectNPFromNToMainFeature(nounInstanceNet);
    connectNPDoneToDoneFeature(nounInstanceNet);
    connectNPAddPrepToPrepFeature(nounInstanceNet);
    connectPrepDoneToPrepOnFeature(nounInstanceNet);
    connectNPAddDetToDetFeature(nounInstanceNet);
    connectVPAddObjToBoundFeature(nounInstanceNet);
    connectVPAddLocToBoundFeature(nounInstanceNet);
    connectVPAddActToBoundFeature(nounInstanceNet);
    connectNPAddPPToBoundFeature(nounInstanceNet);
    connectNPAddPPToPPModFeature(0,2100,nounInstanceNet);
    connectNPAddPPToPPModFeature(1,2200,nounInstanceNet);
  }
  
  private void NPDoneExtinguishNounAccessWord(int word,CABot2Net nounAccessNet)
  {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<10;synapse++) {
        int toNeuron = ((neuron+synapse)%100)+(word*100);
        int fromNeuron = neuron+200; //NPDoneOffset
        neurons[fromNeuron].addConnection(nounAccessNet.neurons[toNeuron],
          -4.0);
      }
    }
  }
  public void connectRuleToNounAccess(CABot2Net nounAccessNet) {
    for (int word = 0; word < numNouns; word ++) {
      NPDoneExtinguishNounAccessWord(word,nounAccessNet);
    }
  }

  private void VPDoneExtinguishVerbAccessWord(int word,CABot2Net verbAccessNet)
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
  public void connectRuleToVerbAccess(CABot2Net verbAccessNet) {
    for (int word = 0; word < numVerbs; word ++) {
      VPDoneExtinguishVerbAccessWord(word,verbAccessNet);
    }
  }
  private void prepDoneExtinguishAPrep(int word,CABot2Net otherNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<10;synapse++) {
	int toNeuron = neuron+synapse+(word*100);
        int fromNeuron = neuron+1000; //PrepDoneOffset
        neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron],-2.0);
      }
    }
  }
  private void prepDoneExtinguishPreps(CABot2Net otherNet) {
    prepDoneExtinguishAPrep(1,otherNet); //toward
    prepDoneExtinguishAPrep(3,otherNet); //with
    prepDoneExtinguishAPrep(4,otherNet); //to
  }

  private void NPAddDetExtinguishADet(int word,CABot2Net otherNet) {
    for (int neuron=0; neuron<100;neuron+=5) {//Every 5th is inhibitory
      for (int synapse=0;synapse<10;synapse++) {
	int toNeuron = neuron+synapse+(word*100);
        int fromNeuron = neuron+1100; //NPAddDetOffset
        neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron],-2.0);
      }
    }
  }
  private void NPAddDetExtinguishDets(CABot2Net otherNet) {
    NPAddDetExtinguishADet(2,otherNet); //toward
  }
  public void connectRuleToOther(CABot2Net otherNet) {
    prepDoneExtinguishPreps(otherNet);
    NPAddDetExtinguishDets(otherNet);
  }

  //like connectNPFromNToMainFeature.  Connect the rule to the main
  //main feature so that, in conjuction with the active instance,
  //the main feature comes on
  private void connectMainVerbToMainFeature(CABot2Net verbInstanceNet) {
    for (int neuron=500;neuron<600;neuron++) {
      for (int word=0;word< 2;word ++) {
        if (neuron%5 !=0) { //skip the inhibitory ones
          int toNeuron = (neuron%20);
          toNeuron += (word*vInstCASize)+120;
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron],
            0.25);
          if (toNeuron%5 ==1) { //make up for the inhibitory one
            neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron-1],
              0.25);
	  }
	}
      }
    }
  }
  private void connectVPDoneToDoneFeature(CABot2Net verbInstanceNet) {
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
    CABot2Net verbInstanceNet) {
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
    CABot2Net verbInstanceNet) {
    for (int neuron=ruleStart;neuron<ruleStart+100;neuron++) {
      if (neuron%5 !=0) { //skip the inhibitory ones
        int toNeuron = (neuron%20);
        toNeuron += (instance*vInstCASize)+240;
        neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron],0.3);
        if (toNeuron%5 ==1) { //make up for the inhibitory one
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron-1],
            0.3);
        } } } }
  private void connectVPFromNPActVPToActFeature(int instance, int ruleStart,
    CABot2Net verbInstanceNet) {
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

  public void connectRuleToVerbInstance(CABot2Net verbInstanceNet) {
    connectMainVerbToMainFeature(verbInstanceNet);
    connectVPDoneToDoneFeature(verbInstanceNet);
    connectVPFromVPNPObjToObjFeature(0,1500,verbInstanceNet);
    connectVPFromVPNPObjToObjFeature(0,1600,verbInstanceNet);
    connectVPFromVPPPLocToLocFeature(0,1200,verbInstanceNet);
    connectVPFromVPPPLocToLocFeature(0,1300,verbInstanceNet);
    connectVPFromVPPPLocToLocFeature(0,1400,verbInstanceNet);
    connectVPFromNPActVPToActFeature(0,1800,verbInstanceNet);
  }

  private void connectMainNounToWord(int instance,int word,
    CABot2Net nounAccessNet) {
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
  public void connectNounInstanceToNounAccess(CABot2Net nounAccessNet) {
    for (int instance = 0; instance < 3; instance++) {
      for (int word = 0; word < numNouns; word++) {
        //connect main verb feature to verbAccess
        connectMainNounToWord(instance,word,nounAccessNet);
      }
    }
  }

  private void connectNounInstanceToRule(int fromInstance,double weight,
					 int toStart, CABot2Net ruleNet) {
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
  }

  //Connect each time neuron in the instance to the rule.
  private void connectNounInstanceTimeToRule(int fromInstance,double weight,
					 int toStart, CABot2Net ruleNet) {
    int fromStart=fromInstance*nInstCASize+300;
    //for each neuron in time (160) excitatory neurons
    //half of the rule neurons get 4 connections (the rest 0)
    for (int neuron = 0; neuron<160;neuron++) {
      int fromNeuron = neuron + fromStart;
      for (int synapse = 0; synapse < 50; synapse ++) {
        int toNeuron = (synapse/5)*10; //0,10,20,..90
        toNeuron += (synapse%5); //0..4,10..14,.., 90..94
        toNeuron += toStart; 
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],weight);
      }
    }
  }

  private void connectPrepOnToRule(int inst,int ruleStart,
    CABot2Net ruleNet) {
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
            neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],.5);
          }
	}
      }
    }
  }

  private void connectPrepOnPreventVPNPRule(int inst,CABot2Net ruleNet,
    int ruleStart) {
    int fromStart=inst*vInstCASize;
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
    CABot2Net ruleNet) {
    for (int neuron = 0; neuron<4;neuron ++) {
      int fromNeuron = (neuron*5)+(instance*nInstCASize);
      fromNeuron +=280; //bound slot 280
      for (int synapse = 0; synapse < 25; synapse ++) {
        int toNeuron = (neuron*25)+synapse+ruleStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],-2.0);
      }
    }
  }
  private void boundNounInstancesSlotStopsRules(CABot2Net ruleNet) {
    boundInstanceStopsRule(0,1500,ruleNet);
    boundInstanceStopsRule(0,1200,ruleNet);
    boundInstanceStopsRule(0,1800,ruleNet);
    boundInstanceStopsRule(1,1300,ruleNet);
    boundInstanceStopsRule(2,1400,ruleNet);
    boundInstanceStopsRule(1,2100,ruleNet);
    boundInstanceStopsRule(2,2200,ruleNet);
  }

  public void connectNounInstanceToRule(CABot2Net ruleNet) {
    connectNounInstanceTimeToRule(0,0.015,1500,ruleNet); //VP -> VPObj 1 1
    connectNounInstanceTimeToRule(1,0.015,1600,ruleNet); //VP -> VPObj 1 2
    connectNounInstanceTimeToRule(0,0.007,1800,ruleNet); //VP -> NPActVP 1 1 .008
    connectNounInstanceToRule(0,0.3,1200,ruleNet); //VP -> VPPPloc 1 1
    connectNounInstanceToRule(1,0.3,1300,ruleNet); //VP -> VPPPloc 1 2
    connectNounInstanceToRule(2,0.3,1400,ruleNet); //VP -> VPPPloc 1 3
    connectPrepOnToRule(0,1200,ruleNet);//VP -> VPPPloc 1 1
    connectPrepOnToRule(1,1300,ruleNet);//VP -> VPPPloc 1 2
    connectPrepOnToRule(2,1400,ruleNet);//VP -> VPPPloc 1 3
    connectNounInstanceToRule(0,0.5,2100,ruleNet); //NP -> NPPP 1 2
    connectNounInstanceToRule(1,0.3,2100,ruleNet); //NP -> NPPP 1 2 
    connectNounInstanceToRule(1,0.5,2200,ruleNet); //NP -> NPPP 2 3
    connectNounInstanceToRule(2,0.3,2200,ruleNet); //NP -> NPPP 2 3
    connectPrepOnToRule(1,2100,ruleNet);//NP -> NPPP 1 2 
    connectPrepOnToRule(2,2200,ruleNet);//NP -> NPPP 2 3 
    for (int instance = 0; instance < 1; instance++) {
      connectPrepOnPreventVPNPRule(instance,ruleNet,1500);
      connectPrepOnPreventVPNPRule(instance,ruleNet,1800);
    }
    boundNounInstancesSlotStopsRules(ruleNet);
   }

  private void connectPrepFeatureToAPrep(int word,CABot2Net otherNet) {
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
  private void connectPrepFeatureToPreps(CABot2Net otherNet) {
    connectPrepFeatureToAPrep(1,otherNet); //toward
    connectPrepFeatureToAPrep(3,otherNet);  //with
    connectPrepFeatureToAPrep(4,otherNet);  //to
  }
  public void connectNounInstanceToOther(CABot2Net otherNet) {
   connectPrepFeatureToPreps(otherNet); 
  }


  //The active access word primes but does not activate the word.
  //10 connections to 1 neuron. .125 gives 1.25 each cycle, so 1.25, 1.8725,
  // ... < 3
  private void connectVerbAccessWordToActVInstRule(int word,CABot2Net ruleNet) 
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
    CABot2Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 500;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  private void connectVerbAccessWordToVPDone(int word, 
    CABot2Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 600;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.11);
      }
    }
  }

  public void connectVerbAccessToRule(CABot2Net ruleNet) {
    for (int word = 0; word < numVerbs; word ++) {
      connectVerbAccessWordToActVInstRule(word,ruleNet);
      connectVerbAccessWordToMainVerb(word,ruleNet);
      connectVerbAccessWordToVPDone(word,ruleNet);
    }
  }
  
  //Each access has to get connections from either side of the 
  //instance so it can bind.
  public void connectVerbInstanceToVerbAccess(CABot2Net verbAccessNet) {
    for (int instance = 0; instance < 2; instance++) {
      for (int word = 0; word < numVerbs; word++) {
        //connect main verb feature to verbAccess
        for (int neuron = 126; neuron<134;neuron++) {
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
 					  int toStart, CABot2Net ruleNet) {
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
  }

  private void verbSlotInhibitsRule(int slotStart,int ruleStart, 
    CABot2Net ruleNet) {
    for (int i = 0; i < 4; i++) {
      int fromNeuron = (i*5) + slotStart;
      for (int synapse = 0; synapse < 25; synapse++) {
        int toNeuron = (i*25) + synapse + ruleStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],-1.5);
      }
    }
  }
  
  public void connectVerbInstanceToRule(CABot2Net ruleNet) {
    int instStart = 0; //vInstCASize;
    connectAVerbInstanceToRule(instStart,0.45,1500,ruleNet); //VP -> VPObj 1 1
    connectAVerbInstanceToRule(instStart,0.45,1600,ruleNet); //VP -> VPObj 1 2
    connectAVerbInstanceToRule(instStart,0.5,800,ruleNet); //S ->VP-Period
    connectAVerbInstanceToRule(0,0.5,1200,ruleNet); //VP->VPPPloc 1 1
    connectAVerbInstanceToRule(0,0.5,1300,ruleNet); //VP->VPPPloc 1 2
    connectAVerbInstanceToRule(0,0.5,1400,ruleNet); //VP->VPPPloc 1 3
    connectAVerbInstanceToRule(0,0.74,1800,ruleNet); //VP->NPactVP 1 1
    //undone why don't obj and act done inhibit their rules
    verbSlotInhibitsRule(100,1300,ruleNet); 
  }

  //Each Verb has 5 features that can bind to NInstance (actor, object,
  // instrument,location,time)
  //Each slot binds to the slot 
  //Each neuron needs 10 connections. (5 to each side)
  private void connectAVInstanceSlotToANInstance(int fromOffset,int vInst, 
    int nInst, CABot2Net ruleNet) {
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
    CABot2Net ruleNet) {
    connectAVInstanceSlotToANInstance(160,vInst,nInst,ruleNet); //actor slot
    connectAVInstanceSlotToANInstance(180,vInst,nInst,ruleNet); //object slot
    connectAVInstanceSlotToANInstance(240,vInst,nInst,ruleNet); //loc slot
  }
  public void connectVerbInstanceToNounInstance(CABot2Net ruleNet) {
    for (int vInstance = 0; vInstance < 2; vInstance++) {
      for (int nInstance = 0; nInstance < 3; nInstance++) {
        connectAVInstanceToANInstance(vInstance,nInstance,ruleNet);
      }
    }
  }

  private void connectPeriodToSFromVPPeriod(CABot2Net ruleNet) 
  {
    for (int neuron=0; neuron<100;neuron++) {
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*10) + 800;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  private void  connectAPrepToNInstFromPrep(int otherCA,CABot2Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (otherCA*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  private void connectAPrepToNPAddPrep(int word, CABot2Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 900;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  private void connectAPrepToPrepDone(int word, CABot2Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 1000;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.11);
      }
    }
  }
  private void  connectPrepsToRules(CABot2Net ruleNet) {
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

  private void connectADetToNPAddDet(int word, CABot2Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (word*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize) + 1100;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  private void  connectADetToNInstFromDet(int otherCA,CABot2Net ruleNet) {
    for (int neuron=0; neuron<100;neuron++) {
      int fromNeuron = (otherCA*100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature ++) {
        int toNeuron = (neuron%5) + (rule1Feature*ruleFeatureSize);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],0.125);
      }
    }
  }
  private void  connectDetsToRules(CABot2Net ruleNet) {
    connectADetToNPAddDet(2,ruleNet);
    connectADetToNInstFromDet(2,ruleNet);
  }
  public void connectOtherToRule(CABot2Net ruleNet) {
    connectPeriodToSFromVPPeriod(ruleNet);
    connectPrepsToRules(ruleNet);
    connectDetsToRules(ruleNet);
  }

  private void connectASemToARuleSel(int word, int pref, double weight,
    CABot2Net vPPPNet) {
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

  public void connectVerbSemToVPPP(CABot2Net vPPPNet) {
    connectASemToARuleSel(0,0,0.4,vPPPNet); //MOVE it stalactite
    connectASemToARuleSel(3,1,0.4,vPPPNet); //SAW girl telescope
  }
  public void connectNounSemToVPPP(CABot2Net vPPPNet) {
    connectASemToARuleSel(2,0,0.4,vPPPNet); //move IT stalactite
    connectASemToARuleSel(3,0,0.8,vPPPNet); //move it STALACTITE
    connectASemToARuleSel(6,1,0.4,vPPPNet); //saw GIRL telescope
    connectASemToARuleSel(7,1,0.8,vPPPNet); //saw girl TELESCOPE
  }

  public void connectVerbSemToNPPP(CABot2Net nPPPNet) {
    connectASemToARuleSel(0,1,0.4,nPPPNet); //MOVE door handle
  }
  public void connectNounSemToNPPP(CABot2Net nPPPNet) {
    connectASemToARuleSel(8,1,0.4,nPPPNet); //move DOOR handle
    connectASemToARuleSel(9,1,0.8,nPPPNet); //move door HANDLE
  }

  public void connectVPPPToRule(CABot2Net ruleNet) {
    int prefCAs = getSize()/preferenceCASize;
    for (int prefCA = 0; prefCA < prefCAs; prefCA++) {
      for (int i=0;i<preferenceCASize; i++) {
        int fromNeuron = (prefCA*preferenceCASize)+i;
        if (i < 50) {
          int toNeuron1 = (i%5) + ((i/5)*10) + 1200;
          int toNeuron2 = toNeuron1+10;
          if ((i%10) > 4) toNeuron2 -=20;
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1],0.4);
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2],0.4);
          toNeuron1 += 100;          
          toNeuron2 += 100;          
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1],0.4);
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2],0.4);
          toNeuron1 += 100;          
          toNeuron2 += 100;          
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1],0.4);
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2],0.4);
	}
      }
    }
  }

  public void connectNPPPToRule(CABot2Net ruleNet) {
    int prefCAs = getSize()/preferenceCASize;
    for (int prefCA = 0; prefCA < prefCAs; prefCA++) {
      for (int i=0;i<preferenceCASize; i++) {
        int fromNeuron = (prefCA*preferenceCASize)+i;
        if (i < 50) {
          int toNeuron1 = (i%5) + ((i/5)*10) + 2100;
          int toNeuron2 = toNeuron1+10;
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1],0.4);
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2],0.4);
	  toNeuron1 += 100;          
          toNeuron2 += 100;          
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1],0.4);
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2],0.4);
	}
      }
    }
  }

  //-----------Set up topology for networks-------------------
  /*****end of intra parsing subnet connections**/
  private void setGoalSetTopology() {
    setFiftyFiftyTopology(1.1);
  }

  
  //undone will need to rewrite for CABot2
  public void readBetweenAllNets() {
    System.out.println("CABot 1 read Between");
    int netsChecked = 0;
    Enumeration eNum = CANT23.nets.elements();
    CABot2Net net = (CABot2Net)eNum.nextElement();

    CABot2Net  inputNet = net;
    CABot2Net  barOneNet = net;
    CABot2Net  nounAccessNet = net;
    CABot2Net  verbAccessNet = net;
    CABot2Net  nounSemNet = net;
    CABot2Net  verbSemNet = net;
    CABot2Net  otherNet = net;
    CABot2Net  nounInstanceNet = net;
    CABot2Net  verbInstanceNet = net;
    CABot2Net  ruleNet = net;
    CABot2Net  vPPPNet = net;
    CABot2Net  nPPPNet = net;
    CABot2Net  visualInputNet = net;
    CABot2Net  retinaNet = net;
    CABot2Net  V1Net = net;
    CABot2Net  V2Net = net;
    CABot2Net  controlNet = net;
    CABot2Net  goal1Net = net;
    CABot2Net  factNet = net;
    CABot2Net  goalSetNet = net;
    CABot2Net  moduleNet = net;
    CABot2Net  actionNet = net;
    CABot2Net goal2Net = net;
    CABot2Net valueNet = net;
    CABot2Net value2Net = net;
    CABot2Net module2Net = net;
    CABot2Net exploreNet = net;

    do  {
System.out.println(net.getName());
      if (net.getName().compareTo("BaseNet") == 0)
        inputNet = net;
      else if (net.getName().compareTo("BarOneNet") == 0)
        barOneNet = net;
      else if (net.getName().compareTo("NounAccessNet") == 0)
        nounAccessNet = net;
      else if (net.getName().compareTo("VerbAccessNet") == 0)
        verbAccessNet = net;
      else if (net.getName().compareTo("NounSemNet") == 0)
        nounSemNet = net;
      else if (net.getName().compareTo("VerbSemNet") == 0)
        verbSemNet = net;
      else if (net.getName().compareTo("OtherNet") == 0)
        otherNet = net;
      else if (net.getName().compareTo("NounInstanceNet") == 0)
        nounInstanceNet = net;
      else if (net.getName().compareTo("VerbInstanceNet") == 0)
        verbInstanceNet = net;
      else if (net.getName().compareTo("RuleNet") == 0)
        ruleNet = net;
      else if (net.getName().compareTo("VPPPNet") == 0)
        vPPPNet = net;
      else if (net.getName().compareTo("NPPPNet") == 0)
        nPPPNet = net;
      else if (net.getName().compareTo("VisualInputNet") == 0)
	visualInputNet = net;
      else if (net.getName().compareTo("RetinaNet") == 0)
	retinaNet = net;
      else if (net.getName().compareTo("V1Net") == 0)
	V1Net = net;
      else if (net.getName().compareTo("V2Net") == 0)
	V2Net = net;
      else if (net.getName().compareTo("ControlNet") == 0)
	controlNet = net;
      else if (net.getName().compareTo("Goal1Net") == 0)
	goal1Net = net;
      else if (net.getName().compareTo("FactNet") == 0)
	factNet = net;
      else if (net.getName().compareTo("GoalSetNet") == 0)
	goalSetNet = net;
      else if (net.getName().compareTo("ModuleNet") == 0)
	moduleNet = net;
      else if (net.getName().compareTo("ActionNet") == 0)
	actionNet = net;
      else if (net.getName().compareTo("Goal2Net") == 0)
	goal2Net = net;
      else if (net.getName().compareTo("ValueNet") == 0)
	valueNet = net;
      else if (net.getName().compareTo("Value2Net") == 0)
	value2Net = net;
      else if (net.getName().compareTo("Module2Net") == 0)
	module2Net = net;
      else if (net.getName().compareTo("ExploreNet") == 0)
	exploreNet = net;
      else 	
        System.out.println(net.getName() + " missed net in connect all");
      netsChecked++;
      if (netsChecked < 27) 
        net = (CABot2Net)eNum.nextElement();
    } while (netsChecked < 27);

    //connectparseNets
    inputNet.readConnectTo(nounAccessNet); 
    inputNet.readConnectTo(verbAccessNet);
    inputNet.readConnectTo(otherNet);
    barOneNet.readConnectTo(nounAccessNet);
    barOneNet.readConnectTo(verbAccessNet);
    barOneNet.readConnectTo(otherNet);
    barOneNet.readConnectTo(ruleNet);
    nounAccessNet.readConnectTo(nounSemNet);
    nounAccessNet.readConnectTo(ruleNet);
    ruleNet.readConnectTo(barOneNet);
    ruleNet.readConnectTo(nounInstanceNet);
    ruleNet.readConnectTo(nounAccessNet);
    ruleNet.readConnectTo(verbInstanceNet);
    ruleNet.readConnectTo(verbAccessNet);
    ruleNet.readConnectTo(otherNet);
    nounInstanceNet.readConnectTo(nounAccessNet);
    nounInstanceNet.readConnectTo(ruleNet);
    nounInstanceNet.readConnectTo(otherNet);
    verbAccessNet.readConnectTo(verbSemNet);
    verbAccessNet.readConnectTo(ruleNet);
    verbInstanceNet.readConnectTo(verbAccessNet);
    verbInstanceNet.readConnectTo(ruleNet);
    verbInstanceNet.readConnectTo(nounInstanceNet);
    otherNet.readConnectTo(ruleNet);
    verbSemNet.readConnectTo(vPPPNet);
    nounSemNet.readConnectTo(vPPPNet);
    verbSemNet.readConnectTo(nPPPNet);
    nounSemNet.readConnectTo(nPPPNet);
    vPPPNet.readConnectTo(ruleNet);
    nPPPNet.readConnectTo(ruleNet);

    //connect vision nets 
    visualInputNet.readConnectTo(retinaNet);
    retinaNet.readConnectTo(V1Net);
    V1Net.readConnectTo(V2Net); 

    //integrate parse3 with goal setting
    verbSemNet.readConnectTo(goalSetNet);
    nounSemNet.readConnectTo(goalSetNet);
    controlNet.readConnectTo(goalSetNet);
    goalSetNet.readConnectTo(goal1Net);

    //connect visual items to facts
    V2Net.readConnectTo(factNet);
    factNet.readConnectTo(V2Net);

    //connect facts, modules and actions
    goal1Net.readConnectTo(moduleNet);
    goal1Net.readConnectTo(factNet);
    factNet.readConnectTo(goal1Net);
    factNet.readConnectTo(moduleNet);
    moduleNet.readConnectTo(goal1Net);
    moduleNet.readConnectTo(factNet);
    moduleNet.readConnectTo(actionNet);
    actionNet.readConnectTo(moduleNet);

    //set up goal learning
    goal1Net.readConnectTo(goal2Net);
    factNet.readConnectTo(goal2Net);
    goal2Net.readConnectTo(module2Net);
    valueNet.readConnectTo(exploreNet);
    exploreNet.readConnectTo(module2Net);
    factNet.readConnectTo(valueNet);
    module2Net.readConnectTo(moduleNet);
    goal2Net.readConnectTo(moduleNet);
    valueNet.readConnectTo(value2Net);
    value2Net.readConnectTo(goal2Net);
    value2Net.readConnectTo(factNet);
    value2Net.readConnectTo(valueNet);


    //Reset any fastbind weights that have been saved.
    CABot2Experiment exp = (CABot2Experiment)CABot2.experiment;
    //exp.clearFastBindNeurons();
  }
  
  
  public void runAllOneStep(int CANTStep) {
    //This series of loops is really chaotic, but I needed to
    //get all of the propogation done in each net in step.
    CABot2.runOneStepStart();
	
    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot2Net net = (CABot2Net)eNum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    eNum = CANT23.nets.elements();
      while (eNum.hasMoreElements()) {
        CABot2Net net = (CABot2Net)eNum.nextElement();
        net.setExternalActivation(CANTStep);
      }
    //net.propogateChange();  
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot2Net net = (CABot2Net)eNum.nextElement();
      net.spontaneousActivate();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot2Net net = (CABot2Net)eNum.nextElement();
      net.setNeuronsFired();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot2Net net = (CABot2Net)eNum.nextElement();
      net.setDecay ();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot2Net net = (CABot2Net)eNum.nextElement();
      net.spreadActivation();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot2Net net = (CABot2Net)eNum.nextElement();
      net.setFatigue();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot2Net net = (CABot2Net)eNum.nextElement();
      net.learn();
      if ((net.getName().compareTo("NounInstanceNet") == 0) ||
          (net.getName().compareTo("VerbInstanceNet") == 0))
        net.fastLearn();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot2Net net = (CABot2Net)eNum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot2Net net = (CABot2Net)eNum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
//if (net.getName().compareTo("VerbNet") == 0)   System.out.println(net.neurons[0].getFatigue() +   " verb Neuron " + net.neurons[0].getActivation());
    }
  }

  private void fastLearn(int firstFast, int lastFast) {
    for (int neuronIndex = 0; neuronIndex < size(); neuronIndex++) 
      {
      if (neurons[neuronIndex] instanceof CANTNeuronFastBind) 
        ((CANTNeuronFastBind)neurons[neuronIndex]).fastLearn();
      }
  }

  public CANTNet getNewNet(String name,int cols, int rows,int topology){
  	CABot2Net net = new CABot2Net (name,cols,rows,topology);
	return (net);
	} 

  /********Vision Connectivity Functions*/
  //to change from 100x100 to 50x50 change these three values
  //it should presumably work for others too.
  //You also need to change the name placement in CANT23Vision.positionWindows
  private int getInputSize(){ return (2500);}
  private int getInputRows(){ return (50);}
  private int getInputCols(){ return (50);}

  private void addOneConnection(int fromRow, int fromCol, CANTNeuron toNeuron,
                                double weight) {
   if ((fromRow < 0) ||(fromRow >= getRows()) ||(fromCol < 0) || (fromCol >= getCols()))
   return;
	   								
   int inputNeuron = fromRow * getCols() + fromCol;
   neurons[inputNeuron].addConnection(toNeuron,weight);
  }

  private void addOneRetinaConnection(int fromRow, int fromCol, int retCat,
    CANTNeuron toNeuron, double weight) {
    if ((fromRow < 0) ||(fromRow >= getInputRows()) ||(fromCol < 0) || 
        (fromCol >= getInputCols())) {
	//System.out.println(fromRow+" " + fromCol + " put " + retCat);
      return;
    }

       								
    int inputNeuron = (fromRow+(getCols()*retCat)) * getCols() + fromCol;
  
    neurons[inputNeuron].addConnection(toNeuron,weight);
  }
    /*
  private void addOneRetinaConnection(int fromRow, int fromCol, 
    CANTNeuron toNeuron, double weight) {
    //if ((fromRow < 0) ||(fromRow >= getRows()) ||(fromCol < 0) || (fromCol >= getCols()))
    if ((fromRow < 0) || (fromCol < 0) || (fromCol >= getCols()) || 
         fromRow >= getRows()*6)
      return;
     								
    int inputNeuron = fromRow * getCols() + fromCol;
  
    neurons[inputNeuron].addConnection(toNeuron,weight);
  }
    */

  private void addOneV1Connection(int fromRow, int fromCol,CANTNeuron toNeuron,
    double weight) {
    if ((fromRow < 0) || (fromCol < 0) || (fromCol >= getInputCols()))
      return;
     								
    int inputNeuron = fromRow * getCols() + fromCol;
  
    neurons[inputNeuron].addConnection(toNeuron,weight);
  }

  private void addOneV1Connection(int featureNum, int fromRow, int fromCol, 
    CANTNeuron toNeuron, double weight) {
    if ((fromRow < 0) || (fromRow >= getInputCols()) || (fromCol < 0) || 
      (fromCol >= getInputCols()))
      return;
     								
    int inputNeuron = fromRow * getCols() + fromCol + 
      featureNum*getInputSize();
  
    neurons[inputNeuron].addConnection(toNeuron,weight);
  }

  private void connectInputTo3x3(CABot2Net retinaNet, int start,
                                   double onVal, double offVal) {
    int row;
    int column;

    for (int inputNeuron = start; inputNeuron<start+size(); inputNeuron++) 
      {
      row = (inputNeuron-start)/getRows();
      column = (inputNeuron-start)%getRows();
      addOneConnection(row,column,retinaNet.neurons[inputNeuron],onVal);
	  
      addOneConnection(row-1,column-1,retinaNet.neurons[inputNeuron],offVal);
      addOneConnection(row-1,column,retinaNet.neurons[inputNeuron],offVal);
      addOneConnection(row-1,column+1,retinaNet.neurons[inputNeuron],offVal);
      addOneConnection(row,column-1,retinaNet.neurons[inputNeuron],offVal);
      addOneConnection(row,column+1,retinaNet.neurons[inputNeuron],offVal);
      addOneConnection(row+1,column-1,retinaNet.neurons[inputNeuron],offVal);
      addOneConnection(row+1,column,retinaNet.neurons[inputNeuron],offVal);
      addOneConnection(row+1,column+1,retinaNet.neurons[inputNeuron],offVal);
      }
  }	


  private void connectInputTo6x6(CABot2Net retinaNet, int start,
                                   double onVal, double offVal) {
  int row;
  int column;

  for (int inputNeuron = start; inputNeuron<start+size(); inputNeuron++) 
    {
    row = (inputNeuron-start)/getRows();
    column = (inputNeuron-start)%getRows();
    addOneConnection(row,column,retinaNet.neurons[inputNeuron],onVal);
    addOneConnection(row,column+1,retinaNet.neurons[inputNeuron],onVal);
    addOneConnection(row+1,column,retinaNet.neurons[inputNeuron],onVal);
    addOneConnection(row+1,column+1,retinaNet.neurons[inputNeuron],onVal);
    
    addOneConnection(row,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+1,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+1,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+1,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+1,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column+1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column+1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-1,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-1,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-1,column,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-1,column+1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-1,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-1,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column+1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column+3,retinaNet.neurons[inputNeuron],offVal);
    }
  }	


  private void connectInputTo9x9(CABot2Net retinaNet, int start,
                                   double onVal, double offVal) {
  int row;
  int column;

  for (int inputNeuron = start; inputNeuron<start+size(); inputNeuron++) 
    {
    row = (inputNeuron-start)/getRows();
    column = (inputNeuron-start)%getRows();
    addOneConnection(row,column,retinaNet.neurons[inputNeuron],onVal);
    addOneConnection(row,column+1,retinaNet.neurons[inputNeuron],onVal);
    addOneConnection(row,column-1,retinaNet.neurons[inputNeuron],onVal);
    addOneConnection(row+1,column,retinaNet.neurons[inputNeuron],onVal);
    addOneConnection(row+1,column-1,retinaNet.neurons[inputNeuron],onVal);
    addOneConnection(row+1,column+1,retinaNet.neurons[inputNeuron],onVal);
    addOneConnection(row-1,column,retinaNet.neurons[inputNeuron],onVal);
    addOneConnection(row-1,column+1,retinaNet.neurons[inputNeuron],onVal);
    addOneConnection(row-1,column-1,retinaNet.neurons[inputNeuron],onVal);
    
    addOneConnection(row-4,column-4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-4,column-3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-4,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-4,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-4,column,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-4,column+1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-4,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-4,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-4,column+4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-3,column-4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-3,column-3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-3,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-3,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-3,column,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-3,column+1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-3,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-3,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-3,column+4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column-4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column-3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column+1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-2,column+4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-1,column-4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-1,column-3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-1,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row-1,column+4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row,column-4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row,column-3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row,column+4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+1,column-4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+1,column-3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+1,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+1,column+4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column-4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column-3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column+1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+2,column+4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column-4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column-3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column+1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+3,column+4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+4,column-4,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+4,column-3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+4,column-2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+4,column-1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+4,column,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+4,column+1,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+4,column+2,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+4,column+3,retinaNet.neurons[inputNeuron],offVal);
    addOneConnection(row+4,column+4,retinaNet.neurons[inputNeuron],offVal);
    }
  }	
	
  public void connectInputToRetina(CABot2Net retinaNet) {
    connectInputTo3x3(retinaNet,0,7.1,-1.25);	
    connectInputTo3x3(retinaNet,size(),-7.1,0.89);	
    connectInputTo6x6(retinaNet,size()*2,1.8,-0.22);	
    connectInputTo6x6(retinaNet,size()*3,-1.8,0.22);	
    connectInputTo9x9(retinaNet,size()*4,0.79,-0.021);	
    connectInputTo9x9(retinaNet,size()*5,-0.79,0.099);	
  }
  
  
  //input was translated to retina 3x3 node to node
  private void connectRetinaToHorizontal(CABot2Net V1Net, 
                                         double onOff33Val, double ignore,
                                         double onOff66Val, double ignore2
                                  	) 
  {
  int row;
  int column;
  for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
    {
    row = inputNeuron/getRows();
    column = inputNeuron%getRows();
    addOneRetinaConnection(row,column-1,0,V1Net.neurons[inputNeuron],
      onOff33Val);
    addOneRetinaConnection(row,column,0,V1Net.neurons[inputNeuron],onOff33Val);
    addOneRetinaConnection(row,column+1,0,V1Net.neurons[inputNeuron],
      onOff33Val);
    }
  }

  private void connectRetinaToSlash(CABot2Net V1Net, double onOff33Val) 
  {
    int row;
    int column;
    int slashOffset=getInputSize();
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getRows();
      column = inputNeuron%getRows();
      addOneRetinaConnection(row-1,column+1,0,
        V1Net.neurons[inputNeuron+slashOffset],onOff33Val);
      addOneRetinaConnection(row,column,0,
        V1Net.neurons[inputNeuron+slashOffset],onOff33Val);
      addOneRetinaConnection(row+1,column,0,
        V1Net.neurons[inputNeuron+slashOffset],onOff33Val);
      }
  }

  private void connectRetinaToBackSlash(CABot2Net V1Net, double onOff33Val) 
  {
    int row;
    int column; 
    int backOffset=getInputSize()*2;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getRows();
      column = inputNeuron%getRows();
      addOneRetinaConnection(row-1,column-1,0,
        V1Net.neurons[inputNeuron+backOffset],onOff33Val);
      addOneRetinaConnection(row,column,0,
        V1Net.neurons[inputNeuron+backOffset],onOff33Val);
      addOneRetinaConnection(row+1,column,0,
        V1Net.neurons[inputNeuron+backOffset],onOff33Val);
      }
  }

  private double threeNFBigVal = 1.4;
  private double threeNFSmallVal = 0.5;
  private double threeFNVal = 0.3;
  private double sixNFVal= 0.3;
  private double sixFNVal= 0.3;
  private double nineNFVal=0.2;
  private double nineFNVal=0.3;
  private void connectRetinaToAndAngle(CABot2Net V1Net) 
  {
    int row;
    int column; 
    int andOffset=getInputSize()*3;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getCols();
      column = inputNeuron%getCols();

      //xy,1y
      addOneRetinaConnection(row,column,0,
        V1Net.neurons[inputNeuron+andOffset], threeNFBigVal);
      addOneRetinaConnection(row+1,column,0,
        V1Net.neurons[inputNeuron+andOffset], threeNFSmallVal);

      //x-1,x1,1-1,11
      addOneRetinaConnection(row,column-1,1,
        V1Net.neurons[inputNeuron+andOffset],threeFNVal);
      addOneRetinaConnection(row,column+1,1,
        V1Net.neurons[inputNeuron+andOffset],threeFNVal);
      addOneRetinaConnection(row+1,column-1,1,
        V1Net.neurons[inputNeuron+andOffset],threeFNVal);
      addOneRetinaConnection(row+1,column+1,1,
        V1Net.neurons[inputNeuron+andOffset],threeFNVal);

      //xy,1-1,1y,11
      addOneRetinaConnection(row,column,2,
        V1Net.neurons[inputNeuron+andOffset],sixNFVal);
      addOneRetinaConnection(row+1,column-1,2,
        V1Net.neurons[inputNeuron+andOffset],sixNFVal);
      addOneRetinaConnection(row+1,column,2,
        V1Net.neurons[inputNeuron+andOffset],sixNFVal);
      addOneRetinaConnection(row+1,column+1,2,
        V1Net.neurons[inputNeuron+andOffset],sixNFVal);

      //2-2,2-1,21,22
      addOneRetinaConnection(row+2,column-2,3,
        V1Net.neurons[inputNeuron+andOffset],sixFNVal);
      addOneRetinaConnection(row+2,column-1,3,
        V1Net.neurons[inputNeuron+andOffset],sixFNVal);
      addOneRetinaConnection(row+2,column+1,3,
        V1Net.neurons[inputNeuron+andOffset],sixFNVal);
      addOneRetinaConnection(row+2,column+2,3,
        V1Net.neurons[inputNeuron+andOffset],sixFNVal);

      //1y,2y,3-1,3y,31
      addOneRetinaConnection(row+1,column,4,
        V1Net.neurons[inputNeuron+andOffset],nineNFVal);
      addOneRetinaConnection(row+2,column,4,
        V1Net.neurons[inputNeuron+andOffset],nineNFVal);
      addOneRetinaConnection(row+3,column-1,4,
        V1Net.neurons[inputNeuron+andOffset],nineNFVal);
      addOneRetinaConnection(row+3,column,4,
        V1Net.neurons[inputNeuron+andOffset],nineNFVal);
      addOneRetinaConnection(row+3,column+1,4,
        V1Net.neurons[inputNeuron+andOffset],nineNFVal);

      //x-2,x2,1-3,13
      addOneRetinaConnection(row,column-2,5,
        V1Net.neurons[inputNeuron+andOffset],nineFNVal);
      addOneRetinaConnection(row,column+2,5,
        V1Net.neurons[inputNeuron+andOffset],nineFNVal);
      addOneRetinaConnection(row+1,column-3,5,
        V1Net.neurons[inputNeuron+andOffset],nineFNVal);
      addOneRetinaConnection(row+1,column+3,5,
        V1Net.neurons[inputNeuron+andOffset],nineFNVal);
      }
  }

  private void connectRetinaToLessThanAngle(CABot2Net V1Net) 
  {
    int row;
    int column; 
    int lessThanOffset=getInputSize()*4;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getCols();
      column = inputNeuron%getCols();
      //xy,x1
      addOneRetinaConnection(row,column,0,
        V1Net.neurons[inputNeuron+lessThanOffset],threeNFBigVal);
      addOneRetinaConnection(row,column+1,0,
        V1Net.neurons[inputNeuron+lessThanOffset],threeNFSmallVal);

      //-1y,-11,1y,11
      addOneRetinaConnection(row-1,column,1,
        V1Net.neurons[inputNeuron+lessThanOffset], threeFNVal);
      addOneRetinaConnection(row-1,column+1,1,
        V1Net.neurons[inputNeuron+lessThanOffset], threeFNVal);
      addOneRetinaConnection(row+1,column,1,
        V1Net.neurons[inputNeuron+lessThanOffset], threeFNVal);
      addOneRetinaConnection(row+1,column+1,1,
        V1Net.neurons[inputNeuron+lessThanOffset], threeFNVal);

      //xy, -11,x1,11
      addOneRetinaConnection(row,column,2,
        V1Net.neurons[inputNeuron+lessThanOffset], sixNFVal);
      addOneRetinaConnection(row-1,column+1,2,
        V1Net.neurons[inputNeuron+lessThanOffset], sixNFVal);
      addOneRetinaConnection(row,column+1,2,
        V1Net.neurons[inputNeuron+lessThanOffset], sixNFVal);
      addOneRetinaConnection(row+1,column+1,2,
        V1Net.neurons[inputNeuron+lessThanOffset], sixNFVal);

      //-22,-12,12,22
      addOneRetinaConnection(row-2,column+2,3,
        V1Net.neurons[inputNeuron+lessThanOffset],sixFNVal);
      addOneRetinaConnection(row-1,column+2,3,
        V1Net.neurons[inputNeuron+lessThanOffset],sixFNVal);
      addOneRetinaConnection(row+1,column+2,3,
        V1Net.neurons[inputNeuron+lessThanOffset],sixFNVal);
      addOneRetinaConnection(row+2,column+2,3,
        V1Net.neurons[inputNeuron+lessThanOffset],sixFNVal);

      //-13,x1,x2,x3,13
      addOneRetinaConnection(row-1,column+3,4,
      V1Net.neurons[inputNeuron+lessThanOffset],nineNFVal);
      addOneRetinaConnection(row,column+1,4,
        V1Net.neurons[inputNeuron+lessThanOffset],nineNFVal);
      addOneRetinaConnection(row,column+2,4,
        V1Net.neurons[inputNeuron+lessThanOffset],nineNFVal);
      addOneRetinaConnection(row,column+3,4,
        V1Net.neurons[inputNeuron+lessThanOffset],nineNFVal);
      addOneRetinaConnection(row+1,column+3,4,
        V1Net.neurons[inputNeuron+lessThanOffset],nineNFVal);

      //-31,-2y,2y,31
      addOneRetinaConnection(row-3,column+1,5,
        V1Net.neurons[inputNeuron+lessThanOffset],nineFNVal);
      addOneRetinaConnection(row-2,column,5,
        V1Net.neurons[inputNeuron+lessThanOffset],nineFNVal);
      addOneRetinaConnection(row+2,column,5,
        V1Net.neurons[inputNeuron+lessThanOffset],nineFNVal);
      addOneRetinaConnection(row+3,column+1,5,
        V1Net.neurons[inputNeuron+lessThanOffset],nineFNVal);
      }
  }

  private void connectRetinaToGreaterThanAngle(CABot2Net V1Net) 
  {
    int row;
    int column; 
    int greaterThanOffset=getInputSize()*5;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getCols();
      column = inputNeuron%getCols();
      //x-1,xy
      addOneRetinaConnection(row,column-1,0,
        V1Net.neurons[inputNeuron+greaterThanOffset],threeNFSmallVal);
      addOneRetinaConnection(row,column,0,
        V1Net.neurons[inputNeuron+greaterThanOffset],threeNFBigVal);

      //-1-1,-1y,1-1,1y
      addOneRetinaConnection(row-1,column-1,1,
        V1Net.neurons[inputNeuron+greaterThanOffset], threeFNVal);
      addOneRetinaConnection(row-1,column,1,
        V1Net.neurons[inputNeuron+greaterThanOffset], threeFNVal);
      addOneRetinaConnection(row+1,column,1,
        V1Net.neurons[inputNeuron+greaterThanOffset], threeFNVal);
      addOneRetinaConnection(row+1,column-1,1,
        V1Net.neurons[inputNeuron+greaterThanOffset], threeFNVal);

      //-1-1,x-1,xy,1-1,
      addOneRetinaConnection(row-1,column-1,2,
        V1Net.neurons[inputNeuron+greaterThanOffset], sixNFVal);
      addOneRetinaConnection(row,column-1,2,
        V1Net.neurons[inputNeuron+greaterThanOffset], sixNFVal);
      addOneRetinaConnection(row,column,2,
        V1Net.neurons[inputNeuron+greaterThanOffset], sixNFVal);
      addOneRetinaConnection(row+1,column-1,2,
        V1Net.neurons[inputNeuron+greaterThanOffset], sixNFVal);

      //-2-2,-1-2,1-2,2-2
      addOneRetinaConnection(row-2,column-2,3,
        V1Net.neurons[inputNeuron+greaterThanOffset],sixFNVal);
      addOneRetinaConnection(row-1,column-2,3,
        V1Net.neurons[inputNeuron+greaterThanOffset],sixFNVal);
      addOneRetinaConnection(row+1,column-2,3,
        V1Net.neurons[inputNeuron+greaterThanOffset],sixFNVal);
      addOneRetinaConnection(row+2,column-2,3,
        V1Net.neurons[inputNeuron+greaterThanOffset],sixFNVal);

      //x-1,x-2,x-3,1-3,13
      addOneRetinaConnection(row,column-1,4,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineNFVal);
      addOneRetinaConnection(row,column-2,4,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineNFVal);
      addOneRetinaConnection(row,column-3,4,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineNFVal);
      addOneRetinaConnection(row+1,column-3,4,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineNFVal);
      addOneRetinaConnection(row-1,column-3,4,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineNFVal);
      

      //-31,-2y,2y, 31
      addOneRetinaConnection(row-3,column+1,5,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineFNVal);
      addOneRetinaConnection(row-2,column,5,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineFNVal);
      addOneRetinaConnection(row+2,column,5,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineFNVal);
      addOneRetinaConnection(row+3,column+1,5,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineFNVal);
     }
  }

  private void connectRetinaToOrAngle(CABot2Net V1Net) 
  {
    int row;
    int column; 
    int orOffset=getInputSize()*6;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getCols();
      column = inputNeuron%getCols();

      //-1y,xy
      addOneRetinaConnection(row-1,column,0,
        V1Net.neurons[inputNeuron+orOffset],threeNFSmallVal);
      addOneRetinaConnection(row,column,0,
        V1Net.neurons[inputNeuron+orOffset],threeNFBigVal);

      //-1-1,-11,x-1,x1
      addOneRetinaConnection(row-1,column-1,1,
        V1Net.neurons[inputNeuron+orOffset], threeFNVal);
      addOneRetinaConnection(row-1,column+1,1,
        V1Net.neurons[inputNeuron+orOffset], threeFNVal);
      addOneRetinaConnection(row,column-1,1,
        V1Net.neurons[inputNeuron+orOffset], threeFNVal);
      addOneRetinaConnection(row,column+1,1,
        V1Net.neurons[inputNeuron+orOffset], threeFNVal);

      //-1-1,-1y,-11,xy
      addOneRetinaConnection(row-1,column-1,2,
        V1Net.neurons[inputNeuron+orOffset], sixNFVal);
      addOneRetinaConnection(row-1,column,2,
        V1Net.neurons[inputNeuron+orOffset], sixNFVal);
      addOneRetinaConnection(row-1,column+1,2,
        V1Net.neurons[inputNeuron+orOffset], sixNFVal);
      addOneRetinaConnection(row,column,2,
        V1Net.neurons[inputNeuron+orOffset], sixNFVal);

      //-2-2,-2-1,-21,-22
      addOneRetinaConnection(row-2,column-2,3,
        V1Net.neurons[inputNeuron+orOffset],sixFNVal);
      addOneRetinaConnection(row-2,column-1,3,
        V1Net.neurons[inputNeuron+orOffset],sixFNVal);
      addOneRetinaConnection(row-2,column+1,3,
        V1Net.neurons[inputNeuron+orOffset],sixFNVal);
      addOneRetinaConnection(row-2,column+2,3,
        V1Net.neurons[inputNeuron+orOffset],sixFNVal);

      //-3-1,-3y,-31,-2y,-1y
      addOneRetinaConnection(row-3,column-1,4,
        V1Net.neurons[inputNeuron+orOffset],nineNFVal);
      addOneRetinaConnection(row-3,column,4,
        V1Net.neurons[inputNeuron+orOffset],nineNFVal);
      addOneRetinaConnection(row-3,column+1,4,
        V1Net.neurons[inputNeuron+orOffset],nineNFVal);
      addOneRetinaConnection(row-2,column,4,
        V1Net.neurons[inputNeuron+orOffset],nineNFVal);
      addOneRetinaConnection(row-1,column,4,
        V1Net.neurons[inputNeuron+orOffset],nineNFVal);

      //-1-3,-13,x-2,x2
      addOneRetinaConnection(row-1,column-3,5,
        V1Net.neurons[inputNeuron+orOffset],nineFNVal);
      addOneRetinaConnection(row-1,column+3,5,
        V1Net.neurons[inputNeuron+orOffset],nineFNVal);
      addOneRetinaConnection(row,column-2,5,
        V1Net.neurons[inputNeuron+orOffset],nineFNVal);
      addOneRetinaConnection(row,column+2,5,
        V1Net.neurons[inputNeuron+orOffset],nineFNVal);
      }
  }

  private void connectRetinaToHEdge (CABot2Net V1Net) 
  {
    int row;
    int column;
  
    int hEdgeOffset=getInputSize()*7;
    double onVal = 0.7;
    double offVal = 0.5;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getInputCols();
      column = inputNeuron%getInputCols();

      //add 5 connections per 66NF neuron and 10 per 66FN
      for (int col = -2 ; col < 3; col++)
        {
        //the onoffs are largely corect though a bottom edge is shifted up 1.
        addOneRetinaConnection(row,column,2,
           V1Net.neurons[hEdgeOffset+inputNeuron+col],onVal);

        //the offons are off by two both above and below
        addOneRetinaConnection(row-2,column,3,
           V1Net.neurons[hEdgeOffset+inputNeuron+col],offVal);
        addOneRetinaConnection(row+2,column,3,
           V1Net.neurons[hEdgeOffset+inputNeuron+col],offVal);
        }
      }    
    }

  private void connectRetinaToSEdge (CABot2Net V1Net) 
  {
    int row;
    int column;
  
    int sEdgeOffset=getInputSize()*8;
    double threeOffOnVal = 0.7;
    double sixOnVal = 0.7;
    double sixOffVal = 0.5;

    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getInputCols();
      column = inputNeuron%getInputCols();
      int toNeuron=sEdgeOffset+inputNeuron;

      addOneRetinaConnection(row,column-1,1,
        V1Net.neurons[toNeuron],threeOffOnVal);
      addOneRetinaConnection(row-1,column,1,
        V1Net.neurons[toNeuron],threeOffOnVal);

      //add 5 connections per 66NF neuron and 10 per 66FN
      for (int offset = -2 ; offset < 3; offset++)
        {
        //the onoffs are largely corect though a bottom edge is shifted up 1.
        addOneRetinaConnection(row+offset,column-offset,2,
           V1Net.neurons[toNeuron],sixOnVal);

        //the offons are off by two both above and below
        addOneRetinaConnection(row-2+offset,column-offset,3,
           V1Net.neurons[toNeuron],sixOffVal);
        addOneRetinaConnection(row+2+offset,column-offset,3,
           V1Net.neurons[toNeuron],sixOffVal);
        }
      }    
  }

  private void connectRetinaToBEdge (CABot2Net V1Net) 
  {
    int row;
    int column;
  
    int bEdgeOffset=getInputSize()*9;
    double threeOffOnVal = 0.7;
    double sixOnVal = 0.7;
    double sixOffVal = 0.5;

    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getInputCols();
      column = inputNeuron%getInputCols();
      int toNeuron=bEdgeOffset+inputNeuron;

      addOneRetinaConnection(row,column+1,1,
        V1Net.neurons[toNeuron],threeOffOnVal);
      addOneRetinaConnection(row-1,column,1,
        V1Net.neurons[toNeuron],threeOffOnVal);

      //add 5 connections per 66NF neuron and 10 per 66FN
      for (int offset = -2 ; offset < 3; offset++)
        {
        //the onoffs are largely corect though a bottom edge is shifted up 1.
        addOneRetinaConnection(row+offset,column+offset,2,
           V1Net.neurons[toNeuron],sixOnVal);

        //the offons are off by two both above and below
        addOneRetinaConnection(row-2+offset,column+offset,3,
           V1Net.neurons[toNeuron],sixOffVal);
        addOneRetinaConnection(row+2+offset,column+offset,3,
           V1Net.neurons[toNeuron],sixOffVal);
        }
      }    
  }

  public void connectRetinaToV1(CABot2Net V1Net) {
    connectRetinaToHorizontal(V1Net,1.4,-0.2,0.5,-0.2);	
    connectRetinaToSlash(V1Net,1.4);	
    connectRetinaToBackSlash(V1Net,1.4);	
    connectRetinaToAndAngle(V1Net);	
    connectRetinaToLessThanAngle(V1Net);	
    connectRetinaToGreaterThanAngle(V1Net);	
    connectRetinaToOrAngle(V1Net);	
    connectRetinaToHEdge(V1Net); 	
    connectRetinaToSEdge(V1Net);	
    connectRetinaToBEdge(V1Net);	
  }

  //Connect a box of V1 type  neurons to a shape
  private void connectV1FeatureToV2Shape(int colGroup, int rowGroup, 
    int featureNum, int boxTop, int boxBot, int boxLeft, int boxRight, 
    int toShape, double weight,CABot2Net V2Net )  {
    
    int toRow = rowGroup*10+5;
    int toCol = colGroup*10+6;
    int toNeuron=toRow*getInputCols() + toCol;
    toNeuron += toShape*getInputSize();

    for (int fromRow=toRow+boxTop; fromRow<toRow+boxBot; fromRow++) {
      for (int fromCol=toCol+boxLeft; fromCol <toCol+boxRight;fromCol++){
        if (toShape < 4) {
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron],weight);
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron+1],weight*.95);
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron+2],weight*.9);
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron+50],weight*.92);
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron+51],weight*.87);
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron+52],weight*0.85);
	}
        else {
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron],weight);
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron+1],weight*.95);
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron+2],weight*1.1);
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron+50],weight);
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron+51],weight*.98);
          addOneV1Connection(featureNum,fromRow,fromCol,
            V2Net.neurons[toNeuron+52],weight*1.05);
	}
      }
    }
  }

    private double uniqueAngleVal = 0.8;  //0.7
  private double sharedAngleVal = 0.3;
    private double uniqueEdgeVal = 0.5; //0.4
  private double sharedEdgeVal = 0.15;


  public void connectV1ToSmallPyramid(CABot2Net V2Net) {
    for (int toColBox = 0; toColBox< getInputCols()/10; toColBox++ ){
      for (int toRowBox = 0; toRowBox< getInputRows()/10; toRowBox++ ){
        int toNeuron=toRowBox*10+5;
        toNeuron*=getInputCols();
        toNeuron+= toColBox*10+5;
        
        //and angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,3,-9,0,-9,9,0,
          uniqueAngleVal,V2Net); 

        //lessthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,4,0,9,-9,0,0,
          sharedAngleVal, V2Net); 

        //greaterthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,5,0,9,0,9,0,
          sharedAngleVal,V2Net); 

        //hedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,7,0,9,-9,9,0,
          sharedEdgeVal,V2Net); 

        //sedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,8,-9,9,-9,0,0,
          uniqueEdgeVal,V2Net); 

        //bedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,9,-9,9,0,9,0,
          uniqueEdgeVal,V2Net); 
      }
    }
  }

  public void connectV1ToSmallStalagtite(CABot2Net V2Net) {
    for (int toColBox = 0; toColBox< getInputCols()/10; toColBox++ ){
      for (int toRowBox = 0; toRowBox< getInputRows()/10; toRowBox++ ){
        int toNeuron=toRowBox*10+5;
        toNeuron*=getInputCols();
        toNeuron+= toColBox*10+5;
        
        //lessthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,4,-9,0,-9,0,1,
          sharedAngleVal,V2Net); 

        //greaterthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,5,-9,0,0,9,1,
          sharedAngleVal,V2Net); 

        //or angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,6,0,9,-9,9,1,
          uniqueAngleVal,V2Net); 

        //hedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,7,-9,0,-9,9,1,
          sharedEdgeVal,V2Net); 

        //sedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,8,-9,9,0,9,1,
          uniqueEdgeVal,V2Net); 
          
        //bedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,9,-9,9,-9,0,1,
          uniqueEdgeVal,V2Net); 
      }
    }
  }

  private int mediumSize= 8;
  public void connectV1ToMediumPyramid(CABot2Net V2Net) {
    for (int toColBox = 0; toColBox< getInputCols()/10; toColBox++ ){
      for (int toRowBox = 0; toRowBox< getInputRows()/10; toRowBox++ ){
        int toNeuron=toRowBox*10+5;
        toNeuron*=getInputCols();
        toNeuron+= toColBox*10+5;
        
        //and angle 
        connectV1FeatureToV2Shape(toColBox,toRowBox,3,-9,0,-9,9,2,
          uniqueAngleVal,V2Net); 

        //lessthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,4,mediumSize+1,15,-15,0,2,
          sharedAngleVal, V2Net); 
        connectV1FeatureToV2Shape(toColBox,toRowBox,4,0,mediumSize,-15,
          -1*(mediumSize+1),2, sharedAngleVal, V2Net); 

        //greaterthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,5,mediumSize+1,15,0,15,2,
          sharedAngleVal,V2Net); 
        connectV1FeatureToV2Shape(toColBox,toRowBox,5,0,mediumSize,mediumSize,
          15,2, sharedAngleVal, V2Net); 

        //hedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,7,mediumSize,15,-15,15,2,
          sharedEdgeVal,V2Net); 


        //sedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,8,-9,15,-15,mediumSize,2,
          uniqueEdgeVal,V2Net); 

        //bedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,9,-9,15,mediumSize,15,2,
          uniqueEdgeVal,V2Net); 
      }
    }
  }

  public void connectV1ToMediumStalagtite(CABot2Net V2Net) {
    for (int toColBox = 0; toColBox< getInputCols()/10; toColBox++ ){
      for (int toRowBox = 0; toRowBox< getInputRows()/10; toRowBox++ ){
        int toNeuron=toRowBox*10+5;
        toNeuron*=getInputCols();
        toNeuron+= toColBox*10+5;
        
        //lessthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,4,-15,-1*mediumSize,
          -15,0,3, sharedAngleVal,V2Net); 
        connectV1FeatureToV2Shape(toColBox,toRowBox,4,-1*(mediumSize-1),0,
          -15,mediumSize,3, sharedAngleVal,V2Net); 

        //greaterthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,5,-15,-1*mediumSize,
          0,15,3, sharedAngleVal,V2Net); 
        connectV1FeatureToV2Shape(toColBox,toRowBox,5,-1*(mediumSize-1),0,
          mediumSize,15,3,sharedAngleVal,V2Net); 

        //or angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,6,0,9,-9,9,3,
          uniqueAngleVal,V2Net); 

        //hedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,7,-15,-1*(mediumSize-1),
          -15,15,3,sharedEdgeVal,V2Net); 

        //sedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,8,-15,9,0,15,3,
          uniqueEdgeVal,V2Net); 
          
        //bedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,9,-15,9,-15,0,3,
          uniqueEdgeVal,V2Net); 
      }
    }
  }

  private int largeSize1= 10;
  private int largeSize2= 20;
  public void connectV1ToLargePyramid(CABot2Net V2Net) {
    for (int toColBox = 0; toColBox< getInputCols()/10; toColBox++ ){
      for (int toRowBox = 0; toRowBox< getInputRows()/10; toRowBox++ ){
        int toNeuron=toRowBox*10+5;
        toNeuron*=getInputCols();
        toNeuron+= toColBox*10+5;
        
        //and angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,3,-9,0,-9,9,4,
          uniqueAngleVal/2,V2Net); 

        //lessthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,4,largeSize1,largeSize2,
          -1*largeSize2,0,4, sharedAngleVal/2, V2Net); 

        //greaterthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,5,largeSize1,largeSize2,
          0,largeSize2,4,  sharedAngleVal/2,V2Net); 

        //hedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,7,largeSize1,largeSize2,
          -1*largeSize2,largeSize2,4, sharedEdgeVal/2,V2Net); 


        //sedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,8,-9,largeSize2,
          -1*largeSize2,0,4, uniqueEdgeVal/2,V2Net); 

        //bedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,9,-9,largeSize2,
          0,largeSize2,4, uniqueEdgeVal/2,V2Net); 
      }
    }
  }

  public void connectV1ToLargeStalagtite(CABot2Net V2Net) {
    for (int toColBox = 0; toColBox< getInputCols()/10; toColBox++ ){
      for (int toRowBox = 0; toRowBox< getInputRows()/10; toRowBox++ ){
        int toNeuron=toRowBox*10+5;
        toNeuron*=getInputCols();
        toNeuron+= toColBox*10+5;
        
        //or angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,6,0,9,-9,9,5,
          uniqueAngleVal/2,V2Net); 

        //lessthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,4,-1*largeSize2,
          -1*largeSize1, -1*largeSize2,0,5, sharedAngleVal/2, V2Net); 

        //greaterthan angle
        connectV1FeatureToV2Shape(toColBox,toRowBox,5,-1*largeSize2,
          -1*largeSize1, 0,largeSize2,5, sharedAngleVal/2,V2Net); 

        //hedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,7,-1*largeSize2,
          -1*largeSize1, -1*largeSize2,largeSize2,5, sharedEdgeVal/2,V2Net); 

        //sedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,8,-1*largeSize2,
          -1*largeSize1, 0,largeSize2,5, uniqueEdgeVal/2,V2Net); 

        //bedge
        connectV1FeatureToV2Shape(toColBox,toRowBox,9,-1*largeSize2,
          -1*largeSize1, -1*largeSize2,0,5, uniqueEdgeVal/2,V2Net); 
      }
    }
  }

  public void connectV1ToV2(CABot2Net V2Net) {
    connectV1ToSmallPyramid(V2Net);	
    connectV1ToSmallStalagtite(V2Net);	
    connectV1ToMediumPyramid(V2Net);	
    connectV1ToMediumStalagtite(V2Net);	
    connectV1ToLargePyramid(V2Net);	
    connectV1ToLargeStalagtite(V2Net);	
  }
  
  //****************intranet connections******************
  private boolean isEvidenceNeuron(int row, int col) {
    if (((row == 5) || (row == 6)) && 
        ((col == 6) || (col == 7) || (col == 8)))
      return true;
    return false;
  }

  private void setConnectionsFromEvidenceNeurons(int fromNeuron,int offset) {
    for (int synapse = 0; synapse < 20; synapse ++) {
      int row = ((int)(CANT23.random.nextFloat()*5) + 5);
      int col = ((int)(CANT23.random.nextFloat()*10));
      if (!isEvidenceNeuron(row,col)) {
        int toNeuron = (row*50)+col+offset;
        addConnection(fromNeuron,toNeuron,2.8); //2.6
      }
    }
  }

  private void setConnectionsWithinCA(int fromNeuron, int offset) {
    for (int synapse = 0; synapse < 14; synapse ++) {
      int row = ((int)(CANT23.random.nextFloat()*5) + 5);
      int col = ((int)(CANT23.random.nextFloat()*10));
      if (!isEvidenceNeuron(row,col)) {
        int toNeuron = (row*50)+col+offset;
        addConnection(fromNeuron,toNeuron,1.0);
      }
    }
  }
  private void buildBottomShapeSizeCA(int shape, int location) {
    int shapeOffset = 2500*shape;
    int rowLarge = location%5;
    int colLarge = location/5;
    int locationOffset = (rowLarge*500)+(colLarge*10);
    for (int row = 5; row< 10; row ++) {
      for (int col = 0; col< 10; col ++) {
        int fromNeuron = (row*50)+col+locationOffset+shapeOffset;
        if ((fromNeuron%5)==0)
          neurons[fromNeuron].setInhibitory(true);
	else {
          neurons[fromNeuron].setInhibitory(false);
          if (isEvidenceNeuron(row,col))
	    setConnectionsFromEvidenceNeurons(fromNeuron,
              shapeOffset+locationOffset);
          else
            setConnectionsWithinCA(fromNeuron,shapeOffset+locationOffset);
        }
      }
    }
  }
    
  private void buildTopShapeSizeCA(int shape, int location) {
    int shapeOffset = 2500*shape;
    int rowLarge = location%5;
    int colLarge = location/5;
    int locationOffset = (rowLarge*500)+(colLarge*10);
    for (int row = 0; row< 5; row ++) {
      for (int col = 0; col< 10; col ++) {
        int fromNeuron = (row*50)+col+locationOffset+shapeOffset;
        if ((fromNeuron%5)==0)
          neurons[fromNeuron].setInhibitory(true);
	else {
          neurons[fromNeuron].setInhibitory(false);
          for (int synapse = 0; synapse < 50; synapse ++) {
	    int toNeuron = synapse +(row*10)+col;
            toNeuron %=50;
            int toRow = toNeuron/10;
            int toCol = toNeuron%10;
            toNeuron = (toRow*50)+toCol+locationOffset+shapeOffset;
            addConnection(fromNeuron,toNeuron,0.5);
	  }
	}
      }
    }
  }
 
  private void connectBottomToTopShapeSizeCA(int shape, int location) {
    int shapeOffset = 2500*shape;
    int rowLarge = location%5;
    int colLarge = location/5;
    int locationOffset = (rowLarge*500)+(colLarge*10);
    for (int row = 5; row< 10; row ++) {
      for (int col = 0; col< 10; col ++) {
        int fromNeuron = (row*50)+col+locationOffset+shapeOffset;
        if ((!neurons[fromNeuron].isInhibitory()) && 
          (!isEvidenceNeuron(row,col))) {
          for (int synapse = 0; synapse < 5; synapse ++) {
            int toCol = (col/5)*5;
            int toNeuron = ((row-5)*50)+toCol+synapse+locationOffset+
              shapeOffset;
            //max 4 connections D=1.1 so w*4*10 < 5 (theta) w< 5/40=1/8 
            //assume less firing so .124 -> .2
            addConnection(fromNeuron,toNeuron,0.2); 
          }
	}
      }
    }
  }

  //Create the CA for Visual categories shape and size specific.  
  //The top 50 are for require activation from outside vision
  //and the active bottom CA to activate.
  //Every 5th neuron is inhibitory.
  //On the bottom 50, 6 neurons are fired only from V1. These
  //provide activation that causes the remaining 44 neurons to
  //ignite.
  private void createShapeSizeCA(int shape, int location) {
    buildBottomShapeSizeCA(shape,location);
    buildTopShapeSizeCA(shape,location);
    connectBottomToTopShapeSizeCA(shape,location);
  }

  private void inhibOtherVisionCA(int fromLRow,int fromLCol, int fromShape,
                                  int toLRow, int toLCol, int toShape) {
    if ((toLRow < 0) || (toLRow ==5) || (toLCol < 0) || (toLCol ==5))
      return;
    int fromOffset = (2500*fromShape)+(fromLRow*500)+(fromLCol*10);
    int toOffset = (2500*toShape)+(toLRow*500)+(toLCol*10);
    for (int row = 0; row < 10; row ++) {
      for (int col = 0; col < 10; col += 5) {
        int fromNeuron = (row*50)+col+fromOffset;
        for (int synapse=0; synapse<5;synapse++) {
          int toNeuron = synapse + (row*50)+col+toOffset;
            addConnection(fromNeuron,toNeuron,2.1); 
	}
      }
    }
  }
  //set up the appropriate CAs for 5x5 locations for each of 5 shapes.
  private void setV2Topology() {
    for (int shape = 0; shape < 6; shape ++) {
      for (int location = 0; location < 25; location ++) {
        createShapeSizeCA(shape,location);
      }
    }
    for (int fromShape = 0; fromShape < 6; fromShape ++) {
      for (int row = 0; row < 5; row ++) {
        for (int col = 0; col < 5; col ++) {
          //shapeSpaceCAInhibitsSameShape
          inhibOtherVisionCA(row,col,fromShape,row-1,col-1,fromShape);
          inhibOtherVisionCA(row,col,fromShape,row-1,col,fromShape);
          inhibOtherVisionCA(row,col,fromShape,row-1,col+1,fromShape);
          inhibOtherVisionCA(row,col,fromShape,row,col-1,fromShape);
          inhibOtherVisionCA(row,col,fromShape,row,col+1,fromShape);
          inhibOtherVisionCA(row,col,fromShape,row+1,col-1,fromShape);
          inhibOtherVisionCA(row,col,fromShape,row+1,col,fromShape);
          inhibOtherVisionCA(row,col,fromShape,row+1,col+1,fromShape);
          for (int toShape = 0; toShape < 6; toShape ++) {
           //shapeSpaceCAInhibitsOtherShape
            if (toShape != fromShape) {
              inhibOtherVisionCA(row,col,fromShape,row-1,col-1,toShape);
              inhibOtherVisionCA(row,col,fromShape,row-1,col,toShape);
              inhibOtherVisionCA(row,col,fromShape,row-1,col+1,toShape);
              inhibOtherVisionCA(row,col,fromShape,row,col-1,toShape);
              inhibOtherVisionCA(row,col,fromShape,row,col,toShape);
              inhibOtherVisionCA(row,col,fromShape,row,col+1,toShape);
              inhibOtherVisionCA(row,col,fromShape,row+1,col-1,toShape);
              inhibOtherVisionCA(row,col,fromShape,row+1,col,toShape);
              inhibOtherVisionCA(row,col,fromShape,row+1,col+1,toShape);
	    }
	  }
	}
      }
    }
  }


  /*******Control and Spreading Activation Connectivity Functions*/
  private int controlCASize=40;
  private void oneControlCAPrimesOneControl(int onCA, int offCA) {
    for (int neuron = 0; neuron<40;neuron ++) {
      int fromNeuron = neuron+(onCA*controlCASize);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuron+(offCA*controlCASize);
        addConnection(fromNeuron,toNeuron,2.0);
        if ((fromNeuron %5) ==1)
          addConnection(fromNeuron,toNeuron-1,2.0);
      }
    }
  }
  private void oneControlCAStopsOneControl(int onCA, int offCA) {
    for (int neuron = 0; neuron<8;neuron ++) {
      int fromNeuron = (neuron*5)+(onCA*controlCASize);
      for (int synapse = 0; synapse < 10; synapse ++) {
        if (((neuron*5) + synapse) < 40) {
          int toNeuron = (neuron*5)+synapse+(offCA*controlCASize);
          addConnection(fromNeuron,toNeuron,2.0);
	}
      }
    }
  }
  private void oneControlCAStartsOneControl(int onCA,int offCA,double weight) {
    for (int neuron = 0; neuron<40;neuron ++) {
      int fromNeuron = neuron+(onCA*controlCASize);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuron+(offCA*controlCASize);
        addConnection(fromNeuron,toNeuron,weight);
        if ((fromNeuron %5) ==1)
          addConnection(fromNeuron,toNeuron-1,weight);
      }
    }
  }
  private void setControlTopology() {
    setFiftyFiftyTopology2(1.1);
    oneControlCAPrimesOneControl(0,1);
    oneControlCAStopsOneControl(1,0);
    oneControlCAStartsOneControl(1,2,3.1);
    oneControlCAStopsOneControl(2,1);
    oneControlCAPrimesOneControl(2,3);
    oneControlCAStopsOneControl(3,2);
    oneControlCAStartsOneControl(3,4,3.1);
    oneControlCAStopsOneControl(4,3);
    oneControlCAStartsOneControl(4,0,3.1);
    oneControlCAStopsOneControl(0,4);
  }

  private void setActionTopology() {
    setFiftyFiftyTopology2(1.1);
  }

  private int neuronsInFact = 40;
  private void setInhibFactNetCA(int fromCA,int toCA,double weight) {
    for (int neuron = 0; neuron < neuronsInFact; neuron ++) 
      {
      int fromNeuron=(fromCA*neuronsInFact)+neuron;
      if (neurons[fromNeuron].isInhibitory()) 
        {
        for (int synapse=0; synapse < 40; synapse++) 
          {
          int toNeuron = (toCA*neuronsInFact)+
            (int)(CANT23.random.nextFloat()*100);
          addConnection(fromNeuron,toNeuron,weight);
          }
        }
      }
  }

  //for goal and fact
  private void setPartialFiftyFiftyTopology(int start, int end) {
    int featureSize=10;
    for (int simpleCA = 4*start; simpleCA < 4*end;simpleCA++) {
      for (int neuron = 0; neuron < featureSize; neuron++) {
        int fromNeuron = (simpleCA*featureSize) + neuron;
        if ((neuron%5) == 0){
          //no inhibitory connections.
          neurons[fromNeuron].setInhibitory(true);
	}        
        else {
          neurons[fromNeuron].setInhibitory(false);
          setFiftyFiftySubCA(1.1,fromNeuron,simpleCA*featureSize);
	}
      }
    }
  }

  //This sets up a fifty fifty goal but the left needs some support
  //to continue running
    private void createSupportedFeature(int featureNum, int neuronsInFeature) {
      for (int neuron = 0; neuron < neuronsInFeature;neuron++) {
	int fromNeuron = (featureNum*neuronsInFeature) + neuron;
      if ((neuron%5) == 0) neurons[fromNeuron].setInhibitory(true);
      else {
        neurons[fromNeuron].setInhibitory(false);
        for (int synapse = 0; synapse < 5; synapse ++) {
          int toNeuron = fromNeuron - (fromNeuron%5) + 5 + synapse;
          double weight = 1.1;
          if ((neuron %10) > 5) {
            toNeuron -= 10;
            weight = 0.95;
	  }
          addConnection(fromNeuron,toNeuron, weight);
	}
      }
    }
  }

  private void connectOneGoalToOneGoal(int fromGoalNum, int toGoalNum,
    double weight) {
    for (int neuronNum=0; neuronNum<neuronsInGoal;neuronNum++) 
      {
      int fromNeuron = neuronNum+(fromGoalNum*neuronsInGoal);
      if (!neurons[fromNeuron].isInhibitory())
        {
        int toNeuron = neuronNum+(toGoalNum*neuronsInGoal);
        addConnection(fromNeuron,toNeuron,weight);
        if ((fromNeuron%5) == 1)
          addConnection(fromNeuron,toNeuron-1,weight);
        }
      }
  }
  private void setGoal1Topology() {
    //set up the normal goals as fiftyfifty2
    setPartialFiftyFiftyTopology(0,8); 
    createSupportedFeature(8,40);  //pyramid is partially supported
    createSupportedFeature(9,40);  //stal is partially supported
    setPartialFiftyFiftyTopology(10,11); //centerStal
    connectOneGoalToOneGoal(6,8,1.0);  //turn to pyramid
    connectOneGoalToOneGoal(6,9,1.0);  //turn to stal
    connectOneGoalToOneGoal(7,8,1.0);  //go to pyramid
    connectOneGoalToOneGoal(7,9,1.0);  //go to stal
    connectOneGoalToOneGoal(10,8,1.0);  //center to pyramid
    connectOneGoalToOneGoal(10,9,1.0);  //center to stal
  }
  
  private void connectOneFactToOneFact(int fromFactNum, int toFactNum,
    double weight) {
    for (int neuronNum=0; neuronNum<neuronsInFact;neuronNum++) 
      {
      int fromNeuron = neuronNum+(fromFactNum*neuronsInFact);
      if (!neurons[fromNeuron].isInhibitory())
        {
        int toNeuron = neuronNum+(toFactNum*neuronsInFact);
        addConnection(fromNeuron,toNeuron,weight);
        if ((fromNeuron%5) == 1)
          addConnection(fromNeuron,toNeuron-1,weight);
        }
      }
  }
  private void oneFactStopsOneFact(int fromFact, int toFact,double weight){
    for (int neuronNum=0; neuronNum<neuronsInFact;neuronNum+=5) 
      {
      int fromNeuron = neuronNum+(fromFact*neuronsInFact);
      //assert(neurons[fromNeuron].isInhibitory())
      for (int synapse=0; synapse < 10;synapse++) 
        {
        int toNeuron;
        if ((neuronNum%10) == 5)
          toNeuron = neuronNum-5+synapse;
        else toNeuron = neuronNum+synapse;
        toNeuron+=toFact*neuronsInFact;
        addConnection(fromNeuron,toNeuron,weight);
        }
      }
  }
  private void setFactTopology() {
    setPartialFiftyFiftyTopology(0,4); //ml1, ml2,mr1 and mr2
    setPartialFiftyFiftyTopology(6,9); //left, right, center,
    setPartialFiftyFiftyTopology(10,14); //no target, (center)left,right,center
    createSupportedFeature(4,40);  //pyramid is partially supported
    createSupportedFeature(5,40);  //stal is partially supported
    createSupportedFeature(9,40);  //big is partially supported rest by v2

    connectOneFactToOneFact(0,1,2.25); //ml1 ml2
    connectOneFactToOneFact(2,3,2.25); //mr1 mr2

    connectOneFactToOneFact(4,10,2.22223); //pyramid no obj
    connectOneFactToOneFact(5,10,2.22223); //stal no obj

    oneFactStopsOneFact(6,10,2.5); //left stops no obj
    oneFactStopsOneFact(7,10,2.5);
    oneFactStopsOneFact(8,10,2.5);
    oneFactStopsOneFact(11,10,2.5);//left2 stops no obj
    oneFactStopsOneFact(12,10,2.5);
    oneFactStopsOneFact(13,10,2.5);

    oneFactStopsOneFact(6,11,2.5);//left stops left2
    oneFactStopsOneFact(7,12,2.5);
    oneFactStopsOneFact(8,13,2.5);
    oneFactStopsOneFact(11,6,2.5);//left2 stops left
    oneFactStopsOneFact(12,7,2.5);
    oneFactStopsOneFact(13,8,2.5);
  }

  private void setModuleTopology() {
    setFiftyFiftyTopology2(1.1);
  }

  //------------------learning goal-action stuff
  private int neuronsInGoal2CA = 200;
  private void setGoal2Topology(int CASize,double exciteWeight) {
    setConnections(0,size());
    for (int neuronNum=0; neuronNum<size(); neuronNum++) 
      {
      for (int synapseNum=0;synapseNum<neurons[neuronNum].getCurrentSynapses();
        synapseNum++) {
     	if ((neuronNum/CASize) == 
          (neurons[neuronNum].synapses[synapseNum].toNeuron.id /CASize)) {
      	  if (neurons[neuronNum].getInhibitory()) 
       	    neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
       	  else 
       	    neurons[neuronNum].synapses[synapseNum].setWeight(exciteWeight);
       	  }
       	else { //opposite CA
          if (neurons[neuronNum].getInhibitory()) {
       	    neurons[neuronNum].synapses[synapseNum].setWeight(-0.99);
	    }
       	  else 
       	    neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
       	  } 
        }
      if (neurons[neuronNum].getInhibitory()) 
        for (int synapse = 0 ; synapse < 10; synapse++) {
          int toNeuron = 0;
          if (neuronNum < 200) toNeuron = 1;
          toNeuron *=200;
          toNeuron += (int) (CANT23.random.nextFloat()*200);
          addConnection(neuronNum,toNeuron,0.99);
          }
      }
  }

  private static double normalRand (float mean, float variance) {
    double p = CANT23.random.nextFloat();
    return mean + Math.sqrt(variance) * Math.log(p / (1-p));
  }

  private void setGaussTopology() {
    for (int neuron = 0; neuron < getSize(); neuron++) {
      for (int synapse = 0; synapse < 100; synapse++) {
        // Normal (Gaussian) connectivity
        int toNeuron = (int) normalRand(neuron,getSize()/2) % getSize();
        if (toNeuron >= 0) toNeuron = toNeuron;
        else toNeuron = toNeuron + getSize();
        addConnection(neuron,toNeuron,1.0);
        }
      }
  }

  private int nOfModule2CAs = 2;
  private  int neuronsInModule2CA = 200;
  private void setModule2Topology() {
    for (int neuron = 0; neuron < getSize(); neuron ++) {
      if ((neuron%5) == 0) neurons[neuron].setInhibitory(true);
      else neurons[neuron].setInhibitory(false);
    }
    
    for (int neuron = 0; neuron < getSize(); neuron ++) {
      int CA = neuron/neuronsInModule2CA;
      if (neurons[neuron].isInhibitory) {
        for (int synapse=0; synapse < 80*((1+nOfModule2CAs)/2); synapse++) {
          int toNeuron = (int) (CANT23.random.nextFloat()*getSize());
          if ((toNeuron/neuronsInModule2CA) != CA)
            addConnection(neuron,toNeuron,2.0);
          }
        }
      else {
        for (int synapse=0; synapse < 15; synapse++) {
          int toNeuron = (int)(CANT23.random.nextFloat()*neuronsInModule2CA)+
            (CA*neuronsInModule2CA);
          addConnection(neuron,toNeuron,1.0);
        }
      }
    }
  }

  private void setSequenceTopology(double nextWeight) {
    int CASize = 200;
    int totalCAs = getSize()/CASize;
	
    //set up inhibitory
    for (int i = 0; i < getSize(); i++)
      {	if (((i%5) == 1) || ((i%5) == 3)) neurons[i].setInhibitory(true);
	else neurons[i].setInhibitory(false);
      }

    for (int CA = 0; CA < totalCAs; CA++)  {
      //add connections and weights	  
      for (int neuronNum = 0; neuronNum < CASize; neuronNum++) {
	int fromNeuron = neuronNum+(CA*CASize);
        if (neurons[fromNeuron].isInhibitory()) {
          //inhibit prior
	  if (CA > (0)) 
            for (int synapse = 0; synapse < 15; synapse++)    {
	      int toNeuron=((neuronNum+synapse)%CASize)+((CA-1)*CASize);
	      addConnection(fromNeuron,toNeuron,2.0);
	      }
	}
	else { //excitatory
	  //set self connections
	  for (int synapse = 1; synapse < 15; synapse++) {
	    int toNeuron;
	    if ((synapse%3)==0)
	      toNeuron=((neuronNum+(synapse*20))%CASize)+(CA*CASize);
	    else toNeuron=((neuronNum+synapse)%CASize)+(CA*CASize);
	      addConnection(fromNeuron,toNeuron,1.5);
          }

          //set next connections
	  if (CA < (totalCAs-1)) 
	     for (int synapse = 0; synapse < 5; synapse++) {
               int toNeuron=((neuronNum+synapse)%CASize)+((CA+1)*CASize);
	       addConnection(fromNeuron,toNeuron,nextWeight);
             }
        }//excite
      }//neuron
    }//CA
  }

  private void allSequenceNodesStopFirst() {
    int CASize = 200;
    int totalCAs = getSize()/CASize;
	
    for (int CA = 1; CA < totalCAs; CA++)  {
      //add connections and weights	  
      for (int neuronNum = 0; neuronNum < CASize; neuronNum++) {
	int fromNeuron = neuronNum+(CA*CASize);
        if (neurons[fromNeuron].isInhibitory()) {
          //inhibit prior
	  if (CA > (0)) 
            for (int synapse = 0; synapse < 15; synapse++)    {
	      int toNeuron=((neuronNum+synapse)%CASize);
	      addConnection(fromNeuron,toNeuron,2.0);
	      }
	}
      }//neuron
    }//CA
  }

  public void initializeNeurons() {
    //set up topologies.

    if (topology == 1){
      createNeurons(50);
      //System.out.println("parse input topology ");
      setInputTopology();
    }
    else if (topology == 2){
      createNeurons(100);
      //System.out.println("bar 1 topology ");
      setBarOneTopology();
    }
    else if (topology == 3){
      createNeurons(60);
      //System.out.println("n/v access topology ");
      setNounAccessTopology();
    }
    else if (topology == 4){
      createNeurons(50);
      //System.out.println("n/v sem topology ");
      setSemTopology();
    }
    else if (topology == 5){
      createNeurons(180);
      //System.out.println("rule topology ");
      setRuleTopology();
    }
    else if (topology == 6){
      createNounInstanceNeurons(110);
      //System.out.println("noun instance topology ");
      setNounInstanceTopology();
    }
    else if (topology == 7){
      createVerbInstanceNeurons(105);
      //System.out.println("verb instance topology ");
      setVerbInstanceTopology();
    }
    else if (topology == 8){
      createNeurons(35);
      //System.out.println("other word topology ");
      setOtherWordTopology();
    }
    else if (topology == 9){
      createNeurons(70);
      System.out.println("rule selection topology ");
      setRuleSelectionTopology();
    }

    else if (topology == 10){
      createNeurons(90);
      System.out.println("goal set topology ");
      setGoalSetTopology();
    }
    //11 goal2 topology below
    //12 value and explore topologies below
    //13 goal1 topology below
    else if (topology == 14){
      createNeurons();
      //no internal net connections
      //System.out.println("visual input topology ");
    }
    else if (topology == 15){
      createNeurons(1500);
      //System.out.println("control topology ");
      setControlTopology();
    }
    else if (topology == 16){
      createNeurons(20);
      //System.out.println("action topology ");
      setActionTopology();
    }
    else if (topology == 13){
      createNeurons(30);
      //System.out.println("goal 1 topology ");
      setGoal1Topology();
    }
    else if (topology == 17){
      createNeurons(90);
      //System.out.println("fact topology ");
      setFactTopology();
    }
    else if (topology == 18){
      createNeurons(90);
      //System.out.println("module topology ");
      setModuleTopology();
    }
    else if (topology == 11){
      createNeurons(100);
      //System.out.println("goal2 topology ");
      setGoal2Topology(neuronsInGoal2CA,0.3);
    }
    else if (topology == 12){
      createNeurons(100);
      //System.out.println("value explore topology ");
      setGaussTopology();
    }
    else if (topology == 22){
      createNeurons(90);
      //System.out.println("module2 topology ");
      setModule2Topology();
    }
    else if (topology == 23){
      createNeurons(90);
      System.out.println("value2 topology ");
      setSequenceTopology(0.23);
      allSequenceNodesStopFirst();
    }
    else if (topology == 19){
      createNeurons();
      //no internal net connections
      //System.out.println("retina topology ");
    }
    else if (topology == 20){
      createNeurons();
      //no internal net connections
      //System.out.println("V1 topology ");
    }
    else if (topology == 21){
      createNeurons(280);
      //System.out.println("V2 topology ");
      setV2Topology();
    }
    //22 module2 above
    //23 value2 above
    else System.out.println("bad toppology specified "+ topology);
  }
  
  
  //**********Connect Nets to Each Other***********************************
  int prepStart = 1800;
  private int prepCASize=360;

  private int goalSetSize = 10;
  private void connectASemToAGoalSet(int word,int goal, CABot2Net goalSetNet) {
    int semCASz = 60;
    for (int fromNeuron=word*semCASz;fromNeuron<(word+1)*semCASz;fromNeuron++) 
    {
      int toNeuron = fromNeuron%10;
      toNeuron += goal*goalSetSize;
      neurons[fromNeuron].addConnection(goalSetNet.neurons[toNeuron],0.25);
    }
  }

  public void connectVerbSemToGoalSet(CABot2Net goalSetNet) {
    connectASemToAGoalSet(0,20,goalSetNet); //move
    connectASemToAGoalSet(1,21,goalSetNet); //turn
    connectASemToAGoalSet(4,22,goalSetNet); //go
    connectASemToAGoalSet(6,23,goalSetNet); //center
  }

  public void connectNounSemToGoalSet(CABot2Net goalSetNet) {
    connectASemToAGoalSet(0,0,goalSetNet); //left
    connectASemToAGoalSet(10,1,goalSetNet); //right
    connectASemToAGoalSet(11,2,goalSetNet); //forward
    connectASemToAGoalSet(12,3,goalSetNet); //backward
    connectASemToAGoalSet(1,4,goalSetNet); //pyramid 
    connectASemToAGoalSet(3,5,goalSetNet); //stalactite
  }

  //turn on goalSet via control and language command semantics
  //each goalset neuron gets 1.25 per cycle
  public void connectControlToGoalSet(CABot2Net goalSetNet) {
    //control 2 primes goal sets
    for (int fromNeuron = 80; fromNeuron < 120; fromNeuron++) {
      if (!neurons[fromNeuron].isInhibitory()) {
        //get offset 0-3
        int toNeuron = (fromNeuron%5)-1;
        toNeuron += (((fromNeuron-80)/10)*4);
        for (int synapse = 0; synapse < 25; synapse ++ ) {
          neurons[fromNeuron].addConnection(goalSetNet.neurons[toNeuron],1.25);
          toNeuron += 16;
	}
      }
    }
    //control 3 turns off goal sets
    for (int i = 0; i < 8; i++) { 
      int fromNeuron = (i*5) + 120; //120 is the control 3 offset
      for (int synapse = 0; synapse < 50; synapse++) {
        int toNeuron = (i*50) + synapse;
        neurons[fromNeuron].addConnection(goalSetNet.neurons[toNeuron],-4.0);
      }
    }
  }

  public void connectControlToValue2(CABot2Net value2Net) {
    //control 2 turns on value2
    for (int i = 80; i < 120; i++) {
      if (!neurons[i].isInhibitory()) {
        for (int synapse = 0; synapse < 6; synapse++) {
          int toNeuron = (i*synapse) % 200;
          neurons[i].addConnection(value2Net.neurons[toNeuron],2.0);
	}
      }
    }
    //control 3 turns off value2
    for (int i = 0; i < 8; i++) { 
      int fromNeuron = (i*5) + 120; //120 is the control 3 offset
      for (int synapse = 0; synapse < 25; synapse++) {
        int toNeuron = (i*25) + synapse + 400;
        neurons[fromNeuron].addConnection(value2Net.neurons[toNeuron],-24.0);
      }
    }
  }

  public void connectValue2ToControl(CABot2Net controlNet) {
    //The third CA primes control CA 3 
    //200 neurons 60% excitatory -> 120 lets just use the first 40
      int toNeuron = 120;
      for (int fromNeuron = 400; fromNeuron < 467; fromNeuron ++) {
        if (!neurons[fromNeuron].isInhibitory()) {
          neurons[fromNeuron].addConnection(controlNet.neurons[toNeuron],1.0);
          toNeuron ++;
	}
      }
  }

  private void connectOneGoalSetToOneGoal(int goalSet,int goal,
    CABot2Net goalNet,   double weight ) 
  {
    for (int i = 0 ; i < 10; i++) {
      int fromNeuron = (goalSet*10)+i;
      for (int synapse = 0; synapse < 4; synapse++) {
        int toNeuron = (i%5) + (synapse*10) + (goal*neuronsInGoal);
        neurons[fromNeuron].addConnection(goalNet.neurons[toNeuron],weight);
      }
    }
  }

  public void connectGoalSetToGoal1(CABot2Net goal1Net) {
    connectOneGoalSetToOneGoal(21,0,goal1Net,1.0); //turn to turn left;
    connectOneGoalSetToOneGoal(0,0,goal1Net,1.0); //left to turn left;
    connectOneGoalSetToOneGoal(21,1,goal1Net,1.0); //turn to turn right
    connectOneGoalSetToOneGoal(1,1,goal1Net,1.0); //right to turn right
    connectOneGoalSetToOneGoal(20,2,goal1Net,1.0); //move to move forward
    connectOneGoalSetToOneGoal(2,2,goal1Net,1.0); //forward to move forward
    connectOneGoalSetToOneGoal(20,3,goal1Net,1.0); //move to move backward
    connectOneGoalSetToOneGoal(3,3,goal1Net,1.0); //move to move backward
    connectOneGoalSetToOneGoal(20,4,goal1Net,1.0); //move to turnleft+go
    connectOneGoalSetToOneGoal(0,4,goal1Net,1.0); //left to turnleft+go
    connectOneGoalSetToOneGoal(20,5,goal1Net,1.0); //move to turnright+go
    connectOneGoalSetToOneGoal(1,5,goal1Net,1.0); //right to turnright+go
    connectOneGoalSetToOneGoal(21,6,goal1Net,1.0); //turn to turn toward
    connectOneGoalSetToOneGoal(4,6,goal1Net,1.0); //pyramid to turn toward
    connectOneGoalSetToOneGoal(5,6,goal1Net,1.0); //stalactite to turn toward
    connectOneGoalSetToOneGoal(22,7,goal1Net,1.0); //go to go to
    connectOneGoalSetToOneGoal(4,7,goal1Net,1.0); //pyramid to go to
    connectOneGoalSetToOneGoal(5,7,goal1Net,1.0); //stalactite go to
    connectOneGoalSetToOneGoal(4,8,goal1Net,1.0); //pyramid to to pyramid
    connectOneGoalSetToOneGoal(5,9,goal1Net,1.0); //stalactite to stalacite
    connectOneGoalSetToOneGoal(23,10,goal1Net,1.0); //center to center
    connectOneGoalSetToOneGoal(4,10,goal1Net,1.0); //pyramid to center
    connectOneGoalSetToOneGoal(5,10,goal1Net,1.0); //stalactite to center
  }

  /*******Action Connectitivity*/
  public void connectControlToFact(CABot2Net factNet) {
    //Top control CA suppress action
    for (int neuronNum=0; neuronNum<200;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapseNum=0;synapseNum < 250; synapseNum++) {
          int toNeuron=(int) (CANT23.random.nextFloat()*factNet.getSize());
          neurons[neuronNum].addConnection(factNet.neurons[toNeuron],-8.0);
	}
    }
    //bottom controls also suppress action
    for (int neuronNum=600; neuronNum<1600;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapseNum=0;synapseNum < 250; synapseNum++) {
          int toNeuron=(int) (CANT23.random.nextFloat()*factNet.getSize());
          neurons[neuronNum].addConnection(factNet.neurons[toNeuron],-8.0);
	}
    }
  }
  
  public void connectStackTopToControl(CABot2Net controlNet) {
    //StackTop surpresses control 2. When stacktop stops, control 2
    //can become active
    for (int neuronNum=0; neuronNum<getSize();neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapseNum=0;synapseNum < 50; synapseNum++) {
          int toNeuron=(int) (CANT23.random.nextFloat()*200) + 200;
          neurons[neuronNum].addConnection(controlNet.neurons[toNeuron],-8.0);
	}
    }
  }
  
  private void connectV2PositionToFact(CABot2Net factNet, int V2GroupCol, 
    int fact) {
    for (int row = 0; row<getInputRows()*4;row ++) {
      for (int col = 2; col< 10;col ++) {
        int fromNeuron = (V2GroupCol*10)+(row*getInputRows())+col;
        for (int synapseNum=0;synapseNum < 20; synapseNum++) {
          int toNeuron=(fact*neuronsInFact)+
            (int)(CANT23.random.nextFloat()*neuronsInFact);
          neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],1.5);
        }
      }
    }
  }

  private void connectV2TopHalfShapeColFact(int shape, int lCol,int factNum,
    CABot2Net factNet) {
    int shapeOffset = shape*2500;
    for (int lRow = 0; lRow < 5; lRow++) {
      int locOffset = (lRow*500) + (lCol *10);
      for (int row = 0; row < 5; row++) {
        for (int col = 0; col < 10; col++) {
          int fromNeuron= (row*50) + col+locOffset+shapeOffset;
          if (!neurons[fromNeuron].isInhibitory()) {
	    //two neurons from each top half connect to each of the 
            //feature neurons 0..4, 10..14 20..24,30..34;
            int toNeuron = (factNum*neuronsInFact);
  	    toNeuron += row + (((col%5)-1)*10);
            neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],
	      0.65); //1.15
	  }
	}
      }
    }
  }
  public void connectV2ToFact(CABot2Net factNet) {
    for (int shape = 0; shape < 4; shape ++) {
      connectV2TopHalfShapeColFact(shape,0,6,factNet); //shapes left left
      connectV2TopHalfShapeColFact(shape,1,6,factNet); //shapes leftC left
      connectV2TopHalfShapeColFact(shape,3,7,factNet); //shapes rightC right
      connectV2TopHalfShapeColFact(shape,4,7,factNet); //shapes right right
      connectV2TopHalfShapeColFact(shape,2,8,factNet); //shapes center center
      connectV2TopHalfShapeColFact(shape,0,11,factNet); //shapes left left
      connectV2TopHalfShapeColFact(shape,1,11,factNet); //shapes leftC left
      connectV2TopHalfShapeColFact(shape,3,12,factNet); //shapes rightC right
      connectV2TopHalfShapeColFact(shape,4,12,factNet); //shapes right right
      connectV2TopHalfShapeColFact(shape,2,13,factNet); //shapes center center
    }
    for (int col = 0; col < 5; col ++) {
      connectV2TopHalfShapeColFact(4,col,9,factNet); //large pyra to big fact
      connectV2TopHalfShapeColFact(5,col,9,factNet); //large stal to big fact
    }
  }

  private void connectToV2TopHalf(int fromNeuron, int shape, CABot2Net V2Net) {
    int shapeOffset = 2500*shape;
    for (int lCol = 0; lCol < 5; lCol++) {
      for (int lRow = 0; lRow < 5; lRow ++) {
        int placeOffset = (lRow*500) + (lCol*10);
        int iRow = (fromNeuron%neuronsInFact)/10;
        int iCol = fromNeuron%10;
        int toNeuron = iCol+(iRow*50)+placeOffset+shapeOffset;
        neurons[fromNeuron].addConnection(V2Net.neurons[toNeuron],0.65);
      }
    }
  }
  private void connectOneFactToV2TopHalfs(int factNum, int arb,
    CABot2Net V2Net) {
    for (int neuronNum=0; neuronNum<neuronsInFact;neuronNum++) 
      {
      int fromNeuron = neuronNum+(factNum*neuronsInFact);
      if (!neurons[fromNeuron].isInhibitory())
        {
        if (arb==0) {
          connectToV2TopHalf(fromNeuron,0,V2Net);
          connectToV2TopHalf(fromNeuron,2,V2Net);
	  }
        else if (arb==1){
          connectToV2TopHalf(fromNeuron,1,V2Net);
          connectToV2TopHalf(fromNeuron,3,V2Net);
  	  }
        else if (arb==2){
          connectToV2TopHalf(fromNeuron,4,V2Net);
  	  }
        else if (arb==3){
          connectToV2TopHalf(fromNeuron,5,V2Net);
  	  }
        }
      }
  }
  public void connectFactToV2(CABot2Net V2Net) {
    connectOneFactToV2TopHalfs(4,0,V2Net); //target pyramid to v2 pyramids
    connectOneFactToV2TopHalfs(5,1,V2Net); //target stals to v2 stals
    connectOneFactToV2TopHalfs(4,2,V2Net); //target pyr to v2 big
    connectOneFactToV2TopHalfs(5,3,V2Net); //target stals to v2 big
  }


//************************************************
  private int neuronsInGoal = 40;
  private int neuronsInModule = 40;

  private void connectOneGoalToOneModule(int goalNum, int moduleNum, 
    CABot2Net moduleNet, double weight) {
    for (int neuronNum=0; neuronNum<neuronsInGoal;neuronNum++) 
      {
      int fromNeuron = neuronNum+(goalNum*neuronsInGoal);
      if (!neurons[fromNeuron].isInhibitory())
        {
        int toNeuron = neuronNum+(moduleNum*neuronsInModule);
        neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron],weight);
        if ((fromNeuron%5) == 1)
          neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron-1],
            weight);
        }
      }
  }

  //center, a goal2 CA, and a module2 CA all need to be firing.
  private void connectGoal1CenterToModule1(CABot2Net module1Net) {
    //connect the center goal to both left and right
    for (int neuronNum=0; neuronNum<neuronsInGoal;neuronNum++) 
      {
      int fromNeuron = neuronNum+400;
      if (!neurons[fromNeuron].isInhibitory())
        { 
        //left
        int toNeuron = neuronNum;
        neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron],
          goal1Module1Wt); 
        if ((fromNeuron%5) == 1)
         neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron-1],
           goal1Module1Wt);
	//right
        toNeuron += neuronsInModule;
        neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron],
          goal1Module1Wt);
        if ((fromNeuron%5) == 1)
         neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron-1],
           goal1Module1Wt);
        }
      }
  }

  public void connectGoal1ToModule(CABot2Net moduleNet) {
    connectOneGoalToOneModule(0,0,moduleNet,2.25);//turn+left
    connectOneGoalToOneModule(1,1,moduleNet,2.25);//turn+right
    connectOneGoalToOneModule(2,2,moduleNet,2.25);//move+foreward
    connectOneGoalToOneModule(3,3,moduleNet,2.25);//move+backward
    connectOneGoalToOneModule(7,2,moduleNet,1.15);//go to forward 1/2 with 
    //center fact
    connectOneGoalToOneModule(6,5,moduleNet,1.15);//turn to center err 1/2 
    //with center fact

    connectGoal1CenterToModule1(moduleNet);
  }

  private void connectOneGoalToOneFact(int goalNum, int factNum,double weight,
    CABot2Net factNet) {

    for (int neuronNum=0; neuronNum<neuronsInGoal;neuronNum++) 
      {
      int fromNeuron = neuronNum+(goalNum*neuronsInGoal);
      if (!neurons[fromNeuron].isInhibitory())
        {
        int toNeuron = neuronNum+(factNum*neuronsInFact);
        neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],weight);
        if ((fromNeuron%5) == 1)
         neurons[fromNeuron].addConnection(factNet.neurons[toNeuron-1],weight);
        }
      }
  }

  private void oneGoalStopsOneFact(int goalNum, int factNum,CABot2Net factNet){
    for (int neuronNum=0; neuronNum<neuronsInGoal;neuronNum+=5) 
      {
      int fromNeuron = neuronNum+(goalNum*neuronsInGoal);
      //assert(neurons[fromNeuron].isInhibitory())
      for (int synapse=0; synapse < 10;synapse++)  {
        int toNeuron;
        if ((neuronNum%10) == 5)
          toNeuron = neuronNum-5+synapse;
        else toNeuron = neuronNum+synapse;
        toNeuron+=factNum*neuronsInFact;
        neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],-1.0);
        }
      }
  }
  public void connectGoal1ToFact(CABot2Net factNet) {
    connectOneGoalToOneFact(4,0,2.25,factNet); //move left to ml1
    connectOneGoalToOneFact(5,2,2.25,factNet); //move right to mr1
    connectOneGoalToOneFact(8,4,2.25,factNet); //pyramid to pyramid 
    connectOneGoalToOneFact(9,5,2.25,factNet); //stal to stal
    connectOneGoalToOneFact(6,6,1.25,factNet); //turn to left
    connectOneGoalToOneFact(6,7,1.25,factNet); //turn to right
    connectOneGoalToOneFact(6,8,1.25,factNet); //turn to center
    connectOneGoalToOneFact(7,6,1.25,factNet); //go to left
    connectOneGoalToOneFact(7,7,1.25,factNet); //go to right
    connectOneGoalToOneFact(7,8,1.25,factNet); //go to center
    connectOneGoalToOneFact(7,9,1.25,factNet); //go to big
    connectOneGoalToOneFact(10,11,1.25,factNet); //center to left2
    connectOneGoalToOneFact(10,12,1.25,factNet); //center to right2
    connectOneGoalToOneFact(10,13,1.25,factNet); //center to center2
    oneGoalStopsOneFact(10,10, factNet); //center prevents object err
  }

  private void oneFactStopsOneGoal(int factNum, int goalNum,CABot2Net goalNet){
    for (int neuronNum=0; neuronNum<neuronsInFact;neuronNum+=5) 
      {
      int fromNeuron = neuronNum+(factNum*neuronsInFact);
      //assert(neurons[fromNeuron].isInhibitory())
      for (int synapse=0; synapse < 10;synapse++) 
        {
        int toNeuron;
        if ((neuronNum%10) == 5)
          toNeuron = neuronNum-5+synapse;
        else toNeuron = neuronNum+synapse;
        toNeuron+=goalNum*neuronsInGoal;
        neurons[fromNeuron].addConnection(goalNet.neurons[toNeuron],-1.0);
        }
      }
  }
  public void connectFactToGoal1(CABot2Net goalNet) {
    oneFactStopsOneGoal(1,4,goalNet); //second move left stops move left
    oneFactStopsOneGoal(3,5,goalNet);//second move right stops move right
    oneFactStopsOneGoal(9,7,goalNet);//object is big stops go
    oneFactStopsOneGoal(13,10,goalNet);//object in center stops center
  }

  private void connectOneFactToOneModule(int factNum, int moduleNum,
    double wt, CABot2Net moduleNet) {

    for (int neuronNum=0; neuronNum<neuronsInFact;neuronNum++) 
      {
      int fromNeuron = neuronNum+(factNum*neuronsInFact);
      if (!neurons[fromNeuron].isInhibitory())
        {
        int toNeuron = neuronNum+(moduleNum*neuronsInModule);
        neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron],wt);
        if ((fromNeuron%5) == 1)
          neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron-1],wt);
        }
      }
  }
  private void oneFactStopsOneModule(int factNum, int moduleNum,
    double wt, CABot2Net moduleNet) {

    for (int neuronNum=0; neuronNum<neuronsInFact;neuronNum++) 
      {
      int fromNeuron = neuronNum+(factNum*neuronsInFact);
      if (neurons[fromNeuron].isInhibitory())
        {
        for (int synapse = 0; synapse<10;synapse++) {
          int toNeuron = neuronNum+(moduleNum*neuronsInModule);
          if ((neuronNum%10) == 0) 
            toNeuron += synapse;
          else toNeuron += (synapse -5);
          neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron],wt);
	  }
        }
      }
  }
  public void connectFactToModule(CABot2Net moduleNet) {
    connectOneFactToOneModule(0,0,2.25,moduleNet);//turnleft1 to left
    connectOneFactToOneModule(1,2,2.25,moduleNet);//turnleft2 to forward
    connectOneFactToOneModule(2,1,2.25,moduleNet);//turnright1 to right
    connectOneFactToOneModule(3,2,2.25,moduleNet);//turnright2 to forward
    connectOneFactToOneModule(6,0,2.25,moduleNet);//object in left to left
    connectOneFactToOneModule(7,1,2.25,moduleNet);//object in right to right
    connectOneFactToOneModule(8,2,1.5,moduleNet);//object in center to foward
    //forward 1/2 with go goal
    connectOneFactToOneModule(10,4,2.22223,moduleNet);//no obj to no obj
    oneFactStopsOneModule(6,4,-2.5,moduleNet);//left, right and centre 
    oneFactStopsOneModule(7,4,-2.5,moduleNet);//inhibit no obj error
    oneFactStopsOneModule(8,4,-2.5,moduleNet);

    connectOneFactToOneModule(8,5,1.5,moduleNet);//obj cent to error turn cent
  }

  private void oneModuleStopsOneGoal(int moduleNum, int goalNum, 
    CABot2Net goalNet) {
    for (int neuronNum=0; neuronNum<neuronsInModule;neuronNum+=5) 
      {
      int fromNeuron = neuronNum+(moduleNum*neuronsInModule);
      //assert(neurons[fromNeuron].isInhibitory())
      for (int synapse=0; synapse < 10;synapse++) 
        {
        int toNeuron;
        if ((neuronNum%10) == 5)
          toNeuron = neuronNum-5+synapse;
        else toNeuron = neuronNum+synapse;
        toNeuron+=goalNum*neuronsInGoal;
        neurons[fromNeuron].addConnection(goalNet.neurons[toNeuron],-1.0);
        }
      }
  }
  public void connectModuletoGoal1(CABot2Net goal1Net) {
    oneModuleStopsOneGoal(0,0,goal1Net); //left left
    oneModuleStopsOneGoal(1,1,goal1Net); //right right
    oneModuleStopsOneGoal(2,2,goal1Net); //forward forward
    oneModuleStopsOneGoal(3,3,goal1Net); //back back
    oneModuleStopsOneGoal(0,6,goal1Net); //left turn
    oneModuleStopsOneGoal(1,6,goal1Net); //right turn
    oneModuleStopsOneGoal(4,6,goal1Net); //no object error turn
    oneModuleStopsOneGoal(4,7,goal1Net); //no object error stops go
    oneModuleStopsOneGoal(5,6,goal1Net); //turn to center to turn
  }

  private void oneModuleStopsOneFact(int moduleNum, int factNum, 
    CABot2Net factNet) {
    for (int neuronNum=0; neuronNum<neuronsInModule;neuronNum+=5) 
      {
      int fromNeuron = neuronNum+(moduleNum*neuronsInModule);
      //assert(neurons[fromNeuron].isInhibitory())
      for (int synapse=0; synapse < 10;synapse++) 
        {
        int toNeuron;
        if ((neuronNum%10) == 5)
          toNeuron = neuronNum-5+synapse;
        else toNeuron = neuronNum+synapse;
        toNeuron+=factNum*neuronsInFact;
        neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],-1.0);
        }
      }
  }
  public void connectModuletoFact(CABot2Net factNet) {
    oneModuleStopsOneFact(0,0,factNet); //left stops moveleft1
    oneModuleStopsOneFact(1,2,factNet); //right stops moveright1
    oneModuleStopsOneFact(2,1,factNet); //forward stops moveleft2
    oneModuleStopsOneFact(2,3,factNet); //forward stops moveright2
    oneModuleStopsOneFact(0,6,factNet); //left stops target on left
    oneModuleStopsOneFact(0,7,factNet); 
    oneModuleStopsOneFact(0,8,factNet); 
    oneModuleStopsOneFact(1,6,factNet); 
    oneModuleStopsOneFact(1,7,factNet); //right stops target on right
    oneModuleStopsOneFact(1,8,factNet); 
    oneModuleStopsOneFact(2,8,factNet); //forward stops target on center
    oneModuleStopsOneFact(4,10,factNet); //no target stops target absent
    oneModuleStopsOneFact(0,11,factNet); //left stops center left right or cent
    oneModuleStopsOneFact(0,12,factNet); 
    oneModuleStopsOneFact(0,13,factNet); 
    oneModuleStopsOneFact(1,11,factNet); //right stops center left rt or cent
    oneModuleStopsOneFact(1,12,factNet); 
    oneModuleStopsOneFact(1,13,factNet); 
  }

  private int neuronsInAction = 40;
  private void connectOneModuleToOneAction(int moduleNum, int actionNum, 
    CABot2Net actionNet) {
    for (int neuronNum=0; neuronNum<neuronsInModule;neuronNum++) 
      {
      int fromNeuron = neuronNum+(moduleNum*neuronsInModule);
      if (!neurons[fromNeuron].isInhibitory())
        {
        int toNeuron = neuronNum+(actionNum*neuronsInAction);
        neurons[fromNeuron].addConnection(actionNet.neurons[toNeuron],2.25);
        if ((fromNeuron%5) == 1)
          neurons[fromNeuron].addConnection(actionNet.neurons[toNeuron-1],
            2.25);
        }
      }
  }
  public void connectModuleToAction(CABot2Net actionNet) {
    connectOneModuleToOneAction(0,0,actionNet);//turnleft
    connectOneModuleToOneAction(1,1,actionNet);//turnright
    connectOneModuleToOneAction(2,2,actionNet);//moveforeward
    connectOneModuleToOneAction(3,3,actionNet);//movebackward
    connectOneModuleToOneAction(4,4,actionNet);//no target error
    connectOneModuleToOneAction(5,5,actionNet);//turn to center error
  }
  

  private void oneActionStopsOneModule(int actionNum, int moduleNum, 
    CABot2Net moduleNet) {

    for (int neuronNum=0; neuronNum<neuronsInAction;neuronNum+=5) 
      {
      int fromNeuron = neuronNum+(actionNum*neuronsInAction);
      //assert(neurons[fromNeuron].isInhibitory())
      for (int synapse=0; synapse < 10;synapse++) 
        {
        int toNeuron;
        if ((neuronNum%10) == 5)
          toNeuron = neuronNum-5+synapse;
        else toNeuron = neuronNum+synapse;
        toNeuron+=moduleNum*neuronsInModule;
        neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron],-1.0);
        }
      }
  }
   
  //action turns off the module that ignited it.
  public void connectActionToModule(CABot2Net moduleNet) {
    oneActionStopsOneModule(0,0,moduleNet);//left left
    oneActionStopsOneModule(1,1,moduleNet);//right right
    oneActionStopsOneModule(2,2,moduleNet);//forward forward
    oneActionStopsOneModule(3,3,moduleNet);//back back
    oneActionStopsOneModule(4,4,moduleNet);//no target error
    oneActionStopsOneModule(5,5,moduleNet);//turn to center

    oneActionStopsOneModule(0,1,moduleNet);//left right
    oneActionStopsOneModule(1,0,moduleNet);//right left
  }

  public void connectGoal1ToGoal2(CABot2Net goal2Net) {
    for (int i = 0; i < 40; i ++) {
      int fromNeuron = 400+i;
      if (!neurons[fromNeuron].isInhibitory()){
        for (int synapse = 0; synapse < 10; synapse ++) {
          int toNeuron = synapse*40 + i;
          neurons[fromNeuron].addConnection(goal2Net.neurons[toNeuron],1.0);
        }
      }
    }
  }

  private void connectOneFactToOneGoal2(int factNum, int goal2Num, 
    CABot2Net goal2Net) {
    for (int neuronNum=0; neuronNum<neuronsInFact;neuronNum++) 
      {
      int fromNeuron = neuronNum+(factNum*neuronsInFact);
      if (!neurons[fromNeuron].isInhibitory()){
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = (synapse*40)+neuronNum+(goal2Num*neuronsInGoal2CA);
          neurons[fromNeuron].addConnection(goal2Net.neurons[toNeuron],2.0);
	  }
        }
      }
  }
  public void connectFactToGoal2(CABot2Net goal2Net) {
    connectOneFactToOneGoal2(11,0,goal2Net); //left to centerLeft
    connectOneFactToOneGoal2(12,1,goal2Net); //right to centerRight
  }

  private void connectToOne(CABot2Net toNet, int fromCA, int toCA,
    int neuronsInFromCA, int neuronsInToCA, int numSynapses, double weight) {
    for (int fromNeuron=fromCA*neuronsInFromCA; 
      fromNeuron<(fromCA + 1)*neuronsInFromCA;fromNeuron++) {
      if (!neurons[fromNeuron].isInhibitory()) {
        for (int synapse=0; synapse < numSynapses; synapse++) {
          int toNeuron = ((int)(CANT23.random.nextFloat()*neuronsInToCA))+
            (toCA*neuronsInToCA);
          neurons[fromNeuron].addConnection(toNet.neurons[toNeuron],weight);
        }
      }
    }
  }

  public void connectGoal2ToModule2(CABot2Net module2Net) {
    connectToOne(module2Net,0,0,200,200,10,0.01);
    connectToOne(module2Net,0,1,200,200,10,0.01);
    connectToOne(module2Net,1,0,200,200,10,0.01);
    connectToOne(module2Net,1,1,200,200,10,0.01);
  }

  public void connectValueToExplore(CABot2Net exploreNet) {
    connectToOne(exploreNet,0,0,400,400,10,-2.0);//Value -> Explore
  }

  public void connectExploreToModule2(CABot2Net module2Net) {
    connectToOne(module2Net,0,0,400,200,10,0.7);
    connectToOne(module2Net,0,1,400,200,10,0.7);
  }

  public void connectFactToValue(CABot2Net valueNet) {
    //object in center turns value on
    for (int i = 0; i < 40; i++) {
      int fromNeuron = 520+i;
      for (int synapse = 0; synapse < 20; synapse ++) {
        int toNeuron = ((int)(CANT23.random.nextFloat()*valueNet.getSize()));
        if (!neurons[fromNeuron].isInhibitory())
          neurons[fromNeuron].addConnection(valueNet.neurons[toNeuron],2.5);
      }
    }
  }

  //To turn a module1 on, there should be one goal2, goal1-center 
  //and one module2.  The module2 chooses between module1s
  private double module2Module1Wt = 0.13; 
  private double goal2Module1Wt = 0.13; 
  private double goal1Module1Wt = 1.0; 
  private void connectOneModule2ToOneModule1(int module2Num, int module1Num, 
    CABot2Net module1Net) {
    //connect the first 80 excitatory neurons 
    int excitatoryFound = 0;
    for (int neuronNum=0; neuronNum<neuronsInModule2CA;neuronNum++) 
      {
      int fromNeuron = neuronNum+(module2Num*neuronsInModule2CA);
      if ((!neurons[fromNeuron].isInhibitory()) && (excitatoryFound < 80))
        {
	int toNeuron = excitatoryFound%(neuronsInModule/2); //0-19
        toNeuron = ((toNeuron/5)*10) + (toNeuron%5); //0-4,10-14,20-24,30-34
        toNeuron += module1Num*neuronsInModule;
        neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron],
          module2Module1Wt);
        excitatoryFound ++;
        }
      }
  }
  public void connectModule2ToModule1(CABot2Net module1Net) {
    connectOneModule2ToOneModule1(0,0,module1Net);
    connectOneModule2ToOneModule1(1,1,module1Net);
  }

  private void connectOneGoal2ToOneModule1(int goal2Num, int module1Num, 
    CABot2Net module1Net) {
    //connect the first 80 excitatory neurons 
    int excitatoryFound = 0;
    for (int neuronNum=0; neuronNum<neuronsInGoal2CA;neuronNum++) 
      {
      int fromNeuron = neuronNum+(goal2Num*neuronsInGoal2CA);
      if ((!neurons[fromNeuron].isInhibitory()) && (excitatoryFound < 80))
        {
	int toNeuron = excitatoryFound%(neuronsInModule/2); //0-19
        toNeuron = ((toNeuron/5)*10) + (toNeuron%5); //0-4,10-14,20-24,30-34
        toNeuron += module1Num*neuronsInModule;
        neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron],
          goal2Module1Wt);
        excitatoryFound ++;
        }
      }
  }
  public void connectGoal2ToModule1(CABot2Net module1Net) {
    connectOneGoal2ToOneModule1(0,0,module1Net);
    connectOneGoal2ToOneModule1(0,1,module1Net);
    connectOneGoal2ToOneModule1(1,0,module1Net);
    connectOneGoal2ToOneModule1(1,1,module1Net);
  }

  public void connectValueToValue2(CABot2Net value2Net) {
    for (int i = 0; i < 400; i++) {
      if (!neurons[i].isInhibitory()) {
        int toNeuron = i % 200;
        neurons[i].addConnection(value2Net.neurons[toNeuron],1.0);
      }
    }
  }

  public void connectValue2ToGoal2(CABot2Net goal2Net) {
    int lastValue2CA = (getSize()/200)-1;
    for (int i = 0; i < 200; i++) {
      int fromNeuron = (lastValue2CA*200)+i;
      if (neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 10; synapse++) {
          int toNeuron = ((int)(CANT23.random.nextFloat()*400));
          neurons[fromNeuron].addConnection(goal2Net.neurons[toNeuron],-4.0);
	}
      }
    }
  }

  private void value2StopsOneFact(int value2CA, int factCA, CABot2Net factNet){
    for (int i = 0; i < 200; i++) {
      int fromNeuron = (value2CA*200)+i;
      if (neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 3; synapse++) {
          int toNeuron = ((int)(CANT23.random.nextFloat()*neuronsInFact));
          toNeuron += factCA*neuronsInFact;
          neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],-4.0);
	}
      }
    }
  }
  public void connectValue2ToFact(CABot2Net factNet) {
    int nLastValue2CA = (getSize()/200)-2;
    value2StopsOneFact(nLastValue2CA,11,factNet);
    value2StopsOneFact(nLastValue2CA,12,factNet);
    value2StopsOneFact(nLastValue2CA,13,factNet);
  }

  public void connectValue2ToValue(CABot2Net valueNet) {
    int lastValue2CA = (getSize()/200)-1;
    for (int i = 0; i < 200; i++) {
      int fromNeuron = (lastValue2CA*200)+i;
      if (neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 10; synapse++) {
          int toNeuron = ((int)(CANT23.random.nextFloat()*400));
          neurons[fromNeuron].addConnection(valueNet.neurons[toNeuron],-4.0);
	}
      }
    }
  }

  //the last rule (S->VP.) finishes parsing
  public void connectRuleToControl(CABot2Net controlNet) {
    for (int fromNeuron= 800; fromNeuron < 840;fromNeuron++) {//not all 100
      if (!neurons[fromNeuron].isInhibitory()) {
	  int toNeuron = (fromNeuron%100)+40;
        neurons[fromNeuron].addConnection(controlNet.neurons[toNeuron],2.1);
	if ((fromNeuron %5) ==1)
          neurons[fromNeuron].addConnection(controlNet.neurons[toNeuron-1],
            2.1);
      }
    }
  }

  public void connectControlToBarOne(CABot2Net barOneNet) {
    for(int i = 0; i < 8 ; i++) {
      int fromNeuron = 40 + (i*5);
      for (int synapse = 0; synapse < 10*5; synapse++) {
        int toGroup = synapse/10;
        int toOffset= synapse%10;
        int toNeuron = (toGroup*40)+toOffset+((i/2)*10);
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron],-2.0);
      }
    }
  }

  //consider this kills two rules not just one.
  private void controlStopsOneRule(int ruleStart, CABot2Net ruleNet) {
    for(int i = 0; i < 8 ; i++) {
      int fromNeuron = 40 + (i*5);
      for (int synapse = 0; synapse < 10*5; synapse++) {
        int toGroup = synapse/10;
        int toOffset= synapse%10;
        int toNeuron = (toGroup*40)+toOffset+((i/2)*10)+ruleStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],-2.0);
      }
    }
  }

  public void connectControlToRule(CABot2Net ruleNet) {
    controlStopsOneRule(300,ruleNet); //stops read next word
    controlStopsOneRule(800,ruleNet); //stops S->VP.
  }

  public void connectControlToOther(CABot2Net otherNet) {
    for(int i = 0; i < 8 ; i++) {
      int fromNeuron = 40 + (i*5);
      for (int synapse = 0; synapse < 10*5; synapse++) {
        int toGroup = synapse/10;
        int toOffset= synapse%10;
        int toNeuron = (toGroup*40)+toOffset+((i/2)*10);
        neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron],-2.0);
      }
    }
  }

  private void oneControlStopsAccess(int controlCA, int numAccess, 
    CABot2Net accessNet) {
    for(int i = 0; i < 8 ; i++) {
      int fromNeuron = (controlCA*controlCASize) + (i*5);
      for (int synapse = 0; synapse <5 ; synapse ++) {
        //there are 100 neurons per access. 
        //Each pair of inhibs suppresses 1 group of 5 with the other ignored.
        int suppressGroups = ((numAccess*100)/controlCASize)+1; 
        int toNeuron = ((i/2)*10)+ synapse; //offset in group
        for (int group = 0; group < suppressGroups; group ++) {
          neurons[fromNeuron].addConnection(accessNet.neurons[toNeuron],-2.0);
          toNeuron += controlCASize;
	}
      }
    }
  }
  public void connectControlToNounAccess(CABot2Net nounAccessNet) {
    oneControlStopsAccess(1,1,nounAccessNet); 
    oneControlStopsAccess(3,numNouns,nounAccessNet); 
  }
  public void connectControlToVerbAccess(CABot2Net verbAccessNet) {
    oneControlStopsAccess(1,1,verbAccessNet); 
    oneControlStopsAccess(3,numVerbs,verbAccessNet); 
  }
  private void oneControlStopsSem(int controlCA, int numSem,CABot2Net semNet) {
    for(int i = 0; i < 8 ; i++) {
      int fromNeuron = (controlCA*controlCASize) + (i*5);
      for (int synapse = 0; synapse < 10; synapse ++) {
        //there are 60 neurons per access. This suppresses in groups of 40;
        int suppressGroups = ((numSem*60)/controlCASize)+1; 
        int toNeuron = ((i/2)*10) + synapse; //offset in group
        for (int group = 0; group < suppressGroups; group ++) {
          neurons[fromNeuron].addConnection(semNet.neurons[toNeuron],-2.0);
          toNeuron += controlCASize;
	}
      }
    }
  }
  public void connectControlToNounSem(CABot2Net nounSemNet) {
    oneControlStopsSem(1,numNouns,nounSemNet); 
    oneControlStopsSem(3,numNouns,nounSemNet); 
  }
  public void connectControlToVerbSem(CABot2Net verbSemNet) {
    oneControlStopsSem(1,numVerbs,verbSemNet); 
    oneControlStopsSem(3,numVerbs,verbSemNet); 
  }

  private void oneControlStopsInstance(int controlCA, int suppressGroups,
    CABot2Net instanceNet) {
    for(int i = 0; i < 8 ; i++) {
      int fromNeuron = (controlCA*controlCASize) + (i*5);
      for (int synapse = 0; synapse < 10; synapse ++) {
        int toNeuron = ((i/2)*10) + synapse; //offset in group
        for (int group = 0; group < suppressGroups; group ++) {
          if (toNeuron < instanceNet.getSize()) {
            neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron],
              -2.0);
            toNeuron += controlCASize;
	  }
	}
      }
    }
  }
  public void connectControlToNounInstance(CABot2Net nounInstanceNet) {
    oneControlStopsInstance(1,(nounInstanceNet.getSize()/40)+1,
      nounInstanceNet);
    oneControlStopsInstance(3,(nounInstanceNet.getSize()/40)+1,
      nounInstanceNet);
  }

  private void oneControlStartsOneVerbInstance(int controlCA,int VICA,
    CABot2Net verbInstanceNet) {
    for (int neuron = 0; neuron<40;neuron ++) {
      int fromNeuron = neuron+(controlCA*controlCASize);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuron+(VICA*vInstCASize);
        neurons[fromNeuron].addConnection(verbInstanceNet.neurons[toNeuron],
              3.0);
        if ((fromNeuron %5) ==1)
          neurons[fromNeuron].addConnection(
            verbInstanceNet.neurons[toNeuron-1],3.0);
      }
    }
  }
  public void connectControlToVerbInstance(CABot2Net verbInstanceNet) {
    oneControlStopsInstance(1,(verbInstanceNet.getSize()/40)+1,
      verbInstanceNet);
    oneControlStartsOneVerbInstance(2,0,verbInstanceNet);
    oneControlStopsInstance(3,(verbInstanceNet.getSize()/40)+1,
      verbInstanceNet);
  }

  public void connectV2ToInstance(CABot2Net instanceNet) {
    for (int fromNeuron=0; fromNeuron<getSize();fromNeuron++) 
      {
      //if it is position invariant
      if (fromNeuron%10 == 1)
        for (int synapseNum=0;synapseNum < 10; synapseNum++) {
          int toNeuron = ((int)(CANT23.random.nextFloat()*instanceNet.getSize()));
          if (neurons[fromNeuron].isInhibitory())
            neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron],
              -0.01);
          else
            neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron],
              0.01);
          }
      }
  }

  /**** stuff other than connectivity**/
  //reset the word networks for the next word.  This might be cheating.
  private void resetInputNet () {
    Enumeration eNum = CANT23.nets.elements();
	
    while (eNum.hasMoreElements()) {
      CABot2Net net = (CABot2Net)eNum.nextElement();
      if ((net.getName().compareTo("BaseNet") ==0))
        net.clear();
    }  
  }

  //---Functions for reading the bitmap for visual input----------
  //get the pattern using readNewPattern  
  //and substitute
  //---Functions for reading the bitmap for visual input----------
  //get the pattern using readNewPattern
  //and substitute
  private void modifyPattern(int patternNumber) {
    //patterns aren't actually patterns but a vector of CANTPatterns
    //we're going to get the CANTPattern at patternNumber
    CANTPattern newPattern = readNewPattern();
    //newPattern.print();
  	
    //and re setElementAt
    patterns.setElementAt(newPattern,patternNumber);
  }

  //---Functions for reading the bitmap for visual input----------
  //get the pattern using readNewPattern
  //and substitute
  private void modifyPattern(int patternNumber,String fileName) {
    //patterns aren't actually patterns but a vector of CANTPatterns
    //we're going to get the CANTPattern at patternNumber
    CANTPattern newPattern = readNewPattern(fileName);
    //newPattern.print();
  	
    //and re setElementAt
    patterns.setElementAt(newPattern,patternNumber);
  }

  private void modifyPattern(int patternNumber, int fileNum) {
  	//patterns aren't actually patterns but a vector of CANTPatterns
    //we're going to get the CANTPattern at patternNumber
    
    //oscillate between pyramids and stalagtites
    String fileName ="pyramid";
    if (fileNum%2 == 1)
      fileName ="stalagtite";
    fileNum = (fileNum/2) + 1;
    fileName = fileName.concat(Integer.toString(fileNum));
    fileName = fileName.concat(".jpg");


    CANTPattern newPattern = readNewPattern(fileName);
    //newPattern.print();
  	
    //and re setElementAt
    try {
      patterns.setElementAt(newPattern,patternNumber);
    }
    catch (ArrayIndexOutOfBoundsException e) {
     System.err.println("problem setting pattern element in modify pattern\n" +
                         e.toString());
    }
  }

  private CANTPattern readNewPattern() {
    CANTPattern readPattern;
    int [] patternPoints = new int[2500];
    int cPoints=0;
    int number;

    CABot2.bmpReader.readPicture();
    cPoints = CABot2.bmpReader.getPictureBits(50,patternPoints);
  	
    //System.out.println(" read New Pattern " + getTotalPatterns());
    readPattern = new CANTPattern(this,"scene",1,cPoints,patternPoints);
    //readPattern.print();
    return readPattern;
  }
  
  private CANTPattern readNewPattern(String fileName) {
    CANTPattern readPattern;
    int [] patternPoints = new int[2500];
    int cPoints=0;
    int number;

    CABot2.bmpReader.readPicture(fileName);
    cPoints = CABot2.bmpReader.getPictureBits(50,patternPoints);
  	
    //System.out.println(" read New Pattern " + getTotalPatterns());
    readPattern = new CANTPattern(this,"scene",1,cPoints,patternPoints);
    //readPattern.print();
    return readPattern;
  }
  
  public void setNextWord(int wordNumber) {
    if (getName().compareTo("BaseNet") != 0)
      return;
    currentWord = wordNumber;  
    if (currentWord != -1 ) 
      {
      resetInputNet();
      }
    else 
      {
      System.out.println("Last Word Read " + CANT23.CANTStep);
      setNeuronsToStimulate(0);
      }
  }
  
  private String visualInputFileName = "";
  public void setVisualInputFile(String fileName) {
    visualInputFileName = fileName;
  }

  private boolean inputFromJPG = true;
  public void changePattern(int cantStep)
  {
    if (getName().compareTo("VisualInputNet") == 0) 
      {
      if (inputFromJPG)
        {
        CABot2Experiment exp = (CABot2Experiment)CABot2.experiment;
        if (exp.runWithCrystalSpace) {
          if (exp.readNewVisualScene)
            modifyPattern(1,visualInputFileName);
        }
        else if ((cantStep%50)==0)
          modifyPattern(1,cantStep/50);	
        setCurrentPattern(1);
        }
      else 
      setCurrentPattern(1);
      }
    else if (getName().compareTo("ControlNet") == 0) 
      {
      if (getCurrentPattern() < 0) setCurrentPattern(0);
      else setCurrentPattern(getCurrentPattern());
      }
    else if (getName().compareTo("BaseNet") == 0)
      return;//setInputWord();
    else if (getName().compareTo("BarOneNet") == 0)
      return;//this can be removed when we make newinstance selection neural
    else if (getName().compareTo("VerbInstanceNet") == 0)
      return;//this can be removed when we make newinstance selection neural
    else if (getName().compareTo("NounInstanceNet") == 0)
      return;//this can be removed when we make newinstance selection neural
    else   
      setCurrentPattern(0);
    return;
  }

  public void kludge () {
    System.out.println("CABot 2 kludge ");

    Enumeration eNum = CANT23.nets.elements();
    CABot2Net tNet= (CABot2Net)eNum.nextElement();
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CANTNet net = (CANTNet)eNum.nextElement();
      if (net.getName().compareTo("ControlNet") == 0)
        tNet = (CABot2Net) net;
      }
    for (int i =40;i < 50;i+=1)
      System.out.println(i+ "test " + tNet.neurons[i].getActivation());

    //CABot2.experiment.writeGoalLearnResults();
    //CABot2.experiment.writeGoal2Module2Weights();

  }
  
  public void measure(int currentStep) {
    System.out.println("measure " + neurons[0].getActivation() + " " + 
      neurons[0].getFired() + " " + 
	  currentStep);
  }
}