/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package  applications.transshipment.generator.xml;

 
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author bode
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement
@XmlSeeAlso(value = {ExportTrain.class, ExportSlot.class, ExportStorage.class}) 
public class ExportStorageLocation  {

    
    public ExportStorageLocation() {
    }

   

}
