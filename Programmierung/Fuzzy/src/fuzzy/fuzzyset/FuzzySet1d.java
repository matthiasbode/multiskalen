package fuzzy.fuzzyset;

import bijava.math.function.ScalarFunction1d;



/**
 * FuzzySet1d.java stellt Eigenschaften und Methoden fuer stetige Fuzzy-Mengen
 * zur Verfuegung.
 *
 * @author Leibniz Universitaet Hannover<br> Institut fuer Bauinformatik
 */
public class FuzzySet1d implements FuzzySet<Double> {

    public ScalarFunction1d membership; // skalare Zugehoerigkeitsfunktion

    /**
     * Erzeugt eine stetige Fuzzy-Menge mit einer skalaren
     * Zugehoerigkeitsfunktion.
     *
     * @param membership skalare Zugehoerigkeitsfunktion.
     */
    public FuzzySet1d(ScalarFunction1d membership) {
        this.membership = membership;
    }

    public FuzzySet1d() {
        this.membership = new ScalarFunction1d() {
            @Override
            public double getValue(double x) {
                return 0;
            }
        };
    }

    /**
     * Liefert die Zugehoerigkeitsvariable fuer einen skalaren Wert.
     *
     * @param x skalarer Wert.
     * @return Zugehoerigkeitsvariable.
     */
    @Override
    public double getMembership(Double x) {
        return membership.getValue(x);
    }

    /**
     * Liefert das Komplement dieser Menge.
     *
     * @return Komplement dieser Menge.
     */
    public FuzzySet1d getComplement() {
        ScalarFunction1d complementFunction = new ScalarFunction1d() {
            @Override
            public double getValue(double x) {
                return 1. - FuzzySet1d.this.membership.getValue(x);
            }
        };

        return new FuzzySet1d(complementFunction);
    }

    /**
     * Liefert den Durchschnitt von dieser und einer anderen Menge.
     *
     * @param set andere stetige Fuzzy-Menge.
     * @return Durchschnitt von dieser und der anderen Menge.
     */
    @Override
    public FuzzySet1d section(FuzzySet s) {
        if (s instanceof FuzzySet1d) {
            final FuzzySet1d set = (FuzzySet1d) s;
            ScalarFunction1d scalarFunction1d = new ScalarFunction1d() {
                @Override
                public double getValue(double x) {
                    return Math.min(FuzzySet1d.this.getMembership(x), set.getMembership(x));
                }
            };

            return new FuzzySet1d(scalarFunction1d);
        }
        throw new UnsupportedOperationException("Durchschnitt nur mit zwei kontinuierlichen FuzzySets möglich");
    }

    /**
     * Liefert die Vereinigung von dieser und einer anderen Menge.
     *
     * @param set andere stetige Fuzzy-Menge.
     * @return Vereinigung von dieser und der anderen Menge.
     */
    @Override
    public FuzzySet1d union(FuzzySet s) {
        if (s instanceof FuzzySet1d) {
            final FuzzySet1d set = (FuzzySet1d) s;
            ScalarFunction1d scalarFunction1d = new ScalarFunction1d() {
                @Override
                public double getValue(double x) {
                    return Math.max(FuzzySet1d.this.getMembership(x), set.getMembership(x));
                }
            };
            return new FuzzySet1d(scalarFunction1d);
        }
        throw new UnsupportedOperationException("Vereinigung nur mit zwei kontinuierlichen FuzzySets möglich");
    }

    /**
     * Liefert die Schnittmenge unterhalb eines Zugehoerigkeitswertes.
     *
     * @param r Zugehoerigkeitswert.
     * @return Schnittmenge.
     */
    public FuzzySet1d cut(final double r) {
        ScalarFunction1d scalarFunction1d = new ScalarFunction1d() {
            @Override
            public double getValue(double x) {
                return Math.min(FuzzySet1d.this.membership.getValue(x), r);
            }
        };
        return new FuzzySet1d(scalarFunction1d);
    }

    /**
     * Ueberprueft die Gleichheit von dieser Menge und einem anderen Objekt.
     *
     * @param object anderes Objekt.
     * @return <code>true</code> falls das Objekt gleich dieser Menge ist.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FuzzySet1d)) {
            return false;
        }
        return ((FuzzySet1d) object).membership.equals(membership);
    }

    public ScalarFunction1d getMembershipFunction() {
        return membership;
    }
}