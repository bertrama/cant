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
	
  private void addOneConnection(int fromRow, int fromCol, CANTNeuron toNeuron,
                                double weight) {
    if ((fromRow < 0) ||(fromRow >= 30) ||(fromCol < 0) || (fromCol >= 30))
	   return;
	   								
    int inputNeuron = fromRow * getCols() + fromCol;
	
	neurons[inputNeuron].addConnection(toNeuron,weight);
  }

  private void addOneRetinaConnection(int fromRow, int fromCol, CANTNeuron toNeuron,
                                double weight) {
    if ((fromRow < 0) ||(fromRow >= 180) ||(fromCol < 0) || (fromCol >= 30))
     return;
     								
    int inputNeuron = fromRow * getCols() + fromCol;
  
    neurons[inputNeuron].addConnection(toNeuron,weight);
  }

  private void addOneV1Connection(int fromRow, int fromCol, CANTNeuron toNeuron,
                                double weight) {
    if ((fromRow < 0) ||(fromRow >= 180) ||(fromCol < 0) || (fromCol >= 30))
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
  	connectInputTo3x3(retinaNet,0,7.1,-1.25);	
    connectInputTo3x3(retinaNet,size(),-7.1,0.89);	
  	connectInputTo6x6(retinaNet,size()*2,1.8,-0.22);	
  	connectInputTo6x6(retinaNet,size()*3,-1.8,0.22);	
  	connectInputTo9x9(retinaNet,size()*4,0.79,-0.021);	
    connectInputTo9x9(retinaNet,size()*5,-0.79,0.099);	
  }
  
  
  //input was translated to retina 3x3 node to node
  private void connectRetinaToHorizontal(CANTNetVision1 V1Net, 
    double onOff33Val, double ignore,
	double onOff66Val, double ignore2
	) 
  {
    int row;
    int column;
    for (int inputNeuron = 0; inputNeuron < 900; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      addOneRetinaConnection(row,column-1,V1Net.neurons[inputNeuron],onOff33Val);
      addOneRetinaConnection(row,column,V1Net.neurons[inputNeuron],onOff33Val);
      addOneRetinaConnection(row,column+1,V1Net.neurons[inputNeuron],onOff33Val);
	  /*using 3x3 for lines
      addOneRetinaConnection(row+60,column-2,V1Net.neurons[inputNeuron],onOff66Val);
      addOneRetinaConnection(row+60,column-1,V1Net.neurons[inputNeuron],onOff66Val);
      addOneRetinaConnection(row+60,column,V1Net.neurons[inputNeuron],onOff66Val);
      addOneRetinaConnection(row+60,column+1,V1Net.neurons[inputNeuron],onOff66Val);
      addOneRetinaConnection(row+60,column+2,V1Net.neurons[inputNeuron],onOff66Val);
      addOneRetinaConnection(row+60,column+3,V1Net.neurons[inputNeuron],onOff66Val);
	  */
      }
  }

  private void connectRetinaToSlash(CANTNetVision1 V1Net, double onOff33Val) 
  {
    int row;
    int column;
    for (int inputNeuron = 0; inputNeuron < 900; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      addOneRetinaConnection(row-1,column+1,V1Net.neurons[inputNeuron+900],onOff33Val);
      addOneRetinaConnection(row,column,V1Net.neurons[inputNeuron+900],onOff33Val);
      addOneRetinaConnection(row+1,column,V1Net.neurons[inputNeuron+900],onOff33Val);
      }
  }

  private void connectRetinaToBackSlash(CANTNetVision1 V1Net, double onOff33Val) 
  {
    int row;
    int column;
    for (int inputNeuron = 0; inputNeuron < 900; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      addOneRetinaConnection(row-1,column-1,V1Net.neurons[inputNeuron+1800],onOff33Val);
      addOneRetinaConnection(row,column,V1Net.neurons[inputNeuron+1800],onOff33Val);
      addOneRetinaConnection(row+1,column,V1Net.neurons[inputNeuron+1800],onOff33Val);
      }
  }

  private void connectRetinaToAndAngle(CANTNetVision1 V1Net, double onOff66Val) 
  {
    int row;
    int column;
    for (int inputNeuron = 1800; inputNeuron < 2700; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      addOneRetinaConnection(row,column,V1Net.neurons[inputNeuron+900],onOff66Val);
      addOneRetinaConnection(row,column+1,V1Net.neurons[inputNeuron+900],onOff66Val);
      addOneRetinaConnection(row+1,column-1,V1Net.neurons[inputNeuron+900],onOff66Val);
      addOneRetinaConnection(row+1,column+2,V1Net.neurons[inputNeuron+900],onOff66Val);
      }
  }

  private void connectRetinaToLessThanAngle(CANTNetVision1 V1Net, double onOff66Val) 
  {
    int row;
    int column;
    for (int inputNeuron = 1800; inputNeuron < 2700; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      addOneRetinaConnection(row,column,V1Net.neurons[inputNeuron+1800],onOff66Val);
      addOneRetinaConnection(row+1,column+1,V1Net.neurons[inputNeuron+1800],onOff66Val);
      }
  }

  private void connectRetinaToGreaterThanAngle(CANTNetVision1 V1Net, double onOff66Val) 
  {
    int row;
    int column;
    for (int inputNeuron = 1800; inputNeuron < 2700; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      addOneRetinaConnection(row,column,V1Net.neurons[inputNeuron+2700],onOff66Val);
      addOneRetinaConnection(row-1,column+1,V1Net.neurons[inputNeuron+2700],onOff66Val);
      }
  }

  public void connectRetinaToV1(CANTNetVision1 V1Net) {
    connectRetinaToHorizontal(V1Net,1.4,-0.2,0.5,-0.2);	
    connectRetinaToSlash(V1Net,1.4);	
    connectRetinaToBackSlash(V1Net,1.4);	
    connectRetinaToAndAngle(V1Net,1.5);	
    connectRetinaToLessThanAngle(V1Net,3.0);	
    connectRetinaToGreaterThanAngle(V1Net,3.0);	
  }

  private void connectRetinaToTriangle(CANTNetVision1 V2Net, double offOn99Val) 
  {
    int row;
    int column;
    for (int inputNeuron = 4500; inputNeuron < 5400; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      addOneRetinaConnection(row,column,V2Net.neurons[65],offOn99Val);
      addOneRetinaConnection(row+1,column,V2Net.neurons[65],offOn99Val);
      }
  }

  private void connectV1ToTriangle(CANTNetVision1 V2Net) 
  {
    int row;
    int column;
    for (int inputNeuron = 0; inputNeuron < 900; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      addOneV1Connection(row,column,V2Net.neurons[65],0.25);
      addOneV1Connection(row+30,column,V2Net.neurons[65],0.25);
      addOneV1Connection(row+60,column,V2Net.neurons[65],0.25);
      addOneV1Connection(row+90,column,V2Net.neurons[65],0.7);
      addOneV1Connection(row+120,column,V2Net.neurons[65],0.2);
      addOneV1Connection(row+150,column,V2Net.neurons[65],0.2);
      }
  }

  public void connectRetinaToV2(CANTNetVision1 V2Net) {
    connectRetinaToTriangle(V2Net,1.0);	
  }

  public void connectV1ToV2(CANTNetVision1 V2Net) {
    connectV1ToTriangle(V2Net);	
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