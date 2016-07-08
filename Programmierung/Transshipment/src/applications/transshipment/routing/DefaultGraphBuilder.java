package applications.transshipment.routing;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.resources.LoadUnitResource;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSHandover;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.util.Pair;
import util.ExportTransportGraph;
import util.SimpleLinkedSet;

/**
 * Diese Klasse beinhaltet Tools um Wege zwischen Resourcen zu suchen und zu
 * bewerten.
 *
 * @author wagenkne, bode
 */
public class DefaultGraphBuilder implements GraphBuilder {

    /**
     * Die transportierenden Systeme der verfuegbaren Resourcen.
     */
    private final List<ConveyanceSystem> availableConveyanceSystems;
    /**
     * Die lagernden Systeme der verfuegbaren Resourcen.
     */
    private final List<LoadUnitStorage> availableStorageSystems;
    /**
     * Die statischen TransferAreas, veraendern sich nicht innerhalb einer
     * Instanz der SequenceTools. Dadurch Einsparung von Rechenzeit durch
     * mehrmaligen Neuberechnens...
     */
    private final List<TransferArea> staticTransferAreas;
    /**
     * Graph, der aus den statischen TransferAreas sowie den Verbindungen
     * zwischen diesen besteht.
     */
    private final TransportGraph staticTransportGraph;
    private MultiJobTerminalProblem problem;

    public DefaultGraphBuilder(MultiJobTerminalProblem problem) {
        this(problem.getTerminal().getAllResources(), problem);
    }

    /**
     * Erstellt eine neue Instanz der SequenceTools, die Berechnungsmoeglich-
     * keiten fuer genau die uebergebene Liste an verfuegbaren Resourcen
     * bereitstellt. Sollten sich die zur verfuegung stehenden Resourcen
     * aendern, muss eine neue Instanz der SequenceTools erzeugt werden.
     *
     * @param availableResources Zur Verfuegung stehende Resourcen.
     * @param problem
     */
    public DefaultGraphBuilder(Collection<LoadUnitResource> availableResources, MultiJobTerminalProblem problem) {
        this.problem = problem;
        this.availableConveyanceSystems = new ArrayList<ConveyanceSystem>();
        this.availableStorageSystems = new ArrayList<LoadUnitStorage>();

        /*
         * Die übergebenen Resourcen durchgehen und aufteilen in transportierende
         * (ConveyanceSystem) sowie lagernde (LoadUnitStorage) Systeme.
         * Eine Resource _kann_ in mehrere Kategorieren fallen und somit auch in
         * mehrere Listen einsortiert werden. Das ist beabsichtigt.
         */
        for (LoadUnitResource lur : availableResources) {
            if (lur instanceof ConveyanceSystem) {
                availableConveyanceSystems.add((ConveyanceSystem) lur);
            }
            if (lur instanceof LoadUnitStorage) {
                availableStorageSystems.add((LoadUnitStorage) lur);
            }
        }

        this.staticTransferAreas = createStaticTransferAreas();
        this.staticTransportGraph = createStaticTransportGraph();
        
        ExportTransportGraph.exportToGraphML(staticTransportGraph, "C:\\Users\\Bode\\Documents\\Promo\\transportGraph.graphml");
        
//        if (TransshipmentParameter.DEBUG) {
//            System.out.println("Static Graph");
//            System.out.println("Knoten: " + staticTransportGraph.numberOfVertices());
//            for (TransferArea loadUnitResource : staticTransportGraph.vertexSet()) {
//                System.out.println(loadUnitResource + "\t" + loadUnitResource.getCenterOfGeneralOperatingArea());
//            }
//            System.out.println("Kanten: " + staticTransportGraph.numberOfEdges());
//            System.out.println("Ressourcen: " + availableResources.size());
//            for (LoadUnitResource loadUnitResource : availableResources) {
//                System.out.println(loadUnitResource);
//            }
//        }
    }

    /**
     * Findet alle statischen {@link TransferArea}s, d.h. alle TransferAreas die
     * waehrend der gesamten Existenz der Instanz der SequenceTools nicht
     * veraendert werden (also die TransferAreas, die nicht vom aktuell
     * betrachteten Job abhaengig sind).
     *
     * @return Ein Set mit den TransferAreas
     */
    private List<TransferArea> createStaticTransferAreas() {
        /*
         * Speichert alle transferAreas ab.
         */
        final List<TransferArea> transferAreas = new ArrayList<>();

        /*
         * Anzahl der transportierenden Systeme zwischenspeichern.. schneller..
         */
        final int nrOfConveyanceSystems = availableConveyanceSystems.size();
        /*
         * Alle transportierenden Resourcen durchgehen und Schnittmengen der
         * Arbeitsbereiche finden. Genauer:
         *
         * Jedes ConveyanceSystem auf Schnittbereich mit jedem anderen
         * ConveyanceSystem abfragen. Auch mit sich selbst. Es wird davon
         * ausgegangen, dass, wenn x zu y transportieren kann, auch y zu x
         * transportieren kann.
         */
        for (int i = 0; i < nrOfConveyanceSystems; i++) {
            final ConveyanceSystem c1 = availableConveyanceSystems.get(i);
            for (int j = i; j < nrOfConveyanceSystems; j++) {
                final ConveyanceSystem c2 = availableConveyanceSystems.get(j);
                /*
                 * Schnittmenge der Arbeitsbereiche.
                 */
                final Area schnittmenge = new Area(c1.getGeneralOperatingArea());
                schnittmenge.intersect(c2.getGeneralOperatingArea());
                /*
                 * Wenn die Schnittmenge nicht leer ist, existiert eine
                 * Verbindung und eine neue TransferArea wird erstellt. Dieser
                 * TransferArea ist zunaechst noch _kein_ Lagersystem zugeordnet
                 */
                if (!schnittmenge.isEmpty()) {
                    final TransferArea transferArea = new TransferArea(c1, c2, null, schnittmenge);
                    //output.CTSO_Logger.println("Transferarea gefunden: " + transferArea);
                    transferAreas.add(transferArea);
                }
            }
        }
        /*
         * Damit eine Uebergabe von Transportsystem t1 zu Transportsystem t2
         * moeglich ist, muss eine lagernde Resource in dem Ueberschneidungsbereich
         * der Arbeitsgebiete von t1 und t2 vorhanden sein. Diese lagernde Resource
         * <b>kann</a> t1 oder t2 sein, sofern diese {@link LoadUnitStorage}
         * implementieren, es kann aber auch eine beliebige andere Resource sein,
         * die {@link LoadUnitStorage} implementiert. Die lagernde Resource wird
         * mit l bezeichnet (Kleinbuchstabe L).
         */
        /*
         * Speichert eventuell nachtraeglich beim Verschneiden hinzugefuegte
         * TransferAreas, damit diese in der folgenden for-Schleife nicht mehr-
         * fach berechnet werden.
         */
        final Set<TransferArea> newTransferAreas = new LinkedHashSet<>();
        /*
         * Alle Ueberschneidungsbereiche = TransferAreas durchgehen
         */
        final Iterator<TransferArea> it = transferAreas.iterator();
        while (it.hasNext()) {
            final TransferArea tA = it.next();
            /*
             * Pruefen, ob eine lagernde Resource in dem Bereich liegt:
             */
            final List<LoadUnitStorage> storageSystemsInTransferArea = new ArrayList<>();
            for (LoadUnitStorage lus : availableStorageSystems) {
                /*
                 * Schnittbereich herausfinden
                 */
                final Area schnittmenge = new Area(lus.getGeneralOperatingArea());
                schnittmenge.intersect(tA.getGeneralOperatingArea());

                /*
                 * Wenn der Schnittbereich nicht leer ist, liegt das StorageSystem
                 * im Bereich der TransferArea. Dann muessen aber noch alle CVS mit dem Storage interagieren koennen.
                 * Es wird die Subressource uebernommen.
                 */
                LoadUnitStorage sublus = null;
                if (!schnittmenge.isEmpty()) {
                    sublus = lus.getSubResource(schnittmenge);
                }

                if (sublus != null) {
                    boolean canInteract = true;
                    // Storage muss Uebergabe machen koennen / wollen
                    if (tA.getConveyanceSystem1() != null && tA.getConveyanceSystem2() != null) {
                        canInteract = this.problem.getStorageInteractionRule(lus).canTransferBetween(tA.getConveyanceSystem1(), tA.getConveyanceSystem2());
                    }

                    for (ConveyanceSystem cvs : tA.getConveyanceSystems()) {
                        if (!this.problem.getConveyanceSystemInteractionRule(cvs).canInteractWith(lus)) {
                            canInteract = false;
                        }
                    }

                    if (canInteract) {
                        storageSystemsInTransferArea.add(sublus);
                    }
                }
            }
            final int nrOfStorageSystemsInTransferArea = storageSystemsInTransferArea.size();
            /*
             * Wenn keine Schnittmengen der TransferArea mit lagernden Systemen
             * bestehen, kann keine Uebergabe erfolgen (Definition!). Somit
             * braucht diese TransferArea nicht weiter beruecksichtigt werden.
             */
            if (nrOfStorageSystemsInTransferArea == 0) {
                //output.CTSO_Logger.println("No LoadUnitStorage in TransferArea " + tA + ". Removing TransferArea.");
                it.remove();
            }
            /*
             * Wenn genau eine Schnittmenge der TransferArea mit einem lagernden
             * System besteht, muss einfach der Bereich der TransferArea
             * begrenzt werden (auf die gemeinsame Schnittmenge von t1, t2 und l).
             * TODO: Evtl. mit naechstem if zusammenfassen wenn alles funktioniert.
             */
            if (nrOfStorageSystemsInTransferArea == 1) {
                //output.CTSO_Logger.println("Found 1 LoadUnitStorage in TransferArea " + tA + ". Calculating new Area.");
                //output.CTSO_Logger.println("Old Area: " + tA.getArea().getBounds2D() + ".");
//                tA.getGeneralOperatingArea().intersect(storageSystemsInTransferArea.get(0).getGeneralOperatingArea());
                //output.CTSO_Logger.println("New Area: " + tA.getArea().getBounds2D() + ".");
                tA.setStorageSystem(storageSystemsInTransferArea.get(0));
            }
            /*
             * Wenn mehrere lagernde Systeme in der Schnittmenge von t1 und t2
             * liegen, muss die TransferArea in mehrere TransferAreas aufgeteilt
             * werden. Je eine TransferArea fuer eine kombination aus t1, t2 und l.
             */
            if (nrOfStorageSystemsInTransferArea > 1) {
                //output.CTSO_Logger.println("Found " + nrOfStorageSystemsInTransferArea + " LoadUnitStorages in TransferArea " + tA + ". Calculating new Areas.");
                /* Kopien der transferArea erstellen */
                final List<TransferArea> splitTransferAreas = new ArrayList<TransferArea>();
                splitTransferAreas.add(tA);
                for (int i = 1; i < nrOfStorageSystemsInTransferArea; i++) {
                    splitTransferAreas.add(new TransferArea(tA));
                }

                /*
                 * Je eine der noch identischen TransferAreas in der splitTransferArea
                 * Liste mit je einem StorageSystem verschneiden.
                 */
                for (int i = 0; i < splitTransferAreas.size(); i++) {
                    //output.CTSO_Logger.println("Old Area: " + splitTransferAreas.get(i).getArea().getBounds2D() + ".");
                    splitTransferAreas.get(i).getGeneralOperatingArea().intersect(storageSystemsInTransferArea.get(i).getGeneralOperatingArea());
                    //output.CTSO_Logger.println("New Area: " + splitTransferAreas.get(i).getArea().getBounds2D() + ".");
                    splitTransferAreas.get(i).setStorageSystem(storageSystemsInTransferArea.get(i));
                    if (i > 0) {
                        newTransferAreas.add(splitTransferAreas.get(i));
                    }
                }
            }
        }
        /* Neue TransferAreas zu den bestehenden TransferAreas hinzufuegen. */
        transferAreas.addAll(newTransferAreas);

//        output.CTSO_Logger.println("TransferAreas: "+transferAreas.size());
        return transferAreas;
    }

    public static Set<ConveyanceSystem> getCS(TransferArea t1, TransferArea t2) {

        final SimpleLinkedSet<ConveyanceSystem> commonRes = t1.getConveyanceSystems().section(t2.getConveyanceSystems());
//        if (commonRes.isEmpty()) {
////            return null;
//            return commonRes;
//        }
        if (commonRes.size() > 1) {
            if (t1.getStorageSystem() instanceof LCSHandover && t2.getStorageSystem() instanceof LCSHandover) {
                for (ConveyanceSystem conveyanceSystem : commonRes) {
                    if (conveyanceSystem instanceof LCSystem) {
                        System.out.println("Entferne Andere als LCS");
                        return commonRes;
//                        return conveyanceSystem;
                    }
                }
            }
//            throw new IllegalArgumentException("Fehlerhafte Zuweisung der ConveyanceSystems für den Transport");
        }
//
//        if (commonRes.size() == 1) {
//            return commonRes;
////            return commonRes.iterator().next();
//        } else {
//            throw new IllegalArgumentException("Fehlerhafte Zuweisung der ConveyanceSystems für den Transport");
////            return commonRes.iterator().next();
//        }
        return commonRes;
    }

    /**
     * Erstellt einen statischen Graphen aus den statischen
     * {@link TransferAreas} und erzeugt die Kanten innerhalb dieses Graphen.
     * Eine Kante wird für je zwei TransferAreas erzeugt, wenn gilt: Beide
     * TransferAreas haben mindestens ein identisches Transportsystem. Haben die
     * TransferAreas mehrere identische Transportsysteme, so werden mehrere
     * Kanten erzeugt.
     *
     * @return Statischer Graph für die Ermittlung der möglichen Transportwege
     */
    private TransportGraph createStaticTransportGraph() {
        /*
         * Neuen Graphen erstellen aus den Informationen, die durch die
         * TransferAreas eingebracht werden.
         */
        final TransportGraph transportGraph = new TransportGraph();

        /*
         * Die TransferAreas als Knoten dem Graphen hinzufuegen.
         */
        for (TransferArea tA : staticTransferAreas) {
            transportGraph.addVertex(tA);
        }

        /*
         * Die Kanten des Graphen ermitteln und hinzufuegen.
         */
        final int nrOfTransferAreas = staticTransferAreas.size();
        for (int i = 0; i < nrOfTransferAreas; i++) {
            final TransferArea t1 = staticTransferAreas.get(i);
            for (int j = i + 1; j < nrOfTransferAreas; j++) {
                final TransferArea t2 = staticTransferAreas.get(j);

                Set<ConveyanceSystem> cs = getCS(t1, t2);
                for (ConveyanceSystem ccs : cs) {

                    if (ccs != null) {
                        Pair<TransferArea, TransferArea> transport = new Pair<TransferArea, TransferArea>(t1, t2);

                        if (this.problem.getConveyanceSystemInteractionRule(ccs).canInteractWith(t2.getStorageSystem())) { // koennte auch raus, weil schon beim Erzeugen der TransferAreas ueberprueft
                            transportGraph.addTransport(transport, ccs);
                        }
                        if (this.problem.getConveyanceSystemInteractionRule(ccs).canInteractWith(t1.getStorageSystem())) { // s.o.
                            transportGraph.addTransport(transport.transposition(), ccs);
                        }
                    }
                }
            }
        }
        return transportGraph;

    }

    public SpecifiedTransportGraph getGraphForJob(final LoadUnitJob job) {
        return getGraphForJob(job, 0);
    }

    /**
     * quasi Kopie der Methode findTransportOperationSequences, die den
     * DijkstraForOneRoute liefert
     *
     * @param job
     * @param currentTime anhand der currentTime wird festgestellt, ob die
     * LoadUnit sich noch am Origin befindet, oder bereits teilweise zum Ziel
     * verladen wurde
     * @param bewertung
     * @return
     */
    @Override
    public SpecifiedTransportGraph getGraphForJob(final LoadUnitJob job, long currentTime) {
        /*
         * Verbindung der transportierenden Resourcen mit den Start- und
         * Zielpunkten herstellen.
         */
        LoadUnitStorage startStorage = job.getOrigin();
        LoadUnitStorage zielStorage = job.getDestination();
        return getGraphForJob(job, startStorage, zielStorage, currentTime);
    }

    @Override
    public SpecifiedTransportGraph getGraphForJob(final LoadUnitJob job, LoadUnitStorage startStorage, LoadUnitStorage zielStorage, long currentTime) {
        /*
         * Verbindung der transportierenden Resourcen mit den Start- und
         * Zielpunkten herstellen.
         */
        final LoadUnitStorage start = startStorage;
        final LoadUnitStorage ziel = zielStorage;

        if (!(start.canHandleLoadUnit(job.getLoadUnit()) && ziel.canHandleLoadUnit(job.getLoadUnit()))) {
            String msg = "Origin oder Destination koennen LU nicht handlen!";
            String msg1 = job.getOrigin() + "\t" + job.getLoadUnit().getOrigin().toString();
            String msg11 = job.getDestination().toString();
            String msg2 = "Origin: " + start.canHandleLoadUnit(job.getLoadUnit());
            String msg3 = "Destination: " + ziel.canHandleLoadUnit(job.getLoadUnit());
            throw new UnsupportedOperationException(msg + "\n" + msg1 + "\n" + msg11 + "\n" + msg2 + "\n" + msg3);
        }

        final ArrayList<TransferArea> startTransferAreas = new ArrayList<TransferArea>();
        final ArrayList<TransferArea> zielTransferAreas = new ArrayList<TransferArea>();

        for (ConveyanceSystem cs : availableConveyanceSystems) {
            if (cs.canHandleLoadUnit(job.getLoadUnit())) {

                final Area schnittmengeStart = new Area(cs.getGeneralOperatingArea());
                final Area schnittmengeZiel = new Area(cs.getGeneralOperatingArea());

                schnittmengeStart.intersect(start.getGeneralOperatingArea());
                schnittmengeZiel.intersect(ziel.getGeneralOperatingArea());

                /*
                 * Wenn sowohl Start- als auch Ziel Resource in einem durch
                 * ConveyanceSystems erreichbaren Gebiet liegen:
                 */
                if (!schnittmengeStart.isEmpty() && this.problem.getConveyanceSystemInteractionRule(cs).canInteractWith(start)) {
                    LoadUnitStorage subStart = start.getSubResource(schnittmengeStart);
                    if (subStart != null) {
                        final TransferArea transferArea = new TransferArea(cs, null, subStart, schnittmengeStart);
                        startTransferAreas.add(transferArea);
                    }

                }
                if (!schnittmengeZiel.isEmpty() && this.problem.getConveyanceSystemInteractionRule(cs).canInteractWith(ziel)) {
                    LoadUnitStorage subZiel = ziel.getSubResource(schnittmengeZiel);
                    if (subZiel != null) {
                        final TransferArea transferArea = new TransferArea(cs, null, subZiel, schnittmengeZiel);
                        zielTransferAreas.add(transferArea);
                    }

                }
            }
        }// end of Schleife ueber ConveyanceSystems

        /*
         * Wenn keine Schnittbereiche zwischen den ConveyanceSystems und der
         * Startresource bestehen: Abbruch und Rueckgabe von null.
         */
        if (startTransferAreas.isEmpty()) {

            throw new UnsupportedOperationException();
        }
//        output.CTSO_Logger.println("Start Transferareas gefunden: " + startTransferAreas);

        /*
         * Wenn keine Schnittbereiche zwischen den ConveyanceSystems und der
         * Zielresource bestehen: Abbruch und Rueckgabe von null.
         */
        if (zielTransferAreas.isEmpty()) {

            throw new UnsupportedOperationException();
        }

        /*
         * Neuen Graphen erstellen aus dem uebergebenen Graphen, um das
         * Original nicht zu veraendern.
         */
        SpecifiedTransportGraph dynamicTransportGraph = new SpecifiedTransportGraph(job, staticTransportGraph);

        /*
         * Die zusaetzlichen TransferAreas (Start und Ziel) als Knoten dem
         * Graphen hinzufuegen.
         */
        for (TransferArea tA : startTransferAreas) {
            dynamicTransportGraph.addVertex(tA);
        }

        for (TransferArea tA : zielTransferAreas) {
            dynamicTransportGraph.addVertex(tA);
        }

        /*
         * jetzt wird ueberprueft, ob diese spezielle LU an einer TransferArea ueberhaupt umgeladen werden kann.
         * sonst wird der entsprechende Knoten geloescht
         */
        HashSet<TransferArea> nodesToRemove = new HashSet<TransferArea>(); // hihi, hier darf ich HashSet
        for (TransferArea node : dynamicTransportGraph.vertexSet()) {
            if (!node.canHandleLoadUnit(job.getLoadUnit())) {
                nodesToRemove.add(node);
            }
        }
        for (TransferArea node : nodesToRemove) {
            dynamicTransportGraph.removeVertex(node);
        }


        /*
         * Die zusaetzlichen Kanten des Graphen ermitteln und hinzufuegen. Nur in eine Richtung !
         */
        for (TransferArea transferArea : dynamicTransportGraph.vertexSet()) {

            for (TransferArea addTA : startTransferAreas) {
                if (!(transferArea == addTA)) { // Wenn die TAs gleich sind, nicht weitermachen, um keine Schleifen zu erzeugen.
                    Set<ConveyanceSystem> cs = getCS(transferArea, addTA);
                    for (ConveyanceSystem ccs : cs) {

                        if (ccs != null) {
                            Pair<TransferArea, TransferArea> transport = new Pair<TransferArea, TransferArea>(transferArea, addTA);

                            if (this.problem.getConveyanceSystemInteractionRule(ccs).canInteractWith(transferArea.getStorageSystem())) { // s.o.
                                dynamicTransportGraph.addTransport(transport.transposition(), ccs);
                            }
                        }
                    }

                }
            } // ueber alle start-/zielAreas

            for (TransferArea addTA : zielTransferAreas) {
                if (!(transferArea == addTA)) { // Wenn die TAs gleich sind, nicht weitermachen, um keine Schleifen zu erzeugen.
                    Set<ConveyanceSystem> cs = getCS(transferArea, addTA);
                    for (ConveyanceSystem ccs : cs) {
                        if (ccs != null) {
                            Pair<TransferArea, TransferArea> transport = new Pair<TransferArea, TransferArea>(transferArea, addTA);

                            if (this.problem.getConveyanceSystemInteractionRule(ccs).canInteractWith(addTA.getStorageSystem())) { // s.o.
                                dynamicTransportGraph.addTransport(transport, ccs);
                            }
                        }
                    }

                }
            } // ueber alle start-/zielAreas

        }


        /*
         * Bewerten der einzelnen Wege
         */
//         for (Pair<TransferArea, TransferArea> pair : dynamicTransportGraph.edgeSet()) {
//            ConveyanceSystem conveyanceSystem = dynamicTransportGraph.getConveyanceSystem(pair);
//            double evaluation = bewertung.evaluate(pair, conveyanceSystem, job.getLoadUnit());
//            dynamicTransportGraph.setEdgeWeight(pair, new DoubleEdgeWeight(evaluation));
//        }
//        
        // virtuelle Start-/Zielknoten
        TransferArea startNode = TransferArea.startTransferArea;
        TransferArea zielNode = TransferArea.endTransferArea;
        dynamicTransportGraph.setStart(startNode);
        dynamicTransportGraph.setZiel(zielNode);

        // Kanten mit Bewertung 0 von und zu virtuellen Start-/Zielknoten hinzufuegen
        for (TransferArea addTA : startTransferAreas) {
            if (addTA == null) {
                System.out.println("SubRessource kann LU nicht handlen, was Superklasse aber kann. Kann durch Gruppenproblematik OK sein.");
                System.out.println(job.getLoadUnit() + "wird nicht gehandled von " + addTA.getStorageSystem());
//                output.CTSO_Logger.errorPrintln("Hier liegt ein SubRessourcen-Fehler vor. Muss behoben werden? ResourceGroup?");
//                output.CTSO_Logger.errorPrintln("Wahrscheinlich kann die Subklasse eine LU nicht handlen, was die Superklasse noch konnte.");
//                output.CTSO_Logger.errorPrintln("Daher wurde der notwenddige Knoten aus dem Graphen gelöscht.");
//                output.CTSO_Logger.errorPrintln("LU: "+lu);
//                output.CTSO_Logger.errorPrintln("startTransferArea ist nicht im dynamicTransportGraph enthalten: "+addTA);
            }
            Pair<TransferArea, TransferArea> transport = new Pair<>(startNode, addTA);
            dynamicTransportGraph.addTransport(transport, null);

//            Transport transport = new Transport(start, dynamicTransportGraph.getNode(addTA), null);
//            dynamicTransportGraph.addEdge(transport);
        }
        for (TransferArea addTA : zielTransferAreas) {
            if (addTA == null) {
                System.out.println("SubRessource kann LU nicht handlen, was Superklasse aber kann. Kann durch Gruppenproblematik OK sein.");
                System.out.println(job.getLoadUnit() + " wird nicht gehandled von " + addTA.getStorageSystem());
//                output.CTSO_Logger.errorPrintln("Hier liegt ein SubRessourcen-Fehler vor. Muss behoben werden? ResourceGroup?");
//                output.CTSO_Logger.errorPrintln("Wahrscheinlich kann die Subklasse eine LU nicht handlen, was die Superklasse noch konnte.");
//                output.CTSO_Logger.errorPrintln("Daher wurde der notwenddige Knoten aus dem Graphen gelöscht.");
//                output.CTSO_Logger.errorPrintln("LU: "+lu);
//                output.CTSO_Logger.errorPrintln("zielTransferArea ist nicht im dynamicTransportGraph enthalten: "+addTA);
            }
            Pair<TransferArea, TransferArea> transport = new Pair<>(addTA, zielNode);
            dynamicTransportGraph.addTransport(transport, null);

//            Transport transport = new Transport(dynamicTransportGraph.getNode(addTA), ziel, null);
//            dynamicTransportGraph.addEdge(transport);
        }
        return dynamicTransportGraph;
    }

    @Override
    public TransportGraph getStaticTransportGraph() {
        return this.staticTransportGraph;
    }
}
