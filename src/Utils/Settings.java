package Utils;

public class Settings {
    public static final boolean useNSGA = false;
    public static final int allowedSegmentSize = 200;
    public static final int triedMerges = 10;
    public static final boolean bestSegment = true;
    public static final int threadSize = 5;
    public static final int genSpan = 10;
    public static final int popSize = 2;
    public static final int parentSize = 15;
    public static final double mutationProb = 0.3;
    public static final double tournamentProb = 0.8;
    public static final double crossoverProb = 0.8;

    public static final String file = "86016" ;
    public static final String path = "training_images/" + file + "/Test image.jpg";
    public static final String pathGreen = "training_images/" + file + "/Test image.jpg";

    public static final boolean runNSGA = true;
    public static final boolean segmentMerge = true;

    // Parameters for weighted fitness
    public static final double edgeValue = 2;
    public static final double connectivity = 100;
    public static final double dev = 10;

}
