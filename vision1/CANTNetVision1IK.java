public class CANTNetVision1 extends CANTNet {
  public CANTNetVision1(){
  }

  public CANTNetVision1(String name,int cols, int rows,int topology){
    super(name,cols,rows,topology);
  }

  public CANTNet getNewNet(String name,int cols, int rows,int topology){
    CANTNetVision1 net = new CANTNetVision1 (name,cols,rows,topology);
    return (net);
    } 
    
  protected void addOneConnection(int fromRow, int fromCol, CANTNeuron toNeuron,
                                double weight) {
    if ((fromRow < 0) ||(fromRow >= 30) ||(fromCol < 0) || (fromCol >= 30))
       return;
                                    
    int inputNeuron = fromRow * getCols() + fromCol;
    
    neurons[inputNeuron].addConnection(toNeuron,weight);
  }

  private void connectInputTo3x3(CANTNetVision1 retinaNet, int start,
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


  private void connectInputTo6x6(CANTNetVision1 retinaNet, int start,
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


  private void connectInputTo9x9(CANTNetVision1 retinaNet, int start,
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
    
  public void connectInputToRetina(CANTNetVision1 retinaNet) {
    // ISK *******
    connectInputTo3x3(retinaNet,0,7.0,-0.2);
    //ISK ********
    connectInputTo3x3(retinaNet,size(),-0.8,0.25);  
    connectInputTo6x6(retinaNet,size()*2,2.0,-0.2); 
    connectInputTo6x6(retinaNet,size()*3,-0.6,0.2); 
    connectInputTo9x9(retinaNet,size()*4,1.0,0.15); 
    connectInputTo9x9(retinaNet,size()*5,-0.5,0.1); 
  }
  
  
  //input was translated to retina 3x3 node to node
  private void connectRetinaToHorizontal(CANTNetVision1 V1Net, double onOff33Val, double ignore) 
  {
    int row;
    int column;
    for (int inputNeuron = 0; inputNeuron < 900; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      addOneConnection(row,column-3,V1Net.neurons[inputNeuron],onOff33Val);
      addOneConnection(row,column-2,V1Net.neurons[inputNeuron],onOff33Val);
      addOneConnection(row,column-1,V1Net.neurons[inputNeuron],onOff33Val);
      addOneConnection(row,column,V1Net.neurons[inputNeuron],onOff33Val);
      addOneConnection(row,column+1,V1Net.neurons[inputNeuron],onOff33Val);
      addOneConnection(row,column+2,V1Net.neurons[inputNeuron],onOff33Val);
      addOneConnection(row,column+3,V1Net.neurons[inputNeuron],onOff33Val);
      }
  }

  public void connectRetinaToV1(CANTNetVision1 V1Net) {
      //ISK ******
//    connectRetinaToHorizontal(V1Net,0.8,-0.2);
    int row;
    int column;
    
        for (int inputNeuron = 0; inputNeuron < 900; inputNeuron++) 
        {
            row = inputNeuron/30;
            column = inputNeuron%30;
            double onVal = 5.0;
            double offVal = -0.625;
            
            addOneConnection(row,column,V1Net.neurons[inputNeuron],onVal);
        
            addOneConnection(row-1,column-1,V1Net.neurons[inputNeuron],offVal);
            addOneConnection(row-1,column,V1Net.neurons[inputNeuron],offVal);
            addOneConnection(row-1,column+1,V1Net.neurons[inputNeuron],offVal);
            addOneConnection(row,column-1,V1Net.neurons[inputNeuron],offVal);
            addOneConnection(row,column+1,V1Net.neurons[inputNeuron],offVal);
            addOneConnection(row+1,column-1,V1Net.neurons[inputNeuron],offVal);
            addOneConnection(row+1,column,V1Net.neurons[inputNeuron],offVal);
            addOneConnection(row+1,column+1,V1Net.neurons[inputNeuron],offVal);
        }    
      }
      // ISK*******

  private int getNeuronGroup (int neuron)
    {
    int neuronGroup=0;
    neuronGroup = (neuron/200);
    return (neuronGroup);  
    }  
  
  public double getInputInternalWeight(int fromNeuron, int toNeuron) {
    int connectionType = 0;  //inntra 0, inter 1, 
    int fromNeuronGroup = 0;
    int toNeuronGroup = 0;
    boolean inhib;
    double intraWeight = 2.0 - Math.random();
    double intraInhib =  -0.1;
    double interWeight = 0.01;
    double interInhib = -0.1;

    inhib = neurons[fromNeuron].isInhibitory();

    fromNeuronGroup = getNeuronGroup(fromNeuron);
    toNeuronGroup = getNeuronGroup(toNeuron);
   
    if (fromNeuronGroup == toNeuronGroup) connectionType = 1;

    if (connectionType == 1)
      if (inhib)
        return (intraInhib);
      else
        return (intraWeight);   
    else //if (connectionType == 0) 
      if (inhib)
        return (interInhib);
      else
        return (interWeight);       
  }
  

  public double getInternalRulesWeight(int fromNeuron, int toNeuron) {
    boolean inhib = neurons[fromNeuron].isInhibitory();
    int internalGroup = getNeuronGroup(fromNeuron);
    int ruleGroup = getNeuronGroup(toNeuron);
    boolean connect = false;
    if (ruleGroup == 11)
      connect = false;
    else if ((internalGroup == 0) || (internalGroup == 12))
      connect = true;
    else if (internalGroup == (ruleGroup +1))
      connect = true; 
  
    if (inhib && connect)
      return (-0.01);
    else if (!inhib && connect)
      return (0.36);    
    else if (!inhib && !connect)
      return (0.01);    
    else // if (inhib && !connect)
      return (-3.6);    
  }

  public double getRulesInternalWeight(int fromNeuron, int toNeuron) {
    double ignoreWeight = 0.01;
    double ignoreInhib =  -0.01;
    double exciteWeight = 2.8;
    double exciteInhib = -0.01;
    double surpressWeight = 0.01;
    double surpressInhib = -4.0;
    boolean inhib = neurons[fromNeuron].isInhibitory();
    int internalGroup = getNeuronGroup(toNeuron);
    int ruleGroup = getNeuronGroup(fromNeuron);
    int connectionType = 0; //0 ignore, 1 supress, 2 excite
    
    if ((internalGroup == 0) || (internalGroup == 12))
      connectionType = 1;
    else if (internalGroup == (ruleGroup +1))
      connectionType = 1; 
    //what about the last rule  
    else if (internalGroup == (ruleGroup +2))
      connectionType = 2; 
  
    if (connectionType == 1)
      if (inhib)
        return (surpressInhib);
      else
        return (surpressWeight);    
    else if (connectionType == 2)   
      if (inhib)
        return (exciteInhib);
      else
        return (exciteWeight);  
    else //if (connectionType == 0) 
      if (inhib)
        return (ignoreInhib);
      else
        return (ignoreWeight);  
  }

  private double getRulesWeight(int fromNeuron, int toNeuron) {
  int connectionType = 0;  //intra 1, exclusive 0, 
  int fromNeuronGroup = 0;
  int toNeuronGroup = 0;
  boolean inhib;
  double intraWeight = 1.7 - Math.random();
  double intraInhib =  -0.01;
  double exclusiveWeight = 0.01;
  double exclusiveInhib = -4.0;

  inhib = neurons[fromNeuron].isInhibitory();

  fromNeuronGroup = getNeuronGroup(fromNeuron);
  toNeuronGroup = getNeuronGroup(toNeuron);
   
  if (fromNeuronGroup == toNeuronGroup) connectionType = 1;
  else  connectionType = 0;
  
  if (connectionType == 1)
    if (inhib)
        return (intraInhib);
    else
      return (intraWeight); 
  else //if (connectionType == 0)   
    if (inhib)
      return (exclusiveInhib);
    else
      return (exclusiveWeight); 
  }

  
  public double getResetInternalWeight(int fromNeuron, int toNeuron) {
    double ignoreWeight = 0.01;
    double ignoreInhib =  -0.01;
    double exciteWeight = 0.5;
    double exciteInhib = -0.01;
    boolean inhib = neurons[fromNeuron].isInhibitory();
    int internalGroup = getNeuronGroup(toNeuron);
    int connectionType = 0; //0 ignore, 1 excite, 
    
  if ((internalGroup == 0) || (internalGroup == 12))
      connectionType = 1;
  
    if (connectionType == 1)
      if (inhib)
        return (exciteInhib);
      else
        return (exciteWeight);  
    else //if (connectionType == 0) 
      if (inhib)
        return (ignoreInhib);
      else
        return (ignoreWeight);  
  }

  public double getDefaultWeight(int fromNeuron) {
    if(neurons[fromNeuron].isInhibitory())
      return (-0.02);
    else
      return (0.02);        
  }  

  public double getWeight(int fromNeuron, int toNeuron) {
    int connectionType = 0;  //inntra 0, inter 1, 
    int fromNeuronGroup = 0;
    int toNeuronGroup = 0;
    boolean inhib;
    double intraWeight = 1.5  - Math.random();  //1.5
    double intraInhib =  -0.01;// - Math.random();
    double interWeight = 0.01;
    double interInhib = -0.12; //-.2 on 13/10

    inhib = neurons[fromNeuron].isInhibitory();

    fromNeuronGroup = getNeuronGroup(fromNeuron);
    toNeuronGroup = getNeuronGroup(toNeuron);
   
    if (fromNeuronGroup == toNeuronGroup) connectionType = 1;

    if (connectionType == 1)
      if (inhib)
        return (intraInhib);
      else
        return (intraWeight);   
    else //if (connectionType == 0) 
      if (inhib)
        return (interInhib);
      else
        return (interWeight);       
  }


  //random connections, but with specific weights calculated in getWeight
  private void setConnections(int startNeuron, int endNeuron) {
    int toNeuron;
    double weight;
    //For Each Neuron
    for (int fromNeuron = startNeuron; fromNeuron<endNeuron; fromNeuron++) 
    {
      int cSynapse = 0; 
      int totalIntraSynapses = 150;
      if (getName().compareTo("bind") == 0)
        totalIntraSynapses = 50;
      else if ((getName().compareTo("finish") == 0) ||
               (getName().compareTo("reset") == 0))
        totalIntraSynapses = 30;
//    else if (getName().compareTo("done") == 0)
//      totalIntraSynapses = 50;
      while (cSynapse < totalIntraSynapses)
      {
         toNeuron = (int)(Math.random()*endNeuron);
         if (getName().compareTo("rules") == 0)
           weight = getRulesWeight(fromNeuron,toNeuron);
         else if 
             (getName().compareTo("bind") == 0)
           weight = getDefaultWeight(fromNeuron);
         else   
           weight = getWeight(fromNeuron,toNeuron);
         neurons[fromNeuron].addConnection(neurons[toNeuron],weight);
         cSynapse = neurons[fromNeuron].currentSynapses;
      }
    }
  }
  
  public void initializeNeurons() {
    neurons = new CANTNeuron[cols*rows];
    for(int i=0;i< cols*rows;i++)
      neurons[i] = new CANTNeuron(totalNeurons++,this);
    if (topology == 1){
      System.out.println("input topology no internal net connections ");
    }
    else if (topology == 2){
      System.out.println("other vision topology");
//      setConnections(0,size());
    }
    else System.out.println("bad toppology specified "+ topology);
  }


  public void measure(int currentStep) {
    System.out.println("measure" + currentStep);
  }

}



