import java.util.*;

public class Parse2Net extends CANTNet {
  private int currentWord = 0;
  private int lastWord = 2;
  
  public Parse2Net(){
  }
  
  public Parse2Net(String name,int cols, int rows,int topology){
  	super(name,cols,rows,topology);
	cyclesToStimulatePerRun = 1000;
  }
  
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
	    if ((prep > 0)  && ((prep %360)>240) && ((prep%3) != 0))
	      neurons[i] = new CANTNeuronFastBind(totalNeurons++,this);
	    else
	      neurons[i] = new CANTNeuron(totalNeurons++,this);
	    }
	}
	else super.createNeurons();	
  }
  
  public void readBetweenAllNets() {
    System.out.println("CANT23 parse2 read Between");
	int netsChecked = 0;
    Enumeration enum = CANT23.nets.elements();
    Parse2Net net = (Parse2Net)enum.nextElement();

    Parse2Net  inputNet = net;
    Parse2Net  instanceNet = net;
    Parse2Net  verbNet = net;
    Parse2Net  nounNet = net;
    Parse2Net  otherNet = net;
    Parse2Net  stackTopNet = net;
    Parse2Net  stackNet = net;
    Parse2Net  pushNet = net;
    Parse2Net  popNet = net;
    Parse2Net  ruleNet = net;
    Parse2Net  eraseNet = net;
    Parse2Net  testNet = net;
    Parse2Net  eraseBoundNet = net;
	
    do  {
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
	  else 	
        System.out.println(net.getName() + " missed net in connect all");
      netsChecked++;
	  if (netsChecked < 13) 
        net = (Parse2Net)enum.nextElement();
    }
	while (netsChecked < 13);

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
    testNet.readConnectTo(eraseNet);
    testNet.readConnectTo(verbNet);
    testNet.readConnectTo(nounNet);
    testNet.readConnectTo(otherNet);
    testNet.readConnectTo(instanceNet);
    ruleNet.readConnectTo(testNet);
    ruleNet.readConnectTo(stackNet);
    ruleNet.readConnectTo(inputNet);
    ruleNet.readConnectTo(stackTopNet);
    ruleNet.readConnectTo(verbNet);
    ruleNet.readConnectTo(instanceNet);
    ruleNet.readConnectTo(popNet);	
  }
  
  
  public void runAllOneStep(int CANTStep) {
    //This series of loops is really chaotic, but I needed to
	//get all of the propogation done in each net in step.
	CANT23Parse2.runOneStepStart();
	
    Enumeration enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    enum = CANT23.nets.elements();
	while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
      net.setExternalActivation(CANTStep);
	}
      //net.propogateChange();  
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
      net.spontaneousActivate();
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
      net.setNeuronsFired();
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
      net.setDecay ();
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
      net.spreadActivation();
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
      net.setFatigue();
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
      net.learn();
      if (net.getName().compareTo("StackNet") == 0)
	    net.fastLearn(150,300);
	  else if (net.getName().compareTo("VerbNet") == 0)
	    net.fastLearn(240,480);
	  else if (net.getName().compareTo("InstanceNet") == 0)
	    net.fastLearn(840,960);
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    enum = CANT23.nets.elements();
    while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
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
  	Parse2Net net = new Parse2Net (name,cols,rows,topology);
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
  	//First four are nouns instances
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
  
  private void setPrepTopology(int prepStart) {
  	int fromNeuron;
	int toNeuron;

	for (int prep = 0; prep < 3; prep++) 
	{
  	  //240 normal neurons	
  	  for (int neuron =0; neuron < 240; neuron++) 
	    {
	    fromNeuron = prepStart + (prep*360) + neuron;
	    for (int synapse = 0 ; synapse < 10; synapse++) 
	      {
	      if (!neurons[fromNeuron].isInhibitory()) 
		    {
		    toNeuron = ((neuron*2)+ synapse) % 300;
		    toNeuron += prepStart + (prep*360) ;
		    addConnection(fromNeuron,toNeuron,1.0);
		    }
	      }
	    }
      //60 interleaved fastbind and normal to bind the slot
	  for (int neuron =0; neuron < 60; neuron++) 
	    {
	    fromNeuron = prepStart + (prep*360) + neuron + 240;;
	    for (int synapse = 0 ; synapse < 25; synapse++) 
	      {
          toNeuron = ((neuron*2)+ synapse) % 120;
		  toNeuron += prepStart+(prep*360)+240;
		  addConnection(fromNeuron,toNeuron,0.25);
	      toNeuron = (((neuron*2)+ synapse) % 240) + prepStart + (prep*360);
	      addConnection(fromNeuron,toNeuron,0.25);
	      }
	    }
      //60 interleaved fastbind and normal for the slot
	  for (int neuron =0; neuron < 60; neuron++) 
	    {
	    fromNeuron = prepStart + (prep*360) + neuron + 300;;
	    for (int synapse = 0 ; synapse < 20; synapse++) 
	      {
	      toNeuron = ((neuron*2)+ synapse) % 60;
		  toNeuron += prepStart + (prep*360) + 300 ;
		  addConnection(fromNeuron,toNeuron,1.0);
	      }
	    }
	  //allow the slot to be bound to the noun instances
	  for (int neuron =0; neuron < 60; neuron++) 
	    {
	    fromNeuron = prepStart + (prep*360) + neuron + 300;;
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

  private void setEraseBoundTopology() {
    for (int neuron=0; neuron<getSize(); neuron++)
	  {
	  if (!neurons[neuron].isInhibitory())
	    for (int synapse = 0; synapse < 20; synapse++)
		  addConnection(neuron,(int)(Math.random()*100),1.0);
	  }
  }

  public void initializeNeurons() {
    //set up topologies.
    createNeurons();
															  
    if (topology == 1){
      System.out.println("input parse topology ");
	  setInputTopology(150,0.45);
    }
    else if (topology == 2){
      System.out.println("noun parse topology ");
      setNounTopology(300);
    }
    else if (topology == 3){
      System.out.println("other parse topology");
    }
    else if (topology == 4){
      System.out.println("stack top parse topology");
      setStackTopTopology(300);
    }
    else if (topology == 5){
      System.out.println("parse  rule topology");
      setParseRuleTopology(300);
    }
    else if (topology == 6){
      System.out.println("parse stack topology");
      setStackTopology(1800);
      return;
    }
    else if (topology == 7){
      System.out.println("test topology");
      setTestTopology();
    }
    else if (topology == 8){
      System.out.println("push topology");  //and pop
      setPushTopology();
    }
    else if (topology == 9){
      System.out.println("verb topology ");
      setVerbTopology();
      return;
    }
    else if (topology == 10){
      System.out.println("erase  topology");
      setEraseTopology();
    }
    else if (topology == 11){
      System.out.println("instance topology ");
      setInstanceTopology(150,0.45);
    }
    else if (topology == 12){
      System.out.println("erase bound topology ");
      setEraseBoundTopology();
    }
    else if (topology == 13){
      System.out.println("other word parse topology ");
      setOtherWordTopology(300);
    }
    else System.out.println("bad toppology specified "+ topology);
  }
  
  
  //**********Connect Nets to Each Other***********************************
  private void connectInputWordToNoun(int inputStart, int nounStart, Parse2Net nounNet) {
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

  public void connectInputToNoun(Parse2Net nounNet) {
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
  
  public void connectInputToOther(Parse2Net otherNet) {
  	//might have to make a connectInputWordToOther later
    connectInputWordToNoun(2*150,0,otherNet); //period to O1
    connectInputWordToNoun(14*150,1*300,otherNet); //the to O2
    connectInputWordToNoun(15*150,2*300,otherNet); //toward to O3
  }

  private void connectInputWordToVerb(int inputStart, int verbStart, Parse2Net verbNet) {
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
  
  private void connectOneNounToOneInstance(int nounStart, int instanceStart,Parse2Net instanceNet) 
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

  public void connectNounToInstance(Parse2Net instanceNet) {
    connectOneNounToOneInstance(0,0,instanceNet); //Follow to I1
    connectOneNounToOneInstance(1*300,1*150,instanceNet); //Left to I2
    connectOneNounToOneInstance(5*300,2*150,instanceNet); //Pyramid to I3
    connectOneNounToOneInstance(8*300,3*150,instanceNet); //It to I4
    connectOneNounToOneInstance(2*300,4*150,instanceNet); //Right to I5
    connectOneNounToOneInstance(3*300,5*150,instanceNet); //Forward to I6
    connectOneNounToOneInstance(4*300,6*150,instanceNet); //Backward to I7
    connectOneNounToOneInstance(6*300,7*150,instanceNet); //Stalagtite to I8
    connectOneNounToOneInstance(7*300,8*150,instanceNet); //Door to I9
  }


  public void connectInputToVerb(Parse2Net verbNet) {
     connectInputWordToVerb(0,0,verbNet); //Follow to V1
     connectInputWordToVerb(5*150,1*480,verbNet); //Move to V2
     connectInputWordToVerb(4*150,2*480,verbNet); //Turn to V3
     connectInputWordToVerb(3*150,3*480,verbNet); //Go to V4
  }
  
  //rule activation functions
  private void connectVerbToV_NPPLoc(Parse2Net ruleNet) {
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
 
 private void connectPrepToVerb_PPLoc(Parse2Net ruleNet) {
 	for (int otherWordNeuron = 600; otherWordNeuron < 840; otherWordNeuron ++) 
 	{
 	  for (int synapse = 0; synapse < 15; synapse++)
 	    {
 	    int ruleOffset = ((otherWordNeuron+synapse) % 300)+1200;;
 	    if (!neurons[otherWordNeuron].isInhibitory())
 	      neurons[otherWordNeuron].addConnection(ruleNet.neurons[ruleOffset],0.010);
 	    }
 	}
 }

 private void connectPPToVerb_PPLoc(Parse2Net ruleNet) {
    for (int prep =0; prep < 3; prep++) 
      {
	  for (int instanceNeuron = 0; instanceNeuron < 60; instanceNeuron +=3) 
        {
		int fromNeuron = prepStart+(prep*360)+300+instanceNeuron;
        if (!neurons[fromNeuron].isInhibitory())
		  {
          for (int synapse = 0; synapse < 75; synapse++)
            {
            int ruleOffset = (((instanceNeuron*5)+synapse) % 300)+1200;
            neurons[fromNeuron].addConnection(ruleNet.neurons[ruleOffset],0.03);//.06 
            }
          }
        }
      }
  }

 private void connectPrepToPP_Noun(Parse2Net ruleNet) {
    for (int otherWordNeuron = 600; otherWordNeuron < 840; otherWordNeuron ++) 
    {
      for (int synapse = 0; synapse < 15; synapse++)
        {
        int ruleOffset = ((otherWordNeuron+synapse) % 300)+900;;
        if (!neurons[otherWordNeuron].isInhibitory())
          neurons[otherWordNeuron].addConnection(ruleNet.neurons[ruleOffset],0.020);//.018
        }
    }
  }

  private void connectNounToPP_Noun(Parse2Net ruleNet) {
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

  private void connectPrepToPrep_Det(Parse2Net ruleNet) {
    for (int otherWordNeuron = 600; otherWordNeuron < 840; otherWordNeuron ++) 
    {
      for (int synapse = 0; synapse < 15; synapse++)
        {
        int ruleOffset = ((otherWordNeuron+synapse) % 300)+600;;
        if (!neurons[otherWordNeuron].isInhibitory())
          neurons[otherWordNeuron].addConnection(ruleNet.neurons[ruleOffset],0.020);//.018
        }
    }
  }

  private void connectDetToPrep_Det(Parse2Net ruleNet) {
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

  private void connectPeriodToV_Period(Parse2Net ruleNet) {
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

  private void connectVerbToV_period(Parse2Net ruleNet) {
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

  private void connectNounToV_Nobj(Parse2Net ruleNet) {
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

  private void connectVerbToV_Nobj(Parse2Net ruleNet) {
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

  public void connectNounToRule(Parse2Net ruleNet) {
    connectNounToPP_Noun(ruleNet); 
    connectNounToV_Nobj(ruleNet); 
  }

  public void connectOtherToRule(Parse2Net ruleNet) {
  	connectPeriodToV_Period(ruleNet);
  	connectPrepToPrep_Det(ruleNet);
  	connectDetToPrep_Det(ruleNet);
  	connectPrepToPP_Noun(ruleNet);
	connectPrepToVerb_PPLoc(ruleNet);
  }
  
  public void connectInstanceToRule(Parse2Net ruleNet) {
    connectPPToVerb_PPLoc(ruleNet);
  }
  
  private void connectPrepToPrepInstance(Parse2Net instanceNet, int otherStart, int prepInstance) {
  	for (int neuronNum = otherStart; neuronNum < otherStart+300; neuronNum++) 
  	  {
  	  for (int synapse = 0; synapse < 6; synapse++)
  	    {
  	    int instanceOffset = (int)(Math.random()*300);
  	    instanceOffset += prepStart+(prepInstance*360);
  	    if (!neurons[neuronNum].isInhibitory())
  	      neurons[neuronNum].addConnection(instanceNet.neurons[instanceOffset],0.2);
  	    }
  	  }
  	}
	
  public void connectOtherToInstance(Parse2Net instanceNet) {
  	connectPrepToPrepInstance(instanceNet,600,0);  //toward to the first prep
  }

  public void connectVerbToRule(Parse2Net ruleNet) {
     connectVerbToV_Nobj(ruleNet); 
     connectVerbToV_period(ruleNet); 
     connectVerbToV_NPPLoc(ruleNet); 
  }

  public void connectVerbToInstance(Parse2Net instanceNet) {
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
  public void connectRuleToTest(Parse2Net testNet) {
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
  }
  
  //activate the first stack item
  public void connectRuleToStack(Parse2Net stackNet) {
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
  public void connectRuleToStackTop(Parse2Net stackTopNet) {
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
  public void connectRuleToInput(Parse2Net inputNet) {
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
  
  public void connectRuleToVerb(Parse2Net verbNet) {
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
  public void connectRuleToInstance(Parse2Net oWordNet) {
    for (int r4Neuron = 900; r4Neuron < 1200; r4Neuron ++) 
      {
	  for (int prep = 0; prep < 3 ; prep++) 
	    {
        for (int synapse = 0; synapse < 3; synapse++)
          {
		  int toNeuron = (r4Neuron+synapse)%60;
		  toNeuron += prepStart + (prep*360) + 300;
          if (!neurons[r4Neuron].isInhibitory())
            neurons[r4Neuron].addConnection(oWordNet.neurons[toNeuron],.5);  //arbitrary
          }
	    }
      }
  }

  public void connectRuleToPop(Parse2Net popNet) {
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

  public void connectInputToPush(Parse2Net pushNet) {
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
  public void connectStackTopToEraseBound(Parse2Net eraseBoundNet) {
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
  private void connectStackTopZeroToPush(Parse2Net pushNet) {
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
  
  public void connectStackTopToPush(Parse2Net pushNet) {
    connectStackTopZeroToPush(pushNet);
  }


  //stacktop elements enervate the appropriate stack elements
  //stacktop 0 doesn't enervate any stack, 1 does the first etc, 2 the 2nd etc.
  public void connectStackTopToStack(Parse2Net stackNet) {
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
  public void connectPopToStackTop(Parse2Net stackTopNet) {
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
  
  public void connectPopToRule(Parse2Net ruleNet) {
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

  public void connectPopToVerb(Parse2Net verbNet) {
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
  
  public void connectPopToNoun(Parse2Net nounNet) {
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

  public void connectPopToOther(Parse2Net otherNet) {
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

  public void connectPopToStack(Parse2Net stackNet) {
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

  public void connectPopToErase(Parse2Net eraseNet) {
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
  public void connectPushToStackTop(Parse2Net stackTopNet) {
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

  public void connectPushToStack(Parse2Net stackNet) {
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
  public void connectPushToNounNet(Parse2Net nounNet) {
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

  public void connectPushToOtherNet(Parse2Net otherNet) {
    for (int pushNeuron = wordStart; pushNeuron < wordFinish; pushNeuron ++) 
      {
      for (int otherWord = 0; otherWord < 3; otherWord++) 
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

  public void connectPushToVerbNet(Parse2Net verbNet) {
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

  
  public void connectPushToTest(Parse2Net testNet) {
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

  public void connectEraseToPop(Parse2Net popNet) {
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
  
  public void connectErBoundToStack(Parse2Net stackNet){
    for (int neuron = 0; neuron < 100; neuron ++) 
      {
        for (int synapse = 0; synapse < 20; synapse++)
        {
        if (neurons[neuron].isInhibitory())
          {
          int toNeuron = (((neuron*3)+synapse)%300)+300;
          neurons[neuron].addConnection(stackNet.neurons[toNeuron],-3.0);
          }
        }
      }
  }

  private void connectEraseToBoundOnce(Parse2Net eraseBoundNet,int eraseStart) {
    for (int eraseNeuron = eraseStart; eraseNeuron < eraseStart+200; eraseNeuron ++) 
      {
      for (int synapse = 0; synapse < 10; synapse++)
        {
        if (!neurons[eraseNeuron].isInhibitory())
  	      {
  	      int toNeuron = (eraseNeuron+synapse)%100;
          neurons[eraseNeuron].addConnection(eraseBoundNet.neurons[toNeuron],.3);
  	      }
        }
      }
  }

  public void connectEraseToBound(Parse2Net eraseBoundNet) {
  	connectEraseToBoundOnce(eraseBoundNet,600);
  	connectEraseToBoundOnce(eraseBoundNet,2000);
  	connectEraseToBoundOnce(eraseBoundNet,3200);
  }

  //This turns stack s1 and s2 to keep their fastbind neurons up.
  public void connectEraseToStack(Parse2Net stackNet) {
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
  
  public void connectEraseToVerb(Parse2Net verbNet) {
    connectTestToShutWord(400,verbNet);
    connectTestToShutWord(800,verbNet);
    connectTestToShutWord(1800,verbNet);
    connectTestToShutWord(2200,verbNet);
    connectTestToShutWord(3000,verbNet);
    connectTestToShutWord(3400,verbNet);
  }

  public void connectEraseToNoun(Parse2Net nounNet) {
    connectTestToShutWord(400,nounNet);
    connectTestToShutWord(800,nounNet);
    connectTestToShutWord(1800,nounNet);
    connectTestToShutWord(2200,nounNet);
    connectTestToShutWord(3000,nounNet);
    connectTestToShutWord(3400,nounNet);
  }
  
  public void connectEraseToOther(Parse2Net otherNet) {
    connectTestToShutWord(400,otherNet);
    connectTestToShutWord(800,otherNet);
    connectTestToShutWord(1800,otherNet);
    connectTestToShutWord(2200,otherNet);
    connectTestToShutWord(3000,otherNet);
    connectTestToShutWord(3400,otherNet);
  }
  
  public void connectEraseToRule(Parse2Net ruleNet) {
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

  public void connectEraseToTest(Parse2Net testNet) {
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
  public void connectTestToPush(Parse2Net pushNet) {
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

  private void connectTestToShutWord(int testStart,Parse2Net shutNet) {
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

  public void connectTestToOther(Parse2Net otherNet) {
    connectTestToShutWord(0,otherNet);
    connectTestToShutWord(200,otherNet);
    connectTestToShutWord(1200,otherNet);
    connectTestToShutWord(2200,otherNet);
    connectTestToShutWord(3200,otherNet);
    connectTestToShutWord(3400,otherNet);
  }

  public void connectTestToVerb(Parse2Net verbNet) {
    connectTestToShutWord(0,verbNet);
    connectTestToShutWord(200,verbNet);
    connectTestToShutWord(1200,verbNet);
    connectTestToShutWord(2200,verbNet);
    connectTestToShutWord(3200,verbNet);
    connectTestToShutWord(3400,verbNet);
  }

  public void connectTestToNoun(Parse2Net nounNet) {
    connectTestToShutWord(0,nounNet);
    connectTestToShutWord(200,nounNet);
    connectTestToShutWord(1200,nounNet);
    connectTestToShutWord(2200,nounNet);
    connectTestToShutWord(3200,nounNet);
    connectTestToShutWord(3400,nounNet);
  }

  public void connectTestToInstance(Parse2Net instanceNet) {
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

  private void connectTestToShut(int testStart,Parse2Net shutNet) {
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

  private void connectTestToStackGo(int testStart, int stackEl, Parse2Net stackNet) {
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

  public void connectTestToStack(Parse2Net stackNet) {
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

  public void connectTestToErase(Parse2Net eraseNet) {
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

  public void connectStackToVerb(Parse2Net verbNet) {
    for (int neuronNum=0; neuronNum<size();neuronNum++) 
	  {
	  if ((neuronNum % 300) > 150)
        for (int synapseNum=0;synapseNum < 100; synapseNum++) 
          ((CANTNeuronFastBind)neurons[neuronNum]).addConnection(
	        verbNet.neurons[(int)(Math.random()*verbNet.getSize())],0.01); //1200
//System.out.println(neuronNum + " " + ((CANTNeuronFastBind)neurons[neuronNum]).getFastSynapses());
	  }
  }

  public void connectStackToOther(Parse2Net otherNet) {
    for (int neuronNum=0; neuronNum<size();neuronNum++) 
    {
    if ((neuronNum % 300) > 150)
        for (int synapseNum=0;synapseNum < 80; synapseNum++) 
          ((CANTNeuronFastBind)neurons[neuronNum]).addConnection(
          otherNet.neurons[(int)(Math.random()*900)],0.01);
    }
  }

  public void connectStackToNoun(Parse2Net nounNet) {
    for (int neuronNum=0; neuronNum<size();neuronNum++) 
    {
    if ((neuronNum % 300) > 150)
        for (int synapseNum=0;synapseNum < 100; synapseNum++) 
          ((CANTNeuronFastBind)neurons[neuronNum]).addConnection(
          nounNet.neurons[(int)(Math.random()*nounNet.getSize())],0.01);
//System.out.println(neuronNum + " " + ((CANTNeuronFastBind)neurons[neuronNum]).getFastSynapses());
    }
  }

  /**** stuff other than connectivity**/
  //reset the word networks for the next word.  This might be cheating.
  private void resetInputNet () {
  	Enumeration enum = CANT23.nets.elements();
	
    while (enum.hasMoreElements()) {
      Parse2Net net = (Parse2Net)enum.nextElement();
	  if ((net.getName().compareTo("BaseNet") ==0))
	    net.clear();
    }  
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
	else   
  	  setCurrentPattern(0);
  	return;
  }

  public void kludge () {
  	System.out.println("parse  2 kludge ");
/*	Parse2Experiment experiment =  (Parse2Experiment)CANT23Parse2.experiment;
	experiment.printSymbolicResult();
	experiment.clearFastBindNeurons();*/

  	Parse2Net ruleNet;
	
	//clear all nets except input and stacktop
	Enumeration enum = CANT23.nets.elements();
	ruleNet= (Parse2Net)enum.nextElement();
	enum = CANT23.nets.elements();
	while (enum.hasMoreElements()) {
	  CANTNet net = (CANTNet)enum.nextElement();
	  if (net.getName().compareTo("RuleNet") == 0)
	  	ruleNet = (Parse2Net) net;
	}

    // print rule activation
    double total=0.0;	
	double high = 0.0;
	for (int i= 1200; i < 1500; i++) 
	  {
	  if (high < ruleNet.neurons[i].getActivation())
	  	high = ruleNet.neurons[i].getActivation();
	  total += ruleNet.neurons[i].getActivation();
	  }
    System.out.println("average " + total/300);
    System.out.println("high " + high);


  	total=0.0;	
  	high = 0.0;
  	for (int i= 300; i < 600; i++) 
  	  {
  	  if (high < ruleNet.neurons[i].getActivation())
  	  	high = ruleNet.neurons[i].getActivation();
  	  total += ruleNet.neurons[i].getActivation();
  	  }
  	System.out.println("average " + total/300);
  	System.out.println("high " + high);
  }
  
  public void measure(int currentStep) {
    System.out.println("measure " + neurons[0].getActivation() + " " + 
      neurons[0].getFired() + " " + 
	  currentStep);
  }
}