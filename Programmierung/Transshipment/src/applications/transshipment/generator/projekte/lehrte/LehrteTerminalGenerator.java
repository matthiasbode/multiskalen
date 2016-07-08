/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.projekte.lehrte;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import applications.transshipment.generator.projekte.duisburg.SimpleLCS;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import applications.transshipment.model.structs.RailroadTrack;
import applications.transshipment.model.structs.Terminal;
import applications.transshipment.model.structs.Wagon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;

/**
 * Am Beispiel Duisburg!
 *
 * @author bode
 */
public class LehrteTerminalGenerator {

    
    public static double abstandVomRandOben = Wagon.StandardBreite / 2;
    public static double breiteProSpur = 5.3;
    public static double breiteLager = 6.25;
    public static double breitemittelbereich = 17.66;
    public static double abstandG1G2 = 2.8;
    public static double abstandG5G6 = 3.1;
    public static double abstandLagerUnten = 0.55;
    public static double laenge = 700;
    public static double breiteKran = 70.1 + abstandVomRandOben;
    public static double kranlaenge = 34;
    public static double LFWagenBreite = 2.5;
    public static double LFWagenLaenge = 12.5;

    

    public LehrteTerminalGenerator( ) {
 
    }

    public Terminal generateTerminal( LehrteInputParameters parameters) {
        Rectangle2D rl1 = new Rectangle2D.Double(LehrteTerminalGenerator.abstandVomRandOben, 0, LehrteTerminalGenerator.breiteLager, LehrteTerminalGenerator.laenge);
        Rectangle2D rg1 = new Rectangle2D.Double(rl1.getMaxX(), 0, LehrteTerminalGenerator.breiteProSpur, LehrteTerminalGenerator.laenge);
        Rectangle2D rg2 = new Rectangle2D.Double(rg1.getMaxX() + LehrteTerminalGenerator.abstandG1G2, 0, LehrteTerminalGenerator.breiteProSpur, LehrteTerminalGenerator.laenge);
        Rectangle2D rg3 = new Rectangle2D.Double(rg2.getMaxX(), 0, LehrteTerminalGenerator.breiteProSpur, LehrteTerminalGenerator.laenge);
        Rectangle2D rlaengs = new Rectangle2D.Double(rg3.getMaxX(), 0, LehrteTerminalGenerator.breitemittelbereich, LehrteTerminalGenerator.laenge);
        Rectangle2D rg4 = new Rectangle2D.Double(rlaengs.getMaxX(), 0, LehrteTerminalGenerator.breiteProSpur, LehrteTerminalGenerator.laenge);
        Rectangle2D rg5 = new Rectangle2D.Double(rg4.getMaxX(), 0, LehrteTerminalGenerator.breiteProSpur, LehrteTerminalGenerator.laenge);
        Rectangle2D rg6 = new Rectangle2D.Double(rg5.getMaxX() + LehrteTerminalGenerator.abstandG5G6, 0, LehrteTerminalGenerator.breiteProSpur, LehrteTerminalGenerator.laenge);
        Rectangle2D rl2 = new Rectangle2D.Double(rg6.getMaxX() + LehrteTerminalGenerator.abstandLagerUnten, 0, LehrteTerminalGenerator.breiteLager, LehrteTerminalGenerator.laenge);
        Rectangle2D recCranes = new Rectangle2D.Double(0, 0, LehrteTerminalGenerator.breiteKran, LehrteTerminalGenerator.laenge);

        RailroadTrack[] glArray = new RailroadTrack[]{new RailroadTrack(rg1, "0"), new RailroadTrack(rg2, "1"), new RailroadTrack(rg3, "2"), new RailroadTrack(rg4, "3"), new RailroadTrack(rg5, "4"), new RailroadTrack(rg6, "5")};

        SimpleStorageRow storageOben = new SimpleStorageRow("oben", rl1, 3.1);
        SimpleStorageRow storageUnten = new SimpleStorageRow("unten", rl2, 3.1);

        ArrayList<LoadUnitStorage> storages = new ArrayList<>();
        storages.add(storageOben);
        storages.add(storageUnten);

        CraneRunway craneRunway = new CraneRunway(recCranes, parameters.getNumberOfCranes());
        LCSystem lcSystem = new LCSystem(rlaengs, parameters.getNumberOfAGVs());
        SimpleLCS.getLCSystemByAngle(lcSystem, parameters);
        storages.add(lcSystem.getHandoverPoints());

        ArrayList<ConveyanceSystem> conveyanceSystems = new ArrayList<>();
        conveyanceSystems.add(lcSystem);
        conveyanceSystems.addAll(craneRunway.getSharingResources());

        Collection<SharedResource> sharedResources = new ArrayList<>();
        sharedResources.add(craneRunway);
        sharedResources.add(lcSystem);

        ArrayList<RailroadTrack> gleise = new ArrayList<>(Arrays.asList(glArray));

        Terminal terminal = new Terminal(TimeSlot.create(parameters.getStart().getTimeInMillis(), parameters.getEnde().getTimeInMillis()), conveyanceSystems, storages, gleise, sharedResources, storageOben);

        return terminal;
    }
}
