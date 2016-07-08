/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.xmlTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author Matthias
 */
public class SchemaGenerator {

    File baseDir;
    Class<?> cls;

    public SchemaGenerator(Class<?> cls, File baseDir) {
        this.baseDir = baseDir;
        this.cls = cls;
    }

    public void generateSchema() {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(cls);
            context.generateSchema(new MySchemaOutputResolver(baseDir));
        } catch (JAXBException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    class MySchemaOutputResolver extends SchemaOutputResolver {

        File baseDir;

        public MySchemaOutputResolver(File baseDir) {
            this.baseDir = baseDir;
        }

        //Output als Datei
        @Override
        public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
            File file  = new File(baseDir, suggestedFileName);
            StreamResult sr =  new StreamResult(file);
            sr.setOutputStream(new FileOutputStream(file));
            return sr;
        }
    }
}

