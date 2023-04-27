package Utils;

import GA.Individual;

import java.util.Comparator;

public enum SegmentCriteria {

    EDGEVALUE, CONNECTIVITY, DEVIATION;

    public static Comparator<Individual> individualComparator (SegmentCriteria criteria) {
         Comparator<Individual> individualComparator;
             if(criteria == EDGEVALUE){
                 individualComparator = (x, y) -> Double.compare(x.getEdgeValue(), y.getEdgeValue());
             }else if(criteria == DEVIATION){
                 individualComparator = (x, y) -> Double.compare(x.getDev(), y.getDev());
             }else{
                 individualComparator = (x, y) -> Double.compare(x.getDev(), y.getDev());
             }
             return individualComparator;
    }

}
