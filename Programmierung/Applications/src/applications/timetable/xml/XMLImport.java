package applications.timetable.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * Die Klasse XMLImport bietet Methoden zum Im- und Export beliebiger
 * Dateien bzw. Objekte. Diese können auf ein Schema getestet, bzw. mit
 * einem Schema versehen werden. Die Schemata müssen im selben Verzeichnis wie
 * die Klassen liegen.
 *
 * @author bode
 * @author weidlich
 */
public abstract class XMLImport {
    
    public static <E> E importXML(File xmlFile, Class<E> cls, boolean check, XmlAdapter... adapters) {
        try {
            JAXBContext jc = JAXBContext.newInstance(cls);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            if (check) {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(cls.getResource(cls.getSimpleName() + ".xsd"));
                unmarshaller.setSchema(schema);
            }
            for (XmlAdapter xmlAdapter : adapters) {
                unmarshaller.setAdapter(xmlAdapter);
            }
            Object obj = unmarshaller.unmarshal(xmlFile);
            if (obj.getClass() == cls) {
                E cp = cls.cast(obj);
                return cp;
            }
        } catch (JAXBException e) {
            System.out.println("Fehler beim Parsen von " + xmlFile + ": " + e.getLocalizedMessage());
        } catch (SAXException ex) {
            System.out.println("Fehler beim Parsen von " + xmlFile + ": " + ex.getLocalizedMessage());
        }
        return null;
    }

    public static <E> void exportXML(File xmlFile, E instance, boolean check) {
        try {


            JAXBContext jc = JAXBContext.newInstance(instance.getClass());
            Marshaller ms = jc.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            //TODO: Genaues Format raussuchen und Location festlegen
            if (check) {
                Class<?> cls = instance.getClass();
                URL schemaURL = cls.getResource(cls.getSimpleName() + ".xsd");
                ms.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, cls.getName() + " " + cls.getSimpleName() + ".xsd");
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(schemaURL);
                ms.setSchema(schema);
            }
            ms.marshal(instance, xmlFile);
        } catch (JAXBException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
    }

    public static <E> String getSchemaLocation(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
        // parse an XML document into a DOM tree
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document document = parser.parse(xmlFile);
        String schema = document.getDocumentElement().getAttribute("xsi:schemaLocation");
        return schema.split(" ")[0];
    }
}
