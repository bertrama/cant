
public class V1v2GratingsConnector {


  public V1v2GratingsConnector() {
  }

  //TODO Wire backslashes to vertical grates!

  public void connectGratings(CABot3Net linesNet, CABot3Net gratingsNet){

    connectHGrid3(linesNet, gratingsNet);
    makeIntraGratingInhibitoryLinks(gratingsNet, "hGrate3");

    makeExcitatoryVBHConnections3(linesNet, gratingsNet, "vline", "vGrate");
    makeIntraGratingInhibitoryLinks(gratingsNet, "vGrate3");

    connectHGrid6(linesNet, gratingsNet);
    makeIntraGratingInhibitoryLinks(gratingsNet, "hGrate6");

    makeExcitatoryVBHConnections6(linesNet, gratingsNet, "vline", "vGrate");
    makeIntraGratingInhibitoryLinks(gratingsNet, "vGrate6");

    makeExcitatoryVBHConnections3(linesNet, gratingsNet, "fSlash", "fSlashGrate");
    // I have chosen not to inhibit from these as the V and H bars should be the "default" - so bslash and fslash should never override vlines or hlines
    //makeIntraGratingInhibitoryLinks(gratingsNet, "fSlashGrate3");

    makeExcitatoryVBHConnections3(linesNet, gratingsNet, "bSlash", "bSlashGrate");
 // I have chosen not to inhibit from these as the V and H bars should be the "default" - so bslash and fslash should never override vlines or hlines
    //makeIntraGratingInhibitoryLinks(gratingsNet, "bSlashGrate3");

  



  }




  /**
   * 
   * @param linesNet
   * @param gratingsNet
   * 
   * Because horizontal grids repeat vertically they must be wired together as gratings differently to the other lines - which wire horizontally
   * Off and on centre line detectors are wired together   
   */
  public void connectHGrid3(CABot3Net linesNet, CABot3Net gratingsNet){
    int netSize =linesNet.getInputSize();
    int h3OnOffset = netSize*getLineOffset("hline", "3on");
 
    int gratingOffset = netSize*getGrateOffset("hGrate3");
    int row;
    int cols = linesNet.getCols();
    int rows = linesNet.getRows();
    double weight = 1.4; 

    for(int i=0; i<netSize; i++){
      //add central connection 3X3 on
      linesNet.neurons[h3OnOffset+i].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
     
      //if we're not "falling off" the edge, add connections to -3 and +3
      row = i/cols;
      if(row >2){
        linesNet.neurons[h3OnOffset+i-cols*3].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
        
      }
      if(row<rows-3){
        linesNet.neurons[h3OnOffset+i+cols*3].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
        
      }
      //if we're not "falling off" the edge, add connections to -2 and +2
      row = i/cols;
      if(row > 1){
        linesNet.neurons[h3OnOffset+i-cols*2].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
        
      }
      if(row<rows
          -2){
        linesNet.neurons[h3OnOffset+i+cols*2].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
        
      }
    }

  }

  /**
   * 
   * @param linesNet
   * @param gratingsNet
   * 
   * Because horizontal grids repeat vertically they must be wired together as gratings differently to the other lines - which wire horizontally
   * Off and on centre line detectors are wired together   
   */
  public void connectHGrid6(CABot3Net linesNet, CABot3Net gratingsNet){
    int netSize =linesNet.getInputSize();
    int h6OnOffset = netSize*getLineOffset("hline", "6on");
    int h6OffOffset = netSize*getLineOffset("hline", "6off");
    int gratingOffset = netSize*getGrateOffset("hGrate6");
    int row;
    int cols = linesNet.getCols();
    int rows = linesNet.getRows();
    double weight =1.4; 

    for(int i=0; i<netSize; i++){
      //add central connection 6X6
      linesNet.neurons[h6OnOffset+i].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
     
      //if we're not "falling off" the edge, add connections to -1 and +1
      row = i/cols;
      if(row > 3){
        linesNet.neurons[h6OnOffset+i-cols*4].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
       
      }
      if(row<rows-4){
        linesNet.neurons[h6OnOffset+i+cols*4].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
        
      }
      //if we're not "falling off" the edge, add connections to -2 and +2
      row = i/cols;
      if(row > 1){
        linesNet.neurons[h6OnOffset+i-cols*2].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
       
      }
      if(row<rows-2){
        linesNet.neurons[h6OnOffset+i+cols*2].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
        
      }
      }

  }
 

  public void makeExcitatoryVBHConnections3(CABot3Net linesNet, CABot3Net gratingsNet, String lineType, String grateType){ 
    int netSize =linesNet.getInputSize();
    int column;
    int cols = linesNet.getCols();
    double weight;
    if (grateType.compareToIgnoreCase("vgrate3")==0){
      weight = 1.5;
    }
    else{
      weight =1.2;
    }
    
    int v3OnOffset = netSize*getLineOffset(lineType, "3on");
    ///int v3OffOffset = netSize*getLineOffset(lineType, "3off");
    int gratingOffset = netSize*getGrateOffset(grateType+"3");

    for(int i=0; i<netSize; i++){
      //add central connection 3X3 and 6X6
      linesNet.neurons[v3OnOffset+i].addConnection(gratingsNet.neurons[gratingOffset+i], weight);

      //if we're not "falling off" the edge, add connections -2 and +2 columns away
      column = i%cols;
      if(column > 1){
        linesNet.neurons[v3OnOffset+i-2].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
      }
      if(column<cols-2){
        linesNet.neurons[v3OnOffset+i+2].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
      }
      //if we're not "falling off" the edge, add connections -4 and +4 columns away
      column = i%cols;
      if(column > 3){
        linesNet.neurons[v3OnOffset+i-4].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
      }

      if(column<cols-4){
        linesNet.neurons[v3OnOffset+i+4].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
      }
      //if we're not "falling off" the edge, add connections -6 and +6 columns away
      if(column > 5){
        linesNet.neurons[v3OnOffset+i-5].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
      }
      if(column<cols-6){
        linesNet.neurons[v3OnOffset+i+5].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
      }
    }
  }


  public void makeExcitatoryVBHConnections6(CABot3Net linesNet, CABot3Net gratingsNet, String lineType, String grateType){ 
    int netSize =linesNet.getInputSize();
    int column;
    int cols = linesNet.getCols();
    double weight; 
    if (grateType.compareToIgnoreCase("vgrate6")==0){
      weight = 1.5;
    }
    else{
      weight =1.5;
    }
    int v6OnOffset = netSize*getLineOffset(lineType, "6on");
       int gratingOffset = netSize*getGrateOffset(grateType+"6");

    for(int i=0; i<netSize; i++){
      //add central connection  6X6
      linesNet.neurons[v6OnOffset+i].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
      
      //if we're not "falling off" the edge, add connections -2 and +2 columns away
      column = i%cols;
      if(column > 1){
        linesNet.neurons[v6OnOffset+i-2].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
       
      }

      if(column<cols-2){
        linesNet.neurons[v6OnOffset+i+2].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
       
      }
      //if we're not "falling off" the edge, add connections -4 and +4 columns away
      column = i%cols;
      if(column > 3){
        linesNet.neurons[v6OnOffset+i-4].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
        
      }

      if(column<cols-4){
        linesNet.neurons[v6OnOffset+i+4].addConnection(gratingsNet.neurons[gratingOffset+i], weight);
       
      }
      //if we're not "falling off" the edge, add connections -6 and +6 columns away
      column = i%cols;
      if(column > 5){
        linesNet.neurons[v6OnOffset+i-5].addConnection(gratingsNet.neurons[gratingOffset+i], weight);

      }

      if(column<cols-6){
        linesNet.neurons[v6OnOffset+i+5].addConnection(gratingsNet.neurons[gratingOffset+i], weight);

      }
    }  
  }


  public void makeIntraGratingInhibitoryLinks(CABot3Net gratingsNet, String grateType){
    int grateOffsetMultiplier = getGrateOffset(grateType);
    //System.out.println("grate is: "+grateType);
    int numGrateTypes = 6;
    int netSize = gratingsNet.getInputSize();
    int cols = gratingsNet.getCols();
    double weight = -5.0;
    for (int i=0; i<numGrateTypes; i++){
      //skip inhibiting itself!
      if(i!=grateOffsetMultiplier){
        for(int j=0; j<netSize; j++){
          gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j], weight);
          //either side, left and right
          if(j>0 && j<cols-1){
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j-1], weight);
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j+1], weight);
          }
          if(j>1 && j<cols-2){
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j-2], weight);
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j+2], weight);
          }
          if(j>2 && j<cols-3){
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j-3], weight);
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j+3], weight);
          }
          if(j>3 && j<cols-4){
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j-4], weight);
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j+4], weight);
          }
          if(j>4 && j<cols-5){
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j-5], weight);
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j+5], weight);
          }
          if(j>5 && j<cols-6){
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j-6], weight);
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j+6], weight);
          }
          //either side up and down
          if(j>cols && j<netSize-cols){
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j-cols], weight);
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j+cols], weight);
          }
          if(j>2*cols && j<netSize-2*cols){
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j-2*cols], weight);
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+j+2*cols], weight);
          }
          //4 around the middle
          if(j>cols && j<netSize-cols && j>0 && j<cols-1){
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+((j-1)-cols)], weight);
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+((j-1)+cols)], weight);
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+((j+1)-cols)], weight);
            gratingsNet.neurons[grateOffsetMultiplier*netSize+j].addConnection(gratingsNet.neurons[i*netSize+((j+1)+cols)], weight);
          }
          
        }
      }
    }
  }

  private int getGrateOffset(String grate) {
    int grateOffset = -1;//deliberately goofy - inelegant fail if badparams
    if(grate.compareToIgnoreCase("hgrate3")==0){
      grateOffset = 0;
    }
    else if (grate.compareToIgnoreCase("vgrate3")==0){
      grateOffset = 1;
    }
    else if (grate.compareToIgnoreCase("hgrate6")==0){
      grateOffset = 2;
    }
    else if (grate.compareToIgnoreCase("vgrate6")==0){
      grateOffset = 3;
    }
    else if (grate.compareToIgnoreCase("fSlashGrate3")==0){
      grateOffset = 4;
    }
    else if (grate.compareToIgnoreCase("bSlashGrate3")==0){
      grateOffset = 5;
    }
    else if (grate.compareToIgnoreCase("fSlashGrate6")==0){
      grateOffset = 6;
    }
    else if (grate.compareToIgnoreCase("bSlashGrate6")==0){
      grateOffset = 7;
    }
    else{
      System.out.println("EEEK: bad params in getGrateOffset, V1v2GratingsConnect.java");
    }

    return grateOffset;
  }

  private int getLineOffset(String line, String fieldType) {
    int offset= -1; //deliberate inelegant fail if badparams

    if(fieldType.compareToIgnoreCase("3on")==0){
      if(line.compareToIgnoreCase("hline")==0){
        offset=0;
      }
      else if (line.compareToIgnoreCase("vline")==0){
        offset=1;
      }
      else if (line.compareToIgnoreCase("fslash")==0){
        offset=2;
      }
      else if(line.compareToIgnoreCase("bslash")==0){
        offset=3;
      }
    }
    else if(fieldType.compareToIgnoreCase("6on")==0){
      if(line.compareToIgnoreCase("hline")==0){
        offset=4;
      }
      else if (line.compareToIgnoreCase("vline")==0){
        offset=5;
      }
      else if (line.compareToIgnoreCase("fslash")==0){
        offset=6;
      }
      else if(line.compareToIgnoreCase("bslash")==0){
        offset=7;
      }

    }
    else if(fieldType.compareToIgnoreCase("3off")==0){
      if(line.compareToIgnoreCase("hline")==0){
        offset=8;
      }
      else if (line.compareToIgnoreCase("vline")==0){
        offset=9;
      }
      else if (line.compareToIgnoreCase("fslash")==0){
        offset=10;
      }
      else if(line.compareToIgnoreCase("bslash")==0){
        offset=11;
      }
    }
    else if(fieldType.compareToIgnoreCase("6off")==0){
      if(line.compareToIgnoreCase("hline")==0){
        offset=12;
      }
      else if (line.compareToIgnoreCase("vline")==0){
        offset=13;
      }
      else if (line.compareToIgnoreCase("fslash")==0){
        offset=14;
      }
      else if(line.compareToIgnoreCase("bslash")==0){
        offset=15;
      }

    }
    else{
      System.out.println("EEEK: Bad parameters for getLineOffset in V1v2Gratings.java");
    }
    return offset;

  }
}
