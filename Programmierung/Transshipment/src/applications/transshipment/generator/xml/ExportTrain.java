/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package  applications.transshipment.generator.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bode
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement
public class ExportTrain  extends ExportStorageLocation {
    public int number;

    public ExportTrain() {
    }

    public ExportTrain(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "ExportTrain{" + "number=" + number + '}';
    }
    
    
    
}
