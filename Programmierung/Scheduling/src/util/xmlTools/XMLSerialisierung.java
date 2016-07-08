/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.xmlTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/**
 * Diese Klasse bietet Möglichkeiten Objekte aus Java in eine XML-Datei zu
 * schreiben und umgekehrt. Die Klassen müssen hierzu XML-Annotations
 * entsprechend der JAXB-API enthalten.
 *
 * @author bode
 */
public class XMLSerialisierung {

    public static <E> E importXML(InputStream stream, Class<E> cls, File schemaFile) {
        try {
            JAXBContext jc = JAXBContext.newInstance(cls);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            if (schemaFile != null) {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(schemaFile);
                unmarshaller.setSchema(schema);
            }
            Object obj = unmarshaller.unmarshal(stream);
            if (obj.getClass() == cls) {
                E cp = cls.cast(obj);
                return cp;
            }
        } catch (JAXBException e) {
            System.out.println("Fehler beim Parsen von " + stream + ": " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (SAXException ex) {
            System.out.println("Fehler beim Parsen von " + stream + ": " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Diese Methode importiert eine XML Datei und gibt die Daten als Objekt
     * zurück. Um zu spezifizieren, von welchem Datentyp die zurückgegebenen
     * Daten sein sollen, muss die Generische Variable E auf den Namen der
     * Klasse und unter cls das class- Element der Klasse übergeben werden. Ist
     * die Variable schemaFile gesetzt, so wird auf das übergebene Schema
     * getestet. Ansonsten "null".
     *
     * @param <E> Rückgabetyp. Datentyp der eingelesenen Daten
     * @param xmlFile Pfad zur XML-Datei
     * @param cls Class-Objekt des Rückgabetyps
     * @param schemaFile Pfad zum XSD-Schema auf das getestet werden soll
     * @return
     */
    public static <E> E importXML(File xmlFile, Class<E> cls, File schemaFile) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(xmlFile);
            return importXML(stream, cls, schemaFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XMLSerialisierung.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                Logger.getLogger(XMLSerialisierung.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * Exportiert Java-Objekt in XML-Files.
     *
     * @param <E> Datentyp der zu exportierenden Klasse
     * @param xmlFile Pfad der Ausgabedatei
     * @param instance Instanz, die gespeichert werden soll
     * @param schemaFile Pfad zur SchemaLocation, die gesetzt werden kann.
     */
    public static <E> void exportXML(File xmlFile, E instance, File schemaFile) {
        try {
            JAXBContext jc = JAXBContext.newInstance(instance.getClass());
            Marshaller ms = jc.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            //TODO: Genaues Format raussuchen und Location festlegen
            if (schemaFile != null) {
                Class<?> cls = instance.getClass();
                ms.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaFile.getAbsolutePath() + " " + schemaFile.getName());
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(schemaFile);
                ms.setSchema(schema);
            }
            ms.marshal(instance, xmlFile);
        } catch (JAXBException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
    }

}
