/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.operations;

import applications.mmrcsp.model.resources.Resource;
import java.util.Set;
import math.FieldElement;

/**
 * Ein RCPSP besteht aus einer Menge von Aktivitäten, hier Operation genannt.
 * Die einzelnen Operationen haben eine Ausführungsdauer p_i. Durch das
 * Scheduling soll die Startzeit S_i der Operation bestimmt werden.
 *
 * @author bode
 */
public interface Operation extends Cloneable  {

    public FieldElement getDemand(Resource r);

    public Set<Resource> getRequieredResources();

    public void setDemand(Resource r, FieldElement rik);

    public FieldElement getDuration();

    public void setDuration(FieldElement duration);

    public int getId();
    
    public Operation clone();

}
