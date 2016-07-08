/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.xml;

import java.util.LinkedHashMap;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bode
 */
@XmlRootElement
public class Routen {

    public LinkedHashMap<String, Integer> routen = new LinkedHashMap<String, Integer>();

    public Routen() {
    }
}


