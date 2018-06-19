import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

// class preColor {
//   String hex;
//   int freq;
//   public preColor(int freq, String hex) {
//     this.hex = hex;
//     this.freq = freq;
//   }
// }
public class imageProcess {

  private static final String COMMA_DELIMITER = ",";
  private static final String NEW_LINE_SEPARATOR = "\n";
    //CSV file header
  private static final String FILE_HEADER = "url,color,color,color";
  private static final String text = "urls.txt";
  private static final String fileName = "res.csv";

  private static final File file = new File(text);
  private static BufferedReader br;
  private static String st;
  private static FileWriter fileWriter = null;

  private static BufferedImage image = null;
  private static URL url = null;

  private static Map<String, Integer> map = new HashMap<>();
  private static int maxFre = 0;
  
  //private static int record = 0;

  public static void main(String[] args)throws Exception {
    try {
        fileWriter = new FileWriter(fileName);
        fileWriter.append(FILE_HEADER.toString());
        fileWriter.append(NEW_LINE_SEPARATOR);

        br = new BufferedReader(new FileReader(file));

        while ((st = br.readLine()) != null) {

          // record++;
          // if(record == 6) break;

          readWriteCSV(st);
        }
        

        

    } catch (Exception e) {
        System.out.println("Error in CsvFileWriter !!!");
        e.printStackTrace();
    } finally {
        try {
          fileWriter.flush();
          fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error while flushing/closing fileWriter !!!");
            e.printStackTrace();
        }
    }
  }

  public static void readWriteCSV(String st) throws Exception {
    try {

      // Read image and processing the image
      url = new URL(st);
      image = ImageIO.read(url);
      int width = image.getWidth();
      int height = image.getHeight();

      for(int i = 0; i < width; i++) {
        for(int j = 0; j < height; j++) {
          int clr =  image.getRGB(i,j); 
          int  r   = (clr & 0x00ff0000) >> 16;
          int  g = (clr & 0x0000ff00) >> 8;
          int  b  =  clr & 0x000000ff;

          String hex = String.format("#%02x%02x%02x", r, g, b);  

          if(!map.containsKey(hex)) map.put(hex, 1);
          else map.put(hex, map.get(hex)+1);

          if(map.get(hex) > maxFre) maxFre = map.get(hex);
        }
      }

      //System.out.println(maxFre);

      // Algorithm for the top 3 colors
      List<String>[] bucket = new List[maxFre+1];
      maxFre = 0;

      for (Map.Entry<String, Integer> entry : map.entrySet()) {
        String key = entry.getKey();
        Integer frequency = entry.getValue();
        if (bucket[frequency] == null) {
          bucket[frequency] = new ArrayList<>();
        }
        bucket[frequency].add(key);
      }
      
      map = new HashMap<>();
      List<String> res = new ArrayList<>();
      for (int pos = bucket.length - 1; pos >= 0 && res.size() < 3; pos--) {
        if (bucket[pos] != null) {
          res.addAll(bucket[pos]);
        }
      }

      //System.out.print(st + " " + res.get(2) + " " + res.get(1) + " " + res.get(0));
      //System.out.println();

      // Write to the csv file
      fileWriter.append(st);
      for(int i = 0; i < res.size(); i++) {
        fileWriter.append(COMMA_DELIMITER);
        fileWriter.append(res.get(i));
      }
      fileWriter.append(NEW_LINE_SEPARATOR);
    } catch(IOException e) {
      System.out.println(e);
    }
  
  }
}
