import GA.GeneticAlgorithm;
import GA.Individual;
import ImageSegment.ImgSegmentationIO;
import Utils.Settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class main {

    public static void main(String[] args) throws InterruptedException {
        String file = Settings.file;
        String path = Settings.path;
        String pathGreen = Settings.pathGreen;

        boolean runNSGA = Settings.useNSGA;
        boolean segmentMerge = Settings.segmentMerge;
        int bestPops = Settings.bestPops;

        List<Individual> bestIndividuals = new ArrayList<>();
        ImgSegmentationIO imgSegmentationIO = new ImgSegmentationIO(file);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(imgSegmentationIO);

        if(runNSGA){
            geneticAlgorithm.runNSGA();
            bestIndividuals = geneticAlgorithm.rankPop(geneticAlgorithm.getPop()).get(0);
            bestIndividuals = removeDups(bestIndividuals);
        }else{
            geneticAlgorithm.runGA();
            bestIndividuals = geneticAlgorithm.getPop();

            bestIndividuals.sort(Comparator.comparingDouble(Individual::getFitnessWithWeights));
            bestIndividuals = bestIndividuals.subList(0,bestPops);
        }
        for(Individual ind : bestIndividuals){
            if(segmentMerge){ind.segmentMergeSmallRecursive(0);}
            imgSegmentationIO.save(file, ind, "b");
            imgSegmentationIO.save(file, ind, "g");
        }
        System.out.println("Done");
        if(runNSGA){
            System.out.println("Parento fronts: " + geneticAlgorithm.getParetoFrontierSize());
            List<List<Individual>> parentoOptimal = geneticAlgorithm.getPopRanked();
            for(List<Individual> ind : parentoOptimal){
                System.out.println("Amount of segments: " + ind.get(0).getNumberOfSeg());
            }
        }
    }
    public static List<Individual> removeDups(List<Individual> pop){
        List<Individual> newPop = new ArrayList<>();
        for(Individual i : pop){
            if(!newPop.contains(i)){
                newPop.add(i);
            }
        }
        System.out.println(pop.size());
        System.out.println(newPop.size());
        return newPop;
    }
}
