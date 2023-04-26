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
import java.util.concurrent.TimeUnit;

public class main {

    public static void main(String[] args) throws InterruptedException {
        String file = Settings.file;
        String path = Settings.path;
        String pathGreen = Settings.pathGreen;

        boolean runNSGA = Settings.runNSGA;
        boolean segmentMerge = Settings.segmentMerge;
        int bestPops = Settings.bestPops;

        List<Individual> bestIndividuals = new ArrayList<>();
        ImgSegmentationIO imgSegmentationIO = new ImgSegmentationIO(file);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(imgSegmentationIO);

        if(runNSGA){
            geneticAlgorithm.runNSGA();
            bestIndividuals = geneticAlgorithm.rankPop(geneticAlgorithm.getPop()).get(0);
        }else{
            geneticAlgorithm.runGA();
            bestIndividuals = geneticAlgorithm.getPop();

            bestIndividuals.sort(Comparator.comparingDouble(p -> p.getFitnessWithWeights()));
            bestIndividuals = bestIndividuals.subList(0,bestPops);
        }
        for(Individual ind : bestIndividuals){
            if(segmentMerge){ind.segmentMergeSmallRecursive(0);}
            imgSegmentationIO.save(file, ind, "b");
            imgSegmentationIO.save(file, ind, "g");
        }
        System.out.println("Done");
        if(runNSGA){
            System.out.println("Parento fronts: " + geneticAlgorithm.getParetoFrontSize());
        }
    }
    }
