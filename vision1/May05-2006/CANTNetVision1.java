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
    if ((fromRow < 0) ||(fromRow >= 210) ||(fromCol < 0) || (fromCol >= 30))
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
      addOneRetinaConnection(row-1,column-1,V1Net.neurons[inputNeuron+1800],onOff66Val);
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
      addOneRetinaConnection(row+1,column-1,V1Net.neurons[inputNeuron+2700],onOff66Val);
      addOneRetinaConnection(row,column,V1Net.neurons[inputNeuron+2700],onOff66Val);
      addOneRetinaConnection(row-1,column+1,V1Net.neurons[inputNeuron+2700],onOff66Val);
      }
  }

  private void connectRetinaToOrAngle(CANTNetVision1 V1Net, double onOff66Val) 
  {
    int row;
    int column;
    for (int inputNeuron = 1800; inputNeuron < 2700; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      addOneRetinaConnection(row,column,V1Net.neurons[inputNeuron+3600],onOff66Val);
      addOneRetinaConnection(row,column+1,V1Net.neurons[inputNeuron+3600],onOff66Val);
      addOneRetinaConnection(row-1,column+2,V1Net.neurons[inputNeuron+3600],onOff66Val);
      addOneRetinaConnection(row-1,column-1,V1Net.neurons[inputNeuron+3600],onOff66Val);
      }
  }

  private void connectRetinaToV1(CANTNetVision1 V1Net) {
    connectRetinaToHorizontal(V1Net,1.4,-0.2,0.5,-0.2);	
    connectRetinaToSlash(V1Net,1.4);	
    connectRetinaToBackSlash(V1Net,1.4);	
    connectRetinaToAndAngle(V1Net,1.1);	
    connectRetinaToLessThanAngle(V1Net,3.0);	
    connectRetinaToGreaterThanAngle(V1Net,3.0);	
    connectRetinaToOrAngle(V1Net,1.1);	
  }

  private void connectRetinaToAndTriangle(CANTNetVision1 V2Net, double offOn99Val) 
  {
    int row;
    int column;
	int triangleRow;
	int triangleCol;
    for (int inputNeuron = 4500; inputNeuron < 5400; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
	  triangleRow = ((row - 150) / 5 ) * 5;
	  triangleCol = ((column +2)/ 5) *5;
      addOneRetinaConnection(row,column,V2Net.neurons[(triangleRow*30)+triangleCol],offOn99Val);
      }
  }

  private void connectRetinaToOrTriangle(CANTNetVision1 V2Net, double offOn99Val) 
  {
    int row;
    int column;
    int triangleRow;
    int triangleCol;
    for (int inputNeuron = 4500; inputNeuron < 5400; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      triangleRow = ((row - 150) / 5 ) * 5;
      triangleCol = ((column + 2) / 5) *5;
      addOneRetinaConnection(row,column,V2Net.neurons[900+(triangleRow*30)+triangleCol],offOn99Val);
      addOneRetinaConnection(row+1,column,V2Net.neurons[900+(triangleRow*30)+triangleCol],offOn99Val);
      }
  }

  private void connectV1ToAndTriangle(CANTNetVision1 V2Net) 
  {
    int row;
    int column;
    int triangleRow;
    int triangleCol;
    for (int inputNeuron = 0; inputNeuron < 900; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
	  triangleRow = (row / 5) * 5;
	  triangleCol = (column / 5) *5;
	  //hline
	  if ((column % 5) > 2 )
        addOneV1Connection(row,column,V2Net.neurons[(triangleRow*30)+triangleCol+5],0.25);
	  else	
	    addOneV1Connection(row,column,V2Net.neurons[(triangleRow*30)+triangleCol],0.25);
	  //sline
	  addOneV1Connection(row+30,column,V2Net.neurons[(triangleRow*30)+triangleCol+5],0.25);
	  addOneV1Connection(row+30,column,V2Net.neurons[((triangleRow+5)*30)+triangleCol+5],0.25);
	  //langle
	  addOneV1Connection(row+120,column,V2Net.neurons[(triangleRow*30)+triangleCol+5],0.2);
	  if (triangleCol > 4) 
	    {
	    //bline
	    addOneV1Connection(row+60,column,V2Net.neurons[(triangleRow*30)+triangleCol],0.25);
	    addOneV1Connection(row+60,column,V2Net.neurons[((triangleRow+5)*30)+triangleCol],0.25);
	    //gangle
	    addOneV1Connection(row+150,column,V2Net.neurons[(triangleRow*30)+triangleCol],0.2);
	    }
	  //aangle
      triangleCol = ((column+2) / 5) *5;
      if (triangleRow > 4)
	    {
        addOneV1Connection(row+90,column,V2Net.neurons[((triangleRow-5)*30)+triangleCol],0.7);	
	    addOneV1Connection(row+90,column,V2Net.neurons[((triangleRow)*30)+triangleCol],0.7);	
	    }
      }
  }

  private void connectV1ToOrTriangle(CANTNetVision1 V2Net) 
  {
    int row;
    int column;
    int triangleRow;
    int triangleCol;
    for (int inputNeuron = 0; inputNeuron < 900; inputNeuron++) 
      {
      row = inputNeuron/30;
      column = inputNeuron%30;
      triangleRow = ((row / 5) * 5) + 30;
      triangleCol = (column / 5) *5;
      //hline
      if (triangleRow < 54)
        if ((column % 5) > 2 )
          addOneV1Connection(row,column,V2Net.neurons[((triangleRow+5)*30)+triangleCol+5],0.25);
        else	
          addOneV1Connection(row,column,V2Net.neurons[((triangleRow+5)*30)+triangleCol],0.25);
      //bline
      addOneV1Connection(row+60,column,V2Net.neurons[(triangleRow*30)+triangleCol+5],0.25);
      if (triangleRow < 54)
        addOneV1Connection(row+60,column,V2Net.neurons[((triangleRow+5)*30)+triangleCol+5],0.25);
      //gangle
      addOneV1Connection(row+150,column,V2Net.neurons[(triangleRow*30)+triangleCol+5],0.2);
      if (triangleCol > 4) 
        {
        //sline
        addOneV1Connection(row+30,column,V2Net.neurons[(triangleRow*30)+triangleCol],0.25);
        if (triangleRow < 54)
          addOneV1Connection(row+30,column,V2Net.neurons[((triangleRow+5)*30)+triangleCol],0.25);
        //langle
        addOneV1Connection(row+120,column,V2Net.neurons[(triangleRow*30)+triangleCol],0.2);
        }
      //oangle
      triangleCol = ((column+2) / 5) *5;
      if (triangleRow > 4)
        {
        addOneV1Connection(row+180,column,V2Net.neurons[((triangleRow-5)*30)+triangleCol],0.7);	
        addOneV1Connection(row+180,column,V2Net.neurons[((triangleRow)*30)+triangleCol],0.7);	
        }
      }
  }

  private void connectRetinaToV2(CANTNetVision1 V2Net) {
    connectRetinaToAndTriangle(V2Net,1.0);	
    connectRetinaToOrTriangle(V2Net,1.0);	
  }

  private void connectV1ToV2(CANTNetVision1 V2Net) {
    connectV1ToAndTriangle(V2Net);	
    connectV1ToOrTriangle(V2Net);	
  }
  
  //****************intranet connections******************
  private void setLargeV2Connections (int baseNeuron) {
    int row = baseNeuron / 30;
    int col = baseNeuron %30;
	int V2Offset; //for the different triangles
	
	if (row >=30) row=row-30;
	if (baseNeuron >= 900) V2Offset = 900;
	else V2Offset = 0;
    for (int connectRow = row - 4; connectRow < row + 4; connectRow++) 
    {
    if ((connectRow >= 0) && (connectRow < 30))
    for (int connectCol = col - 4; connectCol < col + 4; connectCol++)
      {
        if ((connectCol >= 0) && (connectCol < 30))
        addConnection(baseNeuron,V2Offset+(connectRow*30)+connectCol,4.77);
      }
    }	
  }

  private void setTriangleV2Connections (int baseNeuron) {
    int V2Offset; //for the different triangles
  
    if (baseNeuron >= 900) V2Offset = 900;
	else V2Offset = 0;  
  	
    for (int toNeuron = 1; toNeuron < 900; toNeuron+=10)
      {
        addConnection(baseNeuron,toNeuron+V2Offset,Math.random());
      }
  }

  private void setInhibV2Connections (int baseNeuron) {
    int row = baseNeuron / 30;
    int col = baseNeuron %30;
    int V2Offset; //for the different triangles
    
    if (row >=30) row=row-30;
    if (baseNeuron >= 900) V2Offset = 900;
    else V2Offset = 0;
    //above
    for (int connectRow = row - 13; connectRow < row - 2; connectRow++) 
      {
      if ((connectRow >= 0) && (connectRow < 30))
      for (int connectCol = col - 7; connectCol < col + 7; connectCol++)
        {
        if ((connectCol >= 0) && (connectCol < 30))
        addConnection(baseNeuron,V2Offset+(connectRow*30)+connectCol, Math.random());
        }
      }	
    //below
    for (int connectRow = row + 3; connectRow < row + 13; connectRow++) 
      {
      if ((connectRow >= 0) && (connectRow < 30))
      for (int connectCol = col - 7; connectCol < col + 7; connectCol++)
        {
        if ((connectCol >= 0) && (connectCol < 30))
        addConnection(baseNeuron,V2Offset+(connectRow*30)+connectCol, Math.random());
        }
      }	
    //left
    for (int connectRow = row -4; connectRow < row + 2; connectRow++) 
      {
      if ((connectRow >= 0) && (connectRow < 30))
      for (int connectCol = col - 13; connectCol < col - 5; connectCol++)
        {
        if ((connectCol >= 0) && (connectCol < 30))
        addConnection(baseNeuron,V2Offset+(connectRow*30)+connectCol, Math.random());
        }
      }	
    //right
    for (int connectRow = row -2; connectRow < row + 2; connectRow++) 
      {
      if ((connectRow >= 0) && (connectRow < 30))
      for (int connectCol = col + 7; connectCol < col +13; connectCol++)
        {
        if ((connectCol >= 0) && (connectCol < 30))
        addConnection(baseNeuron,V2Offset+(connectRow*30)+connectCol, Math.random());
        }
      }	
	  
	//inhib other triangle
	for (int otherRow = 0; otherRow < 30; otherRow+=5)
	  for (int otherCol = 0; otherCol < 30; otherCol +=5)
	  	if (baseNeuron < 900)
	  	  addConnection(baseNeuron,900+ otherRow*30+otherCol, 2.0);
	  	else
		  addConnection(baseNeuron, otherRow*30+otherCol, 2.0);
		
  }

  private void setSmallV2Connections (int baseNeuron) {
    int row = baseNeuron / 30;
	int col = baseNeuron %30;
	int V2Offset; //for the different triangles
	
	if (row >=30) row=row-30;
	if (baseNeuron >= 900) V2Offset = 900;
	else V2Offset = 0;
	for (int connectRow = row - 2; connectRow < row + 3; connectRow++) 
	  {
	  if ((connectRow >= 0) && (connectRow < 30))
	  for (int connectCol = col - 2; connectCol < col + 3; connectCol++)
	    {
        if ((connectCol >= 0) && (connectCol < 30))
        addConnection(baseNeuron,V2Offset+(connectRow*30)+connectCol,0.3 +(Math.random()));
	    }
	  }	
  }

  //every 25th (5,5) neuron gets external activation (from V1 and retina).
  //That should send activity to the surrounding 100 neurons,
  //Every 3 and 8 neuron should inhibit the ones beyond those
  //The rest should stimulate local 25
  private void setV2Connections(int start, int finish) {
    for (int neuronIndex = start; neuronIndex < finish; neuronIndex ++)
	  {
	  if (((neuronIndex % 5) == 0)  &&  (((neuronIndex / 30) % 5) == 0))
	    {
		neurons[neuronIndex].setInhibitory(false);
        setLargeV2Connections(neuronIndex);
	    }
      else if ((neuronIndex % 10) == 1)
      {
      neurons[neuronIndex].setInhibitory(false);
      setTriangleV2Connections(neuronIndex);
      }
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
    else if (topology == 3){
      System.out.println("vision 2 topology");
	  setV2Connections(0,899);
      setV2Connections(900,1799);
    }
    else System.out.println("bad toppology specified "+ topology);
  }


  public void measure(int currentStep) {
    System.out.println("measure" + currentStep);
  }

}