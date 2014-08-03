import java.util.*;

public class CANTPattern {

    private String name;
    private int id;
    private CANTNet net;
    private int[] patternIndexes;
    private int size;
    private java.util.Vector miniPatterns = new java.util.Vector();

    public int size() {return size; }
    public int getPatternIndex(int i) {return patternIndexes[i];}
    public String getName() {return name;}
    public int getId() {return id;}

    public CANTPattern(CANTNet net ,String name, int id, String[] points ) {
        this.net = net;
        this.id = id;
        this.name = name;
        init(points);
    }

    private void init(String[] points){
      Vector indexVector = new Vector();
      for(int i=0; i<points.length; i++){
        int index = points[i].indexOf(",");
        int startIndex = Integer.parseInt(points[i].substring(0,index));
        int endIndex = Integer.parseInt(points[i].substring(index+1));

        Assert (startIndex < endIndex);
		if (startIndex > endIndex)
          System.out.println(startIndex + " " + endIndex);
        Rectangle rectangle = new Rectangle(startIndex,endIndex);
        int[] tempPatternIndexes = rectangle.initPatternIndexes();
        for(int j=0; j<tempPatternIndexes.length; j++)
          indexVector.add(new Integer(tempPatternIndexes[j]));
      }
      size = indexVector.size();
      patternIndexes = new int[size];
      for(int i=0; i<size; i++){
          patternIndexes[i] = ((Integer)indexVector.get(i)).intValue();
      }


    }
	
  private boolean Assert(boolean test) {
    int x = -1;
    if (! test)
    try{
      x = (1 / (1 +x));
    }
    catch(Exception e){
      System.out.println("Pattern Assert= ");
      return false;
    }
    return true;
  }

  public void arrange(int numberToActivate) {
  	Assert (numberToActivate <= size);
    for (int i= 0; i < numberToActivate; i++) {
      int randomIndex = (int)(Math.random() * size);
      int swap = patternIndexes[i];
      patternIndexes[i] = patternIndexes[randomIndex];
      patternIndexes[randomIndex] = swap;
    }
  }

    public void print(){
//        System.out.println("Name = " + name + "; Id = " + id + "; startIndex = " + startIndex + "; EndnIndex = " + endIndex);
        System.out.print("size = " + size + "; ");
        for(int i=0; i<size; i++){
             System.out.print(" " + i + " = " + patternIndexes[i] + "; ");
        }
        System.out.println("");
    }

  //embedded class
  public class Rectangle{
    private int startIndex;
    private int endIndex;
    private int size;

    public int getStartIndex() {return startIndex;}
    public int getEndIndex() {return endIndex;}
	  
    public Rectangle(int startIndex, int endIndex){
      this.startIndex = startIndex;
      this.endIndex = endIndex;
      setSize();
    }

    private void setSize() {
      int cols = net.getCols();
      int startColumn = startIndex % cols;
      int startRow = startIndex / cols;
      int endColumn = endIndex % cols;
      int endRow    = endIndex / cols;
      size = (endRow - startRow + 1) * (endColumn - startColumn + 1);
      //System.out.println(startColumn+" "+startRow+" "+endColumn+" "+endRow);
    }

    private boolean containsNeuron(int neuronId){
      int cols = net.getCols();
      if ((neuronId >=startIndex) && (neuronId <=endIndex) && ((neuronId % cols) >=startIndex%cols) && ((neuronId % cols) <=endIndex%cols))
        return true;
      else
        return false;
    }

    public int[] initPatternIndexes(){
      int[] patternIndexes = new int[size];
      for(int index = 0,i=startIndex; index<size && i<=endIndex; i++){
        if (containsNeuron(i)) {
          patternIndexes[index++] = i;
        }
      }
      return patternIndexes;
    }
  }//embedded rectangle class
}