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

public class GeneticAlgorithm {

    private static Random rand = new Random();
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
            System.out.println("Generation: " + genCount); // For oversikt
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

//må endres
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
        Individual maxInd = paretoFront.get(paretoFront.size()-1);
        Individual minInd = paretoFront.get(0);
        maxInd.setCrowdingDist(Integer.MAX_VALUE);
        minInd.setCrowdingDist(Integer.MAX_VALUE);

        double minMaxCriteriaDiffInSeg = maxInd.getSegCriteriaValue(criteria);
        minMaxCriteriaDiffInSeg -= minInd.getSegCriteriaValue(criteria);

        double differenceInCriteria;
        for (int i = 1; i < paretoFront.size()-1; i++) {
            differenceInCriteria = paretoFront.get(i+1).getSegCriteriaValue(criteria);
            differenceInCriteria -= paretoFront.get(i-1).getSegCriteriaValue(criteria);
            differenceInCriteria /= minMaxCriteriaDiffInSeg;

            paretoFront.get(i).setCrowdingDist(paretoFront.get(i).getCrowdingDist() + differenceInCriteria);
        }
    }

    public void makePop(){
        System.out.println("Making a pop");
        List<Individual> newPop = Collections.synchronizedList(new ArrayList<>());
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        ThreadPoolExecutor temporaryEx = (ThreadPoolExecutor) Executors.newFixedThreadPool(Settings.threadSize); // ?? må ha?
        for (int i = 0; i < Settings.popSize / 2; i++) {
            temporaryEx.execute(() -> { // Denne burde endres
                Individual ind = new Individual(this.pixels, rand.nextInt(4, Settings.amountOfSegments)); // en annen rand
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
            List<Gene> allowedGenes = this.pixels[individualPixel.r][individualPixel.l].getValidGenes();
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
        for (List<Individual> paretoFrontier : this.popRanked) { // endre navn på params
            assignCrowdingDist(paretoFrontier);
            if (paretoFrontier.size() <= Settings.popSize - this.pop.size()) {
                this.pop.addAll(paretoFrontier);
            } else {
                List<Individual> temp = new ArrayList<>(paretoFrontier);
                temp.sort((x,y) -> Double.compare(y.getCrowdingDist(), x.getCrowdingDist()));
                this.pop.addAll(temp.subList(0, Settings.popSize - this.pop.size()));
            }
        }
    }


    private List<Individual> dominatingSet2(List<Individual> pop) {
        Set<Individual> dominatedSet = new HashSet<>();
        List<Individual> notChosenList = new ArrayList<>();

        notChosenList.add(pop.get(0)); // First member in pop
        for (Individual ind : pop) {
            if (dominatedSet.contains(ind)) {
                continue;
            }
            notChosenList.add(ind);
                for (Individual notChosenInd : notChosenList) {
                    if (dominatedSet.contains(ind) || notChosenInd == ind) {
                        continue;
                    } else if (ind.dominateChecker(notChosenInd)) {
                    dominatedSet.add(notChosenInd);
                    } else if (notChosenInd.dominateChecker(ind)) { // Kan nok endre rekkefølgen på not her når ting funker.
                        dominatedSet.add(ind);
                        break;
                    }
                }
            }
        notChosenList.removeAll(dominatedSet);
        return notChosenList;
        }

        //ny, må sjekke om den funker ordentlig
        private List<Individual> dominatingSet(List<Individual> pop) {
            Set<Individual> dominatedSet = new HashSet<>();
            List<Individual> notChosenList = new ArrayList<>(pop);

            for (int i = 0; i < notChosenList.size(); i++) {
                Individual ind = notChosenList.get(i);

                if (dominatedSet.contains(ind)) {
                    continue;
                }
                for (int j = i + 1; j < notChosenList.size(); j++) {
                    Individual otherInd = notChosenList.get(j);

                    if (dominatedSet.contains(otherInd)) {
                        continue;
                    }
                    if (ind.dominateChecker(otherInd)) {
                        dominatedSet.add(otherInd);
                    }else if (otherInd.dominateChecker(ind)) {
                        dominatedSet.add(ind);
                        break;
                    }
                }
            }
            notChosenList.removeAll(dominatedSet);
            return notChosenList;
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
                    } else if (p2.getCrowdingDist() < p1.getCrowdingDist()){
                        chosen.add(p1);
                    }
                } else if (p2.strictlyBetterFit(p1)) {
                    chosen.add(p2);
                } else if (p1.strictlyBetterFit(p2)) {
                    chosen.add(p1);
                } else {
                    chosen.add(Utils.selectRandom(p1, p2));
                }
            } else {
                chosen.add(Utils.selectRandom(p1, p2));
            }
        }
        return chosen;
    }

    //prøvde å lage ny, men usikker om den funker helt
    public List<Individual> parentSelection2(List<Individual> pop) {
        List<Individual> chosenParents = new ArrayList<>();
        while (chosenParents.size() < Settings.parentSize) {

            Individual parent1 = pop.get(Utils.randomInt(pop.size()));
            Individual parent2 = pop.get(Utils.randomInt(pop.size()));

            if (Utils.randomDouble() < Settings.tournamentProb) {
                if (Settings.useNSGA) {
                    if (parent1.getCrowdingDist() < parent2.getCrowdingDist()) {
                        chosenParents.add(parent2);
                    } else {
                        chosenParents.add(parent1);
                    }
                } else {
                    if (parent2.strictlyBetterFit(parent1)) {
                        chosenParents.add(parent2);
                    } else {
                        chosenParents.add(parent1);
                    }
                }
            } else {
                chosenParents.add(Utils.selectRandom(parent1, parent2));
            }
        }
        return chosenParents;
    }

    public Tuple<Individual, Individual> crossover (Individual p1, Individual p2) {
        List<Gene> g1 = p1.getGenotype();
        List<Gene> g2 = p2.getGenotype();

        if (Utils.randomDouble() < Settings.crossoverProb) {
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
        g1 = mutateGeneRandomly(g1);
        g2 = mutateGeneRandomly(g2);
        return new Tuple<>(new Individual(g1, this.pixels), new Individual(g2, this.pixels));
    }

}
