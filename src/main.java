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

    //private boolean
    public static ThreadPoolExecutor exe = (ThreadPoolExecutor)Executors.newFixedThreadPool(Settings.threadSize);
    public static void main(String[] args) {
        String file = Settings.file;
        String path = Settings.path;
        boolean runNSGA = Settings.runNSGA;
        boolean segmentMerge = Settings.segmentMerge;
        int popSize = Settings.popSize;

        List<Individual> bestIndividuals = new ArrayList<>();
        ImgSegmentationIO imgSegmentationIO = new ImgSegmentationIO(file);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(imgSegmentationIO);

        if(runNSGA){
            geneticAlgorithm.runNSGA();
            bestIndividuals = geneticAlgorithm.rankPop(geneticAlgorithm.getPop()).get(0);
        }else{
            geneticAlgorithm.runGA();

            bestIndividuals = geneticAlgorithm.getPop();
            //Usikker om denne funker
            bestIndividuals.sort(Comparator.comparingDouble(p -> p.getFitnessWithWeights()));
            bestIndividuals = bestIndividuals.subList(0,popSize-1);
        }
        Path bPath = Path.of(path);
        Path gPath = Path.of(path);
        for(Individual ind : bestIndividuals){
            exe.execute(()-> {
                if(segmentMerge){
                    ind.segmentMergeSmallRecursive(0);
                }

                imgSegmentationIO.save(file, ind, "b");
                imgSegmentationIO.save(file, ind, "g");
            });
        }
        exe.shutdown();
        System.out.println("Done");
    }
}