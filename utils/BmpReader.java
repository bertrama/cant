
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.applet.*;
import javax.imageio.*;

public class BmpReader extends Applet{
  static File inputFile;
  static BufferedImage input, outputImage;
  
  static  String filePath;
  
  public void setFilePath(String newPath) {filePath=newPath;}

private static String getCommand () {
  String result = filePath;
  String inputLine = "bob";

  try {
    DataInputStream inputCommandFile = new DataInputStream(
       new FileInputStream(filePath+"csCommand.txt"));
  
    inputLine = inputCommandFile.readLine();
    inputLine = inputCommandFile.readLine();
    inputLine = inputCommandFile.readLine();
  }
  
  catch (IOException e) {
    System.err.println("Can not read csCommand.txt " + e.toString());
  }
  
  result = result.concat(inputLine);
  System.out.println(result);
  
  return(result);
}

public static void readPicture() {
//  System.out.println("Read Bitmap");
  String command;
  
  command = getCommand();
  
  try {
    inputFile = new File(command);
    input = ImageIO.read(inputFile);
    }

  catch (IOException e) {
    System.err.println("could not open jpg");
    }
}

public static void readPicture(String fileName) {
//  System.out.println("Read Bitmap");
  String command = filePath+fileName;
  
  try {
    inputFile = new File(command);
    input = ImageIO.read(inputFile);
	}

  catch (IOException e) {
      System.err.println("could not open jpg" + inputFile);
    }
}

//get the bits that are on from the picture.
public static int getPictureBits(int [] bits) {
  int cBits = 0;
  boolean bitOn;
  
  outputImage = new BufferedImage(100,100,10); //TYPE_BYTE_GRAY
	 
  for (int i = 0; i < 100;i++)
    {
    for (int j = 0; j < 100;j++) 
      {
      //this translates to gray scale
      outputImage.setRGB(i,j,input.getRGB(i*4,j*4));
      if (outputImage.getRGB(i,j) < -8000000)
      bits[cBits++] = i+(j*100);
      }
    }
  return cBits;
}

//get the bits that are on from the picture.
  public static int getPictureBits(int xSize, int [] bits) {
  int cBits = 0;
  boolean bitOn;
  int ySize=xSize;
  int density = 400/xSize; //assumes a 400x400 picture.  
  
  outputImage = new BufferedImage(xSize,ySize,10); //TYPE_BYTE_GRAY
	 
  for (int i = 0; i < xSize;i++)
    {
    for (int j = 0; j < ySize;j++) 
      {
      //this translates to gray scale
      outputImage.setRGB(i,j,input.getRGB(i*density,j*density));
      if (outputImage.getRGB(i,j) < -9000000)
        bits[cBits++] = i+(j*xSize);
      }
    }
  return cBits;
}

//write the picture to a file
public static void processPicture() {
  //outputImage = new BufferedImage(640,480,TYPE_INT_RGB);//1
  outputImage = new BufferedImage(100,100,10);
  //outputImage = new BufferedImage(100,100,TYPE_BYTE_BINARY);//12
  //outputImage = new BufferedImage(640,480,TYPE_BYTE_GRAY);//10
  DataOutputStream outputText;
  
  try {
        outputText = new DataOutputStream(new FileOutputStream("agent1/CANTImage"));
	 
  for (int i = 0; i < 100;i++)
    {
    for (int j = 0; j < 100;j++) 
	  {
      outputImage.setRGB(i,j,input.getRGB(i*4,j*4));
	  if (outputImage.getRGB(i,j) < -8000000)
	  outputText.writeBytes(j+(i*100) + " ");
	  }
    }

  }
  
  catch (IOException e) {
     System.err.println("output file not opened properly\n" +
                          e.toString());
     System.exit(1);
	 }
}

public static void savePicture () {
  String mimeTypes[];
  
  try {
    File outputFile = new File("image.jpg");
    ImageIO.write(outputImage, "JPG", outputFile);
  }


  catch (IOException e) {
      System.err.println("Could not save jpg ");
    }
}
	


//endclass
}