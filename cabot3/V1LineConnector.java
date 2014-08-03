
/**
 * 
 * @author Emma
 * 
 * A Methods-only class that exists to create the connections between the retina net and the V1 line detector  net
 *
 * Name	Date	Comments
 * -----------------------------------
 * ELB  020229	New class to connect retina and line detector cells
 */

public class V1LineConnector {



  public V1LineConnector(){}

  public void connectHLine(String fieldType, CABot3Net retNet, CABot3Net linesNet) {
    int netSize =retNet.getInputSize();
    int retinaOffset = getRetOffset(fieldType)*netSize;
    int lineOffset = getLineOffset(fieldType, "Hline")*netSize;
    int column;
    int row;
    int cols = retNet.getCols();
    double weight=1.2;

    int retNeurIndex;
    int lineNeurIndex;
    //System.out.println("fieldType: "+fieldType+" Hline");
    //We are going to add conections to "inputSize" number of neurons in the line net
    //These neurons will begin in the position determined by the lineOffset value
    //connections will come from neurons that are in the same row (retinotopically)
    for (int i = 0; i < netSize; i++){
      row = i/cols;
      column=i%cols;

      if((row>5 && row<45) && (column>5 && column<cols-5)){//leave off the edges
        retNeurIndex=i+retinaOffset;
        lineNeurIndex=i+lineOffset;
        //System.out.println("lineNeurIndex: "+lineNeurIndex);

        //add central connection
        retNet.neurons[retNeurIndex].addConnection(linesNet.neurons[lineNeurIndex], weight);

        //need to check that the connections arent "falling off" the edge of the retina;
        column=i%retNet.getCols();
        if(column>0){//retinal neuron is not right on the left edge of the vis field - add synapse
          retNet.neurons[retNeurIndex-1].addConnection(linesNet.neurons[lineNeurIndex], weight);
        }
        if(column>1){//retinal neuron is 2 or more cols from the left edge of the vis field - add synapse
          retNet.neurons[retNeurIndex-2].addConnection(linesNet.neurons[lineNeurIndex], weight);
        }
        if (column<retNet.getCols()-2){//we are not right at the RHS of the vis field
          retNet.neurons[retNeurIndex+2].addConnection(linesNet.neurons[lineNeurIndex], weight);
        }
        if (column<retNet.getCols()-1){//we are not right at the RHS of the vis field
          retNet.neurons[retNeurIndex+1].addConnection(linesNet.neurons[lineNeurIndex], weight);
        }
        rebuildHLineEnds(lineNeurIndex, 1, linesNet);
      }
    }


  }

  public void connectVLine(String fieldType, CABot3Net retNet, CABot3Net linesNet) {
    int netSize =retNet.getInputSize();
    int retinaOffset = getRetOffset(fieldType)*netSize;
    // let's move this to the off centre receptors as they work better
    retinaOffset++;
    int lineOffset = getLineOffset(fieldType, "Vline")*netSize;
    int row;
    int column;
    int cols = retNet.getCols();
    double weight=1.5;

    int retNeurIndex;
    int lineNeurIndex;


    //System.out.println("offset: "+retinaOffset);
    //We are going to add conections to "inputSize" number of neurons in the line net
    //These neurons will begin in the position determined by the lineOffset value
    //connections will come from neurons that are in the same row (retinotopically)
    for (int i = 0; i < netSize; i++){
      row = i/cols;
      column=i%cols;
      if(row>5&&row<cols-5&&column>5&&column<cols-5){//leave off the edges
        retNeurIndex=i+retinaOffset;
        lineNeurIndex=i+lineOffset;
        //System.out.println("retNeurIndex: "+retNeurIndex);
        //System.out.println("lineNeurIndex: "+lineNeurIndex);

        //add central connection
        retNet.neurons[retNeurIndex].addConnection(linesNet.neurons[lineNeurIndex], weight);

        //need to check that the connections arent "falling off" the edge of the retina;
        if(row>0){//retinal neuron is not right on the top edge of the vis field - add synapse
          retNet.neurons[retNeurIndex-cols].addConnection(linesNet.neurons[lineNeurIndex], weight);
        }

        if (row<retNet.getCols()-1){//we are not right at the bottom of the vis field
          retNet.neurons[retNeurIndex+cols].addConnection(linesNet.neurons[lineNeurIndex], weight);
        }
        if(row>6 && row<44){
          rebuildVLineEnds(lineNeurIndex, cols, linesNet);
        }
      }
    }
  }


  public void connectFSlash(String fieldType, CABot3Net retNet, CABot3Net linesNet) {
    int netSize =retNet.getInputSize();
    int retinaOffset = getRetOffset(fieldType)*netSize;
    int lineOffset = getLineOffset(fieldType, "FSlash")*netSize;
    int row;
    int column;
    int cols = retNet.getCols();
    int rows = retNet.getRows();
    double exciteWeight=0.9;

    int retNeurIndex;
    int lineNeurIndex;

    //We are going to add connections to "inputSize" number of neurons in the line net
    //These neurons will begin in the position determined by the lineOffset value
    //connections will come from neurons that are in the same row (retinotopically)
    for (int i = 0; i < netSize; i++){
      retNeurIndex=i+retinaOffset;
      lineNeurIndex=i+lineOffset;
      column=i%retNet.getCols();
      row=i/retNet.getCols();
      //System.out.println("retNeurIndex: "+retNeurIndex);
      //System.out.println("lineNeurIndex: "+lineNeurIndex);


      //Add excitatory connections
      //add central connection
      retNet.neurons[retNeurIndex].addConnection(linesNet.neurons[lineNeurIndex], exciteWeight);

      //if not at left or bottom edge, add the -1,+1 offset neuron 
      if(column>0 && row< rows-1){
        retNet.neurons[retNeurIndex+cols-1].addConnection(linesNet.neurons[lineNeurIndex], exciteWeight);
      }
      //if more than one cell away from the left or bottom edge, add the -2,+2 offset neuron 
      if(column>1 && row< rows-2){
        retNet.neurons[retNeurIndex+2*cols-2].addConnection(linesNet.neurons[lineNeurIndex], exciteWeight);
      }
      //if more than 2 cells away from the left or bottom edge, add the -3,+3 offset neuron 
      if(column>2 && row< rows-3){
        retNet.neurons[retNeurIndex+3*cols-3].addConnection(linesNet.neurons[lineNeurIndex], exciteWeight);
      }

      //if not at right or top edge, add the +1,-1 offset neuron 			
      if(column<cols-1 && row>0){
        retNet.neurons[retNeurIndex-(cols-1)].addConnection(linesNet.neurons[lineNeurIndex], exciteWeight);
      }
      //if more than one cell away from the right or top edge, add the +2,-2 offset neuron 
      if(column<cols-2 && row>1){
        retNet.neurons[retNeurIndex-(2*cols-2)].addConnection(linesNet.neurons[lineNeurIndex], exciteWeight);
      }
      //if more than one cell away from the right or top edge, add the +2,-2 offset neuron 
      if(column<cols-3 && row>2){
        retNet.neurons[retNeurIndex-(3*cols-3)].addConnection(linesNet.neurons[lineNeurIndex], exciteWeight);
      }
    }
  }



  public void connectBSlash(String fieldType, CABot3Net retNet, CABot3Net linesNet) {
    int netSize =retNet.getInputSize();
    int retinaOffset = getRetOffset(fieldType)*netSize;
    int lineOffset = getLineOffset("3x3on", "BSlash")*netSize;
    int row;
    int column;
    int cols = retNet.getCols();
    int rows = retNet.getRows();
    double exiteWeight=0.9;
    int retNeurIndex;
    int lineNeurIndex;

    //We are going to add conections to "inputSize" number of neurons in the line net
    //These neurons will begin in the position determined by the lineOffset value
    //connections will come from neurons that are in the same row (retinotopically)
    for (int i = 0; i < netSize; i++){
      retNeurIndex=i+retinaOffset;
      lineNeurIndex=i+lineOffset;
      column=i%retNet.getCols();
      row=i/retNet.getCols();

      //Excitatory Connections
      //add central connection
      retNet.neurons[retNeurIndex].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);

      //if not at left or top edge, add the +1,+1 offset neuron 
      if(column>0 && row>0){
        retNet.neurons[retNeurIndex-(cols+1)].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);
      }
      //if more than one cell away from the left or top edge, add the -2,-2 offset neuron 
      if(column>1 && row>1){
        retNet.neurons[retNeurIndex-(2*cols+2)].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);
      }
      //if more than 2 cells away from the left or top edge, add the -3,-3 offset neuron 
      if(column>2 && row>2){
        retNet.neurons[retNeurIndex-(3*cols+3)].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);
      }

      //if not at right or bottom edge, add the +1,+1 offset neuron 			
      if(column<cols-1 && row<rows-1){
        retNet.neurons[retNeurIndex+cols+1].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);
      }
      //if more than one cell away from the right or top edge, add the +2,+2 offset neuron 
      if(column<cols-2 && row<rows-2){
        retNet.neurons[retNeurIndex+(2*cols+2)].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);
      }
      //if more than one cell away from the right or top edge, add the +2,+2 offset neuron 
      if(column<cols-3 && row<rows-3){
        retNet.neurons[retNeurIndex+(3*cols+3)].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);
      }
    }

  }


  public void connectVItoFSlash(String fieldType, CABot3Net visInNet, CABot3Net linesNet){
    int netSize =linesNet.getInputSize();
    int lineOffset = getLineOffset(fieldType,"FSlash")*netSize;
    int row;
    int column;
    int neuron;
    int cols = linesNet.getCols();
    int rows = visInNet.getRows();
    double exiteWeight=1.2;
    double inhibitWeight = -0.9;
    int visInNeurIndex;
    int lineNeurIndex;

    for (int x=2; x<cols-3; x++){
      for(int y=2; y<rows-3; y++){
        neuron = (y*cols) + x;
        visInNeurIndex = neuron; // no offset for VI net - is a 50*50 net
        lineNeurIndex=neuron+lineOffset;

        //excite along a fslash three pixels long (x+1,y-1)(x,y)(x-1, y+1)
        visInNet.neurons[visInNeurIndex].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);
        visInNet.neurons[visInNeurIndex-(cols-1)].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);
        visInNet.neurons[visInNeurIndex+(cols-1)].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);

        //inhibit along the border either side : left
        visInNet.neurons[visInNeurIndex-cols].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        visInNet.neurons[visInNeurIndex-1].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        visInNet.neurons[visInNeurIndex+(cols-2)].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        //right
        visInNet.neurons[visInNeurIndex-(cols-2)].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        visInNet.neurons[visInNeurIndex+1].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        visInNet.neurons[visInNeurIndex+cols].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        rebuildFSLineEnds(lineNeurIndex, linesNet);
      }
    }

  }

  
  public void connectVItoBSlash(String fieldType, CABot3Net visInNet, CABot3Net linesNet){
    int netSize =linesNet.getInputSize();
    int lineOffset = getLineOffset(fieldType,"FSlash")*netSize;
    int row;
    int column;
    int neuron;
    int cols = linesNet.getCols();
    int rows = visInNet.getRows();
    double exiteWeight=1.8;
    double inhibitWeight = -0.9;
    int visInNeurIndex;
    int lineNeurIndex;

    for (int x=2; x<cols-3; x++){
      for(int y=2; y<rows-3; y++){
        neuron = (y*cols) + x;
        visInNeurIndex = neuron; // no offset for VI net - is a 50*50 net
        lineNeurIndex=neuron+lineOffset;

        //excite along a fslash three pixels long (x+1,y+1)(x,y)(x-1, y-1)
        visInNet.neurons[visInNeurIndex].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);
        visInNet.neurons[visInNeurIndex-(cols+1)].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);
        visInNet.neurons[visInNeurIndex+(cols+1)].addConnection(linesNet.neurons[lineNeurIndex], exiteWeight);

        //inhibit along the border either side : RIGHT
        visInNet.neurons[visInNeurIndex-cols].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        visInNet.neurons[visInNeurIndex+1].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        visInNet.neurons[visInNeurIndex+(cols+2)].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        //left
        visInNet.neurons[visInNeurIndex-(cols+2)].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        visInNet.neurons[visInNeurIndex-1].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        visInNet.neurons[visInNeurIndex+cols].addConnection(linesNet.neurons[lineNeurIndex], inhibitWeight);
        rebuildBSLineEnds(lineNeurIndex, linesNet);
      }
    }

  }
  private void rebuildHLineEnds(int lineNeurIndex, int multiplier, CABot3Net linesNet) {
    int offset;
    double weight=1.0;
    for(int i = -4; i<5; i++){
      if(i!=0){
        offset = lineNeurIndex+(i*multiplier);
        if (offset>=0 && offset<linesNet.getSize()){
          linesNet.neurons[lineNeurIndex].addConnection(linesNet.neurons[offset], weight);
        }
      }
    }
  }

  private void rebuildVLineEnds(int lineNeurIndex, int multiplier, CABot3Net linesNet) {
    int offset;
    double weight=2.4;
    for(int i = -2; i<3; i++){
      if(i!=0){
        offset = lineNeurIndex+(i*50);
        if (offset>=0 && offset<linesNet.getSize()){
          linesNet.neurons[lineNeurIndex].addConnection(linesNet.neurons[offset], weight);
        }
      }
    }
  }
  private void rebuildFSLineEnds(int lineNeurIndex, CABot3Net linesNet) {
    int cols = 50;
    double weight=2.0;
    //Add connections along the slash to rebuild the line ends.
    linesNet.neurons[lineNeurIndex].addConnection(linesNet.neurons[lineNeurIndex-(cols-1)], weight);
    linesNet.neurons[lineNeurIndex].addConnection(linesNet.neurons[lineNeurIndex+(cols-1)], weight);
  }
  private void rebuildBSLineEnds(int lineNeurIndex, CABot3Net linesNet) {
    int cols = 50;
    double weight=2.0;
    //Add connections along the slash to rebuild the line ends.
    linesNet.neurons[lineNeurIndex].addConnection(linesNet.neurons[lineNeurIndex-(cols+1)], weight);
    linesNet.neurons[lineNeurIndex].addConnection(linesNet.neurons[lineNeurIndex+(cols+1)], weight);
  }

private int getLineOffset(String fieldType, String lineType) {
  //offsets: 	3x3 receptors: horizontal = 0, vertical = 1, bslash = 2, fslash = 3
  //			6x6 receptors: horizontal = 4, vertical = 5, bslash = 6, fslash = 7

  int offset = -1; //deliberate fail if receptors text is goofy 

  if(fieldType.compareToIgnoreCase("3x3on")==0){
    if(lineType.compareToIgnoreCase("HLine")==0){
      offset = 0;
    }
    else if(lineType.compareToIgnoreCase("VLine")==0){
      offset = 1;
    } 
    else if(lineType.compareToIgnoreCase("FSlash")==0){
      offset = 2;
    }
    else if(lineType.compareToIgnoreCase("BSlash")==0){
      offset = 3;
    }
  }
  else if(fieldType.compareToIgnoreCase("6x6on")==0){
    if(lineType.compareToIgnoreCase("HLine")==0){
      offset = 4;
    }
    else if(lineType.compareToIgnoreCase("VLine")==0){
      offset = 5;
    }
    else if(lineType.compareToIgnoreCase("FSlash")==0){
      offset = 6;
    }
    else if(lineType.compareToIgnoreCase("BSlash")==0){
      offset = 7;
    }
  }
  if(fieldType.compareToIgnoreCase("3x3Off")==0){
    if(lineType.compareToIgnoreCase("HLine")==0){
      offset = 8;
    }
    else if(lineType.compareToIgnoreCase("VLine")==0){
      offset = 9;
    } 
    else if(lineType.compareToIgnoreCase("FSlash")==0){
      offset = 10;
    }
    else if(lineType.compareToIgnoreCase("BSlash")==0){
      offset = 11;
    }
  }
  else if(fieldType.compareToIgnoreCase("6x6Off")==0){
    if(lineType.compareToIgnoreCase("HLine")==0){
      offset = 12;
    }
    else if(lineType.compareToIgnoreCase("VLine")==0){
      offset = 13;
    }
  
  }
  return offset;
}


private int getRetOffset(String fieldType) {
  //offsets: 	3x3 receptors: on = 0, off = 1
  //			6x6 Receptors: on = 2, off = 3
  //			9x9 Receptors: on = 4, off = 5


  int offset = -1; //deliberate fail if receptors text is goofy 
  if(fieldType.compareToIgnoreCase("3x3on")==0){
    offset = 0;
  }
  else if(fieldType.compareToIgnoreCase("3x3off")==0){
    offset = 1;
  }
  else if(fieldType.compareToIgnoreCase("6x6on")==0){
    offset = 2;
  }
  else if(fieldType.compareToIgnoreCase("6x6off")==0){
    offset = 3;
  }
  else if(fieldType.compareToIgnoreCase("9x9on")==0){
    offset = 4;
  }
  else if(fieldType.compareToIgnoreCase("9x9off")==0){
    offset = 5;
  }
  return offset;
}




}