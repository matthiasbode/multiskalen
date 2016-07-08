/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.resources;

/**
 *
 * @author bode
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 * Deklariert eine Ressource zur Unterressource. Diese muss ihre Oberressource
 * ausgeben koennen.
 * @author thees
 */
public interface SubResource extends Resource {

    /**
     * @return Die Oberressource dieser Unterressource.
     */
    public abstract Resource getSuperResource();

}
