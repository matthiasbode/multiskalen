package applications.transshipment.routing.baeiko;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.util.Pair;


/**
 * Klasse, die den Inhalt der Prioritaetswarteschlange bei der Suche nach dem
 * Algorithmus von Dijkstra enthaelt. Somit kann fuer jeden Schritt der Suche
 * eindeutig angegeben werden, auf welchem Weg der aktuelle Knoten besucht
 * wurde. Dies ist noetig, um nicht nur den Weg mit der besten Bewertung sondern
 * alle moeglichen Wege zu finden.
 *
 * Daher wird der dem Knoten zugeordnete Wert bei der Suche dach dem Algorithmus
 * von Dijkstra nicht ueberschrieben, wenn ein kuerzerer Weg gefunden wurde,
 * sondern zusaetzlich hinzugefuegt.
 *
 * @author wagenkne
 * @param <E>   Klasse der Objekte der Knoten
 * @param <F>   Klasse der Kanten
 */
public class DijkstraPriorityContent<E, F extends Pair<E,E>> implements Comparable<DijkstraPriorityContent<E, F>> {

    private E node;
    private double value;
    private ArrayList<F> pathToThis;
    private static int contentCounter=0;
    private int contentNumber;

    /**
     * Erstellt ein neues Objekt mit den uebergebenen Werten.
     *
     * @param node          Knoten
     * @param value         Bewertung
     * @param pathToThis    Pfad zu diesem Objekt.
     */
    public DijkstraPriorityContent(final E node, final double value, final ArrayList<F> pathToThis) {
        this.node = node;
        this.value = value;
        this.pathToThis = pathToThis;
        contentNumber=contentCounter++;
    }

    /**
     * Gibt die Menge der bereits auf dem Weg besuchten Knoten zurueck.
     * Liste statt Set, da die Erstellung so deutlich schneller erfolgen kann
     * (beim Set werden viele in diesem Fall unnoetige .equals Pruefungen
     * durchgeführt). Die Eindeutigkeit wird hier durch das Vorgehen bei der
     * Erzeugung manuell gesichert.
     *
     * @deprecated  Wenn moeglich nicht nutzen, ist relativ langsam
     * @return  Menge der bereits besuchten Knoten
     */
    public Set<E> getVisitedNodes() {
        Set<E> visitedNodes = new LinkedHashSet<E>();
        if (pathToThis.size() > 0) {
            for (int i = 0; i < pathToThis.size(); i++) {
                if (i == 0) {
                    visitedNodes.add(pathToThis.get(i).getFirst());
                }
                visitedNodes.add(pathToThis.get(i).getSecond());
            }
        }
        return visitedNodes;
    }

    /**
     * Gibt die Bewertung zurueck
     *
     * @return Bewertung
     */
    public double getValue() {
        return value;
    }

    /**
     * Gibt den bisherigen besuchten Pfad zurueck.
     *
     * @return  Bisher besuchter Pfad
     */
    public ArrayList<F> getPathToThis() {
        return pathToThis;
    }

    /**
     * Gibt den Knoten zurueck, dem dieser Eintrag zugeordnet ist.
     *
     * @return  Knoten, der diesem Eintrag zugeordnet ist
     */
    public E getNode() {
        return node;
    }

    /**
     * Vergleicht den Knoten mit einem anderen Knoten.
     * Da die Reihenfolge auch bei gleichen Werten deterministisch sein soll, wird bei gleichen Werten die contentNumber als zweites Kriterium verwendet.
     *
     * @param dpc   Der DijkstraPriorityContent mit dem verglichen werden soll
     * @return      0   bei gleicher Entfernung zum Start
     *              -1  bei geringerer Entfernung zum Start
     *              1   bei groesserer Entfernung zum Start
     *
     */
    @Override
    public int compareTo(DijkstraPriorityContent dpc) {
        if (value > dpc.value) {
            return 1;
        }
        if (value < dpc.value) {
            return -1;
        }
        // also value gleich
        if (contentNumber > dpc.contentNumber) {
            return 1;
        }
        if (contentNumber < dpc.contentNumber) {
            return -1;
        }

        //selbes Objekt:
        return 0;
    }

    @Override
    public String toString() {
        return "DPC: {" + "node=" + node + " --- bewertung=" + value + " --- Weg=" + pathToThis + '}';
    }
}
