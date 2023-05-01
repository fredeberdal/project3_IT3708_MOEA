package Utils;

public class Settings {
    public static final int getAmountOfGenerations = 5;
    public static final boolean useNSGA = true;
    public static final int highestSegmentSize = 30;
    public static final int lowestSegmentSize = 4;
    public static final int triedMerges = 20;
    public static final int getPopSize = 50;
    public static final int bestPops = 5;
    public static final int parentSize = 15;
    public static final double getMutationProb = 0.2;
    public static final double tournamentProb = 0.6;
    public static final double crossoverProb = 0.5;

    public static final String file = "76002"; //86016, 118035, 147091, 176035, 176039, 353013

    public static final boolean segmentMerge = true;

    // Parameters for weighted fitness
    public static final double edgeValue = 2;
    public static final double connectivity = 100;
    public static final double dev = 10;

}
