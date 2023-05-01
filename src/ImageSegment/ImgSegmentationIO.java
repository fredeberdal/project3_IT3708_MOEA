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

public class ImgSegmentationIO {

    private int width;
    private int height;
    private Pixel[][] pixels;

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public Pixel[][] getPixels() { return this.pixels; }

    public ImgSegmentationIO(String file) {
        try (InputStream input = new FileInputStream("training_images/" + file + "/Test image.jpg")) {
            BufferedImage img = ImageIO.read(input);
            this.height = img.getHeight();
            this.width = img.getWidth();
            this.pixels = new Pixel[img.getHeight()][img.getWidth()];

            // Populate the pixels array with the corresponding RGB values from the image
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    Color color = new Color(img.getRGB(j, i));
                    Pixel pixel = new Pixel(new RGB((color.getRed()), (color.getGreen()), color.getBlue()), j, i);
                    pixels[i][j] = pixel;
                }
            }

            // Set the neighbors for each pixel in the pixels array. Pixels are reversely represented.
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    pixels[i][j].setNeighbours(getNeighbours(j, i));
                }
            }
        } catch (IOException exception) {
            System.out.println(exception);
        }
    }

    private Map<Integer, Pixel> getNeighbours(int width, int height) {
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
        if (height+1 < this.height) {
            neighbours.put(4, pixels[height+1][width]);
        }
        if (height-1 >= 0 && width+1 < this.height) {
            neighbours.put(5, pixels[height-1][width+1]);
        }
        if (height+1 < this.height && width+1 < this.width) {
            neighbours.put(6, pixels[height+1][width+1]);
        }
        if (height-1 >= 0 && width-1 >= 0) {
            neighbours.put(7, pixels[height-1][width-1]);
        }
        if (height+1 < this.height && width-1 >= 0) {
            neighbours.put(8, pixels[height+1][width-1]);
        }

        return neighbours;
    }

    public void save (String pathname, Individual ind, String color) {
        int segColor;
        String sumOfSeg;
        String fileColor;
        String path;

        switch (color) {
            case "b" -> {
                segColor = RGB.black.findRGBInt();
                fileColor = "black";
            }
            case "g" -> {
                segColor = RGB.green.findRGBInt();
                fileColor = "green";
            }
            default -> throw new IllegalArgumentException("Color is not black or green.");
        }
        sumOfSeg = ind.getNumberOfSeg() + "_" + ind.getConnectivity() + "_" + ind.getDev() + "_" + ind.getEdgeValue();

        path = "evaluator/student_segments/"+ pathname + "/" + fileColor + "/segments=" + sumOfSeg + ".jpg";

        System.out.println("Saving file for path:  " + path);
        try {
            File file = new File(path);
            BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < this.getHeight(); i++) {
                for (int j = 0; j < this.getWidth(); j++) {
                    if (ind.edgeChecker(pixels[i][j])) {
                        img.setRGB(j, i, segColor);
                    } else {
                        img.setRGB(j, i, fetchBackground(pixels[i][j], color));
                    }
                }
            }
            for (int i = 0; i < getWidth(); i++) {
                img.setRGB(i, 0, segColor);
                img.setRGB(i, this.getHeight() - 1, segColor);
            }
            for (int i = 0; i < getHeight(); i++) {
                img.setRGB(0, i, segColor);
                img.setRGB(this.getWidth() - 1, i, segColor);
            }
             ImageIO.write(img, "jpg", file);
            } catch (IOException exception) {
            System.out.println(exception);
        }

    }

    private int fetchBackground(Pixel pixel, String color) {
        if(color.equals("b")){
            return RGB.white.findRGBInt();
        }else {return pixel.color.findRGBInt();}
    }

}
