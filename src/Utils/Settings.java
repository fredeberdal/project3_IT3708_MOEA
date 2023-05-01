package Utils;

public class Settings {
    public static final boolean useNSGA = true;

    public static final int allowedSegmentSize = 80;
    public static final int triedMerges = 20;
    public static final int getAmountOfGenerations = 5;
    public static final int getPopSize = 30;

    public static final int bestPops = 5;
    public static final int getAmountOfSegments = 30;
    public static final int parentSize = 30;
    public static final double getMutationProb = 0.2;
    public static final double tournamentProb = 0.6;
    public static final double crossoverProb = 0.5;

    public static final String file = "86016"; //86016, 118035, 147091, 176035, 176039, 353013

    public static final String path = "training_images/" + file + "/Test image.jpg";
    public static final String pathGreen = "training_images/" + file + "/Test image.jpg";

    public static final boolean segmentMerge = true;

    // Parameters for weighted fitness
    public static final double edgeValue = 2;
    public static final double connectivity = 100;
    public static final double dev = 10;

}
