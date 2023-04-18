package Utils;

import GA.Individual;

import java.util.Comparator;

public enum SegmentCriteria {

    EDGEVALUE, CONNECTIVITY, DEVIATION;

    //gjøre om hele eller ikke bruke den
    public static Comparator<Individual> individualComparator (SegmentCriteria criteria) {
        return switch (criteria) {
            case EDGEVALUE -> (a, b) -> Double.compare(a.getEdgeValue(), b.getEdgeValue());
            case CONNECTIVITY -> (a, b) -> Double.compare(a.getConnectivity(), b.getConnectivity());
            case DEVIATION -> (a, b) -> Double.compare(a.getDev(), b.getDev());
        };
    }

    // Bruker ikke measure

}
