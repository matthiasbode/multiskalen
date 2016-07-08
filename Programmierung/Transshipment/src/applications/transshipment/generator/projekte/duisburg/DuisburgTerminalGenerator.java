/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.projekte.duisburg;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.generator.projekte.ParameterInputFile;
import applications.transshipment.generator.projekte.TerminalGenerator;
import static applications.transshipment.generator.projekte.duisburg.DuisburgGenerator.ts;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import applications.transshipment.model.structs.RailroadTrack;
import applications.transshipment.model.structs.Terminal;
import fuzzy.number.discrete.FuzzyFactory;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import math.LongValue;

/**
 * Am Beispiel Duisburg!
 *
 * @author bode
 */
public class DuisburgTerminalGenerator implements TerminalGenerator {

    public final static double WagonStandardBreite = 2.5;
    public static double abstandVomRand = WagonStandardBreite / 2;
    public static double laenge = 700;
    public static double breiteLager = 4.00;
    public static double breiteRg51und57 = 4.80;
    public static double gleisMittenAbstand = 5.0; // also Breite pro Gleis
    public static double abstandG56G55 = 2.25;
    public static double abstandLFA = 0.5;
    public static double breiteLFA = 17.75;
    public static double abstandG52G51 = 2.9;
    public static double abstandrueckLKW = 4.0;
    public static double breiteKran = 3 * breiteLager + 2 * breiteRg51und57 + 5 * gleisMittenAbstand + abstandG56G55 + 2 * abstandLFA + breiteLFA + abstandG52G51 + 2 * abstandVomRand;

    ParameterInputFile parameters;

    public Terminal generateTerminal(ParameterInputFile parameters) {
        this.parameters = parameters;

        /**
         * #####################################################################
         * Geometrien
         * #####################################################################
         */
        Rectangle2D rlSued = new Rectangle2D.Double(0, abstandVomRand, laenge, breiteLager);

        /**
         * Gleise
         */
        Rectangle2D rg57 = new Rectangle2D.Double(0, rlSued.getMaxY(), laenge, breiteRg51und57);
        Rectangle2D rg56 = new Rectangle2D.Double(0, rg57.getMaxY(), laenge, gleisMittenAbstand);
        Rectangle2D rg55 = new Rectangle2D.Double(0, rg56.getMaxY() + abstandG56G55, laenge, gleisMittenAbstand);
        /**
         * LFA
         */
        Rectangle2D rLFA = new Rectangle2D.Double(0, rg55.getMaxY() + abstandLFA, laenge, breiteLFA);

        /**
         * Gleise
         */
        Rectangle2D rg54 = new Rectangle2D.Double(0, rLFA.getMaxY() + abstandLFA, laenge, gleisMittenAbstand);
        Rectangle2D rg53 = new Rectangle2D.Double(0, rg54.getMaxY(), laenge, gleisMittenAbstand);
        Rectangle2D rg52 = new Rectangle2D.Double(0, rg53.getMaxY(), laenge, gleisMittenAbstand);
        Rectangle2D rg51 = new Rectangle2D.Double(0, rg52.getMaxY() + abstandG52G51, laenge, breiteRg51und57);

        Rectangle2D rlNord = new Rectangle2D.Double(0, rg51.getMaxY(), laenge, breiteLager);
        Rectangle2D recCranes = new Rectangle2D.Double(0, 0, laenge, breiteKran);

        SimpleStorageRow sNord = new SimpleStorageRow("Lager Nord", rlNord, 3.1);
        SimpleStorageRow sSued = new SimpleStorageRow("Lager Sued", rlSued, 3.1);

        CraneRunway craneRunway = new CraneRunway(recCranes, parameters.getNumberOfCranes());

        ArrayList<ConveyanceSystem> conveyanceSystems = new ArrayList<>();
        ArrayList<LoadUnitStorage> storages = new ArrayList<>();
        ArrayList<RailroadTrack> gleise = new ArrayList<>();

        /**
         * Gleise erzeugen
         */
        gleise.add(new RailroadTrack(rg55, "2"));
        gleise.add(new RailroadTrack(rg54, "3"));
        gleise.add(new RailroadTrack(rg53, "4"));
        gleise.add(new RailroadTrack(rg52, "5"));

        gleise.add(new RailroadTrack(rg57, "0"));
        gleise.add(new RailroadTrack(rg56, "1"));
        gleise.add(new RailroadTrack(rg51, "6"));
        /**
         * Lager hinzufügen
         */
//        SimpleStorage storage = new SimpleStorage();
        storages.add(sNord);
        storages.add(sSued);

        /**
         * HandoverPoints
         */
        LCSystem lcSystem = null;
        if (parameters.getInt(ParameterInputFile.KEY_use_LCS) == 1) {
            lcSystem = new LCSystem(rLFA, parameters.getNumberOfAGVs());

            SimpleLCS.getLCSystemByAngle(lcSystem, parameters);
            storages.add(lcSystem.getHandoverPoints());
        }
//        Collection<HandoverPoint> handoverPointsForLCSystem = HandoverPointsGenerator.getHandoverPointsForLCSystem(rLFA);
//        lcSystem.setHandoverPoints(handoverPointsForLCSystem);
//        storages.addAll(handoverPointsForLCSystem);
        /**
         * Krane hinzufügen
         */
        conveyanceSystems.addAll(craneRunway.getSharingResources());
        if (parameters.getInt(ParameterInputFile.KEY_use_LCS) == 1) {
            conveyanceSystems.add(lcSystem);
        }

        Collection<SharedResource> sharedResources = new ArrayList<>();
        sharedResources.add(craneRunway);
        if (parameters.getInt(ParameterInputFile.KEY_use_LCS) == 1) {
            sharedResources.add(lcSystem);
        }

        TimeSlot tempAvail = null;

        if (TransshipmentParameter.fuzzyMode.equals(TransshipmentParameter.FuzzyMode.crisp)) {
            tempAvail = TimeSlot.create(parameters.getStart().getTimeInMillis(), parameters.getEnde().getTimeInMillis());
        } else {
            tempAvail = new TimeSlot(FuzzyFactory.createLinearInterval(parameters.getStart().getTimeInMillis(), 1000), FuzzyFactory.createLinearInterval(parameters.getEnde().getTimeInMillis(), 1000));
        }

        Terminal terminal = new Terminal(tempAvail, conveyanceSystems, storages, gleise, sharedResources, sSued);

        return terminal;
    }

    @Override
    public String toString() {
        return "DuisburgTerminalGenerator{" + '}';
    }

}
