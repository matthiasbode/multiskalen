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
public class ExportSlot extends ExportStorageLocation {

    public ExportTrain train;
    public int number;

    public ExportSlot() {
    }

    public ExportSlot(ExportTrain train, int number) {
        this.train = train;
        this.number = number;
    }

    @Override
    public String toString() {
        return "ExportSlot{" + "train=" + train + ", number=" + number + '}';
    }

    
    

}
