/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources;
 
import applications.transshipment.model.loadunits.LoadUnit;
import applications.mmrcsp.model.resources.PositionedResource;

/**
 * Eine Resource, die eine Ladeeinheit aufnehmen kann. Es wird angenommen,
 * dass eine Resource unabhaengig von der auszufuehrenden Operation
 * ein generelles Gebiet bedienen kann (Arbeitsbereich).
 * 
 * TODO: beschreiben
 * 
 * @author berthold, hofmann
 */
public interface LoadUnitResource extends PositionedResource {

    /**
     * Bestimmt, ob eine Resource mit einer Ladeeinheit umgehen kann
     * (kann z.B. beschraenkt sein durch maximale Abmessungen).
     *
     * @param loadunit  Die Ladeeinheit, fuer die Ueberprueft werden soll,
     *                  ob sie von der Resource verwendet werden kann.
     * @return          <code>true</code>, wenn diese Resource die Ladeeinheit
     *                  verwenden kann, sonst <code>false</code>.
     */
    public boolean canHandleLoadUnit(LoadUnit loadunit);

     
}

