/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.operations;

import applications.mmrcsp.model.resources.Resource;

/**
 * Interface, das eine Operation beschreibt, die nur auf einer Resource
 * ausgeführt wird, oder die eine ausgezeichnete Resource benötigt.
 *
 * @author bode
 */
public interface SingleResourceOperation extends Operation {

    public Resource getResource();

    public SubOperations getSubOperations();
  
    public void setSubOperations(SubOperations subOperations);
}
