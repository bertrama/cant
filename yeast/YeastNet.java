public class YeastNet extends CANTNet {
  public YeastNet(){
  }

  public YeastNet(String name,int cols, int rows,int topology){
  	super(name,cols,rows,topology);
System.out.println("create yeast net ");
  }
  
  public CANTNet getNewNet(String name,int cols, int rows,int topology){
  	YeastNet net = new YeastNet (name,cols,rows,topology);
  return (net);
  }
  
  public void initializeNeurons() {
    neurons = new CANTNeuron[cols*rows];
    for(int i=0;i< cols*rows;i++)
      neurons[i] = new CANTNeuron(totalNeurons++,this);
    for(int i=0;i< cols*rows;i++)
      if (topology == 1){
        setConnectionsRandomly(i,50,0.1);
      } 
      else System.out.println("bad ttoppology specified "+ topology);
  }


  public void measure(int currentStep) {
    System.out.println("measure" + currentStep);
  }

}