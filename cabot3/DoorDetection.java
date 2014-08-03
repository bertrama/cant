
public class DoorDetection {



  public DoorDetection() {
  }

  /**
   * 
   * @param V1
   * @param V2
   * 
   * Recognises doorjambs by sending activation to all points above a HedgeD neuron
   * and all points below a HedgeU neuron. Only where there is a door jamb (where a 
   * hedgeU is above a hedgeD) will there be enough activation to fire a jamb neuron 

  public static void connectV1FeatureToV2JambOld(CABot3Net v1, CABot3Net v2){
    int hedgeDOffset = 0;
    int hedgeUOffset = 7;
    int vEdgeLOffset = 10;
    int vEdgeROffset = 11;
    int jambOffset = 2;
    double hWeight = 0.6;
    double vWeight = 1.2;

    int netSize =v1.getInputSize();
    int columns = v1.getCols();
    int col;
    int row;
    int v1dIndex;
    int v1uIndex;
    int v1RIndex;
    int v1LIndex;
    int v2Index;

    for(int d = 0; d<netSize; d++){
      v1dIndex = hedgeDOffset*netSize+d;
      v1uIndex = hedgeUOffset*netSize+d;
      v1RIndex = vEdgeROffset*netSize+d;
      v1LIndex = vEdgeLOffset*netSize+d;
      //make upward hedgeD conections
      for(int j = d; j>100; j=j-columns){
        row = j/columns;
        if(row%10>4){//bottom half of the assembly
          v2Index = jambOffset*netSize+j;
          v1.neurons[v1dIndex].addConnection(v2.neurons[v2Index], hWeight);

        }
      }
      //make downward hedgeU connections
      for(int j = d; j<netSize; j=j+columns){
        row = j/columns;
        if(row%10>4){//bottom half of the assembly
          v2Index = jambOffset*netSize+j;
          v1.neurons[v1uIndex].addConnection(v2.neurons[v2Index], hWeight);
        }
      }

      row=d/columns;
      if(row%10>4){//bottom half of assembly
        col = d%columns;
        //make rightward vEdgeL connections
        v2Index = jambOffset*netSize+d;
        for(int j= col; j<columns; j++){
          v1.neurons[v1LIndex].addConnection(v2.neurons[v2Index], vWeight);
          v2Index++;
        }
        //make leftward vEdgeR connections
        v2Index = jambOffset*netSize+d;
        for(int j= col; j>0; j--){
          v1.neurons[v1RIndex].addConnection(v2.neurons[v2Index], vWeight);
          v2Index--;
        }
      }
    }
  }*/

  public static void connectV1FeatureToV2Jamb(CABot3Net v1, CABot3Net v2){
    //int hedgeDOffset = 0;
    //int hedgeUOffset = 7;
    int vEdgeLOffset = 10;
    int vEdgeROffset = 11;
    int jambOffset = 2;
    // double hWeight = 0.6;
    double vWeight = 2.2;

    int netSize =v1.getInputSize();
    int columns = v1.getCols();
    int col;
    int row;
    int bigCol;
    int toRow;
    //int v1dIndex;
    //int v1uIndex;
    int v1RIndex;
    int v1LIndex;
    int v2Index;

    for(int d = 0; d<netSize; d++){
      //v1dIndex = hedgeDOffset*netSize+d;
      //v1uIndex = hedgeUOffset*netSize+d;
      v1RIndex = vEdgeROffset*netSize+d;
      v1LIndex = vEdgeLOffset*netSize+d;
      /**make upward hedgeD conections
        for(int j = d; j>100; j=j-columns){
          row = j/columns;
          if(row%10>4){//bottom half of the assembly
            v2Index = jambOffset*netSize+j;
            v1.neurons[v1dIndex].addConnection(v2.neurons[v2Index], hWeight);

          }
        }
        //make downward hedgeU connections
        for(int j = d; j<netSize; j=j+columns){
          row = j/columns;
          if(row%10>4){//bottom half of the assembly
            v2Index = jambOffset*netSize+j;
            v1.neurons[v1uIndex].addConnection(v2.neurons[v2Index], hWeight);
          }
        }*/

      row=d/columns;
      if(row%10>4){//bottom half of assembly
        toRow = row;}
      else{
        toRow= row+5;
      }
      col = d%columns;
      bigCol = col/10;
      //make vEdgeL connections in this one CA
      v2Index = jambOffset*netSize+toRow*columns+bigCol*10;
      for(int j= 0; j<10; j++){
        v1.neurons[v1LIndex].addConnection(v2.neurons[v2Index], vWeight);
        v2Index++;
      }

      //make vEdgeR connections in this one CA
      v2Index = jambOffset*netSize+toRow*columns+bigCol*10;
      for(int j= 0; j>10; j++){
        v1.neurons[v1RIndex].addConnection(v2.neurons[v2Index], vWeight);
        v2Index++;

      }
    }

  }


  public static void connecJambtoDoor(CABot3Net objRec){
    int jambOffset = 2;
    int doorOffset = 3;
    int netSize =objRec.getInputSize();
    int cols = objRec.getCols();
    int row;
    int toRow;
    int jambIndex;
    int doorIndex;
    double weight = 0.9;

    for(int neuron=0; neuron<netSize; neuron++){
      if(neuron%5!=0){ //skip the inhibitory ones}
        row = neuron/cols;
        if(row%10>4){//bottom half of assembly
          toRow = row;}
        else{
          toRow= row+5;
        }

        int col = neuron%cols;
        jambIndex=(jambOffset*netSize+neuron);

        if(col<20){ //if on LH vis field, add connections to the right
          for(int d = col; d<cols; d=d+10){
            doorIndex=(doorOffset*netSize+toRow*cols+d);
            objRec.neurons[jambIndex].addConnection(objRec.neurons[doorIndex], weight);
          }
        }
        else if(col>30){
          for(int d =col; d>0; d=d-10){ //of on RH vis field, add connections to the left
            doorIndex=(doorOffset*netSize+toRow*cols+d);
            objRec.neurons[jambIndex].addConnection(objRec.neurons[doorIndex], weight);
          }
        }

      }
    }

  }

  /**
   * 
   * @param gratingsNet
   * @param v2net
   * 
   * Add inhibition from gratings to the doorjambs
   * NB inhibits entire CA, not just bottom half
   */
  public static void connectGratingsToV2Jamb(CABot3Net gratingsNet, CABot3Net v2net) {
    int jambOffset=2;
    int netSize = v2net.getInputSize();
    double weight = -3;
    int numOfGratings=2;

    int jambIndex;
    int gratingIndex;

    for(int i =0; i<netSize; i++){
      jambIndex = jambOffset*netSize+i;
      for(int g=0; g<numOfGratings; g++){
        gratingIndex=g*netSize+i;
        gratingsNet.neurons[gratingIndex].addConnection(v2net.neurons[jambIndex], weight);
      }
    }
  }
}
