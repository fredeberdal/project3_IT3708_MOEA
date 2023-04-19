import GA.GeneticAlgorithm;
import GA.Individual;
import ImageSegment.SegmentationImgIO;
import Utils.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class main {

    private String file = Settings.file;
    //private boolean
    public static ThreadPoolExecutor exe = (ThreadPoolExecutor)Executors.newFixedThreadPool(Settings.threadSize);
    public static void main(String[] args) {
        List<Individual> bestIndividuals = new ArrayList<>();
        SegmentationImgIO segImgIo = new SegmentationImgIO(Settings.file);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(segImgIo);


    }
}