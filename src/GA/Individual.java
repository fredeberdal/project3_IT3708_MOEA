package GA;

import Utils.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
                    index = Utils.toIndexGenotype(currentPixel.width, currentPixel.height, xLength);
                }
                if(this.pixels[indexPixel.r][indexPixel.l] != currentPixel){
                    boolean notCurrent = false;
                    for(Segment s : temporarySegments){
                        if(s.hasPixel(currentPixel)){
                            s.addAllPixels(seg);
                            notCurrent = true;
                            break;
                        }
                    }
                    if(notCurrent == true){
                        temporarySegments.add(new Segment(seg));
                    }
                }else{
                    temporarySegments.add(new Segment(seg));
                }
            }
            this.segments = temporarySegments;
            this.numberOfSeg = temporarySegments.size();
            this.edgeValue = Fitness.allEdgeValue(this);
            this.connectivity = Fitness.allConnectivity(this);
            this.dev = Fitness.allDeviation(this);
        }


    }
    public Edge topSegmentEdge(Segment seg){
        double lowestDistance = 1000000;
        Edge topEdge = null;

        for(Pixel p : seg.getPixels()){
            for(Pixel neighbour : p.directionalNeighbours().values()){
                if(seg.hasPixel(neighbour) == true){
                    Edge temporaryEdge = new Edge(p, neighbour);
                    if(temporaryEdge.distance < lowestDistance){
                        topEdge = temporaryEdge;
                        lowestDistance = temporaryEdge.distance;
                    }
                }
            }
        }
        return topEdge;
    }

    public Edge randomSegmentEdge(Segment seg){
        List<Edge> potentialEdges = new ArrayList<>();
        for(Pixel p: seg.getPixels()){
            for(Pixel neighbour: p.directionalNeighbours().values()){
                if(seg.hasPixel(neighbour) == true){
                    potentialEdges.add(new Edge(p, neighbour));
                }
            }
        }
        if(potentialEdges.size() != 0){
            int randomIndex = ThreadLocalRandom.current().nextInt(0, potentialEdges.size());
            return potentialEdges.get(randomIndex);
        }
        return null;
    }

    public void segmentMergeSmallRecursive(int counter){
        List<Segment> allowedSegments = new ArrayList<>();
        for(Segment seg : this.segments){
            if(Settings.allowedSegmentSize < seg.getPixels().size()){
                allowedSegments.add(seg);
            }
        }
        if(this.previous == allowedSegments.size()){
            counter++;
        }
        if(Settings.allowedSegmentSize < counter || allowedSegments.size() == 0){
            return;
        }
        for(Segment seg : allowedSegments){
            Edge e = topSegmentEdge(seg);
            if(e != null){
                //riktig rekkefølge?
                changeGenotype(e.from, e.to);
            }
        }
        this.previous = allowedSegments.size();
        this.makeSegments();
        segmentMergeSmallRecursive(counter);
    }
    public void changeGenotype(Pixel from, Pixel to){
        if(from.equals(to)){
            this.genotype.set(Utils.toIndexGenotype(from.width, from.height, xLength), Gene.NONE);
            return;
        }
        this.genotype.set(Utils.toIndexGenotype(to.width, to.height, xLength), Gene.fromVector(from.width-to.width, from.height-to.height));

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
