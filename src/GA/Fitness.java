package GA;

import Utils.Pixel;
import Utils.RGB;

public class Fitness {


    public static double distance(RGB i, RGB j) {
        return Math.sqrt(
                Math.pow(Math.abs(j.r-i.r), 2)
                        + Math.pow(Math.abs(j.g-i.g), 2)
                        + Math.pow(Math.abs(j.b-i.b), 2)
        );
    }
    public static double connectivity(Individual ind){
        return ind.getSegments().stream().map(segment -> segment.connectivity).reduce(0.0, (sum, el) -> sum+el);
    }
    public static double deviation(Segment seg){
        return seg.getPixels().stream().map(pixel -> distance(pixel.color, seg.getCentroid())).reduce(0.0, (sum, el) -> sum+el);
    }
    





}
