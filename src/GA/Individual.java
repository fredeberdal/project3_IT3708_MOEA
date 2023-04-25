package GA;

import Utils.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static Utils.SegmentCriteria.CONNECTIVITY;
import static Utils.SegmentCriteria.DEVIATION;
import static Utils.Settings.*;

public class Individual {
    private final List<Gene> genotype;
    private final Pixel[][] pixels;
    private List<Segment> segments = new ArrayList<>();
    private int rating, xLength, yLength, numberOfSeg;
    private int previous = 0;
    public double edgeValue, connectivity, dev, crowdingDist;

    public Individual(List<Gene> genotype, Pixel[][] pixels) {
        this.genotype = genotype;
        this.pixels = pixels;
        this.yLength = pixels.length;
        this.xLength = pixels[0].length;
        makeSegments();
        //fixSegments();
    }

    public Individual(Pixel[][] pixels, int numberOfSeg) {
        this.pixels = pixels;
        this.numberOfSeg = numberOfSeg;
        this.yLength = pixels.length;
        this.xLength = pixels[0].length;
        this.genotype = new ArrayList<>();

        primsMST();
        makeSegments();
    }

    public void fixSegments(){
        int counter = 0;
        while(this.segments.size()>allowedSegmentSize && counter < 10){
            segmentMergeSmallRecursive(0);
            counter++;
            System.out.println("hmmm");
        }
    }

    public void makeSegments() {
        Pixel currentPixel;
        int size = genotype.size();
        int index;
        List<Segment> temporarySegments = new ArrayList<>();
        boolean[] nodesVisited = new boolean[size];
        for (int i = 0; i < nodesVisited.length; i++) {
            nodesVisited[i] = false;
        }
        Set<Pixel> seg;
        for (int j = 0; j < size; j++) {
            if (nodesVisited[j]) {
                continue;
            }
            seg = new HashSet<>();
            Tuple<Integer, Integer> indexPixel = Utils.toPixelCoordinates(j, this.xLength);

            currentPixel = this.pixels[indexPixel.r][indexPixel.l];
            seg.add(currentPixel);

            nodesVisited[j] = true;
            currentPixel = currentPixel.directionalNeighbour(this.genotype.get(j));
            index = Utils.toIndexGenotype(currentPixel.width, currentPixel.height, xLength);
            while (nodesVisited[index] == false) {
                seg.add(currentPixel);
                currentPixel = currentPixel.directionalNeighbour(genotype.get(index));
                nodesVisited[index] = true;
                index = Utils.toIndexGenotype(currentPixel.width, currentPixel.height, xLength);
            }
            if (this.pixels[indexPixel.r][indexPixel.l] != currentPixel) {
                boolean notCurrent = false;
                for (Segment s : temporarySegments) {
                    if (s.hasPixel(currentPixel)) {
                        s.addAllPixels(seg);
                        notCurrent = true;
                        break;
                    }
                }
                if (notCurrent == false) {
                    temporarySegments.add(new Segment(seg));
                }
            } else {
                temporarySegments.add(new Segment(seg));
            }
        }


        this.segments = temporarySegments;
        this.numberOfSeg = temporarySegments.size();
        setFitnessValues(this);

    }
    public void setFitnessValues(Individual ind){
        this.edgeValue = Fitness.allEdgeValue(ind);
        this.connectivity = Fitness.allConnectivity(ind);
        this.dev = Fitness.allDeviation(ind);
    }

    public void primsMST() {
        int sumOfNodes = xLength * yLength;
        int randomX = ThreadLocalRandom.current().nextInt(xLength);
        int randomY = ThreadLocalRandom.current().nextInt(yLength);

        for (int i = 0; i < sumOfNodes; i++) {
            this.genotype.add(Gene.NONE);
        }
        Set<Pixel> nodesVisited = new HashSet<>();
        List<Edge> edges = new ArrayList<>();
        Pixel curr = this.pixels[randomY][randomX];

        PriorityQueue<Edge> queue = new PriorityQueue<>();
        while (sumOfNodes > nodesVisited.size()) {
            if (nodesVisited.contains(curr) == false) {
                nodesVisited.add(curr);
                queue.addAll(this.makeEdges(curr));
            }
            Edge edge = queue.poll();
            if (!nodesVisited.contains(edge.to)) {
                changeGenotype(edge);
                edges.add(edge);
            }
            curr = edge.to;
        }

        Collections.sort(edges);
        for (int j = 0; j < numberOfSeg; j++) {
            Edge edgeRemoved = edges.get(j);
            changeGenotype(edgeRemoved);
        }
    }

    public List<Edge> makeEdges(Pixel p) {
        List<Edge> edges = new ArrayList<>();
        //usikker på objects::nonull
        edges = Gene.geneDirections().stream().map(p::directionalNeighbour).filter(n -> n != null).map(n -> new Edge(p, n)).collect(Collectors.toList());
        return edges;
    }

    public void segmentMergeMutation() {
        List<Segment> allowedSegments = segments.stream().filter(seg -> seg.getPixels().size() < Settings.allowedSegmentSize).toList();
        if (allowedSegments.size() != 0) {
            int random = ThreadLocalRandom.current().nextInt(allowedSegments.size());
            Segment randomSegment = allowedSegments.get(random);
            Edge e = null;
            double rand = ThreadLocalRandom.current().nextDouble();
            if(rand < 0.7){ // parametisere?
                e = topSegmentEdge(randomSegment);
            } else {
                e = randomSegmentEdge(randomSegment);
            }
            if (e != null) {
                changeGenotype(e);
                makeSegments();
            }

        }
    }

    public Edge topSegmentEdge(Segment seg) {
        double lowestDistance = Integer.MAX_VALUE;
        Edge topEdge = null;

        for (Pixel p : seg.getPixels()) {
            for (Pixel neighbour : p.directionalNeighbours().values()) {
                if (seg.hasPixel(neighbour) == false) {
                    Edge temporaryEdge = new Edge(p, neighbour);
                    if (temporaryEdge.distance < lowestDistance) {
                        topEdge = temporaryEdge;
                        lowestDistance = temporaryEdge.distance;
                    }
                }
            }
        }
        return topEdge;
    }

    public Edge randomSegmentEdge(Segment seg) {
        List<Edge> potentialEdges = new ArrayList<>();
        for (Pixel p : seg.getPixels()) {
            for (Pixel neighbour : p.directionalNeighbours().values()) {
                if (seg.hasPixel(neighbour) == true) {
                    potentialEdges.add(new Edge(p, neighbour));
                }
            }
        }
        if(potentialEdges.size() > 0){
            int randomIndex = ThreadLocalRandom.current().nextInt(potentialEdges.size());
            return potentialEdges.get(randomIndex);
        }
        return null;
    }

    public void segmentMergeSmallRecursive(int counter) {
        List<Segment> allowedSegments = new ArrayList<>();
        for (Segment seg : this.segments) {
            if (Settings.allowedSegmentSize > seg.getPixels().size()) {
                allowedSegments.add(seg);
            }
        }
        if (this.previous == allowedSegments.size()) {
            counter++;
        }
        if (Settings.triedMerges < counter || allowedSegments.size() == 0) {
            return;
        }
        for (Segment seg : allowedSegments) {
            Edge e = topSegmentEdge(seg);
            if (e != null) {
                changeGenotype(e);
            }
        }
        this.previous = allowedSegments.size();
        this.makeSegments();
        segmentMergeSmallRecursive(counter);
    }

    public void changeGenotype(Edge e) {
        Pixel from = e.from;
        Pixel to = e.to;
        if (from.equals(to)) {
            this.genotype.set(Utils.toIndexGenotype(from.width, from.height, xLength), Gene.NONE);
            return;
        }
        this.genotype.set(Utils.toIndexGenotype(to.width, to.height, xLength), Gene.fromVector(from.width - to.width, from.height - to.height));

    }

    public boolean edgeChecker(Pixel p) {
        boolean isEdge;
        Segment segPixel = null;
        for (Segment segment : segments) {
            if (segment.hasPixel(p)) {
                segPixel = segment;
            }
        }
        if (segPixel == null) {
            segPixel = segments.get(0);
        }
        isEdge = !segPixel.hasPixel(p.directionalNeighbour(Gene.DOWN)) || !segPixel.hasPixel(p.directionalNeighbour(Gene.LEFT));
        return isEdge;
    }

    public boolean strictlyBetterFit(Individual ind) {
        boolean temp;
        if (Settings.useNSGA) {
            temp = this.getRating() < ind.getRating();
        } else {
            temp = this.getFitnessWithWeights() < ind.getFitnessWithWeights();
        }
        return temp;
    }

    public boolean dominateChecker(Individual ind) {
        return this.connectivity < ind.connectivity && this.edgeValue < ind.edgeValue && this.dev < ind.dev;
    }

    public double getSegCriteriaValue(SegmentCriteria criteria) { // mulig å sløyfe på noen måte?
        if (criteria == CONNECTIVITY) {
            return connectivity;
        } else if (criteria == DEVIATION) {
            return dev;
        } else {
            return edgeValue;
        }
    }

    public List<Segment> getSegments() {
        return this.segments;
    }

    public List<Gene> getGenotype() {
        return this.genotype;
    }

    public Pixel[][] getPixels() {
        return this.pixels;
    }

    public int getRating() {
        return this.rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getxLength() {
        return this.xLength;
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
        return this.previous;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }

    public double getEdgeValue() {
        return this.edgeValue;
    }

    public void setEdgeValue(double edgeValue) {
        this.edgeValue = edgeValue;
    }

    public double getConnectivity() {
        return this.connectivity;
    }

    public void setConnectivity(double connectivity) {
        this.connectivity = connectivity;
    }

    public double getDev() {
        return this.dev;
    }

    public void setDev(double dev) {
        this.dev = dev;
    }

    public double getCrowdingDist() {
        return this.crowdingDist;
    }

    public void setCrowdingDist(double crowdingDist) {
        this.crowdingDist = crowdingDist;
    }

    public double getFitnessWithWeights() {
        double sum = (this.edgeValue * Settings.edgeValue) + (this.dev * Settings.dev) + (this.connectivity * Settings.connectivity);
        return sum;
    }
}
