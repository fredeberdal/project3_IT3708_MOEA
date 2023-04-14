package GA;

import Utils.Pixel;
import Utils.Tuple;
import Utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Pixel currentPixel;
        int size = genotype.size();
        int index;
        List<Segment> temporarySegments = new ArrayList<>();
        boolean [] nodesVisited = new boolean[size];
        for(int i = 0; i < nodesVisited.length; i++){
            nodesVisited[i] = false;
        }
        Set<Pixel> seg;
        for(int j = 0; j < size; j++){
            if(nodesVisited[j] == false){
                seg = new HashSet<>();
                Tuple<Integer, Integer> indexPixel = Utils.toPixelCoordinates(j, xLength);

                currentPixel = this.pixels[indexPixel.r][indexPixel.l];
                seg.add(currentPixel);

                nodesVisited[j] = true;
                currentPixel = currentPixel.directionalNeighbour(genotype.get(j));
                index = Utils.toIndexGenotype(currentPixel.width, currentPixel.height, xLength);
                while(nodesVisited[index] == false){
                    seg.add(currentPixel);
                    currentPixel = currentPixel.directionalNeighbour(genotype.get(index));
                    nodesVisited[index] = true;
                    index = Utils.toPixelCoordinates(currentPixel.width, currentPixel.height, xLength);
                }
                if(this.pixels[indexPixel.r][indexPixel.l] != currentPixel){

                }

            }else{

            }
        }


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
