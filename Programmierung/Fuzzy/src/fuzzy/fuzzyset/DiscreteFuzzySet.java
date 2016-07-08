package fuzzy.fuzzyset;

import java.util.LinkedHashMap;

/**
 * DiscretFuzzySet.java stellt Eigenschaften und Methoden
 * fuer diskrete Fuzzy-Mengen zur Verfuegung.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik
 */
public class DiscreteFuzzySet<E> implements FuzzySet<E> {

    DiscreteSet<E> g; // Grundgesamtheit
    LinkedHashMap<E, Double> memberships = new LinkedHashMap<E, Double>();

    DiscreteFuzzySet() {
        g = new DiscreteSet<E>();
    }

    /**
     * Erzeugt eine diskrete Fuzzy-Menge aus geordneten Paaren.
     * Die Zugehoerigkeitsfunktion ist standardmae�ig gleich 0.
     * @param g Grundgesamtheit.
     */
    public DiscreteFuzzySet(DiscreteSet<E> g) {
        this.g = g;
        for (E e : g) {
            memberships.put(e, 0.0);
        }
    }

    /**
     * Setzt die Zugehoerigkeitsvariable
     * eines Elementes der Grundgesamtheit.
     * @param e Element der Grundgesamtheit.
     * @param mue Zugehoerigkeitsvariable.
     */
    public void setMembership(E e, double mue) {
        memberships.put(e, mue);
    }

    /**
     * Liefert die Zugehoerigkeitsvariable
     * eines Elements der Grundgesamtheit.
     * @param e Element der Grundgesamtheit.
     * @return Zugehoerigkeitsvariable.
     */
    @Override
    public double getMembership(E e) {
        if (memberships.containsKey(e)) {
            return memberships.get(e);
        } else {
            return Double.NaN;
        }
    }

    /**
     * Liefert den Traeger oder Einflussbereich dieser Fuzzy-Menge.
     * @return Traeger oder Einflussbereich als diskrete Menge.
     */
    public DiscreteSet<E> getSupport() {
        DiscreteSet<E> support = new DiscreteSet<E>();
        for (E e : g) {
            if (memberships.get(e) != null && memberships.get(e) > 0) {
                support.add(e);
            }
        }
        return support;
    }

    /**
     * Liefert ein alpha-Niveau dieser Menge.
     * @param alpha minimaler Zugehoerigkeitswert des alpha-Niveaus.
     * @return alpha-Niveau als diskrete Menge.
     */
    public DiscreteSet<E> getAlphaCut(double alpha) {
        DiscreteSet<E> alphaCut = new DiscreteSet<E>();
        for (E e : memberships.keySet()) {
            if (memberships.get(e) >= alpha) {
                alphaCut.add(e);
            }
        }
        return alphaCut;
    }

    /**
     * Liefert die Kernmenge dieser Menge.
     * @return Kernmenge als diskrete Menge.
     */
    public DiscreteSet<E> getKernel() {
        return getAlphaCut(1.);
    }

    /**
     * Liefert die Hoehe dieser Menge.
     * @return Hoehe.
     */
    public double getHeight() {
        double height = 0.;
        for (E e : memberships.keySet()) {
            if (memberships.get(e) >= height) {
                height = memberships.get(e);
            }
        }
        return height;
    }

    /**
     * Liefert den Durchschnitt dieser Menge mit einer anderen.
     * @param diskrete Fuzzy-Menge.
     * @return Durchschnitt als diskrete Fuzzy-Menge.
     */
 
    @Override
    public DiscreteFuzzySet<E> section(FuzzySet<E> s) {
        if (s instanceof DiscreteFuzzySet) {
            DiscreteFuzzySet<E> set = (DiscreteFuzzySet<E>) s;
            if (!g.equals(set.g)) {
                return null;
            }
            DiscreteFuzzySet<E> section = new DiscreteFuzzySet<E>(g);
            for (E item : this.memberships.keySet()) {
                section.setMembership(item, Math.min(this.getMembership(item), set.getMembership(item)));
            }
            return section;
        } else {
            throw new UnsupportedOperationException("Durschnitt nur mit zwei diskreten FuzzyMengen möglich");
        }
    }

    /**
     * Liefert die Vereinigung dieser Menge mit einer anderen.
     * @param diskrete Fuzzy-Menge.
     * @return Vereinigung als diskrete Fuzzy-Menge.
     */
 
    @Override
    public DiscreteFuzzySet<E> union(FuzzySet<E> s) {
        if (s instanceof DiscreteFuzzySet) {
            DiscreteFuzzySet<E> set = (DiscreteFuzzySet<E>) s;
            if (!g.equals(set.g)) {
                return null;
            }
            DiscreteFuzzySet<E> union = new DiscreteFuzzySet<E>(g);
            for (E item : this.memberships.keySet()) {
                union.setMembership(item, Math.max(this.getMembership(item), set.getMembership(item)));
            }
            return union;
        } else {
            throw new UnsupportedOperationException("Vereinigung nur mit zwei diskreten FuzzyMengen möglich");
        }
    }

    @Override
    public String toString() {
        String s = "{";
        for (E e : this.memberships.keySet()) {
            s += "{"+e +", m: " + this.memberships.get(e)+"};" ;
        }
        s+="}";
        return s;
    }

    
    
     

    public static void main(String[] args) {
        DiscreteSet<Integer> g = new DiscreteSet<Integer>();
        g.add(1);
        g.add(10);

        DiscreteFuzzySet fs0 = new DiscreteFuzzySet(g);
        fs0.setMembership(1, 0.3);
        System.out.println("Fuzzy-Menge 0:\t" + fs0);

        DiscreteFuzzySet fs1 = new DiscreteFuzzySet(g);
        fs1.setMembership(10, 0.5);
        System.out.println("Fuzzy-Menge 1:\t" + fs1);

        DiscreteFuzzySet fs2 = fs0.union(fs1);
        System.out.println("Vereinigung:\t" + fs2);

        DiscreteFuzzySet fs3 = fs0.section(fs1);
        System.out.println("Durchschnitt:\t" + fs3);
    } 
}