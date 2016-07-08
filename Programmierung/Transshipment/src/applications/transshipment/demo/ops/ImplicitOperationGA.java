/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.ops;

import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
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
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class ImplicitOperationGA implements OperationSubGA<ImplicitOperationIndividual> {

    public boolean RESET;
    public int numberOfGenerationStagnation;
    public Selection<ImplicitOperationIndividual> selOps = new RankingSelection();//new TournamentSelection(4);
    public Crossover<ImplicitOperationIndividual> crossOps = new TwoPointCrossover<>();
    public Mutation<ImplicitOperationIndividual> mutOps = new SwapMutation<>();// new RandomMutation(2); //
    double xOverOps = 1.0;
    double xmutOps = 0.4;

    public ImplicitOperationGA(boolean RESET, Integer numberOfGeneration) {
        this.RESET = RESET;
        this.numberOfGenerationStagnation = numberOfGeneration;
    }

    public ImplicitOperationGA(int numberOfGeneration) {
        this.numberOfGenerationStagnation = numberOfGeneration;
        this.RESET = true;
    }

    public ImplicitOperationGA() {
        this.RESET = false;
    }

    @Override
    public GABundle<ImplicitOperationIndividual> getGA(StartPopulationGenerator<ImplicitOperationIndividual> vPop, boolean parallel) {

        /**
         * GA für Operationen.
         */
        AcceptanceMechanism<ImplicitOperationIndividual> acceptanceOps = RESET ? new ResetThreshold(numberOfGenerationStagnation, vPop) : new ThresholdAcceptance(); // new Elitismus<>(0.1); //
        NextGenerationAlgorithm<ImplicitOperationIndividual> nextGenOps = null;
        if (parallel) {
            nextGenOps = new SelectCrossMutateParallel<>(xOverOps, xmutOps, selOps, mutOps, crossOps);

        } else {
            nextGenOps = new SelectCrossMutate<>(xOverOps, xmutOps, selOps, mutOps, crossOps);
        }
        return new GABundle<>(nextGenOps, acceptanceOps);
    }

    public void print(File folder) {
        FileWriter fw = null;
        try {
            File f = new File(folder, "Operation.txt");
            fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#########Operation-GA################");
            bw.newLine();
            bw.write("RESET: " + this.RESET);
            bw.newLine();
            bw.write("NumberOfStagnation: " + this.numberOfGenerationStagnation);
            bw.newLine();
            bw.write("Selektion: " + this.selOps.getClass());
            bw.newLine();
            bw.write("Mutation: " + this.mutOps.getClass());
            bw.newLine();
            bw.write("Rekombination: " + this.crossOps.getClass());
            bw.newLine();
            bw.write("Mutationsrate: " + this.xmutOps);
            bw.newLine();
            bw.write("Rekombinationrate: " + this.xOverOps);
            bw.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(ImplicitOperationGA.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ImplicitOperationGA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
