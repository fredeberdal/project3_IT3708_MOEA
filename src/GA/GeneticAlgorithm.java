package GA;

import ImageSegment.ImgSegmentationIO;

import Utils.Pixel;
import Utils.Settings;
import Utils.Tuple;
import Utils.Utils;
import Utils.SegmentCriteria;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;

import static Utils.SegmentCriteria.CONNECTIVITY;
import static Utils.SegmentCriteria.DEVIATION;
import static Utils.SegmentCriteria.EDGEVALUE;
import static Utils.Settings.*;

public class GeneticAlgorithm {

    private List<List<Individual>> popRanked;
    private List<Individual> pop;
    private Pixel[][] pixels;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool((Settings.threadSize));

    public GeneticAlgorithm (ImgSegmentationIO segImgIo) {
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
                if (Utils.randomDouble() < Settings.mutationProb) {
                    ind.segmentMergeMutation();
                }
            }
            this.pop = newPop;
            genCount++;
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

    private void assignCrowdingDist(List<Individual> paretoFront) { // Endre param navn
        for (Individual ind : paretoFront) {
            ind.setCrowdingDist(0);
        }
        for (SegmentCriteria criteria : SegmentCriteria.values()) {
            assignCrowdingDistToInd(paretoFront, criteria);
        }
    }

    private void assignCrowdingDistToInd(List<Individual> paretoFront, SegmentCriteria criteria) { // Endre param navn
        paretoFront.sort(SegmentCriteria.individualComparator(criteria));
        Individual maxInd = paretoFront.get(0);
        Individual minInd = paretoFront.get(paretoFront.size());
        maxInd.setCrowdingDist(Integer.MAX_VALUE);
        minInd.setCrowdingDist(Integer.MAX_VALUE);

        double minMaxCriteriaDiffInSeg = maxInd.getSegCriteriaValue(criteria);
        minMaxCriteriaDiffInSeg -= minInd.getSegCriteriaValue(criteria);
        double differenceInCriteria;
        for (int i = 1; i < paretoFront.size(); i++) {
            differenceInCriteria = paretoFront.get(i+1).getSegCriteriaValue(criteria);
            differenceInCriteria -= paretoFront.get(i-1).getSegCriteriaValue(criteria);
            differenceInCriteria /= minMaxCriteriaDiffInSeg;
            paretoFront.get(i).setCrowdingDist(paretoFront.get(i).getCrowdingDist() + differenceInCriteria);
        }
    }

    public void makePop(){
        System.out.println("Making a pop");
        List<Individual> newPop = Collections.synchronizedList(new ArrayList<>());
        ThreadPoolExecutor temporaryEx = (ThreadPoolExecutor) Executors.newFixedThreadPool(Settings.threadSize); // ?? må ha?
        for (int i = 0; i < Settings.popSize / 2; i++) {
            temporaryEx.execute(() -> { // Denne burde endres
                Individual ind = new Individual(this.pixels, Utils.randomInt(30)); // en annen rand
                newPop.add(ind);
            });
        }
        temporaryEx.shutdown();
        while (!temporaryEx.isTerminated())  {
            // Sync up ?? Vi mø prøve uten disse
        }
        System.out.println("Finished making pop");
        this.pop = newPop;
    }
    public List<Gene> mutateGeneRandomly(List<Gene> genes) {
        if (Utils.randomDouble() < Settings.mutationProb) {
            int index = Utils.randomInt(genes.size());
            Tuple<Integer, Integer> individualPixel = Utils.toPixelCoordinates(index, this.pixels[0].length); //Sjekk bruken av toPixelCoordinates
            List<Gene> allowedGenes = this.pixels[individualPixel.l][individualPixel.r].getValidGenes();
            genes.set(index, allowedGenes.get(Utils.randomInt(allowedGenes.size()))); // Rand
        }
        return genes;
    }
    public List<List<Individual>> rankPop (List<Individual> pop) {
        int rank = 1;
        List<List<Individual>> rankedPoP = new ArrayList<>();
        while (pop.size() > 0) {
            List<Individual> domSet = dominatingSet(pop);
            for (Individual i : domSet) {
                i.setRating(rank);
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
    private void newPopFromRank() {
        this.pop.clear();
        for (List<Individual> paretoFront : this.popRanked) { // endre navn på param

        }
    }


    private List<Individual> dominatingSet(List<Individual> pop) {
        List<Individual> notDomList = new ArrayList<>();
        notDomList.add(pop.get(0)); // First member in pop
        Set<Individual> dominatedSet = new HashSet<>();
        for (Individual ind : pop) {
            if (dominatedSet.contains(ind)) {}
            notDomList.add(ind);
                for (Individual notDominatedInd : notDomList) {
                    if (dominatedSet.contains(ind) || notDominatedInd == ind) {
                    } else if (ind.dominates(notDominatedInd)) {
                    dominatedSet.add(notDominatedInd);
                    } else if (notDominatedInd.dominates(ind)) { // Kan nok endre rekkefølgen på not her når ting funker.
                        dominatedSet.add(ind);
                        break;
                    }
                }
            }
        notDomList.removeAll(dominatedSet);
        return notDomList;
        }


    public List<Individual> parentSelection(List<Individual> pop) {
        List<Individual> chosen = new ArrayList<>();
        while (chosen.size() < Settings.parentSize) {
            Individual p1 = pop.get(Utils.randomInt(pop.size()));
            Individual p2 = pop.get(Utils.randomInt(pop.size()));
            if (Utils.randomDouble() < Settings.tournamentProb) {
                if (Settings.useNSGA) {
                    if (p1.getCrowdingDist() < p2.getCrowdingDist()) {
                        chosen.add(p2);
                    } else { // else if
                        chosen.add(p1);
                    }
                } else if (p2.strictlyBetterFit(p1)) {
                    chosen.add(p2);
                } else if (p1.strictlyBetterFit(p2)) {
                    chosen.add(p2);
                } else {
                    chosen.add(Utils.selectRandom(p1, p2));
                }
            } else {
                chosen.add(Utils.selectRandom(p1, p2));
            }
        }
        return chosen;
    }

    public Tuple<Individual, Individual> crossover (Individual p1, Individual p2) {
        List<Gene> g1 = p1.getGenotype();
        List<Gene> g2 = p2.getGenotype();
        if (Utils.randomDouble() < Settings.crossoverProb) {
            int length = g1.size();
            int indexPoint = Utils.randomInt(length);
            List<Gene> temporary = new ArrayList<>(g1.subList(indexPoint, length)); // Kan flytte i en hjelpemetode
            g1.subList(indexPoint, length).clear(); // clear??
            g1.addAll(g2.subList(indexPoint, length));
            g2.subList(indexPoint, length).clear();
            g2.addAll(temporary);
        }
        g1 = mutateGeneRandomly(g1);
        g2 = mutateGeneRandomly(g2);
        return new Tuple<>(new Individual(g1, this.pixels), new Individual(g2, this.pixels));
    }



}
