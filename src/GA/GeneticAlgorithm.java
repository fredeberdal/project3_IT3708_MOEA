package GA;

import ImageSegment.ImgSegmentationIO;

import Utils.Pixel;
import Utils.Settings;
import Utils.Pair;
import Utils.Utils;
import Utils.SegmentCriteria;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GeneticAlgorithm {

    public List<List<Individual>> getPopRanked() {
        return popRanked;
    }

    private List<List<Individual>> popRanked;
    private List<Individual> pop;
    private Pixel[][] pixels;

    public GeneticAlgorithm (ImgSegmentationIO segImgIo) {
        this.pixels = segImgIo.getPixels();
    }

    public List<Individual> getPop() {
        return this.pop;
    }

    public int getParetoFrontierSize() { // Vet ikke hva denne skal gjøre må bytte navn litt
        return this.popRanked.size();
    }

    public void runGA() throws InterruptedException {
        final int amountOfGenerations = Settings.getAmountOfGenerations;
        final int popSize = Settings.getPopSize;
        final ThreadLocalRandom rand = ThreadLocalRandom.current();
        int genCount = 0;
        makePop();

        while (genCount < amountOfGenerations) {
            System.out.println("Current Generation: " + genCount * 10);
            List<Individual> newPop = new ArrayList<>(popSize);
            List<Individual> parents = parentSelection(this.pop);
            for (int i = 0; i < popSize / 2; i++)  {
                final Individual p1 = parents.get(rand.nextInt(parents.size()));
                final Individual p2 = parents.get(rand.nextInt(parents.size()));
                final Pair<Individual, Individual> offspring = crossover(p1, p2);
                newPop.add(offspring.getLeft);
                newPop.add(offspring.getRight);
            }
            for (Individual ind : newPop) {
                if (Utils.randomDouble() < Settings.getMutationProb) {
                    ind.segmentMergeMutation();
                }
            }
            this.pop = newPop;
            genCount++;
            genFix(); //TODO slett
        }
    }


    public void runNSGA() throws InterruptedException {
        final ThreadLocalRandom rand = ThreadLocalRandom.current();
        int genCount = 0;
        makePop();
        final int popSize = Settings.getPopSize;
        rankPop(this.pop);

        while (genCount < Settings.getAmountOfGenerations) {
            System.out.println("Current Generation: " + genCount * 10);
            List<Individual> newPop = new ArrayList<>(popSize);
            List<Individual> parents = parentSelection(this.pop);

            for (int i = 0; i < popSize / 2; i++) {
                final Individual p1 = parents.get(rand.nextInt(parents.size()));
                final Individual p2 = parents.get(rand.nextInt(parents.size()));
                final Pair<Individual, Individual> offspring = crossover(p1, p2);
                newPop.add(offspring.getLeft);
                newPop.add(offspring.getRight);
            }
            for (Individual ind : newPop) {
                if (ThreadLocalRandom.current().nextDouble() < Settings.getMutationProb) {
                    ind.segmentMergeMutation();
                }
            }
            this.pop.addAll(newPop);
            this.popRanked = rankPop(this.pop);
            newPopFromRank();
            genCount++;
            genFix(); //TODO slett
        }
    }

    private void assigningCrowdingDist(List<Individual> paretoFrontier) {
        for (Individual ind : paretoFrontier) {
            ind.setCrowdingDist(0);
        }
        for (SegmentCriteria criteria : SegmentCriteria.values()) {
            assignIndividualCrowdingDist(paretoFrontier, criteria);
        }
    }

    private void assignIndividualCrowdingDist(List<Individual> paretoFrontier, SegmentCriteria criteria) {
        paretoFrontier.sort(SegmentCriteria.individualComparator(criteria)); // Sort
        Individual maxInd = paretoFrontier.get(paretoFrontier.size()-1); // Set crowding dist
        Individual minInd = paretoFrontier.get(0);
        maxInd.setCrowdingDist(Integer.MAX_VALUE);
        minInd.setCrowdingDist(Integer.MAX_VALUE);

        // Difference in criteria between first and last ind
        double minMaxCriteriaDiffInSeg = maxInd.getSegCriteriaValue(criteria) - minInd.getSegCriteriaValue(criteria);

        // Crowding dist for rest
        for (int i = 1; i < paretoFrontier.size()-1; i++) {
            double differenceInCriteria = paretoFrontier.get(i+1).getSegCriteriaValue(criteria) - paretoFrontier.get(i - 1).getSegCriteriaValue(criteria);
            differenceInCriteria /= minMaxCriteriaDiffInSeg;

            paretoFrontier.get(i).setCrowdingDist(paretoFrontier.get(i).getCrowdingDist() + differenceInCriteria);
        }
    }

    public void makePop(){
        System.out.println("Making a pop"); // For oversikt
        List<Individual> newPop = new ArrayList<>();
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        for (int i = 0; i < Settings.getPopSize / 2; i++) {
            Individual ind = new Individual(this.pixels, rand.nextInt(Settings.lowestSegmentSize, Settings.highestSegmentSize) + 1); // Inkluderer upper bound TODO burde kanskje se på mengde segmenter...
            newPop.add(ind);
        }
        System.out.println("Finished making pop");
        this.pop = newPop;
    }

    public List<Gene> randomlyMutateGene(List<Gene> genes) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        double mutationProb = Settings.getMutationProb;

        if (rand.nextDouble() < mutationProb) {
            int index = rand.nextInt(genes.size());
            Pair<Integer, Integer> individualPixel = Utils.toPixelCoordinates(index, this.pixels[0].length);
            List<Gene> allowedGenes = this.pixels[individualPixel.getRight][individualPixel.getLeft].getValidGenes();
            Gene newGene = allowedGenes.get(rand.nextInt(allowedGenes.size()));
            genes.set(index, newGene);
        }
        return genes;
    }

    private void newPopFromRank() {
        this.pop.clear();
        int popSize = Settings.getPopSize;

        for (List<Individual> paretoFrontier : this.popRanked) {
            assigningCrowdingDist(paretoFrontier);

            if (paretoFrontier.size() <= popSize - this.pop.size()) {
                this.pop.addAll(paretoFrontier);
            } else {
                List<Individual> temp = new ArrayList<>(paretoFrontier);
                temp.sort((x,y) -> Double.compare(y.getCrowdingDist(), x.getCrowdingDist()));
                this.pop.addAll(temp.subList(0, popSize - this.pop.size()));
            }
        }
    }

    public List<List<Individual>> rankPop (List<Individual> pop) {
        int rank = 1;
        List<List<Individual>> rankedPoP = new ArrayList<>();

        while (!pop.isEmpty()) {
            List<Individual> front = nonDominatedList(pop);

            for (Individual ind : front) {
                ind.setRating(rank);
            }
            rankedPoP.add(front);
            pop.removeAll(front);
            rank++;
        }
        for (List<Individual> i : rankedPoP) {
            pop.addAll(i);
        }
        return rankedPoP;
    }

    public List<Individual> nonDominatedList(List<Individual> pop) {
        List<Individual> notDominatedList = new ArrayList<>(pop);
        for (int i = 0; i < pop.size(); i++) {
            Individual ind = pop.get(i);
            boolean dominating = false;
            for (int j = 0; j < notDominatedList.size(); j++) {
                Individual otherInd = notDominatedList.get(j);
                if (otherInd == ind) {
                    continue;
                }
                dominating = ind.dominateChecker(otherInd);
                if(dominating){
                    break;
                }
            }
            if(!dominating){
                notDominatedList.add(ind);
            }
        }
        return notDominatedList;
    }

    public List<Individual> parentSelection(List<Individual> pop) {
        List<Individual> chosenParents = new ArrayList<>();
        double tournamentProb = Settings.tournamentProb;
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        while (chosenParents.size() < Settings.parentSize) {
            Individual p1 = pop.get(rand.nextInt(pop.size()));
            Individual p2 = pop.get(rand.nextInt(pop.size()));

            Individual chosenParent;
            if (rand.nextDouble() < tournamentProb) {
                chosenParent = parentSelectionTournament(p1, p2);
            } else {
                chosenParent = rand.nextBoolean() ? p1 : p2;
            }
            chosenParents.add(chosenParent);
        }
        return chosenParents;
    }

    public Individual parentSelectionTournament(Individual p1, Individual p2){
        boolean useNSGA = Settings.useNSGA;
        if(p2.strictlyBetterFit(p1)){
            return p2;
        }else if(p1.strictlyBetterFit(p2)){
            return p1;
        }else if(useNSGA){
            if(p2.getCrowdingDist()>p1.getCrowdingDist()){
                return p2;
            }else if(p2.getCrowdingDist()<p1.getCrowdingDist()){
                return p1;
            }
        }
        return Utils.selectRandom(p1, p2);
    }

    public Pair<Individual, Individual> crossover (Individual p1, Individual p2) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        List<Gene> g1 = p1.getGenotype();
        List<Gene> g2 = p2.getGenotype();

        if (rand.nextDouble() < Settings.crossoverProb) {
            int length = g1.size();

            //int indexPoint = Utils.randomInt(length);
            int indexPoint = ThreadLocalRandom.current().nextInt(1,length);
            List<Gene> p1Segment1 = new ArrayList<>(g1.subList(0, indexPoint));
            List<Gene> p1Segment2 = new ArrayList<>(g1.subList(indexPoint, length));
            List<Gene> p2Segment1 = new ArrayList<>(g2.subList(0, indexPoint));
            List<Gene> p2Segment2 = new ArrayList<>(g2.subList(indexPoint, length));

            g1 = new ArrayList<>();
            g2 = new ArrayList<>();

            g1.addAll(p1Segment1);
            g1.addAll(p2Segment2);
            g2.addAll(p2Segment1);
            g2.addAll(p1Segment2);
        }
        g1 = randomlyMutateGene(g1);
        g2 = randomlyMutateGene(g2);
        return new Pair<>(new Individual(g1, this.pixels), new Individual(g2, this.pixels));
    }

    //TODO slett
    public static void genFix() throws InterruptedException {
        Thread.sleep(1000); //TODO legg på 0
    }

}
