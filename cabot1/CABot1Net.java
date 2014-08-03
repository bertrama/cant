import java.util.*;

public class CABot1Net extends CANTNet {
  private int currentWord = 0;
  
  public CABot1Net(){
  }
  
  public CABot1Net(String name,int cols, int rows,int topology){
    super(name,cols,rows,topology);
    cyclesToStimulatePerRun = 1000;
  }
  
  //-----------Set up topology for networks-------------------
  protected void createNeurons() {
    totalNeurons = 0;
    //stack topology
    if (topology == 6){
      neurons = new CANTNeuron[cols*rows];
      for (int i=0;i< cols*rows;i++) 
	{
        if ((i % 300) > 150)
          neurons[i] = new CANTNeuronFastBind(totalNeurons++,this);
        else
          neurons[i] = new CANTNeuron(totalNeurons++,this);
	}
    }
    //verb topology
    else if (topology == 9){
       neurons = new CANTNeuron[cols*rows];
       for (int i=0;i< cols*rows;i++) 
         {
         if (((i % 480) > 240)  && ((i%3) != 0))
           neurons[i] = new CANTNeuronFastBind(totalNeurons++,this);
         else
           neurons[i] = new CANTNeuron(totalNeurons++,this);
         }
       }
    //instance topology including prep Instances
    else if (topology == 11){
      neurons = new CANTNeuron[cols*rows];
      for (int i=0;i< cols*rows;i++) 
        {
      	int prep = i - prepStart;
        if ((prep > 0)  && ((prep %prepCASize)>240) && ((prep%3) != 0))
          neurons[i] = new CANTNeuronFastBind(totalNeurons++,this);
        else
          neurons[i] = new CANTNeuron(totalNeurons++,this);
        }
      }
    else {
      //System.out.println("Default Create Neurons " + getName());
      super.createNeurons();	
    }
  }
  
  public void readBetweenAllNets() {
    System.out.println("CABot 1 read Between");
    int netsChecked = 0;
    Enumeration eNum = CANT23.nets.elements();
    CABot1Net net = (CABot1Net)eNum.nextElement();

    CABot1Net  inputNet = net;
    CABot1Net  instanceNet = net;
    CABot1Net  verbNet = net;
    CABot1Net  nounNet = net;
    CABot1Net  otherNet = net;
    CABot1Net  stackTopNet = net;
    CABot1Net  stackNet = net;
    CABot1Net  testNet = net;
    CABot1Net  pushNet = net;
    CABot1Net  popNet = net;
    CABot1Net  eraseBoundNet = net;
    CABot1Net  eraseNet = net;
    CABot1Net  ruleNet = net;
    CABot1Net  visInputNet = net;
    CABot1Net  retinaNet = net;
    CABot1Net  V1Net = net;
    CABot1Net  V2Net = net;
    CABot1Net  controlNet = net;
    CABot1Net  factNet = net;
    CABot1Net  moduleNet = net;
    CABot1Net  actionNet = net;
	
    do  {
System.out.println(net.getName());
      if (net.getName().compareTo("BaseNet") == 0)
        inputNet = net;
      else if (net.getName().compareTo("InstanceNet") == 0)
        instanceNet = net;
      else if (net.getName().compareTo("VerbNet") == 0)
        verbNet = net;
      else if (net.getName().compareTo("NounNet") == 0)
        nounNet = net;
      else if (net.getName().compareTo("OWordNet") == 0)
        otherNet = net;
      else if (net.getName().compareTo("StackTopNet") == 0)
        stackTopNet = net;
      else if (net.getName().compareTo("StackNet") == 0) 
        stackNet = net;
      else if (net.getName().compareTo("PushNet") == 0)
        pushNet = net;
      else if (net.getName().compareTo("PopNet") == 0)
        popNet = net;
      else if (net.getName().compareTo("RuleNet") == 0)
        ruleNet = net;
      else if (net.getName().compareTo("EraseNet") == 0)
        eraseNet = net;
      else if (net.getName().compareTo("TestNet") == 0)
        testNet = net;
      else if (net.getName().compareTo("EraseBoundNet") == 0)
	eraseBoundNet = net;
      else if (net.getName().compareTo("VisualInputNet") == 0)
	visInputNet = net;
      else if (net.getName().compareTo("RetinaNet") == 0)
	retinaNet = net;
      else if (net.getName().compareTo("V1Net") == 0)
	V1Net = net;
      else if (net.getName().compareTo("V2Net") == 0)
	V2Net = net;
      else if (net.getName().compareTo("ControlNet") == 0)
	controlNet = net;
      else if (net.getName().compareTo("FactNet") == 0)
	factNet = net;
      else if (net.getName().compareTo("ModuleNet") == 0)
	moduleNet = net;
      else if (net.getName().compareTo("ActionNet") == 0)
	actionNet = net;
      else 	
        System.out.println(net.getName() + " missed net in connect all");
      netsChecked++;
      if (netsChecked < 21) 
        net = (CABot1Net)eNum.nextElement();
    } while (netsChecked < 21);

    //connectparseNets
    inputNet.readConnectTo(verbNet);
    inputNet.readConnectTo(nounNet);
    inputNet.readConnectTo(otherNet);
    inputNet.readConnectTo(pushNet);
    nounNet.readConnectTo(ruleNet);
    nounNet.readConnectTo(instanceNet);
    otherNet.readConnectTo(ruleNet);
    otherNet.readConnectTo(instanceNet);
    verbNet.readConnectTo(ruleNet);
    verbNet.readConnectTo(instanceNet);
    instanceNet.readConnectTo(ruleNet);
    stackTopNet.readConnectTo(pushNet);
    stackTopNet.readConnectTo(stackNet);
    stackTopNet.readConnectTo(eraseBoundNet);
    stackNet.readConnectTo(verbNet);
    stackNet.readConnectTo(otherNet);
    stackNet.readConnectTo(nounNet);
    popNet.readConnectTo(ruleNet);
    popNet.readConnectTo(verbNet);
    popNet.readConnectTo(nounNet);
    popNet.readConnectTo(otherNet);
    popNet.readConnectTo(stackNet);
    popNet.readConnectTo(stackTopNet);
    popNet.readConnectTo(eraseNet);
    pushNet.readConnectTo(verbNet);
    pushNet.readConnectTo(otherNet);
    pushNet.readConnectTo(nounNet);
    pushNet.readConnectTo(stackNet);
    pushNet.readConnectTo(stackTopNet);
    pushNet.readConnectTo(testNet);
    eraseNet.readConnectTo(eraseBoundNet);
    eraseNet.readConnectTo(popNet);
    eraseNet.readConnectTo(stackNet);
    eraseNet.readConnectTo(verbNet);
    eraseNet.readConnectTo(nounNet);
    eraseNet.readConnectTo(otherNet);
    eraseNet.readConnectTo(ruleNet);
    eraseNet.readConnectTo(testNet);
    eraseBoundNet.readConnectTo(stackNet);
    testNet.readConnectTo(pushNet);
    testNet.readConnectTo(stackNet);
    testNet.readConnectTo(verbNet);
    testNet.readConnectTo(nounNet);
    testNet.readConnectTo(otherNet);
    testNet.readConnectTo(instanceNet);
    testNet.readConnectTo(eraseNet);
    ruleNet.readConnectTo(testNet);
    ruleNet.readConnectTo(stackNet);
    ruleNet.readConnectTo(inputNet);
    ruleNet.readConnectTo(stackTopNet);
    ruleNet.readConnectTo(verbNet);
    ruleNet.readConnectTo(instanceNet);
    ruleNet.readConnectTo(popNet);	

    //connect vision nets
    visInputNet.readConnectTo(retinaNet);
    retinaNet.readConnectTo(V1Net);
    V1Net.readConnectTo(V2Net);

    //connect words to facts
    verbNet.readConnectTo(factNet);
    instanceNet.readConnectTo(factNet);

    V2Net.readConnectTo(factNet);

    //connect facts, modules and actions
    factNet.readConnectTo(moduleNet);
    moduleNet.readConnectTo(factNet);
    moduleNet.readConnectTo(actionNet);
    actionNet.readConnectTo(moduleNet);
    
    //manage the control structure
    controlNet.readConnectTo(factNet);
    stackTopNet.readConnectTo(controlNet);
    controlNet.readConnectTo(ruleNet);
    controlNet.readConnectTo(stackNet);
    factNet.readConnectTo(controlNet);
    controlNet.readConnectTo(instanceNet);
    controlNet.readConnectTo(verbNet);
    controlNet.readConnectTo(eraseNet);
    controlNet.readConnectTo(eraseBoundNet);
    eraseNet.readConnectTo(controlNet);
    controlNet.readConnectTo(testNet);

    //Reset any fastbind weights that have been saved.
    CABot1Experiment exp = (CABot1Experiment)CABot1.experiment;
    exp.clearFastBindNeurons();
  }
  
  
  public void runAllOneStep(int CANTStep) {
    //This series of loops is really chaotic, but I needed to
    //get all of the propogation done in each net in step.
    CABot1.runOneStepStart();
	
    Enumeration eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot1Net net = (CABot1Net)eNum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    eNum = CANT23.nets.elements();
      while (eNum.hasMoreElements()) {
        CABot1Net net = (CABot1Net)eNum.nextElement();
        net.setExternalActivation(CANTStep);
      }
    //net.propogateChange();  
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot1Net net = (CABot1Net)eNum.nextElement();
      net.spontaneousActivate();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot1Net net = (CABot1Net)eNum.nextElement();
      net.setNeuronsFired();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot1Net net = (CABot1Net)eNum.nextElement();
      net.setDecay ();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot1Net net = (CABot1Net)eNum.nextElement();
      net.spreadActivation();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot1Net net = (CABot1Net)eNum.nextElement();
      net.setFatigue();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot1Net net = (CABot1Net)eNum.nextElement();
      net.learn();
      if (net.getName().compareTo("StackNet") == 0)
	    net.fastLearn(150,300);
	  else if (net.getName().compareTo("VerbNet") == 0)
	    net.fastLearn(240,480);
	  else if (net.getName().compareTo("InstanceNet") == 0)
	    net.fastLearn(840,960);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot1Net net = (CABot1Net)eNum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CABot1Net net = (CABot1Net)eNum.nextElement();
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
  	CABot1Net net = new CABot1Net (name,cols,rows,topology);
	return (net);
	} 

  private void setConnectionsInCA(int neuronNum,int synapses,int CASize, double weight) {
    int CA = neuronNum/CASize;
	
	for (int synapse = 0; synapse < synapses; synapse++) 
	  {
	  int toNeuron= CASize*CA+(int)(Math.random()*CASize);
	  int curConnections=neurons[neuronNum].getCurrentSynapses();
	  addConnection(neuronNum,toNeuron,weight);
	  if (synapse==neurons[neuronNum].getCurrentSynapses())
	    synapse--;
	  }
  }
	
  private void setInputTopology(int CASize,double exciteWeight) {
  	setConnections(0,size());
    for (int neuronNum=0; neuronNum<size(); neuronNum++) 
	  {
	  for (int synapseNum=0;synapseNum < neurons[neuronNum].getCurrentSynapses(); synapseNum++) 
	    {
		if ((neuronNum/CASize) == 
		(neurons[neuronNum].synapses[synapseNum].toNeuron.id /CASize)) 
		  {
		  if (neurons[neuronNum].getInhibitory()) 
		    neurons[neuronNum].synapses[synapseNum].setWeight(-0.1);
		  else 
		    neurons[neuronNum].synapses[synapseNum].setWeight(exciteWeight);
		  }
		else
		  {
		  if (neurons[neuronNum].getInhibitory()) 
		    neurons[neuronNum].synapses[synapseNum].setWeight(-0.9);
		  else 
		    neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
		  } 
	    }
	  }
  }
  
  int prepStart = 1800;
  private void setInstanceTopology(int CASize,double exciteWeight) {
    //First several are noun instances
    setConnections(0,prepStart);  
    for (int neuronNum=0; neuronNum<prepStart; neuronNum++) 
      {
      for (int synapseNum=0;synapseNum < neurons[neuronNum].getCurrentSynapses(); synapseNum++) 
        {
        if ((neuronNum/CASize) == 
  	  (neurons[neuronNum].synapses[synapseNum].toNeuron.id /CASize)) 
          {
  	  if (neurons[neuronNum].getInhibitory()) 
  	    neurons[neuronNum].synapses[synapseNum].setWeight(-0.1);
  	  else 
  	    neurons[neuronNum].synapses[synapseNum].setWeight(exciteWeight);
  	  }
  	else
  	  {
  	  if (neurons[neuronNum].getInhibitory()) 
  	    neurons[neuronNum].synapses[synapseNum].setWeight(-0.9);
  	  else 
  	    neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
  	  } 
        }
      }
	  
      //set up prep instance topology
      setPrepTopology(prepStart);
  }
  
  //each verb has a core of normal neurons with some fastbind neurons.
  //The first 240 are normal.
  //The next 60 are fastbind, but are part of the core CA.  This enables
  //binding to active slots.
  //There are 3 slots of 60 each, one for actor, one for object and one for
  //location.  These are all fastbind.
  private void setVerbTopology() {
  	int CASize = 300; 
	int fullCASize = 480;  //this is the CA plus the actor and object slots
	int fromNeuron;
	int toNeuron;
	
	//set inhibitory neurons non-randomly
    for (int neuronNum=0; neuronNum<size(); neuronNum++) 
	  {
	  if ((neuronNum%5) == 0) neurons[neuronNum].setInhibitory(true);
	  else neurons[neuronNum].setInhibitory(false);
	  }
	

    for (int verb = 0; verb < 4; verb++) 
      {
      //set up internal CA connections
	  for (int neuron=0; neuron<240;neuron++) 
	    {
	    fromNeuron=(verb*480)+neuron;
	    if (!neurons[fromNeuron].getInhibitory()) 
	      for (int synapse = 0 ; synapse <20;synapse++) 
	        {
	        toNeuron = ((neuron*5)+synapse)%300;
		    toNeuron += (verb*480);
		    addConnection(fromNeuron,toNeuron,0.35);
		    }
	    }
		
	   //add connections from verbs to their slots
	   for (int neuronNum  = 0; neuronNum < 240; neuronNum++)
	     {
	     for (int synapse = 0 ; synapse < 5 ; synapse++)
	       {
	       fromNeuron = (verb*480) + neuronNum;
	       toNeuron =  (neuronNum+synapse)%180;
	       toNeuron+=300+(verb*480);
	       addConnection(fromNeuron,toNeuron,0.17);
	       }
	     }

	   //set up internal connections from body fast bind
	   for (int neuron=240; neuron<300;neuron++) 
	     {
	     fromNeuron=(verb*480)+neuron;
	     if (!neurons[fromNeuron].getInhibitory()) 
	       for (int synapse = 0 ; synapse <20;synapse++) 
	         {
	         toNeuron = ((neuron*5)+synapse)%180;
	         toNeuron += (verb*480)+300;
	         addConnection(fromNeuron,toNeuron,0.01);
	         }
	     }
		
	   //setup connections within slots
	   for (int slot = 0; slot < 3; slot++) 
	     {
	     for (int neuron=0; neuron<60;neuron++) 
	       {
	       fromNeuron=(verb*480)+neuron+(slot*60)+300;
	       if (!neurons[fromNeuron].getInhibitory()) 
	         for (int synapse = 0 ; synapse <20;synapse++) 
	           {
	           toNeuron = (neuron+synapse)%60;
	           toNeuron += (verb*480)+300+(slot*60);
	           addConnection(fromNeuron,toNeuron,1.0);
	           }
	       }
	     }
      }
  }

  private void setNounTopology(int CASize) {
  	setConnections(0,size());
    for (int neuronNum=0; neuronNum<size(); neuronNum++) 
    {
      for (int synapseNum=0;synapseNum < neurons[neuronNum].getCurrentSynapses(); synapseNum++) 
      {
  	  if ((neuronNum/CASize) == 
  	      (neurons[neuronNum].synapses[synapseNum].toNeuron.id /CASize)) 
  	    {
  	    if (neurons[neuronNum].getInhibitory()) 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
  	    else 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(0.35-(Math.random()/4));
  	    }
  	  else
  	    {
  	    if (neurons[neuronNum].getInhibitory()) 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(-0.9);
  	    else 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
  	    } 
      }
    }
  }

  private int prepCASize=360;
  private void setPrepTopology(int prepStart) {
    int fromNeuron;
    int toNeuron;

    for (int prep = 0; prep < 3; prep++) 
      {
      //240 normal neurons	
      for (int neuron =0; neuron < 240; neuron++) 
        {
        fromNeuron = prepStart + (prep*prepCASize) + neuron;
        for (int synapse = 0 ; synapse < 10; synapse++) 
          {
          if (!neurons[fromNeuron].isInhibitory()) 
     	    {
       	    toNeuron = ((neuron*2)+ synapse) % 300;
       	    toNeuron += prepStart + (prep*prepCASize); 
       	    addConnection(fromNeuron,toNeuron,1.0);
       	    }
          }
        }
      //60 interleaved fastbind and normal to bind the slot
      for (int neuron =0; neuron < 60; neuron++) 
        {
        fromNeuron = prepStart + (prep*prepCASize) + neuron + 240;;
        for (int synapse = 0 ; synapse < 25; synapse++) 
          {
          toNeuron = ((neuron*2)+ synapse) % 120;
       	  toNeuron += prepStart+(prep*prepCASize)+240;
       	  addConnection(fromNeuron,toNeuron,0.25);
          toNeuron = (((neuron*2)+ synapse) % 240) + prepStart + 
            (prep*prepCASize);
          addConnection(fromNeuron,toNeuron,0.25);
          }
        }
      //60 interleaved fastbind and normal for the slot
      for (int neuron =0; neuron < 60; neuron++) 
        {
        fromNeuron = prepStart + (prep*prepCASize) + neuron + 300;;
        for (int synapse = 0 ; synapse < 20; synapse++) 
          {
          toNeuron = ((neuron*2)+ synapse) % 60;
      	  toNeuron += prepStart + (prep*prepCASize) + 300 ;
       	  addConnection(fromNeuron,toNeuron,1.0);
          }
        }
      //allow the slot to be bound to the noun instances
      for (int neuron =0; neuron < 60; neuron++) 
        {
        fromNeuron = prepStart + (prep*prepCASize) + neuron + 300;;
        for (int synapse = 0 ; synapse < 80; synapse++) 
          {
          toNeuron = (int)(Math.random() *prepStart);
       	  addConnection(fromNeuron,toNeuron,0.01);
          }
        }
      }
  }

  private void setOtherWordTopology(int CASize) {
  	setConnections(0,getSize());
    for (int neuronNum=0; neuronNum<getSize(); neuronNum++) 
    {
      for (int synapseNum=0;synapseNum < neurons[neuronNum].getCurrentSynapses(); synapseNum++) 
      {
  	  if ((neuronNum/CASize) == 
  	      (neurons[neuronNum].synapses[synapseNum].toNeuron.id /CASize)) 
  	    {
  	    if (neurons[neuronNum].getInhibitory()) 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
  	    else 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(0.35-(Math.random()/4));
  	    }
  	  else
  	    {
  	    if (neurons[neuronNum].getInhibitory()) 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(-0.9);
  	    else 
  	      neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
  	    } 
      }
    }
  }

  private void setParseRuleTopology(int CASize) {
  	setConnections(0,size());
	//add extra inhibitory connections
	for (int neuronNum=0; neuronNum<size(); neuronNum++) 
	  {
	  if (neurons[neuronNum].isInhibitory())
	    {
		for (int synapse = 0; synapse < 50;synapse++) 
		  {
		  int toNeuron = (int)(Math.random()* getSize());
		  addConnection(neuronNum,toNeuron,-0.02);
		  }
	    }
	  }
	
    for (int neuronNum=0; neuronNum<size(); neuronNum++) 
      {
      for (int synapseNum=0;synapseNum < neurons[neuronNum].getCurrentSynapses(); synapseNum++) 
        {
  	    if ((neuronNum/CASize) == 
  	      (neurons[neuronNum].synapses[synapseNum].toNeuron.id /CASize)) 
  	      {
  	      if (neurons[neuronNum].getInhibitory()) 
  	        neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
  	      else 
  	        neurons[neuronNum].synapses[synapseNum].setWeight(0.45-(Math.random()/4));
  	      }
  	    else
  	      {
  	      if (neurons[neuronNum].getInhibitory()) 
  	        neurons[neuronNum].synapses[synapseNum].setWeight(-1.2);
  	      else 
  	        neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
  	      } 
        }
      }
  }


  private void setPushTopology() {
     setSequenceTopology(0.27);
  }

  private void setStackTopTopology(int CASize) {
  	//setup connections within core stacktop CAs
    int CAs = getSize()/CASize;
	
	for (int curCA = 0; curCA < CAs; curCA++) 
	  {
	  int fromNeuron = 0;
	  int toNeuron = 0;

	  //connections from the body
	  for (int neuron = 0 ; neuron < 220 ; neuron++)
	    {
	    fromNeuron = neuron+40+(curCA*CASize);
	    if (!neurons[fromNeuron].getInhibitory()) 
		  {
	      for (int synapse = 0; synapse < 15; synapse++)
		    {
		    toNeuron = ((neuron+synapse)%220)+40+(curCA*CASize);
		    addConnection(fromNeuron,toNeuron,1.0);
		    }
		  //body primes next
		  toNeuron=260+(curCA*CASize)+(fromNeuron%40);
		  if (!neurons[toNeuron].isInhibitory())
            addConnection(fromNeuron,toNeuron,0.2);
		  //body primes prior
		  toNeuron=(curCA*CASize)+(fromNeuron%40);
		  if (!neurons[toNeuron].isInhibitory())
		    addConnection(fromNeuron,toNeuron,0.2);
		  addConnection(fromNeuron,toNeuron,0.2);
		  }
	    else 
		  {
		  //body surpresses prior next
		  if (curCA > 0)
		    {
		    for (int synapse = 0; synapse < 20; synapse++)
		      {
		      toNeuron = ((neuron+synapse)%40)+260+((curCA-1)*CASize);
		      addConnection(fromNeuron,toNeuron,0.5);
		      }
		    }
		  //body surpresses next prior
		  if (curCA < (CAs -1))
		    {
		    for (int synapse = 0; synapse < 20; synapse++)
		      {
		      toNeuron = ((neuron+synapse)%40)+((curCA+1)*CASize);
		      addConnection(fromNeuron,toNeuron,0.5);
		      }
		    }
		  }
	    }
		
	  //connections from next
	  for (int neuron = 0 ; neuron < 40 ; neuron++)
	    {
	    fromNeuron = neuron+260+(curCA*CASize);
	    if ((neuron%5) == 0) neurons[fromNeuron].setInhibitory(true);
		else neurons[fromNeuron].setInhibitory(false);
	    if (!neurons[fromNeuron].getInhibitory()) 
	      {
	      for (int synapse = 0; synapse < 15; synapse++)
	        {
	        toNeuron = ((neuron+synapse)%40)+260+(curCA*CASize);
	        addConnection(fromNeuron,toNeuron,1.5);
			
			//next activates next body, 
			if (curCA < (CAs-1)) 
			  {
	          toNeuron = (((neuron*6)+synapse)%220)+40+((curCA+1)*CASize);
			  addConnection(fromNeuron,toNeuron,0.5);
			  }
	        }
	      }
		else 
	      {
	      //next supresses own body
	      for (int synapse = 0; synapse < 40; synapse++)
	        {
	        toNeuron = (((neuron*6)+synapse)%220)+40+(curCA*CASize);
	        addConnection(fromNeuron,toNeuron,4.0);
	        }
	      //next supresses next next
		  if (curCA < (CAs-1))
		    {
	        for (int synapse = 0; synapse < 20; synapse++)
	          {
	          toNeuron = ((neuron+synapse)%40)+260+((curCA+1)*CASize);
	          addConnection(fromNeuron,toNeuron,2.0);
	          }
		    }
	      }
	    }
		
	  //connections from prior
	  for (int neuron = 0 ; neuron < 40 ; neuron++)
	    {
	    fromNeuron = neuron+(curCA*CASize);
	    if ((neuron%5) == 0) neurons[fromNeuron].setInhibitory(true);
	    else neurons[fromNeuron].setInhibitory(false);
	    if (!neurons[fromNeuron].getInhibitory()) 
	      {
	      for (int synapse = 0; synapse < 15; synapse++)
	        {
	        toNeuron = ((neuron+synapse)%40)+(curCA*CASize);
			//prior activates self
	        addConnection(fromNeuron,toNeuron,1.5);
	  	
	  	    //prior activates prior body, 
	  	    if (curCA >0) 
	  	      {
	          toNeuron = (((neuron*6)+synapse)%220)+40+((curCA-1)*CASize);
	  	      addConnection(fromNeuron,toNeuron,0.5);
	  	      }
	        }
	      }
	      else //inhibitory
	      {
	      //prior supresses own body
	      for (int synapse = 0; synapse < 40; synapse++)
	        {
	        toNeuron = (((neuron*6)+synapse)%220)+40+(curCA*CASize);
	        addConnection(fromNeuron,toNeuron,4.0);
	        }
	      //prior supress prior prior
	      if (curCA > 0)
	        {
	          for (int synapse = 0; synapse < 20; synapse++)
	            {
	            toNeuron = ((neuron+synapse)%40) + ((curCA-1)*CASize);
	            addConnection(fromNeuron,toNeuron,2.0);
	            }
	        }
	      }
        }//end prior loop
	  }//for CA
  }
  
  //A stacktop element has two rows (40 neurons) to activate the prior element
  //and a row for the next element.  These are not part of the CA, but take
  //activation from it.  
  //Stack elements have some fast binding synapses to verb, noun and other. 
  private void setStackTopology(int CASize) {
    //set up intial connections
	for (int i=0;i< cols*rows;i++) 
	  if ((i % 300) > 150)
        setConnectionsInCA(i,20,300,0.1); 
	  else	
	    setConnectionsRandomly(i,100,0.1); 

	for (int neuronNum=0; neuronNum<size();neuronNum++)
	  for (int synapseNum=0;synapseNum < neurons[neuronNum].getCurrentSynapses(); synapseNum++) 
	    {
		int toNeuron = neurons[neuronNum].synapses[synapseNum].toNeuron.getId();
		if ((neuronNum / 300) == (toNeuron / 300)) 
		  { 
	      if (neurons[neuronNum].isInhibitory)
	        neurons[neuronNum].synapses[synapseNum].setWeight(-0.01);
	      else    
		    {
		  	if  ((neurons[neuronNum].id % 300) > 150)
	          neurons[neuronNum].synapses[synapseNum].setWeight(1.0);
			else  
		      neurons[neuronNum].synapses[synapseNum].setWeight(1.0);
		    }
		  }
		else	
	      { 
	      if (neurons[neuronNum].isInhibitory)
	        neurons[neuronNum].synapses[synapseNum].setWeight(-1.0);
	      else   
	        neurons[neuronNum].synapses[synapseNum].setWeight(0.01);
	      }
	    }  
    }

  private void setSequenceTopology(double nextWeight)
    {
	int CASize = 200;
    int totalCAs = getSize()/CASize;
	
	//set up inhibitory
	for (int i = 0; i < getSize(); i++)
	{
	if (((i%5) == 1) || ((i%5) == 3)) neurons[i].setInhibitory(true);
	else neurons[i].setInhibitory(false);
	}

	for (int CA = 0; CA < totalCAs; CA++) 
	  {
	  //add connections and weights	  
	  for (int neuronNum = 0; neuronNum < CASize; neuronNum++)
	    {
		int fromNeuron = neuronNum+(CA*CASize);
	    if (neurons[fromNeuron].isInhibitory())
	      {
	      //inhibit prior
	      if (CA > (0)) 
	        for (int synapse = 0; synapse < 15; synapse++) 
	          {
	          int toNeuron=((neuronNum+synapse)%CASize)+((CA-1)*CASize);
	          addConnection(fromNeuron,toNeuron,2.0);
	          }
	      }
	    else //excitatory
	      {
		  //set self connections
		  for (int synapse = 1; synapse < 15; synapse++) 
		    {
			int toNeuron;
			if ((synapse%3)==0)
			  toNeuron=((neuronNum+(synapse*20))%CASize)+(CA*CASize);
			else toNeuron=((neuronNum+synapse)%CASize)+(CA*CASize);
		    addConnection(fromNeuron,toNeuron,1.5);
		    }
	      //set next connections
	      if (CA < (totalCAs-1)) 
	        for (int synapse = 0; synapse < 5; synapse++) 
		      {
		      int toNeuron=((neuronNum+synapse)%CASize)+((CA+1)*CASize);
		      addConnection(fromNeuron,toNeuron,nextWeight);
		      }
	      }//excite
	    }//neuron
	  }//CA
    }

 
  private void setEraseTopology() {
    //set up connections
    setSequenceTopology(0.27);
  }

  //This is a sequential net to test the rules.
  //The top CA should ignite and then move through the others.
  private void setTestTopology() {
    //set up connections
    setSequenceTopology(0.4);
  }

  //Two CAs each to suppress a stack element
  private void setEraseBoundTopology() {
    for (int neuron=0; neuron<getSize(); neuron++)
      {
      int CA = neuron/100;
      if (!neurons[neuron].isInhibitory())
        for (int synapse = 0; synapse < 20; synapse++) {
          int toNeuron = (int)(Math.random()*100) + (CA*100);
      	  addConnection(neuron,toNeuron,1.0);
	}
      }
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

  private void addOneRetinaConnection(int fromRow, int fromCol, 
    CANTNeuron toNeuron, double weight) {
    //if ((fromRow < 0) ||(fromRow >= getRows()) ||(fromCol < 0) || (fromCol >= getCols()))
    if ((fromRow < 0) || (fromCol < 0) || (fromCol >= getCols()) || 
         fromRow >= getRows()*6)
      return;
     								
    int inputNeuron = fromRow * getCols() + fromCol;
  
    neurons[inputNeuron].addConnection(toNeuron,weight);
  }

  private void addOneV1Connection(int fromRow, int fromCol, CANTNeuron toNeuron,
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

  private void connectInputTo3x3(CABot1Net retinaNet, int start,
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


  private void connectInputTo6x6(CABot1Net retinaNet, int start,
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


  private void connectInputTo9x9(CABot1Net retinaNet, int start,
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
	
  public void connectInputToRetina(CABot1Net retinaNet) {
    connectInputTo3x3(retinaNet,0,7.1,-1.25);	
    connectInputTo3x3(retinaNet,size(),-7.1,0.89);	
    connectInputTo6x6(retinaNet,size()*2,1.8,-0.22);	
    connectInputTo6x6(retinaNet,size()*3,-1.8,0.22);	
    connectInputTo9x9(retinaNet,size()*4,0.79,-0.021);	
    connectInputTo9x9(retinaNet,size()*5,-0.79,0.099);	
  }
  
  
  //input was translated to retina 3x3 node to node
  private void connectRetinaToHorizontal(CABot1Net V1Net, 
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
    addOneRetinaConnection(row,column-1,V1Net.neurons[inputNeuron],onOff33Val);
    addOneRetinaConnection(row,column,V1Net.neurons[inputNeuron],onOff33Val);
    addOneRetinaConnection(row,column+1,V1Net.neurons[inputNeuron],onOff33Val);
    }
  }

  private void connectRetinaToSlash(CABot1Net V1Net, double onOff33Val) 
  {
    int row;
    int column;
    int slashOffset=getInputSize();
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getRows();
      column = inputNeuron%getRows();
      addOneRetinaConnection(row-1,column+1,V1Net.neurons[inputNeuron+slashOffset],onOff33Val);
      addOneRetinaConnection(row,column,V1Net.neurons[inputNeuron+slashOffset],onOff33Val);
      addOneRetinaConnection(row+1,column,V1Net.neurons[inputNeuron+slashOffset],onOff33Val);
      }
  }

  private void connectRetinaToBackSlash(CABot1Net V1Net, double onOff33Val) 
  {
    int row;
    int column; 
    int backOffset=getInputSize()*2;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getRows();
      column = inputNeuron%getRows();
      addOneRetinaConnection(row-1,column-1,V1Net.neurons[inputNeuron+backOffset],onOff33Val);
      addOneRetinaConnection(row,column,V1Net.neurons[inputNeuron+backOffset],onOff33Val);
      addOneRetinaConnection(row+1,column,V1Net.neurons[inputNeuron+backOffset],onOff33Val);
      }
  }

  private double threeNFBigVal = 1.4;
  private double threeNFSmallVal = 0.5;
  private double threeFNVal = 0.3;
  private double sixNFVal= 0.3;
  private double sixFNVal= 0.3;
  private double nineNFVal=0.2;
  private double nineFNVal=0.3;
  private void connectRetinaToAndAngle(CABot1Net V1Net) 
  {
    int row;
    int column; 
    int andOffset=getInputSize()*3;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getCols();
      column = inputNeuron%getCols();

      //xy,1y
      addOneRetinaConnection(row,column,
        V1Net.neurons[inputNeuron+andOffset], threeNFBigVal);
      addOneRetinaConnection(row+1,column,
        V1Net.neurons[inputNeuron+andOffset], threeNFSmallVal);

      //x-1,x1,1-1,11
      addOneRetinaConnection(row+getCols(),column-1,
        V1Net.neurons[inputNeuron+andOffset],threeFNVal);
      addOneRetinaConnection(row+getCols(),column+1,
        V1Net.neurons[inputNeuron+andOffset],threeFNVal);
      addOneRetinaConnection(row+getCols()+1,column-1,
        V1Net.neurons[inputNeuron+andOffset],threeFNVal);
      addOneRetinaConnection(row+getCols()+1,column+1,
        V1Net.neurons[inputNeuron+andOffset],threeFNVal);

      //xy,1-1,1y,11
      addOneRetinaConnection(row+(getCols()*2),column,
        V1Net.neurons[inputNeuron+andOffset],sixNFVal);
      addOneRetinaConnection(row+(getCols()*2)+1,column-1,
        V1Net.neurons[inputNeuron+andOffset],sixNFVal);
      addOneRetinaConnection(row+(getCols()*2)+1,column,
        V1Net.neurons[inputNeuron+andOffset],sixNFVal);
      addOneRetinaConnection(row+(getCols()*2)+1,column+1,
        V1Net.neurons[inputNeuron+andOffset],sixNFVal);

      //2-2,2-1,21,22
      addOneRetinaConnection(row+(getCols()*3)+2,column-2,
        V1Net.neurons[inputNeuron+andOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)+2,column-1,
        V1Net.neurons[inputNeuron+andOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)+2,column+1,
        V1Net.neurons[inputNeuron+andOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)+2,column+2,
        V1Net.neurons[inputNeuron+andOffset],sixFNVal);

      //1y,2y,3-1,3y,31
      addOneRetinaConnection(row+(getCols()*4)+1,column,
        V1Net.neurons[inputNeuron+andOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)+2,column,
        V1Net.neurons[inputNeuron+andOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)+3,column-1,
        V1Net.neurons[inputNeuron+andOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)+3,column,
        V1Net.neurons[inputNeuron+andOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)+3,column+1,
        V1Net.neurons[inputNeuron+andOffset],nineNFVal);

      //x-2,x2,1-3,13
      addOneRetinaConnection(row+(getCols()*5),column-2,
        V1Net.neurons[inputNeuron+andOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5),column+2,
        V1Net.neurons[inputNeuron+andOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5)+1,column-3,
        V1Net.neurons[inputNeuron+andOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5)+1,column+3,
        V1Net.neurons[inputNeuron+andOffset],nineFNVal);
      }
  }

  private void connectRetinaToLessThanAngle(CABot1Net V1Net) 
  {
    int row;
    int column; 
    int lessThanOffset=getInputSize()*4;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getCols();
      column = inputNeuron%getCols();
      //xy,x1
      addOneRetinaConnection(row,column,
        V1Net.neurons[inputNeuron+lessThanOffset],threeNFBigVal);
      addOneRetinaConnection(row,column+1,
        V1Net.neurons[inputNeuron+lessThanOffset],threeNFSmallVal);

      //-1y,-11,1y,11
      addOneRetinaConnection(row+getCols()-1,column,
        V1Net.neurons[inputNeuron+lessThanOffset], threeFNVal);
      addOneRetinaConnection(row+getCols()-1,column+1,
        V1Net.neurons[inputNeuron+lessThanOffset], threeFNVal);
      addOneRetinaConnection(row+getCols()+1,column,
        V1Net.neurons[inputNeuron+lessThanOffset], threeFNVal);
      addOneRetinaConnection(row+getCols()+1,column+1,
        V1Net.neurons[inputNeuron+lessThanOffset], threeFNVal);

      //-1y,xy,1y,11
      addOneRetinaConnection(row+(getCols()*2)-1,column,
        V1Net.neurons[inputNeuron+lessThanOffset], sixNFVal);
      addOneRetinaConnection(row+(getCols()*2),column,
        V1Net.neurons[inputNeuron+lessThanOffset], sixNFVal);
      addOneRetinaConnection(row+(getCols()*2)+1,column,
        V1Net.neurons[inputNeuron+lessThanOffset], sixNFVal);
      addOneRetinaConnection(row+(getCols()*2)+1,column+1,
        V1Net.neurons[inputNeuron+lessThanOffset], sixNFVal);

      //-22,-12,12,22
      addOneRetinaConnection(row+(getCols()*3)-2,column+2,
        V1Net.neurons[inputNeuron+lessThanOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)-1,column+2,
        V1Net.neurons[inputNeuron+lessThanOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)+1,column+2,
        V1Net.neurons[inputNeuron+lessThanOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)+2,column+2,
        V1Net.neurons[inputNeuron+lessThanOffset],sixFNVal);

      //-13,x1,x2,x3,13
      addOneRetinaConnection(row+(getCols()*4)-1,column+3,
      V1Net.neurons[inputNeuron+lessThanOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4),column+1,
        V1Net.neurons[inputNeuron+lessThanOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4),column+2,
        V1Net.neurons[inputNeuron+lessThanOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4),column+3,
        V1Net.neurons[inputNeuron+lessThanOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)+1,column+3,
        V1Net.neurons[inputNeuron+lessThanOffset],nineNFVal);

      //-31,-2y,2y,31
      addOneRetinaConnection(row+(getCols()*5)-3,column+1,
        V1Net.neurons[inputNeuron+lessThanOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5)-2,column,
        V1Net.neurons[inputNeuron+lessThanOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5)+2,column,
        V1Net.neurons[inputNeuron+lessThanOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5)+3,column+1,
        V1Net.neurons[inputNeuron+lessThanOffset],nineFNVal);
      }
  }

  private void connectRetinaToGreaterThanAngle(CABot1Net V1Net) 
  {
    int row;
    int column; 
    int greaterThanOffset=getInputSize()*5;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getCols();
      column = inputNeuron%getCols();
      //x-1,xy
      addOneRetinaConnection(row,column-1,
        V1Net.neurons[inputNeuron+greaterThanOffset],threeNFSmallVal);
      addOneRetinaConnection(row,column,
        V1Net.neurons[inputNeuron+greaterThanOffset],threeNFBigVal);

      //-1-1,-1y,1-1,1y
      addOneRetinaConnection(row+getCols()-1,column-1,
        V1Net.neurons[inputNeuron+greaterThanOffset], threeFNVal);
      addOneRetinaConnection(row+getCols()-1,column,
        V1Net.neurons[inputNeuron+greaterThanOffset], threeFNVal);
      addOneRetinaConnection(row+getCols()+1,column,
        V1Net.neurons[inputNeuron+greaterThanOffset], threeFNVal);
      addOneRetinaConnection(row+getCols()+1,column-1,
        V1Net.neurons[inputNeuron+greaterThanOffset], threeFNVal);

      //-1-1,x-1,xy,1-1,
      addOneRetinaConnection(row+(getCols()*2)-1,column-1,
        V1Net.neurons[inputNeuron+greaterThanOffset], sixNFVal);
      addOneRetinaConnection(row+(getCols()*2),column-1,
        V1Net.neurons[inputNeuron+greaterThanOffset], sixNFVal);
      addOneRetinaConnection(row+(getCols()*2),column,
        V1Net.neurons[inputNeuron+greaterThanOffset], sixNFVal);
      addOneRetinaConnection(row+(getCols()*2)+1,column-1,
        V1Net.neurons[inputNeuron+greaterThanOffset], sixNFVal);

      //-2-2,-1-2,1-2,-2-2
      addOneRetinaConnection(row+(getCols()*3)-2,column-2,
        V1Net.neurons[inputNeuron+greaterThanOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)-1,column-2,
        V1Net.neurons[inputNeuron+greaterThanOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)+1,column-2,
        V1Net.neurons[inputNeuron+greaterThanOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)+2,column-2,
        V1Net.neurons[inputNeuron+greaterThanOffset],sixFNVal);

      //-3-1,-3x,-31,-2x,-1x,
      addOneRetinaConnection(row+(getCols()*4)-3,column-1,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)-3,column,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)-3,column+1,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)-2,column,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)-1,column,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineNFVal);
      

      //-3-1,-2y,2y, 31
      addOneRetinaConnection(row+(getCols()*5)-3,column-1,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5)-2,column,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5)+2,column,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5)+3,column+1,
        V1Net.neurons[inputNeuron+greaterThanOffset],nineFNVal);
     }
  }

  private void connectRetinaToOrAngle(CABot1Net V1Net) 
  {
    int row;
    int column; 
    int orOffset=getInputSize()*6;
    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getCols();
      column = inputNeuron%getCols();

      //-1y,xy
      addOneRetinaConnection(row-1,column,
        V1Net.neurons[inputNeuron+orOffset],threeNFSmallVal);
      addOneRetinaConnection(row,column,
        V1Net.neurons[inputNeuron+orOffset],threeNFBigVal);

      //-1-1,-11,x-1,x1
      addOneRetinaConnection(row+getCols()-1,column-1,
        V1Net.neurons[inputNeuron+orOffset], threeFNVal);
      addOneRetinaConnection(row+getCols()-1,column+1,
        V1Net.neurons[inputNeuron+orOffset], threeFNVal);
      addOneRetinaConnection(row+getCols(),column-1,
        V1Net.neurons[inputNeuron+orOffset], threeFNVal);
      addOneRetinaConnection(row+getCols(),column+1,
        V1Net.neurons[inputNeuron+orOffset], threeFNVal);

      //-1-1,-1y,-11,xy
      addOneRetinaConnection(row+(getCols()*2)-1,column-1,
        V1Net.neurons[inputNeuron+orOffset], sixNFVal);
      addOneRetinaConnection(row+(getCols()*2)-1,column,
        V1Net.neurons[inputNeuron+orOffset], sixNFVal);
      addOneRetinaConnection(row+(getCols()*2)-1,column+1,
        V1Net.neurons[inputNeuron+orOffset], sixNFVal);
      addOneRetinaConnection(row+(getCols()*2),column,
        V1Net.neurons[inputNeuron+orOffset], sixNFVal);

      //-2-2,-2-1,-21,-22
      addOneRetinaConnection(row+(getCols()*3)-2,column-2,
        V1Net.neurons[inputNeuron+orOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)-2,column-1,
        V1Net.neurons[inputNeuron+orOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)-2,column+1,
        V1Net.neurons[inputNeuron+orOffset],sixFNVal);
      addOneRetinaConnection(row+(getCols()*3)-2,column+2,
        V1Net.neurons[inputNeuron+orOffset],sixFNVal);

      //-3-1,-3y,-31,-2y,-1y
      addOneRetinaConnection(row+(getCols()*4)-3,column-1,
        V1Net.neurons[inputNeuron+orOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)-3,column,
        V1Net.neurons[inputNeuron+orOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)-3,column+1,
        V1Net.neurons[inputNeuron+orOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)-2,column,
        V1Net.neurons[inputNeuron+orOffset],nineNFVal);
      addOneRetinaConnection(row+(getCols()*4)-1,column,
        V1Net.neurons[inputNeuron+orOffset],nineNFVal);

      //-1-3,-13,x-2,x2
      addOneRetinaConnection(row+(getCols()*5)-1,column-3,
        V1Net.neurons[inputNeuron+orOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5)-1,column+3,
        V1Net.neurons[inputNeuron+orOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5),column-2,
        V1Net.neurons[inputNeuron+orOffset],nineFNVal);
      addOneRetinaConnection(row+(getCols()*5),column+2,
        V1Net.neurons[inputNeuron+orOffset],nineFNVal);
      }
  }

  private void connectRetinaToHEdge (CABot1Net V1Net) 
  {
    int row;
    int column;
  
    int hEdgeOffset=getInputSize()*7;
    int sixOnOffRowStart = getInputCols()*2;
    int sixOffOnRowStart = getInputCols()*3;
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
        addOneRetinaConnection(row+sixOnOffRowStart,column,
           V1Net.neurons[hEdgeOffset+inputNeuron+col],onVal);

        //the offons are off by two both above and below
        addOneRetinaConnection(row+sixOffOnRowStart-2,column,
           V1Net.neurons[hEdgeOffset+inputNeuron+col],offVal);
        addOneRetinaConnection(row+sixOffOnRowStart+2,column,
           V1Net.neurons[hEdgeOffset+inputNeuron+col],offVal);
        }
      }    
    }

  private void connectRetinaToSEdge (CABot1Net V1Net) 
  {
    int row;
    int column;
  
    int sEdgeOffset=getInputSize()*8;
    int threeOffOnRowStart = getInputCols();
    int sixOnOffRowStart = getInputCols()*2;
    int sixOffOnRowStart = getInputCols()*3;
    double threeOffOnVal = 0.7;
    double sixOnVal = 0.7;
    double sixOffVal = 0.5;

    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getInputCols();
      column = inputNeuron%getInputCols();
      int toNeuron=sEdgeOffset+inputNeuron;

      addOneRetinaConnection(row+threeOffOnRowStart,column-1,
        V1Net.neurons[toNeuron],threeOffOnVal);
      addOneRetinaConnection(row+threeOffOnRowStart+1,column,
        V1Net.neurons[toNeuron],threeOffOnVal);

      //add 5 connections per 66NF neuron and 10 per 66FN
      for (int offset = -2 ; offset < 3; offset++)
        {
        //the onoffs are largely corect though a bottom edge is shifted up 1.
        addOneRetinaConnection(row+sixOnOffRowStart+offset,column-offset,
           V1Net.neurons[toNeuron],sixOnVal);

        //the offons are off by two both above and below
        addOneRetinaConnection(row+sixOffOnRowStart-2+offset,column-offset,
           V1Net.neurons[toNeuron],sixOffVal);
        addOneRetinaConnection(row+sixOffOnRowStart+2+offset,column-offset,
           V1Net.neurons[toNeuron],sixOffVal);
        }
      }    
  }

  private void connectRetinaToBEdge (CABot1Net V1Net) 
  {
    int row;
    int column;
  
    int bEdgeOffset=getInputSize()*9;
    int threeOffOnRowStart = getInputCols();
    int sixOnOffRowStart = getInputCols()*2;
    int sixOffOnRowStart = getInputCols()*3;
    double threeOffOnVal = 0.7;
    double sixOnVal = 0.7;
    double sixOffVal = 0.5;

    for (int inputNeuron = 0; inputNeuron < getInputSize(); inputNeuron++) 
      {
      row = inputNeuron/getInputCols();
      column = inputNeuron%getInputCols();
      int toNeuron=bEdgeOffset+inputNeuron;

      addOneRetinaConnection(row+threeOffOnRowStart,column+1,
        V1Net.neurons[toNeuron],threeOffOnVal);
      addOneRetinaConnection(row+threeOffOnRowStart-1,column,
        V1Net.neurons[toNeuron],threeOffOnVal);

      //add 5 connections per 66NF neuron and 10 per 66FN
      for (int offset = -2 ; offset < 3; offset++)
        {
        //the onoffs are largely corect though a bottom edge is shifted up 1.
        addOneRetinaConnection(row+sixOnOffRowStart+offset,column+offset,
           V1Net.neurons[toNeuron],sixOnVal);

        //the offons are off by two both above and below
        addOneRetinaConnection(row+sixOffOnRowStart-2+offset,column+offset,
           V1Net.neurons[toNeuron],sixOffVal);
        addOneRetinaConnection(row+sixOffOnRowStart+2+offset,column+offset,
           V1Net.neurons[toNeuron],sixOffVal);
        }
      }    
  }

  //undone the edges aren't quite right especially the slashes. crh 23-03-07
  //undone add vEdge crh 23-03-07

  public void connectRetinaToV1(CABot1Net V1Net) {
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
    int toShape, double weight,CABot1Net V2Net )  {
    
    int toRow = rowGroup*10+5;
    int toCol = colGroup*10+5;
    int toNeuron=toRow*getInputCols() + toCol;
    toNeuron += toShape*getInputSize();

    for (int fromRow=toRow+boxTop; fromRow<toRow+boxBot; fromRow++) {
      for (int fromCol=toCol+boxLeft; fromCol <toCol+boxRight;fromCol++){
        addOneV1Connection(featureNum,fromRow,fromCol, 
          V2Net.neurons[toNeuron],weight);
      }
    }
  }

  private double uniqueAngleVal = 0.7;
  private double sharedAngleVal = 0.3;
  private double uniqueEdgeVal = 0.4;
  private double sharedEdgeVal = 0.15;


  public void connectV1ToSmallPyramid(CABot1Net V2Net) {
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

  public void connectV1ToSmallStalagtite(CABot1Net V2Net) {
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
  public void connectV1ToMediumPyramid(CABot1Net V2Net) {
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

  public void connectV1ToMediumStalagtite(CABot1Net V2Net) {
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
  public void connectV1ToLargePyramid(CABot1Net V2Net) {
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

  public void connectV1ToLargeStalagtite(CABot1Net V2Net) {
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

  public void connectV1ToV2(CABot1Net V2Net) {
    connectV1ToSmallPyramid(V2Net);	
    connectV1ToSmallStalagtite(V2Net);	
    connectV1ToMediumPyramid(V2Net);	
    connectV1ToMediumStalagtite(V2Net);	
    connectV1ToLargePyramid(V2Net);	
    connectV1ToLargeStalagtite(V2Net);	
  }
  
  //****************intranet connections******************
  //set connections within a location specific group
  private void setLargeV2Connections (int baseNeuron) {
    int row = baseNeuron / getInputCols();
    int col = baseNeuron %getInputCols();
    int shape;
    int V2Offset; //for the different triangles
    
    shape = baseNeuron/getInputSize();
    V2Offset = shape* getInputSize();
    row -= shape*getInputRows();

    for (int connectRow = row - 4; connectRow < row + 4; connectRow++) 
      {
      if ((connectRow >= 0) && (connectRow < getInputCols()))
        for (int connectCol = col - 4; connectCol < col + 4; connectCol++)
          {
          if ((connectCol >= 0) && (connectCol < getInputCols()))
          addConnection(baseNeuron,V2Offset+(connectRow*getInputCols())+connectCol,5.27);
          }
      }	
  }

  //set connections from location specific to location general
  private void setTriangleV2Connections (int baseNeuron) {
    int shape;
    int V2Offset; //for the different triangles
  
    shape = baseNeuron/getInputSize();
    V2Offset = shape* getInputSize();
  	
    for (int synapse = 0; synapse < 30; synapse++)
      {
      int toColGroup = (int)(Math.random()*(getInputCols()/10));
      int toRowGroup = (int)(Math.random()*(getInputCols()/10));
      int toRow = (int)(Math.random()*10);
      int toNeuron = ((toRowGroup*10) + toRow)*getInputCols();
      toNeuron += toColGroup*10+1;
      toNeuron += V2Offset;
      addConnection(baseNeuron,toNeuron, Math.random());
      }
  }

  //Every inhibitory neuron inhibits another row of groups
  //in all the other shapes.
  private void setV2InhibOtherShapes(int baseNeuron) {
    int otherNeuron;
    int toRowGroup;
    int row = baseNeuron / getInputCols();
    int col = baseNeuron % getInputCols();
    int shape;

    shape = baseNeuron/getInputSize();

    row -= shape*getInputRows();

    //get other group based on row
    toRowGroup = row % 5; 

    for (int otherShape=0; otherShape < 4; otherShape ++) {
      if (shape != otherShape) {
        for (int toColGroup = 0; toColGroup < 5; toColGroup++) {
          otherNeuron = (((toRowGroup*10)+5)*getInputCols())+(toColGroup*10)+
            5;
          addConnection(baseNeuron, otherNeuron+otherShape*getInputSize(), 
            4.0);
          }
      }
    }
  }

  private void setV2InhibAdjacentGroups(int baseNeuron) {
    int otherNeuron;
    int toColGroup,toRowGroup;
    int row = baseNeuron / getInputCols();
    int col = baseNeuron % getInputCols();
    int shape;
    int V2Offset; //for the different triangles

    shape = baseNeuron/getInputSize();

    row -= shape*getInputRows();

    V2Offset = shape* getInputSize();

    int rowGroup = row/10;
    int colGroup = col/10;

    //get other group based on row
    if ((row%10) == 0) {toColGroup=-1;toRowGroup=-1;}
    else if ((row%10) == 1) {toColGroup=-1;toRowGroup=0;}
    else if ((row%10) == 2) {toColGroup=-1;toRowGroup=1;}
    else if ((row%10) == 3) {toColGroup=0;toRowGroup=-1;}
    else if ((row%10) == 4) {toColGroup=0;toRowGroup=1;}
    else if ((row%10) == 5) {toColGroup=1;toRowGroup=-1;}
    else if ((row%10) == 6) {toColGroup=1;toRowGroup=0;}
    else if ((row%10) == 7) {toColGroup=1;toRowGroup=1;}
    else {return;}

    toColGroup += colGroup;
    toRowGroup += rowGroup;

    if ((toColGroup < 0) || (toColGroup>=5) || (toRowGroup < 0) || 
        (toRowGroup>=5)) return;

    otherNeuron = (((toRowGroup*10)+5)*getInputCols())+(toColGroup*10)+5+
      V2Offset;

    addConnection(baseNeuron, otherNeuron, 0.2);
  }

  //the medium sized shapes inhibit both the small shapes
  private void setV2InhibSmallShapes(int baseNeuron) {
    if (baseNeuron < getInputSize()*2) return;
    int rowInGroup=baseNeuron%getInputSize();
    rowInGroup /=getInputCols();
    rowInGroup %=10;
    int baseOffset=(rowInGroup*getInputCols()) + (baseNeuron%10);
    
    for (int rowGroup = 0; rowGroup<5 ;rowGroup++) {
      for (int colGroup = 0; colGroup<5 ;colGroup++) {
        for (int offset = -3 ; offset < 2; offset++) {
          int toNeuron = (rowGroup*10*getInputCols())+colGroup*10;
          toNeuron += (baseOffset+offset);
          addConnection(baseNeuron, toNeuron, 4.0);
          toNeuron += getInputSize();
          addConnection(baseNeuron, toNeuron, 4.0);
	}
      }
    }
  }

  private void setInhibV2Connections (int baseNeuron) {
    setV2InhibOtherShapes(baseNeuron);
    setV2InhibAdjacentGroups(baseNeuron);
    setV2InhibSmallShapes(baseNeuron);
  }

  //set connections within the location specific CAs
  private void setSmallV2Connections (int baseNeuron) {
    int row = baseNeuron / getInputCols();
    int col = baseNeuron %getInputCols();
    int V2Offset; //for the different triangles
	
    if (row >=getInputCols()) row=row-getInputCols();
    int rowGroup = row/10;
    int colGroup = col/10;
    if (baseNeuron >= getInputSize()) V2Offset = getInputSize();
    else V2Offset = 0;

    for (int synapse =0; synapse < 10; synapse++) 
      {
      int toCol = (int)(Math.random()*10);
      int toRow = (int)(Math.random()*10);
      int toNeuron = ((rowGroup*10) + toRow)*getInputCols();

      if ((toCol == 5) && (toRow ==5)) return;
      toNeuron += colGroup*10+toCol;
      toNeuron += V2Offset;

      addConnection(baseNeuron,toNeuron,0.5 +(Math.random()));
      }	
  }

  //The V2 is broken up into sets for pyramid and stalagtite.
  //Within each, there are 10,10 areas.  The centre of these
  //(5,5) gets external activation (from V1).
  //That should send activity to the surrounding 100 neurons,
  //Every 3 and 8 neuron should inhibit the ones beyond those
  //The rest should stimulate local 25
  private void setV2Connections(int start, int finish) {
    for (int neuronIndex = start; neuronIndex < finish; neuronIndex ++)
      {
      //center
      if (((neuronIndex % 10) == 5)  &&  
         (((neuronIndex / getInputCols()) % 10) == 5))
        {
      	neurons[neuronIndex].setInhibitory(false);
        setLargeV2Connections(neuronIndex);
        }
      //location general neurons
      else if ((neuronIndex % 10) == 1)
        {
        neurons[neuronIndex].setInhibitory(false);
        setTriangleV2Connections(neuronIndex);
        }
      //inhibs
      else if ((neuronIndex % 5) == 3)
        {
        neurons[neuronIndex].setInhibitory(true);
	//System.out.println(" inhib " + neuronIndex);
        setInhibV2Connections(neuronIndex);
        }
      else 
        {
        neurons[neuronIndex].setInhibitory(false);
        setSmallV2Connections(neuronIndex);	  
        }
    }
  }
  
  private void setV2BigConnections(int start, int finish) {
    for (int neuronIndex = start; neuronIndex < finish; neuronIndex ++)
      {
      //center
      if (((neuronIndex % 10) == 5)  &&  
         (((neuronIndex / getInputCols()) % 10) == 5))
        {
      	neurons[neuronIndex].setInhibitory(false);
        setLargeV2Connections(neuronIndex);
        }
      //location general neurons
      else if ((neuronIndex % 10) == 1)
        {
        neurons[neuronIndex].setInhibitory(false);
        setTriangleV2Connections(neuronIndex);
        }
      else 
        {
        neurons[neuronIndex].setInhibitory(false);
        setSmallV2Connections(neuronIndex);	  
        }
    }
  }
  
  private void setV2Topology() {
    setV2Connections(0,getInputSize()-1);
    setV2Connections(getInputSize(),(getInputSize()*2)-1);
    setV2Connections(getInputSize()*2,(getInputSize()*3)-1);
    setV2Connections(getInputSize()*3,(getInputSize()*4)-1);
    setV2BigConnections(getInputSize()*4,(getInputSize()*5)-1);
    setV2BigConnections(getInputSize()*5,(getInputSize()*6)-1);
  }


  /*******Control and Spreading Activation Connectivity Functions*/
  //50x20 net with five mutually exclusive CAs
  private void setControlTopology() {
    //1 parsing
    //2 turn stack on
    //3 turn stack off
    //4 start erase and 
    //5 wait for erase to finish
    //6 start second erase
    //7 wait for second erase to finish
    //8 wait for new sentence
    //first control CA stimulates itself, second slightly, and inhibits sixth
    for (int neuron = 0; neuron < 200; neuron ++) 
      {
      if (neurons[neuron].isInhibitory) 
        for (int synapse=0; synapse < 15; synapse++) 
          addConnection(neuron,(((int)(Math.random()*200))+1000),2.0);
      else {
        for (int synapse=0; synapse < 15; synapse++) 
          addConnection(neuron,(((int)(Math.random()*200))),1.0);
        for (int synapse=0; synapse < 5; synapse++) 
          addConnection(neuron,(((int)(Math.random()*200)+200)),1.0);
        }
      }
    //second stimulates itself and inhibits first
    for (int neuron = 200; neuron < 400; neuron ++) 
      {
      if (neurons[neuron].isInhibitory) 
        for (int synapse=0; synapse < 15; synapse++) 
          addConnection(neuron,(((int)(Math.random()*200))),1.0);
      else
        for (int synapse=0; synapse < 15; synapse++) 
          {
          addConnection(neuron,(((int)(Math.random()*200)+200)),1.0);
	  }
      }
    //third stimulates itself and fourth slightly, but inhibits second
    for (int neuron = 400; neuron < 600; neuron ++) 
      {
      if (neurons[neuron].isInhibitory) 
        for (int synapse=0; synapse < 15; synapse++) 
          addConnection(neuron,(((int)(Math.random()*200)+200)),1.0);
      else {
        for (int synapse=0; synapse < 15; synapse++) 
          {
          addConnection(neuron,(((int)(Math.random()*200)+400)),1.0);
	  }
        for (int synapse=0; synapse < 5; synapse++) 
          {
          addConnection(neuron,(((int)(Math.random()*200)+600)),0.35);
	  }
        }
      }
    //fourth stimulates itself, fifth slightly, and inhibits third 
    for (int neuron = 600; neuron < 800; neuron ++) 
      {
      if (neurons[neuron].isInhibitory) 
        for (int synapse=0; synapse < 15; synapse++) 
          addConnection(neuron,(((int)(Math.random()*200)+400)),1.0);
      else {
        for (int synapse=0; synapse < 15; synapse++) {
          addConnection(neuron,(((int)(Math.random()*200)+600)),1.0);
          addConnection(neuron,(((int)(Math.random()*200)+800)),0.1);
	  }
        }
      }
    //fifth stimulates itself, sixth slightly, and inhibits fourth 
    for (int neuron = 800; neuron < 1000; neuron ++) 
      {
      if (neurons[neuron].isInhibitory) 
        for (int synapse=0; synapse < 15; synapse++) 
          addConnection(neuron,(((int)(Math.random()*200)+600)),1.0);
      else {
        for (int synapse=0; synapse < 15; synapse++) 
          {addConnection(neuron,(((int)(Math.random()*200)+800)),1.0); }
          {addConnection(neuron,(((int)(Math.random()*200)+1000)),0.15); }
        }
      }
    //crhz undone
    //sixth stimulates itself, seventh slightly, and inhibits fifth 
    for (int neuron = 1000; neuron < 1200; neuron ++) 
      {
      if (neurons[neuron].isInhibitory) 
        for (int synapse=0; synapse < 15; synapse++) 
          addConnection(neuron,(((int)(Math.random()*200)+800)),1.0);
      else {
        for (int synapse=0; synapse < 15; synapse++) {
          addConnection(neuron,(((int)(Math.random()*200)+1000)),1.0);
          addConnection(neuron,(((int)(Math.random()*200)+1200)),0.1);
	  }
        }
      }
    //seventh stimulates itself, eight slightly, and inhibits sixth
    for (int neuron = 1200; neuron < 1400; neuron ++) 
      {
      if (neurons[neuron].isInhibitory) 
        for (int synapse=0; synapse < 15; synapse++) 
          addConnection(neuron,(((int)(Math.random()*200)+1000)),1.0);
      else {
        for (int synapse=0; synapse < 15; synapse++) 
          {addConnection(neuron,(((int)(Math.random()*200)+1200)),1.0); }
          {addConnection(neuron,(((int)(Math.random()*200)+1400)),0.15); }
        }
      }
    //eighth stimulates itself 
    for (int neuron = 1400; neuron < 1600; neuron ++) 
      {
      if (!neurons[neuron].isInhibitory) 
        for (int synapse=0; synapse < 15; synapse++) 
          {addConnection(neuron,(((int)(Math.random()*200)+1000)),1.0); }
      }
    /*
    for (int neuron = 0; neuron < getSize(); neuron ++) 
      {
      int fromCA neuron/200;
      if (neurons[neuron].isInhibitory) 
        {
        for (int synapse=0; synapse < 40; synapse++) 
     	  {
      	  //make to CA a different CA
      	  int toCA=(int)Math.random()*2;
      	  if (fromCA == 0)
            toCA++;
      	  else if ((fromCA==1) && (toCA == 1))
            toCA =2;

          addConnection(neuron,((int)(Math.random()*200)+(200*toCA)),2.0);
	  }
        }
      else
	{
	for (int synapse=0; synapse < 15; synapse++) 
	  {
	  addConnection(neuron,(((int)(Math.random()*200))+(fromCA*200)),1.0);
	  }
	}
      }
      */
  }

  //30x20 with 6 mutually exclusive CAs
  private void setActionTopology() {
    for (int neuron = 0; neuron < getSize(); neuron ++) 
    {
      int CA = neuron/100;;
      if (neurons[neuron].isInhibitory) 
        {
  	    for (int synapse=0; synapse < 40; synapse++) 
  	      {
		  int toNeuron = (int) (Math.random()*getSize());
	      if ((toNeuron/100) != CA)
  	        addConnection(neuron,toNeuron,2.0);
          }
        }
      else
        {
        for (int synapse=0; synapse < 20; synapse++) 
          {
          addConnection(neuron,((int)(Math.random()*100)+(CA*100)),1.0);
          }
        }
    }
  }

  private int neuronsInFact = 100;
  private void setInhibFactNetCA(int fromCA,int toCA,double weight) {
    for (int neuron = 0; neuron < neuronsInFact; neuron ++) 
      {
      int fromNeuron=(fromCA*neuronsInFact)+neuron;
      if (neurons[fromNeuron].isInhibitory()) 
        {
        for (int synapse=0; synapse < 40; synapse++) 
          {
          int toNeuron = (toCA*neuronsInFact)+(int)(Math.random()*100);
          addConnection(fromNeuron,toNeuron,weight);
          }
        }
      }
  }

  private void setMutuallyExclusiveFacts() {
    setInhibFactNetCA(13,14,1.0);
    setInhibFactNetCA(13,15,1.0);
    setInhibFactNetCA(14,13,1.0);
    setInhibFactNetCA(14,15,1.0);
    setInhibFactNetCA(15,13,1.0);
    setInhibFactNetCA(15,14,1.0);
  }

  private void setMutuallyExclusiveGoals() {
    int goalPool[]=  new int[8];
    int poolSize=8;
    goalPool[0]=0;
    goalPool[1]=1;
    goalPool[2]=2;
    goalPool[3]=3;
    goalPool[4]=4;
    goalPool[5]=6;
    goalPool[6]=7;
    goalPool[7]=10;

    for (int fromPool=0;fromPool<poolSize;fromPool++){
      for (int toPool=0;toPool<poolSize;toPool++){
        if (goalPool[fromPool] != goalPool[toPool]) 
          setInhibFactNetCA(goalPool[fromPool],goalPool[toPool],2.0);
      }
    }
    setInhibFactNetCA(5,0,2.0);
    setInhibFactNetCA(5,1,2.0);
    setInhibFactNetCA(5,2,2.0);
    setInhibFactNetCA(5,3,2.0);
    setInhibFactNetCA(5,7,2.0);
    setInhibFactNetCA(5,10,2.0);
  }

  //N 100 somewhat  mutually exclusive CAs
  private void setFactTopology() {
    for (int neuron = 0; neuron < getSize(); neuron ++) 
    {
      int CA = neuron/100;;
      if (!(neurons[neuron].isInhibitory())) 
        {
        for (int synapse=0; synapse < 12; synapse++) 
          {
          addConnection(neuron,((int)(Math.random()*100)+(CA*100)),1.0);
          }
        }
    }
    setMutuallyExclusiveGoals();
    setMutuallyExclusiveFacts();
  }

  //60x10 with 6 mutually exclusive CAs
  private void setModuleTopology() {
    for (int neuron = 0; neuron < getSize(); neuron ++) 
    {
      int CA = neuron/100;;
      if (neurons[neuron].isInhibitory) 
        {
          for (int synapse=0; synapse < 40; synapse++) 
  	    {
	    int toNeuron = (int) (Math.random()*getSize());
	    if ((toNeuron/100) != CA)
  	      addConnection(neuron,toNeuron,2.0);
            }
        }
      else
        {
        for (int synapse=0; synapse < 15; synapse++) 
          {
          addConnection(neuron,((int)(Math.random()*100)+(CA*100)),1.0);
          }
        }
    }
  }

  public void initializeNeurons() {
    //set up topologies.
    createNeurons();

    if (topology == 1){
      //System.out.println("input parse topology ");
      setInputTopology(150,0.45);
    }
    else if (topology == 2){
      //System.out.println("noun parse topology ");
      setNounTopology(300);
    }
    else if (topology == 3){
      System.out.println("other parse topology");
    }
    else if (topology == 4){
      //System.out.println("stack top parse topology");
      setStackTopTopology(300);
    }
    else if (topology == 5){
      //System.out.println("parse  rule topology");
      setParseRuleTopology(300);
    }
    else if (topology == 6){
      //System.out.println("parse stack topology");
      setStackTopology(1800);
      return;
    }
    else if (topology == 7){
      //System.out.println("test topology");
      setTestTopology();
    }
    else if (topology == 8){
      //System.out.println("push topology");  //and pop
      setPushTopology();
    }
    else if (topology == 9){
      //System.out.println("verb topology ");
      setVerbTopology();
    }
    else if (topology == 10){
      //System.out.println("erase  topology");
      setEraseTopology();
    }
    else if (topology == 11){
      //System.out.println("instance topology ");
      setInstanceTopology(150,0.45);
    }
    else if (topology == 12){
      //System.out.println("erase bound topology ");
      setEraseBoundTopology();
    }
    else if (topology == 13){
      //System.out.println("other word parse topology ");
      setOtherWordTopology(300);
    }
    else if (topology == 14){
      //no internal net connections
      //System.out.println("visual input topology ");
    }
    else if (topology == 15){
      //System.out.println("control topology ");
      setControlTopology();
    }
    else if (topology == 16){
      //System.out.println("action topology ");
      setActionTopology();
    }
    else if (topology == 17){
      //System.out.println("fact topology ");
      setFactTopology();
    }
    else if (topology == 18){
      //System.out.println("module topology ");
      setModuleTopology();
    }
    else if (topology == 19){
      //no internal net connections
      //System.out.println("retina topology ");
    }
    else if (topology == 20){
      //no internal net connections
      //System.out.println("V1 topology ");
    }
    else if (topology == 21){
      //System.out.println("V2 topology ");
      setV2Topology();
    }
    else System.out.println("bad toppology specified "+ topology);
  }
  
  
  //**********Connect Nets to Each Other***********************************
  private void connectInputWordToNoun(int inputStart, int nounStart, CABot1Net nounNet) {
  	int nounOffset;
    for (int inputNeuron = inputStart; inputNeuron < inputStart + 150; inputNeuron ++) 
      {
      for (int synapse = 0; synapse < 7; synapse++)
  	    {
  	    nounOffset = (int)(Math.random()*300);
  	    nounOffset += nounStart;
  	    if (!neurons[inputNeuron].isInhibitory())
          neurons[inputNeuron].addConnection(nounNet.neurons[nounOffset],0.3);
  	    }
      }
  }

  public void connectInputToNoun(CABot1Net nounNet) {
    connectInputWordToNoun(1*150,0,nounNet); //Me to N1
    connectInputWordToNoun(6*150,8*300,nounNet); //It to N8
    connectInputWordToNoun(7*150,1*300,nounNet); //Left to N2
    connectInputWordToNoun(8*150,2*300,nounNet); //Right to N3
    connectInputWordToNoun(9*150,3*300,nounNet); //Forward to N4
    connectInputWordToNoun(10*150,4*300,nounNet); //Backward to N5
    connectInputWordToNoun(11*150,5*300,nounNet); //Pyramid to N5
    connectInputWordToNoun(12*150,6*300,nounNet); //Stalagtite to N6
    connectInputWordToNoun(13*150,7*300,nounNet); //Door to N7
  }
  
  public void connectInputToOther(CABot1Net otherNet) {
    //might have to make a connectInputWordToOther later
    connectInputWordToNoun(2*150,0,otherNet); //period to O1
    connectInputWordToNoun(14*150,1*300,otherNet); //the to O2
    connectInputWordToNoun(15*150,2*300,otherNet); //toward to O3
    connectInputWordToNoun(16*150,3*300,otherNet); //to to O4
  }

  private void connectInputWordToVerb(int inputStart, int verbStart, 
    CABot1Net verbNet) {
    int verbOffset;
    for (int inputNeuron = inputStart; inputNeuron < inputStart + 150; inputNeuron ++) 
	  {
	  for (int synapse = 0; synapse < 7; synapse++)
		{
		verbOffset = (int)(Math.random()*300);
		verbOffset += verbStart;
		if (!neurons[inputNeuron].isInhibitory())
          neurons[inputNeuron].addConnection(verbNet.neurons[verbOffset],0.35);//.5
		}
	  }
  }
  
  private void connectOneNounToOneInstance(int nounStart, int instanceStart,CABot1Net instanceNet) 
    {
      for (int nounNeuron = nounStart; nounNeuron < nounStart+300; nounNeuron ++) 
      {
        for (int synapse = 0; synapse < 3; synapse++)
          {
          int instanceOffset = (int)(Math.random()*150);
		  instanceOffset += instanceStart;
          if (!neurons[nounNeuron].isInhibitory())
            neurons[nounNeuron].addConnection(instanceNet.neurons[instanceOffset],0.2);
          }
      }
    }

  public void connectNounToInstance(CABot1Net instanceNet) {
    connectOneNounToOneInstance(0,0,instanceNet); //Me to I1
    connectOneNounToOneInstance(1*300,1*150,instanceNet); //Left to I2
    connectOneNounToOneInstance(5*300,2*150,instanceNet); //Pyramid to I3
    connectOneNounToOneInstance(8*300,3*150,instanceNet); //It to I4
    connectOneNounToOneInstance(2*300,4*150,instanceNet); //Right to I5
    connectOneNounToOneInstance(3*300,5*150,instanceNet); //Forward to I6
    connectOneNounToOneInstance(4*300,6*150,instanceNet); //Backward to I7
    connectOneNounToOneInstance(6*300,7*150,instanceNet); //Stalagtite to I8
    connectOneNounToOneInstance(7*300,8*150,instanceNet); //Door to I9
  }

  public void connectInputToVerb(CABot1Net verbNet) {
     connectInputWordToVerb(0,0,verbNet); //Follow to V1
     connectInputWordToVerb(5*150,1*480,verbNet); //Move to V2
     connectInputWordToVerb(4*150,2*480,verbNet); //Turn to V3
     connectInputWordToVerb(3*150,3*480,verbNet); //Go to V4
  }
  
  //rule activation functions
  private void connectVerbToV_NPPLoc(CABot1Net ruleNet) {
  for (int verb = 0; verb < 4; verb++)
    {
    for (int verbNeuron = 0; verbNeuron < 240; verbNeuron ++) 
      {
 	  int fromNeuron = verb*480+verbNeuron;
      for (int synapse = 0; synapse < 15; synapse++)
        {
        int ruleOffset = (((verbNeuron*5)+synapse)%300) + 1200;
        if (!neurons[verbNeuron].isInhibitory())
          neurons[fromNeuron].addConnection(ruleNet.neurons[ruleOffset],0.02);
        }
      }
     }
 }
 
 private void connectPrepToVerb_PPLoc(CABot1Net ruleNet) {
 	for (int otherWordNeuron = 600; otherWordNeuron < 1199; otherWordNeuron ++) 
 	{
 	  for (int synapse = 0; synapse < 15; synapse++)
 	    {
 	    int ruleOffset = ((otherWordNeuron+synapse) % 300)+1200;;
 	    if (!neurons[otherWordNeuron].isInhibitory())
 	      neurons[otherWordNeuron].addConnection(ruleNet.neurons[ruleOffset],0.010);
 	    }
 	}
 }

 private void connectPPToVerb_PPLoc(CABot1Net ruleNet) {
    for (int prep =0; prep < 3; prep++) 
      {
	  for (int instanceNeuron = 0; instanceNeuron < 60; instanceNeuron +=3) 
        {
	int fromNeuron = prepStart+(prep*prepCASize)+300+instanceNeuron;
        if (!neurons[fromNeuron].isInhibitory())
		  {
          for (int synapse = 0; synapse < 75; synapse++)
            {
            int ruleOffset = (((instanceNeuron*5)+synapse) % 300)+1200;
            neurons[fromNeuron].addConnection(ruleNet.neurons[ruleOffset],
              0.03);
            }
          }
        }
      }
  }

 private void connectPrepToPP_Noun(CABot1Net ruleNet) {
    for (int otherWordNeuron = 600; otherWordNeuron < 1199;otherWordNeuron ++) 
    {
      for (int synapse = 0; synapse < 15; synapse++)
        {
        int ruleOffset = ((otherWordNeuron+synapse) % 300)+900;;
        if (!neurons[otherWordNeuron].isInhibitory())
          neurons[otherWordNeuron].addConnection(ruleNet.neurons[ruleOffset],0.020);//.018
        }
    }
  }

  private void connectNounToPP_Noun(CABot1Net ruleNet) {
    for (int nounNeuron = 0; nounNeuron < size(); nounNeuron ++) 
    {
      for (int synapse = 0; synapse < 15; synapse++)
        {
        int ruleOffset = ((nounNeuron+synapse) % 300) + 900;
        if (!neurons[nounNeuron].isInhibitory())
          neurons[nounNeuron].addConnection(ruleNet.neurons[ruleOffset],0.018);      
        }
    }
  }

  private void connectPrepToPrep_Det(CABot1Net ruleNet) {
    for (int otherWordNeuron = 600; otherWordNeuron < 1199;otherWordNeuron ++) 
    {
      for (int synapse = 0; synapse < 15; synapse++)
        {
        int ruleOffset = ((otherWordNeuron+synapse) % 300)+600;;
        if (!neurons[otherWordNeuron].isInhibitory())
          neurons[otherWordNeuron].addConnection(ruleNet.neurons[ruleOffset],0.020);//.018
        }
    }
  }

  private void connectDetToPrep_Det(CABot1Net ruleNet) {
    for (int otherWordNeuron = 300; otherWordNeuron < 600; otherWordNeuron ++) 
    {
      for (int synapse = 0; synapse < 15; synapse++)
        {
        int ruleOffset = ((otherWordNeuron+synapse) % 300)+600;;
        if (!neurons[otherWordNeuron].isInhibitory())
          neurons[otherWordNeuron].addConnection(ruleNet.neurons[ruleOffset],0.018);
        }
    }
  }

  private void connectPeriodToV_Period(CABot1Net ruleNet) {
    for (int otherWordNeuron = 0; otherWordNeuron < 300; otherWordNeuron ++) 
    {
      for (int synapse = 0; synapse < 15; synapse++)
        {
        int ruleOffset = ((otherWordNeuron+synapse) % 300)+300;;
        if (!neurons[otherWordNeuron].isInhibitory())
          neurons[otherWordNeuron].addConnection(ruleNet.neurons[ruleOffset],0.021);//.018
        }
    }
  }

  private void connectVerbToV_period(CABot1Net ruleNet) {
    for (int verb = 0; verb < 4; verb++)
      {
      for (int verbNeuron = 0; verbNeuron < 240; verbNeuron ++) 
        {
    	int fromNeuron = (verb*480)+verbNeuron;
        for (int synapse = 0; synapse < 15; synapse++)
          {
          int ruleOffset = (((verbNeuron*5)+synapse)%300)+300;
          if (!neurons[verbNeuron].isInhibitory())
            neurons[fromNeuron].addConnection(ruleNet.neurons[ruleOffset],0.025);
          }
        }
      }
  }

  private void connectNounToV_Nobj(CABot1Net ruleNet) {
    for (int nounNeuron = 0; nounNeuron < size(); nounNeuron ++) 
    {
      for (int synapse = 0; synapse < 15; synapse++)
        {
        int ruleOffset = (nounNeuron+synapse) % 300;
        if (!neurons[nounNeuron].isInhibitory())
          neurons[nounNeuron].addConnection(ruleNet.neurons[ruleOffset],0.018);
        }
    }
  }

  private void connectVerbToV_Nobj(CABot1Net ruleNet) {
  	for (int verb = 0; verb < 4; verb++)
	  {
      for (int verbNeuron = 0; verbNeuron < 240; verbNeuron ++) 
        {
		int fromNeuron = verb*480+verbNeuron;
        for (int synapse = 0; synapse < 15; synapse++)
          {
          int ruleOffset = ((verbNeuron*5)+synapse)%300;
          if (!neurons[verbNeuron].isInhibitory())
            neurons[fromNeuron].addConnection(ruleNet.neurons[ruleOffset],0.025);
          }
        }
	  }
  }

  public void connectNounToRule(CABot1Net ruleNet) {
    connectNounToPP_Noun(ruleNet); 
    connectNounToV_Nobj(ruleNet); 
  }

  public void connectOtherToRule(CABot1Net ruleNet) {
    connectPeriodToV_Period(ruleNet);
    connectPrepToPrep_Det(ruleNet);
    connectDetToPrep_Det(ruleNet);
    connectPrepToPP_Noun(ruleNet);
    connectPrepToVerb_PPLoc(ruleNet);
  }
  
  public void connectInstanceToRule(CABot1Net ruleNet) {
    connectPPToVerb_PPLoc(ruleNet);
  }
  
  private void connectPrepToPrepInstance(CABot1Net instanceNet, int otherStart,
    int prepInstance) {
    for (int neuronNum = otherStart; neuronNum < otherStart+300; neuronNum++) 
      {
      for (int synapse = 0; synapse < 6; synapse++)
        {
        int instanceOffset = (int)(Math.random()*300);
        instanceOffset += prepStart+(prepInstance*prepCASize);
        if (!neurons[neuronNum].isInhibitory())
          neurons[neuronNum].addConnection(instanceNet.neurons[instanceOffset],0.2);
        }
      }
  }
	
  public void connectOtherToInstance(CABot1Net instanceNet) {
    connectPrepToPrepInstance(instanceNet,600,0);  //toward to the first prep
    connectPrepToPrepInstance(instanceNet,900,1);  //to to prep2
  }

  public void connectVerbToRule(CABot1Net ruleNet) {
     connectVerbToV_Nobj(ruleNet); 
     connectVerbToV_period(ruleNet); 
     connectVerbToV_NPPLoc(ruleNet); 
  }

  public void connectVerbToInstance(CABot1Net instanceNet) {
  	int numVerbs = 4;
  	for (int verbs = 0 ; verbs < numVerbs; verbs++) 
	  {
	  for (int neuronNum = 300; neuronNum < 480; neuronNum++) 
	    {
	    for (int synapse = 0; synapse < 120; synapse++)
	      {
	      if (!neurons[neuronNum+(verbs*480)].isInhibitory())
	        neurons[neuronNum+(verbs*480)].addConnection(
	       	  instanceNet.neurons[(int)(Math.random()*instanceNet.size())],0.01);
	      }
	    }
	  }
  }

  //rules 
  //VP -> VP NPobj activate VP via stack, the VP object slot, and let it bind
  //  via the active NP (then pop)
  //VP -> VP period  just stop input and the stack
  //PP -> PP det pop the det
  //PP -> PP noun activate the PP, its slot, and let it bind via the active NP 
  // (then pop)
  public void connectRuleToTest(CABot1Net testNet) {
      /*
    //stop the end of test to stop the start of push
	//VP->VP NP-obj
    for (int r1Neuron = 0; r1Neuron < 300; r1Neuron ++) 
    {
      for (int synapse = 0; synapse < 200; synapse++)
        {
        if (neurons[r1Neuron].isInhibitory())
          neurons[r1Neuron].addConnection(testNet.neurons[(int)(Math.random()*testNet.getSize())],-2.2);
        }
    }
	//PP->prep det, PP -> PP noun and VP -> PPloc
    for (int r1Neuron = 600; r1Neuron < 1500; r1Neuron ++) 
    {
      for (int synapse = 0; synapse < 200; synapse++)
        {
        if (neurons[r1Neuron].isInhibitory())
          neurons[r1Neuron].addConnection(testNet.neurons[(int)(Math.random()*testNet.getSize())],-2.2);
        }
    }
      */
    //stop the end of test to stop the start of push
    for (int r1Neuron = 0; r1Neuron < 1800; r1Neuron ++) 
    {
      for (int synapse = 0; synapse < 100; synapse++)
        {
        if (neurons[r1Neuron].isInhibitory())
          neurons[r1Neuron].addConnection(
            testNet.neurons[(int)(Math.random()*testNet.getSize())],-8.2);
        }
    }
  }
  
  //activate the first stack item
  public void connectRuleToStack(CABot1Net stackNet) {
    //VP -> VP Npobj
    for (int r1Neuron = 0; r1Neuron < 300; r1Neuron ++) 
    {
      for (int synapse = 0; synapse < 20; synapse++)
        {
        if (!neurons[r1Neuron].isInhibitory())
          neurons[r1Neuron].addConnection(
            stackNet.neurons[(int)(Math.random()*300)],0.5);
        }
    }
    //PP -> PP noun
    for (int r1Neuron = 900; r1Neuron < 1200; r1Neuron ++) 
    {
      for (int synapse = 0; synapse < 20; synapse++)
        {
        if (!neurons[r1Neuron].isInhibitory())
          neurons[r1Neuron].addConnection(
            stackNet.neurons[((int)(Math.random()*300)+ 300)],0.5);
        }
    }
    //VP -> VP PPloc
    for (int r1Neuron = 1200; r1Neuron < 1500; r1Neuron ++) 
    {
      for (int synapse = 0; synapse < 20; synapse++)
        {
        if (!neurons[r1Neuron].isInhibitory())
          neurons[r1Neuron].addConnection(
            stackNet.neurons[(int)(Math.random()*300)],1.5);
        }
    }
  }

  //the second rule is stop and shuts down the stacktop  
  public void connectRuleToStackTop(CABot1Net stackTopNet) {
    for (int r1Neuron = 300; r1Neuron < 600; r1Neuron ++) 
    {
      for (int synapse = 0; synapse < 100; synapse++)
        {
        if (neurons[r1Neuron].isInhibitory())
          neurons[r1Neuron].addConnection(
            stackTopNet.neurons[(int)(Math.random()*stackTopNet.getSize())],-5.5);
        }
    }
  }
  
  //the second rule (VP-> VP.) stops and shuts down the input  
  public void connectRuleToInput(CABot1Net inputNet) {
    for (int r1Neuron = 300; r1Neuron < 600; r1Neuron ++) 
    {
      for (int synapse = 0; synapse < 100; synapse++)
        {
        if (neurons[r1Neuron].isInhibitory())
          neurons[r1Neuron].addConnection(
            inputNet.neurons[(int)(Math.random()*inputNet.getSize())],-5.5);
        }
    }
  }
  
  public void connectRuleToVerb(CABot1Net verbNet) {
  	//VP->VP NPobj activates the object items 
  	for (int verb = 0; verb < 4; verb ++) 
	  {
      for (int r1Neuron = 0; r1Neuron < 300; r1Neuron ++) 
        {
		int toNeuron= (verb*480) + 300;
        for (int synapse = 0; synapse < 3; synapse++)
          {
          if (!neurons[r1Neuron].isInhibitory())
            neurons[r1Neuron].addConnection(
              verbNet.neurons[toNeuron+((r1Neuron+synapse)%60)],.08);
          }
        }
	  //VP->VP PP loc activates the loc slot 
	  for (int r1Neuron = 1200; r1Neuron < 1500; r1Neuron ++) 
	    {
            int toNeuron= (verb*480) + 360;
	    for (int synapse = 0; synapse < 3; synapse++)
	      {
	      if (!neurons[r1Neuron].isInhibitory())
	        neurons[r1Neuron].addConnection(
	          verbNet.neurons[toNeuron+((r1Neuron+synapse)%60)],.08);
	      }
	    }
	  }
  }
  
  //PP-> PP noun activates the PP's slot
  public void connectRuleToInstance(CABot1Net oWordNet) {
    for (int r4Neuron = 900; r4Neuron < 1200; r4Neuron ++) 
      {
	  for (int prep = 0; prep < 3 ; prep++) 
	    {
        for (int synapse = 0; synapse < 3; synapse++)
          {
		  int toNeuron = (r4Neuron+synapse)%60;
		  toNeuron += prepStart + (prep*prepCASize) + 300;
          if (!neurons[r4Neuron].isInhibitory())
            neurons[r4Neuron].addConnection(oWordNet.neurons[toNeuron],.5);  //arbitrary
          }
	    }
      }
  }

  public void connectRuleToPop(CABot1Net popNet) {
    for (int r1Neuron = 0; r1Neuron < 300; r1Neuron ++) 
    {
      for (int synapse = 0; synapse < 3; synapse++)
        {
        if (!neurons[r1Neuron].isInhibitory())
          neurons[r1Neuron].addConnection(
            popNet.neurons[(int)(Math.random()*200)],0.4);
        }
    }
    for (int r1Neuron = 600; r1Neuron < 1200; r1Neuron ++) 
    {
      for (int synapse = 0; synapse < 3; synapse++)
        {
        if (!neurons[r1Neuron].isInhibitory())
          neurons[r1Neuron].addConnection(
            popNet.neurons[(int)(Math.random()*200)],0.4);
        }
    }
    for (int r1Neuron = 1200; r1Neuron < 1500; r1Neuron ++) 
    {
      for (int synapse = 0; synapse < 3; synapse++)
        {
        if (!neurons[r1Neuron].isInhibitory())
          neurons[r1Neuron].addConnection(
            popNet.neurons[(int)(Math.random()*200)],0.3);
        }
    }
  }

  public void connectInputToPush(CABot1Net pushNet) {
    for (int inputNeuron = 0; inputNeuron < size(); inputNeuron ++) 
    {
      for (int synapse = 0; synapse < 7; synapse++)
        {
        int pushOffset = (inputNeuron+synapse)%200;
        if (!neurons[inputNeuron].isInhibitory())
          neurons[inputNeuron].addConnection(pushNet.neurons[pushOffset],0.08);//.12
        }
    }
  }
  
  //connectStackTopErase to suppress stack 2 when stacktop is 1
  public void connectStackTopToEraseBound(CABot1Net eraseBoundNet) {
    for (int neuron = 340; neuron<560;neuron++) 
      {
      if (!neurons[neuron].isInhibitory())
        {
     	for (int synapse = 0 ; synapse < 9; synapse ++)
      	  {
       	  int toNeuron= (neuron+synapse)%100;
       	  neurons[neuron].addConnection(eraseBoundNet.neurons[toNeuron],0.15);
       	  }
        }
      }
  }
  
  //Set up connections so that the push rule is called when stacktop is zero.
  private void connectStackTopZeroToPush(CABot1Net pushNet) {
    for (int stackTopNeuron = 40; stackTopNeuron < 260; stackTopNeuron ++) 
      {
      for (int synapse = 0; synapse < 5; synapse++)
  	    {
  	    int pushOffset = (int)(Math.random()*200);
  	    if (!neurons[stackTopNeuron].isInhibitory())
          neurons[stackTopNeuron].addConnection(pushNet.neurons[pushOffset],0.1);
  	    }
      }
  }
  
  public void connectStackTopToPush(CABot1Net pushNet) {
    connectStackTopZeroToPush(pushNet);
  }


  //stacktop elements enervate the appropriate stack elements
  //stacktop 0 doesn't enervate any stack, 1 does the first etc, 2 the 2nd etc.
  public void connectStackTopToStack(CABot1Net stackNet) {
  	for (int stackTopOffset = 1; stackTopOffset< 6 ;stackTopOffset++) 
	  {
      for (int stackTopNeuron = 40; stackTopNeuron < 260; stackTopNeuron ++)
        {
		int fromNeuron = (stackTopOffset*300) + stackTopNeuron;
        for (int synapse = 0; synapse < 10; synapse++)
  	      {
  	      if (!neurons[stackTopNeuron].isInhibitory())
		    {
			int toNeuron = ((stackTopNeuron*2) + synapse) % 300;
			toNeuron +=  (stackTopOffset -1) * 300; 
            neurons[fromNeuron].addConnection(stackNet.neurons[toNeuron],0.2);
		    }	
  	      }
        }
	  }
  }
  
  //pop first decrements stacktop
  //second it turns off rule, stack, and words
  //third it calls erase
  public void connectPopToStackTop(CABot1Net stackTopNet) {
    for (int popNeuron = 0; popNeuron < 200; popNeuron ++) 
      {
      //each neuron needs to connect to each push row
      //note that elements are 1 based for the maths below.
      for (int stackTopEl = 0; stackTopEl < 4; stackTopEl++)
        for (int synapse = 0; synapse < 3; synapse++)
        {
        int stackTopOffset = (stackTopEl*300 + ((popNeuron+synapse)%40));
        if (!neurons[popNeuron].isInhibitory())
          neurons[popNeuron].addConnection(stackTopNet.neurons[stackTopOffset],0.1);
        }
      }
  }
  
  public void connectPopToRule(CABot1Net ruleNet) {
    for (int popNeuron = 200; popNeuron < 400; popNeuron ++) 
      {
      if (neurons[popNeuron].isInhibitory())
        for (int synapse = 0; synapse < 100; synapse++)
          {
          int ruleOffset = (int)(Math.random()*ruleNet.getSize());
          neurons[popNeuron].addConnection(ruleNet.neurons[ruleOffset],-5.2);
          }
      }
  }

  public void connectPopToVerb(CABot1Net verbNet) {
    for (int popNeuron = 200; popNeuron < 400; popNeuron ++) 
      {
      if (neurons[popNeuron].isInhibitory())
        for (int synapse = 0; synapse < 100; synapse++)
          {
          int verbOffset = (int)(Math.random()*verbNet.getSize());
          neurons[popNeuron].addConnection(verbNet.neurons[verbOffset],-9.0);
          }
      }
  }
  
  public void connectPopToNoun(CABot1Net nounNet) {
    for (int popNeuron = 200; popNeuron < 400; popNeuron ++) 
      {
      if (neurons[popNeuron].isInhibitory())
        for (int synapse = 0; synapse < 50; synapse++)
          {
          int nounOffset = (int)(Math.random()*nounNet.getSize());
          neurons[popNeuron].addConnection(nounNet.neurons[nounOffset],-1.2);
          }
      }
  }

  public void connectPopToOther(CABot1Net otherNet) {
    for (int popNeuron = 200; popNeuron < 400; popNeuron ++) 
      {
      if (neurons[popNeuron].isInhibitory())
        for (int synapse = 0; synapse < 50; synapse++)
          {
          int nounOffset = (int)(Math.random()*otherNet.getSize());
          neurons[popNeuron].addConnection(otherNet.neurons[nounOffset],-1.2);
          }
      }
  }

  public void connectPopToStack(CABot1Net stackNet) {
    for (int popNeuron = 200; popNeuron < 400; popNeuron ++) 
      {
      if (neurons[popNeuron].isInhibitory())
        for (int synapse = 0; synapse < 50; synapse++)
          {
          int stackOffset = (int)(Math.random()*900);
          neurons[popNeuron].addConnection(stackNet.neurons[stackOffset],-1.2);
          }
      }
  }

  public void connectPopToErase(CABot1Net eraseNet) {
    for (int popNeuron = 400; popNeuron < 600; popNeuron ++) 
    {
      if (!neurons[popNeuron].isInhibitory())
        for (int synapse = 0; synapse < 5; synapse++)
          {
          int eraseOffset = (int)(Math.random()*200);
          neurons[popNeuron].addConnection(eraseNet.neurons[eraseOffset],0.15);
          }
     }
  }
  
  //Push is a set of 6 sequences that go in roughly 10 cycle blocks
  //1 increments the stack top
  //2 rest to prime stack
  //3 rest
  //4 activates stack
  //4 activates words (getting rest from input to select)
  //5 passes over to test
  public void connectPushToStackTop(CABot1Net stackTopNet) {
    for (int ruleNeuron = 0; ruleNeuron < 200; ruleNeuron ++) 
      {
	  //each neuron needs to connect to each push row
	  //note that elements are 1 based for the maths below.
      for (int stackTopEl = 1; stackTopEl < 5; stackTopEl++)
        for (int synapse = 0; synapse < 3; synapse++)
        {
        int stackTopOffset = (stackTopEl*300 - ((ruleNeuron+synapse) % 40));
        if (!neurons[ruleNeuron].isInhibitory())
          neurons[ruleNeuron].addConnection(stackTopNet.neurons[stackTopOffset],0.1);
        }
    }
  }

  public void connectPushToStack(CABot1Net stackNet) {
    for (int pushNeuron = 600; pushNeuron < 800; pushNeuron ++) 
      {
	  for (int stackCA = 0; stackCA < 6; stackCA++) 
	    {
        for (int synapse = 0; synapse < 5; synapse++)
          {
          if (!neurons[pushNeuron].isInhibitory()) 
		    {
		    int toNeuron = (stackCA * 300) + pushNeuron - 600+synapse;
            neurons[pushNeuron].addConnection(
  	        stackNet.neurons[toNeuron],0.4);
		    }
          }
	    }
      }
  }

  //connect push to noun 
  //the first part of push should provide some activity broadly
  //to the entire net
  private int wordStart = 600;
  private int wordFinish = 800;
  public void connectPushToNounNet(CABot1Net nounNet) {
    for (int pushNeuron = wordStart; pushNeuron < wordFinish; pushNeuron ++) 
      {
      for (int noun = 0; noun < 9; noun++) 
        {
        for (int synapse = 0; synapse < 15; synapse++)
          {
          if (!neurons[pushNeuron].isInhibitory())
            neurons[pushNeuron].addConnection(
            nounNet.neurons[(noun*300)+(((pushNeuron*3)+synapse)%300)],0.13);
          }
        }
      }
  }

    private int otherWords=4;
  public void connectPushToOtherNet(CABot1Net otherNet) {
    for (int pushNeuron = wordStart; pushNeuron < wordFinish; pushNeuron ++) 
      {
      for (int otherWord = 0; otherWord < otherWords; otherWord++) 
        {
        for (int synapse = 0; synapse < 15; synapse++)
          {
          if (!neurons[pushNeuron].isInhibitory())
            neurons[pushNeuron].addConnection(
            otherNet.neurons[(otherWord*300)+(((pushNeuron*3)+synapse)%300)],0.13);
          }
        }
      }
  }

  public void connectPushToVerbNet(CABot1Net verbNet) {
    for (int pushNeuron = wordStart; pushNeuron < wordFinish; pushNeuron ++) 
      {
      for (int verb = 0; verb < 4; verb++) 
	{
        for (int synapse = 0; synapse < 15; synapse++)
          {
          if (!neurons[pushNeuron].isInhibitory())
            neurons[pushNeuron].addConnection(
  	      verbNet.neurons[(verb*480)+(((pushNeuron*3)+synapse)%300)],0.13);
          }
        }
      }
  }

  
  public void connectPushToTest(CABot1Net testNet) {
  	//connect push to the top of test to ignite it.
  	for (int ruleNeuron = 800; ruleNeuron < 1000; ruleNeuron ++) 
  	  {
  	  for (int synapse = 0; synapse < 6; synapse++)
  	    {
  	    if (!neurons[ruleNeuron].isInhibitory())
  	      neurons[ruleNeuron].addConnection(testNet.neurons[(int)(Math.random()*200)],0.25);
  	    }
  	  }
  	//connect push to the bottom of test to extinguish it.
    for (int ruleNeuron = 0; ruleNeuron < 200; ruleNeuron ++) 
      {
      for (int synapse = 0; synapse < 40; synapse++)
        {
        if (neurons[ruleNeuron].isInhibitory())
          neurons[ruleNeuron].addConnection(testNet.neurons[3400+(int)(Math.random()*400)],-2.8);
        }
      }
  }

  public void connectEraseToPop(CABot1Net popNet) {
    //erase turns pop off
    for (int ruleNeuron = 0; ruleNeuron < 200; ruleNeuron ++) 
      {
      for (int synapse = 0; synapse < 80; synapse++)
        {
        if (neurons[ruleNeuron].isInhibitory())
          neurons[ruleNeuron].addConnection(popNet.neurons[(int)(Math.random()*200)+400],-1.2);
        }
      }
  }
  
  public void connectErBoundToStack(CABot1Net stackNet){
    for (int neuron = 0; neuron < 100; neuron ++) 
      {
        for (int synapse = 0; synapse < 40; synapse++)
        {
        if (neurons[neuron].isInhibitory())
          {
          int toNeuron = (((neuron*3)+synapse)%300)+300;
          neurons[neuron].addConnection(stackNet.neurons[toNeuron],-3.0);
          }
        }
      }
    for (int neuron = 100; neuron < 200; neuron ++) 
      {
        for (int synapse = 0; synapse < 40; synapse++)
        {
        if (neurons[neuron].isInhibitory())
          {
          int toNeuron = (((neuron*3)+synapse)%300);
          neurons[neuron].addConnection(stackNet.neurons[toNeuron],-3.0);
          }
        }
      }
  }

  private void connectEraseToBoundOnce(CABot1Net eraseBoundNet,int eraseStart,
    int boundCA)
    {
    for (int eraseNeuron=eraseStart; eraseNeuron<eraseStart+200; eraseNeuron++)
      {
      for (int synapse = 0; synapse < 10; synapse++)
        {
        if (!neurons[eraseNeuron].isInhibitory())
  	  {
          int toNeuron = ((eraseNeuron+synapse)%100) + boundCA*100;
          neurons[eraseNeuron].addConnection(
            eraseBoundNet.neurons[toNeuron],.3);
  	  }
        }
      }
  }

  public void connectEraseToBound(CABot1Net eraseBoundNet) {
    connectEraseToBoundOnce(eraseBoundNet,200,1);
    connectEraseToBoundOnce(eraseBoundNet,600,0);
    connectEraseToBoundOnce(eraseBoundNet,1600,1);
    connectEraseToBoundOnce(eraseBoundNet,2000,0);
    connectEraseToBoundOnce(eraseBoundNet,2800,1);
    connectEraseToBoundOnce(eraseBoundNet,3200,0);
  }

  //This turns stack s1 and s2 to keep their fastbind neurons up.
  public void connectEraseToStack(CABot1Net stackNet) {
    connectTestToStackGo(200,0,stackNet);
    connectTestToStackGo(600,1,stackNet);
    connectTestToStackGo(1600,0,stackNet);
    connectTestToStackGo(2000,1,stackNet);
    connectTestToStackGo(2800,0,stackNet);
    connectTestToStackGo(3200,1,stackNet);

    connectTestToShut(400,stackNet);
    connectTestToShut(800,stackNet);
    connectTestToShut(1800,stackNet);
    connectTestToShut(2200,stackNet);
    connectTestToShut(3000,stackNet);
    connectTestToShut(3400,stackNet);
  }
  
  public void connectEraseToVerb(CABot1Net verbNet) {
    connectTestToShutWord(400,verbNet);
    connectTestToShutWord(800,verbNet);
    connectTestToShutWord(1800,verbNet);
    connectTestToShutWord(2200,verbNet);
    connectTestToShutWord(3000,verbNet);
    connectTestToShutWord(3400,verbNet);
  }

  public void connectEraseToNoun(CABot1Net nounNet) {
    connectTestToShutWord(400,nounNet);
    connectTestToShutWord(800,nounNet);
    connectTestToShutWord(1800,nounNet);
    connectTestToShutWord(2200,nounNet);
    connectTestToShutWord(3000,nounNet);
    connectTestToShutWord(3400,nounNet);
  }
  
  public void connectEraseToOther(CABot1Net otherNet) {
    connectTestToShutWord(400,otherNet);
    connectTestToShutWord(800,otherNet);
    connectTestToShutWord(1800,otherNet);
    connectTestToShutWord(2200,otherNet);
    connectTestToShutWord(3000,otherNet);
    connectTestToShutWord(3400,otherNet);
  }
  
  public void connectEraseToRule(CABot1Net ruleNet) {
    for (int eraseNeuron = 0; eraseNeuron < 3600; eraseNeuron ++) 
      {
      for (int synapse = 0; synapse < 40; synapse++)
        {		
        if (neurons[eraseNeuron].isInhibitory())
          neurons[eraseNeuron].addConnection(
	    ruleNet.neurons[(int)(Math.random()*ruleNet.getSize())],-0.22);
        }
      }
  }

  public void connectEraseToTest(CABot1Net testNet) {
    for (int eraseNeuron = 3400; eraseNeuron < 3600; eraseNeuron ++) 
      {
      for (int synapse = 0; synapse < 20; synapse++)
        {
        if (!neurons[eraseNeuron].isInhibitory())
          neurons[eraseNeuron].addConnection(testNet.neurons[(int)(Math.random()*200)],0.25);
        }
      }
  }

  //if you get to the end of test, just push.
  public void connectTestToPush(CABot1Net pushNet) {
    for (int neuronNum = 0; neuronNum < 400; neuronNum++)
      {
      if (neurons[neuronNum].isInhibitory())
        {
        //inhibit prior in Push
        for (int synapse = 0; synapse < 15; synapse++) 
          {
          int toNeuron=((neuronNum+synapse)%200)+800;
          neurons[neuronNum].addConnection(pushNet.neurons[toNeuron],-2.0);
          }
        }
      }
    for (int neuron = 3600; neuron < 3800; neuron ++) 
      {
      for (int synapse = 0; synapse < 10; synapse++)
        {
        if (!neurons[neuron].isInhibitory())
          neurons[neuron].addConnection(pushNet.neurons[(int)(Math.random()*200)],0.4);
        }
      }
  }

  private void connectTestToShutWord(int testStart,CABot1Net shutNet) {
    for (int neuron = testStart; neuron < testStart+200; neuron ++) 
      {
      for (int synapse = 0; synapse < 100; synapse++)
        {
        if (neurons[neuron].isInhibitory())
          neurons[neuron].addConnection(
  	    shutNet.neurons[(int)(Math.random()*shutNet.getSize())],-1.9);
        }
      }
  }

  public void connectTestToOther(CABot1Net otherNet) {
    connectTestToShutWord(0,otherNet);
    connectTestToShutWord(200,otherNet);
    connectTestToShutWord(1200,otherNet);
    connectTestToShutWord(2200,otherNet);
    connectTestToShutWord(3200,otherNet);
    connectTestToShutWord(3400,otherNet);
  }

  public void connectTestToVerb(CABot1Net verbNet) {
    connectTestToShutWord(0,verbNet);
    connectTestToShutWord(200,verbNet);
    connectTestToShutWord(1200,verbNet);
    connectTestToShutWord(2200,verbNet);
    connectTestToShutWord(3200,verbNet);
    connectTestToShutWord(3400,verbNet);
  }

  public void connectTestToNoun(CABot1Net nounNet) {
    connectTestToShutWord(0,nounNet);
    connectTestToShutWord(200,nounNet);
    connectTestToShutWord(1200,nounNet);
    connectTestToShutWord(2200,nounNet);
    connectTestToShutWord(3200,nounNet);
    connectTestToShutWord(3400,nounNet);
  }

  public void connectTestToInstance(CABot1Net instanceNet) {
  	for (int neuron = 200; neuron < 400; neuron ++) 
  	  {
  	  for (int synapse = 0; synapse < 50; synapse++)
  	    {
  	    if (neurons[neuron].isInhibitory())
  	      neurons[neuron].addConnection(
  	    instanceNet.neurons[(int)(Math.random()*instanceNet.getSize())],-1.9);
  	    }
  	  }
  	for (int neuron = 3400; neuron < 3600; neuron ++) 
  	  {
  	  for (int synapse = 0; synapse < 50; synapse++)
  	    {
  	    if (neurons[neuron].isInhibitory())
  	      neurons[neuron].addConnection(
  	    instanceNet.neurons[(int)(Math.random()*instanceNet.getSize())],-1.9);
  	    }
  	  }
  }

  private void connectTestToShut(int testStart,CABot1Net shutNet) {
    for (int neuron = testStart; neuron < testStart+200; neuron ++) 
      {
      for (int synapse = 0; synapse < 40; synapse++)
        {
        if (neurons[neuron].isInhibitory())
          neurons[neuron].addConnection(
  	    shutNet.neurons[(int)(Math.random()*shutNet.getSize())],-2.8);
        }
      }
  }

  private void connectTestToStackGo(int testStart, int stackEl, CABot1Net stackNet) {
    for (int neuron = testStart; neuron < testStart + 200; neuron ++) 
      {
      for (int synapse = 0; synapse < 20; synapse++)
        {
        if (!neurons[neuron].isInhibitory())
          neurons[neuron].addConnection(
  	    stackNet.neurons[(int)((stackEl*300)+Math.random()*300)],0.5);
        }
      }
  }

  public void connectTestToStack(CABot1Net stackNet) {
    connectTestToShut(0,stackNet);
    connectTestToShut(200,stackNet);
    connectTestToShut(1200,stackNet);
    connectTestToShut(2200,stackNet);
    connectTestToShut(3200,stackNet);
    connectTestToShut(3400,stackNet);
	
	connectTestToStackGo(3000,0,stackNet);
	connectTestToStackGo(2800,0,stackNet);
	connectTestToStackGo(2600,0,stackNet);
	connectTestToStackGo(2400,0,stackNet);
	connectTestToStackGo(2000,1,stackNet);
	connectTestToStackGo(1800,1,stackNet);
	connectTestToStackGo(1600,1,stackNet);
	connectTestToStackGo(1400,1,stackNet);
	connectTestToStackGo(100,2,stackNet);
	connectTestToStackGo(800,2,stackNet);
	connectTestToStackGo(600,2,stackNet);
	connectTestToStackGo(400,2,stackNet);
  }

  public void connectTestToErase(CABot1Net eraseNet) {
    for (int neuron = 0; neuron <  200; neuron ++) 
      {
      for (int synapse = 0; synapse < 20; synapse++)
        {
        if (neurons[neuron].isInhibitory())
          neurons[neuron].addConnection(
            eraseNet.neurons[3400+(int)(Math.random()*200)],-1.5);
        }
      }
  }

  public void connectStackToVerb(CABot1Net verbNet) {
    for (int neuronNum=0; neuronNum<size();neuronNum++) 
	  {
	  if ((neuronNum % 300) > 150)
        for (int synapseNum=0;synapseNum < 100; synapseNum++) 
          ((CANTNeuronFastBind)neurons[neuronNum]).addConnection(
	        verbNet.neurons[(int)(Math.random()*verbNet.getSize())],0.01); //1200
//System.out.println(neuronNum + " " + ((CANTNeuronFastBind)neurons[neuronNum]).getFastSynapses());
	  }
  }

  public void connectStackToOther(CABot1Net otherNet) {
    for (int neuronNum=0; neuronNum<size();neuronNum++) 
    {
    if ((neuronNum % 300) > 150)
      for (int synapseNum=0;synapseNum < 110; synapseNum++) 
        ((CANTNeuronFastBind)neurons[neuronNum]).addConnection(
        otherNet.neurons[(int)(Math.random()*1200)],0.01);
    }
  }

  public void connectStackToNoun(CABot1Net nounNet) {
    for (int neuronNum=0; neuronNum<size();neuronNum++) 
    {
    if ((neuronNum % 300) > 150)
        for (int synapseNum=0;synapseNum < 100; synapseNum++) 
          ((CANTNeuronFastBind)neurons[neuronNum]).addConnection(
          nounNet.neurons[(int)(Math.random()*nounNet.getSize())],0.01);
//System.out.println(neuronNum + " " + ((CANTNeuronFastBind)neurons[neuronNum]).getFastSynapses());
    }
  }
  
  /*******Action Connectitivity*/
  public void connectControlToFact(CABot1Net factNet) {
    //Top control CA suppress action
    for (int neuronNum=0; neuronNum<200;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapseNum=0;synapseNum < 250; synapseNum++) {
          int toNeuron=(int) (Math.random()*factNet.getSize());
          neurons[neuronNum].addConnection(factNet.neurons[toNeuron],-8.0);
	}
    }
    //bottom controls also suppress action
    for (int neuronNum=600; neuronNum<1600;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapseNum=0;synapseNum < 250; synapseNum++) {
          int toNeuron=(int) (Math.random()*factNet.getSize());
          neurons[neuronNum].addConnection(factNet.neurons[toNeuron],-8.0);
	}
    }
  }
  
  public void connectStackTopToControl(CABot1Net controlNet) {
    //StackTop surpresses control 2. When stacktop stops, control 2
    //can become active
    for (int neuronNum=0; neuronNum<getSize();neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapseNum=0;synapseNum < 50; synapseNum++) {
          int toNeuron=(int) (Math.random()*200) + 200;
          neurons[neuronNum].addConnection(controlNet.neurons[toNeuron],-8.0);
	}
    }
  }
  
  public void connectControlToRule(CABot1Net ruleNet) {
    //Second control CA suppress rules
    for (int neuronNum=200; neuronNum<400;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory()){
        for (int synapseNum=0;synapseNum < 50; synapseNum++) {
          int toNeuron=(int) (Math.random()*ruleNet.getSize());
          neurons[neuronNum].addConnection(ruleNet.neurons[toNeuron],-8.0);
	}
        for (int synapseNum=0;synapseNum < 100; synapseNum++) {
          int toNeuron=(int) (Math.random()*200)+200;
          neurons[neuronNum].addConnection(ruleNet.neurons[toNeuron],-8.0);
	}
      }
    }
  }
  

  public void connectFactToControl(CABot1Net controlNet) {
    //Any goal fact should suppress control 2 and 4,  and activate control 3
    for (int neuronNum=0; neuronNum<11*neuronsInFact;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory()) {
        for (int synapseNum=0;synapseNum < 50; synapseNum++) {
          int toNeuron=(int) (Math.random()*200) + 200;
          neurons[neuronNum].addConnection(controlNet.neurons[toNeuron],-8.0);
        }
        for (int synapseNum=0;synapseNum < 50; synapseNum++) {
          int toNeuron=(int) (Math.random()*200) + 600;
          neurons[neuronNum].addConnection(controlNet.neurons[toNeuron],-2.0);
	}
      }
      else 
        for (int synapseNum=0;synapseNum < 5; synapseNum++) {
          int toNeuron=(int) (Math.random()*200) + 400;
          neurons[neuronNum].addConnection(controlNet.neurons[toNeuron],1.0);
	}
    }
  }
  
  public void connectControlToStack(CABot1Net eraseNet) {
    //the second control CA stimulates stack 1
    for (int neuronNum=200; neuronNum<400;neuronNum++) 
      {
      if (!neurons[neuronNum].isInhibitory())
        for (int synapse = 0; synapse < 5; synapse++)
          {
          int eraseOffset = (int)(Math.random()*300);
          neurons[neuronNum].addConnection(eraseNet.neurons[eraseOffset],1.0);
          }
      }
    //The third control CA suppresses the stack
    for (int neuronNum=400; neuronNum<600;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapse = 0; synapse < 105; synapse++)
          {
          int eraseOffset = (int)(Math.random()*300);
          neurons[neuronNum].addConnection(eraseNet.neurons[eraseOffset],-2.0);
          }
      }
  }

  public void connectControlToInstance(CABot1Net instanceNet) {
    //Top control CA suppress action
    for (int neuronNum=400; neuronNum<600;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapse = 0; synapse < 205; synapse++)
          {
          int instanceOffset = (int)(Math.random()*instanceNet.getSize());
          neurons[neuronNum].addConnection(
            instanceNet.neurons[instanceOffset],-8.5);
          }
      }
  }

  public void connectControlToVerb(CABot1Net verbNet) {
    //Top control CA suppress action
    for (int neuronNum=400; neuronNum<600;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapse = 0; synapse < 185; synapse++)
          {
          int verbOffset = (int)(Math.random()*verbNet.getSize());
          neurons[neuronNum].addConnection(verbNet.neurons[verbOffset],-8.5);
          }
      }
  }
  
  public void connectControlToErase(CABot1Net eraseNet) {
    //The fourth control CA starts the erase
    for (int neuronNum=600; neuronNum<800;neuronNum++) 
      {
      if (!neurons[neuronNum].isInhibitory())
        for (int synapse = 0; synapse < 5; synapse++)
          {
          int eraseOffset = (int)(Math.random()*200);
          neurons[neuronNum].addConnection(eraseNet.neurons[eraseOffset],0.15);
          }
      }
    //The sixth stops the last erase and starts another
    for (int neuronNum=1000; neuronNum<1200;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapse = 0; synapse < 50; synapse++)
          {
          int eraseOffset = (int)(Math.random()*200+3400);
          neurons[neuronNum].addConnection(eraseNet.neurons[eraseOffset],-2.1);
          }
      else
        for (int synapse = 0; synapse < 5; synapse++)
          {
          int eraseOffset = (int)(Math.random()*200);
          neurons[neuronNum].addConnection(eraseNet.neurons[eraseOffset],0.15);
          }
      }
    //The eighth stops the second erase
    for (int neuronNum=1400; neuronNum<1600;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapse = 0; synapse < 50; synapse++)
          {
          int eraseOffset = (int)(Math.random()*200+3400);
          neurons[neuronNum].addConnection(eraseNet.neurons[eraseOffset],-2.1);
          }
      }
  }
  
  public void connectControlToEraseBound(CABot1Net eraseBoundNet) {
    //The fourth and fifth control CA turns on the eraseBound CAs to 
    //suppress stack activation
    for (int neuronNum=600; neuronNum<1400;neuronNum++) 
      {
      if (!neurons[neuronNum].isInhibitory())
        for (int synapse = 0; synapse < 18; synapse++)
          {
          int eraseBoundOffset = (int)(Math.random()*200);
          neurons[neuronNum].addConnection(eraseBoundNet.
            neurons[eraseBoundOffset],0.15);
          }
      }
  }
  
  public void connectControlToTest(CABot1Net testNet) {
    //The fifth to eighth inhibit test so it doesn't start up after erase
    for (int neuronNum=800; neuronNum<1600;neuronNum++) 
      {
      if (neurons[neuronNum].isInhibitory())
        for (int synapse = 0; synapse < 80; synapse++)
          {
          int testOffset = (int)(Math.random()*200);
          neurons[neuronNum].addConnection(testNet.neurons[testOffset],-2.0);
          }
      }
  }
  public void connectEraseToControl(CABot1Net controlNet) {
    //start of erase moves control from CA 4 to 5 or
    //6 to 7
    for (int eraseNeuron = 200; eraseNeuron < 400; eraseNeuron ++) 
      {
      if (!neurons[eraseNeuron].isInhibitory()) 
        for (int synapse = 0; synapse < 7; synapse++)
          {
          neurons[eraseNeuron].addConnection(controlNet.neurons[(int)
            (Math.random()*200)+800],0.2);
          neurons[eraseNeuron].addConnection(controlNet.neurons[(int)
            (Math.random()*200)+1200],0.2);
          }
      }
    //end of erase moves from control 5 to control 6 or
    //7 to 8
    for (int eraseNeuron = 3400; eraseNeuron < 3600; eraseNeuron ++) 
      {
      if (neurons[eraseNeuron].isInhibitory())
        for (int synapse = 0; synapse < 40; synapse++) {
          neurons[eraseNeuron].addConnection(controlNet.neurons[(int)
            (Math.random()*200)+800],-1.2);
          neurons[eraseNeuron].addConnection(controlNet.neurons[(int)
            (Math.random()*200)+1200],-1.2);
	}
      else
        for (int synapse = 0; synapse < 6; synapse++){
          neurons[eraseNeuron].addConnection(controlNet.neurons[(int)
            (Math.random()*200)+1000],0.5);
          neurons[eraseNeuron].addConnection(controlNet.neurons[(int)
            (Math.random()*200)+1400],0.5);
	}
      }
  }
  

  //*******Actions
  private void connectOneVerbToOneFact(int verbNum, int factNum, 
    CABot1Net factNet) {
    for (int neuronNum=0; neuronNum<300;neuronNum++) 
      {
      int fromNeuron = neuronNum+(verbNum*480);
      if (!neurons[fromNeuron].isInhibitory())
        for (int synapseNum=0;synapseNum < 4; synapseNum++) 
          {
          int toNeuron = ((int)(Math.random()*100))+(factNum*100);
          neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],0.25);
          }
      }
  }

  public void connectVerbToFact(CABot1Net factNet) {
    connectOneVerbToOneFact(2,0,factNet); //Turn to turn left
    connectOneVerbToOneFact(2,1,factNet); //Turn to turn right
    connectOneVerbToOneFact(1,2,factNet); //Move to move forward
    connectOneVerbToOneFact(1,3,factNet); //Move to move back
    connectOneVerbToOneFact(3,4,factNet); //Go to 'turnleft+go' of factNet
    connectOneVerbToOneFact(3,6,factNet); //Go to 'turnright+go' of factNet
    connectOneVerbToOneFact(2,7,factNet); //Turn to turn toward
    connectOneVerbToOneFact(3,10,factNet); //Go to goto
  }
  
  private void connectOneInstanceToOneFact(int instanceNum, int factNum, 
    CABot1Net factNet) {
    for (int neuronNum=0; neuronNum<150;neuronNum++) 
      {
      int fromNeuron = neuronNum+(instanceNum*150);
        if (!neurons[fromNeuron].isInhibitory())
          {
          for (int synapseNum=0;synapseNum < 4; synapseNum++) {
            int toNeuron = (factNum*neuronsInFact)+
              (int)(Math.random()*neuronsInFact);
            neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],0.5);
	    }
          }
      }
  }

  private void connectOnePrepInstanceToOneFact(int prepInstanceNum, 
    int factNum, CABot1Net factNet) {
    for (int prepNeuron = 0; prepNeuron < 240; prepNeuron++) {
      int fromNeuron= prepStart+(prepInstanceNum*prepCASize)+prepNeuron;
      if (!neurons[fromNeuron].isInhibitory())
        for (int synapseNum=0;synapseNum < 4; synapseNum++) {
          int toNeuron = (factNum*neuronsInFact)+
           (int)(Math.random()*neuronsInFact);
          neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],0.25);
	}
    }
  }

  public void connectInstanceToFact(CABot1Net factNet) {
    connectOneInstanceToOneFact(1,0,factNet); //left to turn left
    connectOneInstanceToOneFact(4,1,factNet); //right to turn right
    connectOneInstanceToOneFact(5,2,factNet); //forward to go Forward
    connectOneInstanceToOneFact(6,3,factNet); //backward to go Backward
    connectOneInstanceToOneFact(1,4,factNet); //left to go left
    connectOneInstanceToOneFact(4,6,factNet); //right to go right
    connectOneInstanceToOneFact(2,8,factNet); //pyramid to pyramid goal
    connectOneInstanceToOneFact(7,9,factNet); //stalagtite to stal goal

    connectOnePrepInstanceToOneFact(0,7,factNet); //toward to turn toward
    connectOnePrepInstanceToOneFact(0,8,factNet); //toward to pyramid goal
    connectOnePrepInstanceToOneFact(0,9,factNet); //toward to stal goal

    connectOnePrepInstanceToOneFact(1,10,factNet); //to to goto
  }
  //connect V2 type neurons to the fact that they are present
  private void connectV2TypeToFact(CABot1Net factNet, int V2CA, int fact) {
    //for each position invariant neuron
    for (int neuronNum=1; neuronNum<getInputSize();neuronNum+=10) 
      {
      int fromNeuron = neuronNum+(getInputSize()*V2CA);
      for (int synapseNum=0;synapseNum < 10; synapseNum++) {
        int toNeuron = (fact*neuronsInFact)+(int)(Math.random()*neuronsInFact);
        neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],2.5);
        }
      }
  }

  private void connectV2PositionToFact(CABot1Net factNet, int V2GroupCol, 
    int fact) {
    for (int row = 0; row<getInputRows()*4;row ++) {
      for (int col = 2; col< 10;col ++) {
        int fromNeuron = (V2GroupCol*10)+(row*getInputRows())+col;
        for (int synapseNum=0;synapseNum < 20; synapseNum++) {
          int toNeuron=(fact*neuronsInFact)+(int)(Math.random()*neuronsInFact);
          neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],1.5);
        }
      }
    }
  }

  public void connectV2ToFact(CABot1Net factNet) {
    connectV2TypeToFact(factNet,0,11); //small pyramid
    connectV2TypeToFact(factNet,1,12); //small stalagtite
    connectV2TypeToFact(factNet,2,11); //large pyramid
    connectV2TypeToFact(factNet,3,12); //large stalagtite

    connectV2TypeToFact(factNet,4,16); //large pyr big object
    connectV2TypeToFact(factNet,5,16); //large stal big object

    connectV2PositionToFact(factNet,0,13);
    connectV2PositionToFact(factNet,1,13);
    connectV2PositionToFact(factNet,2,14);
    connectV2PositionToFact(factNet,3,15);
    connectV2PositionToFact(factNet,4,15);
  }

//************************************************
  private int neuronsInModule = 100;
  private void connectOneFactToOneModule(int factNum, int moduleNum, 
    CABot1Net moduleNet, double weight) {

    for (int neuronNum=0; neuronNum<neuronsInFact;neuronNum++) 
      {
      int fromNeuron = neuronNum+(factNum*neuronsInFact);
      if (!neurons[fromNeuron].isInhibitory())
        {
        for (int synapseNum=0;synapseNum < 10; synapseNum++) {
          int toNeuron = ((int)(Math.random()*neuronsInModule))+
            (moduleNum*neuronsInModule);
          neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron],weight);
          }
        }
      }
  }

  public void connectFactToModule(CABot1Net moduleNet) {
    connectOneFactToOneModule(0,0,moduleNet,0.2);//turn+left
    connectOneFactToOneModule(1,1,moduleNet,0.2);//turn+right
    connectOneFactToOneModule(2,2,moduleNet,0.2);//move+foreward
    connectOneFactToOneModule(3,3,moduleNet,0.2);//move+backward
    connectOneFactToOneModule(4,4,moduleNet,0.2);//goleft1  --> goleft1 
    connectOneFactToOneModule(5,5,moduleNet,0.2);//forward after turn
    connectOneFactToOneModule(6,6,moduleNet,0.2);//goright1 --> goright1 
    connectOneFactToOneModule(7,0,moduleNet,0.02);//turntoward to left
    connectOneFactToOneModule(7,1,moduleNet,0.02);//turntoward to right
    connectOneFactToOneModule(13,0,moduleNet,0.02);//object on left to left
    connectOneFactToOneModule(15,1,moduleNet,0.02);//object on right to right

    connectOneFactToOneModule(7,7,moduleNet,0.02);//turntoward to turncentre
    connectOneFactToOneModule(14,7,moduleNet,0.05);//object ctr to turncentre

    //non object using goals inhibit turncentre
    connectOneFactToOneModule(0,7,moduleNet,-2.0);//left to turncentre
    connectOneFactToOneModule(1,7,moduleNet,-2.0);
    connectOneFactToOneModule(2,7,moduleNet,-2.0);
    connectOneFactToOneModule(3,7,moduleNet,-2.0);
    connectOneFactToOneModule(4,7,moduleNet,-2.0);
    connectOneFactToOneModule(5,7,moduleNet,-2.0);
    connectOneFactToOneModule(6,7,moduleNet,-2.0); //go right

    //An object in the scene can make turn left or right come on via turn towar
    //go left and right inhibit turn left and right 
    connectOneFactToOneModule(4,0,moduleNet,-2.0);
    connectOneFactToOneModule(4,1,moduleNet,-2.0);
    connectOneFactToOneModule(6,0,moduleNet,-2.0);
    connectOneFactToOneModule(6,1,moduleNet,-2.0);
    //go forward and back inhibit turn left and right 
    connectOneFactToOneModule(2,0,moduleNet,-2.0);
    connectOneFactToOneModule(2,1,moduleNet,-2.0);
    connectOneFactToOneModule(3,0,moduleNet,-2.0);
    connectOneFactToOneModule(3,1,moduleNet,-2.0);
    //left and right inhbit each other
    connectOneFactToOneModule(1,0,moduleNet,-2.0);
    connectOneFactToOneModule(0,1,moduleNet,-2.0);

    connectOneFactToOneModule(8,8,moduleNet,0.3);//goal pyramid -> no object
    connectOneFactToOneModule(9,8,moduleNet,0.3);//goal stal -> no object
    connectOneFactToOneModule(11,8,moduleNet,-2.0);//pyramid in scene -> no obj
    connectOneFactToOneModule(12,8,moduleNet,-2.0);//stal in scene -> no object

    connectOneFactToOneModule(10,0,moduleNet,0.025);//goto to left
    connectOneFactToOneModule(13,0,moduleNet,0.03); //left to left
    connectOneFactToOneModule(10,1,moduleNet,0.025);//goto to right
    connectOneFactToOneModule(15,1,moduleNet,0.03); //right to right
    connectOneFactToOneModule(10,2,moduleNet,0.03); //goto to forward
    connectOneFactToOneModule(14,2,moduleNet,0.03); //object ctr to forward
    connectOneFactToOneModule(10,10,moduleNet,0.02); //goto to goto done
    connectOneFactToOneModule(16,10,moduleNet,0.05); //big object to goto done

    connectOneFactToOneModule(13,2,moduleNet,-2.0);//left inhibits forward
    connectOneFactToOneModule(15,2,moduleNet,-2.0);//left inhibits forward

    connectOneFactToOneModule(10,7,moduleNet,-2.0);//goto to turn centre
  }

  private int factNeurons = 100;

  private void connectOneModuleToOneFact(int moduleNum, int factNum, 
    CABot1Net factNet, boolean inhibit) {
    int moduleNeurons = 100;

     for (int neuronNum=0; neuronNum<moduleNeurons;neuronNum++) 
      {
      int fromNeuron = neuronNum+(moduleNum*moduleNeurons);
      if ((!neurons[fromNeuron].isInhibitory()) && (!inhibit))
        {
        for (int synapseNum=0;synapseNum < 20; synapseNum++) {
          int toNeuron = ((int)(Math.random()*factNeurons))+
            (factNum*factNeurons);
          neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],0.9);
          }
        }
      if ((neurons[fromNeuron].isInhibitory()) && (inhibit))
        {
        for (int synapseNum=0;synapseNum < 50; synapseNum++) {
          int toNeuron = ((int)(Math.random()*factNeurons))+
            (factNum*factNeurons);
          neurons[fromNeuron].addConnection(factNet.neurons[toNeuron],-1.5);
          }
        }
      }
  }
  
//moduleNet feedback to factNet, such as 'turnleft'of moduleNet -> 
//'turnleft+go' of factNet
  public void connectModuletoFact(CABot1Net factNet) {
    connectOneModuleToOneFact(4,5,factNet,false);//goleft1j module-> goforward2
    connectOneModuleToOneFact(6,5,factNet,false);//gort1j module-> goforward2

    //modules shut down there prior facts
    connectOneModuleToOneFact(0,0,factNet,true);//left module -> left fact
    connectOneModuleToOneFact(1,1,factNet,true);//right module -> right fact
    connectOneModuleToOneFact(2,2,factNet,true);//forward module ->forward fact
    connectOneModuleToOneFact(3,3,factNet,true);//back module -> back fact
    connectOneModuleToOneFact(5,4,factNet,true);////forwardafterturn ->goleft1
    connectOneModuleToOneFact(5,5,factNet,true);//forwardafterturn -> fat
    connectOneModuleToOneFact(5,6,factNet,true);//forwardafterturn->goright1

    connectOneModuleToOneFact(0,7,factNet,true);//left -> turntoward
    connectOneModuleToOneFact(1,7,factNet,true);//right -> turntoward
    connectOneModuleToOneFact(7,7,factNet,true);//turn centre -> turntoward
    connectOneModuleToOneFact(8,7,factNet,true);//turn no obj -> turntoward
    connectOneModuleToOneFact(0,8,factNet,true);//left -> towardpyramid
    connectOneModuleToOneFact(1,8,factNet,true);//right -> towardpyramid
    connectOneModuleToOneFact(7,8,factNet,true);
    connectOneModuleToOneFact(8,8,factNet,true);
    connectOneModuleToOneFact(0,9,factNet,true);//left -> towardstalagtite
    connectOneModuleToOneFact(1,9,factNet,true);//right -> towardstalagtite
    connectOneModuleToOneFact(7,9,factNet,true);
    connectOneModuleToOneFact(8,9,factNet,true);

    connectOneModuleToOneFact(10,10,factNet,true); //goto done -> goto
  }

  private int neuronsInAction = 100;
  private void connectOneModuleToOneAction(int moduleNum, int actionNum, 
    CABot1Net actionNet) {

    for (int neuronNum=0; neuronNum<neuronsInModule;neuronNum++) 
      {
      int fromNeuron = neuronNum+(moduleNum*neuronsInModule);
      if (!neurons[fromNeuron].isInhibitory())
        {
        for (int synapseNum=0;synapseNum < 10; synapseNum++) {
          int toNeuron = ((int)(Math.random()*neuronsInAction))+
            (actionNum*neuronsInAction);
          neurons[fromNeuron].addConnection(actionNet.neurons[toNeuron],0.9);
          }
        }
      }
  }

  public void connectModuleToAction(CABot1Net actionNet) {
    connectOneModuleToOneAction(0,0,actionNet);//turnleft
    connectOneModuleToOneAction(1,1,actionNet);//turnright
    connectOneModuleToOneAction(2,2,actionNet);//moveforeward
    connectOneModuleToOneAction(3,3,actionNet);//movebackward
    
    //add goleft and goright of moduleNet to goleft and goright of actionNet
    //connectOneModuleToOneAction(4,4,actionNet);//goleft
    //connectOneModuleToOneAction(5,5,actionNet);//goright
    
    connectOneModuleToOneAction(4,0,actionNet);//goleft1 module-> left action
    connectOneModuleToOneAction(5,2,actionNet);//goforward module --> forward 
    connectOneModuleToOneAction(6,1,actionNet);//goleft1 module-> left action

    connectOneModuleToOneAction(7,4,actionNet);//error turn toward centre obj
    connectOneModuleToOneAction(8,5,actionNet);//error turn toward nilobject
  }
  

  private void connectOneActionToOneModule(int actionNum, int moduleNum, 
    CABot1Net moduleNet) {

    for (int neuronNum=0; neuronNum<neuronsInAction;neuronNum++) 
      {
      int fromNeuron = neuronNum+(actionNum*neuronsInAction);
      if (!neurons[fromNeuron].isInhibitory())
        {
        for (int synapseNum=0;synapseNum < 10; synapseNum++) {
          int toNeuron = ((int)(Math.random()*neuronsInModule))+
            (moduleNum*neuronsInModule);
          neurons[fromNeuron].addConnection(moduleNet.neurons[toNeuron],-0.9);
          }
        }
      }
  }
   
  //action turns off the module that ignited it.
  public void connectActionToModule(CABot1Net moduleNet) {
    connectOneActionToOneModule(0,0,moduleNet);//left left
    connectOneActionToOneModule(1,1,moduleNet);//right right
    connectOneActionToOneModule(2,2,moduleNet);//forward forward
    connectOneActionToOneModule(3,3,moduleNet);//back back
    connectOneActionToOneModule(0,4,moduleNet);//left goleft1
    connectOneActionToOneModule(2,5,moduleNet);//forward to forward after turn
    connectOneActionToOneModule(1,6,moduleNet);//right goright1

    connectOneActionToOneModule(4,7,moduleNet);//turn centre to turn centre
    connectOneActionToOneModule(5,8,moduleNet);//no object to no object
  }

  public void connectV2ToInstance(CABot1Net instanceNet) {
    for (int fromNeuron=0; fromNeuron<getSize();fromNeuron++) 
      {
      //if it is position invariant
      if (fromNeuron%10 == 1)
        for (int synapseNum=0;synapseNum < 10; synapseNum++) {
          int toNeuron = ((int)(Math.random()*instanceNet.getSize()));
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
      CABot1Net net = (CABot1Net)eNum.nextElement();
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

    CABot1.bmpReader.readPicture();
    cPoints = CABot1.bmpReader.getPictureBits(50,patternPoints);
  	
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

    CABot1.bmpReader.readPicture(fileName);
    cPoints = CABot1.bmpReader.getPictureBits(50,patternPoints);
  	
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
    if (getName().compareTo("BaseNet") == 0)
      {
      if (currentWord >= 0) 
        {
        setCurrentPattern(currentWord);
        ((CANTPattern)patterns.get(getCurrentPattern())).
      	  arrange(getNeuronsToStimulate());
        }
      }
      else if (getName().compareTo("StackTopNet") == 0)
        {
        setCurrentPattern(0);
        ((CANTPattern)patterns.get(getCurrentPattern())).arrange(getNeuronsToStimulate());
        }
      else if (getName().compareTo("VisualInputNet") == 0) 
        {
        if (inputFromJPG)
          {
          CABot1Experiment exp = (CABot1Experiment)CABot1.experiment;
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
      else if (getName().compareTo("InstanceNet") == 0) 
        {
        CABot1Experiment exp = (CABot1Experiment)CABot1.experiment;
        if (exp.groundWords)
          setCurrentPattern(getCurrentPattern());
        else
          setCurrentPattern(0);
        }
      else   
        setCurrentPattern(0);
      return;
  }

  public void kludge () {
    System.out.println("CABot 1 kludge ");

    CABot1.experiment.resetTest();
  }
  
  public void measure(int currentStep) {
    System.out.println("measure " + neurons[0].getActivation() + " " + 
      neurons[0].getFired() + " " + 
	  currentStep);
  }
}