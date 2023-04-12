package GA;

import Utils.Pixel;

import java.util.ArrayList;
import java.util.List;

public class Individual {
    private final List<Gene> genotype;
    private final Pixel[][] pixels;
    private List<Segment> segments = new ArrayList<>();
    private int rating, xLength, yLength, numberOfSeg;
    private int previous = 0;
    private double edgeValue, connectivity, dev, crowdingDist;

    public Individual(List<Gene> genotype, Pixel [][] pixels) {
        this.genotype = genotype;
        this.pixels = pixels;
        this.yLength = pixels.length;
        this.xLength = pixels[0].length;
    }
    public Individual(Pixel[][] pixels, int numberOfSeg) {
        this.pixels = pixels;
        this.numberOfSeg = numberOfSeg;
        this.yLength = pixels.length;
        this.xLength = pixels[0].length;
        this.genotype = new ArrayList<>();
    }

    public void makeSegments(){

    }
    public List<Segment> getSegments(){
        return segments;
    }

    public List<Gene> getGenotype() {
        return genotype;
    }

    public Pixel[][] getPixels() {
        return pixels;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getxLength() {
        return xLength;
    }

    public void setxLength(int xLength) {
        this.xLength = xLength;
    }

    public int getyLength() {
        return yLength;
    }

    public void setyLength(int yLength) {
        this.yLength = yLength;
    }

    public int getNumberOfSeg() {
        return numberOfSeg;
    }

    public void setNumberOfSeg(int numberOfSeg) {
        this.numberOfSeg = numberOfSeg;
    }

    public int getPrevious() {
        return previous;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }

    public double getEdgeValue() {
        return edgeValue;
    }

    public void setEdgeValue(double edgeValue) {
        this.edgeValue = edgeValue;
    }

    public double getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(double connectivity) {
        this.connectivity = connectivity;
    }

    public double getDev() {
        return dev;
    }

    public void setDev(double dev) {
        this.dev = dev;
    }

    public double getCrowdingDist() {
        return crowdingDist;
    }

    public void setCrowdingDist(double crowdingDist) {
        this.crowdingDist = crowdingDist;
    }
}
