/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.restrictions.instances;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.Restriction;
import java.util.ArrayList;
import math.Field;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class ResourceRestriction extends Restriction {

    private Resource r;

    public ResourceRestriction(Resource r, FieldElement b) {
        super();
        this.r = r;
        this.setB(b);
    }

    @Override
    public boolean comply(ArrayList<FieldElement> x) {
        ArrayList<FieldElement> a = new ArrayList<>();
        /**
         * Zum Aufsummieren der Einzelnen r_{ik} wird auf a-Seite überall eine 1
         * benötigt.
         */
        if (!x.isEmpty()) {
            FieldElement einsElement = Field.getEinsElement(x.get(0).getClass());
            for (FieldElement elem : x) {
                a.add(einsElement);
            }
        }
        this.setA(a);
        return super.comply(x);
    }

    public Resource getResource() {
        return r;
    }

}
