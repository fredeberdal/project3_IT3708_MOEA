package GA;

import Utils.Pixel;
import Utils.RGB;

import java.util.Collection;

public class Fitness {


    public static double distance(RGB i, RGB j) {
        return Math.sqrt(
                Math.pow(Math.abs(j.r-i.r), 2)
                        + Math.pow(Math.abs(j.g-i.g), 2)
                        + Math.pow(Math.abs(j.b-i.b), 2)
        );
    }

    public static double deviation(Segment seg){
        return seg.getPixels().stream().map(pixel -> distance(pixel.color, seg.getCentroid())).reduce(0.0, (sum, el) -> sum+el);
    }

    public static double segConnectivity(Segment seg){
        double con = 0;
        for(Pixel p : seg.getPixels()){
            for(Pixel n : p.getNeighbours().values()){
                if(seg.hasPixel(n)){
                    con += 0;
                } else {
                    con += 0.125;
                }
            }
        }
        return con;
    }

    public static double calculateEdgeValue(Segment seg){
        double eV = 0;
        for (Pixel p : seg.getPixels()){
            Collection<Pixel> n = p.getNeighbours().values();
            for(Pixel neighboru : n){
                if (!seg.hasPixel(neighboru)) {
                    double dist = distance(p.color, neighboru.color);
                    eV += dist;
                }
            }
        }
        return -eV;
    }

    public static double allEdgeValue(Individual ind){
        return ind.getSegments().stream().map(segment -> segment.edgeValue).reduce(0.0, (sum, el) -> sum+el);
    }

    public static double allDeviation(Individual ind){
        return ind.getSegments().stream().map(segment -> segment.dev).reduce(0.0, (sum, el) -> sum+el);
    }

    public static double allConnectivity(Individual ind){
        return ind.getSegments().stream().map(segment -> segment.connectivity).reduce(0.0, (sum, el) -> sum+el);
    }

}
