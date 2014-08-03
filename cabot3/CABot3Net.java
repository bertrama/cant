import java.io.*;
import java.util.*;

public class CABot3Net extends CANTNet {
  private int currentWord = 0;

  public CABot3Net() {
  }

  public CABot3Net(String name, int cols, int rows, int topology) {
    this.cols = cols;
    this.rows = rows;
    setName(name);
    this.topology = topology;
    netFileName = name + ".dat";
    setRecordingActivation(false);
    // super(name,cols,rows,topology);
    cyclesToStimulatePerRun = 1000;
  }

  public void createNeurons() {
    if (topology == 1) {
      createNeurons(50);
    } else if (topology == 2) {
      createNeurons(100);
    } else if (topology == 3) {
      createNeurons(60);
    } else if (topology == 4) {
      createNeurons(50);
    } else if (topology == 5) {
      createNeurons(180);
    } else if (topology == 6) {
	//createNeurons(760);
      createNounInstanceNeurons(140);
    } else if (topology == 7) {
	//createNeurons(500);
      createVerbInstanceNeurons(105);
    } else if (topology == 8) {
      createNeurons(35);
    } else if (topology == 9) {
      createNeurons(70);
    } else if (topology == 10) {
      createNeurons(90);
    } else if (topology == 11) {
      createNeurons(100);
    } else if (topology == 12) {
      createNeurons(100);
    } else if (topology == 13) {
      createNeurons(81);
    } else if (topology == 14) {
     createNeurons(500);  //undone
    } else if (topology == 15) {
      createNeurons(2000);
    } else if (topology == 16) {
      createNeurons(25);
    } else if (topology == 17) {
      createNeurons(200);
    } else if (topology == 18) {
      createNeurons(110);
    } else if (topology == 19) {
      createNeurons(380);
    } else if (topology == 20) {
      createNeurons(100);
    } else if (topology == 21) {
      createNeurons(280);
    } else if (topology == 22) {
      createNeurons(90);
    } else if (topology == 23) {
      createNeurons(90);
    } else if (topology == 24) {
      createNeurons(120);
    } else if (topology == 25) {
      createNeurons(80);
    } else if (topology == 26) {
      createNeurons(180);
      setRuleTwoTopology();
    } else if (topology == 27) {
      createNeurons(20);
      setInstanceCounterTopology();
    } else if (topology == 28) {
      createNeurons(10);
      setNextWordTopology();
    } else if (topology == 30) { // kailash
      createNeurons(200);
    } else if (topology == 31) {
      createNeurons(30);
    } else if (topology == 32) { // room2
      createNeurons(200);
    } else
      System.out.println("bad toppology specified " + topology);
  }

  // set all fast bind neuron weights to .01
  public void resetBindings() {
    for (int neuronIndex = 0; neuronIndex < size(); neuronIndex++) {
      if (neurons[neuronIndex] instanceof CANTNeuronFastBind) {
        for (int synapse = 0; synapse < neurons[neuronIndex].
          getCurrentSynapses(); synapse++) {
          neurons[neuronIndex].synapses[synapse].setWeight(.01);
        }
      }
    }
  }

  // ***Parsing patterns
  private int numNouns = 15;
  private int numVerbs = 9;
  private int numWords = 35;

  // ------------------------Set up patterns for the input words
  private void makeNewWordPattern(int word) {
    CANTPattern readPattern;
    int[] patternPoints = new int[100];
    int cPoints = 0;

    // make the pattern
    for (int i = 0; i < 100; i++) {
      patternPoints[i] = i + (word * 100);
    }
    readPattern = new CANTPattern(this, "word", word, 100, patternPoints);

    // stick the pattern into the pattern vector
    try {
      patterns.add(readPattern);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.err.println("problem setting pattern element in modify pattern\n"
          + e.toString());
    }
  }

  public void setInputPatterns() {
    for (int word = 0; word < numWords; word++) {
      makeNewWordPattern(word);
    }
  }

  public void setExternalActivation(int cantStep) {
    if (getName().compareTo("BaseNet") == 0) {
      int wordToStimulate = CABot3.experiment.currentWord;
      if (wordToStimulate == -1)
        return;
      CANTPattern pattern = (CANTPattern) patterns.get(wordToStimulate);
      int neuronsToStimulate = getNeuronsToStimulate();
      for (int i = 0; i < neuronsToStimulate; i++) {
        int neuronNumber = pattern.getPatternIndex(i);
        double theta = getActivationThreshold();
        neurons[neuronNumber].setActivation(theta
            + (CANT23.random.nextFloat() * theta));
      }
    } else if ((getName().compareTo("BarOneNet") == 0)                         
        || (getName().compareTo("CounterNet") == 0)
        || (getName().compareTo("ControlNet") == 0)
        || (getName().compareTo("VisualInputNet") == 0)
        || (getName().compareTo("InstanceCounterNet") == 0)){

      int curPatt = getCurrentPattern();
      if (curPatt < 0)
        curPatt = 0;
      CANTPattern pattern = (CANTPattern) patterns.get(curPatt);
      for (int i = 0; i < getNeuronsToStimulate(); i++) {
        if (i == pattern.size())
          return;
        int neuronNumber = pattern.getPatternIndex(i);
        double theta = getActivationThreshold();
        neurons[neuronNumber].setActivation(theta + 1);
      }
    }
  }

  // hook to call fast bind learning
  private void fastLearn() {
    for (int neuronIndex = 0; neuronIndex < size(); neuronIndex++) {
      if (neurons[neuronIndex] instanceof CANTNeuronFastBind) {
        ((CANTNeuronFastBind) neurons[neuronIndex]).fastLearn();
      }
    }
  }

  public void subclassLearn() {
    if (getName().compareTo("Goal2Net") != 0)
      return;

    for (int neuronNum = 0; neuronNum < getSize(); neuronNum++) {
      if (neurons[neuronNum].getFired()) { // only learn if it fires
        for (int synapseNum = 0; synapseNum < neurons[neuronNum]
                                                      .getCurrentSynapses(); synapseNum++) {
          // only learn if it's from fact to module
          CANTNeuron toNeuron = neurons[neuronNum].synapses[synapseNum].toNeuron;
          if (toNeuron.parentNet.getName().compareTo("Module2Net") == 0) {
            // modify weight
            neurons[neuronNum].modifySynapticWeight(synapseNum);
          }
        }
      }
    }
  }

  // *******Parsing Topology
  private int inputCASize = 100;

  // A subnet with every fifth neuron inhibitory. No intra-subnet connections.
  private void setInputTopology() {
    int CASize = inputCASize;
    int totalCAs = getSize() / CASize;
    for (int CA = 0; CA < totalCAs; CA++) {
      for (int neuronInCA = 0; neuronInCA < CASize; neuronInCA++) {
        int fromNeuron = neuronInCA + (CA * CASize);

        // every fifth CA is inhibitory
        if ((neuronInCA % 5) == 0) {
          neurons[fromNeuron].setInhibitory(true);
        } else // not inhibitory
        {
          neurons[fromNeuron].setInhibitory(false);
        }
      }
    }
  }

  private void setFiftyFiftySubCA(double weight, int fromNeuron, int toOffset) {
    for (int synapse = 0; synapse < 5; synapse++) {
      // synapse to each of the other 5 neurons on the other side of the
      // simpleCA
      int toNeuron = toOffset + synapse;
      if ((fromNeuron % 10) < 5)
        toNeuron += 5;
      addConnection(fromNeuron, toNeuron, weight);
    }
  }

  // this creates a topology where the network is broken into subCAs of length
  // featureSize; when one half is activated it will oscillate between the two.
  // it makes all neurons excitatory.
  private void setFiftyFiftyTopology(double weight) {
    int featureSize = 10;
    int simpleCAs = getSize() / featureSize;
    for (int simpleCA = 0; simpleCA < simpleCAs; simpleCA++) {
      for (int neuron = 0; neuron < featureSize; neuron++) {
        int fromNeuron = (simpleCA * featureSize) + neuron;
        neurons[fromNeuron].setInhibitory(false);
        setFiftyFiftySubCA(weight, fromNeuron, simpleCA * featureSize);
      }
    }
  }

  // this creates a topology where the network is broken into subCAs of length
  // featureSize; when one half is activated it will oscillate between the two.
  // It makes 80% of neurons excitatory.
  private void setFiftyFiftyTopology2(double weight) {
    int featureSize = 10;
    int simpleCAs = getSize() / featureSize;
    for (int simpleCA = 0; simpleCA < simpleCAs; simpleCA++) {
      for (int neuron = 0; neuron < featureSize; neuron++) {
        int fromNeuron = (simpleCA * featureSize) + neuron;
        if ((neuron % 5) == 0) {
          // no inhibitory connections.
          neurons[fromNeuron].setInhibitory(true);
        } else {
          neurons[fromNeuron].setInhibitory(false);
          setFiftyFiftySubCA(weight, fromNeuron, simpleCA * featureSize);
        }
      }
    }
  }

  // Of the 20 neuron feature sets they are 4NI4FI4FI4NI. The Ns support
  // the running once 1 set is activated.
  private void setFiftyFiftyBindSubCA(int fromNeuron, int toOffset) {
    double weight = 1.1;
    if ((fromNeuron % 20) < 5) {
      for (int synapse = 0; synapse < 5; synapse++) {
        // synapse to each of the other 5 neurons on the other side of the
        // simpleCA
        int toNeuron = toOffset + synapse + 5;
        addConnection(fromNeuron, toNeuron, weight);
        toNeuron += 10;
        addConnection(fromNeuron, toNeuron, weight);
      }
    } else if ((fromNeuron % 20) > 15) {
      for (int synapse = 0; synapse < 5; synapse++) {
        // synapse to each of the other 5 neurons on the other side of the
        // simpleCA
        int toNeuron = toOffset + synapse;
        addConnection(fromNeuron, toNeuron, weight);
        toNeuron -= 10;
        addConnection(fromNeuron, toNeuron, weight);
      }
    }
  }

  // Two CAs wordActive, and barOneActive.
  // All excitatory neurons. Two 50/50 CAs so half are always on.
  private int wordActiveSize = 100;

  private void setBarOneTopology() {
    setFiftyFiftyTopology2(1.1);
  }

  // Break it into CAs that are sized 100, with the idea that each will
  // 50/50 persist as barone
  // Connections from input (CA specific 4 at .45 on each cycle) and barOne
  // (general 2 in each cycle at .0) ignite a particular Noun Access CA.
  // With decay 2 neither is sufficient alone, and the 50/50 persist keeps
  // half on with 0,1,or both input and barOne CAs on.
  private void setNounAccessTopology() {
    setFiftyFiftyTopology(0.9);
  }

  // Every fifth neuron is inhibitory.
  // This is a simple structure of 50/50 CAs. They're piecewise in portions
  // of 1.
  private int nounFeatureSize = 10;

  private void setSemTopology() {
    setFiftyFiftyTopology2(1.1);
  }

  private int ruleSize = 100;

  private void ruleInhibitsRule(int inhibittingRule, int inhibittedRule) {
    int inhibittingStart = inhibittingRule * ruleSize;
    for (int i = 0; i < ruleSize; i += 5) {
      int fromNeuron = i + (inhibittingRule * ruleSize);
      for (int synapse = 0; synapse < 5; synapse++) {
        int toNeuron = i + synapse + (inhibittedRule * ruleSize);
        addConnection(fromNeuron, toNeuron, 1.0); // this is inhibitory
      }
    }
  }

  private void ruleInhibitsRule(int inhibittingRule, int inhibittedRule,
      double val) {
    int inhibittingStart = inhibittingRule * ruleSize;
    for (int i = 0; i < ruleSize; i += 5) {
      int fromNeuron = i + (inhibittingRule * ruleSize);
      for (int synapse = 0; synapse < 5; synapse++) {
        int toNeuron = i + synapse + (inhibittedRule * ruleSize);
        addConnection(fromNeuron, toNeuron, val); // this is inhibitory
      }
    }
  }

  private void prepDoneStartsReadNext() {
    for (int i = 0; i < ruleSize; i++) {
      int fromNeuron = 1000 + i;
      int toNeuron = i + 300;
      if ((i % 10) > 4)
        toNeuron -= 5;
      if ((i % 5) != 0) {
        addConnection(fromNeuron, toNeuron, 2.1);
        if ((i % 5) == 1)
          addConnection(fromNeuron, toNeuron - 1, 2.1);
      }
    }
  }

  // Every fifth neuron is inhibitory.
  // This is a simple structure of 50/50 CAs. They're piecewise in portions
  // of 10.
  private int ruleFeatureSize = 10;

  private void setRuleOneTopology() {
    setFiftyFiftyTopology2(1.1);
    // undone changeFiftyFiftyTopology(1.15,10,20);
    // simple NP
    ruleInhibitsRule(0, 1); // NPInst -( NPFromN
    ruleInhibitsRule(0, 2); // NPInst -( NPDone
    ruleInhibitsRule(1, 2, 0.6); // NPFromN -( NPDone lesser weight for timing
    ruleInhibitsRule(2, 1); // NPDone -( NPFromN
    // simple VP
    // ruleInhibitsRule(4,5); //VPInst -( MainV
    ruleInhibitsRule(4, 6); // VPInst -( VPDone
    ruleInhibitsRule(5, 6); // MainV -( VPDone
    ruleInhibitsRule(6, 5); // VPDone -( MainV
    // kill read word
    ruleInhibitsRule(0, 3); // NPInst -( Read Word
    ruleInhibitsRule(1, 3); // NP FromN -( Read Word
    ruleInhibitsRule(4, 3); // VPInst -( Read Word
    ruleInhibitsRule(8, 3); // AddAdj -( Read Word
    ruleInhibitsRule(11, 3); // AddDet -( Read Word
    // Preps
    ruleInhibitsRule(9, 10); // NPAddPrep -( PrepDone
    ruleInhibitsRule(10, 9); // PrepDone -( NPAddPrep
    ruleInhibitsRule(3, 10); // Read Word -( Prep Done

    ruleInhibitsRule(10, 2); // PrepDone -( NPDone

    prepDoneStartsReadNext();

    //adj 
    ruleInhibitsRule(8, 11); // Add Adj -( Add Det
  }

  // 0-39 are base neurons
  // 40-59 is the done feature
  // Every 5th is normal inhibitory
  // The rest are fastbind features that are
  // 20 neuron feature sets they are 4NI4FI4FI4NI
  private boolean nounInstanceNeuronFastBind(int neuron) {
    if (neuron < 80)
      return false;
    if (neuron >= 260)
      return false;
    if ((neuron % 5) == 0)
      return false;
    if ((neuron % 20) < 5)
      return false;
    if ((neuron % 20) > 15)
      return false;
    return true;
  }

  // There are 8 features for a NP: prepon, main-noun, prepbind, det, adj1,
  // adj2, ppmod,relclause; this is incomplete but that's what we're doing now.
  // The 9th feature is not for binding but is on if its done.
  // This gets turned on when the NP is done and ensures the barone features
  // are off. Add an extra feature that binds to the rest to store which
  // are on.
  // We'll do it with a base instance (40 neurons) + 20 for each of the 10
  // features.
  private int nInstCASize = 500;

  private void createNounInstanceNeurons(int synapses) {
    neurons = new CANTNeuron[cols * rows];
    for (int CA = 0; CA < getSize() / nInstCASize; CA++) {
      for (int i = 0; i < nInstCASize; i++) {
        if (nounInstanceNeuronFastBind(i))
          neurons[i + (CA * nInstCASize)] =
            //new CANTNeuronFastBind(totalNeurons++, this, 0.0005, 750);
            new CANTNeuronFastBind(totalNeurons++, this, 0.001, 760);
        else {
          neurons[i + (CA * nInstCASize)] = new CANTNeuron(totalNeurons++,
              this, synapses);
          if (((i % 5) == 0) && (i < 300))
            neurons[i + (CA * nInstCASize)].setInhibitory(true);
          else
            neurons[i + (CA * nInstCASize)].setInhibitory(false);
        }
      }
    }
  }

  // used for noun instance
  // first 40 are all excitatory, second 20 are 50/50 with 20% inhibitory
  // rest are fastbind features
  private void setNounFeaturesInstanceTopology() {
    int CASize = nInstCASize;
    int featureSize = 10;
    int CAs = getSize() / CASize;
    for (int CA = 0; CA < CAs; CA++) {
      // set up the features with no inhibitory neurons
      for (int feature = 0; feature < 4; feature++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
          int fromNeuron = (CA * CASize) + (feature * featureSize) + neuron;
          neurons[fromNeuron].setInhibitory(false);
          setFiftyFiftySubCA(0.9, fromNeuron, (CA * CASize)
              + (feature * featureSize));
        }
      }
      // set up the features 50/50 with inhibitory neurons
      for (int feature = 4; feature < 8; feature++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
          int fromNeuron = (CA * CASize) + (feature * featureSize) + neuron;
          if ((neuron % 5) == 0) {
            // no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
          } else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftySubCA(1.1, fromNeuron, (CA * CASize)
                + (feature * featureSize));
          }
        }
      }
      // set up the fastbind features
      for (int feature = 8; feature < 26; feature++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
          int fromNeuron = (CA * CASize) + (feature * featureSize) + neuron;
          if ((neuron % 5) == 0) {
            // no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
          } else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftyBindSubCA(fromNeuron, (CA * CASize)
                + (feature * featureSize));
          }
        }
      }
      // set up the features 50/50 with inhibitory neurons
      for (int feature = 26; feature < 30; feature++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
          int fromNeuron = (CA * CASize) + (feature * featureSize) + neuron;
          if ((neuron % 5) == 0) {
            // no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
          } else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftySubCA(1.1, fromNeuron, (CA * CASize)
                + (feature * featureSize));
          }
        }
      }
    }
  }

  private int nounInstanceTimeStart = 300;

  // This component shows how long the instance has been running by
  // the number of neurons firing. The fewer the longer.
  // There are groups of 8 neurons that go off together.
  private void setNounInstanceTimeTopology() {
    double bigWeight = 2.4;
    double weight2 = 0.8;
    for (int CA = 0; CA < 15; CA++) {
      int offset = 300 + (CA * nInstCASize);
      for (int neuronA = 0; neuronA < 40; neuronA++) {
        int neuronB = neuronA + 40;
        int neuronC = neuronA + 80;
        int neuronD = neuronA + 120;

        int neuronA2 = neuronA;
        if ((neuronA2 % 2) == 1)
          neuronA2 -= 1;
        else
          neuronA2 += 1;
        int neuronB2 = neuronA2 + 40;
        int neuronC2 = neuronA2 + 80;
        int neuronD2 = neuronA2 + 120;
        double weight1 = bigWeight + (neuronA / 2) * .05;
        addConnection(neuronA + offset, neuronB + offset, weight1);
        addConnection(neuronB + offset, neuronC + offset, weight1);
        addConnection(neuronC + offset, neuronD + offset, weight1);
        addConnection(neuronD + offset, neuronA + offset, weight1);
        addConnection(neuronA + offset, neuronA2 + offset, weight2);
        addConnection(neuronA + offset, neuronC + offset, weight2);
        addConnection(neuronA + offset, neuronD + offset, weight2);
        addConnection(neuronB + offset, neuronA + offset, weight2);
        addConnection(neuronB + offset, neuronB2 + offset, weight2);
        addConnection(neuronB + offset, neuronD + offset, weight2);
        addConnection(neuronC + offset, neuronA + offset, weight2);
        addConnection(neuronC + offset, neuronB + offset, weight2);
        addConnection(neuronC + offset, neuronC2 + offset, weight2);
        addConnection(neuronD + offset, neuronB + offset, weight2);
        addConnection(neuronD + offset, neuronC + offset, weight2);
        addConnection(neuronD + offset, neuronD2 + offset, weight2);
      }
    }
  }

  // The base of each word connects to its features so that they get
  // half the strength necessary to ignite.
  // Each neuron (including the fastbind neurons) synapse to all the
  // 5 neurons in their group. So each feature neuron has 8 normal and
  // 3 fastbind inputs. 8/1.2 = 0.15
  private void connectNounInstanceBaseToFeatures() {
    for (int CA = 0; CA < getSize() / nInstCASize; CA++) {
      for (int baseNeuron = 0; baseNeuron < 40; baseNeuron++) {
        for (int feature = 0; feature < 9; feature++) {
          if (feature != 7) {
            int fromNeuron = baseNeuron + (CA * nInstCASize);
            for (int synapse = 0; synapse < 5; synapse++) {
              int toNeuron = ((baseNeuron / 5) * 5) % 20; // 0,5,10,15
              toNeuron += synapse + 40 + (feature * 20);
              toNeuron += CA * nInstCASize;
              if (feature == 2)
                addConnection(fromNeuron, toNeuron, 0.3);
              else
                addConnection(fromNeuron, toNeuron, 0.15);
            }
          }
        }
      }
    }
  }

  // The others on needs to have connections to all the other features
  // (except np done, and prepon) so it can bind to them and turn them
  // on during printResults
  private void connectNIOthersOnToOthers() {
    for (int CA = 0; CA < getSize() / nInstCASize; CA++) {
      for (int baseNeuron = 80; baseNeuron < 100; baseNeuron++) {
        for (int feature = 0; feature < 6; feature++) {
          if (feature != 4) {
            int fromNeuron = (CA * nInstCASize) + baseNeuron;
            for (int synapse = 0; synapse < 10; synapse++) {
              int toNeuron = ((baseNeuron / 10) * 10) % 20; // 0,10
              toNeuron += synapse + 100 + (feature * 20) + (CA * nInstCASize);
              addConnection(fromNeuron, toNeuron, 0.01);
            }
          }
        }
      }
    }
  }

  // Bind connects to the base to turn it on when
  private void connectBindToBase() {
    for (int CA = 0; CA < getSize() / nInstCASize; CA++) {
      for (int baseNeuron = 266; baseNeuron < 280; baseNeuron++) {
        if (baseNeuron == 270) baseNeuron=276;
        for (int feature = 0; feature < 2; feature++) {
          int fromNeuron = (CA * nInstCASize) + baseNeuron;
          if (!neurons[fromNeuron].isInhibitory()) {
            for (int synapse = 0; synapse < 5; synapse++) {
              int toNeuron = ((baseNeuron / 10) * 10) % 20; // 0,10
              toNeuron += synapse + (feature * 20) + (CA * nInstCASize)+5;
              addConnection(fromNeuron, toNeuron, 1.001);
            }
          }
        }
      }
    }
  }

  // The bound slot turns off the bind slot so no one else can bind to it.
  private void connectBoundToBind() {
    for (int CA = 0; CA < getSize() / nInstCASize; CA++) {
      for (int baseNeuron = 280; baseNeuron < 300; baseNeuron++) {
        int fromNeuron = (CA * nInstCASize) + baseNeuron;
        if (neurons[fromNeuron].isInhibitory()) {
          for (int synapse = 0; synapse < 5; synapse++) {
            int toNeuron = (baseNeuron - 20) + synapse;
            toNeuron += (CA * nInstCASize);
            addConnection(fromNeuron, toNeuron, 4.0);
          }
        }
      }
    }
  }

  private void nounInstanceDoneExtinguishBarOneFeatures() {
    for (int CA = 0; CA < getSize() / nInstCASize; CA++) {
      for (int doneNeuron = 40; doneNeuron < 60; doneNeuron += 5) {
        for (int feature = 0; feature < 5; feature++) { // the 1st 5 are barone
          // feats
          int fromNeuron = doneNeuron + (CA * nInstCASize);
          for (int synapse = 0; synapse < 10; synapse++) {
            int toNeuron = ((doneNeuron / 5) * 5) % 20; // 0,5,10,15
            toNeuron += synapse + 100 + (feature * 20);
            toNeuron += CA * nInstCASize;
            addConnection(fromNeuron, toNeuron, 2.0);
          }
        }
      }
    }
  }

  private void prepBindPrimePrepOn() {
    for (int CA = 0; CA < getSize() / nInstCASize; CA++) {
      for (int neuron = 100; neuron < 120; neuron++) {
        if (neuron == 105)
          neuron = 116;
        int fromNeuron = neuron + (CA * nInstCASize);
        if (!neurons[fromNeuron].isInhibitory()) {
          int toNeuron = fromNeuron - 40;
          addConnection(fromNeuron, toNeuron, 0.5);
          if ((fromNeuron % 5) == 1)
            addConnection(fromNeuron, toNeuron - 1, 0.5);
        }
      }
    }
  }

  private void prepOnExtinguishPrepBind() {
    for (int CA = 0; CA < getSize() / nInstCASize; CA++) {
      for (int doneNeuron = 60; doneNeuron < 80; doneNeuron += 5) {
        int fromNeuron = doneNeuron + (CA * nInstCASize);
        for (int synapse = 0; synapse < 10; synapse++) {
          int toNeuron = ((doneNeuron / 5) * 5) % 20; // 0,5,10,15
          toNeuron += synapse + 100; // 100 is the prep bind feature offset
          toNeuron += CA * nInstCASize;
          addConnection(fromNeuron, toNeuron, 2.0);
        }
      }
    }
  }

  private void connectAPPModToAnInstance(int fromInst, int toInst) {
    for (int baseNeuron = 206; baseNeuron < 215; baseNeuron++) {
      int fromNeuron = (fromInst * nInstCASize) + baseNeuron;
      if (!neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 10; synapse++) {
          int toNeuron = synapse;
          if (synapse >= 5)
            toNeuron += 5;
          toNeuron += (toInst * nInstCASize) + 260;
          addConnection(fromNeuron, toNeuron, 0.02);
        }
      }
    }
  }

  private void connectPPModToInstances() {
    connectAPPModToAnInstance(0, 1);
    connectAPPModToAnInstance(1, 2);
  }
  private void connectAdjFeatureToAdjDoneFeature(double weight) {
    for (int nInstCA = 0; nInstCA < nounInstanceCAs; nInstCA++) {
      for (int i = 0; i< 4; i++) {
        int fromNeuron = (nInstCA*nInstCASize)+161+i;
        for (int synapse = 0; synapse < 3; synapse++) {
          int toNeuron = (i*3) + synapse;
          if (toNeuron < 10) {
            toNeuron += (nInstCA*nInstCASize)+180;
            addConnection(fromNeuron, toNeuron, weight);
          }
        }
        fromNeuron = (nInstCA*nInstCASize)+176+i;
        for (int synapse = 0; synapse < 3; synapse++) {
          int toNeuron = (i*3) + synapse;
          if (toNeuron < 10) {
            toNeuron += (nInstCA*nInstCASize)+190;
            addConnection(fromNeuron, toNeuron, weight);
          }
        }
      }
    }
  }
  private void connectAdjDoneFeatureToAdjFeature(double weight) {
    for (int nInstCA = 0; nInstCA < nounInstanceCAs; nInstCA++) {
      int fromNeuron = (nInstCA*nInstCASize)+180;
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = (fromNeuron-20)+ synapse;
        addConnection(fromNeuron, toNeuron, weight);
      }
      fromNeuron = (nInstCA*nInstCASize)+195;
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = (fromNeuron-25)+ synapse;
        addConnection(fromNeuron, toNeuron, weight);
      }
    }
  }
  //adj (160-179) binds to the adj.  It slowly turns on adj done (180-199),
  //which in turn shuts it down.
  private void connectAdjFeatures() {
    connectAdjFeatureToAdjDoneFeature(2.3);
    connectAdjDoneFeatureToAdjFeature(4.1);
  }

  private int nounInstanceCAs=15;
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
    connectAdjFeatures();
  }

  // For a CA the first 120 are normal
  // The remaining 180 are in fast bind feature sets of 20 neurons.
  // Every 5th is normal inhibitory.
  // Of the 20 neuron feature sets they are 4NI4FI4FI4NI
  private boolean verbInstanceNeuronFastBind(int neuron) {
    neuron = neuron%500;
    if (neuron < 120)
      return false;
    if (neuron >= verbInstanceTimeStart)
      return false;
    if ((neuron % 5) == 0)
      return false;
    if ((neuron % 20) < 5)
      return false;
    if ((neuron % 20) > 15)
      return false;
    return true;
  }

  private int vInstCASize = 500;

  private void createVerbInstanceNeurons(int synapses) {
    neurons = new CANTNeuron[cols * rows];
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int i = 0; i < vInstCASize; i++) {
        if (verbInstanceNeuronFastBind(i))
          neurons[i + (CA * vInstCASize)] = new CANTNeuronFastBind(
              totalNeurons++, this, 0.0005);
        else {
          neurons[i + (CA * vInstCASize)] = new CANTNeuron(totalNeurons++,
              this, synapses);
          if (((i % 5) == 0) && (i < verbInstanceTimeStart))
            neurons[i + (CA * vInstCASize)].setInhibitory(true);
          else
            neurons[i + (CA * vInstCASize)].setInhibitory(false);
        }
      }
    }
  }

  // First 40 are all excitatory (base); features are 20 long
  // Next 4 features are 50/50 with 20% inhibitory
  // Rest are fastbind features
  private void setVerbFeaturesInstanceTopology() {
    int CASize = vInstCASize;
    int featureSize = 10;
    for (int CA = 0; CA < verbInstanceCAs; CA++) {
      // set up the features with no inhibitory neurons
      for (int feature = 0; feature < 4; feature++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
          int fromNeuron = (CA * CASize) + (feature * featureSize) + neuron;
          neurons[fromNeuron].setInhibitory(false);
          setFiftyFiftySubCA(0.9, fromNeuron, (CA * CASize)
              + (feature * featureSize));
        }
      }
      // set up the features with inhibitory neurons
      for (int feature = 4; feature < 12; feature++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
          int fromNeuron = (CA * CASize) + (feature * featureSize) + neuron;
          if ((neuron % 5) == 0) {
            // no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
          } else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftySubCA(1.1, fromNeuron, (CA * CASize)
                + (feature * featureSize));
          }
        }
      }

      // set up the fastbind features
      for (int feature = 12; feature < 26; feature++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
          int fromNeuron = (CA * CASize) + (feature * featureSize) + neuron;
          if ((neuron % 5) == 0) {
            // no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
          } else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftyBindSubCA(fromNeuron, (CA * CASize)
                + (feature * featureSize));
          }
        }
      }

      // set up the features with inhibitory neurons
      for (int feature = 26; feature < 28; feature++) {
        for (int neuron = 0; neuron < featureSize; neuron++) {
          int fromNeuron = (CA * CASize) + (feature * featureSize) + neuron;
          if ((neuron % 5) == 0) {
            // no inhibitory connections.
            neurons[fromNeuron].setInhibitory(true);
          } else {
            neurons[fromNeuron].setInhibitory(false);
            setFiftyFiftySubCA(1.1, fromNeuron, (CA * CASize)
                + (feature * featureSize));
          }
        }
      }

    }
  }

  private int verbInstanceTimeStart = 300;

  // This component shows how long the instance has been running by
  // the number of neurons firing. The fewer the longer.
  // There are groups of 8 neurons that go off together.
  private void setVerbInstanceTimeTopology() {
    double bigWeight = 2.4;
    double weight2 = 0.8;
    for (int CA = 0; CA < 5; CA++) {
      int offset = verbInstanceTimeStart + (CA * vInstCASize);
      for (int neuronA = 0; neuronA < 40; neuronA++) {
        int neuronB = neuronA + 40;
        int neuronC = neuronA + 80;
        int neuronD = neuronA + 120;

        int neuronA2 = neuronA;
        if ((neuronA2 % 2) == 1)
          neuronA2 -= 1;
        else
          neuronA2 += 1;
        int neuronB2 = neuronA2 + 40;
        int neuronC2 = neuronA2 + 80;
        int neuronD2 = neuronA2 + 120;
        double weight1 = bigWeight + (neuronA / 2) * .05;
        addConnection(neuronA + offset, neuronB + offset, weight1);
        addConnection(neuronB + offset, neuronC + offset, weight1);
        addConnection(neuronC + offset, neuronD + offset, weight1);
        addConnection(neuronD + offset, neuronA + offset, weight1);
        addConnection(neuronA + offset, neuronA2 + offset, weight2);
        addConnection(neuronA + offset, neuronC + offset, weight2);
        addConnection(neuronA + offset, neuronD + offset, weight2);
        addConnection(neuronB + offset, neuronA + offset, weight2);
        addConnection(neuronB + offset, neuronB2 + offset, weight2);
        addConnection(neuronB + offset, neuronD + offset, weight2);
        addConnection(neuronC + offset, neuronA + offset, weight2);
        addConnection(neuronC + offset, neuronB + offset, weight2);
        addConnection(neuronC + offset, neuronC2 + offset, weight2);
        addConnection(neuronD + offset, neuronB + offset, weight2);
        addConnection(neuronD + offset, neuronC + offset, weight2);
        addConnection(neuronD + offset, neuronD2 + offset, weight2);
      }
    }
  }

  private void connectVerbInstanceBaseToFeatures() {
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int baseNeuron = 0; baseNeuron < 40; baseNeuron++) {
        for (int feature = 0; feature < 13; feature++) {
          int fromNeuron = baseNeuron + (CA * vInstCASize);
          for (int synapse = 0; synapse < 5; synapse++) {
            int toNeuron = ((baseNeuron / 5) * 5) % 20; // 0,5,10,15
            toNeuron += synapse + 40 + (feature * 20);
            toNeuron += (CA * vInstCASize);
            if (feature == 5)
              addConnection(fromNeuron, toNeuron, 0.30);
            // skip obj, loc, inst and act done
            else if ((feature != 1) && (feature != 2) && (feature != 3)
                && (feature != 11))
              addConnection(fromNeuron, toNeuron, 0.15);
          }
        }
      }
    }
  }

  private void verbInstanceDoneExtinguishBarOneFeatures() {
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int doneNeuron = 40; doneNeuron < 60; doneNeuron += 5) {
        for (int feature = 0; feature < 1; feature++) { // just base verb
          int fromNeuron = doneNeuron + (CA * vInstCASize);
          for (int synapse = 0; synapse < 10; synapse++) {
            int toNeuron = CA * vInstCASize + (feature * 20) + 120;
            toNeuron += ((synapse + doneNeuron) % 20);
            addConnection(fromNeuron, toNeuron, 4.0);
          }
        }
      }
    }
  }

  // The others on needs to have connections to all the other features
  // (except np done) so it can bind to them and turn them on during
  // printResults
  private void connectVIOthersOnToOthers() {
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int baseNeuron = 146; baseNeuron < 154; baseNeuron++) {
        if (baseNeuron != 150) {
          for (int feature = 0; feature < 7; feature++) {
            if (feature != 1) { // skip self
              int fromNeuron = (CA * vInstCASize) + baseNeuron;
              for (int synapse = 0; synapse < 5; synapse++) {
                int toNeuron = synapse + 120 + (feature * 20)
                + (CA * vInstCASize);
                addConnection(fromNeuron, toNeuron, 0.01); // 0-4
                toNeuron += 15;
                addConnection(fromNeuron, toNeuron, 0.01); // 15-19
              }
            }
          }
        }
      }
    }
  }

  private void vIObjToObjDone() {
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int neuron = 181; neuron < 200; neuron++) {
        if (neuron == 185)
          neuron = 196;
        int fromNeuron = neuron + (CA * vInstCASize);
        int toNeuron = fromNeuron - 120;
        addConnection(fromNeuron, toNeuron, 2.25);
        if ((neuron % 5) == 1)
          addConnection(fromNeuron, toNeuron - 1, 2.25);
      }
    }
  }

  private void vIObjDoneExtinguishObj() {
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int neuron = 0; neuron < 4; neuron++) {
        int fromNeuron = (neuron * 5) + 60 + (CA * vInstCASize);
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = fromNeuron + 120 + synapse;
          addConnection(fromNeuron, toNeuron, 10);
        }
      }
    }
  }

  private void vIActToActDone() {
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int neuron = 161; neuron < 180; neuron++) {
        if (neuron == 165)
          neuron = 176;
        int fromNeuron = neuron + (CA * vInstCASize);
        int toNeuron = fromNeuron - 80;
        addConnection(fromNeuron, toNeuron, 2.25);
        if ((neuron % 5) == 1)
          addConnection(fromNeuron, toNeuron - 1, 2.25);
      }
    }
  }

  private void vIActDoneExtinguishAct() {
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int neuron = 0; neuron < 4; neuron++) {
        int fromNeuron = (neuron * 5) + 80 + (CA * vInstCASize);
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = fromNeuron + 80 + synapse;
          addConnection(fromNeuron, toNeuron, 10);
        }
      }
    }
  }

  private void vILocToLocDone() {
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int neuron = 241; neuron < 260; neuron++) {
        if (neuron == 245)
          neuron = 256;
        int fromNeuron = neuron + (CA * vInstCASize);
        int toNeuron = fromNeuron - 140;
        addConnection(fromNeuron, toNeuron, 2.25);
        if ((neuron % 5) == 1)
          addConnection(fromNeuron, toNeuron - 1, 2.25);
      }
    }
  }

  private void vILocDoneExtinguishLoc() {
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int neuron = 0; neuron < 4; neuron++) {
        int fromNeuron = (neuron * 5) + 100 + (CA * vInstCASize);
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = fromNeuron + 140 + synapse;
          addConnection(fromNeuron, toNeuron, 10);
        }
      }
    }
  }

  private void vIInstToInstDone() {
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int neuron = 221; neuron < 240; neuron++) {
        if (neuron == 225)
          neuron = 236;
        int fromNeuron = neuron + (CA * vInstCASize);
        int toNeuron = fromNeuron + 40;
        addConnection(fromNeuron, toNeuron, 2.45);
        if ((neuron % 5) == 1)
          addConnection(fromNeuron, toNeuron - 1, 2.45);
      }
    }
  }

  private void vIInstDoneExtinguishInst() {
    for (int CA = 0; CA < getSize() / vInstCASize; CA++) {
      for (int neuron = 0; neuron < 4; neuron++) {
        int fromNeuron = (neuron * 5) + 260 + (CA * vInstCASize);
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = fromNeuron - 40 + synapse;
          addConnection(fromNeuron, toNeuron, 10);
        }
      }
    }
  }

  private int verbInstanceCAs=5;
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

  private int counterCAFeatures = 10;
  private int counterCAForwardFeatures = 5;
  private int iCounterCASize = counterCAFeatures*10;
  private void setForwardConnect(int fromCA, int toCA, double weight) {
    for (int feature = 0; feature < counterCAForwardFeatures; feature++) {
      for (int i = 1; i < 5; i++){
        int fromNeuron = (fromCA*iCounterCASize) + (feature*10) + i;
        int toNeuron = (toCA*iCounterCASize) + (feature*10) + i;
        addConnection(fromNeuron, toNeuron, weight);
        addConnection(fromNeuron, toNeuron+50, weight);
        if (i == 1) {
          addConnection(fromNeuron, toNeuron-1, weight);
          addConnection(fromNeuron, toNeuron+49, weight);
        }
      }
    }
  }
  private void setBackConnect(int fromCA, int toCA) {
    for (int feature = 0; feature < counterCAFeatures; feature++) {
      for (int i = 0; i < 6; i+=5){
        int fromNeuron = (fromCA*iCounterCASize) + (feature*10) + i;
        for (int j=0;j<5;j++) {
          int toNeuron = (toCA*iCounterCASize) + (feature*10) + i+j;
          addConnection(fromNeuron, toNeuron, 5.0);
        }
      }
    }
  }

  //the STP binding of instances need time to decay before they
  //can be reused.  Each parse goes to a new instance counter set
  //in a circular queue.  The number of sets is instanceCounterSets
  private int instanceCounterSets = 5;  
  private int vSetStart=20;
  private void setInstanceCounterTopology() {
    //setFiftyFiftyTopology2(1.1);
    int neuron = 0;
    for (int CAs = 0; CAs < 30; CAs ++) {
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
    int nInstPerSet=4;
    int vInstPerSet=2;
    for (int iSet = 0; iSet< instanceCounterSets; iSet++) {
      //inter nSet connections 
      setForwardConnect((iSet*nInstPerSet),1+(iSet*nInstPerSet),2.9); 
      setForwardConnect(1+(iSet*nInstPerSet),2+(iSet*nInstPerSet),2.9);
      setForwardConnect(2+(iSet*nInstPerSet),3+(iSet*nInstPerSet),2.9);
      setBackConnect(1+(iSet*nInstPerSet),(iSet*nInstPerSet));
      setBackConnect(2+(iSet*nInstPerSet),1+(iSet*nInstPerSet));
      setBackConnect(3+(iSet*nInstPerSet),2+(iSet*nInstPerSet));

      //set up connections between noun sets 
      setForwardConnect((iSet*nInstPerSet),(4+(iSet*nInstPerSet))%20,1.1); 
      setForwardConnect(1+(iSet*nInstPerSet),(4+(iSet*nInstPerSet))%20,1.1);
      setForwardConnect(2+(iSet*nInstPerSet),(4+(iSet*nInstPerSet))%20,1.1);
      setForwardConnect(3+(iSet*nInstPerSet),(4+(iSet*nInstPerSet))%20,1.1);
      if (iSet!=0) {
        setBackConnect((iSet*nInstPerSet),(iSet*nInstPerSet)-4);
        setBackConnect((iSet*nInstPerSet),(iSet*nInstPerSet)-3);
        setBackConnect((iSet*nInstPerSet),(iSet*nInstPerSet)-2);
        setBackConnect((iSet*nInstPerSet),(iSet*nInstPerSet)-1);
      }
      else {
        setBackConnect(0,(instanceCounterSets*nInstPerSet)-4); 
        setBackConnect(0,(instanceCounterSets*nInstPerSet)-3);
        setBackConnect(0,(instanceCounterSets*nInstPerSet)-2);
        setBackConnect(0,(instanceCounterSets*nInstPerSet)-1);
      }

      //inter vSet connections 
      setForwardConnect(vSetStart+(iSet*vInstPerSet),
          vSetStart+(iSet*vInstPerSet)+1,2.9);
      setBackConnect(vSetStart+(iSet*vInstPerSet)+1,
          vSetStart+(iSet*vInstPerSet));

      //set up connections between verb sets 
      if (iSet != 4) {
        setForwardConnect(vSetStart+(iSet*vInstPerSet),
            vSetStart+(iSet*vInstPerSet)+2,1.1); 
        setForwardConnect(vSetStart+(iSet*vInstPerSet)+1,
            vSetStart+(iSet*vInstPerSet)+2,1.1); 
      }
      else {
        setForwardConnect(vSetStart+(iSet*vInstPerSet),
            vSetStart,1.1); 
        setForwardConnect(vSetStart+(iSet*vInstPerSet)+1,
            vSetStart,1.1); 
      }
      if (iSet != 0) {
        setBackConnect(vSetStart+(iSet*vInstPerSet),
            vSetStart+(iSet*vInstPerSet)-2);
        setBackConnect(vSetStart+(iSet*vInstPerSet),
            vSetStart+(iSet*vInstPerSet)-1);
      }
      else {
        setBackConnect(vSetStart,
            vSetStart+(instanceCounterSets*vInstPerSet)-2);
        setBackConnect(vSetStart,
            vSetStart+(instanceCounterSets*vInstPerSet)-1);
      }
    }
  }

  private int nextWordSteps=10;
  private void setNextWordTopology() {
    for (int i = 0; i< (nextWordSteps -1) *10; i++) {
      neurons[i].setInhibitory(false);
      addConnection(i,i+10, 4.1);
    }
  }


  private void setOtherWordTopology() {
    setFiftyFiftyTopology(0.9);
  }

  int preferenceCASize = 60;

  private void setRuleSelectionTopology() {
    setFiftyFiftyTopology(0.9);
  }

  // from parse4
  private void rulesInhibitEachOther(int rule1, int rule2) {
    ruleInhibitsRule(rule1, rule2);
    ruleInhibitsRule(rule2, rule1);
  }

  private void setRuleTwoTopology() {
    setFiftyFiftyTopology2(1.1);
    // without preps
    rulesInhibitEachOther(3, 6); // VPFromVPObj 11 )-( VPFromNPActVP 11

    // with preps
    rulesInhibitEachOther(9, 12); // VPFromVPLoc 11 )-( NPfromPPNP 12
    rulesInhibitEachOther(10, 12); // VPFromVPLoc 12 )-( NPfromPPNP 12
    rulesInhibitEachOther(10, 13); // VPFromVPLoc 12 )-( NPfromPPNP 23
    rulesInhibitEachOther(11, 13); // VPFromVPLoc 13 )-( NPfromPPNP 23
    rulesInhibitEachOther(15, 12); // VPFromVPInst 11 )-( NPfromPPNP 12
    rulesInhibitEachOther(16, 12); // VPFromVPInst 12 )-( NPfromPPNP 12
    rulesInhibitEachOther(16, 13); // VPFromVPInst 12 )-( NPfromPPNP 23
    rulesInhibitEachOther(17, 13); // VPFromVPInst 13 )-( NPfromPPNP 23
    rulesInhibitEachOther(9, 15); // VPFromVPLoc 11 )-( VPFromVPInst 11
    rulesInhibitEachOther(10, 16); // VPFromVPLoc 12 )-( VPFromVPInst 12
    rulesInhibitEachOther(11, 17); // VPFromVPLoc 13 )-( VPFromVPInst 13

    rulesInhibitEachOther(12, 13); // NPfromPPNP 12 )-( NPfromPPNP 23
  }

  // *****InterParse Subnet Connections
  // This works for both nounAccess and verb Access
  private void connectOneInputToOneAccess(CABot3Net accessNet, int inputCA,
      int accessCA) {
    for (int neuronNum = 0; neuronNum < inputCASize; neuronNum++) {
      int fromNeuron = (inputCA * inputCASize) + neuronNum;
      for (int synapseNum = 0; synapseNum < 2; synapseNum++) {
        int toNeuron = neuronNum + (accessCA * 100);
        if ((toNeuron % 10) >= 5)
          toNeuron -= 5;
        neurons[fromNeuron].addConnection(accessNet.neurons[toNeuron], 0.9);
      }
    }
  }

  public void connectInputToNounAccess(CABot3Net nounAccessNet) {
    connectOneInputToOneAccess(nounAccessNet, 1, 0); // left
    connectOneInputToOneAccess(nounAccessNet, 6, 1); // pyramid
    connectOneInputToOneAccess(nounAccessNet, 7, 2); // it
    connectOneInputToOneAccess(nounAccessNet, 8, 3); // stalactite
    connectOneInputToOneAccess(nounAccessNet, 9, 4); // I
    connectOneInputToOneAccess(nounAccessNet, 11, 5); // gun
    connectOneInputToOneAccess(nounAccessNet, 13, 6); // girl
    connectOneInputToOneAccess(nounAccessNet, 15, 7); // telescope
    connectOneInputToOneAccess(nounAccessNet, 16, 8); // door
    connectOneInputToOneAccess(nounAccessNet, 17, 9); // handle
    connectOneInputToOneAccess(nounAccessNet, 18, 10); // right
    connectOneInputToOneAccess(nounAccessNet, 19, 11); // forward
    connectOneInputToOneAccess(nounAccessNet, 20, 12); // backward
    connectOneInputToOneAccess(nounAccessNet, 21, 13); // wrong
    connectOneInputToOneAccess(nounAccessNet, 22, 14); // that
  }

  public void connectInputToVerbAccess(CABot3Net verbAccessNet) {
    connectOneInputToOneAccess(verbAccessNet, 0, 0); // move
    connectOneInputToOneAccess(verbAccessNet, 3, 1); // turn
    connectOneInputToOneAccess(verbAccessNet, 10, 2); // found
    connectOneInputToOneAccess(verbAccessNet, 12, 3); // saw
    connectOneInputToOneAccess(verbAccessNet, 24, 4); // go
    connectOneInputToOneAccess(verbAccessNet, 25, 5); // is
    connectOneInputToOneAccess(verbAccessNet, 26, 6); // center
    connectOneInputToOneAccess(verbAccessNet, 30, 7); // explore
    connectOneInputToOneAccess(verbAccessNet, 31, 8); // stop
  }

  public void connectInputToOther(CABot3Net otherNet) {
    connectOneInputToOneAccess(otherNet, 2, 0); // .period
    connectOneInputToOneAccess(otherNet, 4, 1); // toward
    connectOneInputToOneAccess(otherNet, 5, 2); // the
    connectOneInputToOneAccess(otherNet, 14, 3); // with
    connectOneInputToOneAccess(otherNet, 23, 4); // to
    connectOneInputToOneAccess(otherNet, 27, 5); // dangerous
    connectOneInputToOneAccess(otherNet, 32, 6); // striped
    connectOneInputToOneAccess(otherNet, 33, 7); // barred
    connectOneInputToOneAccess(otherNet, 34, 8); // before
  }

  // This works for nounAccess, verbAccess, and other
  // each noun access is primed by the wordActive CA. The particular word
  // selects which nounAccess is actually turned on.
  public void connectBarOneToAccess(CABot3Net accessNet) {
    for (int accessCA = 0; accessCA < accessNet.getSize() / 100; accessCA++) {
      for (int fromNeuron = 0; fromNeuron < wordActiveSize; fromNeuron++) {
        int toNeuron = fromNeuron + (accessCA * 100);
        if ((toNeuron % 10) >= 5)
          toNeuron -= 5;
        neurons[fromNeuron].addConnection(accessNet.neurons[toNeuron], 0.9);
      }
    }
  }

  // Need to activate the first half of each rule feature above 2. In
  // combination with the NounAccess this ignites it. Each neuron connects
  // to one of those so 2->1. Each has 1.25 weights so it goes above
  // 2 but stays below 3.
  private void connectWordActToActNInstRule(CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron = (neuron / ruleFeatureSize) * ruleFeatureSize;
      toNeuron += (neuron % (ruleFeatureSize / 2));
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron], 1.25);
    }
  }

  // reducing just this weight from 1.25 to 1 means it takes an extra cycle
  private void connectWordActToNPFromNRule(CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      if (!neurons[neuron].isInhibitory()) {
        int toNeuron = (neuron / 10) * 10;
        toNeuron += (neuron % 5) + 100;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron], 1.1);
        if ((neuron % 5) == 1) // make up for the inhibitory neuron
          neurons[neuron].addConnection(ruleNet.neurons[toNeuron - 1], 1.1);
      }
    }
  }

  // The barone CA extinguishes (or prevents) the NInst Rule.
  private void connectBarOneToInhibNInstRule(CABot3Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron += 5) {
      int toNeuron = (neuron - 100);
      for (int synapse = 0; synapse < 10; synapse++) {
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron++], -1.25);
        toNeuron %= 100;
      }
    }
  }

  private void connectWordActToNPDoneRule(CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      if (!neurons[neuron].isInhibitory()) {
        int toNeuron = (neuron / 10) * 10;
        toNeuron += (neuron % 5) + 200;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron], 1);
        if ((neuron % 5) == 1) // make up for the inhibitory neuron
          neurons[neuron].addConnection(ruleNet.neurons[toNeuron - 1], 1);
      }
    }
  }

  // Need to activate the first half of each rule feature above 2. In
  // combination with the NounAccess this ignites it. Each neuron connects
  // to one of those so 2->1. Each has 1.25 weights so it goes above
  // 2 but stays below 3.
  private void connectWordActToActVInstRule(CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron = (neuron / ruleFeatureSize) * ruleFeatureSize;
      toNeuron += (neuron % (ruleFeatureSize / 2)) + 400;
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron], 1.25);
    }
  }

  // The barone CA extinguishes (or prevents) the VInst Rule.
  private void connectBarOneToInhibVInstRule(CABot3Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron += 5) {
      int toNeuron = neuron % 100;
      for (int synapse = 0; synapse < 10; synapse++) {
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron++ + 400], -1.25);
        toNeuron %= 100;
      }
    }
  }

  // like connectWordActToNPFromNRule
  private void connectWordActToMainVerbRule(CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron = (neuron / ruleFeatureSize) * ruleFeatureSize;
      toNeuron += (neuron % (ruleFeatureSize / 2) + 500);
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron], 1.0);
    }
  }

  private void connectBarOneToVPDoneRule(CABot3Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron++) {
      if (!neurons[neuron].isInhibitory()) {
        int toNeuron = (neuron / 10) * 10;
        toNeuron += (neuron % 5) + 500;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron], 1.1);
        if ((neuron % 5) == 1) // make up for the inhibitory neuron
          neurons[neuron].addConnection(ruleNet.neurons[toNeuron - 1], 1.1);
      }
    }
  }

  private void connectWordActToNPAddAdjRule(CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron = (neuron / ruleFeatureSize) * ruleFeatureSize;
      toNeuron += (neuron % (ruleFeatureSize / 2) + 800);
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron], 1.25);
    }
  }

  // The barone prevents bar two rules.
  private void connectBarOneToInhibBarTwoRule(int toStart, CABot3Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron += 5) {
      for (int synapse = 0; synapse < 5; synapse++) {
        int toNeuron = neuron % 100;
        if ((neuron % 10) < 5)
          toNeuron += synapse + toStart;
        else
          toNeuron += (-synapse) - 1 + toStart;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron], -1.25);
      }
    }
  }

  // modified connectWordActToNPFromNRule
  private void connectWordActToNPAddPrepRule(CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron = (neuron / ruleFeatureSize) * ruleFeatureSize;
      toNeuron += (neuron % (ruleFeatureSize / 2) + 900);
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron], 1.0);
    }
  }

  private void connectBarOneToPrepDoneRule(CABot3Net ruleNet) {
    for (int neuron = 100; neuron < 200; neuron++) {
      if (!neurons[neuron].isInhibitory()) {
        int toNeuron = (neuron / 10) * 10;
        toNeuron += (neuron % 5) + 900;
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron], 1.2);
        if ((neuron % 5) == 1) // make up for the inhibitory neuron
          neurons[neuron].addConnection(ruleNet.neurons[toNeuron - 1], 1.2);
      }
    }
  }

  private void connectWordActToNPAddDetRule(CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int toNeuron = (neuron / ruleFeatureSize) * ruleFeatureSize;
      toNeuron += (neuron % (ruleFeatureSize / 2) + 1100);
      neurons[neuron].addConnection(ruleNet.neurons[toNeuron], 1.25);
    }
  }

  public void connectBarOneToRuleOne(CABot3Net ruleNet) {
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

  public void connectBarOneToRuleTwo(CABot3Net ruleNet) {
    connectBarOneToInhibBarTwoRule(300, ruleNet); // VP -> VP NPObj 1 1
    connectBarOneToInhibBarTwoRule(400, ruleNet); // VP -> VP NPObj 1 2
    connectBarOneToInhibBarTwoRule(600, ruleNet); // VP -> NPAct VP 1 1
    connectBarOneToInhibBarTwoRule(900, ruleNet); // VP -> VP PPloc 1 1
    connectBarOneToInhibBarTwoRule(1000, ruleNet); // VP -> VP PPloc 1 2
    connectBarOneToInhibBarTwoRule(1100, ruleNet); // VP -> VP PPloc 1 3
    connectBarOneToInhibBarTwoRule(1200, ruleNet); // NP -> NPPP 1 2
    connectBarOneToInhibBarTwoRule(1300, ruleNet); // NP -> NPPP 2 3
    connectBarOneToInhibBarTwoRule(1500, ruleNet); // VP -> VP PPinst 1 1
    connectBarOneToInhibBarTwoRule(1600, ruleNet); // VP -> VP PPinst 1 2
    connectBarOneToInhibBarTwoRule(1700, ruleNet); // VP -> VP PPinst 1 3
  }

  // 10 connections to 1 neuron. .25 gives 2.5 each cycle, so 2.5, 3.75 >4
  private void connectOneAccessToOneSemFeature(CABot3Net semNet, int accessCA,
      int semFeature) {

    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (accessCA * 100) + neuron;
      int toNeuron = (neuron % 5) + (semFeature * nounFeatureSize);
      neurons[fromNeuron].addConnection(semNet.neurons[toNeuron], 0.25);
    }
  }

  private void connectOneAccessToOneSemFeature(CABot3Net semNet, int accessCA,
      int semFeature, int hierStartFeature) {
    for (int feature = semFeature * 3; feature < (semFeature * 3) + 3; feature++) {
      connectOneAccessToOneSemFeature(semNet, accessCA, hierStartFeature
          + feature);
    }
  }

  // works for both nounaccess->nounSem and verbAccess -> verbSem
  private void connectOneAccessToOneSemWord(CABot3Net semNet, int accessCA,
      int semWord) {
    for (int feature = semWord * 6; feature < (semWord * 6) + 6; feature++) {
      connectOneAccessToOneSemFeature(semNet, accessCA, feature);
    }
  }

  private int nounHierStart = 40 * 3;

  private LineNumberReader openWordFile() {
    DataInputStream dIS;
    InputStreamReader inputSR;
    LineNumberReader inputFile = null;
    String wordFile = CABot3.inputPath + CABot3.wordFile;

    try {
      dIS = new DataInputStream(new FileInputStream(wordFile));
      inputSR = new InputStreamReader(dIS);
      inputFile = new LineNumberReader(inputSR);
    }

    catch (IOException e) {
      System.err.println("word file not opened properly\n" + e.toString());
      System.exit(1);
    }
    return inputFile;
  }

  // loop through all the entries until the end of file sticking in all nouns
  private void readNounSems(CABot3Net nounSemNet, LineNumberReader inputFile) {
    StringTokenizer tokenizedLine;
    int nounAccessOffset = 0;
    try {
      String inputLine = inputFile.readLine();
      while (inputLine != null) {
        // System.out.println(inputLine);
        tokenizedLine = new StringTokenizer(inputLine);
        String wordString = tokenizedLine.nextToken();
        String lexClass = tokenizedLine.nextToken();
        if (lexClass.compareTo("noun") == 0) {
          String paramString = tokenizedLine.nextToken();
          int baseSem = Integer.parseInt(paramString);
          connectOneAccessToOneSemWord(nounSemNet, nounAccessOffset, baseSem);
          while (tokenizedLine.hasMoreTokens()) {
            paramString = tokenizedLine.nextToken();
            int hierSem = Integer.parseInt(paramString);
            connectOneAccessToOneSemFeature(nounSemNet, nounAccessOffset,
                hierSem, nounHierStart);
          }
          nounAccessOffset++;
        }
        inputLine = inputFile.readLine();
      }
    } catch (IOException e) {
      System.err.println("word readline problem\n" + e.toString());
      System.exit(1);
    }
  }

  private void readNounSemFile(CABot3Net nounSemNet) {
    LineNumberReader inputFile = openWordFile();
    readNounSems(nounSemNet, inputFile);
    try {
      inputFile.close();
    } catch (IOException e) {
      System.err.println("word n file not closed \n" + e.toString());
      System.exit(1);
    }
  }

  public void connectNounAccessToSem(CABot3Net nounSemNet) {
    readNounSemFile(nounSemNet);
  }

  private int verbHierStart = 120 * 3;

  // loop through all the entries until the end of file sticking in all verbs
  private void readVerbSems(CABot3Net verbSemNet, LineNumberReader inputFile) {
    StringTokenizer tokenizedLine;
    int verbAccessOffset = 0;
    try {
      String inputLine = inputFile.readLine();
      while (inputLine != null) {
        tokenizedLine = new StringTokenizer(inputLine);
        String wordString = tokenizedLine.nextToken();
        String lexClass = tokenizedLine.nextToken();
        if (lexClass.compareTo("verb") == 0) {
          // System.out.println(inputLine);
          String paramString = tokenizedLine.nextToken();
          int baseSem = Integer.parseInt(paramString);
          connectOneAccessToOneSemWord(verbSemNet, verbAccessOffset, baseSem);
          while (tokenizedLine.hasMoreTokens()) {
            paramString = tokenizedLine.nextToken();
            int hierSem = Integer.parseInt(paramString);
            connectOneAccessToOneSemFeature(verbSemNet, verbAccessOffset,
                hierSem, verbHierStart);
          }
          verbAccessOffset++;
        }
        inputLine = inputFile.readLine();
      }
    } catch (IOException e) {
      System.err.println("word readline problem\n" + e.toString());
      System.exit(1);
    }
  }

  private void readVerbSemFile(CABot3Net verbSemNet) {
    LineNumberReader inputFile = openWordFile();
    readVerbSems(verbSemNet, inputFile);
    try {
      inputFile.close();
    } catch (IOException e) {
      System.err.println("word v file not closed \n" + e.toString());
      System.exit(1);
    }
  }

  public void connectVerbAccessToSem(CABot3Net verbSemNet) {
    readVerbSemFile(verbSemNet);
  }

  // The active access word primes but does not activate the word.
  // 10 connections to 1 neuron. .125 gives 1.25 each cycle, so 1.25, 1.8725,
  // ... < 3
  private void connectNounAccessWordToActNInstRule(int word, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (word * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.125);
      }
    }
  }

  private void connectNounAccessWordToNPFromN(int word, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (word * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize) + 100;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.11);
      }
    }
  }

  private void connectNounAccessWordToNPDone(int word, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (word * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize) + 200;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.11);
      }
    }
  }

  public void connectNounAccessToRule(CABot3Net ruleNet) {
    for (int word = 0; word < numNouns; word++) {
      connectNounAccessWordToActNInstRule(word, ruleNet);
      connectNounAccessWordToNPFromN(word, ruleNet);
      connectNounAccessWordToNPDone(word, ruleNet);
    }
  }

  // 4 neurons to 5
  private void NewNInstStartBarOne(CABot3Net barOneNet) {
    for (int neuron = 1; neuron < 100; neuron++) {
      if ((neuron % 5) != 0) { // skip inhibitory neuron.
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = ((neuron / 5) * 5) + synapse + 100;
          neurons[neuron].addConnection(barOneNet.neurons[toNeuron], 1.1);
        }
      }
    }
  }

  // NPDone turns off both bar one and wordon
  private void NPDoneExtinguishBarOne(CABot3Net barOneNet) {
    for (int neuron = 0; neuron < 100; neuron += 5) {// Every 5th is inhibitory
      for (int synapse = 0; synapse < 5; synapse++) {
        int toNeuron = neuron + synapse;
        int fromNeuron = neuron + 200; // NPDoneOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron], -2.0);
        toNeuron += 100;
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron], -2.0);
      }
    }
  }

  // The Read Next Rule turns on the Word On CA by itself
  private void ReadNextIgnitesWordOn(CABot3Net barOneNet) {
    for (int neuron = 300; neuron < 400; neuron++) {
      if ((neuron % 5) != 0) { // skip inhibitory neuron.
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = ((neuron / 5) * 5) + synapse - 300;
          neurons[neuron].addConnection(barOneNet.neurons[toNeuron], 1.1);
        }
      }
    }
  }

  private void NewVInstStartBarOne(CABot3Net barOneNet) {
    for (int neuron = 401; neuron < 500; neuron++) {
      if ((neuron % 5) != 0) { // skip inhibitory neuron.
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = ((neuron / 5) * 5) + synapse - 300;
          neurons[neuron].addConnection(barOneNet.neurons[toNeuron], 1.1);
        }
      }
    }
  }

  // like NPDoneExtinguishBarOne
  private void VPDoneExtinguishBarOne(CABot3Net barOneNet) {
    for (int neuron = 0; neuron < 100; neuron += 5) {// Every 5th is inhibitory
      for (int synapse = 0; synapse < 5; synapse++) {
        int toNeuron = neuron + synapse;
        int fromNeuron = neuron + 600; // NPDoneOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron], -2.0);
        toNeuron += 100;
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron], -2.0);
      }
    }
  }

  private void NPAddAdjExtinguishWordOn(CABot3Net barOneNet) {
    for (int neuron = 0; neuron < 100; neuron += 5) {// Every 5th is inhibitory
      for (int synapse = 0; synapse < 5; synapse++) {
        int toNeuron = neuron + synapse;
        int fromNeuron = neuron + 800; // NPAddDetOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron], -2.0);
      }
    }
  }

  private void PrepDoneExtinguishWordOn(CABot3Net barOneNet) {
    for (int neuron = 0; neuron < 100; neuron += 5) {// Every 5th is inhibitory
      for (int synapse = 0; synapse < 5; synapse++) {
        int toNeuron = neuron + synapse;
        int fromNeuron = neuron + 1000; // PrepDoneOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron], -2.0);
      }
    }
  }

  private void NPAddDetExtinguishWordOn(CABot3Net barOneNet) {
    for (int neuron = 0; neuron < 100; neuron += 5) {// Every 5th is inhibitory
      for (int synapse = 0; synapse < 5; synapse++) {
        int toNeuron = neuron + synapse;
        int fromNeuron = neuron + 1100; // NPAddDetOffset
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron], -2.0);
      }
    }
  }

  public void connectRuleOneToBarOne(CABot3Net barOneNet) {
    NewNInstStartBarOne(barOneNet);
    NPDoneExtinguishBarOne(barOneNet);
    ReadNextIgnitesWordOn(barOneNet);
    NewVInstStartBarOne(barOneNet);
    VPDoneExtinguishBarOne(barOneNet);
    NPAddAdjExtinguishWordOn(barOneNet);
    PrepDoneExtinguishWordOn(barOneNet);
    NPAddDetExtinguishWordOn(barOneNet); 
  }

  private void connectNPFromNToMainFeature(CABot3Net nounInstanceNet) {
    for (int neuron = 100; neuron < 200; neuron++) {
      for (int nInst = 0; nInst < 15; nInst++) {
        if (neuron % 5 != 0) { // skip the inhibitory ones
          int toNeuron = (neuron % 20);
          // the internal neurons are stimulated by the external
          if ((toNeuron < 5) || (toNeuron > 14)) {
            toNeuron += (nInst * nInstCASize) + 120;
            neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
                0.25);
            if (toNeuron % 5 == 1) { // make up for the inhibitory one
              neurons[neuron].addConnection(
                  nounInstanceNet.neurons[toNeuron - 1], 0.25);
            }
          }
        }
      }
    }
  }

  private void connectNPDoneToDoneFeature(CABot3Net nounInstanceNet) {
    for (int neuron = 200; neuron < 300; neuron++) {
      for (int nInstCA = 0; nInstCA < nounInstanceCAs; nInstCA++) {
        if (neuron % 5 != 0) { // skip the inhibitory ones
          int toNeuron = (neuron % 20);
          toNeuron += (nInstCA * nInstCASize) + 40;
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron], 
              0.25);
          if (toNeuron % 5 == 1) { // make up for the inhibitory one
            neurons[neuron].addConnection(
                nounInstanceNet.neurons[toNeuron - 1], 0.25);
          }
        }
      }
    }
  }

  private void connectNPAddAdjToAdjFeature(CABot3Net nounInstanceNet) {
    for (int neuron = 800; neuron < 900; neuron++) {
      for (int nInstCA = 0; nInstCA < nounInstanceCAs; nInstCA++) {
        if (neuron % 5 != 0) { // skip the inhibitory ones
          int toNeuron = (neuron % 20);
          toNeuron += (nInstCA * nInstCASize) + 160;
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],0.3);
          if (toNeuron % 5 == 1) { // make up for the inhibitory one
            neurons[neuron].addConnection(
                nounInstanceNet.neurons[toNeuron - 1], 0.3);
          }
        }
      }
    }
  }

  private void connectNPAddPrepToPrepFeature(CABot3Net nounInstanceNet) {
    for (int neuron = 900; neuron < 1000; neuron++) {
      for (int word = 0; word < 3; word++) {
        if (neuron % 5 != 0) { // skip the inhibitory ones
          int toNeuron = (neuron % 20);
          toNeuron += (word * nInstCASize) + 100;
          neurons[neuron]
                  .addConnection(nounInstanceNet.neurons[toNeuron], 0.25);
          if (toNeuron % 5 == 1) { // make up for the inhibitory one
            neurons[neuron].addConnection(
                nounInstanceNet.neurons[toNeuron - 1], 0.25);
          }
        }
      }
    }
  }

  // To get this feature on you need the base features, the rule and
  // the prep bind feature.
  private void connectPrepDoneToPrepOnFeature(CABot3Net nounInstanceNet) {
    for (int neuron = 1000; neuron < 1100; neuron++) {
      for (int word = 0; word < 3; word++) {
        if (neuron % 5 != 0) { // skip the inhibitory ones
          int toNeuron = (neuron % 20);
          toNeuron += (word * nInstCASize) + 60;
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron],
              0.125);
          if (toNeuron % 5 == 1) { // make up for the inhibitory one
            neurons[neuron].addConnection(
                nounInstanceNet.neurons[toNeuron - 1], 0.125);
          }
        }
      }
    }
  }

  private void connectNPAddDetToDetFeature(CABot3Net nounInstanceNet) {
    for (int neuron = 1100; neuron < 1200; neuron++) {
      for (int word = 0; word < 3; word++) {
        if (neuron % 5 != 0) { // skip the inhibitory ones
          int toNeuron = (neuron % 20);
          toNeuron += (word * nInstCASize) + 140;
          neurons[neuron]
                  .addConnection(nounInstanceNet.neurons[toNeuron], 0.25);
          if (toNeuron % 5 == 1) { // make up for the inhibitory one
            neurons[neuron].addConnection(
                nounInstanceNet.neurons[toNeuron - 1], 0.25);
          }
        }
      }
    }
  }

  private void connectVPAddNPToABoundFeature(int ruleStart, int inst,
      CABot3Net nounInstanceNet) {
    for (int neuron = ruleStart; neuron < ruleStart + 100; neuron++) {
      if (!neurons[neuron].isInhibitory()) { // 80->10
        int toNeuron = (neuron % 100) / 10; // 0..10
        if (toNeuron >= 5)
          toNeuron += 5; // 0..4,10..14
        toNeuron += 280 + (inst * nInstCASize);
        neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron], 0.34); // should
        // take
        // 11
        // cycles
      }
    }
  }

  private void connectVPAddObjToBoundFeature(CABot3Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(300, 0, nounInstanceNet);
    connectVPAddNPToABoundFeature(400, 1, nounInstanceNet);
  }

  private void connectVPAddLocToBoundFeature(CABot3Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(900, 0, nounInstanceNet);
    connectVPAddNPToABoundFeature(1000, 1, nounInstanceNet);
    connectVPAddNPToABoundFeature(1100, 2, nounInstanceNet);
  }

  private void connectVPAddActToBoundFeature(CABot3Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(600, 0, nounInstanceNet);
  }

  private void connectNPAddPPToBoundFeature(CABot3Net nounInstanceNet) {
    connectVPAddNPToABoundFeature(1200, 1, nounInstanceNet);
    connectVPAddNPToABoundFeature(1300, 2, nounInstanceNet);
  }

  private void connectNPAddPPToPPModFeature(int toNounInst, int ruleStart,
      CABot3Net nounInstanceNet) {
    for (int neuron = ruleStart; neuron < ruleStart + 100; neuron++) {
      if (neuron % 5 != 0) { // skip the inhibitory ones
        int toNeuron = (neuron % 20);
        toNeuron += (toNounInst * nInstCASize) + 200;
        neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron], 0.25);
        if (toNeuron % 5 == 1) { // make up for the inhibitory one
          neurons[neuron].addConnection(nounInstanceNet.neurons[toNeuron - 1],
              0.25);
        }
      }
    }
  }

  public void connectRuleOneToNounInstance(CABot3Net nounInstanceNet) {
    connectNPFromNToMainFeature(nounInstanceNet);
    connectNPDoneToDoneFeature(nounInstanceNet);  
    connectNPAddAdjToAdjFeature(nounInstanceNet); 
    connectNPAddPrepToPrepFeature(nounInstanceNet); //undone below add isets
    connectPrepDoneToPrepOnFeature(nounInstanceNet);
    connectNPAddDetToDetFeature(nounInstanceNet);
  }

  public void connectRuleTwoToNounInstance(CABot3Net nounInstanceNet) {
    connectVPAddActToBoundFeature(nounInstanceNet);
    // undone connectVPAddActToDoneFeature(nounInstanceNet);
    connectVPAddObjToBoundFeature(nounInstanceNet);
    // undone connectVPAddInstToBoundFeature(nounInstanceNet);
    connectVPAddLocToBoundFeature(nounInstanceNet);
    connectNPAddPPToBoundFeature(nounInstanceNet);
    connectNPAddPPToPPModFeature(0, 1200, nounInstanceNet);
    connectNPAddPPToPPModFeature(1, 1300, nounInstanceNet);

    // vPAddActStopsInstance(nounInstanceNet,600,0);
  }

  private void NPDoneExtinguishNounAccessWord(int word, CABot3Net nounAccessNet) {
    for (int neuron = 0; neuron < 100; neuron += 5) {// Every 5th is inhibitory
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = ((neuron + synapse) % 100) + (word * 100);
        int fromNeuron = neuron + 200; // NPDoneOffset
        neurons[fromNeuron]
                .addConnection(nounAccessNet.neurons[toNeuron], -4.0);
      }
    }
  }

  public void connectRuleToNounAccess(CABot3Net nounAccessNet) {
    for (int word = 0; word < numNouns; word++) {
      NPDoneExtinguishNounAccessWord(word, nounAccessNet);
    }
  }

  private void VPDoneExtinguishVerbAccessWord(int word, CABot3Net verbAccessNet) {
    for (int neuron = 0; neuron < 100; neuron += 5) {// Every 5th is inhibitory
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = neuron + synapse + (word * 100);
        int fromNeuron = neuron + 600; // NPDoneOffset
        neurons[fromNeuron]
                .addConnection(verbAccessNet.neurons[toNeuron], -2.0);
      }
    }
  }

  public void connectRuleToVerbAccess(CABot3Net verbAccessNet) {
    for (int word = 0; word < numVerbs; word++) {
      VPDoneExtinguishVerbAccessWord(word, verbAccessNet);
    }
  }

  private void prepDoneExtinguishAPrep(int word, CABot3Net otherNet) {
    for (int neuron = 0; neuron < 100; neuron += 5) {// Every 5th is inhibitory
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = ((neuron + synapse)%100) + (word * 100);
        int fromNeuron = neuron + 1000; // PrepDoneOffset
        neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron], -2.0);
      }
    }
  }

  private void prepDoneExtinguishPreps(CABot3Net otherNet) {
    prepDoneExtinguishAPrep(1, otherNet); // toward
    prepDoneExtinguishAPrep(3, otherNet); // with
    prepDoneExtinguishAPrep(4, otherNet); // to
    prepDoneExtinguishAPrep(8, otherNet); // before
  }

  private void NPAddDetExtinguishADet(int word, CABot3Net otherNet) {
    for (int neuron = 0; neuron < 100; neuron += 5) {// Every 5th is inhibitory
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = neuron + synapse + (word * 100);
        int fromNeuron = neuron + 1100; // NPAddDetOffset
        neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron], -2.0);
      }
    }
  }

  private void NPAddDetExtinguishDets(CABot3Net otherNet) {
    NPAddDetExtinguishADet(2, otherNet); // toward
  }

  private void NPFromNExtinguishAnAdj(CABot3Net otherNet,int word) {
    for (int neuron = 0; neuron < 100; neuron += 5) {// Every 5th is inhibitory
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = neuron + synapse + (word * 100);
        int fromNeuron = neuron + 100; // NPFromNOffset
        neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron], -2.0);
      }
    }
  }

  private void NPFromNExtinguishAdjs(CABot3Net otherNet) {
    NPFromNExtinguishAnAdj(otherNet,5);
    NPFromNExtinguishAnAdj(otherNet,6);
    NPFromNExtinguishAnAdj(otherNet,7);
  }

  public void connectRuleOneToOther(CABot3Net otherNet) {
    prepDoneExtinguishPreps(otherNet);
    NPAddDetExtinguishDets(otherNet);
    NPFromNExtinguishAdjs(otherNet);
  }

  //When a new instance rule fires up, send activation to counter.
  //In combination with the current counter, it should move on.
  private void connectARuleToAnICounter(int rule, int counter,
      CABot3Net iCounterNet) {
    int ruleSize= 100;
    for (int i = 1; i < ruleSize; i++) {
      int fromNeuron = (rule*ruleSize)+i;
      int toNeuron = (counter*iCounterCASize)+i;
      neurons[fromNeuron].addConnection(iCounterNet.neurons[toNeuron],0.4);
      if ((i%10)==1) 
        neurons[fromNeuron].addConnection(iCounterNet.neurons[toNeuron-1],0.4);
      if ((i%10)==4) i+=6;
    }
  }

  public void connectRuleOneToInstanceCounter(CABot3Net instanceCounterNet) {
    for (int iSet=0; iSet<instanceCounterSets;iSet++) {
      int toInstanceCounter=iSet*4;
      connectARuleToAnICounter(0,toInstanceCounter+1,instanceCounterNet); 
      connectARuleToAnICounter(0,toInstanceCounter+2,instanceCounterNet);
      connectARuleToAnICounter(0,toInstanceCounter+3,instanceCounterNet);
      toInstanceCounter=vSetStart+(iSet*2)+1;
      connectARuleToAnICounter(4,toInstanceCounter,instanceCounterNet);
    }
  }

  //When instancecounter reset is on, stimulate all first instances.
  //In combination with the current one, the next comes on.
  public void connectControlToInstanceCounter(CABot3Net instanceCounterNet) {
    for (int i = 40; i < 80; i++) {
      for (int iCounterSet=0; iCounterSet<instanceCounterSets; iCounterSet++) {
        for (int synapse = 0; synapse <3 ;synapse++) {
          int toNeuron = ((i-40) + (synapse*40));
          if (toNeuron < 100) {
            //nouns
            toNeuron += iCounterSet*400;
            neurons[i].addConnection(instanceCounterNet.neurons[toNeuron],2.5);
            //verbs
            toNeuron = ((i-40) + (synapse*40));
            toNeuron += (iCounterSet*200)+2000;
            neurons[i].addConnection(instanceCounterNet.neurons[toNeuron],2.5);
          }
        }
      }
    }
  }

  private void aControlStartsNextWord(int item, CABot3Net nextWordNet) {
    //start the 0 counter
    for (int i = 0 ; i <10; i++) {
      int fromNeuron = (item *40)+i;
      neurons[fromNeuron].addConnection(nextWordNet.neurons[i],4.1);
      if (i < 5) 
        neurons[fromNeuron].addConnection(nextWordNet.neurons[i+5],4.1);
      else
        neurons[fromNeuron].addConnection(nextWordNet.neurons[i-5],4.1);
    }
    //stop the other counters
    for (int counter = 1 ; counter <10; counter++) {
      for (int i = 0 ; i <10; i++) {
        int fromNeuron = (item *40)+i;
        int toNeuron = (counter*10)+i;
        neurons[fromNeuron].addConnection(nextWordNet.neurons[toNeuron],-0.5);
        if (i < 5) 
          neurons[fromNeuron].addConnection(nextWordNet.neurons[toNeuron+5],
              -0.5);
        else
          neurons[fromNeuron].addConnection(nextWordNet.neurons[toNeuron-5],
              -0.5);
      }
    }
  }
  public void connectControlToNextWord(CABot3Net nextWordNet) {
    aControlStartsNextWord(2,nextWordNet); //goalset
    aControlStartsNextWord(3,nextWordNet); //clear
    aControlStartsNextWord(4,nextWordNet); //reset
  }

  private void anICounterTurnsABaseInstanceOn(int counter, int instance,
      int instCASize,CABot3Net instanceNet) {
    for (int feature = 0; feature < 4; feature ++) {
      for (int i = 0; i < 10; i++) {
        int fromNeuron = (counter*iCounterCASize) + (feature*10) + i;
        if ((i%5) == 0) fromNeuron++;
        int toNeuron = (instance*instCASize) + (feature*10) +i;
        if (i > 4) toNeuron -=5;
        neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron],
            2.5);
      }
    }
  }


  private void anICounterTurnsATimeInstanceOn(int counter, int instance,
      int instCASize,int timeOffset, CABot3Net instanceNet) {
    for (int feature = 5; feature < 9; feature ++) {
      for (int i = 1; i < 5; i++) {
        int fromNeuron = (counter*iCounterCASize) + (feature*10) + i;
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

  private void anICounterTurnsAnInstanceOn(int counter, int instance,
      int instCASize,int timeOffset, CABot3Net instanceNet) {
    anICounterTurnsABaseInstanceOn(counter,instance,instCASize,instanceNet);
    anICounterTurnsATimeInstanceOn(counter,instance,instCASize,
        timeOffset,instanceNet);
  }

  //the 40-50 neurons turn on the bind slot
  private void aNounCounterTurnsAnInstanceBindOn(int counter, int instance,
      CABot3Net instanceNet) {
    for (int i = 0; i < 10; i++) {
      int fromNeuron = (counter*100) + 40 + i;
      if ((i%5) == 0) fromNeuron++;
      int toNeuron = (instance*nInstCASize) + 260 +i;
      if (i > 4) toNeuron -=5;
      neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron], 2.5);
      neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron+10],2.5);
    }
  }

  public void connectICounterToNounInstance(CABot3Net nounInstanceNet) {
    for (int iSet=0; iSet<instanceCounterSets;iSet++) {
      for (int instance=0; instance < 3; instance ++) {
        int iCounter = (iSet*4)+instance+1;
        int nInstance = (iSet*3)+instance;
        anICounterTurnsAnInstanceOn(iCounter,nInstance,nInstCASize,
            nounInstanceTimeStart, nounInstanceNet);
        aNounCounterTurnsAnInstanceBindOn(iCounter,nInstance,nounInstanceNet);
      }
    }
  }

  //For goal setting, the instance counter has moved onto the next set.
  //The first instance of the set primes the prior instance.
  private void aVerbICounterPrimesPriorInstance(int iSet, 
      CABot3Net verbInstanceNet) {
    int toInstance = (iSet-1);
    if (toInstance<0) toInstance=4;
    for (int i = 0; i<35; i++) {
      if ((i%10)==5)i+=5;
      int fromNeuron = (iSet*200)+(vSetStart*100)+i;
      int toNeuron=toInstance*500+i;
      neurons[fromNeuron].addConnection(verbInstanceNet.neurons[toNeuron],1.5);
    }
  }

  public void connectICounterToVerbInstance(CABot3Net verbInstanceNet) {
    for (int iSet=0; iSet<instanceCounterSets;iSet++) {
      int instanceCounterCA=vSetStart+(iSet*2)+1;
      anICounterTurnsAnInstanceOn(instanceCounterCA,iSet,
          vInstCASize,verbInstanceTimeStart,verbInstanceNet);
      aVerbICounterPrimesPriorInstance(iSet,verbInstanceNet);
    }
  }
  private void connectARuleToStartNextWord(int rule, CABot3Net nextWordNet) {
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
  private void connectARuleToStopNextWord(int rule, CABot3Net nextWordNet) {
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
  private void connectARuleToNextWord(int rule, CABot3Net nextWordNet) {
    connectARuleToStartNextWord(rule, nextWordNet);
    connectARuleToStopNextWord(rule, nextWordNet);
  }
  public void connectRuleOneToNextWord(CABot3Net nextWordNet) {
    connectARuleToNextWord(0,nextWordNet);
    connectARuleToNextWord(1,nextWordNet);
    connectARuleToNextWord(2,nextWordNet);
    connectARuleToNextWord(4,nextWordNet);
    connectARuleToNextWord(5,nextWordNet);
    connectARuleToNextWord(6,nextWordNet);
    connectARuleToNextWord(8,nextWordNet);
    connectARuleToNextWord(9,nextWordNet);
    connectARuleToNextWord(10,nextWordNet);
    connectARuleToNextWord(11,nextWordNet);
  }

  public void connectRuleTwoToNextWord(CABot3Net nextWordNet) {
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

  //Turn on the next word rule when counter is 10.
  public void connectNextWordToRuleOne(CABot3Net ruleOneNet) {
    for (int i = 0; i < 10; i++) {
      int fromNeuron = ((nextWordSteps -1)*10)+i;
      for (int synapse = 0; synapse < 5 ; synapse ++) {
        int toNeuron = (i*10) + synapse+ 300;
        neurons[fromNeuron].addConnection(ruleOneNet.neurons[toNeuron],4.1);
      }
    }
  }

  // like connectNPFromNToMainFeature. Connect the rule to the main
  // main feature so that, in conjuction with the active instance,
  // the main feature comes on
  private void connectMainVerbToMainFeature(CABot3Net verbInstanceNet) {
    for (int neuron = 500; neuron < 600; neuron++) {
      for (int vInstCA = 0; vInstCA < verbInstanceCAs; vInstCA++) {
        if (neuron % 5 != 0) { // skip the inhibitory ones
          int toNeuron = (neuron % 20);
          toNeuron += (vInstCA * vInstCASize) + 120;
          neurons[neuron]
                  .addConnection(verbInstanceNet.neurons[toNeuron], 0.25); // .4 in
          // parse4
          if (toNeuron % 5 == 1) { // make up for the inhibitory one
            neurons[neuron].addConnection(
                verbInstanceNet.neurons[toNeuron - 1], 0.25);
          }
        }
      }
    }
  }

  private void connectVPDoneToDoneFeature(CABot3Net verbInstanceNet) {
    for (int neuron = 600; neuron < 700; neuron++) {
      for (int vInstCA = 0; vInstCA < verbInstanceCAs; vInstCA++) {
        if (neuron % 5 != 0) { // skip the inhibitory ones
          int toNeuron = (neuron % 20);
          toNeuron += (vInstCA * vInstCASize) + 40;
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron], 0.3);
          if (toNeuron % 5 == 1) { // make up for the inhibitory one
            neurons[neuron].addConnection(
                verbInstanceNet.neurons[toNeuron - 1], 0.3);
          }
        }
      }
    }
  }
  public void connectRuleOneToVerbInstance(CABot3Net verbInstanceNet) {
    connectMainVerbToMainFeature(verbInstanceNet);
    connectVPDoneToDoneFeature(verbInstanceNet);
  }


  private void connectVPFromVPNPObjToObjFeature(int ruleStart,
      CABot3Net verbInstanceNet) {
    for (int iSet =0; iSet < instanceCounterSets; iSet++) { 
      for (int neuron = ruleStart; neuron < ruleStart + 100; neuron++) {
        if (neuron % 5 != 0) { // skip the inhibitory ones
          int toNeuron = (neuron % 20);
          toNeuron += (iSet * vInstCASize) + 180 ;
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron],0.3);
          if (toNeuron % 5 == 1) { // make up for the inhibitory one
            neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron-1],
                0.3);
          }
        }
      }
    }
  }

  private void connectVPFromVPPPLocToLocFeature(int instance, int ruleStart,
      CABot3Net verbInstanceNet) {
    for (int neuron = ruleStart; neuron < ruleStart + 100; neuron++) {
      if (neuron % 5 != 0) { // skip the inhibitory ones
        int toNeuron = (neuron % 20);
        toNeuron += (instance * vInstCASize) + 240;
        neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron], 0.3);
        if (toNeuron % 5 == 1) { // make up for the inhibitory one
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron - 1],
              0.3); // undone weights not update from parse4
        }
      }
    }
  }

  private void connectVPFromVPPPInstToInstFeature(int instance, int ruleStart,
      CABot3Net verbInstanceNet) {
    for (int neuron = ruleStart; neuron < ruleStart + 100; neuron++) {
      if (neuron % 5 != 0) { // skip the inhibitory ones
        int toNeuron = (neuron % 20);
        toNeuron += (instance * vInstCASize) + 220;
        neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron], 0.4);
        if (toNeuron % 5 == 1) { // make up for the inhibitory one
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron - 1],
              0.4);
        }
      }
    }
  }

  private void connectVPFromNPActVPToActFeature(int instance, int ruleStart,
      CABot3Net verbInstanceNet) {
    for (int neuron = ruleStart; neuron < ruleStart + 100; neuron++) {
      if (neuron % 5 != 0) { // skip the inhibitory ones
        int toNeuron = (neuron % 20);
        toNeuron += (instance * vInstCASize) + 160;
        neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron], 0.6);
        if (toNeuron % 5 == 1) { // make up for the inhibitory one
          neurons[neuron].addConnection(verbInstanceNet.neurons[toNeuron - 1],
              0.6);
        }
      }
    }
  }

  public void connectRuleTwoToVerbInstance(CABot3Net verbInstanceNet) {
    connectVPFromVPNPObjToObjFeature(300, verbInstanceNet);
    connectVPFromVPNPObjToObjFeature(400, verbInstanceNet);
    //undone these aren't set for iset yet
    connectVPFromNPActVPToActFeature(0, 600, verbInstanceNet);  
    connectVPFromVPPPLocToLocFeature(0, 900, verbInstanceNet);
    connectVPFromVPPPLocToLocFeature(0, 1000, verbInstanceNet);
    connectVPFromVPPPLocToLocFeature(0, 1100, verbInstanceNet);
    connectVPFromVPPPInstToInstFeature(0, 1500, verbInstanceNet);
    connectVPFromVPPPInstToInstFeature(0, 1600, verbInstanceNet);
    connectVPFromVPPPInstToInstFeature(0, 1700, verbInstanceNet);
  }

  private void connectMainNounToWord(int instance, int word,
      CABot3Net nounAccessNet) {
    for (int neuron = 126; neuron < 135; neuron++) {
      if ((neuron % 5) != 0) {
        // each of the 50 access neurons (5-9, 15-19 ...95-99)gets 8 connections
        // connect to the right side of the feature because the left
        // side will activate during binding.
        for (int accessGroup = 0; accessGroup < 5; accessGroup++) {
          int fromNeuron = neuron + (instance * nInstCASize);
          for (int synapse = 0; synapse < 5; synapse++) {
            int toNeuron = word * 100;
            toNeuron += accessGroup * 20;
            toNeuron += synapse + 5;
            neurons[fromNeuron].addConnection(nounAccessNet.neurons[toNeuron],
                0.01);
            toNeuron += 10;
            neurons[fromNeuron].addConnection(nounAccessNet.neurons[toNeuron],
                0.01);
          }
        }
      }
    }
  }

  public void connectNounInstanceToNounAccess(CABot3Net nounAccessNet) {
    for (int instance = 0; instance < 15; instance++) {
      for (int word = 0; word < numNouns; word++) {
        // connect main verb feature to verbAccess
        connectMainNounToWord(instance, word, nounAccessNet);
      }
    }
  }

  private void adjSlotStopsAddAdjRule( CABot3Net ruleOneNet) {
    for (int nInstCA=0; nInstCA < nounInstanceCAs; nInstCA ++ ) { 
      for (int synapse = 0; synapse < 100 ; synapse ++) {
        int toNeuron = synapse + 800; //the addadj rule
        int fNeur = 180+(nInstCA*nInstCASize);
        neurons[fNeur].addConnection(ruleOneNet.neurons[toNeuron],-4.0);
        neurons[fNeur+15].addConnection(ruleOneNet.neurons[toNeuron],-4.0);
      }
    }
  }

  public void connectNounInstanceToRuleOne(CABot3Net ruleOneNet) {
    adjSlotStopsAddAdjRule(ruleOneNet);
  }

  private void connectNounInstanceToRule(int fromInstance, double weight,
      int toStart, double dynamicWeight, CABot3Net ruleNet) {
    for (int iSet=0 ; iSet < instanceCounterSets; iSet++) {
      int fromStart = fromInstance * nInstCASize;
      // for each neuron in the body 40 excitatory neurons
      // half of the rule neurons get 4 connections (the rest 0)
      for (int neuron = 0; neuron < 40; neuron++) {
        int fromNeuron = neuron + fromStart +(iSet*3*nInstCASize) ; 
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = ((synapse * 40) + neuron); // equally distribute from
          // 0-200
          toNeuron %= 100; // equally distribute 0-100
          if ((toNeuron % 10) >= 5)
            toNeuron -= 5; // just the first 5 of any 10
          toNeuron += toStart;
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], weight);
        }
      }
      if (dynamicWeight != 0){
        // Connect the dynamic part
        for (int neuronPair = 0; neuronPair < 80; neuronPair++) {
          int fromNeuron = (neuronPair * 2) + nounInstanceTimeStart + fromStart +
          +(iSet*3*nInstCASize);
          int toNeuron = toStart;
          for (int synapse = 0; synapse < 25; synapse++) {
            neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],
                dynamicWeight);
            if ((toNeuron % 10) == 4)
              toNeuron += 5;
            toNeuron++;
            neurons[fromNeuron + 1].addConnection(ruleNet.neurons[toNeuron],
                dynamicWeight);
            if ((toNeuron % 10) == 4)
              toNeuron += 5;
            toNeuron++;
          }
        }
      }
    }
  }

  // Connect each time neuron in the instance to the rule.
  private void connectNounInstanceTimeToRule(int fromInstance, double weight,
      int toStart, CABot3Net ruleNet) {
    int fromStart = fromInstance * nInstCASize + 300;
    // for each neuron in time (160) excitatory neurons
    // half of the rule neurons get 4 connections (the rest 0)
    for (int neuron = 0; neuron < 160; neuron++) {
      int fromNeuron = neuron + fromStart;
      for (int synapse = 0; synapse < 50; synapse++) {
        int toNeuron = (synapse / 5) * 10; // 0,10,20,..90
        toNeuron += (synapse % 5); // 0..4,10..14,.., 90..94
        toNeuron += toStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], weight);
      }
    }
  }

  private void connectPrepOnToRule(int inst, int ruleStart, CABot3Net ruleNet) {
    int fromStart = inst * nInstCASize;
    for (int neuron = 61; neuron < 80; neuron++) {
      int fromNeuron = neuron + fromStart;
      if (!neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 7; synapse++) { // 16 neurons to 50*2
          int toNeuron = (neuron - 60); // 1-4, 6-9, 11-14 16-19
          toNeuron -= (toNeuron / 5) + 1; // 0-15
          if (toNeuron < 8) {
            toNeuron %= 4; //
            toNeuron = (toNeuron * 7) + synapse;// 0-27
            toNeuron = ((toNeuron / 5) * 10) + (toNeuron % 5); // 0..4,10..14,
            if (toNeuron >= 50)
              toNeuron = 101;
          } else {
            toNeuron %= 4; //
            toNeuron = (toNeuron * 7) + synapse;// 0-27
            toNeuron = ((toNeuron / 5) * 10) + (toNeuron % 5); // 0..4,10..14,
            if (toNeuron >= 50)
              toNeuron = 101;
            toNeuron += 50; // 50..54, 60..64..
          }
          if (toNeuron < 100) {
            toNeuron += ruleStart;// VPVPPPLoc offset
            neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], .5);
          }
        }
      }
    }
  }

  private void connectPrepOnPreventVPNPRule(int inst, CABot3Net ruleNet,
      int ruleStart) {
    int fromStart = inst * vInstCASize;
    for (int neuron = 60; neuron < 79; neuron += 5) {
      int fromNeuron = neuron + fromStart;
      for (int synapse = 0; synapse < 13; synapse++) { // 4 neurons to 50
        int toNeuron = (neuron % 20) / 5; // 0,1,2,3
        toNeuron = (toNeuron * 13) + synapse;// 0-51
        toNeuron = ((toNeuron / 5) * 10) + (toNeuron % 5); // 0..4,10..14,
        toNeuron %= 100; // keep in the rule
        toNeuron += ruleStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], -3);
      }
    }
  }

  // The bound slot turns off (or prevents) rules that would use it.
  private void boundInstanceStopsRule(int instance, int ruleStart,
      CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 4; neuron++) {
      int fromNeuron = (neuron * 5) + (instance * nInstCASize);
      fromNeuron += 280; // bound slot 280
      for (int synapse = 0; synapse < 25; synapse++) {
        int toNeuron = (neuron * 25) + synapse + ruleStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], -2.0);
      }
    }
  }

  private void boundNounInstancesSlotStopsRules(CABot3Net ruleNet) {
    boundInstanceStopsRule(0, 300, ruleNet);
    boundInstanceStopsRule(1, 400, ruleNet);
    boundInstanceStopsRule(0, 600, ruleNet);
    boundInstanceStopsRule(0, 900, ruleNet);
    boundInstanceStopsRule(1, 1000, ruleNet);
    boundInstanceStopsRule(2, 1100, ruleNet);
    boundInstanceStopsRule(1, 1200, ruleNet);
    boundInstanceStopsRule(2, 1300, ruleNet);
    boundInstanceStopsRule(0, 1500, ruleNet);
    boundInstanceStopsRule(1, 1600, ruleNet);
    boundInstanceStopsRule(2, 1700, ruleNet);
  }

  // this only works for the done slot and the PP rule
  private void doneNounInstanceStopsRule(int ruleStart, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 4; neuron++) {
      int fromNeuron = (neuron * 5);
      fromNeuron += 180; // done slot 180
      for (int synapse = 0; synapse < 25; synapse++) {
        int toNeuron = (neuron * 25) + synapse + ruleStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], -2.0);
      }
    }
  }

  public void connectNounInstanceToRuleTwo(CABot3Net ruleNet) {
    connectNounInstanceToRule(0, 0.185, 300, 0.00125, ruleNet); //VP->VPObj 1 1
    connectNounInstanceToRule(1, 0.185, 400, 0.00125, ruleNet); //VP->VPObj 1 2
    connectNounInstanceToRule(0, 0.185, 600, 0.0, ruleNet); // VP -> NPActVP 11
    connectNounInstanceToRule(0, 0.1, 900, 0.0, ruleNet); // VP -> VPPPloc 1 1
    connectNounInstanceToRule(1, 0.1, 1000, 0.0, ruleNet); // VP -> VPPPloc 1 2
    connectNounInstanceToRule(2, 0.1, 1100, 0.0, ruleNet); // VP -> VPPPloc 1 3

    connectPrepOnToRule(0, 900, ruleNet);// VP -> VPPPloc 1 1
    connectPrepOnToRule(1, 1000, ruleNet);// VP -> VPPPloc 1 2
    connectPrepOnToRule(2, 1100, ruleNet);// VP -> VPPPloc 1 3
    connectNounInstanceToRule(0, 0.08, 1500, 0.0, ruleNet); // VP->VPPPinst 1 1
    connectNounInstanceToRule(1, 0.08, 1600, 0.0, ruleNet); // VP->VPPPinst 1 2
    connectNounInstanceToRule(2, 0.08, 1700, 0.0, ruleNet); // VP->VPPPinst 1 3
    connectPrepOnToRule(0, 1500, ruleNet);// VP -> VPPPinst 1 1
    connectPrepOnToRule(1, 1600, ruleNet);// VP -> VPPPinst 1 2
    connectPrepOnToRule(2, 1700, ruleNet);// VP -> VPPPinst 1 3

    connectNounInstanceToRule(0, 0.185, 1200, 0.0, ruleNet); // NP -> NPPP 1 2
    connectNounInstanceToRule(1, 0.145, 1200, 0.0, ruleNet); // NP -> NPPP 1 2
    connectNounInstanceToRule(1, 0.185, 1300, 0.0, ruleNet); // NP -> NPPP 2 3
    connectNounInstanceToRule(2, 0.145, 1300, 0.0, ruleNet); // NP -> NPPP 2 3
    connectPrepOnToRule(1, 1200, ruleNet);// NP -> NPPP 1 2
    connectPrepOnToRule(2, 1300, ruleNet);// NP -> NPPP 2 3

    connectPrepOnPreventVPNPRule(0, ruleNet, 300);
    connectPrepOnPreventVPNPRule(1, ruleNet, 400);
    connectPrepOnPreventVPNPRule(0, ruleNet, 600);
    boundNounInstancesSlotStopsRules(ruleNet);
    doneNounInstanceStopsRule(1200, ruleNet);
  }

  private void connectPrepFeatureToAPrep(int word, CABot3Net otherNet) {
    for (int instance = 0; instance < 2; instance++) {
      for (int neuron = 106; neuron < 114; neuron++) {
        if ((neuron % 5) != 0) {
          // each of the 50 access neurons (5-9, 15-19 ...95-99)gets 8 conns
          // connect to the right side of the feature because the left
          // side will activate during binding.
          for (int accessGroup = 0; accessGroup < 5; accessGroup++) {
            int fromNeuron = neuron + (instance * nInstCASize);
            for (int synapse = 0; synapse < 5; synapse++) {
              int toNeuron = word * 100;
              toNeuron += accessGroup * 20;
              toNeuron += synapse + 5;
              neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron],
                  0.01);
              toNeuron += 10;
              neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron],
                  0.01);
            }
          }
        }
      }
    }
  }

  private void connectAdjFeatureToAnAdj(int word, CABot3Net otherNet) {
    for (int nInstCA = 0; nInstCA < nounInstanceCAs; nInstCA++) {
      for (int neuron = 166; neuron < 174; neuron++) {
        if ((neuron % 5) != 0) {
          // each of the 50 access neurons (5-9, 15-19 ...95-99)gets 8 conns
          // connect to the right side of the feature because the left
          // side will activate during binding.
          for (int accessGroup = 0; accessGroup < 5; accessGroup++) {
            int fromNeuron = neuron + (nInstCA * nInstCASize);
            for (int synapse = 0; synapse < 5; synapse++) {
              int toNeuron = word * 100;
              toNeuron += accessGroup * 20;
              toNeuron += synapse + 5;
              neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron],
                  0.01);
              toNeuron += 10;
              neurons[fromNeuron].addConnection(otherNet.neurons[toNeuron],
                  0.01);
            }
          }
        }
      }
    }
  }

  public void connectNounInstanceToOther(CABot3Net otherNet) {
    connectPrepFeatureToAPrep(1, otherNet); // toward
    connectPrepFeatureToAPrep(3, otherNet); // with
    connectPrepFeatureToAPrep(4, otherNet); // to
    connectAdjFeatureToAnAdj(6,otherNet);//striped
    connectAdjFeatureToAnAdj(7,otherNet);//barred
  }

  // The active access word primes but does not activate the word.
  // 10 connections to 1 neuron. .125 gives 1.25 each cycle, so 1.25, 1.8725,
  // ... < 3
  private void connectVerbAccessWordToActVInstRule(int word, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (word * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        // VInst Rule is 400-500
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize) + 400;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.125);
      }
    }
  }

  // like connectNounAccessWordToNPFromN
  private void connectVerbAccessWordToMainVerb(int word, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (word * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize) + 500;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.125);
      }
    }
  }

  private void connectVerbAccessWordToVPDone(int word, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (word * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize) + 600;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.11);
      }
    }
  }

  public void connectVerbAccessToRule(CABot3Net ruleNet) {
    for (int word = 0; word < numVerbs; word++) {
      connectVerbAccessWordToActVInstRule(word, ruleNet);
      connectVerbAccessWordToMainVerb(word, ruleNet);
      connectVerbAccessWordToVPDone(word, ruleNet);
    }
  }

  // Each access has to get connections from either side of the
  // instance so it can bind.
  public void connectVerbInstanceToVerbAccess(CABot3Net verbAccessNet) {
    for (int instance = 0; instance < verbInstanceCAs; instance++) {
      for (int word = 0; word < numVerbs; word++) {
        // connect main verb feature to verbAccess
        for (int neuron = 126; neuron < 134; neuron++) {
          if ((neuron % 5) != 0)
            // each of the 50 access neurons (5-9, 15-19 ...)gets 8 connections
            // connect to the right side of the feature because the left
            // side will activate during binding.
            for (int accessGroup = 0; accessGroup < 5; accessGroup++) {
              int fromNeuron = neuron + (instance * vInstCASize);
              for (int synapse = 0; synapse < 5; synapse++) {
                int toNeuron = word * 100;
                toNeuron += accessGroup * 20;
                toNeuron += synapse + 5;
                neurons[fromNeuron].addConnection(
                    verbAccessNet.neurons[toNeuron], 0.01);
                toNeuron += 10;
                neurons[fromNeuron].addConnection(
                    verbAccessNet.neurons[toNeuron], 0.01);
              }
            }
        }
      }
    }
  }

  private void connectAVerbInstanceToRule(int fromStart, double weight,
      int toStart, double dynamicWeight, CABot3Net ruleNet) {
    for (int verbInst = 0; verbInst < 5; verbInst++) {
      // for each neuron in the body 40 excitatory neurons
      // half of the rule neurons get 4 connections (the rest 0)
      for (int neuron = 0; neuron < 40; neuron++) {
        int fromNeuron = neuron + fromStart + (verbInst*vInstCASize);
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = ((synapse * 40) + neuron); // equally distribute from
          // 0-200
          toNeuron %= 100; // equally distribute 0-100
          if ((toNeuron % 10) >= 5)
            toNeuron -= 5; // just the first 5 of any 10
          toNeuron += toStart;
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], weight);
        }
      }
      if (dynamicWeight != 0) {
        // Connect the dynamic part
        for (int neuronPair = 0; neuronPair < 80; neuronPair++) {
          int fromNeuron = (neuronPair * 2) + nounInstanceTimeStart+fromStart +
          (verbInst*vInstCASize);
          int toNeuron = toStart;
          for (int synapse = 0; synapse < 25; synapse++) {
            neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],
                dynamicWeight);
            if ((toNeuron % 10) == 4)
              toNeuron += 5;
            toNeuron++;
            neurons[fromNeuron + 1].addConnection(ruleNet.neurons[toNeuron],
                dynamicWeight);
            if ((toNeuron % 10) == 4)
              toNeuron += 5;
            toNeuron++;
          }
        }
      }
    }
  }

  private void verbSlotInhibitsRule(int slotStart, int ruleStart,
      CABot3Net ruleNet) {
    //undone iset not in yet
    for (int i = 0; i < 4; i++) {
      int fromNeuron = (i * 5) + slotStart;
      for (int synapse = 0; synapse < 25; synapse++) {
        int toNeuron = (i * 25) + synapse + ruleStart;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], -1.5);
      }
    }
  }

  public void connectVerbInstanceToRuleTwo(CABot3Net ruleNet) {
    int instStart = 0; // vInstCASize;
    connectAVerbInstanceToRule(0, 0.18, 0, 0.0, ruleNet); // S ->VP-Period .25
    connectAVerbInstanceToRule(0, 0.165, 300, 0.0, ruleNet); // VP -> VPObj 1 1
    connectAVerbInstanceToRule(0, 0.165, 400, 0.0, ruleNet); // VP -> VPObj 1 2
    connectAVerbInstanceToRule(0, 0.17, 600, 0.001, ruleNet); // VP->NPactVP 1 1
    connectAVerbInstanceToRule(0, 0.14, 900, 0.0, ruleNet); // VP->VPPPloc 1 1
    connectAVerbInstanceToRule(0, 0.14, 1000, 0.0, ruleNet); // VP->VPPPloc 1 2
    connectAVerbInstanceToRule(0, 0.14, 1100, 0.0, ruleNet); // VP->VPPPloc 1 3
    connectAVerbInstanceToRule(0, 0.14, 1500, 0.0, ruleNet); // VP->VPPPinst 11
    connectAVerbInstanceToRule(0, 0.14, 1600, 0.0, ruleNet); // VP->VPPPinst 12
    connectAVerbInstanceToRule(0, 0.14, 1700, 0.0, ruleNet); // VP->VPPPinst 13

    // undone why don't obj and act done inhibit their rules
    verbSlotInhibitsRule(100, 1000, ruleNet);
  }

  // Each Verb has 5 features that can bind to NInstance (actor, object,
  // instrument,location,time)
  // Each slot binds to the slot
  // Each neuron needs 10 connections. (5 to each side)
  private void connectAVInstanceSlotToANInstance(int fromOffset, int vInst,
      int nInst, CABot3Net ruleNet) {
    for (int neuron = 6; neuron < 15; neuron++) {
      int fromNeuron = (vInst * vInstCASize) + fromOffset + neuron;

      if (!neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 10; synapse++) {
          int toNeuron = synapse;
          if (synapse >= 5)
            toNeuron += 5;
          toNeuron += (nInst * nInstCASize) + 260;
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.02);
        }
      }
    }
  }

  private void connectAVInstanceToANInstance(int vInst, int nInst,
      CABot3Net ruleNet) {
    connectAVInstanceSlotToANInstance(160, vInst, nInst, ruleNet); // actor slot
    connectAVInstanceSlotToANInstance(180, vInst, nInst, ruleNet); // object
    // slot
    connectAVInstanceSlotToANInstance(220, vInst, nInst, ruleNet); // instrum.
    // slot
    connectAVInstanceSlotToANInstance(240, vInst, nInst, ruleNet); // loc slot
  }

  public void connectVerbInstanceToNounInstance(CABot3Net ruleNet) {
    for (int iSet=0 ; iSet < instanceCounterSets; iSet++) {
      int vInstance = iSet;
      for (int nInstance = 0; nInstance < 3; nInstance++) {
        connectAVInstanceToANInstance(vInstance, nInstance+(iSet*3),ruleNet);
      }
    }
  }

  private void connectPeriodToSFromVPPeriod(CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * 10);
        neurons[neuron].addConnection(ruleNet.neurons[toNeuron], 0.125);
      }
    }
  }

  private void connectAPrepToNInstFromPrep(int otherCA, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (otherCA * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.125);
      }
    }
  }

  private void connectAPrepToNPAddPrep(int word, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (word * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize) + 900;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.125);
      }
    }
  }

  private void connectAPrepToPrepDone(int word, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (word * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize) + 1000;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.11);
      }
    }
  }

  private void connectPrepsToRules(CABot3Net ruleNet) {
    connectAPrepToNInstFromPrep(1, ruleNet);
    connectAPrepToNPAddPrep(1, ruleNet);
    connectAPrepToPrepDone(1, ruleNet);
    connectAPrepToNInstFromPrep(3, ruleNet);
    connectAPrepToNPAddPrep(3, ruleNet);
    connectAPrepToPrepDone(3, ruleNet);
    connectAPrepToNInstFromPrep(4, ruleNet);
    connectAPrepToNPAddPrep(4, ruleNet);
    connectAPrepToPrepDone(4, ruleNet);
    connectAPrepToNInstFromPrep(8, ruleNet);
    connectAPrepToNPAddPrep(8, ruleNet);
    connectAPrepToPrepDone(8, ruleNet);
  }

  private void connectADetToNPAddDet(int word, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (word * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize) + 1100;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.125);
      }
    }
  }

  private void connectADetToNInstFromDet(int otherCA, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (otherCA * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.125);
      }
    }
  }

  private void connectDetsToRules(CABot3Net ruleNet) {
    connectADetToNPAddDet(2, ruleNet);
    connectADetToNInstFromDet(2, ruleNet);
  }

  private void connectAAdjToNPAddAdj(int word, CABot3Net ruleNet) {
    for (int neuron = 0; neuron < 100; neuron++) {
      int fromNeuron = (word * 100) + neuron;
      for (int rule1Feature = 0; rule1Feature < 10; rule1Feature++) {
        int toNeuron = (neuron % 5) + (rule1Feature * ruleFeatureSize) + 800;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], 0.25);
      }
    }
  }

  private void connectAdjsToRules(CABot3Net ruleNet) {
    connectAAdjToNPAddAdj(5, ruleNet);
    connectAAdjToNPAddAdj(6, ruleNet);
    connectAAdjToNPAddAdj(7, ruleNet);
  }

  public void connectOtherToRuleOne(CABot3Net ruleNet) {
    connectPrepsToRules(ruleNet);
    connectDetsToRules(ruleNet);
    connectAdjsToRules(ruleNet);
  }

  public void connectOtherToRuleTwo(CABot3Net ruleNet) {
    connectPeriodToSFromVPPeriod(ruleNet);
  }

  private void connectASemToARuleSel(int word, int pref, double weight,
      CABot3Net vPPPNet) {
    for (int i = 0; i < 60; i++) {
      int fromNeuron = (word * 60) + i;
      if (!neurons[fromNeuron].isInhibitory) {
        int toNeuron = i;
        toNeuron += (pref * preferenceCASize);
        neurons[fromNeuron].addConnection(vPPPNet.neurons[toNeuron], weight);
        if ((i % 5) == 1)
          neurons[fromNeuron].addConnection(vPPPNet.neurons[toNeuron - 1],
              weight);
      }
    }
  }

  public void connectVerbSemToVPPP(CABot3Net vPPPNet) {
    connectASemToARuleSel(0, 0, 0.4, vPPPNet); // MOVE it stalactite
    connectASemToARuleSel(3, 1, 0.4, vPPPNet); // SAW girl telescope
  }

  public void connectNounSemToVPPP(CABot3Net vPPPNet) {
    connectASemToARuleSel(2, 0, 0.4, vPPPNet); // move IT stalactite
    connectASemToARuleSel(3, 0, 0.8, vPPPNet); // move it STALACTITE
    connectASemToARuleSel(6, 1, 0.4, vPPPNet); // saw GIRL telescope
    connectASemToARuleSel(7, 1, 0.8, vPPPNet); // saw girl TELESCOPE
  }

  public void connectVerbSemToNPPP(CABot3Net nPPPNet) {
    connectASemToARuleSel(0, 1, 0.4, nPPPNet); // MOVE door handle
  }

  public void connectNounSemToNPPP(CABot3Net nPPPNet) {
    connectASemToARuleSel(8, 1, 0.4, nPPPNet); // move DOOR handle
    connectASemToARuleSel(9, 1, 0.8, nPPPNet); // move door HANDLE
  }

  private void connectAVPPPToARuleTwo(CABot3Net ruleNet, int vPPP, int rule) {
    for (int i = 0; i < preferenceCASize; i++) {
      int fromNeuron = vPPP + i;
      if (i < 50) {
        int toNeuron1 = (i % 5) + ((i / 5) * 10) + rule;
        int toNeuron2 = toNeuron1 + 10;
        if ((i % 10) > 4)
          toNeuron2 -= 20;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1], 0.32);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2], 0.32);
        toNeuron1 += 100;
        toNeuron2 += 100;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1], 0.32);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2], 0.32);
        toNeuron1 += 100;
        toNeuron2 += 100;
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1], 0.32);
        neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2], 0.32);
      }
    }
  }

  public void connectVPPPToRuleTwo(CABot3Net ruleNet) {
    connectAVPPPToARuleTwo(ruleNet, 0, 900); // Move it toward statlagite loc
    connectAVPPPToARuleTwo(ruleNet, 60, 1500); // saw girl telescope inst
  }

  public void connectNPPPToRuleTwo(CABot3Net ruleNet) {
    int prefCAs = getSize() / preferenceCASize;
    for (int prefCA = 0; prefCA < prefCAs; prefCA++) {
      for (int i = 0; i < preferenceCASize; i++) {
        int fromNeuron = (prefCA * preferenceCASize) + i;
        if (i < 50) {
          int toNeuron1 = (i % 5) + ((i / 5) * 10) + 1200;
          int toNeuron2 = toNeuron1 + 10;
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1], 0.2);
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2], 0.2);
          toNeuron1 += 100;
          toNeuron2 += 100;
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron1], 0.2);
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron2], 0.2);
        }
      }
    }
  }

  // -----------Set up topology for networks-------------------
  /***** end of intra parsing subnet connections **/
  private void setGoalSetTopology() {
    setFiftyFiftyTopology(1.1);
  }

  public void readBetweenAllNets() {
    System.out.println("CABot 3 read Between");
    int netsChecked = 0;
    Enumeration eNum = CANT23.nets.elements();
    CABot3Net net = (CABot3Net) eNum.nextElement();

    CABot3Net inputNet = net;
    CABot3Net barOneNet = net;
    CABot3Net nounAccessNet = net;
    CABot3Net verbAccessNet = net;
    CABot3Net nounSemNet = net;
    CABot3Net verbSemNet = net;
    CABot3Net otherNet = net;
    CABot3Net nounInstanceNet = net;
    CABot3Net verbInstanceNet = net;
    CABot3Net ruleTwoNet = net;
    CABot3Net vPPPNet = net;
    CABot3Net nPPPNet = net;
    CABot3Net visualInputNet = net;
    CABot3Net retinaNet = net;
    CABot3Net V1Net = net;
    CABot3Net objRecNet = net;
    CABot3Net controlNet = net;
    CABot3Net goal1Net = net;
    CABot3Net factNet = net;
    CABot3Net goalSetNet = net;
    CABot3Net moduleNet = net;
    CABot3Net actionNet = net;
    CABot3Net goal2Net = net;
    CABot3Net valueNet = net;
    CABot3Net value2Net = net;
    CABot3Net module2Net = net;
    CABot3Net exploreNet = net;
    CABot3Net v1LinesNet = net;
    CABot3Net nextWordNet = net;
    CABot3Net counterNet = net;
    CABot3Net room2Net = net;
    CABot3Net room1Net = net;
    CABot3Net ruleOneNet = net;
    CABot3Net cogSeqNet = net;
    CABot3Net instanceCounterNet = net;
    CABot3Net gratingsNet = net;

    do {
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
      else if (net.getName().compareTo("RuleTwoNet") == 0)
        ruleTwoNet = net;
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
      else if (net.getName().compareTo("ObjRecNet") == 0)
        objRecNet = net;
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
      else if (net.getName().compareTo("V1LinesNet") == 0)
        v1LinesNet = net;
      else if (net.getName().compareTo("NextWordNet") == 0)
        nextWordNet = net;
      else if (net.getName().compareTo("CounterNet") == 0)
        counterNet = net;
      else if (net.getName().compareTo("Room2Net") == 0)
        room2Net = net;
      else if (net.getName().compareTo("RoomNet") == 0)
        room1Net = net;
      else if (net.getName().compareTo("RuleOneNet") == 0)
        ruleOneNet = net;
      else if (net.getName().compareTo("CogSeqNet") == 0)
        cogSeqNet = net;
      else if (net.getName().compareTo("InstanceCounterNet") == 0)
        instanceCounterNet = net;
      else if (net.getName().compareTo("GratingsNet") == 0)
        gratingsNet = net;
      else
        System.out.println(net.getName() + " missed net in connect all");
      netsChecked++;
      if (netsChecked < 36)
        net = (CABot3Net) eNum.nextElement();
    } while (netsChecked < 36);

    // connectparseNets
    inputNet.readConnectTo(nounAccessNet);
    inputNet.readConnectTo(verbAccessNet);
    inputNet.readConnectTo(otherNet);
    barOneNet.readConnectTo(nounAccessNet);
    barOneNet.readConnectTo(verbAccessNet);
    barOneNet.readConnectTo(otherNet);
    barOneNet.readConnectTo(ruleOneNet);
    barOneNet.readConnectTo(ruleTwoNet);
    nounAccessNet.readConnectTo(nounSemNet);
    nounAccessNet.readConnectTo(ruleOneNet);
    ruleOneNet.readConnectTo(barOneNet);
    ruleOneNet.readConnectTo(nounInstanceNet);
    ruleOneNet.readConnectTo(nounAccessNet);
    ruleOneNet.readConnectTo(verbInstanceNet);
    ruleOneNet.readConnectTo(verbAccessNet);
    ruleOneNet.readConnectTo(otherNet);
    ruleOneNet.readConnectTo(instanceCounterNet);
    ruleOneNet.readConnectTo(nextWordNet);
    instanceCounterNet.readConnectTo(verbInstanceNet);
    instanceCounterNet.readConnectTo(nounInstanceNet);
    ruleTwoNet.readConnectTo(nounInstanceNet);
    ruleTwoNet.readConnectTo(verbInstanceNet);
    ruleTwoNet.readConnectTo(nextWordNet);
    nextWordNet.readConnectTo(ruleOneNet);
    nounInstanceNet.readConnectTo(nounAccessNet);
    nounInstanceNet.readConnectTo(ruleOneNet);
    nounInstanceNet.readConnectTo(ruleTwoNet);
    nounInstanceNet.readConnectTo(otherNet);
    verbAccessNet.readConnectTo(verbSemNet);
    verbAccessNet.readConnectTo(ruleOneNet);
    verbInstanceNet.readConnectTo(verbAccessNet);
    verbInstanceNet.readConnectTo(ruleTwoNet);
    verbInstanceNet.readConnectTo(nounInstanceNet);
    otherNet.readConnectTo(ruleOneNet);
    otherNet.readConnectTo(ruleTwoNet);
    verbSemNet.readConnectTo(vPPPNet);
    nounSemNet.readConnectTo(vPPPNet);
    verbSemNet.readConnectTo(nPPPNet);
    nounSemNet.readConnectTo(nPPPNet);
    vPPPNet.readConnectTo(ruleTwoNet);
    nPPPNet.readConnectTo(ruleTwoNet);

    // connect vision nets
    visualInputNet.readConnectTo(retinaNet);
    retinaNet.readConnectTo(V1Net);
    retinaNet.readConnectTo(objRecNet);
    gratingsNet.readConnectTo(V1Net);
    V1Net.readConnectTo(objRecNet);
    retinaNet.readConnectTo(v1LinesNet);
    visualInputNet.readConnectTo(v1LinesNet);
    gratingsNet.readConnectTo(v1LinesNet);
    gratingsNet.readConnectTo(objRecNet);
    v1LinesNet.readConnectTo(gratingsNet);

    // integrate parse3 with goal setting
    verbSemNet.readConnectTo(goalSetNet);
    nounSemNet.readConnectTo(goalSetNet);
    otherNet.readConnectTo(goalSetNet);
    goalSetNet.readConnectTo(goal1Net);

    // connect visual items to facts
    objRecNet.readConnectTo(factNet);
    factNet.readConnectTo(objRecNet);
    gratingsNet.readConnectTo(factNet);

    // connect facts, modules and actions
    goal1Net.readConnectTo(moduleNet);
    goal1Net.readConnectTo(factNet);
    factNet.readConnectTo(goal1Net);
    factNet.readConnectTo(moduleNet);
    moduleNet.readConnectTo(goal1Net);
    moduleNet.readConnectTo(factNet);
    moduleNet.readConnectTo(actionNet);
    actionNet.readConnectTo(moduleNet);

    // set up goal learning
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

    //hook control up to parsing
    ruleTwoNet.readConnectTo(controlNet);
    controlNet.readConnectTo(barOneNet);
    controlNet.readConnectTo(ruleOneNet);
    controlNet.readConnectTo(ruleTwoNet);
    controlNet.readConnectTo(otherNet);
    controlNet.readConnectTo(nounAccessNet);
    controlNet.readConnectTo(verbAccessNet);
    controlNet.readConnectTo(nounSemNet);
    controlNet.readConnectTo(verbSemNet);
    controlNet.readConnectTo(nounInstanceNet);
    controlNet.readConnectTo(verbInstanceNet);
    controlNet.readConnectTo(instanceCounterNet);
    controlNet.readConnectTo(nextWordNet);

    //hook control up to goal setting.
    controlNet.readConnectTo(goalSetNet);
    controlNet.readConnectTo(value2Net);
    value2Net.readConnectTo(controlNet);

    //connect planning to cognitive mapping
    factNet.readConnectTo(room1Net);
    factNet.readConnectTo(room2Net);
    counterNet.readConnectTo(cogSeqNet);
    factNet.readConnectTo(room1Net);
    factNet.readConnectTo(counterNet);
    room1Net.readConnectTo(factNet);
    counterNet.readConnectTo(factNet);
    goal1Net.readConnectTo(room1Net);

    //Read the connections from cogMap
    cogSeqNet.readConnectTo(room2Net);
    cogSeqNet.readConnectTo(room1Net);
    room1Net.readConnectTo(cogSeqNet);
    room2Net.readConnectTo(cogSeqNet);
    room2Net.readConnectTo(factNet);


    // Reset any fastbind weights that have been saved.
    CABot3Experiment exp = (CABot3Experiment) CABot3.experiment;
    // exp.clearFastBindNeurons();
  }

  public void runAllOneStep(int CANTStep) {
    // This series of loops is really chaotic, but I needed to
    // get all of the propogation done in each net in step.
    CABot3.runOneStepStart();

    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net) eNum.nextElement();
      // net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net) eNum.nextElement();
      net.setExternalActivation(CANTStep);
    }
    // net.propogateChange();
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net) eNum.nextElement();
      net.spontaneousActivate();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net) eNum.nextElement();
      net.setNeuronsFired();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net) eNum.nextElement();
      net.setDecay();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net) eNum.nextElement();
      net.spreadActivation();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net) eNum.nextElement();
      net.setFatigue();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net) eNum.nextElement();
      net.learn();
      if ((net.getName().compareTo("NounInstanceNet") == 0)
          || (net.getName().compareTo("VerbInstanceNet") == 0))
        net.fastLearn();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net) eNum.nextElement();
      net.cantFrame.runOneStep(CANTStep + 1);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net) eNum.nextElement();
      if (net.recordingActivation)
        net.setMeasure(CANTStep);
      // if (net.getName().compareTo("VerbNet") == 0)
      // System.out.println(net.neurons[0].getFatigue() + " verb Neuron " +
      // net.neurons[0].getActivation());
    }
  }

  private void fastLearn(int firstFast, int lastFast) {
    for (int neuronIndex = 0; neuronIndex < size(); neuronIndex++) {
      if (neurons[neuronIndex] instanceof CANTNeuronFastBind)
        ((CANTNeuronFastBind) neurons[neuronIndex]).fastLearn();
    }
  }

  public CANTNet getNewNet(String name, int cols, int rows, int topology) {
    CABot3Net net = new CABot3Net(name, cols, rows, topology);
    return (net);
  }

  /******** Vision Connectivity Functions */
  int getInputSize() {
    return (2500);
  }

  private int getInputRows() {
    return (50);
  }

  private int getInputCols() {
    return (50);
  }

  private void addOneConnection(int fromRow, int fromCol, CANTNeuron toNeuron,
      double weight) {
    if ((fromRow < 0) || (fromRow >= getRows()) || (fromCol < 0)
        || (fromCol >= getCols()))
      return;

    int inputNeuron = fromRow * getCols() + fromCol;
    neurons[inputNeuron].addConnection(toNeuron, weight);
  }

  private void addOneRetinaConnection(int fromRow, int fromCol, int retCat,
      CANTNeuron toNeuron, double weight) {
    if ((fromRow < 0) || (fromRow >= getInputRows()) || (fromCol < 0)
        || (fromCol >= getInputCols())) {
      // System.out.println(fromRow+" " + fromCol + " put " + retCat);
      return;
    }

    int inputNeuron = (fromRow + (getCols() * retCat)) * getCols() + fromCol;

    neurons[inputNeuron].addConnection(toNeuron, weight);
  }

  private void addOneV1Connection(int fromRow, int fromCol,
      CANTNeuron toNeuron, double weight) {
    if ((fromRow < 0) || (fromCol < 0) || (fromCol >= getInputCols()))
      return;

    int inputNeuron = fromRow * getCols() + fromCol;

    neurons[inputNeuron].addConnection(toNeuron, weight);
  }

  private void addOneV1Connection(int featureNum, int fromRow, int fromCol,
      CANTNeuron toNeuron, double weight) {
    if ((fromRow < 0) || (fromRow >= getInputCols()) || (fromCol < 0)
        || (fromCol >= getInputCols()))
      return;

    int inputNeuron = fromRow * getCols() + fromCol + featureNum
    * getInputSize();

    neurons[inputNeuron].addConnection(toNeuron, weight);
  }

  private void connectInputTo3x3(CABot3Net retinaNet, int start, double onVal,
      double offVal) {
    int row;
    int column;

    for (int inputNeuron = start; inputNeuron < start + size(); inputNeuron++) {
      row = (inputNeuron - start) / getRows();
      column = (inputNeuron - start) % getRows();
      addOneConnection(row, column, retinaNet.neurons[inputNeuron], onVal);

      addOneConnection(row - 1, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 1, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row - 1, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row, column - 1, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row, column + 1, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row + 1, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 1, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row + 1, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
    }
  }

  private void connectInputTo6x6(CABot3Net retinaNet, int start, double onVal,
      double offVal) {
    int row;
    int column;

    for (int inputNeuron = start; inputNeuron < start + size(); inputNeuron++) {
      row = (inputNeuron - start) / getRows();
      column = (inputNeuron - start) % getRows();
      addOneConnection(row, column, retinaNet.neurons[inputNeuron], onVal);
      addOneConnection(row, column + 1, retinaNet.neurons[inputNeuron], onVal);
      addOneConnection(row + 1, column, retinaNet.neurons[inputNeuron], onVal);
      addOneConnection(row + 1, column + 1, retinaNet.neurons[inputNeuron],
          onVal);

      addOneConnection(row, column - 1, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row, column - 2, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row, column + 2, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row, column + 3, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row + 1, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 1, column - 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 1, column + 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 1, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column - 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row + 2, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column + 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column - 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row + 3, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column + 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 1, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 1, column - 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 1, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row - 1, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 1, column + 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 1, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column - 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row - 2, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column + 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
    }
  }

  private void connectInputTo9x9(CABot3Net retinaNet, int start, double onVal,
      double offVal) {
    int row;
    int column;

    for (int inputNeuron = start; inputNeuron < start + size(); inputNeuron++) {
      row = (inputNeuron - start) / getRows();
      column = (inputNeuron - start) % getRows();
      addOneConnection(row, column, retinaNet.neurons[inputNeuron], onVal);
      addOneConnection(row, column + 1, retinaNet.neurons[inputNeuron], onVal);
      addOneConnection(row, column - 1, retinaNet.neurons[inputNeuron], onVal);
      addOneConnection(row + 1, column, retinaNet.neurons[inputNeuron], onVal);
      addOneConnection(row + 1, column - 1, retinaNet.neurons[inputNeuron],
          onVal);
      addOneConnection(row + 1, column + 1, retinaNet.neurons[inputNeuron],
          onVal);
      addOneConnection(row - 1, column, retinaNet.neurons[inputNeuron], onVal);
      addOneConnection(row - 1, column + 1, retinaNet.neurons[inputNeuron],
          onVal);
      addOneConnection(row - 1, column - 1, retinaNet.neurons[inputNeuron],
          onVal);

      addOneConnection(row - 4, column - 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 4, column - 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 4, column - 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 4, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 4, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row - 4, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 4, column + 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 4, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 4, column + 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 3, column - 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 3, column - 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 3, column - 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 3, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 3, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row - 3, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 3, column + 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 3, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 3, column + 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column - 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column - 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column - 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row - 2, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column + 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 2, column + 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 1, column - 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 1, column - 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 1, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row - 1, column + 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row, column -4, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row, column -3, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row, column +3, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row, column +4, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row + 1, column - 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 1, column - 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 1, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 1, column + 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column - 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column - 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column - 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row + 2, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column + 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 2, column + 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column - 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column - 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column - 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row + 3, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column + 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 3, column + 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 4, column - 4, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 4, column - 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 4, column - 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 4, column - 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 4, column, retinaNet.neurons[inputNeuron], offVal);
      addOneConnection(row + 4, column + 1, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 4, column + 2, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 4, column + 3, retinaNet.neurons[inputNeuron],
          offVal);
      addOneConnection(row + 4, column + 4, retinaNet.neurons[inputNeuron],
          offVal);
    }
  }

  public void connectInputToRetina(CABot3Net retinaNet) {
    connectInputTo3x3(retinaNet, 0, 7.1, -1.25);
    connectInputTo3x3(retinaNet, size(), -7.1, 0.89);
    connectInputTo6x6(retinaNet, size() * 2, 1.8, -0.22);
    connectInputTo6x6(retinaNet, size() * 3, -1.8, 0.22);
    connectInputTo9x9(retinaNet, size() * 4, 0.79, -0.099);
    connectInputTo9x9(retinaNet, size() * 5, -0.79, 0.099);
  }

  private void connectRetinaToAndAngle(CABot3Net V1Net) {
    double threeNFV = 0.5;
    double threeFNV = 0.5;
    int row;
    int col;
    int andOffset = getInputSize() * 3;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) {
      row = inputNeuron / getCols();
      col = inputNeuron % getCols();
      int tNeuron = inputNeuron + andOffset;
      for (int i = 0; i < 5; i++) {
        addOneRetinaConnection(row + i, col + i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row + i, col - i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row + i, col + i, 0, V1Net.neurons[tNeuron],
            threeFNV);
        addOneRetinaConnection(row + i, col - i, 0, V1Net.neurons[tNeuron],
            threeFNV);
      }
    }
  }

  private void connectRetinaToLessThanAngle(CABot3Net V1Net) {
    double threeNFV = 0.3;
    double threeFNV = 0.3;
    int row;
    int col;
    int lessThanOffset = getInputSize() * 4;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) {
      row = inputNeuron / getCols();
      col = inputNeuron % getCols();
      int tNeuron = inputNeuron + lessThanOffset;
      for (int i = 0; i < 5; i++) {
        addOneRetinaConnection(row, col + i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row, col + i, 0, V1Net.neurons[tNeuron],
            threeFNV);
        addOneRetinaConnection(row+(2*i), col + i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row+(2*i), col + i, 0, V1Net.neurons[tNeuron],
            threeFNV);
        addOneRetinaConnection(row-(2*i), col + i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row-(2*i), col + i, 0, V1Net.neurons[tNeuron],
            threeFNV);
        addOneRetinaConnection(row + 1, col + i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row + 1, col + i, 0, V1Net.neurons[tNeuron],
            threeFNV);
        addOneRetinaConnection(row - 1, col + i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row - 1, col + i, 0, V1Net.neurons[tNeuron],
            threeFNV);
      }
    }
  }

  private void connectRetinaToGreaterThanAngle(CABot3Net V1Net) {
    double threeNFV = 0.3;
    double threeFNV = 0.3;
    int row;
    int col;
    int greaterThanOffset = getInputSize() * 5;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) {
      row = inputNeuron / getCols();
      col = inputNeuron % getCols();
      int tNeuron = inputNeuron + greaterThanOffset;
      for (int i = 0; i < 5; i++) {
        addOneRetinaConnection(row, col - i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row, col - i, 0, V1Net.neurons[tNeuron],
            threeFNV);
        addOneRetinaConnection(row+(2*i), col - i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row+(2*i), col - i, 0, V1Net.neurons[tNeuron],
            threeFNV);
        addOneRetinaConnection(row-(2*i), col - i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row-(2*i), col - i, 0, V1Net.neurons[tNeuron],
            threeFNV);
        addOneRetinaConnection(row + 1, col - i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row + 1, col - i, 0, V1Net.neurons[tNeuron],
            threeFNV);
        addOneRetinaConnection(row - 1, col - i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row - 1, col - i, 0, V1Net.neurons[tNeuron],
            threeFNV);
      }
    }
  }

  private void connectRetinaToOrAngle(CABot3Net V1Net) {
    double threeNFV = 0.5;
    double threeFNV = 0.5;
    int row;
    int col;
    int orOffset = getInputSize() * 6;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) {
      row = inputNeuron / getCols();
      col = inputNeuron % getCols();
      int tNeuron = inputNeuron + orOffset;
      for (int i = 0; i < 5; i++) {
        addOneRetinaConnection(row - i, col + i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row - i, col - i, 0, V1Net.neurons[tNeuron],
            threeNFV);
        addOneRetinaConnection(row - i, col + i, 0, V1Net.neurons[tNeuron],
            threeFNV);
        addOneRetinaConnection(row - i, col - i, 0, V1Net.neurons[tNeuron],
            threeFNV);
      }
    }
  }

  private void addOneRetinaToV1Connection(int fromNeuron, int toRow, int toCol,
      int toDetector, double weight, CABot3Net V1Net) {
    if ((toRow >= 0) && (toRow < 50) && (toCol >= 0) && (toCol < 50)) {
      int toNeuron = (toRow * 50) + toCol + (toDetector * getInputSize());
      neurons[fromNeuron].addConnection(V1Net.neurons[toNeuron], weight);
    }
  }

  private void connectRetinaToVEdgeL(CABot3Net v1Net) {
    // if there is a "dark side" to the left and a "light side" to the right,
    // the neuron must be on the edge of something dark, extending to the left
    int netSize = getInputSize();
    double exWeight = 0.7;
    double inhibWeight = -1.5;

    int vEdgeLOffset = 10;
    int threeOnOffset = 0;
    int threeOffOffset = 1;

    int threeOnIndex;
    int threeOffIndex;

    for (int v1Neuron = 0; v1Neuron < netSize; v1Neuron++) {
      int column = v1Neuron % getInputCols();

      int row = v1Neuron / getInputCols();
      threeOnIndex = threeOnOffset * netSize + v1Neuron; // (dark on the left)
      threeOffIndex = threeOffOffset * netSize + v1Neuron + 1; // (light on the
      // right)

      for (int r = -2; r < 3; r++) {
        addOneRetinaToV1Connection(threeOnIndex, row + r, column, vEdgeLOffset,
            exWeight, v1Net);
        addOneRetinaToV1Connection(threeOffIndex, row + r, column,
            vEdgeLOffset, exWeight, v1Net);

        for (int c = -7; c < -1; c++) {
          addOneRetinaToV1Connection(threeOnIndex, row+r, column + c, vEdgeLOffset,
              inhibWeight, v1Net);
        }
      }
    }
  }

  private void connectRetinaToVEdgeR(CABot3Net v1Net) {
    // if there is a "dark side" to the right and a "light side" to the left,
    // the neuron must be on the edge of something dark, extending to the right
    int netSize = getInputSize();
    double exWeight = 0.9;
    double inhibWeight = -1.5;

    int vEdgeROffset = 11;
    int threeOnOffset = 0;
    int threeOffOffset = 1;

    int threeOnIndex;
    int threeOffIndex;

    for (int v1Neuron = 0; v1Neuron < netSize; v1Neuron++) {
      int column = v1Neuron % getInputCols();

      int row = v1Neuron / getInputCols();
      threeOnIndex = threeOnOffset * netSize + v1Neuron+1; // (dark on the right)
      threeOffIndex = threeOffOffset * netSize + v1Neuron; // (light on the
      // left)

      for (int r = -2; r < 3; r++) {
        addOneRetinaToV1Connection(threeOnIndex, row + r, column, vEdgeROffset,
            exWeight, v1Net);
        addOneRetinaToV1Connection(threeOffIndex, row + r, column, vEdgeROffset, exWeight, v1Net);

        for (int c = 1; c <7; c++) {
          addOneRetinaToV1Connection(threeOnIndex, row+r, column + c, vEdgeROffset,
              inhibWeight, v1Net);
        }
      }
    }
  }

  public void connectRetinaToUnknownObject(CABot3Net objRecNet){
    int threeOnOffset = 0;
    int unknownObjectOffset = 4;
    double weight = 4.2;

    int netSize = getInputSize();
    int cols = objRecNet.getCols();
    int column;
    int fromRow;
    int toRow;
    int threeOnIndex;

    for(int neuron = 0; neuron < netSize; neuron++){
      column = neuron%cols;
      fromRow = neuron/cols;
      toRow = fromRow;
      if(fromRow%10<5){//connect top half of each 10X10 in retina to bottom half in ObjRec.
        toRow = toRow+5;
      }

      threeOnIndex = threeOnOffset * netSize + neuron;
      addOneRetinaToV1Connection(threeOnIndex, toRow, column, unknownObjectOffset,
          weight, objRecNet); //uses the existing "connect to V1" method with the objRecNet as CABot3Net parameter
    }
  }

  private void connectGratingToEdge(String edgeType, String edgeSide,
      CABot3Net v1Net) {
    // Supress vEdge detection if we are in the middle of a vBar Grating, for a
    // 1 pixel radius.
    int netSize = getInputSize();
    double weight = -3.0;
    int gratingOffset;
    int edgeOffset;
    int supressOffset = 0; // for edges we need to move the supression up,down
    // left and right depending on the edge

    if (edgeType == "h3" || edgeType == "H3") {
      gratingOffset = 0;
    } else if (edgeType == "v3" || edgeType == "V3") {
      gratingOffset = 1;
    } else if (edgeType == "h6" || edgeType == "H6") {
      gratingOffset = 2;
    } else if (edgeType == "v6") {
      gratingOffset = 3;
    } else {
      System.out.println("Error Fix connect Grating To Edge " + edgeType);
      return;
    }

    if (edgeSide == "d" || edgeSide == "D") {
      edgeOffset = 0;
      supressOffset = 2 * v1Net.getCols(); // the from neuron is one row below
      // the to neuron
    } else if (edgeSide == "u" || edgeSide == "U") {
      edgeOffset = 7;
      supressOffset = -2 * v1Net.getCols();// the from neuron is one row above
      // the to neuron
    } else if (edgeSide == "a") {
      edgeOffset = 3;
    } else if (edgeSide == "s") { // lessthan
      edgeOffset = 4;
    } else if (edgeSide == "g") {
      edgeOffset = 5;
    } else if (edgeSide == "o") {
      edgeOffset = 6;
    } else if (edgeSide == "l" || edgeSide == "L") {
      edgeOffset = 10;
      supressOffset = +2;// the from neuron is one to the right of the to neuron
    } else if (edgeSide == "r" || edgeSide == "R") {
      edgeOffset = 11;
      supressOffset = -2;// the from neuron is one to the left of the to neuron
    } else {
      System.out.println("Error Fix connect Grating To Edge " + edgeSide);
      return;
    }

    int gratingIndex;

    for (int v1Neuron = 0; v1Neuron < netSize; v1Neuron++) {
      int column = v1Neuron % getInputCols();
      int row = v1Neuron / getInputCols();
      gratingIndex = gratingOffset * netSize + v1Neuron + supressOffset;
      // for(int xShift = -1; xShift <2; xShift ++){
      // for(int yShift = -1; yShift <2; yShift ++){
      addOneGratingToV1Connection(gratingIndex, row, column, edgeOffset,
          weight, v1Net);
      // addOneGratingToV1Connection(gratingIndex, row+xShift, column+yShift,
      // edgeOffset, weight, v1Net);
      // }
      // }
    }
  }

  private void addOneGratingToV1Connection(int fromNeuron, int toRow,
      int toCol, int toDetector, double weight, CABot3Net V1Net) {
    if ((fromNeuron >= 0) && (fromNeuron <= neurons.length) && (toRow >= 0)
        && (toRow < 50) && (toCol >= 0) && (toCol < 50)) {
      int toNeuron = (toRow * 50) + toCol + (toDetector * getInputSize());
      neurons[fromNeuron].addConnection(V1Net.neurons[toNeuron], weight);
    }
  }

  private void connectRetinaToHEdgeD(CABot3Net v1Net) {
    // if there is a "light side" to the bttom and a "dark side" to the top,
    // the neuron must be on the edge of something dark, extending up
    int netSize = getInputSize();
    double exciteWeight = 0.4;
    double inhibWeight = -1.4;

    int hEdgeDOffset = 0;
    int threeOnOffset = 0;
    int threeOffOffset = 1;

    for (int v1Neuron = 0; v1Neuron < netSize; v1Neuron++) {
      int column = v1Neuron % getInputCols();
      int row = v1Neuron / getInputCols();
      int threeOnIndex = threeOnOffset * netSize + v1Neuron; // (dark on top)
      int threeOffIndex = threeOffOffset * netSize + v1Neuron + getInputCols();
      // (light on the bottom)
      for (int c = -2; c < 3; c++) {
        addOneRetinaToV1Connection(threeOnIndex, row, column + c, hEdgeDOffset,
            exciteWeight, v1Net);
        addOneRetinaToV1Connection(threeOffIndex, row, column + c,
            hEdgeDOffset, exciteWeight, v1Net);
        for (int r = -6; r < -3; r++) {
          addOneRetinaToV1Connection(threeOnIndex, row + r, column + c,
              hEdgeDOffset, inhibWeight, v1Net);
        }
      }
    }
  }

  private void connectRetinaToHEdgeU(CABot3Net v1Net) {
    // if there is a "dark side" to the bttom and a "light side" to the top,
    // the neuron must be on the edge of something dark, extending deownwards
    int netSize = getInputSize();
    double exciteWeight = 0.4;
    double inhibWeight = -1.4;

    int hEdgeUOffset = 7;
    int threeOnOffset = 0;
    int threeOffOffset = 1;

    for (int v1Neuron = 0; v1Neuron < netSize; v1Neuron++) {
      int column = v1Neuron % getInputCols();
      int row = v1Neuron / getInputCols();
      int threeOnIndex = threeOnOffset * netSize + v1Neuron + getInputCols(); 
      // (dark on the bottom)
      int threeOffIndex = threeOffOffset * netSize + v1Neuron; //(light on top)
      for (int i = -2; i < 3; i++) {
        addOneRetinaToV1Connection(threeOnIndex, row, column + i, hEdgeUOffset,
            exciteWeight, v1Net);
        addOneRetinaToV1Connection(threeOffIndex, row, column + i,
            hEdgeUOffset, exciteWeight, v1Net);
        for (int r = 6; r > 2; r--) {
          addOneRetinaToV1Connection(threeOnIndex, row + r, column + i,
              hEdgeUOffset, inhibWeight, v1Net);
        }
      }
    }
  }

  private void connectRetinaToSEdgeU(CABot3Net v1Net) {
    // if there is a "dark side" to the top and a "light side" to the bottom,
    // the neuron must be on the edge of something dark, extending upwards
    int netSize = getInputSize();
    double exWeight = 0.6;
    double inhibWeight = -1.4;

    int sEdgeUOffset = 1;
    int threeOffOffset = 1;

    int threeOnIndex;
    int threeOffIndex;

    for (int v1Neuron = 0; v1Neuron < netSize; v1Neuron++) {
      int column = v1Neuron % getInputCols();
      int row = v1Neuron / getInputCols();
      threeOnIndex = v1Neuron; // (dark on the top)
      threeOffIndex = threeOffOffset * netSize + v1Neuron; // (light on the bottom)
      for (int i = -2; i < 3; i++) {
        addOneRetinaToV1Connection(threeOnIndex, row + i, column - i,
            sEdgeUOffset, exWeight, v1Net);
        addOneRetinaToV1Connection(threeOffIndex, row + i + 1, column - i,
            sEdgeUOffset, exWeight, v1Net);
        //extra connections for the slope or more than 45%
        if (i==-2) {
          addOneRetinaToV1Connection(threeOnIndex, row -2, column +1,
              sEdgeUOffset, exWeight, v1Net);
          addOneRetinaToV1Connection(threeOffIndex, row -1, column +1,
              sEdgeUOffset, exWeight, v1Net);
        }
        else if (i==2) {
          addOneRetinaToV1Connection(threeOnIndex, row +2, column -1,
              sEdgeUOffset, exWeight, v1Net);
          addOneRetinaToV1Connection(threeOffIndex, row + 3, column - 1,
              sEdgeUOffset, exWeight, v1Net);
        }
        for(int c = -7; c < -2; c++){
          addOneRetinaToV1Connection(threeOnIndex, row + i, column - i+c,
              sEdgeUOffset, inhibWeight, v1Net);
	      }
      }
    }
  }

  private void connectRetinaToSEdgeD(CABot3Net v1Net) {
    // if there is a "dark side" to the bottom and a "light side" to the top,
    // the neuron must be on the edge of something dark, extending down
    int netSize = getInputSize();
    double exWeight = 0.6;
    double inhibWeight = -1.4;
    int sEdgeDOffset = 8;
    int threeOffOffset = 1;

    int threeOnIndex;
    int threeOffIndex;

    for (int v1Neuron = 0; v1Neuron < netSize; v1Neuron++) {
      int column = v1Neuron % getInputCols();
      int row = v1Neuron / getInputCols();
      threeOnIndex = v1Neuron; // (dark on the bottom)
      threeOffIndex = threeOffOffset * netSize + v1Neuron; // (light on the top)
      for (int i = -2; i < 3; i++) {
        addOneRetinaToV1Connection(threeOnIndex, row + i, column - i,
            sEdgeDOffset, exWeight, v1Net);
        addOneRetinaToV1Connection(threeOffIndex, row + i - 1, column - i,
            sEdgeDOffset, exWeight, v1Net);
        //extra connections for the slope or more than 45%
        if (i==-2) {
          addOneRetinaToV1Connection(threeOnIndex, row -2, column -1,
              sEdgeDOffset, exWeight, v1Net);
          addOneRetinaToV1Connection(threeOffIndex, row -3, column -1,
              sEdgeDOffset, exWeight, v1Net);
        }
        else if (i==2) {
          addOneRetinaToV1Connection(threeOnIndex, row +2, column +1,
              sEdgeDOffset, exWeight, v1Net);
          addOneRetinaToV1Connection(threeOffIndex, row + 1, column +1,
              sEdgeDOffset, exWeight, v1Net);
        }
	for(int c = 3; c<8; c++){
          addOneRetinaToV1Connection(threeOffIndex, row + i, column - i+c,
              sEdgeDOffset, inhibWeight, v1Net);
        }
      }
    }
  }

  private void connectRetinaToBEdgeD(CABot3Net v1Net) {
    // if there is a "dark side" to the bottom and a "light side" to the top,
    // the neuron must be on the edge of something dark, extending downwards
    int netSize = getInputSize();
    double exWeight = 0.6;
    double inhibWeight = -1.4;

    int bEdgeDOffset = 9;
    int threeOffOffset = 1;

    int threeOnIndex;
    int threeOffIndex;

    for (int v1Neuron = 0; v1Neuron < netSize; v1Neuron++) {
      int column = v1Neuron % getInputCols();
      int row = v1Neuron / getInputCols();
      threeOnIndex = v1Neuron; // (dark on the bottom)
      threeOffIndex = threeOffOffset * netSize + v1Neuron; //(light on the top)
      for (int i = -2; i < 3; i++) {
        addOneRetinaToV1Connection(threeOnIndex, row + i, column + i,
            bEdgeDOffset, exWeight, v1Net);
        addOneRetinaToV1Connection(threeOffIndex, row + i - 1, column + i,
            bEdgeDOffset, exWeight, v1Net);
        //extra connections for the slope or more than 45%
        if (i==-2) {
          addOneRetinaToV1Connection(threeOnIndex, row -2, column -1,
              bEdgeDOffset, exWeight, v1Net);
          addOneRetinaToV1Connection(threeOffIndex, row -3, column -1,
              bEdgeDOffset, exWeight, v1Net);
        }
        else if (i==2) {
          addOneRetinaToV1Connection(threeOnIndex, row +2, column +1,
              bEdgeDOffset, exWeight, v1Net);
          addOneRetinaToV1Connection(threeOffIndex, row + 1, column +1,
              bEdgeDOffset, exWeight, v1Net);
        }
        for(int c = -7; c<-2; c++){
          addOneRetinaToV1Connection(threeOnIndex, row + i, column + i+c,
              bEdgeDOffset, inhibWeight, v1Net);
        }
      }
    }
  }

  private void connectRetinaToBEdgeU(CABot3Net v1Net) {
    // if there is a "dark side" to the top and a "light side" to the bottom,
    // the neuron must be on the edge of something dark, extending upwards
    int netSize = getInputSize();
    double exWeight = 0.6;
    double inhibWeight = -1.4;

    int threeOffOffset = 1;

    int bEdgeUOffset = 2;
    int threeOnIndex;
    int threeOffIndex;

    for (int v1Neuron = 0; v1Neuron < netSize; v1Neuron++) {
      int column = v1Neuron % getInputCols();
      int row = v1Neuron / getInputCols();
      threeOnIndex = v1Neuron; // (dark on the bottom)
      threeOffIndex = threeOffOffset * netSize + v1Neuron; //(light on the top)
      for (int i = -2; i < 3; i++) {
        addOneRetinaToV1Connection(threeOnIndex, row + i, column + i,
            bEdgeUOffset, exWeight, v1Net);
        addOneRetinaToV1Connection(threeOffIndex, row + i + 1, column + i,
            bEdgeUOffset, exWeight, v1Net);
        //extra connections for the slope or more than 45%
        if (i==-2) {
          addOneRetinaToV1Connection(threeOnIndex, row -2, column -1,
              bEdgeUOffset, exWeight, v1Net);
          addOneRetinaToV1Connection(threeOffIndex, row -1, column -1,
              bEdgeUOffset, exWeight, v1Net);
        }
        else if (i==2) {
          addOneRetinaToV1Connection(threeOnIndex, row + 2, column + 1,
              bEdgeUOffset, exWeight, v1Net);
          addOneRetinaToV1Connection(threeOffIndex, row + 3, column + 1,
              bEdgeUOffset, exWeight, v1Net);
        }
        for(int c = 3; c<8; c++){
          addOneRetinaToV1Connection(threeOffIndex, row + i + 1, column + i+c,
              bEdgeUOffset, inhibWeight, v1Net);
        }
      }
    }
  }

  public void connectRetinaToV1(CABot3Net V1Net) {
    connectRetinaToHEdgeD(V1Net);
    connectRetinaToSEdgeU(V1Net);
    connectRetinaToBEdgeU(V1Net);
    connectRetinaToAndAngle(V1Net);
    connectRetinaToLessThanAngle(V1Net);
    connectRetinaToGreaterThanAngle(V1Net);
    connectRetinaToOrAngle(V1Net);
    connectRetinaToHEdgeU(V1Net);
    connectRetinaToSEdgeD(V1Net);
    connectRetinaToBEdgeD(V1Net);
    connectRetinaToVEdgeL(V1Net);
    connectRetinaToVEdgeR(V1Net);
  }

  public void connectGratingToV1(CABot3Net V1Net) {
    connectGratingToEdge("v3", "l", V1Net);
    connectGratingToEdge("v3", "r", V1Net);
    connectGratingToEdge("v3", "a", V1Net);
    connectGratingToEdge("v3", "o", V1Net);
    connectGratingToEdge("v3", "g", V1Net);
    connectGratingToEdge("v3", "s", V1Net);
    connectGratingToEdge("h3", "u", V1Net);
    connectGratingToEdge("h3", "d", V1Net);
    connectGratingToEdge("h3", "a", V1Net);
    connectGratingToEdge("h3", "o", V1Net);
    connectGratingToEdge("h3", "g", V1Net);
    connectGratingToEdge("h3", "s", V1Net);
    connectGratingToEdge("v6", "l", V1Net);
    connectGratingToEdge("v6", "r", V1Net);
    connectGratingToEdge("v6", "a", V1Net);
    connectGratingToEdge("v6", "o", V1Net);
    connectGratingToEdge("v6", "g", V1Net);
    connectGratingToEdge("v6", "s", V1Net);
    connectGratingToEdge("h6", "u", V1Net);
    connectGratingToEdge("h6", "d", V1Net);
    connectGratingToEdge("h6", "a", V1Net);
    connectGratingToEdge("h6", "o", V1Net);
    connectGratingToEdge("h6", "g", V1Net);
    connectGratingToEdge("h6", "s", V1Net);
  }

  private int translateRowColToNeuron(int Row, int Col) {
    if ((Row < 0) || (Row >= 50))
      return -1;
    if ((Col < 0) || (Col >= 50))
      return -1;
    return (Row * 50) + Col;
  }

  private void addAV1FeatureToAShapeConn(int fromNeuron, int toShape,
      int toRow, int toCol, double weight, CABot3Net V2Net) {
    int shapeOff = toShape * 2500;

    if ((toRow >= 0) && (toRow < 50) && (toCol >= 0) && (toCol < 50)) {
      if (toRow % 10 < 5)
        toRow += 5;
      int toNeuron = translateRowColToNeuron(toRow, toCol);
      neurons[fromNeuron].addConnection(V2Net.neurons[toNeuron + shapeOff],
          weight);
    }
  }

  private void connectV1FeatureToObjRecShapeDir(int featureNum, int toShape,
      int down, int right, double weight, CABot3Net V2Net) {
    for (int neuron = 0; neuron < 2500; neuron++) {
      int fromRow = neuron / 50;
      int fromCol = neuron % 50;
      int fromNeuron = neuron + featureNum * 2500;
      int toRow = fromRow;

      addAV1FeatureToAShapeConn(fromNeuron, toShape, toRow, fromCol, weight,
          V2Net);
      for (int offset = 1; offset < 11; offset++) {
        int mod = (offset / 2) + 1;
        addAV1FeatureToAShapeConn(fromNeuron, toShape, toRow + (offset * down),
            fromCol + (offset * right), weight, V2Net);
      }
    }
  }

  private double eW = 0.2; // edgeWeight = 0.2
  private double aW = 0.2; // angleWeight = 0.7

  public void connectV1ToPyramid(CABot3Net V2Net) {
    //ANGLES
    connectV1FeatureToObjRecShapeDir(3, 0, 1, 0, aW, V2Net);// and angle down
    connectV1FeatureToObjRecShapeDir(3, 0, 1, -1, aW, V2Net);//and angle dwn lt
    connectV1FeatureToObjRecShapeDir(3, 0, 1, 1, aW, V2Net);//and angle down rt
    //connectV1FeatureToObjRecShapeDir(4, 0, -1, 1, aW, V2Net);// lthan angle
    // upright
    //connectV1FeatureToObjRecShapeDir(4, 0, 0, 1, aW, V2Net);//lthan angle right
    //connectV1FeatureToObjRecShapeDir(5, 0, -1, -1, aW, V2Net);// gthan angle
    // upleft
    //connectV1FeatureToObjRecShapeDir(5, 0, 0, -1, aW, V2Net);//gthan angle left

    //EDGES
    connectV1FeatureToObjRecShapeDir(0, 0, -1, 0, eW, V2Net);// hedged up
    connectV1FeatureToObjRecShapeDir(0, 0, -1, -1, eW, V2Net);// hedged up left
    connectV1FeatureToObjRecShapeDir(0, 0, -1, 1, eW, V2Net);// hedged up right
    connectV1FeatureToObjRecShapeDir(8, 0, 1, 1, eW, V2Net);//sedged down right
    connectV1FeatureToObjRecShapeDir(8, 0, 1, 0, eW, V2Net);// sedged down
    connectV1FeatureToObjRecShapeDir(8, 0, 0, 1, eW, V2Net);// sedged right
    connectV1FeatureToObjRecShapeDir(9, 0, 1, -1, eW, V2Net);//bedged down left
    connectV1FeatureToObjRecShapeDir(9, 0, 1, 0, eW, V2Net);// bedged down
    connectV1FeatureToObjRecShapeDir(9, 0, 0, -1, eW, V2Net);// bedged left
  }

  public void connectV1ToStalactite(CABot3Net V2Net) {
    //ANGLES
    //connectV1FeatureToObjRecShapeDir(4, 1, 1, 1, aW, V2Net);// lthan angle
    // downrt
    //connectV1FeatureToObjRecShapeDir(4, 1, 0, 1, aW, V2Net);//lthan angle right
    //connectV1FeatureToObjRecShapeDir(5, 1, 1, -1, aW, V2Net);//gthan angle dnlt
    //connectV1FeatureToObjRecShapeDir(5, 1, 0, -1, aW, V2Net);//gthan angle lt
    connectV1FeatureToObjRecShapeDir(6, 1, -1, 0, aW, V2Net);// or angle up
    connectV1FeatureToObjRecShapeDir(6, 1, -1, -1, aW, V2Net);//or angle up lft
    connectV1FeatureToObjRecShapeDir(6, 1, -1, 1, aW, V2Net);//or angle up rt

    //EDGES
    connectV1FeatureToObjRecShapeDir(7, 1, 1, 0, eW, V2Net);// hedgeU down
    connectV1FeatureToObjRecShapeDir(7, 1, 1, -1, eW, V2Net);//hedgeu down lft
    connectV1FeatureToObjRecShapeDir(7, 1, 1, 1, eW, V2Net);//hedgeU down rt
    connectV1FeatureToObjRecShapeDir(1, 1, -1, -1, eW, V2Net);// sedgeU up left
    connectV1FeatureToObjRecShapeDir(1, 1, 0, -1, eW, V2Net);//sedgeu left
    connectV1FeatureToObjRecShapeDir(1, 1, -1, 0, eW, V2Net);//sedgeU up
    connectV1FeatureToObjRecShapeDir(2, 1, -1, 1, eW, V2Net);//bedgeU up right
    connectV1FeatureToObjRecShapeDir(2, 1, 0, 1, eW, V2Net);//bedgeu right
    connectV1FeatureToObjRecShapeDir(2, 1, -1, 0, eW, V2Net);//bedgeU up
  }

  public void connectV1ToObjRec(CABot3Net V2Net) {
    connectV1ToPyramid(V2Net);
    connectV1ToStalactite(V2Net);
  }

  // ****************intranet connections******************
  private boolean isEvidenceNeuron(int row, int col) {
    if (((row == 5) || (row == 6)) && ((col == 6) || (col == 7) || (col == 8)))
      return true;
    return false;
  }

  // Set the bottom half of the CA (50 neurons) as a CA.
  private void buildBottomShapeSizeCA(int shape) {
    int shapeOffset = 2500 * shape;
    double wt = 0.5;
    for (int bigRow = 0; bigRow < 5; bigRow++) {
      for (int row = 5; row < 10; row++) {
        for (int col = 0; col < 50; col++) {
          int fromRow = (bigRow * 10) + row;
          int fromNeuron = (fromRow * 50) + col + shapeOffset;
          if ((fromNeuron % 5) == 0)
            neurons[fromNeuron].setInhibitory(true);
          else {
            neurons[fromNeuron].setInhibitory(false);
            for (int synapse = 0; synapse <50; synapse ++) {
              /*
              int flat = fromNeuron%50;
              flat += (synapse*7)+2;
              flat %= 50;
               */
              int toNeuron=((synapse/10)*50)+(synapse %10);//0-49,50-99,200-249
              toNeuron += (bigRow*500)+((col/10)*10)+shapeOffset+250;
              addConnection(fromNeuron, toNeuron, wt);
            }
            /*
           for (int loopRow = 0; loopRow < 4; loopRow++) {
             for (int loopCol = 0; loopCol < 4; loopCol++) {
               int toRow = loopRow + row;
               if (toRow >= 10)
                 toRow -= 5;
               toRow += (bigRow * 10);
               int toCol = loopCol + col;
               if (((col % 10) + loopCol) >= 10)
                toCol -= 10;
              int toNeuron = (toRow * 50) + toCol + shapeOffset;
              addConnection(fromNeuron, toNeuron, wt);
	     }
	    }
             */
          }

        }
      }
    }
  }

  private void buildTopShapeSizeCA(int shape, int location) {
    int shapeOffset = 2500 * shape;
    int rowLarge = location % 5;
    int colLarge = location / 5;
    int locationOffset = (rowLarge * 500) + (colLarge * 10);
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 10; col++) {
        int fromNeuron = (row * 50) + col + locationOffset + shapeOffset;
        if ((fromNeuron % 5) == 0)
          neurons[fromNeuron].setInhibitory(true);
        else {
          neurons[fromNeuron].setInhibitory(false);
          for (int synapse = 0; synapse < 50; synapse++) {
            int toNeuron = synapse + (row * 10) + col;
            toNeuron %= 50;
            int toRow = toNeuron / 10;
            int toCol = toNeuron % 10;
            toNeuron = (toRow * 50) + toCol + locationOffset + shapeOffset;
            addConnection(fromNeuron, toNeuron, 0.5);
          }
        }
      }
    }
  }
  //the top portion of one column inhibits the top portion of another in objRec
  private void topInhibitsAdjacentTop(int shape, int fromBigCol, int toBigCol){
    int sO = shape*2500; //sO = shapeOffset
    for (int bigRow = 0; bigRow < 5; bigRow++) {
      for (int littleRow = 0; littleRow < 5; littleRow++) {
        for (int fromCol = 0; fromCol < 10; fromCol+=5) {
          int fromNeuron=(bigRow*500)+(littleRow*50)+(fromBigCol*10)+fromCol+sO;
          for (int synapse = 0; synapse< 5; synapse++) {
            for (int toBigRow = 0; toBigRow < 5; toBigRow++) {
              int toNeuron = (bigRow*500)+(toBigRow*50)+(toBigCol*10)+fromCol+
              synapse+sO;
              addConnection(fromNeuron, toNeuron, 0.53);
            }
          }
        }
      }
    }
  }

  private void buildTopShapeSizeCA(int shape) {
    for (int location = 0; location < 25; location++)
      buildTopShapeSizeCA(shape, location);
    topInhibitsAdjacentTop(shape,2,3);
  }

  private void connectBottomToTopShapeSizeCA(int shape, int location) {
    int shapeOffset = 2500 * shape;
    int rowLarge = location % 5;
    int colLarge = location / 5;
    int locationOffset = (rowLarge * 500) + (colLarge * 10);
    for (int row = 5; row < 10; row++) {
      for (int col = 0; col < 10; col++) {
        int fromNeuron = (row * 50) + col + locationOffset + shapeOffset;
        if ((!neurons[fromNeuron].isInhibitory())
            && (!isEvidenceNeuron(row, col))) {
          for (int synapse = 0; synapse < 5; synapse++) {
            int toCol = (col / 5) * 5;
            int toNeuron = ((row - 5) * 50) + toCol + synapse + locationOffset
            + shapeOffset;
            // max 4 connections D=1.1 so w*4*10 < 5 (theta) w< 5/40=1/8
            // assume less firing so .124 -> .2
            addConnection(fromNeuron, toNeuron, 0.2);
          }
        }
      }
    }
  }

  private void connectBottomToTopShapeSizeCA(int shape) {
    for (int location = 0; location < 25; location++)
      connectBottomToTopShapeSizeCA(shape, location);
  }

  private void inhibitAdjacentSameShapeRecognizers(int shape) {
    int shapeOffset = 2500 * shape;
    for (int neuron = 0; neuron < 2500; neuron += 5) {
      int fromNeuron = neuron + shapeOffset;
      int fromRow = neuron / 50;
      if ((fromRow % 10) >= 5) {// only for bottom half.
        int fromBigRow = neuron / 500;
        int fromCol = neuron % 50;
        int fromBigCol = fromCol / 10;
        for (int toBigRow = fromBigRow - 1; toBigRow <= fromBigRow + 1; toBigRow++) {
          for (int toBigCol = fromBigCol - 1; toBigCol <= fromBigCol + 1; toBigCol++) {
            // not off the edge
            if ((toBigCol >= 0) && (toBigCol < 5) && (toBigRow >= 0)
                && (toBigRow < 5))
              // not self
              if (!((toBigCol == fromBigCol) && (toBigRow == fromBigRow))) {
                int toNeuron = ((toBigRow - fromBigRow) * 500)
                + ((toBigCol - fromBigCol) * 10);
                toNeuron += fromNeuron;
                double inhibWeight = 1.0;
                addConnection(fromNeuron, toNeuron + 1, inhibWeight);
                addConnection(fromNeuron, toNeuron + 2, inhibWeight);
                addConnection(fromNeuron, toNeuron + 3, inhibWeight);
                addConnection(fromNeuron, toNeuron + 4, inhibWeight);
              }
          }
        }
      }
    }
  }

  /**
   * 
   * @param shape
   * 
   *          NO LONGER used by the Jamb detector to send upward and downward
   *          activation, extending the door jambs
   **/
  private void exciteVerticalSameShapeRecognizers(int shape) {
    int shapeOffset = 2500 * shape;
    double weight = 0.3;
    for (int neuron = 0; neuron < 2500; neuron++) {
      if (neuron % 5 != 0) {// not the inhibitory ones
        int toNeuron;
        int fromNeuron = neuron + shapeOffset;
        int fromCol = neuron % 50;
        int fromRow = neuron / 50;

        if ((fromRow % 10) >= 5) {// only for bottom half.
          int fromBigRow = neuron / 500;
          int fromBigCol = fromCol / 10;
          int toBigCol = fromBigCol; // going up column "n"
          for (int toBigRow = 0; toBigRow < 5; toBigRow++) { // through all the
            // other rows
            if (toBigRow != fromBigRow) { // not self
              for (int toRow = 5; toRow < 10; toRow++) {// ignoring the top half
                // of the big rows
                for (int i = 0; i < 10; i++) {
                  toNeuron = shapeOffset + toBigRow * 500 + toRow * 50
                  + toBigCol * 10 + i;
                  addConnection(fromNeuron, toNeuron + 4, weight);
                }
              }
            }
          }
        }
      }
    }
  }

  private void inhibitLateralSameShapeRecognizers(int shape) {
    int shapeOffset = 2500 * shape;
    double inhibWeight = 1.0;
    int toNeuron;

    for (int neuron = 0; neuron < 2500; neuron += 5) {
      int fromNeuron = neuron + shapeOffset;
      int fromRow = neuron / 50;

      if ((fromRow % 10) >= 5) {// only for bottom half.
        // int fromBigRow = neuron/500;
        int fromCol = neuron % 50;
        int fromBigCol = fromCol / 10;
        for (int toBigCol = fromBigCol - 1; toBigCol <= fromBigCol + 1; toBigCol++) {
          // not off the edge
          if (toBigCol >= 0 && toBigCol < 5) {
            // not self
            if (!(toBigCol == fromBigCol)) {
              for (int i = 0; i < 10; i++) {
                toNeuron = fromRow * 50 + toBigCol * 10 + i;
                addConnection(fromNeuron, toNeuron, inhibWeight);
              }
            }
          }
        }
      }
    }
  }

  // pyramid and stalactite mutually inhibit each other and areas
  private void inhibitAdjacentOtherShapeRecognizers(int shape,int otherShape){
    //System.out.println(" inhibadgy " + shape);
    int shapeOffset = 2500 * shape;
    for (int neuron = 0; neuron < 2500; neuron += 5) {
      int fromNeuron = neuron + shapeOffset;
      int fromRow = neuron / 50;
      if ((fromRow % 10) >= 5) {// only for bottom half.
        int fromBigRow = neuron / 500;
        int fromCol = neuron % 50;
        int fromBigCol = fromCol / 10;
        // account for other shape
        for (int toBigRow = fromBigRow-1; toBigRow<=fromBigRow+1; toBigRow++){
          for (int toBigCol=fromBigCol-1;toBigCol<=fromBigCol+1; toBigCol++) {
            // not off the edge
            if ((toBigCol >= 0) && (toBigCol < 5) && (toBigRow >= 0)
                && (toBigRow < 5)) {
              int toBigBaseRow = toBigRow + (otherShape * 5);
              int toNeuron = (toBigBaseRow * 500)+((toBigCol-fromBigCol)*10);
              toNeuron += ((fromRow % 10) * 50) + (fromNeuron % 50);
              double inhibWeight = 2.0;
              addConnection(fromNeuron, toNeuron, inhibWeight);
              addConnection(fromNeuron, toNeuron + 1, inhibWeight);
              addConnection(fromNeuron, toNeuron + 2, inhibWeight);
              addConnection(fromNeuron, toNeuron + 3, inhibWeight);
              addConnection(fromNeuron, toNeuron + 4, inhibWeight);
            }
          }
        }
      }
    }
  }

  // Create the CA for Visual categories shape and size specific.
  // The top 50 are for require activation from outside vision
  // and the active bottom CA to activate.
  // Every 5th neuron is inhibitory. Inhibit adjacent CAs and
  // ones in the opposite shape.
  private void createShapeSizeCA(int shape) {
    buildBottomShapeSizeCA(shape);
    buildTopShapeSizeCA(shape);
    connectBottomToTopShapeSizeCA(shape);
    /*
    if (shape == 3) {
       inhibitLateralSameShapeRecognizers(shape);
    } else {
       inhibitAdjacentSameShapeRecognizers(shape);
    }
     */
    inhibitAdjacentOtherShapeRecognizers(0,1);
    inhibitAdjacentOtherShapeRecognizers(1,0);
    /*    inhibitAdjacentOtherShapeRecognizers(4,0);
    inhibitAdjacentOtherShapeRecognizers(4,1);
    inhibitAdjacentOtherShapeRecognizers(4,2);
    inhibitAdjacentOtherShapeRecognizers(4,3);
     */

  }

  // set up the appropriate CAs for 5x5 locations for both shapes.
  private void setObjRecTopology() {
    for (int shape = 0; shape < 5; shape++) {
      createShapeSizeCA(shape);
    }
  }

  /******* Control and Spreading Activation Connectivity Functions */
  private int controlCASize = 40;

  private void oneControlCAPrimesOneControl(int onCA, int offCA) {
    for (int neuron = 0; neuron < 40; neuron++) {
      int fromNeuron = neuron + (onCA * controlCASize);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuron + (offCA * controlCASize);
        addConnection(fromNeuron, toNeuron, 2.0);
        if ((fromNeuron % 5) == 1)
          addConnection(fromNeuron, toNeuron - 1, 2.0);
      }
    }
  }

  private void oneControlCAStopsOneControl(int onCA, int offCA) {
    for (int neuron = 0; neuron < 8; neuron++) {
      int fromNeuron = (neuron * 5) + (onCA * controlCASize);
      for (int synapse = 0; synapse < 10; synapse++) {
        if (((neuron * 5) + synapse) < 40) {
          int toNeuron = (neuron * 5) + synapse + (offCA * controlCASize);
          addConnection(fromNeuron, toNeuron, 2.0);
        }
      }
    }
  }

  private void oneControlCAStartsOneControl(int onCA, int offCA, double weight) {
    for (int neuron = 0; neuron < 40; neuron++) {
      int fromNeuron = neuron + (onCA * controlCASize);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuron + (offCA * controlCASize);
        addConnection(fromNeuron, toNeuron, weight);
        if ((fromNeuron % 5) == 1)
          addConnection(fromNeuron, toNeuron - 1, weight);
      }
    }
  }

  private void setControlTopology() {
    setFiftyFiftyTopology2(1.1);
    oneControlCAPrimesOneControl(0, 1);
    oneControlCAStopsOneControl(1, 0);
    oneControlCAStartsOneControl(1, 2, 3.1);
    oneControlCAStopsOneControl(2, 1);
    oneControlCAPrimesOneControl(2, 3);
    oneControlCAStopsOneControl(3, 2);
    oneControlCAStartsOneControl(3, 4, 3.1);
    oneControlCAStopsOneControl(4, 3);
    oneControlCAStartsOneControl(4, 0, 3.1);
    oneControlCAStopsOneControl(0, 4);
  }

  private void setActionTopology() {
    setFiftyFiftyTopology2(1.1);
  }

  private int neuronsInFact = 40;

  private void setInhibFactNetCA(int fromCA, int toCA, double weight) {
    for (int neuron = 0; neuron < neuronsInFact; neuron++) {
      int fromNeuron = (fromCA * neuronsInFact) + neuron;
      if (neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 40; synapse++) {
          int toNeuron = (toCA * neuronsInFact)
          + (int) (CANT23.random.nextFloat() * 100);
          addConnection(fromNeuron, toNeuron, weight);
        }
      }
    }
  }

  // for goal and fact
  private void setPartialFiftyFiftyTopology(int start, int end) {
    int featureSize = 10;
    for (int simpleCA = 4 * start; simpleCA < 4 * end; simpleCA++) {
      for (int neuron = 0; neuron < featureSize; neuron++) {
        int fromNeuron = (simpleCA * featureSize) + neuron;
        if ((neuron % 5) == 0) {
          // no inhibitory connections.
          neurons[fromNeuron].setInhibitory(true);
        } else {
          neurons[fromNeuron].setInhibitory(false);
          setFiftyFiftySubCA(1.1, fromNeuron, simpleCA * featureSize);
        }
      }
    }
  }

  // This sets up a fifty fifty goal but the left needs some support
  // to continue running
  private void createSupportedFeature(int featureNum, int neuronsInFeature) {
    for (int neuron = 0; neuron < neuronsInFeature; neuron++) {
      int fromNeuron = (featureNum * neuronsInFeature) + neuron;
      if ((neuron % 5) == 0)
        neurons[fromNeuron].setInhibitory(true);
      else {
        neurons[fromNeuron].setInhibitory(false);
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = fromNeuron - (fromNeuron % 5) + 5 + synapse;
          double weight = 1.1;
          if ((neuron % 10) > 5) {
            toNeuron -= 10;
            weight = 0.95;
          }
          addConnection(fromNeuron, toNeuron, weight);
        }
      }
    }
  }

  private void connectOneGoalToOneGoal(int fromGoalNum, int toGoalNum,
      double weight) {
    for (int neuronNum = 0; neuronNum < neuronsInGoal; neuronNum++) {
      int fromNeuron = neuronNum + (fromGoalNum * neuronsInGoal);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuronNum + (toGoalNum * neuronsInGoal);
        addConnection(fromNeuron, toNeuron, weight);
        if ((fromNeuron % 5) == 1)
          addConnection(fromNeuron, toNeuron - 1, weight);
      }
    }
  }

  private void setGoal1Topology() {
    // set up the normal goals as fiftyfifty2
    setPartialFiftyFiftyTopology(0, 8);
    createSupportedFeature(8, 40); // pyramid is partially supported
    createSupportedFeature(9, 40); // stal is partially supported
    setPartialFiftyFiftyTopology(10, 23); // center explore, stop, id room,
    //find door in corridor, throughCor, move before sp,bp, bs, ss, getTarg
    //findTarget
    createSupportedFeature(23, 40); //targetFound (on briefly at end of goal)
    setPartialFiftyFiftyTopology(24, 28); //frontJam, moveAfterJam, moveAgain
    //move 

    //find door, in corridor, through corridor
    connectOneGoalToOneGoal(6, 8, 1.0); // turn to pyramid
    connectOneGoalToOneGoal(6, 9, 1.0); // turn to stal
    connectOneGoalToOneGoal(7, 8, 1.0); // go to pyramid
    connectOneGoalToOneGoal(7, 9, 1.0); // go to stal
    connectOneGoalToOneGoal(7, 13, 1.0); // go to door
    connectOneGoalToOneGoal(10, 8, 1.0); // center to pyramid
    connectOneGoalToOneGoal(10, 9, 1.0); // center to stal

    //Explore leads to a simple automata, id room, find door, in door,
    //through door, id room.  Repeat until in a room you already know.
    connectOneGoalToOneGoal(11, 13, 3.0); // explore starts id
    connectOneGoalToOneGoal(14, 13, -6.0); // find door prevents id room
    connectOneGoalToOneGoal(15, 13, -6.0); // to door prevents id room
    connectOneGoalToOneGoal(16, 13, -6.0); // through corridor prevents id room
    connectOneGoalToOneGoal(15, 16, 1.0); // toDoor primes throughCorridor
    connectOneGoalToOneGoal(16, 15, -6.0); // through corridor stops toDoor

    //move before goals
    connectOneGoalToOneGoal(17, 21, 3.0); // move before stiped pyramid starts 
                                          //get target room
    connectOneGoalToOneGoal(18, 21, 3.0); // mbbp starts get target room
    connectOneGoalToOneGoal(19, 21, 3.0); // mbbs starts get target room
    connectOneGoalToOneGoal(20, 21, 3.0); // mbss starts get target room

    //connection for setting target rooms
    connectOneGoalToOneGoal(22, 21, -6.0); // findtarget stops get target
    connectOneGoalToOneGoal(22, 17, -6.0); // findtarget stops before BP
    connectOneGoalToOneGoal(22, 18, -6.0); // findtarget stops before SP
    connectOneGoalToOneGoal(22, 19, -6.0); // findtarget stops before BS
    connectOneGoalToOneGoal(22, 20, -6.0); // findtarget stops before SS

    connectOneGoalToOneGoal(22, 13, 3.0); // fintTarget starts id
    connectOneGoalToOneGoal(22, 23, 2.25); // findTarget start foundTarget
    connectOneGoalToOneGoal(23, 22, -3.0); // foundTarget stops findTarget
    connectOneGoalToOneGoal(23, 13, -6.0); // foundTarget stops id room
    connectOneGoalToOneGoal(23, 14, -6.0); // foundTarget stops find Door

    connectOneGoalToOneGoal(24, 13, -6.0); // atFrontJam prevents idRoom
    connectOneGoalToOneGoal(24, 15, -6.0); // atFrontJam stops toDoor
    connectOneGoalToOneGoal(15, 24, 1.0); // toCorr primes at atFrontJam
    connectOneGoalToOneGoal(16, 24, -6.0); // throughCorr prevents atFrontJam
    connectOneGoalToOneGoal(25, 13, -6.0); //moveAfter prevents idRoom
  }

  private void connectOneFactToOneFact(int fromFactNum, int toFactNum,
      double weight) {
    for (int neuronNum = 0; neuronNum < neuronsInFact; neuronNum++) {
      int fromNeuron = neuronNum + (fromFactNum * neuronsInFact);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuronNum + (toFactNum * neuronsInFact);
        addConnection(fromNeuron, toNeuron, weight);
        if ((fromNeuron % 5) == 1)
          addConnection(fromNeuron, toNeuron - 1, weight);
      }
    }
  }

  private void oneFactStopsOneFact(int fromFact, int toFact, double weight) {
    for (int neuronNum = 0; neuronNum < neuronsInFact; neuronNum += 5) {
      int fromNeuron = neuronNum + (fromFact * neuronsInFact);
      // assert(neurons[fromNeuron].isInhibitory())
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron;
        if ((neuronNum % 10) == 5)
          toNeuron = neuronNum - 5 + synapse;
        else
          toNeuron = neuronNum + synapse;
        toNeuron += toFact * neuronsInFact;
        addConnection(fromNeuron, toNeuron, weight);
      }
    }
  }
  private void oneFactReallyStopsOneFact(int fromFact,int toFact,double wt){
    for (int neuronNum = 0; neuronNum < neuronsInFact; neuronNum += 5) {
      int fromNeuron = neuronNum + (fromFact * neuronsInFact);
      for (int synapse = 0; synapse < 20; synapse++) {
        int toNeuron = synapse + (toFact * neuronsInFact);
        if (neuronNum>=20) toNeuron+=20;
        addConnection(fromNeuron, toNeuron, wt);
      }
    }
  }
  //The four fact boxes send activation to each other.
  private void factSupportsItself(int factNum) {
    for (int i=0; i < neuronsInFact; i++) {
      int fromNeuron = i+ (factNum*neuronsInFact);
      if (!neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse<3;synapse++) {
          int fromGroup=i/10;
          int toNeuron = 0;
          if (synapse == 0) {
            if (fromGroup == 0) toNeuron = 1;
          }
          else if (synapse == 1) {
            if (fromGroup <= 1) toNeuron = 2;
            else toNeuron = 1;
          }
          else if (synapse == 2){
            if (fromGroup == 3) toNeuron = 2;
            else toNeuron = 3;
          }
          toNeuron = (toNeuron*10) + (i%10) + (factNum*neuronsInFact);
          addConnection(fromNeuron, toNeuron, 1.1);
        }
      }
      else { //inhibit other four of the five
        for (int synapse = 0; synapse<4;synapse++) {
          int toNeuron = fromNeuron+1+synapse;
          addConnection(fromNeuron, toNeuron, 0.3);
        }
      }
    }
  }
  //These were the fact connections for CABot2 with possible changes for CABot3
  private void setCABot2Facts() {
    connectOneFactToOneFact(0, 1, 2.25); // ml1 ml2
    connectOneFactToOneFact(2, 3, 2.25); // mr1 mr2

    connectOneFactToOneFact(4, 10, 2.22223); // pyramid no obj
    connectOneFactToOneFact(5, 10, 2.22223); // stal no obj

    oneFactStopsOneFact(6, 10, 2.5); // left stops no obj
    oneFactStopsOneFact(7, 10, 2.5);
    oneFactStopsOneFact(8, 10, 2.5);
    oneFactStopsOneFact(11, 10, 2.5);// left2 stops no obj
    oneFactStopsOneFact(12, 10, 2.5);
    oneFactStopsOneFact(13, 10, 2.5);

    oneFactStopsOneFact(6, 11, 2.5);// left stops left2
    oneFactStopsOneFact(7, 12, 2.5);
    oneFactStopsOneFact(8, 13, 2.5);
    oneFactStopsOneFact(11, 6, 2.5);// left2 stops left
    oneFactStopsOneFact(12, 7, 2.5);
    oneFactStopsOneFact(13, 8, 2.5);
  }
  private void setExploreFacts() {
    //find shape supports explore shapes, patterns and noobj
    connectOneFactToOneFact(35, 14, 1.25); //find shape supports pyramid
    connectOneFactToOneFact(35, 15, 1.25); //find shape supports stal
    connectOneFactToOneFact(35, 16, 1.25); //find shape supports vstripes
    connectOneFactToOneFact(35, 17, 1.25); //find shape supports hstripes
    connectOneFactToOneFact(35, 18, 1.25); //find shape slowly to noobj

    //Patterned shape are not unknown objects
    oneFactStopsOneFact(21, 34, 5.5); //bar pyr stops unknown obj
    oneFactStopsOneFact(22, 34, 5.5); //stripe  pyr stops unknown obj
    oneFactStopsOneFact(23, 34, 5.5); //bar stal stops unknown obj
    oneFactStopsOneFact(24, 34, 5.5); //stripe stal stops unknown obj

    //unk-obj center
    connectOneFactToOneFact(34, 36, 1.35); //unk-obj to unk-obj center
    connectOneFactToOneFact(8, 36, 1.35); //target center to unk-obj center

    connectOneFactToOneFact(35, 37, 2.2223); //find shape slowly starts no-obj
    oneFactStopsOneFact(34, 37, 2.5); //unk-obj stops no-obj

    //test after move toward unknown obj
    connectOneFactToOneFact(35, 38, 1.5); //find shape supports move toward uOb
    connectOneFactToOneFact(38, 39, 2.3); //after move to shape by move toward
    oneFactStopsOneFact(39, 38, 2.5); //after move stops moved toward unk-obj
    oneFactStopsOneFact(39, 34, 2.5); //moved toward unk-obj stops unk-obj
    oneFactStopsOneFact(39, 37, 2.5); //moved toward unk-obj stops no obj
    oneFactStopsOneFact(39, 14, 2.5); //moved toward unk-obj stops pyramid
    oneFactStopsOneFact(39, 15, 2.5); //moved toward unk-obj stops stalactite

    oneFactStopsOneFact(19, 35, 2.5); //moved toward unk-obj stops find shape

    //target-door, door-seen, and cog-seq-start stop a lot of vision facts
    for (int fromFact = 25; fromFact < 32; ) {
      oneFactReallyStopsOneFact(fromFact, 6, 4.5); //target on left 
      oneFactReallyStopsOneFact(fromFact, 7, 4.5); 
      oneFactReallyStopsOneFact(fromFact, 8, 4.5); 
      oneFactReallyStopsOneFact(fromFact, 11, 4.5); //center on left
      oneFactReallyStopsOneFact(fromFact, 12, 4.5); 
      oneFactReallyStopsOneFact(fromFact, 13, 4.5); 
      if (fromFact==25) fromFact=28;
      else if (fromFact==28) fromFact=31;
      else fromFact=32;
    }
    for (int fromFact = 25; fromFact < 34; ) {
      oneFactStopsOneFact(fromFact, 14, 4.5); 
      oneFactStopsOneFact(fromFact, 15, 4.5); 
      oneFactStopsOneFact(fromFact, 16, 4.5); 
      oneFactStopsOneFact(fromFact, 17, 4.5); 
      if (fromFact==25) fromFact=28;
      if (fromFact==28) fromFact=33;
      else fromFact=34;
    }
    //target door stops patterned shapes 
    oneFactStopsOneFact(25, 21, 2.5); //target door stops barredpyr 
    oneFactStopsOneFact(25, 22, 2.5); //target door stops stripedpyr
    oneFactStopsOneFact(25, 23, 2.5); //target door stops barredstal
    oneFactStopsOneFact(25, 24, 2.5); //target door stops stripedstal

    //Go to the door when exploring 
    //get to and through the door
    connectOneFactToOneFact(27, 28, 2.5); //door-ahead starts door seen
    oneFactStopsOneFact(28, 25, 2.5);  //door seen stops find door
    connectOneFactToOneFact(28, 27, 2.5); //door-seen starts door-ahead
    oneFactStopsOneFact(41, 50, 2.5);  //throughDoor stops backJamSeen
    oneFactStopsOneFact(41, 28, 2.5);  //through-door stops door-seen
    connectOneFactToOneFact(41, 29, 2.223); //through-door starts next
    oneFactStopsOneFact(29, 41, 2.5);  //next stops through-door
    oneFactStopsOneFact(29, 28, 2.5);  //door seen (clean up)
    oneFactStopsOneFact(49, 27, 2.5);  //toAndThrough stops doorAhead
    connectOneFactToOneFact(29, 52, -19.0); //door seen (clean up) jamLeft
    connectOneFactToOneFact(29, 55, -9.0); //door seen (clean up) backJamLeft
    connectOneFactToOneFact(41, 52, -9.0); //throughDoor stops jamLeft
    connectOneFactToOneFact(41, 55, -9.0); //throughDoor stops backJamLeft
    connectOneFactToOneFact(40, 50, 1.0); //doorStillSeen primes backJamSeen
    connectOneFactToOneFact(41, 51, 2.5); //throughDoor starts rtAfterCor
    oneFactStopsOneFact(50, 52, 2.5);  //backJamSeen prevents jamleft
    connectOneFactToOneFact(52, 53, 2.25); //jamLeft starts leftBeforeJam
    oneFactStopsOneFact(53, 52, 9.5);  //leftBeforeJam  prevents jamleft
    oneFactStopsOneFact(53, 6, 2.5);  //leftBeforeJam  prevents targetLeft
    oneFactStopsOneFact(53, 11, 2.5);  //leftBeforeJam  prevents centerLeft
    connectOneFactToOneFact(55, 41, 0.5); //backJamOnLeft to throughCorr
    oneFactStopsOneFact(64, 53, 2.5);  //rt3done stops leftBeforeJam


    //turn right 3 times when through door
    oneFactStopsOneFact(40, 56, 2.5);  //doorStillSeen prevents rtAfterCor
    connectOneFactToOneFact(57, 56, -7.5); //rtStart stops rtAfter
    connectOneFactToOneFact(56, 57, 1.5); //rtAfter primes rtStart
    connectOneFactToOneFact(57, 58, 2.5); //rtStart starts rtDone
    connectOneFactToOneFact(58, 59, 2.25); //rtDone starts rt2After
    connectOneFactToOneFact(58, 57, -2.5); //rtDone stops rtStart
    connectOneFactToOneFact(60, 59, -2.5); //rt2Start stops rt2After
    connectOneFactToOneFact(59, 60, 1.5); //rt2After primes rt2Start
    connectOneFactToOneFact(60, 61, 2.5); //rt2Start starts rt2Done
    connectOneFactToOneFact(61, 62, 2.25); //rt2Done starts rt3After
    connectOneFactToOneFact(61, 60, -2.5); //rt2Done stops rt2Start
    connectOneFactToOneFact(63, 62, -2.5); //rt3Start stops rt3After
    connectOneFactToOneFact(62, 63, 1.5); //rt3After primes rt3Start
    connectOneFactToOneFact(63, 64, 2.5); //rt3Start starts rt3Done
    connectOneFactToOneFact(64, 63, -2.5); //rt3Done stops rt3Start

    connectOneFactToOneFact(58, 56, -2.5); //rtDone stops rtAfter
    connectOneFactToOneFact(59, 56, -2.5); //rt2After stops rtAfter
    connectOneFactToOneFact(60, 56, -2.5); //rt2Start stops rtAfter
    connectOneFactToOneFact(61, 56, -2.5); //rt2Done stops rtAfter
    connectOneFactToOneFact(62, 56, -2.5); //rt3After stops rtAfter
    connectOneFactToOneFact(63, 56, -2.5); //rt3Start stops rtAfter
    connectOneFactToOneFact(64, 56, -2.5); //rt3Done stops rtAfter

    
    //new cogmapping stuff 
    oneFactStopsOneFact(30, 19, 4.5);  //inc-count prevents seen room
    oneFactStopsOneFact(31, 19, 4.5);  //cog-seq-start prevents seen room
    oneFactStopsOneFact(31, 35, 4.5);  //cog-seq-start prevents find shape

    oneFactStopsOneFact(19, 20, 2.5); //Seen turns off r1-off
    connectOneFactToOneFact(31, 20, 2.5); //cog-seq r1-off (r1s inhib)
    connectOneFactToOneFact(20, 32, 2.25); //r1-off starts r2-on-start
    connectOneFactToOneFact(32, 42, 2.25); //r2-on-start starts r2-on-done
    oneFactStopsOneFact(32, 31, 1.0); //r2-on stops cog-seq
    oneFactStopsOneFact(32, 20, 2.5); //r2-on stops r1-off
    oneFactStopsOneFact(32, 19, 2.5); //r2-on-start prevents seen-room
    oneFactStopsOneFact(42, 32, 2.5); //r2-on-done stops r2-on-start
    oneFactStopsOneFact(19, 42, 2.5); //Seen stops r2-on-done

    connectOneFactToOneFact(19, 33, 1.0); //seenRoom supports explore-done
    oneFactStopsOneFact(33, 19, 2.5); //Explore-Done stops seen
    oneFactStopsOneFact(33, 25, 2.5); //Explore-Done stops target-door
    oneFactStopsOneFact(33, 14, 8.5); //Explore-Done stops pyr
    oneFactStopsOneFact(33, 15, 8.5); //Explore-Done stops stal
    oneFactStopsOneFact(33, 16, 8.5); //Explore-Done stops vstripes
    oneFactStopsOneFact(33, 17, 8.5); //Explore-Done stops hstripes
    oneFactStopsOneFact(33, 21, 8.5); //Explore-Done stops BP
    oneFactStopsOneFact(33, 22, 8.5); //Explore-Done stops SP
    oneFactStopsOneFact(33, 23, 8.5); //Explore-Done stops BS
    oneFactStopsOneFact(33, 24, 8.5); //Explore-Done stops SS

    //stop explore done
    connectOneFactToOneFact(33, 43, 2.223); //explore-dn starts explore-dn-stop
    oneFactStopsOneFact(43, 33, 2.5); //explore-done-stop stops Explore-Done
  }

  private void createExploreDoneFeature(int featureNum) {
    int neuronsInFeature = 40;
    for (int neuron = 0; neuron < neuronsInFeature; neuron++) {
      int fromNeuron = (featureNum * neuronsInFeature) + neuron;
      if ((neuron % 5) == 0)
        neurons[fromNeuron].setInhibitory(true);
      else {
        neurons[fromNeuron].setInhibitory(false);
        if ((neuron %10) < 5)
          for (int synapse = 0; synapse < 5; synapse++) {
            int toNeuron = fromNeuron - (fromNeuron % 5) + 5 + synapse;
            double weight = 1.1;
            addConnection(fromNeuron, toNeuron, weight);
          }
        else 
          for (int synapse = 0; synapse < 20; synapse++) {
            int toNeuron = (featureNum * neuronsInFeature) + synapse;
            double weight = 1.1;
            addConnection(fromNeuron, toNeuron, weight);
          }
      }
    }
  }

  private void setFactTopology() {
    //setup connections within neurons in individual initial facts
    setPartialFiftyFiftyTopology(0, 4); // ml1, ml2,mr1 and mr2
    setPartialFiftyFiftyTopology(6, 9); // left, right, center,
    setPartialFiftyFiftyTopology(10, 14); // no target, left,right,center
    setPartialFiftyFiftyTopology(14, 20); // pyr, stal, VS, H-Stripes, noObj,
    setPartialFiftyFiftyTopology(20, 33); // R1-Off, BarredPyramid, SP,BS,
    //StripedStal, target door, door absent, door ctr
    //door seen, nxt, inc-cnt, cogSeq-on R2-on-start
    setPartialFiftyFiftyTopology(38, 39); //moved-toward-unknown-object 
    setPartialFiftyFiftyTopology(41, 43); //through-door r2-on-done
    setPartialFiftyFiftyTopology(44, 65);//bump FRmWBarredPyramid SP BS SS 
    //forwardTo&Through, backJamSeen, rtAfterCor,jamLeft, leftBeforeJam,
    //rtFrontJam, backJamOnLeft, rtsAfterCor (9)

    createSupportedFeature(4, 40); // pyramid is partially suppoted
    createSupportedFeature(5, 40); // stal is partially supported
    createSupportedFeature(9, 40); // big is partially supported rest by v2
    createSupportedFeature(19, 40); // seen is supported by goal explore
    createExploreDoneFeature(33);  // explore-done is support by goal explore
    createSupportedFeature(34, 40); //unk-obj supported by shape pattern objrec
    createSupportedFeature(35, 40); //find shape supported by id room
    createSupportedFeature(36, 40); //unk-obj center supported unkObj& targCent
    createSupportedFeature(37, 40); //no object scene supported by find shape
    createSupportedFeature(39, 40); //after move supported by moved toward uObj
    createSupportedFeature(40, 40); //door-still-seen supportedBy objRecJam
    createSupportedFeature(43, 40); //explore-done-stop
    createSupportedFeature(55, 40); //backJamOnLeft supported by throughDoorG

    factSupportsItself(21);
    factSupportsItself(22);
    factSupportsItself(23);
    factSupportsItself(24);
    factSupportsItself(26);
    //factSupportsItself(27);
    factSupportsItself(56); //rtAfterCor
    factSupportsItself(58); 

    //set up connections between facts.
    //Support for cognitive map building
    //stripes and shapes stop no object
    oneFactStopsOneFact(14, 18, 2.5);
    oneFactStopsOneFact(15, 18, 2.5);
    oneFactStopsOneFact(16, 18, 2.5);
    oneFactStopsOneFact(17, 18, 2.5);

    //pyr and stal and hstripes and vstripes inhib each other
    oneFactStopsOneFact(14, 15, 1.25);
    oneFactStopsOneFact(15, 14, 1.25);
    oneFactStopsOneFact(16, 17, 1.25);
    oneFactStopsOneFact(17, 16, 1.25);

    //stripes and shapes turn on combined facts
    connectOneFactToOneFact(14, 21, 1.35); // pyramid supports barred pyramid
    connectOneFactToOneFact(14, 22, 1.35); // pyramid supports striped pyramid
    connectOneFactToOneFact(15, 23, 1.35); // stal supports barred stal
    connectOneFactToOneFact(15, 24, 1.35); // stal supports striped stal
    connectOneFactToOneFact(16, 21, 1.35); // bar supports barred pyramid
    connectOneFactToOneFact(16, 23, 1.35); // bar supports barred stal
    connectOneFactToOneFact(17, 22, 1.35); // stripe supports striped pyramid
    connectOneFactToOneFact(17, 24, 1.35); // stripe supports striped stal

    //prevent two items on problem
    oneFactStopsOneFact(21, 23, 2.5); //barred pyramid stops barred stal
    oneFactStopsOneFact(22, 24, 2.5); //striped pyramid stops striped stal
    oneFactStopsOneFact(21, 22, 2.5); //barred pyramid stops striped pyramid
    oneFactStopsOneFact(23, 24, 2.5); //barred stal stops striped stal

    //seen object means seen room
    connectOneFactToOneFact(21, 19, 1.6); // barred pyr to seen object
    connectOneFactToOneFact(22, 19, 1.6); // striped pyr to seen object
    connectOneFactToOneFact(23, 19, 1.6); // barred stal to seen object
    connectOneFactToOneFact(24, 19, 1.6); // striped stal to seen object

    //find room turns the others off
    oneFactStopsOneFact(19, 18, 2.5); //seen room stops id room
    oneFactStopsOneFact(19, 14, 2.5); //seen room stops pyramid
    oneFactStopsOneFact(19, 15, 2.5); //seen room stops stalactite
    oneFactStopsOneFact(19, 16, 2.5); //seen room stops vstripes
    oneFactStopsOneFact(19, 17, 2.5); //seen room stops hstripes
    oneFactStopsOneFact(19, 21, 2.5); //seen room stops barredpyr 
    oneFactStopsOneFact(19, 22, 2.5); //seen room stops stripedpyr
    oneFactStopsOneFact(19, 23, 2.5); //seen room stops barredstal
    oneFactStopsOneFact(19, 24, 2.5); //seen room stops stripedstal

    //Go to the door when exploring 
    connectOneFactToOneFact(25, 26, 2.22223); //target door to door absent
    oneFactStopsOneFact(27, 26, 2.5); //door in front stops door absent

    connectOneFactToOneFact(29, 30, 1.35); //next primes inc counter
    connectOneFactToOneFact(21, 30, 1.35); // barred pyr to inc counter
    connectOneFactToOneFact(22, 30, 1.35); // striped pyr to inc counter
    connectOneFactToOneFact(23, 30, 1.35); // barred stal to inc counter
    connectOneFactToOneFact(24, 30, 1.35); // striped stal to inc counter
    oneFactStopsOneFact(30, 29, 2.5); //inc counter stops next

    connectOneFactToOneFact(30, 31, 2.25); // inc count starts Cog Seq On
    //cog seq started by time Cog Seq On comes on 
    oneFactStopsOneFact(31, 30, 2.5); //Cog Seq stops inc counter 
    //undoneoneFactStopsOneFact(31, 19, 2.5); //Cog Seq prevents seen room
    oneFactStopsOneFact(29, 19, 2.5); //next prevents seen room

    setExploreFacts();

    oneFactStopsOneFact(21, 45, 2.5); //barred pyramid stops findRoomWithBP
    oneFactStopsOneFact(22, 46, 2.5); //barred pyramid stops findRoomWithSP
    oneFactStopsOneFact(23, 47, 2.5); //barred pyramid stops findRoomWithBS
    oneFactStopsOneFact(24, 48, 2.5); //barred pyramid stops findRoomWithSS

  }

  private void setModuleTopology() {
    setFiftyFiftyTopology2(1.1);
  }

  // ------------------learning goal-action stuff
  private int neuronsInGoal2CA = 200;

  private void setGoal2Topology(int CASize, double exciteWeight) {
    setConnections(0, size());
    for (int neuronNum = 0; neuronNum < size(); neuronNum++) {
      for (int synapseNum = 0; synapseNum < neurons[neuronNum]
                                                    .getCurrentSynapses(); synapseNum++) {
        if ((neuronNum / CASize) == (neurons[neuronNum].synapses[synapseNum].toNeuron.id / CASize)) {
          if (neurons[neuronNum].getInhibitory())
            neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
          else
            neurons[neuronNum].synapses[synapseNum].setWeight(exciteWeight);
        } else { // opposite CA
          if (neurons[neuronNum].getInhibitory()) {
            neurons[neuronNum].synapses[synapseNum].setWeight(-0.99);
          } else
            neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
        }
      }
      if (neurons[neuronNum].getInhibitory())
        for (int synapse = 0; synapse < 10; synapse++) {
          int toNeuron = 0;
          if (neuronNum < 200)
            toNeuron = 1;
          toNeuron *= 200;
          toNeuron += (int) (CANT23.random.nextFloat() * 200);
          addConnection(neuronNum, toNeuron, 0.99);
        }
    }
  }

  private static double normalRand(float mean, float variance) {
    double p = CANT23.random.nextFloat();
    return mean + Math.sqrt(variance) * Math.log(p / (1 - p));
  }

  private void setGaussTopology() {
    for (int neuron = 0; neuron < getSize(); neuron++) {
      for (int synapse = 0; synapse < 100; synapse++) {
        // Normal (Gaussian) connectivity
        int toNeuron = (int) normalRand(neuron, getSize() / 2) % getSize();
        if (toNeuron >= 0)
          toNeuron = toNeuron;
        else
          toNeuron = toNeuron + getSize();
        addConnection(neuron, toNeuron, 1.0);
      }
    }
  }

  private int nOfModule2CAs = 2;
  private int neuronsInModule2CA = 200;

  private void setModule2Topology() {
    for (int neuron = 0; neuron < getSize(); neuron++) {
      if ((neuron % 5) == 0)
        neurons[neuron].setInhibitory(true);
      else
        neurons[neuron].setInhibitory(false);
    }

    for (int neuron = 0; neuron < getSize(); neuron++) {
      int CA = neuron / neuronsInModule2CA;
      if (neurons[neuron].isInhibitory) {
        for (int synapse = 0; synapse < 80 * ((1 + nOfModule2CAs) / 2); synapse++) {
          int toNeuron = (int) (CANT23.random.nextFloat() * getSize());
          if ((toNeuron / neuronsInModule2CA) != CA)
            addConnection(neuron, toNeuron, 2.0);
        }
      } else {
        for (int synapse = 0; synapse < 15; synapse++) {
          int toNeuron = (int) (CANT23.random.nextFloat() * neuronsInModule2CA)
          + (CA * neuronsInModule2CA);
          addConnection(neuron, toNeuron, 1.0);
        }
      }
    }
  }

  private void setSequenceTopology(double nextWeight) {
    int CASize = 200;
    int totalCAs = getSize() / CASize;

    // set up inhibitory
    for (int i = 0; i < getSize(); i++) {
      if (((i % 5) == 1) || ((i % 5) == 3))
        neurons[i].setInhibitory(true);
      else
        neurons[i].setInhibitory(false);
    }

    for (int CA = 0; CA < totalCAs; CA++) {
      // add connections and weights
      for (int neuronNum = 0; neuronNum < CASize; neuronNum++) {
        int fromNeuron = neuronNum + (CA * CASize);
        if (neurons[fromNeuron].isInhibitory()) {
          // inhibit prior
          if (CA > (0))
            for (int synapse = 0; synapse < 15; synapse++) {
              int toNeuron = ((neuronNum + synapse) % CASize)
              + ((CA - 1) * CASize);
              addConnection(fromNeuron, toNeuron, 2.0);
            }
        } else { // excitatory
          // set self connections
          for (int synapse = 1; synapse < 15; synapse++) {
            int toNeuron;
            if ((synapse % 3) == 0)
              toNeuron = ((neuronNum + (synapse * 20)) % CASize)
              + (CA * CASize);
            else
              toNeuron = ((neuronNum + synapse) % CASize) + (CA * CASize);
            addConnection(fromNeuron, toNeuron, 1.5);
          }

          // set next connections
          if (CA < (totalCAs - 1))
            for (int synapse = 0; synapse < 5; synapse++) {
              int toNeuron = ((neuronNum + synapse) % CASize)
              + ((CA + 1) * CASize);
              addConnection(fromNeuron, toNeuron, nextWeight);
            }
        }// excite
      }// neuron
    }// CA
  }

  private void allSequenceNodesStopFirst() {
    int CASize = 200;
    int totalCAs = getSize() / CASize;

    for (int CA = 1; CA < totalCAs; CA++) {
      // add connections and weights
      for (int neuronNum = 0; neuronNum < CASize; neuronNum++) {
        int fromNeuron = neuronNum + (CA * CASize);
        if (neurons[fromNeuron].isInhibitory()) {
          // inhibit prior
          if (CA > (0))
            for (int synapse = 0; synapse < 15; synapse++) {
              int toNeuron = ((neuronNum + synapse) % CASize);
              addConnection(fromNeuron, toNeuron, 2.0);
            }
        }
      }// neuron
    }// CA
  }

  private void setCounterTopology() {
    double forwardWt = 1.4;
    setPartialFiftyFiftyTopology(0, 5); // five counters
    connectOneFactToOneFact(0,1,forwardWt); 
    connectOneFactToOneFact(1,2,forwardWt);
    connectOneFactToOneFact(2,3,forwardWt);
    connectOneFactToOneFact(3,4,forwardWt);
    oneFactStopsOneFact(1, 0, 2.5); 
    oneFactStopsOneFact(2, 1, 2.5); 
    oneFactStopsOneFact(3, 2, 2.5); 
    oneFactStopsOneFact(4, 3, 2.5); 
    //forward inhibition to slow things
    oneFactStopsOneFact(0, 2, 2.5); 
    oneFactStopsOneFact(1, 3, 2.5); 
    oneFactStopsOneFact(2, 4, 2.5); 

  }
  public void initializeNeurons() {
    // set up topologies.

    if (topology == 1) {
      createNeurons(50);
      // System.out.println("parse input topology ");
      setInputTopology();
    } else if (topology == 2) {
      createNeurons(100);
      // System.out.println("bar 1 topology ");
      setBarOneTopology();
    } else if (topology == 3) {
      createNeurons(60);
      // System.out.println("n/v access topology ");
      setNounAccessTopology();
    } else if (topology == 4) {
      createNeurons(50);
      // System.out.println("n/v sem topology ");
      setSemTopology();
    } else if (topology == 5) {
      createNeurons(180);
      // System.out.println("rule topology ");
      setRuleOneTopology();
    } else if (topology == 6) {
      createNounInstanceNeurons(140);
      // System.out.println("noun instance topology ");
      setNounInstanceTopology();
    } else if (topology == 7) {
      createVerbInstanceNeurons(105);
      // System.out.println("verb instance topology ");
      setVerbInstanceTopology();
    } else if (topology == 8) {
      createNeurons(35);
      // System.out.println("other word topology ");
      setOtherWordTopology();
    } else if (topology == 9) {
      createNeurons(70);
      //System.out.println("rule selection topology ");
      setRuleSelectionTopology();
    } else if (topology == 10) {
      createNeurons(90);
      //System.out.println("goal set topology ");
      setGoalSetTopology();
    } else if (topology == 11) {
      createNeurons(100);
      // System.out.println("goal2 topology ");
      setGoal2Topology(neuronsInGoal2CA, 0.3);
    } else if (topology == 12) {
      createNeurons(100);
      // System.out.println("value explore topology ");
      setGaussTopology();
    } else if (topology == 13) {
      createNeurons(80);
      // System.out.println("goal 1 topology ");
      setGoal1Topology();
    } else if (topology == 14) {
      createNeurons();
      // no internal net connections
      // System.out.println("visual input topology ");
    } else if (topology == 15) {
      createNeurons(2000);
      // System.out.println("control topology ");
      setControlTopology();
    } else if (topology == 16) {
      createNeurons(80);
      // System.out.println("action topology ");
      setActionTopology();
    } else if (topology == 17) {
      createNeurons(200);
      // System.out.println("fact topology ");
      setFactTopology();
    } else if (topology == 18) {
      createNeurons(160);
      // System.out.println("module topology ");
      setModuleTopology();
    } else if (topology == 19) {
      createNeurons(380);
      // no internal net connections
      // System.out.println("retina topology ");
    } else if (topology == 20) {
      createNeurons(100);
      // no internal net connections
      // System.out.println("V1 topology ");
    } else if (topology == 21) {
      createNeurons(280);
      //System.out.println("Obj Rec topology ");
      setObjRecTopology();
    } else if (topology == 22) {
      createNeurons(90);
      // System.out.println("module2 topology ");
      setModule2Topology();
    } else if (topology == 23) {
      createNeurons(90);
      //System.out.println("value2 topology ");
      setSequenceTopology(0.23);
      allSequenceNodesStopFirst();
    } else if (topology == 24) {
      createNeurons(120);
      // no internal net connections
      // System.out.println("v1lines topology ");
    } else if (topology == 25) {
      createNeurons(80);
      // no internal net connections
      // System.out.println("gratings topology ");
    } else if (topology == 26) {
      createNeurons(180);
      // System.out.println("rule bar 2 topology ");
      setRuleTwoTopology();
    } else if (topology == 27) {
      createNeurons(20);
      //System.out.println("Instance Counter Topology ");
      setInstanceCounterTopology();
    } else if (topology == 28) {
      createNeurons(10);
      //System.out.println("NextWordTopology ");
      setNextWordTopology();
    } else if (topology == 30) { // kailash
      createNeurons(200);
      setMutualExclusiveNeurons(200); // 200 is the size of each CA in the net
    } else if (topology == 31) {
      createNeurons(30);
      // System.out.println("counter topology ");
      setCounterTopology();
    } else if (topology == 32) { // room2
      createNeurons(200);
      setMutualExclusiveNeurons2(200); // 200 is the size of each CA in the net
    } else
      System.out.println("bad toppology specified " + topology);
  }

  // **********Connect Nets to Each Other***********************************
  int prepStart = 1800;
  private int prepCASize = 360;

  private int goalSetSize = 10;

  private void connectASemToAGoalSet(int word, int goal, CABot3Net goalSetNet){
    int semCASz = 60;
    for (int fromNeuron = word * semCASz; fromNeuron < (word + 1) * semCASz; 
    fromNeuron++) {
      int toNeuron = fromNeuron % 10;
      toNeuron += goal * goalSetSize;
      neurons[fromNeuron].addConnection(goalSetNet.neurons[toNeuron], 0.25);
    }
  }

  public void connectVerbSemToGoalSet(CABot3Net goalSetNet) {
    connectASemToAGoalSet(0, 20, goalSetNet); // move
    connectASemToAGoalSet(1, 21, goalSetNet); // turn
    connectASemToAGoalSet(4, 22, goalSetNet); // go
    connectASemToAGoalSet(6, 23, goalSetNet); // center
    connectASemToAGoalSet(7, 24, goalSetNet); // explore
    connectASemToAGoalSet(8, 25, goalSetNet); // stop
  }

  public void connectNounSemToGoalSet(CABot3Net goalSetNet) {
    connectASemToAGoalSet(0, 0, goalSetNet); // left
    connectASemToAGoalSet(10, 1, goalSetNet); // right
    connectASemToAGoalSet(11, 2, goalSetNet); // forward
    connectASemToAGoalSet(12, 3, goalSetNet); // backward
    connectASemToAGoalSet(1, 4, goalSetNet); // pyramid
    connectASemToAGoalSet(3, 5, goalSetNet); // stalactite
    connectASemToAGoalSet(8, 6, goalSetNet); // door
  }

  private void connectAnOtherToAGoalSet(int word,int goal,
      CABot3Net goalSetNet){
    int otherCASz = 100;
    for (int fromNeuron = word * otherCASz; fromNeuron < (word+1)*otherCASz; 
    fromNeuron++) {
      int toNeuron = fromNeuron % 10;
      toNeuron += goal * goalSetSize;
      neurons[fromNeuron].addConnection(goalSetNet.neurons[toNeuron], 0.2);
    }
  }

  public void connectOtherToGoalSet(CABot3Net goalSetNet) {
    connectAnOtherToAGoalSet(6,30,goalSetNet); //striped
    connectAnOtherToAGoalSet(7,31,goalSetNet); //barred
  }

  // turn on goalSet via control and language command semantics
  // each goalset neuron gets 1.25 per cycle
  public void connectControlToGoalSet(CABot3Net goalSetNet) {
    // control 2 primes goal sets
    for (int fromNeuron = 80; fromNeuron < 120; fromNeuron++) {
      if (!neurons[fromNeuron].isInhibitory()) {
        // get offset 0-3
        int toNeuron = (fromNeuron % 5) - 1;
        toNeuron += (((fromNeuron - 80) / 10) * 4);
        for (int synapse = 0; synapse < 25; synapse++) {
          neurons[fromNeuron].addConnection(goalSetNet.neurons[toNeuron], 1.25);
          toNeuron += 16;
        }
      }
    }
    // control 3 turns off goal sets
    for (int i = 0; i < 8; i++) {
      int fromNeuron = (i * 5) + 120; // 120 is the control 3 offset
      for (int synapse = 0; synapse < 50; synapse++) {
        int toNeuron = (i * 50) + synapse;
        neurons[fromNeuron].addConnection(goalSetNet.neurons[toNeuron], -4.0);
      }
    }
  }

  public void connectControlToValue2(CABot3Net value2Net) {
    // control 2 turns on value2
    for (int i = 80; i < 120; i++) {
      if (!neurons[i].isInhibitory()) {
        for (int synapse = 0; synapse < 6; synapse++) {
          int toNeuron = (i * synapse) % 200;
          neurons[i].addConnection(value2Net.neurons[toNeuron], 2.0);
        }
      }
    }
    // control 3 turns off value2
    for (int i = 0; i < 8; i++) {
      int fromNeuron = (i * 5) + 120; // 120 is the control 3 offset
      for (int synapse = 0; synapse < 25; synapse++) {
        int toNeuron = (i * 25) + synapse + 400;
        neurons[fromNeuron].addConnection(value2Net.neurons[toNeuron], -24.0);
      }
    }
  }

  public void connectValue2ToControl(CABot3Net controlNet) {
    // The third CA primes control CA 3
    // 200 neurons 60% excitatory -> 120 lets just use the first 40
    int toNeuron = 120;
    for (int fromNeuron = 500; fromNeuron < 567; fromNeuron++) {
      if (!neurons[fromNeuron].isInhibitory()) {
        neurons[fromNeuron].addConnection(controlNet.neurons[toNeuron], 1.0);
        toNeuron++;
      }
    }
  }

  private void connectOneGoalSetToOneGoal(int goalSet, int goal,
      CABot3Net goalNet, double weight) {
    for (int i = 0; i < 10; i++) {
      int fromNeuron = (goalSet * 10) + i;
      for (int synapse = 0; synapse < 4; synapse++) {
        int toNeuron = (i % 5) + (synapse * 10) + (goal * neuronsInGoal);
        neurons[fromNeuron].addConnection(goalNet.neurons[toNeuron], weight);
      }
    }
  }

  public void connectGoalSetToGoal1(CABot3Net goal1Net) {
    connectOneGoalSetToOneGoal(21, 0, goal1Net, 1.0); // turn to turn left;
    connectOneGoalSetToOneGoal(0, 0, goal1Net, 1.0); // left to turn left;
    connectOneGoalSetToOneGoal(21, 1, goal1Net, 1.0); // turn to turn right
    connectOneGoalSetToOneGoal(1, 1, goal1Net, 1.0); // right to turn right
    connectOneGoalSetToOneGoal(20, 2, goal1Net, 1.0); // move to move forward
    connectOneGoalSetToOneGoal(2, 2, goal1Net, 1.0); // forward to move forward
    connectOneGoalSetToOneGoal(20, 3, goal1Net, 1.0); // move to move backward
    connectOneGoalSetToOneGoal(3, 3, goal1Net, 1.0); // move to move backward
    connectOneGoalSetToOneGoal(20, 4, goal1Net, 1.0); // move to turnleft+go
    connectOneGoalSetToOneGoal(0, 4, goal1Net, 1.0); // left to turnleft+go
    connectOneGoalSetToOneGoal(20, 5, goal1Net, 1.0); // move to turnright+go
    connectOneGoalSetToOneGoal(1, 5, goal1Net, 1.0); // right to turnright+go
    connectOneGoalSetToOneGoal(21, 6, goal1Net, 1.0); // turn to turn toward
    connectOneGoalSetToOneGoal(4, 6, goal1Net, 1.0); // pyramid to turn toward
    connectOneGoalSetToOneGoal(5, 6, goal1Net, 1.0); // stalactite to turn
    // toward
    connectOneGoalSetToOneGoal(22, 7, goal1Net, 1.0); // go to go to
    connectOneGoalSetToOneGoal(4, 7, goal1Net, 1.0); // pyramid to go to
    connectOneGoalSetToOneGoal(5, 7, goal1Net, 1.0); // stalactite to go to
    connectOneGoalSetToOneGoal(4, 8, goal1Net, 1.0); // pyramid to to pyramid
    connectOneGoalSetToOneGoal(5, 9, goal1Net, 1.0); // stalactite to stalacite
    connectOneGoalSetToOneGoal(6, 13, goal1Net, 1.0); // door to door
    connectOneGoalSetToOneGoal(6, 7, goal1Net, 1.0); // door to go to
    connectOneGoalSetToOneGoal(23, 10, goal1Net, 1.0); // center to center
    connectOneGoalSetToOneGoal(4, 10, goal1Net, 1.0); // pyramid to center
    connectOneGoalSetToOneGoal(5, 10, goal1Net, 1.0); // stalactite to center
    connectOneGoalSetToOneGoal(24, 11, goal1Net, 2.0); // explore to explore
    connectOneGoalSetToOneGoal(25, 12, goal1Net, 2.0); // stop to stop

    // move (pyramid, striped etc.) to move before striped pyramid (etc.)
    connectOneGoalSetToOneGoal(20, 17, goal1Net, 0.6); // move to mbsp
    connectOneGoalSetToOneGoal(4, 17, goal1Net, 0.6); // pyramid to mbsp
    connectOneGoalSetToOneGoal(30, 17, goal1Net, 0.6); // striped to mbsp
    connectOneGoalSetToOneGoal(20, 18, goal1Net, 0.6); // move to mbbp
    connectOneGoalSetToOneGoal(4, 18, goal1Net, 0.6); // pyramid to mbbp
    connectOneGoalSetToOneGoal(31, 18, goal1Net, 0.6); // barred to mbbp
    connectOneGoalSetToOneGoal(20, 19, goal1Net, 0.6); // move to mbbs
    connectOneGoalSetToOneGoal(5, 19, goal1Net, 0.6); // stal to mbbs
    connectOneGoalSetToOneGoal(31, 19, goal1Net, 0.6); // barred to mbbs
    connectOneGoalSetToOneGoal(20, 20, goal1Net, 0.6); // move to mbss
    connectOneGoalSetToOneGoal(5, 20, goal1Net, 0.6); // stal to mbss
    connectOneGoalSetToOneGoal(30, 20, goal1Net, 0.6); // striped to mbss

  }

  /******* Action Connectitivity */
  public void connectControlToFact(CABot3Net factNet) {
    // Top control CA suppress action
    for (int neuronNum = 0; neuronNum < 200; neuronNum++) {
      if (neurons[neuronNum].isInhibitory())
        for (int synapseNum = 0; synapseNum < 250; synapseNum++) {
          int toNeuron = (int) (CANT23.random.nextFloat() * factNet.getSize());
          neurons[neuronNum].addConnection(factNet.neurons[toNeuron], -8.0);
        }
    }
    // bottom controls also suppress action
    for (int neuronNum = 600; neuronNum < 1600; neuronNum++) {
      if (neurons[neuronNum].isInhibitory())
        for (int synapseNum = 0; synapseNum < 250; synapseNum++) {
          int toNeuron = (int) (CANT23.random.nextFloat() * factNet.getSize());
          neurons[neuronNum].addConnection(factNet.neurons[toNeuron], -8.0);
        }
    }
  }

  private void connectV2PositionToFact(CABot3Net factNet, int V2GroupCol,
      int fact) {
    for (int row = 0; row < getInputRows() * 4; row++) {
      for (int col = 2; col < 10; col++) {
        int fromNeuron = (V2GroupCol * 10) + (row * getInputRows()) + col;
        for (int synapseNum = 0; synapseNum < 20; synapseNum++) {
          int toNeuron = (fact * neuronsInFact)
          + (int) (CANT23.random.nextFloat() * neuronsInFact);
          neurons[fromNeuron].addConnection(factNet.neurons[toNeuron], 1.5);
        }
      }
    }
  }

  private void connectV2TopHalfShapeColFact(int shape, int lCol, int factNum,
      CABot3Net factNet) {
    int shapeOffset = shape * 2500;
    for (int lRow = 0; lRow < 5; lRow++) {
      int locOffset = (lRow * 500) + (lCol * 10);
      for (int row = 0; row < 5; row++) {
        for (int col = 0; col < 10; col++) {
          int fromNeuron = (row * 50) + col + locOffset + shapeOffset;
          if (!neurons[fromNeuron].isInhibitory()) {
            // two neurons from each top half connect to each of the
            // feature neurons 0..4, 10..14 20..24,30..34;
            int toNeuron = (factNum * neuronsInFact);
            toNeuron += row + (((col % 5) - 1) * 10);
            neurons[fromNeuron].addConnection(factNet.neurons[toNeuron], 0.65); // 1.15
          }
        }
      }
    }
  }

  public void connectObjRecToFact(CABot3Net factNet) {
    for (int shape = 0; shape < 3; shape ++) {
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
    for (int bigCol=0; bigCol < 5; bigCol ++ ){
      connectV2TopHalfShapeColFact(0,bigCol,14,factNet); //pyramid to pyramid
      connectV2TopHalfShapeColFact(1,bigCol,15,factNet); //stal to stal
      connectV2TopHalfShapeColFact(4,bigCol,34,factNet); //unk to unknown
    }
    connectV2TopHalfShapeColFact(4,2,36,factNet); //unk-obj center
    connectV2TopHalfShapeColFact(4,0,6,factNet); //unk-obj left left
    connectV2TopHalfShapeColFact(4,1,6,factNet); //unk-obj leftC left
    connectV2TopHalfShapeColFact(4,3,7,factNet); //unk-obj rightC right
    connectV2TopHalfShapeColFact(4,4,7,factNet); //unk-obj right right

    connectV2TopHalfShapeColFact(2,2,27,factNet); //jamb center to door cntr
    connectV2TopHalfShapeColFact(2,0,52,factNet); //jamb left to jambLeft
    connectV2TopHalfShapeColFact(2,0,55,factNet); //jamb left to backjambLeft
    for (int col = 0; col < 5; col++) {
      connectV2TopHalfShapeColFact(2,col,40,factNet); //jamb to door-still-seen
    }
    /*door not used now July/31/09
    connectV2TopHalfShapeColFact(3,0,28,factNet); //door all 5 cols to in door
    connectV2TopHalfShapeColFact(3,1,28,factNet); 
    connectV2TopHalfShapeColFact(3,2,28,factNet); 
    connectV2TopHalfShapeColFact(3,3,28,factNet); 
    connectV2TopHalfShapeColFact(3,4,28,factNet); 
     */
  }

  public void connectAGratingToAFact(int grate,int fact, CABot3Net 
      factNet) {
    double wt = 0.2;
    for (int i = 0; i < 2500; i++) {
      int fromNeuron = i + (grate*2500);
      int toNeuron= i%5;
      toNeuron += fact*neuronsInFact;
      neurons[fromNeuron].addConnection(factNet.neurons[toNeuron], wt); 
      neurons[fromNeuron].addConnection(factNet.neurons[toNeuron+10], wt); 
      neurons[fromNeuron].addConnection(factNet.neurons[toNeuron+20], wt); 
      neurons[fromNeuron].addConnection(factNet.neurons[toNeuron+30], wt); 
    }
  }
  //Add connections from the vstripe and hstripe gratings to the facts.conn
  //In collaboration with the find obj fact.
  public void connectGratingToFact(CABot3Net factNet) {
    connectAGratingToAFact(0,17,factNet); //hstripes
    connectAGratingToAFact(1,16,factNet); //vstripes
    connectAGratingToAFact(2,17,factNet); //hstripes  (fat hstripes)
    connectAGratingToAFact(3,16,factNet); //vstripes  (fat vstripes)
    connectAGratingToAFact(4,16,factNet); //sstripes  
    connectAGratingToAFact(5,16,factNet); //bstripes  
 }

  private void connectToObjRecTopHalf(int fromNeuron, int shape, 
      CABot3Net ObjRecNet) {
    int shapeOffset = 2500 * shape;
    for (int lCol = 0; lCol < 5; lCol++) {
      for (int lRow = 0; lRow < 5; lRow++) {
        int placeOffset = (lRow * 500) + (lCol * 10);
        int iRow = (fromNeuron % neuronsInFact) / 10;
        int iCol = fromNeuron % 10;
        int toNeuron = iCol + (iRow * 50) + placeOffset + shapeOffset;
        neurons[fromNeuron].addConnection(ObjRecNet.neurons[toNeuron], 1.3);
      }
    }
  }

  private void connectOneFactToObjRecTopHalfs(int factNum, int visObj, 
    CABot3Net ObjRecNet) {
    for (int neuronNum = 0; neuronNum < neuronsInFact; neuronNum++) {
      int fromNeuron = neuronNum + (factNum * neuronsInFact);
      if (!neurons[fromNeuron].isInhibitory()) {
        connectToObjRecTopHalf(fromNeuron, visObj, ObjRecNet);
      }
    }
  }

  public void connectFactToObjRec(CABot3Net ObjRecNet) {
    connectOneFactToObjRecTopHalfs(4,0,ObjRecNet); //target pyramid 
    connectOneFactToObjRecTopHalfs(5,1,ObjRecNet); //target stals 
    connectOneFactToObjRecTopHalfs(25,2,ObjRecNet); //target door to jam
    //connectOneFactToObjRecTopHalfs(25,3,ObjRecNet); //target door to door
    //findobj to all shapes
    for (int shape = 0; shape < 5; shape++) 
      connectOneFactToObjRecTopHalfs(35,shape,ObjRecNet); 
    connectOneFactToObjRecTopHalfs(28,2,ObjRecNet); //door-ahead to jam
  }

  // ************************************************
  private int neuronsInGoal = 40;
  private int neuronsInModule = 40;

  private void connectOneGoalToOneModule(int goalNum, int moduleNum,
      CABot3Net moduleNet, double weight) {
    for (int neuronNum = 0; neuronNum < neuronsInGoal; neuronNum++) {
      int fromNeuron = neuronNum + (goalNum * neuronsInGoal);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuronNum + (moduleNum * neuronsInModule);
        neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron], weight);
        if ((fromNeuron % 5) == 1)
          neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron - 1],
              weight);
      }
    }
  }

  // center, a goal2 CA, and a module2 CA all need to be firing.
  private void connectGoal1CenterToModule1(CABot3Net module1Net) {
    // connect the center goal to both left and right
    for (int neuronNum = 0; neuronNum < neuronsInGoal; neuronNum++) {
      int fromNeuron = neuronNum + 400;
      if (!neurons[fromNeuron].isInhibitory()) {
        // left
        int toNeuron = neuronNum;
        neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron],
            goal1Module1Wt);
        if ((fromNeuron % 5) == 1)
          neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron - 1],
              goal1Module1Wt);
        // right
        toNeuron += neuronsInModule;
        neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron],
            goal1Module1Wt);
        if ((fromNeuron % 5) == 1)
          neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron - 1],
              goal1Module1Wt);
      }
    }
  }

  public void connectGoal1ToModule(CABot3Net moduleNet) {
    connectOneGoalToOneModule(0, 0, moduleNet, 2.25);// turn+left
    connectOneGoalToOneModule(1, 1, moduleNet, 2.25);// turn+right
    connectOneGoalToOneModule(2, 2, moduleNet, 2.25);// move+foreward
    connectOneGoalToOneModule(3, 3, moduleNet, 2.25);// move+backward
    connectOneGoalToOneModule(7, 2, moduleNet, 1.15);// go to forward 1/2 with
    // center fact
    connectOneGoalToOneModule(6, 5, moduleNet, 1.15);// turn to center err 1/2
    // with center fact
    connectGoal1CenterToModule1(moduleNet);
    connectOneGoalToOneModule(12, 6, moduleNet, 2.25);// stop to stop
  }

  private void connectOneGoalToOneFact(int goalNum, int factNum, double weight,
      CABot3Net factNet) {

    for (int neuronNum = 0; neuronNum < neuronsInGoal; neuronNum++) {
      int fromNeuron = neuronNum + (goalNum * neuronsInGoal);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuronNum + (factNum * neuronsInFact);
        neurons[fromNeuron].addConnection(factNet.neurons[toNeuron], weight);
        if ((fromNeuron % 5) == 1)
          neurons[fromNeuron].addConnection(factNet.neurons[toNeuron - 1],
              weight);
      }
    }
  }

  private void oneGoalStopsOneFact(int goalNum, int factNum, CABot3Net factNet) {
    for (int neuronNum = 0; neuronNum < neuronsInGoal; neuronNum += 5) {
      int fromNeuron = neuronNum + (goalNum * neuronsInGoal);
      // assert(neurons[fromNeuron].isInhibitory())
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron;
        if ((neuronNum % 10) == 5)
          toNeuron = neuronNum - 5 + synapse;
        else
          toNeuron = neuronNum + synapse;
        toNeuron += factNum * neuronsInFact;
        neurons[fromNeuron].addConnection(factNet.neurons[toNeuron], -1.0);
      }
    }
  }

  public void connectGoal1ToFact(CABot3Net factNet) {
    connectOneGoalToOneFact(4, 0, 2.25, factNet); // move left to ml1
    connectOneGoalToOneFact(5, 2, 2.25, factNet); // move right to mr1
    connectOneGoalToOneFact(8, 4, 2.25, factNet); // pyramid to pyramid
    connectOneGoalToOneFact(9, 5, 2.25, factNet); // stal to stal
    connectOneGoalToOneFact(6, 6, 1.25, factNet); // turn to left
    connectOneGoalToOneFact(6, 7, 1.25, factNet); // turn to right
    connectOneGoalToOneFact(6, 8, 1.25, factNet); // turn to center
    connectOneGoalToOneFact(7, 6, 1.25, factNet); // go to left
    connectOneGoalToOneFact(7, 7, 1.25, factNet); // go to right
    connectOneGoalToOneFact(7, 8, 1.25, factNet); // go to center
    connectOneGoalToOneFact(7, 9, 1.25, factNet); // go to big
    connectOneGoalToOneFact(10, 11, 1.25, factNet); // center to left2
    connectOneGoalToOneFact(10, 12, 1.25, factNet); // center to right2
    connectOneGoalToOneFact(10, 13, 1.25, factNet); // center to center2
    oneGoalStopsOneFact(10, 10, factNet); // center prevents object err

    connectOneGoalToOneFact(11, 33, 1.0, factNet); //explore supports end exp
    //connectOneGoalToOneFact(11, 19, 0.33,factNet); //explore supports seen rm
    connectOneGoalToOneFact(13, 19, 1.01,factNet); //id room supports seen room
    connectOneGoalToOneFact(13, 35, 2.25, factNet); //id room starts find shape
    connectOneGoalToOneFact(14, 25, 2.25, factNet); // door to door target
    connectOneGoalToOneFact(15, 44, 1.0, factNet); // toCorr to Bump
    connectOneGoalToOneFact(15, 49, 2.3, factNet); // toCorr to forwardDoor
    connectOneGoalToOneFact(16, 44, 1.0, factNet); // throughCor to Bump
    connectOneGoalToOneFact(16, 49, 2.25, factNet); //throughCor to forwardDoor
    connectOneGoalToOneFact(16, 50, 2.0, factNet); // throughCor to backJam
    oneGoalStopsOneFact(16, 19, factNet); // throughCor stop seen room
    connectOneGoalToOneFact(16, 41, 2.0, factNet); //throughCor to throughCor
    connectOneGoalToOneFact(21, 45, 1.0, factNet); //getTarget supp findRmBP
    connectOneGoalToOneFact(21, 46, 1.0, factNet); //getTarget supp findRmSP
    connectOneGoalToOneFact(21, 47, 1.0, factNet); //getTarget supp findRmBS
    connectOneGoalToOneFact(21, 48, 1.0, factNet); //getTarget supp findRmSS

    oneGoalStopsOneFact(23, 14, factNet); //foundTarget stops  pyramid
    oneGoalStopsOneFact(23, 15, factNet); 
    oneGoalStopsOneFact(23, 16, factNet); 
    oneGoalStopsOneFact(23, 17, factNet); 
    oneGoalStopsOneFact(23, 21, factNet); //foundTarget stops barred pyramid
    oneGoalStopsOneFact(23, 22, factNet); 
    oneGoalStopsOneFact(23, 23, factNet); 
    oneGoalStopsOneFact(23, 24, factNet); 

    connectOneGoalToOneFact(24, 52, -9.0, factNet);//frontJam prevents jamLeft
    connectOneGoalToOneFact(24, 54, 2.5, factNet);//frontJam start rtFrontJam
    oneGoalStopsOneFact(16, 54, factNet);//throughDoor stops rtFrontJam
    connectOneGoalToOneFact(13, 52, -9.0, factNet);//idRoom prevents jamLeft
    connectOneGoalToOneFact(16, 52, -9.0, factNet);//thruDoor prevents jamLeft
    connectOneGoalToOneFact(25, 52, -9.0, factNet);//moveAfter prevents jamLeft
    connectOneGoalToOneFact(25,6 , -9.0, factNet);//moveAfter prev targetLeft
    connectOneGoalToOneFact(25,11 , -9.0, factNet);//moveAfter prev centerLeft
    connectOneGoalToOneFact(16,6 , -9.0, factNet);//thruDoor prev targetLeft
    connectOneGoalToOneFact(16,11 , -9.0, factNet);//thruDoor prev centerLeft

    //through door
    connectOneGoalToOneFact(25, 56, 2.5, factNet);//moveAfter starts rt2After
    connectOneGoalToOneFact(26, 57, 2.223,factNet);//moveAgain starts rt3After
    connectOneGoalToOneFact(27, 58, 2.223,factNet);//moveThird starts rt4After
    oneGoalStopsOneFact(13, 64, factNet);//idRoom stops rt3Done
  }

  private void oneFactStopsOneGoal(int factNum, int goalNum,CABot3Net goalNet){
    for (int neuronNum = 0; neuronNum < neuronsInFact; neuronNum += 5) {
      int fromNeuron = neuronNum + (factNum * neuronsInFact);
      // assert(neurons[fromNeuron].isInhibitory())
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron;
        if ((neuronNum % 10) == 5)
          toNeuron = neuronNum - 5 + synapse;
        else
          toNeuron = neuronNum + synapse;
        toNeuron += goalNum * neuronsInGoal;
        neurons[fromNeuron].addConnection(goalNet.neurons[toNeuron], -1.0);
      }
    }
  }

  private void oneFactStartsOneGoal(int factNum, int goalNum, CABot3Net 
      goalNet) {
    for (int neuronNum = 0; neuronNum < neuronsInGoal; neuronNum++) {
      int fromNeuron = neuronNum + (factNum * neuronsInFact);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuronNum + (goalNum * neuronsInGoal);
        neurons[fromNeuron].addConnection(goalNet.neurons[toNeuron], 2.25);
        if ((fromNeuron % 5) == 1)
          neurons[fromNeuron].addConnection(goalNet.neurons[toNeuron-1],2.25);
      }
    }
  }


  public void connectFactToGoal1(CABot3Net goalNet) {
    oneFactStopsOneGoal(1, 4, goalNet); // second move left stops move left
    oneFactStopsOneGoal(3, 5, goalNet);// second move right stops move right
    oneFactStopsOneGoal(9, 7, goalNet);// object is big stops go
    oneFactStopsOneGoal(13, 10, goalNet);// object in center stops center

    oneFactStartsOneGoal(19, 14, goalNet);// seen room starts find door
    oneFactStopsOneGoal(28, 14, goalNet);// doorSeen stops find door
    oneFactStartsOneGoal(28, 15, goalNet);// doorSeen start toCorridor
    oneFactStopsOneGoal(33, 11, goalNet);// explore-done stops explore
    oneFactStopsOneGoal(33, 13, goalNet);// explore-done stops id-room
    oneFactStopsOneGoal(33, 14, goalNet);// explore-done stops find-door

    oneFactStartsOneGoal(41, 25, goalNet);// throughDoor starts moveAfterJam

    oneFactStartsOneGoal(45, 22, goalNet);//Find room w BP starts FindTarget
    oneFactStartsOneGoal(46, 22, goalNet);//Find room w SP starts FindTarget
    oneFactStartsOneGoal(47, 22, goalNet);//Find room w BS starts FindTarget
    oneFactStartsOneGoal(48, 22, goalNet);//Find room w SS starts FindTarget

    oneFactStopsOneGoal(45, 23, goalNet);//The find rooms prevent the
    oneFactStopsOneGoal(46, 23, goalNet);//targetFound goal coming on.
    oneFactStopsOneGoal(47, 23, goalNet);
    oneFactStopsOneGoal(48, 23, goalNet);

    connectOneGoalToOneFact(44, 24, 2.5, goalNet);//bump starts frontJam 
    oneFactStopsOneGoal(51, 16, goalNet);//rtAfterCor stops throughDoor
    oneFactStopsOneGoal(54, 24, goalNet);//rt2FrontJam stops atFrontJam
    oneFactStartsOneGoal(54, 16, goalNet);//rt2FrontJam starts throughDoor
    oneFactStopsOneGoal(64, 25, goalNet);//rt3Done stops moveAfterThrough
   }

  private void connectOneFactToOneModule(int factNum, int moduleNum, double wt,
      CABot3Net moduleNet) {

    for (int neuronNum = 0; neuronNum < neuronsInFact; neuronNum++) {
      int fromNeuron = neuronNum + (factNum * neuronsInFact);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuronNum + (moduleNum * neuronsInModule);
        neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron], wt);
        if ((fromNeuron % 5) == 1)
          neurons[fromNeuron]
                  .addConnection(moduleNet.neurons[toNeuron - 1], wt);
      }
    }
  }

  private void oneFactStopsOneModule(int factNum, int moduleNum, double wt,
      CABot3Net moduleNet) {

    for (int neuronNum = 0; neuronNum < neuronsInFact; neuronNum++) {
      int fromNeuron = neuronNum + (factNum * neuronsInFact);
      if (neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 10; synapse++) {
          int toNeuron = neuronNum + (moduleNum * neuronsInModule);
          if ((neuronNum % 10) == 0)
            toNeuron += synapse;
          else
            toNeuron += (synapse - 5);
          neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron], wt);
        }
      }
    }
  }

  public void connectFactToModule(CABot3Net moduleNet) {
    connectOneFactToOneModule(0, 0, 2.25, moduleNet);// turnleft1 to left
    connectOneFactToOneModule(1, 2, 2.25, moduleNet);// turnleft2 to forward
    connectOneFactToOneModule(2, 1, 2.25, moduleNet);// turnright1 to right
    connectOneFactToOneModule(3, 2, 2.25, moduleNet);// turnright2 to forward
    connectOneFactToOneModule(6, 0, 2.25, moduleNet);// object in left to left
    connectOneFactToOneModule(7, 1, 2.25, moduleNet);//object in right to right
    connectOneFactToOneModule(8, 2, 1.5, moduleNet);// object in center to
    // foward
    // forward 1/2 with go goal
    connectOneFactToOneModule(10, 4, 2.22223, moduleNet);// no obj to no obj
    oneFactStopsOneModule(6, 4, -2.5, moduleNet);// left, right and centre
    oneFactStopsOneModule(7, 4, -2.5, moduleNet);// inhibit no obj error
    oneFactStopsOneModule(8, 4, -2.5, moduleNet);

    connectOneFactToOneModule(8, 5, 1.5, moduleNet);// obj cent to error turn

    //explore
    connectOneFactToOneModule(18, 2, 2.25, moduleNet);//no object to forward
    connectOneFactToOneModule(26, 1, 2.25, moduleNet);//door absent to right
    connectOneFactToOneModule(27, 2, 2.25, moduleNet);//door ahead to forward
    connectOneFactToOneModule(36, 2, 2.25, moduleNet);//unkObj Cent to forward
    connectOneFactToOneModule(37, 2, 2.25, moduleNet);//No obj to forward
    connectOneFactToOneModule(44, 1, 2.25, moduleNet);//Bump to right
    connectOneFactToOneModule(49, 2, 2.25, moduleNet);//ForwardToForward
    connectOneFactToOneModule(51, 1, 2.25, moduleNet);//Right to right
    connectOneFactToOneModule(52, 0, 2.25, moduleNet);//jamLeft to left
    connectOneFactToOneModule(54, 1, 2.25, moduleNet);//rtFrontJam to right
    connectOneFactToOneModule(56, 1, 2.25, moduleNet);//rtAfter to right
    connectOneFactToOneModule(59, 2, 2.25, moduleNet);//rt2After to forward
    connectOneFactToOneModule(62, 1, 2.25, moduleNet);//rt3After to right
  }

  private void oneModuleStopsOneGoal(int moduleNum, int goalNum,
      CABot3Net goalNet) {
    for (int neuronNum = 0; neuronNum < neuronsInModule; neuronNum += 5) {
      int fromNeuron = neuronNum + (moduleNum * neuronsInModule);
      // assert(neurons[fromNeuron].isInhibitory())
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron;
        if ((neuronNum % 10) == 5)
          toNeuron = neuronNum - 5 + synapse;
        else
          toNeuron = neuronNum + synapse;
        toNeuron += goalNum * neuronsInGoal;
        neurons[fromNeuron].addConnection(goalNet.neurons[toNeuron], -1.0);
      }
    }
  }

  public void connectModuletoGoal1(CABot3Net goal1Net) {
    oneModuleStopsOneGoal(0, 0, goal1Net); // left left
    oneModuleStopsOneGoal(1, 1, goal1Net); // right right
    oneModuleStopsOneGoal(2, 2, goal1Net); // forward forward
    oneModuleStopsOneGoal(3, 3, goal1Net); // back back
    oneModuleStopsOneGoal(0, 6, goal1Net); // left turn
    oneModuleStopsOneGoal(1, 6, goal1Net); // right turn
    oneModuleStopsOneGoal(4, 6, goal1Net); // no object error turn
    oneModuleStopsOneGoal(4, 7, goal1Net); // no object error stops go
    oneModuleStopsOneGoal(5, 6, goal1Net); // turn to center to turn
    oneModuleStopsOneGoal(6, 11, goal1Net); // stop to explore
    oneModuleStopsOneGoal(6, 12, goal1Net); // stop to stop
  }

  private void oneModuleStopsOneFact(int moduleNum, int factNum, double 
      weight, CABot3Net factNet) {
    for (int neuronNum = 0; neuronNum < neuronsInModule; neuronNum += 5) {
      int fromNeuron = neuronNum + (moduleNum * neuronsInModule);
      // assert(neurons[fromNeuron].isInhibitory())
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron;
        if ((neuronNum % 10) == 5)
          toNeuron = neuronNum - 5 + synapse;
        else
          toNeuron = neuronNum + synapse;
        toNeuron += factNum * neuronsInFact;
        neurons[fromNeuron].addConnection(factNet.neurons[toNeuron], weight);
      }
    }
  }

  public void connectModuletoFact(CABot3Net factNet) { 
    oneModuleStopsOneFact(0, 0, -1.0, factNet); // left stops moveleft1
    oneModuleStopsOneFact(1, 2, -1.0, factNet); // right stops moveright1
    oneModuleStopsOneFact(2, 1, -1.0, factNet); // forward stops moveleft2
    oneModuleStopsOneFact(2, 3, -1.0, factNet); // forward stops moveright2
    oneModuleStopsOneFact(0, 6, -1.0, factNet); // left stops target on left
    oneModuleStopsOneFact(0, 7, -1.0, factNet); 
    oneModuleStopsOneFact(0, 8, -1.0, factNet);
    oneModuleStopsOneFact(1, 6, -1.0, factNet);
    oneModuleStopsOneFact(1, 7, -1.0, factNet); // right stops target on right
    oneModuleStopsOneFact(1, 8, -1.0, factNet);
    oneModuleStopsOneFact(2, 8,-1.0,factNet); // forward stops target on center
    oneModuleStopsOneFact(4, 10,-1.0,factNet); // no target stops target absent
    oneModuleStopsOneFact(0,11,-1.0,factNet);//lt stops center left rt or cent
    oneModuleStopsOneFact(0, 12, -1.0, factNet);
    oneModuleStopsOneFact(0, 13, -1.0, factNet);
    oneModuleStopsOneFact(1,11,-1.0,factNet); //rt stops center left rt or cent
    oneModuleStopsOneFact(1, 12, -1.0, factNet);
    oneModuleStopsOneFact(1, 13, -1.0, factNet);

    oneModuleStopsOneFact(2, 18, -4.0, factNet);//forward stops no object
    oneModuleStopsOneFact(1, 26, -4.0, factNet);//rt stops door absent 
    oneModuleStopsOneFact(2, 27, -4.0, factNet);//forward stops door ahead

    oneModuleStopsOneFact(2, 36, -4.0, factNet);//forward unkobj center
    connectOneFactToOneModule(0, 38, 1.5, factNet);//left to move toward unk
    connectOneFactToOneModule(1, 38, 1.5, factNet);//right to move toward unk
    connectOneFactToOneModule(2, 38, 1.5, factNet);//forward to move toward unk

    oneModuleStopsOneFact(1, 44, -4.0, factNet);//right stops bump
    oneModuleStopsOneFact(2, 49, -4.0, factNet);//forward stops forward
    oneModuleStopsOneFact(1, 51, -4.0, factNet);//right stops right
    oneModuleStopsOneFact(0, 52, -4.0, factNet);//left stops jamLeft
    connectOneGoalToOneFact(1, 57, 1.25, factNet); //rt turns on rtStart
    oneModuleStopsOneFact(1, 58, -4.0, factNet);//rt prevents rtDone
    connectOneGoalToOneFact(2, 60, 1.25, factNet); //for turns on rt2Start
    oneModuleStopsOneFact(1, 61, -4.0, factNet);//for prevents rt2Done
    oneModuleStopsOneFact(2, 58, -4.0, factNet);//for stops rtDone
    connectOneGoalToOneFact(1, 63, 1.25, factNet); //rt turns on rt3Start
    oneModuleStopsOneFact(1, 64, -4.0, factNet);//rt prevents rt3Done
  }

  private int neuronsInAction = 40;

    /*
  private void connectOneModuleToOneAction(int moduleNum, int actionNum,
      CABot3Net actionNet) {
    for (int neuronNum = 0; neuronNum < neuronsInModule; neuronNum++) {
      int fromNeuron = neuronNum + (moduleNum * neuronsInModule);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = neuronNum + (actionNum * neuronsInAction);
        neurons[fromNeuron].addConnection(actionNet.neurons[toNeuron], 2.25);
        if ((fromNeuron % 5) == 1)
          neurons[fromNeuron].addConnection(actionNet.neurons[toNeuron - 1],
              2.25);
      }
    }
  }
    */

  private void connectOneModuleToOneAction(int moduleNum, int actionNum,
      CABot3Net actionNet) {
    for (int neuronNum = 0; neuronNum < neuronsInModule; neuronNum++) {
      int fromNeuron = neuronNum + (moduleNum * neuronsInModule);
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = (neuronNum%5) + (actionNum * neuronsInAction);
        neurons[fromNeuron].addConnection(actionNet.neurons[toNeuron], 0.552);
        if ((fromNeuron % 5) == 1)
          neurons[fromNeuron].addConnection(actionNet.neurons[toNeuron - 1],
              0.552);
      }
    }
  }

  public void connectModuleToAction(CABot3Net actionNet) {
    connectOneModuleToOneAction(0, 0, actionNet);// turnleft
    connectOneModuleToOneAction(1, 1, actionNet);// turnright
    connectOneModuleToOneAction(2, 2, actionNet);// moveforeward
    connectOneModuleToOneAction(3, 3, actionNet);// movebackward
    connectOneModuleToOneAction(4, 4, actionNet);// no target error
    connectOneModuleToOneAction(5, 5, actionNet);// turn to center error
    connectOneModuleToOneAction(6, 6, actionNet);// stop to stop
  }

    /*  private void oneActionStopsOneModule(int actionNum, int moduleNum,
      CABot3Net moduleNet) {

    for (int neuronNum = 0; neuronNum < neuronsInAction; neuronNum += 5) {
      int fromNeuron = neuronNum + (actionNum * neuronsInAction);
      // assert(neurons[fromNeuron].isInhibitory())
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron;
        if ((neuronNum % 10) == 5)
          toNeuron = neuronNum - 5 + synapse;
        else
          toNeuron = neuronNum + synapse;
        toNeuron += moduleNum * neuronsInModule;
        neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron], -1.0);
      }
    }
  }
    */
  private void oneActionStopsOneModule(int actionNum, int moduleNum,
      CABot3Net moduleNet) {

    int fromNeuron = (actionNum * neuronsInAction);
    // assert(neurons[fromNeuron].isInhibitory())
    for (int synapse = 0; synapse < 40; synapse++) {
      int toNeuron = synapse;
      toNeuron += moduleNum * neuronsInModule;
      neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron], -1.0);
    }
  }

  // action turns off the module that ignited it.
  public void connectActionToModule(CABot3Net moduleNet) {
    oneActionStopsOneModule(0, 0, moduleNet);// left left
    oneActionStopsOneModule(1, 1, moduleNet);// right right
    oneActionStopsOneModule(2, 2, moduleNet);// forward forward
    oneActionStopsOneModule(3, 3, moduleNet);// back back
    oneActionStopsOneModule(4, 4, moduleNet);// no target error
    oneActionStopsOneModule(5, 5, moduleNet);// turn to center
    oneActionStopsOneModule(6, 6, moduleNet);// stop to stop

    oneActionStopsOneModule(0, 1, moduleNet);// left right
    oneActionStopsOneModule(1, 0, moduleNet);// right left
  }

  public void connectGoal1ToGoal2(CABot3Net goal2Net) {
    for (int i = 0; i < 40; i++) {
      int fromNeuron = 400 + i;
      if (!neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 10; synapse++) {
          int toNeuron = synapse * 40 + i;
          neurons[fromNeuron].addConnection(goal2Net.neurons[toNeuron], 1.0);
        }
      }
    }
  }

  private void connectOneFactToOneGoal2(int factNum, int goal2Num,
      CABot3Net goal2Net) {
    for (int neuronNum = 0; neuronNum < neuronsInFact; neuronNum++) {
      int fromNeuron = neuronNum + (factNum * neuronsInFact);
      if (!neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 5; synapse++) {
          int toNeuron = (synapse * 40) + neuronNum
          + (goal2Num * neuronsInGoal2CA);
          neurons[fromNeuron].addConnection(goal2Net.neurons[toNeuron], 2.0);
        }
      }
    }
  }

  public void connectFactToGoal2(CABot3Net goal2Net) {
    connectOneFactToOneGoal2(11, 0, goal2Net); // left to centerLeft
    connectOneFactToOneGoal2(12, 1, goal2Net); // right to centerRight
  }

  private void connectToOne(CABot3Net toNet, int fromCA, int toCA,
      int neuronsInFromCA, int neuronsInToCA, int numSynapses, double weight) {
    for (int fromNeuron = fromCA * neuronsInFromCA; fromNeuron < (fromCA + 1)
    * neuronsInFromCA; fromNeuron++) {
      if (!neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < numSynapses; synapse++) {
          int toNeuron = ((int) (CANT23.random.nextFloat() * neuronsInToCA))
          + (toCA * neuronsInToCA);
          neurons[fromNeuron].addConnection(toNet.neurons[toNeuron], weight);
        }
      }
    }
  }

  public void connectGoal2ToModule2(CABot3Net module2Net) {
    connectToOne(module2Net, 0, 0, 200, 200, 10, 0.01);
    connectToOne(module2Net, 0, 1, 200, 200, 10, 0.01);
    connectToOne(module2Net, 1, 0, 200, 200, 10, 0.01);
    connectToOne(module2Net, 1, 1, 200, 200, 10, 0.01);
  }

  public void connectValueToExplore(CABot3Net exploreNet) {
    connectToOne(exploreNet, 0, 0, 400, 400, 10, -2.0);// Value -> Explore
  }

  public void connectExploreToModule2(CABot3Net module2Net) {
    connectToOne(module2Net, 0, 0, 400, 200, 10, 0.7);
    connectToOne(module2Net, 0, 1, 400, 200, 10, 0.7);
  }

  public void connectFactToValue(CABot3Net valueNet) {
    // object in center turns value on
    for (int i = 0; i < 40; i++) {
      int fromNeuron = 520 + i;
      for (int synapse = 0; synapse < 20; synapse++) {
        int toNeuron = ((int) (CANT23.random.nextFloat() * valueNet.getSize()));
        if (!neurons[fromNeuron].isInhibitory())
          neurons[fromNeuron].addConnection(valueNet.neurons[toNeuron], 2.5);
      }
    }
  }

  // To turn a module1 on, there should be one goal2, goal1-center
  // and one module2. The module2 chooses between module1s
  private double module2Module1Wt = 0.13;
  private double goal2Module1Wt = 0.13;
  private double goal1Module1Wt = 1.0;

  private void connectOneModule2ToOneModule1(int module2Num, int module1Num,
      CABot3Net module1Net) {
    // connect the first 80 excitatory neurons
    int excitatoryFound = 0;
    for (int neuronNum = 0; neuronNum < neuronsInModule2CA; neuronNum++) {
      int fromNeuron = neuronNum + (module2Num * neuronsInModule2CA);
      if ((!neurons[fromNeuron].isInhibitory()) && (excitatoryFound < 80)) {
        int toNeuron = excitatoryFound % (neuronsInModule / 2); // 0-19
        toNeuron = ((toNeuron / 5) * 10) + (toNeuron % 5); // 0-4,10-14,20-24,30-34
        toNeuron += module1Num * neuronsInModule;
        neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron],
            module2Module1Wt);
        excitatoryFound++;
      }
    }
  }

  public void connectModule2ToModule1(CABot3Net module1Net) {
    connectOneModule2ToOneModule1(0, 0, module1Net);
    connectOneModule2ToOneModule1(1, 1, module1Net);
  }

  private void connectOneGoal2ToOneModule1(int goal2Num, int module1Num,
      CABot3Net module1Net) {
    // connect the first 80 excitatory neurons
    int excitatoryFound = 0;
    for (int neuronNum = 0; neuronNum < neuronsInGoal2CA; neuronNum++) {
      int fromNeuron = neuronNum + (goal2Num * neuronsInGoal2CA);
      if ((!neurons[fromNeuron].isInhibitory()) && (excitatoryFound < 80)) {
        int toNeuron = excitatoryFound % (neuronsInModule / 2); // 0-19
        toNeuron = ((toNeuron / 5) * 10) + (toNeuron % 5); // 0-4,10-14,20-24,30-34
        toNeuron += module1Num * neuronsInModule;
        neurons[fromNeuron].addConnection(module1Net.neurons[toNeuron],
            goal2Module1Wt);
        excitatoryFound++;
      }
    }
  }

  public void connectGoal2ToModule1(CABot3Net module1Net) {
    connectOneGoal2ToOneModule1(0, 0, module1Net);
    connectOneGoal2ToOneModule1(0, 1, module1Net);
    connectOneGoal2ToOneModule1(1, 0, module1Net);
    connectOneGoal2ToOneModule1(1, 1, module1Net);
  }

  public void connectValueToValue2(CABot3Net value2Net) {
    for (int i = 0; i < 400; i++) {
      if (!neurons[i].isInhibitory()) {
        int toNeuron = i % 200;
        neurons[i].addConnection(value2Net.neurons[toNeuron], 1.0);
      }
    }
  }

  public void connectValue2ToGoal2(CABot3Net goal2Net) {
    int lastValue2CA = (getSize() / 200) - 1;
    for (int i = 0; i < 200; i++) {
      int fromNeuron = (lastValue2CA * 200) + i;
      if (neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 10; synapse++) {
          int toNeuron = ((int) (CANT23.random.nextFloat() * 400));
          neurons[fromNeuron].addConnection(goal2Net.neurons[toNeuron], -4.0);
        }
      }
    }
  }

  private void value2StopsOneFact(int value2CA, int factCA, CABot3Net factNet) {
    for (int i = 0; i < 200; i++) {
      int fromNeuron = (value2CA * 200) + i;
      if (neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 3; synapse++) {
          int toNeuron = ((int) (CANT23.random.nextFloat() * neuronsInFact));
          toNeuron += factCA * neuronsInFact;
          neurons[fromNeuron].addConnection(factNet.neurons[toNeuron], -4.0);
        }
      }
    }
  }

  /**
   * @param linesNet
   * @author elb
   * @date 090223 Needed to drive the connection of vision NWs
   */
  public void connectV1LinestoGratings(CABot3Net linesNet) {
    V1v2GratingsConnector connectionFactory = new V1v2GratingsConnector();
    connectionFactory.connectGratings(linesNet, this);
  }

  public void connectGratingsToShapes(int fromNeuron, int toRow, int toCol,
      int shape, CABot3Net ObjRecNet) {
    if ((toRow >= 0) && (toRow < 50) && (toCol >= 0) && (toCol < 50)) {
      int toOffset = shape * 2500;
      int toNeuron = toRow * 50 + toCol + toOffset;
      if ((toRow % 10) < 5)
        toNeuron += 250; // only connect to bottom half of shape
      neurons[fromNeuron].addConnection(ObjRecNet.neurons[toNeuron], 0.3); //.9
    }
  }

  // connect a grate to a particular shape in retinotopic fashion.
  public void connectAGrateToAShape(int grate, int shape, CABot3Net ObjRecNet) {
    int fromOffset = grate * 2500;
    int toOffset = shape * 2500;
    for (int neuron = 0; neuron < 2500; neuron++) {
      int fromNeuron = neuron + fromOffset;
      int toRow = neuron / 50;
      int toCol = neuron % 50; 
      connectGratingsToShapes(fromNeuron, toRow, toCol, shape, ObjRecNet);
      connectGratingsToShapes(fromNeuron, toRow, toCol+1, shape, ObjRecNet);
      connectGratingsToShapes(fromNeuron, toRow, toCol-1, shape, ObjRecNet);
      connectGratingsToShapes(fromNeuron, toRow+1, toCol, shape, ObjRecNet);
      connectGratingsToShapes(fromNeuron, toRow-1, toCol, shape, ObjRecNet);
      /*
       * int fromNeuron = neuron+fromOffset; int toNeuron = neuron+toOffset; int
       * toRow = neuron/50; if ((toRow%10) < 5) toNeuron +=250; //only connect
       * to bottom half of shape
       * neurons[fromNeuron].addConnection(V2Net.neurons[toNeuron],3.0);
       */
    }
  }

  public void connectGratingToObjRec(CABot3Net V2Net) {
    connectAGrateToAShape(0, 0, V2Net); // 3*3 hbar to pyramid
    connectAGrateToAShape(0, 1, V2Net); // 3*3 hbar to stalactite
    connectAGrateToAShape(1, 0, V2Net); // 3*3 vbar to pyramid
    connectAGrateToAShape(1, 1, V2Net); // 3*3 vbar to stalactite
    connectAGrateToAShape(2, 0, V2Net); // 6*6 hbar to pyramid
    connectAGrateToAShape(2, 1, V2Net); // 6*6 hbar to stalactite
    connectAGrateToAShape(3, 0, V2Net); // 6*6 vbar to pyramid
    connectAGrateToAShape(3, 1, V2Net); // 6*6 vbar to stalactite
    connectAGrateToAShape(4, 0, V2Net); // 3*3 sbar to pyramid
    connectAGrateToAShape(4, 1, V2Net); // 3*3 sbar to stalactite
    connectAGrateToAShape(5, 0, V2Net); // 3*3 bbar to pyramid
    connectAGrateToAShape(5, 1, V2Net); // 3*3 bbar to stalactite
 }

  /**
   * @param linesNet
   * @author elb
   * @date 090223 Needed to drive the connection of vision NWs
   */
  public void connectRetinaToV1Lines(CABot3Net linesNet) {
    V1LineConnector connectionFactory = new V1LineConnector();
    connectionFactory.connectHLine("3x3on", this, linesNet);
    connectionFactory.connectVLine("3x3on", this, linesNet);
    connectionFactory.connectHLine("6x6on", this, linesNet);
    connectionFactory.connectVLine("6x6on", this, linesNet);
  }

  public void connectVisInputToV1Slashes(CABot3Net linesNet) {
    V1LineConnector connectionFactory = new V1LineConnector();
    connectionFactory.connectVItoFSlash("3x3on", this, linesNet);
    connectionFactory.connectVItoBSlash("3x3on",this, linesNet);

  }

  public void connectValue2ToFact(CABot3Net factNet) {
    int nLastValue2CA = (getSize() / 200) - 2;
    value2StopsOneFact(nLastValue2CA, 11, factNet);
    value2StopsOneFact(nLastValue2CA, 12, factNet);
    value2StopsOneFact(nLastValue2CA, 13, factNet);
  }

  public void connectValue2ToValue(CABot3Net valueNet) {
    int lastValue2CA = (getSize() / 200) - 1;
    for (int i = 0; i < 200; i++) {
      int fromNeuron = (lastValue2CA * 200) + i;
      if (neurons[fromNeuron].isInhibitory()) {
        for (int synapse = 0; synapse < 10; synapse++) {
          int toNeuron = ((int) (CANT23.random.nextFloat() * 400));
          neurons[fromNeuron].addConnection(valueNet.neurons[toNeuron], -4.0);
        }
      }
    }
  }

  // the last rule (S->VP.) finishes parsing
  public void connectRuleTwoToControl(CABot3Net controlNet) {
    for (int fromNeuron = 0; fromNeuron < 40; fromNeuron++) {// not all 100
      if (!neurons[fromNeuron].isInhibitory()) {
        int toNeuron = (fromNeuron % 100) + 40;
        neurons[fromNeuron].addConnection(controlNet.neurons[toNeuron], 1.9);
        if ((fromNeuron % 5) == 1)
          neurons[fromNeuron].addConnection(controlNet.neurons[toNeuron - 1],
              1.9);
      }
    }
  }

  public void connectControlToBarOne(CABot3Net barOneNet) {
    for (int i = 0; i < 8; i++) {
      int fromNeuron = 40 + (i * 5);
      for (int synapse = 0; synapse < 10 * 5; synapse++) {
        int toGroup = synapse / 10;
        int toOffset = synapse % 10;
        int toNeuron = (toGroup * 40) + toOffset + ((i / 2) * 10);
        neurons[fromNeuron].addConnection(barOneNet.neurons[toNeuron], -2.0);
      }
    }
  }

  // consider this kills two rules not just one.
  private void controlStopsOneRule(int controlCA, int ruleStart, 
      CABot3Net ruleNet) {
    for (int i = 0; i < 40; i++) {
      int fromNeuron = (controlCA*40) + i;
      for (int synapse = 0; synapse < 3; synapse++) {
        int toNeuron = (synapse*40)+i;
        if (toNeuron < 100) {
          toNeuron += ruleStart;
          neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron], -2.0);
        }
      }
    }
  }

  public void connectControlToRuleOne(CABot3Net ruleNet) {
    controlStopsOneRule(1,300, ruleNet); 
    controlStopsOneRule(2,400, ruleNet); //prevents vinst
    controlStopsOneRule(2,500, ruleNet); //prevents head verb
  }

  public void connectControlToRuleTwo(CABot3Net ruleNet) {
    controlStopsOneRule(1,0, ruleNet); // stops S->VP.
    controlStopsOneRule(2,300, ruleNet); // prevents VP->VPNPObj
    controlStopsOneRule(2,600, ruleNet); // prevents VP->VPNPAct
  }

  public void connectControlToOther(CABot3Net otherNet) {
    oneControlStopsAccess(1, 8, otherNet);
    oneControlStopsAccess(3, 8, otherNet);
  }

  private void oneControlStopsAccess(int controlCA, int numAccess,
      CABot3Net accessNet) {
    for (int i = 0; i < 8; i++) {
      int fromNeuron = (controlCA * controlCASize) + (i * 5);
      for (int synapse = 0; synapse < 5; synapse++) {
        // there are 100 neurons per access.
        // Each pair of inhibs suppresses 1 group of 5 with the other ignored.
        int suppressGroups = ((numAccess * 100) / controlCASize) + 1;
        int toNeuron = ((i / 2) * 10) + synapse; // offset in group
        for (int group = 0; group < suppressGroups; group++) {
          neurons[fromNeuron].addConnection(accessNet.neurons[toNeuron], -2.0);
          toNeuron += controlCASize;
        }
      }
    }
  }

  public void connectControlToNounAccess(CABot3Net nounAccessNet) {
    oneControlStopsAccess(1, 1, nounAccessNet);
    oneControlStopsAccess(3, numNouns, nounAccessNet);
  }

  public void connectControlToVerbAccess(CABot3Net verbAccessNet) {
    oneControlStopsAccess(1, 1, verbAccessNet);
    oneControlStopsAccess(3, numVerbs, verbAccessNet);
  }

  private void oneControlStopsSem(int controlCA, int numSem, CABot3Net semNet) {
    for (int i = 0; i < 8; i++) {
      int fromNeuron = (controlCA * controlCASize) + (i * 5);
      for (int synapse = 0; synapse < 10; synapse++) {
        // there are 60 neurons per access. This suppresses in groups of 40;
        int suppressGroups = ((numSem * 60) / controlCASize) + 1;
        int toNeuron = ((i / 2) * 10) + synapse; // offset in group
        for (int group = 0; group < suppressGroups; group++) {
          neurons[fromNeuron].addConnection(semNet.neurons[toNeuron], -2.0);
          toNeuron += controlCASize;
        }
      }
    }
  }

  public void connectControlToNounSem(CABot3Net nounSemNet) {
    oneControlStopsSem(1, numNouns, nounSemNet);
    oneControlStopsSem(3, numNouns, nounSemNet);
  }

  public void connectControlToVerbSem(CABot3Net verbSemNet) {
    oneControlStopsSem(1, numVerbs, verbSemNet);
    oneControlStopsSem(3, numVerbs, verbSemNet);
  }

  private void oneControlStopsInstance(int controlCA, int suppressGroups,
      CABot3Net instanceNet) {
    for (int i = 0; i < 40; i++) {
      int fromNeuron = (controlCA * controlCASize) + i;
      int synapses = (instanceNet.getSize()/40)+1;
      for (int synapse = 0; synapse < synapses; synapse++) {
        int toNeuron = synapse*40+i;
        if (toNeuron < instanceNet.getSize()) {
          neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron],
              -6.0);
        }
      }
    }
  }

  public void connectControlToNounInstance(CABot3Net nounInstanceNet) {
    oneControlStopsInstance(1, (nounInstanceNet.getSize() / 40) + 1,
        nounInstanceNet);
    oneControlStopsInstance(3, (nounInstanceNet.getSize() / 40) + 1,
        nounInstanceNet);
  }

  private void oneControlStartsVerbInstances(int controlCA,
      CABot3Net verbInstanceNet) {
    for (int VICA=0; VICA < instanceCounterSets; VICA ++) {
      for (int neuron = 0; neuron < 40; neuron++) {
        int fromNeuron = neuron + (controlCA * controlCASize);
        if (!neurons[fromNeuron].isInhibitory()) {
          int toNeuron = neuron + (VICA * vInstCASize);
          neurons[fromNeuron].addConnection(verbInstanceNet.neurons[toNeuron],
              1.5); 
          if ((fromNeuron % 5) == 1)
            neurons[fromNeuron].addConnection(
                verbInstanceNet.neurons[toNeuron - 1], 1.5);
        }
      }
    }
  }

  public void connectControlToVerbInstance(CABot3Net verbInstanceNet) {
    oneControlStopsInstance(1, (verbInstanceNet.getSize() / 40) + 1,
        verbInstanceNet);
    oneControlStartsVerbInstances(2, verbInstanceNet);
    oneControlStopsInstance(3, (verbInstanceNet.getSize() / 40) + 1,
        verbInstanceNet);
  }

  public void connectV2ToInstance(CABot3Net instanceNet) {
    for (int fromNeuron = 0; fromNeuron < getSize(); fromNeuron++) {
      // if it is position invariant
      if (fromNeuron % 10 == 1)
        for (int synapseNum = 0; synapseNum < 10; synapseNum++) {
          int toNeuron = ((int) (CANT23.random.nextFloat() * instanceNet
              .getSize()));
          if (neurons[fromNeuron].isInhibitory())
            neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron],
                -0.01);
          else
            neurons[fromNeuron].addConnection(instanceNet.neurons[toNeuron],
                0.01);
        }
    }
  }

  private void connectAFactToARoomFact(int fact,int roomFact,double weight,
      CABot3Net RoomNet)
  {for (int i = 0; i< neuronsInFact; i++) {
    if ((i % 5) != 0) {
      int fromNeuron = (fact*neuronsInFact)+i;
      for (int synapse = 0; synapse < 20; synapse ++) {
        //int toNeuron = (int)(CANT23.random.nextFloat() * 200);
        int toNeuron = ((fromNeuron*10)+ synapse)%200;
        toNeuron += (roomFact*200);
        neurons[fromNeuron].addConnection(RoomNet.neurons[toNeuron],weight);
      }
    }
  }
  }

  public void connectFactToRoomFact(CABot3Net RoomNet) {
    connectAFactToARoomFact(21,0,0.5,RoomNet); //vbar pyr to pyr lines
    connectAFactToARoomFact(22,1,0.5,RoomNet); //hbar pyr to pyr stripes
    connectAFactToARoomFact(23,2,0.5,RoomNet); //hbar stal to stal stripes
    connectAFactToARoomFact(24,3,0.5,RoomNet); //vbar stal to stal lines

    for (int room = 0; room<4; room++) {
      //Find rooms stops Room
      aFactStopsARoom(45,room,RoomNet); //Find rooms w BP stops Room
      aFactStopsARoom(46,room,RoomNet); //Find room w SP 
      aFactStopsARoom(47,room,RoomNet); //Find room w BS 
      aFactStopsARoom(48,room,RoomNet); //Find room w SS 
    }
  }

  private void aFactStopsARoom(int fact,int roomFact,CABot3Net RoomNet) {
    for (int i = 0; i< neuronsInFact; i++) {
      int fromNeuron = (fact*neuronsInFact)+i;
      for (int synapse = 0; synapse < 10; synapse ++) {
        int toNeuron = (((i*5)+ synapse)%200);
        toNeuron += (roomFact*200);
        neurons[fromNeuron].addConnection(RoomNet.neurons[toNeuron],-8.1);
      }
    }
  }

  public void connectFactToRoom2(CABot3Net Room2Net) {
    connectAFactToARoomFact(21,0,1.0,Room2Net); //vbar pyr to pyr lines
    connectAFactToARoomFact(22,1,1.0,Room2Net); //hbar pyr to pyr stripes
    connectAFactToARoomFact(23,2,1.0,Room2Net); //hbar stal to stal stripes
    connectAFactToARoomFact(24,3,1.0,Room2Net); //vbar stal to stal lines
    for (int room = 0; room<4; room++) {
      aFactStopsARoom(20,room,Room2Net); //r1-off turns off all r2s (by 21-24)
      aFactStopsARoom(33,room,Room2Net); //exploreDone turns off all r2s 
      aFactStopsARoom(43,room,Room2Net); //exploreDoneStop turns off all r2s 
      //Find rooms stops Room2
      aFactStopsARoom(45,room,Room2Net); //Find rooms w BP stops Room2
      aFactStopsARoom(46,room,Room2Net); //Find room w SP 
      aFactStopsARoom(47,room,Room2Net); //Find room w BS 
      aFactStopsARoom(48,room,Room2Net); //Find room w SS 
    }
  }

  //the counter stimulates the prior seq (e.g. 1->0, 2->1)
  private void connectACounterToACogSeq(int counter,CABot3Net cogSeqNet) {
    for (int i = 0; i< 40; i++) {
      int fromNeuron = (counter*40)+i;
      for (int synapse = 0; synapse < 5; synapse ++) {
        int toNeuron = ((i*5)+ synapse)+((counter-1)*200);
        neurons[fromNeuron].addConnection(cogSeqNet.neurons[toNeuron],0.6);
      }
    }
  }

  public void connectCounterToCogSeq(CABot3Net cogSeqNet) {
    for (int i = 1; i <5; i++) 
      connectACounterToACogSeq(i,cogSeqNet);
  }

  private void connectAFactToACogSeq(int fact,int counter,
      CABot3Net cogSeqNet) {
    for (int i = 0; i< neuronsInFact; i++) {
      int fromNeuron = (fact*neuronsInFact)+i;
      for (int synapse = 0; synapse < 5; synapse ++) {
        int toNeuron = ((i*5)+ synapse)+(counter*200);
        neurons[fromNeuron].addConnection(cogSeqNet.neurons[toNeuron],0.8);
      }
    }
  }

  private void connectAShapeToACogSeq(int fact,int seq,CABot3Net cogSeqNet) {
    //each room stimulates each seq need next fact and counter to ignite one
    for (int neuron = 0; neuron < 40; neuron++) {
      for (int synapse = 0; synapse < 5; synapse++) {
        int fromNeuron = (fact*40)+neuron;
        int toNeuron = (seq*200)+(neuron*5)+synapse;
        neurons[fromNeuron].addConnection(cogSeqNet.neurons[toNeuron],0.3);
      }
    }
  }

  public void connectFactToCogSeq(CABot3Net cogSeqNet) {
    for (int i = 0; i <4; i++) {
      connectAFactToACogSeq(31,i,cogSeqNet); //cogseq to each cogSeq
      connectAShapeToACogSeq(21,i,cogSeqNet);
      connectAShapeToACogSeq(22,i,cogSeqNet);
      connectAShapeToACogSeq(23,i,cogSeqNet);
      connectAShapeToACogSeq(24,i,cogSeqNet);
    }
  }

  public void connectFactToCounter(CABot3Net counterNet) {
    for (int count = 0; count <5; count++) {
      for (int neuron = 0; neuron <40; neuron ++) {
        int fromNeuron = neuron + 1200; //inc-counter fact 30
        int toNeuron = neuron+(count*40);
        neurons[fromNeuron].addConnection(counterNet.neurons[toNeuron ], 1.25);
      }
    }
  }

  public void connectRoomToFact(CABot3Net factNet) {
    //room on suppresses r1-off
    for (int i = 0; i < getSize(); i++) {
      if (neurons[i].isInhibitory()) {
        int toNeuron = (i%40)+800;
        neurons[i].addConnection(factNet.neurons[toNeuron],-1.0);
        toNeuron = ((i+1)%40)+800;
        neurons[i].addConnection(factNet.neurons[toNeuron],-1.0);
        toNeuron = ((i+2)%40)+800;
        neurons[i].addConnection(factNet.neurons[toNeuron],-1.0);
      }
    }
  }

  public void connectCounterToFact(CABot3Net factNet) {
    connectOneFactToOneModule(4,33,1.0,factNet); //4th count supports end exp
  }

  public void connectGoal1ToRoom1(CABot3Net Room1Net) {
    //goals and facts are pretty much the same so this works.
    connectAFactToARoomFact(17,1,1.0,Room1Net); //striped pyr to HP
    connectAFactToARoomFact(18,0,1.0,Room1Net); //barred pyr to VP
    connectAFactToARoomFact(19,2,1.0,Room1Net); //barred stal to VS
    connectAFactToARoomFact(20,3,1.0,Room1Net); //striped stal to HS
  }

  private void connectARoomToAFact(int room, int fact, CABot3Net factNet) {
    //connect the first 40 excitatory neurons
    int numExcite = 0;
    int neurInRoomCA = 0;
    while (numExcite < 40) {
      int roomNeuron = neurInRoomCA + (room*200);
      if (!neurons[roomNeuron].isInhibitory()) {
        numExcite++;
        int toNeuron = numExcite+(fact*40);
        neurons[roomNeuron].addConnection(factNet.neurons[toNeuron],1.0);
      }
      neurInRoomCA ++;
      if (neurInRoomCA == 200) numExcite = 40;
    }
  }

  public void connectRoom2ToFact(CABot3Net factNet) {
    connectARoomToAFact(0,45,factNet);
    connectARoomToAFact(1,46,factNet);
    connectARoomToAFact(2,47,factNet);
    connectARoomToAFact(3,48,factNet);
  }




  /**** stuff other than connectivity **/
  // reset the word networks for the next word. This might be cheating.
  private void resetInputNet() {
    Enumeration eNum = CANT23.nets.elements();

    while (eNum.hasMoreElements()) {
      CABot3Net net = (CABot3Net) eNum.nextElement();
      if ((net.getName().compareTo("BaseNet") == 0))
        net.clear();
    }
  }

  // ---Functions for reading the bitmap for visual input----------
  // get the pattern using readNewPattern and substitute
  private void modifyPattern(int patternNumber, String fileName) {
    // Patterns aren't actually patterns but a vector of CANTPatterns.
    // We're going to get the CANTPattern at patternNumber.
    CANTPattern newPattern = readNewPattern(fileName);
    // System.out.println(" read New Picture Pattern " + fileName);
    // newPattern.print();

    // and re setElementAt
    patterns.setElementAt(newPattern, patternNumber);
  }

  private CANTPattern readNewPattern(String fileName) {
    CANTPattern readPattern;
    int[] patternPoints = new int[2500];
    int cPoints = 0;
    int number;

    CABot3.bmpReader.readPicture(fileName);
    cPoints = CABot3.bmpReader.getAveragedPictureBits(50, patternPoints);

    // System.out.println(" read New Pattern " + getTotalPatterns());
    readPattern = new CANTPattern(this, "scene", 1, cPoints, patternPoints);
    // readPattern.print();
    return readPattern;
  }

  public void setNextWord(int wordNumber) {
    if (getName().compareTo("BaseNet") != 0)
      return;
    currentWord = wordNumber;
    if (currentWord != -1) {
      resetInputNet();
    } else {
      System.out.println("Last Word Read " + CANT23.CANTStep);
      setNeuronsToStimulate(0);
    }
  }

  private String visualInputFileName = "";

  public void setVisualInputFile(String fileName) {
    visualInputFileName = fileName;
  }

  private boolean inputFromJPG = true;

  public void changePattern(int cantStep) {
    if (getName().compareTo("VisualInputNet") == 0) {
      if (inputFromJPG) {
        CABot3Experiment exp = (CABot3Experiment) CABot3.experiment;
        if (exp.runWithCrystalSpace) {
          if (exp.readNewVisualScene)
            modifyPattern(1, visualInputFileName);
        }
        // else if ((cantStep%50)==0)
        // modifyPattern(1,cantStep/50);
        setCurrentPattern(1);
      } else
        setCurrentPattern(1);
    } else if (getName().compareTo("ControlNet") == 0) {
      if (getCurrentPattern() < 0)
        setCurrentPattern(0);
      else
        setCurrentPattern(getCurrentPattern());
    } else if (getName().compareTo("BaseNet") == 0)
      return;// setInputWord();
    else if (getName().compareTo("BarOneNet") == 0)
      return;// this can be removed when we make newinstance selection neural
    else if (getName().compareTo("VerbInstanceNet") == 0)
      return;// this can be removed when we make newinstance selection neural
    else if (getName().compareTo("NounInstanceNet") == 0)
      return;// this can be removed when we make newinstance selection neural
    else
      setCurrentPattern(0);
    return;
  }

  public void kludge() {
    System.out.println("CABot 2 kludge ");
    CABot3Experiment exp = (CABot3Experiment) CABot3.experiment;
    System.out.println("Commands Emitted " + exp.commandsEmitted);

    Enumeration eNum = CANT23.nets.elements();
    CABot3Net tNet = (CABot3Net) eNum.nextElement();
    CABot3Net t2Net = (CABot3Net) eNum.nextElement();
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CANTNet net = (CANTNet) eNum.nextElement();
      if (net.getName().compareTo("FactNet") == 0)
        tNet = (CABot3Net) net;
    }
    System.out.println("CANT Step " + CANT23.CANTStep );
    for (int i = 0; i < 35; i++) {
    if ((i%10)==5) i += 5;
    tNet.neurons[i].setActivation(5.0);
	     //for (int i = 1760; i < 1770; i++) {
	//	System.out.println(i + "test " + tNet.neurons[i].getActivation() + " "
        //+ tNet.neurons[i].getFatigue());
    }
  }

  public void measure(int currentStep) {

    System.out.println("measure " + neurons[0].getActivation() + " "
        + neurons[0].getFired() + " " + currentStep);
  }

  private void setMutualExclusiveNeurons2(int block_size) {
    for (int neuron = 0; neuron < getSize(); neuron++) {
      int fromGroup = neuron/block_size;
      if (neurons[neuron].isInhibitory()) {
        for (int synapse = 0; synapse < 100; synapse++) {
          int toGroup = ((int)(CANT23.random.nextFloat() * 3)) + 1;
          toGroup = (fromGroup+toGroup)%4;
          int toNeuron = (int)(CANT23.random.nextFloat() * block_size);
          toNeuron += toGroup*block_size;
          addConnection(neuron,toNeuron, 0.5);
        }
      }
      else {
        for (int synapse = 0; synapse < 20; synapse++) {
          int toNeuron = (int)(CANT23.random.nextFloat() * block_size);
          toNeuron += fromGroup*block_size;
          addConnection(neuron,toNeuron, 0.2);
        }
      }
    }
  }

  private void setMutualExclusiveNeurons(int block_size) {
    for (int neuron = 0; neuron < getSize(); neuron++) {
      int fromGroup = neuron/block_size;
      for (int synapse = 0; synapse < 100; synapse++) {
        if (neurons[neuron].isInhibitory()) {
          int toGroup = ((int)(CANT23.random.nextFloat() * 3)) + 1;
          toGroup = (fromGroup+toGroup)%4;
          int toNeuron = (int)(CANT23.random.nextFloat() * block_size);
          toNeuron += toGroup*block_size;
          addConnection(neuron,toNeuron, 0.5);
        }
        else {
          int toNeuron = (int)(CANT23.random.nextFloat() * block_size);
          toNeuron += fromGroup*block_size;
          addConnection(neuron,toNeuron, 0.2);
        }
      }
    }
  }
}