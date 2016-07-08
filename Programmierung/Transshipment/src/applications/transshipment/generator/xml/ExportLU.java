/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bode
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement
public class ExportLU {

    public String Typ;

    public double length;
    @XmlElement
    public ExportStorageLocation origin;
    @XmlElement
    public ExportStorageLocation destination;

    public boolean hazardous;

    public String id;

    public ExportLU() {
    }

    public ExportLU(String Typ, double length) {
        this.Typ = Typ;
        this.length = length;
    }

    @Override
    public String toString() {
        return "ExportLU{" + "Typ=" + Typ + ", length=" + length + ", origin=" + origin + ", destination=" + destination + '}';
    }

}
