package GA;

import ImageSegment.ImageSegmentationIO;
import ImageSegment.SegmentationImgIO;
import Utils.Pixel;
import Utils.Settings;
import Utils.Tuple;
import Utils.Utils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;

public class GeneticAlgorithm {

    private Pixel[][] pixels;
    private List<Individual> pop;
    private List<List<Individual>> popRanked;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool((Settings.threadSize));

    public GeneticAlgorithm (SegmentationImgIO segImgIo) {
        this.pixels = segImgIo.getPixels();
    }

    public List<Individual> getPop() {
        return this.pop;
    }

    public int getParetoFrontSize() { // Vet ikke hva denne skal gjøre må bytte navn litt
        return this.popRanked.size();
    }

    public void runGA() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int genCount = 0;
        makePop();
        while (genCount < Settings.genSpan) {
            System.out.println(genCount); // For oversikt
            List<Individual> newPop = Collections.synchronizedList(new ArrayList<>());
            List<Individual> parents = parentSelection(this.pop);
            for (int i = 0; i < Settings.popSize / 2; i++)  {
                executor.execute(() -> {  // Denne burde endres
                    Individual p1 = parents.get(rand.nextInt(parents.size()));
                    Individual p2 = parents.get(rand.nextInt(parents.size()));
                    Tuple<Individual, Individual> offspring = crossover(p1, p2);
                    newPop.add(offspring.l);
                    newPop.add(offspring.r);
                });
            }
            while (newPop.size() != Settings.popSize) {
                // Sync up ?? Fjern?
            }
            for (Individual ind : newPop) {
                if ( )
            }
        }
    }


    public void runNSGA() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int genCount = 0;
        makePop();
        rankPop(this.pop);
        while (genCount < Settings.genSpan) {
            System.out.println(genCount);
            List<Individual> newPop = Collections.synchronizedList(new ArrayList<>());
            List<Individual> parents = parentSelection(this.pop);
            for (int i = 0; i < Settings.popSize / 2; i++) {
                executor.execute(() ->{ // Denne burde endres
                    Individual p1 = parents.get(rand.nextInt(parents.size()));
                    Individual p2 = parents.get(rand.nextInt(parents.size()));
                    Tuple<Individual, Individual> offspring = crossover(p1, p2);
                    newPop.add(offspring.l);
                    newPop.add(offspring.r);
                });
            }
            while (newPop.size() != Settings.popSize){
                // Sync up??
            }
            for (Individual ind : newPop) {
                if (ThreadLocalRandom.current().nextDouble() < Settings.mutationProb) {
                    ind.segmentMergeMutation();
                }
            }
            this.pop.addAll(newPop);
            this.popRanked = rankPop(this.pop);
            newPopFromRank();
            genCount++;
        }
    }



    public void makePop(){

    }

    public List<List<Individual>> rankPop (List<Individual> pop) {
        List<List<Individual>> rankedPoP = new ArrayList<>();
        int rank = 1;
        while (pop.size() > 0) {
            List<Individual> domSet = findDomenatingSet(pop);
            for (Individual i : domSet) {
                i.setRank(rank);
            }
            rankedPoP.add(domSet);
            pop.removeAll(domSet);
            rank++;
        }
        for (List<Individual> i : rankedPoP) {
            pop.addAll(i);
        }
        return rankedPoP;
    }
}
