/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.modes;

import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.ThresholdAcceptance;
import ga.acceptance.reset.ResetThreshold;
import ga.algorithms.coevolving.GABundle;
import ga.basics.StartPopulationGenerator;
import ga.crossover.Crossover;
import ga.crossover.TwoPointCrossover;
import ga.mutation.Mutation;
import ga.mutation.SwapMutation;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.nextGeneration.SelectCrossMutate;
import ga.nextGeneration.SelectCrossMutateParallel;
import ga.selection.RankingSelection;
import ga.selection.Selection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class ImplicitModeGA implements ModeSubGA<ImplicitModeIndividual> {

    public boolean RESET;
    public int numberOfGenerationStagnation;
    public Selection<ImplicitModeIndividual> selMode = new RankingSelection<>();
    public Crossover<ImplicitModeIndividual> crossMode = new TwoPointCrossover<>();
    public Mutation<ImplicitModeIndividual> mutMode = new SwapMutation<>();
    double xOverMode = 1.0;
    double xmutMode = 0.5;

    public ImplicitModeGA(boolean RESET, Integer numberOfGeneration) {
        this.RESET = RESET;
        this.numberOfGenerationStagnation = numberOfGeneration;
    }

    public ImplicitModeGA(int numberOfGeneration) {
        this.numberOfGenerationStagnation = numberOfGeneration;
        this.RESET = true;
    }

    public ImplicitModeGA() {
        this.RESET = false;
    }

    @Override
    public GABundle<ImplicitModeIndividual> getGA(StartPopulationGenerator<ImplicitModeIndividual> mPop, boolean parallel) {

        AcceptanceMechanism<ImplicitModeIndividual> acceptanceMode = RESET ? new ResetThreshold(numberOfGenerationStagnation, mPop) : new ThresholdAcceptance<>(); //new Elitismus<>(eliteMode); 

        NextGenerationAlgorithm<ImplicitModeIndividual> nextGenMode = null;
        if (parallel) {
            nextGenMode = new SelectCrossMutateParallel<>(xOverMode, xmutMode, selMode, mutMode, crossMode);
        } else {
            nextGenMode = new SelectCrossMutate<>(xOverMode, xmutMode, selMode, mutMode, crossMode);
        }
        return new GABundle<>(nextGenMode, acceptanceMode);
    }

    public void print(File folder)   {
        try {
            File f = new File(folder, "Mode.txt");
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#########Mode-GA################");
            bw.newLine();
            bw.write("RESET: " + this.RESET);
            bw.newLine();
            bw.write("NumberOfStagnation: " + this.numberOfGenerationStagnation);
            bw.newLine();
            bw.write("Selektion: " + this.selMode.getClass());
            bw.newLine();
            bw.write("Mutation: " + this.mutMode.getClass());
            bw.newLine();
            bw.write("Rekombination: " + this.crossMode.getClass());
            bw.newLine();
            bw.write("Mutationsrate: " + this.xmutMode);
            bw.newLine();
            bw.write("Rekombinationrate: " + this.xOverMode);
            bw.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(ImplicitModeGA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
