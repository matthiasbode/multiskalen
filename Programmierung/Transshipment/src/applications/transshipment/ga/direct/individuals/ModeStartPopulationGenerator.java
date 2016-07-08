/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.direct.individuals;

import applications.transshipment.generator.projekte.duisburg.DuisburgTrainGenerator;
import ga.individuals.IntegerIndividual;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import ga.Parameters;
import ga.basics.Population;
import ga.basics.StartPopulationGenerator;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import util.RandomUtilities;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class ModeStartPopulationGenerator implements StartPopulationGenerator<IntegerIndividual> {

    private MultiJobTerminalProblem problem;
    private List<LoadUnitJob> notDirectlyTransportable;
    public boolean loadJSon;

    public ModeStartPopulationGenerator(MultiJobTerminalProblem problem, List<LoadUnitJob> notDirectlyTransportable, boolean loadJSon) {
        this.problem = problem;
        this.notDirectlyTransportable = notDirectlyTransportable;
        this.loadJSon = loadJSon;
    }

//    public IntegerIndividual readFromFile(File f) {
//        LoadUnitJobActivityOnNodeBuilder.DEBUG = true;
//        Routen r = XMLSerialisierung.importXML(f, Routen.class, null);
//        List<Integer> routings = new ArrayList<>();
//
//        for (LoadUnitJob job : problem.getJobs()) {
//            Integer mode = r.routen.get(job.getLoadUnit().getID());
//            if (mode == null) {
//                throw new NoSuchElementException("Keine Route für diese LU gefunden");
//            }
//            routings.add(mode);
//        }
//
//        IntegerIndividual ind = new IntegerIndividual(routings);
//        return ind;
//    }
    @Override
    public Population<IntegerIndividual> generatePopulation(int anzahl, Object... additionalObjects) {
        /**
         * Population aller Moden.
         */
        Population<IntegerIndividual> modePop = new Population<>(IntegerIndividual.class, 0);
        HashMap<String, Double> importJSON = null;
        if (loadJSon) {
            System.out.println("Lese Json-Routen");
            InputStream resourceAsStream = DuisburgTrainGenerator.class.getResourceAsStream("routen/Routes_Duisburg.json");
            importJSON = JSONSerialisierung.importJSON(resourceAsStream, HashMap.class);
        }
        /**
         * Schleife zum Erzeugen von Moden (Routings) und Individuals, die
         * dieses Codieren. Die Operationen werden in allOperations gespeichert
         * und innerhalb der nächsten Schleife für die
         * TopoSortTransshipmentIndivduals verwendet.
         */
        for (int i = 0; i < anzahl; i++) {
            /**
             * #################################################################
             * Routingauswahl generieren
             * #################################################################
             */
            /**
             * Zufällige Auswahl eines Routings für jeden Job
             */
            List<Integer> routings = new ArrayList<>();

            for (LoadUnitJob job : problem.getJobs()) {
                if (loadJSon) {
                    double value = importJSON.get(job.getLoadUnit().getID());
                    routings.add((int) value - 1);
                } else {
                    int sizeOfRoutings = job.getRoutings().size();
                    int randomRouting = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, sizeOfRoutings - 1);
                    /**
                     * Direkttransport ausschließen.
                     */
                    if (notDirectlyTransportable.contains(job)) {
                        randomRouting = RandomUtilities.getRandomValue(Parameters.getRandom(), 1, sizeOfRoutings - 1);
                    }
                    routings.add(randomRouting);
                }

            }
            IntegerIndividual ind = new IntegerIndividual(routings);
            modePop.add(ind);
        }
        return modePop;
    }

}
