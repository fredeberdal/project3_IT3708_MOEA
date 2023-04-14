package ImageSegment;

import GA.Individual;
import Utils.Pixel;
import Utils.RGB;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageSegmentationIO {

    private static int debugImageCount = 0;
    private int width;
    private int height;
    private Pixel[][] pixels;

    public int getImageWidth() { return this.width; }
    public int getImageHeight() { return this.height; }
    public Pixel[][] getPixels() { return this.pixels; }

    public ImageSegmentationIO(String filename) {
        try (InputStream input = new FileInputStream(new File("project3_IT3708_MOEA/training/" + filename + "/Test image.jpg"))) {
            BufferedImage img = ImageIO.read(input);
            this.width = img.getWidth();
            this.height = img.getHeight();
            this.pixels = new Pixel[img.getWidth()][img.getHeight()];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    final Color color = new Color(img.getRGB(i, j));
                    final Pixel pixel = new Pixel(
                            new RGB((color.getRed()), (color.getGreen()), color.getBlue()), i, j);
                    pixels[i][j] = pixel;
                }
            }

            // Pixels er omvendt representert enn en typisk 2D-array. (x = width, y = height)
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    pixels[i][j].setNeighbours(getNeighbours(i, j));
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private Map<Integer, Pixel> getNeighbours(int width, int height) { // Lånt logikk
        Map<Integer, Pixel> neighbours = new HashMap<>();

        if (width+1 < this.width) {
            neighbours.put(1, pixels[height][width+1]);
        }
        if (width-1 >= 0) {
            neighbours.put(2, pixels[height][width-1]);
        }
        if (height-1 >= 0) {
            neighbours.put(3, pixels[height-1][width]);
        }
        if (height+1 < this.height)                     {
            neighbours.put(4, pixels[height+1][width]);
        }
        if (height-1 >= 0 && width+1 < this.height)          {
            neighbours.put(5, pixels[height-1][width+1]);
        }
        if (height+1 < this.height && width+1 < this.width) {
            neighbours.put(6, pixels[height+1][width+1]);
        }
        if (height-1 >= 0 && width-1 >= 0)                  {
            neighbours.put(7, pixels[height-1][width-1]);
        }
        if (height+1 < this.height && width-1 >= 0)         {
            neighbours.put(8, pixels[height+1][width-1]);
        }
        return neighbours;
    }

    public void save (String path, Individual solution, String color) {
        save(path, solution, color, false);
    }

    public void save (String path, Individual solution, String color, boolean checker) {
        if (color != "black" && color != "green") {
            throw new IllegalArgumentException("Color is not black or green.");
        }
        int segColor = color.equals("black") ? RGB.black.findRGBInt() : RGB.green.findRGBInt();
        String txtSuffix = color.equals("black") ? "black" : "green";
        String folder = color.equals("black") ? "" : "_Green";
        String numOfSeg = solution.getNumberOfSeg() + "_" + solution.getConnectivity() + "_" + solution.getDev() + "_" + solution.getEdgeValue();
        String wholePath = "project3_IT3708_MOEA/evaluator/Student_Segmentation_Files" + folder + "/" + path + "/" + txtSuffix + (checker ? debugImageCount++ : numOfSeg + ".jpg");
        System.out.println("Writing file to " + wholePath);
        
        try {
            File output = new File(wholePath);
            BufferedImage img = new BufferedImage(this.getImageWidth(), this.getImageHeight(), BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < getImageHeight(); i++) {
                for (int j = 0; j < getImageWidth(); j++) {
                    if (solution.edge(pixels[i][j])) {  // Må lages i Individual
                        img.setRGB(i, j, segColor);
                    } else {
                        img.setRGB(i, j, getBackground(pixels[i][j], color));
                    }
                }
            }

            //Edge around img
            for (int j = 0; j < getImageWidth(); j++) {
                img.setRGB(j, 0, segColor);
                img.setRGB(j, this.getImageHeight() - 1, segColor);
            }
            for (int i = 0; i < getImageHeight(); i++) {
                img.setRGB(0, i, segColor);
                img.setRGB(this.getImageWidth() - 1, i, segColor);
            }
             ImageIO.write(img, "jpg", output);
            } catch (IOException e) {
            System.out.println(e);
        }
    }

    private int getBackground (Pixel pixel, String color) {
        return color.equals("black") ? RGB.white.findRGBInt() : pixel.color.findRGBInt();
    }

}
