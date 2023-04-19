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

        List<Individual> bestIndividuals = new ArrayList<>();
        ImgSegmentationIO segImgIo = new ImgSegmentationIO(file);
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(segImgIo);

        if(runNSGA){
            geneticAlgorithm.runNSGA();
            bestIndividuals = geneticAlgorithm.rankPop(geneticAlgorithm.getPop()).get(0);
        }else{
            geneticAlgorithm.runGA();

            List<Individual> population = geneticAlgorithm.getPop();
            //Usikker om denne funker
            population.sort(Comparator.comparingDouble(p -> p.getFitnessWithWeights()));
            population.subList(0,4);
        }
        Path bPath = Path.of("path/" + file);
        Path gPath = Path.of("path/" + file);


    }
}