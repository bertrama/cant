import java.awt.*;

public class Matrix extends Panel{

    private static final int TOP_BORDER=10, LEFT_BORDER=10;
    public static final int SQUARE_SIZE = 12;

    private int cols,rows;
    private int[][] cells;


    public Matrix (int Cols, int Rows) {

      cols = Cols;
      rows = Rows;
      this.setBounds(LEFT_BORDER, TOP_BORDER, cols*SQUARE_SIZE, rows*SQUARE_SIZE);
      cells=new int [cols][rows];
      clear();
    }
    public void clear () {
      for (int i =0;i <cols; i++)
      for (int j =0;j <rows; j++)
	      cells[i][j]=0;
    }
    public int setState(int x, int y, int state) {
      if (x< cols && y< rows && x>-1 && y>-1) {
        cells[x][y]=state;
        return state;
      }
      return 0;
    }

    public void paint(Graphics g) {

      g.setColor(Color.white);
      g.fillRect(LEFT_BORDER, TOP_BORDER, cols*SQUARE_SIZE, rows*SQUARE_SIZE);
      g.setColor(Color.blue);
      g.drawRect(LEFT_BORDER, TOP_BORDER, cols*SQUARE_SIZE, rows*SQUARE_SIZE);

      //set the color for drawing circles

            g.setColor(Color.blue.darker());
      //draw circles if the cell is on.
      for (int ycnt=0; ycnt<rows; ycnt++) {
        for (int xcnt=0; xcnt<cols; xcnt++) {
          if (cells[xcnt][ycnt]!=0) {
            g.fillOval(LEFT_BORDER+(xcnt*SQUARE_SIZE),
			   TOP_BORDER+(ycnt*SQUARE_SIZE),
               SQUARE_SIZE,SQUARE_SIZE);
          }
        }
      }
      //this creates the grid
      int acrossA=LEFT_BORDER; //leftborder
      int acrossB=acrossA + (cols*SQUARE_SIZE);  //matches LeftBorder of Bacllayout
      int downA=TOP_BORDER;   //matches top border lentgh
      int downB=downA + (rows*SQUARE_SIZE);

      g.setColor(Color.blue);
      //draw horizontal lines
      for(int j=downA+SQUARE_SIZE; j<downB; j+=SQUARE_SIZE){
        if (j == (downA+(SQUARE_SIZE*(rows/2))))
          g.setColor(new Color(210,23,45));
        g.drawLine(acrossA,j,acrossB,j);
        g.setColor(Color.blue);
      }
      //draw vertical lines
      for(int i=acrossA+SQUARE_SIZE; i<acrossB; i+=SQUARE_SIZE){
        if (i == (acrossA+(SQUARE_SIZE*(cols/4))))
          g.setColor(Color.red);
        else if (i == (acrossA+(SQUARE_SIZE*(cols/2))))
          g.setColor(Color.red);
        else if (i == (acrossA+(SQUARE_SIZE*(cols*3/4))))
          g.setColor(Color.red);

        g.drawLine(i,downA,i,downB);
        g.setColor(Color.blue);
      }
    }

    public int rows() { return rows; }
    public int cols() { return cols; }
/*
    public Dimension getSize() {
      return new Dimension((cols*SQUARE_SIZE)+LEFT_BORDER,(rows*SQUARE_SIZE)+TOP_BORDER);
    }
*/
    public int getHeight() {
      return rows*SQUARE_SIZE;
    }
    public int getWidth() {
      return cols*SQUARE_SIZE;
    }

}