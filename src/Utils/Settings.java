package Utils;

public class Settings {
    public static final boolean useNSGA = false;

    public static final int allowedSegmentSize = 200;
    public static final boolean bestSegment = true;
    public static final int threadSize = 5;
    public static final int genSpan = 100;
    public static final int popSize = 20;
    public static final int parentSize = 10;
    public static final double tournamentProb = 0.8;
    public static final double mutationProb = 0.3;
    public static final String file = "blablafilnavn";
    public static final double crossoverProb = 0.8;

    // Parameters for weighted fitness
    public static final double edgeValue = 4;
    public static final double connectivity = 50;
    public static final double dev = 20;

}
