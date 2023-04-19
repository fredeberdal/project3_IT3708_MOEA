import GA.GeneticAlgorithm;
import GA.Individual;
import ImageSegment.ImgSegmentationIO;
import Utils.Settings;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class main {

    private String file = Settings.file;
    //private boolean
    public static ThreadPoolExecutor exe = (ThreadPoolExecutor)Executors.newFixedThreadPool(Settings.threadSize);
    public static void main(String[] args) {
        String file = Settings.file;
        boolean runNSGA = Settings.runNSGA;
        boolean segmentMerge = Settings.segmentMerge;

        List<Individual> bestIndividuals = new ArrayList<>();
        ImgSegmentationIO imgSegmentationIO = new ImgSegmentationIO(file);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(segImgIo);

        if(runNSGA){
            geneticAlgorithm.runNSGA();
            bestIndividuals = geneticAlgorithm.rankPop(geneticAlgorithm.getPop()).get(0);
        }else{
            geneticAlgorithm.runGA();

            bestIndividuals = geneticAlgorithm.getPop();
            //Usikker om denne funker
            bestIndividuals.sort(Comparator.comparingDouble(p -> p.getFitnessWithWeights()));
            bestIndividuals.subList(0,4);
        }
        Path bPath = Path.of("path/" + file);
        Path gPath = Path.of("path/" + file);
        for(Individual ind : bestIndividuals){
            exe.execute(()-> {
                System.out.println("Bajs");
            });
        }

    }
}