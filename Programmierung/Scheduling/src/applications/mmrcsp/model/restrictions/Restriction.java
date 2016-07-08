/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.restrictions;

import java.util.ArrayList;
import math.Field;
import math.FieldElement;

/**
 * Klasse zur Modellierung von Restriktionen, z.B. S_j-S_i >= d_{ij}^{min} oder
 * S_j-S_i d_{ij}^{max}
 *
 * Diese werden immer in der kanonischen Form des LP definiert - Maximierung der
 * Zielfunktion unter a^T x <=b Gleichungen in Ungleichungen überführen.
 *
 *
 *
 *
 *
 * @author bode
 */
public class Restriction {

    private ArrayList<FieldElement> a;
    private FieldElement b;

    public Restriction() {

    }

    public Restriction(ArrayList<FieldElement> a, FieldElement b) {
        this.a = a;
        this.b = b;
    }

    public Restriction(FieldElement ai, FieldElement aj, FieldElement b) {
        this.a = new ArrayList<>();
        this.a.add(ai);
        this.a.add(aj);
        this.b = b;
    }

    public boolean comply(ArrayList<FieldElement> x) {
        if (x.size() != a.size()) {
            throw new IllegalArgumentException("Dimension der Eingabewerte passen nicht zur Formulierung der Restriktion");
        }
        if (!a.isEmpty()) {
            FieldElement aTX = Field.getNullElement(a.get(0).getClass());
            for (int i = 0; i < a.size(); i++) {
                FieldElement aij = a.get(i);
                FieldElement xi = x.get(i);
                aTX = aTX.add(xi.mult(aij));
            }
            if (aTX.isGreaterThan(b)) {
                return false;
            }
        }

        return true;
    }

    public FieldElement getB() {
        return b;
    }

    public void setB(FieldElement b) {
        this.b = b;
    }

    public void setA(ArrayList<FieldElement> a) {
        this.a = a;
    }

}
